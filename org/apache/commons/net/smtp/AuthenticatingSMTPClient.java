// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net.smtp;

import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import org.apache.commons.net.util.Base64;
import java.net.InetAddress;
import java.io.IOException;
import javax.net.ssl.SSLContext;

public class AuthenticatingSMTPClient extends SMTPSClient
{
    public AuthenticatingSMTPClient() {
    }
    
    public AuthenticatingSMTPClient(final String protocol) {
        super(protocol);
    }
    
    public AuthenticatingSMTPClient(final String proto, final boolean implicit) {
        super(proto, implicit);
    }
    
    public AuthenticatingSMTPClient(final String proto, final boolean implicit, final String encoding) {
        super(proto, implicit, encoding);
    }
    
    public AuthenticatingSMTPClient(final boolean implicit, final SSLContext ctx) {
        super(implicit, ctx);
    }
    
    public AuthenticatingSMTPClient(final String protocol, final String encoding) {
        super(protocol, false, encoding);
    }
    
    public int ehlo(final String hostname) throws IOException {
        return this.sendCommand(15, hostname);
    }
    
    public boolean elogin(final String hostname) throws IOException {
        return SMTPReply.isPositiveCompletion(this.ehlo(hostname));
    }
    
    public boolean elogin() throws IOException {
        final InetAddress host = this.getLocalAddress();
        final String name = host.getHostName();
        return name != null && SMTPReply.isPositiveCompletion(this.ehlo(name));
    }
    
    public int[] getEnhancedReplyCode() {
        final String reply = this.getReplyString().substring(4);
        final String[] parts = reply.substring(0, reply.indexOf(32)).split("\\.");
        final int[] res = new int[parts.length];
        for (int i = 0; i < parts.length; ++i) {
            res[i] = Integer.parseInt(parts[i]);
        }
        return res;
    }
    
    public boolean auth(final AUTH_METHOD method, final String username, final String password) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        if (!SMTPReply.isPositiveIntermediate(this.sendCommand(14, AUTH_METHOD.getAuthName(method)))) {
            return false;
        }
        if (method.equals(AUTH_METHOD.PLAIN)) {
            return SMTPReply.isPositiveCompletion(this.sendCommand(Base64.encodeBase64StringUnChunked(("\u0000" + username + "\u0000" + password).getBytes(this.getCharset()))));
        }
        if (method.equals(AUTH_METHOD.CRAM_MD5)) {
            final byte[] serverChallenge = Base64.decodeBase64(this.getReplyString().substring(4).trim());
            final Mac hmac_md5 = Mac.getInstance("HmacMD5");
            hmac_md5.init(new SecretKeySpec(password.getBytes(this.getCharset()), "HmacMD5"));
            final byte[] hmacResult = this._convertToHexString(hmac_md5.doFinal(serverChallenge)).getBytes(this.getCharset());
            final byte[] usernameBytes = username.getBytes(this.getCharset());
            final byte[] toEncode = new byte[usernameBytes.length + 1 + hmacResult.length];
            System.arraycopy(usernameBytes, 0, toEncode, 0, usernameBytes.length);
            toEncode[usernameBytes.length] = 32;
            System.arraycopy(hmacResult, 0, toEncode, usernameBytes.length + 1, hmacResult.length);
            return SMTPReply.isPositiveCompletion(this.sendCommand(Base64.encodeBase64StringUnChunked(toEncode)));
        }
        if (method.equals(AUTH_METHOD.LOGIN)) {
            return SMTPReply.isPositiveIntermediate(this.sendCommand(Base64.encodeBase64StringUnChunked(username.getBytes(this.getCharset())))) && SMTPReply.isPositiveCompletion(this.sendCommand(Base64.encodeBase64StringUnChunked(password.getBytes(this.getCharset()))));
        }
        return method.equals(AUTH_METHOD.XOAUTH) && SMTPReply.isPositiveIntermediate(this.sendCommand(Base64.encodeBase64StringUnChunked(username.getBytes(this.getCharset()))));
    }
    
    private String _convertToHexString(final byte[] a) {
        final StringBuilder result = new StringBuilder(a.length * 2);
        for (final byte element : a) {
            if ((element & 0xFF) <= 15) {
                result.append("0");
            }
            result.append(Integer.toHexString(element & 0xFF));
        }
        return result.toString();
    }
    
    public enum AUTH_METHOD
    {
        PLAIN, 
        CRAM_MD5, 
        LOGIN, 
        XOAUTH;
        
        public static final String getAuthName(final AUTH_METHOD method) {
            if (method.equals(AUTH_METHOD.PLAIN)) {
                return "PLAIN";
            }
            if (method.equals(AUTH_METHOD.CRAM_MD5)) {
                return "CRAM-MD5";
            }
            if (method.equals(AUTH_METHOD.LOGIN)) {
                return "LOGIN";
            }
            if (method.equals(AUTH_METHOD.XOAUTH)) {
                return "XOAUTH";
            }
            return null;
        }
    }
}
