// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth.token;

import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;

public class TokenRequestContext implements PluginRequestContext
{
    public boolean doIdentityMatching;
    public PaDataType paType;
    public boolean identityInitialized;
    public boolean identityPrompted;
}
