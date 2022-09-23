// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.scheduler;

import java.util.Vector;
import java.util.List;

public class Scheduler extends Thread
{
    List jobList;
    boolean shutdown;
    
    public Scheduler() {
        this.shutdown = false;
        this.jobList = new Vector();
    }
    
    int findIndex(final Job job) {
        final int size = this.jobList.size();
        boolean found = false;
        int i;
        for (i = 0; i < size; ++i) {
            final ScheduledJobEntry se = this.jobList.get(i);
            if (se.job == job) {
                found = true;
                break;
            }
        }
        if (found) {
            return i;
        }
        return -1;
    }
    
    public synchronized boolean delete(final Job job) {
        if (this.shutdown) {
            return false;
        }
        final int i = this.findIndex(job);
        if (i != -1) {
            final ScheduledJobEntry se = this.jobList.remove(i);
            if (se.job != job) {
                new IllegalStateException("Internal programming error");
            }
            if (i == 0) {
                this.notifyAll();
            }
            return true;
        }
        return false;
    }
    
    public synchronized void schedule(final Job job, final long desiredTime) {
        this.schedule(new ScheduledJobEntry(job, desiredTime));
    }
    
    public synchronized void schedule(final Job job, final long desiredTime, final long period) {
        this.schedule(new ScheduledJobEntry(job, desiredTime, period));
    }
    
    public synchronized boolean changePeriod(final Job job, final long newPeriod) {
        if (newPeriod <= 0L) {
            throw new IllegalArgumentException("Period must be an integer langer than zero");
        }
        final int i = this.findIndex(job);
        if (i == -1) {
            return false;
        }
        final ScheduledJobEntry se = this.jobList.get(i);
        se.period = newPeriod;
        return true;
    }
    
    private synchronized void schedule(final ScheduledJobEntry newSJE) {
        if (this.shutdown) {
            return;
        }
        final int max = this.jobList.size();
        final long desiredExecutionTime = newSJE.desiredExecutionTime;
        int i;
        for (i = 0; i < max; ++i) {
            final ScheduledJobEntry sje = this.jobList.get(i);
            if (desiredExecutionTime < sje.desiredExecutionTime) {
                break;
            }
        }
        this.jobList.add(i, newSJE);
        if (i == 0) {
            this.notifyAll();
        }
    }
    
    public synchronized void shutdown() {
        this.shutdown = true;
    }
    
    public synchronized void run() {
        while (!this.shutdown) {
            if (this.jobList.isEmpty()) {
                this.linger();
            }
            else {
                final ScheduledJobEntry sje = this.jobList.get(0);
                final long now = System.currentTimeMillis();
                if (now >= sje.desiredExecutionTime) {
                    this.executeInABox(sje.job);
                    this.jobList.remove(0);
                    if (sje.period <= 0L) {
                        continue;
                    }
                    sje.desiredExecutionTime = now + sje.period;
                    this.schedule(sje);
                }
                else {
                    this.linger(sje.desiredExecutionTime - now);
                }
            }
        }
        this.jobList.clear();
        this.jobList = null;
        System.out.println("Leaving scheduler run method");
    }
    
    void executeInABox(final Job job) {
        try {
            job.execute();
        }
        catch (Exception e) {
            System.err.println("The execution of the job threw an exception");
            e.printStackTrace(System.err);
        }
    }
    
    void linger() {
        try {
            while (this.jobList.isEmpty() && !this.shutdown) {
                this.wait();
            }
        }
        catch (InterruptedException ie) {
            this.shutdown = true;
        }
    }
    
    void linger(final long timeToLinger) {
        try {
            this.wait(timeToLinger);
        }
        catch (InterruptedException ie) {
            this.shutdown = true;
        }
    }
    
    static final class ScheduledJobEntry
    {
        long desiredExecutionTime;
        Job job;
        long period;
        
        ScheduledJobEntry(final Job job, final long desiredTime) {
            this(job, desiredTime, 0L);
        }
        
        ScheduledJobEntry(final Job job, final long desiredTime, final long period) {
            this.period = 0L;
            this.desiredExecutionTime = desiredTime;
            this.job = job;
            this.period = period;
        }
    }
}
