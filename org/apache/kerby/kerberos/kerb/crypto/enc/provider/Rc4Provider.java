// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc.provider;

import java.security.GeneralSecurityException;
import org.apache.kerby.kerberos.kerb.KrbException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

public class Rc4Provider extends AbstractEncryptProvider
{
    public Rc4Provider() {
        super(1, 16, 16);
    }
    
    @Override
    protected void doEncrypt(final byte[] data, final byte[] key, final byte[] cipherState, final boolean encrypt) throws KrbException {
        try {
            final Cipher cipher = Cipher.getInstance("ARCFOUR");
            final SecretKeySpec secretKey = new SecretKeySpec(key, "ARCFOUR");
            cipher.init(encrypt ? 1 : 2, secretKey);
            final byte[] output = cipher.doFinal(data);
            System.arraycopy(output, 0, data, 0, output.length);
        }
        catch (GeneralSecurityException e) {
            final KrbException ke = new KrbException(e.getMessage());
            ke.initCause(e);
            throw ke;
        }
    }
}
