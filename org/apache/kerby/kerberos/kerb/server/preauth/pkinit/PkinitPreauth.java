// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.preauth.pkinit;

import org.slf4j.LoggerFactory;
import java.security.cert.Certificate;
import java.util.Iterator;
import org.apache.kerby.cms.type.SignerInfos;
import org.apache.kerby.cms.type.RevocationInfoChoices;
import org.apache.kerby.cms.type.DigestAlgorithmIdentifiers;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.PkinitPlgCryptoContext;
import org.apache.kerby.cms.type.CertificateChoices;
import org.apache.kerby.cms.type.CertificateSet;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.KdcDhKeyInfo;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.DhRepInfo;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.CertificateHelper;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.PaPkAsRep;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.x509.type.SubjectPublicKeyInfo;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.PkAuthenticator;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import javax.crypto.interfaces.DHPublicKey;
import org.apache.kerby.kerberos.kerb.crypto.dh.DiffieHellmanServer;
import java.math.BigInteger;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.x509.type.DhParameter;
import java.util.Arrays;
import org.apache.kerby.kerberos.kerb.common.CheckSumUtil;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.asn1.parse.Asn1Container;
import org.apache.kerby.asn1.Asn1;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcOption;
import org.apache.kerby.kerberos.kerb.common.KrbUtil;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.PkinitCrypto;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.CmsMessageType;
import org.apache.kerby.cms.type.SignedData;
import org.apache.kerby.cms.type.ContentInfo;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.AuthPack;
import java.io.IOException;
import org.apache.kerby.cms.type.EncapsulatedContentInfo;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.PaPkAsReq;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.server.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.server.KdcContext;
import java.util.HashMap;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.PkinitPreauthMeta;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.kerby.kerberos.kerb.server.preauth.AbstractPreauthPlugin;

public class PkinitPreauth extends AbstractPreauthPlugin
{
    private static final Logger LOG;
    private final Map<String, PkinitKdcContext> pkinitContexts;
    
    public PkinitPreauth() {
        super(new PkinitPreauthMeta());
        this.pkinitContexts = new HashMap<String, PkinitKdcContext>(1);
    }
    
    @Override
    public void initWith(final KdcContext kdcContext) {
        super.initWith(kdcContext);
        final PkinitKdcContext tmp = new PkinitKdcContext();
        tmp.realm = kdcContext.getKdcRealm();
        final String pkinitIdentity = kdcContext.getConfig().getPkinitIdentity();
        tmp.identityOpts.identity = pkinitIdentity;
        this.pkinitContexts.put(kdcContext.getKdcRealm(), tmp);
    }
    
    @Override
    public PluginRequestContext initRequestContext(final KdcRequest kdcRequest) {
        final PkinitRequestContext reqCtx = new PkinitRequestContext();
        return reqCtx;
    }
    
