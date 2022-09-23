// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOCanRetryException extends JDOException
{
    public JDOCanRetryException() {
    }
    
    public JDOCanRetryException(final String msg) {
        super(msg);
    }
    
    public JDOCanRetryException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOCanRetryException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public JDOCanRetryException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDOCanRetryException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public JDOCanRetryException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
