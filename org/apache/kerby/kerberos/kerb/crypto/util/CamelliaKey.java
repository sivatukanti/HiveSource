// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

public class CamelliaKey
{
    static final byte[] SBOX1;
    private static final int[] SIGMA;
    protected int[] subkey;
    protected int[] kw;
    protected int[] ke;
    private int keySize;
    
    public CamelliaKey(final byte[] key, final boolean isEncrypt) {
        this.subkey = new int[96];
        this.kw = new int[8];
        this.ke = new int[12];
        this.init(key, isEncrypt);
    }
    
    private static int rightRotate(final int x, final int s) {
        return (x >>> s) + (x << 32 - s);
    }
    
    private static int leftRotate(final int x, final int s) {
        return (x << s) + (x >>> 32 - s);
    }
    
    private static void roldq(final int rot, final int[] ki, final int ioff, final int[] ko, final int ooff) {
        ko[0 + ooff] = (ki[0 + ioff] << rot | ki[1 + ioff] >>> 32 - rot);
        ko[1 + ooff] = (ki[1 + ioff] << rot | ki[2 + ioff] >>> 32 - rot);
        ko[2 + ooff] = (ki[2 + ioff] << rot | ki[3 + ioff] >>> 32 - rot);
        ko[3 + ooff] = (ki[3 + ioff] << rot | ki[0 + ioff] >>> 32 - rot);
        ki[0 + ioff] = ko[0 + ooff];
        ki[1 + ioff] = ko[1 + ooff];
        ki[2 + ioff] = ko[2 + ooff];
        ki[3 + ioff] = ko[3 + ooff];
    }
    
    private static void decroldq(final int rot, final int[] ki, final int ioff, final int[] ko, final int ooff) {
        ko[2 + ooff] = (ki[0 + ioff] << rot | ki[1 + ioff] >>> 32 - rot);
        ko[3 + ooff] = (ki[1 + ioff] << rot | ki[2 + ioff] >>> 32 - rot);
        ko[0 + ooff] = (ki[2 + ioff] << rot | ki[3 + ioff] >>> 32 - rot);
        ko[1 + ooff] = (ki[3 + ioff] << rot | ki[0 + ioff] >>> 32 - rot);
        ki[0 + ioff] = ko[2 + ooff];
        ki[1 + ioff] = ko[3 + ooff];
        ki[2 + ioff] = ko[0 + ooff];
        ki[3 + ioff] = ko[1 + ooff];
    }
    
    private static void roldqo32(final int rot, final int[] ki, final int ioff, final int[] ko, final int ooff) {
        ko[0 + ooff] = (ki[1 + ioff] << rot - 32 | ki[2 + ioff] >>> 64 - rot);
        ko[1 + ooff] = (ki[2 + ioff] << rot - 32 | ki[3 + ioff] >>> 64 - rot);
        ko[2 + ooff] = (ki[3 + ioff] << rot - 32 | ki[0 + ioff] >>> 64 - rot);
        ko[3 + ooff] = (ki[0 + ioff] << rot - 32 | ki[1 + ioff] >>> 64 - rot);
        ki[0 + ioff] = ko[0 + ooff];
        ki[1 + ioff] = ko[1 + ooff];
        ki[2 + ioff] = ko[2 + ooff];
        ki[3 + ioff] = ko[3 + ooff];
    }
    
    private static void decroldqo32(final int rot, final int[] ki, final int ioff, final int[] ko, final int ooff) {
        ko[2 + ooff] = (ki[1 + ioff] << rot - 32 | ki[2 + ioff] >>> 64 - rot);
        ko[3 + ooff] = (ki[2 + ioff] << rot - 32 | ki[3 + ioff] >>> 64 - rot);
        ko[0 + ooff] = (ki[3 + ioff] << rot - 32 | ki[0 + ioff] >>> 64 - rot);
        ko[1 + ooff] = (ki[0 + ioff] << rot - 32 | ki[1 + ioff] >>> 64 - rot);
        ki[0 + ioff] = ko[2 + ooff];
        ki[1 + ioff] = ko[3 + ooff];
        ki[2 + ioff] = ko[0 + ooff];
        ki[3 + ioff] = ko[1 + ooff];
    }
    
    protected boolean is128() {
        return this.keySize == 16;
    }
    
    private byte lRot8(final byte v, final int rot) {
        return (byte)(v << rot | (v & 0xFF) >>> 8 - rot);
    }
    
