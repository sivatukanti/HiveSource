// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Iterator;
import org.apache.tools.ant.DirectoryScanner;
import java.io.IOException;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import java.util.Hashtable;
import java.io.File;

public class Copydir extends MatchingTask
{
    private File srcDir;
    private File destDir;
    private boolean filtering;
    private boolean flatten;
    private boolean forceOverwrite;
    private Hashtable<String, String> filecopyList;
    
    public Copydir() {
        this.filtering = false;
        this.flatten = false;
        this.forceOverwrite = false;
        this.filecopyList = new Hashtable<String, String>();
    }
    
    public void setSrc(final File src) {
        this.srcDir = src;
    }
    
    public void setDest(final File dest) {
        this.destDir = dest;
    }
    
    public void setFiltering(final boolean filter) {
        this.filtering = filter;
    }
    
    public void setFlatten(final boolean flatten) {
        this.flatten = flatten;
    }
    
    public void setForceoverwrite(final boolean force) {
        this.forceOverwrite = force;
    }
    
    @Override
    public void execute() throws BuildException {
        this.log("DEPRECATED - The copydir task is deprecated.  Use copy instead.");
        if (this.srcDir == null) {
            throw new BuildException("src attribute must be set!", this.getLocation());
        }
        if (!this.srcDir.exists()) {
            throw new BuildException("srcdir " + this.srcDir.toString() + " does not exist!", this.getLocation());
        }
        if (this.destDir == null) {
            throw new BuildException("The dest attribute must be set.", this.getLocation());
        }
        if (this.srcDir.equals(this.destDir)) {
            this.log("Warning: src == dest", 1);
        }
        final DirectoryScanner ds = super.getDirectoryScanner(this.srcDir);
        try {
            final String[] files = ds.getIncludedFiles();
            this.scanDir(this.srcDir, this.destDir, files);
            if (this.filecopyList.size() > 0) {
                this.log("Copying " + this.filecopyList.size() + " file" + ((this.filecopyList.size() == 1) ? "" : "s") + " to " + this.destDir.getAbsolutePath());
                for (final Map.Entry<String, String> e : this.filecopyList.entrySet()) {
                    final String fromFile = e.getKey();
                    final String toFile = e.getValue();
                    try {
                        this.getProject().copyFile(fromFile, toFile, this.filtering, this.forceOverwrite);
                    }
                    catch (IOException ioe) {
                        final String msg = "Failed to copy " + fromFile + " to " + toFile + " due to " + ioe.getMessage();
                        throw new BuildException(msg, ioe, this.getLocation());
                    }
                }
            }
        }
        finally {
            this.filecopyList.clear();
        }
    }
    
    private void scanDir(final File from, final File to, final String[] files) {
        for (int i = 0; i < files.length; ++i) {
            final String filename = files[i];
            final File srcFile = new File(from, filename);
            File destFile;
            if (this.flatten) {
                destFile = new File(to, new File(filename).getName());
            }
            else {
                destFile = new File(to, filename);
            }
            if (this.forceOverwrite || srcFile.lastModified() > destFile.lastModified()) {
                this.filecopyList.put(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
            }
        }
    }
}
