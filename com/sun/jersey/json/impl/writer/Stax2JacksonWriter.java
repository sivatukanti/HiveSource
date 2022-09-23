// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.writer;

import java.util.Arrays;
import java.util.HashSet;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.lang.reflect.ParameterizedType;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import javax.xml.namespace.QName;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import com.sun.jersey.json.impl.DefaultJaxbXmlDocumentStructure;
import java.util.ArrayList;
import com.sun.jersey.api.json.JSONConfiguration;
import javax.xml.bind.JAXBContext;
import org.codehaus.jackson.JsonGenerator;
import java.util.Set;
import java.lang.reflect.Type;
import com.sun.jersey.json.impl.JaxbXmlDocumentStructure;
import java.util.List;
import javax.xml.stream.XMLStreamWriter;

public class Stax2JacksonWriter extends DefaultXmlStreamWriter implements XMLStreamWriter
{
    private final boolean attrsWithPrefix;
    static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
    JacksonStringMergingGenerator generator;
    final List<ProcessingInfo> processingStack;
    boolean writingAttr;
    private JaxbXmlDocumentStructure documentStructure;
    static final Type[] _pt;
    static final Type[] _nst;
    static final Set<Type> primitiveTypes;
    static final Set<Type> nonStringTypes;
    
    static <T> T pop(final List<T> stack) {
        return stack.remove(stack.size() - 1);
    }
    
    static <T> T peek(final List<T> stack) {
        return (stack.size() > 0) ? stack.get(stack.size() - 1) : null;
    }
    
    static <T> T peek2nd(final List<T> stack) {
        return (stack.size() > 1) ? stack.get(stack.size() - 2) : null;
    }
    
    public Stax2JacksonWriter(final JsonGenerator generator, final Class<?> expectedType, final JAXBContext jaxbContext) {
        this(generator, JSONConfiguration.DEFAULT, expectedType, jaxbContext);
    }
    
