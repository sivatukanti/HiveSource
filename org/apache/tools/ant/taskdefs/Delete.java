// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.types.resources.comparators.Reverse;
import org.apache.tools.ant.types.resources.comparators.FileSystem;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.Sort;
import org.apache.tools.ant.types.resources.Restrict;
import java.io.IOException;
import org.apache.tools.ant.Task;
import java.util.Comparator;
import java.util.Arrays;
import org.apache.tools.ant.types.resources.FileResourceIterator;
import org.apache.tools.ant.types.Resource;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;
import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
import org.apache.tools.ant.types.selectors.DependSelector;
import org.apache.tools.ant.types.selectors.DepthSelector;
import org.apache.tools.ant.types.selectors.PresentSelector;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.types.selectors.SizeSelector;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.MajoritySelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import org.apache.tools.ant.types.selectors.NotSelector;
import org.apache.tools.ant.types.selectors.OrSelector;
import org.apache.tools.ant.types.selectors.AndSelector;
import org.apache.tools.ant.types.selectors.SelectSelector;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.util.SymbolicLinkUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.FileSet;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;

public class Delete extends MatchingTask
{
    private static final ResourceComparator REVERSE_FILESYSTEM;
    private static final ResourceSelector EXISTS;
    protected File file;
    protected File dir;
    protected Vector<FileSet> filesets;
    protected boolean usedMatchingTask;
    protected boolean includeEmpty;
    private int verbosity;
    private boolean quiet;
    private boolean failonerror;
    private boolean deleteOnExit;
    private boolean removeNotFollowedSymlinks;
    private Resources rcs;
    private static FileUtils FILE_UTILS;
    private static SymbolicLinkUtils SYMLINK_UTILS;
    private boolean performGc;
    
