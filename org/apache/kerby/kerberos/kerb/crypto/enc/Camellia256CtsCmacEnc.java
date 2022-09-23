// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.crypto.key.KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.key.DkKeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.CamelliaProvider;
import org.apache.kerby.kerberos.kerb.crypto.key.CamelliaKeyMaker;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.Camellia256Provider;

public class Camellia256CtsCmacEnc extends KeKiCmacEnc
{
    public Camellia256CtsCmacEnc() {
        super(new Camellia256Provider(), EncryptionType.CAMELLIA256_CTS_CMAC, new CamelliaKeyMaker(new Camellia256Provider()));
        this.keyMaker(new CamelliaKeyMaker((CamelliaProvider)this.encProvider()));
    }
    
    @Override
    public EncryptionType eType() {
        return EncryptionType.CAMELLIA256_CTS_CMAC;
    }
    
    @Override
    public CheckSumType checksumType() {
        return CheckSumType.CMAC_CAMELLIA256;
    }
}
