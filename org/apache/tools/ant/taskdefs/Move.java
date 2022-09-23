// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Vector;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.DirectoryScanner;
import java.util.Iterator;
import org.apache.tools.ant.types.FileSet;
import java.io.IOException;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Os;

public class Move extends Copy
{
    private boolean performGc;
    
    public Move() {
        this.performGc = Os.isFamily("windows");
        this.setOverwrite(true);
    }
    
    public void setPerformGcOnFailedDelete(final boolean b) {
        this.performGc = b;
    }
    
    @Override
    protected void validateAttributes() throws BuildException {
        if (this.file != null && this.file.isDirectory()) {
            if ((this.destFile != null && this.destDir != null) || (this.destFile == null && this.destDir == null)) {
                throw new BuildException("One and only one of tofile and todir must be set.");
            }
            this.destFile = ((this.destFile == null) ? new File(this.destDir, this.file.getName()) : this.destFile);
            this.destDir = ((this.destDir == null) ? this.destFile.getParentFile() : this.destDir);
            this.completeDirMap.put(this.file, this.destFile);
            this.file = null;
        }
        else {
            super.validateAttributes();
        }
    }
    
    @Override
    protected void doFileOperations() {
        if (this.completeDirMap.size() > 0) {
            for (final File fromDir : this.completeDirMap.keySet()) {
                final File toDir = this.completeDirMap.get(fromDir);
                boolean renamed = false;
                try {
                    this.log("Attempting to rename dir: " + fromDir + " to " + toDir, this.verbosity);
                    renamed = this.renameFile(fromDir, toDir, this.filtering, this.forceOverwrite);
                }
                catch (IOException ioe) {
                    final String msg = "Failed to rename dir " + fromDir + " to " + toDir + " due to " + ioe.getMessage();
                    throw new BuildException(msg, ioe, this.getLocation());
                }
                if (!renamed) {
                    final FileSet fs = new FileSet();
                    fs.setProject(this.getProject());
                    fs.setDir(fromDir);
                    this.addFileset(fs);
                    final DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
                    final String[] files = ds.getIncludedFiles();
                    final String[] dirs = ds.getIncludedDirectories();
                    this.scan(fromDir, toDir, files, dirs);
                }
            }
        }
        final int moveCount = this.fileCopyMap.size();
        if (moveCount > 0) {
            this.log("Moving " + moveCount + " file" + ((moveCount == 1) ? "" : "s") + " to " + this.destDir.getAbsolutePath());
            for (final String fromFile : this.fileCopyMap.keySet()) {
                final File f = new File(fromFile);
                boolean selfMove = false;
                if (f.exists()) {
                    final String[] toFiles = this.fileCopyMap.get(fromFile);
                    for (int i = 0; i < toFiles.length; ++i) {
                        final String toFile = toFiles[i];
                        if (fromFile.equals(toFile)) {
                            this.log("Skipping self-move of " + fromFile, this.verbosity);
                            selfMove = true;
                        }
                        else {
                            final File d = new File(toFile);
                            if (i + 1 == toFiles.length && !selfMove) {
                                this.moveFile(f, d, this.filtering, this.forceOverwrite);
                            }
                            else {
                                this.copyFile(f, d, this.filtering, this.forceOverwrite);
                            }
                        }
                    }
                }
            }
        }
        if (this.includeEmpty) {
            int createCount = 0;
            for (final String fromDirName : this.dirCopyMap.keySet()) {
                final String[] toDirNames = this.dirCopyMap.get(fromDirName);
                boolean selfMove2 = false;
                for (int i = 0; i < toDirNames.length; ++i) {
                    if (fromDirName.equals(toDirNames[i])) {
                        this.log("Skipping self-move of " + fromDirName, this.verbosity);
                        selfMove2 = true;
                    }
                    else {
                        final File d2 = new File(toDirNames[i]);
                        if (!d2.exists()) {
                            if (!d2.mkdirs()) {
                                this.log("Unable to create directory " + d2.getAbsolutePath(), 0);
                            }
                            else {
                                ++createCount;
                            }
                        }
                    }
                }
                final File fromDir2 = new File(fromDirName);
                if (!selfMove2 && this.okToDelete(fromDir2)) {
                    this.deleteDir(fromDir2);
                }
            }
            if (createCount > 0) {
                this.log("Moved " + this.dirCopyMap.size() + " empty director" + ((this.dirCopyMap.size() == 1) ? "y" : "ies") + " to " + createCount + " empty director" + ((createCount == 1) ? "y" : "ies") + " under " + this.destDir.getAbsolutePath());
            }
        }
    }
    
