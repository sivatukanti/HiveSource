// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.io.IOException;

public class InvalidChecksumSizeException extends IOException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidChecksumSizeException(final String s) {
        super(s);
    }
}
