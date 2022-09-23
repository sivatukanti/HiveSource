// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import java.util.Collection;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.zookeeper.data.ACL;
import java.util.List;

class ACLing
{
    private final List<ACL> aclList;
    private final ACLProvider aclProvider;
    
    ACLing(final ACLProvider aclProvider) {
        this(aclProvider, null);
    }
    
    ACLing(final ACLProvider aclProvider, final List<ACL> aclList) {
        this.aclProvider = aclProvider;
        this.aclList = (List<ACL>)((aclList != null) ? ImmutableList.copyOf((Collection<?>)aclList) : null);
    }
    
    List<ACL> getAclList(final String path) {
        List<ACL> localAclList = this.aclList;
        if (localAclList == null) {
            if (path != null) {
                localAclList = this.aclProvider.getAclForPath(path);
                if (localAclList != null) {
                    return localAclList;
                }
            }
            localAclList = this.aclProvider.getDefaultAcl();
        }
        return localAclList;
    }
}
