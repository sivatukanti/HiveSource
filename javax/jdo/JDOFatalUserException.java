// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOFatalUserException extends JDOFatalException
{
    public JDOFatalUserException() {
    }
    
    public JDOFatalUserException(final String msg) {
        super(msg);
    }
    
    public JDOFatalUserException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOFatalUserException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public JDOFatalUserException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDOFatalUserException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public JDOFatalUserException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
