// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import org.eclipse.jetty.util.StringUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.FutureCallback;
import java.util.concurrent.Future;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashMap;
import org.eclipse.jetty.util.log.Log;
import java.util.concurrent.CountDownLatch;
import org.eclipse.jetty.io.EndPoint;
import java.util.Set;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.Executor;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

@ManagedObject("Abstract implementation of the Connector Interface")
public abstract class AbstractConnector extends ContainerLifeCycle implements Connector, Dumpable
{
    protected final Logger LOG;
    private final Map<String, ConnectionFactory> _factories;
    private final Server _server;
    private final Executor _executor;
    private final Scheduler _scheduler;
    private final ByteBufferPool _byteBufferPool;
    private final Thread[] _acceptors;
    private final Set<EndPoint> _endpoints;
    private final Set<EndPoint> _immutableEndPoints;
    private volatile CountDownLatch _stopping;
    private long _idleTimeout;
    private String _defaultProtocol;
    private ConnectionFactory _defaultConnectionFactory;
    private String _name;
    private int _acceptorPriorityDelta;
    
    public AbstractConnector(final Server server, final Executor executor, Scheduler scheduler, ByteBufferPool pool, int acceptors, final ConnectionFactory... factories) {
        this.LOG = Log.getLogger(AbstractConnector.class);
        this._factories = new LinkedHashMap<String, ConnectionFactory>();
        this._endpoints = Collections.newSetFromMap(new ConcurrentHashMap<EndPoint, Boolean>());
        this._immutableEndPoints = Collections.unmodifiableSet((Set<? extends EndPoint>)this._endpoints);
        this._idleTimeout = 30000L;
        this._server = server;
        this._executor = ((executor != null) ? executor : this._server.getThreadPool());
        if (scheduler == null) {
            scheduler = this._server.getBean(Scheduler.class);
        }
        this._scheduler = ((scheduler != null) ? scheduler : new ScheduledExecutorScheduler());
        if (pool == null) {
            pool = this._server.getBean(ByteBufferPool.class);
        }
        this._byteBufferPool = ((pool != null) ? pool : new ArrayByteBufferPool());
        this.addBean(this._server, false);
        this.addBean(this._executor);
        if (executor == null) {
            this.unmanage(this._executor);
        }
        this.addBean(this._scheduler);
        this.addBean(this._byteBufferPool);
        for (final ConnectionFactory factory : factories) {
            this.addConnectionFactory(factory);
        }
        final int cores = Runtime.getRuntime().availableProcessors();
        if (acceptors < 0) {
            acceptors = Math.max(1, Math.min(4, cores / 8));
        }
        if (acceptors > cores) {
            this.LOG.warn("Acceptors should be <= availableProcessors: " + this, new Object[0]);
        }
        this._acceptors = new Thread[acceptors];
    }
    
    @Override
    public Server getServer() {
        return this._server;
    }
    
    @Override
    public Executor getExecutor() {
        return this._executor;
    }
    
    @Override
    public ByteBufferPool getByteBufferPool() {
        return this._byteBufferPool;
    }
    
    @ManagedAttribute("Idle timeout")
    @Override
    public long getIdleTimeout() {
        return this._idleTimeout;
    }
    
    public void setIdleTimeout(final long idleTimeout) {
        this._idleTimeout = idleTimeout;
    }
    
    @ManagedAttribute("number of acceptor threads")
    public int getAcceptors() {
        return this._acceptors.length;
    }
    
    @Override
    protected void doStart() throws Exception {
        this._defaultConnectionFactory = this.getConnectionFactory(this._defaultProtocol);
        if (this._defaultConnectionFactory == null) {
            throw new IllegalStateException("No protocol factory for default protocol: " + this._defaultProtocol);
        }
        final SslConnectionFactory ssl = this.getConnectionFactory(SslConnectionFactory.class);
        if (ssl != null) {
            final String next = ssl.getNextProtocol();
            final ConnectionFactory cf = this.getConnectionFactory(next);
            if (cf == null) {
                throw new IllegalStateException("No protocol factory for SSL next protocol: " + next);
            }
        }
        super.doStart();
        this._stopping = new CountDownLatch(this._acceptors.length);
        for (int i = 0; i < this._acceptors.length; ++i) {
            final Acceptor a = new Acceptor(i);
            this.addBean(a);
            this.getExecutor().execute(a);
        }
        this.LOG.info("Started {}", this);
    }
    
    protected void interruptAcceptors() {
        synchronized (this) {
            for (final Thread thread : this._acceptors) {
                if (thread != null) {
                    thread.interrupt();
                }
            }
        }
    }
    
