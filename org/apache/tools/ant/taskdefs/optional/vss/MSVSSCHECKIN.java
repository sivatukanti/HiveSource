// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class MSVSSCHECKIN extends MSVSS
{
    protected Commandline buildCmdLine() {
        final Commandline commandLine = new Commandline();
        if (this.getVsspath() == null) {
            final String msg = "vsspath attribute must be set!";
            throw new BuildException(msg, this.getLocation());
        }
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("Checkin");
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue(this.getLocalpath());
        commandLine.createArgument().setValue(this.getAutoresponse());
        commandLine.createArgument().setValue(this.getRecursive());
        commandLine.createArgument().setValue(this.getWritable());
        commandLine.createArgument().setValue(this.getLogin());
        commandLine.createArgument().setValue(this.getComment());
        return commandLine;
    }
    
    public void setLocalpath(final Path localPath) {
        super.setInternalLocalPath(localPath.toString());
    }
    
    public void setRecursive(final boolean recursive) {
        super.setInternalRecursive(recursive);
    }
    
    public final void setWritable(final boolean writable) {
        super.setInternalWritable(writable);
    }
    
    public void setAutoresponse(final String response) {
        super.setInternalAutoResponse(response);
    }
    
    public void setComment(final String comment) {
        super.setInternalComment(comment);
    }
}
