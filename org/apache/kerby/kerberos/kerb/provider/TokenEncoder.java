// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.provider;

import java.security.PrivateKey;
import java.security.PublicKey;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.AuthToken;

public interface TokenEncoder
{
    byte[] encodeAsBytes(final AuthToken p0) throws KrbException;
    
    String encodeAsString(final AuthToken p0) throws KrbException;
    
    void setEncryptionKey(final PublicKey p0);
    
    void setEncryptionKey(final byte[] p0);
    
    void setSignKey(final PrivateKey p0);
    
    void setSignKey(final byte[] p0);
}
