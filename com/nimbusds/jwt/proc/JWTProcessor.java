// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt.proc;

import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import java.text.ParseException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jose.proc.SecurityContext;

public interface JWTProcessor<C extends SecurityContext>
{
    JWTClaimsSet process(final String p0, final C p1) throws ParseException, BadJOSEException, JOSEException;
    
    JWTClaimsSet process(final JWT p0, final C p1) throws BadJOSEException, JOSEException;
    
    JWTClaimsSet process(final PlainJWT p0, final C p1) throws BadJOSEException, JOSEException;
    
    JWTClaimsSet process(final SignedJWT p0, final C p1) throws BadJOSEException, JOSEException;
    
    JWTClaimsSet process(final EncryptedJWT p0, final C p1) throws BadJOSEException, JOSEException;
}
