// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.writer;

import javax.xml.stream.XMLStreamException;
import com.sun.jersey.json.impl.reader.JsonNamespaceContext;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamWriter;

public abstract class DefaultXmlStreamWriter implements XMLStreamWriter
{
    private NamespaceContext namespaceContext;
    
    public DefaultXmlStreamWriter() {
        this.namespaceContext = null;
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        if (this.namespaceContext == null) {
            this.namespaceContext = new JsonNamespaceContext();
        }
        return this.namespaceContext;
    }
    
    @Override
    public String getPrefix(final String uri) throws XMLStreamException {
        return this.getNamespaceContext().getPrefix(uri);
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        return null;
    }
    
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        this.namespaceContext = context;
    }
    
    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
    }
    
    @Override
    public void writeCData(final String data) throws XMLStreamException {
        this.writeCharacters(data);
    }
    
    @Override
    public void writeComment(final String data) throws XMLStreamException {
    }
    
    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
    }
    
    @Override
    public void writeDTD(final String dtd) throws XMLStreamException {
    }
    
    @Override
    public void writeEntityRef(final String name) throws XMLStreamException {
    }
    
    @Override
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
    }
    
    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
    }
    
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
    }
    
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
    }
}
