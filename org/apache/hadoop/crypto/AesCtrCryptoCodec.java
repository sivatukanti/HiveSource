// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import java.io.IOException;
import com.google.common.base.Preconditions;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public abstract class AesCtrCryptoCodec extends CryptoCodec
{
    protected static final CipherSuite SUITE;
    private static final int AES_BLOCK_SIZE;
    
    @Override
    public CipherSuite getCipherSuite() {
        return AesCtrCryptoCodec.SUITE;
    }
    
    @Override
    public void calculateIV(final byte[] initIV, long counter, final byte[] IV) {
        Preconditions.checkArgument(initIV.length == AesCtrCryptoCodec.AES_BLOCK_SIZE);
        Preconditions.checkArgument(IV.length == AesCtrCryptoCodec.AES_BLOCK_SIZE);
        int i = IV.length;
        int j = 0;
        int sum = 0;
        while (i-- > 0) {
            sum = (initIV[i] & 0xFF) + (sum >>> 8);
            if (j++ < 8) {
                sum += ((byte)counter & 0xFF);
                counter >>>= 8;
            }
            IV[i] = (byte)sum;
        }
    }
    
    @Override
    public void close() throws IOException {
    }
    
    static {
        SUITE = CipherSuite.AES_CTR_NOPADDING;
        AES_BLOCK_SIZE = AesCtrCryptoCodec.SUITE.getAlgorithmBlockSize();
    }
}
