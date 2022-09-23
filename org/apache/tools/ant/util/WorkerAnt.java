// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class WorkerAnt extends Thread
{
    private Task task;
    private Object notify;
    private volatile boolean finished;
    private volatile BuildException buildException;
    private volatile Throwable exception;
    public static final String ERROR_NO_TASK = "No task defined";
    
    public WorkerAnt(final Task task, final Object notify) {
        this.finished = false;
        this.task = task;
        this.notify = ((notify != null) ? notify : this);
    }
    
    public WorkerAnt(final Task task) {
        this(task, null);
    }
    
    public synchronized BuildException getBuildException() {
        return this.buildException;
    }
    
    public synchronized Throwable getException() {
        return this.exception;
    }
    
    public Task getTask() {
        return this.task;
    }
    
    public synchronized boolean isFinished() {
        return this.finished;
    }
    
    public void waitUntilFinished(final long timeout) throws InterruptedException {
        synchronized (this.notify) {
            if (!this.finished) {
                this.notify.wait(timeout);
            }
        }
    }
    
    public void rethrowAnyBuildException() {
        final BuildException ex = this.getBuildException();
        if (ex != null) {
            throw ex;
        }
    }
    
    private synchronized void caught(final Throwable thrown) {
        this.exception = thrown;
        this.buildException = (BuildException)((thrown instanceof BuildException) ? thrown : new BuildException(thrown));
    }
    
    @Override
    public void run() {
        try {
            if (this.task != null) {
                this.task.execute();
            }
        }
        catch (Throwable thrown) {
            this.caught(thrown);
        }
        finally {
            synchronized (this.notify) {
                this.finished = true;
                this.notify.notifyAll();
            }
        }
    }
}
