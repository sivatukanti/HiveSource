// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.provider.token;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;

public class JwtUtil
{
    public static JWTClaimsSet from(final ReadOnlyJWTClaimsSet readOnlyClaims) {
        final JWTClaimsSet result = new JWTClaimsSet(readOnlyClaims);
        return result;
    }
}
