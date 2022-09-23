// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOUnsupportedOptionException extends JDOUserException
{
    public JDOUnsupportedOptionException() {
    }
    
    public JDOUnsupportedOptionException(final String msg) {
        super(msg);
    }
    
    public JDOUnsupportedOptionException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOUnsupportedOptionException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
}
