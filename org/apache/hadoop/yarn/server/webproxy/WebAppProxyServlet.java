// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webproxy;

import java.util.EnumSet;
import org.apache.hadoop.yarn.webapp.hamlet.HamletImpl;
import java.io.PrintWriter;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;
import java.util.Collection;
import java.util.Arrays;
import org.apache.commons.logging.LogFactory;
import java.net.URISyntaxException;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.yarn.util.Apps;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import java.io.InputStream;
import org.apache.commons.httpclient.Header;
import java.io.OutputStream;
import java.util.Enumeration;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.hadoop.io.IOUtils;
import java.net.URLEncoder;
import org.apache.commons.httpclient.methods.GetMethod;
import java.net.InetAddress;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;
import javax.servlet.http.Cookie;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.TrackingUriPlugin;
import java.util.List;
import java.util.HashSet;
import org.apache.commons.logging.Log;
import javax.servlet.http.HttpServlet;

public class WebAppProxyServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static final Log LOG;
    private static final HashSet<String> passThroughHeaders;
    public static final String PROXY_USER_COOKIE_NAME = "proxy-user";
    private final List<TrackingUriPlugin> trackingUriPlugins;
    private final String rmAppPageUrlBase;
    private final transient YarnConfiguration conf;
    
    public WebAppProxyServlet() {
        this.conf = new YarnConfiguration();
        this.trackingUriPlugins = this.conf.getInstances("yarn.tracking.url.generator", TrackingUriPlugin.class);
        this.rmAppPageUrlBase = StringHelper.pjoin(WebAppUtils.getResolvedRMWebAppURLWithScheme(this.conf), "cluster", "app");
    }
    
    private static void notFound(final HttpServletResponse resp, final String message) throws IOException {
        resp.setStatus(404);
        resp.setContentType("text/html; charset=UTF-8");
        final Page p = new Page(resp.getWriter());
        p.html().h1(message)._();
    }
    
    private static void warnUserPage(final HttpServletResponse resp, final String link, final String user, final ApplicationId id) throws IOException {
        resp.addCookie(makeCheckCookie(id, false));
        resp.setContentType("text/html; charset=UTF-8");
        final Page p = new Page(resp.getWriter());
        p.html().h1("WARNING: The following page may not be safe!").h3()._("click ").a(link, "here")._(" to continue to an Application Master web interface owned by ", user)._()._();
    }
    
    private static void proxyLink(final HttpServletRequest req, final HttpServletResponse resp, final URI link, final Cookie c, final String proxyHost) throws IOException {
        final org.apache.commons.httpclient.URI uri = new org.apache.commons.httpclient.URI(link.toString(), false);
        final HttpClientParams params = new HttpClientParams();
        params.setCookiePolicy("compatibility");
        params.setBooleanParameter("http.protocol.allow-circular-redirects", true);
        final HttpClient client = new HttpClient(params);
        final HostConfiguration config = new HostConfiguration();
        final InetAddress localAddress = InetAddress.getByName(proxyHost);
        if (WebAppProxyServlet.LOG.isDebugEnabled()) {
            WebAppProxyServlet.LOG.debug("local InetAddress for proxy host: " + localAddress.toString());
        }
        config.setLocalAddress(localAddress);
        final HttpMethod method = new GetMethod(uri.getEscapedURI());
        final Enumeration<String> names = req.getHeaderNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            if (WebAppProxyServlet.passThroughHeaders.contains(name)) {
                final String value = req.getHeader(name);
                WebAppProxyServlet.LOG.debug("REQ HEADER: " + name + " : " + value);
                method.setRequestHeader(name, value);
            }
        }
        final String user = req.getRemoteUser();
        if (user != null && !user.isEmpty()) {
            method.setRequestHeader("Cookie", "proxy-user=" + URLEncoder.encode(user, "ASCII"));
        }
        final OutputStream out = resp.getOutputStream();
        try {
            resp.setStatus(client.executeMethod(config, method));
            for (final Header header : method.getResponseHeaders()) {
                resp.setHeader(header.getName(), header.getValue());
            }
            if (c != null) {
                resp.addCookie(c);
            }
            final InputStream in = method.getResponseBodyAsStream();
            if (in != null) {
                IOUtils.copyBytes(in, out, 4096, true);
            }
        }
        finally {
            method.releaseConnection();
        }
    }
    
    private static String getCheckCookieName(final ApplicationId id) {
        return "checked_" + id;
    }
    
    private static Cookie makeCheckCookie(final ApplicationId id, final boolean isSet) {
        final Cookie c = new Cookie(getCheckCookieName(id), String.valueOf(isSet));
        c.setPath(ProxyUriUtils.getPath(id));
        c.setMaxAge(7200);
        return c;
    }
    
    private boolean isSecurityEnabled() {
        final Boolean b = (Boolean)this.getServletContext().getAttribute("IsSecurityEnabled");
        return b != null && b;
    }
    
    private ApplicationReport getApplicationReport(final ApplicationId id) throws IOException, YarnException {
        return ((AppReportFetcher)this.getServletContext().getAttribute("AppUrlFetcher")).getApplicationReport(id);
    }
    
    private String getProxyHost() throws IOException {
        return (String)this.getServletContext().getAttribute("proxyHost");
    }
    
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        try {
            final String userApprovedParamS = req.getParameter("proxyapproved");
            boolean userWasWarned = false;
            boolean userApproved = userApprovedParamS != null && Boolean.valueOf(userApprovedParamS);
            final boolean securityEnabled = this.isSecurityEnabled();
            final String remoteUser = req.getRemoteUser();
            final String pathInfo = req.getPathInfo();
            final String[] parts = pathInfo.split("/", 3);
            if (parts.length < 2) {
                WebAppProxyServlet.LOG.warn(remoteUser + " Gave an invalid proxy path " + pathInfo);
                notFound(resp, "Your path appears to be formatted incorrectly.");
                return;
            }
            final String appId = parts[1];
            final String rest = (parts.length > 2) ? parts[2] : "";
            final ApplicationId id = Apps.toAppID(appId);
            if (id == null) {
                WebAppProxyServlet.LOG.warn(req.getRemoteUser() + " Attempting to access " + appId + " that is invalid");
                notFound(resp, appId + " appears to be formatted incorrectly.");
                return;
            }
            if (securityEnabled) {
                final String cookieName = getCheckCookieName(id);
                final Cookie[] cookies = req.getCookies();
                if (cookies != null) {
                    for (final Cookie c : cookies) {
                        if (cookieName.equals(c.getName())) {
                            userWasWarned = true;
                            userApproved = (userApproved || Boolean.valueOf(c.getValue()));
                            break;
                        }
                    }
                }
            }
            final boolean checkUser = securityEnabled && (!userWasWarned || !userApproved);
            ApplicationReport applicationReport = null;
            try {
                applicationReport = this.getApplicationReport(id);
            }
            catch (ApplicationNotFoundException e3) {
                applicationReport = null;
            }
            if (applicationReport == null) {
                WebAppProxyServlet.LOG.warn(req.getRemoteUser() + " Attempting to access " + id + " that was not found");
                final URI toFetch = ProxyUriUtils.getUriFromTrackingPlugins(id, this.trackingUriPlugins);
                if (toFetch != null) {
                    resp.sendRedirect(resp.encodeRedirectURL(toFetch.toString()));
                    return;
                }
                notFound(resp, "Application " + appId + " could not be found, " + "please try the history server");
            }
            else {
                final String original = applicationReport.getOriginalTrackingUrl();
                URI trackingUri = null;
                if (original == null || original.equals("N/A")) {
                    resp.sendRedirect(resp.encodeRedirectURL(StringHelper.pjoin(this.rmAppPageUrlBase, id.toString())));
                    return;
                }
                if (ProxyUriUtils.getSchemeFromUrl(original).isEmpty()) {
                    trackingUri = ProxyUriUtils.getUriFromAMUrl(WebAppUtils.getHttpSchemePrefix(this.conf), original);
                }
                else {
                    trackingUri = new URI(original);
                }
                final String runningUser = applicationReport.getUser();
                if (checkUser && !runningUser.equals(remoteUser)) {
                    WebAppProxyServlet.LOG.info("Asking " + remoteUser + " if they want to connect to the " + "app master GUI of " + appId + " owned by " + runningUser);
                    warnUserPage(resp, ProxyUriUtils.getPathAndQuery(id, rest, req.getQueryString(), true), runningUser, id);
                    return;
                }
                final URI toFetch2 = new URI(trackingUri.getScheme(), trackingUri.getAuthority(), StringHelper.ujoin(trackingUri.getPath(), rest), req.getQueryString(), null);
                WebAppProxyServlet.LOG.info(req.getRemoteUser() + " is accessing unchecked " + toFetch2 + " which is the app master GUI of " + appId + " owned by " + runningUser);
                switch (applicationReport.getYarnApplicationState()) {
                    case KILLED:
                    case FINISHED:
                    case FAILED: {
                        resp.sendRedirect(resp.encodeRedirectURL(toFetch2.toString()));
                    }
                    default: {
                        Cookie c2 = null;
                        if (userWasWarned && userApproved) {
                            c2 = makeCheckCookie(id, true);
                        }
                        proxyLink(req, resp, toFetch2, c2, this.getProxyHost());
                        break;
                    }
                }
            }
        }
        catch (URISyntaxException e) {
            throw new IOException(e);
        }
        catch (YarnException e2) {
            throw new IOException(e2);
        }
    }
    
    static {
        LOG = LogFactory.getLog(WebAppProxyServlet.class);
        passThroughHeaders = new HashSet<String>(Arrays.asList("User-Agent", "Accept", "Accept-Encoding", "Accept-Language", "Accept-Charset"));
    }
    
    private static class _ implements HamletSpec._
    {
    }
    
    private static class Page extends Hamlet
    {
        Page(final PrintWriter out) {
            super(out, 0, false);
        }
        
        public HTML<WebAppProxyServlet._> html() {
            return new HTML<WebAppProxyServlet._>("html", null, EnumSet.of(EOpt.ENDTAG));
        }
    }
}
