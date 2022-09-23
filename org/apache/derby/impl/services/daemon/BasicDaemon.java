// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.daemon;

import org.apache.derby.iapi.services.monitor.ModuleFactory;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.daemon.Serviceable;
import java.util.LinkedList;
import java.util.List;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.context.ContextService;
import java.util.Vector;
import org.apache.derby.iapi.services.daemon.DaemonService;

public class BasicDaemon implements DaemonService, Runnable
{
    private int numClients;
    private static final int OPTIMAL_QUEUE_SIZE = 100;
    private final Vector subscription;
    protected final ContextService contextService;
    protected final ContextManager contextMgr;
    private final List highPQ;
    private final List normPQ;
    private int nextService;
    private boolean awakened;
    private boolean waiting;
    private boolean inPause;
    private boolean running;
    private boolean stopRequested;
    private boolean stopped;
    private long lastServiceTime;
    private int earlyWakeupCount;
    
    public BasicDaemon(final ContextService contextService) {
        this.contextService = contextService;
        this.contextMgr = contextService.newContextManager();
        this.subscription = new Vector(1, 1);
        this.highPQ = new LinkedList();
        this.normPQ = new LinkedList();
        this.lastServiceTime = System.currentTimeMillis();
    }
    
    public int subscribe(final Serviceable serviceable, final boolean b) {
        final int index;
        synchronized (this) {
            index = this.numClients++;
            this.subscription.add(index, new ServiceRecord(serviceable, b, true));
        }
        return index;
    }
    
    public void unsubscribe(final int index) {
        if (index < 0 || index > this.subscription.size()) {
            return;
        }
        this.subscription.set(index, null);
    }
    
    public void serviceNow(final int index) {
        if (index < 0 || index > this.subscription.size()) {
            return;
        }
        final ServiceRecord serviceRecord = this.subscription.get(index);
        if (serviceRecord == null) {
            return;
        }
        serviceRecord.called();
        this.wakeUp();
    }
    
    public boolean enqueue(final Serviceable serviceable, final boolean b) {
        final ServiceRecord serviceRecord = new ServiceRecord(serviceable, false, false);
        final List list = b ? this.highPQ : this.normPQ;
        final int size;
        synchronized (this) {
            list.add(serviceRecord);
            size = this.highPQ.size();
        }
        if (b && !this.awakened) {
            this.wakeUp();
        }
        return b && size > 100;
    }
    
    public synchronized void clear() {
        this.normPQ.clear();
        this.highPQ.clear();
    }
    
    protected ServiceRecord nextAssignment(final boolean b) {
        while (this.nextService < this.subscription.size()) {
            final ServiceRecord serviceRecord = this.subscription.get(this.nextService++);
            if (serviceRecord != null && (serviceRecord.needImmediateService() || (!b && serviceRecord.needService()))) {
                return serviceRecord;
            }
        }
        ServiceRecord serviceRecord2 = null;
        synchronized (this) {
            if (!this.highPQ.isEmpty()) {
                serviceRecord2 = this.highPQ.remove(0);
            }
        }
        if (b || serviceRecord2 != null) {
            return serviceRecord2;
        }
        ServiceRecord serviceRecord3 = null;
        synchronized (this) {
            if (!this.normPQ.isEmpty()) {
                serviceRecord3 = this.normPQ.remove(0);
            }
        }
        return serviceRecord3;
    }
    
    protected void serviceClient(final ServiceRecord serviceRecord) {
        serviceRecord.serviced();
        final Serviceable client = serviceRecord.client;
        if (client == null) {
            return;
        }
        final ContextManager contextMgr = this.contextMgr;
        try {
            final int performWork = client.performWork(contextMgr);
            if (serviceRecord.subscriber) {
                return;
            }
            if (performWork == 2) {
                final List list = client.serviceASAP() ? this.highPQ : this.normPQ;
                synchronized (this) {
                    list.add(serviceRecord);
                }
            }
        }
        catch (Throwable t) {
            contextMgr.cleanupOnError(t, false);
        }
    }
    
