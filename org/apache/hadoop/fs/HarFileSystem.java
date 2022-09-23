// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.LinkedHashMap;
import org.apache.hadoop.io.IOUtils;
import java.io.Closeable;
import org.apache.hadoop.util.LineReader;
import org.apache.hadoop.io.Text;
import java.util.HashMap;
import java.io.EOFException;
import java.io.InputStream;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.Collections;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;

public class HarFileSystem extends FileSystem
{
    private static final Logger LOG;
    public static final String METADATA_CACHE_ENTRIES_KEY = "fs.har.metadatacache.entries";
    public static final int METADATA_CACHE_ENTRIES_DEFAULT = 10;
    public static final int VERSION = 3;
    private static Map<URI, HarMetaData> harMetaCache;
    private URI uri;
    private Path archivePath;
    private String harAuth;
    private HarMetaData metadata;
    private FileSystem fs;
    
    public HarFileSystem() {
    }
    
    @Override
    public String getScheme() {
        return "har";
    }
    
    public HarFileSystem(final FileSystem fs) {
        this.fs = fs;
        this.statistics = fs.statistics;
    }
    
    private synchronized void initializeMetadataCache(final Configuration conf) {
        if (HarFileSystem.harMetaCache == null) {
            final int cacheSize = conf.getInt("fs.har.metadatacache.entries", 10);
            HarFileSystem.harMetaCache = Collections.synchronizedMap(new LruCache<URI, HarMetaData>(cacheSize));
        }
    }
    
    @Override
    public void initialize(final URI name, final Configuration conf) throws IOException {
        this.initializeMetadataCache(conf);
        final URI underLyingURI = this.decodeHarURI(name, conf);
        final Path harPath = this.archivePath(new Path(name.getScheme(), name.getAuthority(), name.getPath()));
        if (harPath == null) {
            throw new IOException("Invalid path for the Har Filesystem. " + name.toString());
        }
        if (this.fs == null) {
            this.fs = FileSystem.get(underLyingURI, conf);
        }
        this.uri = harPath.toUri();
        this.archivePath = new Path(this.uri.getPath());
        this.harAuth = this.getHarAuth(underLyingURI);
        final Path masterIndexPath = new Path(this.archivePath, "_masterindex");
        final Path archiveIndexPath = new Path(this.archivePath, "_index");
        if (!this.fs.exists(masterIndexPath) || !this.fs.exists(archiveIndexPath)) {
            throw new IOException("Invalid path for the Har Filesystem. No index file in " + harPath);
        }
        this.metadata = HarFileSystem.harMetaCache.get(this.uri);
        if (this.metadata != null) {
            final FileStatus mStat = this.fs.getFileStatus(masterIndexPath);
            final FileStatus aStat = this.fs.getFileStatus(archiveIndexPath);
            if (mStat.getModificationTime() != this.metadata.getMasterIndexTimestamp() || aStat.getModificationTime() != this.metadata.getArchiveIndexTimestamp()) {
                this.metadata = null;
                HarFileSystem.harMetaCache.remove(this.uri);
            }
        }
        if (this.metadata == null) {
            (this.metadata = new HarMetaData(this.fs, masterIndexPath, archiveIndexPath)).parseMetaData();
            HarFileSystem.harMetaCache.put(this.uri, this.metadata);
        }
    }
    
    @Override
    public Configuration getConf() {
        return this.fs.getConf();
    }
    
    public int getHarVersion() throws IOException {
        if (this.metadata != null) {
            return this.metadata.getVersion();
        }
        throw new IOException("Invalid meta data for the Har Filesystem");
    }
    
    private Path archivePath(final Path p) {
        Path retPath = null;
        Path tmp = p;
        for (int i = 0; i < p.depth(); ++i) {
            if (tmp.toString().endsWith(".har")) {
                retPath = tmp;
                break;
            }
            tmp = tmp.getParent();
        }
        return retPath;
    }
    
