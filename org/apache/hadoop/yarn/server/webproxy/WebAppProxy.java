// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webproxy;

import org.apache.commons.logging.LogFactory;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import java.net.URI;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.http.HttpServer2;
import org.apache.commons.logging.Log;
import org.apache.hadoop.service.AbstractService;

public class WebAppProxy extends AbstractService
{
    public static final String FETCHER_ATTRIBUTE = "AppUrlFetcher";
    public static final String IS_SECURITY_ENABLED_ATTRIBUTE = "IsSecurityEnabled";
    public static final String PROXY_HOST_ATTRIBUTE = "proxyHost";
    private static final Log LOG;
    private HttpServer2 proxyServer;
    private String bindAddress;
    private int port;
    private AccessControlList acl;
    private AppReportFetcher fetcher;
    private boolean isSecurityEnabled;
    private String proxyHost;
    
    public WebAppProxy() {
        super(WebAppProxy.class.getName());
        this.proxyServer = null;
        this.bindAddress = null;
        this.port = 0;
        this.acl = null;
        this.fetcher = null;
        this.isSecurityEnabled = false;
        this.proxyHost = null;
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        final String auth = conf.get("hadoop.security.authentication");
        if (auth == null || "simple".equals(auth)) {
            this.isSecurityEnabled = false;
        }
        else if ("kerberos".equals(auth)) {
            this.isSecurityEnabled = true;
        }
        else {
            WebAppProxy.LOG.warn("Unrecongized attribute value for hadoop.security.authentication of " + auth);
        }
        final String proxy = WebAppUtils.getProxyHostAndPort(conf);
        final String[] proxyParts = proxy.split(":");
        this.proxyHost = proxyParts[0];
        this.fetcher = new AppReportFetcher(conf);
        this.bindAddress = conf.get("yarn.web-proxy.address");
        if (this.bindAddress == null || this.bindAddress.isEmpty()) {
            throw new YarnRuntimeException("yarn.web-proxy.address is not set so the proxy will not run.");
        }
        WebAppProxy.LOG.info("Instantiating Proxy at " + this.bindAddress);
        final String[] parts = StringUtils.split(this.bindAddress, ':');
        this.port = 0;
        if (parts.length == 2) {
            this.bindAddress = parts[0];
            this.port = Integer.parseInt(parts[1]);
        }
        this.acl = new AccessControlList(conf.get("yarn.admin.acl", "*"));
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        try {
            final Configuration conf = this.getConfig();
            final HttpServer2.Builder b = new HttpServer2.Builder().setName("proxy").addEndpoint(URI.create(WebAppUtils.getHttpSchemePrefix(conf) + this.bindAddress + ":" + this.port)).setFindPort(this.port == 0).setConf(this.getConfig()).setACL(this.acl);
            if (YarnConfiguration.useHttps(conf)) {
                WebAppUtils.loadSslConfiguration(b);
            }
            (this.proxyServer = b.build()).addServlet("proxy", "/proxy/*", WebAppProxyServlet.class);
            this.proxyServer.setAttribute("AppUrlFetcher", this.fetcher);
            this.proxyServer.setAttribute("IsSecurityEnabled", this.isSecurityEnabled);
            this.proxyServer.setAttribute("proxyHost", this.proxyHost);
            this.proxyServer.start();
        }
        catch (IOException e) {
            WebAppProxy.LOG.fatal("Could not start proxy web server", e);
            throw new YarnRuntimeException("Could not start proxy web server", e);
        }
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        if (this.proxyServer != null) {
            try {
                this.proxyServer.stop();
            }
            catch (Exception e) {
                WebAppProxy.LOG.fatal("Error stopping proxy web server", e);
                throw new YarnRuntimeException("Error stopping proxy web server", e);
            }
        }
        if (this.fetcher != null) {
            this.fetcher.stop();
        }
        super.serviceStop();
    }
    
    public void join() {
        if (this.proxyServer != null) {
            try {
                this.proxyServer.join();
            }
            catch (InterruptedException ex) {}
        }
    }
    
    @VisibleForTesting
    String getBindAddress() {
        return this.bindAddress + ":" + this.port;
    }
    
    static {
        LOG = LogFactory.getLog(WebAppProxy.class);
    }
}
