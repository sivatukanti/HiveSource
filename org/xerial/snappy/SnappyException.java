// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

@Deprecated
public class SnappyException extends Exception
{
    private static final long serialVersionUID = 1L;
    public final SnappyErrorCode errorCode;
    
    public SnappyException(final int code) {
        this(SnappyErrorCode.getErrorCode(code));
    }
    
    public SnappyException(final SnappyErrorCode errorCode) {
        this.errorCode = errorCode;
    }
    
    public SnappyException(final SnappyErrorCode errorCode, final Exception e) {
        super(e);
        this.errorCode = errorCode;
    }
    
    public SnappyException(final SnappyErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public SnappyErrorCode getErrorCode() {
        return this.errorCode;
    }
    
    public static void throwException(final int errorCode) throws SnappyException {
        throw new SnappyException(errorCode);
    }
    
    @Override
    public String getMessage() {
        return String.format("[%s] %s", this.errorCode.name(), super.getMessage());
    }
}
