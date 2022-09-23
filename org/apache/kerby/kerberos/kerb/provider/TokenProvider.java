// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.provider;

public interface TokenProvider extends KrbProvider
{
    TokenEncoder createTokenEncoder();
    
    TokenDecoder createTokenDecoder();
    
    TokenFactory createTokenFactory();
}
