// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.AclEntry;
import java.util.ArrayList;
import org.apache.hadoop.security.token.Token;
import java.util.List;
import org.apache.hadoop.security.SecurityUtil;
import java.util.NoSuchElementException;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import java.util.EnumSet;
import org.apache.hadoop.security.AccessControlException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.conf.Configuration;
import java.util.StringTokenizer;
import com.google.common.annotations.VisibleForTesting;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class AbstractFileSystem
{
    static final Logger LOG;
    private static final Map<URI, FileSystem.Statistics> STATISTICS_TABLE;
    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE;
    private static final Class<?>[] URI_CONFIG_ARGS;
    protected FileSystem.Statistics statistics;
    @VisibleForTesting
    static final String NO_ABSTRACT_FS_ERROR = "No AbstractFileSystem configured for scheme";
    private final URI myUri;
    
    public FileSystem.Statistics getStatistics() {
        return this.statistics;
    }
    
    public boolean isValidName(final String src) {
        final StringTokenizer tokens = new StringTokenizer(src, "/");
        while (tokens.hasMoreTokens()) {
            final String element = tokens.nextToken();
            if (element.equals("..") || element.equals(".") || element.indexOf(":") >= 0) {
                return false;
            }
        }
        return true;
    }
    
    static <T> T newInstance(final Class<T> theClass, final URI uri, final Configuration conf) {
        T result;
        try {
            Constructor<T> meth = (Constructor<T>)AbstractFileSystem.CONSTRUCTOR_CACHE.get(theClass);
            if (meth == null) {
                meth = theClass.getDeclaredConstructor(AbstractFileSystem.URI_CONFIG_ARGS);
                meth.setAccessible(true);
                AbstractFileSystem.CONSTRUCTOR_CACHE.put(theClass, meth);
            }
            result = meth.newInstance(uri, conf);
        }
        catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new RuntimeException(cause);
        }
        catch (Exception e2) {
            throw new RuntimeException(e2);
        }
        return result;
    }
    
    public static AbstractFileSystem createFileSystem(final URI uri, final Configuration conf) throws UnsupportedFileSystemException {
        final String fsImplConf = String.format("fs.AbstractFileSystem.%s.impl", uri.getScheme());
        final Class<?> clazz = conf.getClass(fsImplConf, null);
        if (clazz == null) {
            throw new UnsupportedFileSystemException(String.format("%s=null: %s: %s", fsImplConf, "No AbstractFileSystem configured for scheme", uri.getScheme()));
        }
        return newInstance(clazz, uri, conf);
    }
    
    protected static synchronized FileSystem.Statistics getStatistics(final URI uri) {
        final String scheme = uri.getScheme();
        if (scheme == null) {
            throw new IllegalArgumentException("Scheme not defined in the uri: " + uri);
        }
        final URI baseUri = getBaseUri(uri);
        FileSystem.Statistics result = AbstractFileSystem.STATISTICS_TABLE.get(baseUri);
        if (result == null) {
            result = new FileSystem.Statistics(scheme);
            AbstractFileSystem.STATISTICS_TABLE.put(baseUri, result);
        }
        return result;
    }
    
    private static URI getBaseUri(final URI uri) {
        final String scheme = uri.getScheme();
        final String authority = uri.getAuthority();
        String baseUriString = scheme + "://";
        if (authority != null) {
            baseUriString += authority;
        }
        else {
            baseUriString += "/";
        }
        return URI.create(baseUriString);
    }
    
    public static synchronized void clearStatistics() {
        for (final FileSystem.Statistics stat : AbstractFileSystem.STATISTICS_TABLE.values()) {
            stat.reset();
        }
    }
    
    public static synchronized void printStatistics() {
        for (final Map.Entry<URI, FileSystem.Statistics> pair : AbstractFileSystem.STATISTICS_TABLE.entrySet()) {
            System.out.println("  FileSystem " + pair.getKey().getScheme() + "://" + pair.getKey().getAuthority() + ": " + pair.getValue());
        }
    }
    
    protected static synchronized Map<URI, FileSystem.Statistics> getAllStatistics() {
        final Map<URI, FileSystem.Statistics> statsMap = new HashMap<URI, FileSystem.Statistics>(AbstractFileSystem.STATISTICS_TABLE.size());
        for (final Map.Entry<URI, FileSystem.Statistics> pair : AbstractFileSystem.STATISTICS_TABLE.entrySet()) {
            final URI key = pair.getKey();
            final FileSystem.Statistics value = pair.getValue();
            final FileSystem.Statistics newStatsObj = new FileSystem.Statistics(value);
            statsMap.put(URI.create(key.toString()), newStatsObj);
        }
        return statsMap;
    }
    
    public static AbstractFileSystem get(final URI uri, final Configuration conf) throws UnsupportedFileSystemException {
        return createFileSystem(uri, conf);
    }
    
    public AbstractFileSystem(final URI uri, final String supportedScheme, final boolean authorityNeeded, final int defaultPort) throws URISyntaxException {
        this.myUri = this.getUri(uri, supportedScheme, authorityNeeded, defaultPort);
        this.statistics = getStatistics(uri);
    }
    
    public void checkScheme(final URI uri, final String supportedScheme) {
        final String scheme = uri.getScheme();
        if (scheme == null) {
            throw new HadoopIllegalArgumentException("Uri without scheme: " + uri);
        }
        if (!scheme.equals(supportedScheme)) {
            throw new HadoopIllegalArgumentException("Uri scheme " + uri + " does not match the scheme " + supportedScheme);
        }
    }
    
    private URI getUri(final URI uri, final String supportedScheme, final boolean authorityNeeded, final int defaultPort) throws URISyntaxException {
        this.checkScheme(uri, supportedScheme);
        if (defaultPort < 0 && authorityNeeded) {
            throw new HadoopIllegalArgumentException("FileSystem implementation error -  default port " + defaultPort + " is not valid");
        }
        final String authority = uri.getAuthority();
        if (authority == null) {
            if (authorityNeeded) {
                throw new HadoopIllegalArgumentException("Uri without authority: " + uri);
            }
            return new URI(supportedScheme + ":///");
        }
        else {
            int port = uri.getPort();
            port = ((port == -1) ? defaultPort : port);
            if (port == -1) {
                return new URI(supportedScheme, authority, "/", null);
            }
            return new URI(supportedScheme + "://" + uri.getHost() + ":" + port);
        }
    }
    
    public abstract int getUriDefaultPort();
    
    public URI getUri() {
        return this.myUri;
    }
    
    public void checkPath(final Path path) {
        final URI uri = path.toUri();
        final String thatScheme = uri.getScheme();
        final String thatAuthority = uri.getAuthority();
        if (thatScheme == null) {
            if (thatAuthority != null) {
                throw new InvalidPathException("Path without scheme with non-null authority:" + path);
            }
            if (path.isUriPathAbsolute()) {
                return;
            }
            throw new InvalidPathException("relative paths not allowed:" + path);
        }
        else {
            final String thisScheme = this.getUri().getScheme();
            final String thisHost = this.getUri().getHost();
            final String thatHost = uri.getHost();
            if (!thisScheme.equalsIgnoreCase(thatScheme) || (thisHost != null && !thisHost.equalsIgnoreCase(thatHost)) || (thisHost == null && thatHost != null)) {
                throw new InvalidPathException("Wrong FS: " + path + ", expected: " + this.getUri());
            }
            final int thisPort = this.getUri().getPort();
            int thatPort = uri.getPort();
            if (thatPort == -1) {
                thatPort = this.getUriDefaultPort();
            }
            if (thisPort != thatPort) {
                throw new InvalidPathException("Wrong FS: " + path + ", expected: " + this.getUri());
            }
        }
    }
    
    public String getUriPath(final Path p) {
        this.checkPath(p);
        final String s = p.toUri().getPath();
        if (!this.isValidName(s)) {
            throw new InvalidPathException("Path part " + s + " from URI " + p + " is not a valid filename.");
        }
        return s;
    }
    
    public Path makeQualified(final Path path) {
        this.checkPath(path);
        return path.makeQualified(this.getUri(), null);
    }
    
    public Path getInitialWorkingDirectory() {
        return null;
    }
    
    public Path getHomeDirectory() {
        return new Path("/user/" + System.getProperty("user.name")).makeQualified(this.getUri(), null);
    }
    
    @Deprecated
    public abstract FsServerDefaults getServerDefaults() throws IOException;
    
    public FsServerDefaults getServerDefaults(final Path f) throws IOException {
        return this.getServerDefaults();
    }
    
    public Path resolvePath(final Path p) throws FileNotFoundException, UnresolvedLinkException, AccessControlException, IOException {
        this.checkPath(p);
        return this.getFileStatus(p).getPath();
    }
    
    public final FSDataOutputStream create(final Path f, final EnumSet<CreateFlag> createFlag, final Options.CreateOpts... opts) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, UnresolvedLinkException, IOException {
        this.checkPath(f);
        int bufferSize = -1;
        short replication = -1;
        long blockSize = -1L;
        int bytesPerChecksum = -1;
        Options.ChecksumOpt checksumOpt = null;
        FsPermission permission = null;
        Progressable progress = null;
        Boolean createParent = null;
        for (final Options.CreateOpts iOpt : opts) {
            if (Options.CreateOpts.BlockSize.class.isInstance(iOpt)) {
                if (blockSize != -1L) {
                    throw new HadoopIllegalArgumentException("BlockSize option is set multiple times");
                }
                blockSize = ((Options.CreateOpts.BlockSize)iOpt).getValue();
            }
            else if (Options.CreateOpts.BufferSize.class.isInstance(iOpt)) {
                if (bufferSize != -1) {
                    throw new HadoopIllegalArgumentException("BufferSize option is set multiple times");
                }
                bufferSize = ((Options.CreateOpts.BufferSize)iOpt).getValue();
            }
            else if (Options.CreateOpts.ReplicationFactor.class.isInstance(iOpt)) {
                if (replication != -1) {
                    throw new HadoopIllegalArgumentException("ReplicationFactor option is set multiple times");
                }
                replication = ((Options.CreateOpts.ReplicationFactor)iOpt).getValue();
            }
            else if (Options.CreateOpts.BytesPerChecksum.class.isInstance(iOpt)) {
                if (bytesPerChecksum != -1) {
                    throw new HadoopIllegalArgumentException("BytesPerChecksum option is set multiple times");
                }
                bytesPerChecksum = ((Options.CreateOpts.BytesPerChecksum)iOpt).getValue();
            }
            else if (Options.CreateOpts.ChecksumParam.class.isInstance(iOpt)) {
                if (checksumOpt != null) {
                    throw new HadoopIllegalArgumentException("CreateChecksumType option is set multiple times");
                }
                checksumOpt = ((Options.CreateOpts.ChecksumParam)iOpt).getValue();
            }
            else if (Options.CreateOpts.Perms.class.isInstance(iOpt)) {
                if (permission != null) {
                    throw new HadoopIllegalArgumentException("Perms option is set multiple times");
                }
                permission = ((Options.CreateOpts.Perms)iOpt).getValue();
            }
            else if (Options.CreateOpts.Progress.class.isInstance(iOpt)) {
                if (progress != null) {
                    throw new HadoopIllegalArgumentException("Progress option is set multiple times");
                }
                progress = ((Options.CreateOpts.Progress)iOpt).getValue();
            }
            else {
                if (!Options.CreateOpts.CreateParent.class.isInstance(iOpt)) {
                    throw new HadoopIllegalArgumentException("Unkown CreateOpts of type " + iOpt.getClass().getName());
                }
                if (createParent != null) {
                    throw new HadoopIllegalArgumentException("CreateParent option is set multiple times");
                }
                createParent = ((Options.CreateOpts.CreateParent)iOpt).getValue();
            }
        }
        if (permission == null) {
            throw new HadoopIllegalArgumentException("no permission supplied");
        }
        final FsServerDefaults ssDef = this.getServerDefaults(f);
        if (ssDef.getBlockSize() % ssDef.getBytesPerChecksum() != 0L) {
            throw new IOException("Internal error: default blockSize is not a multiple of default bytesPerChecksum ");
        }
        if (blockSize == -1L) {
            blockSize = ssDef.getBlockSize();
        }
        final Options.ChecksumOpt defaultOpt = new Options.ChecksumOpt(ssDef.getChecksumType(), ssDef.getBytesPerChecksum());
        checksumOpt = Options.ChecksumOpt.processChecksumOpt(defaultOpt, checksumOpt, bytesPerChecksum);
        if (bufferSize == -1) {
            bufferSize = ssDef.getFileBufferSize();
        }
        if (replication == -1) {
            replication = ssDef.getReplication();
        }
        if (createParent == null) {
            createParent = false;
        }
        if (blockSize % bytesPerChecksum != 0L) {
            throw new HadoopIllegalArgumentException("blockSize should be a multiple of checksumsize");
        }
        return this.createInternal(f, createFlag, permission, bufferSize, replication, blockSize, progress, checksumOpt, createParent);
    }
    
    public abstract FSDataOutputStream createInternal(final Path p0, final EnumSet<CreateFlag> p1, final FsPermission p2, final int p3, final short p4, final long p5, final Progressable p6, final Options.ChecksumOpt p7, final boolean p8) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, UnresolvedLinkException, IOException;
    
    public abstract void mkdir(final Path p0, final FsPermission p1, final boolean p2) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, UnresolvedLinkException, IOException;
    
    public abstract boolean delete(final Path p0, final boolean p1) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException;
    
    public FSDataInputStream open(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        return this.open(f, this.getServerDefaults(f).getFileBufferSize());
    }
    
    public abstract FSDataInputStream open(final Path p0, final int p1) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException;
    
    public boolean truncate(final Path f, final long newLength) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support truncate");
    }
    
    public abstract boolean setReplication(final Path p0, final short p1) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException;
    
    public final void rename(final Path src, final Path dst, final Options.Rename... options) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnresolvedLinkException, IOException {
        boolean overwrite = false;
        if (null != options) {
            for (final Options.Rename option : options) {
                if (option == Options.Rename.OVERWRITE) {
                    overwrite = true;
                }
            }
        }
        this.renameInternal(src, dst, overwrite);
    }
    
    public abstract void renameInternal(final Path p0, final Path p1) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnresolvedLinkException, IOException;
    
    public void renameInternal(final Path src, final Path dst, final boolean overwrite) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnresolvedLinkException, IOException {
        final FileStatus srcStatus = this.getFileLinkStatus(src);
        FileStatus dstStatus;
        try {
            dstStatus = this.getFileLinkStatus(dst);
        }
        catch (IOException e) {
            dstStatus = null;
        }
        if (dstStatus != null) {
            if (dst.equals(src)) {
                throw new FileAlreadyExistsException("The source " + src + " and destination " + dst + " are the same");
            }
            if (srcStatus.isSymlink() && dst.equals(srcStatus.getSymlink())) {
                throw new FileAlreadyExistsException("Cannot rename symlink " + src + " to its target " + dst);
            }
            if (srcStatus.isDirectory() != dstStatus.isDirectory()) {
                throw new IOException("Source " + src + " and destination " + dst + " must both be directories");
            }
            if (!overwrite) {
                throw new FileAlreadyExistsException("Rename destination " + dst + " already exists.");
            }
            if (dstStatus.isDirectory()) {
                final RemoteIterator<FileStatus> list = this.listStatusIterator(dst);
                if (list != null && list.hasNext()) {
                    throw new IOException("Rename cannot overwrite non empty destination directory " + dst);
                }
            }
            this.delete(dst, false);
        }
        else {
            final Path parent = dst.getParent();
            final FileStatus parentStatus = this.getFileStatus(parent);
            if (parentStatus.isFile()) {
                throw new ParentNotDirectoryException("Rename destination parent " + parent + " is a file.");
            }
        }
        this.renameInternal(src, dst);
    }
    
    public boolean supportsSymlinks() {
        return false;
    }
    
    public void createSymlink(final Path target, final Path link, final boolean createParent) throws IOException, UnresolvedLinkException {
        throw new IOException("File system does not support symlinks");
    }
    
    public Path getLinkTarget(final Path f) throws IOException {
        throw new AssertionError((Object)("Implementation Error: " + this.getClass() + " that threw an UnresolvedLinkException, causing this method to be called, needs to override this method."));
    }
    
    public abstract void setPermission(final Path p0, final FsPermission p1) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException;
    
    public abstract void setOwner(final Path p0, final String p1, final String p2) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException;
    
    public abstract void setTimes(final Path p0, final long p1, final long p2) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException;
    
    public abstract FileChecksum getFileChecksum(final Path p0) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException;
    
    public abstract FileStatus getFileStatus(final Path p0) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException;
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "Hive" })
    public void access(final Path path, final FsAction mode) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        FileSystem.checkAccessPermissions(this.getFileStatus(path), mode);
    }
    
    public FileStatus getFileLinkStatus(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        return this.getFileStatus(f);
    }
    
    public abstract BlockLocation[] getFileBlockLocations(final Path p0, final long p1, final long p2) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException;
    
    public FsStatus getFsStatus(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        return this.getFsStatus();
    }
    
    public abstract FsStatus getFsStatus() throws AccessControlException, FileNotFoundException, IOException;
    
    public RemoteIterator<FileStatus> listStatusIterator(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        return new RemoteIterator<FileStatus>() {
            private int i = 0;
            private FileStatus[] statusList = AbstractFileSystem.this.listStatus(f);
            
            @Override
            public boolean hasNext() {
                return this.i < this.statusList.length;
            }
            
            @Override
            public FileStatus next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return this.statusList[this.i++];
            }
        };
    }
    
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        return new RemoteIterator<LocatedFileStatus>() {
            private RemoteIterator<FileStatus> itor = AbstractFileSystem.this.listStatusIterator(f);
            
            @Override
            public boolean hasNext() throws IOException {
                return this.itor.hasNext();
            }
            
            @Override
            public LocatedFileStatus next() throws IOException {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No more entry in " + f);
                }
                final FileStatus result = this.itor.next();
                BlockLocation[] locs = null;
                if (result.isFile()) {
                    locs = AbstractFileSystem.this.getFileBlockLocations(result.getPath(), 0L, result.getLen());
                }
                return new LocatedFileStatus(result, locs);
            }
        };
    }
    
    public abstract FileStatus[] listStatus(final Path p0) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException;
    
    public RemoteIterator<Path> listCorruptFileBlocks(final Path path) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " does not support listCorruptFileBlocks");
    }
    
    public abstract void setVerifyChecksum(final boolean p0) throws AccessControlException, IOException;
    
    public String getCanonicalServiceName() {
        return SecurityUtil.buildDTServiceName(this.getUri(), this.getUriDefaultPort());
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
    public List<Token<?>> getDelegationTokens(final String renewer) throws IOException {
        return new ArrayList<Token<?>>(0);
    }
    
    public void modifyAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support modifyAclEntries");
    }
    
    public void removeAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support removeAclEntries");
    }
    
    public void removeDefaultAcl(final Path path) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support removeDefaultAcl");
    }
    
    public void removeAcl(final Path path) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support removeAcl");
    }
    
    public void setAcl(final Path path, final List<AclEntry> aclSpec) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support setAcl");
    }
    
    public AclStatus getAclStatus(final Path path) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support getAclStatus");
    }
    
    public void setXAttr(final Path path, final String name, final byte[] value) throws IOException {
        this.setXAttr(path, name, value, EnumSet.of(XAttrSetFlag.CREATE, XAttrSetFlag.REPLACE));
    }
    
    public void setXAttr(final Path path, final String name, final byte[] value, final EnumSet<XAttrSetFlag> flag) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support setXAttr");
    }
    
    public byte[] getXAttr(final Path path, final String name) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support getXAttr");
    }
    
    public Map<String, byte[]> getXAttrs(final Path path) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support getXAttrs");
    }
    
    public Map<String, byte[]> getXAttrs(final Path path, final List<String> names) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support getXAttrs");
    }
    
    public List<String> listXAttrs(final Path path) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support listXAttrs");
    }
    
    public void removeXAttr(final Path path, final String name) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support removeXAttr");
    }
    
    public Path createSnapshot(final Path path, final String snapshotName) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support createSnapshot");
    }
    
    public void renameSnapshot(final Path path, final String snapshotOldName, final String snapshotNewName) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support renameSnapshot");
    }
    
    public void deleteSnapshot(final Path snapshotDir, final String snapshotName) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support deleteSnapshot");
    }
    
    public void setStoragePolicy(final Path path, final String policyName) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support setStoragePolicy");
    }
    
    public void unsetStoragePolicy(final Path src) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support unsetStoragePolicy");
    }
    
    public BlockStoragePolicySpi getStoragePolicy(final Path src) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support getStoragePolicy");
    }
    
    public Collection<? extends BlockStoragePolicySpi> getAllStoragePolicies() throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support getAllStoragePolicies");
    }
    
    @Override
    public int hashCode() {
        return this.myUri.hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other instanceof AbstractFileSystem && this.myUri.equals(((AbstractFileSystem)other).myUri);
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbstractFileSystem.class);
        STATISTICS_TABLE = new HashMap<URI, FileSystem.Statistics>();
        CONSTRUCTOR_CACHE = new ConcurrentHashMap<Class<?>, Constructor<?>>();
        URI_CONFIG_ARGS = new Class[] { URI.class, Configuration.class };
    }
}
