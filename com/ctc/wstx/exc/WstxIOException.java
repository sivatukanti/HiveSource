// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.exc;

import java.io.IOException;

public class WstxIOException extends WstxException
{
    public WstxIOException(final IOException ie) {
        super(ie);
    }
    
    public WstxIOException(final String msg) {
        super(msg);
    }
}
