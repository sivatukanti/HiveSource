// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class AdminACLsManager
{
    static Log LOG;
    private final UserGroupInformation owner;
    private final AccessControlList adminAcl;
    private final boolean aclsEnabled;
    
    public AdminACLsManager(final Configuration conf) {
        this.adminAcl = new AccessControlList(conf.get("yarn.admin.acl", "*"));
        try {
            this.owner = UserGroupInformation.getCurrentUser();
            this.adminAcl.addUser(this.owner.getShortUserName());
        }
        catch (IOException e) {
            AdminACLsManager.LOG.warn("Could not add current user to admin:" + e);
            throw new YarnRuntimeException(e);
        }
        this.aclsEnabled = conf.getBoolean("yarn.acl.enable", false);
    }
    
    public UserGroupInformation getOwner() {
        return this.owner;
    }
    
    public boolean areACLsEnabled() {
        return this.aclsEnabled;
    }
    
    public AccessControlList getAdminAcl() {
        return this.adminAcl;
    }
    
    public boolean isAdmin(final UserGroupInformation callerUGI) {
        return this.adminAcl.isUserAllowed(callerUGI);
    }
    
    public boolean checkAccess(final UserGroupInformation callerUGI) {
        return !this.areACLsEnabled() || this.isAdmin(callerUGI);
    }
    
    static {
        AdminACLsManager.LOG = LogFactory.getLog(AdminACLsManager.class);
    }
}
