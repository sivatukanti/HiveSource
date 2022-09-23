// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp;

public class JspException extends Exception
{
    public JspException() {
    }
    
    public JspException(final String msg) {
        super(msg);
    }
    
    public JspException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public JspException(final Throwable cause) {
        super(cause);
    }
    
    @Deprecated
    public Throwable getRootCause() {
        return this.getCause();
    }
}
