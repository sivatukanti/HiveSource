// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.mortbay.jetty.AbstractGenerator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.mortbay.util.ByteArrayISO8859Writer;
import org.mortbay.jetty.HttpConnection;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ErrorHandler extends AbstractHandler
{
    boolean _showStacks;
    String _cacheControl;
    
    public ErrorHandler() {
        this._showStacks = true;
        this._cacheControl = "must-revalidate,no-cache,no-store";
    }
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException {
        final HttpConnection connection = HttpConnection.getCurrentConnection();
        connection.getRequest().setHandled(true);
        final String method = request.getMethod();
        if (!method.equals("GET") && !method.equals("POST") && !method.equals("HEAD")) {
            return;
        }
        response.setContentType("text/html; charset=iso-8859-1");
        if (this._cacheControl != null) {
            response.setHeader("Cache-Control", this._cacheControl);
        }
        final ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(4096);
        this.handleErrorPage(request, writer, connection.getResponse().getStatus(), connection.getResponse().getReason());
        writer.flush();
        response.setContentLength(writer.size());
        writer.writeTo(response.getOutputStream());
        writer.destroy();
    }
    
    protected void handleErrorPage(final HttpServletRequest request, final Writer writer, final int code, final String message) throws IOException {
        this.writeErrorPage(request, writer, code, message, this._showStacks);
    }
    
    protected void writeErrorPage(final HttpServletRequest request, final Writer writer, final int code, String message, final boolean showStacks) throws IOException {
        if (message == null) {
            message = AbstractGenerator.getReason(code);
        }
        writer.write("<html>\n<head>\n");
        this.writeErrorPageHead(request, writer, code, message);
        writer.write("</head>\n<body>");
        this.writeErrorPageBody(request, writer, code, message, showStacks);
        writer.write("\n</body>\n</html>\n");
    }
    
    protected void writeErrorPageHead(final HttpServletRequest request, final Writer writer, final int code, final String message) throws IOException {
        writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"/>\n");
        writer.write("<title>Error ");
        writer.write(Integer.toString(code));
        writer.write(32);
        this.write(writer, message);
        writer.write("</title>\n");
    }
    
    protected void writeErrorPageBody(final HttpServletRequest request, final Writer writer, final int code, final String message, final boolean showStacks) throws IOException {
        final String uri = request.getRequestURI();
        this.writeErrorPageMessage(request, writer, code, message, uri);
        if (showStacks) {
            this.writeErrorPageStacks(request, writer);
        }
        writer.write("<hr /><i><small>Powered by Jetty://</small></i>");
        for (int i = 0; i < 20; ++i) {
            writer.write("<br/>                                                \n");
        }
    }
    
    protected void writeErrorPageMessage(final HttpServletRequest request, final Writer writer, final int code, final String message, final String uri) throws IOException {
        writer.write("<h2>HTTP ERROR ");
        writer.write(Integer.toString(code));
        writer.write("</h2>\n<p>Problem accessing ");
        this.write(writer, uri);
        writer.write(". Reason:\n<pre>    ");
        this.write(writer, message);
        writer.write("</pre></p>");
    }
    
    protected void writeErrorPageStacks(final HttpServletRequest request, final Writer writer) throws IOException {
        for (Throwable th = (Throwable)request.getAttribute("javax.servlet.error.exception"); th != null; th = th.getCause()) {
            writer.write("<h3>Caused by:</h3><pre>");
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            th.printStackTrace(pw);
            pw.flush();
            this.write(writer, sw.getBuffer().toString());
            writer.write("</pre>\n");
        }
    }
    
    public String getCacheControl() {
        return this._cacheControl;
    }
    
    public void setCacheControl(final String cacheControl) {
        this._cacheControl = cacheControl;
    }
    
    public boolean isShowStacks() {
        return this._showStacks;
    }
    
    public void setShowStacks(final boolean showStacks) {
        this._showStacks = showStacks;
    }
    
    protected void write(final Writer writer, final String string) throws IOException {
        if (string == null) {
            return;
        }
        for (int i = 0; i < string.length(); ++i) {
            final char c = string.charAt(i);
            switch (c) {
                case '&': {
                    writer.write("&amp;");
                    break;
                }
                case '<': {
                    writer.write("&lt;");
                    break;
                }
                case '>': {
                    writer.write("&gt;");
                    break;
                }
                default: {
                    if (Character.isISOControl(c) && !Character.isWhitespace(c)) {
                        writer.write(63);
                        break;
                    }
                    writer.write(c);
                    break;
                }
            }
        }
    }
}
