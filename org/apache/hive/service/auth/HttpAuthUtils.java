// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.commons.codec.binary.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.apache.commons.logging.LogFactory;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.security.AccessControlContext;
import org.apache.hadoop.security.UserGroupInformation;
import javax.security.auth.Subject;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.hive.shims.ShimLoader;
import java.util.Set;
import org.apache.commons.logging.Log;

public final class HttpAuthUtils
{
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC = "Basic";
    public static final String NEGOTIATE = "Negotiate";
    private static final Log LOG;
    private static final String COOKIE_ATTR_SEPARATOR = "&";
    private static final String COOKIE_CLIENT_USER_NAME = "cu";
    private static final String COOKIE_CLIENT_RAND_NUMBER = "rn";
    private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";
    private static final Set<String> COOKIE_ATTRIBUTES;
    
    public static String getKerberosServiceTicket(final String principal, final String host, final String serverHttpUrl, final boolean assumeSubject) throws Exception {
        final String serverPrincipal = ShimLoader.getHadoopThriftAuthBridge().getServerPrincipal(principal, host);
        if (!assumeSubject) {
            final UserGroupInformation clientUGI = ShimLoader.getHadoopThriftAuthBridge().getCurrentUGIWithConf("kerberos");
            return clientUGI.doAs((PrivilegedExceptionAction<String>)new HttpKerberosClientAction(serverPrincipal, serverHttpUrl));
        }
        final AccessControlContext context = AccessController.getContext();
        final Subject subject = Subject.getSubject(context);
        if (subject == null) {
            throw new Exception("The Subject is not set");
        }
        return Subject.doAs(subject, (PrivilegedExceptionAction<String>)new HttpKerberosClientAction(serverPrincipal, serverHttpUrl));
    }
    
    public static String createCookieToken(final String clientUserName) {
        final StringBuffer sb = new StringBuffer();
        sb.append("cu").append("=").append(clientUserName).append("&");
        sb.append("rn").append("=").append(new Random(System.currentTimeMillis()).nextLong());
        return sb.toString();
    }
    
    public static String getUserNameFromCookieToken(final String tokenStr) {
        final Map<String, String> map = splitCookieToken(tokenStr);
        if (!map.keySet().equals(HttpAuthUtils.COOKIE_ATTRIBUTES)) {
            HttpAuthUtils.LOG.error("Invalid token with missing attributes " + tokenStr);
            return null;
        }
        return map.get("cu");
    }
    
    private static Map<String, String> splitCookieToken(final String tokenStr) {
        final Map<String, String> map = new HashMap<String, String>();
        final StringTokenizer st = new StringTokenizer(tokenStr, "&");
        while (st.hasMoreTokens()) {
            final String part = st.nextToken();
            final int separator = part.indexOf("=");
            if (separator == -1) {
                HttpAuthUtils.LOG.error("Invalid token string " + tokenStr);
                return null;
            }
            final String key = part.substring(0, separator);
            final String value = part.substring(separator + 1);
            map.put(key, value);
        }
        return map;
    }
    
    private HttpAuthUtils() {
        throw new UnsupportedOperationException("Can't initialize class");
    }
    
    static {
        LOG = LogFactory.getLog(HttpAuthUtils.class);
        COOKIE_ATTRIBUTES = new HashSet<String>(Arrays.asList("cu", "rn"));
    }
    
    public static class HttpKerberosClientAction implements PrivilegedExceptionAction<String>
    {
        public static final String HTTP_RESPONSE = "HTTP_RESPONSE";
        public static final String SERVER_HTTP_URL = "SERVER_HTTP_URL";
        private final String serverPrincipal;
        private final String serverHttpUrl;
        private final Base64 base64codec;
        private final HttpContext httpContext;
        
        public HttpKerberosClientAction(final String serverPrincipal, final String serverHttpUrl) {
            this.serverPrincipal = serverPrincipal;
            this.serverHttpUrl = serverHttpUrl;
            this.base64codec = new Base64(0);
            (this.httpContext = new BasicHttpContext()).setAttribute("SERVER_HTTP_URL", serverHttpUrl);
        }
        
        @Override
        public String run() throws Exception {
            final Oid mechOid = new Oid("1.2.840.113554.1.2.2");
            final Oid krb5PrincipalOid = new Oid("1.2.840.113554.1.2.2.1");
            final GSSManager manager = GSSManager.getInstance();
            final GSSName serverName = manager.createName(this.serverPrincipal, krb5PrincipalOid);
            final GSSContext gssContext = manager.createContext(serverName, mechOid, null, 0);
            gssContext.requestMutualAuth(false);
            final byte[] inToken = new byte[0];
            final byte[] outToken = gssContext.initSecContext(inToken, 0, inToken.length);
            gssContext.dispose();
            return new String(this.base64codec.encode(outToken));
        }
    }
}
