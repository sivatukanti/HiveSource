// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth;

import org.apache.kerby.kerberos.kerb.preauth.PaFlags;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.List;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.client.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.client.KrbContext;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;

public interface KrbPreauth extends PreauthPluginMeta
{
    void init(final KrbContext p0);
    
    PluginRequestContext initRequestContext(final KdcRequest p0);
    
    void prepareQuestions(final KdcRequest p0, final PluginRequestContext p1) throws KrbException;
    
    List<EncryptionType> getEncTypes(final KdcRequest p0, final PluginRequestContext p1);
    
    void setPreauthOptions(final KdcRequest p0, final PluginRequestContext p1, final KOptions p2);
    
    void tryFirst(final KdcRequest p0, final PluginRequestContext p1, final PaData p2) throws KrbException;
    
    boolean process(final KdcRequest p0, final PluginRequestContext p1, final PaDataEntry p2, final PaData p3) throws KrbException;
    
    boolean tryAgain(final KdcRequest p0, final PluginRequestContext p1, final PaDataType p2, final PaData p3, final PaData p4);
    
    PaFlags getFlags(final PaDataType p0);
    
    void destroy();
}
