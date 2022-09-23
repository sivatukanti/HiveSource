// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.utils;

import org.apache.zookeeper.data.ACL;
import java.util.List;

public interface InternalACLProvider
{
    List<ACL> getDefaultAcl();
    
    List<ACL> getAclForPath(final String p0);
}
