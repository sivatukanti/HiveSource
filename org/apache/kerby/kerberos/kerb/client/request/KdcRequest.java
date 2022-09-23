// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.kerberos.kerb.client.KrbKdcOption;
import org.apache.kerby.kerberos.kerb.client.KrbOptionGroup;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcOption;
import org.apache.kerby.kerberos.kerb.client.preauth.PreauthHandler;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.crypto.EncryptionHandler;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import java.util.Iterator;
import org.apache.kerby.kerberos.kerb.type.base.HostAddresses;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.KrbOption;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.KrbException;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.client.preauth.KrbFastRequestState;
import org.apache.kerby.kerberos.kerb.client.preauth.PreauthContext;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcRep;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReqBody;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcOptions;
import org.apache.kerby.kerberos.kerb.type.base.HostAddress;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.client.KrbContext;
import java.util.Map;

public abstract class KdcRequest
{
    protected Map<String, Object> credCache;
    private KrbContext context;
    private Object sessionData;
    private KOptions requestOptions;
    private PrincipalName serverPrincipal;
    private List<HostAddress> hostAddresses;
    private KdcOptions kdcOptions;
    private List<EncryptionType> encryptionTypes;
    private EncryptionType chosenEncryptionType;
    private int chosenNonce;
    private KdcReq kdcReq;
    private KdcReqBody reqBody;
    private KdcRep kdcRep;
    private PreauthContext preauthContext;
    private KrbFastRequestState fastRequestState;
    private EncryptionKey asKey;
    private byte[] outerRequestBody;
    private boolean isRetrying;
    
    public KdcRequest(final KrbContext context) {
        this.hostAddresses = new ArrayList<HostAddress>();
        this.kdcOptions = new KdcOptions();
        this.context = context;
        this.isRetrying = false;
        this.credCache = new HashMap<String, Object>();
        this.preauthContext = context.getPreauthHandler().preparePreauthContext(this);
        this.fastRequestState = new KrbFastRequestState();
    }
    
    public KrbFastRequestState getFastRequestState() {
        return this.fastRequestState;
    }
    
    public void setFastRequestState(final KrbFastRequestState state) {
        this.fastRequestState = state;
    }
    
    public byte[] getOuterRequestBody() {
        return this.outerRequestBody.clone();
    }
    
    public void setOuterRequestBody(final byte[] outerRequestBody) {
        this.outerRequestBody = outerRequestBody.clone();
    }
    
    public Object getSessionData() {
        return this.sessionData;
    }
    
    public void setSessionData(final Object sessionData) {
        this.sessionData = sessionData;
    }
    
    public KOptions getRequestOptions() {
        return this.requestOptions;
    }
    
    public void setRequestOptions(final KOptions options) {
        this.requestOptions = options;
    }
    
    public boolean isRetrying() {
        return this.isRetrying;
    }
    
    public EncryptionKey getAsKey() throws KrbException {
        return this.asKey;
    }
    
    public void setAsKey(final EncryptionKey asKey) {
        this.asKey = asKey;
    }
    
    public void setAllowedPreauth(final PaDataType paType) {
        this.preauthContext.setAllowedPaType(paType);
    }
    
    public Map<String, Object> getCredCache() {
        return this.credCache;
    }
    
    public void setPreauthRequired(final boolean preauthRequired) {
        this.preauthContext.setPreauthRequired(preauthRequired);
    }
    
    public void resetPrequthContxt() {
        this.preauthContext.reset();
    }
    
    public PreauthContext getPreauthContext() {
        return this.preauthContext;
    }
    
    public KdcReq getKdcReq() {
        return this.kdcReq;
    }
    
    public void setKdcReq(final KdcReq kdcReq) {
        this.kdcReq = kdcReq;
    }
    
    protected KdcReqBody getReqBody(final KerberosTime renewTill) throws KrbException {
        if (this.reqBody == null) {
            this.reqBody = this.makeReqBody(renewTill);
        }
        return this.reqBody;
    }
    
