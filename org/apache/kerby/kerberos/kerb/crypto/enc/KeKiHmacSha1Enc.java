// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.crypto.util.Hmac;
import org.apache.kerby.kerberos.kerb.KrbException;
import java.nio.charset.StandardCharsets;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;
import org.apache.kerby.kerberos.kerb.crypto.key.DkKeyMaker;

public abstract class KeKiHmacSha1Enc extends KeKiEnc
{
    private DkKeyMaker km;
    
    public KeKiHmacSha1Enc(final EncryptProvider encProvider, final HashProvider hashProvider, final DkKeyMaker km) {
        super(encProvider, hashProvider);
        this.km = km;
    }
    
    @Override
    public byte[] prf(final byte[] key, final byte[] seed) throws KrbException {
        final byte[] prfConst = "prf".getBytes(StandardCharsets.UTF_8);
        final int cksumSize = this.hashProvider().hashSize() / this.encProvider().blockSize() * this.encProvider().blockSize();
        final byte[] cksum = new byte[cksumSize];
        final byte[] output = new byte[this.prfSize()];
        this.hashProvider().hash(seed);
        System.arraycopy(this.hashProvider().output(), 0, cksum, 0, cksumSize);
        final byte[] kp = this.km.dk(key, prfConst);
        this.encProvider().encrypt(kp, cksum);
        System.arraycopy(cksum, 0, output, 0, this.prfSize());
        return output;
    }
    
    @Override
    protected byte[] makeChecksum(final byte[] key, final byte[] data, final int hashSize) throws KrbException {
        final byte[] hash = Hmac.hmac(this.hashProvider(), key, data);
        final byte[] output = new byte[hashSize];
        System.arraycopy(hash, 0, output, 0, hashSize);
        return output;
    }
}
