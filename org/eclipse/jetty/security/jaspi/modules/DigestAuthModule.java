// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi.modules;

import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.B64Code;
import java.security.MessageDigest;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.Subject;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.AuthException;
import java.util.Map;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.callback.CallbackHandler;
import org.eclipse.jetty.util.log.Logger;

public class DigestAuthModule extends BaseAuthModule
{
    private static final Logger LOG;
    protected long maxNonceAge;
    protected long nonceSecret;
    protected boolean useStale;
    private String realmName;
    private static final String REALM_KEY = "org.eclipse.jetty.security.jaspi.modules.RealmName";
    
    public DigestAuthModule() {
        this.maxNonceAge = 0L;
        this.nonceSecret = ((long)this.hashCode() ^ System.currentTimeMillis());
        this.useStale = false;
    }
    
    public DigestAuthModule(final CallbackHandler callbackHandler, final String realmName) {
        super(callbackHandler);
        this.maxNonceAge = 0L;
        this.nonceSecret = ((long)this.hashCode() ^ System.currentTimeMillis());
        this.useStale = false;
        this.realmName = realmName;
    }
    
    @Override
    public void initialize(final MessagePolicy requestPolicy, final MessagePolicy responsePolicy, final CallbackHandler handler, final Map options) throws AuthException {
        super.initialize(requestPolicy, responsePolicy, handler, options);
        this.realmName = options.get("org.eclipse.jetty.security.jaspi.modules.RealmName");
    }
    
    @Override
    public AuthStatus validateRequest(final MessageInfo messageInfo, final Subject clientSubject, final Subject serviceSubject) throws AuthException {
        final HttpServletRequest request = (HttpServletRequest)messageInfo.getRequestMessage();
        final HttpServletResponse response = (HttpServletResponse)messageInfo.getResponseMessage();
        final String credentials = request.getHeader("Authorization");
        try {
            boolean stale = false;
            final long timestamp = System.currentTimeMillis();
            if (credentials != null) {
                if (DigestAuthModule.LOG.isDebugEnabled()) {
                    DigestAuthModule.LOG.debug("Credentials: " + credentials, new Object[0]);
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
                            if (name == null) {
                                continue;
                            }
                            if ("username".equalsIgnoreCase(name)) {
                                digest.username = tok;
                                continue;
                            }
                            if ("realm".equalsIgnoreCase(name)) {
                                digest.realm = tok;
                                continue;
                            }
                            if ("nonce".equalsIgnoreCase(name)) {
                                digest.nonce = tok;
                                continue;
                            }
                            if ("nc".equalsIgnoreCase(name)) {
                                digest.nc = tok;
                                continue;
                            }
                            if ("cnonce".equalsIgnoreCase(name)) {
                                digest.cnonce = tok;
                                continue;
                            }
                            if ("qop".equalsIgnoreCase(name)) {
                                digest.qop = tok;
                                continue;
                            }
                            if ("uri".equalsIgnoreCase(name)) {
                                digest.uri = tok;
                                continue;
                            }
                            if ("response".equalsIgnoreCase(name)) {
                                digest.response = tok;
                                continue;
                            }
                            continue;
                        }
                    }
                }
                final int n = this.checkNonce(digest.nonce, timestamp);
                if (n > 0) {
                    if (this.login(clientSubject, digest.username, digest, "DIGEST", messageInfo)) {
                        return AuthStatus.SUCCESS;
                    }
                }
                else if (n == 0) {
                    stale = true;
                }
            }
            if (!this.isMandatory(messageInfo)) {
                return AuthStatus.SUCCESS;
            }
            String domain = request.getContextPath();
            if (domain == null) {
                domain = "/";
            }
            response.setHeader("WWW-Authenticate", "Digest realm=\"" + this.realmName + "\", domain=\"" + domain + "\", nonce=\"" + this.newNonce(timestamp) + "\", algorithm=MD5, qop=\"auth\"" + (this.useStale ? (" stale=" + stale) : ""));
            response.sendError(401);
            return AuthStatus.SEND_CONTINUE;
        }
        catch (IOException e) {
            throw new AuthException(e.getMessage());
        }
        catch (UnsupportedCallbackException e2) {
            throw new AuthException(e2.getMessage());
        }
    }
    
    public String newNonce(long ts) {
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
            DigestAuthModule.LOG.warn(e);
        }
        for (int j = 0; j < hash.length; ++j) {
            nounce[8 + j] = hash[j];
            if (j == 23) {
                break;
            }
        }
        return new String(B64Code.encode(nounce));
    }
    
    public int checkNonce(final String nonce, final long timestamp) {
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
            final long age = timestamp - ts;
            if (DigestAuthModule.LOG.isDebugEnabled()) {
                DigestAuthModule.LOG.debug("age=" + age, new Object[0]);
            }
            byte[] hash = null;
            try {
                final MessageDigest md = MessageDigest.getInstance("MD5");
                md.reset();
                md.update(n2, 0, 16);
                hash = md.digest();
            }
            catch (Exception e) {
                DigestAuthModule.LOG.warn(e);
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
            DigestAuthModule.LOG.ignore(e2);
            return -1;
        }
    }
    
    static {
        LOG = Log.getLogger(DigestAuthModule.class);
    }
    
    private static class Digest extends Credential
    {
        private static final long serialVersionUID = -1866670896275159116L;
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
        
        @Override
        public boolean check(final Object credentials) {
            final String password = (String)((credentials instanceof String) ? credentials : credentials.toString());
            try {
                final MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] ha1;
                if (credentials instanceof MD5) {
                    ha1 = ((MD5)credentials).getDigest();
                }
                else {
                    md.update(this.username.getBytes("ISO-8859-1"));
                    md.update((byte)58);
                    md.update(this.realm.getBytes("ISO-8859-1"));
                    md.update((byte)58);
                    md.update(password.getBytes("ISO-8859-1"));
                    ha1 = md.digest();
                }
                md.reset();
                md.update(this.method.getBytes("ISO-8859-1"));
                md.update((byte)58);
                md.update(this.uri.getBytes("ISO-8859-1"));
                final byte[] ha2 = md.digest();
                md.update(TypeUtil.toString(ha1, 16).getBytes("ISO-8859-1"));
                md.update((byte)58);
                md.update(this.nonce.getBytes("ISO-8859-1"));
                md.update((byte)58);
                md.update(this.nc.getBytes("ISO-8859-1"));
                md.update((byte)58);
                md.update(this.cnonce.getBytes("ISO-8859-1"));
                md.update((byte)58);
                md.update(this.qop.getBytes("ISO-8859-1"));
                md.update((byte)58);
                md.update(TypeUtil.toString(ha2, 16).getBytes("ISO-8859-1"));
                final byte[] digest = md.digest();
                return TypeUtil.toString(digest, 16).equalsIgnoreCase(this.response);
            }
            catch (Exception e) {
                DigestAuthModule.LOG.warn(e);
                return false;
            }
        }
        
        @Override
        public String toString() {
            return this.username + "," + this.response;
        }
    }
}
