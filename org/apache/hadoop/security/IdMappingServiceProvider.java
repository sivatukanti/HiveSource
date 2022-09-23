// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface IdMappingServiceProvider
{
    int getUid(final String p0) throws IOException;
    
    int getGid(final String p0) throws IOException;
    
    String getUserName(final int p0, final String p1);
    
    String getGroupName(final int p0, final String p1);
    
    int getUidAllowingUnknown(final String p0);
    
    int getGidAllowingUnknown(final String p0);
}
