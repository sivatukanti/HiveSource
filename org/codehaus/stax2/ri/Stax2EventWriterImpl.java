// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import java.util.Iterator;
import javax.xml.stream.events.StartElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.evt.XMLEvent2;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLEventWriter;

public class Stax2EventWriterImpl implements XMLEventWriter, XMLStreamConstants
{
    final XMLStreamWriter2 mWriter;
    
    public Stax2EventWriterImpl(final XMLStreamWriter2 mWriter) {
        this.mWriter = mWriter;
    }
    
    public void add(final XMLEvent obj) throws XMLStreamException {
        switch (obj.getEventType()) {
            case 10: {
                final Attribute attribute = (Attribute)obj;
                final QName name = attribute.getName();
                this.mWriter.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attribute.getValue());
                break;
            }
            case 8: {
                this.mWriter.writeEndDocument();
                break;
            }
            case 2: {
                this.mWriter.writeEndElement();
                break;
            }
            case 13: {
                final Namespace namespace = (Namespace)obj;
                this.mWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
                break;
            }
            case 7: {
                final StartDocument startDocument = (StartDocument)obj;
                if (!startDocument.encodingSet()) {
                    this.mWriter.writeStartDocument(startDocument.getVersion());
                }
                else {
                    this.mWriter.writeStartDocument(startDocument.getCharacterEncodingScheme(), startDocument.getVersion());
                }
                break;
            }
            case 1: {
                final StartElement startElement = obj.asStartElement();
                final QName name2 = startElement.getName();
                this.mWriter.writeStartElement(name2.getPrefix(), name2.getLocalPart(), name2.getNamespaceURI());
                final Iterator namespaces = startElement.getNamespaces();
                while (namespaces.hasNext()) {
                    this.add(namespaces.next());
                }
                final Iterator attributes = startElement.getAttributes();
                while (attributes.hasNext()) {
                    this.add(attributes.next());
                }
                break;
            }
            case 4: {
                final Characters characters = obj.asCharacters();
                final String data = characters.getData();
                if (characters.isCData()) {
                    this.mWriter.writeCData(data);
                }
                else {
                    this.mWriter.writeCharacters(data);
                }
                break;
            }
            case 12: {
                this.mWriter.writeCData(obj.asCharacters().getData());
                break;
            }
            case 5: {
                this.mWriter.writeComment(((Comment)obj).getText());
                break;
            }
            case 11: {
                this.mWriter.writeDTD(((DTD)obj).getDocumentTypeDeclaration());
                break;
            }
            case 9: {
                this.mWriter.writeEntityRef(((EntityReference)obj).getName());
                break;
            }
            case 3: {
                final ProcessingInstruction processingInstruction = (ProcessingInstruction)obj;
                this.mWriter.writeProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData());
                break;
            }
            default: {
                if (obj instanceof XMLEvent2) {
                    ((XMLEvent2)obj).writeUsing(this.mWriter);
                    break;
                }
                throw new XMLStreamException("Don't know how to output event " + obj);
            }
        }
    }
    
    public void add(final XMLEventReader xmlEventReader) throws XMLStreamException {
        while (xmlEventReader.hasNext()) {
            this.add(xmlEventReader.nextEvent());
        }
    }
    
    public void close() throws XMLStreamException {
        this.mWriter.close();
    }
    
    public void flush() throws XMLStreamException {
        this.mWriter.flush();
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.mWriter.getNamespaceContext();
    }
    
    public String getPrefix(final String s) throws XMLStreamException {
        return this.mWriter.getPrefix(s);
    }
    
    public void setDefaultNamespace(final String defaultNamespace) throws XMLStreamException {
        this.mWriter.setDefaultNamespace(defaultNamespace);
    }
    
    public void setNamespaceContext(final NamespaceContext namespaceContext) throws XMLStreamException {
        this.mWriter.setNamespaceContext(namespaceContext);
    }
    
    public void setPrefix(final String s, final String s2) throws XMLStreamException {
        this.mWriter.setPrefix(s, s2);
    }
}
