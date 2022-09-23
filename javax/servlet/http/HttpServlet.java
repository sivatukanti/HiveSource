// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.http;

import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.text.MessageFormat;
import javax.servlet.ServletOutputStream;
import java.util.Enumeration;
import java.lang.reflect.Method;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.ResourceBundle;
import javax.servlet.GenericServlet;

public abstract class HttpServlet extends GenericServlet
{
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";
    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings;
    
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String protocol = req.getProtocol();
        final String msg = HttpServlet.lStrings.getString("http.method_get_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(405, msg);
        }
        else {
            resp.sendError(400, msg);
        }
    }
    
    protected long getLastModified(final HttpServletRequest req) {
        return -1L;
    }
    
    protected void doHead(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final NoBodyResponse response = new NoBodyResponse(resp);
        this.doGet(req, response);
        response.setContentLength();
    }
    
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String protocol = req.getProtocol();
        final String msg = HttpServlet.lStrings.getString("http.method_post_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(405, msg);
        }
        else {
            resp.sendError(400, msg);
        }
    }
    
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String protocol = req.getProtocol();
        final String msg = HttpServlet.lStrings.getString("http.method_put_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(405, msg);
        }
        else {
            resp.sendError(400, msg);
        }
    }
    
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String protocol = req.getProtocol();
        final String msg = HttpServlet.lStrings.getString("http.method_delete_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(405, msg);
        }
        else {
            resp.sendError(400, msg);
        }
    }
    
    private Method[] getAllDeclaredMethods(final Class<? extends HttpServlet> c) {
        Class<?> clazz = c;
        Method[] allMethods = null;
        while (!clazz.equals(HttpServlet.class)) {
            final Method[] thisMethods = clazz.getDeclaredMethods();
            if (allMethods != null && allMethods.length > 0) {
                final Method[] subClassMethods = allMethods;
                allMethods = new Method[thisMethods.length + subClassMethods.length];
                System.arraycopy(thisMethods, 0, allMethods, 0, thisMethods.length);
                System.arraycopy(subClassMethods, 0, allMethods, thisMethods.length, subClassMethods.length);
            }
            else {
                allMethods = thisMethods;
            }
            clazz = clazz.getSuperclass();
        }
        return (allMethods != null) ? allMethods : new Method[0];
    }
    
    protected void doOptions(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final Method[] methods = this.getAllDeclaredMethods(this.getClass());
        boolean ALLOW_GET = false;
        boolean ALLOW_HEAD = false;
        boolean ALLOW_POST = false;
        boolean ALLOW_PUT = false;
        boolean ALLOW_DELETE = false;
        final boolean ALLOW_TRACE = true;
        final boolean ALLOW_OPTIONS = true;
        for (int i = 0; i < methods.length; ++i) {
            final String methodName = methods[i].getName();
            if (methodName.equals("doGet")) {
                ALLOW_GET = true;
                ALLOW_HEAD = true;
            }
            else if (methodName.equals("doPost")) {
                ALLOW_POST = true;
            }
            else if (methodName.equals("doPut")) {
                ALLOW_PUT = true;
            }
            else if (methodName.equals("doDelete")) {
                ALLOW_DELETE = true;
            }
        }
        final StringBuilder allow = new StringBuilder();
        if (ALLOW_GET) {
            allow.append("GET");
        }
        if (ALLOW_HEAD) {
            if (allow.length() > 0) {
                allow.append(", ");
            }
            allow.append("HEAD");
        }
        if (ALLOW_POST) {
            if (allow.length() > 0) {
                allow.append(", ");
            }
            allow.append("POST");
        }
        if (ALLOW_PUT) {
            if (allow.length() > 0) {
                allow.append(", ");
            }
            allow.append("PUT");
        }
        if (ALLOW_DELETE) {
            if (allow.length() > 0) {
                allow.append(", ");
            }
            allow.append("DELETE");
        }
        if (ALLOW_TRACE) {
            if (allow.length() > 0) {
                allow.append(", ");
            }
            allow.append("TRACE");
        }
        if (ALLOW_OPTIONS) {
            if (allow.length() > 0) {
                allow.append(", ");
            }
            allow.append("OPTIONS");
        }
        resp.setHeader("Allow", allow.toString());
    }
    
    protected void doTrace(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String CRLF = "\r\n";
        final StringBuilder buffer = new StringBuilder("TRACE ").append(req.getRequestURI()).append(" ").append(req.getProtocol());
        final Enumeration<String> reqHeaderEnum = req.getHeaderNames();
        while (reqHeaderEnum.hasMoreElements()) {
            final String headerName = reqHeaderEnum.nextElement();
            buffer.append(CRLF).append(headerName).append(": ").append(req.getHeader(headerName));
        }
        buffer.append(CRLF);
        final int responseLength = buffer.length();
        resp.setContentType("message/http");
        resp.setContentLength(responseLength);
        final ServletOutputStream out = resp.getOutputStream();
        out.print(buffer.toString());
    }
    
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String method = req.getMethod();
        if (method.equals("GET")) {
            final long lastModified = this.getLastModified(req);
            if (lastModified == -1L) {
                this.doGet(req, resp);
            }
            else {
                final long ifModifiedSince = req.getDateHeader("If-Modified-Since");
                if (ifModifiedSince < lastModified) {
                    this.maybeSetLastModified(resp, lastModified);
                    this.doGet(req, resp);
                }
                else {
                    resp.setStatus(304);
                }
            }
        }
        else if (method.equals("HEAD")) {
            final long lastModified = this.getLastModified(req);
            this.maybeSetLastModified(resp, lastModified);
            this.doHead(req, resp);
        }
        else if (method.equals("POST")) {
            this.doPost(req, resp);
        }
        else if (method.equals("PUT")) {
            this.doPut(req, resp);
        }
        else if (method.equals("DELETE")) {
            this.doDelete(req, resp);
        }
        else if (method.equals("OPTIONS")) {
            this.doOptions(req, resp);
        }
        else if (method.equals("TRACE")) {
            this.doTrace(req, resp);
        }
        else {
            String errMsg = HttpServlet.lStrings.getString("http.method_not_implemented");
            final Object[] errArgs = { method };
            errMsg = MessageFormat.format(errMsg, errArgs);
            resp.sendError(501, errMsg);
        }
    }
    
    private void maybeSetLastModified(final HttpServletResponse resp, final long lastModified) {
        if (resp.containsHeader("Last-Modified")) {
            return;
        }
        if (lastModified >= 0L) {
            resp.setDateHeader("Last-Modified", lastModified);
        }
    }
    
    @Override
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
        if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
            throw new ServletException("non-HTTP request or response");
        }
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        this.service(request, response);
    }
    
    static {
        HttpServlet.lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
    }
}
