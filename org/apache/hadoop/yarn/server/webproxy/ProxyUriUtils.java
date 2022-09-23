// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webproxy;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import org.apache.hadoop.yarn.util.TrackingUriPlugin;
import java.util.List;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.logging.Log;

public class ProxyUriUtils
{
    private static final Log LOG;
    public static final String PROXY_SERVLET_NAME = "proxy";
    public static final String PROXY_BASE = "/proxy/";
    public static final String PROXY_PATH_SPEC = "/proxy/*";
    public static final String PROXY_APPROVAL_PARAM = "proxyapproved";
    
    private static String uriEncode(final Object o) {
        try {
            assert o != null : "o canot be null";
            return URLEncoder.encode(o.toString(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is not supported by this system?", e);
        }
    }
    
    public static String getPath(final ApplicationId id) {
        if (id == null) {
            throw new IllegalArgumentException("Application id cannot be null ");
        }
        return StringHelper.ujoin("/proxy/", uriEncode(id));
    }
    
    public static String getPath(final ApplicationId id, final String path) {
        if (path == null) {
            return getPath(id);
        }
        return StringHelper.ujoin(getPath(id), path);
    }
    
    public static String getPathAndQuery(final ApplicationId id, final String path, final String query, final boolean approved) {
        final StringBuilder newp = new StringBuilder();
        newp.append(getPath(id, path));
        final boolean first = appendQuery(newp, query, true);
        if (approved) {
            appendQuery(newp, "proxyapproved=true", first);
        }
        return newp.toString();
    }
    
    private static boolean appendQuery(final StringBuilder builder, final String query, final boolean first) {
        if (query != null && !query.isEmpty()) {
            if (first && !query.startsWith("?")) {
                builder.append('?');
            }
            if (!first && !query.startsWith("&")) {
                builder.append('&');
            }
            builder.append(query);
            return false;
        }
        return first;
    }
    
    public static URI getProxyUri(final URI originalUri, final URI proxyUri, final ApplicationId id) {
        try {
            final String path = getPath(id, (originalUri == null) ? "/" : originalUri.getPath());
            return new URI(proxyUri.getScheme(), proxyUri.getAuthority(), path, (originalUri == null) ? null : originalUri.getQuery(), (originalUri == null) ? null : originalUri.getFragment());
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("Could not proxify " + originalUri, e);
        }
    }
    
    public static URI getUriFromAMUrl(final String scheme, final String noSchemeUrl) throws URISyntaxException {
        if (getSchemeFromUrl(noSchemeUrl).isEmpty()) {
            return new URI(scheme + noSchemeUrl);
        }
        return new URI(noSchemeUrl);
    }
    
    public static URI getUriFromTrackingPlugins(final ApplicationId id, final List<TrackingUriPlugin> trackingUriPlugins) throws URISyntaxException {
        URI toRet = null;
        for (final TrackingUriPlugin plugin : trackingUriPlugins) {
            toRet = plugin.getTrackingUri(id);
            if (toRet != null) {
                return toRet;
            }
        }
        return null;
    }
    
    public static String getSchemeFromUrl(final String url) {
        int index = 0;
        if (url != null) {
            index = url.indexOf("://");
        }
        if (index > 0) {
            return url.substring(0, index);
        }
        return "";
    }
    
    static {
        LOG = LogFactory.getLog(ProxyUriUtils.class);
    }
}
