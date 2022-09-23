// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.key.KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.CamelliaProvider;
import org.apache.kerby.kerberos.kerb.crypto.key.CamelliaKeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.Camellia128Provider;

public class CmacCamellia128CheckSum extends CmacKcCheckSum
{
    public CmacCamellia128CheckSum() {
        super(new Camellia128Provider(), 16, 16);
        this.keyMaker(new CamelliaKeyMaker((CamelliaProvider)this.encProvider()));
    }
    
    @Override
    public int confounderSize() {
        return 16;
    }
    
    @Override
    public CheckSumType cksumType() {
        return CheckSumType.CMAC_CAMELLIA128;
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
