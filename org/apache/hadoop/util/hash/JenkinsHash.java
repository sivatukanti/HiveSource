// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.hash;

import java.io.IOException;
import java.io.FileInputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class JenkinsHash extends Hash
{
    private static long INT_MASK;
    private static long BYTE_MASK;
    private static JenkinsHash _instance;
    
    public static Hash getInstance() {
        return JenkinsHash._instance;
    }
    
    private static long rot(final long val, final int pos) {
        return (long)Integer.rotateLeft((int)(val & JenkinsHash.INT_MASK), pos) & JenkinsHash.INT_MASK;
    }
    
    @Override
    public int hash(final byte[] key, final int nbytes, final int initval) {
        int length = nbytes;
        long c;
        long a;
        long b = a = (c = (0xDEADBEEFL + length + initval & JenkinsHash.INT_MASK));
        int offset = 0;
        while (length > 12) {
            a = (a + ((long)key[offset + 0] & JenkinsHash.BYTE_MASK) & JenkinsHash.INT_MASK);
            a = (a + (((long)key[offset + 1] & JenkinsHash.BYTE_MASK) << 8 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            a = (a + (((long)key[offset + 2] & JenkinsHash.BYTE_MASK) << 16 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            a = (a + (((long)key[offset + 3] & JenkinsHash.BYTE_MASK) << 24 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            b = (b + ((long)key[offset + 4] & JenkinsHash.BYTE_MASK) & JenkinsHash.INT_MASK);
            b = (b + (((long)key[offset + 5] & JenkinsHash.BYTE_MASK) << 8 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            b = (b + (((long)key[offset + 6] & JenkinsHash.BYTE_MASK) << 16 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            b = (b + (((long)key[offset + 7] & JenkinsHash.BYTE_MASK) << 24 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            c = (c + ((long)key[offset + 8] & JenkinsHash.BYTE_MASK) & JenkinsHash.INT_MASK);
            c = (c + (((long)key[offset + 9] & JenkinsHash.BYTE_MASK) << 8 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            c = (c + (((long)key[offset + 10] & JenkinsHash.BYTE_MASK) << 16 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            c = (c + (((long)key[offset + 11] & JenkinsHash.BYTE_MASK) << 24 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            a = (a - c & JenkinsHash.INT_MASK);
            a ^= rot(c, 4);
            c = (c + b & JenkinsHash.INT_MASK);
            b = (b - a & JenkinsHash.INT_MASK);
            b ^= rot(a, 6);
            a = (a + c & JenkinsHash.INT_MASK);
            c = (c - b & JenkinsHash.INT_MASK);
            c ^= rot(b, 8);
            b = (b + a & JenkinsHash.INT_MASK);
            a = (a - c & JenkinsHash.INT_MASK);
            a ^= rot(c, 16);
            c = (c + b & JenkinsHash.INT_MASK);
            b = (b - a & JenkinsHash.INT_MASK);
            b ^= rot(a, 19);
            a = (a + c & JenkinsHash.INT_MASK);
            c = (c - b & JenkinsHash.INT_MASK);
            c ^= rot(b, 4);
            b = (b + a & JenkinsHash.INT_MASK);
            offset += 12;
            length -= 12;
        }
        switch (length) {
            case 12: {
                c = (c + (((long)key[offset + 11] & JenkinsHash.BYTE_MASK) << 24 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            }
            case 11: {
                c = (c + (((long)key[offset + 10] & JenkinsHash.BYTE_MASK) << 16 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            }
            case 10: {
                c = (c + (((long)key[offset + 9] & JenkinsHash.BYTE_MASK) << 8 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            }
            case 9: {
                c = (c + ((long)key[offset + 8] & JenkinsHash.BYTE_MASK) & JenkinsHash.INT_MASK);
            }
            case 8: {
                b = (b + (((long)key[offset + 7] & JenkinsHash.BYTE_MASK) << 24 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            }
            case 7: {
                b = (b + (((long)key[offset + 6] & JenkinsHash.BYTE_MASK) << 16 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            }
            case 6: {
                b = (b + (((long)key[offset + 5] & JenkinsHash.BYTE_MASK) << 8 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            }
            case 5: {
                b = (b + ((long)key[offset + 4] & JenkinsHash.BYTE_MASK) & JenkinsHash.INT_MASK);
            }
            case 4: {
                a = (a + (((long)key[offset + 3] & JenkinsHash.BYTE_MASK) << 24 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            }
            case 3: {
                a = (a + (((long)key[offset + 2] & JenkinsHash.BYTE_MASK) << 16 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            }
            case 2: {
                a = (a + (((long)key[offset + 1] & JenkinsHash.BYTE_MASK) << 8 & JenkinsHash.INT_MASK) & JenkinsHash.INT_MASK);
            }
            case 1: {
                a = (a + ((long)key[offset + 0] & JenkinsHash.BYTE_MASK) & JenkinsHash.INT_MASK);
                break;
            }
            case 0: {
                return (int)(c & JenkinsHash.INT_MASK);
            }
        }
        c ^= b;
        c = (c - rot(b, 14) & JenkinsHash.INT_MASK);
        a ^= c;
        a = (a - rot(c, 11) & JenkinsHash.INT_MASK);
        b ^= a;
        b = (b - rot(a, 25) & JenkinsHash.INT_MASK);
        c ^= b;
        c = (c - rot(b, 16) & JenkinsHash.INT_MASK);
        a ^= c;
        a = (a - rot(c, 4) & JenkinsHash.INT_MASK);
        b ^= a;
        b = (b - rot(a, 14) & JenkinsHash.INT_MASK);
        c ^= b;
        c = (c - rot(b, 24) & JenkinsHash.INT_MASK);
        return (int)(c & JenkinsHash.INT_MASK);
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: JenkinsHash filename");
            System.exit(-1);
        }
        try (final FileInputStream in = new FileInputStream(args[0])) {
            final byte[] bytes = new byte[512];
            int value = 0;
            final JenkinsHash hash = new JenkinsHash();
            for (int length = in.read(bytes); length > 0; length = in.read(bytes)) {
                value = hash.hash(bytes, length, value);
            }
            System.out.println(Math.abs(value));
        }
    }
    
    static {
        JenkinsHash.INT_MASK = 4294967295L;
        JenkinsHash.BYTE_MASK = 255L;
        JenkinsHash._instance = new JenkinsHash();
    }
}
