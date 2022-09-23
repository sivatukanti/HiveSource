// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.sos;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class SOSLabel extends SOS
{
    public void setVersion(final String version) {
        super.setInternalVersion(version);
    }
    
    public void setLabel(final String label) {
        super.setInternalLabel(label);
    }
    
    public void setComment(final String comment) {
        super.setInternalComment(comment);
    }
    
    protected Commandline buildCmdLine() {
        this.commandLine = new Commandline();
        this.commandLine.createArgument().setValue("-command");
        this.commandLine.createArgument().setValue("AddLabel");
        this.getRequiredAttributes();
        if (this.getLabel() == null) {
            throw new BuildException("label attribute must be set!", this.getLocation());
        }
        this.commandLine.createArgument().setValue("-label");
        this.commandLine.createArgument().setValue(this.getLabel());
        this.commandLine.createArgument().setValue(this.getVerbose());
        if (this.getComment() != null) {
            this.commandLine.createArgument().setValue("-log");
            this.commandLine.createArgument().setValue(this.getComment());
        }
        return this.commandLine;
    }
}