    public void run() {
        this.contextService.setCurrentContextManager(this.contextMgr);
        while (true) {
            while (!this.stopRequested()) {
                final boolean rest = this.rest();
                if (this.stopRequested()) {
                    synchronized (this) {
                        this.running = false;
                        this.stopped = true;
                    }
                    this.contextMgr.cleanupOnError(StandardException.normalClose(), false);
                    this.contextService.resetCurrentContextManager(this.contextMgr);
                    return;
                }
                if (this.inPause()) {
                    continue;
                }
                this.work(rest);
            }
            continue;
        }
    }
    
    public void pause() {
        synchronized (this) {
            this.inPause = true;
            while (this.running) {
                try {
                    this.wait();
                }
                catch (InterruptedException ex) {
                    InterruptStatus.setInterrupted();
                }
            }
        }
    }
    
    public void resume() {
        synchronized (this) {
            this.inPause = false;
        }
    }
    
    public void stop() {
        if (this.stopped) {
            return;
        }
        synchronized (this) {
            this.stopRequested = true;
            this.notifyAll();
        }
        this.pause();
    }
    
    public void waitUntilQueueIsEmpty() {
        while (true) {
            synchronized (this) {
                boolean b = true;
                for (int i = 0; i < this.subscription.size(); ++i) {
                    final ServiceRecord serviceRecord = this.subscription.get(i);
                    if (serviceRecord != null && serviceRecord.needService()) {
                        b = false;
                        break;
                    }
                }
                if (this.highPQ.isEmpty() && b && !this.running) {
                    return;
                }
                this.notifyAll();
                try {
                    this.wait();
                }
                catch (InterruptedException ex) {
                    InterruptStatus.setInterrupted();
                }
            }
        }
    }
    
    private synchronized boolean stopRequested() {
        return this.stopRequested;
    }
    
    private synchronized boolean inPause() {
        return this.inPause;
    }
    
    protected synchronized void wakeUp() {
        if (!this.awakened) {
            this.awakened = true;
            if (this.waiting) {
                this.notifyAll();
            }
        }
    }
    
    private boolean rest() {
        boolean b = false;
        boolean awakened;
        synchronized (this) {
            try {
                if (!this.awakened) {
                    this.waiting = true;
                    this.wait(10000L);
                    this.waiting = false;
                }
            }
            catch (InterruptedException ex) {}
            this.nextService = 0;
            awakened = this.awakened;
            if (awakened && this.earlyWakeupCount++ > 20) {
                this.earlyWakeupCount = 0;
                b = true;
            }
            this.awakened = false;
        }
        if (b) {
            final long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.lastServiceTime > 10000L) {
                this.lastServiceTime = currentTimeMillis;
                awakened = false;
            }
        }
        return awakened;
    }
    
    private void work(final boolean b) {
        int n = 0;
        int n2 = 10;
        if (b && this.highPQ.size() > 100) {
            n2 = 2;
        }
        final int n3 = 100 / n2;
        for (ServiceRecord serviceRecord = this.nextAssignment(b); serviceRecord != null; serviceRecord = this.nextAssignment(b)) {
            synchronized (this) {
                if (this.inPause || this.stopRequested) {
                    break;
                }
                this.running = true;
            }
            try {
                this.serviceClient(serviceRecord);
                ++n;
            }
            finally {
                synchronized (this) {
                    this.running = false;
                    this.notifyAll();
                    if (this.inPause || this.stopRequested) {
                        break;
                    }
                }
            }
            if (n % 50 == 0) {
                this.nextService = 0;
            }
            if (n % n3 == 0) {
                this.yield();
            }
        }
    }
    
    private void yield() {
        final int priority = Thread.currentThread().getPriority();
        if (priority <= 1) {
            Thread.yield();
        }
        else {
            final ModuleFactory monitor = Monitor.getMonitor();
            if (monitor != null) {
                monitor.setThreadPriority(1);
            }
            Thread.yield();
            if (monitor != null) {
                monitor.setThreadPriority(priority);
            }
        }
    }
}
