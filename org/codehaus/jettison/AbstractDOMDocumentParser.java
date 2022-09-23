// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import javax.xml.stream.XMLOutputFactory;
import java.io.ByteArrayOutputStream;
import javax.xml.stream.XMLInputFactory;
import org.w3c.dom.Document;
import java.io.InputStream;

public class AbstractDOMDocumentParser
{
    private AbstractXMLInputFactory inputFactory;
    
    protected AbstractDOMDocumentParser(final AbstractXMLInputFactory inputFactory) {
        this.inputFactory = inputFactory;
    }
    
    public Document parse(final InputStream input) throws IOException {
        try {
            final XMLStreamReader streamReader = this.inputFactory.createXMLStreamReader(input);
            final XMLInputFactory readerFactory = XMLInputFactory.newInstance();
            final XMLEventReader eventReader = readerFactory.createXMLEventReader(streamReader);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            final XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(baos);
            eventWriter.add(eventReader);
            eventWriter.close();
            final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return this.getDocumentBuilder().parse(bais);
        }
        catch (Exception ex) {
            final IOException ioex = new IOException("Cannot parse input stream");
            ioex.initCause(ex);
            throw ioex;
        }
    }
    
    private DocumentBuilder getDocumentBuilder() {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            return builder;
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException("Failed to create DocumentBuilder", e);
        }
    }
}
