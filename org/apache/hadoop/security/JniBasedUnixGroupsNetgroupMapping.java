// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Iterator;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class JniBasedUnixGroupsNetgroupMapping extends JniBasedUnixGroupsMapping
{
    private static final Logger LOG;
    
    native String[] getUsersForNetgroupJNI(final String p0);
    
    @Override
    public List<String> getGroups(final String user) throws IOException {
        final List<String> groups = new LinkedList<String>(super.getGroups(user));
        NetgroupCache.getNetgroups(user, groups);
        return groups;
    }
    
    @Override
    public void cacheGroupsRefresh() throws IOException {
        final List<String> groups = NetgroupCache.getNetgroupNames();
        NetgroupCache.clear();
        this.cacheGroupsAdd(groups);
    }
    
    @Override
    public void cacheGroupsAdd(final List<String> groups) throws IOException {
        for (final String group : groups) {
            if (group.length() == 0) {
                continue;
            }
            if (group.charAt(0) != '@' || NetgroupCache.isCached(group)) {
                continue;
            }
            NetgroupCache.add(group, this.getUsersForNetgroup(group));
        }
    }
    
    protected synchronized List<String> getUsersForNetgroup(final String netgroup) {
        String[] users = null;
        try {
            users = this.getUsersForNetgroupJNI(netgroup.substring(1));
        }
        catch (Exception e) {
            if (JniBasedUnixGroupsNetgroupMapping.LOG.isDebugEnabled()) {
                JniBasedUnixGroupsNetgroupMapping.LOG.debug("Error getting users for netgroup " + netgroup, e);
            }
            else {
                JniBasedUnixGroupsNetgroupMapping.LOG.info("Error getting users for netgroup " + netgroup + ": " + e.getMessage());
            }
        }
        if (users != null && users.length != 0) {
            return Arrays.asList(users);
        }
        return new LinkedList<String>();
    }
    
    static {
        LOG = LoggerFactory.getLogger(JniBasedUnixGroupsNetgroupMapping.class);
        if (!NativeCodeLoader.isNativeCodeLoaded()) {
            throw new RuntimeException("Bailing out since native library couldn't be loaded");
        }
        JniBasedUnixGroupsNetgroupMapping.LOG.debug("Using JniBasedUnixGroupsNetgroupMapping for Netgroup resolution");
    }
}
