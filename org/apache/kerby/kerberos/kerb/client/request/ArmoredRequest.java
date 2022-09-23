// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.kerberos.kerb.type.kdc.KdcReqBody;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.crypto.EncryptionHandler;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.crypto.fast.FastUtil;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.ap.Authenticator;
import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import org.apache.kerby.kerberos.kerb.type.ap.ApOptions;
import org.apache.kerby.kerberos.kerb.type.ap.ApReq;
import org.apache.kerby.kerberos.kerb.type.fast.ArmorType;
import org.apache.kerby.kerberos.kerb.type.fast.KrbFastArmor;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.common.CheckSumUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.type.fast.KrbFastArmoredReq;
import org.apache.kerby.kerberos.kerb.type.fast.PaFxFastRequest;
import org.apache.kerby.kerberos.kerb.type.fast.KrbFastReq;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.type.kdc.AsReq;
import org.apache.kerby.kerberos.kerb.ccache.CredentialCache;
import java.io.IOException;
import java.io.File;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.KrbOption;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.client.preauth.KrbFastRequestState;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.ccache.Credential;

public class ArmoredRequest
{
    private Credential credential;
    private EncryptionKey subKey;
    private EncryptionKey armorCacheKey;
    private KdcRequest kdcRequest;
    
    public ArmoredRequest(final KdcRequest kdcRequest) {
        this.kdcRequest = kdcRequest;
    }
    
    public void process() throws KrbException {
        final KdcReq kdcReq = this.kdcRequest.getKdcReq();
        final KrbFastRequestState state = this.kdcRequest.getFastRequestState();
        this.fastAsArmor(state, this.kdcRequest.getArmorKey(), this.subKey, this.credential, kdcReq);
        this.kdcRequest.setFastRequestState(state);
        this.kdcRequest.setOuterRequestBody(KrbCodec.encode(state.getFastOuterRequest()));
        kdcReq.getPaData().addElement(this.makeFastEntry(state, kdcReq, this.kdcRequest.getOuterRequestBody()));
    }
    
    protected void preauth() throws KrbException {
        final KOptions preauthOptions = this.getPreauthOptions();
        this.getCredential(preauthOptions);
        this.armorCacheKey = this.getArmorCacheKey(this.credential);
        this.subKey = this.getSubKey(this.armorCacheKey.getKeyType());
        final EncryptionKey armorKey = this.makeArmorKey(this.subKey, this.armorCacheKey);
        this.kdcRequest.getFastRequestState().setArmorKey(armorKey);
    }
    
    private void getCredential(final KOptions kOptions) throws KrbException {
        if (kOptions.contains(KrbOption.ARMOR_CACHE)) {
            final String ccache = kOptions.getStringOption(KrbOption.ARMOR_CACHE);
            this.credential = this.getCredentialFromFile(ccache);
        }
        else if (kOptions.contains(KrbOption.TGT)) {
            final TgtTicket tgt = (TgtTicket)kOptions.getOptionValue(KrbOption.TGT);
            this.credential = new Credential(tgt);
        }
    }
    
    public KOptions getPreauthOptions() {
        final KOptions results = new KOptions();
        final KOptions krbOptions = this.kdcRequest.getRequestOptions();
        if (krbOptions.contains(KrbOption.ARMOR_CACHE)) {
            results.add(krbOptions.getOption(KrbOption.ARMOR_CACHE));
        }
        else if (krbOptions.contains(KrbOption.TGT)) {
            results.add(krbOptions.getOption(KrbOption.TGT));
        }
        return results;
    }
    
    public EncryptionKey getClientKey() throws KrbException {
        return this.kdcRequest.getFastRequestState().getArmorKey();
    }
    
    public EncryptionKey getArmorCacheKey() {
        return this.armorCacheKey;
    }
    
    private Credential getCredentialFromFile(final String ccache) throws KrbException {
        final File ccacheFile = new File(ccache);
        CredentialCache cc = null;
        try {
            cc = resolveCredCache(ccacheFile);
        }
        catch (IOException e) {
            throw new KrbException("Failed to load armor cache file");
        }
        return cc.getCredentials().iterator().next();
    }
    
    private static CredentialCache resolveCredCache(final File ccacheFile) throws IOException {
        final CredentialCache cc = new CredentialCache();
        cc.load(ccacheFile);
        return cc;
    }
    
