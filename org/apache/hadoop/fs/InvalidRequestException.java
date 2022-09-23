// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;

public class InvalidRequestException extends IOException
{
    static final long serialVersionUID = 0L;
    
    public InvalidRequestException(final String str) {
        super(str);
    }
    
    public InvalidRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
