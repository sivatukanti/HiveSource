// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.logaggregation;

import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.ipc.RPC;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.client.ClientRMProxy;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import java.util.TimerTask;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.AccessControlException;
import java.io.IOException;
import java.util.Timer;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.LimitedPrivate({ "yarn", "mapreduce" })
public class AggregatedLogDeletionService extends AbstractService
{
    private static final Log LOG;
    private Timer timer;
    private long checkIntervalMsecs;
    private LogDeletionTask task;
    
    private static void logIOException(final String comment, final IOException e) {
        if (e instanceof AccessControlException) {
            String message = e.getMessage();
            message = message.split("\n")[0];
            AggregatedLogDeletionService.LOG.warn(comment + " " + message);
        }
        else {
            AggregatedLogDeletionService.LOG.error(comment, e);
        }
    }
    
    public AggregatedLogDeletionService() {
        super(AggregatedLogDeletionService.class.getName());
        this.timer = null;
    }
    
    @Override
    protected void serviceStart() throws Exception {
        this.scheduleLogDeletionTask();
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        this.stopRMClient();
        this.stopTimer();
        super.serviceStop();
    }
    
    private void setLogAggCheckIntervalMsecs(final long retentionSecs) {
        final Configuration conf = this.getConfig();
        this.checkIntervalMsecs = 1000L * conf.getLong("yarn.log-aggregation.retain-check-interval-seconds", -1L);
        if (this.checkIntervalMsecs <= 0L) {
            this.checkIntervalMsecs = retentionSecs * 1000L / 10L;
        }
    }
    
    public void refreshLogRetentionSettings() throws IOException {
        if (this.getServiceState() == Service.STATE.STARTED) {
            final Configuration conf = this.createConf();
            this.setConfig(conf);
            this.stopRMClient();
            this.stopTimer();
            this.scheduleLogDeletionTask();
        }
        else {
            AggregatedLogDeletionService.LOG.warn("Failed to execute refreshLogRetentionSettings : Aggregated Log Deletion Service is not started");
        }
    }
    
    private void scheduleLogDeletionTask() throws IOException {
        final Configuration conf = this.getConfig();
        if (!conf.getBoolean("yarn.log-aggregation-enable", false)) {
            return;
        }
        final long retentionSecs = conf.getLong("yarn.log-aggregation.retain-seconds", -1L);
        if (retentionSecs < 0L) {
            AggregatedLogDeletionService.LOG.info("Log Aggregation deletion is disabled because retention is too small (" + retentionSecs + ")");
            return;
        }
        this.setLogAggCheckIntervalMsecs(retentionSecs);
        this.task = new LogDeletionTask(conf, retentionSecs, this.creatRMClient());
        (this.timer = new Timer()).scheduleAtFixedRate(this.task, 0L, this.checkIntervalMsecs);
    }
    
