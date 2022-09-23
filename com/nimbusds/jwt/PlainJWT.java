// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt;

import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEObject;
import net.minidev.json.JSONObject;
import java.text.ParseException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.PlainHeader;
import com.nimbusds.jose.Payload;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.PlainObject;

@ThreadSafe
public class PlainJWT extends PlainObject implements JWT
{
    private static final long serialVersionUID = 1L;
    
    public PlainJWT(final JWTClaimsSet claimsSet) {
        super(new Payload(claimsSet.toJSONObject()));
    }
    
    public PlainJWT(final PlainHeader header, final JWTClaimsSet claimsSet) {
        super(header, new Payload(claimsSet.toJSONObject()));
    }
    
    public PlainJWT(final Base64URL firstPart, final Base64URL secondPart) throws ParseException {
        super(firstPart, secondPart);
    }
    
    @Override
    public JWTClaimsSet getJWTClaimsSet() throws ParseException {
        final JSONObject json = this.getPayload().toJSONObject();
        if (json == null) {
            throw new ParseException("Payload of unsecured JOSE object is not a valid JSON object", 0);
        }
        return JWTClaimsSet.parse(json);
    }
    
    public static PlainJWT parse(final String s) throws ParseException {
        final Base64URL[] parts = JOSEObject.split(s);
        if (!parts[2].toString().isEmpty()) {
            throw new ParseException("Unexpected third Base64URL part in the unsecured JWT object", 0);
        }
        return new PlainJWT(parts[0], parts[1]);
    }
}
