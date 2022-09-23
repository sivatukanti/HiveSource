// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth.token;

import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.pa.token.TokenInfo;
import org.apache.kerby.kerberos.kerb.type.base.KrbTokenBase;
import org.apache.kerby.kerberos.kerb.type.pa.token.PaTokenRequest;
import org.apache.kerby.kerberos.kerb.type.base.KrbToken;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.preauth.PaFlag;
import org.apache.kerby.kerberos.kerb.preauth.PaFlags;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.type.base.AuthToken;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.TokenOption;
import org.apache.kerby.KOptions;
import java.util.Collections;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.List;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.client.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.client.KrbContext;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;
import org.apache.kerby.kerberos.kerb.preauth.token.TokenPreauthMeta;
import org.apache.kerby.kerberos.kerb.client.preauth.AbstractPreauthPlugin;

public class TokenPreauth extends AbstractPreauthPlugin
{
    private TokenContext tokenContext;
    
    public TokenPreauth() {
        super(new TokenPreauthMeta());
    }
    
    @Override
    public void init(final KrbContext context) {
        super.init(context);
        this.tokenContext = new TokenContext();
    }
    
    @Override
    public PluginRequestContext initRequestContext(final KdcRequest kdcRequest) {
        final TokenRequestContext reqCtx = new TokenRequestContext();
        return reqCtx;
    }
    
    @Override
    public void prepareQuestions(final KdcRequest kdcRequest, final PluginRequestContext requestContext) {
    }
    
    @Override
    public List<EncryptionType> getEncTypes(final KdcRequest kdcRequest, final PluginRequestContext requestContext) {
        return Collections.emptyList();
    }
    
    @Override
    public void setPreauthOptions(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final KOptions options) {
        this.tokenContext.usingIdToken = options.getBooleanOption(TokenOption.USE_TOKEN, false);
        if (this.tokenContext.usingIdToken) {
            if (options.contains(TokenOption.USER_ID_TOKEN)) {
                this.tokenContext.token = (AuthToken)options.getOptionValue(TokenOption.USER_ID_TOKEN);
            }
        }
        else if (options.contains(TokenOption.USER_AC_TOKEN)) {
            this.tokenContext.token = (AuthToken)options.getOptionValue(TokenOption.USER_AC_TOKEN);
        }
    }
    
    @Override
    public void tryFirst(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaData outPadata) throws KrbException {
        if (kdcRequest.getAsKey() == null) {
            kdcRequest.needAsKey();
        }
        outPadata.addElement(this.makeEntry(kdcRequest));
    }
    
    @Override
    public boolean process(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataEntry inPadata, final PaData outPadata) throws KrbException {
        if (kdcRequest.getAsKey() == null) {
            kdcRequest.needAsKey();
        }
        outPadata.addElement(this.makeEntry(kdcRequest));
        return true;
    }
    
    @Override
    public boolean tryAgain(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataType preauthType, final PaData errPadata, final PaData outPadata) {
        return false;
    }
    
    @Override
    public PaFlags getFlags(final PaDataType paType) {
        final PaFlags paFlags = new PaFlags(0);
        paFlags.setFlag(PaFlag.PA_REAL);
        return paFlags;
    }
    
    private PaDataEntry makeEntry(final KdcRequest kdcRequest) throws KrbException {
        final KOptions options = kdcRequest.getPreauthOptions();
        final KOption idToken = options.getOption(TokenOption.USER_ID_TOKEN);
        final KOption acToken = options.getOption(TokenOption.USER_AC_TOKEN);
        KrbToken krbToken;
        if (idToken != null) {
            krbToken = (KrbToken)idToken.getOptionInfo().getValue();
        }
        else {
            if (acToken == null) {
                throw new KrbException("missing token.");
            }
            krbToken = (KrbToken)acToken.getOptionInfo().getValue();
        }
        final PaTokenRequest tokenPa = new PaTokenRequest();
        tokenPa.setToken(krbToken);
        final TokenInfo info = new TokenInfo();
        info.setTokenVendor(krbToken.getIssuer());
        tokenPa.setTokenInfo(info);
        final EncryptedData paDataValue = EncryptionUtil.seal(tokenPa, kdcRequest.getAsKey(), KeyUsage.PA_TOKEN);
        final PaDataEntry paDataEntry = new PaDataEntry();
        paDataEntry.setPaDataType(PaDataType.TOKEN_REQUEST);
        paDataEntry.setPaDataValue(KrbCodec.encode(paDataValue));
        return paDataEntry;
    }
}
