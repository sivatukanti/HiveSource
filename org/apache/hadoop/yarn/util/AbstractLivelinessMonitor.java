// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import java.util.Iterator;
import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class AbstractLivelinessMonitor<O> extends AbstractService
{
    private static final Log LOG;
    private Thread checkerThread;
    private volatile boolean stopped;
    public static final int DEFAULT_EXPIRE = 300000;
    private int expireInterval;
    private int monitorInterval;
    private final Clock clock;
    private Map<O, Long> running;
    
    public AbstractLivelinessMonitor(final String name, final Clock clock) {
        super(name);
        this.expireInterval = 300000;
        this.monitorInterval = this.expireInterval / 3;
        this.running = new HashMap<O, Long>();
        this.clock = clock;
    }
    
    @Override
    protected void serviceStart() throws Exception {
        assert !this.stopped : "starting when already stopped";
        (this.checkerThread = new Thread(new PingChecker())).setName("Ping Checker");
        this.checkerThread.start();
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        this.stopped = true;
        if (this.checkerThread != null) {
            this.checkerThread.interrupt();
        }
        super.serviceStop();
    }
    
    protected abstract void expire(final O p0);
    
    protected void setExpireInterval(final int expireInterval) {
        this.expireInterval = expireInterval;
    }
    
    protected void setMonitorInterval(final int monitorInterval) {
        this.monitorInterval = monitorInterval;
    }
    
    public synchronized void receivedPing(final O ob) {
        if (this.running.containsKey(ob)) {
            this.running.put(ob, this.clock.getTime());
        }
    }
    
    public synchronized void register(final O ob) {
        this.running.put(ob, this.clock.getTime());
    }
    
    public synchronized void unregister(final O ob) {
        this.running.remove(ob);
    }
    
    static {
        LOG = LogFactory.getLog(AbstractLivelinessMonitor.class);
    }
    
    private class PingChecker implements Runnable
    {
        @Override
        public void run() {
            while (!AbstractLivelinessMonitor.this.stopped && !Thread.currentThread().isInterrupted()) {
                synchronized (AbstractLivelinessMonitor.this) {
                    final Iterator<Map.Entry<O, Long>> iterator = AbstractLivelinessMonitor.this.running.entrySet().iterator();
                    final long currentTime = AbstractLivelinessMonitor.this.clock.getTime();
                    while (iterator.hasNext()) {
                        final Map.Entry<O, Long> entry = iterator.next();
                        if (currentTime > entry.getValue() + AbstractLivelinessMonitor.this.expireInterval) {
                            iterator.remove();
                            AbstractLivelinessMonitor.this.expire(entry.getKey());
                            AbstractLivelinessMonitor.LOG.info("Expired:" + entry.getKey().toString() + " Timed out after " + AbstractLivelinessMonitor.this.expireInterval / 1000 + " secs");
                        }
                    }
                }
                try {
                    Thread.sleep(AbstractLivelinessMonitor.this.monitorInterval);
                    continue;
                }
                catch (InterruptedException e) {
                    AbstractLivelinessMonitor.LOG.info(AbstractLivelinessMonitor.this.getName() + " thread interrupted");
                }
                break;
            }
        }
    }
}