    @Override
    public Future<Void> shutdown() {
        return new FutureCallback(true);
    }
    
    @Override
    protected void doStop() throws Exception {
        this.interruptAcceptors();
        final long stopTimeout = this.getStopTimeout();
        final CountDownLatch stopping = this._stopping;
        if (stopTimeout > 0L && stopping != null) {
            stopping.await(stopTimeout, TimeUnit.MILLISECONDS);
        }
        this._stopping = null;
        super.doStop();
        for (final Acceptor a : this.getBeans(Acceptor.class)) {
            this.removeBean(a);
        }
        this.LOG.info("Stopped {}", this);
    }
    
    public void join() throws InterruptedException {
        this.join(0L);
    }
    
    public void join(final long timeout) throws InterruptedException {
        synchronized (this) {
            for (final Thread thread : this._acceptors) {
                if (thread != null) {
                    thread.join(timeout);
                }
            }
        }
    }
    
    protected abstract void accept(final int p0) throws IOException, InterruptedException;
    
    protected boolean isAccepting() {
        return this.isRunning();
    }
    
    @Override
    public ConnectionFactory getConnectionFactory(final String protocol) {
        synchronized (this._factories) {
            return this._factories.get(StringUtil.asciiToLowerCase(protocol));
        }
    }
    
    @Override
    public <T> T getConnectionFactory(final Class<T> factoryType) {
        synchronized (this._factories) {
            for (final ConnectionFactory f : this._factories.values()) {
                if (factoryType.isAssignableFrom(f.getClass())) {
                    return (T)f;
                }
            }
            return null;
        }
    }
    
    public void addConnectionFactory(final ConnectionFactory factory) {
        synchronized (this._factories) {
            final Set<ConnectionFactory> to_remove = new HashSet<ConnectionFactory>();
            for (String key : factory.getProtocols()) {
                key = StringUtil.asciiToLowerCase(key);
                final ConnectionFactory old = this._factories.remove(key);
                if (old != null) {
                    if (old.getProtocol().equals(this._defaultProtocol)) {
                        this._defaultProtocol = null;
                    }
                    to_remove.add(old);
                }
                this._factories.put(key, factory);
            }
            for (final ConnectionFactory f : this._factories.values()) {
                to_remove.remove(f);
            }
            for (final ConnectionFactory old2 : to_remove) {
                this.removeBean(old2);
                if (this.LOG.isDebugEnabled()) {
                    this.LOG.debug("{} removed {}", this, old2);
                }
            }
            this.addBean(factory);
            if (this._defaultProtocol == null) {
                this._defaultProtocol = factory.getProtocol();
            }
            if (this.LOG.isDebugEnabled()) {
                this.LOG.debug("{} added {}", this, factory);
            }
        }
    }
    
    public void addFirstConnectionFactory(final ConnectionFactory factory) {
        synchronized (this._factories) {
            final List<ConnectionFactory> existings = new ArrayList<ConnectionFactory>(this._factories.values());
            this._factories.clear();
            this.addConnectionFactory(factory);
            for (final ConnectionFactory existing : existings) {
                this.addConnectionFactory(existing);
            }
            this._defaultProtocol = factory.getProtocol();
        }
    }
    
    public void addIfAbsentConnectionFactory(final ConnectionFactory factory) {
        synchronized (this._factories) {
            final String key = StringUtil.asciiToLowerCase(factory.getProtocol());
            if (this._factories.containsKey(key)) {
                if (this.LOG.isDebugEnabled()) {
                    this.LOG.debug("{} addIfAbsent ignored {}", this, factory);
                }
            }
            else {
                this._factories.put(key, factory);
                this.addBean(factory);
                if (this._defaultProtocol == null) {
                    this._defaultProtocol = factory.getProtocol();
                }
                if (this.LOG.isDebugEnabled()) {
                    this.LOG.debug("{} addIfAbsent added {}", this, factory);
                }
            }
        }
    }
    
    public ConnectionFactory removeConnectionFactory(final String protocol) {
        synchronized (this._factories) {
            final ConnectionFactory factory = this._factories.remove(StringUtil.asciiToLowerCase(protocol));
            this.removeBean(factory);
            return factory;
        }
    }
    
    @Override
    public Collection<ConnectionFactory> getConnectionFactories() {
        synchronized (this._factories) {
            return this._factories.values();
        }
    }
    
    public void setConnectionFactories(final Collection<ConnectionFactory> factories) {
        synchronized (this._factories) {
            final List<ConnectionFactory> existing = new ArrayList<ConnectionFactory>(this._factories.values());
            for (final ConnectionFactory factory : existing) {
                this.removeConnectionFactory(factory.getProtocol());
            }
            for (final ConnectionFactory factory : factories) {
                if (factory != null) {
                    this.addConnectionFactory(factory);
                }
            }
        }
    }
    
