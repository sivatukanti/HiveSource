// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.provider;

import org.apache.kerby.kerberos.kerb.type.base.AuthToken;

public interface TokenFactory
{
    AuthToken createToken();
}
