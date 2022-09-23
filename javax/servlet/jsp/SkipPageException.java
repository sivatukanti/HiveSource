// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp;

public class SkipPageException extends JspException
{
    public SkipPageException() {
    }
    
    public SkipPageException(final String message) {
        super(message);
    }
    
    public SkipPageException(final String message, final Throwable rootCause) {
        super(message, rootCause);
    }
    
    public SkipPageException(final Throwable rootCause) {
        super(rootCause);
    }
}
