// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.util.LinkedHashMap;
import org.apache.commons.logging.LogFactory;
import java.util.List;
import org.apache.http.cookie.Cookie;
import org.apache.http.client.CookieStore;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import java.net.URI;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.thrift.TStatusCode;
import java.sql.SQLException;
import org.apache.hive.service.cli.thrift.TStatus;
import org.apache.commons.logging.Log;

public class Utils
{
    public static final Log LOG;
    public static final String URL_PREFIX = "jdbc:hive2://";
    public static final String DEFAULT_PORT = "10000";
    public static final String DEFAULT_DATABASE = "default";
    private static final String URI_JDBC_PREFIX = "jdbc:";
    private static final String URI_HIVE_PREFIX = "hive2:";
    static final String HIVE_SERVER2_RETRY_KEY = "hive.server2.retryserver";
    static final String HIVE_SERVER2_RETRY_TRUE = "true";
    static final String HIVE_SERVER2_RETRY_FALSE = "false";
    
    public static void verifySuccessWithInfo(final TStatus status) throws SQLException {
        verifySuccess(status, true);
    }
    
    public static void verifySuccess(final TStatus status) throws SQLException {
        verifySuccess(status, false);
    }
    
    public static void verifySuccess(final TStatus status, final boolean withInfo) throws SQLException {
        if (status.getStatusCode() == TStatusCode.SUCCESS_STATUS || (withInfo && status.getStatusCode() == TStatusCode.SUCCESS_WITH_INFO_STATUS)) {
            return;
        }
        throw new HiveSQLException(status);
    }
    
    public static JdbcConnectionParams parseURL(String uri) throws JdbcUriParseException, SQLException, ZooKeeperHiveClientException {
        final JdbcConnectionParams connParams = new JdbcConnectionParams();
        if (!uri.startsWith("jdbc:hive2://")) {
            throw new JdbcUriParseException("Bad URL format: Missing prefix jdbc:hive2://");
        }
        if (uri.equalsIgnoreCase("jdbc:hive2://")) {
            connParams.setEmbeddedMode(true);
            return connParams;
        }
        final String dummyAuthorityString = "dummyhost:00000";
        final String suppliedAuthorities = getAuthorities(uri, connParams);
        if (suppliedAuthorities == null || suppliedAuthorities.isEmpty()) {
            connParams.setEmbeddedMode(true);
        }
        else {
            Utils.LOG.info("Supplied authorities: " + suppliedAuthorities);
            final String[] authorityList = suppliedAuthorities.split(",");
            connParams.setSuppliedAuthorityList(authorityList);
            uri = uri.replace(suppliedAuthorities, dummyAuthorityString);
        }
        final URI jdbcURI = URI.create(uri.substring("jdbc:".length()));
        final Pattern pattern = Pattern.compile("([^;]*)=([^;]*)[;]?");
        String sessVars = jdbcURI.getPath();
        if (sessVars != null && !sessVars.isEmpty()) {
            String dbName = "";
            sessVars = sessVars.substring(1);
            if (!sessVars.contains(";")) {
                dbName = sessVars;
            }
            else {
                dbName = sessVars.substring(0, sessVars.indexOf(59));
                sessVars = sessVars.substring(sessVars.indexOf(59) + 1);
                if (sessVars != null) {
                    final Matcher sessMatcher = pattern.matcher(sessVars);
                    while (sessMatcher.find()) {
                        if (connParams.getSessionVars().put(sessMatcher.group(1), sessMatcher.group(2)) != null) {
                            throw new JdbcUriParseException("Bad URL format: Multiple values for property " + sessMatcher.group(1));
                        }
                    }
                }
            }
            if (!dbName.isEmpty()) {
                connParams.setDbName(dbName);
            }
        }
        final String confStr = jdbcURI.getQuery();
        if (confStr != null) {
            final Matcher confMatcher = pattern.matcher(confStr);
            while (confMatcher.find()) {
                connParams.getHiveConfs().put(confMatcher.group(1), confMatcher.group(2));
            }
        }
        final String varStr = jdbcURI.getFragment();
        if (varStr != null) {
            final Matcher varMatcher = pattern.matcher(varStr);
            while (varMatcher.find()) {
                connParams.getHiveVars().put(varMatcher.group(1), varMatcher.group(2));
            }
        }
        final String usageUrlBase = "jdbc:hive2://<host>:<port>/dbName;";
        String newUsage = usageUrlBase + "saslQop" + "=<qop_value>";
        handleParamDeprecation(connParams.getSessionVars(), connParams.getSessionVars(), "sasl.qop", "saslQop", newUsage);
        newUsage = usageUrlBase + "transportMode" + "=<transport_mode_value>";
        handleParamDeprecation(connParams.getHiveConfs(), connParams.getSessionVars(), "hive.server2.transport.mode", "transportMode", newUsage);
        newUsage = usageUrlBase + "httpPath" + "=<http_path_value>";
        handleParamDeprecation(connParams.getHiveConfs(), connParams.getSessionVars(), "hive.server2.thrift.http.path", "httpPath", newUsage);
        if (connParams.isEmbeddedMode()) {
            connParams.setHost(jdbcURI.getHost());
            connParams.setPort(jdbcURI.getPort());
        }
        else {
            final String resolvedAuthorityString = resolveAuthority(connParams);
            Utils.LOG.info("Resolved authority: " + resolvedAuthorityString);
            uri = uri.replace(dummyAuthorityString, resolvedAuthorityString);
            connParams.setJdbcUriString(uri);
            URI resolvedAuthorityURI = null;
            try {
                resolvedAuthorityURI = new URI(null, resolvedAuthorityString, null, null, null);
            }
            catch (URISyntaxException e) {
                throw new JdbcUriParseException("Bad URL format: ", e);
            }
            connParams.setHost(resolvedAuthorityURI.getHost());
            connParams.setPort(resolvedAuthorityURI.getPort());
        }
        return connParams;
    }
    