    private int sbox2(final int x) {
        return this.lRot8(CamelliaKey.SBOX1[x], 1) & 0xFF;
    }
    
    private int sbox3(final int x) {
        return this.lRot8(CamelliaKey.SBOX1[x], 7) & 0xFF;
    }
    
    private int sbox4(final int x) {
        return CamelliaKey.SBOX1[this.lRot8((byte)x, 1) & 0xFF] & 0xFF;
    }
    
    protected void fls(final int[] s, final int[] fkey, final int keyoff) {
        final int n = 1;
        s[n] ^= leftRotate(s[0] & fkey[0 + keyoff], 1);
        final int n2 = 0;
        s[n2] ^= (fkey[1 + keyoff] | s[1]);
        final int n3 = 2;
        s[n3] ^= (fkey[3 + keyoff] | s[3]);
        final int n4 = 3;
        s[n4] ^= leftRotate(fkey[2 + keyoff] & s[2], 1);
    }
    
    protected void f2(final int[] s, final int[] skey, final int keyoff) {
        int t1 = s[0] ^ skey[0 + keyoff];
        int u = this.sbox4(t1 & 0xFF);
        u |= this.sbox3(t1 >>> 8 & 0xFF) << 8;
        u |= this.sbox2(t1 >>> 16 & 0xFF) << 16;
        u |= (CamelliaKey.SBOX1[t1 >>> 24 & 0xFF] & 0xFF) << 24;
        int t2 = s[1] ^ skey[1 + keyoff];
        int v = CamelliaKey.SBOX1[t2 & 0xFF] & 0xFF;
        v |= this.sbox4(t2 >>> 8 & 0xFF) << 8;
        v |= this.sbox3(t2 >>> 16 & 0xFF) << 16;
        v |= this.sbox2(t2 >>> 24 & 0xFF) << 24;
        v = leftRotate(v, 8);
        u ^= v;
        v = (leftRotate(v, 8) ^ u);
        u = (rightRotate(u, 8) ^ v);
        final int n = 2;
        s[n] ^= (leftRotate(v, 16) ^ u);
        final int n2 = 3;
        s[n2] ^= leftRotate(u, 8);
        t1 = (s[2] ^ skey[2 + keyoff]);
        u = this.sbox4(t1 & 0xFF);
        u |= this.sbox3(t1 >>> 8 & 0xFF) << 8;
        u |= this.sbox2(t1 >>> 16 & 0xFF) << 16;
        u |= (CamelliaKey.SBOX1[t1 >>> 24 & 0xFF] & 0xFF) << 24;
        t2 = (s[3] ^ skey[3 + keyoff]);
        v = (CamelliaKey.SBOX1[t2 & 0xFF] & 0xFF);
        v |= this.sbox4(t2 >>> 8 & 0xFF) << 8;
        v |= this.sbox3(t2 >>> 16 & 0xFF) << 16;
        v |= this.sbox2(t2 >>> 24 & 0xFF) << 24;
        v = leftRotate(v, 8);
        u ^= v;
        v = (leftRotate(v, 8) ^ u);
        u = (rightRotate(u, 8) ^ v);
        final int n3 = 0;
        s[n3] ^= (leftRotate(v, 16) ^ u);
        final int n4 = 1;
        s[n4] ^= leftRotate(u, 8);
    }
    
