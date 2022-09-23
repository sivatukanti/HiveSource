// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class MSVSSCREATE extends MSVSS
{
    @Override
    Commandline buildCmdLine() {
        final Commandline commandLine = new Commandline();
        if (this.getVsspath() == null) {
            final String msg = "vsspath attribute must be set!";
            throw new BuildException(msg, this.getLocation());
        }
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("Create");
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue(this.getComment());
        commandLine.createArgument().setValue(this.getAutoresponse());
        commandLine.createArgument().setValue(this.getQuiet());
        commandLine.createArgument().setValue(this.getLogin());
        return commandLine;
    }
    
    public void setComment(final String comment) {
        super.setInternalComment(comment);
    }
    
    public final void setQuiet(final boolean quiet) {
        super.setInternalQuiet(quiet);
    }
    
    public void setAutoresponse(final String response) {
        super.setInternalAutoResponse(response);
    }
}
