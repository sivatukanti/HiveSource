// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.preauth;

import org.apache.kerby.kerberos.kerb.preauth.PaFlags;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.server.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.server.KdcContext;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;

public interface KdcPreauth extends PreauthPluginMeta
{
    void initWith(final KdcContext p0);
    
    PluginRequestContext initRequestContext(final KdcRequest p0);
    
    void provideEdata(final KdcRequest p0, final PluginRequestContext p1, final PaData p2) throws KrbException;
    
    boolean verify(final KdcRequest p0, final PluginRequestContext p1, final PaDataEntry p2) throws KrbException;
    
    void providePaData(final KdcRequest p0, final PluginRequestContext p1, final PaData p2);
    
    PaFlags getFlags(final KdcRequest p0, final PluginRequestContext p1, final PaDataType p2);
    
    void destroy();
}
