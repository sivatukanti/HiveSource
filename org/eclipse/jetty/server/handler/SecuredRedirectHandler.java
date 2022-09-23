// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import javax.servlet.ServletException;
import java.io.IOException;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.util.URIUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;

public class SecuredRedirectHandler extends AbstractHandler
{
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final HttpChannel channel = baseRequest.getHttpChannel();
        if (baseRequest.isSecure() || channel == null) {
            return;
        }
        final HttpConfiguration httpConfig = channel.getHttpConfiguration();
        if (httpConfig == null) {
            response.sendError(403, "No http configuration available");
            return;
        }
        if (httpConfig.getSecurePort() > 0) {
            final String scheme = httpConfig.getSecureScheme();
            final int port = httpConfig.getSecurePort();
            final String url = URIUtil.newURI(scheme, baseRequest.getServerName(), port, baseRequest.getRequestURI(), baseRequest.getQueryString());
            response.setContentLength(0);
            response.sendRedirect(url);
        }
        else {
            response.sendError(403, "Not Secure");
        }
        baseRequest.setHandled(true);
    }
}
