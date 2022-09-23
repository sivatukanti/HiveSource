// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import java.util.Stack;
import java.util.Iterator;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.BuildException;
import java.io.File;

public abstract class ArchiveFileSet extends FileSet
{
    private static final int BASE_OCTAL = 8;
    public static final int DEFAULT_DIR_MODE = 16877;
    public static final int DEFAULT_FILE_MODE = 33188;
    private Resource src;
    private String prefix;
    private String fullpath;
    private boolean hasDir;
    private int fileMode;
    private int dirMode;
    private boolean fileModeHasBeenSet;
    private boolean dirModeHasBeenSet;
    private static final String ERROR_DIR_AND_SRC_ATTRIBUTES = "Cannot set both dir and src attributes";
    private static final String ERROR_PATH_AND_PREFIX = "Cannot set both fullpath and prefix attributes";
    private boolean errorOnMissingArchive;
    
    public ArchiveFileSet() {
        this.src = null;
        this.prefix = "";
        this.fullpath = "";
        this.hasDir = false;
        this.fileMode = 33188;
        this.dirMode = 16877;
        this.fileModeHasBeenSet = false;
        this.dirModeHasBeenSet = false;
        this.errorOnMissingArchive = true;
    }
    
    protected ArchiveFileSet(final FileSet fileset) {
        super(fileset);
        this.src = null;
        this.prefix = "";
        this.fullpath = "";
        this.hasDir = false;
        this.fileMode = 33188;
        this.dirMode = 16877;
        this.fileModeHasBeenSet = false;
        this.dirModeHasBeenSet = false;
        this.errorOnMissingArchive = true;
    }
    
    protected ArchiveFileSet(final ArchiveFileSet fileset) {
        super(fileset);
        this.src = null;
        this.prefix = "";
        this.fullpath = "";
        this.hasDir = false;
        this.fileMode = 33188;
        this.dirMode = 16877;
        this.fileModeHasBeenSet = false;
        this.dirModeHasBeenSet = false;
        this.errorOnMissingArchive = true;
        this.src = fileset.src;
        this.prefix = fileset.prefix;
        this.fullpath = fileset.fullpath;
        this.hasDir = fileset.hasDir;
        this.fileMode = fileset.fileMode;
        this.dirMode = fileset.dirMode;
        this.fileModeHasBeenSet = fileset.fileModeHasBeenSet;
        this.dirModeHasBeenSet = fileset.dirModeHasBeenSet;
        this.errorOnMissingArchive = fileset.errorOnMissingArchive;
    }
    
    @Override
    public void setDir(final File dir) throws BuildException {
        this.checkAttributesAllowed();
        if (this.src != null) {
            throw new BuildException("Cannot set both dir and src attributes");
        }
        super.setDir(dir);
        this.hasDir = true;
    }
    
    public void addConfigured(final ResourceCollection a) {
        this.checkChildrenAllowed();
        if (a.size() != 1) {
            throw new BuildException("only single argument resource collections are supported as archives");
        }
        this.setSrcResource(a.iterator().next());
    }
    
    public void setSrc(final File srcFile) {
        this.setSrcResource(new FileResource(srcFile));
    }
    
    public void setSrcResource(final Resource src) {
        this.checkArchiveAttributesAllowed();
        if (this.hasDir) {
            throw new BuildException("Cannot set both dir and src attributes");
        }
        this.src = src;
        this.setChecked(false);
    }
    