    private static void handleParamDeprecation(final Map<String, String> fromMap, final Map<String, String> toMap, final String deprecatedName, final String newName, final String newUsage) {
        if (fromMap.containsKey(deprecatedName)) {
            Utils.LOG.warn("***** JDBC param deprecation *****");
            Utils.LOG.warn("The use of " + deprecatedName + " is deprecated.");
            Utils.LOG.warn("Please use " + newName + " like so: " + newUsage);
            final String paramValue = fromMap.remove(deprecatedName);
            toMap.put(newName, paramValue);
        }
    }
    
    private static String getAuthorities(final String uri, final JdbcConnectionParams connParams) throws JdbcUriParseException {
        final int fromIndex = "jdbc:hive2://".length();
        int toIndex = -1;
        final ArrayList<String> toIndexChars = new ArrayList<String>(Arrays.asList("/", "?", "#"));
        for (final String toIndexChar : toIndexChars) {
            toIndex = uri.indexOf(toIndexChar, fromIndex);
            if (toIndex > 0) {
                break;
            }
        }
        String authorities;
        if (toIndex < 0) {
            authorities = uri.substring(fromIndex);
        }
        else {
            authorities = uri.substring(fromIndex, toIndex);
        }
        return authorities;
    }
    
    private static String resolveAuthority(final JdbcConnectionParams connParams) throws JdbcUriParseException, ZooKeeperHiveClientException {
        final String serviceDiscoveryMode = connParams.getSessionVars().get("serviceDiscoveryMode");
        if (serviceDiscoveryMode != null && "zooKeeper".equalsIgnoreCase(serviceDiscoveryMode)) {
            return resolveAuthorityUsingZooKeeper(connParams);
        }
        final String authority = connParams.getAuthorityList()[0];
        final URI jdbcURI = URI.create("hive2://" + authority);
        if (jdbcURI.getAuthority() != null && jdbcURI.getHost() == null) {
            throw new JdbcUriParseException("Bad URL format. Hostname not found  in authority part of the url: " + jdbcURI.getAuthority() + ". Are you missing a '/' after the hostname ?");
        }
        return jdbcURI.getAuthority();
    }
    
