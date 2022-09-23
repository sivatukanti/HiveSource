// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote;

public class AdminContext
{
    private AdminSetting adminSetting;
    
    public void init(final AdminSetting adminSetting) {
        this.adminSetting = adminSetting;
    }
    
    public AdminSetting getAdminSetting() {
        return this.adminSetting;
    }
    
    public AdminConfig getConfig() {
        return this.adminSetting.getAdminConfig();
    }
}
