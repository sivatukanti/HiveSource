// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientRequest;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collection;
import java.security.MessageDigest;
import java.util.regex.Pattern;
import java.security.SecureRandom;

public final class HTTPDigestAuthFilter extends ClientFilter
{
    private static final int CNONCE_NB_BYTES = 4;
    private static final SecureRandom randomGenerator;
    private static final Pattern TOKENS_PATTERN;
    private final String user;
    private final String pass;
    private final ThreadLocal<State> state;
    
    public HTTPDigestAuthFilter(final String user, final String pass) {
        this.state = new ThreadLocal<State>() {
            @Override
            protected State initialValue() {
                return new State();
            }
        };
        this.user = user;
        this.pass = pass;
    }
    
    private static void addKeyVal(final StringBuffer buffer, final String key, final String val, final boolean withQuotes) {
        final String quote = withQuotes ? "\"" : "";
        buffer.append(key + '=' + quote + val + quote + ',');
    }
    
    private static void addKeyVal(final StringBuffer buffer, final String key, final String val) {
        addKeyVal(buffer, key, val, true);
    }
    
    private static String convertToHex(final byte[] data) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; ++i) {
            int halfbyte = data[i] >>> 4 & 0xF;
            int two_halfs = 0;
            do {
                if (0 <= halfbyte && halfbyte <= 9) {
                    buf.append((char)(48 + halfbyte));
                }
                else {
                    buf.append((char)(97 + (halfbyte - 10)));
                }
                halfbyte = (data[i] & 0xF);
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
    
    static String MD5(final String text) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            final byte[] md5hash = md.digest();
            final String result = convertToHex(md5hash);
            return result;
        }
        catch (Exception e) {
            throw new Error(e);
        }
    }
    
    static String concatMD5(final String... vals) {
        final StringBuffer buff = new StringBuffer();
        for (final String val : vals) {
            buff.append(val);
            buff.append(':');
        }
        buff.deleteCharAt(buff.length() - 1);
        return MD5(buff.toString());
    }
    
    String randHexBytes(final int nbBytes) {
        final byte[] bytes = new byte[nbBytes];
        HTTPDigestAuthFilter.randomGenerator.nextBytes(bytes);
        return convertToHex(bytes);
    }
    
    static HashMap<String, String> parseHeaders(final Collection<String> lines) {
        for (final String line : lines) {
            final String[] parts = line.trim().split("\\s+", 2);
            if (parts.length != 2) {
                continue;
            }
            if (!parts[0].toLowerCase().equals("digest")) {
                continue;
            }
            final Matcher match = HTTPDigestAuthFilter.TOKENS_PATTERN.matcher(parts[1]);
            final HashMap<String, String> result = new HashMap<String, String>();
            while (match.find()) {
                final int nbGroups = match.groupCount();
                if (nbGroups != 4) {
                    continue;
                }
                final String key = match.group(1);
                final String valNoQuotes = match.group(3);
                final String valQuotes = match.group(4);
                result.put(key, (valNoQuotes == null) ? valQuotes : valNoQuotes);
            }
            return result;
        }
        return null;
    }
    
    @Override
    public ClientResponse handle(final ClientRequest request) throws ClientHandlerException {
        boolean reqHadAuthHeaders = false;
        if (this.state.get().nextNonce != null) {
            reqHadAuthHeaders = true;
            String qopStr = null;
            if (this.state.get().qop != null) {
                qopStr = ((this.state.get().qop == QOP.AUTH_INT) ? "auth-int" : "auth");
            }
            final StringBuffer buff = new StringBuffer();
            buff.append("Digest ");
            addKeyVal(buff, "username", this.user);
            addKeyVal(buff, "realm", this.state.get().realm);
            addKeyVal(buff, "nonce", this.state.get().nextNonce);
            if (this.state.get().opaque != null) {
                addKeyVal(buff, "opaque", this.state.get().opaque);
            }
            if (this.state.get().algorithm != null) {
                addKeyVal(buff, "algorithm", this.state.get().algorithm, false);
            }
            if (this.state.get().qop != null) {
                addKeyVal(buff, "qop", qopStr, false);
            }
            final String HA1 = concatMD5(this.user, this.state.get().realm, this.pass);
            final String uri = request.getURI().getPath();
            addKeyVal(buff, "uri", uri);
            String HA2;
            if (this.state.get().qop == QOP.AUTH_INT && request.getEntity() != null) {
                HA2 = concatMD5(request.getMethod(), uri, request.getEntity().toString());
            }
            else {
                HA2 = concatMD5(request.getMethod(), uri);
            }
            String response;
            if (this.state.get().qop == null) {
                response = concatMD5(HA1, this.state.get().nextNonce, HA2);
            }
            else {
                final String cnonce = this.randHexBytes(4);
                final String nc = String.format("%08x", this.state.get().counter);
                final State state = this.state.get();
                ++state.counter;
                addKeyVal(buff, "cnonce", cnonce);
                addKeyVal(buff, "nc", nc, false);
                response = concatMD5(HA1, this.state.get().nextNonce, nc, cnonce, qopStr, HA2);
            }
            addKeyVal(buff, "response", response);
            buff.deleteCharAt(buff.length() - 1);
            final String authLine = buff.toString();
            request.getHeaders().add("Authorization", authLine);
        }
        final ClientResponse response2 = this.getNext().handle(request);
        if (response2.getClientResponseStatus() != ClientResponse.Status.UNAUTHORIZED) {
            return response2;
        }
        final HashMap<String, String> map = parseHeaders(response2.getHeaders().get("WWW-Authenticate"));
        if (map == null) {
            return response2;
        }
        this.state.get().realm = map.get("realm");
        this.state.get().nextNonce = map.get("nonce");
        this.state.get().opaque = map.get("opaque");
        this.state.get().algorithm = map.get("algorithm");
        this.state.get().domain = map.get("domain");
        final String qop = map.get("qop");
        if (qop == null) {
            this.state.get().qop = null;
        }
        else if (qop.contains("auth-int")) {
            this.state.get().qop = QOP.AUTH_INT;
        }
        else if (qop.contains("auth")) {
            this.state.get().qop = QOP.AUTH;
        }
        else {
            this.state.get().qop = null;
        }
        final String staleStr = map.get("stale");
        final boolean stale = staleStr != null && staleStr.toLowerCase().equals("true");
        if (stale || !reqHadAuthHeaders) {
            return this.handle(request);
        }
        return response2;
    }
    
    static {
        try {
            randomGenerator = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (Exception e) {
            throw new Error(e);
        }
        TOKENS_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*(\"([^\"]+)\"|(\\w+))\\s*,?\\s*");
    }
    
    private enum QOP
    {
        AUTH, 
        AUTH_INT;
    }
    
    private class State
    {
        String nextNonce;
        String realm;
        String opaque;
        String algorithm;
        String domain;
        QOP qop;
        int counter;
        
        private State() {
            this.qop = null;
            this.counter = 1;
        }
    }
}
