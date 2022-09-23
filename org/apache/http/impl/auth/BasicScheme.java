// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.impl.auth;

import org.apache.http.message.BufferedHeader;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.util.Args;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.Header;
import org.apache.http.Consts;
import org.apache.http.auth.ChallengeState;
import java.nio.charset.Charset;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class BasicScheme extends RFC2617Scheme
{
    private static final long serialVersionUID = -1931571557597830536L;
    private boolean complete;
    
    public BasicScheme(final Charset credentialsCharset) {
        super(credentialsCharset);
        this.complete = false;
    }
    
    @Deprecated
    public BasicScheme(final ChallengeState challengeState) {
        super(challengeState);
    }
    
    public BasicScheme() {
        this(Consts.ASCII);
    }
    
    @Override
    public String getSchemeName() {
        return "basic";
    }
    
    @Override
    public void processChallenge(final Header header) throws MalformedChallengeException {
        super.processChallenge(header);
        this.complete = true;
    }
    
    @Override
    public boolean isComplete() {
        return this.complete;
    }
    
    @Override
    public boolean isConnectionBased() {
        return false;
    }
    
    @Deprecated
    @Override
    public Header authenticate(final Credentials credentials, final HttpRequest request) throws AuthenticationException {
        return this.authenticate(credentials, request, new BasicHttpContext());
    }
    
    @Override
    public Header authenticate(final Credentials credentials, final HttpRequest request, final HttpContext context) throws AuthenticationException {
        Args.notNull(credentials, "Credentials");
        Args.notNull(request, "HTTP request");
        final StringBuilder tmp = new StringBuilder();
        tmp.append(credentials.getUserPrincipal().getName());
        tmp.append(":");
        tmp.append((credentials.getPassword() == null) ? "null" : credentials.getPassword());
        final Base64 base64codec = new Base64(0);
        final byte[] base64password = base64codec.encode(EncodingUtils.getBytes(tmp.toString(), this.getCredentialsCharset(request)));
        final CharArrayBuffer buffer = new CharArrayBuffer(32);
        if (this.isProxy()) {
            buffer.append("Proxy-Authorization");
        }
        else {
            buffer.append("Authorization");
        }
        buffer.append(": Basic ");
        buffer.append(base64password, 0, base64password.length);
        return new BufferedHeader(buffer);
    }
    
    @Deprecated
    public static Header authenticate(final Credentials credentials, final String charset, final boolean proxy) {
        Args.notNull(credentials, "Credentials");
        Args.notNull(charset, "charset");
        final StringBuilder tmp = new StringBuilder();
        tmp.append(credentials.getUserPrincipal().getName());
        tmp.append(":");
        tmp.append((credentials.getPassword() == null) ? "null" : credentials.getPassword());
        final byte[] base64password = Base64.encodeBase64(EncodingUtils.getBytes(tmp.toString(), charset), false);
        final CharArrayBuffer buffer = new CharArrayBuffer(32);
        if (proxy) {
            buffer.append("Proxy-Authorization");
        }
        else {
            buffer.append("Authorization");
        }
        buffer.append(": Basic ");
        buffer.append(base64password, 0, base64password.length);
        return new BufferedHeader(buffer);
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BASIC [complete=").append(this.complete).append("]");
        return builder.toString();
    }
}
