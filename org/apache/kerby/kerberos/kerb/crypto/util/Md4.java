// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

import java.security.DigestException;
import java.security.MessageDigest;

public class Md4 extends MessageDigest
{
    public static final int BYTE_DIGEST_LENGTH = 16;
    public static final int BYTE_BLOCK_LENGTH = 64;
    private static final int A = 1732584193;
    private static final int B = -271733879;
    private static final int C = -1732584194;
    private static final int D = 271733878;
    private int a;
    private int b;
    private int c;
    private int d;
    private long msgLength;
    private final byte[] buffer;
    
    public Md4() {
        super("MD4");
        this.a = 1732584193;
        this.b = -271733879;
        this.c = -1732584194;
        this.d = 271733878;
        this.buffer = new byte[64];
        this.engineReset();
    }
    
    @Override
    protected int engineGetDigestLength() {
        return 16;
    }
    
    @Override
    protected void engineUpdate(final byte b) {
        final int pos = (int)(this.msgLength % 64L);
        this.buffer[pos] = b;
        ++this.msgLength;
        if (pos == 63) {
            this.process(this.buffer, 0);
        }
    }
    
    @Override
    protected void engineUpdate(final byte[] b, final int offset, final int len) {
        int pos = (int)(this.msgLength % 64L);
        final int nbOfCharsToFillBuf = 64 - pos;
        int blkStart = 0;
        this.msgLength += len;
        if (len >= nbOfCharsToFillBuf) {
            System.arraycopy(b, offset, this.buffer, pos, nbOfCharsToFillBuf);
            this.process(this.buffer, 0);
            for (blkStart = nbOfCharsToFillBuf; blkStart + 64 - 1 < len; blkStart += 64) {
                this.process(b, offset + blkStart);
            }
            pos = 0;
        }
        if (blkStart < len) {
            System.arraycopy(b, offset + blkStart, this.buffer, pos, len - blkStart);
        }
    }
    
    @Override
    protected byte[] engineDigest() {
        final byte[] p = this.pad();
        this.engineUpdate(p, 0, p.length);
        final byte[] digest = { (byte)this.a, (byte)(this.a >>> 8), (byte)(this.a >>> 16), (byte)(this.a >>> 24), (byte)this.b, (byte)(this.b >>> 8), (byte)(this.b >>> 16), (byte)(this.b >>> 24), (byte)this.c, (byte)(this.c >>> 8), (byte)(this.c >>> 16), (byte)(this.c >>> 24), (byte)this.d, (byte)(this.d >>> 8), (byte)(this.d >>> 16), (byte)(this.d >>> 24) };
        this.engineReset();
        return digest;
    }
    
    @Override
    protected int engineDigest(final byte[] buf, final int offset, final int len) throws DigestException {
        if (offset < 0 || offset + len >= buf.length) {
            throw new DigestException("Wrong offset or not enough space to store the digest");
        }
        final int destLength = Math.min(len, 16);
        System.arraycopy(this.engineDigest(), 0, buf, offset, destLength);
        return destLength;
    }
    
    @Override
    protected void engineReset() {
        this.a = 1732584193;
        this.b = -271733879;
        this.c = -1732584194;
        this.d = 271733878;
        this.msgLength = 0L;
    }
    
    private byte[] pad() {
        final int pos = (int)(this.msgLength % 64L);
        final int padLength = (pos < 56) ? (64 - pos) : (128 - pos);
        final byte[] pad = new byte[padLength];
        pad[0] = -128;
        final long bits = this.msgLength << 3;
        int index = padLength - 8;
        for (int i = 0; i < 8; ++i) {
            pad[index++] = (byte)(bits >>> (i << 3));
        }
        return pad;
    }
    
