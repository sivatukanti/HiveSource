// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.authentication;

import org.eclipse.jetty.util.TypeUtil;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.eclipse.jetty.util.security.Credential;
import java.util.BitSet;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.B64Code;
import java.util.Objects;
import org.eclipse.jetty.server.UserIdentity;
import java.io.IOException;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.http.HttpHeader;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.server.Authentication;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.security.Authenticator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;
import java.security.SecureRandom;
import org.eclipse.jetty.util.log.Logger;

public class DigestAuthenticator extends LoginAuthenticator
{
    private static final Logger LOG;
    private final SecureRandom _random;
    private long _maxNonceAgeMs;
    private int _maxNC;
    private ConcurrentMap<String, Nonce> _nonceMap;
    private Queue<Nonce> _nonceQueue;
    
    public DigestAuthenticator() {
        this._random = new SecureRandom();
        this._maxNonceAgeMs = 60000L;
        this._maxNC = 1024;
        this._nonceMap = new ConcurrentHashMap<String, Nonce>();
        this._nonceQueue = new ConcurrentLinkedQueue<Nonce>();
    }
    
    @Override
    public void setConfiguration(final Authenticator.AuthConfiguration configuration) {
        super.setConfiguration(configuration);
        final String mna = configuration.getInitParameter("maxNonceAge");
        if (mna != null) {
            this.setMaxNonceAge(Long.valueOf(mna));
        }
        final String mnc = configuration.getInitParameter("maxNonceCount");
        if (mnc != null) {
            this.setMaxNonceCount(Integer.valueOf(mnc));
        }
    }
    
    public int getMaxNonceCount() {
        return this._maxNC;
    }
    
    public void setMaxNonceCount(final int maxNC) {
        this._maxNC = maxNC;
    }
    
    public long getMaxNonceAge() {
        return this._maxNonceAgeMs;
    }
    
    public void setMaxNonceAge(final long maxNonceAgeInMillis) {
        this._maxNonceAgeMs = maxNonceAgeInMillis;
    }
    
    @Override
    public String getAuthMethod() {
        return "DIGEST";
    }
    
    @Override
    public boolean secureResponse(final ServletRequest req, final ServletResponse res, final boolean mandatory, final Authentication.User validatedUser) throws ServerAuthException {
        return true;
    }
    
