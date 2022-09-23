// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.impl;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.server.KdcUtil;
import org.apache.kerby.config.Config;
import org.apache.kerby.kerberos.kerb.identity.CacheableIdentityService;
import org.apache.kerby.kerberos.kerb.identity.backend.MemoryIdentityBackend;
import org.apache.kerby.kerberos.kerb.identity.IdentityService;
import org.apache.kerby.kerberos.kerb.identity.backend.IdentityBackend;
import org.apache.kerby.kerberos.kerb.server.KdcSetting;
import org.apache.kerby.kerberos.kerb.identity.backend.BackendConfig;
import org.apache.kerby.kerberos.kerb.server.KdcConfig;

public class AbstractInternalKdcServer implements InternalKdcServer
{
    private boolean started;
    private final KdcConfig kdcConfig;
    private final BackendConfig backendConfig;
    private final KdcSetting kdcSetting;
    private IdentityBackend backend;
    private IdentityService identityService;
    
    public AbstractInternalKdcServer(final KdcSetting kdcSetting) {
        this.kdcSetting = kdcSetting;
        this.kdcConfig = kdcSetting.getKdcConfig();
        this.backendConfig = kdcSetting.getBackendConfig();
    }
    
    @Override
    public KdcSetting getSetting() {
        return this.kdcSetting;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    protected String getServiceName() {
        return this.kdcConfig.getKdcServiceName();
    }
    
    protected IdentityService getIdentityService() {
        if (this.identityService == null) {
            if (this.backend instanceof MemoryIdentityBackend) {
                this.identityService = this.backend;
            }
            else {
                this.identityService = new CacheableIdentityService(this.backendConfig, this.backend);
            }
        }
        return this.identityService;
    }
    
    @Override
    public void init() throws KrbException {
        this.backend = KdcUtil.getBackend(this.backendConfig);
    }
    
    @Override
    public void start() throws KrbException {
        try {
            this.doStart();
        }
        catch (Exception e) {
            throw new KrbException("Failed to start " + this.getServiceName(), e);
        }
        this.started = true;
    }
    
    public boolean enableDebug() {
        return this.kdcConfig.enableDebug();
    }
    
    @Override
    public IdentityBackend getIdentityBackend() {
        return this.backend;
    }
    
    protected void doStart() throws Exception {
        this.backend.start();
    }
    
    @Override
    public void stop() throws KrbException {
        try {
            this.doStop();
        }
        catch (Exception e) {
            throw new KrbException("Failed to stop " + this.getServiceName(), e);
        }
        this.started = false;
    }
    
    protected void doStop() throws Exception {
        this.backend.stop();
    }
}
