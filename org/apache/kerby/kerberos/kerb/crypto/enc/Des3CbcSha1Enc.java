// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.crypto.key.KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.key.DkKeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;
import org.apache.kerby.kerberos.kerb.crypto.key.Des3KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Sha1Provider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.Des3Provider;

public class Des3CbcSha1Enc extends KeKiHmacSha1Enc
{
    public Des3CbcSha1Enc() {
        super(new Des3Provider(), new Sha1Provider(), new Des3KeyMaker(new Des3Provider()));
        this.keyMaker(new Des3KeyMaker(this.encProvider()));
    }
    
    @Override
    public int paddingSize() {
        return this.encProvider().blockSize();
    }
    
    @Override
    public EncryptionType eType() {
        return EncryptionType.DES3_CBC_SHA1;
    }
    
    @Override
    public CheckSumType checksumType() {
        return CheckSumType.HMAC_SHA1_DES3;
    }
}
