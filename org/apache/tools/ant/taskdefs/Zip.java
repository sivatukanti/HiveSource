// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.zip.Zip64Mode;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.types.EnumeratedAttribute;
import java.util.Enumeration;
import java.util.Stack;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.CRC32;
import org.apache.tools.ant.types.resources.FileResource;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.util.Arrays;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.MergingMapper;
import org.apache.tools.ant.util.IdentityMapper;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.tools.ant.types.resources.FileProvider;
import java.io.InputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.ant.types.resources.ArchiveResource;
import org.apache.tools.ant.types.resources.ZipResource;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.ant.types.ArchiveFileSet;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.PatternSet;
import java.io.IOException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.zip.ZipOutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.zip.ZipExtraField;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.types.FileSet;
import java.util.Vector;
import java.util.Hashtable;
import org.apache.tools.ant.types.ZipScanner;
import java.io.File;

public class Zip extends MatchingTask
{
    private static final int BUFFER_SIZE = 8192;
    private static final int ROUNDUP_MILLIS = 1999;
    protected File zipFile;
    private ZipScanner zs;
    private File baseDir;
    protected Hashtable<String, String> entries;
    private Vector<FileSet> groupfilesets;
    private Vector<ZipFileSet> filesetsFromGroupfilesets;
    protected String duplicate;
    private boolean doCompress;
    private boolean doUpdate;
    private boolean savedDoUpdate;
    private boolean doFilesonly;
    protected String archiveType;
    private static final long EMPTY_CRC;
    protected String emptyBehavior;
    private Vector<ResourceCollection> resources;
    protected Hashtable<String, String> addedDirs;
    private Vector<String> addedFiles;
    private static final ResourceSelector MISSING_SELECTOR;
    private static final ResourceUtils.ResourceSelectorProvider MISSING_DIR_PROVIDER;
    protected boolean doubleFilePass;
    protected boolean skipWriting;
    private static final FileUtils FILE_UTILS;
    private boolean updatedFile;
    private boolean addingNewFiles;
    private String encoding;
    private boolean keepCompression;
    private boolean roundUp;
    private String comment;
    private int level;
    private boolean preserve0Permissions;
    private boolean useLanguageEncodingFlag;
    private UnicodeExtraField createUnicodeExtraFields;
    private boolean fallBackToUTF8;
    private Zip64ModeAttribute zip64Mode;
    private static final ThreadLocal<Boolean> HAVE_NON_FILE_SET_RESOURCES_TO_ADD;
    private static final ThreadLocal<ZipExtraField[]> CURRENT_ZIP_EXTRA;
    
    public Zip() {
        this.entries = new Hashtable<String, String>();
        this.groupfilesets = new Vector<FileSet>();
        this.filesetsFromGroupfilesets = new Vector<ZipFileSet>();
        this.duplicate = "add";
        this.doCompress = true;
        this.doUpdate = false;
        this.savedDoUpdate = false;
        this.doFilesonly = false;
        this.archiveType = "zip";
        this.emptyBehavior = "skip";
        this.resources = new Vector<ResourceCollection>();
        this.addedDirs = new Hashtable<String, String>();
        this.addedFiles = new Vector<String>();
        this.doubleFilePass = false;
        this.skipWriting = false;
        this.updatedFile = false;
        this.addingNewFiles = false;
        this.keepCompression = false;
        this.roundUp = true;
        this.comment = "";
        this.level = -1;
        this.preserve0Permissions = false;
        this.useLanguageEncodingFlag = true;
        this.createUnicodeExtraFields = UnicodeExtraField.NEVER;
        this.fallBackToUTF8 = false;
        this.zip64Mode = Zip64ModeAttribute.AS_NEEDED;
    }
    
    protected final boolean isFirstPass() {
        return !this.doubleFilePass || this.skipWriting;
    }
    
    @Deprecated
    public void setZipfile(final File zipFile) {
        this.setDestFile(zipFile);
    }
    
    @Deprecated
    public void setFile(final File file) {
        this.setDestFile(file);
    }
    
    public void setDestFile(final File destFile) {
        this.zipFile = destFile;
    }
    
    public File getDestFile() {
        return this.zipFile;
    }
    
    public void setBasedir(final File baseDir) {
        this.baseDir = baseDir;
    }
    
    public void setCompress(final boolean c) {
        this.doCompress = c;
    }
    
    public boolean isCompress() {
        return this.doCompress;
    }
    
    public void setFilesonly(final boolean f) {
        this.doFilesonly = f;
    }
    
    public void setUpdate(final boolean c) {
        this.doUpdate = c;
        this.savedDoUpdate = c;
    }
    
    public boolean isInUpdateMode() {
        return this.doUpdate;
    }
    
    public void addFileset(final FileSet set) {
        this.add(set);
    }
    
    public void addZipfileset(final ZipFileSet set) {
        this.add(set);
    }
    
    public void add(final ResourceCollection a) {
        this.resources.add(a);
    }
    
    public void addZipGroupFileset(final FileSet set) {
        this.groupfilesets.addElement(set);
    }
    
    public void setDuplicate(final Duplicate df) {
        this.duplicate = df.getValue();
    }
    
