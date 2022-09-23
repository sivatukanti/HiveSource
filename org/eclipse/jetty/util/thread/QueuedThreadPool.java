// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread;

import org.eclipse.jetty.util.log.Log;
import java.util.List;
import java.util.Collections;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import java.util.concurrent.RejectedExecutionException;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.util.Iterator;
import org.eclipse.jetty.util.BlockingArrayQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.annotation.Name;
import java.util.concurrent.BlockingQueue;
import org.eclipse.jetty.util.ConcurrentHashSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

@ManagedObject("A thread pool")
public class QueuedThreadPool extends AbstractLifeCycle implements ThreadPool.SizedThreadPool, Dumpable
{
    private static final Logger LOG;
    private final AtomicInteger _threadsStarted;
    private final AtomicInteger _threadsIdle;
    private final AtomicLong _lastShrink;
    private final ConcurrentHashSet<Thread> _threads;
    private final Object _joinLock;
    private final BlockingQueue<Runnable> _jobs;
    private final ThreadGroup _threadGroup;
    private String _name;
    private int _idleTimeout;
    private int _maxThreads;
    private int _minThreads;
    private int _priority;
    private boolean _daemon;
    private boolean _detailedDump;
    private int _lowThreadsThreshold;
    private Runnable _runnable;
    
    public QueuedThreadPool() {
        this(200);
    }
    
    public QueuedThreadPool(@Name("maxThreads") final int maxThreads) {
        this(maxThreads, 8);
    }
    
    public QueuedThreadPool(@Name("maxThreads") final int maxThreads, @Name("minThreads") final int minThreads) {
        this(maxThreads, minThreads, 60000);
    }
    
    public QueuedThreadPool(@Name("maxThreads") final int maxThreads, @Name("minThreads") final int minThreads, @Name("idleTimeout") final int idleTimeout) {
        this(maxThreads, minThreads, idleTimeout, null);
    }
    
    public QueuedThreadPool(@Name("maxThreads") final int maxThreads, @Name("minThreads") final int minThreads, @Name("idleTimeout") final int idleTimeout, @Name("queue") final BlockingQueue<Runnable> queue) {
        this(maxThreads, minThreads, idleTimeout, queue, null);
    }
    
