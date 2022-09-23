// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote;

import org.apache.kerby.config.ConfigKey;

public enum AdminConfigKey implements ConfigKey
{
    KRB_DEBUG((Object)true), 
    ADMIN_HOST((Object)"localhost"), 
    ADMIN_PORT((Object)null), 
    ADMIN_ALLOW_UDP((Object)false), 
    ADMIN_ALLOW_TCP((Object)false), 
    ADMIN_UDP_PORT((Object)null), 
    ADMIN_TCP_PORT((Object)null), 
    ADMIN_DOMAIN((Object)"example.com"), 
    DEFAULT_REALM((Object)null), 
    ADMIN_REALM((Object)"EXAMPLE.COM"), 
    KEYTAB_FILE, 
    PROTOCOL, 
    SERVER_NAME((Object)"localhost");
    
    private Object defaultValue;
    
    private AdminConfigKey() {
        this.defaultValue = null;
    }
    
    private AdminConfigKey(final Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    @Override
    public String getPropertyKey() {
        return this.name().toLowerCase();
    }
    
    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }
}
