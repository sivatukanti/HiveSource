// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.util;

import java.util.HashSet;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import java.util.Collection;
import java.util.Set;
import java.security.Principal;

public class AuthToken implements Principal
{
    private static final String ATTR_SEPARATOR = "&";
    private static final String USER_NAME = "u";
    private static final String PRINCIPAL = "p";
    private static final String MAX_INACTIVES = "i";
    private static final String EXPIRES = "e";
    private static final String TYPE = "t";
    private static final Set<String> ATTRIBUTES;
    private String userName;
    private String principal;
    private String type;
    private long maxInactives;
    private long expires;
    private String tokenStr;
    private static final String ILLEGAL_ARG_MSG = " is NULL, empty or contains a '&'";
    
    protected AuthToken() {
        this.userName = null;
        this.principal = null;
        this.type = null;
        this.maxInactives = -1L;
        this.expires = -1L;
        this.tokenStr = "ANONYMOUS";
        this.generateToken();
    }
    
    public AuthToken(final String userName, final String principal, final String type) {
        checkForIllegalArgument(userName, "userName");
        checkForIllegalArgument(principal, "principal");
        checkForIllegalArgument(type, "type");
        this.userName = userName;
        this.principal = principal;
        this.type = type;
        this.maxInactives = -1L;
        this.expires = -1L;
    }
    
    protected static void checkForIllegalArgument(final String value, final String name) {
        if (value == null || value.length() == 0 || value.contains("&")) {
            throw new IllegalArgumentException(name + " is NULL, empty or contains a '&'");
        }
    }
    
    public void setMaxInactives(final long interval) {
        this.maxInactives = interval;
    }
    
    public void setExpires(final long expires) {
        this.expires = expires;
        this.generateToken();
    }
    
    public boolean isExpired() {
        return (this.getMaxInactives() != -1L && System.currentTimeMillis() > this.getMaxInactives()) || (this.getExpires() != -1L && System.currentTimeMillis() > this.getExpires());
    }
    
    private void generateToken() {
        final StringBuffer sb = new StringBuffer();
        sb.append("u").append("=").append(this.getUserName()).append("&");
        sb.append("p").append("=").append(this.getName()).append("&");
        sb.append("t").append("=").append(this.getType()).append("&");
        if (this.getMaxInactives() != -1L) {
            sb.append("i").append("=").append(this.getMaxInactives()).append("&");
        }
        sb.append("e").append("=").append(this.getExpires());
        this.tokenStr = sb.toString();
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    @Override
    public String getName() {
        return this.principal;
    }
    
    public String getType() {
        return this.type;
    }
    
    public long getMaxInactives() {
        return this.maxInactives;
    }
    
    public long getExpires() {
        return this.expires;
    }
    
    @Override
    public String toString() {
        return this.tokenStr;
    }
    
    public static AuthToken parse(String tokenStr) throws AuthenticationException {
        if (tokenStr.length() >= 2 && tokenStr.charAt(0) == '\"' && tokenStr.charAt(tokenStr.length() - 1) == '\"') {
            tokenStr = tokenStr.substring(1, tokenStr.length() - 1);
        }
        final Map<String, String> map = split(tokenStr);
        map.remove("s");
        if (!map.keySet().containsAll(AuthToken.ATTRIBUTES)) {
            throw new AuthenticationException("Invalid token string, missing attributes");
        }
        final long expires = Long.parseLong(map.get("e"));
        final AuthToken token = new AuthToken(map.get("u"), map.get("p"), map.get("t"));
        if (map.containsKey("i")) {
            final long maxInactives = Long.parseLong(map.get("i"));
            token.setMaxInactives(maxInactives);
        }
        token.setExpires(expires);
        return token;
    }
    
    private static Map<String, String> split(final String tokenStr) throws AuthenticationException {
        final Map<String, String> map = new HashMap<String, String>();
        final StringTokenizer st = new StringTokenizer(tokenStr, "&");
        while (st.hasMoreTokens()) {
            final String part = st.nextToken();
            final int separator = part.indexOf(61);
            if (separator == -1) {
                throw new AuthenticationException("Invalid authentication token");
            }
            final String key = part.substring(0, separator);
            final String value = part.substring(separator + 1);
            map.put(key, value);
        }
        return map;
    }
    
    static {
        ATTRIBUTES = new HashSet<String>(Arrays.asList("u", "p", "e", "t"));
    }
}
