// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOFatalDataStoreException extends JDOFatalException
{
    public JDOFatalDataStoreException() {
    }
    
    public JDOFatalDataStoreException(final String msg) {
        super(msg);
    }
    
    public JDOFatalDataStoreException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDOFatalDataStoreException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOFatalDataStoreException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public JDOFatalDataStoreException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public JDOFatalDataStoreException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
