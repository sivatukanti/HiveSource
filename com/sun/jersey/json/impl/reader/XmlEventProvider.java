// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import java.util.Queue;
import org.codehaus.jackson.JsonLocation;
import java.util.List;
import org.codehaus.jackson.JsonToken;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.util.logging.Level;
import java.util.LinkedList;
import org.codehaus.jackson.JsonParser;
import java.util.Stack;
import java.util.Deque;
import com.sun.jersey.api.json.JSONConfiguration;
import java.util.logging.Logger;

public abstract class XmlEventProvider
{
    private static final Logger LOGGER;
    private final JSONConfiguration configuration;
    private final CachedJsonParser parser;
    private final String rootName;
    private final Deque<JsonXmlEvent> eventQueue;
    private final Stack<ProcessingInfo> processingStack;
    
    protected XmlEventProvider(final JsonParser parser, final JSONConfiguration configuration, final String rootName) throws XMLStreamException {
        this.eventQueue = new LinkedList<JsonXmlEvent>();
        this.processingStack = new Stack<ProcessingInfo>();
        this.parser = new CachedJsonParser(parser);
        this.configuration = configuration;
        this.rootName = rootName;
        try {
            this.readNext();
        }
        catch (XMLStreamException ex) {
            XmlEventProvider.LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            throw new XMLStreamException(ex);
        }
    }
    
    void close() throws XMLStreamException {
        this.eventQueue.clear();
        this.processingStack.empty();
        try {
            this.parser.close();
        }
        catch (IOException ioe) {
            throw new XMLStreamException(ioe);
        }
    }
    
    protected JsonXmlEvent createEndElementEvent(final QName elementName, final Location location) {
        return new EndElementEvent(elementName, location);
    }
    
    protected JsonXmlEvent createStartElementEvent(final QName elementName, final Location location) {
        return new StartElementEvent(elementName, location);
    }
    
    protected String getAttributeName(final String jsonFieldName) {
        return ('@' == jsonFieldName.charAt(0)) ? jsonFieldName.substring(1) : jsonFieldName;
    }
    
    protected abstract QName getAttributeQName(final String p0);
    
    JsonXmlEvent getCurrentNode() {
        return this.eventQueue.peek();
    }
    
    protected abstract QName getElementQName(final String p0);
    
    protected JSONConfiguration getJsonConfiguration() {
        return this.configuration;
    }
    
    private String getPrimitiveFieldValue(final JsonToken jsonToken, final String jsonFieldValue) throws IOException {
        if (jsonToken == JsonToken.VALUE_FALSE || jsonToken == JsonToken.VALUE_TRUE || jsonToken == JsonToken.VALUE_STRING || jsonToken == JsonToken.VALUE_NUMBER_FLOAT || jsonToken == JsonToken.VALUE_NUMBER_INT || jsonToken == JsonToken.VALUE_NULL) {
            return jsonFieldValue;
        }
        throw new IOException("Not an XML value, expected primitive value!");
    }
    
    protected abstract boolean isAttribute(final String p0);
    
    void processAttributesOfCurrentElement() throws XMLStreamException {
        this.eventQueue.peek().setAttributes(new LinkedList<JsonXmlEvent.Attribute>());
        this.processTokens(true);
    }
    
