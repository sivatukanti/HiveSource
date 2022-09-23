// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

public class JDOEnhanceException extends JDOException
{
    public JDOEnhanceException() {
    }
    
    public JDOEnhanceException(final String msg) {
        super(msg);
    }
    
    public JDOEnhanceException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public JDOEnhanceException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
}
