// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.commons.logging.LogFactory;
import java.util.Collections;
import org.apache.hadoop.util.ZKUtil;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class RMZKUtils
{
    private static final Log LOG;
    
    public static List<ACL> getZKAcls(final Configuration conf) throws Exception {
        String zkAclConf = conf.get("yarn.resourcemanager.zk-acl", "world:anyone:rwcda");
        try {
            zkAclConf = ZKUtil.resolveConfIndirection(zkAclConf);
            return ZKUtil.parseACLs(zkAclConf);
        }
        catch (Exception e) {
            RMZKUtils.LOG.error("Couldn't read ACLs based on yarn.resourcemanager.zk-acl");
            throw e;
        }
    }
    
    public static List<ZKUtil.ZKAuthInfo> getZKAuths(final Configuration conf) throws Exception {
        String zkAuthConf = conf.get("yarn.resourcemanager.zk-auth");
        try {
            zkAuthConf = ZKUtil.resolveConfIndirection(zkAuthConf);
            if (zkAuthConf != null) {
                return ZKUtil.parseAuth(zkAuthConf);
            }
            return Collections.emptyList();
        }
        catch (Exception e) {
            RMZKUtils.LOG.error("Couldn't read Auth based on yarn.resourcemanager.zk-auth");
            throw e;
        }
    }
    
    static {
        LOG = LogFactory.getLog(RMZKUtils.class);
    }
}