    private void init(final byte[] key, final boolean isEncrypt) {
        this.keySize = key.length;
        final int[] k = new int[8];
        final int[] ka = new int[4];
        final int[] kb = new int[4];
        final int[] t = new int[4];
        switch (key.length) {
            case 16: {
                k[0] = BytesUtil.bytes2int(key, 0, true);
                k[1] = BytesUtil.bytes2int(key, 4, true);
                k[2] = BytesUtil.bytes2int(key, 8, true);
                k[3] = BytesUtil.bytes2int(key, 12, true);
                final int[] array = k;
                final int n = 4;
                final int[] array2 = k;
                final int n2 = 5;
                final int[] array3 = k;
                final int n3 = 6;
                final int[] array4 = k;
                final int n4 = 7;
                final int n5 = 0;
                array3[n3] = (array4[n4] = n5);
                array[n] = (array2[n2] = n5);
                break;
            }
            case 24: {
                k[0] = BytesUtil.bytes2int(key, 0, true);
                k[1] = BytesUtil.bytes2int(key, 4, true);
                k[2] = BytesUtil.bytes2int(key, 8, true);
                k[3] = BytesUtil.bytes2int(key, 12, true);
                k[4] = BytesUtil.bytes2int(key, 16, true);
                k[5] = BytesUtil.bytes2int(key, 20, true);
                k[6] = ~k[4];
                k[7] = ~k[5];
                break;
            }
            case 32: {
                k[0] = BytesUtil.bytes2int(key, 0, true);
                k[1] = BytesUtil.bytes2int(key, 4, true);
                k[2] = BytesUtil.bytes2int(key, 8, true);
                k[3] = BytesUtil.bytes2int(key, 12, true);
                k[4] = BytesUtil.bytes2int(key, 16, true);
                k[5] = BytesUtil.bytes2int(key, 20, true);
                k[6] = BytesUtil.bytes2int(key, 24, true);
                k[7] = BytesUtil.bytes2int(key, 28, true);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid key size, only support 16/24/32 bytes");
            }
        }
        for (int i = 0; i < 4; ++i) {
            ka[i] = (k[i] ^ k[i + 4]);
        }
        this.f2(ka, CamelliaKey.SIGMA, 0);
        for (int i = 0; i < 4; ++i) {
            final int[] array5 = ka;
            final int n6 = i;
            array5[n6] ^= k[i];
        }
        this.f2(ka, CamelliaKey.SIGMA, 4);
        if (this.keySize == 16) {
            if (isEncrypt) {
                this.kw[0] = k[0];
                this.kw[1] = k[1];
                this.kw[2] = k[2];
                this.kw[3] = k[3];
                roldq(15, k, 0, this.subkey, 4);
                roldq(30, k, 0, this.subkey, 12);
                roldq(15, k, 0, t, 0);
                this.subkey[18] = t[2];
                this.subkey[19] = t[3];
                roldq(17, k, 0, this.ke, 4);
                roldq(17, k, 0, this.subkey, 24);
                roldq(17, k, 0, this.subkey, 32);
                this.subkey[0] = ka[0];
                this.subkey[1] = ka[1];
                this.subkey[2] = ka[2];
                this.subkey[3] = ka[3];
                roldq(15, ka, 0, this.subkey, 8);
                roldq(15, ka, 0, this.ke, 0);
                roldq(15, ka, 0, t, 0);
                this.subkey[16] = t[0];
                this.subkey[17] = t[1];
                roldq(15, ka, 0, this.subkey, 20);
                roldqo32(34, ka, 0, this.subkey, 28);
                roldq(17, ka, 0, this.kw, 4);
            }
            else {
                this.kw[4] = k[0];
                this.kw[5] = k[1];
                this.kw[6] = k[2];
                this.kw[7] = k[3];
                decroldq(15, k, 0, this.subkey, 28);
                decroldq(30, k, 0, this.subkey, 20);
                decroldq(15, k, 0, t, 0);
                this.subkey[16] = t[0];
                this.subkey[17] = t[1];
                decroldq(17, k, 0, this.ke, 0);
                decroldq(17, k, 0, this.subkey, 8);
                decroldq(17, k, 0, this.subkey, 0);
                this.subkey[34] = ka[0];
                this.subkey[35] = ka[1];
                this.subkey[32] = ka[2];
                this.subkey[33] = ka[3];
                decroldq(15, ka, 0, this.subkey, 24);
                decroldq(15, ka, 0, this.ke, 4);
                decroldq(15, ka, 0, t, 0);
                this.subkey[18] = t[2];
                this.subkey[19] = t[3];
                decroldq(15, ka, 0, this.subkey, 12);
                decroldqo32(34, ka, 0, this.subkey, 4);
                roldq(17, ka, 0, this.kw, 0);
            }
        }
        else {
            for (int i = 0; i < 4; ++i) {
                kb[i] = (ka[i] ^ k[i + 4]);
            }
            this.f2(kb, CamelliaKey.SIGMA, 8);
            if (isEncrypt) {
                this.kw[0] = k[0];
                this.kw[1] = k[1];
                this.kw[2] = k[2];
                this.kw[3] = k[3];
                roldqo32(45, k, 0, this.subkey, 16);
                roldq(15, k, 0, this.ke, 4);
                roldq(17, k, 0, this.subkey, 32);
                roldqo32(34, k, 0, this.subkey, 44);
                roldq(15, k, 4, this.subkey, 4);
                roldq(15, k, 4, this.ke, 0);
                roldq(30, k, 4, this.subkey, 24);
                roldqo32(34, k, 4, this.subkey, 36);
                roldq(15, ka, 0, this.subkey, 8);
                roldq(30, ka, 0, this.subkey, 20);
                this.ke[8] = ka[1];
                this.ke[9] = ka[2];
                this.ke[10] = ka[3];
                this.ke[11] = ka[0];
                roldqo32(49, ka, 0, this.subkey, 40);
                this.subkey[0] = kb[0];
                this.subkey[1] = kb[1];
                this.subkey[2] = kb[2];
                this.subkey[3] = kb[3];
                roldq(30, kb, 0, this.subkey, 12);
                roldq(30, kb, 0, this.subkey, 28);
                roldqo32(51, kb, 0, this.kw, 4);
            }
            else {
                this.kw[4] = k[0];
                this.kw[5] = k[1];
                this.kw[6] = k[2];
                this.kw[7] = k[3];
                decroldqo32(45, k, 0, this.subkey, 28);
                decroldq(15, k, 0, this.ke, 4);
                decroldq(17, k, 0, this.subkey, 12);
                decroldqo32(34, k, 0, this.subkey, 0);
                decroldq(15, k, 4, this.subkey, 40);
                decroldq(15, k, 4, this.ke, 8);
                decroldq(30, k, 4, this.subkey, 20);
                decroldqo32(34, k, 4, this.subkey, 8);
                decroldq(15, ka, 0, this.subkey, 36);
                decroldq(30, ka, 0, this.subkey, 24);
                this.ke[2] = ka[1];
                this.ke[3] = ka[2];
                this.ke[0] = ka[3];
                this.ke[1] = ka[0];
                decroldqo32(49, ka, 0, this.subkey, 4);
                this.subkey[46] = kb[0];
                this.subkey[47] = kb[1];
                this.subkey[44] = kb[2];
                this.subkey[45] = kb[3];
                decroldq(30, kb, 0, this.subkey, 32);
                decroldq(30, kb, 0, this.subkey, 16);
                roldqo32(51, kb, 0, this.kw, 0);
            }
        }
    }
    
