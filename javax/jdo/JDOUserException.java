// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOUserException extends JDOCanRetryException
{
    public JDOUserException() {
    }
    
    public JDOUserException(final String msg) {
        super(msg);
    }
    
    public JDOUserException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOUserException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public JDOUserException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDOUserException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public JDOUserException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
