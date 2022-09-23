// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class NoJspServlet extends HttpServlet
{
    protected void doGet(final HttpServletRequest req, final HttpServletResponse response) throws ServletException, IOException {
        response.sendError(500, "JSP support not configured");
    }
}