    private void stopTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }
    
    public long getCheckIntervalMsecs() {
        return this.checkIntervalMsecs;
    }
    
    protected Configuration createConf() {
        return new Configuration();
    }
    
    @VisibleForTesting
    protected ApplicationClientProtocol creatRMClient() throws IOException {
        return ClientRMProxy.createRMProxy(this.getConfig(), ApplicationClientProtocol.class);
    }
    
    @VisibleForTesting
    protected void stopRMClient() {
        if (this.task != null && this.task.getRMClient() != null) {
            RPC.stopProxy(this.task.getRMClient());
        }
    }
    
    static {
        LOG = LogFactory.getLog(AggregatedLogDeletionService.class);
    }
    
    static class LogDeletionTask extends TimerTask
    {
        private Configuration conf;
        private long retentionMillis;
        private String suffix;
        private Path remoteRootLogDir;
        private ApplicationClientProtocol rmClient;
        
        public LogDeletionTask(final Configuration conf, final long retentionSecs, final ApplicationClientProtocol rmClient) {
            this.suffix = null;
            this.remoteRootLogDir = null;
            this.rmClient = null;
            this.conf = conf;
            this.retentionMillis = retentionSecs * 1000L;
            this.suffix = LogAggregationUtils.getRemoteNodeLogDirSuffix(conf);
            this.remoteRootLogDir = new Path(conf.get("yarn.nodemanager.remote-app-log-dir", "/tmp/logs"));
            this.rmClient = rmClient;
        }
        
        @Override
        public void run() {
            final long cutoffMillis = System.currentTimeMillis() - this.retentionMillis;
            AggregatedLogDeletionService.LOG.info("aggregated log deletion started.");
            try {
                final FileSystem fs = this.remoteRootLogDir.getFileSystem(this.conf);
                for (final FileStatus userDir : fs.listStatus(this.remoteRootLogDir)) {
                    if (userDir.isDirectory()) {
                        final Path userDirPath = new Path(userDir.getPath(), this.suffix);
                        deleteOldLogDirsFrom(userDirPath, cutoffMillis, fs, this.rmClient);
                    }
                }
            }
            catch (IOException e) {
                logIOException("Error reading root log dir this deletion attempt is being aborted", e);
            }
            AggregatedLogDeletionService.LOG.info("aggregated log deletion finished.");
        }
        
        private static void deleteOldLogDirsFrom(final Path dir, final long cutoffMillis, final FileSystem fs, final ApplicationClientProtocol rmClient) {
            try {
                for (final FileStatus appDir : fs.listStatus(dir)) {
                    if (appDir.isDirectory() && appDir.getModificationTime() < cutoffMillis) {
                        final boolean appTerminated = isApplicationTerminated(ConverterUtils.toApplicationId(appDir.getPath().getName()), rmClient);
                        if (appTerminated && shouldDeleteLogDir(appDir, cutoffMillis, fs)) {
                            try {
                                AggregatedLogDeletionService.LOG.info("Deleting aggregated logs in " + appDir.getPath());
                                fs.delete(appDir.getPath(), true);
                            }
                            catch (IOException e) {
                                logIOException("Could not delete " + appDir.getPath(), e);
                            }
                        }
                        else if (!appTerminated) {
                            try {
                                for (final FileStatus node : fs.listStatus(appDir.getPath())) {
                                    if (node.getModificationTime() < cutoffMillis) {
                                        try {
                                            fs.delete(node.getPath(), true);
                                        }
                                        catch (IOException ex) {
                                            logIOException("Could not delete " + appDir.getPath(), ex);
                                        }
                                    }
                                }
                            }
                            catch (IOException e) {
                                logIOException("Error reading the contents of " + appDir.getPath(), e);
                            }
                        }
                    }
                }
            }
            catch (IOException e2) {
                logIOException("Could not read the contents of " + dir, e2);
            }
        }
        
        private static boolean shouldDeleteLogDir(final FileStatus dir, final long cutoffMillis, final FileSystem fs) {
            boolean shouldDelete = true;
            try {
                for (final FileStatus node : fs.listStatus(dir.getPath())) {
                    if (node.getModificationTime() >= cutoffMillis) {
                        shouldDelete = false;
                        break;
                    }
                }
            }
            catch (IOException e) {
                logIOException("Error reading the contents of " + dir.getPath(), e);
                shouldDelete = false;
            }
            return shouldDelete;
        }
        
        private static boolean isApplicationTerminated(final ApplicationId appId, final ApplicationClientProtocol rmClient) throws IOException {
            ApplicationReport appReport = null;
            try {
                appReport = rmClient.getApplicationReport(GetApplicationReportRequest.newInstance(appId)).getApplicationReport();
            }
            catch (ApplicationNotFoundException e2) {
                return true;
            }
            catch (YarnException e) {
                throw new IOException(e);
            }
            final YarnApplicationState currentState = appReport.getYarnApplicationState();
            return currentState == YarnApplicationState.FAILED || currentState == YarnApplicationState.KILLED || currentState == YarnApplicationState.FINISHED;
        }
        
        public ApplicationClientProtocol getRMClient() {
            return this.rmClient;
        }
    }
}
