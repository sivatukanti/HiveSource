// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class Timeout
{
    private static final Logger LOG;
    private Object _lock;
    private long _duration;
    private volatile long _now;
    private Task _head;
    
    public Timeout() {
        this._now = System.currentTimeMillis();
        this._head = new Task();
        this._lock = new Object();
        this._head._timeout = this;
    }
    
    public Timeout(final Object lock) {
        this._now = System.currentTimeMillis();
        this._head = new Task();
        this._lock = lock;
        this._head._timeout = this;
    }
    
    public long getDuration() {
        return this._duration;
    }
    
    public void setDuration(final long duration) {
        this._duration = duration;
    }
    
    public long setNow() {
        return this._now = System.currentTimeMillis();
    }
    
    public long getNow() {
        return this._now;
    }
    
    public void setNow(final long now) {
        this._now = now;
    }
    
    public Task expired() {
        synchronized (this._lock) {
            final long _expiry = this._now - this._duration;
            if (this._head._next == this._head) {
                return null;
            }
            final Task task = this._head._next;
            if (task._timestamp > _expiry) {
                return null;
            }
            task.unlink();
            task._expired = true;
            return task;
        }
    }
    
    public void tick() {
        final long expiry = this._now - this._duration;
        Task task = null;
    Label_0012_Outer:
        while (true) {
            while (true) {
                try {
                    while (true) {
                        synchronized (this._lock) {
                            task = this._head._next;
                            if (task == this._head || task._timestamp > expiry) {
                                break Label_0012_Outer;
                            }
                            task.unlink();
                            task._expired = true;
                            task.expire();
                        }
                        task.expired();
                    }
                }
                catch (Throwable th) {
                    Timeout.LOG.warn("EXCEPTION ", th);
                    continue Label_0012_Outer;
                }
                continue;
            }
        }
    }
    
    public void tick(final long now) {
        this._now = now;
        this.tick();
    }
    
    public void schedule(final Task task) {
        this.schedule(task, 0L);
    }
    
    public void schedule(final Task task, final long delay) {
        synchronized (this._lock) {
            if (task._timestamp != 0L) {
                task.unlink();
                task._timestamp = 0L;
            }
            task._timeout = this;
            task._expired = false;
            task._delay = delay;
            task._timestamp = this._now + delay;
            Task last;
            for (last = this._head._prev; last != this._head && last._timestamp > task._timestamp; last = last._prev) {}
            last.link(task);
        }
    }
    
    public void cancelAll() {
        synchronized (this._lock) {
            final Task head = this._head;
            final Task head2 = this._head;
            final Task head3 = this._head;
            head2._prev = head3;
            head._next = head3;
        }
    }
    
    public boolean isEmpty() {
        synchronized (this._lock) {
            return this._head._next == this._head;
        }
    }
    
    public long getTimeToNext() {
        synchronized (this._lock) {
            if (this._head._next == this._head) {
                return -1L;
            }
            final long to_next = this._duration + this._head._next._timestamp - this._now;
            return (to_next < 0L) ? 0L : to_next;
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        for (Task task = this._head._next; task != this._head; task = task._next) {
            buf.append("-->");
            buf.append(task);
        }
        return buf.toString();
    }
    
    static {
        LOG = Log.getLogger(Timeout.class);
    }
    
    public static class Task
    {
        Task _next;
        Task _prev;
        Timeout _timeout;
        long _delay;
        long _timestamp;
        boolean _expired;
        
        protected Task() {
            this._timestamp = 0L;
            this._expired = false;
            this._prev = this;
            this._next = this;
        }
        
        public long getTimestamp() {
            return this._timestamp;
        }
        
        public long getAge() {
            final Timeout t = this._timeout;
            if (t != null) {
                final long now = t._now;
                if (now != 0L && this._timestamp != 0L) {
                    return now - this._timestamp;
                }
            }
            return 0L;
        }
        
        private void unlink() {
            this._next._prev = this._prev;
            this._prev._next = this._next;
            this._prev = this;
            this._next = this;
            this._expired = false;
        }
        
        private void link(final Task task) {
            final Task next_next = this._next;
            this._next._prev = task;
            this._next = task;
            this._next._next = next_next;
            this._next._prev = this;
        }
        
        public void schedule(final Timeout timer) {
            timer.schedule(this);
        }
        
        public void schedule(final Timeout timer, final long delay) {
            timer.schedule(this, delay);
        }
        
        public void reschedule() {
            final Timeout timeout = this._timeout;
            if (timeout != null) {
                timeout.schedule(this, this._delay);
            }
        }
        
        public void cancel() {
            final Timeout timeout = this._timeout;
            if (timeout != null) {
                synchronized (timeout._lock) {
                    this.unlink();
                    this._timestamp = 0L;
                }
            }
        }
        
        public boolean isExpired() {
            return this._expired;
        }
        
        public boolean isScheduled() {
            return this._next != this;
        }
        
        protected void expire() {
        }
        
        public void expired() {
        }
    }
}