    private static String resolveAuthorityUsingZooKeeper(final JdbcConnectionParams connParams) throws ZooKeeperHiveClientException {
        connParams.setZooKeeperEnsemble(joinStringArray(connParams.getAuthorityList(), ","));
        return ZooKeeperHiveClientHelper.getNextServerUriFromZooKeeper(connParams);
    }
    
    static void updateConnParamsFromZooKeeper(final JdbcConnectionParams connParams) throws ZooKeeperHiveClientException {
        connParams.getRejectedHostZnodePaths().add(connParams.getCurrentHostZnodePath());
        final String serverUriString = ZooKeeperHiveClientHelper.getNextServerUriFromZooKeeper(connParams);
        URI serverUri = null;
        try {
            serverUri = new URI(null, serverUriString, null, null, null);
        }
        catch (URISyntaxException e) {
            throw new ZooKeeperHiveClientException(e);
        }
        final String oldServerHost = connParams.getHost();
        final int oldServerPort = connParams.getPort();
        final String newServerHost = serverUri.getHost();
        final int newServerPort = serverUri.getPort();
        connParams.setHost(newServerHost);
        connParams.setPort(newServerPort);
        connParams.setJdbcUriString(connParams.getJdbcUriString().replace(oldServerHost + ":" + oldServerPort, newServerHost + ":" + newServerPort));
    }
    
