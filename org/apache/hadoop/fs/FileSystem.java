// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.io.MultipleIOException;
import org.apache.hadoop.util.ShutdownHookManager;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.IdentityHashMap;
import org.slf4j.LoggerFactory;
import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import org.apache.htrace.core.TraceScope;
import org.apache.htrace.core.Tracer;
import org.apache.hadoop.util.ReflectionUtils;
import java.util.ServiceConfigurationError;
import org.apache.hadoop.util.ClassUtil;
import java.util.ServiceLoader;
import java.util.Collection;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.AclEntry;
import java.util.List;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.fs.permission.FsAction;
import java.util.Stack;
import java.util.NoSuchElementException;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.FileNotFoundException;
import java.util.EnumSet;
import org.apache.hadoop.fs.permission.FsCreateModes;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.util.DataChecksum;
import java.util.TreeSet;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.SecurityUtil;
import java.net.URISyntaxException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.UserGroupInformation;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import java.util.Set;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.DelegationTokenIssuer;
import java.io.Closeable;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class FileSystem extends Configured implements Closeable, DelegationTokenIssuer
{
    public static final String FS_DEFAULT_NAME_KEY = "fs.defaultFS";
    public static final String DEFAULT_FS = "file:///";
    @InterfaceAudience.Private
    public static final Log LOG;
    private static final Logger LOGGER;
    public static final int SHUTDOWN_HOOK_PRIORITY = 10;
    public static final String TRASH_PREFIX = ".Trash";
    public static final String USER_HOME_PREFIX = "/user";
    static final Cache CACHE;
    private Cache.Key key;
    private static final Map<Class<? extends FileSystem>, Statistics> statisticsTable;
    protected Statistics statistics;
    private final Set<Path> deleteOnExit;
    boolean resolveSymlinks;
    private static final PathFilter DEFAULT_FILTER;
    private static volatile boolean FILE_SYSTEMS_LOADED;
    private static final Map<String, Class<? extends FileSystem>> SERVICE_FILE_SYSTEMS;
    private static boolean symlinksEnabled;
    
    @VisibleForTesting
    static void addFileSystemForTesting(final URI uri, final Configuration conf, final FileSystem fs) throws IOException {
        FileSystem.CACHE.map.put(new Cache.Key(uri, conf), fs);
    }
    
    public static FileSystem get(final URI uri, final Configuration conf, final String user) throws IOException, InterruptedException {
        final String ticketCachePath = conf.get("hadoop.security.kerberos.ticket.cache.path");
        final UserGroupInformation ugi = UserGroupInformation.getBestUGI(ticketCachePath, user);
        return ugi.doAs((PrivilegedExceptionAction<FileSystem>)new PrivilegedExceptionAction<FileSystem>() {
            @Override
            public FileSystem run() throws IOException {
                return FileSystem.get(uri, conf);
            }
        });
    }
    
    public static FileSystem get(final Configuration conf) throws IOException {
        return get(getDefaultUri(conf), conf);
    }
    
    public static URI getDefaultUri(final Configuration conf) {
        final URI uri = URI.create(fixName(conf.get("fs.defaultFS", "file:///")));
        if (uri.getScheme() == null) {
            throw new IllegalArgumentException("No scheme in default FS: " + uri);
        }
        return uri;
    }
    
    public static void setDefaultUri(final Configuration conf, final URI uri) {
        conf.set("fs.defaultFS", uri.toString());
    }
    
    public static void setDefaultUri(final Configuration conf, final String uri) {
        setDefaultUri(conf, URI.create(fixName(uri)));
    }
    
    public void initialize(final URI name, final Configuration conf) throws IOException {
        String scheme;
        if (name.getScheme() == null || name.getScheme().isEmpty()) {
            scheme = getDefaultUri(conf).getScheme();
        }
        else {
            scheme = name.getScheme();
        }
        this.statistics = getStatistics(scheme, this.getClass());
        this.resolveSymlinks = conf.getBoolean("fs.client.resolve.remote.symlinks", true);
    }
    
    public String getScheme() {
        throw new UnsupportedOperationException("Not implemented by the " + this.getClass().getSimpleName() + " FileSystem implementation");
    }
    
    public abstract URI getUri();
    
    protected URI getCanonicalUri() {
        return this.canonicalizeUri(this.getUri());
    }
    
    protected URI canonicalizeUri(URI uri) {
        if (uri.getPort() == -1 && this.getDefaultPort() > 0) {
            try {
                uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), this.getDefaultPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
            }
            catch (URISyntaxException e) {
                throw new AssertionError((Object)("Valid URI became unparseable: " + uri));
            }
        }
        return uri;
    }
    
    protected int getDefaultPort() {
        return 0;
    }
    
    protected static FileSystem getFSofPath(final Path absOrFqPath, final Configuration conf) throws UnsupportedFileSystemException, IOException {
        absOrFqPath.checkNotSchemeWithRelative();
        absOrFqPath.checkNotRelative();
        return get(absOrFqPath.toUri(), conf);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    @Override
    public String getCanonicalServiceName() {
        return (this.getChildFileSystems() == null) ? SecurityUtil.buildDTServiceName(this.getUri(), this.getDefaultPort()) : null;
    }
    
    @Deprecated
    public String getName() {
        return this.getUri().toString();
    }
    
    @Deprecated
    public static FileSystem getNamed(final String name, final Configuration conf) throws IOException {
        return get(URI.create(fixName(name)), conf);
    }
    
    private static String fixName(String name) {
        if (name.equals("local")) {
            FileSystem.LOGGER.warn("\"local\" is a deprecated filesystem name. Use \"file:///\" instead.");
            name = "file:///";
        }
        else if (name.indexOf(47) == -1) {
            FileSystem.LOGGER.warn("\"" + name + "\" is a deprecated filesystem name. Use \"hdfs://" + name + "/\" instead.");
            name = "hdfs://" + name;
        }
        return name;
    }
    
    public static LocalFileSystem getLocal(final Configuration conf) throws IOException {
        return (LocalFileSystem)get(LocalFileSystem.NAME, conf);
    }
    
    public static FileSystem get(final URI uri, final Configuration conf) throws IOException {
        final String scheme = uri.getScheme();
        final String authority = uri.getAuthority();
        if (scheme == null && authority == null) {
            return get(conf);
        }
        if (scheme != null && authority == null) {
            final URI defaultUri = getDefaultUri(conf);
            if (scheme.equals(defaultUri.getScheme()) && defaultUri.getAuthority() != null) {
                return get(defaultUri, conf);
            }
        }
        final String disableCacheName = String.format("fs.%s.impl.disable.cache", scheme);
        if (conf.getBoolean(disableCacheName, false)) {
            FileSystem.LOGGER.debug("Bypassing cache to create filesystem {}", uri);
            return createFileSystem(uri, conf);
        }
        return FileSystem.CACHE.get(uri, conf);
    }
    
    public static FileSystem newInstance(final URI uri, final Configuration conf, final String user) throws IOException, InterruptedException {
        final String ticketCachePath = conf.get("hadoop.security.kerberos.ticket.cache.path");
        final UserGroupInformation ugi = UserGroupInformation.getBestUGI(ticketCachePath, user);
        return ugi.doAs((PrivilegedExceptionAction<FileSystem>)new PrivilegedExceptionAction<FileSystem>() {
            @Override
            public FileSystem run() throws IOException {
                return FileSystem.newInstance(uri, conf);
            }
        });
    }
    
    public static FileSystem newInstance(final URI uri, final Configuration config) throws IOException {
        final String scheme = uri.getScheme();
        final String authority = uri.getAuthority();
        if (scheme == null) {
            return newInstance(config);
        }
        if (authority == null) {
            final URI defaultUri = getDefaultUri(config);
            if (scheme.equals(defaultUri.getScheme()) && defaultUri.getAuthority() != null) {
                return newInstance(defaultUri, config);
            }
        }
        return FileSystem.CACHE.getUnique(uri, config);
    }
    
    public static FileSystem newInstance(final Configuration conf) throws IOException {
        return newInstance(getDefaultUri(conf), conf);
    }
    
    public static LocalFileSystem newInstanceLocal(final Configuration conf) throws IOException {
        return (LocalFileSystem)newInstance(LocalFileSystem.NAME, conf);
    }
    
    public static void closeAll() throws IOException {
        FileSystem.CACHE.closeAll();
    }
    
    public static void closeAllForUGI(final UserGroupInformation ugi) throws IOException {
        FileSystem.CACHE.closeAll(ugi);
    }
    
    public Path makeQualified(final Path path) {
        this.checkPath(path);
        return path.makeQualified(this.getUri(), this.getWorkingDirectory());
    }
    
    @InterfaceAudience.Private
    @Override
    public Token<?> getDelegationToken(final String renewer) throws IOException {
        return null;
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS" })
    @VisibleForTesting
    public FileSystem[] getChildFileSystems() {
        return null;
    }
    
    @InterfaceAudience.Private
    @Override
    public DelegationTokenIssuer[] getAdditionalTokenIssuers() throws IOException {
        return this.getChildFileSystems();
    }
    
    public static FSDataOutputStream create(final FileSystem fs, final Path file, final FsPermission permission) throws IOException {
        final FSDataOutputStream out = fs.create(file);
        fs.setPermission(file, permission);
        return out;
    }
    
    public static boolean mkdirs(final FileSystem fs, final Path dir, final FsPermission permission) throws IOException {
        final boolean result = fs.mkdirs(dir);
        fs.setPermission(dir, permission);
        return result;
    }
    
    protected FileSystem() {
        super(null);
        this.deleteOnExit = new TreeSet<Path>();
    }
    
    protected void checkPath(final Path path) {
        URI uri = path.toUri();
        final String thatScheme = uri.getScheme();
        if (thatScheme == null) {
            return;
        }
        final URI thisUri = this.getCanonicalUri();
        final String thisScheme = thisUri.getScheme();
        if (thisScheme.equalsIgnoreCase(thatScheme)) {
            final String thisAuthority = thisUri.getAuthority();
            String thatAuthority = uri.getAuthority();
            if (thatAuthority == null && thisAuthority != null) {
                final URI defaultUri = getDefaultUri(this.getConf());
                if (thisScheme.equalsIgnoreCase(defaultUri.getScheme())) {
                    uri = defaultUri;
                }
                else {
                    uri = null;
                }
            }
            if (uri != null) {
                uri = this.canonicalizeUri(uri);
                thatAuthority = uri.getAuthority();
                if (thisAuthority == thatAuthority || (thisAuthority != null && thisAuthority.equalsIgnoreCase(thatAuthority))) {
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Wrong FS: " + path + ", expected: " + this.getUri());
    }
    
    public BlockLocation[] getFileBlockLocations(final FileStatus file, final long start, final long len) throws IOException {
        if (file == null) {
            return null;
        }
        if (start < 0L || len < 0L) {
            throw new IllegalArgumentException("Invalid start or len parameter");
        }
        if (file.getLen() <= start) {
            return new BlockLocation[0];
        }
        final String[] name = { "localhost:9866" };
        final String[] host = { "localhost" };
        return new BlockLocation[] { new BlockLocation(name, host, 0L, file.getLen()) };
    }
    
    public BlockLocation[] getFileBlockLocations(final Path p, final long start, final long len) throws IOException {
        if (p == null) {
            throw new NullPointerException();
        }
        final FileStatus file = this.getFileStatus(p);
        return this.getFileBlockLocations(file, start, len);
    }
    
    @Deprecated
    public FsServerDefaults getServerDefaults() throws IOException {
        final Configuration config = this.getConf();
        return new FsServerDefaults(this.getDefaultBlockSize(), config.getInt("io.bytes.per.checksum", 512), 65536, this.getDefaultReplication(), config.getInt("io.file.buffer.size", 4096), false, 0L, DataChecksum.Type.CRC32, "");
    }
    
    public FsServerDefaults getServerDefaults(final Path p) throws IOException {
        return this.getServerDefaults();
    }
    
    public Path resolvePath(final Path p) throws IOException {
        this.checkPath(p);
        return this.getFileStatus(p).getPath();
    }
    
    public abstract FSDataInputStream open(final Path p0, final int p1) throws IOException;
    
    public FSDataInputStream open(final Path f) throws IOException {
        return this.open(f, this.getConf().getInt("io.file.buffer.size", 4096));
    }
    
    public FSDataInputStream open(final PathHandle fd) throws IOException {
        return this.open(fd, this.getConf().getInt("io.file.buffer.size", 4096));
    }
    
    public FSDataInputStream open(final PathHandle fd, final int bufferSize) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public final PathHandle getPathHandle(final FileStatus stat, final Options.HandleOpt... opt) {
        if (null == opt || 0 == opt.length) {
            return this.createPathHandle(stat, Options.HandleOpt.path());
        }
        return this.createPathHandle(stat, opt);
    }
    
    protected PathHandle createPathHandle(final FileStatus stat, final Options.HandleOpt... opt) {
        throw new UnsupportedOperationException();
    }
    
    public FSDataOutputStream create(final Path f) throws IOException {
        return this.create(f, true);
    }
    
    public FSDataOutputStream create(final Path f, final boolean overwrite) throws IOException {
        return this.create(f, overwrite, this.getConf().getInt("io.file.buffer.size", 4096), this.getDefaultReplication(f), this.getDefaultBlockSize(f));
    }
    
    public FSDataOutputStream create(final Path f, final Progressable progress) throws IOException {
        return this.create(f, true, this.getConf().getInt("io.file.buffer.size", 4096), this.getDefaultReplication(f), this.getDefaultBlockSize(f), progress);
    }
    
    public FSDataOutputStream create(final Path f, final short replication) throws IOException {
        return this.create(f, true, this.getConf().getInt("io.file.buffer.size", 4096), replication, this.getDefaultBlockSize(f));
    }
    
    public FSDataOutputStream create(final Path f, final short replication, final Progressable progress) throws IOException {
        return this.create(f, true, this.getConf().getInt("io.file.buffer.size", 4096), replication, this.getDefaultBlockSize(f), progress);
    }
    
    public FSDataOutputStream create(final Path f, final boolean overwrite, final int bufferSize) throws IOException {
        return this.create(f, overwrite, bufferSize, this.getDefaultReplication(f), this.getDefaultBlockSize(f));
    }
    
    public FSDataOutputStream create(final Path f, final boolean overwrite, final int bufferSize, final Progressable progress) throws IOException {
        return this.create(f, overwrite, bufferSize, this.getDefaultReplication(f), this.getDefaultBlockSize(f), progress);
    }
    
    public FSDataOutputStream create(final Path f, final boolean overwrite, final int bufferSize, final short replication, final long blockSize) throws IOException {
        return this.create(f, overwrite, bufferSize, replication, blockSize, null);
    }
    
    public FSDataOutputStream create(final Path f, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return this.create(f, FsCreateModes.applyUMask(FsPermission.getFileDefault(), FsPermission.getUMask(this.getConf())), overwrite, bufferSize, replication, blockSize, progress);
    }
    
    public abstract FSDataOutputStream create(final Path p0, final FsPermission p1, final boolean p2, final int p3, final short p4, final long p5, final Progressable p6) throws IOException;
    
    public FSDataOutputStream create(final Path f, final FsPermission permission, final EnumSet<CreateFlag> flags, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return this.create(f, permission, flags, bufferSize, replication, blockSize, progress, null);
    }
    
    public FSDataOutputStream create(final Path f, final FsPermission permission, final EnumSet<CreateFlag> flags, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Options.ChecksumOpt checksumOpt) throws IOException {
        return this.create(f, permission, flags.contains(CreateFlag.OVERWRITE), bufferSize, replication, blockSize, progress);
    }
    
    @Deprecated
    protected FSDataOutputStream primitiveCreate(final Path f, final FsPermission absolutePermission, final EnumSet<CreateFlag> flag, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Options.ChecksumOpt checksumOpt) throws IOException {
        final boolean pathExists = this.exists(f);
        CreateFlag.validate(f, pathExists, flag);
        if (pathExists && flag.contains(CreateFlag.APPEND)) {
            return this.append(f, bufferSize, progress);
        }
        return this.create(f, absolutePermission, flag.contains(CreateFlag.OVERWRITE), bufferSize, replication, blockSize, progress);
    }
    
    @Deprecated
    protected boolean primitiveMkdir(final Path f, final FsPermission absolutePermission) throws IOException {
        return this.mkdirs(f, absolutePermission);
    }
    
    @Deprecated
    protected void primitiveMkdir(final Path f, final FsPermission absolutePermission, final boolean createParent) throws IOException {
        if (!createParent) {
            final FileStatus stat = this.getFileStatus(f.getParent());
            if (stat == null) {
                throw new FileNotFoundException("Missing parent:" + f);
            }
            if (!stat.isDirectory()) {
                throw new ParentNotDirectoryException("parent is not a dir");
            }
        }
        if (!this.mkdirs(f, absolutePermission)) {
            throw new IOException("mkdir of " + f + " failed");
        }
    }
    
    public FSDataOutputStream createNonRecursive(final Path f, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return this.createNonRecursive(f, FsPermission.getFileDefault(), overwrite, bufferSize, replication, blockSize, progress);
    }
    
    public FSDataOutputStream createNonRecursive(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return this.createNonRecursive(f, permission, overwrite ? EnumSet.of(CreateFlag.CREATE, CreateFlag.OVERWRITE) : EnumSet.of(CreateFlag.CREATE), bufferSize, replication, blockSize, progress);
    }
    
    public FSDataOutputStream createNonRecursive(final Path f, final FsPermission permission, final EnumSet<CreateFlag> flags, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        throw new IOException("createNonRecursive unsupported for this filesystem " + this.getClass());
    }
    
    public boolean createNewFile(final Path f) throws IOException {
        if (this.exists(f)) {
            return false;
        }
        this.create(f, false, this.getConf().getInt("io.file.buffer.size", 4096)).close();
        return true;
    }
    
    public FSDataOutputStream append(final Path f) throws IOException {
        return this.append(f, this.getConf().getInt("io.file.buffer.size", 4096), null);
    }
    
    public FSDataOutputStream append(final Path f, final int bufferSize) throws IOException {
        return this.append(f, bufferSize, null);
    }
    
    public abstract FSDataOutputStream append(final Path p0, final int p1, final Progressable p2) throws IOException;
    
    public void concat(final Path trg, final Path[] psrcs) throws IOException {
        throw new UnsupportedOperationException("Not implemented by the " + this.getClass().getSimpleName() + " FileSystem implementation");
    }
    
    @Deprecated
    public short getReplication(final Path src) throws IOException {
        return this.getFileStatus(src).getReplication();
    }
    
    public boolean setReplication(final Path src, final short replication) throws IOException {
        return true;
    }
    
    public abstract boolean rename(final Path p0, final Path p1) throws IOException;
    
    @Deprecated
    protected void rename(final Path src, final Path dst, final Options.Rename... options) throws IOException {
        final FileStatus srcStatus = this.getFileLinkStatus(src);
        if (srcStatus == null) {
            throw new FileNotFoundException("rename source " + src + " not found.");
        }
        boolean overwrite = false;
        if (null != options) {
            for (final Options.Rename option : options) {
                if (option == Options.Rename.OVERWRITE) {
                    overwrite = true;
                }
            }
        }
        FileStatus dstStatus;
        try {
            dstStatus = this.getFileLinkStatus(dst);
        }
        catch (IOException e) {
            dstStatus = null;
        }
        if (dstStatus != null) {
            if (srcStatus.isDirectory() != dstStatus.isDirectory()) {
                throw new IOException("Source " + src + " Destination " + dst + " both should be either file or directory");
            }
            if (!overwrite) {
                throw new FileAlreadyExistsException("rename destination " + dst + " already exists.");
            }
            if (dstStatus.isDirectory()) {
                final FileStatus[] list = this.listStatus(dst);
                if (list != null && list.length != 0) {
                    throw new IOException("rename cannot overwrite non empty destination directory " + dst);
                }
            }
            this.delete(dst, false);
        }
        else {
            final Path parent = dst.getParent();
            final FileStatus parentStatus = this.getFileStatus(parent);
            if (parentStatus == null) {
                throw new FileNotFoundException("rename destination parent " + parent + " not found.");
            }
            if (!parentStatus.isDirectory()) {
                throw new ParentNotDirectoryException("rename destination parent " + parent + " is a file.");
            }
        }
        if (!this.rename(src, dst)) {
            throw new IOException("rename from " + src + " to " + dst + " failed.");
        }
    }
    
    public boolean truncate(final Path f, final long newLength) throws IOException {
        throw new UnsupportedOperationException("Not implemented by the " + this.getClass().getSimpleName() + " FileSystem implementation");
    }
    
    @Deprecated
    public boolean delete(final Path f) throws IOException {
        return this.delete(f, true);
    }
    
    public abstract boolean delete(final Path p0, final boolean p1) throws IOException;
    
    public boolean deleteOnExit(final Path f) throws IOException {
        if (!this.exists(f)) {
            return false;
        }
        synchronized (this.deleteOnExit) {
            this.deleteOnExit.add(f);
        }
        return true;
    }
    
    public boolean cancelDeleteOnExit(final Path f) {
        synchronized (this.deleteOnExit) {
            return this.deleteOnExit.remove(f);
        }
    }
    
    protected void processDeleteOnExit() {
        synchronized (this.deleteOnExit) {
            final Iterator<Path> iter = this.deleteOnExit.iterator();
            while (iter.hasNext()) {
                final Path path = iter.next();
                try {
                    if (this.exists(path)) {
                        this.delete(path, true);
                    }
                }
                catch (IOException e) {
                    FileSystem.LOGGER.info("Ignoring failure to deleteOnExit for path {}", path);
                }
                iter.remove();
            }
        }
    }
    
    public boolean exists(final Path f) throws IOException {
        try {
            return this.getFileStatus(f) != null;
        }
        catch (FileNotFoundException e) {
            return false;
        }
    }
    
    @Deprecated
    public boolean isDirectory(final Path f) throws IOException {
        try {
            return this.getFileStatus(f).isDirectory();
        }
        catch (FileNotFoundException e) {
            return false;
        }
    }
    
    @Deprecated
    public boolean isFile(final Path f) throws IOException {
        try {
            return this.getFileStatus(f).isFile();
        }
        catch (FileNotFoundException e) {
            return false;
        }
    }
    
    @Deprecated
    public long getLength(final Path f) throws IOException {
        return this.getFileStatus(f).getLen();
    }
    
    public ContentSummary getContentSummary(final Path f) throws IOException {
        final FileStatus status = this.getFileStatus(f);
        if (status.isFile()) {
            final long length = status.getLen();
            return new ContentSummary.Builder().length(length).fileCount(1L).directoryCount(0L).spaceConsumed(length).build();
        }
        final long[] summary = { 0L, 0L, 1L };
        for (final FileStatus s : this.listStatus(f)) {
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
    
    public QuotaUsage getQuotaUsage(final Path f) throws IOException {
        return this.getContentSummary(f);
    }
    
    public abstract FileStatus[] listStatus(final Path p0) throws FileNotFoundException, IOException;
    
    @InterfaceAudience.Private
    protected DirectoryEntries listStatusBatch(final Path f, final byte[] token) throws FileNotFoundException, IOException {
        final FileStatus[] listing = this.listStatus(f);
        return new DirectoryEntries(listing, null, false);
    }
    
    private void listStatus(final ArrayList<FileStatus> results, final Path f, final PathFilter filter) throws FileNotFoundException, IOException {
        final FileStatus[] listing = this.listStatus(f);
        Preconditions.checkNotNull(listing, (Object)"listStatus should not return NULL");
        for (int i = 0; i < listing.length; ++i) {
            if (filter.accept(listing[i].getPath())) {
                results.add(listing[i]);
            }
        }
    }
    
    public RemoteIterator<Path> listCorruptFileBlocks(final Path path) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " does not support listCorruptFileBlocks");
    }
    
    public FileStatus[] listStatus(final Path f, final PathFilter filter) throws FileNotFoundException, IOException {
        final ArrayList<FileStatus> results = new ArrayList<FileStatus>();
        this.listStatus(results, f, filter);
        return results.toArray(new FileStatus[results.size()]);
    }
    
    public FileStatus[] listStatus(final Path[] files) throws FileNotFoundException, IOException {
        return this.listStatus(files, FileSystem.DEFAULT_FILTER);
    }
    
    public FileStatus[] listStatus(final Path[] files, final PathFilter filter) throws FileNotFoundException, IOException {
        final ArrayList<FileStatus> results = new ArrayList<FileStatus>();
        for (int i = 0; i < files.length; ++i) {
            this.listStatus(results, files[i], filter);
        }
        return results.toArray(new FileStatus[results.size()]);
    }
    
    public FileStatus[] globStatus(final Path pathPattern) throws IOException {
        return new Globber(this, pathPattern, FileSystem.DEFAULT_FILTER).glob();
    }
    
    public FileStatus[] globStatus(final Path pathPattern, final PathFilter filter) throws IOException {
        return new Globber(this, pathPattern, filter).glob();
    }
    
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws FileNotFoundException, IOException {
        return this.listLocatedStatus(f, FileSystem.DEFAULT_FILTER);
    }
    
    protected RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f, final PathFilter filter) throws FileNotFoundException, IOException {
        return new RemoteIterator<LocatedFileStatus>() {
            private final FileStatus[] stats = FileSystem.this.listStatus(f, filter);
            private int i = 0;
            
            @Override
            public boolean hasNext() {
                return this.i < this.stats.length;
            }
            
            @Override
            public LocatedFileStatus next() throws IOException {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No more entries in " + f);
                }
                final FileStatus result = this.stats[this.i++];
                final BlockLocation[] locs = (BlockLocation[])(result.isFile() ? FileSystem.this.getFileBlockLocations(result, 0L, result.getLen()) : null);
                return new LocatedFileStatus(result, locs);
            }
        };
    }
    
    public RemoteIterator<FileStatus> listStatusIterator(final Path p) throws FileNotFoundException, IOException {
        return new DirListingIterator<FileStatus>(p);
    }
    
    public RemoteIterator<LocatedFileStatus> listFiles(final Path f, final boolean recursive) throws FileNotFoundException, IOException {
        return new RemoteIterator<LocatedFileStatus>() {
            private Stack<RemoteIterator<LocatedFileStatus>> itors = new Stack<RemoteIterator<LocatedFileStatus>>();
            private RemoteIterator<LocatedFileStatus> curItor = FileSystem.this.listLocatedStatus(f);
            private LocatedFileStatus curFile;
            
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
                else if (recursive) {
                    this.itors.push(this.curItor);
                    this.curItor = FileSystem.this.listLocatedStatus(stat.getPath());
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
    
    public Path getHomeDirectory() {
        return this.makeQualified(new Path("/user/" + System.getProperty("user.name")));
    }
    
    public abstract void setWorkingDirectory(final Path p0);
    
    public abstract Path getWorkingDirectory();
    
    protected Path getInitialWorkingDirectory() {
        return null;
    }
    
    public boolean mkdirs(final Path f) throws IOException {
        return this.mkdirs(f, FsPermission.getDirDefault());
    }
    
    public abstract boolean mkdirs(final Path p0, final FsPermission p1) throws IOException;
    
    public void copyFromLocalFile(final Path src, final Path dst) throws IOException {
        this.copyFromLocalFile(false, src, dst);
    }
    
    public void moveFromLocalFile(final Path[] srcs, final Path dst) throws IOException {
        this.copyFromLocalFile(true, true, srcs, dst);
    }
    
    public void moveFromLocalFile(final Path src, final Path dst) throws IOException {
        this.copyFromLocalFile(true, src, dst);
    }
    
    public void copyFromLocalFile(final boolean delSrc, final Path src, final Path dst) throws IOException {
        this.copyFromLocalFile(delSrc, true, src, dst);
    }
    
    public void copyFromLocalFile(final boolean delSrc, final boolean overwrite, final Path[] srcs, final Path dst) throws IOException {
        final Configuration conf = this.getConf();
        FileUtil.copy(getLocal(conf), srcs, this, dst, delSrc, overwrite, conf);
    }
    
    public void copyFromLocalFile(final boolean delSrc, final boolean overwrite, final Path src, final Path dst) throws IOException {
        final Configuration conf = this.getConf();
        FileUtil.copy(getLocal(conf), src, this, dst, delSrc, overwrite, conf);
    }
    
    public void copyToLocalFile(final Path src, final Path dst) throws IOException {
        this.copyToLocalFile(false, src, dst);
    }
    
    public void moveToLocalFile(final Path src, final Path dst) throws IOException {
        this.copyToLocalFile(true, src, dst);
    }
    
    public void copyToLocalFile(final boolean delSrc, final Path src, final Path dst) throws IOException {
        this.copyToLocalFile(delSrc, src, dst, false);
    }
    
    public void copyToLocalFile(final boolean delSrc, final Path src, final Path dst, final boolean useRawLocalFileSystem) throws IOException {
        final Configuration conf = this.getConf();
        FileSystem local = null;
        if (useRawLocalFileSystem) {
            local = getLocal(conf).getRawFileSystem();
        }
        else {
            local = getLocal(conf);
        }
        FileUtil.copy(this, src, local, dst, delSrc, conf);
    }
    
    public Path startLocalOutput(final Path fsOutputFile, final Path tmpLocalFile) throws IOException {
        return tmpLocalFile;
    }
    
    public void completeLocalOutput(final Path fsOutputFile, final Path tmpLocalFile) throws IOException {
        this.moveFromLocalFile(tmpLocalFile, fsOutputFile);
    }
    
    @Override
    public void close() throws IOException {
        this.processDeleteOnExit();
        FileSystem.CACHE.remove(this.key, this);
    }
    
    public long getUsed() throws IOException {
        final Path path = new Path("/");
        return this.getUsed(path);
    }
    
    public long getUsed(final Path path) throws IOException {
        return this.getContentSummary(path).getLength();
    }
    
    @Deprecated
    public long getBlockSize(final Path f) throws IOException {
        return this.getFileStatus(f).getBlockSize();
    }
    
    @Deprecated
    public long getDefaultBlockSize() {
        return this.getConf().getLong("fs.local.block.size", 33554432L);
    }
    
    public long getDefaultBlockSize(final Path f) {
        return this.getDefaultBlockSize();
    }
    
    @Deprecated
    public short getDefaultReplication() {
        return 1;
    }
    
    public short getDefaultReplication(final Path path) {
        return this.getDefaultReplication();
    }
    
    public abstract FileStatus getFileStatus(final Path p0) throws IOException;
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "Hive" })
    public void access(final Path path, final FsAction mode) throws AccessControlException, FileNotFoundException, IOException {
        checkAccessPermissions(this.getFileStatus(path), mode);
    }
    
    @InterfaceAudience.Private
    static void checkAccessPermissions(final FileStatus stat, final FsAction mode) throws AccessControlException, IOException {
        final FsPermission perm = stat.getPermission();
        final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        final String user = ugi.getShortUserName();
        if (user.equals(stat.getOwner())) {
            if (perm.getUserAction().implies(mode)) {
                return;
            }
        }
        else if (ugi.getGroups().contains(stat.getGroup())) {
            if (perm.getGroupAction().implies(mode)) {
                return;
            }
        }
        else if (perm.getOtherAction().implies(mode)) {
            return;
        }
        throw new AccessControlException(String.format("Permission denied: user=%s, path=\"%s\":%s:%s:%s%s", user, stat.getPath(), stat.getOwner(), stat.getGroup(), stat.isDirectory() ? "d" : "-", perm));
    }
    
    protected Path fixRelativePart(final Path p) {
        if (p.isUriPathAbsolute()) {
            return p;
        }
        return new Path(this.getWorkingDirectory(), p);
    }
    
    public void createSymlink(final Path target, final Path link, final boolean createParent) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
        throw new UnsupportedOperationException("Filesystem does not support symlinks!");
    }
    
    public FileStatus getFileLinkStatus(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        return this.getFileStatus(f);
    }
    
    public boolean supportsSymlinks() {
        return false;
    }
    
    public Path getLinkTarget(final Path f) throws IOException {
        throw new UnsupportedOperationException("Filesystem does not support symlinks!");
    }
    
    protected Path resolveLink(final Path f) throws IOException {
        throw new UnsupportedOperationException("Filesystem does not support symlinks!");
    }
    
    public FileChecksum getFileChecksum(final Path f) throws IOException {
        return this.getFileChecksum(f, Long.MAX_VALUE);
    }
    
    public FileChecksum getFileChecksum(final Path f, final long length) throws IOException {
        return null;
    }
    
    public void setVerifyChecksum(final boolean verifyChecksum) {
    }
    
    public void setWriteChecksum(final boolean writeChecksum) {
    }
    
    public FsStatus getStatus() throws IOException {
        return this.getStatus(null);
    }
    
    public FsStatus getStatus(final Path p) throws IOException {
        return new FsStatus(Long.MAX_VALUE, 0L, Long.MAX_VALUE);
    }
    
    public void setPermission(final Path p, final FsPermission permission) throws IOException {
    }
    
    public void setOwner(final Path p, final String username, final String groupname) throws IOException {
    }
    
    public void setTimes(final Path p, final long mtime, final long atime) throws IOException {
    }
    
    public final Path createSnapshot(final Path path) throws IOException {
        return this.createSnapshot(path, null);
    }
    
    public Path createSnapshot(final Path path, final String snapshotName) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support createSnapshot");
    }
    
    public void renameSnapshot(final Path path, final String snapshotOldName, final String snapshotNewName) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support renameSnapshot");
    }
    
    public void deleteSnapshot(final Path path, final String snapshotName) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support deleteSnapshot");
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
    
    public void setStoragePolicy(final Path src, final String policyName) throws IOException {
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
    
    public Path getTrashRoot(final Path path) {
        return this.makeQualified(new Path(this.getHomeDirectory().toUri().getPath(), ".Trash"));
    }
    
    public Collection<FileStatus> getTrashRoots(final boolean allUsers) {
        final Path userHome = new Path(this.getHomeDirectory().toUri().getPath());
        final List<FileStatus> ret = new ArrayList<FileStatus>();
        try {
            if (!allUsers) {
                final Path userTrash = new Path(userHome, ".Trash");
                if (this.exists(userTrash)) {
                    ret.add(this.getFileStatus(userTrash));
                }
            }
            else {
                final Path homeParent = userHome.getParent();
                if (this.exists(homeParent)) {
                    final FileStatus[] listStatus;
                    final FileStatus[] candidates = listStatus = this.listStatus(homeParent);
                    for (final FileStatus candidate : listStatus) {
                        final Path userTrash2 = new Path(candidate.getPath(), ".Trash");
                        if (this.exists(userTrash2)) {
                            candidate.setPath(userTrash2);
                            ret.add(candidate);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            FileSystem.LOGGER.warn("Cannot get all trash roots", e);
        }
        return ret;
    }
    
    private static void loadFileSystems() {
        FileSystem.LOGGER.debug("Loading filesystems");
        synchronized (FileSystem.class) {
            if (!FileSystem.FILE_SYSTEMS_LOADED) {
                final ServiceLoader<FileSystem> serviceLoader = ServiceLoader.load(FileSystem.class);
                final Iterator<FileSystem> it = serviceLoader.iterator();
                while (it.hasNext()) {
                    try {
                        final FileSystem fs = it.next();
                        try {
                            FileSystem.SERVICE_FILE_SYSTEMS.put(fs.getScheme(), fs.getClass());
                            if (!FileSystem.LOGGER.isDebugEnabled()) {
                                continue;
                            }
                            FileSystem.LOGGER.debug("{}:// = {} from {}", fs.getScheme(), fs.getClass(), ClassUtil.findContainingJar(fs.getClass()));
                        }
                        catch (Exception e) {
                            FileSystem.LOGGER.warn("Cannot load: {} from {}", fs, ClassUtil.findContainingJar(fs.getClass()));
                            FileSystem.LOGGER.info("Full exception loading: {}", fs, e);
                        }
                    }
                    catch (ServiceConfigurationError ee) {
                        FileSystem.LOG.warn("Cannot load filesystem: " + ee);
                        for (Throwable cause = ee.getCause(); cause != null; cause = cause.getCause()) {
                            FileSystem.LOG.warn(cause.toString());
                        }
                        FileSystem.LOG.debug("Stack Trace", ee);
                    }
                }
                FileSystem.FILE_SYSTEMS_LOADED = true;
            }
        }
    }
    
    public static Class<? extends FileSystem> getFileSystemClass(final String scheme, final Configuration conf) throws IOException {
        if (!FileSystem.FILE_SYSTEMS_LOADED) {
            loadFileSystems();
        }
        FileSystem.LOGGER.debug("Looking for FS supporting {}", scheme);
        Class<? extends FileSystem> clazz = null;
        if (conf != null) {
            final String property = "fs." + scheme + ".impl";
            FileSystem.LOGGER.debug("looking for configuration option {}", property);
            clazz = (Class<? extends FileSystem>)conf.getClass(property, null);
        }
        else {
            FileSystem.LOGGER.debug("No configuration: skipping check for fs.{}.impl", scheme);
        }
        if (clazz == null) {
            FileSystem.LOGGER.debug("Looking in service filesystems for implementation class");
            clazz = FileSystem.SERVICE_FILE_SYSTEMS.get(scheme);
        }
        else {
            FileSystem.LOGGER.debug("Filesystem {} defined in configuration option", scheme);
        }
        if (clazz == null) {
            throw new UnsupportedFileSystemException("No FileSystem for scheme \"" + scheme + "\"");
        }
        FileSystem.LOGGER.debug("FS for {} is {}", scheme, clazz);
        return clazz;
    }
    
    private static FileSystem createFileSystem(final URI uri, final Configuration conf) throws IOException {
        final Tracer tracer = FsTracer.get(conf);
        try (final TraceScope scope = tracer.newScope("FileSystem#createFileSystem")) {
            scope.addKVAnnotation("scheme", uri.getScheme());
            final Class<?> clazz = getFileSystemClass(uri.getScheme(), conf);
            final FileSystem fs = ReflectionUtils.newInstance(clazz, conf);
            fs.initialize(uri, conf);
            return fs;
        }
    }
    
    @Deprecated
    public static synchronized Map<String, Statistics> getStatistics() {
        final Map<String, Statistics> result = new HashMap<String, Statistics>();
        for (final Statistics stat : FileSystem.statisticsTable.values()) {
            result.put(stat.getScheme(), stat);
        }
        return result;
    }
    
    @Deprecated
    public static synchronized List<Statistics> getAllStatistics() {
        return new ArrayList<Statistics>(FileSystem.statisticsTable.values());
    }
    
    @Deprecated
    public static synchronized Statistics getStatistics(final String scheme, final Class<? extends FileSystem> cls) {
        Preconditions.checkArgument(scheme != null, (Object)"No statistics is allowed for a file system with null scheme!");
        Statistics result = FileSystem.statisticsTable.get(cls);
        if (result == null) {
            final Statistics newStats = new Statistics(scheme);
            FileSystem.statisticsTable.put(cls, newStats);
            result = newStats;
            GlobalStorageStatistics.INSTANCE.put(scheme, new GlobalStorageStatistics.StorageStatisticsProvider() {
                @Override
                public StorageStatistics provide() {
                    return new FileSystemStorageStatistics(scheme, newStats);
                }
            });
        }
        return result;
    }
    
    public static synchronized void clearStatistics() {
        GlobalStorageStatistics.INSTANCE.reset();
    }
    
    public static synchronized void printStatistics() throws IOException {
        for (final Map.Entry<Class<? extends FileSystem>, Statistics> pair : FileSystem.statisticsTable.entrySet()) {
            System.out.println("  FileSystem " + pair.getKey().getName() + ": " + pair.getValue());
        }
    }
    
    @VisibleForTesting
    public static boolean areSymlinksEnabled() {
        return FileSystem.symlinksEnabled;
    }
    
    @VisibleForTesting
    public static void enableSymlinks() {
        FileSystem.symlinksEnabled = true;
    }
    
    public StorageStatistics getStorageStatistics() {
        return new EmptyStorageStatistics(this.getUri().toString());
    }
    
    public static GlobalStorageStatistics getGlobalStorageStatistics() {
        return GlobalStorageStatistics.INSTANCE;
    }
    
    public FSDataOutputStreamBuilder createFile(final Path path) {
        return ((FSDataOutputStreamBuilder<S, FileSystemDataOutputStreamBuilder>)new FileSystemDataOutputStreamBuilder(this, path)).create().overwrite(true);
    }
    
    public FSDataOutputStreamBuilder appendFile(final Path path) {
        return new FileSystemDataOutputStreamBuilder(this, path).append();
    }
    
    static {
        LOG = LogFactory.getLog(FileSystem.class);
        LOGGER = LoggerFactory.getLogger(FileSystem.class);
        CACHE = new Cache();
        statisticsTable = new IdentityHashMap<Class<? extends FileSystem>, Statistics>();
        DEFAULT_FILTER = new PathFilter() {
            @Override
            public boolean accept(final Path file) {
                return true;
            }
        };
        FileSystem.FILE_SYSTEMS_LOADED = false;
        SERVICE_FILE_SYSTEMS = new HashMap<String, Class<? extends FileSystem>>();
        FileSystem.symlinksEnabled = false;
    }
    
    @InterfaceAudience.Private
    public static class DirectoryEntries
    {
        private final FileStatus[] entries;
        private final byte[] token;
        private final boolean hasMore;
        
        public DirectoryEntries(final FileStatus[] entries, final byte[] token, final boolean hasMore) {
            this.entries = entries;
            if (token != null) {
                this.token = token.clone();
            }
            else {
                this.token = null;
            }
            this.hasMore = hasMore;
        }
        
        public FileStatus[] getEntries() {
            return this.entries;
        }
        
        public byte[] getToken() {
            return this.token;
        }
        
        public boolean hasMore() {
            return this.hasMore;
        }
    }
    
    protected class DirListingIterator<T extends FileStatus> implements RemoteIterator<T>
    {
        private final Path path;
        private DirectoryEntries entries;
        private int i;
        
        DirListingIterator(final Path path) {
            this.i = 0;
            this.path = path;
        }
        
        @Override
        public boolean hasNext() throws IOException {
            if (this.entries == null) {
                this.fetchMore();
            }
            return this.i < this.entries.getEntries().length || this.entries.hasMore();
        }
        
        private void fetchMore() throws IOException {
            byte[] token = null;
            if (this.entries != null) {
                token = this.entries.getToken();
            }
            this.entries = FileSystem.this.listStatusBatch(this.path, token);
            this.i = 0;
        }
        
        @Override
        public T next() throws IOException {
            Preconditions.checkState(this.hasNext(), (Object)"No more items in iterator");
            if (this.i == this.entries.getEntries().length) {
                this.fetchMore();
            }
            return (T)this.entries.getEntries()[this.i++];
        }
    }
    
    static class Cache
    {
        private final ClientFinalizer clientFinalizer;
        private final Map<Key, FileSystem> map;
        private final Set<Key> toAutoClose;
        private static AtomicLong unique;
        
        Cache() {
            this.clientFinalizer = new ClientFinalizer();
            this.map = new HashMap<Key, FileSystem>();
            this.toAutoClose = new HashSet<Key>();
        }
        
        FileSystem get(final URI uri, final Configuration conf) throws IOException {
            final Key key = new Key(uri, conf);
            return this.getInternal(uri, conf, key);
        }
        
        FileSystem getUnique(final URI uri, final Configuration conf) throws IOException {
            final Key key = new Key(uri, conf, Cache.unique.getAndIncrement());
            return this.getInternal(uri, conf, key);
        }
        
        private FileSystem getInternal(final URI uri, final Configuration conf, final Key key) throws IOException {
            FileSystem fs;
            synchronized (this) {
                fs = this.map.get(key);
            }
            if (fs != null) {
                return fs;
            }
            fs = createFileSystem(uri, conf);
            synchronized (this) {
                final FileSystem oldfs = this.map.get(key);
                if (oldfs != null) {
                    fs.close();
                    return oldfs;
                }
                if (this.map.isEmpty() && !ShutdownHookManager.get().isShutdownInProgress()) {
                    ShutdownHookManager.get().addShutdownHook(this.clientFinalizer, 10);
                }
                fs.key = key;
                this.map.put(key, fs);
                if (conf.getBoolean("fs.automatic.close", true)) {
                    this.toAutoClose.add(key);
                }
                return fs;
            }
        }
        
        synchronized void remove(final Key key, final FileSystem fs) {
            final FileSystem cachedFs = this.map.remove(key);
            if (fs == cachedFs) {
                this.toAutoClose.remove(key);
            }
            else if (cachedFs != null) {
                this.map.put(key, cachedFs);
            }
        }
        
        synchronized void closeAll() throws IOException {
            this.closeAll(false);
        }
        
        synchronized void closeAll(final boolean onlyAutomatic) throws IOException {
            final List<IOException> exceptions = new ArrayList<IOException>();
            final List<Key> keys = new ArrayList<Key>();
            keys.addAll(this.map.keySet());
            for (final Key key : keys) {
                final FileSystem fs = this.map.get(key);
                if (onlyAutomatic && !this.toAutoClose.contains(key)) {
                    continue;
                }
                this.map.remove(key);
                this.toAutoClose.remove(key);
                if (fs == null) {
                    continue;
                }
                try {
                    fs.close();
                }
                catch (IOException ioe) {
                    exceptions.add(ioe);
                }
            }
            if (!exceptions.isEmpty()) {
                throw MultipleIOException.createIOException(exceptions);
            }
        }
        
        synchronized void closeAll(final UserGroupInformation ugi) throws IOException {
            final List<FileSystem> targetFSList = new ArrayList<FileSystem>(this.map.entrySet().size());
            for (final Map.Entry<Key, FileSystem> entry : this.map.entrySet()) {
                final Key key = entry.getKey();
                final FileSystem fs = entry.getValue();
                if (ugi.equals(key.ugi) && fs != null) {
                    targetFSList.add(fs);
                }
            }
            final List<IOException> exceptions = new ArrayList<IOException>();
            for (final FileSystem fs2 : targetFSList) {
                try {
                    fs2.close();
                }
                catch (IOException ioe) {
                    exceptions.add(ioe);
                }
            }
            if (!exceptions.isEmpty()) {
                throw MultipleIOException.createIOException(exceptions);
            }
        }
        
        static {
            Cache.unique = new AtomicLong(1L);
        }
        
        private class ClientFinalizer implements Runnable
        {
            @Override
            public synchronized void run() {
                try {
                    Cache.this.closeAll(true);
                }
                catch (IOException e) {
                    FileSystem.LOGGER.info("FileSystem.Cache.closeAll() threw an exception:\n" + e);
                }
            }
        }
        
        static class Key
        {
            final String scheme;
            final String authority;
            final UserGroupInformation ugi;
            final long unique;
            
            Key(final URI uri, final Configuration conf) throws IOException {
                this(uri, conf, 0L);
            }
            
            Key(final URI uri, final Configuration conf, final long unique) throws IOException {
                this.scheme = ((uri.getScheme() == null) ? "" : StringUtils.toLowerCase(uri.getScheme()));
                this.authority = ((uri.getAuthority() == null) ? "" : StringUtils.toLowerCase(uri.getAuthority()));
                this.unique = unique;
                this.ugi = UserGroupInformation.getCurrentUser();
            }
            
            @Override
            public int hashCode() {
                return (this.scheme + this.authority).hashCode() + this.ugi.hashCode() + (int)this.unique;
            }
            
            static boolean isEqual(final Object a, final Object b) {
                return a == b || (a != null && a.equals(b));
            }
            
            @Override
            public boolean equals(final Object obj) {
                if (obj == this) {
                    return true;
                }
                if (obj instanceof Key) {
                    final Key that = (Key)obj;
                    return isEqual(this.scheme, that.scheme) && isEqual(this.authority, that.authority) && isEqual(this.ugi, that.ugi) && this.unique == that.unique;
                }
                return false;
            }
            
            @Override
            public String toString() {
                return "(" + this.ugi.toString() + ")@" + this.scheme + "://" + this.authority;
            }
        }
    }
    
    public static final class Statistics
    {
        private final String scheme;
        private final StatisticsData rootData;
        private final ThreadLocal<StatisticsData> threadData;
        private final Set<StatisticsDataReference> allData;
        private static final ReferenceQueue<Thread> STATS_DATA_REF_QUEUE;
        private static final Thread STATS_DATA_CLEANER;
        
        public Statistics(final String scheme) {
            this.scheme = scheme;
            this.rootData = new StatisticsData();
            this.threadData = new ThreadLocal<StatisticsData>();
            this.allData = new HashSet<StatisticsDataReference>();
        }
        
        public Statistics(final Statistics other) {
            this.scheme = other.scheme;
            this.rootData = new StatisticsData();
            other.visitAll((StatisticsAggregator<Object>)new StatisticsAggregator<Void>() {
                @Override
                public void accept(final StatisticsData data) {
                    Statistics.this.rootData.add(data);
                }
                
                @Override
                public Void aggregate() {
                    return null;
                }
            });
            this.threadData = new ThreadLocal<StatisticsData>();
            this.allData = new HashSet<StatisticsDataReference>();
        }
        
        public StatisticsData getThreadStatistics() {
            StatisticsData data = this.threadData.get();
            if (data == null) {
                data = new StatisticsData();
                this.threadData.set(data);
                final StatisticsDataReference ref = new StatisticsDataReference(data, Thread.currentThread());
                synchronized (this) {
                    this.allData.add(ref);
                }
            }
            return data;
        }
        
        public void incrementBytesRead(final long newBytes) {
            final StatisticsData threadStatistics = this.getThreadStatistics();
            threadStatistics.bytesRead += newBytes;
        }
        
        public void incrementBytesWritten(final long newBytes) {
            final StatisticsData threadStatistics = this.getThreadStatistics();
            threadStatistics.bytesWritten += newBytes;
        }
        
        public void incrementReadOps(final int count) {
            final StatisticsData threadStatistics = this.getThreadStatistics();
            threadStatistics.readOps += count;
        }
        
        public void incrementLargeReadOps(final int count) {
            final StatisticsData threadStatistics = this.getThreadStatistics();
            threadStatistics.largeReadOps += count;
        }
        
        public void incrementWriteOps(final int count) {
            final StatisticsData threadStatistics = this.getThreadStatistics();
            threadStatistics.writeOps += count;
        }
        
        public void incrementBytesReadErasureCoded(final long newBytes) {
            final StatisticsData threadStatistics = this.getThreadStatistics();
            threadStatistics.bytesReadErasureCoded += newBytes;
        }
        
        public void incrementBytesReadByDistance(final int distance, final long newBytes) {
            switch (distance) {
                case 0: {
                    final StatisticsData threadStatistics = this.getThreadStatistics();
                    threadStatistics.bytesReadLocalHost += newBytes;
                    break;
                }
                case 1:
                case 2: {
                    final StatisticsData threadStatistics2 = this.getThreadStatistics();
                    threadStatistics2.bytesReadDistanceOfOneOrTwo += newBytes;
                    break;
                }
                case 3:
                case 4: {
                    final StatisticsData threadStatistics3 = this.getThreadStatistics();
                    threadStatistics3.bytesReadDistanceOfThreeOrFour += newBytes;
                    break;
                }
                default: {
                    final StatisticsData threadStatistics4 = this.getThreadStatistics();
                    threadStatistics4.bytesReadDistanceOfFiveOrLarger += newBytes;
                    break;
                }
            }
        }
        
        private synchronized <T> T visitAll(final StatisticsAggregator<T> visitor) {
            visitor.accept(this.rootData);
            for (final StatisticsDataReference ref : this.allData) {
                final StatisticsData data = ref.getData();
                visitor.accept(data);
            }
            return visitor.aggregate();
        }
        
        public long getBytesRead() {
            return this.visitAll((StatisticsAggregator<Long>)new StatisticsAggregator<Long>() {
                private long bytesRead = 0L;
                
                @Override
                public void accept(final StatisticsData data) {
                    this.bytesRead += data.bytesRead;
                }
                
                @Override
                public Long aggregate() {
                    return this.bytesRead;
                }
            });
        }
        
        public long getBytesWritten() {
            return this.visitAll((StatisticsAggregator<Long>)new StatisticsAggregator<Long>() {
                private long bytesWritten = 0L;
                
                @Override
                public void accept(final StatisticsData data) {
                    this.bytesWritten += data.bytesWritten;
                }
                
                @Override
                public Long aggregate() {
                    return this.bytesWritten;
                }
            });
        }
        
        public int getReadOps() {
            return this.visitAll((StatisticsAggregator<Integer>)new StatisticsAggregator<Integer>() {
                private int readOps = 0;
                
                @Override
                public void accept(final StatisticsData data) {
                    this.readOps += data.readOps;
                    this.readOps += data.largeReadOps;
                }
                
                @Override
                public Integer aggregate() {
                    return this.readOps;
                }
            });
        }
        
        public int getLargeReadOps() {
            return this.visitAll((StatisticsAggregator<Integer>)new StatisticsAggregator<Integer>() {
                private int largeReadOps = 0;
                
                @Override
                public void accept(final StatisticsData data) {
                    this.largeReadOps += data.largeReadOps;
                }
                
                @Override
                public Integer aggregate() {
                    return this.largeReadOps;
                }
            });
        }
        
        public int getWriteOps() {
            return this.visitAll((StatisticsAggregator<Integer>)new StatisticsAggregator<Integer>() {
                private int writeOps = 0;
                
                @Override
                public void accept(final StatisticsData data) {
                    this.writeOps += data.writeOps;
                }
                
                @Override
                public Integer aggregate() {
                    return this.writeOps;
                }
            });
        }
        
        public long getBytesReadByDistance(final int distance) {
            long bytesRead = 0L;
            switch (distance) {
                case 0: {
                    bytesRead = this.getData().getBytesReadLocalHost();
                    break;
                }
                case 1:
                case 2: {
                    bytesRead = this.getData().getBytesReadDistanceOfOneOrTwo();
                    break;
                }
                case 3:
                case 4: {
                    bytesRead = this.getData().getBytesReadDistanceOfThreeOrFour();
                    break;
                }
                default: {
                    bytesRead = this.getData().getBytesReadDistanceOfFiveOrLarger();
                    break;
                }
            }
            return bytesRead;
        }
        
        public StatisticsData getData() {
            return this.visitAll((StatisticsAggregator<StatisticsData>)new StatisticsAggregator<StatisticsData>() {
                private StatisticsData all = new StatisticsData();
                
                @Override
                public void accept(final StatisticsData data) {
                    this.all.add(data);
                }
                
                @Override
                public StatisticsData aggregate() {
                    return this.all;
                }
            });
        }
        
        public long getBytesReadErasureCoded() {
            return this.visitAll((StatisticsAggregator<Long>)new StatisticsAggregator<Long>() {
                private long bytesReadErasureCoded = 0L;
                
                @Override
                public void accept(final StatisticsData data) {
                    this.bytesReadErasureCoded += data.bytesReadErasureCoded;
                }
                
                @Override
                public Long aggregate() {
                    return this.bytesReadErasureCoded;
                }
            });
        }
        
        @Override
        public String toString() {
            return this.visitAll((StatisticsAggregator<String>)new StatisticsAggregator<String>() {
                private StatisticsData total = new StatisticsData();
                
                @Override
                public void accept(final StatisticsData data) {
                    this.total.add(data);
                }
                
                @Override
                public String aggregate() {
                    return this.total.toString();
                }
            });
        }
        
        public void reset() {
            this.visitAll((StatisticsAggregator<Object>)new StatisticsAggregator<Void>() {
                private StatisticsData total = new StatisticsData();
                
                @Override
                public void accept(final StatisticsData data) {
                    this.total.add(data);
                }
                
                @Override
                public Void aggregate() {
                    this.total.negate();
                    Statistics.this.rootData.add(this.total);
                    return null;
                }
            });
        }
        
        public String getScheme() {
            return this.scheme;
        }
        
        @VisibleForTesting
        synchronized int getAllThreadLocalDataSize() {
            return this.allData.size();
        }
        
        static {
            STATS_DATA_REF_QUEUE = new ReferenceQueue<Thread>();
            (STATS_DATA_CLEANER = new Thread(new StatisticsDataReferenceCleaner())).setName(StatisticsDataReferenceCleaner.class.getName());
            Statistics.STATS_DATA_CLEANER.setDaemon(true);
            Statistics.STATS_DATA_CLEANER.start();
        }
        
        public static class StatisticsData
        {
            private volatile long bytesRead;
            private volatile long bytesWritten;
            private volatile int readOps;
            private volatile int largeReadOps;
            private volatile int writeOps;
            private volatile long bytesReadLocalHost;
            private volatile long bytesReadDistanceOfOneOrTwo;
            private volatile long bytesReadDistanceOfThreeOrFour;
            private volatile long bytesReadDistanceOfFiveOrLarger;
            private volatile long bytesReadErasureCoded;
            
            void add(final StatisticsData other) {
                this.bytesRead += other.bytesRead;
                this.bytesWritten += other.bytesWritten;
                this.readOps += other.readOps;
                this.largeReadOps += other.largeReadOps;
                this.writeOps += other.writeOps;
                this.bytesReadLocalHost += other.bytesReadLocalHost;
                this.bytesReadDistanceOfOneOrTwo += other.bytesReadDistanceOfOneOrTwo;
                this.bytesReadDistanceOfThreeOrFour += other.bytesReadDistanceOfThreeOrFour;
                this.bytesReadDistanceOfFiveOrLarger += other.bytesReadDistanceOfFiveOrLarger;
                this.bytesReadErasureCoded += other.bytesReadErasureCoded;
            }
            
            void negate() {
                this.bytesRead = -this.bytesRead;
                this.bytesWritten = -this.bytesWritten;
                this.readOps = -this.readOps;
                this.largeReadOps = -this.largeReadOps;
                this.writeOps = -this.writeOps;
                this.bytesReadLocalHost = -this.bytesReadLocalHost;
                this.bytesReadDistanceOfOneOrTwo = -this.bytesReadDistanceOfOneOrTwo;
                this.bytesReadDistanceOfThreeOrFour = -this.bytesReadDistanceOfThreeOrFour;
                this.bytesReadDistanceOfFiveOrLarger = -this.bytesReadDistanceOfFiveOrLarger;
                this.bytesReadErasureCoded = -this.bytesReadErasureCoded;
            }
            
            @Override
            public String toString() {
                return this.bytesRead + " bytes read, " + this.bytesWritten + " bytes written, " + this.readOps + " read ops, " + this.largeReadOps + " large read ops, " + this.writeOps + " write ops";
            }
            
            public long getBytesRead() {
                return this.bytesRead;
            }
            
            public long getBytesWritten() {
                return this.bytesWritten;
            }
            
            public int getReadOps() {
                return this.readOps;
            }
            
            public int getLargeReadOps() {
                return this.largeReadOps;
            }
            
            public int getWriteOps() {
                return this.writeOps;
            }
            
            public long getBytesReadLocalHost() {
                return this.bytesReadLocalHost;
            }
            
            public long getBytesReadDistanceOfOneOrTwo() {
                return this.bytesReadDistanceOfOneOrTwo;
            }
            
            public long getBytesReadDistanceOfThreeOrFour() {
                return this.bytesReadDistanceOfThreeOrFour;
            }
            
            public long getBytesReadDistanceOfFiveOrLarger() {
                return this.bytesReadDistanceOfFiveOrLarger;
            }
            
            public long getBytesReadErasureCoded() {
                return this.bytesReadErasureCoded;
            }
        }
        
        private final class StatisticsDataReference extends WeakReference<Thread>
        {
            private final StatisticsData data;
            
            private StatisticsDataReference(final StatisticsData data, final Thread thread) {
                super(thread, Statistics.STATS_DATA_REF_QUEUE);
                this.data = data;
            }
            
            public StatisticsData getData() {
                return this.data;
            }
            
            public void cleanUp() {
                synchronized (Statistics.this) {
                    Statistics.this.rootData.add(this.data);
                    Statistics.this.allData.remove(this);
                }
            }
        }
        
        private static class StatisticsDataReferenceCleaner implements Runnable
        {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        final StatisticsDataReference ref = (StatisticsDataReference)Statistics.STATS_DATA_REF_QUEUE.remove();
                        ref.cleanUp();
                    }
                    catch (InterruptedException ie) {
                        FileSystem.LOGGER.warn("Cleaner thread interrupted, will stop", ie);
                        Thread.currentThread().interrupt();
                    }
                    catch (Throwable th) {
                        FileSystem.LOGGER.warn("Exception in the cleaner thread but it will continue to run", th);
                    }
                }
            }
        }
        
        private interface StatisticsAggregator<T>
        {
            void accept(final StatisticsData p0);
            
            T aggregate();
        }
    }
    
    private static final class FileSystemDataOutputStreamBuilder extends FSDataOutputStreamBuilder<FSDataOutputStream, FileSystemDataOutputStreamBuilder>
    {
        protected FileSystemDataOutputStreamBuilder(final FileSystem fileSystem, final Path p) {
            super(fileSystem, p);
        }
        
        @Override
        public FSDataOutputStream build() throws IOException {
            if (this.getFlags().contains(CreateFlag.CREATE) || this.getFlags().contains(CreateFlag.OVERWRITE)) {
                if (this.isRecursive()) {
                    return this.getFS().create(this.getPath(), this.getPermission(), this.getFlags(), this.getBufferSize(), this.getReplication(), this.getBlockSize(), this.getProgress(), this.getChecksumOpt());
                }
                return this.getFS().createNonRecursive(this.getPath(), this.getPermission(), this.getFlags(), this.getBufferSize(), this.getReplication(), this.getBlockSize(), this.getProgress());
            }
            else {
                if (this.getFlags().contains(CreateFlag.APPEND)) {
                    return this.getFS().append(this.getPath(), this.getBufferSize(), this.getProgress());
                }
                throw new IOException("Must specify either create, overwrite or append");
            }
        }
        
        @Override
        protected FileSystemDataOutputStreamBuilder getThisBuilder() {
            return this;
        }
    }
}
