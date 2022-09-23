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
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.Camellia128Provider;

public class Camellia128CtsCmacEnc extends KeKiCmacEnc
{
    public Camellia128CtsCmacEnc() {
        super(new Camellia128Provider(), EncryptionType.CAMELLIA128_CTS_CMAC, new CamelliaKeyMaker(new Camellia128Provider()));
        this.keyMaker(new CamelliaKeyMaker((CamelliaProvider)this.encProvider()));
    }
    
    @Override
    public EncryptionType eType() {
        return EncryptionType.CAMELLIA128_CTS_CMAC;
    }
    
    @Override
    public CheckSumType checksumType() {
        return CheckSumType.CMAC_CAMELLIA128;
    }
}
