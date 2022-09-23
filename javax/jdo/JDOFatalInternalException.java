// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOFatalInternalException extends JDOFatalException
{
    public JDOFatalInternalException() {
    }
    
    public JDOFatalInternalException(final String msg) {
        super(msg);
    }
    
    public JDOFatalInternalException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOFatalInternalException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public JDOFatalInternalException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDOFatalInternalException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public JDOFatalInternalException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
