// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sax;

import org.xml.sax.SAXException;

public final class WrappedSaxException extends RuntimeException
{
    final SAXException mCause;
    
    public WrappedSaxException(final SAXException cause) {
        this.mCause = cause;
    }
    
    public SAXException getSaxException() {
        return this.mCause;
    }
    
    @Override
    public String toString() {
        return this.mCause.toString();
    }
}
