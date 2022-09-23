// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLStreamConstants;
import org.codehaus.stax2.XMLEventReader2;

public class Stax2FilteredEventReader implements XMLEventReader2, XMLStreamConstants
{
    final XMLEventReader2 mReader;
    final EventFilter mFilter;
    
    public Stax2FilteredEventReader(final XMLEventReader2 mReader, final EventFilter mFilter) {
        this.mReader = mReader;
        this.mFilter = mFilter;
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
        try {
            return this.peek() != null;
        }
        catch (XMLStreamException cause) {
            throw new RuntimeException(cause);
        }
    }
    
    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent nextEvent;
        do {
            nextEvent = this.mReader.nextEvent();
        } while (nextEvent != null && !this.mFilter.accept(nextEvent));
        return nextEvent;
    }
    
    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException cause) {
            throw new RuntimeException(cause);
        }
    }
    
    public XMLEvent nextTag() throws XMLStreamException {
        XMLEvent nextTag;
        do {
            nextTag = this.mReader.nextTag();
        } while (nextTag != null && !this.mFilter.accept(nextTag));
        return nextTag;
    }
    
    public XMLEvent peek() throws XMLStreamException {
        XMLEvent peek;
        while (true) {
            peek = this.mReader.peek();
            if (peek == null || this.mFilter.accept(peek)) {
                break;
            }
            this.mReader.nextEvent();
        }
        return peek;
    }
    
    public void remove() {
        this.mReader.remove();
    }
    
    public boolean hasNextEvent() throws XMLStreamException {
        return this.peek() != null;
    }
    
    public boolean isPropertySupported(final String s) {
        return this.mReader.isPropertySupported(s);
    }
    
    public boolean setProperty(final String s, final Object o) {
        return this.mReader.setProperty(s, o);
    }
}
