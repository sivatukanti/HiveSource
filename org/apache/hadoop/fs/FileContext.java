// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.Closeable;
import org.apache.hadoop.io.IOUtils;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Arrays;
import javax.annotation.Nonnull;
import java.util.IdentityHashMap;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.AclEntry;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.hadoop.security.token.Token;
import java.util.List;
import java.util.HashSet;
import java.util.TreeSet;
import org.apache.hadoop.util.ShutdownHookManager;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.fs.permission.FsCreateModes;
import java.util.EnumSet;
import org.apache.hadoop.security.AccessControlException;
import java.io.FileNotFoundException;
import java.security.PrivilegedExceptionAction;
import java.net.URI;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.io.IOException;
import org.apache.htrace.core.Tracer;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.conf.Configuration;
import java.util.Set;
import java.util.Map;
import org.apache.hadoop.fs.permission.FsPermission;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class FileContext
{
    public static final Logger LOG;
    public static final FsPermission DEFAULT_PERM;
    public static final FsPermission DIR_DEFAULT_PERM;
    public static final FsPermission FILE_DEFAULT_PERM;
    public static final int SHUTDOWN_HOOK_PRIORITY = 20;
    static final Map<FileContext, Set<Path>> DELETE_ON_EXIT;
    static final FileContextFinalizer FINALIZER;
    private static final PathFilter DEFAULT_FILTER;
    private final AbstractFileSystem defaultFS;
    private Path workingDir;
    private FsPermission umask;
    private final Configuration conf;
    private final UserGroupInformation ugi;
    final boolean resolveSymlinks;
    private final Tracer tracer;
    private final Util util;
    
    private FileContext(final AbstractFileSystem defFs, final Configuration aConf) {
        this.defaultFS = defFs;
        this.conf = aConf;
        this.tracer = FsTracer.get(aConf);
        try {
            this.ugi = UserGroupInformation.getCurrentUser();
        }
        catch (IOException e) {
            FileContext.LOG.error("Exception in getCurrentUser: ", e);
            throw new RuntimeException("Failed to get the current user while creating a FileContext", e);
        }
        this.workingDir = this.defaultFS.getInitialWorkingDirectory();
        if (this.workingDir == null) {
            this.workingDir = this.defaultFS.getHomeDirectory();
        }
        this.resolveSymlinks = this.conf.getBoolean("fs.client.resolve.remote.symlinks", true);
        this.util = new Util();
    }
    
    Path fixRelativePart(final Path p) {
        Preconditions.checkNotNull(p, (Object)"path cannot be null");
        if (p.isUriPathAbsolute()) {
            return p;
        }
        return new Path(this.workingDir, p);
    }
    
    static void processDeleteOnExit() {
        synchronized (FileContext.DELETE_ON_EXIT) {
            final Set<Map.Entry<FileContext, Set<Path>>> set = FileContext.DELETE_ON_EXIT.entrySet();
            for (final Map.Entry<FileContext, Set<Path>> entry : set) {
                final FileContext fc = entry.getKey();
                final Set<Path> paths = entry.getValue();
                for (final Path path : paths) {
                    try {
                        fc.delete(path, true);
                    }
                    catch (IOException e) {
                        FileContext.LOG.warn("Ignoring failure to deleteOnExit for path " + path);
                    }
                }
            }
            FileContext.DELETE_ON_EXIT.clear();
        }
    }
    
    protected AbstractFileSystem getFSofPath(final Path absOrFqPath) throws UnsupportedFileSystemException, IOException {
        absOrFqPath.checkNotSchemeWithRelative();
        absOrFqPath.checkNotRelative();
        try {
            this.defaultFS.checkPath(absOrFqPath);
            return this.defaultFS;
        }
        catch (Exception e) {
            return getAbstractFileSystem(this.ugi, absOrFqPath.toUri(), this.conf);
        }
    }
    
    private static AbstractFileSystem getAbstractFileSystem(final UserGroupInformation user, final URI uri, final Configuration conf) throws UnsupportedFileSystemException, IOException {
        try {
            return user.doAs((PrivilegedExceptionAction<AbstractFileSystem>)new PrivilegedExceptionAction<AbstractFileSystem>() {
                @Override
                public AbstractFileSystem run() throws UnsupportedFileSystemException {
                    return AbstractFileSystem.get(uri, conf);
                }
            });
        }
        catch (RuntimeException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw ex;
        }
        catch (InterruptedException ex2) {
            FileContext.LOG.error(ex2.toString());
            throw new IOException("Failed to get the AbstractFileSystem for path: " + uri, ex2);
        }
    }
    
    public static FileContext getFileContext(final AbstractFileSystem defFS, final Configuration aConf) {
        return new FileContext(defFS, aConf);
    }
    
    protected static FileContext getFileContext(final AbstractFileSystem defaultFS) {
        return getFileContext(defaultFS, new Configuration());
    }
    
    public static FileContext getFileContext() throws UnsupportedFileSystemException {
        return getFileContext(new Configuration());
    }
    
    public static FileContext getLocalFSFileContext() throws UnsupportedFileSystemException {
        return getFileContext(FsConstants.LOCAL_FS_URI);
    }
    
    public static FileContext getFileContext(final URI defaultFsUri) throws UnsupportedFileSystemException {
        return getFileContext(defaultFsUri, new Configuration());
    }
    
    public static FileContext getFileContext(final URI defaultFsUri, final Configuration aConf) throws UnsupportedFileSystemException {
        UserGroupInformation currentUser = null;
        AbstractFileSystem defaultAfs = null;
        if (defaultFsUri.getScheme() == null) {
            return getFileContext(aConf);
        }
        try {
            currentUser = UserGroupInformation.getCurrentUser();
            defaultAfs = getAbstractFileSystem(currentUser, defaultFsUri, aConf);
        }
        catch (UnsupportedFileSystemException ex) {
            throw ex;
        }
        catch (IOException ex2) {
            FileContext.LOG.error(ex2.toString());
            throw new RuntimeException(ex2);
        }
        return getFileContext(defaultAfs, aConf);
    }
    
    public static FileContext getFileContext(final Configuration aConf) throws UnsupportedFileSystemException {
        final URI defaultFsUri = URI.create(aConf.get("fs.defaultFS", "file:///"));
        if (defaultFsUri.getScheme() != null && !defaultFsUri.getScheme().trim().isEmpty()) {
            return getFileContext(defaultFsUri, aConf);
        }
        throw new UnsupportedFileSystemException(String.format("%s: URI configured via %s carries no scheme", defaultFsUri, "fs.defaultFS"));
    }
    
    public static FileContext getLocalFSFileContext(final Configuration aConf) throws UnsupportedFileSystemException {
        return getFileContext(FsConstants.LOCAL_FS_URI, aConf);
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public AbstractFileSystem getDefaultFileSystem() {
        return this.defaultFS;
    }
    
    public void setWorkingDirectory(final Path newWDir) throws IOException {
        newWDir.checkNotSchemeWithRelative();
        final Path newWorkingDir = new Path(this.workingDir, newWDir);
        final FileStatus status = this.getFileStatus(newWorkingDir);
        if (status.isFile()) {
            throw new FileNotFoundException("Cannot setWD to a file");
        }
        this.workingDir = newWorkingDir;
    }
    
    public Path getWorkingDirectory() {
        return this.workingDir;
    }
    
    public UserGroupInformation getUgi() {
        return this.ugi;
    }
    
    public Path getHomeDirectory() {
        return this.defaultFS.getHomeDirectory();
    }
    
    public FsPermission getUMask() {
        return (this.umask != null) ? this.umask : FsPermission.getUMask(this.conf);
    }
    
    public void setUMask(final FsPermission newUmask) {
        this.umask = newUmask;
    }
    
    public Path resolvePath(final Path f) throws FileNotFoundException, UnresolvedLinkException, AccessControlException, IOException {
        return this.resolve(f);
    }
    
    public Path makeQualified(final Path path) {
        return path.makeQualified(this.defaultFS.getUri(), this.getWorkingDirectory());
    }
    
    public FSDataOutputStream create(final Path f, final EnumSet<CreateFlag> createFlag, final Options.CreateOpts... opts) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        final Options.CreateOpts.Perms permOpt = Options.CreateOpts.getOpt(Options.CreateOpts.Perms.class, opts);
        FsPermission permission = (permOpt != null) ? permOpt.getValue() : FileContext.FILE_DEFAULT_PERM;
        permission = FsCreateModes.applyUMask(permission, this.getUMask());
        final Options.CreateOpts[] updatedOpts = Options.CreateOpts.setOpt(Options.CreateOpts.perms(permission), opts);
        return new FSLinkResolver<FSDataOutputStream>() {
            @Override
            public FSDataOutputStream next(final AbstractFileSystem fs, final Path p) throws IOException {
                return fs.create(p, createFlag, updatedOpts);
            }
        }.resolve(this, absF);
    }
    
    public FSDataOutputStreamBuilder<FSDataOutputStream, ?> create(final Path f) throws IOException {
        return ((FSDataOutputStreamBuilder<S, FSDataOutputStreamBuilder<FSDataOutputStream, ?>>)new FCDataOutputStreamBuilder(this, f)).create();
    }
    
    public void mkdir(final Path dir, final FsPermission permission, final boolean createParent) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
        final Path absDir = this.fixRelativePart(dir);
        final FsPermission absFerms = FsCreateModes.applyUMask((permission == null) ? FsPermission.getDirDefault() : permission, this.getUMask());
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                fs.mkdir(p, absFerms, createParent);
                return null;
            }
        }.resolve(this, absDir);
    }
    
    public boolean delete(final Path f, final boolean recursive) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<Boolean>() {
            @Override
            public Boolean next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.delete(p, recursive);
            }
        }.resolve(this, absF);
    }
    
    public FSDataInputStream open(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<FSDataInputStream>() {
            @Override
            public FSDataInputStream next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.open(p);
            }
        }.resolve(this, absF);
    }
    
    public FSDataInputStream open(final Path f, final int bufferSize) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<FSDataInputStream>() {
            @Override
            public FSDataInputStream next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.open(p, bufferSize);
            }
        }.resolve(this, absF);
    }
    
    public boolean truncate(final Path f, final long newLength) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<Boolean>() {
            @Override
            public Boolean next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.truncate(p, newLength);
            }
        }.resolve(this, absF);
    }
    
    public boolean setReplication(final Path f, final short replication) throws AccessControlException, FileNotFoundException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<Boolean>() {
            @Override
            public Boolean next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.setReplication(p, replication);
            }
        }.resolve(this, absF);
    }
    
    public void rename(final Path src, final Path dst, final Options.Rename... options) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
        final Path absSrc = this.fixRelativePart(src);
        final Path absDst = this.fixRelativePart(dst);
        final AbstractFileSystem srcFS = this.getFSofPath(absSrc);
        final AbstractFileSystem dstFS = this.getFSofPath(absDst);
        if (!srcFS.getUri().equals(dstFS.getUri())) {
            throw new IOException("Renames across AbstractFileSystems not supported");
        }
        try {
            srcFS.rename(absSrc, absDst, options);
        }
        catch (UnresolvedLinkException e) {
            final Path source = this.resolveIntermediate(absSrc);
            new FSLinkResolver<Void>() {
                @Override
                public Void next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                    fs.rename(source, p, options);
                    return null;
                }
            }.resolve(this, absDst);
        }
    }
    
    public void setPermission(final Path f, final FsPermission permission) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                fs.setPermission(p, permission);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public void setOwner(final Path f, final String username, final String groupname) throws AccessControlException, UnsupportedFileSystemException, FileNotFoundException, IOException {
        if (username == null && groupname == null) {
            throw new HadoopIllegalArgumentException("username and groupname cannot both be null");
        }
        final Path absF = this.fixRelativePart(f);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                fs.setOwner(p, username, groupname);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public void setTimes(final Path f, final long mtime, final long atime) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                fs.setTimes(p, mtime, atime);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public FileChecksum getFileChecksum(final Path f) throws AccessControlException, FileNotFoundException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<FileChecksum>() {
            @Override
            public FileChecksum next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.getFileChecksum(p);
            }
        }.resolve(this, absF);
    }
    
    public void setVerifyChecksum(final boolean verifyChecksum, final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.resolve(this.fixRelativePart(f));
        this.getFSofPath(absF).setVerifyChecksum(verifyChecksum);
    }
    
    public FileStatus getFileStatus(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<FileStatus>() {
            @Override
            public FileStatus next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.getFileStatus(p);
            }
        }.resolve(this, absF);
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "Hive" })
    public void access(final Path path, final FsAction mode) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absPath = this.fixRelativePart(path);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                fs.access(p, mode);
                return null;
            }
        }.resolve(this, absPath);
    }
    
    public FileStatus getFileLinkStatus(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<FileStatus>() {
            @Override
            public FileStatus next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                final FileStatus fi = fs.getFileLinkStatus(p);
                if (fi.isSymlink()) {
                    fi.setSymlink(FSLinkResolver.qualifySymlinkTarget(fs.getUri(), p, fi.getSymlink()));
                }
                return fi;
            }
        }.resolve(this, absF);
    }
    
    public Path getLinkTarget(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<Path>() {
            @Override
            public Path next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                final FileStatus fi = fs.getFileLinkStatus(p);
                return fi.getSymlink();
            }
        }.resolve(this, absF);
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
    @InterfaceStability.Evolving
    public BlockLocation[] getFileBlockLocations(final Path f, final long start, final long len) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<BlockLocation[]>() {
            @Override
            public BlockLocation[] next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.getFileBlockLocations(p, start, len);
            }
        }.resolve(this, absF);
    }
    
    public FsStatus getFsStatus(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        if (f == null) {
            return this.defaultFS.getFsStatus();
        }
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<FsStatus>() {
            @Override
            public FsStatus next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.getFsStatus(p);
            }
        }.resolve(this, absF);
    }
    
    public void createSymlink(final Path target, final Path link, final boolean createParent) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
        if (!FileSystem.areSymlinksEnabled()) {
            throw new UnsupportedOperationException("Symlinks not supported");
        }
        final Path nonRelLink = this.fixRelativePart(link);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                fs.createSymlink(target, p, createParent);
                return null;
            }
        }.resolve(this, nonRelLink);
    }
    
    public RemoteIterator<FileStatus> listStatus(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<RemoteIterator<FileStatus>>() {
            @Override
            public RemoteIterator<FileStatus> next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.listStatusIterator(p);
            }
        }.resolve(this, absF);
    }
    
    public RemoteIterator<Path> listCorruptFileBlocks(final Path path) throws IOException {
        final Path absF = this.fixRelativePart(path);
        return new FSLinkResolver<RemoteIterator<Path>>() {
            @Override
            public RemoteIterator<Path> next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.listCorruptFileBlocks(p);
            }
        }.resolve(this, absF);
    }
    
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final Path absF = this.fixRelativePart(f);
        return new FSLinkResolver<RemoteIterator<LocatedFileStatus>>() {
            @Override
            public RemoteIterator<LocatedFileStatus> next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.listLocatedStatus(p);
            }
        }.resolve(this, absF);
    }
    
    public boolean deleteOnExit(final Path f) throws AccessControlException, IOException {
        if (!this.util().exists(f)) {
            return false;
        }
        synchronized (FileContext.DELETE_ON_EXIT) {
            if (FileContext.DELETE_ON_EXIT.isEmpty()) {
                ShutdownHookManager.get().addShutdownHook(FileContext.FINALIZER, 20);
            }
            Set<Path> set = FileContext.DELETE_ON_EXIT.get(this);
            if (set == null) {
                set = new TreeSet<Path>();
                FileContext.DELETE_ON_EXIT.put(this, set);
            }
            set.add(f);
        }
        return true;
    }
    
    public Util util() {
        return this.util;
    }
    
    private void checkDest(final String srcName, final Path dst, final boolean overwrite) throws AccessControlException, IOException {
        try {
            final FileStatus dstFs = this.getFileStatus(dst);
            if (dstFs.isDirectory()) {
                if (null == srcName) {
                    throw new IOException("Target " + dst + " is a directory");
                }
                this.checkDest(null, new Path(dst, srcName), overwrite);
            }
            else if (!overwrite) {
                throw new IOException("Target " + new Path(dst, srcName) + " already exists");
            }
        }
        catch (FileNotFoundException ex) {}
    }
    
    private static void checkDependencies(final Path qualSrc, final Path qualDst) throws IOException {
        if (isSameFS(qualSrc, qualDst)) {
            final String srcq = qualSrc.toString() + "/";
            final String dstq = qualDst.toString() + "/";
            if (dstq.startsWith(srcq)) {
                if (srcq.length() == dstq.length()) {
                    throw new IOException("Cannot copy " + qualSrc + " to itself.");
                }
                throw new IOException("Cannot copy " + qualSrc + " to its subdirectory " + qualDst);
            }
        }
    }
    
    private static boolean isSameFS(final Path qualPath1, final Path qualPath2) {
        final URI srcUri = qualPath1.toUri();
        final URI dstUri = qualPath2.toUri();
        return srcUri.getScheme().equals(dstUri.getScheme()) && (srcUri.getAuthority() == null || dstUri.getAuthority() == null || !srcUri.getAuthority().equals(dstUri.getAuthority()));
    }
    
    protected Path resolve(final Path f) throws FileNotFoundException, UnresolvedLinkException, AccessControlException, IOException {
        return new FSLinkResolver<Path>() {
            @Override
            public Path next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.resolvePath(p);
            }
        }.resolve(this, f);
    }
    
    protected Path resolveIntermediate(final Path f) throws IOException {
        return new FSLinkResolver<FileStatus>() {
            @Override
            public FileStatus next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                return fs.getFileLinkStatus(p);
            }
        }.resolve(this, f).getPath();
    }
    
    Set<AbstractFileSystem> resolveAbstractFileSystems(final Path f) throws IOException {
        final Path absF = this.fixRelativePart(f);
        final HashSet<AbstractFileSystem> result = new HashSet<AbstractFileSystem>();
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                result.add(fs);
                fs.getFileStatus(p);
                return null;
            }
        }.resolve(this, absF);
        return result;
    }
    
    public static FileSystem.Statistics getStatistics(final URI uri) {
        return AbstractFileSystem.getStatistics(uri);
    }
    
    public static void clearStatistics() {
        AbstractFileSystem.clearStatistics();
    }
    
    public static void printStatistics() {
        AbstractFileSystem.printStatistics();
    }
    
    public static Map<URI, FileSystem.Statistics> getAllStatistics() {
        return AbstractFileSystem.getAllStatistics();
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
    public List<Token<?>> getDelegationTokens(final Path p, final String renewer) throws IOException {
        final Set<AbstractFileSystem> afsSet = this.resolveAbstractFileSystems(p);
        final List<Token<?>> tokenList = new ArrayList<Token<?>>();
        for (final AbstractFileSystem afs : afsSet) {
            final List<Token<?>> afsTokens = afs.getDelegationTokens(renewer);
            tokenList.addAll(afsTokens);
        }
        return tokenList;
    }
    
    public void modifyAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        final Path absF = this.fixRelativePart(path);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException {
                fs.modifyAclEntries(p, aclSpec);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public void removeAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        final Path absF = this.fixRelativePart(path);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException {
                fs.removeAclEntries(p, aclSpec);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public void removeDefaultAcl(final Path path) throws IOException {
        final Path absF = this.fixRelativePart(path);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException {
                fs.removeDefaultAcl(p);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public void removeAcl(final Path path) throws IOException {
        final Path absF = this.fixRelativePart(path);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException {
                fs.removeAcl(p);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public void setAcl(final Path path, final List<AclEntry> aclSpec) throws IOException {
        final Path absF = this.fixRelativePart(path);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException {
                fs.setAcl(p, aclSpec);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public AclStatus getAclStatus(final Path path) throws IOException {
        final Path absF = this.fixRelativePart(path);
        return new FSLinkResolver<AclStatus>() {
            @Override
            public AclStatus next(final AbstractFileSystem fs, final Path p) throws IOException {
                return fs.getAclStatus(p);
            }
        }.resolve(this, absF);
    }
    
    public void setXAttr(final Path path, final String name, final byte[] value) throws IOException {
        this.setXAttr(path, name, value, EnumSet.of(XAttrSetFlag.CREATE, XAttrSetFlag.REPLACE));
    }
    
    public void setXAttr(final Path path, final String name, final byte[] value, final EnumSet<XAttrSetFlag> flag) throws IOException {
        final Path absF = this.fixRelativePart(path);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException {
                fs.setXAttr(p, name, value, flag);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public byte[] getXAttr(final Path path, final String name) throws IOException {
        final Path absF = this.fixRelativePart(path);
        return new FSLinkResolver<byte[]>() {
            @Override
            public byte[] next(final AbstractFileSystem fs, final Path p) throws IOException {
                return fs.getXAttr(p, name);
            }
        }.resolve(this, absF);
    }
    
    public Map<String, byte[]> getXAttrs(final Path path) throws IOException {
        final Path absF = this.fixRelativePart(path);
        return new FSLinkResolver<Map<String, byte[]>>() {
            @Override
            public Map<String, byte[]> next(final AbstractFileSystem fs, final Path p) throws IOException {
                return fs.getXAttrs(p);
            }
        }.resolve(this, absF);
    }
    
    public Map<String, byte[]> getXAttrs(final Path path, final List<String> names) throws IOException {
        final Path absF = this.fixRelativePart(path);
        return new FSLinkResolver<Map<String, byte[]>>() {
            @Override
            public Map<String, byte[]> next(final AbstractFileSystem fs, final Path p) throws IOException {
                return fs.getXAttrs(p, names);
            }
        }.resolve(this, absF);
    }
    
    public void removeXAttr(final Path path, final String name) throws IOException {
        final Path absF = this.fixRelativePart(path);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException {
                fs.removeXAttr(p, name);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public List<String> listXAttrs(final Path path) throws IOException {
        final Path absF = this.fixRelativePart(path);
        return new FSLinkResolver<List<String>>() {
            @Override
            public List<String> next(final AbstractFileSystem fs, final Path p) throws IOException {
                return fs.listXAttrs(p);
            }
        }.resolve(this, absF);
    }
    
    public final Path createSnapshot(final Path path) throws IOException {
        return this.createSnapshot(path, null);
    }
    
    public Path createSnapshot(final Path path, final String snapshotName) throws IOException {
        final Path absF = this.fixRelativePart(path);
        return new FSLinkResolver<Path>() {
            @Override
            public Path next(final AbstractFileSystem fs, final Path p) throws IOException {
                return fs.createSnapshot(p, snapshotName);
            }
        }.resolve(this, absF);
    }
    
    public void renameSnapshot(final Path path, final String snapshotOldName, final String snapshotNewName) throws IOException {
        final Path absF = this.fixRelativePart(path);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException {
                fs.renameSnapshot(p, snapshotOldName, snapshotNewName);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public void deleteSnapshot(final Path path, final String snapshotName) throws IOException {
        final Path absF = this.fixRelativePart(path);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException {
                fs.deleteSnapshot(p, snapshotName);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public void setStoragePolicy(final Path path, final String policyName) throws IOException {
        final Path absF = this.fixRelativePart(path);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException {
                fs.setStoragePolicy(path, policyName);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public void unsetStoragePolicy(final Path src) throws IOException {
        final Path absF = this.fixRelativePart(src);
        new FSLinkResolver<Void>() {
            @Override
            public Void next(final AbstractFileSystem fs, final Path p) throws IOException {
                fs.unsetStoragePolicy(src);
                return null;
            }
        }.resolve(this, absF);
    }
    
    public BlockStoragePolicySpi getStoragePolicy(final Path path) throws IOException {
        final Path absF = this.fixRelativePart(path);
        return new FSLinkResolver<BlockStoragePolicySpi>() {
            @Override
            public BlockStoragePolicySpi next(final AbstractFileSystem fs, final Path p) throws IOException {
                return fs.getStoragePolicy(p);
            }
        }.resolve(this, absF);
    }
    
    public Collection<? extends BlockStoragePolicySpi> getAllStoragePolicies() throws IOException {
        return this.defaultFS.getAllStoragePolicies();
    }
    
    Tracer getTracer() {
        return this.tracer;
    }
    
    static {
        LOG = LoggerFactory.getLogger(FileContext.class);
        DEFAULT_PERM = FsPermission.getDefault();
        DIR_DEFAULT_PERM = FsPermission.getDirDefault();
        FILE_DEFAULT_PERM = FsPermission.getFileDefault();
        DELETE_ON_EXIT = new IdentityHashMap<FileContext, Set<Path>>();
        FINALIZER = new FileContextFinalizer();
        DEFAULT_FILTER = new PathFilter() {
            @Override
            public boolean accept(final Path file) {
                return true;
            }
        };
    }
    
    private static final class FCDataOutputStreamBuilder extends FSDataOutputStreamBuilder<FSDataOutputStream, FCDataOutputStreamBuilder>
    {
        private final FileContext fc;
        
        private FCDataOutputStreamBuilder(@Nonnull final FileContext fc, @Nonnull final Path p) throws IOException {
            super(fc, p);
            Preconditions.checkNotNull(this.fc = fc);
        }
        
        @Override
        protected FCDataOutputStreamBuilder getThisBuilder() {
            return this;
        }
        
        @Override
        public FSDataOutputStream build() throws IOException {
            final EnumSet<CreateFlag> flags = this.getFlags();
            final List<Options.CreateOpts> createOpts = new ArrayList<Options.CreateOpts>(Arrays.asList(Options.CreateOpts.blockSize(this.getBlockSize()), Options.CreateOpts.bufferSize(this.getBufferSize()), Options.CreateOpts.repFac(this.getReplication()), Options.CreateOpts.perms(this.getPermission())));
            if (this.getChecksumOpt() != null) {
                createOpts.add(Options.CreateOpts.checksumParam(this.getChecksumOpt()));
            }
            if (this.getProgress() != null) {
                createOpts.add(Options.CreateOpts.progress(this.getProgress()));
            }
            if (this.isRecursive()) {
                createOpts.add(Options.CreateOpts.createParent());
            }
            return this.fc.create(this.getPath(), flags, (Options.CreateOpts[])createOpts.toArray(new Options.CreateOpts[0]));
        }
    }
    
    public class Util
    {
        public boolean exists(final Path f) throws AccessControlException, UnsupportedFileSystemException, IOException {
            try {
                final FileStatus fs = FileContext.this.getFileStatus(f);
                assert fs != null;
                return true;
            }
            catch (FileNotFoundException e) {
                return false;
            }
        }
        
        public ContentSummary getContentSummary(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
            final FileStatus status = FileContext.this.getFileStatus(f);
            if (status.isFile()) {
                final long length = status.getLen();
                return new ContentSummary.Builder().length(length).fileCount(1L).directoryCount(0L).spaceConsumed(length).build();
            }
            final long[] summary = { 0L, 0L, 1L };
            final RemoteIterator<FileStatus> statusIterator = FileContext.this.listStatus(f);
            while (statusIterator.hasNext()) {
                final FileStatus s = statusIterator.next();
                final long length2 = s.getLen();
                final ContentSummary c = s.isDirectory() ? this.getContentSummary(s.getPath()) : new ContentSummary.Builder().length(length2).fileCount(1L).directoryCount(0L).spaceConsumed(length2).build();
                final long[] array = summary;
                final int n = 0;
                array[n] += c.getLength();
                final long[] array2 = summary;
                final int n2 = 1;
                array2[n2] += c.getFileCount();
                final long[] array3 = summary;
                final int n3 = 2;
                array3[n3] += c.getDirectoryCount();
            }
            return new ContentSummary.Builder().length(summary[0]).fileCount(summary[1]).directoryCount(summary[2]).spaceConsumed(summary[0]).build();
        }
        
        public FileStatus[] listStatus(final Path[] files) throws AccessControlException, FileNotFoundException, IOException {
            return this.listStatus(files, FileContext.DEFAULT_FILTER);
        }
        
        public FileStatus[] listStatus(final Path f, final PathFilter filter) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
            final ArrayList<FileStatus> results = new ArrayList<FileStatus>();
            this.listStatus(results, f, filter);
            return results.toArray(new FileStatus[results.size()]);
        }
        
        public FileStatus[] listStatus(final Path[] files, final PathFilter filter) throws AccessControlException, FileNotFoundException, IOException {
            final ArrayList<FileStatus> results = new ArrayList<FileStatus>();
            for (int i = 0; i < files.length; ++i) {
                this.listStatus(results, files[i], filter);
            }
            return results.toArray(new FileStatus[results.size()]);
        }
        
        private void listStatus(final ArrayList<FileStatus> results, final Path f, final PathFilter filter) throws AccessControlException, FileNotFoundException, IOException {
            final FileStatus[] listing = this.listStatus(f);
            if (listing != null) {
                for (int i = 0; i < listing.length; ++i) {
                    if (filter.accept(listing[i].getPath())) {
                        results.add(listing[i]);
                    }
                }
            }
        }
        
        public FileStatus[] listStatus(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
            final Path absF = FileContext.this.fixRelativePart(f);
            return new FSLinkResolver<FileStatus[]>() {
                @Override
                public FileStatus[] next(final AbstractFileSystem fs, final Path p) throws IOException, UnresolvedLinkException {
                    return fs.listStatus(p);
                }
            }.resolve(FileContext.this, absF);
        }
        
        public RemoteIterator<LocatedFileStatus> listFiles(final Path f, final boolean recursive) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
            return new RemoteIterator<LocatedFileStatus>() {
                private Stack<RemoteIterator<LocatedFileStatus>> itors = new Stack<RemoteIterator<LocatedFileStatus>>();
                RemoteIterator<LocatedFileStatus> curItor = FileContext.this.listLocatedStatus(f);
                LocatedFileStatus curFile;
                
                @Override
                public boolean hasNext() throws IOException {
                    while (this.curFile == null) {
                        if (this.curItor.hasNext()) {
                            this.handleFileStat(this.curItor.next());
                        }
                        else {
                            if (this.itors.empty()) {
                                return false;
                            }
                            this.curItor = this.itors.pop();
                        }
                    }
                    return true;
                }
                
                private void handleFileStat(final LocatedFileStatus stat) throws IOException {
                    if (stat.isFile()) {
                        this.curFile = stat;
                    }
                    else if (stat.isSymlink()) {
                        final FileStatus symstat = FileContext.this.getFileStatus(stat.getSymlink());
                        if (symstat.isFile() || (recursive && symstat.isDirectory())) {
                            this.itors.push(this.curItor);
                            this.curItor = FileContext.this.listLocatedStatus(stat.getPath());
                        }
                    }
                    else if (recursive) {
                        this.itors.push(this.curItor);
                        this.curItor = FileContext.this.listLocatedStatus(stat.getPath());
                    }
                }
                
                @Override
                public LocatedFileStatus next() throws IOException {
                    if (this.hasNext()) {
                        final LocatedFileStatus result = this.curFile;
                        this.curFile = null;
                        return result;
                    }
                    throw new NoSuchElementException("No more entry in " + f);
                }
            };
        }
        
        public FileStatus[] globStatus(final Path pathPattern) throws AccessControlException, UnsupportedFileSystemException, IOException {
            return new Globber(FileContext.this, pathPattern, FileContext.DEFAULT_FILTER).glob();
        }
        
        public FileStatus[] globStatus(final Path pathPattern, final PathFilter filter) throws AccessControlException, UnsupportedFileSystemException, IOException {
            return new Globber(FileContext.this, pathPattern, filter).glob();
        }
        
        public boolean copy(final Path src, final Path dst) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
            return this.copy(src, dst, false, false);
        }
        
        public boolean copy(final Path src, final Path dst, final boolean deleteSource, final boolean overwrite) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
            src.checkNotSchemeWithRelative();
            dst.checkNotSchemeWithRelative();
            final Path qSrc = FileContext.this.makeQualified(src);
            final Path qDst = FileContext.this.makeQualified(dst);
            FileContext.this.checkDest(qSrc.getName(), qDst, overwrite);
            final FileStatus fs = FileContext.this.getFileStatus(qSrc);
            if (fs.isDirectory()) {
                checkDependencies(qSrc, qDst);
                FileContext.this.mkdir(qDst, FsPermission.getDirDefault(), true);
                final FileStatus[] listStatus;
                final FileStatus[] contents = listStatus = this.listStatus(qSrc);
                for (final FileStatus content : listStatus) {
                    this.copy(FileContext.this.makeQualified(content.getPath()), FileContext.this.makeQualified(new Path(qDst, content.getPath().getName())), deleteSource, overwrite);
                }
            }
            else {
                final EnumSet<CreateFlag> createFlag = overwrite ? EnumSet.of(CreateFlag.CREATE, CreateFlag.OVERWRITE) : EnumSet.of(CreateFlag.CREATE);
                final InputStream in = FileContext.this.open(qSrc);
                try (final OutputStream out = FileContext.this.create(qDst, createFlag, new Options.CreateOpts[0])) {
                    IOUtils.copyBytes(in, out, FileContext.this.conf, true);
                }
                finally {
                    IOUtils.closeStream(in);
                }
            }
            return !deleteSource || FileContext.this.delete(qSrc, true);
        }
    }
    
    static class FileContextFinalizer implements Runnable
    {
        @Override
        public synchronized void run() {
            FileContext.processDeleteOnExit();
        }
    }
}
