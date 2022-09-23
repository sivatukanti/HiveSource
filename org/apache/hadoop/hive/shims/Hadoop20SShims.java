// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import org.apache.hadoop.security.KerberosName;
import java.net.InetSocketAddress;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.TaskID;
import java.util.Iterator;
import org.apache.hadoop.mapred.JobTracker;
import org.apache.hadoop.mapred.JobInProgress;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.Credentials;
import java.nio.ByteBuffer;
import java.lang.reflect.Constructor;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.FSDataInputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.fs.ProxyFileSystem;
import java.net.URI;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.util.TreeMap;
import org.apache.hadoop.fs.BlockLocation;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.mapred.WebHCatJTShim20S;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.util.VersionInfo;
import org.apache.hadoop.io.LongWritable;
import java.util.Comparator;
import org.apache.hadoop.mapred.lib.TotalOrderPartitioner;
import org.apache.hadoop.fs.Trash;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.ClusterStatus;
import java.net.MalformedURLException;
import org.apache.hadoop.mapred.TaskLogServlet;
import java.net.URL;
import java.util.ArrayList;
import org.apache.hadoop.fs.FileStatus;
import java.io.IOException;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.InputSplit;

public class Hadoop20SShims extends HadoopShimsSecure
{
    private volatile HadoopShims.HCatHadoopShims hcatShimInstance;
    
    @Override
    public HadoopShims.CombineFileInputFormatShim getCombineFileInputFormat() {
        return new CombineFileInputFormatShim() {
            public RecordReader getRecordReader(final InputSplit split, final JobConf job, final Reporter reporter) throws IOException {
                throw new IOException("CombineFileInputFormat.getRecordReader not needed.");
            }
            
            protected FileStatus[] listStatus(final JobConf job) throws IOException {
                final FileStatus[] result = super.listStatus(job);
                boolean foundDir = false;
                for (final FileStatus stat : result) {
                    if (stat.isDir()) {
                        foundDir = true;
                        break;
                    }
                }
                if (!foundDir) {
                    return result;
                }
                final ArrayList<FileStatus> files = new ArrayList<FileStatus>();
                for (final FileStatus stat2 : result) {
                    if (!stat2.isDir()) {
                        files.add(stat2);
                    }
                }
                return files.toArray(new FileStatus[files.size()]);
            }
        };
    }
    
    @Override
    public String getTaskAttemptLogUrl(final JobConf conf, final String taskTrackerHttpAddress, final String taskAttemptId) throws MalformedURLException {
        final URL taskTrackerHttpURL = new URL(taskTrackerHttpAddress);
        return TaskLogServlet.getTaskLogUrl(taskTrackerHttpURL.getHost(), Integer.toString(taskTrackerHttpURL.getPort()), taskAttemptId);
    }
    
    @Override
    public HadoopShims.JobTrackerState getJobTrackerState(final ClusterStatus clusterStatus) throws Exception {
        switch (clusterStatus.getJobTrackerState()) {
            case INITIALIZING: {
                return HadoopShims.JobTrackerState.INITIALIZING;
            }
            case RUNNING: {
                return HadoopShims.JobTrackerState.RUNNING;
            }
            default: {
                final String errorMsg = "Unrecognized JobTracker state: " + clusterStatus.getJobTrackerState();
                throw new Exception(errorMsg);
            }
        }
    }
    
    @Override
    public TaskAttemptContext newTaskAttemptContext(final Configuration conf, final Progressable progressable) {
        return new TaskAttemptContext(conf, new TaskAttemptID()) {
            public void progress() {
                progressable.progress();
            }
        };
    }
    
    @Override
    public TaskAttemptID newTaskAttemptID(final JobID jobId, final boolean isMap, final int taskId, final int id) {
        return new TaskAttemptID(jobId.getJtIdentifier(), jobId.getId(), isMap, taskId, id);
    }
    
