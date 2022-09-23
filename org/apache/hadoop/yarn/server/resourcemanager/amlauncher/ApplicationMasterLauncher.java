// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.amlauncher;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.service.AbstractService;

public class ApplicationMasterLauncher extends AbstractService implements EventHandler<AMLauncherEvent>
{
    private static final Log LOG;
    private final ThreadPoolExecutor launcherPool;
    private LauncherThread launcherHandlingThread;
    private final BlockingQueue<Runnable> masterEvents;
    protected final RMContext context;
    
    public ApplicationMasterLauncher(final RMContext context) {
        super(ApplicationMasterLauncher.class.getName());
        this.masterEvents = new LinkedBlockingQueue<Runnable>();
        this.context = context;
        this.launcherPool = new ThreadPoolExecutor(10, 10, 1L, TimeUnit.HOURS, new LinkedBlockingQueue<Runnable>());
        this.launcherHandlingThread = new LauncherThread();
    }
    
    @Override
    protected void serviceStart() throws Exception {
        this.launcherHandlingThread.start();
        super.serviceStart();
    }
    
    protected Runnable createRunnableLauncher(final RMAppAttempt application, final AMLauncherEventType event) {
        final Runnable launcher = new AMLauncher(this.context, application, event, this.getConfig());
        return launcher;
    }
    
    private void launch(final RMAppAttempt application) {
        final Runnable launcher = this.createRunnableLauncher(application, AMLauncherEventType.LAUNCH);
        this.masterEvents.add(launcher);
    }
    
    @Override
    protected void serviceStop() throws Exception {
        this.launcherHandlingThread.interrupt();
        try {
            this.launcherHandlingThread.join();
        }
        catch (InterruptedException ie) {
            ApplicationMasterLauncher.LOG.info(this.launcherHandlingThread.getName() + " interrupted during join ", ie);
        }
        this.launcherPool.shutdown();
    }
    
    private void cleanup(final RMAppAttempt application) {
        final Runnable launcher = this.createRunnableLauncher(application, AMLauncherEventType.CLEANUP);
        this.masterEvents.add(launcher);
    }
    
    @Override
    public synchronized void handle(final AMLauncherEvent appEvent) {
        final AMLauncherEventType event = appEvent.getType();
        final RMAppAttempt application = appEvent.getAppAttempt();
        switch (event) {
            case LAUNCH: {
                this.launch(application);
                break;
            }
            case CLEANUP: {
                this.cleanup(application);
                break;
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(ApplicationMasterLauncher.class);
    }
    
    private class LauncherThread extends Thread
    {
        public LauncherThread() {
            super("ApplicationMaster Launcher");
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    final Runnable toLaunch = ApplicationMasterLauncher.this.masterEvents.take();
                    ApplicationMasterLauncher.this.launcherPool.execute(toLaunch);
                }
                catch (InterruptedException e) {
                    ApplicationMasterLauncher.LOG.warn(this.getClass().getName() + " interrupted. Returning.");
                }
            }
        }
    }
}
