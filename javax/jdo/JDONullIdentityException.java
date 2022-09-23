// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDONullIdentityException extends JDOUserException
{
    public JDONullIdentityException() {
    }
    
    public JDONullIdentityException(final String msg) {
        super(msg);
    }
    
    public JDONullIdentityException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDONullIdentityException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDONullIdentityException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
}
