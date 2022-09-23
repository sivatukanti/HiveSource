// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk.source;

import java.util.Collections;
import com.nimbusds.jose.jwk.JWK;
import java.util.List;
import com.nimbusds.jose.jwk.JWKSelector;
import java.util.Iterator;
import java.util.Set;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.util.Resource;
import java.text.ParseException;
import java.io.IOException;
import com.nimbusds.jose.RemoteKeySourceException;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jose.jwk.JWKSet;
import java.util.concurrent.atomic.AtomicReference;
import java.net.URL;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.proc.SecurityContext;

@ThreadSafe
public class RemoteJWKSet<C extends SecurityContext> implements JWKSource<C>
{
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 250;
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 250;
    public static final int DEFAULT_HTTP_SIZE_LIMIT = 51200;
    private final URL jwkSetURL;
    private final AtomicReference<JWKSet> cachedJWKSet;
    private final ResourceRetriever jwkSetRetriever;
    
    public RemoteJWKSet(final URL jwkSetURL) {
        this(jwkSetURL, null);
    }
    
    public RemoteJWKSet(final URL jwkSetURL, final ResourceRetriever resourceRetriever) {
        this.cachedJWKSet = new AtomicReference<JWKSet>();
        if (jwkSetURL == null) {
            throw new IllegalArgumentException("The JWK set URL must not be null");
        }
        this.jwkSetURL = jwkSetURL;
        if (resourceRetriever != null) {
            this.jwkSetRetriever = resourceRetriever;
        }
        else {
            this.jwkSetRetriever = new DefaultResourceRetriever(250, 250, 51200);
        }
    }
    
    private JWKSet updateJWKSetFromURL() throws RemoteKeySourceException {
        Resource res;
        try {
            res = this.jwkSetRetriever.retrieveResource(this.jwkSetURL);
        }
        catch (IOException e) {
            throw new RemoteKeySourceException("Couldn't retrieve remote JWK set: " + e.getMessage(), e);
        }
        JWKSet jwkSet;
        try {
            jwkSet = JWKSet.parse(res.getContent());
        }
        catch (ParseException e2) {
            throw new RemoteKeySourceException("Couldn't parse remote JWK set: " + e2.getMessage(), e2);
        }
        this.cachedJWKSet.set(jwkSet);
        return jwkSet;
    }
    
    public URL getJWKSetURL() {
        return this.jwkSetURL;
    }
    
    public ResourceRetriever getResourceRetriever() {
        return this.jwkSetRetriever;
    }
    
    public JWKSet getCachedJWKSet() {
        return this.cachedJWKSet.get();
    }
    
    protected static String getFirstSpecifiedKeyID(final JWKMatcher jwkMatcher) {
        final Set<String> keyIDs = jwkMatcher.getKeyIDs();
        if (keyIDs == null || keyIDs.isEmpty()) {
            return null;
        }
        for (final String id : keyIDs) {
            if (id != null) {
                return id;
            }
        }
        return null;
    }
    
    @Override
    public List<JWK> get(final JWKSelector jwkSelector, final C context) throws RemoteKeySourceException {
        JWKSet jwkSet = this.cachedJWKSet.get();
        if (jwkSet == null) {
            jwkSet = this.updateJWKSetFromURL();
        }
        final List<JWK> matches = jwkSelector.select(jwkSet);
        if (!matches.isEmpty()) {
            return matches;
        }
        final String soughtKeyID = getFirstSpecifiedKeyID(jwkSelector.getMatcher());
        if (soughtKeyID == null) {
            return Collections.emptyList();
        }
        if (jwkSet.getKeyByKeyId(soughtKeyID) != null) {
            return Collections.emptyList();
        }
        jwkSet = this.updateJWKSetFromURL();
        if (jwkSet == null) {
            return Collections.emptyList();
        }
        return jwkSelector.select(jwkSet);
    }
}
