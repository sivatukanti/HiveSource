// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlets;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.util.log.Logger;

public class CloseableDoSFilter extends DoSFilter
{
    private static final Logger LOG;
    
    @Override
    protected void closeConnection(final HttpServletRequest request, final HttpServletResponse response, final Thread thread) {
        try {
            final Request base_request = (Request)((request instanceof Request) ? request : AbstractHttpConnection.getCurrentConnection().getRequest());
            base_request.getConnection().getEndPoint().close();
        }
        catch (IOException e) {
            CloseableDoSFilter.LOG.warn(e);
        }
    }
    
    static {
        LOG = Log.getLogger(CloseableDoSFilter.class);
    }
}
