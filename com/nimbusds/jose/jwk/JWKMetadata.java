// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import com.nimbusds.jose.util.X509CertChainUtils;
import com.nimbusds.jose.util.Base64;
import java.util.List;
import com.nimbusds.jose.util.Base64URL;
import java.net.URI;
import com.nimbusds.jose.Algorithm;
import java.util.Set;
import java.text.ParseException;
import com.nimbusds.jose.util.JSONObjectUtils;
import net.minidev.json.JSONObject;

final class JWKMetadata
{
    static KeyType parseKeyType(final JSONObject o) throws ParseException {
        return KeyType.parse(JSONObjectUtils.getString(o, "kty"));
    }
    
    static KeyUse parseKeyUse(final JSONObject o) throws ParseException {
        if (o.containsKey("use")) {
            return KeyUse.parse(JSONObjectUtils.getString(o, "use"));
        }
        return null;
    }
    
    static Set<KeyOperation> parseKeyOperations(final JSONObject o) throws ParseException {
        if (o.containsKey("key_ops")) {
            return KeyOperation.parse(JSONObjectUtils.getStringList(o, "key_ops"));
        }
        return null;
    }
    
    static Algorithm parseAlgorithm(final JSONObject o) throws ParseException {
        if (o.containsKey("alg")) {
            return new Algorithm(JSONObjectUtils.getString(o, "alg"));
        }
        return null;
    }
    
    static String parseKeyID(final JSONObject o) throws ParseException {
        if (o.containsKey("kid")) {
            return JSONObjectUtils.getString(o, "kid");
        }
        return null;
    }
    
    static URI parseX509CertURL(final JSONObject o) throws ParseException {
        if (o.containsKey("x5u")) {
            return JSONObjectUtils.getURI(o, "x5u");
        }
        return null;
    }
    
    static Base64URL parseX509CertThumbprint(final JSONObject o) throws ParseException {
        if (o.containsKey("x5t")) {
            return new Base64URL(JSONObjectUtils.getString(o, "x5t"));
        }
        return null;
    }
    
    static Base64URL parseX509CertSHA256Thumbprint(final JSONObject o) throws ParseException {
        if (o.containsKey("x5t#S256")) {
            return new Base64URL(JSONObjectUtils.getString(o, "x5t#S256"));
        }
        return null;
    }
    
    static List<Base64> parseX509CertChain(final JSONObject o) throws ParseException {
        if (o.containsKey("x5c")) {
            return X509CertChainUtils.parseX509CertChain(JSONObjectUtils.getJSONArray(o, "x5c"));
        }
        return null;
    }
}
