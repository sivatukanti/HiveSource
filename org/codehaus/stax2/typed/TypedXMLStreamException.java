// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.typed;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class TypedXMLStreamException extends XMLStreamException
{
    private static final long serialVersionUID = 1L;
    protected String mLexical;
    
    public TypedXMLStreamException(final String mLexical, final String msg) {
        super(msg);
        this.mLexical = mLexical;
    }
    
    public TypedXMLStreamException(final String mLexical, final IllegalArgumentException th) {
        super(th);
        this.mLexical = mLexical;
    }
    
    public TypedXMLStreamException(final String mLexical, final String msg, final IllegalArgumentException th) {
        super(msg, th);
        this.mLexical = mLexical;
    }
    
    public TypedXMLStreamException(final String mLexical, final String msg, final Location location, final IllegalArgumentException th) {
        super(msg, location, th);
        this.mLexical = mLexical;
    }
    
    public TypedXMLStreamException(final String mLexical, final String msg, final Location location) {
        super(msg, location);
        this.mLexical = mLexical;
    }
    
    public String getLexical() {
        return this.mLexical;
    }
}
