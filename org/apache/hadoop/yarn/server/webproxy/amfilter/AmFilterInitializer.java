// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webproxy.amfilter;

import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.FilterContainer;
import org.apache.hadoop.http.FilterInitializer;

public class AmFilterInitializer extends FilterInitializer
{
    private static final String FILTER_NAME = "AM_PROXY_FILTER";
    private static final String FILTER_CLASS;
    
    @Override
    public void initFilter(final FilterContainer container, final Configuration conf) {
        final Map<String, String> params = new HashMap<String, String>();
        final List<String> proxies = WebAppUtils.getProxyHostsAndPortsForAmFilter(conf);
        StringBuilder sb = new StringBuilder();
        for (final String proxy : proxies) {
            sb.append(proxy.split(":")[0]).append(",");
        }
        sb.setLength(sb.length() - 1);
        params.put("PROXY_HOSTS", sb.toString());
        final String prefix = WebAppUtils.getHttpSchemePrefix(conf);
        final String proxyBase = this.getApplicationWebProxyBase();
        sb = new StringBuilder();
        for (final String proxy2 : proxies) {
            sb.append(prefix).append(proxy2).append(proxyBase).append(",");
        }
        sb.setLength(sb.length() - 1);
        params.put("PROXY_URI_BASES", sb.toString());
        container.addFilter("AM_PROXY_FILTER", AmFilterInitializer.FILTER_CLASS, params);
    }
    
    @VisibleForTesting
    protected String getApplicationWebProxyBase() {
        return System.getenv("APPLICATION_WEB_PROXY_BASE");
    }
    
    static {
        FILTER_CLASS = AmIpFilter.class.getCanonicalName();
    }
}
