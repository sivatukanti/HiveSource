// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

public class ELException extends RuntimeException
{
    public ELException() {
    }
    
    public ELException(final String pMessage) {
        super(pMessage);
    }
    
    public ELException(final Throwable pRootCause) {
        super(pRootCause);
    }
    
    public ELException(final String pMessage, final Throwable pRootCause) {
        super(pMessage, pRootCause);
    }
}