    private JsonXmlEvent processTokens(boolean processAttributes) throws XMLStreamException {
        if (!processAttributes) {
            this.eventQueue.poll();
        }
        try {
            if (!this.eventQueue.isEmpty() && !processAttributes) {
                return this.eventQueue.peek();
            }
            while (true) {
                final JsonToken jsonToken = this.parser.nextToken();
                final ProcessingInfo pi = this.processingStack.isEmpty() ? null : this.processingStack.peek();
                if (jsonToken == null) {
                    return this.getCurrentNode();
                }
                switch (jsonToken) {
                    case FIELD_NAME: {
                        final String fieldName = this.parser.getCurrentName();
                        if (this.isAttribute(fieldName)) {
                            final QName attributeName = this.getAttributeQName(fieldName);
                            final String attributeValue = this.getPrimitiveFieldValue(this.parser.nextToken(), this.parser.getText());
                            this.eventQueue.peek().getAttributes().add(new JsonXmlEvent.Attribute(attributeName, attributeValue));
                            continue;
                        }
                        processAttributes = false;
                        if ("$".equals(fieldName)) {
                            final String value = this.getPrimitiveFieldValue(this.parser.nextToken(), this.parser.getText());
                            this.eventQueue.add(new CharactersEvent(value, new StaxLocation(this.parser.getCurrentLocation())));
                            continue;
                        }
                        final QName elementName = this.getElementQName(fieldName);
                        final JsonLocation currentLocation = this.parser.getCurrentLocation();
                        final boolean isRootEmpty = this.isEmptyElement(fieldName, true);
                        if (isRootEmpty) {
                            this.eventQueue.add(this.createStartElementEvent(elementName, new StaxLocation(currentLocation)));
                            this.eventQueue.add(this.createEndElementEvent(elementName, new StaxLocation(currentLocation)));
                            this.eventQueue.add(new EndDocumentEvent(new StaxLocation(this.parser.getCurrentLocation())));
                        }
                        else {
                            if (!this.isEmptyArray() && !this.isEmptyElement(fieldName, false)) {
                                this.eventQueue.add(this.createStartElementEvent(elementName, new StaxLocation(currentLocation)));
                                this.processingStack.add(new ProcessingInfo(elementName, false, true));
                            }
                            if (!this.parser.hasMoreTokens()) {
                                this.eventQueue.add(new EndDocumentEvent(new StaxLocation(this.parser.getCurrentLocation())));
                            }
                        }
                        if (this.eventQueue.isEmpty()) {
                            continue;
                        }
                        return this.getCurrentNode();
                    }
                    case START_OBJECT: {
                        if (pi == null) {
                            this.eventQueue.add(new StartDocumentEvent(new StaxLocation(0, 0, 0)));
                            return this.getCurrentNode();
                        }
                        if (pi.isArray && !pi.isFirstElement) {
                            this.eventQueue.add(this.createStartElementEvent(pi.name, new StaxLocation(this.parser.getCurrentLocation())));
                            return this.getCurrentNode();
                        }
                        pi.isFirstElement = false;
                        continue;
                    }
                    case END_OBJECT: {
                        processAttributes = false;
                        this.eventQueue.add(this.createEndElementEvent(pi.name, new StaxLocation(this.parser.getCurrentLocation())));
                        if (!pi.isArray) {
                            this.processingStack.pop();
                        }
                        if (this.processingStack.isEmpty()) {
                            this.eventQueue.add(new EndDocumentEvent(new StaxLocation(this.parser.getCurrentLocation())));
                            final JsonToken nextToken = this.parser.nextToken();
                            if ((nextToken != null && nextToken != JsonToken.END_OBJECT) || this.parser.peek() != null) {
                                throw new RuntimeException("Unexpected token: " + this.parser.getText());
                            }
                        }
                        return this.getCurrentNode();
                    }
                    case VALUE_FALSE:
                    case VALUE_NULL:
                    case VALUE_NUMBER_FLOAT:
                    case VALUE_NUMBER_INT:
                    case VALUE_TRUE:
                    case VALUE_STRING: {
                        if (!pi.isFirstElement) {
                            this.eventQueue.add(this.createStartElementEvent(pi.name, new StaxLocation(this.parser.getCurrentLocation())));
                        }
                        else {
                            pi.isFirstElement = false;
                        }
                        if (jsonToken != JsonToken.VALUE_NULL) {
                            this.eventQueue.add(new CharactersEvent(this.parser.getText(), new StaxLocation(this.parser.getCurrentLocation())));
                        }
                        this.eventQueue.add(new EndElementEvent(pi.name, new StaxLocation(this.parser.getCurrentLocation())));
                        if (!pi.isArray) {
                            this.processingStack.pop();
                        }
                        if (this.processingStack.isEmpty()) {
                            this.eventQueue.add(new EndDocumentEvent(new StaxLocation(this.parser.getCurrentLocation())));
                        }
                        processAttributes = false;
                        return this.getCurrentNode();
                    }
                    case START_ARRAY: {
                        this.processingStack.peek().isArray = true;
                        continue;
                    }
                    case END_ARRAY: {
                        this.processingStack.pop();
                        processAttributes = false;
                        continue;
                    }
                    default: {
                        throw new IllegalStateException("Unknown JSON token: " + jsonToken);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new XMLStreamException(e);
        }
    }
    
    private boolean isEmptyArray() throws IOException {
        final JsonToken jsonToken = this.parser.peek();
        if (jsonToken == JsonToken.START_ARRAY && this.parser.peekNext() == JsonToken.END_ARRAY) {
            this.parser.poll();
            this.parser.poll();
            return true;
        }
        return false;
    }
    
    private boolean isEmptyElement(final String fieldName, final boolean checkRoot) throws IOException {
        if (!checkRoot || (fieldName != null && fieldName.equals(this.rootName))) {
            final JsonToken jsonToken = this.parser.peek();
            if (jsonToken == JsonToken.VALUE_NULL) {
                this.parser.poll();
                return true;
            }
        }
        return false;
    }
    
    JsonXmlEvent readNext() throws XMLStreamException {
        return this.processTokens(false);
    }
    
    static {
        LOGGER = Logger.getLogger(XmlEventProvider.class.getName());
    }
    
    private static class ProcessingInfo
    {
        QName name;
        boolean isArray;
        boolean isFirstElement;
        
        ProcessingInfo(final QName name, final boolean isArray, final boolean isFirstElement) {
            this.name = name;
            this.isArray = isArray;
            this.isFirstElement = isFirstElement;
        }
    }
    
    private static class CachedJsonParser
    {
        private final JsonParser parser;
        private final Queue<JsonToken> tokens;
        
        public CachedJsonParser(final JsonParser parser) {
            this.tokens = new LinkedList<JsonToken>();
            this.parser = parser;
        }
        
        public JsonToken nextToken() throws IOException {
            return this.tokens.isEmpty() ? this.parser.nextToken() : this.tokens.poll();
        }
        
        public JsonToken peekNext() throws IOException {
            final JsonToken jsonToken = this.parser.nextToken();
            this.tokens.add(jsonToken);
            return jsonToken;
        }
        
        public JsonToken peek() throws IOException {
            if (this.tokens.isEmpty()) {
                this.tokens.add(this.parser.nextToken());
            }
            return this.tokens.peek();
        }
        
        public JsonToken poll() throws IOException {
            return this.tokens.poll();
        }
        
        public void close() throws IOException {
            this.parser.close();
        }
        
        public JsonLocation getCurrentLocation() {
            return this.parser.getCurrentLocation();
        }
        
        public String getText() throws IOException {
            return this.parser.getText();
        }
        
        public String getCurrentName() throws IOException {
            return this.parser.getCurrentName();
        }
        
        public boolean hasMoreTokens() throws IOException {
            try {
                return this.peek() != null;
            }
            catch (IOException e) {
                return false;
            }
        }
    }
}
