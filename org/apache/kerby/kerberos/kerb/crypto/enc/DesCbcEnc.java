// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.crypto.AbstractCryptoTypeHandler;
import org.apache.kerby.kerberos.kerb.crypto.util.Confounder;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Md5Provider;
import org.apache.kerby.kerberos.kerb.crypto.key.KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.key.DesKeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.DesProvider;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;

abstract class DesCbcEnc extends AbstractEncTypeHandler
{
    DesCbcEnc(final HashProvider hashProvider) {
        super(new DesProvider(), hashProvider, 16);
        this.keyMaker(new DesKeyMaker(this.encProvider()));
    }
    
    @Override
    public byte[] prf(final byte[] key, final byte[] seed) throws KrbException {
        final Md5Provider md5Provider = new Md5Provider();
        md5Provider.hash(seed);
        final byte[] output = md5Provider.output();
        this.encProvider().encrypt(key, output);
        return output;
    }
    
    @Override
    protected int paddingLength(final int inputLen) {
        final int payloadLen = this.confounderSize() + this.checksumSize() + inputLen;
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
        final int dataLen = workLens[2];
        final int paddingLen = workLens[3];
        final byte[] confounder = Confounder.makeBytes(confounderLen);
        System.arraycopy(confounder, 0, workBuffer, 0, confounderLen);
        for (int i = confounderLen + checksumLen + dataLen; i < paddingLen; ++i) {
            workBuffer[i] = 0;
        }
        this.hashProvider().hash(workBuffer);
        final byte[] cksum = this.hashProvider().output();
        System.arraycopy(cksum, 0, workBuffer, confounderLen, checksumLen);
        this.encProvider().encrypt(key, iv, workBuffer);
    }
    
    @Override
    protected byte[] decryptWith(final byte[] workBuffer, final int[] workLens, final byte[] key, final byte[] iv, final int usage) throws KrbException {
        final int confounderLen = workLens[0];
        final int checksumLen = workLens[1];
        final int dataLen = workLens[2];
        this.encProvider().decrypt(key, iv, workBuffer);
        final byte[] checksum = new byte[checksumLen];
        for (int i = 0; i < checksumLen; ++i) {
            checksum[i] = workBuffer[confounderLen + i];
            workBuffer[confounderLen + i] = 0;
        }
        this.hashProvider().hash(workBuffer);
        final byte[] newChecksum = this.hashProvider().output();
        if (!AbstractCryptoTypeHandler.checksumEqual(checksum, newChecksum)) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_BAD_INTEGRITY);
        }
        final byte[] data = new byte[dataLen];
        System.arraycopy(workBuffer, confounderLen + checksumLen, data, 0, dataLen);
        return data;
    }
}
