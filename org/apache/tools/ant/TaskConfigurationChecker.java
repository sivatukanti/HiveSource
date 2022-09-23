// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class TaskConfigurationChecker
{
    private List<String> errors;
    private final Task task;
    
    public TaskConfigurationChecker(final Task task) {
        this.errors = new ArrayList<String>();
        this.task = task;
    }
    
    public void assertConfig(final boolean condition, final String errormessage) {
        if (!condition) {
            this.errors.add(errormessage);
        }
    }
    
    public void fail(final String errormessage) {
        this.errors.add(errormessage);
    }
    
    public void checkErrors() throws BuildException {
        if (!this.errors.isEmpty()) {
            final StringBuffer sb = new StringBuffer();
            sb.append("Configurationerror on <");
            sb.append(this.task.getTaskName());
            sb.append(">:");
            sb.append(System.getProperty("line.separator"));
            for (final String msg : this.errors) {
                sb.append("- ");
                sb.append(msg);
                sb.append(System.getProperty("line.separator"));
            }
            throw new BuildException(sb.toString(), this.task.getLocation());
        }
    }
}
