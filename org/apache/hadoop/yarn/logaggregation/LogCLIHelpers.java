// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.logaggregation;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.PrintStream;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.FileStatus;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configurable;

public class LogCLIHelpers implements Configurable
{
    private Configuration conf;
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public int dumpAContainersLogs(final String appId, final String containerId, final String nodeId, final String jobOwner) throws IOException {
        final Path remoteRootLogDir = new Path(this.getConf().get("yarn.nodemanager.remote-app-log-dir", "/tmp/logs"));
        final String suffix = LogAggregationUtils.getRemoteNodeLogDirSuffix(this.getConf());
        final Path remoteAppLogDir = LogAggregationUtils.getRemoteAppLogDir(remoteRootLogDir, ConverterUtils.toApplicationId(appId), jobOwner, suffix);
        RemoteIterator<FileStatus> nodeFiles;
        try {
            final Path qualifiedLogDir = FileContext.getFileContext(this.getConf()).makeQualified(remoteAppLogDir);
            nodeFiles = FileContext.getFileContext(qualifiedLogDir.toUri(), this.getConf()).listStatus(remoteAppLogDir);
        }
        catch (FileNotFoundException fnf) {
            logDirNotExist(remoteAppLogDir.toString());
            return -1;
        }
        boolean foundContainerLogs = false;
        while (nodeFiles.hasNext()) {
            final FileStatus thisNodeFile = nodeFiles.next();
            final String fileName = thisNodeFile.getPath().getName();
            if (fileName.contains(LogAggregationUtils.getNodeString(nodeId)) && !fileName.endsWith(".tmp")) {
                AggregatedLogFormat.LogReader reader = null;
                try {
                    reader = new AggregatedLogFormat.LogReader(this.getConf(), thisNodeFile.getPath());
                    if (this.dumpAContainerLogs(containerId, reader, System.out, thisNodeFile.getModificationTime()) <= -1) {
                        continue;
                    }
                    foundContainerLogs = true;
                }
                finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
        }
        if (!foundContainerLogs) {
            containerLogNotFound(containerId);
            return -1;
        }
        return 0;
    }
    
    @InterfaceAudience.Private
    public int dumpAContainerLogs(final String containerIdStr, final AggregatedLogFormat.LogReader reader, final PrintStream out, final long logUploadedTime) throws IOException {
        AggregatedLogFormat.LogKey key;
        DataInputStream valueStream;
        for (key = new AggregatedLogFormat.LogKey(), valueStream = reader.next(key); valueStream != null && !key.toString().equals(containerIdStr); key = new AggregatedLogFormat.LogKey(), valueStream = reader.next(key)) {}
        if (valueStream == null) {
            return -1;
        }
        boolean foundContainerLogs = false;
        try {
            while (true) {
                AggregatedLogFormat.LogReader.readAContainerLogsForALogType(valueStream, out, logUploadedTime);
                foundContainerLogs = true;
            }
        }
        catch (EOFException eof) {
            if (foundContainerLogs) {
                return 0;
            }
            return -1;
        }
    }
    
    @InterfaceAudience.Private
    public int dumpAllContainersLogs(final ApplicationId appId, final String appOwner, final PrintStream out) throws IOException {
        final Path remoteRootLogDir = new Path(this.getConf().get("yarn.nodemanager.remote-app-log-dir", "/tmp/logs"));
        final String user = appOwner;
        final String logDirSuffix = LogAggregationUtils.getRemoteNodeLogDirSuffix(this.getConf());
        final Path remoteAppLogDir = LogAggregationUtils.getRemoteAppLogDir(remoteRootLogDir, appId, user, logDirSuffix);
        RemoteIterator<FileStatus> nodeFiles;
        try {
            final Path qualifiedLogDir = FileContext.getFileContext(this.getConf()).makeQualified(remoteAppLogDir);
            nodeFiles = FileContext.getFileContext(qualifiedLogDir.toUri(), this.getConf()).listStatus(remoteAppLogDir);
        }
        catch (FileNotFoundException fnf) {
            logDirNotExist(remoteAppLogDir.toString());
            return -1;
        }
        boolean foundAnyLogs = false;
        while (nodeFiles.hasNext()) {
            final FileStatus thisNodeFile = nodeFiles.next();
            if (!thisNodeFile.getPath().getName().endsWith(".tmp")) {
                final AggregatedLogFormat.LogReader reader = new AggregatedLogFormat.LogReader(this.getConf(), thisNodeFile.getPath());
                try {
                    AggregatedLogFormat.LogKey key = new AggregatedLogFormat.LogKey();
                    DataInputStream valueStream = reader.next(key);
                    while (valueStream != null) {
                        final String containerString = "\n\nContainer: " + key + " on " + thisNodeFile.getPath().getName();
                        out.println(containerString);
                        out.println(StringUtils.repeat("=", containerString.length()));
                        try {
                            while (true) {
                                AggregatedLogFormat.LogReader.readAContainerLogsForALogType(valueStream, out, thisNodeFile.getModificationTime());
                                foundAnyLogs = true;
                            }
                        }
                        catch (EOFException eof) {
                            key = new AggregatedLogFormat.LogKey();
                            valueStream = reader.next(key);
                            continue;
                        }
                        break;
                    }
                }
                finally {
                    reader.close();
                }
            }
        }
        if (!foundAnyLogs) {
            emptyLogDir(remoteAppLogDir.toString());
            return -1;
        }
        return 0;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    private static void containerLogNotFound(final String containerId) {
        System.out.println("Logs for container " + containerId + " are not present in this log-file.");
    }
    
    private static void logDirNotExist(final String remoteAppLogDir) {
        System.out.println(remoteAppLogDir + "does not exist.");
        System.out.println("Log aggregation has not completed or is not enabled.");
    }
    
    private static void emptyLogDir(final String remoteAppLogDir) {
        System.out.println(remoteAppLogDir + "does not have any log files.");
    }
}
