// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMDelegationTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.resourcemanager.security.AMRMTokenSecretManager;
import org.apache.hadoop.service.AbstractService;

public class RMSecretManagerService extends AbstractService
{
    AMRMTokenSecretManager amRmTokenSecretManager;
    NMTokenSecretManagerInRM nmTokenSecretManager;
    ClientToAMTokenSecretManagerInRM clientToAMSecretManager;
    RMContainerTokenSecretManager containerTokenSecretManager;
    RMDelegationTokenSecretManager rmDTSecretManager;
    RMContextImpl rmContext;
    
    public RMSecretManagerService(final Configuration conf, final RMContextImpl rmContext) {
        super(RMSecretManagerService.class.getName());
        (this.rmContext = rmContext).setNMTokenSecretManager(this.nmTokenSecretManager = this.createNMTokenSecretManager(conf));
        rmContext.setContainerTokenSecretManager(this.containerTokenSecretManager = this.createContainerTokenSecretManager(conf));
        rmContext.setClientToAMTokenSecretManager(this.clientToAMSecretManager = this.createClientToAMTokenSecretManager());
        rmContext.setAMRMTokenSecretManager(this.amRmTokenSecretManager = this.createAMRMTokenSecretManager(conf, this.rmContext));
        rmContext.setRMDelegationTokenSecretManager(this.rmDTSecretManager = this.createRMDelegationTokenSecretManager(conf, rmContext));
    }
    
    public void serviceInit(final Configuration conf) throws Exception {
        super.serviceInit(conf);
    }
    
    public void serviceStart() throws Exception {
        this.amRmTokenSecretManager.start();
        this.containerTokenSecretManager.start();
        this.nmTokenSecretManager.start();
        try {
            this.rmDTSecretManager.startThreads();
        }
        catch (IOException ie) {
            throw new YarnRuntimeException("Failed to start secret manager threads", ie);
        }
        super.serviceStart();
    }
    
    public void serviceStop() throws Exception {
        if (this.rmDTSecretManager != null) {
            this.rmDTSecretManager.stopThreads();
        }
        if (this.amRmTokenSecretManager != null) {
            this.amRmTokenSecretManager.stop();
        }
        if (this.containerTokenSecretManager != null) {
            this.containerTokenSecretManager.stop();
        }
        if (this.nmTokenSecretManager != null) {
            this.nmTokenSecretManager.stop();
        }
        super.serviceStop();
    }
    
    protected RMContainerTokenSecretManager createContainerTokenSecretManager(final Configuration conf) {
        return new RMContainerTokenSecretManager(conf);
    }
    
    protected NMTokenSecretManagerInRM createNMTokenSecretManager(final Configuration conf) {
        return new NMTokenSecretManagerInRM(conf);
    }
    
    protected AMRMTokenSecretManager createAMRMTokenSecretManager(final Configuration conf, final RMContext rmContext) {
        return new AMRMTokenSecretManager(conf, rmContext);
    }
    
    protected ClientToAMTokenSecretManagerInRM createClientToAMTokenSecretManager() {
        return new ClientToAMTokenSecretManagerInRM();
    }
    
    @VisibleForTesting
    protected RMDelegationTokenSecretManager createRMDelegationTokenSecretManager(final Configuration conf, final RMContext rmContext) {
        final long secretKeyInterval = conf.getLong("yarn.resourcemanager.delegation.key.update-interval", 86400000L);
        final long tokenMaxLifetime = conf.getLong("yarn.resourcemanager.delegation.token.max-lifetime", 604800000L);
        final long tokenRenewInterval = conf.getLong("yarn.resourcemanager.delegation.token.renew-interval", 86400000L);
        return new RMDelegationTokenSecretManager(secretKeyInterval, tokenMaxLifetime, tokenRenewInterval, 3600000L, rmContext);
    }
}
