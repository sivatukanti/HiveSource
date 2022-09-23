// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.Task;

public class Deltree extends Task
{
    private File dir;
    
    public void setDir(final File dir) {
        this.dir = dir;
    }
    
    @Override
    public void execute() throws BuildException {
        this.log("DEPRECATED - The deltree task is deprecated.  Use delete instead.");
        if (this.dir == null) {
            throw new BuildException("dir attribute must be set!", this.getLocation());
        }
        if (this.dir.exists()) {
            if (!this.dir.isDirectory()) {
                if (!this.dir.delete()) {
                    throw new BuildException("Unable to delete directory " + this.dir.getAbsolutePath(), this.getLocation());
                }
            }
            else {
                this.log("Deleting: " + this.dir.getAbsolutePath());
                try {
                    this.removeDir(this.dir);
                }
                catch (IOException ioe) {
                    final String msg = "Unable to delete " + this.dir.getAbsolutePath();
                    throw new BuildException(msg, this.getLocation());
                }
            }
        }
    }
    
    private void removeDir(final File dir) throws IOException {
        final String[] list = dir.list();
        for (int i = 0; i < list.length; ++i) {
            final String s = list[i];
            final File f = new File(dir, s);
            if (f.isDirectory()) {
                this.removeDir(f);
            }
            else if (!f.delete()) {
                throw new BuildException("Unable to delete file " + f.getAbsolutePath());
            }
        }
        if (!dir.delete()) {
            throw new BuildException("Unable to delete directory " + dir.getAbsolutePath());
        }
    }
}
