// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

import java.io.IOException;

public class WireParseException extends IOException
{
    public WireParseException() {
    }
    
    public WireParseException(final String s) {
        super(s);
    }
    
    public WireParseException(final String s, final Throwable cause) {
        super(s);
        this.initCause(cause);
    }
}
