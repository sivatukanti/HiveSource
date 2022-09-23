// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class NoJspServlet extends HttpServlet
{
    private boolean _warned;
    
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse response) throws ServletException, IOException {
        if (!this._warned) {
            this.getServletContext().log("No JSP support.  Check that JSP jars are in lib/jsp and that the JSP option has been specified to start.jar");
        }
        this._warned = true;
        response.sendError(500, "JSP support not configured");
    }
}
