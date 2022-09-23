// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import com.google.common.base.Joiner;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class DefaultPage extends TextPage
{
    static final Joiner valJoiner;
    
    @Override
    public void render() {
        this.puts("Request URI: ", this.request().getRequestURI());
        this.puts("Query parameters:");
        final Map<String, String[]> params = this.request().getParameterMap();
        for (final Map.Entry<String, String[]> e : params.entrySet()) {
            this.puts("  ", e.getKey(), "=", DefaultPage.valJoiner.join(e.getValue()));
        }
        this.puts("More parameters:");
        for (final Map.Entry<String, String> e2 : this.moreParams().entrySet()) {
            this.puts("  ", e2.getKey(), "=", e2.getValue());
        }
        this.puts("Path info: ", this.request().getPathInfo());
        this.puts("Path translated: ", this.request().getPathTranslated());
        this.puts("Auth type: ", this.request().getAuthType());
        this.puts("Remote address: " + this.request().getRemoteAddr());
        this.puts("Remote user: ", this.request().getRemoteUser());
        this.puts("Servlet attributes:");
        final Enumeration<String> attrNames = this.request().getAttributeNames();
        while (attrNames.hasMoreElements()) {
            final String key = attrNames.nextElement();
            this.puts("  ", key, "=", this.request().getAttribute(key));
        }
        this.puts("Headers:");
        final Enumeration<String> headerNames = this.request().getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String key2 = headerNames.nextElement();
            this.puts("  ", key2, "=", this.request().getHeader(key2));
        }
    }
    
    static {
        valJoiner = Joiner.on(", ");
    }
}
