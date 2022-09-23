// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.util.Vector;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public class CopyPath extends Task
{
    public static final String ERROR_NO_DESTDIR = "No destDir specified";
    public static final String ERROR_NO_PATH = "No path specified";
    public static final String ERROR_NO_MAPPER = "No mapper specified";
    private static final FileUtils FILE_UTILS;
    private FileNameMapper mapper;
    private Path path;
    private File destDir;
    private long granularity;
    private boolean preserveLastModified;
    
    public CopyPath() {
        this.granularity = CopyPath.FILE_UTILS.getFileTimestampGranularity();
        this.preserveLastModified = false;
    }
    
    public void setDestDir(final File destDir) {
        this.destDir = destDir;
    }
    
    public void add(final FileNameMapper newmapper) {
        if (this.mapper != null) {
            throw new BuildException("Only one mapper allowed");
        }
        this.mapper = newmapper;
    }
    
    public void setPath(final Path s) {
        this.createPath().append(s);
    }
    
    public void setPathRef(final Reference r) {
        this.createPath().setRefid(r);
    }
    
    public Path createPath() {
        if (this.path == null) {
            this.path = new Path(this.getProject());
        }
        return this.path;
    }
    
    public void setGranularity(final long granularity) {
        this.granularity = granularity;
    }
    
    public void setPreserveLastModified(final boolean preserveLastModified) {
        this.preserveLastModified = preserveLastModified;
    }
    
    protected void validateAttributes() throws BuildException {
        if (this.destDir == null) {
            throw new BuildException("No destDir specified");
        }
        if (this.mapper == null) {
            throw new BuildException("No mapper specified");
        }
        if (this.path == null) {
            throw new BuildException("No path specified");
        }
    }
    
    @Override
    public void execute() throws BuildException {
        this.log("This task should have never been released and was obsoleted by ResourceCollection support in <copy> available since Ant 1.7.0.  Don't use it.", 0);
        this.validateAttributes();
        final String[] sourceFiles = this.path.list();
        if (sourceFiles.length == 0) {
            this.log("Path is empty", 3);
            return;
        }
        for (int sources = 0; sources < sourceFiles.length; ++sources) {
            final String sourceFileName = sourceFiles[sources];
            final File sourceFile = new File(sourceFileName);
            final String[] toFiles = this.mapper.mapFileName(sourceFileName);
            for (int i = 0; i < toFiles.length; ++i) {
                final String destFileName = toFiles[i];
                final File destFile = new File(this.destDir, destFileName);
                if (sourceFile.equals(destFile)) {
                    this.log("Skipping self-copy of " + sourceFileName, 3);
                }
                else if (sourceFile.isDirectory()) {
                    this.log("Skipping directory " + sourceFileName);
                }
                else {
                    try {
                        this.log("Copying " + sourceFile + " to " + destFile, 3);
                        CopyPath.FILE_UTILS.copyFile(sourceFile, destFile, null, null, false, this.preserveLastModified, null, null, this.getProject());
                    }
                    catch (IOException ioe) {
                        String msg = "Failed to copy " + sourceFile + " to " + destFile + " due to " + ioe.getMessage();
                        if (destFile.exists() && !destFile.delete()) {
                            msg = msg + " and I couldn't delete the corrupt " + destFile;
                        }
                        throw new BuildException(msg, ioe, this.getLocation());
                    }
                }
            }
        }
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
