// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal.jzlib;

final class Adler32
{
    private static final int BASE = 65521;
    private static final int NMAX = 5552;
    
    static long adler32(final long adler, final byte[] buf, int index, int len) {
        if (buf == null) {
            return 1L;
        }
        long s1 = adler & 0xFFFFL;
        long s2 = adler >> 16 & 0xFFFFL;
        while (len > 0) {
            int k = (len < 5552) ? len : 5552;
            len -= k;
            while (k >= 16) {
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                s1 += (buf[index++] & 0xFF);
                s2 += s1;
                k -= 16;
            }
            if (k != 0) {
                do {
                    s1 += (buf[index++] & 0xFF);
                    s2 += s1;
                } while (--k != 0);
            }
            s1 %= 65521L;
            s2 %= 65521L;
        }
        return s2 << 16 | s1;
    }
    
    private Adler32() {
    }
}
