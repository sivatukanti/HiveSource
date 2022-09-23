// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.io.IOException;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface GroupMappingServiceProvider
{
    public static final String GROUP_MAPPING_CONFIG_PREFIX = "hadoop.security.group.mapping";
    
    List<String> getGroups(final String p0) throws IOException;
    
    void cacheGroupsRefresh() throws IOException;
    
    void cacheGroupsAdd(final List<String> p0) throws IOException;
}
