// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOObjectNotFoundException extends JDODataStoreException
{
    public JDOObjectNotFoundException() {
    }
    
    public JDOObjectNotFoundException(final String msg) {
        super(msg);
    }
    
    public JDOObjectNotFoundException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public JDOObjectNotFoundException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public JDOObjectNotFoundException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOObjectNotFoundException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public JDOObjectNotFoundException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
