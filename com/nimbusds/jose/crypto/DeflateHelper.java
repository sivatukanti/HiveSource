// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.DeflateUtils;
import com.nimbusds.jose.CompressionAlgorithm;
import com.nimbusds.jose.JWEHeader;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class DeflateHelper
{
    public static byte[] applyCompression(final JWEHeader jweHeader, final byte[] bytes) throws JOSEException {
        final CompressionAlgorithm compressionAlg = jweHeader.getCompressionAlgorithm();
        if (compressionAlg == null) {
            return bytes;
        }
        if (compressionAlg.equals(CompressionAlgorithm.DEF)) {
            try {
                return DeflateUtils.compress(bytes);
            }
            catch (Exception e) {
                throw new JOSEException("Couldn't compress plain text: " + e.getMessage(), e);
            }
        }
        throw new JOSEException("Unsupported compression algorithm: " + compressionAlg);
    }
    
    public static byte[] applyDecompression(final JWEHeader jweHeader, final byte[] bytes) throws JOSEException {
        final CompressionAlgorithm compressionAlg = jweHeader.getCompressionAlgorithm();
        if (compressionAlg == null) {
            return bytes;
        }
        if (compressionAlg.equals(CompressionAlgorithm.DEF)) {
            try {
                return DeflateUtils.decompress(bytes);
            }
            catch (Exception e) {
                throw new JOSEException("Couldn't decompress plain text: " + e.getMessage(), e);
            }
        }
        throw new JOSEException("Unsupported compression algorithm: " + compressionAlg);
    }
}
