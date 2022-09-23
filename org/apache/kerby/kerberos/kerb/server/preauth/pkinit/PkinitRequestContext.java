// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.preauth.pkinit;

import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.AuthPack;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;

public class PkinitRequestContext implements PluginRequestContext
{
    public AuthPack authPack;
    public PaDataType paType;
}
