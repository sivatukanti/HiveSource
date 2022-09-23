// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import java.security.NoSuchAlgorithmException;
import org.apache.hadoop.hdfs.protocol.EncryptionZone;
import org.apache.hadoop.crypto.key.KeyProvider;
import org.apache.hadoop.security.authentication.util.KerberosName;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.ProxyFileSystem;
import org.apache.hadoop.net.NetUtils;
import java.net.InetSocketAddress;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.tez.test.MiniTezCluster;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.apache.hadoop.security.Credentials;
import java.nio.ByteBuffer;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import java.lang.reflect.Constructor;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.fs.TrashPolicy;
import java.security.AccessControlException;
import org.apache.hadoop.fs.DefaultFileAccess;
import org.apache.hadoop.fs.FSDataInputStream;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;
import com.google.common.collect.Iterables;
import com.google.common.base.Predicate;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.AclEntry;
import com.google.common.base.Joiner;
import org.apache.hadoop.fs.permission.AclEntryType;
import org.apache.hadoop.fs.permission.AclEntryScope;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.util.TreeMap;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.LocatedFileStatus;
import java.util.ArrayList;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.mapred.WebHCatJTShim23;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.crypto.key.KeyProviderCryptoExtension;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import java.util.Comparator;
import org.apache.hadoop.mapred.lib.TotalOrderPartitioner;
import org.apache.hadoop.fs.Trash;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import com.google.common.base.Objects;
import org.apache.hadoop.util.JvmPauseMonitor;
import org.apache.hadoop.mapreduce.task.JobContextImpl;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.ClusterStatus;
import java.net.MalformedURLException;
import java.util.Iterator;
import org.apache.hadoop.fs.FileStatus;
import java.util.List;
import org.apache.hadoop.mapreduce.JobContext;
import java.io.IOException;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.InputSplit;
import java.lang.reflect.Method;

public class Hadoop23Shims extends HadoopShimsSecure
{
    HadoopShims.MiniDFSShim cluster;
    final boolean zeroCopy;
    final boolean storagePolicy;
    private volatile HadoopShims.HCatHadoopShims hcatShimInstance;
    protected static final Method accessMethod;
    protected static final Method getPasswordMethod;
    private static Boolean hdfsEncryptionSupport;
    
    public Hadoop23Shims() {
        this.cluster = null;
        boolean zcr = false;
        boolean storage = false;
        try {
            Class.forName("org.apache.hadoop.fs.CacheFlag", false, ShimLoader.class.getClassLoader());
            zcr = true;
        }
        catch (ClassNotFoundException ex) {}
        if (zcr) {
            try {
                Class.forName("org.apache.hadoop.hdfs.protocol.BlockStoragePolicy", false, ShimLoader.class.getClassLoader());
                storage = true;
            }
            catch (ClassNotFoundException ex2) {}
        }
        this.storagePolicy = storage;
        this.zeroCopy = zcr;
    }
    
    @Override
    public HadoopShims.CombineFileInputFormatShim getCombineFileInputFormat() {
        return new CombineFileInputFormatShim() {
            public RecordReader getRecordReader(final InputSplit split, final JobConf job, final Reporter reporter) throws IOException {
                throw new IOException("CombineFileInputFormat.getRecordReader not needed.");
            }
            
            protected List<FileStatus> listStatus(final JobContext job) throws IOException {
                final List<FileStatus> result = (List<FileStatus>)super.listStatus(job);
                final Iterator<FileStatus> it = result.iterator();
                while (it.hasNext()) {
                    final FileStatus stat = it.next();
                    if (!stat.isFile()) {
                        it.remove();
                    }
                }
                return result;
            }
        };
    }
    
    @Override
    public String getTaskAttemptLogUrl(final JobConf conf, final String taskTrackerHttpAddress, final String taskAttemptId) throws MalformedURLException {
        if (conf.get("mapreduce.framework.name") != null && conf.get("mapreduce.framework.name").equals("yarn")) {
            Hadoop23Shims.LOG.warn("Can't fetch tasklog: TaskLogServlet is not supported in MR2 mode.");
            return null;
        }
        Hadoop23Shims.LOG.warn("Can't fetch tasklog: TaskLogServlet is not supported in MR1 mode.");
        return null;
    }
    
    @Override
    public HadoopShims.JobTrackerState getJobTrackerState(final ClusterStatus clusterStatus) throws Exception {
        switch (clusterStatus.getJobTrackerStatus()) {
            case INITIALIZING: {
                return HadoopShims.JobTrackerState.INITIALIZING;
            }
            case RUNNING: {
                return HadoopShims.JobTrackerState.RUNNING;
            }
            default: {
                final String errorMsg = "Unrecognized JobTracker state: " + clusterStatus.getJobTrackerStatus();
                throw new Exception(errorMsg);
            }
        }
    }
    
    @Override
    public TaskAttemptContext newTaskAttemptContext(final Configuration conf, final Progressable progressable) {
        TaskAttemptID taskAttemptId = TaskAttemptID.forName(conf.get("mapreduce.task.attempt.id"));
        if (taskAttemptId == null) {
            taskAttemptId = new TaskAttemptID();
        }
        return (TaskAttemptContext)new TaskAttemptContextImpl(conf, taskAttemptId) {
            public void progress() {
                progressable.progress();
            }
        };
    }
    
