// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.sos;

import org.apache.tools.ant.types.Commandline;

public class SOSGet extends SOS
{
    public final void setFile(final String filename) {
        super.setInternalFilename(filename);
    }
    
    public void setRecursive(final boolean recursive) {
        super.setInternalRecursive(recursive);
    }
    
    public void setVersion(final String version) {
        super.setInternalVersion(version);
    }
    
    public void setLabel(final String label) {
        super.setInternalLabel(label);
    }
    
    protected Commandline buildCmdLine() {
        this.commandLine = new Commandline();
        if (this.getFilename() != null) {
            this.commandLine.createArgument().setValue("-command");
            this.commandLine.createArgument().setValue("GetFile");
            this.commandLine.createArgument().setValue("-file");
            this.commandLine.createArgument().setValue(this.getFilename());
            if (this.getVersion() != null) {
                this.commandLine.createArgument().setValue("-revision");
                this.commandLine.createArgument().setValue(this.getVersion());
            }
        }
        else {
            this.commandLine.createArgument().setValue("-command");
            this.commandLine.createArgument().setValue("GetProject");
            this.commandLine.createArgument().setValue(this.getRecursive());
            if (this.getLabel() != null) {
                this.commandLine.createArgument().setValue("-label");
                this.commandLine.createArgument().setValue(this.getLabel());
            }
        }
        this.getRequiredAttributes();
        this.getOptionalAttributes();
        return this.commandLine;
    }
}
