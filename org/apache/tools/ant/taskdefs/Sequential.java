// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import java.util.Iterator;
import org.apache.tools.ant.property.LocalProperties;
import java.util.Vector;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.Task;

public class Sequential extends Task implements TaskContainer
{
    private Vector nestedTasks;
    
    public Sequential() {
        this.nestedTasks = new Vector();
    }
    
    public void addTask(final Task nestedTask) {
        this.nestedTasks.addElement(nestedTask);
    }
    
    @Override
    public void execute() throws BuildException {
        final LocalProperties localProperties = LocalProperties.get(this.getProject());
        localProperties.enterScope();
        try {
            for (final Task nestedTask : this.nestedTasks) {
                nestedTask.perform();
            }
        }
        finally {
            localProperties.exitScope();
        }
    }
}