    public File getSrc(final Project p) {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(p)).getSrc(p);
        }
        return this.getSrc();
    }
    
    public void setErrorOnMissingArchive(final boolean errorOnMissingArchive) {
        this.checkAttributesAllowed();
        this.errorOnMissingArchive = errorOnMissingArchive;
    }
    
    public File getSrc() {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getCheckedRef()).getSrc();
        }
        this.dieOnCircularReference();
        if (this.src != null) {
            final FileProvider fp = this.src.as(FileProvider.class);
            if (fp != null) {
                return fp.getFile();
            }
        }
        return null;
    }
    
    @Override
    protected Object getCheckedRef(final Project p) {
        return this.getRef(p);
    }
    
    public void setPrefix(final String prefix) {
        this.checkArchiveAttributesAllowed();
        if (!"".equals(prefix) && !"".equals(this.fullpath)) {
            throw new BuildException("Cannot set both fullpath and prefix attributes");
        }
        this.prefix = prefix;
    }
    
    public String getPrefix(final Project p) {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(p)).getPrefix(p);
        }
        this.dieOnCircularReference(p);
        return this.prefix;
    }
    
    public void setFullpath(final String fullpath) {
        this.checkArchiveAttributesAllowed();
        if (!"".equals(this.prefix) && !"".equals(fullpath)) {
            throw new BuildException("Cannot set both fullpath and prefix attributes");
        }
        this.fullpath = fullpath;
    }
    
    public String getFullpath(final Project p) {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(p)).getFullpath(p);
        }
        this.dieOnCircularReference(p);
        return this.fullpath;
    }
    
    protected abstract ArchiveScanner newArchiveScanner();
    
    @Override
    public DirectoryScanner getDirectoryScanner(final Project p) {
        if (this.isReference()) {
            return this.getRef(p).getDirectoryScanner(p);
        }
        this.dieOnCircularReference();
        if (this.src == null) {
            return super.getDirectoryScanner(p);
        }
        if (!this.src.isExists() && this.errorOnMissingArchive) {
            throw new BuildException("The archive " + this.src.getName() + " doesn't exist");
        }
        if (this.src.isDirectory()) {
            throw new BuildException("The archive " + this.src.getName() + " can't be a directory");
        }
        final ArchiveScanner as = this.newArchiveScanner();
        as.setErrorOnMissingArchive(this.errorOnMissingArchive);
        as.setSrc(this.src);
        super.setDir(p.getBaseDir());
        this.setupDirectoryScanner(as, p);
        as.init();
        return as;
    }
    
    @Override
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return ((ResourceCollection)this.getRef(this.getProject())).iterator();
        }
        if (this.src == null) {
            return super.iterator();
        }
        final ArchiveScanner as = (ArchiveScanner)this.getDirectoryScanner(this.getProject());
        return as.getResourceFiles(this.getProject());
    }
    
    @Override
    public int size() {
        if (this.isReference()) {
            return ((ResourceCollection)this.getRef(this.getProject())).size();
        }
        if (this.src == null) {
            return super.size();
        }
        final ArchiveScanner as = (ArchiveScanner)this.getDirectoryScanner(this.getProject());
        return as.getIncludedFilesCount();
    }
    
    @Override
    public boolean isFilesystemOnly() {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getCheckedRef()).isFilesystemOnly();
        }
        this.dieOnCircularReference();
        return this.src == null;
    }
    
    public void setFileMode(final String octalString) {
        this.checkArchiveAttributesAllowed();
        this.integerSetFileMode(Integer.parseInt(octalString, 8));
    }
    
    public void integerSetFileMode(final int mode) {
        this.fileModeHasBeenSet = true;
        this.fileMode = (0x8000 | mode);
    }
    
    public int getFileMode(final Project p) {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(p)).getFileMode(p);
        }
        this.dieOnCircularReference();
        return this.fileMode;
    }
    
    public boolean hasFileModeBeenSet() {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(this.getProject())).hasFileModeBeenSet();
        }
        this.dieOnCircularReference();
        return this.fileModeHasBeenSet;
    }
    
    public void setDirMode(final String octalString) {
        this.checkArchiveAttributesAllowed();
        this.integerSetDirMode(Integer.parseInt(octalString, 8));
    }
    
    public void integerSetDirMode(final int mode) {
        this.dirModeHasBeenSet = true;
        this.dirMode = (0x4000 | mode);
    }
    
    public int getDirMode(final Project p) {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(p)).getDirMode(p);
        }
        this.dieOnCircularReference();
        return this.dirMode;
    }
    
    public boolean hasDirModeBeenSet() {
        if (this.isReference()) {
            return ((ArchiveFileSet)this.getRef(this.getProject())).hasDirModeBeenSet();
        }
        this.dieOnCircularReference();
        return this.dirModeHasBeenSet;
    }
    
    protected void configureFileSet(final ArchiveFileSet zfs) {
        zfs.setPrefix(this.prefix);
        zfs.setFullpath(this.fullpath);
        zfs.fileModeHasBeenSet = this.fileModeHasBeenSet;
        zfs.fileMode = this.fileMode;
        zfs.dirModeHasBeenSet = this.dirModeHasBeenSet;
        zfs.dirMode = this.dirMode;
    }
    
    @Override
    public Object clone() {
        if (this.isReference()) {
            return this.getCheckedRef(ArchiveFileSet.class, this.getDataTypeName(), this.getProject()).clone();
        }
        return super.clone();
    }
    
    @Override
    public String toString() {
        if (this.hasDir && this.getProject() != null) {
            return super.toString();
        }
        return (this.src == null) ? null : this.src.getName();
    }
    
    @Deprecated
    public String getPrefix() {
        return this.prefix;
    }
    
    @Deprecated
    public String getFullpath() {
        return this.fullpath;
    }
    
    @Deprecated
    public int getFileMode() {
        return this.fileMode;
    }
    
    @Deprecated
    public int getDirMode() {
        return this.dirMode;
    }
    
    private void checkArchiveAttributesAllowed() {
        if (this.getProject() == null || (this.isReference() && this.getRefid().getReferencedObject(this.getProject()) instanceof ArchiveFileSet)) {
            this.checkAttributesAllowed();
        }
    }
    
    @Override
    protected synchronized void dieOnCircularReference(final Stack<Object> stk, final Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        super.dieOnCircularReference(stk, p);
        if (!this.isReference()) {
            if (this.src != null) {
                DataType.pushAndInvokeCircularReferenceCheck(this.src, stk, p);
            }
            this.setChecked(true);
        }
    }
}
