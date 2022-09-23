// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.security;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.Iterator;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.yarn.security.AdminACLsManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ApplicationACLsManager
{
    private static final Log LOG;
    private static AccessControlList DEFAULT_YARN_APP_ACL;
    private final Configuration conf;
    private final AdminACLsManager adminAclsManager;
    private final ConcurrentMap<ApplicationId, Map<ApplicationAccessType, AccessControlList>> applicationACLS;
    
    public ApplicationACLsManager(final Configuration conf) {
        this.applicationACLS = new ConcurrentHashMap<ApplicationId, Map<ApplicationAccessType, AccessControlList>>();
        this.conf = conf;
        this.adminAclsManager = new AdminACLsManager(this.conf);
    }
    
    public boolean areACLsEnabled() {
        return this.adminAclsManager.areACLsEnabled();
    }
    
    public void addApplication(final ApplicationId appId, final Map<ApplicationAccessType, String> acls) {
        final Map<ApplicationAccessType, AccessControlList> finalMap = new HashMap<ApplicationAccessType, AccessControlList>(acls.size());
        for (final Map.Entry<ApplicationAccessType, String> acl : acls.entrySet()) {
            finalMap.put(acl.getKey(), new AccessControlList(acl.getValue()));
        }
        this.applicationACLS.put(appId, finalMap);
    }
    
    public void removeApplication(final ApplicationId appId) {
        this.applicationACLS.remove(appId);
    }
    
    public boolean checkAccess(final UserGroupInformation callerUGI, final ApplicationAccessType applicationAccessType, final String applicationOwner, final ApplicationId applicationId) {
        if (ApplicationACLsManager.LOG.isDebugEnabled()) {
            ApplicationACLsManager.LOG.debug("Verifying access-type " + applicationAccessType + " for " + callerUGI + " on application " + applicationId + " owned by " + applicationOwner);
        }
        final String user = callerUGI.getShortUserName();
        if (!this.areACLsEnabled()) {
            return true;
        }
        AccessControlList applicationACL = ApplicationACLsManager.DEFAULT_YARN_APP_ACL;
        final Map<ApplicationAccessType, AccessControlList> acls = this.applicationACLS.get(applicationId);
        if (acls == null) {
            if (ApplicationACLsManager.LOG.isDebugEnabled()) {
                ApplicationACLsManager.LOG.debug("ACL not found for application " + applicationId + " owned by " + applicationOwner + ". Using default [" + " " + "]");
            }
        }
        else {
            final AccessControlList applicationACLInMap = acls.get(applicationAccessType);
            if (applicationACLInMap != null) {
                applicationACL = applicationACLInMap;
            }
            else if (ApplicationACLsManager.LOG.isDebugEnabled()) {
                ApplicationACLsManager.LOG.debug("ACL not found for access-type " + applicationAccessType + " for application " + applicationId + " owned by " + applicationOwner + ". Using default [" + " " + "]");
            }
        }
        return this.adminAclsManager.isAdmin(callerUGI) || user.equals(applicationOwner) || applicationACL.isUserAllowed(callerUGI);
    }
    
    static {
        LOG = LogFactory.getLog(ApplicationACLsManager.class);
        ApplicationACLsManager.DEFAULT_YARN_APP_ACL = new AccessControlList(" ");
    }
}
