// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webproxy.amfilter;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.util.RMHAUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.HashSet;
import javax.servlet.ServletException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import javax.servlet.FilterConfig;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.servlet.Filter;

@InterfaceAudience.Public
public class AmIpFilter implements Filter
{
    private static final Log LOG;
    @Deprecated
    public static final String PROXY_HOST = "PROXY_HOST";
    @Deprecated
    public static final String PROXY_URI_BASE = "PROXY_URI_BASE";
    static final String PROXY_HOSTS = "PROXY_HOSTS";
    static final String PROXY_HOSTS_DELIMITER = ",";
    static final String PROXY_URI_BASES = "PROXY_URI_BASES";
    static final String PROXY_URI_BASES_DELIMITER = ",";
    private static final long updateInterval = 300000L;
    private String[] proxyHosts;
    private Set<String> proxyAddresses;
    private long lastUpdate;
    private Map<String, String> proxyUriBases;
    
    public AmIpFilter() {
        this.proxyAddresses = null;
    }
    
    @Override
    public void init(final FilterConfig conf) throws ServletException {
        if (conf.getInitParameter("PROXY_HOST") != null && conf.getInitParameter("PROXY_URI_BASE") != null) {
            this.proxyHosts = new String[] { conf.getInitParameter("PROXY_HOST") };
            (this.proxyUriBases = new HashMap<String, String>(1)).put("dummy", conf.getInitParameter("PROXY_URI_BASE"));
        }
        else {
            this.proxyHosts = conf.getInitParameter("PROXY_HOSTS").split(",");
            final String[] proxyUriBasesArr = conf.getInitParameter("PROXY_URI_BASES").split(",");
            this.proxyUriBases = new HashMap<String, String>();
            for (final String proxyUriBase : proxyUriBasesArr) {
                try {
                    final URL url = new URL(proxyUriBase);
                    this.proxyUriBases.put(url.getHost() + ":" + url.getPort(), proxyUriBase);
                }
                catch (MalformedURLException e) {
                    AmIpFilter.LOG.warn(proxyUriBase + " does not appear to be a valid URL", e);
                }
            }
        }
    }
    
    protected Set<String> getProxyAddresses() throws ServletException {
        final long now = System.currentTimeMillis();
        synchronized (this) {
            if (this.proxyAddresses == null || this.lastUpdate + 300000L >= now) {
                this.proxyAddresses = new HashSet<String>();
                for (final String proxyHost : this.proxyHosts) {
                    try {
                        for (final InetAddress add : InetAddress.getAllByName(proxyHost)) {
                            if (AmIpFilter.LOG.isDebugEnabled()) {
                                AmIpFilter.LOG.debug("proxy address is: " + add.getHostAddress());
                            }
                            this.proxyAddresses.add(add.getHostAddress());
                        }
                        this.lastUpdate = now;
                    }
                    catch (UnknownHostException e) {
                        AmIpFilter.LOG.warn("Could not locate " + proxyHost + " - skipping", e);
                    }
                }
                if (this.proxyAddresses.isEmpty()) {
                    throw new ServletException("Could not locate any of the proxy hosts");
                }
            }
            return this.proxyAddresses;
        }
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
        if (!(req instanceof HttpServletRequest)) {
            throw new ServletException("This filter only works for HTTP/HTTPS");
        }
        final HttpServletRequest httpReq = (HttpServletRequest)req;
        final HttpServletResponse httpResp = (HttpServletResponse)resp;
        if (AmIpFilter.LOG.isDebugEnabled()) {
            AmIpFilter.LOG.debug("Remote address for request is: " + httpReq.getRemoteAddr());
        }
        if (!this.getProxyAddresses().contains(httpReq.getRemoteAddr())) {
            String redirectUrl = this.findRedirectUrl();
            redirectUrl = httpResp.encodeRedirectURL(redirectUrl + httpReq.getRequestURI());
            httpResp.sendRedirect(redirectUrl);
            return;
        }
        String user = null;
        if (httpReq.getCookies() != null) {
            for (final Cookie c : httpReq.getCookies()) {
                if ("proxy-user".equals(c.getName())) {
                    user = c.getValue();
                    break;
                }
            }
        }
        if (user == null) {
            AmIpFilter.LOG.warn("Could not find proxy-user cookie, so user will not be set");
            chain.doFilter(req, resp);
        }
        else {
            final AmIpPrincipal principal = new AmIpPrincipal(user);
            final ServletRequest requestWrapper = new AmIpServletRequestWrapper(httpReq, principal);
            chain.doFilter(requestWrapper, resp);
        }
    }
    
    protected String findRedirectUrl() throws ServletException {
        String addr;
        if (this.proxyUriBases.size() == 1) {
            addr = this.proxyUriBases.values().iterator().next();
        }
        else {
            final YarnConfiguration conf = new YarnConfiguration();
            final String activeRMId = RMHAUtils.findActiveRMHAId(conf);
            final String addressPropertyPrefix = YarnConfiguration.useHttps(conf) ? "yarn.resourcemanager.webapp.https.address" : "yarn.resourcemanager.webapp.address";
            final String host = conf.get(HAUtil.addSuffix(addressPropertyPrefix, activeRMId));
            addr = this.proxyUriBases.get(host);
        }
        if (addr == null) {
            throw new ServletException("Could not determine the proxy server for redirection");
        }
        return addr;
    }
    
    static {
        LOG = LogFactory.getLog(AmIpFilter.class);
    }
}
