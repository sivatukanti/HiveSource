// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.io.InputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import java.security.MessageDigest;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class MD5Hash implements WritableComparable<MD5Hash>
{
    public static final int MD5_LEN = 16;
    private static final ThreadLocal<MessageDigest> DIGESTER_FACTORY;
    private byte[] digest;
    private static final char[] HEX_DIGITS;
    
    public MD5Hash() {
        this.digest = new byte[16];
    }
    
    public MD5Hash(final String hex) {
        this.setDigest(hex);
    }
    
    public MD5Hash(final byte[] digest) {
        if (digest.length != 16) {
            throw new IllegalArgumentException("Wrong length: " + digest.length);
        }
        this.digest = digest;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        in.readFully(this.digest);
    }
    
    public static MD5Hash read(final DataInput in) throws IOException {
        final MD5Hash result = new MD5Hash();
        result.readFields(in);
        return result;
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.write(this.digest);
    }
    
    public void set(final MD5Hash that) {
        System.arraycopy(that.digest, 0, this.digest, 0, 16);
    }
    
    public byte[] getDigest() {
        return this.digest;
    }
    
    public static MD5Hash digest(final byte[] data) {
        return digest(data, 0, data.length);
    }
    
    public static MessageDigest getDigester() {
        final MessageDigest digester = MD5Hash.DIGESTER_FACTORY.get();
        digester.reset();
        return digester;
    }
    
    public static MD5Hash digest(final InputStream in) throws IOException {
        final byte[] buffer = new byte[4096];
        final MessageDigest digester = getDigester();
        int n;
        while ((n = in.read(buffer)) != -1) {
            digester.update(buffer, 0, n);
        }
        return new MD5Hash(digester.digest());
    }
    
    public static MD5Hash digest(final byte[] data, final int start, final int len) {
        final MessageDigest digester = getDigester();
        digester.update(data, start, len);
        final byte[] digest = digester.digest();
        return new MD5Hash(digest);
    }
    
    public static MD5Hash digest(final byte[][] dataArr, final int start, final int len) {
        final MessageDigest digester = getDigester();
        for (final byte[] data : dataArr) {
            digester.update(data, start, len);
        }
        final byte[] digest = digester.digest();
        return new MD5Hash(digest);
    }
    
    public static MD5Hash digest(final String string) {
        return digest(UTF8.getBytes(string));
    }
    
    public static MD5Hash digest(final UTF8 utf8) {
        return digest(utf8.getBytes(), 0, utf8.getLength());
    }
    
    public long halfDigest() {
        long value = 0L;
        for (int i = 0; i < 8; ++i) {
            value |= ((long)this.digest[i] & 0xFFL) << 8 * (7 - i);
        }
        return value;
    }
    
    public int quarterDigest() {
        int value = 0;
        for (int i = 0; i < 4; ++i) {
            value |= (this.digest[i] & 0xFF) << 8 * (3 - i);
        }
        return value;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MD5Hash)) {
            return false;
        }
        final MD5Hash other = (MD5Hash)o;
        return Arrays.equals(this.digest, other.digest);
    }
    
    @Override
    public int hashCode() {
        return this.quarterDigest();
    }
    
    @Override
    public int compareTo(final MD5Hash that) {
        return WritableComparator.compareBytes(this.digest, 0, 16, that.digest, 0, 16);
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(32);
        for (int i = 0; i < 16; ++i) {
            final int b = this.digest[i];
            buf.append(MD5Hash.HEX_DIGITS[b >> 4 & 0xF]);
            buf.append(MD5Hash.HEX_DIGITS[b & 0xF]);
        }
        return buf.toString();
    }
    
    public void setDigest(final String hex) {
        if (hex.length() != 32) {
            throw new IllegalArgumentException("Wrong length: " + hex.length());
        }
        final byte[] digest = new byte[16];
        for (int i = 0; i < 16; ++i) {
            final int j = i << 1;
            digest[i] = (byte)(charToNibble(hex.charAt(j)) << 4 | charToNibble(hex.charAt(j + 1)));
        }
        this.digest = digest;
    }
    
    private static final int charToNibble(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return 10 + (c - 'a');
        }
        if (c >= 'A' && c <= 'F') {
            return 10 + (c - 'A');
        }
        throw new RuntimeException("Not a hex character: " + c);
    }
    
    static {
        DIGESTER_FACTORY = new ThreadLocal<MessageDigest>() {
            @Override
            protected MessageDigest initialValue() {
                try {
                    return MessageDigest.getInstance("MD5");
                }
                catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        WritableComparator.define(MD5Hash.class, new Comparator());
        HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(MD5Hash.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            return WritableComparator.compareBytes(b1, s1, 16, b2, s2, 16);
        }
    }
}
