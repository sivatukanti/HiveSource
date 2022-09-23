// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import org.codehaus.stax2.XMLEventReader2;

public class Stax2EventReaderAdapter implements XMLEventReader2
{
    final XMLEventReader mReader;
    
    protected Stax2EventReaderAdapter(final XMLEventReader mReader) {
        this.mReader = mReader;
    }
    
    public static XMLEventReader2 wrapIfNecessary(final XMLEventReader xmlEventReader) {
        if (xmlEventReader instanceof XMLEventReader2) {
            return (XMLEventReader2)xmlEventReader;
        }
        return new Stax2EventReaderAdapter(xmlEventReader);
    }
    
    public void close() throws XMLStreamException {
        this.mReader.close();
    }
    
    public String getElementText() throws XMLStreamException {
        return this.mReader.getElementText();
    }
    
    public Object getProperty(final String s) {
        return this.mReader.getProperty(s);
    }
    
    public boolean hasNext() {
        return this.mReader.hasNext();
    }
    
    public XMLEvent nextEvent() throws XMLStreamException {
        return this.mReader.nextEvent();
    }
    
    public Object next() {
        return this.mReader.next();
    }
    
    public XMLEvent nextTag() throws XMLStreamException {
        return this.mReader.nextTag();
    }
    
    public XMLEvent peek() throws XMLStreamException {
        return this.mReader.peek();
    }
    
    public void remove() {
        this.mReader.remove();
    }
    
    public boolean hasNextEvent() throws XMLStreamException {
        return this.peek() != null;
    }
    
    public boolean isPropertySupported(final String s) {
        try {
            this.mReader.getProperty(s);
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
        return true;
    }
    
    public boolean setProperty(final String s, final Object o) {
        return false;
    }
}
