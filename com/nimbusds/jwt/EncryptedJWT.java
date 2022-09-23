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
import com.nimbusds.jose.JWEHeader;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.JWEObject;

@ThreadSafe
public class EncryptedJWT extends JWEObject implements JWT
{
    private static final long serialVersionUID = 1L;
    
    public EncryptedJWT(final JWEHeader header, final JWTClaimsSet claimsSet) {
        super(header, new Payload(claimsSet.toJSONObject()));
    }
    
    public EncryptedJWT(final Base64URL firstPart, final Base64URL secondPart, final Base64URL thirdPart, final Base64URL fourthPart, final Base64URL fifthPart) throws ParseException {
        super(firstPart, secondPart, thirdPart, fourthPart, fifthPart);
    }
    
    @Override
    public JWTClaimsSet getJWTClaimsSet() throws ParseException {
        final Payload payload = this.getPayload();
        if (payload == null) {
            return null;
        }
        final JSONObject json = payload.toJSONObject();
        if (json == null) {
            throw new ParseException("Payload of JWE object is not a valid JSON object", 0);
        }
        return JWTClaimsSet.parse(json);
    }
    
    public static EncryptedJWT parse(final String s) throws ParseException {
        final Base64URL[] parts = JOSEObject.split(s);
        if (parts.length != 5) {
            throw new ParseException("Unexpected number of Base64URL parts, must be five", 0);
        }
        return new EncryptedJWT(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }
}
