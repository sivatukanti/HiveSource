// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Md5Provider;

public final class RsaMd5DesCheckSum extends ConfounderedDesCheckSum
{
    public RsaMd5DesCheckSum() {
        super(new Md5Provider(), 24, 24);
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.RSA_MD5_DES;
    }
}
