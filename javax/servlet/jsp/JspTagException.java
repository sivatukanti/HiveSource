// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp;

public class JspTagException extends JspException
{
    public JspTagException(final String msg) {
        super(msg);
    }
    
    public JspTagException() {
    }
    
    public JspTagException(final String message, final Throwable rootCause) {
        super(message, rootCause);
    }
    
    public JspTagException(final Throwable rootCause) {
        super(rootCause);
    }
}
