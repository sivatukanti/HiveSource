// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.evt;

import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.exc.WstxIOException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.EntityDeclaration;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public abstract class WEntityDeclaration extends BaseEventImpl implements EntityDeclaration
{
    public WEntityDeclaration(final Location loc) {
        super(loc);
    }
    
    @Override
    public abstract String getBaseURI();
    
    @Override
    public abstract String getName();
    
    @Override
    public abstract String getNotationName();
    
    @Override
    public abstract String getPublicId();
    
    @Override
    public abstract String getReplacementText();
    
    @Override
    public abstract String getSystemId();
    
    @Override
    public int getEventType() {
        return 15;
    }
    
    public abstract void writeEnc(final Writer p0) throws IOException;
    
    @Override
    public void writeAsEncodedUnicode(final Writer w) throws XMLStreamException {
        try {
            this.writeEnc(w);
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 w) throws XMLStreamException {
        throw new XMLStreamException("Can not write entity declarations using an XMLStreamWriter");
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof EntityDeclaration)) {
            return false;
        }
        final EntityDeclaration other = (EntityDeclaration)o;
        return BaseEventImpl.stringsWithNullsEqual(this.getName(), other.getName()) && BaseEventImpl.stringsWithNullsEqual(this.getBaseURI(), other.getBaseURI()) && BaseEventImpl.stringsWithNullsEqual(this.getNotationName(), other.getNotationName()) && BaseEventImpl.stringsWithNullsEqual(this.getPublicId(), other.getPublicId()) && BaseEventImpl.stringsWithNullsEqual(this.getReplacementText(), other.getReplacementText()) && BaseEventImpl.stringsWithNullsEqual(this.getSystemId(), other.getSystemId());
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
