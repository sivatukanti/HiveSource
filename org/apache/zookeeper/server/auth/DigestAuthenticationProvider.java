// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.auth;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.ServerCnxn;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import org.slf4j.Logger;

public class DigestAuthenticationProvider implements AuthenticationProvider
{
    private static final Logger LOG;
    private static final String superDigest;
    
    @Override
    public String getScheme() {
        return "digest";
    }
    
    private static final String base64Encode(final byte[] b) {
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < b.length) {
            int pad = 0;
            int v = (b[i++] & 0xFF) << 16;
            if (i < b.length) {
                v |= (b[i++] & 0xFF) << 8;
            }
            else {
                ++pad;
            }
            if (i < b.length) {
                v |= (b[i++] & 0xFF);
            }
            else {
                ++pad;
            }
            sb.append(encode(v >> 18));
            sb.append(encode(v >> 12));
            if (pad < 2) {
                sb.append(encode(v >> 6));
            }
            else {
                sb.append('=');
            }
            if (pad < 1) {
                sb.append(encode(v));
            }
            else {
                sb.append('=');
            }
        }
        return sb.toString();
    }
    
    private static final char encode(int i) {
        i &= 0x3F;
        if (i < 26) {
            return (char)(65 + i);
        }
        if (i < 52) {
            return (char)(97 + i - 26);
        }
        if (i < 62) {
            return (char)(48 + i - 52);
        }
        return (i == 62) ? '+' : '/';
    }
    
    public static String generateDigest(final String idPassword) throws NoSuchAlgorithmException {
        final String[] parts = idPassword.split(":", 2);
        final byte[] digest = MessageDigest.getInstance("SHA1").digest(idPassword.getBytes());
        return parts[0] + ":" + base64Encode(digest);
    }
    
    @Override
    public KeeperException.Code handleAuthentication(final ServerCnxn cnxn, final byte[] authData) {
        final String id = new String(authData);
        try {
            final String digest = generateDigest(id);
            if (digest.equals(DigestAuthenticationProvider.superDigest)) {
                cnxn.addAuthInfo(new Id("super", ""));
            }
            cnxn.addAuthInfo(new Id(this.getScheme(), digest));
            return KeeperException.Code.OK;
        }
        catch (NoSuchAlgorithmException e) {
            DigestAuthenticationProvider.LOG.error("Missing algorithm", e);
            return KeeperException.Code.AUTHFAILED;
        }
    }
    
    @Override
    public boolean isAuthenticated() {
        return true;
    }
    
    @Override
    public boolean isValid(final String id) {
        final String[] parts = id.split(":");
        return parts.length == 2;
    }
    
    @Override
    public boolean matches(final String id, final String aclExpr) {
        return id.equals(aclExpr);
    }
    
    public static void main(final String[] args) throws NoSuchAlgorithmException {
        for (int i = 0; i < args.length; ++i) {
            System.out.println(args[i] + "->" + generateDigest(args[i]));
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(DigestAuthenticationProvider.class);
        superDigest = System.getProperty("zookeeper.DigestAuthenticationProvider.superDigest");
    }
}
