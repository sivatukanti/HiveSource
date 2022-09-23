// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.key.KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.key.Des3KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.Des3Provider;

public class HmacSha1Des3CheckSum extends HmacKcCheckSum
{
    public HmacSha1Des3CheckSum() {
        super(new Des3Provider(), 20, 20);
        this.keyMaker(new Des3KeyMaker(this.encProvider()));
    }
    
    @Override
    public int confounderSize() {
        return 8;
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.HMAC_SHA1_DES3;
    }
    
    @Override
    public boolean isSafe() {
        return true;
    }
    
    @Override
    public int cksumSize() {
        return 20;
    }
    
    @Override
    public int keySize() {
        return 24;
    }
}
