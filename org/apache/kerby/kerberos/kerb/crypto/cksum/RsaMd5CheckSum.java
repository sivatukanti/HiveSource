// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Md5Provider;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.AbstractUnkeyedCheckSumTypeHandler;

public class RsaMd5CheckSum extends AbstractUnkeyedCheckSumTypeHandler
{
    public RsaMd5CheckSum() {
        super(new Md5Provider(), 16, 16);
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.RSA_MD5;
    }
}
