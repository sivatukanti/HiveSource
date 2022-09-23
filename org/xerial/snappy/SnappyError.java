// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

public class SnappyError extends Error
{
    private static final long serialVersionUID = 1L;
    public final SnappyErrorCode errorCode;
    
    public SnappyError(final SnappyErrorCode code) {
        this.errorCode = code;
    }
    
    public SnappyError(final SnappyErrorCode code, final Error e) {
        super(e);
        this.errorCode = code;
    }
    
    public SnappyError(final SnappyErrorCode code, final String message) {
        super(message);
        this.errorCode = code;
    }
    
    @Override
    public String getMessage() {
        return String.format("[%s] %s", this.errorCode.name(), super.getMessage());
    }
}
