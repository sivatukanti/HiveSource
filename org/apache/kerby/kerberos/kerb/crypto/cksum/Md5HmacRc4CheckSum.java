// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.util.Hmac;
import org.apache.kerby.kerberos.kerb.crypto.util.Rc4;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Md5Provider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.Rc4Provider;

public class Md5HmacRc4CheckSum extends AbstractKeyedCheckSumTypeHandler
{
    public Md5HmacRc4CheckSum() {
        super(new Rc4Provider(), new Md5Provider(), 16, 16);
    }
    
    @Override
    public int confounderSize() {
        return 8;
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.MD5_HMAC_ARCFOUR;
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
        final byte[] ksign = key;
        final byte[] salt = Rc4.getSalt(usage, false);
        this.hashProvider().hash(salt);
        this.hashProvider().hash(data, start, len);
        final byte[] hashTmp = this.hashProvider().output();
        return Hmac.hmac(this.hashProvider(), ksign, hashTmp);
    }
}