    @Override
    public TaskAttemptID newTaskAttemptID(final JobID jobId, final boolean isMap, final int taskId, final int id) {
        return new TaskAttemptID(jobId.getJtIdentifier(), jobId.getId(), isMap ? TaskType.MAP : TaskType.REDUCE, taskId, id);
    }
    
    @Override
    public JobContext newJobContext(final Job job) {
        return (JobContext)new JobContextImpl(job.getConfiguration(), job.getJobID());
    }
    
    @Override
    public void startPauseMonitor(final Configuration conf) {
        try {
            Class.forName("org.apache.hadoop.util.JvmPauseMonitor");
            final JvmPauseMonitor pauseMonitor = new JvmPauseMonitor(conf);
            pauseMonitor.start();
        }
        catch (Throwable t) {
            Hadoop23Shims.LOG.warn("Could not initiate the JvmPauseMonitor thread. GCs and Pauses may not be warned upon.", t);
        }
    }
    
    @Override
    public boolean isLocalMode(final Configuration conf) {
        return "local".equals(conf.get("mapreduce.framework.name"));
    }
    
    @Override
    public String getJobLauncherRpcAddress(final Configuration conf) {
        return conf.get("yarn.resourcemanager.address");
    }
    
    @Override
    public void setJobLauncherRpcAddress(final Configuration conf, final String val) {
        if (val.equals("local")) {
            conf.set("mapreduce.framework.name", val);
            conf.set("mapreduce.jobtracker.address", val);
        }
        else {
            conf.set("mapreduce.framework.name", "yarn");
            conf.set("yarn.resourcemanager.address", val);
        }
    }
    
    @Override
    public String getJobLauncherHttpAddress(final Configuration conf) {
        return conf.get("yarn.resourcemanager.webapp.address");
    }
    
    protected boolean isExtendedAclEnabled(final Configuration conf) {
        return Objects.equal(conf.get("dfs.namenode.acls.enabled"), "true");
    }
    
    @Override
    public long getDefaultBlockSize(final FileSystem fs, final Path path) {
        return fs.getDefaultBlockSize(path);
    }
    
    @Override
    public short getDefaultReplication(final FileSystem fs, final Path path) {
        return fs.getDefaultReplication(path);
    }
    
    @Override
    public boolean moveToAppropriateTrash(final FileSystem fs, final Path path, final Configuration conf) throws IOException {
        return Trash.moveToAppropriateTrash(fs, path, conf);
    }
    
    @Override
    public void setTotalOrderPartitionFile(final JobConf jobConf, final Path partitionFile) {
        TotalOrderPartitioner.setPartitionFile(jobConf, partitionFile);
    }
    
    @Override
    public Comparator<LongWritable> getLongComparator() {
        return new Comparator<LongWritable>() {
            @Override
            public int compare(final LongWritable o1, final LongWritable o2) {
                return o1.compareTo(o2);
            }
        };
    }
    
    @Override
    public void refreshDefaultQueue(final Configuration conf, final String userName) throws IOException {
        if (StringUtils.isNotBlank(userName) && this.isFairScheduler(conf)) {
            ShimLoader.getSchedulerShims().refreshDefaultQueue(conf, userName);
        }
    }
    
    private boolean isFairScheduler(final Configuration conf) {
        return "org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler".equalsIgnoreCase(conf.get("yarn.resourcemanager.scheduler.class"));
    }
    
    @Override
    public MiniMrShim getMiniMrCluster(final Configuration conf, final int numberOfTaskTrackers, final String nameNode, final int numDir) throws IOException {
        return new MiniMrShim(conf, numberOfTaskTrackers, nameNode, numDir);
    }
    
    @Override
    public MiniMrShim getMiniTezCluster(final Configuration conf, final int numberOfTaskTrackers, final String nameNode, final int numDir) throws IOException {
        return new MiniTezShim(conf, numberOfTaskTrackers, nameNode, numDir);
    }
    
    private void configureImpersonation(final Configuration conf) {
        String user;
        try {
            user = Utils.getUGI().getShortUserName();
        }
        catch (Exception e) {
            final String msg = "Cannot obtain username: " + e;
            throw new IllegalStateException(msg, e);
        }
        conf.set("hadoop.proxyuser." + user + ".groups", "*");
        conf.set("hadoop.proxyuser." + user + ".hosts", "*");
    }
    
    @Override
    public MiniMrShim getMiniSparkCluster(final Configuration conf, final int numberOfTaskTrackers, final String nameNode, final int numDir) throws IOException {
        return new MiniSparkShim(conf, numberOfTaskTrackers, nameNode, numDir);
    }
    
    @Override
    public HadoopShims.MiniDFSShim getMiniDfs(final Configuration conf, final int numDataNodes, final boolean format, final String[] racks) throws IOException {
        this.configureImpersonation(conf);
        final MiniDFSCluster miniDFSCluster = new MiniDFSCluster(conf, numDataNodes, format, racks);
        final KeyProviderCryptoExtension keyProvider = miniDFSCluster.getNameNode().getNamesystem().getProvider();
        if (keyProvider != null) {
            miniDFSCluster.getFileSystem().getClient().setKeyProvider(keyProvider);
        }
        return this.cluster = new MiniDFSShim(miniDFSCluster);
    }
    