    @Override
    public boolean verify(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataEntry paData) throws KrbException {
        PkinitPreauth.LOG.info("pkinit verify padata: entered!");
        final PkinitRequestContext reqCtx = (PkinitRequestContext)requestContext;
        final PrincipalName serverPrincipal = kdcRequest.getServerEntry().getPrincipal();
        kdcRequest.setServerPrincipal(serverPrincipal);
        final PkinitKdcContext pkinitContext = this.findContext(serverPrincipal);
        if (pkinitContext == null) {
            return false;
        }
        reqCtx.paType = paData.getPaDataType();
        if (paData.getPaDataType() == PaDataType.PK_AS_REQ) {
            PkinitPreauth.LOG.info("processing PK_AS_REQ");
            final PaPkAsReq paPkAsReq = KrbCodec.decode(paData.getPaDataValue(), PaPkAsReq.class);
            final byte[] signedAuthPack = paPkAsReq.getSignedAuthPack();
            AuthPack authPack = null;
            if (kdcRequest.isAnonymous()) {
                final EncapsulatedContentInfo eContentInfo = new EncapsulatedContentInfo();
                try {
                    eContentInfo.decode(signedAuthPack);
                }
                catch (IOException e) {
                    PkinitPreauth.LOG.error("Fail to decode signedAuthPack. " + e);
                }
                authPack = KrbCodec.decode(eContentInfo.getContent(), AuthPack.class);
            }
            else {
                final ContentInfo contentInfo = new ContentInfo();
                try {
                    contentInfo.decode(signedAuthPack);
                }
                catch (IOException e) {
                    PkinitPreauth.LOG.error("Fail to decode signedAuthPack");
                }
                final SignedData signedData = contentInfo.getContentAs(SignedData.class);
                PkinitCrypto.verifyCmsSignedData(CmsMessageType.CMS_SIGN_CLIENT, signedData);
                final Boolean isSigned = signedData.isSigned();
                if (isSigned) {
                    PkinitPreauth.LOG.info("Signed data.");
                }
                else {
                    final PrincipalName clientPrincial = kdcRequest.getClientEntry().getPrincipal();
                    final PrincipalName anonymousPrincipal = KrbUtil.makeAnonymousPrincipal();
                    if (kdcRequest.getKdcOptions().isFlagSet(KdcOption.REQUEST_ANONYMOUS) && !KrbUtil.pricipalCompareIgnoreRealm(clientPrincial, anonymousPrincipal)) {
                        final String errMsg = "Pkinit request not signed, but client not anonymous.";
                        PkinitPreauth.LOG.error(errMsg);
                        throw new KrbException(KrbErrorCode.KDC_ERR_PREAUTH_FAILED, errMsg);
                    }
                }
                authPack = KrbCodec.decode(signedData.getEncapContentInfo().getContent(), AuthPack.class);
            }
            final PkAuthenticator pkAuthenticator = authPack.getPkAuthenticator();
            this.checkClockskew(kdcRequest, pkAuthenticator.getCtime());
            byte[] reqBodyBytes = null;
            if (kdcRequest.getReqPackage() == null) {
                PkinitPreauth.LOG.error("ReqBodyBytes isn't available");
                return false;
            }
            Asn1ParseResult parseResult = null;
            try {
                parseResult = Asn1.parse(kdcRequest.getReqPackage());
            }
            catch (IOException e2) {
                PkinitPreauth.LOG.error("Fail to parse reqPackage. " + e2);
            }
            final Asn1Container container = (Asn1Container)parseResult;
            final List<Asn1ParseResult> parseResults = container.getChildren();
            final Asn1Container parsingItem = parseResults.get(0);
            final List<Asn1ParseResult> items = parsingItem.getChildren();
            if (items.size() > 3) {
                final ByteBuffer bodyBuffer = items.get(3).getBodyBuffer();
                reqBodyBytes = new byte[bodyBuffer.remaining()];
                bodyBuffer.get(reqBodyBytes);
            }
            CheckSum expectedCheckSum = null;
            try {
                expectedCheckSum = CheckSumUtil.makeCheckSum(CheckSumType.NIST_SHA, reqBodyBytes);
            }
            catch (KrbException e3) {
                PkinitPreauth.LOG.error("Unable to calculate AS REQ checksum.", e3.getMessage());
            }
            final byte[] receivedCheckSumByte = pkAuthenticator.getPaChecksum();
            if (expectedCheckSum.getChecksum().length != receivedCheckSumByte.length || !Arrays.equals(expectedCheckSum.getChecksum(), receivedCheckSumByte)) {
                PkinitPreauth.LOG.debug("received checksum length: " + receivedCheckSumByte.length + ", expected checksum type: " + expectedCheckSum.getCksumtype() + ", expected checksum length: " + expectedCheckSum.encodingLength());
                final String errorMessage = "Failed to match the checksum.";
                PkinitPreauth.LOG.error(errorMessage);
                throw new KrbException(KrbErrorCode.KDC_ERR_PA_CHECKSUM_MUST_BE_INCLUDED, errorMessage);
            }
            final SubjectPublicKeyInfo publicKeyInfo = authPack.getClientPublicValue();
            if (publicKeyInfo.getSubjectPubKey() != null) {
                final DhParameter dhParameter = authPack.getClientPublicValue().getAlgorithm().getParametersAs(DhParameter.class);
                PkinitCrypto.serverCheckDH(pkinitContext.pluginOpts, pkinitContext.cryptoctx, dhParameter);
                final byte[] clientSubjectPubKey = publicKeyInfo.getSubjectPubKey().getValue();
                final Asn1Integer clientPubKey = KrbCodec.decode(clientSubjectPubKey, Asn1Integer.class);
                final BigInteger y = clientPubKey.getValue();
                final BigInteger p = dhParameter.getP();
                final BigInteger g = dhParameter.getG();
                final DHPublicKey dhPublicKey = PkinitCrypto.createDHPublicKey(p, g, y);
                final DiffieHellmanServer server = new DiffieHellmanServer();
                DHPublicKey serverPubKey = null;
                try {
                    serverPubKey = (DHPublicKey)server.initAndDoPhase(dhPublicKey.getEncoded());
                }
                catch (Exception e4) {
                    PkinitPreauth.LOG.error("Fail to create server public key.", e4);
                }
                final EncryptionKey secretKey = server.generateKey(null, null, kdcRequest.getEncryptionType());
                kdcRequest.setClientKey(secretKey);
                final String identity = pkinitContext.identityOpts.identity;
                final PaPkAsRep paPkAsRep = this.makePaPkAsRep(serverPubKey, identity);
                final PaDataEntry paDataEntry = this.makeEntry(paPkAsRep);
                kdcRequest.getPreauthContext().getOutputPaData().add(paDataEntry);
            }
            else {
                if (!kdcRequest.isAnonymous()) {
                    final String errMessage = "Anonymous pkinit without DH public value not supported.";
                    PkinitPreauth.LOG.error(errMessage);
                    throw new KrbException(KrbErrorCode.KDC_ERR_PREAUTH_FAILED, errMessage);
                }
                System.out.println("rsa");
            }
        }
        return true;
    }
    
