// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.mapred;

import org.apache.commons.logging.LogFactory;
import java.util.List;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.ClientRMProxy;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import java.util.Collections;
import org.apache.hadoop.yarn.api.protocolrecords.ApplicationsRequestScope;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.mapreduce.TypeConverter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.mapreduce.Job;
import java.net.URI;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.shims.HadoopShims;

public class WebHCatJTShim23 implements HadoopShims.WebHCatJTShim
{
    private static final Log LOG;
    private JobClient jc;
    private final Configuration conf;
    
    public WebHCatJTShim23(final Configuration conf, final UserGroupInformation ugi) throws IOException {
        try {
            this.conf = conf;
            this.jc = ugi.doAs((PrivilegedExceptionAction<JobClient>)new PrivilegedExceptionAction<JobClient>() {
                @Override
                public JobClient run() throws IOException, InterruptedException {
                    return new JobClient(conf);
                }
            });
        }
        catch (InterruptedException ex) {
            throw new RuntimeException("Failed to create JobClient", ex);
        }
    }
    
    @Override
    public JobProfile getJobProfile(final JobID jobid) throws IOException {
        final RunningJob rj = this.getJob(jobid);
        if (rj == null) {
            return null;
        }
        final JobStatus jobStatus = rj.getJobStatus();
        return new JobProfile(jobStatus.getUsername(), (org.apache.hadoop.mapreduce.JobID)jobStatus.getJobID(), jobStatus.getJobFile(), jobStatus.getTrackingUrl(), jobStatus.getJobName());
    }
    
    @Override
    public JobStatus getJobStatus(final JobID jobid) throws IOException {
        final RunningJob rj = this.getJob(jobid);
        if (rj == null) {
            return null;
        }
        return rj.getJobStatus();
    }
    
    @Override
    public void killJob(final JobID jobid) throws IOException {
        final RunningJob rj = this.getJob(jobid);
        if (rj == null) {
            return;
        }
        rj.killJob();
    }
    
    @Override
    public JobStatus[] getAllJobs() throws IOException {
        return this.jc.getAllJobs();
    }
    
    @Override
    public void close() {
        try {
            this.jc.close();
        }
        catch (IOException ex) {}
    }
    
    @Override
    public void addCacheFile(final URI uri, final Job job) {
        job.addCacheFile(uri);
    }
    
    private RunningJob getJob(final JobID jobid) throws IOException {
        try {
            return this.jc.getJob(jobid);
        }
        catch (IOException ex) {
            final String msg = ex.getMessage();
            if (msg != null && msg.contains("ApplicationNotFoundException")) {
                WebHCatJTShim23.LOG.info("Job(" + jobid + ") not found: " + msg);
                return null;
            }
            throw ex;
        }
    }
    
    @Override
    public void killJobs(final String tag, final long timestamp) {
        try {
            WebHCatJTShim23.LOG.info("Looking for jobs to kill...");
            final Set<ApplicationId> childJobs = this.getYarnChildJobs(tag, timestamp);
            if (childJobs.isEmpty()) {
                WebHCatJTShim23.LOG.info("No jobs found from");
                return;
            }
            WebHCatJTShim23.LOG.info(String.format("Found MR jobs count: %d", childJobs.size()));
            WebHCatJTShim23.LOG.info("Killing all found jobs");
            final YarnClient yarnClient = YarnClient.createYarnClient();
            yarnClient.init(this.conf);
            yarnClient.start();
            for (final ApplicationId app : childJobs) {
                WebHCatJTShim23.LOG.info(String.format("Killing job: %s ...", app));
                yarnClient.killApplication(app);
                WebHCatJTShim23.LOG.info(String.format("Job %s killed", app));
            }
        }
        catch (YarnException ye) {
            throw new RuntimeException("Exception occurred while killing child job(s)", ye);
        }
        catch (IOException ioe) {
            throw new RuntimeException("Exception occurred while killing child job(s)", ioe);
        }
    }
    
    @Override
    public Set<String> getJobs(final String tag, final long timestamp) {
        final Set<ApplicationId> childYarnJobs = this.getYarnChildJobs(tag, timestamp);
        final Set<String> childJobs = new HashSet<String>();
        for (final ApplicationId id : childYarnJobs) {
            final String childJobId = TypeConverter.fromYarn(id).toString();
            childJobs.add(childJobId);
        }
        return childJobs;
    }
    
    private Set<ApplicationId> getYarnChildJobs(final String tag, final long timestamp) {
        final Set<ApplicationId> childYarnJobs = new HashSet<ApplicationId>();
        WebHCatJTShim23.LOG.info(String.format("Querying RM for tag = %s, starting with ts = %s", tag, timestamp));
        final GetApplicationsRequest gar = GetApplicationsRequest.newInstance();
        gar.setScope(ApplicationsRequestScope.OWN);
        gar.setStartRange(timestamp, System.currentTimeMillis());
        gar.setApplicationTags(Collections.singleton(tag));
        try {
            final ApplicationClientProtocol proxy = ClientRMProxy.createRMProxy(this.conf, ApplicationClientProtocol.class);
            final GetApplicationsResponse apps = proxy.getApplications(gar);
            final List<ApplicationReport> appsList = apps.getApplicationList();
            for (final ApplicationReport appReport : appsList) {
                childYarnJobs.add(appReport.getApplicationId());
            }
        }
        catch (IOException ioe) {
            throw new RuntimeException("Exception occurred while finding child jobs", ioe);
        }
        catch (YarnException ye) {
            throw new RuntimeException("Exception occurred while finding child jobs", ye);
        }
        return childYarnJobs;
    }
    
    static {
        LOG = LogFactory.getLog(WebHCatJTShim23.class);
    }
}
