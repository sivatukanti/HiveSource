// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.logaggregation;

import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class LogAggregationUtils
{
    public static final String TMP_FILE_SUFFIX = ".tmp";
    
    public static Path getRemoteNodeLogFileForApp(final Path remoteRootLogDir, final ApplicationId appId, final String user, final NodeId nodeId, final String suffix) {
        return new Path(getRemoteAppLogDir(remoteRootLogDir, appId, user, suffix), getNodeString(nodeId));
    }
    
    public static Path getRemoteAppLogDir(final Path remoteRootLogDir, final ApplicationId appId, final String user, final String suffix) {
        return new Path(getRemoteLogSuffixedDir(remoteRootLogDir, user, suffix), appId.toString());
    }
    
    public static Path getRemoteLogSuffixedDir(final Path remoteRootLogDir, final String user, final String suffix) {
        if (suffix == null || suffix.isEmpty()) {
            return getRemoteLogUserDir(remoteRootLogDir, user);
        }
        return new Path(getRemoteLogUserDir(remoteRootLogDir, user), suffix);
    }
    
    public static Path getRemoteLogUserDir(final Path remoteRootLogDir, final String user) {
        return new Path(remoteRootLogDir, user);
    }
    
    public static String getRemoteNodeLogDirSuffix(final Configuration conf) {
        return conf.get("yarn.nodemanager.remote-app-log-dir-suffix", "logs");
    }
    
    @VisibleForTesting
    public static String getNodeString(final NodeId nodeId) {
        return nodeId.toString().replace(":", "_");
    }
    
    @VisibleForTesting
    public static String getNodeString(final String nodeId) {
        return nodeId.toString().replace(":", "_");
    }
}
