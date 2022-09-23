// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.viewfs;

import java.util.BitSet;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.RemoteIterator;
import java.io.OutputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.io.MultipleIOException;
import java.util.Arrays;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.net.NodeBase;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.net.ScriptBasedMapping;
import org.apache.hadoop.net.DNSToSwitchMapping;
import java.util.ArrayList;
import java.net.InetAddress;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetworkTopology;
import org.apache.hadoop.net.Node;
import java.util.EnumSet;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.FileSystem;

@InterfaceAudience.Private
final class NflyFSystem extends FileSystem
{
    private static final Log LOG;
    private static final String NFLY_TMP_PREFIX = "_nfly_tmp_";
    private static final int DEFAULT_MIN_REPLICATION = 2;
    private static URI nflyURI;
    private final NflyNode[] nodes;
    private final int minReplication;
    private final EnumSet<NflyKey> nflyFlags;
    private final Node myNode;
    private final NetworkTopology topology;
    
    private MRNflyNode[] workSet() {
        final MRNflyNode[] res = new MRNflyNode[this.nodes.length];
        for (int i = 0; i < res.length; ++i) {
            res[i] = new MRNflyNode(this.nodes[i]);
        }
        return res;
    }
    
    private static String getRack(final String rackString) {
        return (rackString == null) ? "/default-rack" : rackString;
    }
    
    private NflyFSystem(final URI[] uris, final Configuration conf, final int minReplication, final EnumSet<NflyKey> nflyFlags) throws IOException {
        if (uris.length < minReplication) {
            throw new IOException(minReplication + " < " + uris.length + ": Minimum replication < #destinations");
        }
        this.setConf(conf);
        final String localHostName = InetAddress.getLocalHost().getHostName();
        final List<String> hostStrings = new ArrayList<String>(uris.length + 1);
        for (final URI uri : uris) {
            final String uriHost = uri.getHost();
            hostStrings.add((uriHost == null) ? localHostName : uriHost);
        }
        hostStrings.add(localHostName);
        final DNSToSwitchMapping tmpDns = ReflectionUtils.newInstance(conf.getClass("net.topology.node.switch.mapping.impl", ScriptBasedMapping.class, DNSToSwitchMapping.class), conf);
        final List<String> rackStrings = tmpDns.resolve(hostStrings);
        this.nodes = new NflyNode[uris.length];
        final Iterator<String> rackIter = rackStrings.iterator();
        for (int i = 0; i < this.nodes.length; ++i) {
            this.nodes[i] = new NflyNode(hostStrings.get(i), rackIter.next(), uris[i], conf);
        }
        this.myNode = new NodeBase(localHostName, getRack(rackIter.next()));
        (this.topology = NetworkTopology.getInstance(conf)).sortByDistance(this.myNode, this.nodes, this.nodes.length);
        this.minReplication = minReplication;
        this.nflyFlags = nflyFlags;
        this.statistics = FileSystem.getStatistics(NflyFSystem.nflyURI.getScheme(), this.getClass());
    }
    
    private Path getNflyTmpPath(final Path f) {
        return new Path(f.getParent(), "_nfly_tmp_" + f.getName());
    }
    
