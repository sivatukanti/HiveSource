// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLEventWriter;

public class AbstractXMLEventWriter implements XMLEventWriter
{
    private XMLStreamWriter streamWriter;
    
    public AbstractXMLEventWriter(final XMLStreamWriter streamWriter) {
        this.streamWriter = streamWriter;
    }
    
    public void add(final XMLEvent event) throws XMLStreamException {
        if (event.isStartDocument()) {
            this.streamWriter.writeStartDocument();
        }
        else if (event.isStartElement()) {
            final StartElement element = event.asStartElement();
            final QName elQName = element.getName();
            if (elQName.getPrefix().length() > 0 && elQName.getNamespaceURI().length() > 0) {
                this.streamWriter.writeStartElement(elQName.getPrefix(), elQName.getLocalPart(), elQName.getNamespaceURI());
            }
            else if (elQName.getNamespaceURI().length() > 0) {
                this.streamWriter.writeStartElement(elQName.getNamespaceURI(), elQName.getLocalPart());
            }
            else {
                this.streamWriter.writeStartElement(elQName.getLocalPart());
            }
            final Iterator namespaces = element.getNamespaces();
            while (namespaces.hasNext()) {
                final Namespace ns = namespaces.next();
                final String prefix = ns.getPrefix();
                final String nsURI = ns.getNamespaceURI();
                this.streamWriter.writeNamespace(prefix, nsURI);
            }
            final Iterator attris = element.getAttributes();
            while (attris.hasNext()) {
                final Attribute attr = attris.next();
                final QName atQName = attr.getName();
                final String value = attr.getValue();
                if (atQName.getPrefix().length() > 0 && atQName.getNamespaceURI().length() > 0) {
                    this.streamWriter.writeAttribute(atQName.getPrefix(), atQName.getNamespaceURI(), atQName.getLocalPart(), value);
                }
                else if (atQName.getNamespaceURI().length() > 0) {
                    this.streamWriter.writeAttribute(atQName.getNamespaceURI(), atQName.getLocalPart(), value);
                }
                else {
                    this.streamWriter.writeAttribute(atQName.getLocalPart(), value);
                }
            }
        }
        else if (event.isCharacters()) {
            final Characters chars = event.asCharacters();
            this.streamWriter.writeCharacters(chars.getData());
        }
        else if (event.isEndElement()) {
            this.streamWriter.writeEndElement();
        }
        else {
            if (!event.isEndDocument()) {
                throw new XMLStreamException("Unsupported event type: " + event);
            }
            this.streamWriter.writeEndDocument();
        }
    }
    
    public void add(final XMLEventReader eventReader) throws XMLStreamException {
        while (eventReader.hasNext()) {
            final XMLEvent event = eventReader.nextEvent();
            this.add(event);
        }
        this.close();
    }
    
    public void close() throws XMLStreamException {
        this.streamWriter.close();
    }
    
    public void flush() throws XMLStreamException {
        this.streamWriter.flush();
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.streamWriter.getNamespaceContext();
    }
    
    public String getPrefix(final String prefix) throws XMLStreamException {
        return this.streamWriter.getPrefix(prefix);
    }
    
    public void setDefaultNamespace(final String namespace) throws XMLStreamException {
        this.streamWriter.setDefaultNamespace(namespace);
    }
    
    public void setNamespaceContext(final NamespaceContext nsContext) throws XMLStreamException {
        this.streamWriter.setNamespaceContext(nsContext);
    }
    
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        this.streamWriter.setPrefix(prefix, uri);
    }
}
