// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.EndDocument;

public class EndDocumentEventImpl extends BaseEventImpl implements EndDocument
{
    public EndDocumentEventImpl(final Location location) {
        super(location);
    }
    
    @Override
    public int getEventType() {
        return 8;
    }
    
    @Override
    public boolean isEndDocument() {
        return true;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        xmlStreamWriter2.writeEndDocument();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof EndDocument);
    }
    
    @Override
    public int hashCode() {
        return 8;
    }
}
