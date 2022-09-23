// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public class Rename extends Task
{
    private static final FileUtils FILE_UTILS;
    private File src;
    private File dest;
    private boolean replace;
    
    public Rename() {
        this.replace = true;
    }
    
    public void setSrc(final File src) {
        this.src = src;
    }
    
    public void setDest(final File dest) {
        this.dest = dest;
    }
    
    public void setReplace(final String replace) {
        this.replace = Project.toBoolean(replace);
    }
    
    @Override
    public void execute() throws BuildException {
        this.log("DEPRECATED - The rename task is deprecated.  Use move instead.");
        if (this.dest == null) {
            throw new BuildException("dest attribute is required", this.getLocation());
        }
        if (this.src == null) {
            throw new BuildException("src attribute is required", this.getLocation());
        }
        if (!this.replace && this.dest.exists()) {
            throw new BuildException(this.dest + " already exists.");
        }
        try {
            Rename.FILE_UTILS.rename(this.src, this.dest);
        }
        catch (IOException e) {
            throw new BuildException("Unable to rename " + this.src + " to " + this.dest, e, this.getLocation());
        }
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
