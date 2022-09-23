// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.thread;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashSet;
import org.mortbay.log.Log;
import java.util.Set;
import java.util.List;
import java.io.Serializable;
import org.mortbay.component.AbstractLifeCycle;

public class BoundedThreadPool extends AbstractLifeCycle implements Serializable, ThreadPool
{
    private static int __id;
    private boolean _daemon;
    private int _id;
    private List _idle;
    private final Object _lock;
    private final Object _joinLock;
    private long _lastShrink;
    private int _maxIdleTimeMs;
    private int _maxThreads;
    private int _minThreads;
    private String _name;
    private List _queue;
    private Set _threads;
    private boolean _warned;
    int _lowThreads;
    int _priority;
    
    public BoundedThreadPool() {
        this._lock = new Object();
        this._joinLock = new Object();
        this._maxIdleTimeMs = 60000;
        this._maxThreads = 255;
        this._minThreads = 1;
        this._warned = false;
        this._lowThreads = 0;
        this._priority = 5;
        this._name = "btpool" + BoundedThreadPool.__id++;
    }
    
    public boolean dispatch(final Runnable job) {
        synchronized (this._lock) {
            if (!this.isRunning() || job == null) {
                return false;
            }
            final int idle = this._idle.size();
            if (idle > 0) {
                final PoolThread thread = this._idle.remove(idle - 1);
                thread.dispatch(job);
            }
            else if (this._threads.size() < this._maxThreads) {
                this.newThread(job);
            }
            else {
                if (!this._warned) {
                    this._warned = true;
                    Log.debug("Out of threads for {}", this);
                }
                this._queue.add(job);
            }
        }
        return true;
    }
    
    public int getIdleThreads() {
        return (this._idle == null) ? 0 : this._idle.size();
    }
    
    public int getLowThreads() {
        return this._lowThreads;
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
        synchronized (this._lock) {
            return this._queue.size();
        }
    }
    
    public boolean isDaemon() {
        return this._daemon;
    }
    
    public boolean isLowOnThreads() {
        synchronized (this._lock) {
            return this._queue.size() > this._lowThreads;
        }
    }
    
    public void join() throws InterruptedException {
        synchronized (this._joinLock) {
            while (this.isRunning()) {
                this._joinLock.wait();
            }
        }
        while (this.isStopping()) {
            Thread.sleep(10L);
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
        synchronized (this._lock) {
            while (this.isStarted() && this._threads.size() < this._minThreads) {
                this.newThread(null);
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
        this._queue = new LinkedList();
        for (int i = 0; i < this._minThreads; ++i) {
            this.newThread(null);
        }
    }
    
    protected void doStop() throws Exception {
        super.doStop();
        for (int i = 0; i < 100; ++i) {
            synchronized (this._lock) {
                final Iterator iter = this._threads.iterator();
                while (iter.hasNext()) {
                    iter.next().interrupt();
                }
            }
            Thread.yield();
            if (this._threads.size() == 0) {
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
    
    protected PoolThread newThread(final Runnable job) {
        synchronized (this._lock) {
            final PoolThread thread = new PoolThread(job);
            this._threads.add(thread);
            thread.setName(this._name + "-" + this._id++);
            thread.start();
            return thread;
        }
    }
    
    protected void stopJob(final Thread thread, final Object job) {
        thread.interrupt();
    }
    
    public class PoolThread extends Thread
    {
        Runnable _job;
        
        PoolThread() {
            this._job = null;
            this.setDaemon(BoundedThreadPool.this._daemon);
            this.setPriority(BoundedThreadPool.this._priority);
        }
        
        PoolThread(final Runnable job) {
            this._job = null;
            this.setDaemon(BoundedThreadPool.this._daemon);
            this.setPriority(BoundedThreadPool.this._priority);
            this._job = job;
        }
        
        public void run() {
            try {
                Runnable job = null;
                synchronized (this) {
                    job = this._job;
                    this._job = null;
                }
                while (BoundedThreadPool.this.isRunning()) {
                    if (job != null) {
                        final Runnable todo = job;
                        job = null;
                        todo.run();
                    }
                    else {
                        synchronized (BoundedThreadPool.this._lock) {
                            if (BoundedThreadPool.this._queue.size() > 0) {
                                job = BoundedThreadPool.this._queue.remove(0);
                                continue;
                            }
                            BoundedThreadPool.this._warned = false;
                            if (BoundedThreadPool.this._threads.size() > BoundedThreadPool.this._maxThreads || (BoundedThreadPool.this._idle.size() > 0 && BoundedThreadPool.this._threads.size() > BoundedThreadPool.this._minThreads)) {
                                final long now = System.currentTimeMillis();
                                if (now - BoundedThreadPool.this._lastShrink > BoundedThreadPool.this.getMaxIdleTimeMs()) {
                                    BoundedThreadPool.this._lastShrink = now;
                                    return;
                                }
                            }
                            BoundedThreadPool.this._idle.add(this);
                        }
                        try {
                            synchronized (this) {
                                if (this._job == null) {
                                    this.wait(BoundedThreadPool.this.getMaxIdleTimeMs());
                                }
                                job = this._job;
                                this._job = null;
                            }
                        }
                        catch (InterruptedException e) {
                            Log.ignore(e);
                        }
                        finally {
                            synchronized (BoundedThreadPool.this._lock) {
                                BoundedThreadPool.this._idle.remove(this);
                            }
                        }
                    }
                }
            }
            finally {
                synchronized (BoundedThreadPool.this._lock) {
                    BoundedThreadPool.this._threads.remove(this);
                }
                Runnable job2 = null;
                synchronized (this) {
                    job2 = this._job;
                }
                if (job2 != null && BoundedThreadPool.this.isRunning()) {
                    BoundedThreadPool.this.dispatch(job2);
                }
            }
        }
        
        void dispatch(final Runnable job) {
            synchronized (this) {
                if (this._job != null || job == null) {
                    throw new IllegalStateException();
                }
                this._job = job;
                this.notify();
            }
        }
    }
}
