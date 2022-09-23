// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ccm;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

public class CCMReconfigure extends Continuus
{
    private String ccmProject;
    private boolean recurse;
    private boolean verbose;
    public static final String FLAG_RECURSE = "/recurse";
    public static final String FLAG_VERBOSE = "/verbose";
    public static final String FLAG_PROJECT = "/project";
    
    public CCMReconfigure() {
        this.ccmProject = null;
        this.recurse = false;
        this.verbose = false;
        this.setCcmAction("reconfigure");
    }
    
    @Override
    public void execute() throws BuildException {
        final Commandline commandLine = new Commandline();
        int result = 0;
        commandLine.setExecutable(this.getCcmCommand());
        commandLine.createArgument().setValue(this.getCcmAction());
        this.checkOptions(commandLine);
        result = this.run(commandLine);
        if (Execute.isFailure(result)) {
            final String msg = "Failed executing: " + commandLine.toString();
            throw new BuildException(msg, this.getLocation());
        }
    }
    
    private void checkOptions(final Commandline cmd) {
        if (this.isRecurse()) {
            cmd.createArgument().setValue("/recurse");
        }
        if (this.isVerbose()) {
            cmd.createArgument().setValue("/verbose");
        }
        if (this.getCcmProject() != null) {
            cmd.createArgument().setValue("/project");
            cmd.createArgument().setValue(this.getCcmProject());
        }
    }
    
    public String getCcmProject() {
        return this.ccmProject;
    }
    
    public void setCcmProject(final String v) {
        this.ccmProject = v;
    }
    
    public boolean isRecurse() {
        return this.recurse;
    }
    
    public void setRecurse(final boolean v) {
        this.recurse = v;
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
    
    public void setVerbose(final boolean v) {
        this.verbose = v;
    }
}
