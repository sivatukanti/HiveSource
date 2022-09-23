// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.txw2.output;

import com.sun.xml.txw2.TypedXmlWriter;

public final class TXWSerializer implements XmlSerializer
{
    public final TypedXmlWriter txw;
    
    public TXWSerializer(final TypedXmlWriter txw) {
        this.txw = txw;
    }
    
    public void startDocument() {
        throw new UnsupportedOperationException();
    }
    
    public void endDocument() {
        throw new UnsupportedOperationException();
    }
    
    public void beginStartTag(final String uri, final String localName, final String prefix) {
        throw new UnsupportedOperationException();
    }
    
    public void writeAttribute(final String uri, final String localName, final String prefix, final StringBuilder value) {
        throw new UnsupportedOperationException();
    }
    
    public void writeXmlns(final String prefix, final String uri) {
        throw new UnsupportedOperationException();
    }
    
    public void endStartTag(final String uri, final String localName, final String prefix) {
        throw new UnsupportedOperationException();
    }
    
    public void endTag() {
        throw new UnsupportedOperationException();
    }
    
    public void text(final StringBuilder text) {
        throw new UnsupportedOperationException();
    }
    
    public void cdata(final StringBuilder text) {
        throw new UnsupportedOperationException();
    }
    
    public void comment(final StringBuilder comment) {
        throw new UnsupportedOperationException();
    }
    
    public void flush() {
        throw new UnsupportedOperationException();
    }
}
