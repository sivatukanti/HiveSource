// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.pkix;

import org.apache.kerby.cms.type.SignedData;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;

public final class PkiUtil
{
    private PkiUtil() {
    }
    
    public static byte[] getSignedData(final PrivateKey privateKey, final X509Certificate certificate, final byte[] dataToSign, final String eContentType) throws PkiException {
        return null;
    }
    
    public static boolean validateSignedData(final SignedData signedData) throws PkiException {
        return false;
    }
}
