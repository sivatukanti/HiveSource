// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;

public class Rc4HmacExpEnc extends Rc4HmacEnc
{
    public Rc4HmacExpEnc() {
        super(true);
    }
    
    @Override
    public EncryptionType eType() {
        return EncryptionType.ARCFOUR_HMAC_EXP;
    }
}
