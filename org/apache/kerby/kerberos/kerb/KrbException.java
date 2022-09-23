// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb;

public class KrbException extends Exception
{
    private static final long serialVersionUID = 7305497872367599428L;
    private KrbErrorCode errorCode;
    
    public KrbException(final String message) {
        super(message);
    }
    
    public KrbException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public KrbException(final KrbErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public KrbException(final KrbErrorCode errorCode, final Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    public KrbException(final KrbErrorCode errorCode, final String message) {
        super(message + " with error code: " + errorCode.name());
        this.errorCode = errorCode;
    }
    
    public KrbErrorCode getKrbErrorCode() {
        return this.errorCode;
    }
}