    @Override
    public URI getUri() {
        return NflyFSystem.nflyURI;
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException {
        final List<IOException> ioExceptions = new ArrayList<IOException>(this.nodes.length);
        int numNotFounds = 0;
        final MRNflyNode[] workSet;
        final MRNflyNode[] mrNodes = workSet = this.workSet();
        for (final MRNflyNode nflyNode : workSet) {
            try {
                if (!this.nflyFlags.contains(NflyKey.repairOnRead) && !this.nflyFlags.contains(NflyKey.readMostRecent)) {
                    return nflyNode.getFs().open(f, bufferSize);
                }
                nflyNode.updateFileStatus(f);
            }
            catch (FileNotFoundException fnfe) {
                nflyNode.status = notFoundStatus(f);
                ++numNotFounds;
                processThrowable(nflyNode, "open", fnfe, ioExceptions, f);
            }
            catch (Throwable t) {
                processThrowable(nflyNode, "open", t, ioExceptions, f);
            }
        }
        if (this.nflyFlags.contains(NflyKey.readMostRecent)) {
            Arrays.sort(mrNodes);
        }
        final FSDataInputStream fsdisAfterRepair = this.repairAndOpen(mrNodes, f, bufferSize);
        if (fsdisAfterRepair != null) {
            return fsdisAfterRepair;
        }
        this.mayThrowFileNotFound(ioExceptions, numNotFounds);
        throw MultipleIOException.createIOException(ioExceptions);
    }
    
    private static FileStatus notFoundStatus(final Path f) {
        return new FileStatus(-1L, false, 0, 0L, 0L, f);
    }
    
    private FSDataInputStream repairAndOpen(final MRNflyNode[] mrNodes, final Path f, final int bufferSize) {
        long maxMtime = 0L;
        for (final MRNflyNode srcNode : mrNodes) {
            if (srcNode.status != null) {
                if (srcNode.status.getLen() >= 0L) {
                    if (srcNode.status.getModificationTime() > maxMtime) {
                        maxMtime = srcNode.status.getModificationTime();
                    }
                    for (final MRNflyNode dstNode : mrNodes) {
                        if (dstNode.status != null) {
                            if (srcNode.compareTo(dstNode) != 0) {
                                try {
                                    final FileStatus srcStatus = srcNode.cloneStatus();
                                    srcStatus.setPath(f);
                                    final Path tmpPath = this.getNflyTmpPath(f);
                                    FileUtil.copy(srcNode.getFs(), srcStatus, dstNode.getFs(), tmpPath, false, true, this.getConf());
                                    dstNode.getFs().delete(f, false);
                                    if (dstNode.getFs().rename(tmpPath, f)) {
                                        try {
                                            dstNode.getFs().setTimes(f, srcNode.status.getModificationTime(), srcNode.status.getAccessTime());
                                        }
                                        finally {
                                            srcStatus.setPath(dstNode.getFs().makeQualified(f));
                                            dstNode.status = srcStatus;
                                        }
                                    }
                                }
                                catch (IOException ioe) {
                                    NflyFSystem.LOG.info(f + " " + srcNode + "->" + dstNode + ": Failed to repair", ioe);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (maxMtime > 0L) {
            final List<MRNflyNode> mrList = new ArrayList<MRNflyNode>();
            for (final MRNflyNode openNode : mrNodes) {
                if (openNode.status != null && openNode.status.getLen() >= 0L && openNode.status.getModificationTime() == maxMtime) {
                    mrList.add(openNode);
                }
            }
            final MRNflyNode[] readNodes = mrList.toArray(new MRNflyNode[0]);
            this.topology.sortByDistance(this.myNode, readNodes, readNodes.length);
            final MRNflyNode[] array = readNodes;
            final int length4 = array.length;
            int l = 0;
            while (l < length4) {
                final MRNflyNode rNode = array[l];
                try {
                    return rNode.getFs().open(f, bufferSize);
                }
                catch (IOException e) {
                    NflyFSystem.LOG.info(f + ": Failed to open at " + rNode.getFs().getUri());
                    ++l;
                    continue;
                }
                break;
            }
        }
        return null;
    }
    
    private void mayThrowFileNotFound(final List<IOException> ioExceptions, final int numNotFounds) throws FileNotFoundException {
        if (numNotFounds == this.nodes.length) {
            throw (FileNotFoundException)ioExceptions.get(this.nodes.length - 1);
        }
    }
    
    @Override
    public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return new FSDataOutputStream(new NflyOutputStream(f, permission, overwrite, bufferSize, replication, blockSize, progress), this.statistics);
    }
    
    @Override
    public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
        return null;
    }
    
    @Override
    public boolean rename(final Path src, final Path dst) throws IOException {
        final List<IOException> ioExceptions = new ArrayList<IOException>();
        int numNotFounds = 0;
        boolean succ = true;
        for (final NflyNode nflyNode : this.nodes) {
            try {
                succ &= nflyNode.fs.rename(src, dst);
            }
            catch (FileNotFoundException fnfe) {
                ++numNotFounds;
                processThrowable(nflyNode, "rename", fnfe, ioExceptions, src, dst);
            }
            catch (Throwable t) {
                processThrowable(nflyNode, "rename", t, ioExceptions, src, dst);
                succ = false;
            }
        }
        this.mayThrowFileNotFound(ioExceptions, numNotFounds);
        if (ioExceptions.size() == this.nodes.length) {
            throw MultipleIOException.createIOException(ioExceptions);
        }
        return succ;
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws IOException {
        final List<IOException> ioExceptions = new ArrayList<IOException>();
        int numNotFounds = 0;
        boolean succ = true;
        for (final NflyNode nflyNode : this.nodes) {
            try {
                succ &= nflyNode.fs.delete(f);
            }
            catch (FileNotFoundException fnfe) {
                ++numNotFounds;
                processThrowable(nflyNode, "delete", fnfe, ioExceptions, f);
            }
            catch (Throwable t) {
                processThrowable(nflyNode, "delete", t, ioExceptions, f);
                succ = false;
            }
        }
        this.mayThrowFileNotFound(ioExceptions, numNotFounds);
        if (ioExceptions.size() == this.nodes.length) {
            throw MultipleIOException.createIOException(ioExceptions);
        }
        return succ;
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws FileNotFoundException, IOException {
        final List<IOException> ioExceptions = new ArrayList<IOException>(this.nodes.length);
        final MRNflyNode[] mrNodes = this.workSet();
        if (this.nflyFlags.contains(NflyKey.readMostRecent)) {
            int numNotFounds = 0;
            for (final MRNflyNode nflyNode : mrNodes) {
                try {
                    nflyNode.updateFileStatus(f);
                }
                catch (FileNotFoundException fnfe) {
                    ++numNotFounds;
                    processThrowable(nflyNode, "listStatus", fnfe, ioExceptions, f);
                }
                catch (Throwable t) {
                    processThrowable(nflyNode, "listStatus", t, ioExceptions, f);
                }
            }
            this.mayThrowFileNotFound(ioExceptions, numNotFounds);
            Arrays.sort(mrNodes);
        }
        int numNotFounds = 0;
        for (final MRNflyNode nflyNode : mrNodes) {
            try {
                final FileStatus[] realStats = nflyNode.getFs().listStatus(f);
                final FileStatus[] nflyStats = new FileStatus[realStats.length];
                for (int i = 0; i < realStats.length; ++i) {
                    nflyStats[i] = new NflyStatus(nflyNode.getFs(), realStats[i]);
                }
                return nflyStats;
            }
            catch (FileNotFoundException fnfe) {
                ++numNotFounds;
                processThrowable(nflyNode, "listStatus", fnfe, ioExceptions, f);
            }
            catch (Throwable t) {
                processThrowable(nflyNode, "listStatus", t, ioExceptions, f);
            }
        }
        this.mayThrowFileNotFound(ioExceptions, numNotFounds);
        throw MultipleIOException.createIOException(ioExceptions);
    }
    
    @Override
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws FileNotFoundException, IOException {
        return super.listLocatedStatus(f);
    }
    
    @Override
    public void setWorkingDirectory(final Path newDir) {
        for (final NflyNode nflyNode : this.nodes) {
            nflyNode.fs.setWorkingDirectory(newDir);
        }
    }
    
    @Override
    public Path getWorkingDirectory() {
        return this.nodes[0].fs.getWorkingDirectory();
    }
    
    @Override
    public boolean mkdirs(final Path f, final FsPermission permission) throws IOException {
        boolean succ = true;
        for (final NflyNode nflyNode : this.nodes) {
            succ &= nflyNode.fs.mkdirs(f, permission);
        }
        return succ;
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws IOException {
        final List<IOException> ioExceptions = new ArrayList<IOException>(this.nodes.length);
        int numNotFounds = 0;
        final MRNflyNode[] mrNodes = this.workSet();
        long maxMtime = Long.MIN_VALUE;
        int maxMtimeIdx = Integer.MIN_VALUE;
        for (int i = 0; i < mrNodes.length; ++i) {
            final MRNflyNode nflyNode = mrNodes[i];
            try {
                nflyNode.updateFileStatus(f);
                if (!this.nflyFlags.contains(NflyKey.readMostRecent)) {
                    return nflyNode.nflyStatus();
                }
                final long nflyTime = nflyNode.status.getModificationTime();
                if (nflyTime > maxMtime) {
                    maxMtime = nflyTime;
                    maxMtimeIdx = i;
                }
            }
            catch (FileNotFoundException fnfe) {
                ++numNotFounds;
                processThrowable(nflyNode, "getFileStatus", fnfe, ioExceptions, f);
            }
            catch (Throwable t) {
                processThrowable(nflyNode, "getFileStatus", t, ioExceptions, f);
            }
        }
        if (maxMtimeIdx >= 0) {
            return mrNodes[maxMtimeIdx].nflyStatus();
        }
        this.mayThrowFileNotFound(ioExceptions, numNotFounds);
        throw MultipleIOException.createIOException(ioExceptions);
    }
    
    private static void processThrowable(final NflyNode nflyNode, final String op, final Throwable t, final List<IOException> ioExceptions, final Path... f) {
        final String errMsg = Arrays.toString(f) + ": failed to " + op + " " + nflyNode.fs.getUri();
        IOException ioex;
        if (t instanceof FileNotFoundException) {
            ioex = new FileNotFoundException(errMsg);
            ioex.initCause(t);
        }
        else {
            ioex = new IOException(errMsg, t);
        }
        if (ioExceptions != null) {
            ioExceptions.add(ioex);
        }
    }
    
    static FileSystem createFileSystem(final URI[] uris, final Configuration conf, final String settings) throws IOException {
        int minRepl = 2;
        final EnumSet<NflyKey> nflyFlags = EnumSet.noneOf(NflyKey.class);
        final String[] split;
        final String[] kvPairs = split = StringUtils.split(settings);
        for (final String kv : split) {
            final String[] kvPair = StringUtils.split(kv, '=');
            if (kvPair.length != 2) {
                throw new IllegalArgumentException(kv);
            }
            final NflyKey nflyKey = NflyKey.valueOf(kvPair[0]);
            switch (nflyKey) {
                case minReplication: {
                    minRepl = Integer.parseInt(kvPair[1]);
                    break;
                }
                case repairOnRead:
                case readMostRecent: {
                    if (Boolean.valueOf(kvPair[1])) {
                        nflyFlags.add(nflyKey);
                        break;
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException(nflyKey + ": Infeasible");
                }
            }
        }
        return new NflyFSystem(uris, conf, minRepl, nflyFlags);
    }
    
    static {
        LOG = LogFactory.getLog(NflyFSystem.class);
        NflyFSystem.nflyURI = URI.create("nfly:///");
    }
    
    enum NflyKey
    {
        minReplication, 
        readMostRecent, 
        repairOnRead;
    }
    
    private static class NflyNode extends NodeBase
    {
        private final ChRootedFileSystem fs;
        
        NflyNode(final String hostName, final String rackName, final URI uri, final Configuration conf) throws IOException {
            this(hostName, rackName, new ChRootedFileSystem(uri, conf));
        }
        
        NflyNode(final String hostName, final String rackName, final ChRootedFileSystem fs) {
            super(hostName, rackName);
            this.fs = fs;
        }
        
        ChRootedFileSystem getFs() {
            return this.fs;
        }
        
        @Override
        public boolean equals(final Object o) {
            return super.equals(o);
        }
        
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
    
    private static final class MRNflyNode extends NflyNode implements Comparable<MRNflyNode>
    {
        private FileStatus status;
        
        private MRNflyNode(final NflyNode n) {
            super(n.getName(), n.getNetworkLocation(), n.fs);
        }
        
        private void updateFileStatus(final Path f) throws IOException {
            final FileStatus tmpStatus = this.getFs().getFileStatus(f);
            this.status = ((tmpStatus == null) ? notFoundStatus(f) : tmpStatus);
        }
        
        @Override
        public int compareTo(final MRNflyNode other) {
            if (this.status == null) {
                return (other.status != null) ? 1 : 0;
            }
            if (other.status == null) {
                return -1;
            }
            final long mtime = this.status.getModificationTime();
            final long their = other.status.getModificationTime();
            return Long.compare(their, mtime);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof MRNflyNode)) {
                return false;
            }
            final MRNflyNode other = (MRNflyNode)o;
            return 0 == this.compareTo(other);
        }
        
        @Override
        public int hashCode() {
            return super.hashCode();
        }
        
        private FileStatus nflyStatus() throws IOException {
            return new NflyStatus(this.getFs(), this.status);
        }
        
        private FileStatus cloneStatus() throws IOException {
            return new FileStatus(this.status.getLen(), this.status.isDirectory(), this.status.getReplication(), this.status.getBlockSize(), this.status.getModificationTime(), this.status.getAccessTime(), null, null, null, this.status.isSymlink() ? this.status.getSymlink() : null, this.status.getPath());
        }
    }
    
    private final class NflyOutputStream extends OutputStream
    {
        private final Path nflyPath;
        private final Path tmpPath;
        private final FSDataOutputStream[] outputStreams;
        private final BitSet opSet;
        private final boolean useOverwrite;
        
        private NflyOutputStream(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
            this.nflyPath = f;
            this.tmpPath = NflyFSystem.this.getNflyTmpPath(f);
            this.outputStreams = new FSDataOutputStream[NflyFSystem.this.nodes.length];
            for (int i = 0; i < this.outputStreams.length; ++i) {
                this.outputStreams[i] = NflyFSystem.this.nodes[i].fs.create(this.tmpPath, permission, true, bufferSize, replication, blockSize, progress);
            }
            (this.opSet = new BitSet(this.outputStreams.length)).set(0, this.outputStreams.length);
            this.useOverwrite = false;
        }
        
        private void mayThrow(final List<IOException> ioExceptions) throws IOException {
            final IOException ioe = MultipleIOException.createIOException(ioExceptions);
            if (this.opSet.cardinality() < NflyFSystem.this.minReplication) {
                throw ioe;
            }
            if (NflyFSystem.LOG.isDebugEnabled()) {
                NflyFSystem.LOG.debug("Exceptions occurred: " + ioe);
            }
        }
        
        @Override
        public void write(final int d) throws IOException {
            final List<IOException> ioExceptions = new ArrayList<IOException>();
            for (int i = this.opSet.nextSetBit(0); i >= 0; i = this.opSet.nextSetBit(i + 1)) {
                try {
                    this.outputStreams[i].write(d);
                }
                catch (Throwable t) {
                    this.osException(i, "write", t, ioExceptions);
                }
            }
            this.mayThrow(ioExceptions);
        }
        
        private void osException(final int i, final String op, final Throwable t, final List<IOException> ioExceptions) {
            this.opSet.clear(i);
            processThrowable(NflyFSystem.this.nodes[i], op, t, ioExceptions, new Path[] { this.tmpPath, this.nflyPath });
        }
        
        @Override
        public void write(final byte[] bytes, final int offset, final int len) throws IOException {
            final List<IOException> ioExceptions = new ArrayList<IOException>();
            for (int i = this.opSet.nextSetBit(0); i >= 0; i = this.opSet.nextSetBit(i + 1)) {
                try {
                    this.outputStreams[i].write(bytes, offset, len);
                }
                catch (Throwable t) {
                    this.osException(i, "write", t, ioExceptions);
                }
            }
            this.mayThrow(ioExceptions);
        }
        
        @Override
        public void flush() throws IOException {
            final List<IOException> ioExceptions = new ArrayList<IOException>();
            for (int i = this.opSet.nextSetBit(0); i >= 0; i = this.opSet.nextSetBit(i + 1)) {
                try {
                    this.outputStreams[i].flush();
                }
                catch (Throwable t) {
                    this.osException(i, "flush", t, ioExceptions);
                }
            }
            this.mayThrow(ioExceptions);
        }
        
        @Override
        public void close() throws IOException {
            final List<IOException> ioExceptions = new ArrayList<IOException>();
            for (int i = this.opSet.nextSetBit(0); i >= 0; i = this.opSet.nextSetBit(i + 1)) {
                try {
                    this.outputStreams[i].close();
                }
                catch (Throwable t) {
                    this.osException(i, "close", t, ioExceptions);
                }
            }
            if (this.opSet.cardinality() < NflyFSystem.this.minReplication) {
                this.cleanupAllTmpFiles();
                throw new IOException("Failed to sufficiently replicate: min=" + NflyFSystem.this.minReplication + " actual=" + this.opSet.cardinality());
            }
            this.commit();
        }
        
        private void cleanupAllTmpFiles() throws IOException {
            for (int i = 0; i < this.outputStreams.length; ++i) {
                try {
                    NflyFSystem.this.nodes[i].fs.delete(this.tmpPath);
                }
                catch (Throwable t) {
                    processThrowable(NflyFSystem.this.nodes[i], "delete", t, null, new Path[] { this.tmpPath });
                }
            }
        }
        
        private void commit() throws IOException {
            final List<IOException> ioExceptions = new ArrayList<IOException>();
            for (int i = this.opSet.nextSetBit(0); i >= 0; i = this.opSet.nextSetBit(i + 1)) {
                final NflyNode nflyNode = NflyFSystem.this.nodes[i];
                try {
                    if (this.useOverwrite) {
                        nflyNode.fs.delete(this.nflyPath);
                    }
                    nflyNode.fs.rename(this.tmpPath, this.nflyPath);
                }
                catch (Throwable t) {
                    this.osException(i, "commit", t, ioExceptions);
                }
            }
            if (this.opSet.cardinality() < NflyFSystem.this.minReplication) {
                throw MultipleIOException.createIOException(ioExceptions);
            }
            final long commitTime = System.currentTimeMillis();
            for (int j = this.opSet.nextSetBit(0); j >= 0; j = this.opSet.nextSetBit(j + 1)) {
                try {
                    NflyFSystem.this.nodes[j].fs.setTimes(this.nflyPath, commitTime, commitTime);
                }
                catch (Throwable t2) {
                    NflyFSystem.LOG.info("Failed to set timestamp: " + NflyFSystem.this.nodes[j] + " " + this.nflyPath);
                }
            }
        }
    }
    
    static final class NflyStatus extends FileStatus
    {
        private static final long serialVersionUID = 569538264L;
        private final FileStatus realStatus;
        private final String strippedRoot;
        
        private NflyStatus(final ChRootedFileSystem realFs, final FileStatus realStatus) throws IOException {
            this.realStatus = realStatus;
            this.strippedRoot = realFs.stripOutRoot(realStatus.getPath());
        }
        
        String stripRoot() throws IOException {
            return this.strippedRoot;
        }
        
        @Override
        public long getLen() {
            return this.realStatus.getLen();
        }
        
        @Override
        public boolean isFile() {
            return this.realStatus.isFile();
        }
        
        @Override
        public boolean isDirectory() {
            return this.realStatus.isDirectory();
        }
        
        @Override
        public boolean isSymlink() {
            return this.realStatus.isSymlink();
        }
        
        @Override
        public long getBlockSize() {
            return this.realStatus.getBlockSize();
        }
        
        @Override
        public short getReplication() {
            return this.realStatus.getReplication();
        }
        
        @Override
        public long getModificationTime() {
            return this.realStatus.getModificationTime();
        }
        
        @Override
        public long getAccessTime() {
            return this.realStatus.getAccessTime();
        }
        
        @Override
        public FsPermission getPermission() {
            return this.realStatus.getPermission();
        }
        
        @Override
        public String getOwner() {
            return this.realStatus.getOwner();
        }
        
        @Override
        public String getGroup() {
            return this.realStatus.getGroup();
        }
        
        @Override
        public Path getPath() {
            return this.realStatus.getPath();
        }
        
        @Override
        public void setPath(final Path p) {
            this.realStatus.setPath(p);
        }
        
        @Override
        public Path getSymlink() throws IOException {
            return this.realStatus.getSymlink();
        }
        
        @Override
        public void setSymlink(final Path p) {
            this.realStatus.setSymlink(p);
        }
        
        @Override
        public boolean equals(final Object o) {
            return this.realStatus.equals(o);
        }
        
        @Override
        public int hashCode() {
            return this.realStatus.hashCode();
        }
        
        @Override
        public String toString() {
            return this.realStatus.toString();
        }
    }
}
