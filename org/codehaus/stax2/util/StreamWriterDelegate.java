// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StreamWriterDelegate implements XMLStreamWriter
{
    protected XMLStreamWriter mDelegate;
    
    public StreamWriterDelegate(final XMLStreamWriter mDelegate) {
        this.mDelegate = mDelegate;
    }
    
    public void setParent(final XMLStreamWriter mDelegate) {
        this.mDelegate = mDelegate;
    }
    
    public XMLStreamWriter getParent() {
        return this.mDelegate;
    }
    
    public void close() throws XMLStreamException {
        this.mDelegate.close();
    }
    
    public void flush() throws XMLStreamException {
        this.mDelegate.flush();
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.mDelegate.getNamespaceContext();
    }
    
    public String getPrefix(final String s) throws XMLStreamException {
        return this.mDelegate.getPrefix(s);
    }
    
    public Object getProperty(final String s) throws IllegalArgumentException {
        return this.mDelegate.getProperty(s);
    }
    
    public void setDefaultNamespace(final String defaultNamespace) throws XMLStreamException {
        this.mDelegate.setDefaultNamespace(defaultNamespace);
    }
    
    public void setNamespaceContext(final NamespaceContext namespaceContext) throws XMLStreamException {
        this.mDelegate.setNamespaceContext(namespaceContext);
    }
    
    public void setPrefix(final String s, final String s2) throws XMLStreamException {
        this.mDelegate.setPrefix(s, s2);
    }
    
    public void writeAttribute(final String s, final String s2) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2);
    }
    
    public void writeAttribute(final String s, final String s2, final String s3) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3);
    }
    
    public void writeAttribute(final String s, final String s2, final String s3, final String s4) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, s4);
    }
    
    public void writeCData(final String s) throws XMLStreamException {
        this.mDelegate.writeCData(s);
    }
    
    public void writeCharacters(final String s) throws XMLStreamException {
        this.mDelegate.writeCharacters(s);
    }
    
    public void writeCharacters(final char[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(array, n, n2);
    }
    
    public void writeComment(final String s) throws XMLStreamException {
        this.mDelegate.writeComment(s);
    }
    
    public void writeDTD(final String s) throws XMLStreamException {
        this.mDelegate.writeDTD(s);
    }
    
    public void writeDefaultNamespace(final String s) throws XMLStreamException {
        this.mDelegate.writeDefaultNamespace(s);
    }
    
    public void writeEmptyElement(final String s) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(s);
    }
    
    public void writeEmptyElement(final String s, final String s2) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(s, s2);
    }
    
    public void writeEmptyElement(final String s, final String s2, final String s3) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(s, s2, s3);
    }
    
    public void writeEndDocument() throws XMLStreamException {
        this.mDelegate.writeEndDocument();
    }
    
    public void writeEndElement() throws XMLStreamException {
        this.mDelegate.writeEndElement();
    }
    
    public void writeEntityRef(final String s) throws XMLStreamException {
        this.mDelegate.writeEntityRef(s);
    }
    
    public void writeNamespace(final String s, final String s2) throws XMLStreamException {
        this.mDelegate.writeNamespace(s, s2);
    }
    
    public void writeProcessingInstruction(final String s) throws XMLStreamException {
        this.mDelegate.writeProcessingInstruction(s);
    }
    
    public void writeProcessingInstruction(final String s, final String s2) throws XMLStreamException {
        this.mDelegate.writeProcessingInstruction(s, s2);
    }
    
    public void writeStartDocument() throws XMLStreamException {
        this.mDelegate.writeStartDocument();
    }
    
    public void writeStartDocument(final String s) throws XMLStreamException {
        this.mDelegate.writeStartDocument(s);
    }
    
    public void writeStartDocument(final String s, final String s2) throws XMLStreamException {
        this.mDelegate.writeStartDocument(s, s2);
    }
    
    public void writeStartElement(final String s) throws XMLStreamException {
        this.mDelegate.writeStartElement(s);
    }
    
    public void writeStartElement(final String s, final String s2) throws XMLStreamException {
        this.mDelegate.writeStartElement(s, s2);
    }
    
    public void writeStartElement(final String s, final String s2, final String s3) throws XMLStreamException {
        this.mDelegate.writeStartElement(s, s2, s3);
    }
}
