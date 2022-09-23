// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class JniBasedUnixGroupsMapping implements GroupMappingServiceProvider
{
    private static final Logger LOG;
    
    static native void anchorNative();
    
    static native String[] getGroupsForUser(final String p0);
    
    private static void logError(final int groupId, final String error) {
        JniBasedUnixGroupsMapping.LOG.error("error looking up the name of group " + groupId + ": " + error);
    }
    
    @Override
    public List<String> getGroups(final String user) throws IOException {
        String[] groups = new String[0];
        try {
            groups = getGroupsForUser(user);
        }
        catch (Exception e) {
            if (JniBasedUnixGroupsMapping.LOG.isDebugEnabled()) {
                JniBasedUnixGroupsMapping.LOG.debug("Error getting groups for " + user, e);
            }
            else {
                JniBasedUnixGroupsMapping.LOG.info("Error getting groups for " + user + ": " + e.getMessage());
            }
        }
        return Arrays.asList(groups);
    }
    
    @Override
    public void cacheGroupsRefresh() throws IOException {
    }
    
    @Override
    public void cacheGroupsAdd(final List<String> groups) throws IOException {
    }
    
    static {
        LOG = LoggerFactory.getLogger(JniBasedUnixGroupsMapping.class);
        if (!NativeCodeLoader.isNativeCodeLoaded()) {
            throw new RuntimeException("Bailing out since native library couldn't be loaded");
        }
        anchorNative();
        JniBasedUnixGroupsMapping.LOG.debug("Using JniBasedUnixGroupsMapping for Group resolution");
    }
}
