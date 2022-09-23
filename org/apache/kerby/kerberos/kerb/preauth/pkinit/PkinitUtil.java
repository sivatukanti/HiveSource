// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.pkinit;

import org.apache.kerby.kerberos.kerb.type.pa.pkinit.ReplyKeyPack;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.KdcDhKeyInfo;
import org.apache.kerby.pkix.PkiException;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.pkix.PkiUtil;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.AuthPack;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;

public class PkinitUtil
{
    private static final String ID_PKINIT_AUTHDATA = "1.3.6.1.5.2.3.1";
    
    public static byte[] getSignedAuthPack(final PrivateKey privateKey, final X509Certificate certificate, final AuthPack authPack) throws KrbException {
        final byte[] dataToSign = KrbCodec.encode(authPack);
        byte[] signedData;
        try {
            signedData = PkiUtil.getSignedData(privateKey, certificate, dataToSign, "1.3.6.1.5.2.3.1");
        }
        catch (PkiException e) {
            throw new KrbException("Failed to sign data", e);
        }
        return signedData;
    }
    
    public static byte[] getSignedKdcDhKeyInfo(final PrivateKey privateKey, final X509Certificate certificate, final KdcDhKeyInfo kdcDhKeyInfo) throws KrbException {
        final byte[] dataToSign = KrbCodec.encode(kdcDhKeyInfo);
        byte[] signedData;
        try {
            signedData = PkiUtil.getSignedData(privateKey, certificate, dataToSign, "1.3.6.1.5.2.3.1");
        }
        catch (PkiException e) {
            throw new KrbException("Failed to sign data", e);
        }
        return signedData;
    }
    
    public static byte[] getSignedReplyKeyPack(final PrivateKey privateKey, final X509Certificate certificate, final ReplyKeyPack replyKeyPack) throws KrbException {
        final byte[] dataToSign = KrbCodec.encode(replyKeyPack);
        byte[] signedData;
        try {
            signedData = PkiUtil.getSignedData(privateKey, certificate, dataToSign, "1.3.6.1.5.2.3.1");
        }
        catch (PkiException e) {
            throw new KrbException("Failed to sign data", e);
        }
        return signedData;
    }
}
