// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth.pkinit;

import org.slf4j.LoggerFactory;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.preauth.PaFlag;
import org.apache.kerby.kerberos.kerb.preauth.PaFlags;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.util.Iterator;
import org.apache.kerby.cms.type.CertificateSet;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.DhRepInfo;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.KdcDhKeyInfo;
import org.apache.kerby.kerberos.kerb.common.KrbUtil;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.cms.type.CertificateChoices;
import org.apache.kerby.x509.type.Certificate;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.CertificateHelper;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.CmsMessageType;
import org.apache.kerby.cms.type.SignedData;
import java.io.IOException;
import org.apache.kerby.cms.type.ContentInfo;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.PaPkAsRep;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.PkinitPlgCryptoContext;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.TrustedCertifiers;
import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.interfaces.DHPublicKey;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.x509.type.SubjectPublicKeyInfo;
import org.apache.kerby.x509.type.DhParameter;
import org.apache.kerby.kerberos.kerb.crypto.dh.DhGroup;
import org.apache.kerby.kerberos.kerb.crypto.dh.DiffieHellmanClient;
import org.apache.kerby.x509.type.AlgorithmIdentifier;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.PkinitCrypto;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.PkAuthenticator;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.AuthPack;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.PaPkAsReq;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.common.CheckSumUtil;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import java.util.Date;
import java.util.Calendar;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.PkinitIdenity;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.PkinitOption;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.client.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.client.KrbContext;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.PkinitPreauthMeta;
import org.slf4j.Logger;
import org.apache.kerby.kerberos.kerb.client.preauth.AbstractPreauthPlugin;

public class PkinitPreauth extends AbstractPreauthPlugin
{
    private static final Logger LOG;
    private PkinitContext pkinitContext;
    
    public PkinitPreauth() {
        super(new PkinitPreauthMeta());
    }
    
    @Override
    public void init(final KrbContext context) {
        super.init(context);
        this.pkinitContext = new PkinitContext();
    }
    
    @Override
    public PluginRequestContext initRequestContext(final KdcRequest kdcRequest) {
        final PkinitRequestContext reqCtx = new PkinitRequestContext();
        reqCtx.updateRequestOpts(this.pkinitContext.pluginOpts);
        return reqCtx;
    }
    
    @Override
    public void setPreauthOptions(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final KOptions options) {
        if (options.contains(PkinitOption.X509_IDENTITY)) {
            this.pkinitContext.identityOpts.identity = options.getStringOption(PkinitOption.X509_IDENTITY);
        }
        if (options.contains(PkinitOption.X509_ANCHORS)) {
            final String anchorsString = options.getStringOption(PkinitOption.X509_ANCHORS);
            List<String> anchors;
            if (anchorsString == null) {
                anchors = kdcRequest.getContext().getConfig().getPkinitAnchors();
            }
            else {
                anchors = Arrays.asList(anchorsString);
            }
            this.pkinitContext.identityOpts.anchors.addAll(anchors);
        }
        if (options.contains(PkinitOption.USING_RSA)) {
            this.pkinitContext.pluginOpts.usingRsa = options.getBooleanOption(PkinitOption.USING_RSA, true);
        }
    }
    
    @Override
    public void prepareQuestions(final KdcRequest kdcRequest, final PluginRequestContext requestContext) {
        final PkinitRequestContext reqCtx = (PkinitRequestContext)requestContext;
        if (!reqCtx.identityInitialized) {
            PkinitIdenity.initialize(reqCtx.identityOpts, kdcRequest.getClientPrincipal());
            reqCtx.identityInitialized = true;
        }
    }
    
