// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDODataStoreException extends JDOCanRetryException
{
    public JDODataStoreException() {
    }
    
    public JDODataStoreException(final String msg) {
        super(msg);
    }
    
    public JDODataStoreException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDODataStoreException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public JDODataStoreException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDODataStoreException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public JDODataStoreException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
