// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.thread;

import java.util.Iterator;
import org.mortbay.log.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.Serializable;
import org.mortbay.component.AbstractLifeCycle;

public class QueuedThreadPool extends AbstractLifeCycle implements Serializable, ThreadPool
{
    private String _name;
    private Set _threads;
    private List _idle;
    private Runnable[] _jobs;
    private int _nextJob;
    private int _nextJobSlot;
    private int _queued;
    private int _maxQueued;
    private boolean _daemon;
    private int _id;
    private final Object _lock;
    private final Object _threadsLock;
    private final Object _joinLock;
    private long _lastShrink;
    private int _maxIdleTimeMs;
    private int _maxThreads;
    private int _minThreads;
    private boolean _warned;
    private int _lowThreads;
    private int _priority;
    private int _spawnOrShrinkAt;
    private int _maxStopTimeMs;
    
    public QueuedThreadPool() {
        this._lock = new Lock();
        this._threadsLock = new Lock();
        this._joinLock = new Lock();
        this._maxIdleTimeMs = 60000;
        this._maxThreads = 250;
        this._minThreads = 2;
        this._warned = false;
        this._lowThreads = 0;
        this._priority = 5;
        this._spawnOrShrinkAt = 0;
        this._name = "qtp-" + this.hashCode();
    }
    
    public QueuedThreadPool(final int maxThreads) {
        this();
        this.setMaxThreads(maxThreads);
    }
    
    public boolean dispatch(final Runnable job) {
        if (!this.isRunning() || job == null) {
            return false;
        }
        PoolThread thread = null;
        boolean spawn = false;
        synchronized (this._lock) {
            final int idle = this._idle.size();
            if (idle > 0) {
                thread = this._idle.remove(idle - 1);
            }
            else {
                ++this._queued;
                if (this._queued > this._maxQueued) {
                    this._maxQueued = this._queued;
                }
                this._jobs[this._nextJobSlot++] = job;
                if (this._nextJobSlot == this._jobs.length) {
                    this._nextJobSlot = 0;
                }
                if (this._nextJobSlot == this._nextJob) {
                    final Runnable[] jobs = new Runnable[this._jobs.length + this._maxThreads];
                    final int split = this._jobs.length - this._nextJob;
                    if (split > 0) {
                        System.arraycopy(this._jobs, this._nextJob, jobs, 0, split);
                    }
                    if (this._nextJob != 0) {
                        System.arraycopy(this._jobs, 0, jobs, split, this._nextJobSlot);
                    }
                    this._jobs = jobs;
                    this._nextJob = 0;
                    this._nextJobSlot = this._queued;
                }
                spawn = (this._queued > this._spawnOrShrinkAt);
            }
        }
        if (thread != null) {
            thread.dispatch(job);
        }
        else if (spawn) {
            this.newThread();
        }
        return true;
    }
    
    public int getIdleThreads() {
        return (this._idle == null) ? 0 : this._idle.size();
    }
    
    public int getLowThreads() {
        return this._lowThreads;
    }
    
    public int getMaxQueued() {
        return this._maxQueued;
    }
    
    public int getMaxIdleTimeMs() {
        return this._maxIdleTimeMs;
    }
    
    public int getMaxThreads() {
        return this._maxThreads;
    }
    
    public int getMinThreads() {
        return this._minThreads;
    }
    
    public String getName() {
        return this._name;
    }
    
    public int getThreads() {
        return this._threads.size();
    }
    
    public int getThreadsPriority() {
        return this._priority;
    }
    
    public int getQueueSize() {
        return this._queued;
    }
    
    public int getSpawnOrShrinkAt() {
        return this._spawnOrShrinkAt;
    }
    
    public void setSpawnOrShrinkAt(final int spawnOrShrinkAt) {
        this._spawnOrShrinkAt = spawnOrShrinkAt;
    }
    
    public int getMaxStopTimeMs() {
        return this._maxStopTimeMs;
    }
    
    public void setMaxStopTimeMs(final int stopTimeMs) {
        this._maxStopTimeMs = stopTimeMs;
    }
    
    public boolean isDaemon() {
        return this._daemon;
    }
    
    public boolean isLowOnThreads() {
        return this._queued > this._lowThreads;
    }
    
    public void join() throws InterruptedException {
        synchronized (this._joinLock) {
            while (this.isRunning()) {
                this._joinLock.wait();
            }
        }
        while (this.isStopping()) {
            Thread.sleep(100L);
        }
    }
    
    public void setDaemon(final boolean daemon) {
        this._daemon = daemon;
    }
    
    public void setLowThreads(final int lowThreads) {
        this._lowThreads = lowThreads;
    }
    
    public void setMaxIdleTimeMs(final int maxIdleTimeMs) {
        this._maxIdleTimeMs = maxIdleTimeMs;
    }
    
    public void setMaxThreads(final int maxThreads) {
        if (this.isStarted() && maxThreads < this._minThreads) {
            throw new IllegalArgumentException("!minThreads<maxThreads");
        }
        this._maxThreads = maxThreads;
    }
    
