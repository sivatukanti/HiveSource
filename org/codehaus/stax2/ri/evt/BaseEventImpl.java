// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import java.util.Iterator;
import java.io.IOException;
import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Characters;
import javax.xml.stream.Location;
import org.codehaus.stax2.evt.XMLEvent2;

public abstract class BaseEventImpl implements XMLEvent2
{
    protected final Location mLocation;
    
    protected BaseEventImpl(final Location mLocation) {
        this.mLocation = mLocation;
    }
    
    public Characters asCharacters() {
        return (Characters)this;
    }
    
    public EndElement asEndElement() {
        return (EndElement)this;
    }
    
    public StartElement asStartElement() {
        return (StartElement)this;
    }
    
    public abstract int getEventType();
    
    public Location getLocation() {
        return this.mLocation;
    }
    
    public QName getSchemaType() {
        return null;
    }
    
    public boolean isAttribute() {
        return false;
    }
    
    public boolean isCharacters() {
        return false;
    }
    
    public boolean isEndDocument() {
        return false;
    }
    
    public boolean isEndElement() {
        return false;
    }
    
    public boolean isEntityReference() {
        return false;
    }
    
    public boolean isNamespace() {
        return false;
    }
    
    public boolean isProcessingInstruction() {
        return false;
    }
    
    public boolean isStartDocument() {
        return false;
    }
    
    public boolean isStartElement() {
        return false;
    }
    
    public abstract void writeAsEncodedUnicode(final Writer p0) throws XMLStreamException;
    
    public abstract void writeUsing(final XMLStreamWriter2 p0) throws XMLStreamException;
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public abstract int hashCode();
    
    @Override
    public String toString() {
        return "[Stax Event #" + this.getEventType() + "]";
    }
    
    protected void throwFromIOE(final IOException th) throws XMLStreamException {
        throw new XMLStreamException(th.getMessage(), th);
    }
    
    protected static boolean stringsWithNullsEqual(final String s, final String anObject) {
        if (s == null || s.length() == 0) {
            return anObject == null || anObject.length() == 0;
        }
        return anObject != null && s.equals(anObject);
    }
    
    protected static boolean iteratedEquals(final Iterator iterator, final Iterator iterator2) {
        if (iterator == null || iterator2 == null) {
            return iterator == iterator2;
        }
        while (iterator.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            }
            if (!iterator.next().equals(iterator2.next())) {
                return false;
            }
        }
        return true;
    }
    
    protected static int addHash(final Iterator iterator, final int n) {
        int n2 = n;
        if (iterator != null) {
            while (iterator.hasNext()) {
                n2 ^= iterator.next().hashCode();
            }
        }
        return n2;
    }
}
