// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.curator.framework.api.ACLProvider;

public class DefaultACLProvider implements ACLProvider
{
    @Override
    public List<ACL> getDefaultAcl() {
        return ZooDefs.Ids.OPEN_ACL_UNSAFE;
    }
    
    @Override
    public List<ACL> getAclForPath(final String path) {
        return ZooDefs.Ids.OPEN_ACL_UNSAFE;
    }
}
