// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.util.Arrays;
import com.google.common.net.InetAddresses;
import org.xbill.DNS.Name;
import org.xbill.DNS.ResolverConfig;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import org.apache.hadoop.util.ZKUtil;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.util.StopWatch;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedAction;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenInfo;
import java.util.Iterator;
import java.net.InetSocketAddress;
import org.apache.hadoop.net.NetUtils;
import java.net.URI;
import java.net.UnknownHostException;
import org.apache.hadoop.net.DNS;
import javax.annotation.Nullable;
import org.apache.hadoop.util.StringUtils;
import java.net.InetAddress;
import java.io.IOException;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.hadoop.conf.Configuration;
import java.util.ServiceLoader;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public final class SecurityUtil
{
    public static final Logger LOG;
    public static final String HOSTNAME_PATTERN = "_HOST";
    public static final String FAILED_TO_GET_UGI_MSG_HEADER = "Failed to obtain user group information:";
    @VisibleForTesting
    static boolean useIpForTokenService;
    @VisibleForTesting
    static HostResolver hostResolver;
    private static boolean logSlowLookups;
    private static int slowLookupThresholdMs;
    private static ServiceLoader<SecurityInfo> securityInfoProviders;
    private static SecurityInfo[] testProviders;
    
    private SecurityUtil() {
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static void setConfiguration(final Configuration conf) {
        SecurityUtil.LOG.info("Updating Configuration");
        setConfigurationInternal(conf);
    }
    
    private static void setConfigurationInternal(final Configuration conf) {
        final boolean useIp = conf.getBoolean("hadoop.security.token.service.use_ip", true);
        setTokenServiceUseIp(useIp);
        SecurityUtil.logSlowLookups = conf.getBoolean("hadoop.security.dns.log-slow-lookups.enabled", false);
        SecurityUtil.slowLookupThresholdMs = conf.getInt("hadoop.security.dns.log-slow-lookups.threshold.ms", 1000);
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public static void setTokenServiceUseIp(final boolean flag) {
        if (SecurityUtil.LOG.isDebugEnabled()) {
            SecurityUtil.LOG.debug("Setting hadoop.security.token.service.use_ip to " + flag);
        }
        SecurityUtil.hostResolver = ((SecurityUtil.useIpForTokenService = flag) ? new StandardHostResolver() : new QualifiedHostResolver());
    }
    
    static boolean isTGSPrincipal(final KerberosPrincipal principal) {
        return principal != null && principal.getName().equals("krbtgt/" + principal.getRealm() + "@" + principal.getRealm());
    }
    
    protected static boolean isOriginalTGT(final KerberosTicket ticket) {
        return isTGSPrincipal(ticket.getServer());
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static String getServerPrincipal(final String principalConfig, final String hostname) throws IOException {
        final String[] components = getComponents(principalConfig);
        if (components == null || components.length != 3 || !components[1].equals("_HOST")) {
            return principalConfig;
        }
        return replacePattern(components, hostname);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static String getServerPrincipal(final String principalConfig, final InetAddress addr) throws IOException {
        final String[] components = getComponents(principalConfig);
        if (components == null || components.length != 3 || !components[1].equals("_HOST")) {
            return principalConfig;
        }
        if (addr == null) {
            throw new IOException("Can't replace _HOST pattern since client address is null");
        }
        return replacePattern(components, addr.getCanonicalHostName());
    }
    
    private static String[] getComponents(final String principalConfig) {
        if (principalConfig == null) {
            return null;
        }
        return principalConfig.split("[/@]");
    }
    
    private static String replacePattern(final String[] components, final String hostname) throws IOException {
        String fqdn = hostname;
        if (fqdn == null || fqdn.isEmpty() || fqdn.equals("0.0.0.0")) {
            fqdn = getLocalHostName(null);
        }
        return components[0] + "/" + StringUtils.toLowerCase(fqdn) + "@" + components[2];
    }
    
    static String getLocalHostName(@Nullable final Configuration conf) throws UnknownHostException {
        if (conf != null) {
            final String dnsInterface = conf.get("hadoop.security.dns.interface");
            final String nameServer = conf.get("hadoop.security.dns.nameserver");
            if (dnsInterface != null) {
                return DNS.getDefaultHost(dnsInterface, nameServer, true);
            }
            if (nameServer != null) {
                throw new IllegalArgumentException("hadoop.security.dns.nameserver requires hadoop.security.dns.interface. Check yourconfiguration.");
            }
        }
        return InetAddress.getLocalHost().getCanonicalHostName();
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static void login(final Configuration conf, final String keytabFileKey, final String userNameKey) throws IOException {
        login(conf, keytabFileKey, userNameKey, getLocalHostName(conf));
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static void login(final Configuration conf, final String keytabFileKey, final String userNameKey, final String hostname) throws IOException {
        if (!UserGroupInformation.isSecurityEnabled()) {
            return;
        }
        final String keytabFilename = conf.get(keytabFileKey);
        if (keytabFilename == null || keytabFilename.length() == 0) {
            throw new IOException("Running in secure mode, but config doesn't have a keytab");
        }
        final String principalConfig = conf.get(userNameKey, System.getProperty("user.name"));
        final String principalName = getServerPrincipal(principalConfig, hostname);
        UserGroupInformation.loginUserFromKeytab(principalName, keytabFilename);
    }
    
    public static String buildDTServiceName(final URI uri, final int defPort) {
        final String authority = uri.getAuthority();
        if (authority == null) {
            return null;
        }
        final InetSocketAddress addr = NetUtils.createSocketAddr(authority, defPort);
        return buildTokenService(addr).toString();
    }
    
    public static String getHostFromPrincipal(final String principalName) {
        return new HadoopKerberosName(principalName).getHostName();
    }
    
    @InterfaceAudience.Private
    public static void setSecurityInfoProviders(final SecurityInfo... providers) {
        SecurityUtil.testProviders = providers;
    }
    
    public static KerberosInfo getKerberosInfo(final Class<?> protocol, final Configuration conf) {
        for (final SecurityInfo provider : SecurityUtil.testProviders) {
            final KerberosInfo result = provider.getKerberosInfo(protocol, conf);
            if (result != null) {
                return result;
            }
        }
        synchronized (SecurityUtil.securityInfoProviders) {
            for (final SecurityInfo provider2 : SecurityUtil.securityInfoProviders) {
                final KerberosInfo result2 = provider2.getKerberosInfo(protocol, conf);
                if (result2 != null) {
                    return result2;
                }
            }
        }
        return null;
    }
    
    public static TokenInfo getTokenInfo(final Class<?> protocol, final Configuration conf) {
        for (final SecurityInfo provider : SecurityUtil.testProviders) {
            final TokenInfo result = provider.getTokenInfo(protocol, conf);
            if (result != null) {
                return result;
            }
        }
        synchronized (SecurityUtil.securityInfoProviders) {
            for (final SecurityInfo provider2 : SecurityUtil.securityInfoProviders) {
                final TokenInfo result2 = provider2.getTokenInfo(protocol, conf);
                if (result2 != null) {
                    return result2;
                }
            }
        }
        return null;
    }
    
    public static InetSocketAddress getTokenServiceAddr(final Token<?> token) {
        return NetUtils.createSocketAddr(token.getService().toString());
    }
    
    public static void setTokenService(final Token<?> token, final InetSocketAddress addr) {
        final Text service = buildTokenService(addr);
        if (token != null) {
            token.setService(service);
            if (SecurityUtil.LOG.isDebugEnabled()) {
                SecurityUtil.LOG.debug("Acquired token " + token);
            }
        }
        else {
            SecurityUtil.LOG.warn("Failed to get token for service " + service);
        }
    }
    
    public static Text buildTokenService(final InetSocketAddress addr) {
        String host = null;
        if (SecurityUtil.useIpForTokenService) {
            if (addr.isUnresolved()) {
                throw new IllegalArgumentException(new UnknownHostException(addr.getHostName()));
            }
            host = addr.getAddress().getHostAddress();
        }
        else {
            host = StringUtils.toLowerCase(addr.getHostName());
        }
        return new Text(host + ":" + addr.getPort());
    }
    
    public static Text buildTokenService(final URI uri) {
        return buildTokenService(NetUtils.createSocketAddr(uri.getAuthority()));
    }
    
    public static <T> T doAsLoginUserOrFatal(final PrivilegedAction<T> action) {
        if (UserGroupInformation.isSecurityEnabled()) {
            UserGroupInformation ugi = null;
            try {
                ugi = UserGroupInformation.getLoginUser();
            }
            catch (IOException e) {
                SecurityUtil.LOG.error("Exception while getting login user", e);
                e.printStackTrace();
                Runtime.getRuntime().exit(-1);
            }
            return ugi.doAs(action);
        }
        return action.run();
    }
    
    public static <T> T doAsLoginUser(final PrivilegedExceptionAction<T> action) throws IOException {
        return doAsUser(UserGroupInformation.getLoginUser(), action);
    }
    
    public static <T> T doAsCurrentUser(final PrivilegedExceptionAction<T> action) throws IOException {
        return doAsUser(UserGroupInformation.getCurrentUser(), action);
    }
    
    private static <T> T doAsUser(final UserGroupInformation ugi, final PrivilegedExceptionAction<T> action) throws IOException {
        try {
            return ugi.doAs(action);
        }
        catch (InterruptedException ie) {
            throw new IOException(ie);
        }
    }
    
    @InterfaceAudience.Private
    public static InetAddress getByName(final String hostname) throws UnknownHostException {
        if (SecurityUtil.logSlowLookups || SecurityUtil.LOG.isTraceEnabled()) {
            final StopWatch lookupTimer = new StopWatch().start();
            final InetAddress result = SecurityUtil.hostResolver.getByName(hostname);
            final long elapsedMs = lookupTimer.stop().now(TimeUnit.MILLISECONDS);
            if (elapsedMs >= SecurityUtil.slowLookupThresholdMs) {
                SecurityUtil.LOG.warn("Slow name lookup for " + hostname + ". Took " + elapsedMs + " ms.");
            }
            else if (SecurityUtil.LOG.isTraceEnabled()) {
                SecurityUtil.LOG.trace("Name lookup for " + hostname + " took " + elapsedMs + " ms.");
            }
            return result;
        }
        return SecurityUtil.hostResolver.getByName(hostname);
    }
    
    public static UserGroupInformation.AuthenticationMethod getAuthenticationMethod(final Configuration conf) {
        final String value = conf.get("hadoop.security.authentication", "simple");
        try {
            return Enum.valueOf(UserGroupInformation.AuthenticationMethod.class, StringUtils.toUpperCase(value));
        }
        catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Invalid attribute value for hadoop.security.authentication of " + value);
        }
    }
    
    public static void setAuthenticationMethod(UserGroupInformation.AuthenticationMethod authenticationMethod, final Configuration conf) {
        if (authenticationMethod == null) {
            authenticationMethod = UserGroupInformation.AuthenticationMethod.SIMPLE;
        }
        conf.set("hadoop.security.authentication", StringUtils.toLowerCase(authenticationMethod.toString()));
    }
    
    public static boolean isPrivilegedPort(final int port) {
        return port < 1024;
    }
    
    public static List<ZKUtil.ZKAuthInfo> getZKAuthInfos(final Configuration conf, final String configKey) throws IOException {
        final char[] zkAuthChars = conf.getPassword(configKey);
        String zkAuthConf = (zkAuthChars != null) ? String.valueOf(zkAuthChars) : null;
        try {
            zkAuthConf = ZKUtil.resolveConfIndirection(zkAuthConf);
            if (zkAuthConf != null) {
                return ZKUtil.parseAuth(zkAuthConf);
            }
            return Collections.emptyList();
        }
        catch (IOException | ZKUtil.BadAuthFormatException ex2) {
            final Exception ex;
            final Exception e = ex;
            SecurityUtil.LOG.error("Couldn't read Auth based on {}", configKey);
            throw e;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(SecurityUtil.class);
        setConfigurationInternal(new Configuration());
        SecurityUtil.securityInfoProviders = ServiceLoader.load(SecurityInfo.class);
        SecurityUtil.testProviders = new SecurityInfo[0];
    }
    
    static class StandardHostResolver implements HostResolver
    {
        @Override
        public InetAddress getByName(final String host) throws UnknownHostException {
            return InetAddress.getByName(host);
        }
    }
    
    protected static class QualifiedHostResolver implements HostResolver
    {
        private List<String> searchDomains;
        
        protected QualifiedHostResolver() {
            this.searchDomains = new ArrayList<String>();
            final ResolverConfig resolverConfig = ResolverConfig.getCurrentConfig();
            final Name[] names = resolverConfig.searchPath();
            if (names != null) {
                for (final Name name : names) {
                    this.searchDomains.add(name.toString());
                }
            }
        }
        
        @Override
        public InetAddress getByName(final String host) throws UnknownHostException {
            InetAddress addr = null;
            if (InetAddresses.isInetAddress(host)) {
                addr = InetAddresses.forString(host);
                addr = InetAddress.getByAddress(host, addr.getAddress());
            }
            else if (host.endsWith(".")) {
                addr = this.getByExactName(host);
            }
            else if (host.contains(".")) {
                addr = this.getByExactName(host);
                if (addr == null) {
                    addr = this.getByNameWithSearch(host);
                }
            }
            else {
                final InetAddress loopback = InetAddress.getByName(null);
                if (host.equalsIgnoreCase(loopback.getHostName())) {
                    addr = InetAddress.getByAddress(host, loopback.getAddress());
                }
                else {
                    addr = this.getByNameWithSearch(host);
                    if (addr == null) {
                        addr = this.getByExactName(host);
                    }
                }
            }
            if (addr == null) {
                throw new UnknownHostException(host);
            }
            return addr;
        }
        
        InetAddress getByExactName(final String host) {
            InetAddress addr = null;
            String fqHost = host;
            if (!fqHost.endsWith(".")) {
                fqHost += ".";
            }
            try {
                addr = this.getInetAddressByName(fqHost);
                addr = InetAddress.getByAddress(host, addr.getAddress());
            }
            catch (UnknownHostException ex) {}
            return addr;
        }
        
        InetAddress getByNameWithSearch(final String host) {
            InetAddress addr = null;
            if (host.endsWith(".")) {
                addr = this.getByExactName(host);
            }
            else {
                for (final String domain : this.searchDomains) {
                    final String dot = domain.startsWith(".") ? "" : ".";
                    addr = this.getByExactName(host + dot + domain);
                    if (addr != null) {
                        break;
                    }
                }
            }
            return addr;
        }
        
        InetAddress getInetAddressByName(final String host) throws UnknownHostException {
            return InetAddress.getByName(host);
        }
        
        void setSearchDomains(final String... domains) {
            this.searchDomains = Arrays.asList(domains);
        }
    }
    
    interface HostResolver
    {
        InetAddress getByName(final String p0) throws UnknownHostException;
    }
}
