// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.exc;

import javax.xml.stream.XMLStreamException;

public class WstxLazyException extends RuntimeException
{
    final XMLStreamException mOrig;
    
    public WstxLazyException(final XMLStreamException origEx) {
        super(origEx.getMessage(), origEx);
        this.mOrig = origEx;
    }
    
    public static void throwLazily(final XMLStreamException ex) throws WstxLazyException {
        throw new WstxLazyException(ex);
    }
    
    @Override
    public String getMessage() {
        return "[" + this.getClass().getName() + "] " + this.mOrig.getMessage();
    }
    
    @Override
    public String toString() {
        return "[" + this.getClass().getName() + "] " + this.mOrig.toString();
    }
}