    @Override
    public Authentication validateRequest(final ServletRequest req, final ServletResponse res, final boolean mandatory) throws ServerAuthException {
        if (!mandatory) {
            return new DeferredAuthentication(this);
        }
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        final String credentials = request.getHeader(HttpHeader.AUTHORIZATION.asString());
        try {
            boolean stale = false;
            if (credentials != null) {
                if (DigestAuthenticator.LOG.isDebugEnabled()) {
                    DigestAuthenticator.LOG.debug("Credentials: " + credentials, new Object[0]);
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
                            continue;
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
                final int n = this.checkNonce(digest, (Request)request);
                if (n > 0) {
                    final UserIdentity user = this.login(digest.username, digest, req);
                    if (user != null) {
                        return new UserAuthentication(this.getAuthMethod(), user);
                    }
                }
                else if (n == 0) {
                    stale = true;
                }
            }
            if (!DeferredAuthentication.isDeferred(response)) {
                String domain = request.getContextPath();
                if (domain == null) {
                    domain = "/";
                }
                response.setHeader(HttpHeader.WWW_AUTHENTICATE.asString(), "Digest realm=\"" + this._loginService.getName() + "\", domain=\"" + domain + "\", nonce=\"" + this.newNonce((Request)request) + "\", algorithm=MD5, qop=\"auth\", stale=" + stale);
                response.sendError(401);
                return Authentication.SEND_CONTINUE;
            }
            return Authentication.UNAUTHENTICATED;
        }
        catch (IOException e) {
            throw new ServerAuthException(e);
        }
    }
    
    @Override
    public UserIdentity login(final String username, final Object credentials, final ServletRequest request) {
        final Digest digest = (Digest)credentials;
        if (!Objects.equals(digest.realm, this._loginService.getName())) {
            return null;
        }
        return super.login(username, credentials, request);
    }
    
    public String newNonce(final Request request) {
        Nonce nonce;
        do {
            final byte[] nounce = new byte[24];
            this._random.nextBytes(nounce);
            nonce = new Nonce(new String(B64Code.encode(nounce)), request.getTimeStamp(), this.getMaxNonceCount());
        } while (this._nonceMap.putIfAbsent(nonce._nonce, nonce) != null);
        this._nonceQueue.add(nonce);
        return nonce._nonce;
    }
    
    private int checkNonce(final Digest digest, final Request request) {
        final long expired = request.getTimeStamp() - this.getMaxNonceAge();
        for (Nonce nonce = this._nonceQueue.peek(); nonce != null && nonce._ts < expired; nonce = this._nonceQueue.peek()) {
            this._nonceQueue.remove(nonce);
            this._nonceMap.remove(nonce._nonce);
        }
        try {
            final Nonce nonce = this._nonceMap.get(digest.nonce);
            if (nonce == null) {
                return 0;
            }
            final long count = Long.parseLong(digest.nc, 16);
            if (count >= this._maxNC) {
                return 0;
            }
            if (nonce.seen((int)count)) {
                return -1;
            }
            return 1;
        }
        catch (Exception e) {
            DigestAuthenticator.LOG.ignore(e);
            return -1;
        }
    }
    
    static {
        LOG = Log.getLogger(DigestAuthenticator.class);
    }
    
    private static class Nonce
    {
        final String _nonce;
        final long _ts;
        final BitSet _seen;
        
        public Nonce(final String nonce, final long ts, final int size) {
            this._nonce = nonce;
            this._ts = ts;
            this._seen = new BitSet(size);
        }
        
        public boolean seen(final int count) {
            synchronized (this) {
                if (count >= this._seen.size()) {
                    return true;
                }
                final boolean s = this._seen.get(count);
                this._seen.set(count);
                return s;
            }
        }
    }
    
    private static class Digest extends Credential
    {
        private static final long serialVersionUID = -2484639019549527724L;
        final String method;
        String username;
        String realm;
        String nonce;
        String nc;
        String cnonce;
        String qop;
        String uri;
        String response;
        
        Digest(final String m) {
            this.username = "";
            this.realm = "";
            this.nonce = "";
            this.nc = "";
            this.cnonce = "";
            this.qop = "";
            this.uri = "";
            this.response = "";
            this.method = m;
        }
        
        @Override
        public boolean check(Object credentials) {
            if (credentials instanceof char[]) {
                credentials = new String((char[])credentials);
            }
            final String password = (String)((credentials instanceof String) ? credentials : credentials.toString());
            try {
                final MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] ha1;
                if (credentials instanceof MD5) {
                    ha1 = ((MD5)credentials).getDigest();
                }
                else {
                    md.update(this.username.getBytes(StandardCharsets.ISO_8859_1));
                    md.update((byte)58);
                    md.update(this.realm.getBytes(StandardCharsets.ISO_8859_1));
                    md.update((byte)58);
                    md.update(password.getBytes(StandardCharsets.ISO_8859_1));
                    ha1 = md.digest();
                }
                md.reset();
                md.update(this.method.getBytes(StandardCharsets.ISO_8859_1));
                md.update((byte)58);
                md.update(this.uri.getBytes(StandardCharsets.ISO_8859_1));
                final byte[] ha2 = md.digest();
                md.update(TypeUtil.toString(ha1, 16).getBytes(StandardCharsets.ISO_8859_1));
                md.update((byte)58);
                md.update(this.nonce.getBytes(StandardCharsets.ISO_8859_1));
                md.update((byte)58);
                md.update(this.nc.getBytes(StandardCharsets.ISO_8859_1));
                md.update((byte)58);
                md.update(this.cnonce.getBytes(StandardCharsets.ISO_8859_1));
                md.update((byte)58);
                md.update(this.qop.getBytes(StandardCharsets.ISO_8859_1));
                md.update((byte)58);
                md.update(TypeUtil.toString(ha2, 16).getBytes(StandardCharsets.ISO_8859_1));
                final byte[] digest = md.digest();
                return Credential.stringEquals(TypeUtil.toString(digest, 16).toLowerCase(), (this.response == null) ? null : this.response.toLowerCase());
            }
            catch (Exception e) {
                DigestAuthenticator.LOG.warn(e);
                return false;
            }
        }
        
        @Override
        public String toString() {
            return this.username + "," + this.response;
        }
    }
}
