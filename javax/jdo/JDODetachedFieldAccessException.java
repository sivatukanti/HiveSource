// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDODetachedFieldAccessException extends JDOUserException
{
    public JDODetachedFieldAccessException() {
    }
    
    public JDODetachedFieldAccessException(final String msg) {
        super(msg);
    }
    
    public JDODetachedFieldAccessException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDODetachedFieldAccessException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDODetachedFieldAccessException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
}
