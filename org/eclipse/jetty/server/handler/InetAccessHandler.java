// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.util.log.Log;
import java.util.Collection;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.HttpChannel;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.InetAddressSet;
import java.net.InetAddress;
import org.eclipse.jetty.util.IncludeExcludeSet;
import org.eclipse.jetty.util.log.Logger;

public class InetAccessHandler extends HandlerWrapper
{
    private static final Logger LOG;
    private final IncludeExcludeSet<String, InetAddress> _set;
    
    public InetAccessHandler() {
        this._set = new IncludeExcludeSet<String, InetAddress>((Class<SET>)InetAddressSet.class);
    }
    
    public void include(final String pattern) {
        this._set.include(pattern);
    }
    
    public void include(final String... patterns) {
        this._set.include(patterns);
    }
    
    public void exclude(final String pattern) {
        this._set.exclude(pattern);
    }
    
    public void exclude(final String... patterns) {
        this._set.exclude(patterns);
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final HttpChannel channel = baseRequest.getHttpChannel();
        if (channel != null) {
            final EndPoint endp = channel.getEndPoint();
            if (endp != null) {
                final InetSocketAddress address = endp.getRemoteAddress();
                if (address != null && !this.isAllowed(address.getAddress(), request)) {
                    response.sendError(403);
                    baseRequest.setHandled(true);
                    return;
                }
            }
        }
        this.getHandler().handle(target, baseRequest, request, response);
    }
    
    protected boolean isAllowed(final InetAddress address, final HttpServletRequest request) {
        return this.isAllowed(address);
    }
    
    @Deprecated
    protected boolean isAllowed(final InetAddress address) {
        final boolean allowed = this._set.test(address);
        if (InetAccessHandler.LOG.isDebugEnabled()) {
            InetAccessHandler.LOG.debug("{} {} {}", this, allowed ? "allowed" : "denied", address);
        }
        return allowed;
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        this.dumpBeans(out, indent, this._set.getIncluded(), this._set.getExcluded());
    }
    
    static {
        LOG = Log.getLogger(InetAccessHandler.class);
    }
}
