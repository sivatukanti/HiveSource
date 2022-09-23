// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote.impl;

import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminSetting;
import org.apache.kerby.kerberos.kerb.KrbException;

public interface InternalAdminClient
{
    void init() throws KrbException;
    
    AdminSetting getSetting();
}
