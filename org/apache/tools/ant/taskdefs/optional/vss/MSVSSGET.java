// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class MSVSSGET extends MSVSS
{
    @Override
    Commandline buildCmdLine() {
        final Commandline commandLine = new Commandline();
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("Get");
        if (this.getVsspath() == null) {
            throw new BuildException("vsspath attribute must be set!", this.getLocation());
        }
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue(this.getLocalpath());
        commandLine.createArgument().setValue(this.getAutoresponse());
        commandLine.createArgument().setValue(this.getQuiet());
        commandLine.createArgument().setValue(this.getRecursive());
        commandLine.createArgument().setValue(this.getVersionDateLabel());
        commandLine.createArgument().setValue(this.getWritable());
        commandLine.createArgument().setValue(this.getLogin());
        commandLine.createArgument().setValue(this.getFileTimeStamp());
        commandLine.createArgument().setValue(this.getWritableFiles());
        return commandLine;
    }
    
    public void setLocalpath(final Path localPath) {
        super.setInternalLocalPath(localPath.toString());
    }
    
    public final void setRecursive(final boolean recursive) {
        super.setInternalRecursive(recursive);
    }
    
    public final void setQuiet(final boolean quiet) {
        super.setInternalQuiet(quiet);
    }
    
    public final void setWritable(final boolean writable) {
        super.setInternalWritable(writable);
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
}
