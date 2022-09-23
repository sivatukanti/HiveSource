// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt;

import com.nimbusds.jose.util.Base64URL;
import java.text.ParseException;
import com.nimbusds.jose.Header;
import java.io.Serializable;

public interface JWT extends Serializable
{
    Header getHeader();
    
    JWTClaimsSet getJWTClaimsSet() throws ParseException;
    
    Base64URL[] getParsedParts();
    
    String getParsedString();
    
    String serialize();
}
