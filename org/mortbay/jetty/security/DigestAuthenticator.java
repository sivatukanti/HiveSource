// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import org.mortbay.util.TypeUtil;
import java.security.MessageDigest;
import java.io.IOException;
import org.mortbay.util.StringUtil;
import org.mortbay.util.QuotedStringTokenizer;
import org.mortbay.log.Log;
import java.security.Principal;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Request;

public class DigestAuthenticator implements Authenticator
{
    protected long maxNonceAge;
    protected long nonceSecret;
    protected boolean useStale;
    
    public DigestAuthenticator() {
        this.maxNonceAge = 0L;
        this.nonceSecret = ((long)this.hashCode() ^ System.currentTimeMillis());
        this.useStale = false;
    }
    
    public Principal authenticate(final UserRealm realm, final String pathInContext, final Request request, final Response response) throws IOException {
        boolean stale = false;
        Principal user = null;
        final String credentials = request.getHeader("Authorization");
        if (credentials != null) {
            if (Log.isDebugEnabled()) {
                Log.debug("Credentials: " + credentials);
            }
            final QuotedStringTokenizer tokenizer = new QuotedStringTokenizer(credentials, "=, ", true, false);
            final Digest digest = new Digest(request.getMethod());
            String last = null;
            String name = null;
            while (tokenizer.hasMoreTokens()) {
                final String tok = tokenizer.nextToken();
                final char c = (tok.length() == 1) ? tok.charAt(0) : '\0';
                switch (c) {
                    case '=': {
                        name = last;
                        last = tok;
                        continue;
                    }
                    case ',': {
                        name = null;
                    }
                    case ' ': {
                        continue;
                    }
                    default: {
                        last = tok;
                        if (name != null) {
                            if ("username".equalsIgnoreCase(name)) {
                                digest.username = tok;
                            }
                            else if ("realm".equalsIgnoreCase(name)) {
                                digest.realm = tok;
                            }
                            else if ("nonce".equalsIgnoreCase(name)) {
                                digest.nonce = tok;
                            }
                            else if ("nc".equalsIgnoreCase(name)) {
                                digest.nc = tok;
                            }
                            else if ("cnonce".equalsIgnoreCase(name)) {
                                digest.cnonce = tok;
                            }
                            else if ("qop".equalsIgnoreCase(name)) {
                                digest.qop = tok;
                            }
                            else if ("uri".equalsIgnoreCase(name)) {
                                digest.uri = tok;
                            }
                            else if ("response".equalsIgnoreCase(name)) {
                                digest.response = tok;
                            }
                            name = null;
                            continue;
                        }
                        continue;
                    }
                }
            }
            final int n = this.checkNonce(digest.nonce, request);
            if (n > 0) {
                user = realm.authenticate(digest.username, digest, request);
            }
            else if (n == 0) {
                stale = true;
            }
            if (user == null) {
                Log.warn("AUTH FAILURE: user " + StringUtil.printable(digest.username));
            }
            else {
                request.setAuthType("DIGEST");
                request.setUserPrincipal(user);
            }
        }
        if (user == null && response != null) {
            this.sendChallenge(realm, request, response, stale);
        }
        return user;
    }
    
    public String getAuthMethod() {
        return "DIGEST";
    }
    
    public void sendChallenge(final UserRealm realm, final Request request, final Response response, final boolean stale) throws IOException {
        String domain = request.getContextPath();
        if (domain == null) {
            domain = "/";
        }
        response.setHeader("WWW-Authenticate", "Digest realm=\"" + realm.getName() + "\", domain=\"" + domain + "\", nonce=\"" + this.newNonce(request) + "\", algorithm=MD5, qop=\"auth\"" + (this.useStale ? (" stale=" + stale) : ""));
        response.sendError(401);
    }
    
