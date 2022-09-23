// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authorize;

import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.security.UserGroupInformation;
import com.google.common.base.Preconditions;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce", "HBase", "Hive" })
public class ProxyUsers
{
    public static final String CONF_HADOOP_PROXYUSER = "hadoop.proxyuser";
    private static volatile ImpersonationProvider sip;
    
    private static ImpersonationProvider getInstance(final Configuration conf) {
        final Class<? extends ImpersonationProvider> clazz = conf.getClass("hadoop.security.impersonation.provider.class", DefaultImpersonationProvider.class, ImpersonationProvider.class);
        return ReflectionUtils.newInstance(clazz, conf);
    }
    
    public static void refreshSuperUserGroupsConfiguration() {
        refreshSuperUserGroupsConfiguration(new Configuration());
    }
    
    public static void refreshSuperUserGroupsConfiguration(final Configuration conf, final String proxyUserPrefix) {
        Preconditions.checkArgument(proxyUserPrefix != null && !proxyUserPrefix.isEmpty(), (Object)"prefix cannot be NULL or empty");
        final ImpersonationProvider ip = getInstance(conf);
        ip.init(proxyUserPrefix);
        ProxyUsers.sip = ip;
        ProxyServers.refresh(conf);
    }
    
    public static void refreshSuperUserGroupsConfiguration(final Configuration conf) {
        refreshSuperUserGroupsConfiguration(conf, "hadoop.proxyuser");
    }
    
    public static void authorize(final UserGroupInformation user, final String remoteAddress) throws AuthorizationException {
        if (ProxyUsers.sip == null) {
            refreshSuperUserGroupsConfiguration();
        }
        ProxyUsers.sip.authorize(user, remoteAddress);
    }
    
    @Deprecated
    public static void authorize(final UserGroupInformation user, final String remoteAddress, final Configuration conf) throws AuthorizationException {
        authorize(user, remoteAddress);
    }
    
    @VisibleForTesting
    public static DefaultImpersonationProvider getDefaultImpersonationProvider() {
        return (DefaultImpersonationProvider)ProxyUsers.sip;
    }
}
