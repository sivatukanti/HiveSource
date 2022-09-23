// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.nativeio;

import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.fs.PathIOException;
import org.apache.hadoop.util.CleanerUtil;
import java.nio.MappedByteBuffer;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.util.PerformanceAdvisory;
import org.slf4j.LoggerFactory;
import java.nio.channels.FileChannel;
import org.apache.hadoop.io.IOUtils;
import java.io.Closeable;
import java.nio.channels.WritableByteChannel;
import java.io.FileInputStream;
import org.apache.hadoop.fs.HardLink;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.SecureIOUtils;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.util.Shell;
import java.io.FileDescriptor;
import java.lang.reflect.Field;
import sun.misc.Unsafe;
import org.apache.hadoop.util.NativeCodeLoader;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class NativeIO
{
    private static boolean workaroundNonThreadSafePasswdCalls;
    private static final Logger LOG;
    private static boolean nativeLoaded;
    private static final Map<Long, CachedUid> uidCache;
    private static long cacheTimeout;
    private static boolean initialized;
    
    public static boolean isAvailable() {
        return NativeCodeLoader.isNativeCodeLoaded() && NativeIO.nativeLoaded;
    }
    
    private static native void initNative();
    
    static long getMemlockLimit() {
        return isAvailable() ? getMemlockLimit0() : 0L;
    }
    
    private static native long getMemlockLimit0();
    
    static long getOperatingSystemPageSize() {
        try {
            final Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            final Unsafe unsafe = (Unsafe)f.get(null);
            return unsafe.pageSize();
        }
        catch (Throwable e) {
            NativeIO.LOG.warn("Unable to get operating system page size.  Guessing 4096.", e);
            return 4096L;
        }
    }
    
    private static String stripDomain(String name) {
        final int i = name.indexOf(92);
        if (i != -1) {
            name = name.substring(i + 1);
        }
        return name;
    }
    
    public static String getOwner(final FileDescriptor fd) throws IOException {
        ensureInitialized();
        if (Shell.WINDOWS) {
            String owner = getOwner(fd);
            owner = stripDomain(owner);
            return owner;
        }
        final long uid = getUIDforFDOwnerforOwner(fd);
        CachedUid cUid = NativeIO.uidCache.get(uid);
        final long now = System.currentTimeMillis();
        if (cUid != null && cUid.timestamp + NativeIO.cacheTimeout > now) {
            return cUid.username;
        }
        final String user = getUserName(uid);
        NativeIO.LOG.info("Got UserName " + user + " for UID " + uid + " from the native implementation");
        cUid = new CachedUid(user, now);
        NativeIO.uidCache.put(uid, cUid);
        return user;
    }
    
    public static FileDescriptor getShareDeleteFileDescriptor(final File f, final long seekOffset) throws IOException {
        if (!Shell.WINDOWS) {
            final RandomAccessFile rf = new RandomAccessFile(f, "r");
            if (seekOffset > 0L) {
                rf.seek(seekOffset);
            }
            return rf.getFD();
        }
        final FileDescriptor fd = Windows.createFile(f.getAbsolutePath(), 2147483648L, 7L, 3L);
        if (seekOffset > 0L) {
            Windows.setFilePointer(fd, seekOffset, 0L);
        }
        return fd;
    }
    
    public static FileOutputStream getCreateForWriteFileOutputStream(final File f, final int permissions) throws IOException {
        if (!Shell.WINDOWS) {
            try {
                final FileDescriptor fd = POSIX.open(f.getAbsolutePath(), POSIX.O_WRONLY | POSIX.O_CREAT | POSIX.O_EXCL, permissions);
                return new FileOutputStream(fd);
            }
            catch (NativeIOException nioe) {
                if (nioe.getErrno() == Errno.EEXIST) {
                    throw new SecureIOUtils.AlreadyExistsException(nioe);
                }
                throw nioe;
            }
        }
        try {
            final FileDescriptor fd = Windows.createFile(f.getCanonicalPath(), 1073741824L, 7L, 1L);
            POSIX.chmod(f.getCanonicalPath(), permissions);
            return new FileOutputStream(fd);
        }
        catch (NativeIOException nioe) {
            if (nioe.getErrorCode() == 80L) {
                throw new SecureIOUtils.AlreadyExistsException(nioe);
            }
            throw nioe;
        }
    }
    
    private static synchronized void ensureInitialized() {
        if (!NativeIO.initialized) {
            NativeIO.cacheTimeout = new Configuration().getLong("hadoop.security.uid.cache.secs", 14400L) * 1000L;
            NativeIO.LOG.info("Initialized cache for UID to User mapping with a cache timeout of " + NativeIO.cacheTimeout / 1000L + " seconds.");
            NativeIO.initialized = true;
        }
    }
    
    public static void renameTo(final File src, final File dst) throws IOException {
        if (!NativeIO.nativeLoaded) {
            if (!src.renameTo(dst)) {
                throw new IOException("renameTo(src=" + src + ", dst=" + dst + ") failed.");
            }
        }
        else {
            renameTo0(src.getAbsolutePath(), dst.getAbsolutePath());
        }
    }
    
    @Deprecated
    public static void link(final File src, final File dst) throws IOException {
        if (!NativeIO.nativeLoaded) {
            HardLink.createHardLink(src, dst);
        }
        else {
            link0(src.getAbsolutePath(), dst.getAbsolutePath());
        }
    }
    
    private static native void renameTo0(final String p0, final String p1) throws NativeIOException;
    
    private static native void link0(final String p0, final String p1) throws NativeIOException;
    
    public static void copyFileUnbuffered(final File src, final File dst) throws IOException {
        if (NativeIO.nativeLoaded && Shell.WINDOWS) {
            copyFileUnbuffered0(src.getAbsolutePath(), dst.getAbsolutePath());
        }
        else {
            final FileInputStream fis = new FileInputStream(src);
            FileChannel input = null;
            try {
                input = fis.getChannel();
                try (final FileOutputStream fos = new FileOutputStream(dst);
                     final FileChannel output = fos.getChannel()) {
                    for (long remaining = input.size(), position = 0L, transferred = 0L; remaining > 0L; remaining -= transferred, position += transferred) {
                        transferred = input.transferTo(position, remaining, output);
                    }
                }
            }
            finally {
                IOUtils.cleanupWithLogger(NativeIO.LOG, input, fis);
            }
        }
    }
    
    private static native void copyFileUnbuffered0(final String p0, final String p1) throws NativeIOException;
    
    static {
        NativeIO.workaroundNonThreadSafePasswdCalls = false;
        LOG = LoggerFactory.getLogger(NativeIO.class);
        NativeIO.nativeLoaded = false;
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            try {
                initNative();
                NativeIO.nativeLoaded = true;
            }
            catch (Throwable t) {
                PerformanceAdvisory.LOG.debug("Unable to initialize NativeIO libraries", t);
            }
        }
        uidCache = new ConcurrentHashMap<Long, CachedUid>();
        NativeIO.initialized = false;
    }
    
    public static class POSIX
    {
        public static int O_RDONLY;
        public static int O_WRONLY;
        public static int O_RDWR;
        public static int O_CREAT;
        public static int O_EXCL;
        public static int O_NOCTTY;
        public static int O_TRUNC;
        public static int O_APPEND;
        public static int O_NONBLOCK;
        public static int O_SYNC;
        public static int POSIX_FADV_NORMAL;
        public static int POSIX_FADV_RANDOM;
        public static int POSIX_FADV_SEQUENTIAL;
        public static int POSIX_FADV_WILLNEED;
        public static int POSIX_FADV_DONTNEED;
        public static int POSIX_FADV_NOREUSE;
        public static int SYNC_FILE_RANGE_WAIT_BEFORE;
        public static int SYNC_FILE_RANGE_WRITE;
        public static int SYNC_FILE_RANGE_WAIT_AFTER;
        private static final Logger LOG;
        public static boolean fadvisePossible;
        private static boolean nativeLoaded;
        private static boolean syncFileRangePossible;
        static final String WORKAROUND_NON_THREADSAFE_CALLS_KEY = "hadoop.workaround.non.threadsafe.getpwuid";
        static final boolean WORKAROUND_NON_THREADSAFE_CALLS_DEFAULT = true;
        private static long cacheTimeout;
        private static CacheManipulator cacheManipulator;
        private static final Map<Integer, CachedName> USER_ID_NAME_CACHE;
        private static final Map<Integer, CachedName> GROUP_ID_NAME_CACHE;
        public static final int MMAP_PROT_READ = 1;
        public static final int MMAP_PROT_WRITE = 2;
        public static final int MMAP_PROT_EXEC = 4;
        
        public static CacheManipulator getCacheManipulator() {
            return POSIX.cacheManipulator;
        }
        
        public static void setCacheManipulator(final CacheManipulator cacheManipulator) {
            POSIX.cacheManipulator = cacheManipulator;
        }
        
        public static boolean isAvailable() {
            return NativeCodeLoader.isNativeCodeLoaded() && POSIX.nativeLoaded;
        }
        
        private static void assertCodeLoaded() throws IOException {
            if (!isAvailable()) {
                throw new IOException("NativeIO was not loaded");
            }
        }
        
        public static native FileDescriptor open(final String p0, final int p1, final int p2) throws IOException;
        
        private static native Stat fstat(final FileDescriptor p0) throws IOException;
        
        private static native Stat stat(final String p0) throws IOException;
        
        private static native void chmodImpl(final String p0, final int p1) throws IOException;
        
        public static void chmod(final String path, final int mode) throws IOException {
            if (!Shell.WINDOWS) {
                chmodImpl(path, mode);
            }
            else {
                try {
                    chmodImpl(path, mode);
                }
                catch (NativeIOException nioe) {
                    if (nioe.getErrorCode() == 3L) {
                        throw new NativeIOException("No such file or directory", Errno.ENOENT);
                    }
                    POSIX.LOG.warn(String.format("NativeIO.chmod error (%d): %s", nioe.getErrorCode(), nioe.getMessage()));
                    throw new NativeIOException("Unknown error", Errno.UNKNOWN);
                }
            }
        }
        
        static native void posix_fadvise(final FileDescriptor p0, final long p1, final long p2, final int p3) throws NativeIOException;
        
        static native void sync_file_range(final FileDescriptor p0, final long p1, final long p2, final int p3) throws NativeIOException;
        
        static void posixFadviseIfPossible(final String identifier, final FileDescriptor fd, final long offset, final long len, final int flags) throws NativeIOException {
            if (POSIX.nativeLoaded && POSIX.fadvisePossible) {
                try {
                    posix_fadvise(fd, offset, len, flags);
                }
                catch (UnsatisfiedLinkError ule) {
                    POSIX.fadvisePossible = false;
                }
            }
        }
        
        public static void syncFileRangeIfPossible(final FileDescriptor fd, final long offset, final long nbytes, final int flags) throws NativeIOException {
            if (POSIX.nativeLoaded && POSIX.syncFileRangePossible) {
                try {
                    sync_file_range(fd, offset, nbytes, flags);
                }
                catch (UnsupportedOperationException uoe) {
                    POSIX.syncFileRangePossible = false;
                }
                catch (UnsatisfiedLinkError ule) {
                    POSIX.syncFileRangePossible = false;
                }
            }
        }
        
        static native void mlock_native(final ByteBuffer p0, final long p1) throws NativeIOException;
        
        static void mlock(final ByteBuffer buffer, final long len) throws IOException {
            assertCodeLoaded();
            if (!buffer.isDirect()) {
                throw new IOException("Cannot mlock a non-direct ByteBuffer");
            }
            mlock_native(buffer, len);
        }
        
        public static void munmap(final MappedByteBuffer buffer) {
            if (CleanerUtil.UNMAP_SUPPORTED) {
                try {
                    CleanerUtil.getCleaner().freeBuffer(buffer);
                }
                catch (IOException e) {
                    POSIX.LOG.info("Failed to unmap the buffer", e);
                }
            }
            else {
                POSIX.LOG.trace(CleanerUtil.UNMAP_NOT_SUPPORTED_REASON);
            }
        }
        
        private static native long getUIDforFDOwnerforOwner(final FileDescriptor p0) throws IOException;
        
        private static native String getUserName(final long p0) throws IOException;
        
        public static Stat getFstat(final FileDescriptor fd) throws IOException {
            Stat stat = null;
            if (!Shell.WINDOWS) {
                stat = fstat(fd);
                stat.owner = getName(IdCache.USER, stat.ownerId);
                stat.group = getName(IdCache.GROUP, stat.groupId);
            }
            else {
                try {
                    stat = fstat(fd);
                }
                catch (NativeIOException nioe) {
                    if (nioe.getErrorCode() == 6L) {
                        throw new NativeIOException("The handle is invalid.", Errno.EBADF);
                    }
                    POSIX.LOG.warn(String.format("NativeIO.getFstat error (%d): %s", nioe.getErrorCode(), nioe.getMessage()));
                    throw new NativeIOException("Unknown error", Errno.UNKNOWN);
                }
            }
            return stat;
        }
        
        public static Stat getStat(final String path) throws IOException {
            if (path == null) {
                final String errMessage = "Path is null";
                POSIX.LOG.warn(errMessage);
                throw new IOException(errMessage);
            }
            Stat stat = null;
            try {
                if (!Shell.WINDOWS) {
                    stat = stat(path);
                    stat.owner = getName(IdCache.USER, stat.ownerId);
                    stat.group = getName(IdCache.GROUP, stat.groupId);
                }
                else {
                    stat = stat(path);
                }
            }
            catch (NativeIOException nioe) {
                POSIX.LOG.warn("NativeIO.getStat error ({}): {} -- file path: {}", nioe.getErrorCode(), nioe.getMessage(), path);
                throw new PathIOException(path, nioe);
            }
            return stat;
        }
        
        private static String getName(final IdCache domain, final int id) throws IOException {
            final Map<Integer, CachedName> idNameCache = (domain == IdCache.USER) ? POSIX.USER_ID_NAME_CACHE : POSIX.GROUP_ID_NAME_CACHE;
            CachedName cachedName = idNameCache.get(id);
            final long now = System.currentTimeMillis();
            String name;
            if (cachedName != null && cachedName.timestamp + POSIX.cacheTimeout > now) {
                name = cachedName.name;
            }
            else {
                name = ((domain == IdCache.USER) ? getUserName(id) : getGroupName(id));
                if (POSIX.LOG.isDebugEnabled()) {
                    final String type = (domain == IdCache.USER) ? "UserName" : "GroupName";
                    POSIX.LOG.debug("Got " + type + " " + name + " for ID " + id + " from the native implementation");
                }
                cachedName = new CachedName(name, now);
                idNameCache.put(id, cachedName);
            }
            return name;
        }
        
        static native String getUserName(final int p0) throws IOException;
        
        static native String getGroupName(final int p0) throws IOException;
        
        public static native long mmap(final FileDescriptor p0, final int p1, final boolean p2, final long p3) throws IOException;
        
        public static native void munmap(final long p0, final long p1) throws IOException;
        
        static {
            POSIX.O_RDONLY = -1;
            POSIX.O_WRONLY = -1;
            POSIX.O_RDWR = -1;
            POSIX.O_CREAT = -1;
            POSIX.O_EXCL = -1;
            POSIX.O_NOCTTY = -1;
            POSIX.O_TRUNC = -1;
            POSIX.O_APPEND = -1;
            POSIX.O_NONBLOCK = -1;
            POSIX.O_SYNC = -1;
            POSIX.POSIX_FADV_NORMAL = -1;
            POSIX.POSIX_FADV_RANDOM = -1;
            POSIX.POSIX_FADV_SEQUENTIAL = -1;
            POSIX.POSIX_FADV_WILLNEED = -1;
            POSIX.POSIX_FADV_DONTNEED = -1;
            POSIX.POSIX_FADV_NOREUSE = -1;
            POSIX.SYNC_FILE_RANGE_WAIT_BEFORE = 1;
            POSIX.SYNC_FILE_RANGE_WRITE = 2;
            POSIX.SYNC_FILE_RANGE_WAIT_AFTER = 4;
            LOG = LoggerFactory.getLogger(NativeIO.class);
            POSIX.fadvisePossible = false;
            POSIX.nativeLoaded = false;
            POSIX.syncFileRangePossible = true;
            POSIX.cacheTimeout = -1L;
            POSIX.cacheManipulator = new CacheManipulator();
            if (NativeCodeLoader.isNativeCodeLoaded()) {
                try {
                    final Configuration conf = new Configuration();
                    NativeIO.workaroundNonThreadSafePasswdCalls = conf.getBoolean("hadoop.workaround.non.threadsafe.getpwuid", true);
                    initNative();
                    POSIX.nativeLoaded = true;
                    POSIX.cacheTimeout = conf.getLong("hadoop.security.uid.cache.secs", 14400L) * 1000L;
                    POSIX.LOG.debug("Initialized cache for IDs to User/Group mapping with a  cache timeout of " + POSIX.cacheTimeout / 1000L + " seconds.");
                }
                catch (Throwable t) {
                    PerformanceAdvisory.LOG.debug("Unable to initialize NativeIO libraries", t);
                }
            }
            USER_ID_NAME_CACHE = new ConcurrentHashMap<Integer, CachedName>();
            GROUP_ID_NAME_CACHE = new ConcurrentHashMap<Integer, CachedName>();
        }
        
        @VisibleForTesting
        public static class CacheManipulator
        {
            public void mlock(final String identifier, final ByteBuffer buffer, final long len) throws IOException {
                POSIX.mlock(buffer, len);
            }
            
            public long getMemlockLimit() {
                return NativeIO.getMemlockLimit();
            }
            
            public long getOperatingSystemPageSize() {
                return NativeIO.getOperatingSystemPageSize();
            }
            
            public void posixFadviseIfPossible(final String identifier, final FileDescriptor fd, final long offset, final long len, final int flags) throws NativeIOException {
                POSIX.posixFadviseIfPossible(identifier, fd, offset, len, flags);
            }
            
            public boolean verifyCanMlock() {
                return NativeIO.isAvailable();
            }
        }
        
        @VisibleForTesting
        public static class NoMlockCacheManipulator extends CacheManipulator
        {
            @Override
            public void mlock(final String identifier, final ByteBuffer buffer, final long len) throws IOException {
                POSIX.LOG.info("mlocking " + identifier);
            }
            
            @Override
            public long getMemlockLimit() {
                return 1125899906842624L;
            }
            
            @Override
            public long getOperatingSystemPageSize() {
                return 4096L;
            }
            
            @Override
            public boolean verifyCanMlock() {
                return true;
            }
        }
        
        public static class Stat
        {
            private int ownerId;
            private int groupId;
            private String owner;
            private String group;
            private int mode;
            public static int S_IFMT;
            public static int S_IFIFO;
            public static int S_IFCHR;
            public static int S_IFDIR;
            public static int S_IFBLK;
            public static int S_IFREG;
            public static int S_IFLNK;
            public static int S_IFSOCK;
            public static int S_ISUID;
            public static int S_ISGID;
            public static int S_ISVTX;
            public static int S_IRUSR;
            public static int S_IWUSR;
            public static int S_IXUSR;
            
            Stat(final int ownerId, final int groupId, final int mode) {
                this.ownerId = ownerId;
                this.groupId = groupId;
                this.mode = mode;
            }
            
            Stat(final String owner, final String group, final int mode) {
                if (!Shell.WINDOWS) {
                    this.owner = owner;
                }
                else {
                    this.owner = stripDomain(owner);
                }
                if (!Shell.WINDOWS) {
                    this.group = group;
                }
                else {
                    this.group = stripDomain(group);
                }
                this.mode = mode;
            }
            
            @Override
            public String toString() {
                return "Stat(owner='" + this.owner + "', group='" + this.group + "', mode=" + this.mode + ")";
            }
            
            public String getOwner() {
                return this.owner;
            }
            
            public String getGroup() {
                return this.group;
            }
            
            public int getMode() {
                return this.mode;
            }
            
            static {
                Stat.S_IFMT = -1;
                Stat.S_IFIFO = -1;
                Stat.S_IFCHR = -1;
                Stat.S_IFDIR = -1;
                Stat.S_IFBLK = -1;
                Stat.S_IFREG = -1;
                Stat.S_IFLNK = -1;
                Stat.S_IFSOCK = -1;
                Stat.S_ISUID = -1;
                Stat.S_ISGID = -1;
                Stat.S_ISVTX = -1;
                Stat.S_IRUSR = -1;
                Stat.S_IWUSR = -1;
                Stat.S_IXUSR = -1;
            }
        }
        
        private static class CachedName
        {
            final long timestamp;
            final String name;
            
            public CachedName(final String name, final long timestamp) {
                this.name = name;
                this.timestamp = timestamp;
            }
        }
        
        private enum IdCache
        {
            USER, 
            GROUP;
        }
    }
    
    public static class Windows
    {
        public static final long GENERIC_READ = 2147483648L;
        public static final long GENERIC_WRITE = 1073741824L;
        public static final long FILE_SHARE_READ = 1L;
        public static final long FILE_SHARE_WRITE = 2L;
        public static final long FILE_SHARE_DELETE = 4L;
        public static final long CREATE_NEW = 1L;
        public static final long CREATE_ALWAYS = 2L;
        public static final long OPEN_EXISTING = 3L;
        public static final long OPEN_ALWAYS = 4L;
        public static final long TRUNCATE_EXISTING = 5L;
        public static final long FILE_BEGIN = 0L;
        public static final long FILE_CURRENT = 1L;
        public static final long FILE_END = 2L;
        public static final long FILE_ATTRIBUTE_NORMAL = 128L;
        
        public static void createDirectoryWithMode(final File path, final int mode) throws IOException {
            createDirectoryWithMode0(path.getAbsolutePath(), mode);
        }
        
        private static native void createDirectoryWithMode0(final String p0, final int p1) throws NativeIOException;
        
        public static native FileDescriptor createFile(final String p0, final long p1, final long p2, final long p3) throws IOException;
        
        public static FileOutputStream createFileOutputStreamWithMode(final File path, final boolean append, final int mode) throws IOException {
            final long desiredAccess = 1073741824L;
            final long shareMode = 3L;
            final long creationDisposition = append ? 4L : 2L;
            return new FileOutputStream(createFileWithMode0(path.getAbsolutePath(), desiredAccess, shareMode, creationDisposition, mode));
        }
        
        private static native FileDescriptor createFileWithMode0(final String p0, final long p1, final long p2, final long p3, final int p4) throws NativeIOException;
        
        public static native long setFilePointer(final FileDescriptor p0, final long p1, final long p2) throws IOException;
        
        private static native String getOwner(final FileDescriptor p0) throws IOException;
        
        private static native boolean access0(final String p0, final int p1);
        
        public static boolean access(final String path, final AccessRight desiredAccess) throws IOException {
            return access0(path, desiredAccess.accessRight());
        }
        
        public static native void extendWorkingSetSize(final long p0) throws IOException;
        
        static {
            if (NativeCodeLoader.isNativeCodeLoaded()) {
                try {
                    initNative();
                    NativeIO.nativeLoaded = true;
                }
                catch (Throwable t) {
                    PerformanceAdvisory.LOG.debug("Unable to initialize NativeIO libraries", t);
                }
            }
        }
        
        public enum AccessRight
        {
            ACCESS_READ(1), 
            ACCESS_WRITE(2), 
            ACCESS_EXECUTE(32);
            
            private final int accessRight;
            
            private AccessRight(final int access) {
                this.accessRight = access;
            }
            
            public int accessRight() {
                return this.accessRight;
            }
        }
    }
    
    private static class CachedUid
    {
        final long timestamp;
        final String username;
        
        public CachedUid(final String username, final long timestamp) {
            this.timestamp = timestamp;
            this.username = username;
        }
    }
}
