// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.http;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public abstract class IsActiveServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    public static final String SERVLET_NAME = "isActive";
    public static final String PATH_SPEC = "/isActive";
    public static final String RESPONSE_ACTIVE = "I am Active!";
    public static final String RESPONSE_NOT_ACTIVE = "I am not Active!";
    
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        resp.addHeader("Connection", "close");
        if (!this.isActive()) {
            resp.sendError(405, "I am not Active!");
            return;
        }
        resp.setStatus(200);
        resp.getWriter().write("I am Active!");
        resp.getWriter().flush();
    }
    
    protected abstract boolean isActive();
}
