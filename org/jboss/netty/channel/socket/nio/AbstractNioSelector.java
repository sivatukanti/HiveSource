// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.jboss.netty.util.internal.DeadLockProofWorker;
import org.jboss.netty.channel.ChannelException;
import java.nio.channels.SelectableChannel;
import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SocketChannel;
import java.nio.channels.DatagramChannel;
import java.util.Iterator;
import java.util.ConcurrentModificationException;
import java.nio.channels.SelectionKey;
import java.util.concurrent.RejectedExecutionException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channel;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jboss.netty.util.ThreadNameDeterminer;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.channels.Selector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import org.jboss.netty.logging.InternalLogger;
import java.util.concurrent.atomic.AtomicInteger;

abstract class AbstractNioSelector implements NioSelector
{
    private static final AtomicInteger nextId;
    private final int id;
    protected static final InternalLogger logger;
    private static final int CLEANUP_INTERVAL = 256;
    private final Executor executor;
    protected volatile Thread thread;
    final CountDownLatch startupLatch;
    protected volatile Selector selector;
    protected final AtomicBoolean wakenUp;
    private final Queue<Runnable> taskQueue;
    private volatile int cancelledKeys;
    private final CountDownLatch shutdownLatch;
    private volatile boolean shutdown;
    
    AbstractNioSelector(final Executor executor) {
        this(executor, null);
    }
    
    AbstractNioSelector(final Executor executor, final ThreadNameDeterminer determiner) {
        this.id = AbstractNioSelector.nextId.incrementAndGet();
        this.startupLatch = new CountDownLatch(1);
        this.wakenUp = new AtomicBoolean();
        this.taskQueue = new ConcurrentLinkedQueue<Runnable>();
        this.shutdownLatch = new CountDownLatch(1);
        this.executor = executor;
        this.openSelector(determiner);
    }
    
    public void register(final Channel channel, final ChannelFuture future) {
        final Runnable task = this.createRegisterTask(channel, future);
        this.registerTask(task);
    }
    
    protected final void registerTask(final Runnable task) {
        this.taskQueue.add(task);
        final Selector selector = this.selector;
        if (selector != null) {
            if (this.wakenUp.compareAndSet(false, true)) {
                selector.wakeup();
            }
        }
        else if (this.taskQueue.remove(task)) {
            throw new RejectedExecutionException("Worker has already been shutdown");
        }
    }
    
    protected final boolean isIoThread() {
        return Thread.currentThread() == this.thread;
    }
    
    public void rebuildSelector() {
        if (!this.isIoThread()) {
            this.taskQueue.add(new Runnable() {
                public void run() {
                    AbstractNioSelector.this.rebuildSelector();
                }
            });
            return;
        }
        final Selector oldSelector = this.selector;
        if (oldSelector == null) {
            return;
        }
        Selector newSelector;
        try {
            newSelector = SelectorUtil.open();
        }
        catch (Exception e) {
            AbstractNioSelector.logger.warn("Failed to create a new Selector.", e);
            return;
        }
        int nChannels = 0;
        while (true) {
            try {
                for (final SelectionKey key : oldSelector.keys()) {
                    try {
                        if (key.channel().keyFor(newSelector) != null) {
                            continue;
                        }
                        final int interestOps = key.interestOps();
                        key.cancel();
                        key.channel().register(newSelector, interestOps, key.attachment());
                        ++nChannels;
                    }
                    catch (Exception e2) {
                        AbstractNioSelector.logger.warn("Failed to re-register a Channel to the new Selector,", e2);
                        this.close(key);
                    }
                }
            }
            catch (ConcurrentModificationException e3) {
                continue;
            }
            break;
        }
        this.selector = newSelector;
        try {
            oldSelector.close();
        }
        catch (Throwable t) {
            if (AbstractNioSelector.logger.isWarnEnabled()) {
                AbstractNioSelector.logger.warn("Failed to close the old Selector.", t);
            }
        }
        AbstractNioSelector.logger.info("Migrated " + nChannels + " channel(s) to the new Selector,");
    }
    
