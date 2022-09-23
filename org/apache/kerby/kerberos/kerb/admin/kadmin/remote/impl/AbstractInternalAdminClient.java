// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote.impl;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminSetting;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminContext;

public abstract class AbstractInternalAdminClient implements InternalAdminClient
{
    private AdminContext context;
    private final AdminSetting krbSetting;
    
    public AbstractInternalAdminClient(final AdminSetting krbSetting) {
        this.krbSetting = krbSetting;
    }
    
    protected AdminContext getContext() {
        return this.context;
    }
    
    @Override
    public AdminSetting getSetting() {
        return this.krbSetting;
    }
    
    @Override
    public void init() throws KrbException {
        (this.context = new AdminContext()).init(this.krbSetting);
    }
    
    protected String fixPrincipal(String principal) {
        if (!principal.contains("@")) {
            principal = principal + "@" + this.krbSetting.getKdcRealm();
        }
        return principal;
    }
}
