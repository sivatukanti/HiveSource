// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.util;

import java.io.IOException;
import org.apache.hadoop.http.HttpServer2;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.hadoop.http.HttpConfig;
import java.net.InetSocketAddress;
import java.util.Iterator;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.util.RMHAUtils;
import org.apache.hadoop.yarn.conf.HAUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class WebAppUtils
{
    public static final String WEB_APP_TRUSTSTORE_PASSWORD_KEY = "ssl.server.truststore.password";
    public static final String WEB_APP_KEYSTORE_PASSWORD_KEY = "ssl.server.keystore.password";
    public static final String WEB_APP_KEY_PASSWORD_KEY = "ssl.server.keystore.keypassword";
    public static final String HTTPS_PREFIX = "https://";
    public static final String HTTP_PREFIX = "http://";
    
    public static void setRMWebAppPort(final Configuration conf, final int port) {
        String hostname = getRMWebAppURLWithoutScheme(conf);
        hostname = (hostname.contains(":") ? hostname.substring(0, hostname.indexOf(":")) : hostname);
        setRMWebAppHostnameAndPort(conf, hostname, port);
    }
    
    public static void setRMWebAppHostnameAndPort(final Configuration conf, final String hostname, final int port) {
        final String resolvedAddress = hostname + ":" + port;
        if (YarnConfiguration.useHttps(conf)) {
            conf.set("yarn.resourcemanager.webapp.https.address", resolvedAddress);
        }
        else {
            conf.set("yarn.resourcemanager.webapp.address", resolvedAddress);
        }
    }
    
    public static void setNMWebAppHostNameAndPort(final Configuration conf, final String hostName, final int port) {
        if (YarnConfiguration.useHttps(conf)) {
            conf.set("yarn.nodemanager.webapp.https.address", hostName + ":" + port);
        }
        else {
            conf.set("yarn.nodemanager.webapp.address", hostName + ":" + port);
        }
    }
    
    public static String getRMWebAppURLWithScheme(final Configuration conf) {
        return getHttpSchemePrefix(conf) + getRMWebAppURLWithoutScheme(conf);
    }
    
    public static String getRMWebAppURLWithoutScheme(final Configuration conf) {
        if (YarnConfiguration.useHttps(conf)) {
            return conf.get("yarn.resourcemanager.webapp.https.address", "0.0.0.0:8090");
        }
        return conf.get("yarn.resourcemanager.webapp.address", "0.0.0.0:8088");
    }
    
    public static List<String> getProxyHostsAndPortsForAmFilter(final Configuration conf) {
        final List<String> addrs = new ArrayList<String>();
        final String proxyAddr = conf.get("yarn.web-proxy.address");
        if (proxyAddr == null || proxyAddr.isEmpty()) {
            if (HAUtil.isHAEnabled(conf)) {
                final List<String> haAddrs = RMHAUtils.getRMHAWebappAddresses(new YarnConfiguration(conf));
                for (final String addr : haAddrs) {
                    try {
                        final InetSocketAddress socketAddr = NetUtils.createSocketAddr(addr);
                        addrs.add(getResolvedAddress(socketAddr));
                    }
                    catch (IllegalArgumentException ex) {}
                }
            }
            if (addrs.isEmpty()) {
                addrs.add(getResolvedRMWebAppURLWithoutScheme(conf));
            }
        }
        else {
            addrs.add(proxyAddr);
        }
        return addrs;
    }
    
    public static String getProxyHostAndPort(final Configuration conf) {
        String addr = conf.get("yarn.web-proxy.address");
        if (addr == null || addr.isEmpty()) {
            addr = getResolvedRMWebAppURLWithoutScheme(conf);
        }
        return addr;
    }
    
    public static String getResolvedRMWebAppURLWithScheme(final Configuration conf) {
        return getHttpSchemePrefix(conf) + getResolvedRMWebAppURLWithoutScheme(conf);
    }
    
    public static String getResolvedRMWebAppURLWithoutScheme(final Configuration conf) {
        return getResolvedRMWebAppURLWithoutScheme(conf, YarnConfiguration.useHttps(conf) ? HttpConfig.Policy.HTTPS_ONLY : HttpConfig.Policy.HTTP_ONLY);
    }
    
    public static String getResolvedRMWebAppURLWithoutScheme(final Configuration conf, final HttpConfig.Policy httpPolicy) {
        InetSocketAddress address = null;
        if (httpPolicy == HttpConfig.Policy.HTTPS_ONLY) {
            address = conf.getSocketAddr("yarn.resourcemanager.webapp.https.address", "0.0.0.0:8090", 8090);
        }
        else {
            address = conf.getSocketAddr("yarn.resourcemanager.webapp.address", "0.0.0.0:8088", 8088);
        }
        return getResolvedAddress(address);
    }
    
    private static String getResolvedAddress(InetSocketAddress address) {
        address = NetUtils.getConnectAddress(address);
        final StringBuilder sb = new StringBuilder();
        final InetAddress resolved = address.getAddress();
        if (resolved == null || resolved.isAnyLocalAddress() || resolved.isLoopbackAddress()) {
            String lh = address.getHostName();
            try {
                lh = InetAddress.getLocalHost().getCanonicalHostName();
            }
            catch (UnknownHostException ex) {}
            sb.append(lh);
        }
        else {
            sb.append(address.getHostName());
        }
        sb.append(":").append(address.getPort());
        return sb.toString();
    }
    
    public static String getWebAppBindURL(final Configuration conf, final String hostProperty, String webAppURLWithoutScheme) {
        final String host = conf.getTrimmed(hostProperty);
        if (host != null && !host.isEmpty()) {
            if (!webAppURLWithoutScheme.contains(":")) {
                throw new YarnRuntimeException("webAppURLWithoutScheme must include port specification but doesn't: " + webAppURLWithoutScheme);
            }
            webAppURLWithoutScheme = host + ":" + webAppURLWithoutScheme.split(":")[1];
        }
        return webAppURLWithoutScheme;
    }
    
    public static String getNMWebAppURLWithoutScheme(final Configuration conf) {
        if (YarnConfiguration.useHttps(conf)) {
            return conf.get("yarn.nodemanager.webapp.https.address", "0.0.0.0:8044");
        }
        return conf.get("yarn.nodemanager.webapp.address", "0.0.0.0:8042");
    }
    
    public static String getAHSWebAppURLWithoutScheme(final Configuration conf) {
        if (YarnConfiguration.useHttps(conf)) {
            return conf.get("yarn.timeline-service.webapp.https.address", "0.0.0.0:8190");
        }
        return conf.get("yarn.timeline-service.webapp.address", "0.0.0.0:8188");
    }
    
    public static String getURLWithScheme(final String schemePrefix, final String url) {
        if (url.indexOf("://") > 0) {
            return url;
        }
        return schemePrefix + url;
    }
    
    public static String getRunningLogURL(final String nodeHttpAddress, final String containerId, final String user) {
        if (nodeHttpAddress == null || nodeHttpAddress.isEmpty() || containerId == null || containerId.isEmpty() || user == null || user.isEmpty()) {
            return null;
        }
        return StringHelper.PATH_JOINER.join(nodeHttpAddress, "node", "containerlogs", containerId, user);
    }
    
    public static String getAggregatedLogURL(final String serverHttpAddress, final String allocatedNode, final String containerId, final String entity, final String user) {
        if (serverHttpAddress == null || serverHttpAddress.isEmpty() || allocatedNode == null || allocatedNode.isEmpty() || containerId == null || containerId.isEmpty() || entity == null || entity.isEmpty() || user == null || user.isEmpty()) {
            return null;
        }
        return StringHelper.PATH_JOINER.join(serverHttpAddress, "applicationhistory", "logs", allocatedNode, containerId, entity, user);
    }
    
    public static String getHttpSchemePrefix(final Configuration conf) {
        return YarnConfiguration.useHttps(conf) ? "https://" : "http://";
    }
    
    public static HttpServer2.Builder loadSslConfiguration(final HttpServer2.Builder builder) {
        return loadSslConfiguration(builder, null);
    }
    
    public static HttpServer2.Builder loadSslConfiguration(final HttpServer2.Builder builder, Configuration sslConf) {
        if (sslConf == null) {
            sslConf = new Configuration(false);
        }
        final boolean needsClientAuth = false;
        sslConf.addResource("ssl-server.xml");
        return builder.needsClientAuth(needsClientAuth).keyPassword(getPassword(sslConf, "ssl.server.keystore.keypassword")).keyStore(sslConf.get("ssl.server.keystore.location"), getPassword(sslConf, "ssl.server.keystore.password"), sslConf.get("ssl.server.keystore.type", "jks")).trustStore(sslConf.get("ssl.server.truststore.location"), getPassword(sslConf, "ssl.server.truststore.password"), sslConf.get("ssl.server.truststore.type", "jks"));
    }
    
    static String getPassword(final Configuration conf, final String alias) {
        String password = null;
        try {
            final char[] passchars = conf.getPassword(alias);
            if (passchars != null) {
                password = new String(passchars);
            }
        }
        catch (IOException ioe) {
            password = null;
        }
        return password;
    }
}
