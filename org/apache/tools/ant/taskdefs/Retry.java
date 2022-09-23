// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.Task;

public class Retry extends Task implements TaskContainer
{
    private Task nestedTask;
    private int retryCount;
    private int retryDelay;
    
    public Retry() {
        this.retryCount = 1;
        this.retryDelay = 0;
    }
    
    public synchronized void addTask(final Task t) {
        if (this.nestedTask != null) {
            throw new BuildException("The retry task container accepts a single nested task (which may be a sequential task container)");
        }
        this.nestedTask = t;
    }
    
    public void setRetryCount(final int n) {
        this.retryCount = n;
    }
    
    public void setRetryDelay(final int retryDelay) {
        if (retryDelay < 0) {
            throw new BuildException("delay must be a non-negative number");
        }
        this.retryDelay = retryDelay;
    }
    
    @Override
    public void execute() throws BuildException {
        final StringBuffer errorMessages = new StringBuffer();
        int i = 0;
        while (i <= this.retryCount) {
            try {
                this.nestedTask.perform();
            }
            catch (Exception e) {
                errorMessages.append(e.getMessage());
                if (i >= this.retryCount) {
                    final StringBuffer exceptionMessage = new StringBuffer();
                    exceptionMessage.append("Task [").append(this.nestedTask.getTaskName());
                    exceptionMessage.append("] failed after [").append(this.retryCount);
                    exceptionMessage.append("] attempts; giving up.").append(StringUtils.LINE_SEP);
                    exceptionMessage.append("Error messages:").append(StringUtils.LINE_SEP);
                    exceptionMessage.append(errorMessages);
                    throw new BuildException(exceptionMessage.toString(), this.getLocation());
                }
                String msg;
                if (this.retryDelay > 0) {
                    msg = "Attempt [" + i + "]: error occurred; retrying after " + this.retryDelay + " ms...";
                }
                else {
                    msg = "Attempt [" + i + "]: error occurred; retrying...";
                }
                this.log(msg, e, 2);
                errorMessages.append(StringUtils.LINE_SEP);
                if (this.retryDelay > 0) {
                    try {
                        Thread.sleep(this.retryDelay);
                    }
                    catch (InterruptedException ex) {}
                }
                ++i;
                continue;
            }
            break;
        }
    }
}
