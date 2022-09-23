// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp;

public final class ErrorData
{
    private Throwable throwable;
    private int statusCode;
    private String uri;
    private String servletName;
    
    public ErrorData(final Throwable throwable, final int statusCode, final String uri, final String servletName) {
        this.throwable = throwable;
        this.statusCode = statusCode;
        this.uri = uri;
        this.servletName = servletName;
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
    
    public String getRequestURI() {
        return this.uri;
    }
    
    public String getServletName() {
        return this.servletName;
    }
}
