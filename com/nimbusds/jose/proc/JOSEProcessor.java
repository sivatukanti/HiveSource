// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.PlainObject;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
import com.nimbusds.jose.Payload;

public interface JOSEProcessor<C extends SecurityContext>
{
    Payload process(final String p0, final C p1) throws ParseException, BadJOSEException, JOSEException;
    
    Payload process(final JOSEObject p0, final C p1) throws BadJOSEException, JOSEException;
    
    Payload process(final PlainObject p0, final C p1) throws BadJOSEException, JOSEException;
    
    Payload process(final JWSObject p0, final C p1) throws BadJOSEException, JOSEException;
    
    Payload process(final JWEObject p0, final C p1) throws BadJOSEException, JOSEException;
}
