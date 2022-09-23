// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import com.google.common.collect.Sets;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.hadoop.http.HtmlQuoting;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.inject.Inject;
import java.util.Set;
import com.google.inject.Injector;
import javax.inject.Singleton;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

@Singleton
public class RMWebAppFilter extends GuiceContainer
{
    private Injector injector;
    private static final long serialVersionUID = 1L;
    private static final Set<String> NON_REDIRECTED_URIS;
    
    @Inject
    public RMWebAppFilter(final Injector injector) {
        super(injector);
        this.injector = injector;
    }
    
    @Override
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        String uri = HtmlQuoting.quoteHtmlChars(request.getRequestURI());
        if (uri == null) {
            uri = "/";
        }
        final RMWebApp rmWebApp = this.injector.getInstance(RMWebApp.class);
        rmWebApp.checkIfStandbyRM();
        if (rmWebApp.isStandby() && this.shouldRedirect(rmWebApp, uri)) {
            final String redirectPath = rmWebApp.getRedirectPath() + uri;
            if (redirectPath != null && !redirectPath.isEmpty()) {
                final String redirectMsg = "This is standby RM. Redirecting to the current active RM: " + redirectPath;
                response.addHeader("Refresh", "3; url=" + redirectPath);
                final PrintWriter out = response.getWriter();
                out.println(redirectMsg);
                return;
            }
        }
        super.doFilter(request, response, chain);
    }
    
    private boolean shouldRedirect(final RMWebApp rmWebApp, final String uri) {
        return !uri.equals("/" + rmWebApp.wsName() + "/v1/cluster/info") && !uri.equals("/" + rmWebApp.name() + "/cluster") && !RMWebAppFilter.NON_REDIRECTED_URIS.contains(uri);
    }
    
    static {
        NON_REDIRECTED_URIS = Sets.newHashSet("/conf", "/stacks", "/logLevel", "/logs");
    }
}
