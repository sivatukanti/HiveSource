// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.google.inject.Singleton;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.servlet.http.HttpServlet;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
@Singleton
public class DefaultWrapperServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    
    @InterfaceAudience.Private
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final RequestDispatcher rd = this.getServletContext().getNamedDispatcher("default");
        final HttpServletRequest wrapped = new HttpServletRequestWrapper(req) {
            @Override
            public String getServletPath() {
                return "";
            }
        };
        rd.forward(wrapped, resp);
    }
}
