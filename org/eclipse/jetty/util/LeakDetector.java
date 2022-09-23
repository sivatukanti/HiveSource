// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.lang.ref.PhantomReference;
import org.eclipse.jetty.util.log.Log;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.lang.ref.ReferenceQueue;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class LeakDetector<T> extends AbstractLifeCycle implements Runnable
{
    private static final Logger LOG;
    private final ReferenceQueue<T> queue;
    private final ConcurrentMap<String, LeakInfo> resources;
    private Thread thread;
    
    public LeakDetector() {
        this.queue = new ReferenceQueue<T>();
        this.resources = new ConcurrentHashMap<String, LeakInfo>();
    }
    
    public boolean acquired(final T resource) {
        final String id = this.id(resource);
        final LeakInfo info = this.resources.putIfAbsent(id, new LeakInfo((Object)resource, id));
        return info == null;
    }
    
    public boolean released(final T resource) {
        final String id = this.id(resource);
        final LeakInfo info = this.resources.remove(id);
        return info != null;
    }
    
    public String id(final T resource) {
        return String.valueOf(System.identityHashCode(resource));
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        (this.thread = new Thread(this, this.getClass().getSimpleName())).setDaemon(true);
        this.thread.start();
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        this.thread.interrupt();
    }
    
    @Override
    public void run() {
        try {
            while (this.isRunning()) {
                final LeakInfo leakInfo = (LeakInfo)this.queue.remove();
                if (LeakDetector.LOG.isDebugEnabled()) {
                    LeakDetector.LOG.debug("Resource GC'ed: {}", leakInfo);
                }
                if (this.resources.remove(leakInfo.id) != null) {
                    this.leaked(leakInfo);
                }
            }
        }
        catch (InterruptedException ex) {}
    }
    
    protected void leaked(final LeakInfo leakInfo) {
        LeakDetector.LOG.warn("Resource leaked: " + leakInfo.description, leakInfo.stackFrames);
    }
    
    static {
        LOG = Log.getLogger(LeakDetector.class);
    }
    
    public class LeakInfo extends PhantomReference<T>
    {
        private final String id;
        private final String description;
        private final Throwable stackFrames;
        
        private LeakInfo(final T referent, final String id) {
            super(referent, LeakDetector.this.queue);
            this.id = id;
            this.description = referent.toString();
            this.stackFrames = new Throwable();
        }
        
        public String getResourceDescription() {
            return this.description;
        }
        
        public Throwable getStackFrames() {
            return this.stackFrames;
        }
        
        @Override
        public String toString() {
            return this.description;
        }
    }
}
