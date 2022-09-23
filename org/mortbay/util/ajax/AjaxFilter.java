// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util.ajax;

import java.io.IOException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.Filter;

public class AjaxFilter implements Filter
{
    ServletContext context;
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.context = filterConfig.getServletContext();
    }
    
    public ServletContext getContext() {
        return this.context;
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final String[] method = request.getParameterValues("ajax");
        final String[] message = request.getParameterValues("message");
        if (method != null && method.length > 0) {
            final HttpServletRequest srequest = (HttpServletRequest)request;
            final HttpServletResponse sresponse = (HttpServletResponse)response;
            final StringWriter sout = new StringWriter();
            final PrintWriter out = new PrintWriter(sout);
            out.println("<ajax-response>");
            final AjaxResponse aResponse = new AjaxResponse(srequest, out);
            for (int i = 0; i < method.length; ++i) {
                this.handle(method[i], message[i], srequest, aResponse);
            }
            out.println("</ajax-response>");
            final byte[] ajax = sout.toString().getBytes("UTF-8");
            sresponse.setHeader("Pragma", "no-cache");
            sresponse.addHeader("Cache-Control", "must-revalidate,no-cache,no-store");
            sresponse.setDateHeader("Expires", 0L);
            sresponse.setContentType("text/xml; charset=UTF-8");
            sresponse.setContentLength(ajax.length);
            sresponse.getOutputStream().write(ajax);
            sresponse.flushBuffer();
        }
        else {
            chain.doFilter(request, response);
        }
    }
    
    public void handle(final String method, final String message, final HttpServletRequest request, final AjaxResponse response) {
        response.elementResponse(null, "<span class=\"error\">No implementation for " + method + " " + request.getParameter("member") + "</span>");
    }
    
    public void destroy() {
        this.context = null;
    }
    
    public static String encodeText(final String s) {
        StringBuffer buf = null;
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            String r = null;
            switch (c) {
                case '<': {
                    r = "&lt;";
                    break;
                }
                case '>': {
                    r = "&gt;";
                    break;
                }
                case '&': {
                    r = "&amp;";
                    break;
                }
            }
            if (r != null) {
                if (buf == null) {
                    buf = new StringBuffer(s.length() * 2);
                    buf.append(s.subSequence(0, i));
                }
                buf.append(r);
            }
            else if (buf != null) {
                buf.append(c);
            }
        }
        if (buf != null) {
            return buf.toString();
        }
        return s;
    }
    
    public static class AjaxResponse
    {
        private HttpServletRequest request;
        private PrintWriter out;
        
        private AjaxResponse(final HttpServletRequest request, final PrintWriter out) {
            this.out = out;
            this.request = request;
        }
        
        public void elementResponse(String id, final String element) {
            if (id == null) {
                id = this.request.getParameter("id");
            }
            if (id == null) {
                id = "unknown";
            }
            this.out.println("<response type=\"element\" id=\"" + id + "\">" + element + "</response>");
        }
        
        public void objectResponse(String id, final String element) {
            if (id == null) {
                id = this.request.getParameter("id");
            }
            if (id == null) {
                id = "unknown";
            }
            this.out.println("<response type=\"object\" id=\"" + id + "\">" + element + "</response>");
        }
    }
}
