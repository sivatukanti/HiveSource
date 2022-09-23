// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class MSVSSCHECKOUT extends MSVSS
{
    protected Commandline buildCmdLine() {
        final Commandline commandLine = new Commandline();
        if (this.getVsspath() == null) {
            final String msg = "vsspath attribute must be set!";
            throw new BuildException(msg, this.getLocation());
        }
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("Checkout");
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue(this.getLocalpath());
        commandLine.createArgument().setValue(this.getAutoresponse());
        commandLine.createArgument().setValue(this.getRecursive());
        commandLine.createArgument().setValue(this.getVersionDateLabel());
        commandLine.createArgument().setValue(this.getLogin());
        commandLine.createArgument().setValue(this.getFileTimeStamp());
        commandLine.createArgument().setValue(this.getWritableFiles());
        commandLine.createArgument().setValue(this.getGetLocalCopy());
        return commandLine;
    }
    
    public void setLocalpath(final Path localPath) {
        super.setInternalLocalPath(localPath.toString());
    }
    
    public void setRecursive(final boolean recursive) {
        super.setInternalRecursive(recursive);
    }
    
    public void setVersion(final String version) {
        super.setInternalVersion(version);
    }
    
    public void setDate(final String date) {
        super.setInternalDate(date);
    }
    
    public void setLabel(final String label) {
        super.setInternalLabel(label);
    }
    
    public void setAutoresponse(final String response) {
        super.setInternalAutoResponse(response);
    }
    
    public void setFileTimeStamp(final CurrentModUpdated timestamp) {
        super.setInternalFileTimeStamp(timestamp);
    }
    
    public void setWritableFiles(final WritableFiles files) {
        super.setInternalWritableFiles(files);
    }
    
    public void setGetLocalCopy(final boolean get) {
        super.setInternalGetLocalCopy(get);
    }
}