    public Stax2JacksonWriter(final JsonGenerator generator, final JSONConfiguration config, final Class<?> expectedType, final JAXBContext jaxbContext) {
        this.processingStack = new ArrayList<ProcessingInfo>();
        this.writingAttr = false;
        this.attrsWithPrefix = config.isUsingPrefixesAtNaturalAttributes();
        this.generator = JacksonStringMergingGenerator.createGenerator(generator);
        this.documentStructure = DefaultJaxbXmlDocumentStructure.getXmlDocumentStructure(jaxbContext, expectedType, false);
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.writeStartElement(null, localName, null);
    }
    
    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writeStartElement(null, localName, namespaceURI);
    }
    
    private void ensureStartObjectBeforeFieldName(final ProcessingInfo pi) throws IOException {
        if (pi != null && pi.afterFN) {
            this.generator.writeStartObject();
            peek2nd(this.processingStack).startObjectWritten = true;
            pi.afterFN = false;
        }
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        try {
            if (!this.writingAttr) {
                this.pushPropInfo(namespaceURI, localName, null);
            }
            final ProcessingInfo currentPI = peek(this.processingStack);
            final ProcessingInfo parentPI = peek2nd(this.processingStack);
            if (!currentPI.isArray) {
                if (parentPI != null && parentPI.lastUnderlyingPI != null && parentPI.lastUnderlyingPI.isArray) {
                    this.generator.writeEndArray();
                    parentPI.afterFN = false;
                }
                this.ensureStartObjectBeforeFieldName(parentPI);
                this.generator.writeFieldName(localName);
                currentPI.afterFN = true;
            }
            else if (parentPI == null || !currentPI.equals(parentPI.lastUnderlyingPI)) {
                if (parentPI != null && parentPI.lastUnderlyingPI != null && parentPI.lastUnderlyingPI.isArray) {
                    this.generator.writeEndArray();
                    parentPI.afterFN = false;
                }
                this.ensureStartObjectBeforeFieldName(parentPI);
                this.generator.writeFieldName(localName);
                this.generator.writeStartArray();
                currentPI.afterFN = true;
            }
            else {
                currentPI.afterFN = true;
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }
    
    private void pushPropInfo(final String namespaceUri, final String localName, final String value) {
        final QName qname = new QName((namespaceUri == null) ? "" : namespaceUri, localName);
        if (this.writingAttr) {
            this.documentStructure.handleAttribute(qname, value);
        }
        else {
            this.documentStructure.startElement(qname);
        }
        final ProcessingInfo parentPI = peek(this.processingStack);
        final boolean sameArrayCollection = this.documentStructure.isSameArrayCollection();
        if (localName != null && parentPI != null && parentPI.lastUnderlyingPI != null && (localName.equals(parentPI.lastUnderlyingPI.elementName.getLocalPart()) || sameArrayCollection)) {
            this.processingStack.add(new ProcessingInfo(parentPI.lastUnderlyingPI));
            return;
        }
        final Type rt = this.documentStructure.getEntityType(qname, this.writingAttr);
        final Type individualType = this.documentStructure.getIndividualType();
        if (null == rt) {
            this.processingStack.add(new ProcessingInfo(qname, false, null, null));
            return;
        }
        if (Stax2JacksonWriter.primitiveTypes.contains(rt)) {
            this.processingStack.add(new ProcessingInfo(qname, false, rt, individualType));
            return;
        }
        if (this.documentStructure.isArrayCollection() && !this.writingAttr && (parentPI == null || !parentPI.isArray || !sameArrayCollection)) {
            this.processingStack.add(new ProcessingInfo(qname, true, rt, individualType));
            return;
        }
        this.processingStack.add(new ProcessingInfo(qname, false, rt, individualType));
    }
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.writeEmptyElement(null, localName, null);
    }
    
    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writeEmptyElement(null, localName, namespaceURI);
    }
    
    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.writeStartElement(prefix, localName, namespaceURI);
        this.writeEndElement();
    }
    
    private void cleanlyEndObject(final ProcessingInfo pi) throws IOException {
        if (pi.startObjectWritten) {
            this.generator.writeEndObject();
        }
        else if (pi.afterFN && pi.lastUnderlyingPI == null) {
            if (this.documentStructure.isArrayCollection() || this.documentStructure.hasSubElements()) {
                this.generator.writeStartObject();
                this.generator.writeEndObject();
            }
            else {
                this.generator.writeNull();
            }
        }
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        try {
            final ProcessingInfo removedPI = pop(this.processingStack);
            final ProcessingInfo currentPI = peek(this.processingStack);
            if (currentPI != null) {
                currentPI.lastUnderlyingPI = removedPI;
            }
            if (removedPI.lastUnderlyingPI != null && removedPI.lastUnderlyingPI.isArray) {
                this.generator.writeEndArray();
            }
            this.cleanlyEndObject(removedPI);
            this.documentStructure.endElement(removedPI.elementName);
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        try {
            this.generator.writeEndObject();
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void close() throws XMLStreamException {
        try {
            this.generator.close();
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void flush() throws XMLStreamException {
        try {
            this.generator.flush();
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.writeAttribute(null, null, localName, value);
    }
    
    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.writeAttribute(null, namespaceURI, localName, value);
    }
    
    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.writingAttr = true;
        this.pushPropInfo(namespaceURI, localName, value);
        this.writeStartElement(prefix, this.attrsWithPrefix ? ("@" + localName) : localName, namespaceURI);
        this.writingAttr = false;
        this.writeCharacters(value, "type".equals(localName) && "http://www.w3.org/2001/XMLSchema-instance".equals(namespaceURI));
        this.writeEndElement();
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.writeStartDocument(null, null);
    }
    
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
        this.writeStartDocument(null, version);
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        try {
            this.generator.writeStartObject();
        }
        catch (IOException ex) {
            if (!(ex instanceof SocketTimeoutException) && !(ex instanceof SocketException)) {
                Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, "IO exception", ex);
                throw new XMLStreamException(ex);
            }
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.FINE, "Socket excption", ex);
        }
    }
    
    private void writeCharacters(final String text, final boolean forceString) throws XMLStreamException {
        try {
            final ProcessingInfo currentPI = peek(this.processingStack);
            if (currentPI.startObjectWritten && !currentPI.afterFN) {
                this.generator.writeFieldName("$");
            }
            currentPI.afterFN = false;
            final Type valueType = this.getValueType(currentPI.rawType, currentPI.individualType);
            if (forceString || !Stax2JacksonWriter.nonStringTypes.contains(valueType)) {
                if (!currentPI.isArray) {
                    this.generator.writeStringToMerge(text);
                }
                else {
                    this.generator.writeString(text);
                }
            }
            else {
                this.writePrimitiveType(text, valueType);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }
    
    private void writePrimitiveType(final String text, final Type valueType) throws IOException {
        if (Boolean.TYPE == valueType || Boolean.class == valueType) {
            this.generator.writeBoolean(Boolean.parseBoolean(text));
        }
        else {
            this.generator.writeNumber(text);
        }
    }
    
    private Type getValueType(final Type rawType, final Type individualType) {
        if (individualType != null) {
            return individualType;
        }
        if (rawType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)rawType;
            final Type parameterizedTypeRawType = parameterizedType.getRawType();
            if (parameterizedTypeRawType instanceof Class && Collection.class.isAssignableFrom((Class<?>)parameterizedTypeRawType)) {
                final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                    return actualTypeArguments[0];
                }
            }
        }
        return rawType;
    }
    
    @Override
    public void writeCharacters(final String text) throws XMLStreamException {
        this.writeCharacters(text, false);
    }
    
    @Override
    public void writeCharacters(final char[] text, final int start, final int length) throws XMLStreamException {
        this.writeCharacters(new String(text, start, length));
    }
    
    static {
        _pt = new Type[] { Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Boolean.TYPE, Character.TYPE, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, Character.class, String.class };
        _nst = new Type[] { Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Boolean.TYPE, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, BigInteger.class, BigDecimal.class };
        primitiveTypes = new HashSet<Type>() {
            {
                this.addAll(Arrays.asList(Stax2JacksonWriter._pt));
            }
        };
        nonStringTypes = new HashSet<Type>() {
            {
                this.addAll(Arrays.asList(Stax2JacksonWriter._nst));
            }
        };
    }
    
    private static class ProcessingInfo
    {
        boolean isArray;
        Type rawType;
        Type individualType;
        ProcessingInfo lastUnderlyingPI;
        boolean startObjectWritten;
        boolean afterFN;
        QName elementName;
        
        public ProcessingInfo(final QName elementName, final boolean isArray, final Type rawType, final Type individualType) {
            this.startObjectWritten = false;
            this.afterFN = false;
            this.elementName = elementName;
            this.isArray = isArray;
            this.rawType = rawType;
            this.individualType = individualType;
        }
        
        public ProcessingInfo(final ProcessingInfo pi) {
            this(pi.elementName, pi.isArray, pi.rawType, pi.individualType);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final ProcessingInfo other = (ProcessingInfo)obj;
            return this.isArray == other.isArray && (this.elementName == other.elementName || (this.elementName != null && this.elementName.equals(other.elementName))) && (this.rawType == other.rawType || (this.rawType != null && this.rawType.equals(other.rawType))) && (this.individualType == other.individualType || (this.individualType != null && this.individualType.equals(other.individualType)));
        }
        
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 47 * hash + (this.isArray ? 1 : 0);
            hash = 47 * hash + ((this.elementName != null) ? this.elementName.hashCode() : 0);
            hash = 47 * hash + ((this.rawType != null) ? this.rawType.hashCode() : 0);
            hash = 47 * hash + ((this.individualType != null) ? this.individualType.hashCode() : 0);
            return hash;
        }
    }
}
