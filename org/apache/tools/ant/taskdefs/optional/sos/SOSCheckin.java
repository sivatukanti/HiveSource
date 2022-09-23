// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.sos;

import org.apache.tools.ant.types.Commandline;

public class SOSCheckin extends SOS
{
    public final void setFile(final String filename) {
        super.setInternalFilename(filename);
    }
    
    public void setRecursive(final boolean recursive) {
        super.setInternalRecursive(recursive);
    }
    
    public void setComment(final String comment) {
        super.setInternalComment(comment);
    }
    
    protected Commandline buildCmdLine() {
        this.commandLine = new Commandline();
        if (this.getFilename() != null) {
            this.commandLine.createArgument().setValue("-command");
            this.commandLine.createArgument().setValue("CheckInFile");
            this.commandLine.createArgument().setValue("-file");
            this.commandLine.createArgument().setValue(this.getFilename());
        }
        else {
            this.commandLine.createArgument().setValue("-command");
            this.commandLine.createArgument().setValue("CheckInProject");
            this.commandLine.createArgument().setValue(this.getRecursive());
        }
        this.getRequiredAttributes();
        this.getOptionalAttributes();
        if (this.getComment() != null) {
            this.commandLine.createArgument().setValue("-log");
            this.commandLine.createArgument().setValue(this.getComment());
        }
        return this.commandLine;
    }
}
