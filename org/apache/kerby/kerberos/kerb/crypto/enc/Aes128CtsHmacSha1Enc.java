// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.crypto.key.KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.key.DkKeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.AesProvider;
import org.apache.kerby.kerberos.kerb.crypto.key.AesKeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Sha1Provider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.Aes128Provider;

public class Aes128CtsHmacSha1Enc extends KeKiHmacSha1Enc
{
    public Aes128CtsHmacSha1Enc() {
        super(new Aes128Provider(), new Sha1Provider(), new AesKeyMaker(new Aes128Provider()));
        this.keyMaker(new AesKeyMaker((AesProvider)this.encProvider()));
    }
    
    @Override
    public int checksumSize() {
        return 12;
    }
    
    @Override
    public EncryptionType eType() {
        return EncryptionType.AES128_CTS_HMAC_SHA1_96;
    }
    
    @Override
    public CheckSumType checksumType() {
        return CheckSumType.HMAC_SHA1_96_AES128;
    }
}
