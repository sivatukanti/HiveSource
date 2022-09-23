// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.log;

import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.util.Times;
import java.util.Map;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import java.io.IOException;
import org.apache.hadoop.yarn.logaggregation.AggregatedLogFormat;
import org.apache.hadoop.fs.FileStatus;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.yarn.logaggregation.LogAggregationUtils;
import org.apache.hadoop.fs.Path;
import com.google.inject.Inject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class AggregatedLogsBlock extends HtmlBlock
{
    private final Configuration conf;
    
    @Inject
    AggregatedLogsBlock(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    protected void render(final Block html) {
        final ContainerId containerId = this.verifyAndGetContainerId(html);
        final NodeId nodeId = this.verifyAndGetNodeId(html);
        final String appOwner = this.verifyAndGetAppOwner(html);
        final LogLimits logLimits = this.verifyAndGetLogLimits(html);
        if (containerId == null || nodeId == null || appOwner == null || appOwner.isEmpty() || logLimits == null) {
            return;
        }
        final ApplicationId applicationId = containerId.getApplicationAttemptId().getApplicationId();
        String logEntity = this.$("entity.string");
        if (logEntity == null || logEntity.isEmpty()) {
            logEntity = containerId.toString();
        }
        if (!this.conf.getBoolean("yarn.log-aggregation-enable", false)) {
            html.h1()._("Aggregation is not enabled. Try the nodemanager at " + nodeId)._();
            return;
        }
        final Path remoteRootLogDir = new Path(this.conf.get("yarn.nodemanager.remote-app-log-dir", "/tmp/logs"));
        final Path remoteAppDir = LogAggregationUtils.getRemoteAppLogDir(remoteRootLogDir, applicationId, appOwner, LogAggregationUtils.getRemoteNodeLogDirSuffix(this.conf));
        RemoteIterator<FileStatus> nodeFiles;
        try {
            final Path qualifiedLogDir = FileContext.getFileContext(this.conf).makeQualified(remoteAppDir);
            nodeFiles = FileContext.getFileContext(qualifiedLogDir.toUri(), this.conf).listStatus(remoteAppDir);
        }
        catch (FileNotFoundException fnf) {
            html.h1()._("Logs not available for " + logEntity + ". Aggregation may not be complete, " + "Check back later or try the nodemanager at " + nodeId)._();
            return;
        }
        catch (Exception ex2) {
            html.h1()._("Error getting logs at " + nodeId)._();
            return;
        }
        boolean foundLog = false;
        final String desiredLogType = this.$("log.type");
        try {
            while (nodeFiles.hasNext()) {
                AggregatedLogFormat.LogReader reader = null;
                try {
                    final FileStatus thisNodeFile = nodeFiles.next();
                    if (!thisNodeFile.getPath().getName().contains(LogAggregationUtils.getNodeString(nodeId)) || thisNodeFile.getPath().getName().endsWith(".tmp")) {
                        continue;
                    }
                    final long logUploadedTime = thisNodeFile.getModificationTime();
                    reader = new AggregatedLogFormat.LogReader(this.conf, thisNodeFile.getPath());
                    String owner = null;
                    Map<ApplicationAccessType, String> appAcls = null;
                    try {
                        owner = reader.getApplicationOwner();
                        appAcls = reader.getApplicationAcls();
                    }
                    catch (IOException e) {
                        AggregatedLogsBlock.LOG.error("Error getting logs for " + logEntity, e);
                        continue;
                    }
                    final ApplicationACLsManager aclsManager = new ApplicationACLsManager(this.conf);
                    aclsManager.addApplication(applicationId, appAcls);
                    final String remoteUser = this.request().getRemoteUser();
                    UserGroupInformation callerUGI = null;
                    if (remoteUser != null) {
                        callerUGI = UserGroupInformation.createRemoteUser(remoteUser);
                    }
                    if (callerUGI != null && !aclsManager.checkAccess(callerUGI, ApplicationAccessType.VIEW_APP, owner, applicationId)) {
                        html.h1()._("User [" + remoteUser + "] is not authorized to view the logs for " + logEntity + " in log file [" + thisNodeFile.getPath().getName() + "]")._();
                        AggregatedLogsBlock.LOG.error("User [" + remoteUser + "] is not authorized to view the logs for " + logEntity);
                    }
                    else {
                        final AggregatedLogFormat.ContainerLogsReader logReader = reader.getContainerLogsReader(containerId);
                        if (logReader == null) {
                            continue;
                        }
                        foundLog = this.readContainerLogs(html, logReader, logLimits, desiredLogType, logUploadedTime);
                    }
                }
                catch (IOException ex) {
                    AggregatedLogsBlock.LOG.error("Error getting logs for " + logEntity, ex);
                }
                finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
            if (!foundLog) {
                if (desiredLogType.isEmpty()) {
                    html.h1("No logs available for container " + containerId.toString());
                }
                else {
                    html.h1("Unable to locate '" + desiredLogType + "' log for container " + containerId.toString());
                }
            }
        }
        catch (IOException e2) {
            html.h1()._("Error getting logs for " + logEntity)._();
            AggregatedLogsBlock.LOG.error("Error getting logs for " + logEntity, e2);
        }
    }
    
    private boolean readContainerLogs(final Block html, final AggregatedLogFormat.ContainerLogsReader logReader, final LogLimits logLimits, final String desiredLogType, final long logUpLoadTime) throws IOException {
        final int bufferSize = 65536;
        final char[] cbuf = new char[bufferSize];
        boolean foundLog = false;
        for (String logType = logReader.nextLog(); logType != null; logType = logReader.nextLog()) {
            if (desiredLogType == null || desiredLogType.isEmpty() || desiredLogType.equals(logType)) {
                final long logLength = logReader.getCurrentLogLength();
                if (foundLog) {
                    html.pre()._("\n\n")._();
                }
                html.p()._("Log Type: " + logType)._();
                html.p()._("Log Upload Time: " + Times.format(logUpLoadTime))._();
                html.p()._("Log Length: " + Long.toString(logLength))._();
                long start = (logLimits.start < 0L) ? (logLength + logLimits.start) : logLimits.start;
                start = ((start < 0L) ? 0L : start);
                start = ((start > logLength) ? logLength : start);
                long end = (logLimits.end < 0L) ? (logLength + logLimits.end) : logLimits.end;
                end = ((end < 0L) ? 0L : end);
                end = ((end > logLength) ? logLength : end);
                end = ((end < start) ? start : end);
                long toRead = end - start;
                if (toRead < logLength) {
                    html.p()._("Showing " + toRead + " bytes of " + logLength + " total. Click ").a(this.url("logs", this.$("nm.id"), this.$("container.id"), this.$("entity.string"), this.$("app.owner"), logType, "?start=0"), "here")._(" for the full log.")._();
                }
                long ret;
                for (long totalSkipped = 0L; totalSkipped < start; totalSkipped += ret) {
                    ret = logReader.skip(start - totalSkipped);
                    if (ret < 0L) {
                        throw new IOException("Premature EOF from container log");
                    }
                }
                int len = 0;
                int currentToRead = (toRead > bufferSize) ? bufferSize : ((int)toRead);
                final Hamlet.PRE<Hamlet> pre = html.pre();
                while (toRead > 0L && (len = logReader.read(cbuf, 0, currentToRead)) > 0) {
                    pre._(new String(cbuf, 0, len));
                    toRead -= len;
                    currentToRead = ((toRead > bufferSize) ? bufferSize : ((int)toRead));
                }
                pre._();
                foundLog = true;
            }
        }
        return foundLog;
    }
    
    private ContainerId verifyAndGetContainerId(final Block html) {
        final String containerIdStr = this.$("container.id");
        if (containerIdStr == null || containerIdStr.isEmpty()) {
            html.h1()._("Cannot get container logs without a ContainerId")._();
            return null;
        }
        ContainerId containerId = null;
        try {
            containerId = ConverterUtils.toContainerId(containerIdStr);
        }
        catch (IllegalArgumentException e) {
            html.h1()._("Cannot get container logs for invalid containerId: " + containerIdStr)._();
            return null;
        }
        return containerId;
    }
    
    private NodeId verifyAndGetNodeId(final Block html) {
        final String nodeIdStr = this.$("nm.id");
        if (nodeIdStr == null || nodeIdStr.isEmpty()) {
            html.h1()._("Cannot get container logs without a NodeId")._();
            return null;
        }
        NodeId nodeId = null;
        try {
            nodeId = ConverterUtils.toNodeId(nodeIdStr);
        }
        catch (IllegalArgumentException e) {
            html.h1()._("Cannot get container logs. Invalid nodeId: " + nodeIdStr)._();
            return null;
        }
        return nodeId;
    }
    
    private String verifyAndGetAppOwner(final Block html) {
        final String appOwner = this.$("app.owner");
        if (appOwner == null || appOwner.isEmpty()) {
            html.h1()._("Cannot get container logs without an app owner")._();
        }
        return appOwner;
    }
    
    private LogLimits verifyAndGetLogLimits(final Block html) {
        long start = -4096L;
        long end = Long.MAX_VALUE;
        boolean isValid = true;
        final String startStr = this.$("start");
        if (startStr != null && !startStr.isEmpty()) {
            try {
                start = Long.parseLong(startStr);
            }
            catch (NumberFormatException e) {
                isValid = false;
                html.h1()._("Invalid log start value: " + startStr)._();
            }
        }
        final String endStr = this.$("end");
        if (endStr != null && !endStr.isEmpty()) {
            try {
                end = Long.parseLong(endStr);
            }
            catch (NumberFormatException e2) {
                isValid = false;
                html.h1()._("Invalid log end value: " + endStr)._();
            }
        }
        if (!isValid) {
            return null;
        }
        final LogLimits limits = new LogLimits();
        limits.start = start;
        limits.end = end;
        return limits;
    }
    
    private static class LogLimits
    {
        long start;
        long end;
    }
}