    public Delete() {
        this.file = null;
        this.dir = null;
        this.filesets = new Vector<FileSet>();
        this.usedMatchingTask = false;
        this.includeEmpty = false;
        this.verbosity = 3;
        this.quiet = false;
        this.failonerror = true;
        this.deleteOnExit = false;
        this.removeNotFollowedSymlinks = false;
        this.rcs = null;
        this.performGc = Os.isFamily("windows");
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public void setDir(final File dir) {
        this.dir = dir;
        this.getImplicitFileSet().setDir(dir);
    }
    
    public void setVerbose(final boolean verbose) {
        if (verbose) {
            this.verbosity = 2;
        }
        else {
            this.verbosity = 3;
        }
    }
    
    public void setQuiet(final boolean quiet) {
        this.quiet = quiet;
        if (quiet) {
            this.failonerror = false;
        }
    }
    
    public void setFailOnError(final boolean failonerror) {
        this.failonerror = failonerror;
    }
    
    public void setDeleteOnExit(final boolean deleteOnExit) {
        this.deleteOnExit = deleteOnExit;
    }
    
    public void setIncludeEmptyDirs(final boolean includeEmpty) {
        this.includeEmpty = includeEmpty;
    }
    
    public void setPerformGcOnFailedDelete(final boolean b) {
        this.performGc = b;
    }
    
    public void addFileset(final FileSet set) {
        this.filesets.addElement(set);
    }
    
    public void add(final ResourceCollection rc) {
        if (rc == null) {
            return;
        }
        if (this.rcs == null) {
            (this.rcs = new Resources()).setCache(true);
        }
        this.rcs.add(rc);
    }
    
    @Override
    public PatternSet.NameEntry createInclude() {
        this.usedMatchingTask = true;
        return super.createInclude();
    }
    
    @Override
    public PatternSet.NameEntry createIncludesFile() {
        this.usedMatchingTask = true;
        return super.createIncludesFile();
    }
    
    @Override
    public PatternSet.NameEntry createExclude() {
        this.usedMatchingTask = true;
        return super.createExclude();
    }
    
    @Override
    public PatternSet.NameEntry createExcludesFile() {
        this.usedMatchingTask = true;
        return super.createExcludesFile();
    }
    
    @Override
    public PatternSet createPatternSet() {
        this.usedMatchingTask = true;
        return super.createPatternSet();
    }
    
    @Override
    public void setIncludes(final String includes) {
        this.usedMatchingTask = true;
        super.setIncludes(includes);
    }
    
    @Override
    public void setExcludes(final String excludes) {
        this.usedMatchingTask = true;
        super.setExcludes(excludes);
    }
    
    @Override
    public void setDefaultexcludes(final boolean useDefaultExcludes) {
        this.usedMatchingTask = true;
        super.setDefaultexcludes(useDefaultExcludes);
    }
    
    @Override
    public void setIncludesfile(final File includesfile) {
        this.usedMatchingTask = true;
        super.setIncludesfile(includesfile);
    }
    
    @Override
    public void setExcludesfile(final File excludesfile) {
        this.usedMatchingTask = true;
        super.setExcludesfile(excludesfile);
    }
    
    @Override
    public void setCaseSensitive(final boolean isCaseSensitive) {
        this.usedMatchingTask = true;
        super.setCaseSensitive(isCaseSensitive);
    }
    
    @Override
    public void setFollowSymlinks(final boolean followSymlinks) {
        this.usedMatchingTask = true;
        super.setFollowSymlinks(followSymlinks);
    }
    
    public void setRemoveNotFollowedSymlinks(final boolean b) {
        this.removeNotFollowedSymlinks = b;
    }
    
    @Override
    public void addSelector(final SelectSelector selector) {
        this.usedMatchingTask = true;
        super.addSelector(selector);
    }
    
    @Override
    public void addAnd(final AndSelector selector) {
        this.usedMatchingTask = true;
        super.addAnd(selector);
    }
    
    @Override
    public void addOr(final OrSelector selector) {
        this.usedMatchingTask = true;
        super.addOr(selector);
    }
    
    @Override
    public void addNot(final NotSelector selector) {
        this.usedMatchingTask = true;
        super.addNot(selector);
    }
    
    @Override
    public void addNone(final NoneSelector selector) {
        this.usedMatchingTask = true;
        super.addNone(selector);
    }
    
    @Override
    public void addMajority(final MajoritySelector selector) {
        this.usedMatchingTask = true;
        super.addMajority(selector);
    }
    
    @Override
    public void addDate(final DateSelector selector) {
        this.usedMatchingTask = true;
        super.addDate(selector);
    }
    
    @Override
    public void addSize(final SizeSelector selector) {
        this.usedMatchingTask = true;
        super.addSize(selector);
    }
    
    @Override
    public void addFilename(final FilenameSelector selector) {
        this.usedMatchingTask = true;
        super.addFilename(selector);
    }
    
    @Override
    public void addCustom(final ExtendSelector selector) {
        this.usedMatchingTask = true;
        super.addCustom(selector);
    }
    
    @Override
    public void addContains(final ContainsSelector selector) {
        this.usedMatchingTask = true;
        super.addContains(selector);
    }
    
    @Override
    public void addPresent(final PresentSelector selector) {
        this.usedMatchingTask = true;
        super.addPresent(selector);
    }
    
    @Override
    public void addDepth(final DepthSelector selector) {
        this.usedMatchingTask = true;
        super.addDepth(selector);
    }
    
    @Override
    public void addDepend(final DependSelector selector) {
        this.usedMatchingTask = true;
        super.addDepend(selector);
    }
    
    @Override
    public void addContainsRegexp(final ContainsRegexpSelector selector) {
        this.usedMatchingTask = true;
        super.addContainsRegexp(selector);
    }
    
    @Override
    public void addModified(final ModifiedSelector selector) {
        this.usedMatchingTask = true;
        super.addModified(selector);
    }
    
    @Override
    public void add(final FileSelector selector) {
        this.usedMatchingTask = true;
        super.add(selector);
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.usedMatchingTask) {
            this.log("DEPRECATED - Use of the implicit FileSet is deprecated.  Use a nested fileset element instead.", this.quiet ? 3 : this.verbosity);
        }
        if (this.file == null && this.dir == null && this.filesets.size() == 0 && this.rcs == null) {
            throw new BuildException("At least one of the file or dir attributes, or a nested resource collection, must be set.");
        }
        if (this.quiet && this.failonerror) {
            throw new BuildException("quiet and failonerror cannot both be set to true", this.getLocation());
        }
        if (this.file != null) {
            if (this.file.exists()) {
                if (this.file.isDirectory()) {
                    this.log("Directory " + this.file.getAbsolutePath() + " cannot be removed using the file attribute.  " + "Use dir instead.", this.quiet ? 3 : this.verbosity);
                }
                else {
                    this.log("Deleting: " + this.file.getAbsolutePath());
                    if (!this.delete(this.file)) {
                        this.handle("Unable to delete file " + this.file.getAbsolutePath());
                    }
                }
            }
            else if (this.isDanglingSymlink(this.file)) {
                this.log("Trying to delete file " + this.file.getAbsolutePath() + " which looks like a broken symlink.", this.quiet ? 3 : this.verbosity);
                if (!this.delete(this.file)) {
                    this.handle("Unable to delete file " + this.file.getAbsolutePath());
                }
            }
            else {
                this.log("Could not find file " + this.file.getAbsolutePath() + " to delete.", this.quiet ? 3 : this.verbosity);
            }
        }
        if (this.dir != null && !this.usedMatchingTask) {
            if (this.dir.exists() && this.dir.isDirectory()) {
                if (this.verbosity == 3) {
                    this.log("Deleting directory " + this.dir.getAbsolutePath());
                }
                this.removeDir(this.dir);
            }
            else if (this.isDanglingSymlink(this.dir)) {
                this.log("Trying to delete directory " + this.dir.getAbsolutePath() + " which looks like a broken symlink.", this.quiet ? 3 : this.verbosity);
                if (!this.delete(this.dir)) {
                    this.handle("Unable to delete directory " + this.dir.getAbsolutePath());
                }
            }
        }
        final Resources resourcesToDelete = new Resources();
        resourcesToDelete.setProject(this.getProject());
        resourcesToDelete.setCache(true);
        final Resources filesetDirs = new Resources();
        filesetDirs.setProject(this.getProject());
        filesetDirs.setCache(true);
        FileSet implicit = null;
        if (this.usedMatchingTask && this.dir != null && this.dir.isDirectory()) {
            implicit = this.getImplicitFileSet();
            implicit.setProject(this.getProject());
            this.filesets.add(implicit);
        }
        for (int size = this.filesets.size(), i = 0; i < size; ++i) {
            FileSet fs = this.filesets.get(i);
            if (fs.getProject() == null) {
                this.log("Deleting fileset with no project specified; assuming executing project", 3);
                fs = (FileSet)fs.clone();
                fs.setProject(this.getProject());
            }
            final File fsDir = fs.getDir();
            if (!fs.getErrorOnMissingDir()) {
                if (fsDir == null) {
                    continue;
                }
                if (!fsDir.exists()) {
                    continue;
                }
            }
            if (fsDir == null) {
                throw new BuildException("File or Resource without directory or file specified");
            }
            if (!fsDir.isDirectory()) {
                this.handle("Directory does not exist: " + fsDir);
            }
            else {
                final DirectoryScanner ds = fs.getDirectoryScanner();
                final String[] files = ds.getIncludedFiles();
                resourcesToDelete.add(new ResourceCollection() {
                    public boolean isFilesystemOnly() {
                        return true;
                    }
                    
                    public int size() {
                        return files.length;
                    }
                    
                    public Iterator<Resource> iterator() {
                        return new FileResourceIterator(Delete.this.getProject(), fsDir, files);
                    }
                });
                if (this.includeEmpty) {
                    filesetDirs.add(new ReverseDirs(this.getProject(), fsDir, ds.getIncludedDirectories()));
                }
                if (this.removeNotFollowedSymlinks) {
                    final String[] n = ds.getNotFollowedSymlinks();
                    if (n.length > 0) {
                        final String[] links = new String[n.length];
                        System.arraycopy(n, 0, links, 0, n.length);
                        Arrays.sort(links, ReverseDirs.REVERSE);
                        for (int l = 0; l < links.length; ++l) {
                            try {
                                Delete.SYMLINK_UTILS.deleteSymbolicLink(new File(links[l]), this);
                            }
                            catch (IOException ex) {
                                this.handle(ex);
                            }
                        }
                    }
                }
            }
        }
        resourcesToDelete.add(filesetDirs);
        if (this.rcs != null) {
            final Restrict exists = new Restrict();
            exists.add(Delete.EXISTS);
            exists.add(this.rcs);
            final Sort s = new Sort();
            s.add(Delete.REVERSE_FILESYSTEM);
            s.add(exists);
            resourcesToDelete.add(s);
        }
        try {
            if (resourcesToDelete.isFilesystemOnly()) {
                for (final Resource r : resourcesToDelete) {
                    final File f = r.as(FileProvider.class).getFile();
                    if (!f.exists()) {
                        continue;
                    }
                    if (f.isDirectory() && f.list().length != 0) {
                        continue;
                    }
                    this.log("Deleting " + f, this.verbosity);
                    if (this.delete(f) || !this.failonerror) {
                        continue;
                    }
                    this.handle("Unable to delete " + (f.isDirectory() ? "directory " : "file ") + f);
                }
            }
            else {
                this.handle(this.getTaskName() + " handles only filesystem resources");
            }
        }
        catch (Exception e) {
            this.handle(e);
        }
        finally {
            if (implicit != null) {
                this.filesets.remove(implicit);
            }
        }
    }
    