    public KdcRep getKdcRep() {
        return this.kdcRep;
    }
    
    public void setKdcRep(final KdcRep kdcRep) {
        this.kdcRep = kdcRep;
    }
    
    protected KdcReqBody makeReqBody(final KerberosTime renewTill) throws KrbException {
        final KdcReqBody body = new KdcReqBody();
        final long startTime = System.currentTimeMillis();
        body.setFrom(new KerberosTime(startTime));
        final PrincipalName cName = this.getClientPrincipal();
        body.setCname(cName);
        body.setRealm(this.getContext().getKrbSetting().getKdcRealm());
        final PrincipalName sName = this.getServerPrincipal();
        body.setSname(sName);
        body.setTill(new KerberosTime(startTime + this.getTicketValidTime()));
        KerberosTime rtime;
        if (renewTill != null) {
            rtime = renewTill;
        }
        else {
            long renewLifetime;
            if (this.getRequestOptions().contains(KrbOption.RENEWABLE_TIME)) {
                renewLifetime = this.getRequestOptions().getIntegerOption(KrbOption.RENEWABLE_TIME);
            }
            else {
                renewLifetime = this.getContext().getKrbSetting().getKrbConfig().getRenewLifetime();
            }
            rtime = new KerberosTime(startTime + renewLifetime * 1000L);
        }
        body.setRtime(rtime);
        final int nonce = this.generateNonce();
        body.setNonce(nonce);
        this.setChosenNonce(nonce);
        body.setKdcOptions(this.getKdcOptions());
        final HostAddresses addresses = this.getHostAddresses();
        if (addresses != null) {
            body.setAddresses(addresses);
        }
        body.setEtypes(this.getEncryptionTypes());
        return body;
    }
    
    public KdcOptions getKdcOptions() {
        return this.kdcOptions;
    }
    
    public void setKdcOptions(final KdcOptions kdcOptions) {
        this.kdcOptions = kdcOptions;
    }
    
    public HostAddresses getHostAddresses() {
        HostAddresses addresses = null;
        if (!this.hostAddresses.isEmpty()) {
            addresses = new HostAddresses();
            for (final HostAddress ha : this.hostAddresses) {
                addresses.addElement(ha);
            }
        }
        return addresses;
    }
    
    public void setHostAddresses(final List<HostAddress> hostAddresses) {
        this.hostAddresses = hostAddresses;
    }
    
    public KrbContext getContext() {
        return this.context;
    }
    
    public void setContext(final KrbContext context) {
        this.context = context;
    }
    
    protected byte[] decryptWithClientKey(final EncryptedData data, final KeyUsage usage) throws KrbException {
        final EncryptionKey tmpKey = this.getClientKey();
        if (tmpKey == null) {
            throw new KrbException("Client key isn't availalbe");
        }
        return EncryptionHandler.decrypt(data, tmpKey, usage);
    }
    
    public abstract PrincipalName getClientPrincipal();
    
    public PrincipalName getServerPrincipal() {
        return this.serverPrincipal;
    }
    
    public void setServerPrincipal(final PrincipalName serverPrincipal) {
        this.serverPrincipal = serverPrincipal;
    }
    
    public List<EncryptionType> getEncryptionTypes() {
        if (this.encryptionTypes == null) {
            this.encryptionTypes = this.context.getConfig().getEncryptionTypes();
        }
        return EncryptionUtil.orderEtypesByStrength(this.encryptionTypes);
    }
    
    public void setEncryptionTypes(final List<EncryptionType> encryptionTypes) {
        this.encryptionTypes = encryptionTypes;
    }
    
    public EncryptionType getChosenEncryptionType() {
        return this.chosenEncryptionType;
    }
    
    public void setChosenEncryptionType(final EncryptionType chosenEncryptionType) {
        this.chosenEncryptionType = chosenEncryptionType;
    }
    
