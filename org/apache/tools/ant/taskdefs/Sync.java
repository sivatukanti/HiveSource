// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Enumeration;
import org.apache.tools.ant.types.AbstractFileSet;
import java.util.Map;
import org.apache.tools.ant.types.Resource;
import java.util.HashSet;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.ResourceCollection;
import java.util.Iterator;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import java.util.Set;
import java.io.File;
import java.util.LinkedHashSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.Task;

public class Sync extends Task
{
    private MyCopy myCopy;
    private SyncTarget syncTarget;
    private Resources resources;
    
    public Sync() {
        this.resources = null;
    }
    
    @Override
    public void init() throws BuildException {
        this.configureTask(this.myCopy = new MyCopy());
        this.myCopy.setFiltering(false);
        this.myCopy.setIncludeEmptyDirs(false);
        this.myCopy.setPreserveLastModified(true);
    }
    
    private void configureTask(final Task helper) {
        helper.setProject(this.getProject());
        helper.setTaskName(this.getTaskName());
        helper.setOwningTarget(this.getOwningTarget());
        helper.init();
    }
    
    @Override
    public void execute() throws BuildException {
        final File toDir = this.myCopy.getToDir();
        final Set allFiles = this.myCopy.nonOrphans;
        final boolean noRemovalNecessary = !toDir.exists() || toDir.list().length < 1;
        this.log("PASS#1: Copying files to " + toDir, 4);
        this.myCopy.execute();
        if (noRemovalNecessary) {
            this.log("NO removing necessary in " + toDir, 4);
            return;
        }
        final Set preservedDirectories = new LinkedHashSet();
        this.log("PASS#2: Removing orphan files from " + toDir, 4);
        final int[] removedFileCount = this.removeOrphanFiles(allFiles, toDir, preservedDirectories);
        this.logRemovedCount(removedFileCount[0], "dangling director", "y", "ies");
        this.logRemovedCount(removedFileCount[1], "dangling file", "", "s");
        if (!this.myCopy.getIncludeEmptyDirs() || this.getExplicitPreserveEmptyDirs() == Boolean.FALSE) {
            this.log("PASS#3: Removing empty directories from " + toDir, 4);
            int removedDirCount = 0;
            if (!this.myCopy.getIncludeEmptyDirs()) {
                removedDirCount = this.removeEmptyDirectories(toDir, false, preservedDirectories);
            }
            else {
                removedDirCount = this.removeEmptyDirectories(preservedDirectories);
            }
            this.logRemovedCount(removedDirCount, "empty director", "y", "ies");
        }
    }
    
    private void logRemovedCount(final int count, final String prefix, final String singularSuffix, final String pluralSuffix) {
        final File toDir = this.myCopy.getToDir();
        String what = (prefix == null) ? "" : prefix;
        what += ((count < 2) ? singularSuffix : pluralSuffix);
        if (count > 0) {
            this.log("Removed " + count + " " + what + " from " + toDir, 2);
        }
        else {
            this.log("NO " + what + " to remove from " + toDir, 3);
        }
    }
    
    private int[] removeOrphanFiles(final Set nonOrphans, final File toDir, final Set preservedDirectories) {
        final int[] removedCount = { 0, 0 };
        final String[] excls = nonOrphans.toArray(new String[nonOrphans.size() + 1]);
        excls[nonOrphans.size()] = "";
        DirectoryScanner ds = null;
        if (this.syncTarget != null) {
            final FileSet fs = this.syncTarget.toFileSet(false);
            fs.setDir(toDir);
            final PatternSet ps = this.syncTarget.mergePatterns(this.getProject());
            fs.appendExcludes(ps.getIncludePatterns(this.getProject()));
            fs.appendIncludes(ps.getExcludePatterns(this.getProject()));
            fs.setDefaultexcludes(!this.syncTarget.getDefaultexcludes());
            final FileSelector[] s = this.syncTarget.getSelectors(this.getProject());
            if (s.length > 0) {
                final NoneSelector ns = new NoneSelector();
                for (int i = 0; i < s.length; ++i) {
                    ns.appendSelector(s[i]);
                }
                fs.appendSelector(ns);
            }
            ds = fs.getDirectoryScanner(this.getProject());
        }
        else {
            ds = new DirectoryScanner();
            ds.setBasedir(toDir);
        }
        ds.addExcludes(excls);
        ds.scan();
        final String[] files = ds.getIncludedFiles();
        for (int j = 0; j < files.length; ++j) {
            final File f = new File(toDir, files[j]);
            this.log("Removing orphan file: " + f, 4);
            f.delete();
            final int[] array = removedCount;
            final int n = 1;
            ++array[n];
        }
        final String[] dirs = ds.getIncludedDirectories();
        for (int k = dirs.length - 1; k >= 0; --k) {
            final File f2 = new File(toDir, dirs[k]);
            final String[] children = f2.list();
            if (children == null || children.length < 1) {
                this.log("Removing orphan directory: " + f2, 4);
                f2.delete();
                final int[] array2 = removedCount;
                final int n2 = 0;
                ++array2[n2];
            }
        }
        final Boolean ped = this.getExplicitPreserveEmptyDirs();
        if (ped != null && ped != this.myCopy.getIncludeEmptyDirs()) {
            final FileSet fs2 = this.syncTarget.toFileSet(true);
            fs2.setDir(toDir);
            final String[] preservedDirs = fs2.getDirectoryScanner(this.getProject()).getIncludedDirectories();
            for (int l = preservedDirs.length - 1; l >= 0; --l) {
                preservedDirectories.add(new File(toDir, preservedDirs[l]));
            }
        }
        return removedCount;
    }
    
