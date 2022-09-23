// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authorize;

import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.MachineList;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
@InterfaceAudience.Public
public class DefaultImpersonationProvider implements ImpersonationProvider
{
    private static final String CONF_HOSTS = ".hosts";
    private static final String CONF_USERS = ".users";
    private static final String CONF_GROUPS = ".groups";
    private Map<String, AccessControlList> proxyUserAcl;
    private Map<String, MachineList> proxyHosts;
    private Configuration conf;
    private static DefaultImpersonationProvider testProvider;
    private String configPrefix;
    
    public DefaultImpersonationProvider() {
        this.proxyUserAcl = new HashMap<String, AccessControlList>();
        this.proxyHosts = new HashMap<String, MachineList>();
    }
    
    public static synchronized DefaultImpersonationProvider getTestProvider() {
        if (DefaultImpersonationProvider.testProvider == null) {
            (DefaultImpersonationProvider.testProvider = new DefaultImpersonationProvider()).setConf(new Configuration());
            DefaultImpersonationProvider.testProvider.init("hadoop.proxyuser");
        }
        return DefaultImpersonationProvider.testProvider;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public void init(final String configurationPrefix) {
        this.configPrefix = configurationPrefix + (configurationPrefix.endsWith(".") ? "" : ".");
        final String prefixRegEx = this.configPrefix.replace(".", "\\.");
        final String usersGroupsRegEx = prefixRegEx + "[\\S]*(" + Pattern.quote(".users") + "|" + Pattern.quote(".groups") + ")";
        final String hostsRegEx = prefixRegEx + "[\\S]*" + Pattern.quote(".hosts");
        Map<String, String> allMatchKeys = this.conf.getValByRegex(usersGroupsRegEx);
        for (final Map.Entry<String, String> entry : allMatchKeys.entrySet()) {
            final String aclKey = this.getAclKey(entry.getKey());
            if (!this.proxyUserAcl.containsKey(aclKey)) {
                this.proxyUserAcl.put(aclKey, new AccessControlList(allMatchKeys.get(aclKey + ".users"), allMatchKeys.get(aclKey + ".groups")));
            }
        }
        allMatchKeys = this.conf.getValByRegex(hostsRegEx);
        for (final Map.Entry<String, String> entry : allMatchKeys.entrySet()) {
            this.proxyHosts.put(entry.getKey(), new MachineList(entry.getValue()));
        }
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public void authorize(final UserGroupInformation user, final String remoteAddress) throws AuthorizationException {
        if (user == null) {
            throw new IllegalArgumentException("user is null.");
        }
        final UserGroupInformation realUser = user.getRealUser();
        if (realUser == null) {
            return;
        }
        final AccessControlList acl = this.proxyUserAcl.get(this.configPrefix + realUser.getShortUserName());
        if (acl == null || !acl.isUserAllowed(user)) {
            throw new AuthorizationException("User: " + realUser.getUserName() + " is not allowed to impersonate " + user.getUserName());
        }
        final MachineList MachineList = this.proxyHosts.get(this.getProxySuperuserIpConfKey(realUser.getShortUserName()));
        if (MachineList == null || !MachineList.includes(remoteAddress)) {
            throw new AuthorizationException("Unauthorized connection for super-user: " + realUser.getUserName() + " from IP " + remoteAddress);
        }
    }
    
    private String getAclKey(final String key) {
        final int endIndex = key.lastIndexOf(".");
        if (endIndex != -1) {
            return key.substring(0, endIndex);
        }
        return key;
    }
    
    public String getProxySuperuserUserConfKey(final String userName) {
        return this.configPrefix + userName + ".users";
    }
    
    public String getProxySuperuserGroupConfKey(final String userName) {
        return this.configPrefix + userName + ".groups";
    }
    
    public String getProxySuperuserIpConfKey(final String userName) {
        return this.configPrefix + userName + ".hosts";
    }
    
    @VisibleForTesting
    public Map<String, Collection<String>> getProxyGroups() {
        final Map<String, Collection<String>> proxyGroups = new HashMap<String, Collection<String>>();
        for (final Map.Entry<String, AccessControlList> entry : this.proxyUserAcl.entrySet()) {
            proxyGroups.put(entry.getKey() + ".groups", entry.getValue().getGroups());
        }
        return proxyGroups;
    }
    
    @VisibleForTesting
    public Map<String, Collection<String>> getProxyHosts() {
        final Map<String, Collection<String>> tmpProxyHosts = new HashMap<String, Collection<String>>();
        for (final Map.Entry<String, MachineList> proxyHostEntry : this.proxyHosts.entrySet()) {
            tmpProxyHosts.put(proxyHostEntry.getKey(), proxyHostEntry.getValue().getCollection());
        }
        return tmpProxyHosts;
    }
}
