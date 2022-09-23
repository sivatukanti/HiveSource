// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.key;

import org.apache.kerby.kerberos.kerb.crypto.util.Des;
import org.apache.kerby.kerberos.kerb.crypto.util.BytesUtil;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public class DesKeyMaker extends AbstractKeyMaker
{
    public DesKeyMaker(final EncryptProvider encProvider) {
        super(encProvider);
    }
    
    @Override
    public byte[] str2key(final String string, final String salt, final byte[] param) throws KrbException {
        String error = null;
        int type = 0;
        if (param != null) {
            if (param.length != 1) {
                error = "Invalid param to S2K";
            }
            type = param[0];
            if (type != 0 && type != 1) {
                error = "Invalid param to S2K";
            }
        }
        if (type == 1) {
            error = "AFS not supported yet";
        }
        if (error != null) {
            throw new KrbException(error);
        }
        return this.toKey(string, salt);
    }
    
    private byte[] toKey(final String string, final String salt) throws KrbException {
        final byte[] bytes = AbstractKeyMaker.makePasswdSalt(string, salt);
        final byte[] paddedBytes = BytesUtil.padding(bytes, 8);
        final byte[] fanFoldedKey = fanFold(string, salt, paddedBytes);
        final byte[] intermediateKey = intermediateKey(fanFoldedKey);
        final byte[] key = this.desEncryptedKey(intermediateKey, paddedBytes);
        keyCorrection(key);
        return key;
    }
    
    public static byte[] fanFold(final String string, final String salt, byte[] paddedBytes) {
        if (paddedBytes == null) {
            final byte[] bytes = AbstractKeyMaker.makePasswdSalt(string, salt);
            paddedBytes = BytesUtil.padding(bytes, 8);
        }
        final int blocksOfbytes8 = paddedBytes.length / 8;
        boolean odd = true;
        final byte[] bits56 = new byte[8];
        final byte[] tempString = new byte[8];
        for (int i = 0; i < blocksOfbytes8; ++i) {
            System.arraycopy(paddedBytes, 8 * i, bits56, 0, 8);
            removeMSBits(bits56);
            if (!odd) {
                reverse(bits56);
            }
            odd = !odd;
            BytesUtil.xor(bits56, 0, tempString);
        }
        return tempString;
    }
    
    public static byte[] intermediateKey(final byte[] fanFoldedKey) {
        final byte[] keyBytes = addParityBits(fanFoldedKey);
        keyCorrection(keyBytes);
        return keyBytes;
    }
    
    private byte[] desEncryptedKey(final byte[] intermediateKey, final byte[] originalBytes) throws KrbException {
        byte[] resultKey = null;
        if (this.encProvider().supportCbcMac()) {
            resultKey = this.encProvider().cbcMac(intermediateKey, intermediateKey, originalBytes);
            keyCorrection(resultKey);
            return resultKey;
        }
        throw new KrbException("cbcMac should be supported by the provider: " + this.encProvider().getClass());
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
        final byte[] keyBytes = getEightBits(randomBits);
        addParity(keyBytes);
        keyCorrection(keyBytes);
        return keyBytes;
    }
    
    private static byte[] removeMSBits(final byte[] bits56) {
        return bits56;
    }
    
    private static void reverse(final byte[] bits56) {
        for (int i = 0; i < 8; ++i) {
            byte bt = bits56[i];
            int t1 = bt >> 6 & 0x1;
            int t2 = bt >> 0 & 0x1;
            if (t1 != t2) {
                bt ^= 0x41;
            }
            t1 = (bt >> 5 & 0x1);
            t2 = (bt >> 1 & 0x1);
            if (t1 != t2) {
                bt ^= 0x22;
            }
            t1 = (bt >> 4 & 0x1);
            t2 = (bt >> 2 & 0x1);
            if (t1 != t2) {
                bt ^= 0x14;
            }
            bits56[i] = bt;
        }
        byte bt = bits56[7];
        bits56[7] = bits56[0];
        bits56[0] = bt;
        bt = bits56[6];
        bits56[6] = bits56[1];
        bits56[1] = bt;
        bt = bits56[5];
        bits56[5] = bits56[2];
        bits56[2] = bt;
        bt = bits56[4];
        bits56[4] = bits56[3];
        bits56[3] = bt;
    }
    
    private static byte[] addParityBits(final byte[] bits56) {
        for (int i = 0; i < 8; ++i) {
            final int n = i;
            bits56[n] <<= 1;
        }
        addParity(bits56);
        return bits56;
    }
    
    private static void keyCorrection(final byte[] key) {
        addParity(key);
        Des.fixKey(key, 0, key.length);
    }
    
    private static int smask(final int step) {
        return (1 << step) - 1;
    }
    
    private static byte pstep(final byte x, final int step) {
        return (byte)((x & smask(step)) ^ (x >> step & smask(step)));
    }
    
    private static byte parityChar(final byte abyte) {
        return pstep(pstep(pstep(abyte, 4), 2), 1);
    }
    
    private static void addParity(final byte[] key) {
        for (int i = 0; i < key.length; ++i) {
            final int n = i;
            key[n] &= (byte)254;
            final int n2 = i;
            key[n2] |= (byte)(0x1 ^ parityChar(key[i]));
        }
    }
    
    private static boolean checkKeyParity(final byte[] key) {
        for (int i = 0; i < key.length; ++i) {
            if ((key[i] & 0x1) == parityChar((byte)(key[i] & 0xFE))) {
                return false;
            }
        }
        return true;
    }
}
