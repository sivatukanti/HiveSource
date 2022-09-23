// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.key;

import org.apache.kerby.kerberos.kerb.crypto.util.Des;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.util.Nfold;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public class Des3KeyMaker extends DkKeyMaker
{
    public Des3KeyMaker(final EncryptProvider encProvider) {
        super(encProvider);
    }
    
    @Override
    public byte[] str2key(final String string, final String salt, final byte[] param) throws KrbException {
        final byte[] utf8Bytes = AbstractKeyMaker.makePasswdSalt(string, salt);
        final int keyInputSize = this.encProvider().keyInputSize();
        final byte[] tmpKey = this.random2Key(Nfold.nfold(utf8Bytes, keyInputSize));
        return this.dk(tmpKey, Des3KeyMaker.KERBEROS_CONSTANT);
    }
    
    private static byte[] getEightBits(final byte[] bits56) {
        final byte[] bits57 = new byte[8];
        System.arraycopy(bits56, 0, bits57, 0, 7);
        bits57[7] = (byte)((bits56[0] & 0x1) << 1 | (bits56[1] & 0x1) << 2 | (bits56[2] & 0x1) << 3 | (bits56[3] & 0x1) << 4 | (bits56[4] & 0x1) << 5 | (bits56[5] & 0x1) << 6 | (bits56[6] & 0x1) << 7);
        return bits57;
    }
    
    @Override
    public byte[] random2Key(final byte[] randomBits) throws KrbException {
        if (randomBits.length != this.encProvider().keyInputSize()) {
            throw new KrbException("Invalid random bits, not of correct bytes size");
        }
        final byte[] key = new byte[this.encProvider().keySize()];
        final byte[] tmp1 = new byte[7];
        for (int i = 0; i < 3; ++i) {
            System.arraycopy(randomBits, i * 7, key, i * 8, 7);
            System.arraycopy(randomBits, i * 7, tmp1, 0, 7);
            final byte[] tmp2 = getEightBits(tmp1);
            key[8 * (i + 1) - 1] = tmp2[7];
            final int nthByte = i * 8;
            key[nthByte + 7] = (byte)((key[nthByte + 0] & 0x1) << 1 | (key[nthByte + 1] & 0x1) << 2 | (key[nthByte + 2] & 0x1) << 3 | (key[nthByte + 3] & 0x1) << 4 | (key[nthByte + 4] & 0x1) << 5 | (key[nthByte + 5] & 0x1) << 6 | (key[nthByte + 6] & 0x1) << 7);
            for (int j = 0; j < 8; ++j) {
                int tmp3 = key[nthByte + j] & 0xFE;
                tmp3 |= ((Integer.bitCount(tmp3) & 0x1) ^ 0x1);
                key[nthByte + j] = (byte)tmp3;
            }
        }
        for (int i = 0; i < 3; ++i) {
            Des.fixKey(key, i * 8, 8);
        }
        return key;
    }
}