    private PkinitKdcContext findContext(final PrincipalName principal) {
        final String realm = principal.getRealm();
        if (this.pkinitContexts.containsKey(realm)) {
            return this.pkinitContexts.get(realm);
        }
        return null;
    }
    
    private PaDataEntry makeEntry(final PaPkAsRep paPkAsRep) throws KrbException {
        final PaDataEntry paDataEntry = new PaDataEntry();
        paDataEntry.setPaDataType(PaDataType.PK_AS_REP);
        try {
            paDataEntry.setPaDataValue(paPkAsRep.encode());
        }
        catch (IOException e) {
            PkinitPreauth.LOG.error("Fail to encode PaDataEntry. " + e);
        }
        return paDataEntry;
    }
    
    private PaPkAsRep makePaPkAsRep(final DHPublicKey severPubKey, final String identityString) throws KrbException {
        final List<X509Certificate> certificates = new ArrayList<X509Certificate>();
        if (identityString != null) {
            final List<String> identityList = Arrays.asList(identityString.split(","));
            for (final String identity : identityList) {
                try {
                    final List<Certificate> loadedCerts = CertificateHelper.loadCerts(identity);
                    if (loadedCerts.isEmpty()) {
                        continue;
                    }
                    certificates.add(loadedCerts.iterator().next());
                }
                catch (KrbException e) {
                    PkinitPreauth.LOG.warn("Error loading X.509 Certificate", e);
                }
            }
        }
        else {
            PkinitPreauth.LOG.warn("No PKINIT identity keys specified");
        }
        final PaPkAsRep paPkAsRep = new PaPkAsRep();
        final DhRepInfo dhRepInfo = new DhRepInfo();
        final KdcDhKeyInfo kdcDhKeyInfo = new KdcDhKeyInfo();
        final Asn1Integer publickey = new Asn1Integer(severPubKey.getY());
        final byte[] pubKeyData = KrbCodec.encode(publickey);
        kdcDhKeyInfo.setSubjectPublicKey(pubKeyData);
        kdcDhKeyInfo.setNonce(0);
        kdcDhKeyInfo.setDHKeyExpiration(new KerberosTime(System.currentTimeMillis() + 86400000L));
        byte[] signedDataBytes = null;
        final CertificateSet certificateSet = new CertificateSet();
        for (final X509Certificate x509Certificate : certificates) {
            final org.apache.kerby.x509.type.Certificate certificate = PkinitCrypto.changeToCertificate(x509Certificate);
            final CertificateChoices certificateChoices = new CertificateChoices();
            certificateChoices.setCertificate(certificate);
            certificateSet.addElement(certificateChoices);
        }
        final String oid = PkinitPlgCryptoContext.getIdPkinitDHKeyDataOID();
        signedDataBytes = PkinitCrypto.cmsSignedDataCreate(KrbCodec.encode(kdcDhKeyInfo), oid, 3, null, certificateSet, null, null);
        dhRepInfo.setDHSignedData(signedDataBytes);
        paPkAsRep.setDHRepInfo(dhRepInfo);
        return paPkAsRep;
    }
    
    private boolean checkClockskew(final KdcRequest kdcRequest, final KerberosTime time) throws KrbException {
        final long clockSkew = kdcRequest.getKdcContext().getConfig().getAllowableClockSkew() * 1000L;
        if (!time.isInClockSkew(clockSkew)) {
            throw new KrbException(KrbErrorCode.KDC_ERR_PREAUTH_FAILED);
        }
        return true;
    }
    
    static {
        LOG = LoggerFactory.getLogger(PkinitPreauth.class);
    }
}
