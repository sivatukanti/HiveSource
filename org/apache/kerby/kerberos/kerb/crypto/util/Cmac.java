// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

import java.util.Arrays;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public class Cmac
{
    private static byte[] constRb;
    
    public static byte[] cmac(final EncryptProvider encProvider, final byte[] key, final byte[] data, final int outputSize) throws KrbException {
        return cmac(encProvider, key, data, 0, data.length, outputSize);
    }
    
    public static byte[] cmac(final EncryptProvider encProvider, final byte[] key, final byte[] data, final int start, final int len, final int outputSize) throws KrbException {
        final byte[] hash = cmac(encProvider, key, data, start, len);
        if (hash.length > outputSize) {
            final byte[] output = new byte[outputSize];
            System.arraycopy(hash, 0, output, 0, outputSize);
            return output;
        }
        return hash;
    }
    
    public static byte[] cmac(final EncryptProvider encProvider, final byte[] key, final byte[] data) throws KrbException {
        return cmac(encProvider, key, data, 0, data.length);
    }
    
    public static byte[] cmac(final EncryptProvider encProvider, final byte[] key, final byte[] data, final int start, final int len) throws KrbException {
        final int blockSize = encProvider.blockSize();
        final byte[] y = new byte[blockSize];
        final byte[] mLast = new byte[blockSize];
        final byte[] padded = new byte[blockSize];
        final byte[] k1 = new byte[blockSize];
        final byte[] k2 = new byte[blockSize];
        makeSubkey(encProvider, key, k1, k2);
        int n = (len + blockSize - 1) / blockSize;
        boolean lastIsComplete;
        if (n == 0) {
            n = 1;
            lastIsComplete = false;
        }
        else {
            lastIsComplete = (len % blockSize == 0);
        }
        final byte[] cipherState = new byte[blockSize];
        final byte[] cipher = new byte[blockSize];
        for (int i = 0; i < n - 1; ++i) {
            System.arraycopy(data, i * blockSize, cipher, 0, blockSize);
            encryptBlock(encProvider, key, cipherState, cipher);
            System.arraycopy(cipher, 0, cipherState, 0, blockSize);
        }
        System.arraycopy(cipher, 0, y, 0, blockSize);
        final int lastPos = (n - 1) * blockSize;
        final int lastLen = lastIsComplete ? blockSize : (len % blockSize);
        final byte[] lastBlock = new byte[lastLen];
        System.arraycopy(data, lastPos, lastBlock, 0, lastLen);
        if (lastIsComplete) {
            BytesUtil.xor(lastBlock, k1, mLast);
        }
        else {
            padding(lastBlock, padded);
            BytesUtil.xor(padded, k2, mLast);
        }
        encryptBlock(encProvider, key, cipherState, mLast);
        return mLast;
    }
    
    private static void makeSubkey(final EncryptProvider encProvider, final byte[] key, final byte[] k1, final byte[] k2) throws KrbException {
        final byte[] l = new byte[k1.length];
        Arrays.fill(l, (byte)0);
        encryptBlock(encProvider, key, null, l);
        if ((l[0] & 0x80) == 0x0) {
            leftShiftByOne(l, k1);
        }
        else {
            final byte[] tmp = new byte[k1.length];
            leftShiftByOne(l, tmp);
            BytesUtil.xor(tmp, Cmac.constRb, k1);
        }
        if ((k1[0] & 0x80) == 0x0) {
            leftShiftByOne(k1, k2);
        }
        else {
            final byte[] tmp = new byte[k1.length];
            leftShiftByOne(k1, tmp);
            BytesUtil.xor(tmp, Cmac.constRb, k2);
        }
    }
    
    private static void encryptBlock(final EncryptProvider encProvider, final byte[] key, byte[] cipherState, final byte[] block) throws KrbException {
        if (cipherState == null) {
            cipherState = new byte[encProvider.blockSize()];
        }
        if (encProvider.supportCbcMac()) {
            encProvider.cbcMac(key, cipherState, block);
        }
        else {
            encProvider.encrypt(key, cipherState, block);
        }
    }
    
    private static void leftShiftByOne(final byte[] input, final byte[] output) {
        byte overflow = 0;
        for (int i = input.length - 1; i >= 0; --i) {
            output[i] = (byte)(input[i] << 1);
            final int n = i;
            output[n] |= overflow;
            overflow = (byte)(((input[i] & 0x80) != 0x0) ? 1 : 0);
        }
    }
    
    private static void padding(final byte[] data, final byte[] padded) {
        final int len = data.length;
        System.arraycopy(data, 0, padded, 0, len);
        padded[len] = -128;
        for (int i = len + 1; i < padded.length; ++i) {
            padded[i] = 0;
        }
    }
    
    static {
        Cmac.constRb = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -121 };
    }
}