    @Override
    public void tryFirst(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaData outPadata) throws KrbException {
        final int nonce = kdcRequest.getChosenNonce();
        final long now = System.currentTimeMillis();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(now));
        final int cusec = calendar.get(13);
        final KerberosTime ctime = new KerberosTime(now);
        CheckSum checkSum = null;
        try {
            checkSum = CheckSumUtil.makeCheckSum(CheckSumType.NIST_SHA, KrbCodec.encode(kdcRequest.getKdcReq().getReqBody()));
        }
        catch (KrbException e) {
            throw new KrbException("Fail to encode checksum.", e);
        }
        final PaPkAsReq paPkAsReq = this.makePaPkAsReq(kdcRequest, (PkinitRequestContext)requestContext, cusec, ctime, nonce, checkSum);
        outPadata.addElement(this.makeEntry(paPkAsReq));
    }
    
    @Override
    public boolean process(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataEntry inPadata, final PaData outPadata) throws KrbException {
        final PkinitRequestContext reqCtx = (PkinitRequestContext)requestContext;
        if (inPadata == null) {
            return false;
        }
        boolean processingRequest = false;
        switch (inPadata.getPaDataType()) {
            case PK_AS_REQ: {
                processingRequest = true;
                break;
            }
        }
        if (processingRequest) {
            this.generateRequest(reqCtx, kdcRequest, outPadata);
            return true;
        }
        final EncryptionType encType = kdcRequest.getEncType();
        this.processReply(kdcRequest, reqCtx, inPadata, encType);
        return true;
    }
    
    private void generateRequest(final PkinitRequestContext reqCtx, final KdcRequest kdcRequest, final PaData outPadata) {
    }
    
    private PaPkAsReq makePaPkAsReq(final KdcRequest kdcRequest, final PkinitRequestContext reqCtx, final int cusec, final KerberosTime ctime, final int nonce, final CheckSum checkSum) throws KrbException {
        final KdcRequest kdc = kdcRequest;
        PkinitPreauth.LOG.info("Making the PK_AS_REQ.");
        final PaPkAsReq paPkAsReq = new PaPkAsReq();
        final AuthPack authPack = new AuthPack();
        final PkAuthenticator pkAuthen = new PkAuthenticator();
        final boolean usingRsa = this.pkinitContext.pluginOpts.usingRsa;
        reqCtx.paType = PaDataType.PK_AS_REQ;
        pkAuthen.setCusec(cusec);
        pkAuthen.setCtime(ctime);
        pkAuthen.setNonce(nonce);
        pkAuthen.setPaChecksum(checkSum.getChecksum());
        authPack.setPkAuthenticator(pkAuthen);
        authPack.setsupportedCmsTypes(this.pkinitContext.pluginOpts.createSupportedCMSTypes());
        if (!usingRsa) {
            PkinitPreauth.LOG.info("DH key transport algorithm.");
            final String content = "0x06 07 2A 86 48 ce 3e 02 01";
            final Asn1ObjectIdentifier dhOid = PkinitCrypto.createOid(content);
            final AlgorithmIdentifier dhAlg = new AlgorithmIdentifier();
            dhAlg.setAlgorithm(dhOid.getValue());
            final DiffieHellmanClient client = new DiffieHellmanClient();
            DHPublicKey clientPubKey = null;
            try {
                clientPubKey = client.init(DhGroup.MODP_GROUP2);
            }
            catch (Exception e) {
                PkinitPreauth.LOG.error("DiffieHellmanClient init with failure. " + e);
            }
            reqCtx.setDhClient(client);
            DHParameterSpec type = null;
            try {
                type = clientPubKey.getParams();
            }
            catch (Exception e2) {
                PkinitPreauth.LOG.error("Fail to get params from client public key. " + e2);
            }
            final BigInteger q = type.getP().shiftRight(1);
            final DhParameter dhParameter = new DhParameter();
            dhParameter.setP(type.getP());
            dhParameter.setG(type.getG());
            dhParameter.setQ(q);
            dhAlg.setParameters(dhParameter);
            final SubjectPublicKeyInfo pubInfo = new SubjectPublicKeyInfo();
            pubInfo.setAlgorithm(dhAlg);
            final Asn1Integer publickey = new Asn1Integer(clientPubKey.getY());
            pubInfo.setSubjectPubKey(KrbCodec.encode(publickey));
            authPack.setClientPublicValue(pubInfo);
            final byte[] signedAuthPack = this.signAuthPack(authPack);
            paPkAsReq.setSignedAuthPack(signedAuthPack);
        }
        else {
            PkinitPreauth.LOG.info("RSA key transport algorithm");
        }
        final TrustedCertifiers trustedCertifiers = this.pkinitContext.pluginOpts.createTrustedCertifiers();
        paPkAsReq.setTrustedCertifiers(trustedCertifiers);
        return paPkAsReq;
    }
    
    private byte[] signAuthPack(final AuthPack authPack) throws KrbException {
        final String oid = PkinitPlgCryptoContext.getIdPkinitAuthDataOID();
        final byte[] signedDataBytes = PkinitCrypto.eContentInfoCreate(KrbCodec.encode(authPack), oid);
        return signedDataBytes;
    }
    
    private void processReply(final KdcRequest kdcRequest, final PkinitRequestContext reqCtx, final PaDataEntry paEntry, final EncryptionType encType) throws KrbException {
        if (paEntry.getPaDataType() == PaDataType.PK_AS_REP) {
            PkinitPreauth.LOG.info("processing PK_AS_REP");
            final PaPkAsRep paPkAsRep = KrbCodec.decode(paEntry.getPaDataValue(), PaPkAsRep.class);
            final DhRepInfo dhRepInfo = paPkAsRep.getDHRepInfo();
            final byte[] dhSignedData = dhRepInfo.getDHSignedData();
            final ContentInfo contentInfo = new ContentInfo();
            try {
                contentInfo.decode(dhSignedData);
            }
            catch (IOException e) {
                PkinitPreauth.LOG.error("Fail to decode dhSignedData. " + e);
            }
            final SignedData signedData = contentInfo.getContentAs(SignedData.class);
            PkinitCrypto.verifyCmsSignedData(CmsMessageType.CMS_SIGN_SERVER, signedData);
            if (kdcRequest.getContext().getConfig().getPkinitAnchors().isEmpty()) {
                PkinitPreauth.LOG.error("No PKINIT anchors specified");
                throw new KrbException("No PKINIT anchors specified");
            }
            final String anchorFileName = kdcRequest.getContext().getConfig().getPkinitAnchors().get(0);
            X509Certificate x509Certificate = null;
            try {
                final List<java.security.cert.Certificate> certs = CertificateHelper.loadCerts(anchorFileName);
                if (certs != null && !certs.isEmpty()) {
                    x509Certificate = certs.iterator().next();
                }
            }
            catch (KrbException e2) {
                PkinitPreauth.LOG.error("Fail to load certs from archor file. " + e2);
            }
            if (x509Certificate == null) {
                PkinitPreauth.LOG.error("Failed to load PKINIT anchor");
                throw new KrbException("Failed to load PKINIT anchor");
            }
            final CertificateSet certificateSet = signedData.getCertificates();
            if (certificateSet == null || certificateSet.getElements().isEmpty()) {
                throw new KrbException("No PKINIT Certs");
            }
            final List<Certificate> certificates = new ArrayList<Certificate>();
            final List<CertificateChoices> certificateChoicesList = certificateSet.getElements();
            for (final CertificateChoices certificateChoices : certificateChoicesList) {
                certificates.add(certificateChoices.getCertificate());
            }
            try {
                PkinitCrypto.validateChain(certificates, x509Certificate);
            }
            catch (Exception e3) {
                throw new KrbException(KrbErrorCode.KDC_ERR_INVALID_CERTIFICATE, e3);
            }
            final PrincipalName kdcPrincipal = KrbUtil.makeTgsPrincipal(kdcRequest.getContext().getConfig().getKdcRealm());
            final boolean validSan = PkinitCrypto.verifyKdcSan(kdcRequest.getContext().getConfig().getPkinitKdcHostName(), kdcPrincipal, certificates);
            if (!validSan) {
                PkinitPreauth.LOG.error("Did not find an acceptable SAN in KDC certificate");
            }
            PkinitPreauth.LOG.info("skipping EKU check");
            PkinitPreauth.LOG.info("as_rep: DH key transport algorithm");
            final KdcDhKeyInfo kdcDhKeyInfo = new KdcDhKeyInfo();
            try {
                kdcDhKeyInfo.decode(signedData.getEncapContentInfo().getContent());
            }
            catch (IOException e4) {
                final String errMessage = "failed to decode KdcDhKeyInfo " + e4.getMessage();
                PkinitPreauth.LOG.error(errMessage);
                throw new KrbException(errMessage);
            }
            final byte[] subjectPublicKey = kdcDhKeyInfo.getSubjectPublicKey().getValue();
            final Asn1Integer clientPubKey = KrbCodec.decode(subjectPublicKey, Asn1Integer.class);
            final BigInteger y = clientPubKey.getValue();
            final DiffieHellmanClient client = reqCtx.getDhClient();
            final BigInteger p = client.getDhParam().getP();
            final BigInteger g = client.getDhParam().getG();
            final DHPublicKey dhPublicKey = PkinitCrypto.createDHPublicKey(p, g, y);
            EncryptionKey secretKey = null;
            try {
                client.doPhase(dhPublicKey.getEncoded());
                secretKey = client.generateKey(null, null, encType);
            }
            catch (Exception e5) {
                PkinitPreauth.LOG.error("DiffieHellmanClient do parse failed. " + e5);
            }
            if (secretKey == null) {
                throw new KrbException("Fail to create client key.");
            }
            kdcRequest.setAsKey(secretKey);
        }
    }
    
    @Override
    public boolean tryAgain(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataType preauthType, final PaData errPadata, final PaData outPadata) {
        final PkinitRequestContext reqCtx = (PkinitRequestContext)requestContext;
        if (reqCtx.paType != preauthType && errPadata == null) {
            return false;
        }
        final boolean doAgain = false;
        for (final PaDataEntry pde : errPadata.getElements()) {
            System.out.println(pde.getPaDataType());
        }
        if (doAgain) {
            this.generateRequest(reqCtx, kdcRequest, outPadata);
        }
        return false;
    }
    
    @Override
    public PaFlags getFlags(final PaDataType paType) {
        final PaFlags paFlags = new PaFlags(0);
        paFlags.setFlag(PaFlag.PA_REAL);
        return paFlags;
    }
    
    private PaDataEntry makeEntry(final PaPkAsReq paPkAsReq) throws KrbException {
        final PaDataEntry paDataEntry = new PaDataEntry();
        paDataEntry.setPaDataType(PaDataType.PK_AS_REQ);
        paDataEntry.setPaDataValue(KrbCodec.encode(paPkAsReq));
        return paDataEntry;
    }
    
    static {
        LOG = LoggerFactory.getLogger(PkinitPreauth.class);
    }
}
