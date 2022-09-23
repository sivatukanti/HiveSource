// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.impl;

import org.apache.kerby.kerberos.kerb.type.ticket.SgtTicket;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.client.KrbSetting;
import org.apache.kerby.kerberos.kerb.KrbException;

public interface InternalKrbClient
{
    void init() throws KrbException;
    
    KrbSetting getSetting();
    
    TgtTicket requestTgt(final KOptions p0) throws KrbException;
    
    SgtTicket requestSgt(final KOptions p0) throws KrbException;
}
