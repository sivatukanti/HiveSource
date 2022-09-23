// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline.security;

import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.security.client.TimelineDelegationTokenIdentifier;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.apache.hadoop.yarn.util.timeline.TimelineUtils;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class TimelineDelegationTokenSecretManagerService extends AbstractService
{
    private TimelineDelegationTokenSecretManager secretManager;
    private InetSocketAddress serviceAddr;
    
    public TimelineDelegationTokenSecretManagerService() {
        super(TimelineDelegationTokenSecretManagerService.class.getName());
        this.secretManager = null;
        this.serviceAddr = null;
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        final long secretKeyInterval = conf.getLong("yarn.resourcemanager.delegation.key.update-interval", 86400000L);
        final long tokenMaxLifetime = conf.getLong("yarn.resourcemanager.delegation.token.max-lifetime", 604800000L);
        final long tokenRenewInterval = conf.getLong("yarn.resourcemanager.delegation.token.renew-interval", 86400000L);
        (this.secretManager = new TimelineDelegationTokenSecretManager(secretKeyInterval, tokenMaxLifetime, tokenRenewInterval, 3600000L)).startThreads();
        this.serviceAddr = TimelineUtils.getTimelineTokenServiceAddress(this.getConfig());
        super.init(conf);
    }
    
    @Override
    protected void serviceStop() throws Exception {
        this.secretManager.stopThreads();
        super.stop();
    }
    
    public TimelineDelegationTokenSecretManager getTimelineDelegationTokenSecretManager() {
        return this.secretManager;
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static class TimelineDelegationTokenSecretManager extends AbstractDelegationTokenSecretManager<TimelineDelegationTokenIdentifier>
    {
        public TimelineDelegationTokenSecretManager(final long delegationKeyUpdateInterval, final long delegationTokenMaxLifetime, final long delegationTokenRenewInterval, final long delegationTokenRemoverScanInterval) {
            super(delegationKeyUpdateInterval, delegationTokenMaxLifetime, delegationTokenRenewInterval, delegationTokenRemoverScanInterval);
        }
        
        @Override
        public TimelineDelegationTokenIdentifier createIdentifier() {
            return new TimelineDelegationTokenIdentifier();
        }
    }
}
