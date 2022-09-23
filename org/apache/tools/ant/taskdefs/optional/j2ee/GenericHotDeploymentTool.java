// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.taskdefs.Java;

public class GenericHotDeploymentTool extends AbstractHotDeploymentTool
{
    private Java java;
    private String className;
    private static final String[] VALID_ACTIONS;
    
    public Commandline.Argument createArg() {
        return this.java.createArg();
    }
    
    public Commandline.Argument createJvmarg() {
        return this.java.createJvmarg();
    }
    
    @Override
    protected boolean isActionValid() {
        return this.getTask().getAction().equals(GenericHotDeploymentTool.VALID_ACTIONS[0]);
    }
    
    @Override
    public void setTask(final ServerDeploy task) {
        super.setTask(task);
        this.java = new Java(task);
    }
    
    @Override
    public void deploy() throws BuildException {
        this.java.setClassname(this.className);
        this.java.setClasspath(this.getClasspath());
        this.java.setFork(true);
        this.java.setFailonerror(true);
        this.java.execute();
    }
    
    @Override
    public void validateAttributes() throws BuildException {
        super.validateAttributes();
        if (this.className == null) {
            throw new BuildException("The classname attribute must be set");
        }
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
    
    public Java getJava() {
        return this.java;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    static {
        VALID_ACTIONS = new String[] { "deploy" };
    }
}
