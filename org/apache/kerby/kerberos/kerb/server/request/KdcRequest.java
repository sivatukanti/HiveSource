// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.request;

import org.slf4j.LoggerFactory;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcOptions;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcOption;
import org.apache.kerby.kerberos.kerb.type.base.MethodData;
import org.apache.kerby.kerberos.kerb.type.base.EtypeInfo2Entry;
import org.apache.kerby.kerberos.kerb.type.base.EtypeInfoEntry;
import org.apache.kerby.kerberos.kerb.type.base.EtypeInfo;
import org.apache.kerby.kerberos.kerb.type.base.EtypeInfo2;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.KrbError;
import org.apache.kerby.kerberos.kerb.server.KdcRecoverableException;
import java.util.Date;
import org.apache.kerby.kerberos.kerb.common.KrbUtil;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessage;
import org.apache.kerby.kerberos.kerb.crypto.fast.FastUtil;
import org.apache.kerby.kerberos.kerb.type.ap.Authenticator;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.ticket.EncTicketPart;
import org.apache.kerby.kerberos.kerb.type.ap.ApReq;
import org.apache.kerby.kerberos.kerb.type.fast.ArmorType;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.fast.KrbFastArmor;
import org.apache.kerby.kerberos.kerb.type.fast.KrbFastArmoredReq;
import java.util.Iterator;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.crypto.CheckSumHandler;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.crypto.EncryptionHandler;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.fast.KrbFastReq;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.type.fast.PaFxFastRequest;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.server.preauth.PreauthHandler;
import java.nio.ByteBuffer;
import org.apache.kerby.kerberos.kerb.type.base.AuthToken;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.server.preauth.KdcFastContext;
import org.apache.kerby.kerberos.kerb.server.preauth.PreauthContext;
import org.apache.kerby.kerberos.kerb.identity.KrbIdentity;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.net.InetAddress;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcRep;
import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import org.apache.kerby.kerberos.kerb.server.KdcContext;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import org.slf4j.Logger;

public abstract class KdcRequest
{
    private static final Logger LOG;
    private final KdcReq kdcReq;
    private final KdcContext kdcContext;
    private Ticket ticket;
    private boolean isPreAuthenticated;
    private KdcRep reply;
    private InetAddress clientAddress;
    private boolean isTcp;
    private EncryptionType encryptionType;
    private EncryptionKey clientKey;
    private KrbIdentity clientEntry;
    private KrbIdentity serverEntry;
    private EncryptionKey serverKey;
    private KrbIdentity tgsEntry;
    private PreauthContext preauthContext;
    private KdcFastContext fastContext;
    private PrincipalName clientPrincipal;
    private PrincipalName serverPrincipal;
    private byte[] innerBodyout;
    private AuthToken token;
    private boolean isToken;
    private boolean isPkinit;
    private boolean isAnonymous;
    private EncryptionKey sessionKey;
    private ByteBuffer reqPackage;
    private boolean isHttps;
    
    public EncryptionKey getSessionKey() {
        return this.sessionKey;
    }
    
    public void setSessionKey(final EncryptionKey sessionKey) {
        this.sessionKey = sessionKey;
    }
    
    public KdcRequest(final KdcReq kdcReq, final KdcContext kdcContext) {
        this.isTcp = true;
        this.isToken = false;
        this.isPkinit = false;
        this.isAnonymous = false;
        this.isHttps = false;
        this.kdcReq = kdcReq;
        this.kdcContext = kdcContext;
        this.preauthContext = kdcContext.getPreauthHandler().preparePreauthContext(this);
        this.fastContext = new KdcFastContext();
    }
    
    public KdcContext getKdcContext() {
        return this.kdcContext;
    }
    
    public KdcReq getKdcReq() {
        return this.kdcReq;
    }
    
    public PreauthContext getPreauthContext() {
        return this.preauthContext;
    }
    
    public void process() throws KrbException {
        this.checkVersion();
        this.checkTgsEntry();
        this.kdcFindFast();
        if (this.isPreauthRequired()) {
            this.kdcFindFast();
        }
        this.checkEncryptionType();
        if (PreauthHandler.isToken(this.getKdcReq().getPaData())) {
            this.isToken = true;
            if (this.isPreauthRequired()) {
                this.preauth();
            }
            this.checkClient();
            this.checkServer();
        }
        else {
            if (PreauthHandler.isPkinit(this.getKdcReq().getPaData())) {
                this.isPkinit = true;
            }
            this.checkClient();
            this.checkServer();
            if (this.isPreauthRequired()) {
                this.preauth();
            }
        }
        this.checkPolicy();
        this.issueTicket();
        this.makeReply();
    }
    
