// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

import java.util.Arrays;

public class Nfold
{
    public static byte[] nfold(final byte[] inBytes, final int size) {
        final int inBytesNum = inBytes.length;
        int a;
        final int outBytesNum = a = size;
        int c;
        for (int b = inBytesNum; b != 0; b = a % b, a = c) {
            c = b;
        }
        final int lcm = outBytesNum * inBytesNum / a;
        final byte[] outBytes = new byte[outBytesNum];
        Arrays.fill(outBytes, (byte)0);
        int tmpByte = 0;
        for (int i = lcm - 1; i >= 0; --i) {
            int tmp = (inBytesNum << 3) - 1;
            tmp += ((inBytesNum << 3) + 13) * (i / inBytesNum);
            tmp += inBytesNum - i % inBytesNum << 3;
            final int msbit = tmp % (inBytesNum << 3);
            tmp = (((inBytes[(inBytesNum - 1 - (msbit >>> 3)) % inBytesNum] & 0xFF) << 8 | (inBytes[(inBytesNum - (msbit >>> 3)) % inBytesNum] & 0xFF)) >>> (msbit & 0x7) + 1 & 0xFF);
            tmpByte += tmp;
            tmp = (outBytes[i % outBytesNum] & 0xFF);
            tmpByte += tmp;
            outBytes[i % outBytesNum] = (byte)(tmpByte & 0xFF);
            tmpByte >>>= 8;
        }
        if (tmpByte != 0) {
            for (int i = outBytesNum - 1; i >= 0; --i) {
                tmpByte += (outBytes[i] & 0xFF);
                outBytes[i] = (byte)(tmpByte & 0xFF);
                tmpByte >>>= 8;
            }
        }
        return outBytes;
    }
}
