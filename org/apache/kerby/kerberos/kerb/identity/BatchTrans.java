// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.identity;

import org.apache.kerby.kerberos.kerb.KrbException;

public interface BatchTrans
{
    void commit() throws KrbException;
    
    void rollback() throws KrbException;
    
    BatchTrans addIdentity(final KrbIdentity p0) throws KrbException;
    
    BatchTrans updateIdentity(final KrbIdentity p0) throws KrbException;
    
    BatchTrans deleteIdentity(final String p0) throws KrbException;
}
