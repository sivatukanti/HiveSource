// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class MSVSSCP extends MSVSS
{
    protected Commandline buildCmdLine() {
        final Commandline commandLine = new Commandline();
        if (this.getVsspath() == null) {
            final String msg = "vsspath attribute must be set!";
            throw new BuildException(msg, this.getLocation());
        }
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("CP");
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue(this.getAutoresponse());
        commandLine.createArgument().setValue(this.getLogin());
        return commandLine;
    }
    
    public void setAutoresponse(final String response) {
        super.setInternalAutoResponse(response);
    }
}
