// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

public class PropertyNotWritableException extends ELException
{
    public PropertyNotWritableException() {
    }
    
    public PropertyNotWritableException(final String pMessage) {
        super(pMessage);
    }
    
    public PropertyNotWritableException(final Throwable exception) {
        super(exception);
    }
    
    public PropertyNotWritableException(final String pMessage, final Throwable pRootCause) {
        super(pMessage, pRootCause);
    }
}