    public int generateNonce() {
        return this.context.generateNonce();
    }
    
    public int getChosenNonce() {
        return this.chosenNonce;
    }
    
    public void setChosenNonce(final int nonce) {
        this.chosenNonce = nonce;
    }
    
    public abstract EncryptionKey getClientKey() throws KrbException;
    
    public long getTicketValidTime() {
        if (this.getRequestOptions().contains(KrbOption.LIFE_TIME)) {
            return this.getRequestOptions().getIntegerOption(KrbOption.LIFE_TIME) * 1000;
        }
        return this.context.getTicketValidTime();
    }
    
    public KerberosTime getTicketTillTime() {
        final long now = System.currentTimeMillis();
        return new KerberosTime(now - 694967296L);
    }
    
    public void addHost(final String hostNameOrIpAddress) throws UnknownHostException {
        final InetAddress address = InetAddress.getByName(hostNameOrIpAddress);
        this.hostAddresses.add(new HostAddress(address));
    }
    
    public void process() throws KrbException {
        this.processKdcOptions();
        this.preauth();
    }
    
    public abstract void processResponse(final KdcRep p0) throws KrbException;
    
    public KOptions getPreauthOptions() {
        return new KOptions();
    }
    
    protected void preauth() throws KrbException {
        final List<EncryptionType> etypes = this.getEncryptionTypes();
        if (etypes.isEmpty()) {
            throw new KrbException("No encryption type is configured and available");
        }
        final EncryptionType encryptionType = etypes.iterator().next();
        this.setChosenEncryptionType(encryptionType);
        this.getPreauthHandler().preauth(this);
    }
    
    protected PreauthHandler getPreauthHandler() {
        return this.getContext().getPreauthHandler();
    }
    
    public void needAsKey() throws KrbException {
        final EncryptionKey clientKey = this.getClientKey();
        if (clientKey == null) {
            throw new RuntimeException("Client key should be prepared or prompted at this time!");
        }
        this.setAsKey(clientKey);
    }
    
    public EncryptionType getEncType() {
        return this.getChosenEncryptionType();
    }
    
    public void askQuestion(final String question, final String challenge) {
        this.preauthContext.getUserResponser().askQuestion(question, challenge);
    }
    
    public EncryptionKey getArmorKey() {
        return this.fastRequestState.getArmorKey();
    }
    
    public KerberosTime getPreauthTime() {
        return KerberosTime.now();
    }
    
    public Object getCacheValue(final String key) {
        return this.credCache.get(key);
    }
    
    public void cacheValue(final String key, final Object value) {
        this.credCache.put(key, value);
    }
    
    protected void processKdcOptions() {
        this.kdcOptions.setFlag(KdcOption.FORWARDABLE);
        this.kdcOptions.setFlag(KdcOption.PROXIABLE);
        this.kdcOptions.setFlag(KdcOption.RENEWABLE_OK);
        for (final KOption kOpt : this.requestOptions.getOptions()) {
            if (kOpt.getOptionInfo().getGroup() == KrbOptionGroup.KDC_FLAGS) {
                KrbKdcOption krbKdcOption = (KrbKdcOption)kOpt;
                boolean flagValue = this.requestOptions.getBooleanOption(kOpt, true);
                if (kOpt.equals(KrbKdcOption.NOT_FORWARDABLE)) {
                    krbKdcOption = KrbKdcOption.FORWARDABLE;
                    flagValue = !flagValue;
                }
                if (kOpt.equals(KrbKdcOption.NOT_PROXIABLE)) {
                    krbKdcOption = KrbKdcOption.PROXIABLE;
                    flagValue = !flagValue;
                }
                final KdcOption kdcOption = KdcOption.valueOf(krbKdcOption.name());
                this.kdcOptions.setFlag(kdcOption, flagValue);
            }
        }
    }
}
