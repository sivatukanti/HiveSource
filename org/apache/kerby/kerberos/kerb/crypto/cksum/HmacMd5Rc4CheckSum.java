// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.util.Rc4;
import org.apache.kerby.kerberos.kerb.crypto.util.Hmac;
import java.nio.charset.StandardCharsets;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Md5Provider;

public class HmacMd5Rc4CheckSum extends AbstractKeyedCheckSumTypeHandler
{
    public HmacMd5Rc4CheckSum() {
        super(null, new Md5Provider(), 16, 16);
    }
    
    @Override
    public int confounderSize() {
        return 8;
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.HMAC_MD5_ARCFOUR;
    }
    
    @Override
    public boolean isSafe() {
        return true;
    }
    
    @Override
    public int cksumSize() {
        return 16;
    }
    
    @Override
    public int keySize() {
        return 16;
    }
    
    @Override
    protected byte[] doChecksumWithKey(final byte[] data, final int start, final int len, final byte[] key, final int usage) throws KrbException {
        final byte[] signKey = "signaturekey".getBytes(StandardCharsets.UTF_8);
        final byte[] newSignKey = new byte[signKey.length + 1];
        System.arraycopy(signKey, 0, newSignKey, 0, signKey.length);
        final byte[] ksign = Hmac.hmac(this.hashProvider(), key, newSignKey);
        final byte[] salt = Rc4.getSalt(usage, false);
        this.hashProvider().hash(salt);
        this.hashProvider().hash(data, start, len);
        final byte[] hashTmp = this.hashProvider().output();
        return Hmac.hmac(this.hashProvider(), ksign, hashTmp);
    }
}
