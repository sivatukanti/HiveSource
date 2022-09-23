// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.http;

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import java.io.PrintWriter;
import java.util.ResourceBundle;

class NoBodyResponse extends HttpServletResponseWrapper
{
    private static final ResourceBundle lStrings;
    private NoBodyOutputStream noBody;
    private PrintWriter writer;
    private boolean didSetContentLength;
    private boolean usingOutputStream;
    
    NoBodyResponse(final HttpServletResponse r) {
        super(r);
        this.noBody = new NoBodyOutputStream();
    }
    
    void setContentLength() {
        if (!this.didSetContentLength) {
            if (this.writer != null) {
                this.writer.flush();
            }
            this.setContentLength(this.noBody.getContentLength());
        }
    }
    
    @Override
    public void setContentLength(final int len) {
        super.setContentLength(len);
        this.didSetContentLength = true;
    }
    
    @Override
    public void setContentLengthLong(final long len) {
        super.setContentLengthLong(len);
        this.didSetContentLength = true;
    }
    
    @Override
    public void setHeader(final String name, final String value) {
        super.setHeader(name, value);
        this.checkHeader(name);
    }
    
    @Override
    public void addHeader(final String name, final String value) {
        super.addHeader(name, value);
        this.checkHeader(name);
    }
    
    @Override
    public void setIntHeader(final String name, final int value) {
        super.setIntHeader(name, value);
        this.checkHeader(name);
    }
    
    @Override
    public void addIntHeader(final String name, final int value) {
        super.addIntHeader(name, value);
        this.checkHeader(name);
    }
    
    private void checkHeader(final String name) {
        if ("content-length".equalsIgnoreCase(name)) {
            this.didSetContentLength = true;
        }
    }
    
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.writer != null) {
            throw new IllegalStateException(NoBodyResponse.lStrings.getString("err.ise.getOutputStream"));
        }
        this.usingOutputStream = true;
        return this.noBody;
    }
    
    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException {
        if (this.usingOutputStream) {
            throw new IllegalStateException(NoBodyResponse.lStrings.getString("err.ise.getWriter"));
        }
        if (this.writer == null) {
            final OutputStreamWriter w = new OutputStreamWriter(this.noBody, this.getCharacterEncoding());
            this.writer = new PrintWriter(w);
        }
        return this.writer;
    }
    
    static {
        lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
    }
}
