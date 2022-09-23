// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.Objects;
import java.net.SocketAddress;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

public abstract class SelectorManager extends ContainerLifeCycle implements Dumpable
{
    public static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    protected static final Logger LOG;
    private final Executor executor;
    private final Scheduler scheduler;
    private final ManagedSelector[] _selectors;
    private long _connectTimeout;
    private ExecutionStrategy.Factory _executionFactory;
    private long _selectorIndex;
    
    protected SelectorManager(final Executor executor, final Scheduler scheduler) {
        this(executor, scheduler, (Runtime.getRuntime().availableProcessors() + 1) / 2);
    }
    
    protected SelectorManager(final Executor executor, final Scheduler scheduler, final int selectors) {
        this._connectTimeout = 15000L;
        this._executionFactory = ExecutionStrategy.Factory.getDefault();
        if (selectors <= 0) {
            throw new IllegalArgumentException("No selectors");
        }
        this.executor = executor;
        this.scheduler = scheduler;
        this._selectors = new ManagedSelector[selectors];
    }
    
    public Executor getExecutor() {
        return this.executor;
    }
    
    public Scheduler getScheduler() {
        return this.scheduler;
    }
    
    public long getConnectTimeout() {
        return this._connectTimeout;
    }
    
    public void setConnectTimeout(final long milliseconds) {
        this._connectTimeout = milliseconds;
    }
    
    public ExecutionStrategy.Factory getExecutionStrategyFactory() {
        return this._executionFactory;
    }
    
    public void setExecutionStrategyFactory(final ExecutionStrategy.Factory _executionFactory) {
        if (this.isRunning()) {
            throw new IllegalStateException("Cannot change " + ExecutionStrategy.Factory.class.getSimpleName() + " after start()");
        }
        this._executionFactory = _executionFactory;
    }
    
    @Deprecated
    public int getSelectorPriorityDelta() {
        return 0;
    }
    
    @Deprecated
    public void setSelectorPriorityDelta(final int selectorPriorityDelta) {
    }
    
    protected void execute(final Runnable task) {
        this.executor.execute(task);
    }
    
    public int getSelectorCount() {
        return this._selectors.length;
    }
    
    private ManagedSelector chooseSelector(final SocketChannel channel) {
        ManagedSelector candidate1 = null;
        if (channel != null) {
            try {
                final SocketAddress remote = channel.getRemoteAddress();
                if (remote instanceof InetSocketAddress) {
                    final byte[] addr = ((InetSocketAddress)remote).getAddress().getAddress();
                    if (addr != null) {
                        final int s = addr[addr.length - 1] & 0xFF;
                        candidate1 = this._selectors[s % this.getSelectorCount()];
                    }
                }
            }
            catch (IOException x) {
                SelectorManager.LOG.ignore(x);
            }
        }
        final long s2 = this._selectorIndex++;
        final int index = (int)(s2 % this.getSelectorCount());
        final ManagedSelector candidate2 = this._selectors[index];
        if (candidate1 == null || candidate1.size() >= candidate2.size() * 2) {
            return candidate2;
        }
        return candidate1;
    }
    
    public void connect(final SocketChannel channel, final Object attachment) {
        final ManagedSelector chooseSelector;
        final ManagedSelector managedSelector;
        final ManagedSelector set = managedSelector = (chooseSelector = this.chooseSelector(channel));
        Objects.requireNonNull(managedSelector);
        chooseSelector.submit(managedSelector.new Connect(channel, attachment));
    }
    
    public void accept(final SocketChannel channel) {
        this.accept(channel, null);
    }
    
    public void accept(final SocketChannel channel, final Object attachment) {
        final ManagedSelector chooseSelector;
        final ManagedSelector managedSelector;
        final ManagedSelector selector = managedSelector = (chooseSelector = this.chooseSelector(channel));
        Objects.requireNonNull(managedSelector);
        chooseSelector.submit(managedSelector.new Accept(channel, attachment));
    }
    
    public void acceptor(final ServerSocketChannel server) {
        final ManagedSelector chooseSelector;
        final ManagedSelector managedSelector;
        final ManagedSelector selector = managedSelector = (chooseSelector = this.chooseSelector(null));
        Objects.requireNonNull(managedSelector);
        chooseSelector.submit(managedSelector.new Acceptor(server));
    }
    
    protected void accepted(final SocketChannel channel) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void doStart() throws Exception {
        for (int i = 0; i < this._selectors.length; ++i) {
            final ManagedSelector selector = this.newSelector(i);
            this.addBean(this._selectors[i] = selector);
        }
        super.doStart();
    }
    
    protected ManagedSelector newSelector(final int id) {
        return new ManagedSelector(this, id, this.getExecutionStrategyFactory());
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        for (final ManagedSelector selector : this._selectors) {
            this.removeBean(selector);
        }
    }
    
    protected void endPointOpened(final EndPoint endpoint) {
        endpoint.onOpen();
    }
    
    protected void endPointClosed(final EndPoint endpoint) {
        endpoint.onClose();
    }
    
    public void connectionOpened(final Connection connection) {
        try {
            connection.onOpen();
        }
        catch (Throwable x) {
            if (this.isRunning()) {
                SelectorManager.LOG.warn("Exception while notifying connection " + connection, x);
            }
            else {
                SelectorManager.LOG.debug("Exception while notifying connection " + connection, x);
            }
            throw x;
        }
    }
    
    public void connectionClosed(final Connection connection) {
        try {
            connection.onClose();
        }
        catch (Throwable x) {
            SelectorManager.LOG.debug("Exception while notifying connection " + connection, x);
        }
    }
    
    protected boolean finishConnect(final SocketChannel channel) throws IOException {
        return channel.finishConnect();
    }
    
    protected void connectionFailed(final SocketChannel channel, final Throwable ex, final Object attachment) {
        SelectorManager.LOG.warn(String.format("%s - %s", channel, attachment), ex);
    }
    
    protected abstract EndPoint newEndPoint(final SocketChannel p0, final ManagedSelector p1, final SelectionKey p2) throws IOException;
    
    public abstract Connection newConnection(final SocketChannel p0, final EndPoint p1, final Object p2) throws IOException;
    
    static {
        LOG = Log.getLogger(SelectorManager.class);
    }
}
