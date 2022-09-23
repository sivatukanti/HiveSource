// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authorize;

import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import java.util.Set;
import org.apache.hadoop.security.KerberosInfo;
import java.io.IOException;
import org.apache.hadoop.security.SecurityUtil;
import java.net.InetAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.IdentityHashMap;
import org.slf4j.Logger;
import org.apache.hadoop.util.MachineList;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class ServiceAuthorizationManager
{
    static final String BLOCKED = ".blocked";
    static final String HOSTS = ".hosts";
    private static final String HADOOP_POLICY_FILE = "hadoop-policy.xml";
    private volatile Map<Class<?>, AccessControlList[]> protocolToAcls;
    private volatile Map<Class<?>, MachineList[]> protocolToMachineLists;
    @Deprecated
    public static final String SERVICE_AUTHORIZATION_CONFIG = "hadoop.security.authorization";
    public static final Logger AUDITLOG;
    private static final String AUTHZ_SUCCESSFUL_FOR = "Authorization successful for ";
    private static final String AUTHZ_FAILED_FOR = "Authorization failed for ";
    
    public ServiceAuthorizationManager() {
        this.protocolToAcls = new IdentityHashMap<Class<?>, AccessControlList[]>();
        this.protocolToMachineLists = new IdentityHashMap<Class<?>, MachineList[]>();
    }
    
    public void authorize(final UserGroupInformation user, final Class<?> protocol, final Configuration conf, final InetAddress addr) throws AuthorizationException {
        final AccessControlList[] acls = this.protocolToAcls.get(protocol);
        final MachineList[] hosts = this.protocolToMachineLists.get(protocol);
        if (acls == null || hosts == null) {
            throw new AuthorizationException("Protocol " + protocol + " is not known.");
        }
        final KerberosInfo krbInfo = SecurityUtil.getKerberosInfo(protocol, conf);
        String clientPrincipal = null;
        if (krbInfo != null) {
            final String clientKey = krbInfo.clientPrincipal();
            if (clientKey != null && !clientKey.isEmpty()) {
                try {
                    clientPrincipal = SecurityUtil.getServerPrincipal(conf.get(clientKey), addr);
                }
                catch (IOException e) {
                    throw (AuthorizationException)new AuthorizationException("Can't figure out Kerberos principal name for connection from " + addr + " for user=" + user + " protocol=" + protocol).initCause(e);
                }
            }
        }
        if ((clientPrincipal != null && !clientPrincipal.equals(user.getUserName())) || acls.length != 2 || !acls[0].isUserAllowed(user) || acls[1].isUserAllowed(user)) {
            final String cause = (clientPrincipal != null) ? (": this service is only accessible by " + clientPrincipal) : ": denied by configured ACL";
            ServiceAuthorizationManager.AUDITLOG.warn("Authorization failed for " + user + " for protocol=" + protocol + cause);
            throw new AuthorizationException("User " + user + " is not authorized for protocol " + protocol + cause);
        }
        if (addr != null) {
            final String hostAddress = addr.getHostAddress();
            if (hosts.length != 2 || !hosts[0].includes(hostAddress) || hosts[1].includes(hostAddress)) {
                ServiceAuthorizationManager.AUDITLOG.warn("Authorization failed for  for protocol=" + protocol + " from host = " + hostAddress);
                throw new AuthorizationException("Host " + hostAddress + " is not authorized for protocol " + protocol);
            }
        }
        ServiceAuthorizationManager.AUDITLOG.info("Authorization successful for " + user + " for protocol=" + protocol);
    }
    
    public void refresh(final Configuration conf, final PolicyProvider provider) {
        final String policyFile = System.getProperty("hadoop.policy.file", "hadoop-policy.xml");
        final Configuration policyConf = new Configuration(conf);
        policyConf.addResource(policyFile);
        this.refreshWithLoadedConfiguration(policyConf, provider);
    }
    
    @InterfaceAudience.Private
    public void refreshWithLoadedConfiguration(final Configuration conf, final PolicyProvider provider) {
        final Map<Class<?>, AccessControlList[]> newAcls = new IdentityHashMap<Class<?>, AccessControlList[]>();
        final Map<Class<?>, MachineList[]> newMachineLists = new IdentityHashMap<Class<?>, MachineList[]>();
        final String defaultAcl = conf.get("security.service.authorization.default.acl", "*");
        final String defaultBlockedAcl = conf.get("security.service.authorization.default.acl.blocked", "");
        final String defaultServiceHostsKey = this.getHostKey("security.service.authorization.default.acl");
        final String defaultMachineList = conf.get(defaultServiceHostsKey, "*");
        final String defaultBlockedMachineList = conf.get(defaultServiceHostsKey + ".blocked", "");
        final Service[] services = provider.getServices();
        if (services != null) {
            for (final Service service : services) {
                final AccessControlList acl = new AccessControlList(conf.get(service.getServiceKey(), defaultAcl));
                final AccessControlList blockedAcl = new AccessControlList(conf.get(service.getServiceKey() + ".blocked", defaultBlockedAcl));
                newAcls.put(service.getProtocol(), new AccessControlList[] { acl, blockedAcl });
                final String serviceHostsKey = this.getHostKey(service.getServiceKey());
                final MachineList machineList = new MachineList(conf.get(serviceHostsKey, defaultMachineList));
                final MachineList blockedMachineList = new MachineList(conf.get(serviceHostsKey + ".blocked", defaultBlockedMachineList));
                newMachineLists.put(service.getProtocol(), new MachineList[] { machineList, blockedMachineList });
            }
        }
        this.protocolToAcls = newAcls;
        this.protocolToMachineLists = newMachineLists;
    }
    
    private String getHostKey(final String serviceKey) {
        final int endIndex = serviceKey.lastIndexOf(".");
        if (endIndex != -1) {
            return serviceKey.substring(0, endIndex) + ".hosts";
        }
        return serviceKey;
    }
    
    @VisibleForTesting
    public Set<Class<?>> getProtocolsWithAcls() {
        return this.protocolToAcls.keySet();
    }
    
    @VisibleForTesting
    public AccessControlList getProtocolsAcls(final Class<?> className) {
        return this.protocolToAcls.get(className)[0];
    }
    
    @VisibleForTesting
    public AccessControlList getProtocolsBlockedAcls(final Class<?> className) {
        return this.protocolToAcls.get(className)[1];
    }
    
    @VisibleForTesting
    public Set<Class<?>> getProtocolsWithMachineLists() {
        return this.protocolToMachineLists.keySet();
    }
    
    @VisibleForTesting
    public MachineList getProtocolsMachineList(final Class<?> className) {
        return this.protocolToMachineLists.get(className)[0];
    }
    
    @VisibleForTesting
    public MachineList getProtocolsBlockedMachineList(final Class<?> className) {
        return this.protocolToMachineLists.get(className)[1];
    }
    
    static {
        AUDITLOG = LoggerFactory.getLogger("SecurityLogger." + ServiceAuthorizationManager.class.getName());
    }
}
