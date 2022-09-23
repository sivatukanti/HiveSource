// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline.security;

import org.apache.commons.logging.LogFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.timeline.EntityIdentifier;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import java.util.HashMap;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import java.util.Collections;
import org.apache.commons.collections.map.LRUMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.timeline.TimelineStore;
import java.util.Map;
import org.apache.hadoop.yarn.security.AdminACLsManager;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class TimelineACLsManager
{
    private static final Log LOG;
    private static final int DOMAIN_ACCESS_ENTRY_CACHE_SIZE = 100;
    private AdminACLsManager adminAclsManager;
    private Map<String, AccessControlListExt> aclExts;
    private TimelineStore store;
    
    public TimelineACLsManager(final Configuration conf) {
        this.adminAclsManager = new AdminACLsManager(conf);
        this.aclExts = Collections.synchronizedMap((Map<String, AccessControlListExt>)new LRUMap(100));
    }
    
    public void setTimelineStore(final TimelineStore store) {
        this.store = store;
    }
    
    private AccessControlListExt loadDomainFromTimelineStore(final String domainId) throws IOException {
        if (this.store == null) {
            return null;
        }
        final TimelineDomain domain = this.store.getDomain(domainId);
        if (domain == null) {
            return null;
        }
        return this.putDomainIntoCache(domain);
    }
    
    public void replaceIfExist(final TimelineDomain domain) {
        if (this.aclExts.containsKey(domain.getId())) {
            this.putDomainIntoCache(domain);
        }
    }
    
    private AccessControlListExt putDomainIntoCache(final TimelineDomain domain) {
        final Map<ApplicationAccessType, AccessControlList> acls = new HashMap<ApplicationAccessType, AccessControlList>(2);
        acls.put(ApplicationAccessType.VIEW_APP, new AccessControlList(StringHelper.cjoin(domain.getReaders())));
        acls.put(ApplicationAccessType.MODIFY_APP, new AccessControlList(StringHelper.cjoin(domain.getWriters())));
        final AccessControlListExt aclExt = new AccessControlListExt(domain.getOwner(), acls);
        this.aclExts.put(domain.getId(), aclExt);
        return aclExt;
    }
    
    public boolean checkAccess(final UserGroupInformation callerUGI, final ApplicationAccessType applicationAccessType, final TimelineEntity entity) throws YarnException, IOException {
        if (TimelineACLsManager.LOG.isDebugEnabled()) {
            TimelineACLsManager.LOG.debug("Verifying the access of " + ((callerUGI == null) ? null : callerUGI.getShortUserName()) + " on the timeline entity " + new EntityIdentifier(entity.getEntityId(), entity.getEntityType()));
        }
        if (!this.adminAclsManager.areACLsEnabled()) {
            return true;
        }
        AccessControlListExt aclExt = this.aclExts.get(entity.getDomainId());
        if (aclExt == null) {
            aclExt = this.loadDomainFromTimelineStore(entity.getDomainId());
        }
        if (aclExt == null) {
            throw new YarnException("Domain information of the timeline entity " + new EntityIdentifier(entity.getEntityId(), entity.getEntityType()) + " doesn't exist.");
        }
        final String owner = aclExt.owner;
        AccessControlList domainACL = aclExt.acls.get(applicationAccessType);
        if (domainACL == null) {
            if (TimelineACLsManager.LOG.isDebugEnabled()) {
                TimelineACLsManager.LOG.debug("ACL not found for access-type " + applicationAccessType + " for domain " + entity.getDomainId() + " owned by " + owner + ". Using default [" + " " + "]");
            }
            domainACL = new AccessControlList(" ");
        }
        return callerUGI != null && (this.adminAclsManager.isAdmin(callerUGI) || callerUGI.getShortUserName().equals(owner) || domainACL.isUserAllowed(callerUGI));
    }
    
    public boolean checkAccess(final UserGroupInformation callerUGI, final TimelineDomain domain) throws YarnException, IOException {
        if (TimelineACLsManager.LOG.isDebugEnabled()) {
            TimelineACLsManager.LOG.debug("Verifying the access of " + ((callerUGI == null) ? null : callerUGI.getShortUserName()) + " on the timeline domain " + domain);
        }
        if (!this.adminAclsManager.areACLsEnabled()) {
            return true;
        }
        final String owner = domain.getOwner();
        if (owner == null || owner.length() == 0) {
            throw new YarnException("Owner information of the timeline domain " + domain.getId() + " is corrupted.");
        }
        return callerUGI != null && (this.adminAclsManager.isAdmin(callerUGI) || callerUGI.getShortUserName().equals(owner));
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public AdminACLsManager setAdminACLsManager(final AdminACLsManager adminAclsManager) {
        final AdminACLsManager oldAdminACLsManager = this.adminAclsManager;
        this.adminAclsManager = adminAclsManager;
        return oldAdminACLsManager;
    }
    
    static {
        LOG = LogFactory.getLog(TimelineACLsManager.class);
    }
    
    private static class AccessControlListExt
    {
        private String owner;
        private Map<ApplicationAccessType, AccessControlList> acls;
        
        public AccessControlListExt(final String owner, final Map<ApplicationAccessType, AccessControlList> acls) {
            this.owner = owner;
            this.acls = acls;
        }
    }
}
