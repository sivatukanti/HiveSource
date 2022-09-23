// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.DataOutput;
import org.apache.hadoop.util.StringUtils;
import java.util.StringTokenizer;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.nio.ByteBuffer;
import java.io.EOFException;
import java.io.FileInputStream;
import java.util.Optional;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.FileTime;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributeView;
import org.apache.hadoop.io.nativeio.NativeIO;
import java.util.Arrays;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.io.IOUtils;
import java.util.EnumSet;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import java.io.InputStream;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.io.File;
import com.google.common.annotations.VisibleForTesting;
import java.net.URI;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class RawLocalFileSystem extends FileSystem
{
    static final URI NAME;
    private Path workingDir;
    private static boolean useDeprecatedFileStatus;
    
    @VisibleForTesting
    public static void useStatIfAvailable() {
        RawLocalFileSystem.useDeprecatedFileStatus = !Stat.isAvailable();
    }
    
    public RawLocalFileSystem() {
        this.workingDir = this.getInitialWorkingDirectory();
    }
    
    private Path makeAbsolute(final Path f) {
        if (f.isAbsolute()) {
            return f;
        }
        return new Path(this.workingDir, f);
    }
    
    public File pathToFile(Path path) {
        this.checkPath(path);
        if (!path.isAbsolute()) {
            path = new Path(this.getWorkingDirectory(), path);
        }
        return new File(path.toUri().getPath());
    }
    
    @Override
    public URI getUri() {
        return RawLocalFileSystem.NAME;
    }
    
    @Override
    public void initialize(final URI uri, final Configuration conf) throws IOException {
        super.initialize(uri, conf);
        this.setConf(conf);
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException {
        this.getFileStatus(f);
        return new FSDataInputStream(new BufferedFSInputStream(new LocalFSFileInputStream(f), bufferSize));
    }
    
    @Override
    public FSDataInputStream open(PathHandle fd, final int bufferSize) throws IOException {
        if (!(fd instanceof LocalFileSystemPathHandle)) {
            fd = new LocalFileSystemPathHandle(fd.bytes());
        }
        final LocalFileSystemPathHandle id = (LocalFileSystemPathHandle)fd;
        id.verify(this.getFileStatus(new Path(id.getPath())));
        return new FSDataInputStream(new BufferedFSInputStream(new LocalFSFileInputStream(new Path(id.getPath())), bufferSize));
    }
    
    @Override
    public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
        final FileStatus status = this.getFileStatus(f);
        if (status.isDirectory()) {
            throw new IOException("Cannot append to a diretory (=" + f + " )");
        }
        return new FSDataOutputStream(new BufferedOutputStream(this.createOutputStreamWithMode(f, true, null), bufferSize), this.statistics, status.getLen());
    }
    
    @Override
    public FSDataOutputStream create(final Path f, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return this.create(f, overwrite, true, bufferSize, replication, blockSize, progress, null);
    }
    
    private FSDataOutputStream create(final Path f, final boolean overwrite, final boolean createParent, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final FsPermission permission) throws IOException {
        if (this.exists(f) && !overwrite) {
            throw new FileAlreadyExistsException("File already exists: " + f);
        }
        final Path parent = f.getParent();
        if (parent != null && !this.mkdirs(parent)) {
            throw new IOException("Mkdirs failed to create " + parent.toString());
        }
        return new FSDataOutputStream(new BufferedOutputStream(this.createOutputStreamWithMode(f, false, permission), bufferSize), this.statistics);
    }
    
    protected OutputStream createOutputStream(final Path f, final boolean append) throws IOException {
        return this.createOutputStreamWithMode(f, append, null);
    }
    
    protected OutputStream createOutputStreamWithMode(final Path f, final boolean append, final FsPermission permission) throws IOException {
        return new LocalFSFileOutputStream(f, append, permission);
    }
    
    @Override
    public FSDataOutputStream createNonRecursive(final Path f, final FsPermission permission, final EnumSet<CreateFlag> flags, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        if (this.exists(f) && !flags.contains(CreateFlag.OVERWRITE)) {
            throw new FileAlreadyExistsException("File already exists: " + f);
        }
        return new FSDataOutputStream(new BufferedOutputStream(this.createOutputStreamWithMode(f, false, permission), bufferSize), this.statistics);
    }
    
    @Override
    public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        final FSDataOutputStream out = this.create(f, overwrite, true, bufferSize, replication, blockSize, progress, permission);
        return out;
    }
    
    @Override
    public FSDataOutputStream createNonRecursive(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        final FSDataOutputStream out = this.create(f, overwrite, false, bufferSize, replication, blockSize, progress, permission);
        return out;
    }
    
    @Override
    public void concat(final Path trg, final Path[] psrcs) throws IOException {
        final int bufferSize = 4096;
        try (final FSDataOutputStream out = this.create(trg)) {
            for (final Path src : psrcs) {
                try (final FSDataInputStream in = this.open(src)) {
                    IOUtils.copyBytes(in, out, 4096, false);
                }
            }
        }
    }
    
    @Override
    public boolean rename(final Path src, final Path dst) throws IOException {
        final File srcFile = this.pathToFile(src);
        final File dstFile = this.pathToFile(dst);
        if (srcFile.renameTo(dstFile)) {
            return true;
        }
        if (Shell.WINDOWS && this.handleEmptyDstDirectoryOnWindows(src, srcFile, dst, dstFile)) {
            return true;
        }
        if (RawLocalFileSystem.LOG.isDebugEnabled()) {
            RawLocalFileSystem.LOG.debug("Falling through to a copy of " + src + " to " + dst);
        }
        return FileUtil.copy(this, src, this, dst, true, this.getConf());
    }
    
    @VisibleForTesting
    public final boolean handleEmptyDstDirectoryOnWindows(final Path src, final File srcFile, final Path dst, final File dstFile) throws IOException {
        try {
            final FileStatus sdst = this.getFileStatus(dst);
            final String[] dstFileList = dstFile.list();
            if (dstFileList != null && sdst.isDirectory() && dstFileList.length == 0) {
                if (RawLocalFileSystem.LOG.isDebugEnabled()) {
                    RawLocalFileSystem.LOG.debug("Deleting empty destination and renaming " + src + " to " + dst);
                }
                if (this.delete(dst, false) && srcFile.renameTo(dstFile)) {
                    return true;
                }
            }
        }
        catch (FileNotFoundException ex) {}
        return false;
    }
    
    @Override
    public boolean truncate(final Path f, final long newLength) throws IOException {
        final FileStatus status = this.getFileStatus(f);
        if (status == null) {
            throw new FileNotFoundException("File " + f + " not found");
        }
        if (status.isDirectory()) {
            throw new IOException("Cannot truncate a directory (=" + f + ")");
        }
        final long oldLength = status.getLen();
        if (newLength > oldLength) {
            throw new IllegalArgumentException("Cannot truncate to a larger file size. Current size: " + oldLength + ", truncate size: " + newLength + ".");
        }
        try (final FileOutputStream out = new FileOutputStream(this.pathToFile(f), true)) {
            try {
                out.getChannel().truncate(newLength);
            }
            catch (IOException e) {
                throw new FSError(e);
            }
        }
        return true;
    }
    
    @Override
    public boolean delete(final Path p, final boolean recursive) throws IOException {
        final File f = this.pathToFile(p);
        if (!f.exists()) {
            return false;
        }
        if (f.isFile()) {
            return f.delete();
        }
        if (!recursive && f.isDirectory() && FileUtil.listFiles(f).length != 0) {
            throw new IOException("Directory " + f.toString() + " is not empty");
        }
        return FileUtil.fullyDelete(f);
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws IOException {
        final File localf = this.pathToFile(f);
        if (!localf.exists()) {
            throw new FileNotFoundException("File " + f + " does not exist");
        }
        if (localf.isDirectory()) {
            final String[] names = FileUtil.list(localf);
            final FileStatus[] results = new FileStatus[names.length];
            int j = 0;
            for (int i = 0; i < names.length; ++i) {
                try {
                    results[j] = this.getFileStatus(new Path(f, new Path(null, null, names[i])));
                    ++j;
                }
                catch (FileNotFoundException ex) {}
            }
            if (j == names.length) {
                return results;
            }
            return Arrays.copyOf(results, j);
        }
        else {
            if (!RawLocalFileSystem.useDeprecatedFileStatus) {
                return new FileStatus[] { this.getFileStatus(f) };
            }
            return new FileStatus[] { new DeprecatedRawLocalFileStatus(localf, this.getDefaultBlockSize(f), this) };
        }
    }
    
    protected boolean mkOneDir(final File p2f) throws IOException {
        return this.mkOneDirWithMode(new Path(p2f.getAbsolutePath()), p2f, null);
    }
    
    protected boolean mkOneDirWithMode(final Path p, final File p2f, FsPermission permission) throws IOException {
        if (permission == null) {
            permission = FsPermission.getDirDefault();
        }
        permission = permission.applyUMask(FsPermission.getUMask(this.getConf()));
        if (Shell.WINDOWS && NativeIO.isAvailable()) {
            try {
                NativeIO.Windows.createDirectoryWithMode(p2f, permission.toShort());
                return true;
            }
            catch (IOException e) {
                if (RawLocalFileSystem.LOG.isDebugEnabled()) {
                    RawLocalFileSystem.LOG.debug(String.format("NativeIO.createDirectoryWithMode error, path = %s, mode = %o", p2f, permission.toShort()), e);
                }
                return false;
            }
        }
        final boolean b = p2f.mkdir();
        if (b) {
            this.setPermission(p, permission);
        }
        return b;
    }
    
    @Override
    public boolean mkdirs(final Path f) throws IOException {
        return this.mkdirsWithOptionalPermission(f, null);
    }
    
    @Override
    public boolean mkdirs(final Path f, final FsPermission permission) throws IOException {
        return this.mkdirsWithOptionalPermission(f, permission);
    }
    
    private boolean mkdirsWithOptionalPermission(final Path f, final FsPermission permission) throws IOException {
        if (f == null) {
            throw new IllegalArgumentException("mkdirs path arg is null");
        }
        final Path parent = f.getParent();
        final File p2f = this.pathToFile(f);
        File parent2f = null;
        if (parent != null) {
            parent2f = this.pathToFile(parent);
            if (parent2f != null && parent2f.exists() && !parent2f.isDirectory()) {
                throw new ParentNotDirectoryException("Parent path is not a directory: " + parent);
            }
        }
        if (p2f.exists() && !p2f.isDirectory()) {
            throw new FileAlreadyExistsException("Destination exists and is not a directory: " + p2f.getCanonicalPath());
        }
        return (parent == null || parent2f.exists() || this.mkdirs(parent)) && (this.mkOneDirWithMode(f, p2f, permission) || p2f.isDirectory());
    }
    
    @Override
    public Path getHomeDirectory() {
        return this.makeQualified(new Path(System.getProperty("user.home")));
    }
    
    @Override
    public void setWorkingDirectory(final Path newDir) {
        this.checkPath(this.workingDir = this.makeAbsolute(newDir));
    }
    
    @Override
    public Path getWorkingDirectory() {
        return this.workingDir;
    }
    
    @Override
    protected Path getInitialWorkingDirectory() {
        return this.makeQualified(new Path(System.getProperty("user.dir")));
    }
    
    @Override
    public FsStatus getStatus(final Path p) throws IOException {
        final File partition = this.pathToFile((p == null) ? new Path("/") : p);
        return new FsStatus(partition.getTotalSpace(), partition.getTotalSpace() - partition.getFreeSpace(), partition.getFreeSpace());
    }
    
    @Override
    public void moveFromLocalFile(final Path src, final Path dst) throws IOException {
        this.rename(src, dst);
    }
    
    @Override
    public Path startLocalOutput(final Path fsOutputFile, final Path tmpLocalFile) throws IOException {
        return fsOutputFile;
    }
    
    @Override
    public void completeLocalOutput(final Path fsWorkingFile, final Path tmpLocalFile) throws IOException {
    }
    
    @Override
    public void close() throws IOException {
        super.close();
    }
    
    @Override
    public String toString() {
        return "LocalFS";
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws IOException {
        return this.getFileLinkStatusInternal(f, true);
    }
    
    @Deprecated
    private FileStatus deprecatedGetFileStatus(final Path f) throws IOException {
        final File path = this.pathToFile(f);
        if (path.exists()) {
            return new DeprecatedRawLocalFileStatus(this.pathToFile(f), this.getDefaultBlockSize(f), this);
        }
        throw new FileNotFoundException("File " + f + " does not exist");
    }
    
    @Override
    public void setOwner(final Path p, final String username, final String groupname) throws IOException {
        FileUtil.setOwner(this.pathToFile(p), username, groupname);
    }
    
    @Override
    public void setPermission(final Path p, final FsPermission permission) throws IOException {
        if (NativeIO.isAvailable()) {
            NativeIO.POSIX.chmod(this.pathToFile(p).getCanonicalPath(), permission.toShort());
        }
        else {
            final String perm = String.format("%04o", permission.toShort());
            Shell.execCommand(Shell.getSetPermissionCommand(perm, false, FileUtil.makeShellPath(this.pathToFile(p), true)));
        }
    }
    
    @Override
    public void setTimes(final Path p, final long mtime, final long atime) throws IOException {
        try {
            final BasicFileAttributeView view = Files.getFileAttributeView(this.pathToFile(p).toPath(), BasicFileAttributeView.class, new LinkOption[0]);
            final FileTime fmtime = (mtime >= 0L) ? FileTime.fromMillis(mtime) : null;
            final FileTime fatime = (atime >= 0L) ? FileTime.fromMillis(atime) : null;
            view.setTimes(fmtime, fatime, null);
        }
        catch (NoSuchFileException e) {
            throw new FileNotFoundException("File " + p + " does not exist");
        }
    }
    
    @Override
    protected PathHandle createPathHandle(final FileStatus stat, final Options.HandleOpt... opts) {
        if (stat.isDirectory() || stat.isSymlink()) {
            throw new IllegalArgumentException("PathHandle only available for files");
        }
        final String authority = stat.getPath().toUri().getAuthority();
        if (authority != null && !authority.equals("file://")) {
            throw new IllegalArgumentException("Wrong FileSystem: " + stat.getPath());
        }
        final Options.HandleOpt.Data data = Options.HandleOpt.getOpt(Options.HandleOpt.Data.class, opts).orElse(Options.HandleOpt.changed(false));
        final Options.HandleOpt.Location loc = Options.HandleOpt.getOpt(Options.HandleOpt.Location.class, opts).orElse(Options.HandleOpt.moved(false));
        if (loc.allowChange()) {
            throw new UnsupportedOperationException("Tracking file movement in basic FileSystem is not supported");
        }
        final Path p = stat.getPath();
        final Optional<Long> mtime = data.allowChange() ? Optional.empty() : Optional.of(stat.getModificationTime());
        return new LocalFileSystemPathHandle(p.toString(), mtime);
    }
    
    @Override
    public boolean supportsSymlinks() {
        return true;
    }
    
    @Override
    public void createSymlink(final Path target, final Path link, final boolean createParent) throws IOException {
        if (!FileSystem.areSymlinksEnabled()) {
            throw new UnsupportedOperationException("Symlinks not supported");
        }
        final String targetScheme = target.toUri().getScheme();
        if (targetScheme != null && !"file".equals(targetScheme)) {
            throw new IOException("Unable to create symlink to non-local file system: " + target.toString());
        }
        if (createParent) {
            this.mkdirs(link.getParent());
        }
        final int result = FileUtil.symLink(target.toString(), this.makeAbsolute(link).toString());
        if (result != 0) {
            throw new IOException("Error " + result + " creating symlink " + link + " to " + target);
        }
    }
    
    @Override
    public FileStatus getFileLinkStatus(final Path f) throws IOException {
        final FileStatus fi = this.getFileLinkStatusInternal(f, false);
        if (fi.isSymlink()) {
            final Path targetQual = FSLinkResolver.qualifySymlinkTarget(this.getUri(), fi.getPath(), fi.getSymlink());
            fi.setSymlink(targetQual);
        }
        return fi;
    }
    
    private FileStatus getFileLinkStatusInternal(final Path f, final boolean dereference) throws IOException {
        if (!RawLocalFileSystem.useDeprecatedFileStatus) {
            return this.getNativeFileLinkStatus(f, dereference);
        }
        if (dereference) {
            return this.deprecatedGetFileStatus(f);
        }
        return this.deprecatedGetFileLinkStatusInternal(f);
    }
    
    @Deprecated
    private FileStatus deprecatedGetFileLinkStatusInternal(final Path f) throws IOException {
        final String target = FileUtil.readLink(new File(f.toString()));
        try {
            final FileStatus fs = this.getFileStatus(f);
            if (target.isEmpty()) {
                return fs;
            }
            return new FileStatus(fs.getLen(), false, fs.getReplication(), fs.getBlockSize(), fs.getModificationTime(), fs.getAccessTime(), fs.getPermission(), fs.getOwner(), fs.getGroup(), new Path(target), f);
        }
        catch (FileNotFoundException e) {
            if (!target.isEmpty()) {
                return new FileStatus(0L, false, 0, 0L, 0L, 0L, FsPermission.getDefault(), "", "", new Path(target), f);
            }
            throw e;
        }
    }
    
    private FileStatus getNativeFileLinkStatus(final Path f, final boolean dereference) throws IOException {
        this.checkPath(f);
        final Stat stat = new Stat(f, this.getDefaultBlockSize(f), dereference, this);
        final FileStatus status = stat.getFileStatus();
        return status;
    }
    
    @Override
    public Path getLinkTarget(final Path f) throws IOException {
        final FileStatus fi = this.getFileLinkStatusInternal(f, false);
        return fi.getSymlink();
    }
    
    static {
        NAME = URI.create("file:///");
        RawLocalFileSystem.useDeprecatedFileStatus = true;
    }
    
    class LocalFSFileInputStream extends FSInputStream implements HasFileDescriptor
    {
        private FileInputStream fis;
        private long position;
        
        public LocalFSFileInputStream(final Path f) throws IOException {
            this.fis = new FileInputStream(RawLocalFileSystem.this.pathToFile(f));
        }
        
        @Override
        public void seek(final long pos) throws IOException {
            if (pos < 0L) {
                throw new EOFException("Cannot seek to a negative offset");
            }
            this.fis.getChannel().position(pos);
            this.position = pos;
        }
        
        @Override
        public long getPos() throws IOException {
            return this.position;
        }
        
        @Override
        public boolean seekToNewSource(final long targetPos) throws IOException {
            return false;
        }
        
        @Override
        public int available() throws IOException {
            return this.fis.available();
        }
        
        @Override
        public void close() throws IOException {
            this.fis.close();
        }
        
        @Override
        public boolean markSupported() {
            return false;
        }
        
        @Override
        public int read() throws IOException {
            try {
                final int value = this.fis.read();
                if (value >= 0) {
                    ++this.position;
                    RawLocalFileSystem.this.statistics.incrementBytesRead(1L);
                }
                return value;
            }
            catch (IOException e) {
                throw new FSError(e);
            }
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            this.validatePositionedReadArgs(this.position, b, off, len);
            try {
                final int value = this.fis.read(b, off, len);
                if (value > 0) {
                    this.position += value;
                    RawLocalFileSystem.this.statistics.incrementBytesRead(value);
                }
                return value;
            }
            catch (IOException e) {
                throw new FSError(e);
            }
        }
        
        @Override
        public int read(final long position, final byte[] b, final int off, final int len) throws IOException {
            this.validatePositionedReadArgs(position, b, off, len);
            if (len == 0) {
                return 0;
            }
            final ByteBuffer bb = ByteBuffer.wrap(b, off, len);
            try {
                final int value = this.fis.getChannel().read(bb, position);
                if (value > 0) {
                    RawLocalFileSystem.this.statistics.incrementBytesRead(value);
                }
                return value;
            }
            catch (IOException e) {
                throw new FSError(e);
            }
        }
        
        @Override
        public long skip(final long n) throws IOException {
            final long value = this.fis.skip(n);
            if (value > 0L) {
                this.position += value;
            }
            return value;
        }
        
        @Override
        public FileDescriptor getFileDescriptor() throws IOException {
            return this.fis.getFD();
        }
    }
    
    class LocalFSFileOutputStream extends OutputStream
    {
        private FileOutputStream fos;
        
        private LocalFSFileOutputStream(final Path f, final boolean append, FsPermission permission) throws IOException {
            final File file = RawLocalFileSystem.this.pathToFile(f);
            if (!append && permission == null) {
                permission = FsPermission.getFileDefault();
            }
            if (permission == null) {
                this.fos = new FileOutputStream(file, append);
            }
            else {
                permission = permission.applyUMask(FsPermission.getUMask(RawLocalFileSystem.this.getConf()));
                if (Shell.WINDOWS && NativeIO.isAvailable()) {
                    this.fos = NativeIO.Windows.createFileOutputStreamWithMode(file, append, permission.toShort());
                }
                else {
                    this.fos = new FileOutputStream(file, append);
                    boolean success = false;
                    try {
                        RawLocalFileSystem.this.setPermission(f, permission);
                        success = true;
                    }
                    finally {
                        if (!success) {
                            IOUtils.cleanup(FileSystem.LOG, this.fos);
                        }
                    }
                }
            }
        }
        
        @Override
        public void close() throws IOException {
            this.fos.close();
        }
        
        @Override
        public void flush() throws IOException {
            this.fos.flush();
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            try {
                this.fos.write(b, off, len);
            }
            catch (IOException e) {
                throw new FSError(e);
            }
        }
        
        @Override
        public void write(final int b) throws IOException {
            try {
                this.fos.write(b);
            }
            catch (IOException e) {
                throw new FSError(e);
            }
        }
    }
    
    @Deprecated
    static class DeprecatedRawLocalFileStatus extends FileStatus
    {
        private boolean isPermissionLoaded() {
            return !super.getOwner().isEmpty();
        }
        
        private static long getLastAccessTime(final File f) throws IOException {
            long accessTime;
            try {
                accessTime = Files.readAttributes(f.toPath(), BasicFileAttributes.class, new LinkOption[0]).lastAccessTime().toMillis();
            }
            catch (NoSuchFileException e) {
                throw new FileNotFoundException("File " + f + " does not exist");
            }
            return accessTime;
        }
        
        DeprecatedRawLocalFileStatus(final File f, final long defaultBlockSize, final FileSystem fs) throws IOException {
            super(f.length(), f.isDirectory(), 1, defaultBlockSize, f.lastModified(), getLastAccessTime(f), null, null, null, new Path(f.getPath()).makeQualified(fs.getUri(), fs.getWorkingDirectory()));
        }
        
        @Override
        public FsPermission getPermission() {
            if (!this.isPermissionLoaded()) {
                this.loadPermissionInfo();
            }
            return super.getPermission();
        }
        
        @Override
        public String getOwner() {
            if (!this.isPermissionLoaded()) {
                this.loadPermissionInfo();
            }
            return super.getOwner();
        }
        
        @Override
        public String getGroup() {
            if (!this.isPermissionLoaded()) {
                this.loadPermissionInfo();
            }
            return super.getGroup();
        }
        
        private synchronized void loadPermissionInfo() {
            if (!this.isPermissionLoaded() && NativeIO.isAvailable()) {
                try {
                    this.loadPermissionInfoByNativeIO();
                }
                catch (IOException ex) {
                    FileSystem.LOG.debug("Native call failed", ex);
                }
            }
            if (!this.isPermissionLoaded()) {
                this.loadPermissionInfoByNonNativeIO();
            }
        }
        
        @VisibleForTesting
        void loadPermissionInfoByNonNativeIO() {
            IOException e = null;
            try {
                final String output = FileUtil.execCommand(new File(this.getPath().toUri()), Shell.getGetPermissionCommand());
                final StringTokenizer t = new StringTokenizer(output, Shell.TOKEN_SEPARATOR_REGEX);
                String permission = t.nextToken();
                if (permission.length() > 10) {
                    permission = permission.substring(0, 10);
                }
                this.setPermission(FsPermission.valueOf(permission));
                t.nextToken();
                String owner = t.nextToken();
                String group = t.nextToken();
                if (Shell.WINDOWS) {
                    owner = this.removeDomain(owner);
                    group = this.removeDomain(group);
                }
                this.setOwner(owner);
                this.setGroup(group);
                if (e != null) {
                    throw new RuntimeException("Error while running command to get file permissions : " + StringUtils.stringifyException(e));
                }
            }
            catch (Shell.ExitCodeException ioe) {
                if (ioe.getExitCode() != 1) {
                    e = ioe;
                }
                else {
                    this.setPermission(null);
                    this.setOwner(null);
                    this.setGroup(null);
                }
            }
            catch (IOException ioe2) {
                e = ioe2;
            }
            finally {
                if (e != null) {
                    throw new RuntimeException("Error while running command to get file permissions : " + StringUtils.stringifyException(e));
                }
            }
        }
        
        private String removeDomain(String str) {
            final int index = str.indexOf("\\");
            if (index != -1) {
                str = str.substring(index + 1);
            }
            return str;
        }
        
        @VisibleForTesting
        void loadPermissionInfoByNativeIO() throws IOException {
            final Path path = this.getPath();
            String pathName = path.toUri().getPath();
            if (Shell.WINDOWS && pathName.startsWith("/")) {
                pathName = pathName.substring(1);
            }
            try {
                final NativeIO.POSIX.Stat stat = NativeIO.POSIX.getStat(pathName);
                final String owner = stat.getOwner();
                final String group = stat.getGroup();
                final int mode = stat.getMode();
                this.setOwner(owner);
                this.setGroup(group);
                this.setPermission(new FsPermission(mode));
            }
            catch (IOException e) {
                this.setOwner(null);
                this.setGroup(null);
                this.setPermission(null);
                throw e;
            }
        }
        
        @Override
        public void write(final DataOutput out) throws IOException {
            if (!this.isPermissionLoaded()) {
                this.loadPermissionInfo();
            }
            super.write(out);
        }
    }
}
