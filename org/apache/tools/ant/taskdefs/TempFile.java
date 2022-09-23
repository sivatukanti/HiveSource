// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public class TempFile extends Task
{
    private static final FileUtils FILE_UTILS;
    private String property;
    private File destDir;
    private String prefix;
    private String suffix;
    private boolean deleteOnExit;
    private boolean createFile;
    
    public TempFile() {
        this.destDir = null;
        this.suffix = "";
    }
    
    public void setProperty(final String property) {
        this.property = property;
    }
    
    public void setDestDir(final File destDir) {
        this.destDir = destDir;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
    
    public void setDeleteOnExit(final boolean deleteOnExit) {
        this.deleteOnExit = deleteOnExit;
    }
    
    public boolean isDeleteOnExit() {
        return this.deleteOnExit;
    }
    
    public void setCreateFile(final boolean createFile) {
        this.createFile = createFile;
    }
    
    public boolean isCreateFile() {
        return this.createFile;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.property == null || this.property.length() == 0) {
            throw new BuildException("no property specified");
        }
        if (this.destDir == null) {
            this.destDir = this.getProject().resolveFile(".");
        }
        final File tfile = TempFile.FILE_UTILS.createTempFile(this.prefix, this.suffix, this.destDir, this.deleteOnExit, this.createFile);
        this.getProject().setNewProperty(this.property, tfile.toString());
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