    @Override
    public HadoopShims.HCatHadoopShims getHCatShim() {
        if (this.hcatShimInstance == null) {
            this.hcatShimInstance = new HCatHadoopShims23();
        }
        return this.hcatShimInstance;
    }
    
    @Override
    public HadoopShims.WebHCatJTShim getWebHCatShim(final Configuration conf, final UserGroupInformation ugi) throws IOException {
        return new WebHCatJTShim23(conf, ugi);
    }
    
    @Override
    public List<FileStatus> listLocatedStatus(final FileSystem fs, final Path path, final PathFilter filter) throws IOException {
        final RemoteIterator<LocatedFileStatus> itr = fs.listLocatedStatus(path);
        final List<FileStatus> result = new ArrayList<FileStatus>();
        while (itr.hasNext()) {
            final FileStatus stat = itr.next();
            if (filter == null || filter.accept(stat.getPath())) {
                result.add(stat);
            }
        }
        return result;
    }
    
    @Override
    public BlockLocation[] getLocations(final FileSystem fs, final FileStatus status) throws IOException {
        if (status instanceof LocatedFileStatus) {
            return ((LocatedFileStatus)status).getBlockLocations();
        }
        return fs.getFileBlockLocations(status, 0L, status.getLen());
    }
    
    @Override
    public TreeMap<Long, BlockLocation> getLocationsWithOffset(final FileSystem fs, final FileStatus status) throws IOException {
        final TreeMap<Long, BlockLocation> offsetBlockMap = new TreeMap<Long, BlockLocation>();
        final BlockLocation[] locations2;
        final BlockLocation[] locations = locations2 = this.getLocations(fs, status);
        for (final BlockLocation location : locations2) {
            offsetBlockMap.put(location.getOffset(), location);
        }
        return offsetBlockMap;
    }
    
    @Override
    public void hflush(final FSDataOutputStream stream) throws IOException {
        stream.hflush();
    }
    
    @Override
    public HadoopShims.HdfsFileStatus getFullFileStatus(final Configuration conf, final FileSystem fs, final Path file) throws IOException {
        final FileStatus fileStatus = fs.getFileStatus(file);
        AclStatus aclStatus = null;
        if (this.isExtendedAclEnabled(conf)) {
            try {
                aclStatus = fs.getAclStatus(file);
            }
            catch (Exception e) {
                Hadoop23Shims.LOG.info("Skipping ACL inheritance: File system for path " + file + " " + "does not support ACLs but dfs.namenode.acls.enabled is set to true: " + e, e);
            }
        }
        return new Hadoop23FileStatus(fileStatus, aclStatus);
    }
    
