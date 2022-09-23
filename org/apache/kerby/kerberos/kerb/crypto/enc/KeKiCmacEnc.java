// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.util.Cmac;
import org.apache.kerby.kerberos.kerb.crypto.EncryptionHandler;
import java.nio.charset.StandardCharsets;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.crypto.key.DkKeyMaker;

public abstract class KeKiCmacEnc extends KeKiEnc
{
    private DkKeyMaker km;
    private EncryptionType eType;
    
    public KeKiCmacEnc(final EncryptProvider encProvider, final EncryptionType eType, final DkKeyMaker km) {
        super(encProvider, null);
        this.eType = eType;
        this.km = km;
    }
    
    @Override
    public int checksumSize() {
        return this.encProvider().blockSize();
    }
    
    @Override
    public byte[] prf(final byte[] key, final byte[] seed) throws KrbException {
        final byte[] prfConst = "prf".getBytes(StandardCharsets.UTF_8);
        if (EncryptionHandler.getEncHandler(this.eType()).prfSize() != this.encProvider().blockSize()) {
            return null;
        }
        final byte[] kp = this.km.dk(key, prfConst);
        return Cmac.cmac(this.encProvider(), kp, seed);
    }
    
    @Override
    protected byte[] makeChecksum(final byte[] key, final byte[] data, final int hashSize) throws KrbException {
        final byte[] hash = Cmac.cmac(this.encProvider(), key, data);
        final byte[] output = new byte[hashSize];
        System.arraycopy(hash, 0, output, 0, hashSize);
        return output;
    }
}
