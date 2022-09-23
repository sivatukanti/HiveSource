// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import java.util.Iterator;
import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.JsonParser;
import java.io.IOException;
import com.sun.jersey.api.json.JSONJAXBContext;
import org.codehaus.jackson.JsonFactory;
import javax.xml.bind.JAXBContext;
import com.sun.jersey.api.json.JSONConfiguration;
import java.io.Reader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class JsonXmlStreamReader implements XMLStreamReader
{
    private final XmlEventProvider eventProvider;
    private final JsonNamespaceContext namespaceContext;
    private XMLStreamException validationException;
    
    public static XMLStreamReader create(final Reader reader, final JSONConfiguration configuration, String rootName, final Class<?> expectedType, JAXBContext jaxbContext, final boolean readingList) throws XMLStreamException {
        try {
            if ((rootName == null || "".equals(rootName)) && configuration.isRootUnwrapping()) {
                rootName = "rootElement";
            }
            final JsonParser rawParser = new JsonFactory().createJsonParser(reader);
            final JsonParser nonListParser = configuration.isRootUnwrapping() ? JacksonRootAddingParser.createRootAddingParser(rawParser, rootName) : rawParser;
            XmlEventProvider eventStack = null;
            switch (configuration.getNotation()) {
                case MAPPED: {
                    eventStack = new MappedNotationEventProvider(nonListParser, configuration, rootName);
                    break;
                }
                case NATURAL: {
                    if (jaxbContext instanceof JSONJAXBContext) {
                        jaxbContext = ((JSONJAXBContext)jaxbContext).getOriginalJaxbContext();
                    }
                    if (!readingList) {
                        eventStack = new NaturalNotationEventProvider(nonListParser, configuration, rootName, jaxbContext, expectedType);
                        break;
                    }
                    eventStack = new NaturalNotationEventProvider(JacksonRootAddingParser.createRootAddingParser(nonListParser, "jsonArrayRootElement"), configuration, rootName, jaxbContext, expectedType);
                    break;
                }
            }
            return new JsonXmlStreamReader(eventStack);
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    private JsonXmlStreamReader(final XmlEventProvider nodeStack) {
        this.namespaceContext = new JsonNamespaceContext();
        this.eventProvider = nodeStack;
    }
    
    private List<JsonXmlEvent.Attribute> getAttributes() {
        if (this.getEventType() != 1 && this.getEventType() != 10) {
            throw new IllegalArgumentException("Parser must be on START_ELEMENT or ATTRIBUTE to read next attribute.");
        }
        final JsonXmlEvent currentNode = this.eventProvider.getCurrentNode();
        try {
            if (currentNode.getAttributes() == null) {
                this.eventProvider.processAttributesOfCurrentElement();
            }
            return currentNode.getAttributes();
        }
        catch (XMLStreamException xse) {
            this.validationException = xse;
            return Collections.emptyList();
        }
    }
    
    private JsonXmlEvent.Attribute getAttribute(final int index) {
        final List<JsonXmlEvent.Attribute> attributes = this.getAttributes();
        if (index < 0 || index >= attributes.size()) {
            return null;
        }
        return attributes.get(index);
    }
    
    @Override
    public void close() throws XMLStreamException {
        this.eventProvider.close();
    }
    
    @Override
    public int getAttributeCount() {
        return this.getAttributes().size();
    }
    
    @Override
    public String getAttributeLocalName(final int index) {
        final JsonXmlEvent.Attribute attribute = this.getAttribute(index);
        return (attribute == null) ? null : attribute.getName().getLocalPart();
    }
    
    @Override
    public QName getAttributeName(final int index) {
        final JsonXmlEvent.Attribute attribute = this.getAttribute(index);
        return (attribute == null) ? null : attribute.getName();
    }
    
    @Override
    public String getAttributeNamespace(final int index) {
        final JsonXmlEvent.Attribute attribute = this.getAttribute(index);
        return (attribute == null) ? null : attribute.getName().getNamespaceURI();
    }
    
    @Override
    public String getAttributePrefix(final int index) {
        final JsonXmlEvent.Attribute attribute = this.getAttribute(index);
        return (attribute == null) ? null : attribute.getName().getPrefix();
    }
    
    @Override
    public String getAttributeType(final int index) {
        return null;
    }
    
    @Override
    public String getAttributeValue(final String namespaceURI, final String localName) {
        if (localName == null || "".equals(localName)) {
            return null;
        }
        for (final JsonXmlEvent.Attribute attribute : this.getAttributes()) {
            if (localName.equals(attribute.getName().getLocalPart()) && (namespaceURI == null || namespaceURI.equals(attribute.getName().getNamespaceURI()))) {
                return attribute.getValue();
            }
        }
        return null;
    }
    
    @Override
    public String getAttributeValue(final int index) {
        final JsonXmlEvent.Attribute attribute = this.getAttribute(index);
        return (attribute == null) ? null : attribute.getValue();
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        return "UTF-8";
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        if (this.getEventType() != 1) {
            throw new XMLStreamException("Parser must be on START_ELEMENT to read next text.", this.getLocation());
        }
        int eventType = this.next();
        final StringBuilder content = new StringBuilder();
        while (eventType != 2) {
            if (eventType == 4 || eventType == 12 || eventType == 6 || eventType == 9) {
                content.append(this.getText());
            }
            else if (eventType != 3) {
                if (eventType != 5) {
                    if (eventType == 8) {
                        throw new XMLStreamException("Unexpected end of document when reading element text content.", this.getLocation());
                    }
                    if (eventType == 1) {
                        throw new XMLStreamException("Element text content may not contain START_ELEMENT.", this.getLocation());
                    }
                    throw new XMLStreamException("Unexpected event type " + eventType + ".", this.getLocation());
                }
            }
            eventType = this.next();
        }
        return content.toString();
    }
    
    @Override
    public String getEncoding() {
        return "UTF-8";
    }
    
    @Override
    public int getEventType() {
        return this.eventProvider.getCurrentNode().getEventType();
    }
    
    @Override
    public String getLocalName() {
        final int eventType = this.getEventType();
        if (eventType != 1 && eventType != 2 && eventType != 9) {
            throw new IllegalArgumentException("Parser must be on START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE to read local name.");
        }
        return this.eventProvider.getCurrentNode().getName().getLocalPart();
    }
    
    @Override
    public Location getLocation() {
        return this.eventProvider.getCurrentNode().getLocation();
    }
    
    @Override
    public QName getName() {
        final int eventType = this.getEventType();
        if (eventType != 1 && eventType != 2) {
            throw new IllegalArgumentException("Parser must be on START_ELEMENT or END_ELEMENT to read the name.");
        }
        return this.eventProvider.getCurrentNode().getName();
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }
    
    @Override
    public int getNamespaceCount() {
        return this.namespaceContext.getNamespaceCount();
    }
    
    @Override
    public String getNamespacePrefix(final int index) {
        return null;
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        return null;
    }
    
    @Override
    public String getNamespaceURI(final int index) {
        return null;
    }
    
    @Override
    public String getNamespaceURI() {
        final int eventType = this.getEventType();
        if (eventType != 1 && eventType != 2) {
            throw new IllegalArgumentException("Parser must be on START_ELEMENT or END_ELEMENT to read the namespace URI.");
        }
        return this.eventProvider.getCurrentNode().getName().getNamespaceURI();
    }
    
    @Override
    public String getPIData() {
        return null;
    }
    
    @Override
    public String getPITarget() {
        return null;
    }
    
    @Override
    public String getPrefix() {
        return this.eventProvider.getCurrentNode().getPrefix();
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Name is null.");
        }
        return null;
    }
    
    @Override
    public String getText() {
        final int eventType = this.getEventType();
        if (eventType == 4 || eventType == 12 || eventType == 6 || eventType == 9) {
            return this.eventProvider.getCurrentNode().getText();
        }
        throw new IllegalArgumentException("Parser must be on CHARACTERS, CDATA, SPACE or ENTITY_REFERENCE to read text.");
    }
    
    @Override
    public char[] getTextCharacters() {
        final String text = this.getText();
        return (text != null) ? text.toCharArray() : new char[0];
    }
    
    @Override
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        this.getText().getChars(sourceStart, sourceStart + length, target, targetStart);
        return length;
    }
    
    @Override
    public int getTextLength() {
        final String text = this.getText();
        return (text == null) ? 0 : text.length();
    }
    
    @Override
    public int getTextStart() {
        return 0;
    }
    
    @Override
    public String getVersion() {
        return null;
    }
    
    @Override
    public boolean hasName() {
        final int eventType = this.getEventType();
        if (eventType != 1 && eventType != 2) {
            throw new IllegalArgumentException("Parser must be on START_ELEMENT or END_ELEMENT to read the name.");
        }
        return this.eventProvider.getCurrentNode().getName() != null;
    }
    
    @Override
    public boolean hasNext() throws XMLStreamException {
        if (this.validationException != null) {
            throw this.validationException;
        }
        return this.eventProvider.getCurrentNode().getEventType() != 8;
    }
    
    @Override
    public boolean hasText() {
        final int eventType = this.getEventType();
        return eventType == 4 || eventType == 12 || eventType == 6 || eventType == 9 || eventType == 5 || eventType == 11;
    }
    
    @Override
    public boolean isAttributeSpecified(final int index) {
        return false;
    }
    
    @Override
    public boolean isCharacters() {
        return this.eventProvider.getCurrentNode().getEventType() == 4;
    }
    
    @Override
    public boolean isEndElement() {
        return this.eventProvider.getCurrentNode().getEventType() == 2;
    }
    
    @Override
    public boolean isStandalone() {
        return false;
    }
    
    @Override
    public boolean isStartElement() {
        return this.eventProvider.getCurrentNode().getEventType() == 1;
    }
    
    @Override
    public boolean isWhiteSpace() {
        return false;
    }
    
    @Override
    public int next() throws XMLStreamException {
        if (!this.hasNext()) {
            throw new IllegalArgumentException("No more parsing elements.");
        }
        return this.eventProvider.readNext().getEventType();
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        int eventType;
        for (eventType = this.next(); (eventType == 4 && this.isWhiteSpace()) || (eventType == 12 && this.isWhiteSpace()) || eventType == 6 || eventType == 3 || eventType == 5; eventType = this.next()) {}
        if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException("Expected start or end tag.", this.getLocation());
        }
        return eventType;
    }
    
    @Override
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
    }
    
    @Override
    public boolean standaloneSet() {
        return false;
    }
}
