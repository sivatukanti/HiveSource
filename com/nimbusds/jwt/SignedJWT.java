// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt;

import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEObject;
import net.minidev.json.JSONObject;
import java.text.ParseException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.JWSHeader;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.JWSObject;

@ThreadSafe
public class SignedJWT extends JWSObject implements JWT
{
    private static final long serialVersionUID = 1L;
    
    public SignedJWT(final JWSHeader header, final JWTClaimsSet claimsSet) {
        super(header, new Payload(claimsSet.toJSONObject()));
    }
    
    public SignedJWT(final Base64URL firstPart, final Base64URL secondPart, final Base64URL thirdPart) throws ParseException {
        super(firstPart, secondPart, thirdPart);
    }
    
    @Override
    public JWTClaimsSet getJWTClaimsSet() throws ParseException {
        final JSONObject json = this.getPayload().toJSONObject();
        if (json == null) {
            throw new ParseException("Payload of JWS object is not a valid JSON object", 0);
        }
        return JWTClaimsSet.parse(json);
    }
    
    public static SignedJWT parse(final String s) throws ParseException {
        final Base64URL[] parts = JOSEObject.split(s);
        if (parts.length != 3) {
            throw new ParseException("Unexpected number of Base64URL parts, must be three", 0);
        }
        return new SignedJWT(parts[0], parts[1], parts[2]);
    }
}
