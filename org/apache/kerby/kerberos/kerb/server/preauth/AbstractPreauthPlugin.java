// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.preauth;

import org.apache.kerby.kerberos.kerb.preauth.PaFlags;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.server.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.server.KdcContext;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;

public class AbstractPreauthPlugin implements KdcPreauth
{
    private PreauthPluginMeta pluginMeta;
    
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
    public void initWith(final KdcContext kdcContext) {
    }
    
    @Override
    public PluginRequestContext initRequestContext(final KdcRequest kdcRequest) {
        return null;
    }
    
    @Override
    public void provideEdata(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaData outPaData) throws KrbException {
    }
    
    @Override
    public boolean verify(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataEntry paData) throws KrbException {
        return false;
    }
    
    @Override
    public void providePaData(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaData paData) {
    }
    
    @Override
    public PaFlags getFlags(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataType paType) {
        return null;
    }
    
    @Override
    public void destroy() {
    }
}
