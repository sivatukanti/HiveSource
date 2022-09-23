// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Md4Provider;

public class RsaMd4DesCheckSum extends ConfounderedDesCheckSum
{
    public RsaMd4DesCheckSum() {
        super(new Md4Provider(), 24, 24);
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.RSA_MD4_DES;
    }
}
