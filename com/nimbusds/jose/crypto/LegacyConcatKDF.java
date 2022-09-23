// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.io.IOException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.IntegerUtils;
import java.io.ByteArrayOutputStream;
import com.nimbusds.jose.EncryptionMethod;
import javax.crypto.SecretKey;

class LegacyConcatKDF
{
    private static final byte[] ONE_BYTES;
    private static final byte[] ZERO_BYTES;
    private static final byte[] ENCRYPTION_BYTES;
    private static final byte[] INTEGRITY_BYTES;
    
    static {
        ONE_BYTES = new byte[] { 0, 0, 0, 1 };
        ZERO_BYTES = new byte[4];
        ENCRYPTION_BYTES = new byte[] { 69, 110, 99, 114, 121, 112, 116, 105, 111, 110 };
        INTEGRITY_BYTES = new byte[] { 73, 110, 116, 101, 103, 114, 105, 116, 121 };
    }
    
    public static SecretKey generateCEK(final SecretKey key, final EncryptionMethod enc, final byte[] epu, final byte[] epv) throws JOSEException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int hashBitLength;
        try {
            baos.write(LegacyConcatKDF.ONE_BYTES);
            final byte[] cmkBytes = key.getEncoded();
            baos.write(cmkBytes);
            final int cmkBitLength = hashBitLength = cmkBytes.length * 8;
            final int cekBitLength = cmkBitLength / 2;
            final byte[] cekBitLengthBytes = IntegerUtils.toBytes(cekBitLength);
            baos.write(cekBitLengthBytes);
            final byte[] encBytes = enc.toString().getBytes();
            baos.write(encBytes);
            if (epu != null) {
                baos.write(IntegerUtils.toBytes(epu.length));
                baos.write(epu);
            }
            else {
                baos.write(LegacyConcatKDF.ZERO_BYTES);
            }
            if (epv != null) {
                baos.write(IntegerUtils.toBytes(epv.length));
                baos.write(epv);
            }
            else {
                baos.write(LegacyConcatKDF.ZERO_BYTES);
            }
            baos.write(LegacyConcatKDF.ENCRYPTION_BYTES);
        }
        catch (IOException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        final byte[] hashInput = baos.toByteArray();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-" + hashBitLength);
        }
        catch (NoSuchAlgorithmException e2) {
            throw new JOSEException(e2.getMessage(), e2);
        }
        final byte[] hashOutput = md.digest(hashInput);
        final byte[] cekBytes = new byte[hashOutput.length / 2];
        System.arraycopy(hashOutput, 0, cekBytes, 0, cekBytes.length);
        return new SecretKeySpec(cekBytes, "AES");
    }
    
    public static SecretKey generateCIK(final SecretKey key, final EncryptionMethod enc, final byte[] epu, final byte[] epv) throws JOSEException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int cikBitLength;
        int hashBitLength;
        try {
            baos.write(LegacyConcatKDF.ONE_BYTES);
            final byte[] cmkBytes = key.getEncoded();
            baos.write(cmkBytes);
            hashBitLength = (cikBitLength = cmkBytes.length * 8);
            final byte[] cikBitLengthBytes = IntegerUtils.toBytes(cikBitLength);
            baos.write(cikBitLengthBytes);
            final byte[] encBytes = enc.toString().getBytes();
            baos.write(encBytes);
            if (epu != null) {
                baos.write(IntegerUtils.toBytes(epu.length));
                baos.write(epu);
            }
            else {
                baos.write(LegacyConcatKDF.ZERO_BYTES);
            }
            if (epv != null) {
                baos.write(IntegerUtils.toBytes(epv.length));
                baos.write(epv);
            }
            else {
                baos.write(LegacyConcatKDF.ZERO_BYTES);
            }
            baos.write(LegacyConcatKDF.INTEGRITY_BYTES);
        }
        catch (IOException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        final byte[] hashInput = baos.toByteArray();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-" + hashBitLength);
        }
        catch (NoSuchAlgorithmException e2) {
            throw new JOSEException(e2.getMessage(), e2);
        }
        return new SecretKeySpec(md.digest(hashInput), "HMACSHA" + cikBitLength);
    }
    
    private LegacyConcatKDF() {
    }
}
