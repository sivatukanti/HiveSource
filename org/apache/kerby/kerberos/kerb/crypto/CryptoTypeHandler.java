// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto;

import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public interface CryptoTypeHandler
{
    String name();
    
    String displayName();
    
    EncryptProvider encProvider();
    
    HashProvider hashProvider();
}
