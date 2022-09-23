// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.key.KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.CamelliaProvider;
import org.apache.kerby.kerberos.kerb.crypto.key.CamelliaKeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.Camellia256Provider;

public class CmacCamellia256CheckSum extends CmacKcCheckSum
{
    public CmacCamellia256CheckSum() {
        super(new Camellia256Provider(), 16, 16);
        this.keyMaker(new CamelliaKeyMaker((CamelliaProvider)this.encProvider()));
    }
    
    @Override
    public int confounderSize() {
        return 16;
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.CMAC_CAMELLIA256;
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
}