    private void checkTgsEntry() throws KrbException {
        final KrbIdentity tgsEntry = this.getEntry(this.getTgsPrincipal().getName());
        this.setTgsEntry(tgsEntry);
    }
    
    private void kdcFindFast() throws KrbException {
        final PaData paData = this.getKdcReq().getPaData();
        if (paData != null) {
            for (final PaDataEntry paEntry : paData.getElements()) {
                if (paEntry.getPaDataType() == PaDataType.FX_FAST) {
                    KdcRequest.LOG.info("Found fast padata and starting to process it.");
                    PaFxFastRequest paFxFastRequest = new PaFxFastRequest();
                    KrbFastArmoredReq fastArmoredReq = null;
                    try {
                        paFxFastRequest = KrbCodec.decode(paEntry.getPaDataValue(), PaFxFastRequest.class);
                    }
                    catch (KrbException e) {
                        final String errMessage = "Decode PaFxFastRequest failed. " + e.getMessage();
                        KdcRequest.LOG.error(errMessage);
                        throw new KrbException(errMessage);
                    }
                    fastArmoredReq = paFxFastRequest.getFastArmoredReq();
                    final KrbFastArmor fastArmor = fastArmoredReq.getArmor();
                    if (fastArmor == null) {
                        return;
                    }
                    try {
                        this.armorApRequest(fastArmor);
                    }
                    catch (KrbException e2) {
                        final String errMessage2 = "Get armor key failed. " + e2.getMessage();
                        KdcRequest.LOG.error(errMessage2);
                        throw new KrbException(errMessage2);
                    }
                    if (this.getArmorKey() == null) {
                        return;
                    }
                    final EncryptedData encryptedData = fastArmoredReq.getEncryptedFastReq();
                    KrbFastReq fastReq;
                    try {
                        fastReq = KrbCodec.decode(EncryptionHandler.decrypt(encryptedData, this.getArmorKey(), KeyUsage.FAST_ENC), KrbFastReq.class);
                    }
                    catch (KrbException e3) {
                        final String errMessage3 = "Decode KrbFastReq failed. " + e3.getMessage();
                        KdcRequest.LOG.error(errMessage3);
                        throw new KrbException(errMessage3);
                    }
                    try {
                        this.innerBodyout = KrbCodec.encode(fastReq.getKdcReqBody());
                    }
                    catch (KrbException e3) {
                        final String errMessage3 = "Encode KdcReqBody failed. " + e3.getMessage();
                        KdcRequest.LOG.error(errMessage3);
                        throw new KrbException(errMessage3);
                    }
                    final CheckSum checkSum = fastArmoredReq.getReqChecksum();
                    if (checkSum == null) {
                        KdcRequest.LOG.warn("Checksum is empty.");
                        throw new KrbException(KrbErrorCode.KDC_ERR_PA_CHECKSUM_MUST_BE_INCLUDED);
                    }
                    byte[] reqBody;
                    try {
                        reqBody = KrbCodec.encode(this.getKdcReq().getReqBody());
                    }
                    catch (KrbException e4) {
                        final String errMessage4 = "Encode the ReqBody failed. " + e4.getMessage();
                        KdcRequest.LOG.error(errMessage4);
                        throw new KrbException(errMessage4);
                    }
                    try {
                        CheckSumHandler.verifyWithKey(checkSum, reqBody, this.getArmorKey().getKeyData(), KeyUsage.FAST_REQ_CHKSUM);
                    }
                    catch (KrbException e4) {
                        final String errMessage4 = "Verify the ReqBody failed. " + e4.getMessage();
                        KdcRequest.LOG.error(errMessage4);
                        throw new KrbException(errMessage4);
                    }
                }
            }
        }
    }
    
