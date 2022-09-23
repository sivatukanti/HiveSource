// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.StringUtils;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.text.StringEscapeUtils;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import javax.servlet.http.HttpServlet;

public class ReconfigurationServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG;
    public static final String CONF_SERVLET_RECONFIGURABLE_PREFIX = "conf.servlet.reconfigurable.";
    
    @Override
    public void init() throws ServletException {
        super.init();
    }
    
    private Reconfigurable getReconfigurable(final HttpServletRequest req) {
        ReconfigurationServlet.LOG.info("servlet path: " + req.getServletPath());
        ReconfigurationServlet.LOG.info("getting attribute: conf.servlet.reconfigurable." + req.getServletPath());
        return (Reconfigurable)this.getServletContext().getAttribute("conf.servlet.reconfigurable." + req.getServletPath());
    }
    
    private void printHeader(final PrintWriter out, final String nodeName) {
        out.print("<html><head>");
        out.printf("<title>%s Reconfiguration Utility</title>%n", StringEscapeUtils.escapeHtml4(nodeName));
        out.print("</head><body>\n");
        out.printf("<h1>%s Reconfiguration Utility</h1>%n", StringEscapeUtils.escapeHtml4(nodeName));
    }
    
    private void printFooter(final PrintWriter out) {
        out.print("</body></html>\n");
    }
    
    private void printConf(final PrintWriter out, final Reconfigurable reconf) {
        final Configuration oldConf = reconf.getConf();
        final Configuration newConf = new Configuration();
        final Collection<ReconfigurationUtil.PropertyChange> changes = ReconfigurationUtil.getChangedProperties(newConf, oldConf);
        boolean changeOK = true;
        out.println("<form action=\"\" method=\"post\">");
        out.println("<table border=\"1\">");
        out.println("<tr><th>Property</th><th>Old value</th>");
        out.println("<th>New value </th><th></th></tr>");
        for (final ReconfigurationUtil.PropertyChange c : changes) {
            out.print("<tr><td>");
            if (!reconf.isPropertyReconfigurable(c.prop)) {
                out.print("<font color=\"red\">" + StringEscapeUtils.escapeHtml4(c.prop) + "</font>");
                changeOK = false;
            }
            else {
                out.print(StringEscapeUtils.escapeHtml4(c.prop));
                out.print("<input type=\"hidden\" name=\"" + StringEscapeUtils.escapeHtml4(c.prop) + "\" value=\"" + StringEscapeUtils.escapeHtml4(c.newVal) + "\"/>");
            }
            out.print("</td><td>" + ((c.oldVal == null) ? "<it>default</it>" : StringEscapeUtils.escapeHtml4(c.oldVal)) + "</td><td>" + ((c.newVal == null) ? "<it>default</it>" : StringEscapeUtils.escapeHtml4(c.newVal)) + "</td>");
            out.print("</tr>\n");
        }
        out.println("</table>");
        if (!changeOK) {
            out.println("<p><font color=\"red\">WARNING: properties marked red will not be changed until the next restart.</font></p>");
        }
        out.println("<input type=\"submit\" value=\"Apply\" />");
        out.println("</form>");
    }
    
    private Enumeration<String> getParams(final HttpServletRequest req) {
        return req.getParameterNames();
    }
    
    private void applyChanges(final PrintWriter out, final Reconfigurable reconf, final HttpServletRequest req) throws ReconfigurationException {
        final Configuration oldConf = reconf.getConf();
        final Configuration newConf = new Configuration();
        final Enumeration<String> params = this.getParams(req);
        synchronized (oldConf) {
            while (params.hasMoreElements()) {
                final String rawParam = params.nextElement();
                final String param = StringEscapeUtils.unescapeHtml4(rawParam);
                final String value = StringEscapeUtils.unescapeHtml4(req.getParameter(rawParam));
                if (value != null) {
                    if (value.equals(newConf.getRaw(param)) || value.equals("default") || value.equals("null") || value.isEmpty()) {
                        if ((value.equals("default") || value.equals("null") || value.isEmpty()) && oldConf.getRaw(param) != null) {
                            out.println("<p>Changed \"" + StringEscapeUtils.escapeHtml4(param) + "\" from \"" + StringEscapeUtils.escapeHtml4(oldConf.getRaw(param)) + "\" to default</p>");
                            reconf.reconfigureProperty(param, null);
                        }
                        else if (!value.equals("default") && !value.equals("null") && !value.isEmpty() && (oldConf.getRaw(param) == null || !oldConf.getRaw(param).equals(value))) {
                            if (oldConf.getRaw(param) == null) {
                                out.println("<p>Changed \"" + StringEscapeUtils.escapeHtml4(param) + "\" from default to \"" + StringEscapeUtils.escapeHtml4(value) + "\"</p>");
                            }
                            else {
                                out.println("<p>Changed \"" + StringEscapeUtils.escapeHtml4(param) + "\" from \"" + StringEscapeUtils.escapeHtml4(oldConf.getRaw(param)) + "\" to \"" + StringEscapeUtils.escapeHtml4(value) + "\"</p>");
                            }
                            reconf.reconfigureProperty(param, value);
                        }
                        else {
                            ReconfigurationServlet.LOG.info("property " + param + " unchanged");
                        }
                    }
                    else {
                        out.println("<p>\"" + StringEscapeUtils.escapeHtml4(param) + "\" not changed because value has changed from \"" + StringEscapeUtils.escapeHtml4(value) + "\" to \"" + StringEscapeUtils.escapeHtml4(newConf.getRaw(param)) + "\" since approval</p>");
                    }
                }
            }
        }
    }
    
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        ReconfigurationServlet.LOG.info("GET");
        resp.setContentType("text/html");
        final PrintWriter out = resp.getWriter();
        final Reconfigurable reconf = this.getReconfigurable(req);
        final String nodeName = reconf.getClass().getCanonicalName();
        this.printHeader(out, nodeName);
        this.printConf(out, reconf);
        this.printFooter(out);
    }
    
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        ReconfigurationServlet.LOG.info("POST");
        resp.setContentType("text/html");
        final PrintWriter out = resp.getWriter();
        final Reconfigurable reconf = this.getReconfigurable(req);
        final String nodeName = reconf.getClass().getCanonicalName();
        this.printHeader(out, nodeName);
        try {
            this.applyChanges(out, reconf, req);
        }
        catch (ReconfigurationException e) {
            resp.sendError(500, StringUtils.stringifyException(e));
            return;
        }
        out.println("<p><a href=\"" + req.getServletPath() + "\">back</a></p>");
        this.printFooter(out);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ReconfigurationServlet.class);
    }
}