    private void process(final byte[] in, int offset) {
        final int aa = this.a;
        final int bb = this.b;
        final int cc = this.c;
        final int dd = this.d;
        final int[] x = new int[16];
        for (int i = 0; i < 16; ++i) {
            x[i] = ((in[offset++] & 0xFF) | (in[offset++] & 0xFF) << 8 | (in[offset++] & 0xFF) << 16 | (in[offset++] & 0xFF) << 24);
        }
        this.a += ((this.b & this.c) | (~this.b & this.d)) + x[0];
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += ((this.a & this.b) | (~this.a & this.c)) + x[1];
        this.d = (this.d << 7 | this.d >>> 25);
        this.c += ((this.d & this.a) | (~this.d & this.b)) + x[2];
        this.c = (this.c << 11 | this.c >>> 21);
        this.b += ((this.c & this.d) | (~this.c & this.a)) + x[3];
        this.b = (this.b << 19 | this.b >>> 13);
        this.a += ((this.b & this.c) | (~this.b & this.d)) + x[4];
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += ((this.a & this.b) | (~this.a & this.c)) + x[5];
        this.d = (this.d << 7 | this.d >>> 25);
        this.c += ((this.d & this.a) | (~this.d & this.b)) + x[6];
        this.c = (this.c << 11 | this.c >>> 21);
        this.b += ((this.c & this.d) | (~this.c & this.a)) + x[7];
        this.b = (this.b << 19 | this.b >>> 13);
        this.a += ((this.b & this.c) | (~this.b & this.d)) + x[8];
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += ((this.a & this.b) | (~this.a & this.c)) + x[9];
        this.d = (this.d << 7 | this.d >>> 25);
        this.c += ((this.d & this.a) | (~this.d & this.b)) + x[10];
        this.c = (this.c << 11 | this.c >>> 21);
        this.b += ((this.c & this.d) | (~this.c & this.a)) + x[11];
        this.b = (this.b << 19 | this.b >>> 13);
        this.a += ((this.b & this.c) | (~this.b & this.d)) + x[12];
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += ((this.a & this.b) | (~this.a & this.c)) + x[13];
        this.d = (this.d << 7 | this.d >>> 25);
        this.c += ((this.d & this.a) | (~this.d & this.b)) + x[14];
        this.c = (this.c << 11 | this.c >>> 21);
        this.b += ((this.c & this.d) | (~this.c & this.a)) + x[15];
        this.b = (this.b << 19 | this.b >>> 13);
        this.a += ((this.b & (this.c | this.d)) | (this.c & this.d)) + x[0] + 1518500249;
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += ((this.a & (this.b | this.c)) | (this.b & this.c)) + x[4] + 1518500249;
        this.d = (this.d << 5 | this.d >>> 27);
        this.c += ((this.d & (this.a | this.b)) | (this.a & this.b)) + x[8] + 1518500249;
        this.c = (this.c << 9 | this.c >>> 23);
        this.b += ((this.c & (this.d | this.a)) | (this.d & this.a)) + x[12] + 1518500249;
        this.b = (this.b << 13 | this.b >>> 19);
        this.a += ((this.b & (this.c | this.d)) | (this.c & this.d)) + x[1] + 1518500249;
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += ((this.a & (this.b | this.c)) | (this.b & this.c)) + x[5] + 1518500249;
        this.d = (this.d << 5 | this.d >>> 27);
        this.c += ((this.d & (this.a | this.b)) | (this.a & this.b)) + x[9] + 1518500249;
        this.c = (this.c << 9 | this.c >>> 23);
        this.b += ((this.c & (this.d | this.a)) | (this.d & this.a)) + x[13] + 1518500249;
        this.b = (this.b << 13 | this.b >>> 19);
        this.a += ((this.b & (this.c | this.d)) | (this.c & this.d)) + x[2] + 1518500249;
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += ((this.a & (this.b | this.c)) | (this.b & this.c)) + x[6] + 1518500249;
        this.d = (this.d << 5 | this.d >>> 27);
        this.c += ((this.d & (this.a | this.b)) | (this.a & this.b)) + x[10] + 1518500249;
        this.c = (this.c << 9 | this.c >>> 23);
        this.b += ((this.c & (this.d | this.a)) | (this.d & this.a)) + x[14] + 1518500249;
        this.b = (this.b << 13 | this.b >>> 19);
        this.a += ((this.b & (this.c | this.d)) | (this.c & this.d)) + x[3] + 1518500249;
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += ((this.a & (this.b | this.c)) | (this.b & this.c)) + x[7] + 1518500249;
        this.d = (this.d << 5 | this.d >>> 27);
        this.c += ((this.d & (this.a | this.b)) | (this.a & this.b)) + x[11] + 1518500249;
        this.c = (this.c << 9 | this.c >>> 23);
        this.b += ((this.c & (this.d | this.a)) | (this.d & this.a)) + x[15] + 1518500249;
        this.b = (this.b << 13 | this.b >>> 19);
        this.a += (this.b ^ this.c ^ this.d) + x[0] + 1859775393;
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += (this.a ^ this.b ^ this.c) + x[8] + 1859775393;
        this.d = (this.d << 9 | this.d >>> 23);
        this.c += (this.d ^ this.a ^ this.b) + x[4] + 1859775393;
        this.c = (this.c << 11 | this.c >>> 21);
        this.b += (this.c ^ this.d ^ this.a) + x[12] + 1859775393;
        this.b = (this.b << 15 | this.b >>> 17);
        this.a += (this.b ^ this.c ^ this.d) + x[2] + 1859775393;
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += (this.a ^ this.b ^ this.c) + x[10] + 1859775393;
        this.d = (this.d << 9 | this.d >>> 23);
        this.c += (this.d ^ this.a ^ this.b) + x[6] + 1859775393;
        this.c = (this.c << 11 | this.c >>> 21);
        this.b += (this.c ^ this.d ^ this.a) + x[14] + 1859775393;
        this.b = (this.b << 15 | this.b >>> 17);
        this.a += (this.b ^ this.c ^ this.d) + x[1] + 1859775393;
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += (this.a ^ this.b ^ this.c) + x[9] + 1859775393;
        this.d = (this.d << 9 | this.d >>> 23);
        this.c += (this.d ^ this.a ^ this.b) + x[5] + 1859775393;
        this.c = (this.c << 11 | this.c >>> 21);
        this.b += (this.c ^ this.d ^ this.a) + x[13] + 1859775393;
        this.b = (this.b << 15 | this.b >>> 17);
        this.a += (this.b ^ this.c ^ this.d) + x[3] + 1859775393;
        this.a = (this.a << 3 | this.a >>> 29);
        this.d += (this.a ^ this.b ^ this.c) + x[11] + 1859775393;
        this.d = (this.d << 9 | this.d >>> 23);
        this.c += (this.d ^ this.a ^ this.b) + x[7] + 1859775393;
        this.c = (this.c << 11 | this.c >>> 21);
        this.b += (this.c ^ this.d ^ this.a) + x[15] + 1859775393;
        this.b = (this.b << 15 | this.b >>> 17);
        this.a += aa;
        this.b += bb;
        this.c += cc;
        this.d += dd;
    }
}