    private void fastAsArmor(final KrbFastRequestState state, final EncryptionKey armorKey, final EncryptionKey subKey, final Credential credential, final KdcReq kdcReq) throws KrbException {
        state.setArmorKey(armorKey);
        state.setFastArmor(this.fastArmorApRequest(subKey, credential));
        final KdcReq fastOuterRequest = new AsReq();
        fastOuterRequest.setReqBody(kdcReq.getReqBody());
        fastOuterRequest.setPaData(null);
        state.setFastOuterRequest(fastOuterRequest);
    }
    
    private PaDataEntry makeFastEntry(final KrbFastRequestState state, final KdcReq kdcReq, final byte[] outerRequestBody) throws KrbException {
        final KrbFastReq fastReq = new KrbFastReq();
        fastReq.setKdcReqBody(kdcReq.getReqBody());
        fastReq.setFastOptions(state.getFastOptions());
        final PaFxFastRequest paFxFastRequest = new PaFxFastRequest();
        final KrbFastArmoredReq armoredReq = new KrbFastArmoredReq();
        armoredReq.setArmor(state.getFastArmor());
        final CheckSum reqCheckSum = CheckSumUtil.makeCheckSumWithKey(CheckSumType.NONE, outerRequestBody, state.getArmorKey(), KeyUsage.FAST_REQ_CHKSUM);
        armoredReq.setReqChecksum(reqCheckSum);
        armoredReq.setEncryptedFastReq(EncryptionUtil.seal(fastReq, state.getArmorKey(), KeyUsage.FAST_ENC));
        paFxFastRequest.setFastArmoredReq(armoredReq);
        final PaDataEntry paDataEntry = new PaDataEntry();
        paDataEntry.setPaDataType(PaDataType.FX_FAST);
        paDataEntry.setPaDataValue(KrbCodec.encode(paFxFastRequest));
        return paDataEntry;
    }
    
    private KrbFastArmor fastArmorApRequest(final EncryptionKey subKey, final Credential credential) throws KrbException {
        final KrbFastArmor fastArmor = new KrbFastArmor();
        fastArmor.setArmorType(ArmorType.ARMOR_AP_REQUEST);
        final ApReq apReq = this.makeApReq(subKey, credential);
        fastArmor.setArmorValue(KrbCodec.encode(apReq));
        return fastArmor;
    }
    
    private ApReq makeApReq(final EncryptionKey subKey, final Credential credential) throws KrbException {
        final ApReq apReq = new ApReq();
        final ApOptions apOptions = new ApOptions();
        apReq.setApOptions(apOptions);
        final Ticket ticket = credential.getTicket();
        apReq.setTicket(ticket);
        final Authenticator authenticator = this.makeAuthenticator(credential, subKey);
        apReq.setAuthenticator(authenticator);
        final EncryptedData authnData = EncryptionUtil.seal(authenticator, credential.getKey(), KeyUsage.AP_REQ_AUTH);
        apReq.setEncryptedAuthenticator(authnData);
        return apReq;
    }
    
    private EncryptionKey makeArmorKey(final EncryptionKey subKey, final EncryptionKey armorCacheKey) throws KrbException {
        final EncryptionKey armorKey = FastUtil.makeArmorKey(subKey, armorCacheKey);
        return armorKey;
    }
    
    private EncryptionKey getSubKey(final EncryptionType type) throws KrbException {
        return EncryptionHandler.random2Key(type);
    }
    
    private EncryptionKey getArmorCacheKey(final Credential credential) throws KrbException {
        final EncryptionKey armorCacheKey = credential.getKey();
        return armorCacheKey;
    }
    
    protected Authenticator makeAuthenticator(final Credential credential, final EncryptionKey subKey) throws KrbException {
        final Authenticator authenticator = new Authenticator();
        authenticator.setAuthenticatorVno(5);
        authenticator.setCname(credential.getClientName());
        authenticator.setCrealm(credential.getClientRealm());
        authenticator.setCtime(KerberosTime.now());
        authenticator.setCusec(0);
        authenticator.setSubKey(subKey);
        final KdcReqBody reqBody = this.kdcRequest.getReqBody(null);
        final CheckSum checksum = CheckSumUtil.seal(reqBody, null, subKey, KeyUsage.TGS_REQ_AUTH_CKSUM);
        authenticator.setCksum(checksum);
        return authenticator;
    }
}
