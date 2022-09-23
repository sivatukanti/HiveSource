// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import com.nimbusds.jose.util.StandardCharset;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.Map;
import net.minidev.json.JSONObject;
import java.util.LinkedHashMap;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64URL;

public final class ThumbprintUtils
{
    public static Base64URL compute(final JWK jwk) throws JOSEException {
        return compute("SHA-256", jwk);
    }
    
    public static Base64URL compute(final String hashAlg, final JWK jwk) throws JOSEException {
        final LinkedHashMap<String, ?> orderedParams = jwk.getRequiredParams();
        return compute(hashAlg, orderedParams);
    }
    
    public static Base64URL compute(final String hashAlg, final LinkedHashMap<String, ?> params) throws JOSEException {
        final String json = JSONObject.toJSONString(params);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(hashAlg);
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Couldn't compute JWK thumbprint: Unsupported hash algorithm: " + e.getMessage(), e);
        }
        md.update(json.getBytes(StandardCharset.UTF_8));
        return Base64URL.encode(md.digest());
    }
}
