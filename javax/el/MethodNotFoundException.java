// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

public class MethodNotFoundException extends ELException
{
    public MethodNotFoundException() {
    }
    
    public MethodNotFoundException(final String message) {
        super(message);
    }
    
    public MethodNotFoundException(final Throwable exception) {
        super(exception);
    }
    
    public MethodNotFoundException(final String pMessage, final Throwable pRootCause) {
        super(pMessage, pRootCause);
    }
}
