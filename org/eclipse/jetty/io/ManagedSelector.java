// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.CountDownLatch;
import java.nio.channels.CancelledKeyException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.jetty.util.log.Log;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import java.io.Closeable;
import java.nio.channels.ServerSocketChannel;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.io.IOException;
import java.util.ArrayDeque;
import java.nio.channels.Selector;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import java.util.Queue;
import org.eclipse.jetty.util.thread.Locker;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class ManagedSelector extends AbstractLifeCycle implements Runnable, Dumpable
{
    private static final Logger LOG;
    private final Locker _locker;
    private boolean _selecting;
    private final Queue<Runnable> _actions;
    private final SelectorManager _selectorManager;
    private final int _id;
    private final ExecutionStrategy _strategy;
    private Selector _selector;
    
    public ManagedSelector(final SelectorManager selectorManager, final int id) {
        this(selectorManager, id, ExecutionStrategy.Factory.getDefault());
    }
    
    public ManagedSelector(final SelectorManager selectorManager, final int id, final ExecutionStrategy.Factory executionFactory) {
        this._locker = new Locker();
        this._selecting = false;
        this._actions = new ArrayDeque<Runnable>();
        this._selectorManager = selectorManager;
        this._id = id;
        this._strategy = executionFactory.newExecutionStrategy(new SelectorProducer(), selectorManager.getExecutor());
        this.setStopTimeout(5000L);
    }
    
    public ExecutionStrategy getExecutionStrategy() {
        return this._strategy;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        this._selector = this.newSelector();
        this._selectorManager.execute(this);
    }
    
    protected Selector newSelector() throws IOException {
        return Selector.open();
    }
    
    public int size() {
        final Selector s = this._selector;
        if (s == null) {
            return 0;
        }
        return s.keys().size();
    }
    
    @Override
    protected void doStop() throws Exception {
        if (ManagedSelector.LOG.isDebugEnabled()) {
            ManagedSelector.LOG.debug("Stopping {}", this);
        }
        final CloseEndPoints close_endps = new CloseEndPoints();
        this.submit(close_endps);
        close_endps.await(this.getStopTimeout());
        super.doStop();
        final CloseSelector close_selector = new CloseSelector();
        this.submit(close_selector);
        close_selector.await(this.getStopTimeout());
        if (ManagedSelector.LOG.isDebugEnabled()) {
            ManagedSelector.LOG.debug("Stopped {}", this);
        }
    }
    
    public void submit(final Runnable change) {
        if (ManagedSelector.LOG.isDebugEnabled()) {
            ManagedSelector.LOG.debug("Queued change {} on {}", change, this);
        }
        Selector selector = null;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            this._actions.offer(change);
            if (this._selecting) {
                selector = this._selector;
                this._selecting = false;
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        if (selector != null) {
            selector.wakeup();
        }
    }
    
    @Override
    public void run() {
        this._strategy.execute();
    }
    
    private Runnable processConnect(final SelectionKey key, final Connect connect) {
        final SocketChannel channel = (SocketChannel)key.channel();
        try {
            key.attach(connect.attachment);
            final boolean connected = this._selectorManager.finishConnect(channel);
            if (ManagedSelector.LOG.isDebugEnabled()) {
                ManagedSelector.LOG.debug("Connected {} {}", connected, channel);
            }
            if (!connected) {
                throw new ConnectException();
            }
            if (connect.timeout.cancel()) {
                key.interestOps(0);
                return new CreateEndPoint(channel, key) {
                    @Override
                    protected void failed(final Throwable failure) {
                        super.failed(failure);
                        connect.failed(failure);
                    }
                };
            }
            throw new SocketTimeoutException("Concurrent Connect Timeout");
        }
        catch (Throwable x) {
            connect.failed(x);
            return null;
        }
    }
    
    private void processAccept(final SelectionKey key) {
        final ServerSocketChannel server = (ServerSocketChannel)key.channel();
        SocketChannel channel = null;
        try {
            while ((channel = server.accept()) != null) {
                this._selectorManager.accepted(channel);
            }
        }
        catch (Throwable x) {
            this.closeNoExceptions(channel);
            ManagedSelector.LOG.warn("Accept failed for channel " + channel, x);
        }
    }
    
    private void closeNoExceptions(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (Throwable x) {
            ManagedSelector.LOG.ignore(x);
        }
    }
    
    private EndPoint createEndPoint(final SocketChannel channel, final SelectionKey selectionKey) throws IOException {
        final EndPoint endPoint = this._selectorManager.newEndPoint(channel, this, selectionKey);
        final Connection connection = this._selectorManager.newConnection(channel, endPoint, selectionKey.attachment());
        endPoint.setConnection(connection);
        selectionKey.attach(endPoint);
        this._selectorManager.endPointOpened(endPoint);
        this._selectorManager.connectionOpened(connection);
        if (ManagedSelector.LOG.isDebugEnabled()) {
            ManagedSelector.LOG.debug("Created {}", endPoint);
        }
        return endPoint;
    }
    
    public void destroyEndPoint(final EndPoint endPoint) {
        final Connection connection = endPoint.getConnection();
        this.submit(new DestroyEndPoint(endPoint));
    }
    
    @Override
    public String dump() {
        return ContainerLifeCycle.dump(this);
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        out.append(String.valueOf(this)).append(" id=").append(String.valueOf(this._id)).append(System.lineSeparator());
        final Selector selector = this._selector;
        if (selector != null && selector.isOpen()) {
            final ArrayList<Object> dump = new ArrayList<Object>(selector.keys().size() * 2);
            final DumpKeys dumpKeys = new DumpKeys((List)dump);
            this.submit(dumpKeys);
            dumpKeys.await(5L, TimeUnit.SECONDS);
            ContainerLifeCycle.dump(out, indent, dump);
        }
    }
    
    @Override
    public String toString() {
        final Selector selector = this._selector;
        return String.format("%s id=%s keys=%d selected=%d", super.toString(), this._id, (selector != null && selector.isOpen()) ? selector.keys().size() : -1, (selector != null && selector.isOpen()) ? selector.selectedKeys().size() : -1);
    }
    
    private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable exception) {
                x0.addSuppressed(exception);
            }
        }
        else {
            x1.close();
        }
    }
    
    static {
        LOG = Log.getLogger(ManagedSelector.class);
    }
    
    private class SelectorProducer implements ExecutionStrategy.Producer
    {
        private Set<SelectionKey> _keys;
        private Iterator<SelectionKey> _cursor;
        
        private SelectorProducer() {
            this._keys = Collections.emptySet();
            this._cursor = Collections.emptyIterator();
        }
        
        @Override
        public Runnable produce() {
            while (true) {
                final Runnable task = this.processSelected();
                if (task != null) {
                    return task;
                }
                final Runnable action = this.runActions();
                if (action != null) {
                    return action;
                }
                this.update();
                if (!this.select()) {
                    return null;
                }
            }
        }
        
        private Runnable runActions() {
            Runnable action;
            while (true) {
                final Locker.Lock lock = ManagedSelector.this._locker.lock();
                Throwable x0 = null;
                try {
                    action = ManagedSelector.this._actions.poll();
                    if (action == null) {
                        ManagedSelector.this._selecting = true;
                        return null;
                    }
                }
                catch (Throwable t) {
                    x0 = t;
                    throw t;
                }
                finally {
                    if (lock != null) {
                        $closeResource(x0, lock);
                    }
                }
                if (action instanceof Product) {
                    break;
                }
                this.runChange(action);
            }
            return action;
        }
        
        private void runChange(final Runnable change) {
            try {
                if (ManagedSelector.LOG.isDebugEnabled()) {
                    ManagedSelector.LOG.debug("Running change {}", change);
                }
                change.run();
            }
            catch (Throwable x) {
                ManagedSelector.LOG.debug("Could not run change " + change, x);
            }
        }
        
        private boolean select() {
            try {
                final Selector selector = ManagedSelector.this._selector;
                if (selector != null && selector.isOpen()) {
                    if (ManagedSelector.LOG.isDebugEnabled()) {
                        ManagedSelector.LOG.debug("Selector loop waiting on select", new Object[0]);
                    }
                    final int selected = selector.select();
                    if (ManagedSelector.LOG.isDebugEnabled()) {
                        ManagedSelector.LOG.debug("Selector loop woken up from select, {}/{} selected", selected, selector.keys().size());
                    }
                    final Locker.Lock lock = ManagedSelector.this._locker.lock();
                    Throwable x2 = null;
                    try {
                        ManagedSelector.this._selecting = false;
                    }
                    catch (Throwable t) {
                        x2 = t;
                        throw t;
                    }
                    finally {
                        if (lock != null) {
                            $closeResource(x2, lock);
                        }
                    }
                    this._keys = selector.selectedKeys();
                    this._cursor = this._keys.iterator();
                    return true;
                }
            }
            catch (Throwable x) {
                ManagedSelector.this.closeNoExceptions(ManagedSelector.this._selector);
                if (ManagedSelector.this.isRunning()) {
                    ManagedSelector.LOG.warn(x);
                }
                else {
                    ManagedSelector.LOG.debug(x);
                }
            }
            return false;
        }
        
        private Runnable processSelected() {
            while (this._cursor.hasNext()) {
                final SelectionKey key = this._cursor.next();
                if (key.isValid()) {
                    final Object attachment = key.attachment();
                    try {
                        if (attachment instanceof SelectableEndPoint) {
                            final Runnable task = ((SelectableEndPoint)attachment).onSelected();
                            if (task != null) {
                                return task;
                            }
                            continue;
                        }
                        else if (key.isConnectable()) {
                            final Runnable task = ManagedSelector.this.processConnect(key, (Connect)attachment);
                            if (task != null) {
                                return task;
                            }
                            continue;
                        }
                        else {
                            if (!key.isAcceptable()) {
                                throw new IllegalStateException("key=" + key + ", att=" + attachment + ", iOps=" + key.interestOps() + ", rOps=" + key.readyOps());
                            }
                            ManagedSelector.this.processAccept(key);
                        }
                    }
                    catch (CancelledKeyException x2) {
                        ManagedSelector.LOG.debug("Ignoring cancelled key for channel {}", key.channel());
                        if (!(attachment instanceof EndPoint)) {
                            continue;
                        }
                        ManagedSelector.this.closeNoExceptions((Closeable)attachment);
                    }
                    catch (Throwable x) {
                        ManagedSelector.LOG.warn("Could not process key for channel " + key.channel(), x);
                        if (!(attachment instanceof EndPoint)) {
                            continue;
                        }
                        ManagedSelector.this.closeNoExceptions((Closeable)attachment);
                    }
                }
                else {
                    if (ManagedSelector.LOG.isDebugEnabled()) {
                        ManagedSelector.LOG.debug("Selector loop ignoring invalid key for channel {}", key.channel());
                    }
                    final Object attachment = key.attachment();
                    if (!(attachment instanceof EndPoint)) {
                        continue;
                    }
                    ManagedSelector.this.closeNoExceptions((Closeable)attachment);
                }
            }
            return null;
        }
        
        private void update() {
            for (final SelectionKey key : this._keys) {
                this.updateKey(key);
            }
            this._keys.clear();
        }
        
        private void updateKey(final SelectionKey key) {
            final Object attachment = key.attachment();
            if (attachment instanceof SelectableEndPoint) {
                ((SelectableEndPoint)attachment).updateKey();
            }
        }
        
        private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
            if (x0 != null) {
                try {
                    x1.close();
                }
                catch (Throwable exception) {
                    x0.addSuppressed(exception);
                }
            }
            else {
                x1.close();
            }
        }
    }
    
    private class DumpKeys implements Runnable
    {
        private final CountDownLatch latch;
        private final List<Object> _dumps;
        
        private DumpKeys(final List<Object> dumps) {
            this.latch = new CountDownLatch(1);
            this._dumps = dumps;
        }
        
        @Override
        public void run() {
            final Selector selector = ManagedSelector.this._selector;
            if (selector != null && selector.isOpen()) {
                final Set<SelectionKey> keys = selector.keys();
                this._dumps.add(selector + " keys=" + keys.size());
                for (final SelectionKey key : keys) {
                    try {
                        this._dumps.add(String.format("SelectionKey@%x{i=%d}->%s", key.hashCode(), key.interestOps(), key.attachment()));
                    }
                    catch (Throwable x) {
                        ManagedSelector.LOG.ignore(x);
                    }
                }
            }
            this.latch.countDown();
        }
        
        public boolean await(final long timeout, final TimeUnit unit) {
            try {
                return this.latch.await(timeout, unit);
            }
            catch (InterruptedException x) {
                return false;
            }
        }
    }
    
    class Acceptor implements Runnable
    {
        private final ServerSocketChannel _channel;
        
        public Acceptor(final ServerSocketChannel channel) {
            this._channel = channel;
        }
        
        @Override
        public void run() {
            try {
                final SelectionKey key = this._channel.register(ManagedSelector.this._selector, 16, null);
                if (ManagedSelector.LOG.isDebugEnabled()) {
                    ManagedSelector.LOG.debug("{} acceptor={}", this, key);
                }
            }
            catch (Throwable x) {
                ManagedSelector.this.closeNoExceptions(this._channel);
                ManagedSelector.LOG.warn(x);
            }
        }
    }
    
    class Accept implements Runnable, Closeable
    {
        private final SocketChannel channel;
        private final Object attachment;
        
        Accept(final SocketChannel channel, final Object attachment) {
            this.channel = channel;
            this.attachment = attachment;
        }
        
        @Override
        public void close() {
            ManagedSelector.LOG.debug("closed accept of {}", this.channel);
            ManagedSelector.this.closeNoExceptions(this.channel);
        }
        
        @Override
        public void run() {
            try {
                final SelectionKey key = this.channel.register(ManagedSelector.this._selector, 0, this.attachment);
                ManagedSelector.this.submit(new CreateEndPoint(this.channel, key));
            }
            catch (Throwable x) {
                ManagedSelector.this.closeNoExceptions(this.channel);
                ManagedSelector.LOG.debug(x);
            }
        }
    }
    
    private class CreateEndPoint implements Product, Closeable
    {
        private final SocketChannel channel;
        private final SelectionKey key;
        
        public CreateEndPoint(final SocketChannel channel, final SelectionKey key) {
            this.channel = channel;
            this.key = key;
        }
        
        @Override
        public void run() {
            try {
                ManagedSelector.this.createEndPoint(this.channel, this.key);
            }
            catch (Throwable x) {
                ManagedSelector.LOG.debug(x);
                this.failed(x);
            }
        }
        
        @Override
        public void close() {
            ManagedSelector.LOG.debug("closed creation of {}", this.channel);
            ManagedSelector.this.closeNoExceptions(this.channel);
        }
        
        protected void failed(final Throwable failure) {
            ManagedSelector.this.closeNoExceptions(this.channel);
            ManagedSelector.LOG.debug(failure);
        }
    }
    
    class Connect implements Runnable
    {
        private final AtomicBoolean failed;
        private final SocketChannel channel;
        private final Object attachment;
        private final Scheduler.Task timeout;
        
        Connect(final SocketChannel channel, final Object attachment) {
            this.failed = new AtomicBoolean();
            this.channel = channel;
            this.attachment = attachment;
            this.timeout = ManagedSelector.this._selectorManager.getScheduler().schedule(new ConnectTimeout(this), ManagedSelector.this._selectorManager.getConnectTimeout(), TimeUnit.MILLISECONDS);
        }
        
        @Override
        public void run() {
            try {
                this.channel.register(ManagedSelector.this._selector, 8, this);
            }
            catch (Throwable x) {
                this.failed(x);
            }
        }
        
        private void failed(final Throwable failure) {
            if (this.failed.compareAndSet(false, true)) {
                this.timeout.cancel();
                ManagedSelector.this.closeNoExceptions(this.channel);
                ManagedSelector.this._selectorManager.connectionFailed(this.channel, failure, this.attachment);
            }
        }
    }
    
    private class ConnectTimeout implements Runnable
    {
        private final Connect connect;
        
        private ConnectTimeout(final Connect connect) {
            this.connect = connect;
        }
        
        @Override
        public void run() {
            final SocketChannel channel = this.connect.channel;
            if (channel.isConnectionPending()) {
                if (ManagedSelector.LOG.isDebugEnabled()) {
                    ManagedSelector.LOG.debug("Channel {} timed out while connecting, closing it", channel);
                }
                this.connect.failed(new SocketTimeoutException("Connect Timeout"));
            }
        }
    }
    
    private class CloseEndPoints implements Runnable
    {
        private final CountDownLatch _latch;
        private CountDownLatch _allClosed;
        
        private CloseEndPoints() {
            this._latch = new CountDownLatch(1);
        }
        
        @Override
        public void run() {
            final List<EndPoint> end_points = new ArrayList<EndPoint>();
            for (final SelectionKey key : ManagedSelector.this._selector.keys()) {
                if (key.isValid()) {
                    final Object attachment = key.attachment();
                    if (!(attachment instanceof EndPoint)) {
                        continue;
                    }
                    end_points.add((EndPoint)attachment);
                }
            }
            final int size = end_points.size();
            if (ManagedSelector.LOG.isDebugEnabled()) {
                ManagedSelector.LOG.debug("Closing {} endPoints on {}", size, ManagedSelector.this);
            }
            this._allClosed = new CountDownLatch(size);
            this._latch.countDown();
            for (final EndPoint endp : end_points) {
                ManagedSelector.this.submit(new EndPointCloser(endp, this._allClosed));
            }
            if (ManagedSelector.LOG.isDebugEnabled()) {
                ManagedSelector.LOG.debug("Closed {} endPoints on {}", size, ManagedSelector.this);
            }
        }
        
        public boolean await(final long timeout) {
            try {
                return this._latch.await(timeout, TimeUnit.MILLISECONDS) && this._allClosed.await(timeout, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException x) {
                return false;
            }
        }
    }
    
    private class EndPointCloser implements Product
    {
        private final EndPoint _endPoint;
        private final CountDownLatch _latch;
        
        private EndPointCloser(final EndPoint endPoint, final CountDownLatch latch) {
            this._endPoint = endPoint;
            this._latch = latch;
        }
        
        @Override
        public void run() {
            ManagedSelector.this.closeNoExceptions(this._endPoint.getConnection());
            this._latch.countDown();
        }
    }
    
    private class CloseSelector implements Runnable
    {
        private CountDownLatch _latch;
        
        private CloseSelector() {
            this._latch = new CountDownLatch(1);
        }
        
        @Override
        public void run() {
            final Selector selector = ManagedSelector.this._selector;
            ManagedSelector.this._selector = null;
            ManagedSelector.this.closeNoExceptions(selector);
            this._latch.countDown();
        }
        
        public boolean await(final long timeout) {
            try {
                return this._latch.await(timeout, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException x) {
                return false;
            }
        }
    }
    
    private class DestroyEndPoint implements Product, Closeable
    {
        private final EndPoint _endPoint;
        
        private DestroyEndPoint(final EndPoint endPoint) {
            this._endPoint = endPoint;
        }
        
        @Override
        public void run() {
            if (ManagedSelector.LOG.isDebugEnabled()) {
                ManagedSelector.LOG.debug("Destroyed {}", this._endPoint);
            }
            final Connection connection = this._endPoint.getConnection();
            if (connection != null) {
                ManagedSelector.this._selectorManager.connectionClosed(connection);
            }
            ManagedSelector.this._selectorManager.endPointClosed(this._endPoint);
        }
        
        @Override
        public void close() throws IOException {
            this.run();
        }
    }
    
    private interface Product extends Runnable
    {
    }
    
    public interface SelectableEndPoint extends EndPoint
    {
        Runnable onSelected();
        
        void updateKey();
    }
}
