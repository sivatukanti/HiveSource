// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.util.IntegerUtils;
import javax.crypto.Mac;
import com.nimbusds.jose.util.ByteUtils;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.io.IOException;
import com.nimbusds.jose.JOSEException;
import java.io.ByteArrayOutputStream;
import com.nimbusds.jose.util.StandardCharset;
import com.nimbusds.jose.JWEAlgorithm;

class PBKDF2
{
    public static byte[] ZERO_BYTE;
    
    static {
        PBKDF2.ZERO_BYTE = new byte[1];
    }
    
    public static byte[] formatSalt(final JWEAlgorithm alg, final byte[] salt) throws JOSEException {
        final byte[] algBytes = alg.toString().getBytes(StandardCharset.UTF_8);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(algBytes);
            out.write(PBKDF2.ZERO_BYTE);
            out.write(salt);
        }
        catch (IOException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        return out.toByteArray();
    }
    
    public static SecretKey deriveKey(final byte[] password, final byte[] formattedSalt, final int iterationCount, final PRFParams prfParams) throws JOSEException {
        final SecretKey macKey = new SecretKeySpec(password, prfParams.getMACAlgorithm());
        final Mac prf = HMAC.getInitMac(macKey, prfParams.getMacProvider());
        final int hLen = prf.getMacLength();
        final long maxDerivedKeyLength = 4294967295L;
        if (prfParams.getDerivedKeyByteLength() > maxDerivedKeyLength) {
            throw new JOSEException("derived key too long " + prfParams.getDerivedKeyByteLength());
        }
        final int l = (int)Math.ceil(prfParams.getDerivedKeyByteLength() / (double)hLen);
        final int r = prfParams.getDerivedKeyByteLength() - (l - 1) * hLen;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < l; ++i) {
            byte[] block = extractBlock(formattedSalt, iterationCount, i + 1, prf);
            if (i == l - 1) {
                block = ByteUtils.subArray(block, 0, r);
            }
            byteArrayOutputStream.write(block, 0, block.length);
        }
        return new SecretKeySpec(byteArrayOutputStream.toByteArray(), "AES");
    }
    
    private static byte[] extractBlock(final byte[] salt, final int iterationCount, final int blockIndex, final Mac prf) {
        byte[] lastU = null;
        byte[] xorU = null;
        for (int i = 1; i <= iterationCount; ++i) {
            byte[] currentU;
            if (i == 1) {
                final byte[] inputBytes = ByteUtils.concat(new byte[][] { salt, IntegerUtils.toBytes(blockIndex) });
                currentU = (xorU = prf.doFinal(inputBytes));
            }
            else {
                currentU = prf.doFinal(lastU);
                for (int j = 0; j < currentU.length; ++j) {
                    xorU[j] ^= currentU[j];
                }
            }
            lastU = currentU;
        }
        return xorU;
    }
    
    private PBKDF2() {
    }
}