    public String newNonce(final Request request) {
        long ts = request.getTimeStamp();
        long sk = this.nonceSecret;
        final byte[] nounce = new byte[24];
        for (int i = 0; i < 8; ++i) {
            nounce[i] = (byte)(ts & 0xFFL);
            ts >>= 8;
            nounce[8 + i] = (byte)(sk & 0xFFL);
            sk >>= 8;
        }
        byte[] hash = null;
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(nounce, 0, 16);
            hash = md.digest();
        }
        catch (Exception e) {
            Log.warn(e);
        }
        for (int j = 0; j < hash.length; ++j) {
            nounce[8 + j] = hash[j];
            if (j == 23) {
                break;
            }
        }
        return new String(B64Code.encode(nounce));
    }
    
    public int checkNonce(final String nonce, final Request request) {
        try {
            final byte[] n = B64Code.decode(nonce.toCharArray());
            if (n.length != 24) {
                return -1;
            }
            long ts = 0L;
            long sk = this.nonceSecret;
            final byte[] n2 = new byte[16];
            System.arraycopy(n, 0, n2, 0, 8);
            for (int i = 0; i < 8; ++i) {
                n2[8 + i] = (byte)(sk & 0xFFL);
                sk >>= 8;
                ts = (ts << 8) + (0xFFL & (long)n[7 - i]);
            }
            final long age = request.getTimeStamp() - ts;
            if (Log.isDebugEnabled()) {
                Log.debug("age=" + age);
            }
            byte[] hash = null;
            try {
                final MessageDigest md = MessageDigest.getInstance("MD5");
                md.reset();
                md.update(n2, 0, 16);
                hash = md.digest();
            }
            catch (Exception e) {
                Log.warn(e);
            }
            for (int j = 0; j < 16; ++j) {
                if (n[j + 8] != hash[j]) {
                    return -1;
                }
            }
            if (this.maxNonceAge > 0L && (age < 0L || age > this.maxNonceAge)) {
                return 0;
            }
            return 1;
        }
        catch (Exception e2) {
            Log.ignore(e2);
            return -1;
        }
    }
    
    public long getMaxNonceAge() {
        return this.maxNonceAge;
    }
    
    public void setMaxNonceAge(final long maxNonceAge) {
        this.maxNonceAge = maxNonceAge;
    }
    
    public long getNonceSecret() {
        return this.nonceSecret;
    }
    
    public void setNonceSecret(final long nonceSecret) {
        this.nonceSecret = nonceSecret;
    }
    
    public void setUseStale(final boolean us) {
        this.useStale = us;
    }
    
    public boolean getUseStale() {
        return this.useStale;
    }
    
    private static class Digest extends Credential
    {
        String method;
        String username;
        String realm;
        String nonce;
        String nc;
        String cnonce;
        String qop;
        String uri;
        String response;
        
        Digest(final String m) {
            this.method = null;
            this.username = null;
            this.realm = null;
            this.nonce = null;
            this.nc = null;
            this.cnonce = null;
            this.qop = null;
            this.uri = null;
            this.response = null;
            this.method = m;
        }
        
        public boolean check(final Object credentials) {
            final String password = (String)((credentials instanceof String) ? credentials : credentials.toString());
            try {
                final MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] ha1;
                if (credentials instanceof MD5) {
                    ha1 = ((MD5)credentials).getDigest();
                }
                else {
                    md.update(this.username.getBytes(StringUtil.__ISO_8859_1));
                    md.update((byte)58);
                    md.update(this.realm.getBytes(StringUtil.__ISO_8859_1));
                    md.update((byte)58);
                    md.update(password.getBytes(StringUtil.__ISO_8859_1));
                    ha1 = md.digest();
                }
                md.reset();
                md.update(this.method.getBytes(StringUtil.__ISO_8859_1));
                md.update((byte)58);
                md.update(this.uri.getBytes(StringUtil.__ISO_8859_1));
                final byte[] ha2 = md.digest();
                md.update(TypeUtil.toString(ha1, 16).getBytes(StringUtil.__ISO_8859_1));
                md.update((byte)58);
                md.update(this.nonce.getBytes(StringUtil.__ISO_8859_1));
                md.update((byte)58);
                md.update(this.nc.getBytes(StringUtil.__ISO_8859_1));
                md.update((byte)58);
                md.update(this.cnonce.getBytes(StringUtil.__ISO_8859_1));
                md.update((byte)58);
                md.update(this.qop.getBytes(StringUtil.__ISO_8859_1));
                md.update((byte)58);
                md.update(TypeUtil.toString(ha2, 16).getBytes(StringUtil.__ISO_8859_1));
                final byte[] digest = md.digest();
                return TypeUtil.toString(digest, 16).equalsIgnoreCase(this.response);
            }
            catch (Exception e) {
                Log.warn(e);
                return false;
            }
        }
        
        public String toString() {
            return this.username + "," + this.response;
        }
    }
}
