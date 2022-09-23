// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.pkinit;

import org.slf4j.LoggerFactory;
import java.security.cert.CertificateEncodingException;
import org.apache.kerby.util.HexUtil;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import java.io.IOException;
import java.security.cert.CertPathValidatorException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.PKIXParameters;
import java.util.Collections;
import java.security.cert.TrustAnchor;
import java.security.cert.CertPathValidator;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.kerby.x509.type.Certificate;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.cms.type.EncapsulatedContentInfo;
import org.apache.kerby.cms.type.SignedContentInfo;
import org.apache.kerby.cms.type.SignerInfos;
import org.apache.kerby.cms.type.RevocationInfoChoices;
import org.apache.kerby.cms.type.CertificateSet;
import org.apache.kerby.cms.type.DigestAlgorithmIdentifiers;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.KeyFactory;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.interfaces.DHPublicKey;
import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import org.apache.kerby.x509.type.DhParameter;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.cms.type.SignedData;
import org.slf4j.Logger;

public class PkinitCrypto
{
    private static final Logger LOG;
    
    public static void verifyCmsSignedData(final CmsMessageType cmsMsgType, final SignedData signedData) throws KrbException {
        final String oid = pkinitType2OID(cmsMsgType);
        if (oid == null) {
            throw new KrbException("Can't get the right oid ");
        }
        final String etype = signedData.getEncapContentInfo().getContentType();
        if (oid.equals(etype)) {
            PkinitCrypto.LOG.info("CMS Verification successful");
            return;
        }
        PkinitCrypto.LOG.error("Wrong oid in eContentType");
        throw new KrbException(KrbErrorCode.KDC_ERR_PREAUTH_FAILED, "Wrong oid in eContentType");
    }
    
    public static String pkinitType2OID(final CmsMessageType cmsMsgType) {
        switch (cmsMsgType) {
            case UNKNOWN: {
                return null;
            }
            case CMS_SIGN_CLIENT: {
                return PkinitPlgCryptoContext.getIdPkinitAuthDataOID();
            }
            case CMS_SIGN_SERVER: {
                return PkinitPlgCryptoContext.getIdPkinitDHKeyDataOID();
            }
            case CMS_ENVEL_SERVER: {
                return PkinitPlgCryptoContext.getIdPkinitRkeyDataOID();
            }
            default: {
                return null;
            }
        }
    }
    
    public static void serverCheckDH(final PluginOpts pluginOpts, final PkinitPlgCryptoContext cryptoctx, final DhParameter dhParameter) throws KrbException {
        final int dhPrimeBits = dhParameter.getP().bitLength();
        if (dhPrimeBits < pluginOpts.dhMinBits) {
            final String errMsg = "client sent dh params with " + dhPrimeBits + "bits, we require " + pluginOpts.dhMinBits;
            PkinitCrypto.LOG.error(errMsg);
            throw new KrbException(KrbErrorCode.KDC_ERR_DH_KEY_PARAMETERS_NOT_ACCEPTED, errMsg);
        }
        if (!checkDHWellknown(cryptoctx, dhParameter, dhPrimeBits)) {
            throw new KrbException(KrbErrorCode.KDC_ERR_DH_KEY_PARAMETERS_NOT_ACCEPTED);
        }
    }
    
    public static boolean checkDHWellknown(final PkinitPlgCryptoContext cryptoctx, final DhParameter dhParameter, final int dhPrimeBits) throws KrbException {
        boolean valid = false;
        switch (dhPrimeBits) {
            case 1024:
            case 2048:
            case 4096: {
                valid = pkinitCheckDhParams(cryptoctx.createDHParameterSpec(dhPrimeBits), dhParameter);
                break;
            }
        }
        return valid;
    }
    
    public static boolean pkinitCheckDhParams(final DHParameterSpec dh1, final DhParameter dh2) {
        if (!dh1.getP().equals(dh2.getP())) {
            PkinitCrypto.LOG.error("p is not well-known group dhparameter");
            return false;
        }
        if (!dh1.getG().equals(dh2.getG())) {
            PkinitCrypto.LOG.error("bad g dhparameter");
            return false;
        }
        PkinitCrypto.LOG.info("Good dhparams", (Object)dh1.getP().bitLength());
        return true;
    }
    