    public void setWhenempty(final WhenEmpty we) {
        this.emptyBehavior = we.getValue();
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setKeepCompression(final boolean keep) {
        this.keepCompression = keep;
    }
    
    public void setComment(final String comment) {
        this.comment = comment;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setLevel(final int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public void setRoundUp(final boolean r) {
        this.roundUp = r;
    }
    
    public void setPreserve0Permissions(final boolean b) {
        this.preserve0Permissions = b;
    }
    
    public boolean getPreserve0Permissions() {
        return this.preserve0Permissions;
    }
    
    public void setUseLanguageEncodingFlag(final boolean b) {
        this.useLanguageEncodingFlag = b;
    }
    
    public boolean getUseLanguageEnodingFlag() {
        return this.useLanguageEncodingFlag;
    }
    
    public void setCreateUnicodeExtraFields(final UnicodeExtraField b) {
        this.createUnicodeExtraFields = b;
    }
    
    public UnicodeExtraField getCreateUnicodeExtraFields() {
        return this.createUnicodeExtraFields;
    }
    
    public void setFallBackToUTF8(final boolean b) {
        this.fallBackToUTF8 = b;
    }
    
    public boolean getFallBackToUTF8() {
        return this.fallBackToUTF8;
    }
    
    public void setZip64Mode(final Zip64ModeAttribute b) {
        this.zip64Mode = b;
    }
    
    public Zip64ModeAttribute getZip64Mode() {
        return this.zip64Mode;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.doubleFilePass) {
            this.skipWriting = true;
            this.executeMain();
            this.skipWriting = false;
            this.executeMain();
        }
        else {
            this.executeMain();
        }
    }
    
    protected boolean hasUpdatedFile() {
        return this.updatedFile;
    }
    
    public void executeMain() throws BuildException {
        this.checkAttributesAndElements();
        File renamedFile = null;
        this.addingNewFiles = true;
        this.processDoUpdate();
        this.processGroupFilesets();
        final Vector<ResourceCollection> vfss = new Vector<ResourceCollection>();
        if (this.baseDir != null) {
            final FileSet fs = (FileSet)this.getImplicitFileSet().clone();
            fs.setDir(this.baseDir);
            vfss.addElement(fs);
        }
        for (int size = this.resources.size(), i = 0; i < size; ++i) {
            final ResourceCollection rc = this.resources.elementAt(i);
            vfss.addElement(rc);
        }
        final ResourceCollection[] fss = new ResourceCollection[vfss.size()];
        vfss.copyInto(fss);
        boolean success = false;
        try {
            final ArchiveState state = this.getResourcesToAdd(fss, this.zipFile, false);
            if (!state.isOutOfDate()) {
                return;
            }
            final File parent = this.zipFile.getParentFile();
            if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
                throw new BuildException("Failed to create missing parent directory for " + this.zipFile);
            }
            this.updatedFile = true;
            if (!this.zipFile.exists() && state.isWithoutAnyResources()) {
                this.createEmptyZip(this.zipFile);
                return;
            }
            final Resource[][] addThem = state.getResourcesToAdd();
            if (this.doUpdate) {
                renamedFile = this.renameFile();
            }
            final String action = this.doUpdate ? "Updating " : "Building ";
            if (!this.skipWriting) {
                this.log(action + this.archiveType + ": " + this.zipFile.getAbsolutePath());
            }
            ZipOutputStream zOut = null;
            try {
                if (!this.skipWriting) {
                    zOut = new ZipOutputStream(this.zipFile);
                    zOut.setEncoding(this.encoding);
                    zOut.setUseLanguageEncodingFlag(this.useLanguageEncodingFlag);
                    zOut.setCreateUnicodeExtraFields(this.createUnicodeExtraFields.getPolicy());
                    zOut.setFallbackToUTF8(this.fallBackToUTF8);
                    zOut.setMethod(this.doCompress ? 8 : 0);
                    zOut.setLevel(this.level);
                    zOut.setUseZip64(this.zip64Mode.getMode());
                }
                this.initZipOutputStream(zOut);
                for (int j = 0; j < fss.length; ++j) {
                    if (addThem[j].length != 0) {
                        this.addResources(fss[j], addThem[j], zOut);
                    }
                }
                if (this.doUpdate) {
                    this.addingNewFiles = false;
                    final ZipFileSet oldFiles = new ZipFileSet();
                    oldFiles.setProject(this.getProject());
                    oldFiles.setSrc(renamedFile);
                    oldFiles.setDefaultexcludes(false);
                    for (int addSize = this.addedFiles.size(), k = 0; k < addSize; ++k) {
                        final PatternSet.NameEntry ne = oldFiles.createExclude();
                        ne.setName(this.addedFiles.elementAt(k));
                    }
                    final DirectoryScanner ds = oldFiles.getDirectoryScanner(this.getProject());
                    ((ZipScanner)ds).setEncoding(this.encoding);
                    final String[] f = ds.getIncludedFiles();
                    Resource[] r = new Resource[f.length];
                    for (int l = 0; l < f.length; ++l) {
                        r[l] = ds.getResource(f[l]);
                    }
                    if (!this.doFilesonly) {
                        final String[] d = ds.getIncludedDirectories();
                        final Resource[] dr = new Resource[d.length];
                        for (int m = 0; m < d.length; ++m) {
                            dr[m] = ds.getResource(d[m]);
                        }
                        final Resource[] tmp = r;
                        r = new Resource[tmp.length + dr.length];
                        System.arraycopy(dr, 0, r, 0, dr.length);
                        System.arraycopy(tmp, 0, r, dr.length, tmp.length);
                    }
                    this.addResources(oldFiles, r, zOut);
                }
                if (zOut != null) {
                    zOut.setComment(this.comment);
                }
                this.finalizeZipOutputStream(zOut);
                if (this.doUpdate && !renamedFile.delete()) {
                    this.log("Warning: unable to delete temporary file " + renamedFile.getName(), 1);
                }
                success = true;
            }
            finally {
                this.closeZout(zOut, success);
            }
        }
        catch (IOException ioe) {
            String msg = "Problem creating " + this.archiveType + ": " + ioe.getMessage();
            if ((!this.doUpdate || renamedFile != null) && !this.zipFile.delete()) {
                msg += " (and the archive is probably corrupt but I could not delete it)";
            }
            if (this.doUpdate && renamedFile != null) {
                try {
                    Zip.FILE_UTILS.rename(renamedFile, this.zipFile);
                }
                catch (IOException e) {
                    msg = msg + " (and I couldn't rename the temporary file " + renamedFile.getName() + " back)";
                }
            }
            throw new BuildException(msg, ioe, this.getLocation());
        }
        finally {
            this.cleanUp();
        }
    }
    
    private File renameFile() {
        final File renamedFile = Zip.FILE_UTILS.createTempFile("zip", ".tmp", this.zipFile.getParentFile(), true, false);
        try {
            Zip.FILE_UTILS.rename(this.zipFile, renamedFile);
        }
        catch (SecurityException e) {
            throw new BuildException("Not allowed to rename old file (" + this.zipFile.getAbsolutePath() + ") to temporary file");
        }
        catch (IOException e2) {
            throw new BuildException("Unable to rename old file (" + this.zipFile.getAbsolutePath() + ") to temporary file");
        }
        return renamedFile;
    }
    
    private void closeZout(final ZipOutputStream zOut, final boolean success) throws IOException {
        if (zOut == null) {
            return;
        }
        try {
            zOut.close();
        }
        catch (IOException ex) {
            if (success) {
                throw ex;
            }
        }
    }
    
    private void checkAttributesAndElements() {
        if (this.baseDir == null && this.resources.size() == 0 && this.groupfilesets.size() == 0 && "zip".equals(this.archiveType)) {
            throw new BuildException("basedir attribute must be set, or at least one resource collection must be given!");
        }
        if (this.zipFile == null) {
            throw new BuildException("You must specify the " + this.archiveType + " file to create!");
        }
        if (this.zipFile.exists() && !this.zipFile.isFile()) {
            throw new BuildException(this.zipFile + " is not a file.");
        }
        if (this.zipFile.exists() && !this.zipFile.canWrite()) {
            throw new BuildException(this.zipFile + " is read-only.");
        }
    }
    
    private void processDoUpdate() {
        if (this.doUpdate && !this.zipFile.exists()) {
            this.doUpdate = false;
            this.logWhenWriting("ignoring update attribute as " + this.archiveType + " doesn't exist.", 4);
        }
    }
    
    private void processGroupFilesets() {
        for (int size = this.groupfilesets.size(), i = 0; i < size; ++i) {
            this.logWhenWriting("Processing groupfileset ", 3);
            final FileSet fs = this.groupfilesets.elementAt(i);
            final FileScanner scanner = fs.getDirectoryScanner(this.getProject());
            final String[] files = scanner.getIncludedFiles();
            final File basedir = scanner.getBasedir();
            for (int j = 0; j < files.length; ++j) {
                this.logWhenWriting("Adding file " + files[j] + " to fileset", 3);
                final ZipFileSet zf = new ZipFileSet();
                zf.setProject(this.getProject());
                zf.setSrc(new File(basedir, files[j]));
                this.add(zf);
                this.filesetsFromGroupfilesets.addElement(zf);
            }
        }
    }
    
    protected final boolean isAddingNewFiles() {
        return this.addingNewFiles;
    }
    
    protected final void addResources(final FileSet fileset, final Resource[] resources, final ZipOutputStream zOut) throws IOException {
        String prefix = "";
        String fullpath = "";
        int dirMode = 16877;
        int fileMode = 33188;
        ArchiveFileSet zfs = null;
        if (fileset instanceof ArchiveFileSet) {
            zfs = (ArchiveFileSet)fileset;
            prefix = zfs.getPrefix(this.getProject());
            fullpath = zfs.getFullpath(this.getProject());
            dirMode = zfs.getDirMode(this.getProject());
            fileMode = zfs.getFileMode(this.getProject());
        }
        if (prefix.length() > 0 && fullpath.length() > 0) {
            throw new BuildException("Both prefix and fullpath attributes must not be set on the same fileset.");
        }
        if (resources.length != 1 && fullpath.length() > 0) {
            throw new BuildException("fullpath attribute may only be specified for filesets that specify a single file.");
        }
        if (prefix.length() > 0) {
            if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                prefix += "/";
            }
            this.addParentDirs(null, prefix, zOut, "", dirMode);
        }
        ZipFile zf = null;
        try {
            boolean dealingWithFiles = false;
            File base = null;
            if (zfs == null || zfs.getSrc(this.getProject()) == null) {
                dealingWithFiles = true;
                base = fileset.getDir(this.getProject());
            }
            else if (zfs instanceof ZipFileSet) {
                zf = new ZipFile(zfs.getSrc(this.getProject()), this.encoding);
            }
            for (int i = 0; i < resources.length; ++i) {
                String name = null;
                if (fullpath.length() > 0) {
                    name = fullpath;
                }
                else {
                    name = resources[i].getName();
                }
                name = name.replace(File.separatorChar, '/');
                if (!"".equals(name)) {
                    if (resources[i].isDirectory()) {
                        if (!this.doFilesonly) {
                            final int thisDirMode = (zfs != null && zfs.hasDirModeBeenSet()) ? dirMode : this.getUnixMode(resources[i], zf, dirMode);
                            this.addDirectoryResource(resources[i], name, prefix, base, zOut, dirMode, thisDirMode);
                        }
                    }
                    else {
                        this.addParentDirs(base, name, zOut, prefix, dirMode);
                        if (dealingWithFiles) {
                            final File f = Zip.FILE_UTILS.resolveFile(base, resources[i].getName());
                            this.zipFile(f, zOut, prefix + name, fileMode);
                        }
                        else {
                            final int thisFileMode = (zfs != null && zfs.hasFileModeBeenSet()) ? fileMode : this.getUnixMode(resources[i], zf, fileMode);
                            this.addResource(resources[i], name, prefix, zOut, thisFileMode, zf, (zfs == null) ? null : zfs.getSrc(this.getProject()));
                        }
                    }
                }
            }
        }
        finally {
            if (zf != null) {
                zf.close();
            }
        }
    }
    