    private void armorApRequest(final KrbFastArmor fastArmor) throws KrbException {
        if (fastArmor.getArmorType() == ArmorType.ARMOR_AP_REQUEST) {
            ApReq apReq;
            try {
                apReq = KrbCodec.decode(fastArmor.getArmorValue(), ApReq.class);
            }
            catch (KrbException e) {
                final String errMessage = "Decode ApReq failed. " + e.getMessage();
                KdcRequest.LOG.error(errMessage);
                throw new KrbException(errMessage);
            }
            final Ticket ticket = apReq.getTicket();
            final EncryptionType encType = ticket.getEncryptedEncPart().getEType();
            final EncryptionKey tgsKey = this.getTgsEntry().getKeys().get(encType);
            if (ticket.getTktvno() != 5) {
                throw new KrbException(KrbErrorCode.KRB_AP_ERR_BADVERSION);
            }
            EncTicketPart encPart = null;
            try {
                encPart = EncryptionUtil.unseal(ticket.getEncryptedEncPart(), tgsKey, KeyUsage.KDC_REP_TICKET, EncTicketPart.class);
            }
            catch (KrbException e2) {
                final String errMessage2 = "Unseal EncTicketPart failed. " + e2.getMessage();
                KdcRequest.LOG.error(errMessage2);
                throw new KrbException(errMessage2);
            }
            ticket.setEncPart(encPart);
            final EncryptionKey encKey = ticket.getEncPart().getKey();
            this.setSessionKey(encKey);
            Authenticator authenticator = null;
            try {
                authenticator = EncryptionUtil.unseal(apReq.getEncryptedAuthenticator(), encKey, KeyUsage.AP_REQ_AUTH, Authenticator.class);
            }
            catch (KrbException e3) {
                final String errMessage3 = "Unseal Authenticator failed. " + e3.getMessage();
                KdcRequest.LOG.error(errMessage3);
                throw new KrbException(errMessage3);
            }
            EncryptionKey armorKey = null;
            try {
                armorKey = FastUtil.cf2(authenticator.getSubKey(), "subkeyarmor", encKey, "ticketarmor");
            }
            catch (KrbException e4) {
                final String errMessage4 = "Create armor key failed. " + e4.getMessage();
                KdcRequest.LOG.error(errMessage4);
                throw new KrbException(errMessage4);
            }
            this.setArmorKey(armorKey);
        }
    }
    
    public KrbIdentity getTgsEntry() {
        return this.tgsEntry;
    }
    
    public void setTgsEntry(final KrbIdentity tgsEntry) {
        this.tgsEntry = tgsEntry;
    }
    
    public boolean isTcp() {
        return this.isTcp;
    }
    
    public void isTcp(final boolean isTcp) {
        this.isTcp = isTcp;
    }
    
    public KrbMessage getReply() {
        return this.reply;
    }
    
    public void setReply(final KdcRep reply) {
        this.reply = reply;
    }
    
    public InetAddress getClientAddress() {
        return this.clientAddress;
    }
    
    public void setClientAddress(final InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }
    
    public EncryptionType getEncryptionType() {
        return this.encryptionType;
    }
    
    public void setEncryptionType(final EncryptionType encryptionType) {
        this.encryptionType = encryptionType;
    }
    
    public Ticket getTicket() {
        return this.ticket;
    }
    
    public void setTicket(final Ticket ticket) {
        this.ticket = ticket;
    }
    
    public boolean isPreAuthenticated() {
        return this.isPreAuthenticated;
    }
    
    public void setPreAuthenticated(final boolean isPreAuthenticated) {
        this.isPreAuthenticated = isPreAuthenticated;
    }
    
    public KrbIdentity getServerEntry() {
        return this.serverEntry;
    }
    
    public void setServerEntry(final KrbIdentity serverEntry) {
        this.serverEntry = serverEntry;
    }
    
    public KrbIdentity getClientEntry() {
        return this.clientEntry;
    }
    
    public void setClientEntry(final KrbIdentity clientEntry) {
        this.clientEntry = clientEntry;
    }
    
    public EncryptionKey getClientKey(final EncryptionType encType) throws KrbException {
        return this.getClientEntry().getKey(encType);
    }
    
    public EncryptionKey getClientKey() {
        return this.clientKey;
    }
    
    public void setClientKey(final EncryptionKey clientKey) {
        this.clientKey = clientKey;
    }
    
    public EncryptionKey getServerKey() {
        return this.serverKey;
    }
    
    public void setServerKey(final EncryptionKey serverKey) {
        this.serverKey = serverKey;
    }
    
    public PrincipalName getTgsPrincipal() {
        final PrincipalName result = KrbUtil.makeTgsPrincipal(this.kdcContext.getKdcRealm());
        return result;
    }
    
    protected abstract void makeReply() throws KrbException;
    
