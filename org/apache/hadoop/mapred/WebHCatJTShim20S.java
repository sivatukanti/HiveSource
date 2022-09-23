// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.mapred;

import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.Job;
import java.net.URI;
import java.net.InetSocketAddress;
import org.apache.hadoop.ipc.VersionedProtocol;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.shims.HadoopShims;

public class WebHCatJTShim20S implements HadoopShims.WebHCatJTShim
{
    private JobSubmissionProtocol cnx;
    
    public WebHCatJTShim20S(final Configuration conf, final UserGroupInformation ugi) throws IOException {
        this.cnx = (JobSubmissionProtocol)RPC.getProxy((Class)JobSubmissionProtocol.class, 28L, this.getAddress(conf), ugi, conf, NetUtils.getSocketFactory(conf, JobSubmissionProtocol.class));
    }
    
    @Override
    public JobProfile getJobProfile(final JobID jobid) throws IOException {
        return this.cnx.getJobProfile(jobid);
    }
    
    @Override
    public JobStatus getJobStatus(final JobID jobid) throws IOException {
        return this.cnx.getJobStatus(jobid);
    }
    
    @Override
    public void killJob(final JobID jobid) throws IOException {
        this.cnx.killJob(jobid);
    }
    
    @Override
    public JobStatus[] getAllJobs() throws IOException {
        return this.cnx.getAllJobs();
    }
    
    @Override
    public void close() {
        RPC.stopProxy((VersionedProtocol)this.cnx);
    }
    
    private InetSocketAddress getAddress(final Configuration conf) {
        final String jobTrackerStr = conf.get("mapred.job.tracker", "localhost:8012");
        return NetUtils.createSocketAddr(jobTrackerStr);
    }
    
    @Override
    public void addCacheFile(final URI uri, final Job job) {
        DistributedCache.addCacheFile(uri, job.getConfiguration());
    }
    
    @Override
    public void killJobs(final String tag, final long timestamp) {
    }
    
    @Override
    public Set<String> getJobs(final String tag, final long timestamp) {
        return new HashSet<String>();
    }
}
