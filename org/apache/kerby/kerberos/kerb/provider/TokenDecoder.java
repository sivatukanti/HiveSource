// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.provider;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.type.base.AuthToken;

public interface TokenDecoder
{
    AuthToken decodeFromBytes(final byte[] p0) throws IOException;
    
    AuthToken decodeFromString(final String p0) throws IOException;
    
    void setVerifyKey(final PublicKey p0);
    
    void setVerifyKey(final byte[] p0);
    
    void setDecryptionKey(final PrivateKey p0);
    
    void setDecryptionKey(final byte[] p0);
    
    boolean isSigned();
}