    private void handle(final String msg) {
        this.handle(new BuildException(msg));
    }
    
    private void handle(final Exception e) {
        if (this.failonerror) {
            throw (e instanceof BuildException) ? e : new BuildException(e);
        }
        this.log(e, this.quiet ? 3 : this.verbosity);
    }
    
    private boolean delete(final File f) {
        if (Delete.FILE_UTILS.tryHardToDelete(f, this.performGc)) {
            return true;
        }
        if (this.deleteOnExit) {
            final int level = this.quiet ? 3 : 2;
            this.log("Failed to delete " + f + ", calling deleteOnExit." + " This attempts to delete the file when the Ant jvm" + " has exited and might not succeed.", level);
            f.deleteOnExit();
            return true;
        }
        return false;
    }
    
    protected void removeDir(final File d) {
        String[] list = d.list();
        if (list == null) {
            list = new String[0];
        }
        for (int i = 0; i < list.length; ++i) {
            final String s = list[i];
            final File f = new File(d, s);
            if (f.isDirectory()) {
                this.removeDir(f);
            }
            else {
                this.log("Deleting " + f.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
                if (!this.delete(f)) {
                    this.handle("Unable to delete file " + f.getAbsolutePath());
                }
            }
        }
        this.log("Deleting directory " + d.getAbsolutePath(), this.verbosity);
        if (!this.delete(d)) {
            this.handle("Unable to delete directory " + d.getAbsolutePath());
        }
    }
    
    protected void removeFiles(final File d, final String[] files, final String[] dirs) {
        if (files.length > 0) {
            this.log("Deleting " + files.length + " files from " + d.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
            for (int j = 0; j < files.length; ++j) {
                final File f = new File(d, files[j]);
                this.log("Deleting " + f.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
                if (!this.delete(f)) {
                    this.handle("Unable to delete file " + f.getAbsolutePath());
                }
            }
        }
        if (dirs.length > 0 && this.includeEmpty) {
            int dirCount = 0;
            for (int i = dirs.length - 1; i >= 0; --i) {
                final File currDir = new File(d, dirs[i]);
                final String[] dirFiles = currDir.list();
                if (dirFiles == null || dirFiles.length == 0) {
                    this.log("Deleting " + currDir.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
                    if (!this.delete(currDir)) {
                        this.handle("Unable to delete directory " + currDir.getAbsolutePath());
                    }
                    else {
                        ++dirCount;
                    }
                }
            }
            if (dirCount > 0) {
                this.log("Deleted " + dirCount + " director" + ((dirCount == 1) ? "y" : "ies") + " form " + d.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
            }
        }
    }
    
    private boolean isDanglingSymlink(final File f) {
        try {
            return Delete.SYMLINK_UTILS.isDanglingSymbolicLink(f);
        }
        catch (IOException e) {
            this.log("Error while trying to detect " + f.getAbsolutePath() + " as broken symbolic link. " + e.getMessage(), this.quiet ? 3 : this.verbosity);
            return false;
        }
    }
    
    static {
        REVERSE_FILESYSTEM = new Reverse(new FileSystem());
        EXISTS = new Exists();
        Delete.FILE_UTILS = FileUtils.getFileUtils();
        Delete.SYMLINK_UTILS = SymbolicLinkUtils.getSymbolicLinkUtils();
    }
    
    private static class ReverseDirs implements ResourceCollection
    {
        static final Comparator<Comparable<?>> REVERSE;
        private Project project;
        private File basedir;
        private String[] dirs;
        
        ReverseDirs(final Project project, final File basedir, final String[] dirs) {
            this.project = project;
            this.basedir = basedir;
            Arrays.sort(this.dirs = dirs, ReverseDirs.REVERSE);
        }
        
        public Iterator<Resource> iterator() {
            return new FileResourceIterator(this.project, this.basedir, this.dirs);
        }
        
        public boolean isFilesystemOnly() {
            return true;
        }
        
        public int size() {
            return this.dirs.length;
        }
        
        static {
            REVERSE = new Comparator<Comparable<?>>() {
                public int compare(final Comparable<?> foo, final Comparable<?> bar) {
                    return foo.compareTo(bar) * -1;
                }
            };
        }
    }
}