    private void addDirectoryResource(final Resource r, String name, final String prefix, final File base, final ZipOutputStream zOut, final int defaultDirMode, final int thisDirMode) throws IOException {
        if (!name.endsWith("/")) {
            name += "/";
        }
        final int nextToLastSlash = name.lastIndexOf("/", name.length() - 2);
        if (nextToLastSlash != -1) {
            this.addParentDirs(base, name.substring(0, nextToLastSlash + 1), zOut, prefix, defaultDirMode);
        }
        this.zipDir(r, zOut, prefix + name, thisDirMode, (r instanceof ZipResource) ? ((ZipResource)r).getExtraFields() : null);
    }
    
    private int getUnixMode(final Resource r, final ZipFile zf, final int defaultMode) throws IOException {
        int unixMode = defaultMode;
        if (zf != null) {
            final ZipEntry ze = zf.getEntry(r.getName());
            unixMode = ze.getUnixMode();
            if ((unixMode == 0 || unixMode == 16384) && !this.preserve0Permissions) {
                unixMode = defaultMode;
            }
        }
        else if (r instanceof ArchiveResource) {
            unixMode = ((ArchiveResource)r).getMode();
        }
        return unixMode;
    }
    
    private void addResource(final Resource r, final String name, final String prefix, final ZipOutputStream zOut, final int mode, final ZipFile zf, final File fromArchive) throws IOException {
        if (zf != null) {
            final ZipEntry ze = zf.getEntry(r.getName());
            if (ze != null) {
                final boolean oldCompress = this.doCompress;
                if (this.keepCompression) {
                    this.doCompress = (ze.getMethod() == 8);
                }
                InputStream is = null;
                try {
                    is = zf.getInputStream(ze);
                    this.zipFile(is, zOut, prefix + name, ze.getTime(), fromArchive, mode, ze.getExtraFields(true));
                }
                finally {
                    this.doCompress = oldCompress;
                    FileUtils.close(is);
                }
            }
        }
        else {
            InputStream is2 = null;
            try {
                is2 = r.getInputStream();
                this.zipFile(is2, zOut, prefix + name, r.getLastModified(), fromArchive, mode, (r instanceof ZipResource) ? ((ZipResource)r).getExtraFields() : null);
            }
            finally {
                FileUtils.close(is2);
            }
        }
    }
    