    static {
        SBOX1 = new byte[] { 112, -126, 44, -20, -77, 39, -64, -27, -28, -123, 87, 53, -22, 12, -82, 65, 35, -17, 107, -109, 69, 25, -91, 33, -19, 14, 79, 78, 29, 101, -110, -67, -122, -72, -81, -113, 124, -21, 31, -50, 62, 48, -36, 95, 94, -59, 11, 26, -90, -31, 57, -54, -43, 71, 93, 61, -39, 1, 90, -42, 81, 86, 108, 77, -117, 13, -102, 102, -5, -52, -80, 45, 116, 18, 43, 32, -16, -79, -124, -103, -33, 76, -53, -62, 52, 126, 118, 5, 109, -73, -87, 49, -47, 23, 4, -41, 20, 88, 58, 97, -34, 27, 17, 28, 50, 15, -100, 22, 83, 24, -14, 34, -2, 68, -49, -78, -61, -75, 122, -111, 36, 8, -24, -88, 96, -4, 105, 80, -86, -48, -96, 125, -95, -119, 98, -105, 84, 91, 30, -107, -32, -1, 100, -46, 16, -60, 0, 72, -93, -9, 117, -37, -118, 3, -26, -38, 9, 63, -35, -108, -121, 92, -125, 2, -51, 74, -112, 51, 115, 103, -10, -13, -99, 127, -65, -30, 82, -101, -40, 38, -56, 55, -58, 59, -127, -106, 111, 75, 19, -66, 99, 46, -23, 121, -89, -116, -97, 110, -68, -114, 41, -11, -7, -74, 47, -3, -76, 89, 120, -104, 6, 106, -25, 70, 113, -70, -44, 37, -85, 66, -120, -94, -115, -6, 114, 7, -71, 85, -8, -18, -84, 10, 54, 73, 42, 104, 60, 56, -15, -92, 64, 40, -45, 123, -69, -55, 67, -63, 21, -29, -83, -12, 119, -57, -128, -98 };
        SIGMA = new int[] { -1600231809, 1003262091, -1233459112, 1286239154, -957401297, -380665154, 1426019237, -237801700, 283453434, -563598051, -1336506174, -1276722691 };
    }
}
