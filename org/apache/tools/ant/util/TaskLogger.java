// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.apache.tools.ant.Task;

public final class TaskLogger
{
    private Task task;
    
    public TaskLogger(final Task task) {
        this.task = task;
    }
    
    public void info(final String message) {
        this.task.log(message, 2);
    }
    
    public void error(final String message) {
        this.task.log(message, 0);
    }
    
    public void warning(final String message) {
        this.task.log(message, 1);
    }
    
    public void verbose(final String message) {
        this.task.log(message, 3);
    }
    
    public void debug(final String message) {
        this.task.log(message, 4);
    }
}