    public QueuedThreadPool(@Name("maxThreads") final int maxThreads, @Name("minThreads") final int minThreads, @Name("idleTimeout") final int idleTimeout, @Name("queue") BlockingQueue<Runnable> queue, @Name("threadGroup") final ThreadGroup threadGroup) {
        this._threadsStarted = new AtomicInteger();
        this._threadsIdle = new AtomicInteger();
        this._lastShrink = new AtomicLong();
        this._threads = new ConcurrentHashSet<Thread>();
        this._joinLock = new Object();
        this._name = "qtp" + this.hashCode();
        this._priority = 5;
        this._daemon = false;
        this._detailedDump = false;
        this._lowThreadsThreshold = 1;
        this._runnable = new Runnable() {
            @Override
            public void run() {
                boolean shrink = false;
                boolean ignore = false;
                try {
                    Runnable job = (Runnable)QueuedThreadPool.this._jobs.poll();
                    if (job != null && QueuedThreadPool.this._threadsIdle.get() == 0) {
                        QueuedThreadPool.this.startThreads(1);
                    }
                Label_0420:
                    while (QueuedThreadPool.this.isRunning()) {
                        while (job != null && QueuedThreadPool.this.isRunning()) {
                            if (QueuedThreadPool.LOG.isDebugEnabled()) {
                                QueuedThreadPool.LOG.debug("run {}", job);
                            }
                            QueuedThreadPool.this.runJob(job);
                            if (QueuedThreadPool.LOG.isDebugEnabled()) {
                                QueuedThreadPool.LOG.debug("ran {}", job);
                            }
                            if (Thread.interrupted()) {
                                ignore = true;
                                break Label_0420;
                            }
                            job = (Runnable)QueuedThreadPool.this._jobs.poll();
                        }
                        try {
                            QueuedThreadPool.this._threadsIdle.incrementAndGet();
                            while (QueuedThreadPool.this.isRunning() && job == null) {
                                if (QueuedThreadPool.this._idleTimeout <= 0) {
                                    job = QueuedThreadPool.this._jobs.take();
                                }
                                else {
                                    final int size = QueuedThreadPool.this._threadsStarted.get();
                                    if (size > QueuedThreadPool.this._minThreads) {
                                        final long last = QueuedThreadPool.this._lastShrink.get();
                                        final long now = System.nanoTime();
                                        if ((last == 0L || now - last > TimeUnit.MILLISECONDS.toNanos(QueuedThreadPool.this._idleTimeout)) && QueuedThreadPool.this._lastShrink.compareAndSet(last, now) && QueuedThreadPool.this._threadsStarted.compareAndSet(size, size - 1)) {
                                            shrink = true;
                                            break Label_0420;
                                        }
                                    }
                                    job = QueuedThreadPool.this.idleJobPoll();
                                }
                            }
                        }
                        finally {
                            if (QueuedThreadPool.this._threadsIdle.decrementAndGet() == 0) {
                                QueuedThreadPool.this.startThreads(1);
                            }
                        }
                    }
                }
                catch (InterruptedException e) {
                    ignore = true;
                    QueuedThreadPool.LOG.ignore(e);
                }
                catch (Throwable e2) {
                    QueuedThreadPool.LOG.warn(e2);
                }
                finally {
                    if (!shrink && QueuedThreadPool.this.isRunning()) {
                        if (!ignore) {
                            QueuedThreadPool.LOG.warn("Unexpected thread death: {} in {}", this, QueuedThreadPool.this);
                        }
                        if (QueuedThreadPool.this._threadsStarted.decrementAndGet() < QueuedThreadPool.this.getMaxThreads()) {
                            QueuedThreadPool.this.startThreads(1);
                        }
                    }
                    QueuedThreadPool.this._threads.remove(Thread.currentThread());
                }
            }
        };
        this.setMinThreads(minThreads);
        this.setMaxThreads(maxThreads);
        this.setIdleTimeout(idleTimeout);
        this.setStopTimeout(5000L);
        if (queue == null) {
            final int capacity = Math.max(this._minThreads, 8);
            queue = new BlockingArrayQueue<Runnable>(capacity, capacity);
        }
        this._jobs = queue;
        this._threadGroup = threadGroup;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        this._threadsStarted.set(0);
        this.startThreads(this._minThreads);
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        final long timeout = this.getStopTimeout();
        final BlockingQueue<Runnable> jobs = this.getQueue();
        if (timeout <= 0L) {
            jobs.clear();
        }
        final Runnable noop = () -> {};
        int i = this._threadsStarted.get();
        while (i-- > 0) {
            jobs.offer(noop);
        }
        long stopby = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout) / 2L;
        for (final Thread thread : this._threads) {
            final long canwait = TimeUnit.NANOSECONDS.toMillis(stopby - System.nanoTime());
            if (canwait > 0L) {
                thread.join(canwait);
            }
        }
        if (this._threadsStarted.get() > 0) {
            for (final Thread thread : this._threads) {
                thread.interrupt();
            }
        }
        stopby = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout) / 2L;
        for (final Thread thread : this._threads) {
            final long canwait = TimeUnit.NANOSECONDS.toMillis(stopby - System.nanoTime());
            if (canwait > 0L) {
                thread.join(canwait);
            }
        }
        Thread.yield();
        final int size = this._threads.size();
        if (size > 0) {
            Thread.yield();
            if (QueuedThreadPool.LOG.isDebugEnabled()) {
                for (final Thread unstopped : this._threads) {
                    final StringBuilder dmp = new StringBuilder();
                    for (final StackTraceElement element : unstopped.getStackTrace()) {
                        dmp.append(System.lineSeparator()).append("\tat ").append(element);
                    }
                    QueuedThreadPool.LOG.warn("Couldn't stop {}{}", unstopped, dmp.toString());
                }
            }
            else {
                for (final Thread unstopped : this._threads) {
                    QueuedThreadPool.LOG.warn("{} Couldn't stop {}", this, unstopped);
                }
            }
        }
        synchronized (this._joinLock) {
            this._joinLock.notifyAll();
        }
    }
    
    public void setDaemon(final boolean daemon) {
        this._daemon = daemon;
    }
    
    public void setIdleTimeout(final int idleTimeout) {
        this._idleTimeout = idleTimeout;
    }
    
    @Override
    public void setMaxThreads(final int maxThreads) {
        this._maxThreads = maxThreads;
        if (this._minThreads > this._maxThreads) {
            this._minThreads = this._maxThreads;
        }
    }
    
    @Override
    public void setMinThreads(final int minThreads) {
        this._minThreads = minThreads;
        if (this._minThreads > this._maxThreads) {
            this._maxThreads = this._minThreads;
        }
        final int threads = this._threadsStarted.get();
        if (this.isStarted() && threads < this._minThreads) {
            this.startThreads(this._minThreads - threads);
        }
    }
    
    public void setName(final String name) {
        if (this.isRunning()) {
            throw new IllegalStateException("started");
        }
        this._name = name;
    }
    
    public void setThreadsPriority(final int priority) {
        this._priority = priority;
    }
    
    @ManagedAttribute("maximum time a thread may be idle in ms")
    public int getIdleTimeout() {
        return this._idleTimeout;
    }
    
    @ManagedAttribute("maximum number of threads in the pool")
    @Override
    public int getMaxThreads() {
        return this._maxThreads;
    }
    
    @ManagedAttribute("minimum number of threads in the pool")
    @Override
    public int getMinThreads() {
        return this._minThreads;
    }
    
    @ManagedAttribute("name of the thread pool")
    public String getName() {
        return this._name;
    }
    
    @ManagedAttribute("priority of threads in the pool")
    public int getThreadsPriority() {
        return this._priority;
    }
    
    @ManagedAttribute("size of the job queue")
    public int getQueueSize() {
        return this._jobs.size();
    }
    
    @ManagedAttribute("thread pool uses daemon threads")
    public boolean isDaemon() {
        return this._daemon;
    }
    
    @ManagedAttribute("reports additional details in the dump")
    public boolean isDetailedDump() {
        return this._detailedDump;
    }
    
    public void setDetailedDump(final boolean detailedDump) {
        this._detailedDump = detailedDump;
    }
    
    @ManagedAttribute("threshold at which the pool is low on threads")
    public int getLowThreadsThreshold() {
        return this._lowThreadsThreshold;
    }
    
    public void setLowThreadsThreshold(final int lowThreadsThreshold) {
        this._lowThreadsThreshold = lowThreadsThreshold;
    }
    
    @Override
    public void execute(final Runnable job) {
        if (QueuedThreadPool.LOG.isDebugEnabled()) {
            QueuedThreadPool.LOG.debug("queue {}", job);
        }
        if (!this.isRunning() || !this._jobs.offer(job)) {
            QueuedThreadPool.LOG.warn("{} rejected {}", this, job);
            throw new RejectedExecutionException(job.toString());
        }
        if (this.getThreads() == 0) {
            this.startThreads(1);
        }
    }
    
    @Override
    public void join() throws InterruptedException {
        synchronized (this._joinLock) {
            while (this.isRunning()) {
                this._joinLock.wait();
            }
        }
        while (this.isStopping()) {
            Thread.sleep(1L);
        }
    }
    
    @ManagedAttribute("number of threads in the pool")
    @Override
    public int getThreads() {
        return this._threadsStarted.get();
    }
    
    @ManagedAttribute("number of idle threads in the pool")
    @Override
    public int getIdleThreads() {
        return this._threadsIdle.get();
    }
    
    @ManagedAttribute("number of busy threads in the pool")
    public int getBusyThreads() {
        return this.getThreads() - this.getIdleThreads();
    }
    
    @ManagedAttribute(value = "thread pool is low on threads", readonly = true)
    @Override
    public boolean isLowOnThreads() {
        return this.getMaxThreads() - this.getThreads() + this.getIdleThreads() - this.getQueueSize() <= this.getLowThreadsThreshold();
    }
    
    private boolean startThreads(int threadsToStart) {
        while (threadsToStart > 0 && this.isRunning()) {
            final int threads = this._threadsStarted.get();
            if (threads >= this._maxThreads) {
                return false;
            }
            if (!this._threadsStarted.compareAndSet(threads, threads + 1)) {
                continue;
            }
            boolean started = false;
            try {
                final Thread thread = this.newThread(this._runnable);
                thread.setDaemon(this.isDaemon());
                thread.setPriority(this.getThreadsPriority());
                thread.setName(this._name + "-" + thread.getId());
                this._threads.add(thread);
                thread.start();
                started = true;
                --threadsToStart;
            }
            finally {
                if (!started) {
                    this._threadsStarted.decrementAndGet();
                }
            }
        }
        return true;
    }
    
    protected Thread newThread(final Runnable runnable) {
        return new Thread(this._threadGroup, runnable);
    }
    
    @ManagedOperation("dumps thread pool state")
    @Override
    public String dump() {
        return ContainerLifeCycle.dump(this);
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        final List<Object> threads = new ArrayList<Object>(this.getMaxThreads());
        for (final Thread thread : this._threads) {
            final StackTraceElement[] trace = thread.getStackTrace();
            boolean inIdleJobPoll = false;
            for (final StackTraceElement t : trace) {
                if ("idleJobPoll".equals(t.getMethodName())) {
                    inIdleJobPoll = true;
                    break;
                }
            }
            final boolean idle = inIdleJobPoll;
            if (this.isDetailedDump()) {
                threads.add(new Dumpable() {
                    @Override
                    public void dump(final Appendable out, final String indent) throws IOException {
                        out.append(String.valueOf(thread.getId())).append(' ').append(thread.getName()).append(' ').append(thread.getState().toString()).append(idle ? " IDLE" : "");
                        if (thread.getPriority() != 5) {
                            out.append(" prio=").append(String.valueOf(thread.getPriority()));
                        }
                        out.append(System.lineSeparator());
                        if (!idle) {
                            ContainerLifeCycle.dump(out, indent, Arrays.asList(trace));
                        }
                    }
                    
                    @Override
                    public String dump() {
                        return null;
                    }
                });
            }
            else {
                final int p = thread.getPriority();
                threads.add(thread.getId() + " " + thread.getName() + " " + thread.getState() + " @ " + ((trace.length > 0) ? trace[0] : "???") + (idle ? " IDLE" : "") + ((p == 5) ? "" : (" prio=" + p)));
            }
        }
        List<Runnable> jobs = Collections.emptyList();
        if (this.isDetailedDump()) {
            jobs = new ArrayList<Runnable>(this.getQueue());
        }
        ContainerLifeCycle.dumpObject(out, this);
        ContainerLifeCycle.dump(out, indent, threads, jobs);
    }
    
    @Override
    public String toString() {
        return String.format("%s{%s,%d<=%d<=%d,i=%d,q=%d}", this._name, this.getState(), this.getMinThreads(), this.getThreads(), this.getMaxThreads(), this.getIdleThreads(), (this._jobs == null) ? -1 : this._jobs.size());
    }
    
    private Runnable idleJobPoll() throws InterruptedException {
        return this._jobs.poll(this._idleTimeout, TimeUnit.MILLISECONDS);
    }
    
    protected void runJob(final Runnable job) {
        job.run();
    }
    
    protected BlockingQueue<Runnable> getQueue() {
        return this._jobs;
    }
    
    @Deprecated
    public void setQueue(final BlockingQueue<Runnable> queue) {
        throw new UnsupportedOperationException("Use constructor injection");
    }
    
    @ManagedOperation("interrupts a pool thread")
    public boolean interruptThread(@Name("id") final long id) {
        for (final Thread thread : this._threads) {
            if (thread.getId() == id) {
                thread.interrupt();
                return true;
            }
        }
        return false;
    }
    
    @ManagedOperation("dumps a pool thread stack")
    public String dumpThread(@Name("id") final long id) {
        for (final Thread thread : this._threads) {
            if (thread.getId() == id) {
                final StringBuilder buf = new StringBuilder();
                buf.append(thread.getId()).append(" ").append(thread.getName()).append(" ");
                buf.append(thread.getState()).append(":").append(System.lineSeparator());
                for (final StackTraceElement element : thread.getStackTrace()) {
                    buf.append("  at ").append(element.toString()).append(System.lineSeparator());
                }
                return buf.toString();
            }
        }
        return null;
    }
    
    static {
        LOG = Log.getLogger(QueuedThreadPool.class);
    }
}
