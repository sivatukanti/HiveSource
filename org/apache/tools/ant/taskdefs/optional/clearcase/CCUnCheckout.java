// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

public class CCUnCheckout extends ClearCase
{
    private boolean mKeep;
    public static final String FLAG_KEEPCOPY = "-keep";
    public static final String FLAG_RM = "-rm";
    
    public CCUnCheckout() {
        this.mKeep = false;
    }
    
    @Override
    public void execute() throws BuildException {
        final Commandline commandLine = new Commandline();
        final Project aProj = this.getProject();
        int result = 0;
        if (this.getViewPath() == null) {
            this.setViewPath(aProj.getBaseDir().getPath());
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("uncheckout");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getViewPathBasename(), 3);
        }
        result = this.run(commandLine);
        if (Execute.isFailure(result) && this.getFailOnErr()) {
            final String msg = "Failed executing: " + commandLine.toString();
            throw new BuildException(msg, this.getLocation());
        }
    }
    
    private void checkOptions(final Commandline cmd) {
        if (this.getKeepCopy()) {
            cmd.createArgument().setValue("-keep");
        }
        else {
            cmd.createArgument().setValue("-rm");
        }
        cmd.createArgument().setValue(this.getViewPath());
    }
    
    public void setKeepCopy(final boolean keep) {
        this.mKeep = keep;
    }
    
    public boolean getKeepCopy() {
        return this.mKeep;
    }
}
