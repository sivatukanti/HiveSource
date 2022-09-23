// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.key.KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.AesProvider;
import org.apache.kerby.kerberos.kerb.crypto.key.AesKeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.Aes128Provider;

public class HmacSha1Aes128CheckSum extends HmacKcCheckSum
{
    public HmacSha1Aes128CheckSum() {
        super(new Aes128Provider(), 20, 12);
        this.keyMaker(new AesKeyMaker((AesProvider)this.encProvider()));
    }
    
    @Override
    public int confounderSize() {
        return 16;
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.HMAC_SHA1_96_AES128;
    }
    
    @Override
    public boolean isSafe() {
        return true;
    }
    
    @Override
    public int cksumSize() {
        return 12;
    }
    
    @Override
    public int keySize() {
        return 16;
    }
}
