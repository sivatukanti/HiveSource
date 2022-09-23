// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.bzip2.CBZip2OutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.TarFileSet;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.ArchiveFileSet;
import java.util.Iterator;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.types.resources.FileProvider;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.MergingMapper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.SourceFileScanner;
import java.io.InputStream;
import org.apache.tools.ant.types.resources.TarResource;
import org.apache.tools.ant.types.resources.ArchiveResource;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import java.util.Enumeration;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import org.apache.tools.tar.TarOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;
import java.util.Vector;
import java.io.File;

public class Tar extends MatchingTask
{
    private static final int BUFFER_SIZE = 8192;
    @Deprecated
    public static final String WARN = "warn";
    @Deprecated
    public static final String FAIL = "fail";
    @Deprecated
    public static final String TRUNCATE = "truncate";
    @Deprecated
    public static final String GNU = "gnu";
    @Deprecated
    public static final String OMIT = "omit";
    File tarFile;
    File baseDir;
    private TarLongFileMode longFileMode;
    Vector filesets;
    private Vector resourceCollections;
    Vector fileSetFiles;
    private boolean longWarningGiven;
    private TarCompressionMethod compression;
    
    public Tar() {
        this.longFileMode = new TarLongFileMode();
        this.filesets = new Vector();
        this.resourceCollections = new Vector();
        this.fileSetFiles = new Vector();
        this.longWarningGiven = false;
        this.compression = new TarCompressionMethod();
    }
    
    public TarFileSet createTarFileSet() {
        final TarFileSet fs = new TarFileSet();
        fs.setProject(this.getProject());
        this.filesets.addElement(fs);
        return fs;
    }
    
    public void add(final ResourceCollection res) {
        this.resourceCollections.add(res);
    }
    
    @Deprecated
    public void setTarfile(final File tarFile) {
        this.tarFile = tarFile;
    }
    
    public void setDestFile(final File destFile) {
        this.tarFile = destFile;
    }
    
    public void setBasedir(final File baseDir) {
        this.baseDir = baseDir;
    }
    
    @Deprecated
    public void setLongfile(final String mode) {
        this.log("DEPRECATED - The setLongfile(String) method has been deprecated. Use setLongfile(Tar.TarLongFileMode) instead.");
        (this.longFileMode = new TarLongFileMode()).setValue(mode);
    }
    
    public void setLongfile(final TarLongFileMode mode) {
        this.longFileMode = mode;
    }
    
