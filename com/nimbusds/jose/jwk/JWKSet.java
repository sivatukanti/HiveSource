// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.util.ArrayList;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.security.interfaces.ECPublicKey;
import com.nimbusds.jose.JOSEException;
import java.security.interfaces.RSAPublicKey;
import java.security.KeyStore;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.RestrictedResourceRetriever;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import java.net.URL;
import java.io.IOException;
import com.nimbusds.jose.util.IOUtils;
import java.nio.charset.Charset;
import java.io.File;
import java.text.ParseException;
import com.nimbusds.jose.util.JSONObjectUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

public class JWKSet
{
    public static final String MIME_TYPE = "application/jwk-set+json; charset=UTF-8";
    private final List<JWK> keys;
    private final Map<String, Object> customMembers;
    
    public JWKSet() {
        this.keys = new LinkedList<JWK>();
        this.customMembers = new HashMap<String, Object>();
    }
    
    public JWKSet(final JWK key) {
        this.keys = new LinkedList<JWK>();
        this.customMembers = new HashMap<String, Object>();
        if (key == null) {
            throw new IllegalArgumentException("The JWK must not be null");
        }
        this.keys.add(key);
    }
    
    public JWKSet(final List<JWK> keys) {
        this.keys = new LinkedList<JWK>();
        this.customMembers = new HashMap<String, Object>();
        if (keys == null) {
            throw new IllegalArgumentException("The JWK list must not be null");
        }
        this.keys.addAll(keys);
    }
    
    public JWKSet(final List<JWK> keys, final Map<String, Object> customMembers) {
        this.keys = new LinkedList<JWK>();
        this.customMembers = new HashMap<String, Object>();
        if (keys == null) {
            throw new IllegalArgumentException("The JWK list must not be null");
        }
        this.keys.addAll(keys);
        this.customMembers.putAll(customMembers);
    }
    
    public List<JWK> getKeys() {
        return this.keys;
    }
    
    public JWK getKeyByKeyId(final String kid) {
        for (final JWK key : this.getKeys()) {
            if (key.getKeyID() != null && key.getKeyID().equals(kid)) {
                return key;
            }
        }
        return null;
    }
    
    public Map<String, Object> getAdditionalMembers() {
        return this.customMembers;
    }
    
    public JWKSet toPublicJWKSet() {
        final List<JWK> publicKeyList = new LinkedList<JWK>();
        for (final JWK key : this.keys) {
            final JWK publicKey = key.toPublicJWK();
            if (publicKey != null) {
                publicKeyList.add(publicKey);
            }
        }
        return new JWKSet(publicKeyList, this.customMembers);
    }
    
    public JSONObject toJSONObject() {
        return this.toJSONObject(true);
    }
    
    public JSONObject toJSONObject(final boolean publicKeysOnly) {
        final JSONObject o = new JSONObject(this.customMembers);
        final JSONArray a = new JSONArray();
        for (final JWK key : this.keys) {
            if (publicKeysOnly) {
                final JWK publicKey = key.toPublicJWK();
                if (publicKey == null) {
                    continue;
                }
                ((ArrayList<JSONObject>)a).add(publicKey.toJSONObject());
            }
            else {
                ((ArrayList<JSONObject>)a).add(key.toJSONObject());
            }
        }
        ((HashMap<String, JSONArray>)o).put("keys", a);
        return o;
    }
    
    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }
    
    public static JWKSet parse(final String s) throws ParseException {
        return parse(JSONObjectUtils.parse(s));
    }
    
    public static JWKSet parse(final JSONObject json) throws ParseException {
        final JSONArray keyArray = JSONObjectUtils.getJSONArray(json, "keys");
        final List<JWK> keys = new LinkedList<JWK>();
        for (int i = 0; i < keyArray.size(); ++i) {
            if (!(keyArray.get(i) instanceof JSONObject)) {
                throw new ParseException("The \"keys\" JSON array must contain JSON objects only", 0);
            }
            final JSONObject keyJSON = ((ArrayList<JSONObject>)keyArray).get(i);
            try {
                keys.add(JWK.parse(keyJSON));
            }
            catch (ParseException e) {
                throw new ParseException("Invalid JWK at position " + i + ": " + e.getMessage(), 0);
            }
        }
        final JWKSet jwkSet = new JWKSet(keys);
        for (final Map.Entry<String, Object> entry : json.entrySet()) {
            if (entry.getKey() != null) {
                if (entry.getKey().equals("keys")) {
                    continue;
                }
                jwkSet.getAdditionalMembers().put(entry.getKey(), entry.getValue());
            }
        }
        return jwkSet;
    }
    
    public static JWKSet load(final File file) throws IOException, ParseException {
        return parse(IOUtils.readFileToString(file, Charset.forName("UTF-8")));
    }
    
    public static JWKSet load(final URL url, final int connectTimeout, final int readTimeout, final int sizeLimit) throws IOException, ParseException {
        final RestrictedResourceRetriever resourceRetriever = new DefaultResourceRetriever(connectTimeout, readTimeout, sizeLimit);
        final Resource resource = resourceRetriever.retrieveResource(url);
        return parse(resource.getContent());
    }
    
    public static JWKSet load(final URL url) throws IOException, ParseException {
        return load(url, 0, 0, 0);
    }
    
    public static JWKSet load(final KeyStore keyStore, final PasswordLookup pwLookup) throws KeyStoreException {
        final List<JWK> jwks = new LinkedList<JWK>();
        Enumeration<String> keyAliases = keyStore.aliases();
        while (keyAliases.hasMoreElements()) {
            final String keyAlias = keyAliases.nextElement();
            final char[] keyPassword = (pwLookup == null) ? "".toCharArray() : pwLookup.lookupPassword(keyAlias);
            final Certificate cert = keyStore.getCertificate(keyAlias);
            if (cert == null) {
                continue;
            }
            if (cert.getPublicKey() instanceof RSAPublicKey) {
                RSAKey rsaJWK;
                try {
                    rsaJWK = RSAKey.load(keyStore, keyAlias, keyPassword);
                }
                catch (JOSEException ex) {
                    continue;
                }
                if (rsaJWK == null) {
                    continue;
                }
                jwks.add(rsaJWK);
            }
            else {
                if (!(cert.getPublicKey() instanceof ECPublicKey)) {
                    continue;
                }
                ECKey ecJWK;
                try {
                    ecJWK = ECKey.load(keyStore, keyAlias, keyPassword);
                }
                catch (JOSEException ex2) {
                    continue;
                }
                if (ecJWK == null) {
                    continue;
                }
                jwks.add(ecJWK);
            }
        }
        keyAliases = keyStore.aliases();
        while (keyAliases.hasMoreElements()) {
            final String keyAlias = keyAliases.nextElement();
            final char[] keyPassword = (pwLookup == null) ? "".toCharArray() : pwLookup.lookupPassword(keyAlias);
            OctetSequenceKey octJWK;
            try {
                octJWK = OctetSequenceKey.load(keyStore, keyAlias, keyPassword);
            }
            catch (JOSEException ex3) {
                continue;
            }
            if (octJWK != null) {
                jwks.add(octJWK);
            }
        }
        return new JWKSet(jwks);
    }
}
