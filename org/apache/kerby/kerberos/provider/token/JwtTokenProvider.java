// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.provider.token;

import org.apache.kerby.kerberos.kerb.type.base.AuthToken;
import org.apache.kerby.kerberos.kerb.provider.TokenFactory;
import org.apache.kerby.kerberos.kerb.provider.TokenDecoder;
import org.apache.kerby.kerberos.kerb.provider.TokenEncoder;
import org.apache.kerby.kerberos.kerb.provider.TokenProvider;

public class JwtTokenProvider implements TokenProvider
{
    @Override
    public TokenEncoder createTokenEncoder() {
        return new JwtTokenEncoder();
    }
    
    @Override
    public TokenDecoder createTokenDecoder() {
        return new JwtTokenDecoder();
    }
    
    @Override
    public TokenFactory createTokenFactory() {
        return new TokenFactory() {
            @Override
            public AuthToken createToken() {
                return new JwtAuthToken();
            }
        };
    }
}
