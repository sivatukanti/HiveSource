// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.preauth.builtin;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.server.request.TgsRequest;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.server.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;
import org.apache.kerby.kerberos.kerb.preauth.builtin.TgtPreauthMeta;
import org.apache.kerby.kerberos.kerb.server.preauth.AbstractPreauthPlugin;

public class TgtPreauth extends AbstractPreauthPlugin
{
    public TgtPreauth() {
        super(new TgtPreauthMeta());
    }
    
    @Override
    public boolean verify(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataEntry paData) throws KrbException {
        final TgsRequest tgsRequest = (TgsRequest)kdcRequest;
        tgsRequest.verifyAuthenticator(paData);
        return true;
    }
}