    protected final void addResources(final ResourceCollection rc, final Resource[] resources, final ZipOutputStream zOut) throws IOException {
        if (rc instanceof FileSet) {
            this.addResources((FileSet)rc, resources, zOut);
            return;
        }
        for (int i = 0; i < resources.length; ++i) {
            final Resource resource = resources[i];
            String name = resource.getName();
            if (name != null) {
                name = name.replace(File.separatorChar, '/');
                if (!"".equals(name)) {
                    if (!resource.isDirectory() || !this.doFilesonly) {
                        File base = null;
                        final FileProvider fp = resource.as(FileProvider.class);
                        if (fp != null) {
                            base = ResourceUtils.asFileResource(fp).getBaseDir();
                        }
                        if (resource.isDirectory()) {
                            this.addDirectoryResource(resource, name, "", base, zOut, 16877, 16877);
                        }
                        else {
                            this.addParentDirs(base, name, zOut, "", 16877);
                            if (fp != null) {
                                final File f = fp.getFile();
                                this.zipFile(f, zOut, name, 33188);
                            }
                            else {
                                this.addResource(resource, name, "", zOut, 33188, null, null);
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void initZipOutputStream(final ZipOutputStream zOut) throws IOException, BuildException {
    }
    
    protected void finalizeZipOutputStream(final ZipOutputStream zOut) throws IOException, BuildException {
    }
    
    protected boolean createEmptyZip(final File zipFile) throws BuildException {
        if (!this.skipWriting) {
            this.log("Note: creating empty " + this.archiveType + " archive " + zipFile, 2);
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(zipFile);
            final byte[] empty = new byte[22];
            empty[0] = 80;
            empty[1] = 75;
            empty[2] = 5;
            empty[3] = 6;
            os.write(empty);
        }
        catch (IOException ioe) {
            throw new BuildException("Could not create empty ZIP archive (" + ioe.getMessage() + ")", ioe, this.getLocation());
        }
        finally {
            FileUtils.close(os);
        }
        return true;
    }
    
    private synchronized ZipScanner getZipScanner() {
        if (this.zs == null) {
            (this.zs = new ZipScanner()).setEncoding(this.encoding);
            this.zs.setSrc(this.zipFile);
        }
        return this.zs;
    }
    
    protected ArchiveState getResourcesToAdd(final ResourceCollection[] rcs, final File zipFile, final boolean needsUpdate) throws BuildException {
        final ArrayList<ResourceCollection> filesets = new ArrayList<ResourceCollection>();
        final ArrayList<ResourceCollection> rest = new ArrayList<ResourceCollection>();
        for (int i = 0; i < rcs.length; ++i) {
            if (rcs[i] instanceof FileSet) {
                filesets.add(rcs[i]);
            }
            else {
                rest.add(rcs[i]);
            }
        }
        final ResourceCollection[] rc = rest.toArray(new ResourceCollection[rest.size()]);
        ArchiveState as = this.getNonFileSetResourcesToAdd(rc, zipFile, needsUpdate);
        final FileSet[] fs = filesets.toArray(new FileSet[filesets.size()]);
        final ArchiveState as2 = this.getResourcesToAdd(fs, zipFile, as.isOutOfDate());
        if (!as.isOutOfDate() && as2.isOutOfDate()) {
            as = this.getNonFileSetResourcesToAdd(rc, zipFile, true);
        }
        final Resource[][] toAdd = new Resource[rcs.length][];
        int fsIndex = 0;
        int restIndex = 0;
        for (int j = 0; j < rcs.length; ++j) {
            if (rcs[j] instanceof FileSet) {
                toAdd[j] = as2.getResourcesToAdd()[fsIndex++];
            }
            else {
                toAdd[j] = as.getResourcesToAdd()[restIndex++];
            }
        }
        return new ArchiveState(as2.isOutOfDate(), toAdd);
    }
    
    protected ArchiveState getResourcesToAdd(final FileSet[] filesets, final File zipFile, boolean needsUpdate) throws BuildException {
        final Resource[][] initialResources = this.grabResources(filesets);
        if (isEmpty(initialResources)) {
            if (Boolean.FALSE.equals(Zip.HAVE_NON_FILE_SET_RESOURCES_TO_ADD.get())) {
                if (needsUpdate && this.doUpdate) {
                    return new ArchiveState(true, initialResources);
                }
                if (this.emptyBehavior.equals("skip")) {
                    if (this.doUpdate) {
                        this.logWhenWriting(this.archiveType + " archive " + zipFile + " not updated because no new files were" + " included.", 3);
                    }
                    else {
                        this.logWhenWriting("Warning: skipping " + this.archiveType + " archive " + zipFile + " because no files were included.", 1);
                    }
                }
                else {
                    if (this.emptyBehavior.equals("fail")) {
                        throw new BuildException("Cannot create " + this.archiveType + " archive " + zipFile + ": no files were included.", this.getLocation());
                    }
                    if (!zipFile.exists()) {
                        needsUpdate = true;
                    }
                }
            }
            return new ArchiveState(needsUpdate, initialResources);
        }
        if (!zipFile.exists()) {
            return new ArchiveState(true, initialResources);
        }
        if (needsUpdate && !this.doUpdate) {
            return new ArchiveState(true, initialResources);
        }
        final Resource[][] newerResources = new Resource[filesets.length][];
        for (int i = 0; i < filesets.length; ++i) {
            if (!(this.fileset instanceof ZipFileSet) || ((ZipFileSet)this.fileset).getSrc(this.getProject()) == null) {
                final File base = filesets[i].getDir(this.getProject());
                for (int j = 0; j < initialResources[i].length; ++j) {
                    final File resourceAsFile = Zip.FILE_UTILS.resolveFile(base, initialResources[i][j].getName());
                    if (resourceAsFile.equals(zipFile)) {
                        throw new BuildException("A zip file cannot include itself", this.getLocation());
                    }
                }
            }
        }
        for (int i = 0; i < filesets.length; ++i) {
            if (initialResources[i].length == 0) {
                newerResources[i] = new Resource[0];
            }
            else {
                FileNameMapper myMapper = new IdentityMapper();
                if (filesets[i] instanceof ZipFileSet) {
                    final ZipFileSet zfs = (ZipFileSet)filesets[i];
                    if (zfs.getFullpath(this.getProject()) != null && !zfs.getFullpath(this.getProject()).equals("")) {
                        final MergingMapper fm = new MergingMapper();
                        fm.setTo(zfs.getFullpath(this.getProject()));
                        myMapper = fm;
                    }
                    else if (zfs.getPrefix(this.getProject()) != null && !zfs.getPrefix(this.getProject()).equals("")) {
                        final GlobPatternMapper gm = new GlobPatternMapper();
                        gm.setFrom("*");
                        String prefix = zfs.getPrefix(this.getProject());
                        if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                            prefix += "/";
                        }
                        gm.setTo(prefix + "*");
                        myMapper = gm;
                    }
                }
                newerResources[i] = this.selectOutOfDateResources(initialResources[i], myMapper);
                needsUpdate = (needsUpdate || newerResources[i].length > 0);
                if (needsUpdate && !this.doUpdate) {
                    break;
                }
            }
        }
        if (needsUpdate && !this.doUpdate) {
            return new ArchiveState(true, initialResources);
        }
        return new ArchiveState(needsUpdate, newerResources);
    }
    
    protected ArchiveState getNonFileSetResourcesToAdd(final ResourceCollection[] rcs, final File zipFile, boolean needsUpdate) throws BuildException {
        final Resource[][] initialResources = this.grabNonFileSetResources(rcs);
        final boolean empty = isEmpty(initialResources);
        Zip.HAVE_NON_FILE_SET_RESOURCES_TO_ADD.set(!empty);
        if (empty) {
            return new ArchiveState(needsUpdate, initialResources);
        }
        if (!zipFile.exists()) {
            return new ArchiveState(true, initialResources);
        }
        if (needsUpdate && !this.doUpdate) {
            return new ArchiveState(true, initialResources);
        }
        final Resource[][] newerResources = new Resource[rcs.length][];
        for (int i = 0; i < rcs.length; ++i) {
            if (initialResources[i].length == 0) {
                newerResources[i] = new Resource[0];
            }
            else {
                for (int j = 0; j < initialResources[i].length; ++j) {
                    final FileProvider fp = initialResources[i][j].as(FileProvider.class);
                    if (fp != null && zipFile.equals(fp.getFile())) {
                        throw new BuildException("A zip file cannot include itself", this.getLocation());
                    }
                }
                newerResources[i] = this.selectOutOfDateResources(initialResources[i], new IdentityMapper());
                needsUpdate = (needsUpdate || newerResources[i].length > 0);
                if (needsUpdate && !this.doUpdate) {
                    break;
                }
            }
        }
        if (needsUpdate && !this.doUpdate) {
            return new ArchiveState(true, initialResources);
        }
        return new ArchiveState(needsUpdate, newerResources);
    }
    
    private Resource[] selectOutOfDateResources(final Resource[] initial, final FileNameMapper mapper) {
        final Resource[] rs = this.selectFileResources(initial);
        Resource[] result = ResourceUtils.selectOutOfDateSources(this, rs, mapper, this.getZipScanner());
        if (!this.doFilesonly) {
            final Union u = new Union();
            u.addAll(Arrays.asList(this.selectDirectoryResources(initial)));
            final ResourceCollection rc = ResourceUtils.selectSources(this, u, mapper, this.getZipScanner(), Zip.MISSING_DIR_PROVIDER);
            if (rc.size() > 0) {
                final ArrayList<Resource> newer = new ArrayList<Resource>();
                newer.addAll(Arrays.asList(((Union)rc).listResources()));
                newer.addAll(Arrays.asList(result));
                result = newer.toArray(result);
            }
        }
        return result;
    }
    
    protected Resource[][] grabResources(final FileSet[] filesets) {
        final Resource[][] result = new Resource[filesets.length][];
        for (int i = 0; i < filesets.length; ++i) {
            boolean skipEmptyNames = true;
            if (filesets[i] instanceof ZipFileSet) {
                final ZipFileSet zfs = (ZipFileSet)filesets[i];
                skipEmptyNames = (zfs.getPrefix(this.getProject()).equals("") && zfs.getFullpath(this.getProject()).equals(""));
            }
            final DirectoryScanner rs = filesets[i].getDirectoryScanner(this.getProject());
            if (rs instanceof ZipScanner) {
                ((ZipScanner)rs).setEncoding(this.encoding);
            }
            final Vector<Resource> resources = new Vector<Resource>();
            if (!this.doFilesonly) {
                final String[] directories = rs.getIncludedDirectories();
                for (int j = 0; j < directories.length; ++j) {
                    if (!"".equals(directories[j]) || !skipEmptyNames) {
                        resources.addElement(rs.getResource(directories[j]));
                    }
                }
            }
            final String[] files = rs.getIncludedFiles();
            for (int j = 0; j < files.length; ++j) {
                if (!"".equals(files[j]) || !skipEmptyNames) {
                    resources.addElement(rs.getResource(files[j]));
                }
            }
            resources.copyInto(result[i] = new Resource[resources.size()]);
        }
        return result;
    }
    
    protected Resource[][] grabNonFileSetResources(final ResourceCollection[] rcs) {
        final Resource[][] result = new Resource[rcs.length][];
        for (int i = 0; i < rcs.length; ++i) {
            final ArrayList<Resource> dirs = new ArrayList<Resource>();
            final ArrayList<Resource> files = new ArrayList<Resource>();
            for (final Resource r : rcs[i]) {
                if (r.isExists()) {
                    if (r.isDirectory()) {
                        dirs.add(r);
                    }
                    else {
                        files.add(r);
                    }
                }
            }
            Collections.sort(dirs, new Comparator<Resource>() {
                public int compare(final Resource r1, final Resource r2) {
                    return r1.getName().compareTo(r2.getName());
                }
            });
            final ArrayList<Resource> rs = new ArrayList<Resource>(dirs);
            rs.addAll(files);
            result[i] = rs.toArray(new Resource[rs.size()]);
        }
        return result;
    }
    
    protected void zipDir(final File dir, final ZipOutputStream zOut, final String vPath, final int mode) throws IOException {
        this.zipDir(dir, zOut, vPath, mode, null);
    }
    
    protected void zipDir(final File dir, final ZipOutputStream zOut, final String vPath, final int mode, final ZipExtraField[] extra) throws IOException {
        this.zipDir((dir == null) ? ((Resource)null) : new FileResource(dir), zOut, vPath, mode, extra);
    }
    
    protected void zipDir(final Resource dir, final ZipOutputStream zOut, final String vPath, final int mode, final ZipExtraField[] extra) throws IOException {
        if (this.doFilesonly) {
            this.logWhenWriting("skipping directory " + vPath + " for file-only archive", 3);
            return;
        }
        if (this.addedDirs.get(vPath) != null) {
            return;
        }
        this.logWhenWriting("adding directory " + vPath, 3);
        this.addedDirs.put(vPath, vPath);
        if (!this.skipWriting) {
            final ZipEntry ze = new ZipEntry(vPath);
            final int millisToAdd = this.roundUp ? 1999 : 0;
            if (dir != null && dir.isExists()) {
                ze.setTime(dir.getLastModified() + millisToAdd);
            }
            else {
                ze.setTime(System.currentTimeMillis() + millisToAdd);
            }
            ze.setSize(0L);
            ze.setMethod(0);
            ze.setCrc(Zip.EMPTY_CRC);
            ze.setUnixMode(mode);
            if (extra != null) {
                ze.setExtraFields(extra);
            }
            zOut.putNextEntry(ze);
        }
    }
    
    protected final ZipExtraField[] getCurrentExtraFields() {
        return Zip.CURRENT_ZIP_EXTRA.get();
    }
    
    protected final void setCurrentExtraFields(final ZipExtraField[] extra) {
        Zip.CURRENT_ZIP_EXTRA.set(extra);
    }
    
    protected void zipFile(InputStream in, final ZipOutputStream zOut, final String vPath, final long lastModified, final File fromArchive, final int mode) throws IOException {
        if (this.entries.containsKey(vPath)) {
            if (this.duplicate.equals("preserve")) {
                this.logWhenWriting(vPath + " already added, skipping", 2);
                return;
            }
            if (this.duplicate.equals("fail")) {
                throw new BuildException("Duplicate file " + vPath + " was found and the duplicate " + "attribute is 'fail'.");
            }
            this.logWhenWriting("duplicate file " + vPath + " found, adding.", 3);
        }
        else {
            this.logWhenWriting("adding entry " + vPath, 3);
        }
        this.entries.put(vPath, vPath);
        if (!this.skipWriting) {
            final ZipEntry ze = new ZipEntry(vPath);
            ze.setTime(lastModified);
            ze.setMethod(this.doCompress ? 8 : 0);
            if (!zOut.isSeekable() && !this.doCompress) {
                long size = 0L;
                final CRC32 cal = new CRC32();
                if (!in.markSupported()) {
                    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    final byte[] buffer = new byte[8192];
                    int count = 0;
                    do {
                        size += count;
                        cal.update(buffer, 0, count);
                        bos.write(buffer, 0, count);
                        count = in.read(buffer, 0, buffer.length);
                    } while (count != -1);
                    in = new ByteArrayInputStream(bos.toByteArray());
                }
                else {
                    in.mark(Integer.MAX_VALUE);
                    final byte[] buffer2 = new byte[8192];
                    int count2 = 0;
                    do {
                        size += count2;
                        cal.update(buffer2, 0, count2);
                        count2 = in.read(buffer2, 0, buffer2.length);
                    } while (count2 != -1);
                    in.reset();
                }
                ze.setSize(size);
                ze.setCrc(cal.getValue());
            }
            ze.setUnixMode(mode);
            final ZipExtraField[] extra = this.getCurrentExtraFields();
            if (extra != null) {
                ze.setExtraFields(extra);
            }
            zOut.putNextEntry(ze);
            final byte[] buffer3 = new byte[8192];
            int count3 = 0;
            do {
                if (count3 != 0) {
                    zOut.write(buffer3, 0, count3);
                }
                count3 = in.read(buffer3, 0, buffer3.length);
            } while (count3 != -1);
        }
        this.addedFiles.addElement(vPath);
    }
    
    protected final void zipFile(final InputStream in, final ZipOutputStream zOut, final String vPath, final long lastModified, final File fromArchive, final int mode, final ZipExtraField[] extra) throws IOException {
        try {
            this.setCurrentExtraFields(extra);
            this.zipFile(in, zOut, vPath, lastModified, fromArchive, mode);
        }
        finally {
            this.setCurrentExtraFields(null);
        }
    }
    
    protected void zipFile(final File file, final ZipOutputStream zOut, final String vPath, final int mode) throws IOException {
        if (file.equals(this.zipFile)) {
            throw new BuildException("A zip file cannot include itself", this.getLocation());
        }
        final FileInputStream fIn = new FileInputStream(file);
        try {
            this.zipFile(fIn, zOut, vPath, file.lastModified() + (this.roundUp ? 1999 : 0), null, mode);
        }
        finally {
            fIn.close();
        }
    }
    
    protected final void addParentDirs(final File baseDir, final String entry, final ZipOutputStream zOut, final String prefix, final int dirMode) throws IOException {
        if (!this.doFilesonly) {
            final Stack<String> directories = new Stack<String>();
            int slashPos = entry.length();
            while ((slashPos = entry.lastIndexOf(47, slashPos - 1)) != -1) {
                final String dir = entry.substring(0, slashPos + 1);
                if (this.addedDirs.get(prefix + dir) != null) {
                    break;
                }
                directories.push(dir);
            }
            while (!directories.isEmpty()) {
                final String dir = directories.pop();
                File f = null;
                if (baseDir != null) {
                    f = new File(baseDir, dir);
                }
                else {
                    f = new File(dir);
                }
                this.zipDir(f, zOut, prefix + dir, dirMode);
            }
        }
    }
    
    protected void cleanUp() {
        this.addedDirs.clear();
        this.addedFiles.removeAllElements();
        this.entries.clear();
        this.addingNewFiles = false;
        this.doUpdate = this.savedDoUpdate;
        final Enumeration<ZipFileSet> e = this.filesetsFromGroupfilesets.elements();
        while (e.hasMoreElements()) {
            final ZipFileSet zf = e.nextElement();
            this.resources.removeElement(zf);
        }
        this.filesetsFromGroupfilesets.removeAllElements();
        Zip.HAVE_NON_FILE_SET_RESOURCES_TO_ADD.set(Boolean.FALSE);
    }
    
    public void reset() {
        this.resources.removeAllElements();
        this.zipFile = null;
        this.baseDir = null;
        this.groupfilesets.removeAllElements();
        this.duplicate = "add";
        this.archiveType = "zip";
        this.doCompress = true;
        this.emptyBehavior = "skip";
        this.doUpdate = false;
        this.doFilesonly = false;
        this.encoding = null;
    }
    
    protected static final boolean isEmpty(final Resource[][] r) {
        for (int i = 0; i < r.length; ++i) {
            if (r[i].length > 0) {
                return false;
            }
        }
        return true;
    }
    
    protected Resource[] selectFileResources(final Resource[] orig) {
        return this.selectResources(orig, new ResourceSelector() {
            public boolean isSelected(final Resource r) {
                if (!r.isDirectory()) {
                    return true;
                }
                if (Zip.this.doFilesonly) {
                    Zip.this.logWhenWriting("Ignoring directory " + r.getName() + " as only files will" + " be added.", 3);
                }
                return false;
            }
        });
    }
    
    protected Resource[] selectDirectoryResources(final Resource[] orig) {
        return this.selectResources(orig, new ResourceSelector() {
            public boolean isSelected(final Resource r) {
                return r.isDirectory();
            }
        });
    }
    
    protected Resource[] selectResources(final Resource[] orig, final ResourceSelector selector) {
        if (orig.length == 0) {
            return orig;
        }
        final ArrayList<Resource> v = new ArrayList<Resource>(orig.length);
        for (int i = 0; i < orig.length; ++i) {
            if (selector.isSelected(orig[i])) {
                v.add(orig[i]);
            }
        }
        if (v.size() != orig.length) {
            return v.toArray(new Resource[v.size()]);
        }
        return orig;
    }
    
    protected void logWhenWriting(final String msg, final int level) {
        if (!this.skipWriting) {
            this.log(msg, level);
        }
    }
    
    static {
        EMPTY_CRC = new CRC32().getValue();
        MISSING_SELECTOR = new ResourceSelector() {
            public boolean isSelected(final Resource target) {
                return !target.isExists();
            }
        };
        MISSING_DIR_PROVIDER = new ResourceUtils.ResourceSelectorProvider() {
            public ResourceSelector getTargetSelectorForSource(final Resource sr) {
                return Zip.MISSING_SELECTOR;
            }
        };
        FILE_UTILS = FileUtils.getFileUtils();
        HAVE_NON_FILE_SET_RESOURCES_TO_ADD = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return Boolean.FALSE;
            }
        };
        CURRENT_ZIP_EXTRA = new ThreadLocal<ZipExtraField[]>();
    }
    
    public static class WhenEmpty extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "fail", "skip", "create" };
        }
    }
    
    public static class Duplicate extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "add", "preserve", "fail" };
        }
    }
    
    public static class ArchiveState
    {
        private boolean outOfDate;
        private Resource[][] resourcesToAdd;
        
        ArchiveState(final boolean state, final Resource[][] r) {
            this.outOfDate = state;
            this.resourcesToAdd = r;
        }
        
        public boolean isOutOfDate() {
            return this.outOfDate;
        }
        
        public Resource[][] getResourcesToAdd() {
            return this.resourcesToAdd;
        }
        
        public boolean isWithoutAnyResources() {
            if (this.resourcesToAdd == null) {
                return true;
            }
            for (int counter = 0; counter < this.resourcesToAdd.length; ++counter) {
                if (this.resourcesToAdd[counter] != null && this.resourcesToAdd[counter].length > 0) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public static final class UnicodeExtraField extends EnumeratedAttribute
    {
        private static final Map<String, ZipOutputStream.UnicodeExtraFieldPolicy> POLICIES;
        private static final String NEVER_KEY = "never";
        private static final String ALWAYS_KEY = "always";
        private static final String N_E_KEY = "not-encodeable";
        public static final UnicodeExtraField NEVER;
        
        @Override
        public String[] getValues() {
            return new String[] { "never", "always", "not-encodeable" };
        }
        
        private UnicodeExtraField(final String name) {
            this.setValue(name);
        }
        
        public UnicodeExtraField() {
        }
        
        public ZipOutputStream.UnicodeExtraFieldPolicy getPolicy() {
            return UnicodeExtraField.POLICIES.get(this.getValue());
        }
        
        static {
            (POLICIES = new HashMap<String, ZipOutputStream.UnicodeExtraFieldPolicy>()).put("never", ZipOutputStream.UnicodeExtraFieldPolicy.NEVER);
            UnicodeExtraField.POLICIES.put("always", ZipOutputStream.UnicodeExtraFieldPolicy.ALWAYS);
            UnicodeExtraField.POLICIES.put("not-encodeable", ZipOutputStream.UnicodeExtraFieldPolicy.NOT_ENCODEABLE);
            NEVER = new UnicodeExtraField("never");
        }
    }
    
    public static final class Zip64ModeAttribute extends EnumeratedAttribute
    {
        private static final Map<String, Zip64Mode> MODES;
        private static final String NEVER_KEY = "never";
        private static final String ALWAYS_KEY = "always";
        private static final String A_N_KEY = "as-needed";
        public static final Zip64ModeAttribute NEVER;
        public static final Zip64ModeAttribute AS_NEEDED;
        
        @Override
        public String[] getValues() {
            return new String[] { "never", "always", "as-needed" };
        }
        
        private Zip64ModeAttribute(final String name) {
            this.setValue(name);
        }
        
        public Zip64ModeAttribute() {
        }
        
        public Zip64Mode getMode() {
            return Zip64ModeAttribute.MODES.get(this.getValue());
        }
        
        static {
            (MODES = new HashMap<String, Zip64Mode>()).put("never", Zip64Mode.Never);
            Zip64ModeAttribute.MODES.put("always", Zip64Mode.Always);
            Zip64ModeAttribute.MODES.put("as-needed", Zip64Mode.AsNeeded);
            NEVER = new Zip64ModeAttribute("never");
            AS_NEEDED = new Zip64ModeAttribute("as-needed");
        }
    }
}
