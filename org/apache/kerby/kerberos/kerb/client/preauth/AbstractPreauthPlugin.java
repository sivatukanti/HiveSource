// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth;

import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.preauth.PaFlag;
import org.apache.kerby.kerberos.kerb.preauth.PaFlags;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.KOptions;
import java.util.Collections;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.List;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.client.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.client.KrbContext;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;

public class AbstractPreauthPlugin implements KrbPreauth
{
    private PreauthPluginMeta pluginMeta;
    protected KrbContext context;
    
    public AbstractPreauthPlugin(final PreauthPluginMeta meta) {
        this.pluginMeta = meta;
    }
    
    @Override
    public String getName() {
        return this.pluginMeta.getName();
    }
    
    @Override
    public int getVersion() {
        return this.pluginMeta.getVersion();
    }
    
    @Override
    public PaDataType[] getPaTypes() {
        return this.pluginMeta.getPaTypes();
    }
    
    @Override
    public void init(final KrbContext context) {
        this.context = context;
    }
    
    @Override
    public PluginRequestContext initRequestContext(final KdcRequest kdcRequest) {
        return null;
    }
    
    @Override
    public void prepareQuestions(final KdcRequest kdcRequest, final PluginRequestContext requestContext) throws KrbException {
        kdcRequest.needAsKey();
    }
    
    @Override
    public List<EncryptionType> getEncTypes(final KdcRequest kdcRequest, final PluginRequestContext requestContext) {
        return Collections.emptyList();
    }
    
    @Override
    public void setPreauthOptions(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final KOptions options) {
    }
    
    @Override
    public void tryFirst(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaData outPadata) throws KrbException {
    }
    
    @Override
    public boolean process(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataEntry inPadata, final PaData outPadata) throws KrbException {
        return false;
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
    
    @Override
    public void destroy() {
    }
}