    private void moveFile(final File fromFile, final File toFile, final boolean filtering, final boolean overwrite) {
        boolean moved = false;
        try {
            this.log("Attempting to rename: " + fromFile + " to " + toFile, this.verbosity);
            moved = this.renameFile(fromFile, toFile, filtering, this.forceOverwrite);
        }
        catch (IOException ioe) {
            final String msg = "Failed to rename " + fromFile + " to " + toFile + " due to " + ioe.getMessage();
            throw new BuildException(msg, ioe, this.getLocation());
        }
        if (!moved) {
            this.copyFile(fromFile, toFile, filtering, overwrite);
            if (!this.getFileUtils().tryHardToDelete(fromFile, this.performGc)) {
                throw new BuildException("Unable to delete file " + fromFile.getAbsolutePath());
            }
        }
    }
    
    private void copyFile(final File fromFile, final File toFile, final boolean filtering, final boolean overwrite) {
        try {
            this.log("Copying " + fromFile + " to " + toFile, this.verbosity);
            final FilterSetCollection executionFilters = new FilterSetCollection();
            if (filtering) {
                executionFilters.addFilterSet(this.getProject().getGlobalFilterSet());
            }
            final Iterator filterIter = this.getFilterSets().iterator();
            while (filterIter.hasNext()) {
                executionFilters.addFilterSet(filterIter.next());
            }
            this.getFileUtils().copyFile(fromFile, toFile, executionFilters, this.getFilterChains(), this.forceOverwrite, this.getPreserveLastModified(), false, this.getEncoding(), this.getOutputEncoding(), this.getProject(), this.getForce());
        }
        catch (IOException ioe) {
            final String msg = "Failed to copy " + fromFile + " to " + toFile + " due to " + ioe.getMessage();
            throw new BuildException(msg, ioe, this.getLocation());
        }
    }
    
    protected boolean okToDelete(final File d) {
        final String[] list = d.list();
        if (list == null) {
            return false;
        }
        for (int i = 0; i < list.length; ++i) {
            final String s = list[i];
            final File f = new File(d, s);
            if (!f.isDirectory()) {
                return false;
            }
            if (!this.okToDelete(f)) {
                return false;
            }
        }
        return true;
    }
    
    protected void deleteDir(final File d) {
        this.deleteDir(d, false);
    }
    
    protected void deleteDir(final File d, final boolean deleteFiles) {
        final String[] list = d.list();
        if (list == null) {
            return;
        }
        int i = 0;
        while (i < list.length) {
            final String s = list[i];
            final File f = new File(d, s);
            if (f.isDirectory()) {
                this.deleteDir(f);
                ++i;
            }
            else {
                if (deleteFiles && !this.getFileUtils().tryHardToDelete(f, this.performGc)) {
                    throw new BuildException("Unable to delete file " + f.getAbsolutePath());
                }
                throw new BuildException("UNEXPECTED ERROR - The file " + f.getAbsolutePath() + " should not exist!");
            }
        }
        this.log("Deleting directory " + d.getAbsolutePath(), this.verbosity);
        if (!this.getFileUtils().tryHardToDelete(d, this.performGc)) {
            throw new BuildException("Unable to delete directory " + d.getAbsolutePath());
        }
    }
    
    protected boolean renameFile(File sourceFile, File destFile, final boolean filtering, final boolean overwrite) throws IOException, BuildException {
        if (destFile.isDirectory() || filtering || this.getFilterSets().size() > 0 || this.getFilterChains().size() > 0) {
            return false;
        }
        if (destFile.isFile() && !destFile.canWrite()) {
            if (!this.getForce()) {
                throw new IOException("can't replace read-only destination file " + destFile);
            }
            if (!this.getFileUtils().tryHardToDelete(destFile)) {
                throw new IOException("failed to delete read-only destination file " + destFile);
            }
        }
        final File parent = destFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        else if (destFile.isFile()) {
            sourceFile = this.getFileUtils().normalize(sourceFile.getAbsolutePath()).getCanonicalFile();
            destFile = this.getFileUtils().normalize(destFile.getAbsolutePath());
            if (destFile.getAbsolutePath().equals(sourceFile.getAbsolutePath())) {
                this.log("Rename of " + sourceFile + " to " + destFile + " is a no-op.", 3);
                return true;
            }
            if (!this.getFileUtils().areSame(sourceFile, destFile) && !this.getFileUtils().tryHardToDelete(destFile, this.performGc)) {
                throw new BuildException("Unable to remove existing file " + destFile);
            }
        }
        return sourceFile.renameTo(destFile);
    }
}
