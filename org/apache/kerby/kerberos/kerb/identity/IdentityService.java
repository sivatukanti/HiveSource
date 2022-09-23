// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.identity;

import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationData;
import org.apache.kerby.kerberos.kerb.type.ticket.EncTicketPart;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcClientRequest;
import org.apache.kerby.kerberos.kerb.KrbException;

public interface IdentityService
{
    boolean supportBatchTrans();
    
    BatchTrans startBatchTrans() throws KrbException;
    
    Iterable<String> getIdentities() throws KrbException;
    
    KrbIdentity getIdentity(final String p0) throws KrbException;
    
    AuthorizationData getIdentityAuthorizationData(final KdcClientRequest p0, final EncTicketPart p1) throws KrbException;
    
    KrbIdentity addIdentity(final KrbIdentity p0) throws KrbException;
    
    KrbIdentity updateIdentity(final KrbIdentity p0) throws KrbException;
    
    void deleteIdentity(final String p0) throws KrbException;
}
