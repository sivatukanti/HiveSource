// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.exc;

import javax.xml.stream.Location;

public class WstxUnexpectedCharException extends WstxParsingException
{
    final char mChar;
    
    public WstxUnexpectedCharException(final String msg, final Location loc, final char c) {
        super(msg, loc);
        this.mChar = c;
    }
    
    public char getChar() {
        return this.mChar;
    }
}