    private int removeEmptyDirectories(final File dir, final boolean removeIfEmpty, final Set preservedEmptyDirectories) {
        int removedCount = 0;
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; ++i) {
                final File file = children[i];
                if (file.isDirectory()) {
                    removedCount += this.removeEmptyDirectories(file, true, preservedEmptyDirectories);
                }
            }
            if (children.length > 0) {
                children = dir.listFiles();
            }
            if (children.length < 1 && removeIfEmpty && !preservedEmptyDirectories.contains(dir)) {
                this.log("Removing empty directory: " + dir, 4);
                dir.delete();
                ++removedCount;
            }
        }
        return removedCount;
    }
    
    private int removeEmptyDirectories(final Set preservedEmptyDirectories) {
        int removedCount = 0;
        for (final File f : preservedEmptyDirectories) {
            final String[] s = f.list();
            if (s == null || s.length == 0) {
                this.log("Removing empty directory: " + f, 4);
                f.delete();
                ++removedCount;
            }
        }
        return removedCount;
    }
    
    public void setTodir(final File destDir) {
        this.myCopy.setTodir(destDir);
    }
    
    public void setVerbose(final boolean verbose) {
        this.myCopy.setVerbose(verbose);
    }
    
    public void setOverwrite(final boolean overwrite) {
        this.myCopy.setOverwrite(overwrite);
    }
    
    public void setIncludeEmptyDirs(final boolean includeEmpty) {
        this.myCopy.setIncludeEmptyDirs(includeEmpty);
    }
    
    public void setFailOnError(final boolean failonerror) {
        this.myCopy.setFailOnError(failonerror);
    }
    
    public void addFileset(final FileSet set) {
        this.add(set);
    }
    
    public void add(final ResourceCollection rc) {
        if (rc instanceof FileSet && rc.isFilesystemOnly()) {
            this.myCopy.add(rc);
        }
        else {
            if (this.resources == null) {
                final Restrict r = new Restrict();
                r.add(new Exists());
                r.add(this.resources = new Resources());
                this.myCopy.add(r);
            }
            this.resources.add(rc);
        }
    }
    
    public void setGranularity(final long granularity) {
        this.myCopy.setGranularity(granularity);
    }
    
    public void addPreserveInTarget(final SyncTarget s) {
        if (this.syncTarget != null) {
            throw new BuildException("you must not specify multiple preserveintarget elements.");
        }
        this.syncTarget = s;
    }
    
    private Boolean getExplicitPreserveEmptyDirs() {
        return (this.syncTarget == null) ? null : this.syncTarget.getPreserveEmptyDirs();
    }
    
    private static void assertTrue(final String message, final boolean condition) {
        if (!condition) {
            throw new BuildException("Assertion Error: " + message);
        }
    }
    
    public static class MyCopy extends Copy
    {
        private Set nonOrphans;
        
        public MyCopy() {
            this.nonOrphans = new HashSet();
        }
        
        @Override
        protected void scan(final File fromDir, final File toDir, final String[] files, final String[] dirs) {
            assertTrue("No mapper", this.mapperElement == null);
            super.scan(fromDir, toDir, files, dirs);
            for (int i = 0; i < files.length; ++i) {
                this.nonOrphans.add(files[i]);
            }
            for (int i = 0; i < dirs.length; ++i) {
                this.nonOrphans.add(dirs[i]);
            }
        }
        
        @Override
        protected Map scan(final Resource[] resources, final File toDir) {
            assertTrue("No mapper", this.mapperElement == null);
            for (int i = 0; i < resources.length; ++i) {
                this.nonOrphans.add(resources[i].getName());
            }
            return super.scan(resources, toDir);
        }
        
        public File getToDir() {
            return this.destDir;
        }
        
        public boolean getIncludeEmptyDirs() {
            return this.includeEmpty;
        }
        
        @Override
        protected boolean supportsNonFileResources() {
            return true;
        }
    }
    
    public static class SyncTarget extends AbstractFileSet
    {
        private Boolean preserveEmptyDirs;
        
        @Override
        public void setDir(final File dir) throws BuildException {
            throw new BuildException("preserveintarget doesn't support the dir attribute");
        }
        
        public void setPreserveEmptyDirs(final boolean b) {
            this.preserveEmptyDirs = b;
        }
        
        public Boolean getPreserveEmptyDirs() {
            return this.preserveEmptyDirs;
        }
        
        private FileSet toFileSet(final boolean withPatterns) {
            final FileSet fs = new FileSet();
            fs.setCaseSensitive(this.isCaseSensitive());
            fs.setFollowSymlinks(this.isFollowSymlinks());
            fs.setMaxLevelsOfSymlinks(this.getMaxLevelsOfSymlinks());
            fs.setProject(this.getProject());
            if (withPatterns) {
                final PatternSet ps = this.mergePatterns(this.getProject());
                fs.appendIncludes(ps.getIncludePatterns(this.getProject()));
                fs.appendExcludes(ps.getExcludePatterns(this.getProject()));
                final Enumeration e = this.selectorElements();
                while (e.hasMoreElements()) {
                    fs.appendSelector(e.nextElement());
                }
                fs.setDefaultexcludes(this.getDefaultexcludes());
            }
            return fs;
        }
    }
}