    public void run() {
        this.thread = Thread.currentThread();
        this.startupLatch.countDown();
        int selectReturnsImmediately = 0;
        Selector selector = this.selector;
        if (selector == null) {
            return;
        }
        final long minSelectTimeout = SelectorUtil.SELECT_TIMEOUT_NANOS * 80L / 100L;
        boolean wakenupFromLoop = false;
        while (true) {
            this.wakenUp.set(false);
            try {
                final long beforeSelect = System.nanoTime();
                final int selected = this.select(selector);
                if (selected == 0 && !wakenupFromLoop && !this.wakenUp.get()) {
                    final long timeBlocked = System.nanoTime() - beforeSelect;
                    if (timeBlocked < minSelectTimeout) {
                        boolean notConnected = false;
                        for (final SelectionKey key : selector.keys()) {
                            final SelectableChannel ch = key.channel();
                            try {
                                if ((ch instanceof DatagramChannel || ch.isOpen()) && (!(ch instanceof SocketChannel) || ((SocketChannel)ch).isConnected() || ((SocketChannel)ch).isConnectionPending())) {
                                    continue;
                                }
                                notConnected = true;
                                key.cancel();
                            }
                            catch (CancelledKeyException ex) {}
                        }
                        if (notConnected) {
                            selectReturnsImmediately = 0;
                        }
                        else if (Thread.interrupted() && !this.shutdown) {
                            if (AbstractNioSelector.logger.isDebugEnabled()) {
                                AbstractNioSelector.logger.debug("Selector.select() returned prematurely because the I/O thread has been interrupted. Use shutdown() to shut the NioSelector down.");
                            }
                            selectReturnsImmediately = 0;
                        }
                        else {
                            ++selectReturnsImmediately;
                        }
                    }
                    else {
                        selectReturnsImmediately = 0;
                    }
                }
                else {
                    selectReturnsImmediately = 0;
                }
                if (SelectorUtil.EPOLL_BUG_WORKAROUND) {
                    if (selectReturnsImmediately == 1024) {
                        this.rebuildSelector();
                        selector = this.selector;
                        selectReturnsImmediately = 0;
                        wakenupFromLoop = false;
                        continue;
                    }
                }
                else {
                    selectReturnsImmediately = 0;
                }
                if (this.wakenUp.get()) {
                    wakenupFromLoop = true;
                    selector.wakeup();
                }
                else {
                    wakenupFromLoop = false;
                }
                this.cancelledKeys = 0;
                this.processTaskQueue();
                selector = this.selector;
                if (this.shutdown) {
                    this.selector = null;
                    this.processTaskQueue();
                    for (final SelectionKey k : selector.keys()) {
                        this.close(k);
                    }
                    try {
                        selector.close();
                    }
                    catch (IOException e) {
                        AbstractNioSelector.logger.warn("Failed to close a selector.", e);
                    }
                    this.shutdownLatch.countDown();
                    break;
                }
                this.process(selector);
            }
            catch (Throwable t) {
                AbstractNioSelector.logger.warn("Unexpected exception in the selector loop.", t);
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException ex2) {}
            }
        }
    }
    
    private void openSelector(final ThreadNameDeterminer determiner) {
        try {
            this.selector = SelectorUtil.open();
        }
        catch (Throwable t) {
            throw new ChannelException("Failed to create a selector.", t);
        }
        boolean success = false;
        try {
            DeadLockProofWorker.start(this.executor, this.newThreadRenamingRunnable(this.id, determiner));
            success = true;
        }
        finally {
            if (!success) {
                try {
                    this.selector.close();
                }
                catch (Throwable t2) {
                    AbstractNioSelector.logger.warn("Failed to close a selector.", t2);
                }
                this.selector = null;
            }
        }
        assert this.selector != null && this.selector.isOpen();
    }
    
    private void processTaskQueue() {
        while (true) {
            final Runnable task = this.taskQueue.poll();
            if (task == null) {
                break;
            }
            task.run();
            try {
                this.cleanUpCancelledKeys();
            }
            catch (IOException ex) {}
        }
    }
    
    protected final void increaseCancelledKeys() {
        ++this.cancelledKeys;
    }
    
    protected final boolean cleanUpCancelledKeys() throws IOException {
        if (this.cancelledKeys >= 256) {
            this.cancelledKeys = 0;
            this.selector.selectNow();
            return true;
        }
        return false;
    }
    
    public void shutdown() {
        if (this.isIoThread()) {
            throw new IllegalStateException("Must not be called from a I/O-Thread to prevent deadlocks!");
        }
        final Selector selector = this.selector;
        this.shutdown = true;
        if (selector != null) {
            selector.wakeup();
        }
        try {
            this.shutdownLatch.await();
        }
        catch (InterruptedException e) {
            AbstractNioSelector.logger.error("Interrupted while wait for resources to be released #" + this.id);
            Thread.currentThread().interrupt();
        }
    }
    
    protected abstract void process(final Selector p0) throws IOException;
    
    protected int select(final Selector selector) throws IOException {
        return SelectorUtil.select(selector);
    }
    
    protected abstract void close(final SelectionKey p0);
    
    protected abstract ThreadRenamingRunnable newThreadRenamingRunnable(final int p0, final ThreadNameDeterminer p1);
    
    protected abstract Runnable createRegisterTask(final Channel p0, final ChannelFuture p1);
    
    static {
        nextId = new AtomicInteger();
        logger = InternalLoggerFactory.getInstance(AbstractNioSelector.class);
    }
}
