// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Sleep extends Task
{
    private boolean failOnError;
    private int seconds;
    private int hours;
    private int minutes;
    private int milliseconds;
    
    public Sleep() {
        this.failOnError = true;
        this.seconds = 0;
        this.hours = 0;
        this.minutes = 0;
        this.milliseconds = 0;
    }
    
    public void setSeconds(final int seconds) {
        this.seconds = seconds;
    }
    
    public void setHours(final int hours) {
        this.hours = hours;
    }
    
    public void setMinutes(final int minutes) {
        this.minutes = minutes;
    }
    
    public void setMilliseconds(final int milliseconds) {
        this.milliseconds = milliseconds;
    }
    
    public void doSleep(final long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException ex) {}
    }
    
    public void setFailOnError(final boolean failOnError) {
        this.failOnError = failOnError;
    }
    
    private long getSleepTime() {
        return ((this.hours * 60L + this.minutes) * 60L + this.seconds) * 1000L + this.milliseconds;
    }
    
    public void validate() throws BuildException {
        if (this.getSleepTime() < 0L) {
            throw new BuildException("Negative sleep periods are not supported");
        }
    }
    
    @Override
    public void execute() throws BuildException {
        try {
            this.validate();
            final long sleepTime = this.getSleepTime();
            this.log("sleeping for " + sleepTime + " milliseconds", 3);
            this.doSleep(sleepTime);
        }
        catch (Exception e) {
            if (this.failOnError) {
                throw new BuildException(e);
            }
            final String text = e.toString();
            this.log(text, 0);
        }
    }
}
