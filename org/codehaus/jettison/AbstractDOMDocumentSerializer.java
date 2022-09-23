// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.xml.transform.Source;
import javax.xml.stream.XMLInputFactory;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Element;
import java.io.OutputStream;

public class AbstractDOMDocumentSerializer
{
    private OutputStream output;
    private AbstractXMLOutputFactory writerFactory;
    
    public AbstractDOMDocumentSerializer(final OutputStream output, final AbstractXMLOutputFactory writerFactory) {
        this.output = output;
        this.writerFactory = writerFactory;
    }
    
    public void serialize(final Element el) throws IOException {
        if (this.output == null) {
            throw new IllegalStateException("OutputStream cannot be null");
        }
        try {
            final DOMSource source = new DOMSource(el);
            final XMLInputFactory readerFactory = XMLInputFactory.newInstance();
            final XMLStreamReader streamReader = readerFactory.createXMLStreamReader(source);
            final XMLEventReader eventReader = readerFactory.createXMLEventReader(streamReader);
            final XMLEventWriter eventWriter = this.writerFactory.createXMLEventWriter(this.output);
            eventWriter.add(eventReader);
            eventWriter.close();
        }
        catch (XMLStreamException ex) {
            final IOException ioex = new IOException("Cannot serialize: " + el);
            ioex.initCause(ex);
            throw ioex;
        }
    }
}