    private URI decodeHarURI(final URI rawURI, final Configuration conf) throws IOException {
        final String tmpAuth = rawURI.getAuthority();
        if (tmpAuth == null) {
            return FileSystem.getDefaultUri(conf);
        }
        final String authority = rawURI.getAuthority();
        final int i = authority.indexOf(45);
        if (i < 0) {
            throw new IOException("URI: " + rawURI + " is an invalid Har URI since '-' not found.  Expecting har://<scheme>-<host>/<path>.");
        }
        if (rawURI.getQuery() != null) {
            throw new IOException("query component in Path not supported  " + rawURI);
        }
        URI tmp;
        try {
            final URI baseUri = new URI(authority.replaceFirst("-", "://"));
            tmp = new URI(baseUri.getScheme(), baseUri.getAuthority(), rawURI.getPath(), rawURI.getQuery(), rawURI.getFragment());
        }
        catch (URISyntaxException e) {
            throw new IOException("URI: " + rawURI + " is an invalid Har URI. Expecting har://<scheme>-<host>/<path>.");
        }
        return tmp;
    }
    
    private static String decodeString(final String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, "UTF-8");
    }
    
    private String decodeFileName(final String fname) throws UnsupportedEncodingException {
        final int version = this.metadata.getVersion();
        if (version == 2 || version == 3) {
            return decodeString(fname);
        }
        return fname;
    }
    
    @Override
    public Path getWorkingDirectory() {
        return new Path(this.uri.toString());
    }
    
    public Path getInitialWorkingDirectory() {
        return this.getWorkingDirectory();
    }
    
    @Override
    public FsStatus getStatus(final Path p) throws IOException {
        return this.fs.getStatus(p);
    }
    
    private String getHarAuth(final URI underLyingUri) {
        String auth = underLyingUri.getScheme() + "-";
        if (underLyingUri.getHost() != null) {
            if (underLyingUri.getUserInfo() != null) {
                auth += underLyingUri.getUserInfo();
                auth += "@";
            }
            auth += underLyingUri.getHost();
            if (underLyingUri.getPort() != -1) {
                auth += ":";
                auth += underLyingUri.getPort();
            }
        }
        else {
            auth += ":";
        }
        return auth;
    }
    
    @Override
    protected URI getCanonicalUri() {
        return this.fs.getCanonicalUri();
    }
    
    @Override
    protected URI canonicalizeUri(final URI uri) {
        return this.fs.canonicalizeUri(uri);
    }
    
    @Override
    public URI getUri() {
        return this.uri;
    }
    
    @Override
    protected void checkPath(final Path path) {
        this.fs.checkPath(path);
    }
    
    @Override
    public Path resolvePath(final Path p) throws IOException {
        return this.fs.resolvePath(p);
    }
    
    private Path getPathInHar(final Path path) {
        final Path harPath = new Path(path.toUri().getPath());
        if (this.archivePath.compareTo(harPath) == 0) {
            return new Path("/");
        }
        Path tmp = new Path(harPath.getName());
        for (Path parent = harPath.getParent(); parent.compareTo(this.archivePath) != 0; parent = parent.getParent()) {
            if (parent.toString().equals("/")) {
                tmp = null;
                break;
            }
            tmp = new Path(parent.getName(), tmp);
        }
        if (tmp != null) {
            tmp = new Path("/", tmp);
        }
        return tmp;
    }
    
    private Path makeRelative(final String initial, final Path p) {
        final String scheme = this.uri.getScheme();
        final String authority = this.uri.getAuthority();
        final Path root = new Path("/");
        if (root.compareTo(p) == 0) {
            return new Path(scheme, authority, initial);
        }
        Path retPath = new Path(p.getName());
        Path parent = p.getParent();
        for (int i = 0; i < p.depth() - 1; ++i) {
            retPath = new Path(parent.getName(), retPath);
            parent = parent.getParent();
        }
        return new Path(new Path(scheme, authority, initial), retPath.toString());
    }
    
    @Override
    public Path makeQualified(final Path path) {
        Path fsPath = path;
        if (!path.isAbsolute()) {
            fsPath = new Path(this.archivePath, path);
        }
        final URI tmpURI = fsPath.toUri();
        return new Path(this.uri.getScheme(), this.harAuth, tmpURI.getPath());
    }
    
    static BlockLocation[] fixBlockLocations(final BlockLocation[] locations, final long start, final long len, final long fileOffsetInHar) {
        final long end = start + len;
        for (final BlockLocation location : locations) {
            final long harBlockStart = location.getOffset() - fileOffsetInHar;
            final long harBlockEnd = harBlockStart + location.getLength();
            if (start > harBlockStart) {
                location.setOffset(start);
                location.setLength(location.getLength() - (start - harBlockStart));
            }
            else {
                location.setOffset(harBlockStart);
            }
            if (harBlockEnd > end) {
                location.setLength(location.getLength() - (harBlockEnd - end));
            }
        }
        return locations;
    }
    
    @Override
    public BlockLocation[] getFileBlockLocations(final FileStatus file, final long start, final long len) throws IOException {
        final HarStatus hstatus = this.getFileHarStatus(file.getPath());
        final Path partPath = new Path(this.archivePath, hstatus.getPartName());
        final FileStatus partStatus = this.metadata.getPartFileStatus(partPath);
        final BlockLocation[] locations = this.fs.getFileBlockLocations(partStatus, hstatus.getStartIndex() + start, len);
        return fixBlockLocations(locations, start, len, hstatus.getStartIndex());
    }
    
    public static int getHarHash(final Path p) {
        return p.toString().hashCode() & Integer.MAX_VALUE;
    }
    
    private void fileStatusesInIndex(final HarStatus parent, final List<FileStatus> statuses) throws IOException {
        String parentString = parent.getName();
        if (!parentString.endsWith("/")) {
            parentString += "/";
        }
        final Path harPath = new Path(parentString);
        final int harlen = harPath.depth();
        final Map<String, FileStatus> cache = new TreeMap<String, FileStatus>();
        for (final HarStatus hstatus : this.metadata.archive.values()) {
            final String child = hstatus.getName();
            if (child.startsWith(parentString)) {
                final Path thisPath = new Path(child);
                if (thisPath.depth() != harlen + 1) {
                    continue;
                }
                statuses.add(this.toFileStatus(hstatus, cache));
            }
        }
    }
    
    private FileStatus toFileStatus(final HarStatus h, final Map<String, FileStatus> cache) throws IOException {
        FileStatus underlying = null;
        if (cache != null) {
            underlying = cache.get(h.partName);
        }
        if (underlying == null) {
            final Path p = h.isDir ? this.archivePath : new Path(this.archivePath, h.partName);
            underlying = this.fs.getFileStatus(p);
            if (cache != null) {
                cache.put(h.partName, underlying);
            }
        }
        long modTime = 0L;
        final int version = this.metadata.getVersion();
        if (version < 3) {
            modTime = underlying.getModificationTime();
        }
        else if (version == 3) {
            modTime = h.getModificationTime();
        }
        return new FileStatus(h.isDir() ? 0L : h.getLength(), h.isDir(), underlying.getReplication(), underlying.getBlockSize(), modTime, underlying.getAccessTime(), underlying.getPermission(), underlying.getOwner(), underlying.getGroup(), this.makeRelative(this.uri.getPath(), new Path(h.name)));
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws IOException {
        final HarStatus hstatus = this.getFileHarStatus(f);
        return this.toFileStatus(hstatus, null);
    }
    
    private HarStatus getFileHarStatus(final Path f) throws IOException {
        final Path p = this.makeQualified(f);
        final Path harPath = this.getPathInHar(p);
        if (harPath == null) {
            throw new IOException("Invalid file name: " + f + " in " + this.uri);
        }
        final HarStatus hstatus = this.metadata.archive.get(harPath);
        if (hstatus == null) {
            throw new FileNotFoundException("File: " + f + " does not exist in " + this.uri);
        }
        return hstatus;
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f, final long length) {
        return null;
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException {
        final HarStatus hstatus = this.getFileHarStatus(f);
        if (hstatus.isDir()) {
            throw new FileNotFoundException(f + " : not a file in " + this.archivePath);
        }
        return new HarFSDataInputStream(this.fs, new Path(this.archivePath, hstatus.getPartName()), hstatus.getStartIndex(), hstatus.getLength(), bufferSize);
    }
    
    @Override
    protected PathHandle createPathHandle(final FileStatus stat, final Options.HandleOpt... opts) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public FSDataInputStream open(final PathHandle fd, final int bufferSize) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public FileSystem[] getChildFileSystems() {
        return new FileSystem[] { this.fs };
    }
    
    @Override
    public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        throw new IOException("Har: create not allowed.");
    }
    
    @Override
    public FSDataOutputStream createNonRecursive(final Path f, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        throw new IOException("Har: create not allowed.");
    }
    
    @Override
    public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
        throw new IOException("Har: append not allowed.");
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        if (this.fs != null) {
            try {
                this.fs.close();
            }
            catch (IOException ex) {}
        }
    }
    
    @Override
    public boolean setReplication(final Path src, final short replication) throws IOException {
        throw new IOException("Har: setReplication not allowed");
    }
    
    @Override
    public boolean rename(final Path src, final Path dst) throws IOException {
        throw new IOException("Har: rename not allowed");
    }
    
    @Override
    public FSDataOutputStream append(final Path f) throws IOException {
        throw new IOException("Har: append not allowed");
    }
    
    @Override
    public boolean truncate(final Path f, final long newLength) throws IOException {
        throw new IOException("Har: truncate not allowed");
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws IOException {
        throw new IOException("Har: delete not allowed");
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws IOException {
        final List<FileStatus> statuses = new ArrayList<FileStatus>();
        final Path tmpPath = this.makeQualified(f);
        final Path harPath = this.getPathInHar(tmpPath);
        final HarStatus hstatus = this.metadata.archive.get(harPath);
        if (hstatus == null) {
            throw new FileNotFoundException("File " + f + " not found in " + this.archivePath);
        }
        if (hstatus.isDir()) {
            this.fileStatusesInIndex(hstatus, statuses);
        }
        else {
            statuses.add(this.toFileStatus(hstatus, null));
        }
        return statuses.toArray(new FileStatus[statuses.size()]);
    }
    
    @Override
    public Path getHomeDirectory() {
        return new Path(this.uri.toString());
    }
    
    @Override
    public void setWorkingDirectory(final Path newDir) {
    }
    
    @Override
    public boolean mkdirs(final Path f, final FsPermission permission) throws IOException {
        throw new IOException("Har: mkdirs not allowed");
    }
    
    @Override
    public void copyFromLocalFile(final boolean delSrc, final boolean overwrite, final Path src, final Path dst) throws IOException {
        throw new IOException("Har: copyfromlocalfile not allowed");
    }
    
    @Override
    public void copyFromLocalFile(final boolean delSrc, final boolean overwrite, final Path[] srcs, final Path dst) throws IOException {
        throw new IOException("Har: copyfromlocalfile not allowed");
    }
    
    @Override
    public void copyToLocalFile(final boolean delSrc, final Path src, final Path dst) throws IOException {
        FileUtil.copy(this, src, FileSystem.getLocal(this.getConf()), dst, false, this.getConf());
    }
    
    @Override
    public Path startLocalOutput(final Path fsOutputFile, final Path tmpLocalFile) throws IOException {
        throw new IOException("Har: startLocalOutput not allowed");
    }
    
    @Override
    public void completeLocalOutput(final Path fsOutputFile, final Path tmpLocalFile) throws IOException {
        throw new IOException("Har: completeLocalOutput not allowed");
    }
    
    @Override
    public void setOwner(final Path p, final String username, final String groupname) throws IOException {
        throw new IOException("Har: setowner not allowed");
    }
    
    @Override
    public void setTimes(final Path p, final long mtime, final long atime) throws IOException {
        throw new IOException("Har: setTimes not allowed");
    }
    
    @Override
    public void setPermission(final Path p, final FsPermission permission) throws IOException {
        throw new IOException("Har: setPermission not allowed");
    }
    
    HarMetaData getMetadata() {
        return this.metadata;
    }
    
    @Override
    public FsServerDefaults getServerDefaults() throws IOException {
        return this.fs.getServerDefaults();
    }
    
    @Override
    public FsServerDefaults getServerDefaults(final Path f) throws IOException {
        return this.fs.getServerDefaults(f);
    }
    
    @Override
    public long getUsed() throws IOException {
        return this.fs.getUsed();
    }
    
    @Override
    public long getUsed(final Path path) throws IOException {
        return this.fs.getUsed(path);
    }
    
    @Override
    public long getDefaultBlockSize() {
        return this.fs.getDefaultBlockSize();
    }
    
    @Override
    public long getDefaultBlockSize(final Path f) {
        return this.fs.getDefaultBlockSize(f);
    }
    
    @Override
    public short getDefaultReplication() {
        return this.fs.getDefaultReplication();
    }
    
    @Override
    public short getDefaultReplication(final Path f) {
        return this.fs.getDefaultReplication(f);
    }
    
    @Override
    public FSDataOutputStreamBuilder createFile(final Path path) {
        return this.fs.createFile(path);
    }
    
    @Override
    public FSDataOutputStreamBuilder appendFile(final Path path) {
        return this.fs.appendFile(path);
    }
    
    static {
        LOG = LoggerFactory.getLogger(HarFileSystem.class);
    }
    
    static class Store
    {
        public long begin;
        public long end;
        
        public Store(final long begin, final long end) {
            this.begin = begin;
            this.end = end;
        }
    }
    
    private class HarStatus
    {
        boolean isDir;
        String name;
        List<String> children;
        String partName;
        long startIndex;
        long length;
        long modificationTime;
        
        public HarStatus(final String harString) throws UnsupportedEncodingException {
            this.modificationTime = 0L;
            final String[] splits = harString.split(" ");
            this.name = HarFileSystem.this.decodeFileName(splits[0]);
            this.isDir = "dir".equals(splits[1]);
            this.partName = splits[2];
            this.startIndex = Long.parseLong(splits[3]);
            this.length = Long.parseLong(splits[4]);
            final int version = HarFileSystem.this.metadata.getVersion();
            String[] propSplits = null;
            if (this.isDir) {
                if (version == 3) {
                    propSplits = decodeString(this.partName).split(" ");
                }
                this.children = new ArrayList<String>();
                for (int i = 5; i < splits.length; ++i) {
                    this.children.add(HarFileSystem.this.decodeFileName(splits[i]));
                }
            }
            else if (version == 3) {
                propSplits = decodeString(splits[5]).split(" ");
            }
            if (propSplits != null && propSplits.length >= 4) {
                this.modificationTime = Long.parseLong(propSplits[0]);
            }
        }
        
        public boolean isDir() {
            return this.isDir;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getPartName() {
            return this.partName;
        }
        
        public long getStartIndex() {
            return this.startIndex;
        }
        
        public long getLength() {
            return this.length;
        }
        
        public long getModificationTime() {
            return this.modificationTime;
        }
    }
    
    private static class HarFSDataInputStream extends FSDataInputStream
    {
        public HarFSDataInputStream(final FileSystem fs, final Path p, final long start, final long length, final int bufsize) throws IOException {
            super(new HarFsInputStream(fs, p, start, length, bufsize));
        }
        
        private static class HarFsInputStream extends FSInputStream implements CanSetDropBehind, CanSetReadahead
        {
            private long position;
            private long start;
            private long end;
            private final FSDataInputStream underLyingStream;
            private final byte[] oneBytebuff;
            
            HarFsInputStream(final FileSystem fs, final Path path, final long start, final long length, final int bufferSize) throws IOException {
                this.oneBytebuff = new byte[1];
                if (length < 0L) {
                    throw new IllegalArgumentException("Negative length [" + length + "]");
                }
                (this.underLyingStream = fs.open(path, bufferSize)).seek(start);
                this.start = start;
                this.position = start;
                this.end = start + length;
            }
            
            @Override
            public synchronized int available() throws IOException {
                final long remaining = this.end - this.underLyingStream.getPos();
                if (remaining > 2147483647L) {
                    return Integer.MAX_VALUE;
                }
                return (int)remaining;
            }
            
            @Override
            public synchronized void close() throws IOException {
                this.underLyingStream.close();
                super.close();
            }
            
            @Override
            public void mark(final int readLimit) {
            }
            
            @Override
            public void reset() throws IOException {
                throw new IOException("reset not implemented.");
            }
            
            @Override
            public synchronized int read() throws IOException {
                final int ret = this.read(this.oneBytebuff, 0, 1);
                return (ret <= 0) ? -1 : (this.oneBytebuff[0] & 0xFF);
            }
            
            @Override
            public synchronized int read(final byte[] b) throws IOException {
                final int ret = this.read(b, 0, b.length);
                return ret;
            }
            
            @Override
            public synchronized int read(final byte[] b, final int offset, final int len) throws IOException {
                if (len == 0) {
                    return 0;
                }
                int newlen = len;
                int ret = -1;
                if (this.position + len > this.end) {
                    newlen = (int)(this.end - this.position);
                }
                if (newlen == 0) {
                    return ret;
                }
                ret = this.underLyingStream.read(b, offset, newlen);
                this.position += ret;
                return ret;
            }
            
            @Override
            public synchronized long skip(final long n) throws IOException {
                long tmpN = n;
                if (tmpN > 0L) {
                    final long actualRemaining = this.end - this.position;
                    if (tmpN > actualRemaining) {
                        tmpN = actualRemaining;
                    }
                    this.underLyingStream.seek(tmpN + this.position);
                    this.position += tmpN;
                    return tmpN;
                }
                return 0L;
            }
            
            @Override
            public synchronized long getPos() throws IOException {
                return this.position - this.start;
            }
            
            @Override
            public synchronized void seek(final long pos) throws IOException {
                this.validatePosition(pos);
                this.position = this.start + pos;
                this.underLyingStream.seek(this.position);
            }
            
            private void validatePosition(final long pos) throws IOException {
                if (pos < 0L) {
                    throw new IOException("Negative position: " + pos);
                }
                final long length = this.end - this.start;
                if (pos > length) {
                    throw new IOException("Position behind the end of the stream (length = " + length + "): " + pos);
                }
            }
            
            @Override
            public boolean seekToNewSource(final long targetPos) throws IOException {
                return false;
            }
            
            @Override
            public int read(final long pos, final byte[] b, final int offset, final int length) throws IOException {
                int nlength = length;
                if (this.start + nlength + pos > this.end) {
                    nlength = (int)(this.end - this.start - pos);
                }
                if (nlength <= 0) {
                    return -1;
                }
                return this.underLyingStream.read(pos + this.start, b, offset, nlength);
            }
            
            @Override
            public void readFully(final long pos, final byte[] b, final int offset, final int length) throws IOException {
                this.validatePositionedReadArgs(pos, b, offset, length);
                if (length == 0) {
                    return;
                }
                if (this.start + length + pos > this.end) {
                    throw new EOFException("Not enough bytes to read.");
                }
                this.underLyingStream.readFully(pos + this.start, b, offset, length);
            }
            
            @Override
            public void setReadahead(final Long readahead) throws IOException {
                this.underLyingStream.setReadahead(readahead);
            }
            
            @Override
            public void setDropBehind(final Boolean dropBehind) throws IOException {
                this.underLyingStream.setDropBehind(dropBehind);
            }
        }
    }
    
    private class HarMetaData
    {
        private FileSystem fs;
        private int version;
        private Path masterIndexPath;
        private Path archiveIndexPath;
        private long masterIndexTimestamp;
        private long archiveIndexTimestamp;
        List<Store> stores;
        Map<Path, HarStatus> archive;
        private Map<Path, FileStatus> partFileStatuses;
        
        public HarMetaData(final FileSystem fs, final Path masterIndexPath, final Path archiveIndexPath) {
            this.stores = new ArrayList<Store>();
            this.archive = new HashMap<Path, HarStatus>();
            this.partFileStatuses = new HashMap<Path, FileStatus>();
            this.fs = fs;
            this.masterIndexPath = masterIndexPath;
            this.archiveIndexPath = archiveIndexPath;
        }
        
        public FileStatus getPartFileStatus(final Path partPath) throws IOException {
            FileStatus status = this.partFileStatuses.get(partPath);
            if (status == null) {
                status = this.fs.getFileStatus(partPath);
                this.partFileStatuses.put(partPath, status);
            }
            return status;
        }
        
        public long getMasterIndexTimestamp() {
            return this.masterIndexTimestamp;
        }
        
        public long getArchiveIndexTimestamp() {
            return this.archiveIndexTimestamp;
        }
        
        private int getVersion() {
            return this.version;
        }
        
        private void parseMetaData() throws IOException {
            final Text line = new Text();
            FSDataInputStream in = null;
            LineReader lin = null;
            try {
                in = this.fs.open(this.masterIndexPath);
                final FileStatus masterStat = this.fs.getFileStatus(this.masterIndexPath);
                this.masterIndexTimestamp = masterStat.getModificationTime();
                lin = new LineReader(in, HarFileSystem.this.getConf());
                long read = lin.readLine(line);
                final String versionLine = line.toString();
                final String[] arr = versionLine.split(" ");
                this.version = Integer.parseInt(arr[0]);
                if (this.version > 3) {
                    throw new IOException("Invalid version " + this.version + " expected " + 3);
                }
                while (read < masterStat.getLen()) {
                    final int b = lin.readLine(line);
                    read += b;
                    final String[] readStr = line.toString().split(" ");
                    this.stores.add(new Store(Long.parseLong(readStr[2]), Long.parseLong(readStr[3])));
                    line.clear();
                }
            }
            catch (IOException ioe) {
                HarFileSystem.LOG.warn("Encountered exception ", ioe);
                throw ioe;
            }
            finally {
                IOUtils.cleanupWithLogger(HarFileSystem.LOG, lin, in);
            }
            final FSDataInputStream aIn = this.fs.open(this.archiveIndexPath);
            try {
                final FileStatus archiveStat = this.fs.getFileStatus(this.archiveIndexPath);
                this.archiveIndexTimestamp = archiveStat.getModificationTime();
                for (final Store s : this.stores) {
                    long read = 0L;
                    aIn.seek(s.begin);
                    final LineReader aLin = new LineReader(aIn, HarFileSystem.this.getConf());
                    while (read + s.begin < s.end) {
                        final int tmp = aLin.readLine(line);
                        read += tmp;
                        final String lineFeed = line.toString();
                        final String[] parsed = lineFeed.split(" ");
                        parsed[0] = HarFileSystem.this.decodeFileName(parsed[0]);
                        this.archive.put(new Path(parsed[0]), new HarStatus(lineFeed));
                        line.clear();
                    }
                }
            }
            finally {
                IOUtils.cleanupWithLogger(HarFileSystem.LOG, aIn);
            }
        }
    }
    
    private static class LruCache<K, V> extends LinkedHashMap<K, V>
    {
        private final int MAX_ENTRIES;
        
        public LruCache(final int maxEntries) {
            super(maxEntries + 1, 1.0f, true);
            this.MAX_ENTRIES = maxEntries;
        }
        
        @Override
        protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
            return this.size() > this.MAX_ENTRIES;
        }
    }
}