    @Override
    public JobContext newJobContext(final Job job) {
        return new JobContext(job.getConfiguration(), job.getJobID());
    }
    
    @Override
    public void startPauseMonitor(final Configuration conf) {
    }
    
    @Override
    public boolean isLocalMode(final Configuration conf) {
        return "local".equals(this.getJobLauncherRpcAddress(conf));
    }
    
    @Override
    public String getJobLauncherRpcAddress(final Configuration conf) {
        return conf.get("mapred.job.tracker");
    }
    
    @Override
    public void setJobLauncherRpcAddress(final Configuration conf, final String val) {
        conf.set("mapred.job.tracker", val);
    }
    
    @Override
    public String getJobLauncherHttpAddress(final Configuration conf) {
        return conf.get("mapred.job.tracker.http.address");
    }
    
    @Override
    public boolean moveToAppropriateTrash(final FileSystem fs, final Path path, final Configuration conf) throws IOException {
        final Configuration dupConf = new Configuration(conf);
        FileSystem.setDefaultUri(dupConf, fs.getUri());
        final Trash trash = new Trash(dupConf);
        return trash.moveToTrash(path);
    }
    
    @Override
    public long getDefaultBlockSize(final FileSystem fs, final Path path) {
        return fs.getDefaultBlockSize();
    }
    
    @Override
    public short getDefaultReplication(final FileSystem fs, final Path path) {
        return fs.getDefaultReplication();
    }
    
