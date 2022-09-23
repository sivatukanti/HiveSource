// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.CombineFileSplit;
import java.net.InetSocketAddress;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.TaskID;
import java.util.Set;
import org.apache.hadoop.mapred.JobStatus;
import org.apache.hadoop.mapred.JobProfile;
import java.security.NoSuchAlgorithmException;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.security.Credentials;
import java.nio.ByteBuffer;
import org.apache.hadoop.fs.permission.FsPermission;
import java.security.AccessControlException;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.FSDataInputStream;
import java.util.Map;
import java.net.URI;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.util.TreeMap;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import java.util.List;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.LongWritable;
import java.util.Comparator;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.mapred.ClusterStatus;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.MalformedURLException;
import org.apache.hadoop.mapred.JobConf;

public interface HadoopShims
{
    String getTaskAttemptLogUrl(final JobConf p0, final String p1, final String p2) throws MalformedURLException;
    
    MiniMrShim getMiniMrCluster(final Configuration p0, final int p1, final String p2, final int p3) throws IOException;
    
    MiniMrShim getMiniTezCluster(final Configuration p0, final int p1, final String p2, final int p3) throws IOException;
    
    MiniMrShim getMiniSparkCluster(final Configuration p0, final int p1, final String p2, final int p3) throws IOException;
    
    MiniDFSShim getMiniDfs(final Configuration p0, final int p1, final boolean p2, final String[] p3) throws IOException;
    
    CombineFileInputFormatShim getCombineFileInputFormat();
    
    JobTrackerState getJobTrackerState(final ClusterStatus p0) throws Exception;
    
    TaskAttemptContext newTaskAttemptContext(final Configuration p0, final Progressable p1);
    
    TaskAttemptID newTaskAttemptID(final JobID p0, final boolean p1, final int p2, final int p3);
    
    JobContext newJobContext(final Job p0);
    
    void startPauseMonitor(final Configuration p0);
    
    boolean isLocalMode(final Configuration p0);
    
    String getJobLauncherRpcAddress(final Configuration p0);
    
    void setJobLauncherRpcAddress(final Configuration p0, final String p1);
    
    String getJobLauncherHttpAddress(final Configuration p0);
    
    boolean moveToAppropriateTrash(final FileSystem p0, final Path p1, final Configuration p2) throws IOException;
    
    long getDefaultBlockSize(final FileSystem p0, final Path p1);
    
    short getDefaultReplication(final FileSystem p0, final Path p1);
    
    void refreshDefaultQueue(final Configuration p0, final String p1) throws IOException;
    
    void setTotalOrderPartitionFile(final JobConf p0, final Path p1);
    
    Comparator<LongWritable> getLongComparator();
    
    List<FileStatus> listLocatedStatus(final FileSystem p0, final Path p1, final PathFilter p2) throws IOException;
    
    BlockLocation[] getLocations(final FileSystem p0, final FileStatus p1) throws IOException;
    
    TreeMap<Long, BlockLocation> getLocationsWithOffset(final FileSystem p0, final FileStatus p1) throws IOException;
    
    void hflush(final FSDataOutputStream p0) throws IOException;
    
    HdfsFileStatus getFullFileStatus(final Configuration p0, final FileSystem p1, final Path p2) throws IOException;
    
    void setFullFileStatus(final Configuration p0, final HdfsFileStatus p1, final FileSystem p2, final Path p3) throws IOException;
    
    HCatHadoopShims getHCatShim();
    
    WebHCatJTShim getWebHCatShim(final Configuration p0, final UserGroupInformation p1) throws IOException;
    
    FileSystem createProxyFileSystem(final FileSystem p0, final URI p1);
    
    Map<String, String> getHadoopConfNames();
    
    StoragePolicyShim getStoragePolicyShim(final FileSystem p0);
    
    ZeroCopyReaderShim getZeroCopyReader(final FSDataInputStream p0, final ByteBufferPoolShim p1) throws IOException;
    
    DirectDecompressorShim getDirectDecompressor(final DirectCompressionType p0);
    
    Configuration getConfiguration(final JobContext p0);
    
    JobConf getJobConf(final org.apache.hadoop.mapred.JobContext p0);
    
    FileSystem getNonCachedFileSystem(final URI p0, final Configuration p1) throws IOException;
    
    void getMergedCredentials(final JobConf p0) throws IOException;
    
    void mergeCredentials(final JobConf p0, final JobConf p1) throws IOException;
    
    void checkFileAccess(final FileSystem p0, final FileStatus p1, final FsAction p2) throws IOException, AccessControlException, Exception;
    
    String getPassword(final Configuration p0, final String p1) throws IOException;
    
    boolean supportStickyBit();
    
    boolean hasStickyBit(final FsPermission p0);
    
    boolean supportTrashFeature();
    
    Path getCurrentTrashPath(final Configuration p0, final FileSystem p1);
    
    boolean isDirectory(final FileStatus p0);
    
    KerberosNameShim getKerberosNameShim(final String p0) throws IOException;
    
    boolean runDistCp(final Path p0, final Path p1, final Configuration p2) throws IOException;
    
    HdfsEncryptionShim createHdfsEncryptionShim(final FileSystem p0, final Configuration p1) throws IOException;
    
    Path getPathWithoutSchemeAndAuthority(final Path p0);
    
    int readByteBuffer(final FSDataInputStream p0, final ByteBuffer p1) throws IOException;
    
    void addDelegationTokens(final FileSystem p0, final Credentials p1, final String p2) throws IOException;
    
    public enum JobTrackerState
    {
        INITIALIZING, 
        RUNNING;
    }
    
    public enum StoragePolicyValue
    {
        MEMORY, 
        SSD, 
        DEFAULT;
        
