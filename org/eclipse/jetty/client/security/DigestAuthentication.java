// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.security;

import org.eclipse.jetty.util.TypeUtil;
import java.security.MessageDigest;
import java.io.IOException;
import org.eclipse.jetty.client.HttpExchange;
import java.util.Map;

public class DigestAuthentication implements Authentication
{
    private static final String NC = "00000001";
    Realm securityRealm;
    Map details;
    
    public DigestAuthentication(final Realm realm, final Map details) {
        this.securityRealm = realm;
        this.details = details;
    }
    
    public void setCredentials(final HttpExchange exchange) throws IOException {
        final StringBuilder buffer = new StringBuilder().append("Digest");
        buffer.append(" ").append("username").append('=').append('\"').append(this.securityRealm.getPrincipal()).append('\"');
        buffer.append(", ").append("realm").append('=').append('\"').append(String.valueOf(this.details.get("realm"))).append('\"');
        buffer.append(", ").append("nonce").append('=').append('\"').append(String.valueOf(this.details.get("nonce"))).append('\"');
        buffer.append(", ").append("uri").append('=').append('\"').append(exchange.getURI()).append('\"');
        buffer.append(", ").append("algorithm").append('=').append(String.valueOf(this.details.get("algorithm")));
        final String cnonce = this.newCnonce(exchange, this.securityRealm, this.details);
        buffer.append(", ").append("response").append('=').append('\"').append(this.newResponse(cnonce, exchange, this.securityRealm, this.details)).append('\"');
        buffer.append(", ").append("qop").append('=').append(String.valueOf(this.details.get("qop")));
        buffer.append(", ").append("nc").append('=').append("00000001");
        buffer.append(", ").append("cnonce").append('=').append('\"').append(cnonce).append('\"');
        exchange.setRequestHeader("Authorization", new String(buffer.toString().getBytes("ISO-8859-1")));
    }
    
    protected String newResponse(final String cnonce, final HttpExchange exchange, final Realm securityRealm, final Map details) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(securityRealm.getPrincipal().getBytes("ISO-8859-1"));
            md.update((byte)58);
            md.update(String.valueOf(details.get("realm")).getBytes("ISO-8859-1"));
            md.update((byte)58);
            md.update(securityRealm.getCredentials().getBytes("ISO-8859-1"));
            final byte[] ha1 = md.digest();
            md.reset();
            md.update(exchange.getMethod().getBytes("ISO-8859-1"));
            md.update((byte)58);
            md.update(exchange.getURI().getBytes("ISO-8859-1"));
            final byte[] ha2 = md.digest();
            md.update(TypeUtil.toString(ha1, 16).getBytes("ISO-8859-1"));
            md.update((byte)58);
            md.update(String.valueOf(details.get("nonce")).getBytes("ISO-8859-1"));
            md.update((byte)58);
            md.update("00000001".getBytes("ISO-8859-1"));
            md.update((byte)58);
            md.update(cnonce.getBytes("ISO-8859-1"));
            md.update((byte)58);
            md.update(String.valueOf(details.get("qop")).getBytes("ISO-8859-1"));
            md.update((byte)58);
            md.update(TypeUtil.toString(ha2, 16).getBytes("ISO-8859-1"));
            final byte[] digest = md.digest();
            return encode(digest);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    protected String newCnonce(final HttpExchange exchange, final Realm securityRealm, final Map details) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] b = md.digest(String.valueOf(System.currentTimeMillis()).getBytes("ISO-8859-1"));
            return encode(b);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static String encode(final byte[] data) {
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < data.length; ++i) {
            buffer.append(Integer.toHexString((data[i] & 0xF0) >>> 4));
            buffer.append(Integer.toHexString(data[i] & 0xF));
        }
        return buffer.toString();
    }
}
