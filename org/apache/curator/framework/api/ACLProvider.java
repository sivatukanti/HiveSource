// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.curator.utils.InternalACLProvider;

public interface ACLProvider extends InternalACLProvider
{
    List<ACL> getDefaultAcl();
    
    List<ACL> getAclForPath(final String p0);
}
