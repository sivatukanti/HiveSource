// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.Calendar;
import com.google.common.base.Preconditions;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletResponse;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ServletUtil
{
    public static final String HTML_TAIL;
    
    public static PrintWriter initHTML(final ServletResponse response, final String title) throws IOException {
        response.setContentType("text/html");
        final PrintWriter out = response.getWriter();
        out.println("<html>\n<link rel='stylesheet' type='text/css' href='/static/hadoop.css'>\n<title>" + title + "</title>\n<body>\n<h1>" + title + "</h1>\n");
        return out;
    }
    
    public static String getParameter(final ServletRequest request, final String name) {
        String s = request.getParameter(name);
        if (s == null) {
            return null;
        }
        s = s.trim();
        return (s.length() == 0) ? null : s;
    }
    
    public static long parseLongParam(final ServletRequest request, final String param) throws IOException {
        final String paramStr = request.getParameter(param);
        if (paramStr == null) {
            throw new IOException("Invalid request has no " + param + " parameter");
        }
        return Long.parseLong(paramStr);
    }
    
    public static String htmlFooter() {
        return ServletUtil.HTML_TAIL;
    }
    
    public static String getRawPath(final HttpServletRequest request, final String servletName) {
        Preconditions.checkArgument(request.getRequestURI().startsWith(servletName + "/"));
        return request.getRequestURI().substring(servletName.length());
    }
    
    static {
        HTML_TAIL = "<hr />\n<a href='http://hadoop.apache.org'>Hadoop</a>, " + Calendar.getInstance().get(1) + ".\n</body></html>";
    }
}
