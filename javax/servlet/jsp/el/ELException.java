// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.el;

public class ELException extends Exception
{
    private Throwable mRootCause;
    
    public ELException() {
    }
    
    public ELException(final String pMessage) {
        super(pMessage);
    }
    
    public ELException(final Throwable pRootCause) {
        super(pRootCause.getLocalizedMessage());
        this.mRootCause = pRootCause;
    }
    
    public ELException(final String pMessage, final Throwable pRootCause) {
        super(pMessage);
        this.mRootCause = pRootCause;
    }
    
    public Throwable getRootCause() {
        return this.mRootCause;
    }
}
