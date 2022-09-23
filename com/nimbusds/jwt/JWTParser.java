// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt;

import net.minidev.json.JSONObject;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.Base64URL;
import java.text.ParseException;

public final class JWTParser
{
    public static JWT parse(final String s) throws ParseException {
        final int firstDotPos = s.indexOf(".");
        if (firstDotPos == -1) {
            throw new ParseException("Invalid JWT serialization: Missing dot delimiter(s)", 0);
        }
        final Base64URL header = new Base64URL(s.substring(0, firstDotPos));
        JSONObject jsonObject;
        try {
            jsonObject = JSONObjectUtils.parse(header.decodeToString());
        }
        catch (ParseException e) {
            throw new ParseException("Invalid unsecured/JWS/JWE header: " + e.getMessage(), 0);
        }
        final Algorithm alg = Header.parseAlgorithm(jsonObject);
        if (alg.equals(Algorithm.NONE)) {
            return PlainJWT.parse(s);
        }
        if (alg instanceof JWSAlgorithm) {
            return SignedJWT.parse(s);
        }
        if (alg instanceof JWEAlgorithm) {
            return EncryptedJWT.parse(s);
        }
        throw new AssertionError((Object)("Unexpected algorithm type: " + alg));
    }
    
    private JWTParser() {
    }
}
