// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc.provider;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.util.Camellia;

public abstract class CamelliaProvider extends AbstractEncryptProvider
{
    public CamelliaProvider(final int blockSize, final int keyInputSize, final int keySize) {
        super(blockSize, keyInputSize, keySize);
    }
    
    @Override
    protected void doEncrypt(final byte[] data, final byte[] key, final byte[] cipherState, final boolean encrypt) throws KrbException {
        final Camellia cipher = new Camellia();
        cipher.setKey(encrypt, key);
        if (encrypt) {
            cipher.encrypt(data, cipherState);
        }
        else {
            cipher.decrypt(data, cipherState);
        }
    }
    
    @Override
    public boolean supportCbcMac() {
        return true;
    }
    
    @Override
    public byte[] cbcMac(final byte[] key, final byte[] cipherState, final byte[] data) {
        final Camellia cipher = new Camellia();
        cipher.setKey(true, key);
        final int blocksNum = data.length / this.blockSize();
        cipher.cbcEnc(data, 0, blocksNum, cipherState);
        return data;
    }
}