    public void setCompression(final TarCompressionMethod mode) {
        this.compression = mode;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.tarFile == null) {
            throw new BuildException("tarfile attribute must be set!", this.getLocation());
        }
        if (this.tarFile.exists() && this.tarFile.isDirectory()) {
            throw new BuildException("tarfile is a directory!", this.getLocation());
        }
        if (this.tarFile.exists() && !this.tarFile.canWrite()) {
            throw new BuildException("Can not write to the specified tarfile!", this.getLocation());
        }
        final Vector savedFileSets = (Vector)this.filesets.clone();
        try {
            if (this.baseDir != null) {
                if (!this.baseDir.exists()) {
                    throw new BuildException("basedir does not exist!", this.getLocation());
                }
                final TarFileSet mainFileSet = new TarFileSet(this.fileset);
                mainFileSet.setDir(this.baseDir);
                this.filesets.addElement(mainFileSet);
            }
            if (this.filesets.size() == 0 && this.resourceCollections.size() == 0) {
                throw new BuildException("You must supply either a basedir attribute or some nested resource collections.", this.getLocation());
            }
            boolean upToDate = true;
            Enumeration e = this.filesets.elements();
            while (e.hasMoreElements()) {
                upToDate &= this.check(e.nextElement());
            }
            e = this.resourceCollections.elements();
            while (e.hasMoreElements()) {
                upToDate &= this.check(e.nextElement());
            }
            if (upToDate) {
                this.log("Nothing to do: " + this.tarFile.getAbsolutePath() + " is up to date.", 2);
                return;
            }
            final File parent = this.tarFile.getParentFile();
            if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
                throw new BuildException("Failed to create missing parent directory for " + this.tarFile);
            }
            this.log("Building tar: " + this.tarFile.getAbsolutePath(), 2);
            TarOutputStream tOut = null;
            try {
                tOut = new TarOutputStream(this.compression.compress(new BufferedOutputStream(new FileOutputStream(this.tarFile))));
                tOut.setDebug(true);
                if (this.longFileMode.isTruncateMode()) {
                    tOut.setLongFileMode(1);
                }
                else if (this.longFileMode.isFailMode() || this.longFileMode.isOmitMode()) {
                    tOut.setLongFileMode(0);
                }
                else if (this.longFileMode.isPosixMode()) {
                    tOut.setLongFileMode(3);
                }
                else {
                    tOut.setLongFileMode(2);
                }
                this.longWarningGiven = false;
                Enumeration e2 = this.filesets.elements();
                while (e2.hasMoreElements()) {
                    this.tar(e2.nextElement(), tOut);
                }
                e2 = this.resourceCollections.elements();
                while (e2.hasMoreElements()) {
                    this.tar(e2.nextElement(), tOut);
                }
            }
            catch (IOException ioe) {
                final String msg = "Problem creating TAR: " + ioe.getMessage();
                throw new BuildException(msg, ioe, this.getLocation());
            }
            finally {
                FileUtils.close(tOut);
            }
        }
        finally {
            this.filesets = savedFileSets;
        }
    }
    
    protected void tarFile(final File file, final TarOutputStream tOut, final String vPath, final TarFileSet tarFileSet) throws IOException {
        if (file.equals(this.tarFile)) {
            return;
        }
        this.tarResource(new FileResource(file), tOut, vPath, tarFileSet);
    }
    
    protected void tarResource(final Resource r, final TarOutputStream tOut, String vPath, final TarFileSet tarFileSet) throws IOException {
        if (!r.isExists()) {
            return;
        }
        boolean preserveLeadingSlashes = false;
        if (tarFileSet != null) {
            final String fullpath = tarFileSet.getFullpath(this.getProject());
            if (fullpath.length() > 0) {
                vPath = fullpath;
            }
            else {
                if (vPath.length() <= 0) {
                    return;
                }
                String prefix = tarFileSet.getPrefix(this.getProject());
                if (prefix.length() > 0 && !prefix.endsWith("/")) {
                    prefix += "/";
                }
                vPath = prefix + vPath;
            }
            preserveLeadingSlashes = tarFileSet.getPreserveLeadingSlashes();
            if (vPath.startsWith("/") && !preserveLeadingSlashes) {
                final int l = vPath.length();
                if (l <= 1) {
                    return;
                }
                vPath = vPath.substring(1, l);
            }
        }
        if (r.isDirectory() && !vPath.endsWith("/")) {
            vPath += "/";
        }
        if (vPath.length() >= 100) {
            if (this.longFileMode.isOmitMode()) {
                this.log("Omitting: " + vPath, 2);
                return;
            }
            if (this.longFileMode.isWarnMode()) {
                this.log("Entry: " + vPath + " longer than " + 100 + " characters.", 1);
                if (!this.longWarningGiven) {
                    this.log("Resulting tar file can only be processed successfully by GNU compatible tar commands", 1);
                    this.longWarningGiven = true;
                }
            }
            else if (this.longFileMode.isFailMode()) {
                throw new BuildException("Entry: " + vPath + " longer than " + 100 + "characters.", this.getLocation());
            }
        }
        final TarEntry te = new TarEntry(vPath, preserveLeadingSlashes);
        te.setModTime(r.getLastModified());
        if (r instanceof ArchiveResource) {
            final ArchiveResource ar = (ArchiveResource)r;
            te.setMode(ar.getMode());
            if (r instanceof TarResource) {
                final TarResource tr = (TarResource)r;
                te.setUserName(tr.getUserName());
                te.setUserId(tr.getUid());
                te.setGroupName(tr.getGroup());
                te.setGroupId(tr.getGid());
            }
        }
        if (!r.isDirectory()) {
            if (r.size() > 8589934591L) {
                throw new BuildException("Resource: " + r + " larger than " + 8589934591L + " bytes.");
            }
            te.setSize(r.getSize());
            if (tarFileSet != null && tarFileSet.hasFileModeBeenSet()) {
                te.setMode(tarFileSet.getMode());
            }
        }
        else if (tarFileSet != null && tarFileSet.hasDirModeBeenSet()) {
            te.setMode(tarFileSet.getDirMode(this.getProject()));
        }
        if (tarFileSet != null) {
            if (tarFileSet.hasUserNameBeenSet()) {
                te.setUserName(tarFileSet.getUserName());
            }
            if (tarFileSet.hasGroupBeenSet()) {
                te.setGroupName(tarFileSet.getGroup());
            }
            if (tarFileSet.hasUserIdBeenSet()) {
                te.setUserId(tarFileSet.getUid());
            }
            if (tarFileSet.hasGroupIdBeenSet()) {
                te.setGroupId(tarFileSet.getGid());
            }
        }
        InputStream in = null;
        try {
            tOut.putNextEntry(te);
            if (!r.isDirectory()) {
                in = r.getInputStream();
                final byte[] buffer = new byte[8192];
                int count = 0;
                do {
                    tOut.write(buffer, 0, count);
                    count = in.read(buffer, 0, buffer.length);
                } while (count != -1);
            }
            tOut.closeEntry();
        }
        finally {
            FileUtils.close(in);
        }
    }
    
    @Deprecated
    protected boolean archiveIsUpToDate(final String[] files) {
        return this.archiveIsUpToDate(files, this.baseDir);
    }
    
    protected boolean archiveIsUpToDate(final String[] files, final File dir) {
        final SourceFileScanner sfs = new SourceFileScanner(this);
        final MergingMapper mm = new MergingMapper();
        mm.setTo(this.tarFile.getAbsolutePath());
        return sfs.restrict(files, dir, null, mm).length == 0;
    }
    
    protected boolean archiveIsUpToDate(final Resource r) {
        return SelectorUtils.isOutOfDate(new FileResource(this.tarFile), r, FileUtils.getFileUtils().getFileTimestampGranularity());
    }
    
    protected boolean supportsNonFileResources() {
        return this.getClass().equals(Tar.class);
    }
    
    protected boolean check(final ResourceCollection rc) {
        boolean upToDate = true;
        if (isFileFileSet(rc)) {
            final FileSet fs = (FileSet)rc;
            upToDate = this.check(fs.getDir(this.getProject()), getFileNames(fs));
        }
        else {
            if (!rc.isFilesystemOnly() && !this.supportsNonFileResources()) {
                throw new BuildException("only filesystem resources are supported");
            }
            if (rc.isFilesystemOnly()) {
                final HashSet basedirs = new HashSet();
                final HashMap basedirToFilesMap = new HashMap();
                for (final Resource res : rc) {
                    final FileResource r = ResourceUtils.asFileResource(res.as(FileProvider.class));
                    File base = r.getBaseDir();
                    if (base == null) {
                        base = Copy.NULL_FILE_PLACEHOLDER;
                    }
                    basedirs.add(base);
                    Vector files = basedirToFilesMap.get(base);
                    if (files == null) {
                        files = new Vector();
                        basedirToFilesMap.put(base, files);
                    }
                    if (base == Copy.NULL_FILE_PLACEHOLDER) {
                        files.add(r.getFile().getAbsolutePath());
                    }
                    else {
                        files.add(r.getName());
                    }
                }
                for (final File base2 : basedirs) {
                    final Vector f = basedirToFilesMap.get(base2);
                    final String[] files2 = f.toArray(new String[f.size()]);
                    upToDate &= this.check((base2 == Copy.NULL_FILE_PLACEHOLDER) ? null : base2, files2);
                }
            }
            else {
                Resource r2;
                for (Iterator<Resource> iter2 = rc.iterator(); upToDate && iter2.hasNext(); upToDate = this.archiveIsUpToDate(r2)) {
                    r2 = iter2.next();
                }
            }
        }
        return upToDate;
    }
    
    protected boolean check(final File basedir, final String[] files) {
        boolean upToDate = true;
        if (!this.archiveIsUpToDate(files, basedir)) {
            upToDate = false;
        }
        for (int i = 0; i < files.length; ++i) {
            if (this.tarFile.equals(new File(basedir, files[i]))) {
                throw new BuildException("A tar file cannot include itself", this.getLocation());
            }
        }
        return upToDate;
    }
    
    protected void tar(final ResourceCollection rc, final TarOutputStream tOut) throws IOException {
        ArchiveFileSet afs = null;
        if (rc instanceof ArchiveFileSet) {
            afs = (ArchiveFileSet)rc;
        }
        if (afs != null && afs.size() > 1 && afs.getFullpath(this.getProject()).length() > 0) {
            throw new BuildException("fullpath attribute may only be specified for filesets that specify a single file.");
        }
        final TarFileSet tfs = this.asTarFileSet(afs);
        if (isFileFileSet(rc)) {
            final FileSet fs = (FileSet)rc;
            final String[] files = getFileNames(fs);
            for (int i = 0; i < files.length; ++i) {
                final File f = new File(fs.getDir(this.getProject()), files[i]);
                final String name = files[i].replace(File.separatorChar, '/');
                this.tarFile(f, tOut, name, tfs);
            }
        }
        else if (rc.isFilesystemOnly()) {
            for (final Resource r : rc) {
                final File f2 = r.as(FileProvider.class).getFile();
                this.tarFile(f2, tOut, f2.getName(), tfs);
            }
        }
        else {
            for (final Resource r : rc) {
                this.tarResource(r, tOut, r.getName(), tfs);
            }
        }
    }
    
    protected static boolean isFileFileSet(final ResourceCollection rc) {
        return rc instanceof FileSet && rc.isFilesystemOnly();
    }
    
    protected static String[] getFileNames(final FileSet fs) {
        final DirectoryScanner ds = fs.getDirectoryScanner(fs.getProject());
        final String[] directories = ds.getIncludedDirectories();
        final String[] filesPerSe = ds.getIncludedFiles();
        final String[] files = new String[directories.length + filesPerSe.length];
        System.arraycopy(directories, 0, files, 0, directories.length);
        System.arraycopy(filesPerSe, 0, files, directories.length, filesPerSe.length);
        return files;
    }
    
    protected TarFileSet asTarFileSet(final ArchiveFileSet archiveFileSet) {
        TarFileSet tfs = null;
        if (archiveFileSet != null && archiveFileSet instanceof TarFileSet) {
            tfs = (TarFileSet)archiveFileSet;
        }
        else {
            tfs = new TarFileSet();
            tfs.setProject(this.getProject());
            if (archiveFileSet != null) {
                tfs.setPrefix(archiveFileSet.getPrefix(this.getProject()));
                tfs.setFullpath(archiveFileSet.getFullpath(this.getProject()));
                if (archiveFileSet.hasFileModeBeenSet()) {
                    tfs.integerSetFileMode(archiveFileSet.getFileMode(this.getProject()));
                }
                if (archiveFileSet.hasDirModeBeenSet()) {
                    tfs.integerSetDirMode(archiveFileSet.getDirMode(this.getProject()));
                }
                if (archiveFileSet instanceof org.apache.tools.ant.types.TarFileSet) {
                    final org.apache.tools.ant.types.TarFileSet t = (org.apache.tools.ant.types.TarFileSet)archiveFileSet;
                    if (t.hasUserNameBeenSet()) {
                        tfs.setUserName(t.getUserName());
                    }
                    if (t.hasGroupBeenSet()) {
                        tfs.setGroup(t.getGroup());
                    }
                    if (t.hasUserIdBeenSet()) {
                        tfs.setUid(t.getUid());
                    }
                    if (t.hasGroupIdBeenSet()) {
                        tfs.setGid(t.getGid());
                    }
                }
            }
        }
        return tfs;
    }
    
    public static class TarFileSet extends org.apache.tools.ant.types.TarFileSet
    {
        private String[] files;
        private boolean preserveLeadingSlashes;
        
        public TarFileSet(final FileSet fileset) {
            super(fileset);
            this.files = null;
            this.preserveLeadingSlashes = false;
        }
        
        public TarFileSet() {
            this.files = null;
            this.preserveLeadingSlashes = false;
        }
        
        public String[] getFiles(final Project p) {
            if (this.files == null) {
                this.files = Tar.getFileNames(this);
            }
            return this.files;
        }
        
        public void setMode(final String octalString) {
            this.setFileMode(octalString);
        }
        
        public int getMode() {
            return this.getFileMode(this.getProject());
        }
        
        public void setPreserveLeadingSlashes(final boolean b) {
            this.preserveLeadingSlashes = b;
        }
        
        public boolean getPreserveLeadingSlashes() {
            return this.preserveLeadingSlashes;
        }
    }
    
    public static class TarLongFileMode extends EnumeratedAttribute
    {
        public static final String WARN = "warn";
        public static final String FAIL = "fail";
        public static final String TRUNCATE = "truncate";
        public static final String GNU = "gnu";
        public static final String POSIX = "posix";
        public static final String OMIT = "omit";
        private final String[] validModes;
        
        public TarLongFileMode() {
            this.validModes = new String[] { "warn", "fail", "truncate", "gnu", "posix", "omit" };
            this.setValue("warn");
        }
        
        @Override
        public String[] getValues() {
            return this.validModes;
        }
        
        public boolean isTruncateMode() {
            return "truncate".equalsIgnoreCase(this.getValue());
        }
        
        public boolean isWarnMode() {
            return "warn".equalsIgnoreCase(this.getValue());
        }
        
        public boolean isGnuMode() {
            return "gnu".equalsIgnoreCase(this.getValue());
        }
        
        public boolean isFailMode() {
            return "fail".equalsIgnoreCase(this.getValue());
        }
        
        public boolean isOmitMode() {
            return "omit".equalsIgnoreCase(this.getValue());
        }
        
        public boolean isPosixMode() {
            return "posix".equalsIgnoreCase(this.getValue());
        }
    }
    
    public static final class TarCompressionMethod extends EnumeratedAttribute
    {
        private static final String NONE = "none";
        private static final String GZIP = "gzip";
        private static final String BZIP2 = "bzip2";
        
        public TarCompressionMethod() {
            this.setValue("none");
        }
        
        @Override
        public String[] getValues() {
            return new String[] { "none", "gzip", "bzip2" };
        }
        
        private OutputStream compress(final OutputStream ostream) throws IOException {
            final String v = this.getValue();
            if ("gzip".equals(v)) {
                return new GZIPOutputStream(ostream);
            }
            if ("bzip2".equals(v)) {
                ostream.write(66);
                ostream.write(90);
                return new CBZip2OutputStream(ostream);
            }
            return ostream;
        }
    }
}
