// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.StandardCharset;
import java.security.Provider;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.IntegerUtils;
import com.nimbusds.jose.util.ByteUtils;
import java.io.ByteArrayOutputStream;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.jca.JCAAware;

@ThreadSafe
class ConcatKDF implements JCAAware<JCAContext>
{
    private final String jcaHashAlg;
    private final JCAContext jcaContext;
    
    public ConcatKDF(final String jcaHashAlg) {
        this.jcaContext = new JCAContext();
        if (jcaHashAlg == null) {
            throw new IllegalArgumentException("The JCA hash algorithm must not be null");
        }
        this.jcaHashAlg = jcaHashAlg;
    }
    
    public String getHashAlgorithm() {
        return this.jcaHashAlg;
    }
    
    @Override
    public JCAContext getJCAContext() {
        return this.jcaContext;
    }
    
    public SecretKey deriveKey(final SecretKey sharedSecret, final int keyLengthBits, final byte[] otherInfo) throws JOSEException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final MessageDigest md = this.getMessageDigest();
        for (int i = 1; i <= computeDigestCycles(ByteUtils.safeBitLength(md.getDigestLength()), keyLengthBits); ++i) {
            final byte[] counterBytes = IntegerUtils.toBytes(i);
            md.update(counterBytes);
            md.update(sharedSecret.getEncoded());
            if (otherInfo != null) {
                md.update(otherInfo);
            }
            try {
                baos.write(md.digest());
            }
            catch (IOException e) {
                throw new JOSEException("Couldn't write derived key: " + e.getMessage(), e);
            }
        }
        final byte[] derivedKeyMaterial = baos.toByteArray();
        final int keyLengthBytes = ByteUtils.byteLength(keyLengthBits);
        if (derivedKeyMaterial.length == keyLengthBytes) {
            return new SecretKeySpec(derivedKeyMaterial, "AES");
        }
        return new SecretKeySpec(ByteUtils.subArray(derivedKeyMaterial, 0, keyLengthBytes), "AES");
    }
    
    public SecretKey deriveKey(final SecretKey sharedSecret, final int keyLength, final byte[] algID, final byte[] partyUInfo, final byte[] partyVInfo, final byte[] suppPubInfo, final byte[] suppPrivInfo) throws JOSEException {
        final byte[] otherInfo = composeOtherInfo(algID, partyUInfo, partyVInfo, suppPubInfo, suppPrivInfo);
        return this.deriveKey(sharedSecret, keyLength, otherInfo);
    }
    
    public static byte[] composeOtherInfo(final byte[] algID, final byte[] partyUInfo, final byte[] partyVInfo, final byte[] suppPubInfo, final byte[] suppPrivInfo) {
        return ByteUtils.concat(new byte[][] { algID, partyUInfo, partyVInfo, suppPubInfo, suppPrivInfo });
    }
    
    private MessageDigest getMessageDigest() throws JOSEException {
        final Provider provider = this.getJCAContext().getProvider();
        try {
            if (provider == null) {
                return MessageDigest.getInstance(this.jcaHashAlg);
            }
            return MessageDigest.getInstance(this.jcaHashAlg, provider);
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Couldn't get message digest for KDF: " + e.getMessage(), e);
        }
    }
    
    public static int computeDigestCycles(final int digestLengthBits, final int keyLengthBits) {
        return (keyLengthBits + digestLengthBits - 1) / digestLengthBits;
    }
    
    public static byte[] encodeNoData() {
        return new byte[0];
    }
    
    public static byte[] encodeIntData(final int data) {
        return IntegerUtils.toBytes(data);
    }
    
    public static byte[] encodeStringData(final String data) {
        final byte[] bytes = (byte[])((data != null) ? data.getBytes(StandardCharset.UTF_8) : null);
        return encodeDataWithLength(bytes);
    }
    
    public static byte[] encodeDataWithLength(final byte[] data) {
        final byte[] bytes = (data != null) ? data : new byte[0];
        final byte[] length = IntegerUtils.toBytes(bytes.length);
        return ByteUtils.concat(new byte[][] { length, bytes });
    }
    
    public static byte[] encodeDataWithLength(final Base64URL data) {
        final byte[] bytes = (byte[])((data != null) ? data.decode() : null);
        return encodeDataWithLength(bytes);
    }
}
