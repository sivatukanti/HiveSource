// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class MSVSSLABEL extends MSVSS
{
    @Override
    Commandline buildCmdLine() {
        final Commandline commandLine = new Commandline();
        if (this.getVsspath() == null) {
            throw new BuildException("vsspath attribute must be set!", this.getLocation());
        }
        final String label = this.getLabel();
        if (label.equals("")) {
            final String msg = "label attribute must be set!";
            throw new BuildException(msg, this.getLocation());
        }
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("Label");
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue(this.getComment());
        commandLine.createArgument().setValue(this.getAutoresponse());
        commandLine.createArgument().setValue(label);
        commandLine.createArgument().setValue(this.getVersion());
        commandLine.createArgument().setValue(this.getLogin());
        return commandLine;
    }
    
    public void setLabel(final String label) {
        super.setInternalLabel(label);
    }
    
    public void setVersion(final String version) {
        super.setInternalVersion(version);
    }
    
    public void setComment(final String comment) {
        super.setInternalComment(comment);
    }
    
    public void setAutoresponse(final String response) {
        super.setInternalAutoResponse(response);
    }
}
