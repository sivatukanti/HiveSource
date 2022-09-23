// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Md5Provider;

public class DesCbcMd5Enc extends DesCbcEnc
{
    public DesCbcMd5Enc() {
        super(new Md5Provider());
    }
    
    @Override
    public EncryptionType eType() {
        return EncryptionType.DES_CBC_MD5;
    }
    
    @Override
    public CheckSumType checksumType() {
        return CheckSumType.RSA_MD5_DES;
    }
}
