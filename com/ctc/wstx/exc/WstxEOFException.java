// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.exc;

import javax.xml.stream.Location;

public class WstxEOFException extends WstxParsingException
{
    public WstxEOFException(final String msg, final Location loc) {
        super(msg, loc);
    }
}
