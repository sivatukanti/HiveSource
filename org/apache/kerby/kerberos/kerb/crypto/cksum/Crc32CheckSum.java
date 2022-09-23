// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Crc32Provider;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.AbstractUnkeyedCheckSumTypeHandler;

public class Crc32CheckSum extends AbstractUnkeyedCheckSumTypeHandler
{
    public Crc32CheckSum() {
        super(new Crc32Provider(), 4, 4);
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.CRC32;
    }
}
