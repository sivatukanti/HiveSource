// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import java.io.File;
import org.apache.tools.ant.Task;

public class Copyfile extends Task
{
    private File srcFile;
    private File destFile;
    private boolean filtering;
    private boolean forceOverwrite;
    
    public Copyfile() {
        this.filtering = false;
        this.forceOverwrite = false;
    }
    
    public void setSrc(final File src) {
        this.srcFile = src;
    }
    
    public void setForceoverwrite(final boolean force) {
        this.forceOverwrite = force;
    }
    
    public void setDest(final File dest) {
        this.destFile = dest;
    }
    
    public void setFiltering(final String filter) {
        this.filtering = Project.toBoolean(filter);
    }
    
    @Override
    public void execute() throws BuildException {
        this.log("DEPRECATED - The copyfile task is deprecated.  Use copy instead.");
        if (this.srcFile == null) {
            throw new BuildException("The src attribute must be present.", this.getLocation());
        }
        if (!this.srcFile.exists()) {
            throw new BuildException("src " + this.srcFile.toString() + " does not exist.", this.getLocation());
        }
        if (this.destFile == null) {
            throw new BuildException("The dest attribute must be present.", this.getLocation());
        }
        if (this.srcFile.equals(this.destFile)) {
            this.log("Warning: src == dest", 1);
        }
        if (!this.forceOverwrite) {
            if (this.srcFile.lastModified() <= this.destFile.lastModified()) {
                return;
            }
        }
        try {
            this.getProject().copyFile(this.srcFile, this.destFile, this.filtering, this.forceOverwrite);
        }
        catch (IOException ioe) {
            final String msg = "Error copying file: " + this.srcFile.getAbsolutePath() + " due to " + ioe.getMessage();
            throw new BuildException(msg);
        }
    }
}