        public static StoragePolicyValue lookup(final String name) {
            if (name == null) {
                return StoragePolicyValue.DEFAULT;
            }
            return valueOf(name.toUpperCase().trim());
        }
    }
    
    public enum DirectCompressionType
    {
        NONE, 
        ZLIB_NOHEADER, 
        ZLIB, 
        SNAPPY;
    }
    
    public static class NoopHdfsEncryptionShim implements HdfsEncryptionShim
    {
        @Override
        public boolean isPathEncrypted(final Path path) throws IOException {
            return false;
        }
        
        @Override
        public boolean arePathsOnSameEncryptionZone(final Path path1, final Path path2) throws IOException {
            return true;
        }
        
        @Override
        public int comparePathKeyStrength(final Path path1, final Path path2) throws IOException {
            return 0;
        }
        
        @Override
        public void createEncryptionZone(final Path path, final String keyName) {
        }
        
        @Override
        public void createKey(final String keyName, final int bitLength) {
        }
        
        @Override
        public void deleteKey(final String keyName) throws IOException {
        }
        
        @Override
        public List<String> getKeys() throws IOException {
            return null;
        }
    }
    
    public interface HdfsEncryptionShim
    {
        boolean isPathEncrypted(final Path p0) throws IOException;
        
        boolean arePathsOnSameEncryptionZone(final Path p0, final Path p1) throws IOException;
        
        int comparePathKeyStrength(final Path p0, final Path p1) throws IOException;
        
        @VisibleForTesting
        void createEncryptionZone(final Path p0, final String p1) throws IOException;
        
        @VisibleForTesting
        void createKey(final String p0, final int p1) throws IOException, NoSuchAlgorithmException;
        
        @VisibleForTesting
        void deleteKey(final String p0) throws IOException;
        
        @VisibleForTesting
        List<String> getKeys() throws IOException;
    }
    
    public interface KerberosNameShim
    {
        String getDefaultRealm();
        
        String getServiceName();
        
        String getHostName();
        
        String getRealm();
        
        String getShortName() throws IOException;
    }
    
    public interface DirectDecompressorShim
    {
        void decompress(final ByteBuffer p0, final ByteBuffer p1) throws IOException;
    }
    
    public interface ZeroCopyReaderShim
    {
        ByteBuffer readBuffer(final int p0, final boolean p1) throws IOException;
        
        void releaseBuffer(final ByteBuffer p0);
    }
    
    public interface ByteBufferPoolShim
    {
        ByteBuffer getBuffer(final boolean p0, final int p1);
        
        void putBuffer(final ByteBuffer p0);
    }
    
    public interface StoragePolicyShim
    {
        void setStoragePolicy(final Path p0, final StoragePolicyValue p1) throws IOException;
    }
    
    public interface WebHCatJTShim
    {
        JobProfile getJobProfile(final org.apache.hadoop.mapred.JobID p0) throws IOException;
        
        JobStatus getJobStatus(final org.apache.hadoop.mapred.JobID p0) throws IOException;
        
        void killJob(final org.apache.hadoop.mapred.JobID p0) throws IOException;
        
        JobStatus[] getAllJobs() throws IOException;
        
        void close();
        
        void addCacheFile(final URI p0, final Job p1);
        
        void killJobs(final String p0, final long p1);
        
        Set<String> getJobs(final String p0, final long p1);
    }
    
    public interface HCatHadoopShims
    {
        TaskID createTaskID();
        
        TaskAttemptID createTaskAttemptID();
        
        TaskAttemptContext createTaskAttemptContext(final Configuration p0, final TaskAttemptID p1);
        
        org.apache.hadoop.mapred.TaskAttemptContext createTaskAttemptContext(final JobConf p0, final org.apache.hadoop.mapred.TaskAttemptID p1, final Progressable p2);
        
        JobContext createJobContext(final Configuration p0, final JobID p1);
        
        org.apache.hadoop.mapred.JobContext createJobContext(final JobConf p0, final JobID p1, final Progressable p2);
        
        void commitJob(final OutputFormat p0, final Job p1) throws IOException;
        
        void abortJob(final OutputFormat p0, final Job p1) throws IOException;
        
        InetSocketAddress getResourceManagerAddress(final Configuration p0);
        
        String getPropertyName(final PropertyName p0);
        
        boolean isFileInHDFS(final FileSystem p0, final Path p1) throws IOException;
        
        public enum PropertyName
        {
            CACHE_ARCHIVES, 
            CACHE_FILES, 
            CACHE_SYMLINK, 
            CLASSPATH_ARCHIVES, 
            CLASSPATH_FILES;
        }
    }
    
    public interface HdfsFileStatus
    {
        FileStatus getFileStatus();
        
        void debugLog();
    }
    
    public interface CombineFileInputFormatShim<K, V>
    {
        Path[] getInputPathsShim(final JobConf p0);
        
        void createPool(final JobConf p0, final PathFilter... p1);
        
        CombineFileSplit[] getSplits(final JobConf p0, final int p1) throws IOException;
        
        CombineFileSplit getInputSplitShim() throws IOException;
        
        RecordReader getRecordReader(final JobConf p0, final CombineFileSplit p1, final Reporter p2, final Class<RecordReader<K, V>> p3) throws IOException;
    }
    
    public interface MiniDFSShim
    {
        FileSystem getFileSystem() throws IOException;
        
        void shutdown() throws IOException;
    }
    
    public interface MiniMrShim
    {
        int getJobTrackerPort() throws UnsupportedOperationException;
        
        void shutdown() throws IOException;
        
        void setupConfiguration(final Configuration p0);
    }
}
