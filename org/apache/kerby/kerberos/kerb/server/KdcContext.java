// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server;

import org.apache.kerby.kerberos.kerb.server.preauth.PreauthHandler;
import org.apache.kerby.kerberos.kerb.server.replay.ReplayCheckService;
import org.apache.kerby.kerberos.kerb.identity.IdentityService;

public class KdcContext
{
    private final KdcSetting kdcSetting;
    private IdentityService identityService;
    private ReplayCheckService replayCache;
    private PreauthHandler preauthHandler;
    
    public KdcContext(final KdcSetting kdcSetting) {
        this.kdcSetting = kdcSetting;
    }
    
    public KdcSetting getKdcSetting() {
        return this.kdcSetting;
    }
    
    public KdcConfig getConfig() {
        return this.kdcSetting.getKdcConfig();
    }
    
    public void setPreauthHandler(final PreauthHandler preauthHandler) {
        this.preauthHandler = preauthHandler;
    }
    
    public PreauthHandler getPreauthHandler() {
        return this.preauthHandler;
    }
    
    public void setReplayCache(final ReplayCheckService replayCache) {
        this.replayCache = replayCache;
    }
    
    public ReplayCheckService getReplayCache() {
        return this.replayCache;
    }
    
    public void setIdentityService(final IdentityService identityService) {
        this.identityService = identityService;
    }
    
    public IdentityService getIdentityService() {
        return this.identityService;
    }
    
    public String getKdcRealm() {
        return this.kdcSetting.getKdcRealm();
    }
}