    @Override
    public void setFullFileStatus(final Configuration conf, final HadoopShims.HdfsFileStatus sourceStatus, final FileSystem fs, final Path target) throws IOException {
        final String group = sourceStatus.getFileStatus().getGroup();
        try {
            final FsShell fsShell = new FsShell();
            fsShell.setConf(conf);
            this.run(fsShell, new String[] { "-chgrp", "-R", group, target.toString() });
            if (this.isExtendedAclEnabled(conf)) {
                try {
                    final AclStatus aclStatus = ((Hadoop23FileStatus)sourceStatus).getAclStatus();
                    if (aclStatus != null) {
                        final List<AclEntry> aclEntries = aclStatus.getEntries();
                        this.removeBaseAclEntries(aclEntries);
                        final FsPermission sourcePerm = sourceStatus.getFileStatus().getPermission();
                        aclEntries.add(this.newAclEntry(AclEntryScope.ACCESS, AclEntryType.USER, sourcePerm.getUserAction()));
                        aclEntries.add(this.newAclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, sourcePerm.getGroupAction()));
                        aclEntries.add(this.newAclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, sourcePerm.getOtherAction()));
                        final String aclEntry = Joiner.on(",").join(aclStatus.getEntries());
                        this.run(fsShell, new String[] { "-setfacl", "-R", "--set", aclEntry, target.toString() });
                    }
                }
                catch (Exception e) {
                    Hadoop23Shims.LOG.info("Skipping ACL inheritance: File system for path " + target + " " + "does not support ACLs but dfs.namenode.acls.enabled is set to true: " + e, e);
                }
            }
            else {
                final String permission = Integer.toString(sourceStatus.getFileStatus().getPermission().toShort(), 8);
                this.run(fsShell, new String[] { "-chmod", "-R", permission, target.toString() });
            }
        }
        catch (Exception e2) {
            throw new IOException("Unable to set permissions of " + target, e2);
        }
        try {
            if (Hadoop23Shims.LOG.isDebugEnabled()) {
                this.getFullFileStatus(conf, fs, target).debugLog();
            }
        }
        catch (Exception ex) {}
    }
    
    private AclEntry newAclEntry(final AclEntryScope scope, final AclEntryType type, final FsAction permission) {
        return new AclEntry.Builder().setScope(scope).setType(type).setPermission(permission).build();
    }
    
    private void removeBaseAclEntries(final List<AclEntry> entries) {
        Iterables.removeIf(entries, new Predicate<AclEntry>() {
            @Override
            public boolean apply(final AclEntry input) {
                return input.getName() == null;
            }
        });
    }
    
    @Override
    public FileSystem createProxyFileSystem(final FileSystem fs, final URI uri) {
        return new ProxyFileSystem23(fs, uri);
    }
    
    @Override
    public Map<String, String> getHadoopConfNames() {
        final Map<String, String> ret = new HashMap<String, String>();
        ret.put("HADOOPFS", "fs.defaultFS");
        ret.put("HADOOPMAPFILENAME", "mapreduce.map.input.file");
        ret.put("HADOOPMAPREDINPUTDIR", "mapreduce.input.fileinputformat.inputdir");
        ret.put("HADOOPMAPREDINPUTDIRRECURSIVE", "mapreduce.input.fileinputformat.input.dir.recursive");
        ret.put("MAPREDMAXSPLITSIZE", "mapreduce.input.fileinputformat.split.maxsize");
        ret.put("MAPREDMINSPLITSIZE", "mapreduce.input.fileinputformat.split.minsize");
        ret.put("MAPREDMINSPLITSIZEPERNODE", "mapreduce.input.fileinputformat.split.minsize.per.node");
        ret.put("MAPREDMINSPLITSIZEPERRACK", "mapreduce.input.fileinputformat.split.minsize.per.rack");
        ret.put("HADOOPNUMREDUCERS", "mapreduce.job.reduces");
        ret.put("HADOOPJOBNAME", "mapreduce.job.name");
        ret.put("HADOOPSPECULATIVEEXECREDUCERS", "mapreduce.reduce.speculative");
        ret.put("MAPREDSETUPCLEANUPNEEDED", "mapreduce.job.committer.setup.cleanup.needed");
        ret.put("MAPREDTASKCLEANUPNEEDED", "mapreduce.job.committer.task.cleanup.needed");
        return ret;
    }
    
    @Override
    public HadoopShims.ZeroCopyReaderShim getZeroCopyReader(final FSDataInputStream in, final HadoopShims.ByteBufferPoolShim pool) throws IOException {
        if (this.zeroCopy) {
            return ZeroCopyShims.getZeroCopyReader(in, pool);
        }
        return null;
    }
    
    @Override
    public HadoopShims.DirectDecompressorShim getDirectDecompressor(final HadoopShims.DirectCompressionType codec) {
        if (this.zeroCopy) {
            return ZeroCopyShims.getDirectDecompressor(codec);
        }
        return null;
    }
    
    @Override
    public Configuration getConfiguration(final JobContext context) {
        return context.getConfiguration();
    }
    
    @Override
    public JobConf getJobConf(final org.apache.hadoop.mapred.JobContext context) {
        return context.getJobConf();
    }
    
    @Override
    public FileSystem getNonCachedFileSystem(final URI uri, final Configuration conf) throws IOException {
        return FileSystem.newInstance(uri, conf);
    }
    
    @Override
    public void getMergedCredentials(final JobConf jobConf) throws IOException {
        jobConf.getCredentials().mergeAll(UserGroupInformation.getCurrentUser().getCredentials());
    }
    
    @Override
    public void mergeCredentials(final JobConf dest, final JobConf src) throws IOException {
        dest.getCredentials().mergeAll(src.getCredentials());
    }
    
    @Override
    public void checkFileAccess(final FileSystem fs, final FileStatus stat, final FsAction action) throws IOException, AccessControlException, Exception {
        try {
            if (Hadoop23Shims.accessMethod == null) {
                DefaultFileAccess.checkFileAccess(fs, stat, action);
            }
            else {
                Hadoop23Shims.accessMethod.invoke(fs, stat.getPath(), action);
            }
        }
        catch (Exception err) {
            throw wrapAccessException(err);
        }
    }
    
    private static Exception wrapAccessException(final Exception err) {
        final int maxDepth = 20;
        Throwable curErr = err;
        for (int idx = 0; curErr != null && idx < 20; curErr = curErr.getCause(), ++idx) {
            if (curErr instanceof org.apache.hadoop.security.AccessControlException || curErr.getClass().getName().equals("org.apache.hadoop.fs.permission.AccessControlException")) {
                final Exception newErr = new AccessControlException(curErr.getMessage());
                newErr.initCause(err);
                return newErr;
            }
        }
        return err;
    }
    
    @Override
    public String getPassword(final Configuration conf, final String name) throws IOException {
        if (Hadoop23Shims.getPasswordMethod == null) {
            return conf.get(name);
        }
        try {
            final char[] pw = (char[])Hadoop23Shims.getPasswordMethod.invoke(conf, name);
            if (pw == null) {
                return null;
            }
            return new String(pw);
        }
        catch (Exception err) {
            throw new IOException(err.getMessage(), err);
        }
    }
    
    @Override
    public boolean supportStickyBit() {
        return true;
    }
    
    @Override
    public boolean hasStickyBit(final FsPermission permission) {
        return permission.getStickyBit();
    }
    
    @Override
    public boolean supportTrashFeature() {
        return true;
    }
    
    @Override
    public Path getCurrentTrashPath(final Configuration conf, final FileSystem fs) {
        final TrashPolicy tp = TrashPolicy.getInstance(conf, fs, fs.getHomeDirectory());
        return tp.getCurrentTrashDir();
    }
    
    @Override
    public KerberosNameShim getKerberosNameShim(final String name) throws IOException {
        return new KerberosNameShim(name);
    }
    
    @Override
    public boolean isDirectory(final FileStatus fileStatus) {
        return fileStatus.isDirectory();
    }
    
    @Override
    public HadoopShims.StoragePolicyShim getStoragePolicyShim(final FileSystem fs) {
        if (!this.storagePolicy) {
            return null;
        }
        try {
            return new StoragePolicyShim((DistributedFileSystem)fs);
        }
        catch (ClassCastException ce) {
            return null;
        }
    }
    
    @Override
    public boolean runDistCp(final Path src, final Path dst, final Configuration conf) throws IOException {
        final String[] params = { "-update", "-skipcrccheck", src.toString(), dst.toString() };
        int rc;
        try {
            final Class clazzDistCp = Class.forName("org.apache.hadoop.tools.DistCp");
            final Constructor c = clazzDistCp.getConstructor((Class[])new Class[0]);
            c.setAccessible(true);
            final Tool distcp = c.newInstance(new Object[0]);
            distcp.setConf(conf);
            rc = distcp.run(params);
        }
        catch (ClassNotFoundException e) {
            throw new IOException("Cannot find DistCp class package: " + e.getMessage());
        }
        catch (NoSuchMethodException e2) {
            throw new IOException("Cannot get DistCp constructor: " + e2.getMessage());
        }
        catch (Exception e3) {
            throw new IOException("Cannot execute DistCp process: " + e3, e3);
        }
        return 0 == rc;
    }
    
    public static boolean isHdfsEncryptionSupported() {
        if (Hadoop23Shims.hdfsEncryptionSupport == null) {
            Method m = null;
            try {
                m = HdfsAdmin.class.getMethod("getEncryptionZoneForPath", Path.class);
            }
            catch (NoSuchMethodException ex) {}
            Hadoop23Shims.hdfsEncryptionSupport = (m != null);
        }
        return Hadoop23Shims.hdfsEncryptionSupport;
    }
    
    @Override
    public HadoopShims.HdfsEncryptionShim createHdfsEncryptionShim(final FileSystem fs, final Configuration conf) throws IOException {
        if (isHdfsEncryptionSupported()) {
            final URI uri = fs.getUri();
            if ("hdfs".equals(uri.getScheme())) {
                return new HdfsEncryptionShim(uri, conf);
            }
        }
        return new HadoopShims.NoopHdfsEncryptionShim();
    }
    
    @Override
    public Path getPathWithoutSchemeAndAuthority(final Path path) {
        return Path.getPathWithoutSchemeAndAuthority(path);
    }
    
    @Override
    public int readByteBuffer(final FSDataInputStream file, final ByteBuffer dest) throws IOException {
        final int pos = dest.position();
        final int result = file.read(dest);
        if (result > 0) {
            dest.position(pos + result);
        }
        return result;
    }
    
    @Override
    public void addDelegationTokens(final FileSystem fs, final Credentials cred, final String uname) throws IOException {
        fs.addDelegationTokens(uname, cred);
    }
    
    static {
        Method m = null;
        try {
            m = FileSystem.class.getMethod("access", Path.class, FsAction.class);
        }
        catch (NoSuchMethodException ex) {}
        accessMethod = m;
        try {
            m = Configuration.class.getMethod("getPassword", String.class);
        }
        catch (NoSuchMethodException err) {
            m = null;
        }
        getPasswordMethod = m;
    }
    
    public class MiniMrShim implements HadoopShims.MiniMrShim
    {
        private final MiniMRCluster mr;
        private final Configuration conf;
        
        public MiniMrShim() {
            this.mr = null;
            this.conf = null;
        }
        
        public MiniMrShim(final Configuration conf, final int numberOfTaskTrackers, final String nameNode, final int numDir) throws IOException {
            this.conf = conf;
            final JobConf jConf = new JobConf(conf);
            jConf.set("yarn.scheduler.capacity.root.queues", "default");
            jConf.set("yarn.scheduler.capacity.root.default.capacity", "100");
            this.mr = new MiniMRCluster(numberOfTaskTrackers, nameNode, numDir, (String[])null, (String[])null, jConf);
        }
        
        @Override
        public int getJobTrackerPort() throws UnsupportedOperationException {
            String address = this.conf.get("yarn.resourcemanager.address");
            address = StringUtils.substringAfterLast(address, ":");
            if (StringUtils.isBlank(address)) {
                throw new IllegalArgumentException("Invalid YARN resource manager port.");
            }
            return Integer.parseInt(address);
        }
        
        @Override
        public void shutdown() throws IOException {
            this.mr.shutdown();
        }
        
        @Override
        public void setupConfiguration(final Configuration conf) {
            final JobConf jConf = this.mr.createJobConf();
            for (final Map.Entry<String, String> pair : jConf) {
                conf.set(pair.getKey(), pair.getValue());
            }
        }
    }
    
    public class MiniTezShim extends MiniMrShim
    {
        private final MiniTezCluster mr;
        private final Configuration conf;
        
        public MiniTezShim(final Configuration conf, final int numberOfTaskTrackers, final String nameNode, final int numDir) throws IOException {
            this.mr = new MiniTezCluster("hive", numberOfTaskTrackers);
            conf.set("fs.defaultFS", nameNode);
            conf.set("tez.am.log.level", "DEBUG");
            conf.set("yarn.app.mapreduce.am.staging-dir", "/apps_staging_dir");
            this.mr.init(conf);
            this.mr.start();
            this.conf = this.mr.getConfig();
        }
        
        @Override
        public int getJobTrackerPort() throws UnsupportedOperationException {
            String address = this.conf.get("yarn.resourcemanager.address");
            address = StringUtils.substringAfterLast(address, ":");
            if (StringUtils.isBlank(address)) {
                throw new IllegalArgumentException("Invalid YARN resource manager port.");
            }
            return Integer.parseInt(address);
        }
        
        @Override
        public void shutdown() throws IOException {
            this.mr.stop();
        }
        
        @Override
        public void setupConfiguration(final Configuration conf) {
            final Configuration config = this.mr.getConfig();
            for (final Map.Entry<String, String> pair : config) {
                conf.set(pair.getKey(), pair.getValue());
            }
            Path jarPath = new Path("hdfs:///user/hive");
            Path hdfsPath = new Path("hdfs:///user/");
            try {
                final FileSystem fs = Hadoop23Shims.this.cluster.getFileSystem();
                jarPath = fs.makeQualified(jarPath);
                conf.set("hive.jar.directory", jarPath.toString());
                fs.mkdirs(jarPath);
                hdfsPath = fs.makeQualified(hdfsPath);
                conf.set("hive.user.install.directory", hdfsPath.toString());
                fs.mkdirs(hdfsPath);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public class MiniSparkShim extends MiniMrShim
    {
        private final MiniSparkOnYARNCluster mr;
        private final Configuration conf;
        
        public MiniSparkShim(final Configuration conf, final int numberOfTaskTrackers, final String nameNode, final int numDir) throws IOException {
            this.mr = new MiniSparkOnYARNCluster("sparkOnYarn");
            conf.set("fs.defaultFS", nameNode);
            conf.set("yarn.resourcemanager.scheduler.class", "org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler");
            Hadoop23Shims.this.configureImpersonation(conf);
            this.mr.init(conf);
            this.mr.start();
            this.conf = this.mr.getConfig();
        }
        
        @Override
        public int getJobTrackerPort() throws UnsupportedOperationException {
            String address = this.conf.get("yarn.resourcemanager.address");
            address = StringUtils.substringAfterLast(address, ":");
            if (StringUtils.isBlank(address)) {
                throw new IllegalArgumentException("Invalid YARN resource manager port.");
            }
            return Integer.parseInt(address);
        }
        
        @Override
        public void shutdown() throws IOException {
            this.mr.stop();
        }
        
        @Override
        public void setupConfiguration(final Configuration conf) {
            final Configuration config = this.mr.getConfig();
            for (final Map.Entry<String, String> pair : config) {
                conf.set(pair.getKey(), pair.getValue());
            }
            Path jarPath = new Path("hdfs:///user/hive");
            Path hdfsPath = new Path("hdfs:///user/");
            try {
                final FileSystem fs = Hadoop23Shims.this.cluster.getFileSystem();
                jarPath = fs.makeQualified(jarPath);
                conf.set("hive.jar.directory", jarPath.toString());
                fs.mkdirs(jarPath);
                hdfsPath = fs.makeQualified(hdfsPath);
                conf.set("hive.user.install.directory", hdfsPath.toString());
                fs.mkdirs(hdfsPath);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public class MiniDFSShim implements HadoopShims.MiniDFSShim
    {
        private final MiniDFSCluster cluster;
        
        public MiniDFSShim(final MiniDFSCluster cluster) {
            this.cluster = cluster;
        }
        
        @Override
        public FileSystem getFileSystem() throws IOException {
            return (FileSystem)this.cluster.getFileSystem();
        }
        
        @Override
        public void shutdown() {
            this.cluster.shutdown();
        }
    }
    
    private final class HCatHadoopShims23 implements HadoopShims.HCatHadoopShims
    {
        @Override
        public TaskID createTaskID() {
            return new TaskID("", 0, TaskType.MAP, 0);
        }
        
        @Override
        public TaskAttemptID createTaskAttemptID() {
            return new TaskAttemptID("", 0, TaskType.MAP, 0, 0);
        }
        
        @Override
        public TaskAttemptContext createTaskAttemptContext(final Configuration conf, final TaskAttemptID taskId) {
            return (TaskAttemptContext)new TaskAttemptContextImpl((conf instanceof JobConf) ? new JobConf(conf) : conf, taskId);
        }
        
        @Override
        public org.apache.hadoop.mapred.TaskAttemptContext createTaskAttemptContext(final JobConf conf, final org.apache.hadoop.mapred.TaskAttemptID taskId, final Progressable progressable) {
            org.apache.hadoop.mapred.TaskAttemptContext newContext = null;
            try {
                final Constructor construct = org.apache.hadoop.mapred.TaskAttemptContextImpl.class.getDeclaredConstructor(JobConf.class, org.apache.hadoop.mapred.TaskAttemptID.class, Reporter.class);
                construct.setAccessible(true);
                newContext = construct.newInstance(new JobConf((Configuration)conf), taskId, progressable);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            return newContext;
        }
        
        @Override
        public JobContext createJobContext(final Configuration conf, final JobID jobId) {
            return (JobContext)new JobContextImpl((conf instanceof JobConf) ? new JobConf(conf) : conf, jobId);
        }
        
        @Override
        public org.apache.hadoop.mapred.JobContext createJobContext(final JobConf conf, final JobID jobId, final Progressable progressable) {
            return (org.apache.hadoop.mapred.JobContext)new org.apache.hadoop.mapred.JobContextImpl(new JobConf((Configuration)conf), jobId, progressable);
        }
        
        @Override
        public void commitJob(final OutputFormat outputFormat, final Job job) throws IOException {
        }
        
        @Override
        public void abortJob(final OutputFormat outputFormat, final Job job) throws IOException {
        }
        
        @Override
        public InetSocketAddress getResourceManagerAddress(final Configuration conf) {
            final String addr = conf.get("yarn.resourcemanager.address", "localhost:8032");
            return NetUtils.createSocketAddr(addr);
        }
        
        @Override
        public String getPropertyName(final PropertyName name) {
            switch (name) {
                case CACHE_ARCHIVES: {
                    return "mapreduce.job.cache.archives";
                }
                case CACHE_FILES: {
                    return "mapreduce.job.cache.files";
                }
                case CACHE_SYMLINK: {
                    return "mapreduce.job.cache.symlink.create";
                }
                case CLASSPATH_ARCHIVES: {
                    return "mapreduce.job.classpath.archives";
                }
                case CLASSPATH_FILES: {
                    return "mapreduce.job.classpath.files";
                }
                default: {
                    return "";
                }
            }
        }
        
        @Override
        public boolean isFileInHDFS(final FileSystem fs, final Path path) throws IOException {
            return "hdfs".equals(fs.resolvePath(path).toUri().getScheme());
        }
    }
    
    public class Hadoop23FileStatus implements HadoopShims.HdfsFileStatus
    {
        private final FileStatus fileStatus;
        private final AclStatus aclStatus;
        
        public Hadoop23FileStatus(final FileStatus fileStatus, final AclStatus aclStatus) {
            this.fileStatus = fileStatus;
            this.aclStatus = aclStatus;
        }
        
        @Override
        public FileStatus getFileStatus() {
            return this.fileStatus;
        }
        
        public AclStatus getAclStatus() {
            return this.aclStatus;
        }
        
        @Override
        public void debugLog() {
            if (this.fileStatus != null) {
                HadoopShimsSecure.LOG.debug(this.fileStatus.toString());
            }
            if (this.aclStatus != null) {
                HadoopShimsSecure.LOG.debug(this.aclStatus.toString());
            }
        }
    }
    
    class ProxyFileSystem23 extends ProxyFileSystem
    {
        public ProxyFileSystem23(final FileSystem fs) {
            super(fs);
        }
        
        public ProxyFileSystem23(final FileSystem fs, final URI uri) {
            super(fs, uri);
        }
        
        @Override
        public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws FileNotFoundException, IOException {
            return new RemoteIterator<LocatedFileStatus>() {
                private final RemoteIterator<LocatedFileStatus> stats = FilterFileSystem.this.listLocatedStatus(ProxyFileSystem.this.swizzleParamPath(f));
                
                @Override
                public boolean hasNext() throws IOException {
                    return this.stats.hasNext();
                }
                
                @Override
                public LocatedFileStatus next() throws IOException {
                    final LocatedFileStatus result = this.stats.next();
                    return new LocatedFileStatus(ProxyFileSystem.this.swizzleFileStatus(result, false), result.getBlockLocations());
                }
            };
        }
        
        @Override
        public void access(final Path path, final FsAction action) throws AccessControlException, FileNotFoundException, IOException {
            final Path underlyingFsPath = this.swizzleParamPath(path);
            final FileStatus underlyingFsStatus = this.fs.getFileStatus(underlyingFsPath);
            try {
                if (Hadoop23Shims.accessMethod != null) {
                    Hadoop23Shims.accessMethod.invoke(this.fs, underlyingFsPath, action);
                }
                else {
                    DefaultFileAccess.checkFileAccess(this.fs, underlyingFsStatus, action);
                }
            }
            catch (AccessControlException err) {
                throw err;
            }
            catch (FileNotFoundException err2) {
                throw err2;
            }
            catch (IOException err3) {
                throw err3;
            }
            catch (Exception err4) {
                throw new RuntimeException(err4.getMessage(), err4);
            }
        }
    }
    
    public class KerberosNameShim implements HadoopShims.KerberosNameShim
    {
        private final KerberosName kerberosName;
        
        public KerberosNameShim(final String name) {
            this.kerberosName = new KerberosName(name);
        }
        
        @Override
        public String getDefaultRealm() {
            return this.kerberosName.getDefaultRealm();
        }
        
        @Override
        public String getServiceName() {
            return this.kerberosName.getServiceName();
        }
        
        @Override
        public String getHostName() {
            return this.kerberosName.getHostName();
        }
        
        @Override
        public String getRealm() {
            return this.kerberosName.getRealm();
        }
        
        @Override
        public String getShortName() throws IOException {
            return this.kerberosName.getShortName();
        }
    }
    
    public static class StoragePolicyShim implements HadoopShims.StoragePolicyShim
    {
        private final DistributedFileSystem dfs;
        
        public StoragePolicyShim(final DistributedFileSystem fs) {
            this.dfs = fs;
        }
        
        @Override
        public void setStoragePolicy(final Path path, final HadoopShims.StoragePolicyValue policy) throws IOException {
            switch (policy) {
                case MEMORY: {
                    this.dfs.setStoragePolicy(path, "LAZY_PERSIST");
                    break;
                }
                case SSD: {
                    this.dfs.setStoragePolicy(path, "ALL_SSD");
                    break;
                }
                case DEFAULT: {
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown storage policy " + policy);
                }
            }
        }
    }
    
    public class HdfsEncryptionShim implements HadoopShims.HdfsEncryptionShim
    {
        private final String HDFS_SECURITY_DEFAULT_CIPHER = "AES/CTR/NoPadding";
        private HdfsAdmin hdfsAdmin;
        private KeyProvider keyProvider;
        private Configuration conf;
        
        public HdfsEncryptionShim(final URI uri, final Configuration conf) throws IOException {
            this.hdfsAdmin = null;
            this.keyProvider = null;
            final DistributedFileSystem dfs = (DistributedFileSystem)FileSystem.get(uri, conf);
            this.conf = conf;
            this.keyProvider = dfs.getClient().getKeyProvider();
            this.hdfsAdmin = new HdfsAdmin(uri, conf);
        }
        
        @Override
        public boolean isPathEncrypted(final Path path) throws IOException {
            Path fullPath;
            if (path.isAbsolute()) {
                fullPath = path;
            }
            else {
                fullPath = path.getFileSystem(this.conf).makeQualified(path);
            }
            return "hdfs".equalsIgnoreCase(path.toUri().getScheme()) && this.hdfsAdmin.getEncryptionZoneForPath(fullPath) != null;
        }
        
        @Override
        public boolean arePathsOnSameEncryptionZone(final Path path1, final Path path2) throws IOException {
            final EncryptionZone zone1 = this.hdfsAdmin.getEncryptionZoneForPath(path1);
            final EncryptionZone zone2 = this.hdfsAdmin.getEncryptionZoneForPath(path2);
            return (zone1 == null && zone2 == null) || (zone1 != null && zone2 != null && zone1.equals((Object)zone2));
        }
        
        @Override
        public int comparePathKeyStrength(final Path path1, final Path path2) throws IOException {
            final EncryptionZone zone1 = this.hdfsAdmin.getEncryptionZoneForPath(path1);
            final EncryptionZone zone2 = this.hdfsAdmin.getEncryptionZoneForPath(path2);
            if (zone1 == null && zone2 == null) {
                return 0;
            }
            if (zone1 == null) {
                return -1;
            }
            if (zone2 == null) {
                return 1;
            }
            return this.compareKeyStrength(zone1.getKeyName(), zone2.getKeyName());
        }
        
        @Override
        public void createEncryptionZone(final Path path, final String keyName) throws IOException {
            this.hdfsAdmin.createEncryptionZone(path, keyName);
        }
        
        @Override
        public void createKey(final String keyName, final int bitLength) throws IOException, NoSuchAlgorithmException {
            this.checkKeyProvider();
            if (this.keyProvider.getMetadata(keyName) == null) {
                final KeyProvider.Options options = new KeyProvider.Options(this.conf);
                options.setCipher("AES/CTR/NoPadding");
                options.setBitLength(bitLength);
                this.keyProvider.createKey(keyName, options);
                this.keyProvider.flush();
                return;
            }
            throw new IOException("key '" + keyName + "' already exists");
        }
        
        @Override
        public void deleteKey(final String keyName) throws IOException {
            this.checkKeyProvider();
            if (this.keyProvider.getMetadata(keyName) != null) {
                this.keyProvider.deleteKey(keyName);
                this.keyProvider.flush();
                return;
            }
            throw new IOException("key '" + keyName + "' does not exist.");
        }
        
        @Override
        public List<String> getKeys() throws IOException {
            this.checkKeyProvider();
            return this.keyProvider.getKeys();
        }
        
        private void checkKeyProvider() throws IOException {
            if (this.keyProvider == null) {
                throw new IOException("HDFS security key provider is not configured on your server.");
            }
        }
        
        private int compareKeyStrength(final String keyname1, final String keyname2) throws IOException {
            if (this.keyProvider == null) {
                throw new IOException("HDFS security key provider is not configured on your server.");
            }
            final KeyProvider.Metadata meta1 = this.keyProvider.getMetadata(keyname1);
            final KeyProvider.Metadata meta2 = this.keyProvider.getMetadata(keyname2);
            if (meta1.getBitLength() < meta2.getBitLength()) {
                return -1;
            }
            if (meta1.getBitLength() == meta2.getBitLength()) {
                return 0;
            }
            return 1;
        }
    }
}
