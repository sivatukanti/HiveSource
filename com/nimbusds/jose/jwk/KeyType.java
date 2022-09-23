// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.Algorithm;
import net.minidev.json.JSONObject;
import com.nimbusds.jose.Requirement;
import net.jcip.annotations.Immutable;
import java.io.Serializable;
import net.minidev.json.JSONAware;

@Immutable
public final class KeyType implements JSONAware, Serializable
{
    private static final long serialVersionUID = 1L;
    private final String value;
    private final Requirement requirement;
    public static final KeyType EC;
    public static final KeyType RSA;
    public static final KeyType OCT;
    
    static {
        EC = new KeyType("EC", Requirement.RECOMMENDED);
        RSA = new KeyType("RSA", Requirement.REQUIRED);
        OCT = new KeyType("oct", Requirement.OPTIONAL);
    }
    
    public KeyType(final String value, final Requirement req) {
        if (value == null) {
            throw new IllegalArgumentException("The key type value must not be null");
        }
        this.value = value;
        this.requirement = req;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public Requirement getRequirement() {
        return this.requirement;
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        return object != null && object instanceof KeyType && this.toString().equals(object.toString());
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    @Override
    public String toJSONString() {
        return "\"" + JSONObject.escape(this.value) + '\"';
    }
    
    public static KeyType parse(final String s) {
        if (s.equals(KeyType.EC.getValue())) {
            return KeyType.EC;
        }
        if (s.equals(KeyType.RSA.getValue())) {
            return KeyType.RSA;
        }
        if (s.equals(KeyType.OCT.getValue())) {
            return KeyType.OCT;
        }
        return new KeyType(s, null);
    }
    
    public static KeyType forAlgorithm(final Algorithm alg) {
        if (alg == null) {
            return null;
        }
        if (JWSAlgorithm.Family.RSA.contains(alg)) {
            return KeyType.RSA;
        }
        if (JWSAlgorithm.Family.EC.contains(alg)) {
            return KeyType.EC;
        }
        if (JWSAlgorithm.Family.HMAC_SHA.contains(alg)) {
            return KeyType.OCT;
        }
        if (JWEAlgorithm.Family.RSA.contains(alg)) {
            return KeyType.RSA;
        }
        if (JWEAlgorithm.Family.ECDH_ES.contains(alg)) {
            return KeyType.EC;
        }
        if (JWEAlgorithm.DIR.equals(alg)) {
            return KeyType.OCT;
        }
        if (JWEAlgorithm.Family.AES_GCM_KW.contains(alg)) {
            return KeyType.OCT;
        }
        if (JWEAlgorithm.Family.AES_KW.contains(alg)) {
            return KeyType.OCT;
        }
        if (JWEAlgorithm.Family.PBES2.contains(alg)) {
            return KeyType.OCT;
        }
        return null;
    }
}
