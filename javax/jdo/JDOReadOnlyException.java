// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOReadOnlyException extends JDOUserException
{
    public JDOReadOnlyException() {
    }
    
    public JDOReadOnlyException(final String msg) {
        super(msg);
    }
    
    public JDOReadOnlyException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOReadOnlyException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
}