    public static DHPublicKey createDHPublicKey(final BigInteger p, final BigInteger g, final BigInteger y) {
        final DHPublicKeySpec dhPublicKeySpec = new DHPublicKeySpec(y, p, g);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("DH");
        }
        catch (NoSuchAlgorithmException e) {
            PkinitCrypto.LOG.error("Fail to get dh instance. " + e);
        }
        DHPublicKey dhPublicKey = null;
        try {
            if (keyFactory != null) {
                dhPublicKey = (DHPublicKey)keyFactory.generatePublic(dhPublicKeySpec);
            }
        }
        catch (InvalidKeySpecException e2) {
            PkinitCrypto.LOG.error("Fail to generate public key. " + e2);
        }
        return dhPublicKey;
    }
    
    public static byte[] cmsSignedDataCreate(final byte[] data, final String oid, final int version, final DigestAlgorithmIdentifiers digestAlgorithmIdentifiers, final CertificateSet certificateSet, final RevocationInfoChoices crls, final SignerInfos signerInfos) throws KrbException {
        final SignedContentInfo contentInfo = new SignedContentInfo();
        contentInfo.setContentType("1.2.840.113549.1.7.2");
        final SignedData signedData = new SignedData();
        signedData.setVersion(version);
        if (digestAlgorithmIdentifiers != null) {
            signedData.setDigestAlgorithms(digestAlgorithmIdentifiers);
        }
        final EncapsulatedContentInfo eContentInfo = new EncapsulatedContentInfo();
        eContentInfo.setContentType(oid);
        eContentInfo.setContent(data);
        signedData.setEncapContentInfo(eContentInfo);
        if (certificateSet != null) {
            signedData.setCertificates(certificateSet);
        }
        if (crls != null) {
            signedData.setCrls(crls);
        }
        if (signerInfos != null) {
            signedData.setSignerInfos(signerInfos);
        }
        contentInfo.setSignedData(signedData);
        return KrbCodec.encode(contentInfo);
    }
    
    public static byte[] eContentInfoCreate(final byte[] data, final String oid) throws KrbException {
        final EncapsulatedContentInfo eContentInfo = new EncapsulatedContentInfo();
        eContentInfo.setContentType(oid);
        eContentInfo.setContent(data);
        return KrbCodec.encode(eContentInfo);
    }
    
    public static X509Certificate[] createCertChain(final PkinitPlgCryptoContext cryptoContext) throws CertificateNotYetValidException, CertificateExpiredException {
        PkinitCrypto.LOG.info("Building certificate chain.");
        final X509Certificate[] clientChain = new X509Certificate[3];
        return clientChain;
    }
    
    public static boolean verifyKdcSan(final String hostname, final PrincipalName kdcPrincipal, final List<Certificate> certificates) throws KrbException {
        if (hostname == null) {
            PkinitCrypto.LOG.info("No pkinit_kdc_hostname values found in config file");
        }
        else {
            PkinitCrypto.LOG.info("pkinit_kdc_hostname values found in config file");
        }
        try {
            final List<PrincipalName> princs = cryptoRetrieveCertSans(certificates);
            if (princs == null) {
                return false;
            }
            for (final PrincipalName princ : princs) {
                PkinitCrypto.LOG.info("PKINIT client found id-pkinit-san in KDC cert: " + princ.getName());
            }
            PkinitCrypto.LOG.info("Checking pkinit sans.");
            if (princs.contains(kdcPrincipal)) {
                PkinitCrypto.LOG.info("pkinit san match found");
                return true;
            }
            PkinitCrypto.LOG.info("no pkinit san match found");
            return false;
        }
        catch (KrbException e) {
            final String errMessage = "PKINIT client failed to decode SANs in KDC cert." + e;
            PkinitCrypto.LOG.error(errMessage);
            throw new KrbException(KrbErrorCode.KDC_NAME_MISMATCH, errMessage);
        }
    }
    
    public static List<PrincipalName> cryptoRetrieveCertSans(final List<Certificate> certificates) throws KrbException {
        if (certificates.size() == 0) {
            PkinitCrypto.LOG.info("no certificate!");
            return null;
        }
        return cryptoRetrieveX509Sans(certificates);
    }
    
    public static List<PrincipalName> cryptoRetrieveX509Sans(final List<Certificate> certificates) throws KrbException {
        final List<PrincipalName> principalNames = new ArrayList<PrincipalName>();
        for (final Certificate cert : certificates) {
            PkinitCrypto.LOG.info("Looking for SANs in cert: " + cert.getTBSCertificate().getSubject());
        }
        return principalNames;
    }
    
    public static void validateChain(final List<Certificate> certificateList, final X509Certificate anchor) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CertPathValidatorException, IOException {
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        final List<X509Certificate> certsList = new ArrayList<X509Certificate>(certificateList.size());
        for (final Certificate cert : certificateList) {
            final X509Certificate parsedCert = (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(cert.encode()));
            certsList.add(parsedCert);
        }
        final CertPath certPath = certificateFactory.generateCertPath(certsList);
        final CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
        final TrustAnchor trustAnchor = new TrustAnchor(anchor, null);
        final PKIXParameters parameters = new PKIXParameters(Collections.singleton(trustAnchor));
        parameters.setRevocationEnabled(false);
        cpv.validate(certPath, parameters);
    }
    
    public static Asn1ObjectIdentifier createOid(final String content) throws KrbException {
        final Asn1ObjectIdentifier oid = new Asn1ObjectIdentifier();
        oid.useDER();
        KrbCodec.decode(HexUtil.hex2bytesFriendly(content), oid);
        return oid;
    }
    
    public static Certificate changeToCertificate(final X509Certificate x509Certificate) {
        final Certificate certificate = new Certificate();
        try {
            certificate.decode(x509Certificate.getEncoded());
        }
        catch (IOException e) {
            PkinitCrypto.LOG.error("Fail to decode certificate. " + e);
        }
        catch (CertificateEncodingException e2) {
            PkinitCrypto.LOG.error("Fail to encode x509 certificate. " + e2);
        }
        return certificate;
    }
    
    static {
        LOG = LoggerFactory.getLogger(PkinitCrypto.class);
    }
}
