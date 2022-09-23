// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.exc;

import javax.xml.stream.Location;

public class WstxParsingException extends WstxException
{
    public WstxParsingException(final String msg, final Location loc) {
        super(msg, loc);
    }
    
    public WstxParsingException(final String msg) {
        super(msg);
    }
}
