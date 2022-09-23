// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOOptimisticVerificationException extends JDOFatalDataStoreException
{
    public JDOOptimisticVerificationException() {
    }
    
    public JDOOptimisticVerificationException(final String msg) {
        super(msg);
    }
    
    public JDOOptimisticVerificationException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDOOptimisticVerificationException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOOptimisticVerificationException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public JDOOptimisticVerificationException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