    private static String joinStringArray(final String[] stringArray, final String seperator) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int cur = 0, end = stringArray.length; cur < end; ++cur) {
            if (cur > 0) {
                stringBuilder.append(seperator);
            }
            stringBuilder.append(stringArray[cur]);
        }
        return stringBuilder.toString();
    }
    
    static int getVersionPart(final String fullVersion, final int position) {
        int version = -1;
        try {
            final String[] tokens = fullVersion.split("[\\.-]");
            if (tokens != null && tokens.length > 1 && tokens[position] != null) {
                version = Integer.parseInt(tokens[position]);
            }
        }
        catch (Exception e) {
            version = -1;
        }
        return version;
    }
    
    static boolean needToSendCredentials(final CookieStore cookieStore, final String cookieName, final boolean isSSL) {
        if (cookieName == null || cookieStore == null) {
            return true;
        }
        final List<Cookie> cookies = cookieStore.getCookies();
        for (final Cookie c : cookies) {
            if (c.isSecure() && !isSSL) {
                continue;
            }
            if (c.getName().equals(cookieName)) {
                return false;
            }
        }
        return true;
    }
    
    static {
        LOG = LogFactory.getLog(Utils.class.getName());
    }
    
    public static class JdbcConnectionParams
    {
        static final String AUTH_TYPE = "auth";
        static final String AUTH_QOP_DEPRECATED = "sasl.qop";
        static final String AUTH_QOP = "saslQop";
        static final String AUTH_SIMPLE = "noSasl";
        static final String AUTH_TOKEN = "delegationToken";
        static final String AUTH_USER = "user";
        static final String AUTH_PRINCIPAL = "principal";
        static final String AUTH_PASSWD = "password";
        static final String AUTH_KERBEROS_AUTH_TYPE = "kerberosAuthType";
        static final String AUTH_KERBEROS_AUTH_TYPE_FROM_SUBJECT = "fromSubject";
        static final String ANONYMOUS_USER = "anonymous";
        static final String ANONYMOUS_PASSWD = "anonymous";
        static final String USE_SSL = "ssl";
        static final String SSL_TRUST_STORE = "sslTrustStore";
        static final String SSL_TRUST_STORE_PASSWORD = "trustStorePassword";
        static final String TRANSPORT_MODE_DEPRECATED = "hive.server2.transport.mode";
        static final String TRANSPORT_MODE = "transportMode";
        static final String HTTP_PATH_DEPRECATED = "hive.server2.thrift.http.path";
        static final String HTTP_PATH = "httpPath";
        static final String SERVICE_DISCOVERY_MODE = "serviceDiscoveryMode";
        static final String SERVICE_DISCOVERY_MODE_NONE = "none";
        static final String SERVICE_DISCOVERY_MODE_ZOOKEEPER = "zooKeeper";
        static final String ZOOKEEPER_NAMESPACE = "zooKeeperNamespace";
        static final String ZOOKEEPER_DEFAULT_NAMESPACE = "hiveserver2";
        static final String COOKIE_AUTH = "cookieAuth";
        static final String COOKIE_AUTH_FALSE = "false";
        static final String COOKIE_NAME = "cookieName";
        static final String DEFAULT_COOKIE_NAMES_HS2 = "hive.server2.auth";
        static final String HTTP_HEADER_PREFIX = "http.header.";
        static final String USE_TWO_WAY_SSL = "twoWay";
        static final String TRUE = "true";
        static final String SSL_KEY_STORE = "sslKeyStore";
        static final String SSL_KEY_STORE_PASSWORD = "keyStorePassword";
        static final String SSL_KEY_STORE_TYPE = "JKS";
        static final String SUNX509_ALGORITHM_STRING = "SunX509";
        static final String SUNJSSE_ALGORITHM_STRING = "SunJSSE";
        static final String SSL_TRUST_STORE_TYPE = "JKS";
        private String host;
        private int port;
        private String jdbcUriString;
        private String dbName;
        private Map<String, String> hiveConfs;
        private Map<String, String> hiveVars;
        private Map<String, String> sessionVars;
        private boolean isEmbeddedMode;
        private String[] authorityList;
        private String zooKeeperEnsemble;
        private String currentHostZnodePath;
        private List<String> rejectedHostZnodePaths;
        
        public JdbcConnectionParams() {
            this.host = null;
            this.dbName = "default";
            this.hiveConfs = new LinkedHashMap<String, String>();
            this.hiveVars = new LinkedHashMap<String, String>();
            this.sessionVars = new LinkedHashMap<String, String>();
            this.isEmbeddedMode = false;
            this.zooKeeperEnsemble = null;
            this.rejectedHostZnodePaths = new ArrayList<String>();
        }
        
        public String getHost() {
            return this.host;
        }
        
        public int getPort() {
            return this.port;
        }
        
        public String getJdbcUriString() {
            return this.jdbcUriString;
        }
        
        public String getDbName() {
            return this.dbName;
        }
        
        public Map<String, String> getHiveConfs() {
            return this.hiveConfs;
        }
        
        public Map<String, String> getHiveVars() {
            return this.hiveVars;
        }
        
        public boolean isEmbeddedMode() {
            return this.isEmbeddedMode;
        }
        
        public Map<String, String> getSessionVars() {
            return this.sessionVars;
        }
        
        public String[] getAuthorityList() {
            return this.authorityList;
        }
        
        public String getZooKeeperEnsemble() {
            return this.zooKeeperEnsemble;
        }
        
        public List<String> getRejectedHostZnodePaths() {
            return this.rejectedHostZnodePaths;
        }
        
        public String getCurrentHostZnodePath() {
            return this.currentHostZnodePath;
        }
        
        public void setHost(final String host) {
            this.host = host;
        }
        
        public void setPort(final int port) {
            this.port = port;
        }
        
        public void setJdbcUriString(final String jdbcUriString) {
            this.jdbcUriString = jdbcUriString;
        }
        
        public void setDbName(final String dbName) {
            this.dbName = dbName;
        }
        
        public void setHiveConfs(final Map<String, String> hiveConfs) {
            this.hiveConfs = hiveConfs;
        }
        
        public void setHiveVars(final Map<String, String> hiveVars) {
            this.hiveVars = hiveVars;
        }
        
        public void setEmbeddedMode(final boolean embeddedMode) {
            this.isEmbeddedMode = embeddedMode;
        }
        
        public void setSessionVars(final Map<String, String> sessionVars) {
            this.sessionVars = sessionVars;
        }
        
        public void setSuppliedAuthorityList(final String[] authorityList) {
            this.authorityList = authorityList;
        }
        
        public void setZooKeeperEnsemble(final String zooKeeperEnsemble) {
            this.zooKeeperEnsemble = zooKeeperEnsemble;
        }
        
        public void setCurrentHostZnodePath(final String currentHostZnodePath) {
            this.currentHostZnodePath = currentHostZnodePath;
        }
    }
}