    @ManagedAttribute("The priority delta to apply to acceptor threads")
    public int getAcceptorPriorityDelta() {
        return this._acceptorPriorityDelta;
    }
    
    public void setAcceptorPriorityDelta(final int acceptorPriorityDelta) {
        final int old = this._acceptorPriorityDelta;
        this._acceptorPriorityDelta = acceptorPriorityDelta;
        if (old != acceptorPriorityDelta && this.isStarted()) {
            for (final Thread thread : this._acceptors) {
                thread.setPriority(Math.max(1, Math.min(10, thread.getPriority() - old + acceptorPriorityDelta)));
            }
        }
    }
    
    @ManagedAttribute("Protocols supported by this connector")
    @Override
    public List<String> getProtocols() {
        synchronized (this._factories) {
            return new ArrayList<String>(this._factories.keySet());
        }
    }
    
    public void clearConnectionFactories() {
        synchronized (this._factories) {
            this._factories.clear();
        }
    }
    
    @ManagedAttribute("This connector's default protocol")
    public String getDefaultProtocol() {
        return this._defaultProtocol;
    }
    
    public void setDefaultProtocol(final String defaultProtocol) {
        this._defaultProtocol = StringUtil.asciiToLowerCase(defaultProtocol);
        if (this.isRunning()) {
            this._defaultConnectionFactory = this.getConnectionFactory(this._defaultProtocol);
        }
    }
    
    @Override
    public ConnectionFactory getDefaultConnectionFactory() {
        if (this.isStarted()) {
            return this._defaultConnectionFactory;
        }
        return this.getConnectionFactory(this._defaultProtocol);
    }
    
    protected boolean handleAcceptFailure(final Throwable previous, final Throwable current) {
        if (this.isAccepting()) {
            if (previous == null) {
                this.LOG.warn(current);
            }
            else {
                this.LOG.debug(current);
            }
            try {
                Thread.sleep(1000L);
                return true;
            }
            catch (Throwable x) {
                return false;
            }
        }
        this.LOG.ignore(current);
        return false;
    }
    
    @Override
    public Collection<EndPoint> getConnectedEndPoints() {
        return this._immutableEndPoints;
    }
    
    protected void onEndPointOpened(final EndPoint endp) {
        this._endpoints.add(endp);
    }
    
    protected void onEndPointClosed(final EndPoint endp) {
        this._endpoints.remove(endp);
    }
    
    @Override
    public Scheduler getScheduler() {
        return this._scheduler;
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x{%s,%s}", (this._name == null) ? this.getClass().getSimpleName() : this._name, this.hashCode(), this.getDefaultProtocol(), this.getProtocols());
    }
    
    private class Acceptor implements Runnable
    {
        private final int _id;
        private String _name;
        
        private Acceptor(final int id) {
            this._id = id;
        }
        
        @Override
        public void run() {
            final Thread thread = Thread.currentThread();
            final String name = thread.getName();
            thread.setName(this._name = String.format("%s-acceptor-%d@%x-%s", name, this._id, this.hashCode(), AbstractConnector.this.toString()));
            final int priority = thread.getPriority();
            if (AbstractConnector.this._acceptorPriorityDelta != 0) {
                thread.setPriority(Math.max(1, Math.min(10, priority + AbstractConnector.this._acceptorPriorityDelta)));
            }
            synchronized (AbstractConnector.this) {
                AbstractConnector.this._acceptors[this._id] = thread;
            }
            try {
                Throwable exception = null;
                while (AbstractConnector.this.isAccepting()) {
                    try {
                        AbstractConnector.this.accept(this._id);
                        exception = null;
                    }
                    catch (Throwable x) {
                        if (!AbstractConnector.this.handleAcceptFailure(exception, x)) {
                            break;
                        }
                        exception = x;
                    }
                }
            }
            finally {
                thread.setName(name);
                if (AbstractConnector.this._acceptorPriorityDelta != 0) {
                    thread.setPriority(priority);
                }
                synchronized (AbstractConnector.this) {
                    AbstractConnector.this._acceptors[this._id] = null;
                }
                final CountDownLatch stopping = AbstractConnector.this._stopping;
                if (stopping != null) {
                    stopping.countDown();
                }
            }
        }
        
        @Override
        public String toString() {
            final String name = this._name;
            if (name == null) {
                return String.format("acceptor-%d@%x", this._id, this.hashCode());
            }
            return name;
        }
    }
}
