// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOUserCallbackException extends JDOUserException
{
    public JDOUserCallbackException() {
    }
    
    public JDOUserCallbackException(final String msg) {
        super(msg);
    }
    
    public JDOUserCallbackException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOUserCallbackException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public JDOUserCallbackException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDOUserCallbackException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public JDOUserCallbackException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
