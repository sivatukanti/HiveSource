// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

public enum SnappyErrorCode
{
    UNKNOWN(0), 
    FAILED_TO_LOAD_NATIVE_LIBRARY(1), 
    PARSING_ERROR(2), 
    NOT_A_DIRECT_BUFFER(3), 
    OUT_OF_MEMORY(4), 
    FAILED_TO_UNCOMPRESS(5);
    
    public final int id;
    
    private SnappyErrorCode(final int id) {
        this.id = id;
    }
    
    public static SnappyErrorCode getErrorCode(final int id) {
        for (final SnappyErrorCode code : values()) {
            if (code.id == id) {
                return code;
            }
        }
        return SnappyErrorCode.UNKNOWN;
    }
    
    public static String getErrorMessage(final int id) {
        return getErrorCode(id).name();
    }
}
