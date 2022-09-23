// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Nice extends Task
{
    private Integer newPriority;
    private String currentPriority;
    
    @Override
    public void execute() throws BuildException {
        final Thread self = Thread.currentThread();
        final int priority = self.getPriority();
        if (this.currentPriority != null) {
            final String current = Integer.toString(priority);
            this.getProject().setNewProperty(this.currentPriority, current);
        }
        if (this.newPriority != null && priority != this.newPriority) {
            try {
                self.setPriority(this.newPriority);
            }
            catch (SecurityException e) {
                this.log("Unable to set new priority -a security manager is in the way", 1);
            }
            catch (IllegalArgumentException iae) {
                throw new BuildException("Priority out of range", iae);
            }
        }
    }
    
    public void setCurrentPriority(final String currentPriority) {
        this.currentPriority = currentPriority;
    }
    
    public void setNewPriority(final int newPriority) {
        if (newPriority < 1 || newPriority > 10) {
            throw new BuildException("The thread priority is out of the range 1-10");
        }
        this.newPriority = new Integer(newPriority);
    }
}
