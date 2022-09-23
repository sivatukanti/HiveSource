// 
// Decompiled by Procyon v0.5.36
// 

package javax.transaction;

public class SystemException extends Exception
{
    public int errorCode;
    
    public SystemException() {
    }
    
    public SystemException(final String message) {
        super(message);
    }
    
    public SystemException(final int errorCode) {
        this.errorCode = errorCode;
    }
}