    public void setMinThreads(final int minThreads) {
        if (this.isStarted() && (minThreads <= 0 || minThreads > this._maxThreads)) {
            throw new IllegalArgumentException("!0<=minThreads<maxThreads");
        }
        this._minThreads = minThreads;
        synchronized (this._threadsLock) {
            while (this.isStarted() && this._threads.size() < this._minThreads) {
                this.newThread();
            }
        }
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public void setThreadsPriority(final int priority) {
        this._priority = priority;
    }
    
    protected void doStart() throws Exception {
        if (this._maxThreads < this._minThreads || this._minThreads <= 0) {
            throw new IllegalArgumentException("!0<minThreads<maxThreads");
        }
        this._threads = new HashSet();
        this._idle = new ArrayList();
        this._jobs = new Runnable[this._maxThreads];
        for (int i = 0; i < this._minThreads; ++i) {
            this.newThread();
        }
    }
    
    protected void doStop() throws Exception {
        super.doStop();
        final long start = System.currentTimeMillis();
        for (int i = 0; i < 100; ++i) {
            synchronized (this._threadsLock) {
                final Iterator iter = this._threads.iterator();
                while (iter.hasNext()) {
                    iter.next().interrupt();
                }
            }
            Thread.yield();
            if (this._threads.size() == 0) {
                break;
            }
            if (this._maxStopTimeMs > 0 && this._maxStopTimeMs < System.currentTimeMillis() - start) {
                break;
            }
            try {
                Thread.sleep(i * 100);
            }
            catch (InterruptedException ex) {}
        }
        if (this._threads.size() > 0) {
            Log.warn(this._threads.size() + " threads could not be stopped");
        }
        synchronized (this._joinLock) {
            this._joinLock.notifyAll();
        }
    }
    
    protected void newThread() {
        synchronized (this._threadsLock) {
            if (this._threads.size() < this._maxThreads) {
                final PoolThread thread = new PoolThread();
                this._threads.add(thread);
                thread.setName(thread.hashCode() + "@" + this._name + "-" + this._id++);
                thread.start();
            }
            else if (!this._warned) {
                this._warned = true;
                Log.debug("Max threads for {}", this);
            }
        }
    }
    
    protected void stopJob(final Thread thread, final Object job) {
        thread.interrupt();
    }
    
    public String dump() {
        final StringBuffer buf = new StringBuffer();
        synchronized (this._threadsLock) {
            for (final Thread thread : this._threads) {
                buf.append(thread.getName()).append(" ").append(thread.toString()).append('\n');
            }
        }
        return buf.toString();
    }
    
    public boolean stopThread(final String name) {
        synchronized (this._threadsLock) {
            for (final Thread thread : this._threads) {
                if (name.equals(thread.getName())) {
                    thread.stop();
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean interruptThread(final String name) {
        synchronized (this._threadsLock) {
            for (final Thread thread : this._threads) {
                if (name.equals(thread.getName())) {
                    thread.interrupt();
                    return true;
                }
            }
        }
        return false;
    }
    
    public class PoolThread extends Thread
    {
        Runnable _job;
        
        PoolThread() {
            this._job = null;
            this.setDaemon(QueuedThreadPool.this._daemon);
            this.setPriority(QueuedThreadPool.this._priority);
        }
        
        public void run() {
            boolean idle = false;
            Runnable job = null;
            try {
                while (QueuedThreadPool.this.isRunning()) {
                    if (job != null) {
                        final Runnable todo = job;
                        job = null;
                        idle = false;
                        todo.run();
                    }
                    synchronized (QueuedThreadPool.this._lock) {
                        if (QueuedThreadPool.this._queued > 0) {
                            QueuedThreadPool.this._queued--;
                            job = QueuedThreadPool.this._jobs[QueuedThreadPool.this._nextJob];
                            QueuedThreadPool.this._jobs[QueuedThreadPool.this._nextJob++] = null;
                            if (QueuedThreadPool.this._nextJob != QueuedThreadPool.this._jobs.length) {
                                continue;
                            }
                            QueuedThreadPool.this._nextJob = 0;
                            continue;
                        }
                        final int threads = QueuedThreadPool.this._threads.size();
                        if (threads > QueuedThreadPool.this._minThreads && (threads > QueuedThreadPool.this._maxThreads || QueuedThreadPool.this._idle.size() > QueuedThreadPool.this._spawnOrShrinkAt)) {
                            final long now = System.currentTimeMillis();
                            if (now - QueuedThreadPool.this._lastShrink > QueuedThreadPool.this.getMaxIdleTimeMs()) {
                                QueuedThreadPool.this._lastShrink = now;
                                QueuedThreadPool.this._idle.remove(this);
                                return;
                            }
                        }
                        if (!idle) {
                            QueuedThreadPool.this._idle.add(this);
                            idle = true;
                        }
                    }
                    synchronized (this) {
                        if (this._job == null) {
                            this.wait(QueuedThreadPool.this.getMaxIdleTimeMs());
                        }
                        job = this._job;
                        this._job = null;
                    }
                }
            }
            catch (InterruptedException e) {
                Log.ignore(e);
            }
            finally {
                synchronized (QueuedThreadPool.this._lock) {
                    QueuedThreadPool.this._idle.remove(this);
                }
                synchronized (QueuedThreadPool.this._threadsLock) {
                    QueuedThreadPool.this._threads.remove(this);
                }
                synchronized (this) {
                    job = this._job;
                }
                if (job != null) {
                    QueuedThreadPool.this.dispatch(job);
                }
            }
        }
        
        void dispatch(final Runnable job) {
            synchronized (this) {
                this._job = job;
                this.notify();
            }
        }
    }
    
    private class Lock
    {
    }
}
