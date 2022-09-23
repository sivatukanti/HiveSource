// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.util.Collections;
import java.util.List;

public class NullGroupsMapping implements GroupMappingServiceProvider
{
    @Override
    public void cacheGroupsAdd(final List<String> groups) {
    }
    
    @Override
    public List<String> getGroups(final String user) {
        return Collections.emptyList();
    }
    
    @Override
    public void cacheGroupsRefresh() {
    }
}