    protected void checkVersion() throws KrbException {
        final KdcReq request = this.getKdcReq();
        final int kerberosVersion = request.getPvno();
        if (kerberosVersion != 5) {
            KdcRequest.LOG.warn("Kerberos version: " + kerberosVersion + " should equal to " + 5);
            throw new KrbException(KrbErrorCode.KDC_ERR_BAD_PVNO);
        }
    }
    
    protected void checkPolicy() throws KrbException {
        final KrbIdentity entry = this.getClientEntry();
        if (entry != null) {
            if (entry.isDisabled()) {
                KdcRequest.LOG.warn("Client entry " + entry.getPrincipalName() + " is disabled.");
                throw new KrbException(KrbErrorCode.KDC_ERR_CLIENT_REVOKED);
            }
            if (entry.isLocked()) {
                KdcRequest.LOG.warn("Client entry " + entry.getPrincipalName() + " is expired.");
                throw new KrbException(KrbErrorCode.KDC_ERR_CLIENT_REVOKED);
            }
            if (entry.getExpireTime().lessThan(new Date().getTime())) {
                throw new KrbException(KrbErrorCode.KDC_ERR_CLIENT_REVOKED);
            }
        }
        else {
            KdcRequest.LOG.info("Client entry is empty.");
        }
    }
    
    protected abstract void checkClient() throws KrbException;
    
    protected void preauth() throws KrbException {
        final KdcReq request = this.getKdcReq();
        final PaData preAuthData = request.getPaData();
        if (this.isAnonymous && !this.isPkinit) {
            KdcRequest.LOG.info("Need PKINIT.");
            final KrbError krbError = this.makePreAuthenticationError(this.kdcContext, request, KrbErrorCode.KDC_ERR_PREAUTH_REQUIRED, true);
            throw new KdcRecoverableException(krbError);
        }
        if (preAuthData == null || preAuthData.isEmpty()) {
            KdcRequest.LOG.info("The preauth data is empty.");
            final KrbError krbError = this.makePreAuthenticationError(this.kdcContext, request, KrbErrorCode.KDC_ERR_PREAUTH_REQUIRED, false);
            throw new KdcRecoverableException(krbError);
        }
        this.getPreauthHandler().verify(this, preAuthData);
        this.setPreAuthenticated(true);
    }
    
    protected void setPreauthRequired(final boolean preauthRequired) {
        this.preauthContext.setPreauthRequired(preauthRequired);
    }
    
    protected boolean isPreauthRequired() {
        return this.preauthContext.isPreauthRequired();
    }
    
    protected PreauthHandler getPreauthHandler() {
        return this.kdcContext.getPreauthHandler();
    }
    
    protected void checkEncryptionType() throws KrbException {
        final List<EncryptionType> requestedTypes = this.getKdcReq().getReqBody().getEtypes();
        final EncryptionType bestType = EncryptionUtil.getBestEncryptionType(requestedTypes, this.kdcContext.getConfig().getEncryptionTypes());
        if (bestType == null) {
            KdcRequest.LOG.error("Can't get the best encryption type.");
            throw new KrbException(KrbErrorCode.KDC_ERR_ETYPE_NOSUPP);
        }
        this.setEncryptionType(bestType);
    }
    
    protected void authenticate() throws KrbException {
        this.checkEncryptionType();
        this.checkPolicy();
    }
    
    protected abstract void issueTicket() throws KrbException;
    
    private void checkServer() throws KrbException {
        final KdcReq request = this.getKdcReq();
        final PrincipalName principal = request.getReqBody().getSname();
        String serverRealm = request.getReqBody().getRealm();
        if (serverRealm == null || serverRealm.isEmpty()) {
            KdcRequest.LOG.info("Can't get the server realm from request, and try to get from kdcContext.");
            serverRealm = this.kdcContext.getKdcRealm();
        }
        principal.setRealm(serverRealm);
        final KrbIdentity serverEntry = this.getEntry(principal.getName());
        if (serverEntry == null) {
            KdcRequest.LOG.error("Principal: " + principal.getName() + " is not known");
            throw new KrbException(KrbErrorCode.KDC_ERR_S_PRINCIPAL_UNKNOWN);
        }
        this.setServerEntry(serverEntry);
        for (final EncryptionType encType : request.getReqBody().getEtypes()) {
            if (serverEntry.getKeys().containsKey(encType)) {
                final EncryptionKey serverKey = serverEntry.getKeys().get(encType);
                this.setServerKey(serverKey);
                break;
            }
        }
    }
    
