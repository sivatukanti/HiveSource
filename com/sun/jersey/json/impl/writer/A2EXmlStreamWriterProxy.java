// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.writer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collection;
import java.util.List;
import javax.xml.stream.XMLStreamWriter;

public class A2EXmlStreamWriterProxy implements XMLStreamWriter
{
    XMLStreamWriter underlyingWriter;
    List<String> attr2ElemNames;
    List<XmlAttribute> unwrittenAttrs;
    
    public A2EXmlStreamWriterProxy(final XMLStreamWriter writer, final Collection<String> attr2ElemNames) {
        this.unwrittenAttrs = null;
        this.underlyingWriter = writer;
        (this.attr2ElemNames = new LinkedList<String>()).addAll(attr2ElemNames);
    }
    
    private void flushUnwrittenAttrs() throws XMLStreamException {
        if (this.unwrittenAttrs != null) {
            for (final XmlAttribute a : this.unwrittenAttrs) {
                this.underlyingWriter.writeStartElement(a.prefix, a.localName, a.namespaceUri);
                this.underlyingWriter.writeCharacters(a.value);
                this.underlyingWriter.writeEndElement();
            }
            this.unwrittenAttrs = null;
        }
    }
    
    @Override
    public void writeStartElement(final String arg0) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeStartElement(arg0);
    }
    
    @Override
    public void writeStartElement(final String arg0, final String arg1) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeStartElement(arg0, arg1);
    }
    
    @Override
    public void writeStartElement(final String arg0, final String arg1, final String arg2) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeStartElement(arg0, arg1, arg2);
    }
    
    @Override
    public void writeEmptyElement(final String arg0, final String arg1) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeEmptyElement(arg0, arg1);
    }
    
    @Override
    public void writeEmptyElement(final String arg0, final String arg1, final String arg2) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeEmptyElement(arg0, arg1, arg2);
    }
    
    @Override
    public void writeEmptyElement(final String arg0) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeEmptyElement(arg0);
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeEndElement();
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        this.underlyingWriter.writeEndDocument();
    }
    
    @Override
    public void close() throws XMLStreamException {
        this.underlyingWriter.close();
    }
    
    @Override
    public void flush() throws XMLStreamException {
        this.underlyingWriter.flush();
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
        if (!this.attr2ElemNames.contains(localName)) {
            this.underlyingWriter.writeAttribute(prefix, namespaceURI, localName, value);
        }
        else {
            if (this.unwrittenAttrs == null) {
                this.unwrittenAttrs = new LinkedList<XmlAttribute>();
            }
            this.unwrittenAttrs.add(new XmlAttribute(prefix, namespaceURI, localName, value));
        }
    }
    
    @Override
    public void writeNamespace(final String arg0, final String arg1) throws XMLStreamException {
        this.underlyingWriter.writeNamespace(arg0, arg1);
    }
    
    @Override
    public void writeDefaultNamespace(final String arg0) throws XMLStreamException {
        this.underlyingWriter.writeDefaultNamespace(arg0);
    }
    
    @Override
    public void writeComment(final String arg0) throws XMLStreamException {
        this.underlyingWriter.writeComment(arg0);
    }
    
    @Override
    public void writeProcessingInstruction(final String arg0) throws XMLStreamException {
        this.underlyingWriter.writeProcessingInstruction(arg0);
    }
    
    @Override
    public void writeProcessingInstruction(final String arg0, final String arg1) throws XMLStreamException {
        this.underlyingWriter.writeProcessingInstruction(arg0, arg1);
    }
    
    @Override
    public void writeCData(final String arg0) throws XMLStreamException {
        this.underlyingWriter.writeCData(arg0);
    }
    
    @Override
    public void writeDTD(final String arg0) throws XMLStreamException {
        this.underlyingWriter.writeDTD(arg0);
    }
    
    @Override
    public void writeEntityRef(final String arg0) throws XMLStreamException {
        this.underlyingWriter.writeEntityRef(arg0);
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.underlyingWriter.writeStartDocument();
    }
    
    @Override
    public void writeStartDocument(final String arg0) throws XMLStreamException {
        this.underlyingWriter.writeStartDocument(arg0);
    }
    
    @Override
    public void writeStartDocument(final String arg0, final String arg1) throws XMLStreamException {
        this.underlyingWriter.writeStartDocument(arg0, arg1);
    }
    
    @Override
    public void writeCharacters(final String arg0) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeCharacters(arg0);
    }
    
    @Override
    public void writeCharacters(final char[] arg0, final int arg1, final int arg2) throws XMLStreamException {
        this.flushUnwrittenAttrs();
        this.underlyingWriter.writeCharacters(arg0, arg1, arg2);
    }
    
    @Override
    public String getPrefix(final String arg0) throws XMLStreamException {
        return this.underlyingWriter.getPrefix(arg0);
    }
    
    @Override
    public void setPrefix(final String arg0, final String arg1) throws XMLStreamException {
        this.underlyingWriter.setPrefix(arg0, arg1);
    }
    
    @Override
    public void setDefaultNamespace(final String arg0) throws XMLStreamException {
        this.underlyingWriter.setDefaultNamespace(arg0);
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext arg0) throws XMLStreamException {
        this.underlyingWriter.setNamespaceContext(arg0);
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.underlyingWriter.getNamespaceContext();
    }
    
    @Override
    public Object getProperty(final String arg0) throws IllegalArgumentException {
        return this.underlyingWriter.getProperty(arg0);
    }
    
    private class XmlAttribute
    {
        String prefix;
        String namespaceUri;
        String localName;
        String value;
        
        XmlAttribute(final String prefix, final String nsUri, final String localName, final String value) {
            this.prefix = prefix;
            this.namespaceUri = nsUri;
            this.localName = localName;
            this.value = value;
        }
    }
}
