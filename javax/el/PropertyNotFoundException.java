// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

public class PropertyNotFoundException extends ELException
{
    public PropertyNotFoundException() {
    }
    
    public PropertyNotFoundException(final String message) {
        super(message);
    }
    
    public PropertyNotFoundException(final Throwable exception) {
        super(exception);
    }
    
    public PropertyNotFoundException(final String pMessage, final Throwable pRootCause) {
        super(pMessage, pRootCause);
    }
}
