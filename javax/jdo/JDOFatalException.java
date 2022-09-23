// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOFatalException extends JDOException
{
    public JDOFatalException() {
    }
    
    public JDOFatalException(final String msg) {
        super(msg);
    }
    
    public JDOFatalException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOFatalException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public JDOFatalException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDOFatalException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public JDOFatalException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
