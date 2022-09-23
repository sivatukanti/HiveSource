// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.crypto.AbstractCryptoTypeHandler;
import org.apache.kerby.kerberos.kerb.crypto.util.BytesUtil;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.util.Confounder;
import org.apache.kerby.kerberos.kerb.crypto.key.DkKeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;

public abstract class KeKiEnc extends AbstractEncTypeHandler
{
    public KeKiEnc(final EncryptProvider encProvider, final HashProvider hashProvider) {
        super(encProvider, hashProvider, 16);
    }
    
    @Override
    public int paddingSize() {
        return 0;
    }
    
    @Override
    protected int paddingLength(final int inputLen) {
        final int payloadLen = this.confounderSize() + inputLen;
        final int padding = this.paddingSize();
        if (padding == 0 || payloadLen % padding == 0) {
            return 0;
        }
        return padding - payloadLen % padding;
    }
    
    @Override
    protected void encryptWith(final byte[] workBuffer, final int[] workLens, final byte[] key, final byte[] iv, final int usage) throws KrbException {
        final int confounderLen = workLens[0];
        final int checksumLen = workLens[1];
        final int inputLen = workLens[2];
        final int paddingLen = workLens[3];
        final byte[] constant = { (byte)(usage >> 24 & 0xFF), (byte)(usage >> 16 & 0xFF), (byte)(usage >> 8 & 0xFF), (byte)(usage & 0xFF), -86 };
        final byte[] ke = ((DkKeyMaker)this.keyMaker()).dk(key, constant);
        constant[4] = 85;
        final byte[] ki = ((DkKeyMaker)this.keyMaker()).dk(key, constant);
        final byte[] tmpEnc = new byte[confounderLen + inputLen + paddingLen];
        final byte[] confounder = Confounder.makeBytes(confounderLen);
        System.arraycopy(confounder, 0, tmpEnc, 0, confounderLen);
        System.arraycopy(workBuffer, confounderLen + checksumLen, tmpEnc, confounderLen, inputLen);
        for (int i = confounderLen + inputLen; i < paddingLen; ++i) {
            tmpEnc[i] = 0;
        }
        final byte[] checksum = this.makeChecksum(ki, tmpEnc, checksumLen);
        this.encProvider().encrypt(ke, iv, tmpEnc);
        System.arraycopy(tmpEnc, 0, workBuffer, 0, tmpEnc.length);
        System.arraycopy(checksum, 0, workBuffer, tmpEnc.length, checksum.length);
    }
    
    @Override
    protected byte[] decryptWith(final byte[] workBuffer, final int[] workLens, final byte[] key, final byte[] iv, final int usage) throws KrbException {
        final int confounderLen = workLens[0];
        final int checksumLen = workLens[1];
        final int dataLen = workLens[2];
        final byte[] constant = new byte[5];
        BytesUtil.int2bytes(usage, constant, 0, true);
        constant[4] = -86;
        final byte[] ke = ((DkKeyMaker)this.keyMaker()).dk(key, constant);
        constant[4] = 85;
        final byte[] ki = ((DkKeyMaker)this.keyMaker()).dk(key, constant);
        final byte[] tmpEnc = new byte[confounderLen + dataLen];
        System.arraycopy(workBuffer, 0, tmpEnc, 0, confounderLen + dataLen);
        final byte[] checksum = new byte[checksumLen];
        System.arraycopy(workBuffer, confounderLen + dataLen, checksum, 0, checksumLen);
        this.encProvider().decrypt(ke, iv, tmpEnc);
        final byte[] newChecksum = this.makeChecksum(ki, tmpEnc, checksumLen);
        if (!AbstractCryptoTypeHandler.checksumEqual(checksum, newChecksum)) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_BAD_INTEGRITY);
        }
        final byte[] data = new byte[dataLen];
        System.arraycopy(tmpEnc, confounderLen, data, 0, dataLen);
        return data;
    }
    
    protected abstract byte[] makeChecksum(final byte[] p0, final byte[] p1, final int p2) throws KrbException;
}