    @Override
    public void refreshDefaultQueue(final Configuration conf, final String userName) {
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
                return o1.compareTo((Object)o2);
            }
        };
    }
    
    @Override
    public MiniMrShim getMiniMrCluster(final Configuration conf, final int numberOfTaskTrackers, final String nameNode, final int numDir) throws IOException {
        return new MiniMrShim(conf, numberOfTaskTrackers, nameNode, numDir);
    }
    
    @Override
    public MiniMrShim getMiniTezCluster(final Configuration conf, final int numberOfTaskTrackers, final String nameNode, final int numDir) throws IOException {
        throw new IOException("Cannot run tez on current hadoop, Version: " + VersionInfo.getVersion());
    }
    
    @Override
    public MiniMrShim getMiniSparkCluster(final Configuration conf, final int numberOfTaskTrackers, final String nameNode, final int numDir) throws IOException {
        throw new IOException("Cannot run Spark on YARN on current Hadoop, Version: " + VersionInfo.getVersion());
    }
    
    @Override
    public HadoopShims.MiniDFSShim getMiniDfs(final Configuration conf, final int numDataNodes, final boolean format, final String[] racks) throws IOException {
        return new MiniDFSShim(new MiniDFSCluster(conf, numDataNodes, format, racks));
    }
    
    @Override
    public HadoopShims.HCatHadoopShims getHCatShim() {
        if (this.hcatShimInstance == null) {
            this.hcatShimInstance = new HCatHadoopShims20S();
        }
        return this.hcatShimInstance;
    }
    
    @Override
    public HadoopShims.WebHCatJTShim getWebHCatShim(final Configuration conf, final UserGroupInformation ugi) throws IOException {
        return new WebHCatJTShim20S(conf, ugi);
    }
    
    @Override
    public List<FileStatus> listLocatedStatus(final FileSystem fs, final Path path, final PathFilter filter) throws IOException {
        return Arrays.asList(fs.listStatus(path, filter));
    }
    
    @Override
    public BlockLocation[] getLocations(final FileSystem fs, final FileStatus status) throws IOException {
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
        stream.sync();
    }
    
    @Override
    public HadoopShims.HdfsFileStatus getFullFileStatus(final Configuration conf, final FileSystem fs, final Path file) throws IOException {
        return new Hadoop20SFileStatus(fs.getFileStatus(file));
    }
    
    @Override
    public void setFullFileStatus(final Configuration conf, final HadoopShims.HdfsFileStatus sourceStatus, final FileSystem fs, final Path target) throws IOException {
        final String group = sourceStatus.getFileStatus().getGroup();
        final String permission = Integer.toString(sourceStatus.getFileStatus().getPermission().toShort(), 8);
        try {
            final FsShell fshell = new FsShell();
            fshell.setConf(conf);
            this.run(fshell, new String[] { "-chgrp", "-R", group, target.toString() });
            this.run(fshell, new String[] { "-chmod", "-R", permission, target.toString() });
        }
        catch (Exception e) {
            throw new IOException("Unable to set permissions of " + target, e);
        }
        try {
            if (Hadoop20SShims.LOG.isDebugEnabled()) {
                this.getFullFileStatus(conf, fs, target).debugLog();
            }
        }
        catch (Exception ex) {}
    }
    
    @Override
    public FileSystem createProxyFileSystem(final FileSystem fs, final URI uri) {
        return new ProxyFileSystem(fs, uri);
    }
    
    @Override
    public Map<String, String> getHadoopConfNames() {
        final Map<String, String> ret = new HashMap<String, String>();
        ret.put("HADOOPFS", "fs.default.name");
        ret.put("HADOOPMAPFILENAME", "map.input.file");
        ret.put("HADOOPMAPREDINPUTDIR", "mapred.input.dir");
        ret.put("HADOOPMAPREDINPUTDIRRECURSIVE", "mapred.input.dir.recursive");
        ret.put("MAPREDMAXSPLITSIZE", "mapred.max.split.size");
        ret.put("MAPREDMINSPLITSIZE", "mapred.min.split.size");
        ret.put("MAPREDMINSPLITSIZEPERNODE", "mapred.min.split.size.per.node");
        ret.put("MAPREDMINSPLITSIZEPERRACK", "mapred.min.split.size.per.rack");
        ret.put("HADOOPNUMREDUCERS", "mapred.reduce.tasks");
        ret.put("HADOOPJOBNAME", "mapred.job.name");
        ret.put("HADOOPSPECULATIVEEXECREDUCERS", "mapred.reduce.tasks.speculative.execution");
        ret.put("MAPREDSETUPCLEANUPNEEDED", "mapred.committer.job.setup.cleanup.needed");
        ret.put("MAPREDTASKCLEANUPNEEDED", "mapreduce.job.committer.task.cleanup.needed");
        return ret;
    }
    
    @Override
    public HadoopShims.ZeroCopyReaderShim getZeroCopyReader(final FSDataInputStream in, final HadoopShims.ByteBufferPoolShim pool) throws IOException {
        return null;
    }
    
    @Override
    public HadoopShims.DirectDecompressorShim getDirectDecompressor(final HadoopShims.DirectCompressionType codec) {
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
        final boolean origDisableHDFSCache = conf.getBoolean("fs." + uri.getScheme() + ".impl.disable.cache", false);
        conf.setBoolean("fs." + uri.getScheme() + ".impl.disable.cache", true);
        final FileSystem fs = FileSystem.get(uri, conf);
        conf.setBoolean("fs." + uri.getScheme() + ".impl.disable.cache", origDisableHDFSCache);
        return fs;
    }
    
    @Override
    public void getMergedCredentials(final JobConf jobConf) throws IOException {
        throw new IOException("Merging of credentials not supported in this version of hadoop");
    }
    
    @Override
    public void mergeCredentials(final JobConf dest, final JobConf src) throws IOException {
        throw new IOException("Merging of credentials not supported in this version of hadoop");
    }
    
    @Override
    public String getPassword(final Configuration conf, final String name) {
        return conf.get(name);
    }
    
    @Override
    public boolean supportStickyBit() {
        return false;
    }
    
    @Override
    public boolean hasStickyBit(final FsPermission permission) {
        return false;
    }
    
    @Override
    public boolean supportTrashFeature() {
        return false;
    }
    
    @Override
    public Path getCurrentTrashPath(final Configuration conf, final FileSystem fs) {
        return null;
    }
    
    @Override
    public boolean isDirectory(final FileStatus fileStatus) {
        return fileStatus.isDir();
    }
    
    @Override
    public KerberosNameShim getKerberosNameShim(final String name) throws IOException {
        return new KerberosNameShim(name);
    }
    
    @Override
    public HadoopShims.StoragePolicyShim getStoragePolicyShim(final FileSystem fs) {
        return null;
    }
    
    @Override
    public boolean runDistCp(final Path src, final Path dst, final Configuration conf) throws IOException {
        final String[] params = { "-update", "-skipcrccheck", src.toString(), dst.toString() };
        int rc;
        try {
            final Class clazzDistCp = Class.forName("org.apache.hadoop.tools.distcp2");
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
    
    @Override
    public HadoopShims.HdfsEncryptionShim createHdfsEncryptionShim(final FileSystem fs, final Configuration conf) throws IOException {
        return new HadoopShims.NoopHdfsEncryptionShim();
    }
    
    @Override
    public Path getPathWithoutSchemeAndAuthority(final Path path) {
        return path;
    }
    
    @Override
    public int readByteBuffer(final FSDataInputStream file, final ByteBuffer dest) throws IOException {
        final int pos = dest.position();
        if (dest.hasArray()) {
            final int result = file.read(dest.array(), dest.arrayOffset(), dest.remaining());
            if (result > 0) {
                dest.position(pos + result);
            }
            return result;
        }
        final byte[] arr = new byte[dest.remaining()];
        final int result2 = file.read(arr, 0, arr.length);
        if (result2 > 0) {
            dest.put(arr, 0, result2);
            dest.position(pos + result2);
        }
        return result2;
    }
    
    @Override
    public void addDelegationTokens(final FileSystem fs, final Credentials cred, final String uname) throws IOException {
        final Token<?> fsToken = fs.getDelegationToken(uname);
        cred.addToken(fsToken.getService(), (Token<? extends TokenIdentifier>)fsToken);
    }
    
    public class MiniMrShim implements HadoopShims.MiniMrShim
    {
        private final MiniMRCluster mr;
        
        public MiniMrShim(final Configuration conf, final int numberOfTaskTrackers, final String nameNode, final int numDir) throws IOException {
            this.mr = new MiniMRCluster(numberOfTaskTrackers, nameNode, numDir);
        }
        
        @Override
        public int getJobTrackerPort() throws UnsupportedOperationException {
            return this.mr.getJobTrackerPort();
        }
        
        @Override
        public void shutdown() throws IOException {
            final MiniMRCluster.JobTrackerRunner runner = this.mr.getJobTrackerRunner();
            final JobTracker tracker = runner.getJobTracker();
            if (tracker != null) {
                for (final JobInProgress running : tracker.getRunningJobs()) {
                    try {
                        running.kill();
                    }
                    catch (Exception ex) {}
                }
            }
            runner.shutdown();
        }
        
        @Override
        public void setupConfiguration(final Configuration conf) {
            Hadoop20SShims.this.setJobLauncherRpcAddress(conf, "localhost:" + this.mr.getJobTrackerPort());
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
            return this.cluster.getFileSystem();
        }
        
        @Override
        public void shutdown() {
            this.cluster.shutdown();
        }
    }
    
    private final class HCatHadoopShims20S implements HadoopShims.HCatHadoopShims
    {
        @Override
        public TaskID createTaskID() {
            return new TaskID();
        }
        
        @Override
        public TaskAttemptID createTaskAttemptID() {
            return new TaskAttemptID();
        }
        
        @Override
        public TaskAttemptContext createTaskAttemptContext(final Configuration conf, final TaskAttemptID taskId) {
            return new TaskAttemptContext(conf, taskId);
        }
        
        @Override
        public org.apache.hadoop.mapred.TaskAttemptContext createTaskAttemptContext(final JobConf conf, final org.apache.hadoop.mapred.TaskAttemptID taskId, final Progressable progressable) {
            org.apache.hadoop.mapred.TaskAttemptContext newContext = null;
            try {
                final Constructor construct = org.apache.hadoop.mapred.TaskAttemptContext.class.getDeclaredConstructor(JobConf.class, org.apache.hadoop.mapred.TaskAttemptID.class, Progressable.class);
                construct.setAccessible(true);
                newContext = construct.newInstance(conf, taskId, progressable);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            return newContext;
        }
        
        @Override
        public JobContext createJobContext(final Configuration conf, final JobID jobId) {
            return new JobContext(conf, jobId);
        }
        
        @Override
        public org.apache.hadoop.mapred.JobContext createJobContext(final JobConf conf, final JobID jobId, final Progressable progressable) {
            org.apache.hadoop.mapred.JobContext newContext = null;
            try {
                final Constructor construct = org.apache.hadoop.mapred.JobContext.class.getDeclaredConstructor(JobConf.class, JobID.class, Progressable.class);
                construct.setAccessible(true);
                newContext = construct.newInstance(conf, jobId, progressable);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            return newContext;
        }
        
        @Override
        public void commitJob(final OutputFormat outputFormat, final Job job) throws IOException {
            if (job.getConfiguration().get("mapred.job.tracker", "").equalsIgnoreCase("local")) {
                try {
                    outputFormat.getOutputCommitter(this.createTaskAttemptContext(job.getConfiguration(), this.createTaskAttemptID())).commitJob((JobContext)job);
                }
                catch (IOException e) {
                    throw new IOException("Failed to cleanup job", e);
                }
                catch (InterruptedException e2) {
                    throw new IOException("Failed to cleanup job", e2);
                }
            }
        }
        
        @Override
        public void abortJob(final OutputFormat outputFormat, final Job job) throws IOException {
            if (job.getConfiguration().get("mapred.job.tracker", "").equalsIgnoreCase("local")) {
                try {
                    outputFormat.getOutputCommitter(this.createTaskAttemptContext(job.getConfiguration(), new TaskAttemptID())).abortJob((JobContext)job, JobStatus.State.FAILED);
                }
                catch (IOException e) {
                    throw new IOException("Failed to abort job", e);
                }
                catch (InterruptedException e2) {
                    throw new IOException("Failed to abort job", e2);
                }
            }
        }
        
        @Override
        public InetSocketAddress getResourceManagerAddress(final Configuration conf) {
            return JobTracker.getAddress(conf);
        }
        
        @Override
        public String getPropertyName(final PropertyName name) {
            switch (name) {
                case CACHE_ARCHIVES: {
                    return "mapred.cache.archives";
                }
                case CACHE_FILES: {
                    return "mapred.cache.files";
                }
                case CACHE_SYMLINK: {
                    return "mapred.create.symlink";
                }
                case CLASSPATH_ARCHIVES: {
                    return "mapred.job.classpath.archives";
                }
                case CLASSPATH_FILES: {
                    return "mapred.job.classpath.files";
                }
                default: {
                    return "";
                }
            }
        }
        
        @Override
        public boolean isFileInHDFS(final FileSystem fs, final Path path) throws IOException {
            return "hdfs".equals(fs.getUri().getScheme());
        }
    }
    
    public class Hadoop20SFileStatus implements HadoopShims.HdfsFileStatus
    {
        private FileStatus fileStatus;
        
        public Hadoop20SFileStatus(final FileStatus fileStatus) {
            this.fileStatus = fileStatus;
        }
        
        @Override
        public FileStatus getFileStatus() {
            return this.fileStatus;
        }
        
        @Override
        public void debugLog() {
            if (this.fileStatus != null) {
                HadoopShimsSecure.LOG.debug(this.fileStatus.toString());
            }
        }
    }
    
    public class KerberosNameShim implements HadoopShims.KerberosNameShim
    {
        private KerberosName kerberosName;
        
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
}