    protected KrbError makePreAuthenticationError(final KdcContext kdcContext, final KdcReq request, final KrbErrorCode errorCode, final boolean pkinit) throws KrbException {
        final List<EncryptionType> encryptionTypes = kdcContext.getConfig().getEncryptionTypes();
        final List<EncryptionType> clientEtypes = request.getReqBody().getEtypes();
        final boolean isNewEtype = true;
        final EtypeInfo2 eTypeInfo2 = new EtypeInfo2();
        final EtypeInfo eTypeInfo3 = new EtypeInfo();
        for (final EncryptionType encryptionType : encryptionTypes) {
            if (clientEtypes.contains(encryptionType)) {
                if (!isNewEtype) {
                    final EtypeInfoEntry etypeInfoEntry = new EtypeInfoEntry();
                    etypeInfoEntry.setEtype(encryptionType);
                    etypeInfoEntry.setSalt(null);
                    eTypeInfo3.add(etypeInfoEntry);
                }
                final EtypeInfo2Entry etypeInfo2Entry = new EtypeInfo2Entry();
                etypeInfo2Entry.setEtype(encryptionType);
                eTypeInfo2.add(etypeInfo2Entry);
            }
        }
        byte[] encTypeInfo = null;
        byte[] encTypeInfo2 = null;
        if (!isNewEtype) {
            encTypeInfo = KrbCodec.encode(eTypeInfo3);
        }
        encTypeInfo2 = KrbCodec.encode(eTypeInfo2);
        final MethodData methodData = new MethodData();
        if (!isNewEtype) {
            methodData.add(new PaDataEntry(PaDataType.ETYPE_INFO, encTypeInfo));
        }
        methodData.add(new PaDataEntry(PaDataType.ETYPE_INFO2, encTypeInfo2));
        if (pkinit) {
            methodData.add(new PaDataEntry(PaDataType.PK_AS_REQ, "empty".getBytes()));
            methodData.add(new PaDataEntry(PaDataType.PK_AS_REP, "empty".getBytes()));
        }
        final KrbError krbError = new KrbError();
        krbError.setErrorCode(errorCode);
        final byte[] encodedData = KrbCodec.encode(methodData);
        krbError.setEdata(encodedData);
        return krbError;
    }
    
    protected KrbIdentity getEntry(final String principal) throws KrbException {
        final KrbIdentity entry = this.kdcContext.getIdentityService().getIdentity(principal);
        return entry;
    }
    
    protected ByteBuffer getRequestBody() throws KrbException {
        return null;
    }
    
    public EncryptionKey getArmorKey() throws KrbException {
        return this.fastContext.getArmorKey();
    }
    
    protected void setArmorKey(final EncryptionKey armorKey) {
        this.fastContext.setArmorKey(armorKey);
    }
    
    public PrincipalName getClientPrincipal() {
        return this.clientPrincipal;
    }
    
    public void setClientPrincipal(final PrincipalName clientPrincipal) {
        this.clientPrincipal = clientPrincipal;
    }
    
    public PrincipalName getServerPrincipal() {
        return this.serverPrincipal;
    }
    
    public void setServerPrincipal(final PrincipalName serverPrincipal) {
        this.serverPrincipal = serverPrincipal;
    }
    
    protected byte[] getInnerBodyout() {
        return this.innerBodyout;
    }
    
    protected boolean isToken() {
        return this.isToken;
    }
    
    public boolean isHttps() {
        return this.isHttps;
    }
    
    public void setHttps(final boolean https) {
        this.isHttps = https;
    }
    
    public void setToken(final AuthToken authToken) {
        this.token = authToken;
    }
    
    protected AuthToken getToken() {
        return this.token;
    }
    
    protected boolean isPkinit() {
        return this.isPkinit;
    }
    
    public boolean isAnonymous() {
        return this.getKdcOptions().isFlagSet(KdcOption.REQUEST_ANONYMOUS);
    }
    
    public KdcOptions getKdcOptions() {
        return this.kdcReq.getReqBody().getKdcOptions();
    }
    
    public void setReqPackage(final ByteBuffer reqPackage) {
        this.reqPackage = reqPackage;
    }
    
    public ByteBuffer getReqPackage() {
        return this.reqPackage;
    }
    
    static {
        LOG = LoggerFactory.getLogger(KdcRequest.class);
    }
}
