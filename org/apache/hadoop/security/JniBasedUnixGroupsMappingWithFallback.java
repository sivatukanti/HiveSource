// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.util.PerformanceAdvisory;
import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.Logger;

public class JniBasedUnixGroupsMappingWithFallback implements GroupMappingServiceProvider
{
    private static final Logger LOG;
    private GroupMappingServiceProvider impl;
    
    public JniBasedUnixGroupsMappingWithFallback() {
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            this.impl = new JniBasedUnixGroupsMapping();
        }
        else {
            PerformanceAdvisory.LOG.debug("Falling back to shell based");
            this.impl = new ShellBasedUnixGroupsMapping();
        }
        if (JniBasedUnixGroupsMappingWithFallback.LOG.isDebugEnabled()) {
            JniBasedUnixGroupsMappingWithFallback.LOG.debug("Group mapping impl=" + this.impl.getClass().getName());
        }
    }
    
    @Override
    public List<String> getGroups(final String user) throws IOException {
        return this.impl.getGroups(user);
    }
    
    @Override
    public void cacheGroupsRefresh() throws IOException {
        this.impl.cacheGroupsRefresh();
    }
    
    @Override
    public void cacheGroupsAdd(final List<String> groups) throws IOException {
        this.impl.cacheGroupsAdd(groups);
    }
    
    static {
        LOG = LoggerFactory.getLogger(JniBasedUnixGroupsMappingWithFallback.class);
    }
}
