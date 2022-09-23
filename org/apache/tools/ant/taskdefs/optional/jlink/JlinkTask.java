// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jlink;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import java.io.File;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class JlinkTask extends MatchingTask
{
    private File outfile;
    private Path mergefiles;
    private Path addfiles;
    private boolean compress;
    
    public JlinkTask() {
        this.outfile = null;
        this.mergefiles = null;
        this.addfiles = null;
        this.compress = false;
    }
    
    public void setOutfile(final File outfile) {
        this.outfile = outfile;
    }
    
    public Path createMergefiles() {
        if (this.mergefiles == null) {
            this.mergefiles = new Path(this.getProject());
        }
        return this.mergefiles.createPath();
    }
    
    public void setMergefiles(final Path mergefiles) {
        if (this.mergefiles == null) {
            this.mergefiles = mergefiles;
        }
        else {
            this.mergefiles.append(mergefiles);
        }
    }
    
    public Path createAddfiles() {
        if (this.addfiles == null) {
            this.addfiles = new Path(this.getProject());
        }
        return this.addfiles.createPath();
    }
    
    public void setAddfiles(final Path addfiles) {
        if (this.addfiles == null) {
            this.addfiles = addfiles;
        }
        else {
            this.addfiles.append(addfiles);
        }
    }
    
    public void setCompress(final boolean compress) {
        this.compress = compress;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.outfile == null) {
            throw new BuildException("outfile attribute is required! Please set.");
        }
        if (!this.haveAddFiles() && !this.haveMergeFiles()) {
            throw new BuildException("addfiles or mergefiles required! Please set.");
        }
        this.log("linking:     " + this.outfile.getPath());
        this.log("compression: " + this.compress, 3);
        final jlink linker = new jlink();
        linker.setOutfile(this.outfile.getPath());
        linker.setCompression(this.compress);
        if (this.haveMergeFiles()) {
            this.log("merge files: " + this.mergefiles.toString(), 3);
            linker.addMergeFiles(this.mergefiles.list());
        }
        if (this.haveAddFiles()) {
            this.log("add files: " + this.addfiles.toString(), 3);
            linker.addAddFiles(this.addfiles.list());
        }
        try {
            linker.link();
        }
        catch (Exception ex) {
            throw new BuildException(ex, this.getLocation());
        }
    }
    
    private boolean haveAddFiles() {
        return this.haveEntries(this.addfiles);
    }
    
    private boolean haveMergeFiles() {
        return this.haveEntries(this.mergefiles);
    }
    
    private boolean haveEntries(final Path p) {
        return p != null && p.size() > 0;
    }
}
