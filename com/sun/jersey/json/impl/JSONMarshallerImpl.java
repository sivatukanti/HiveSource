// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import javax.xml.validation.Schema;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.PropertyException;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import java.io.Writer;
import java.io.File;
import java.io.OutputStream;
import javax.xml.transform.Result;
import javax.xml.bind.JAXBException;
import com.sun.jersey.api.json.JSONConfiguration;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public final class JSONMarshallerImpl extends BaseJSONMarshaller implements Marshaller
{
    public JSONMarshallerImpl(final JAXBContext jaxbContext, final JSONConfiguration jsonConfig) throws JAXBException {
        super(jaxbContext, jsonConfig);
    }
    
    @Override
    public void marshal(final Object jaxbObject, final Result result) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, result);
    }
    
    @Override
    public void marshal(final Object jaxbObject, final OutputStream os) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, os);
    }
    
    @Override
    public void marshal(final Object jaxbObject, final File file) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, file);
    }
    
    @Override
    public void marshal(final Object jaxbObject, final Writer writer) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, writer);
    }
    
    @Override
    public void marshal(final Object jaxbObject, final ContentHandler handler) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, handler);
    }
    
    @Override
    public void marshal(final Object jaxbObject, final Node node) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, node);
    }
    
    @Override
    public void marshal(final Object jaxbObject, final XMLStreamWriter writer) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, writer);
    }
    
    @Override
    public void marshal(final Object jaxbObject, final XMLEventWriter writer) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, writer);
    }
    
    @Override
    public Node getNode(final Object jaxbObject) throws JAXBException {
        return this.jaxbMarshaller.getNode(jaxbObject);
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws PropertyException {
        if (name == null) {
            throw new IllegalArgumentException("Name can't be null.");
        }
        if (name.equals("com.sun.jersey.api.json.JSONMarshaller.formatted")) {
            if (!(value instanceof Boolean)) {
                throw new PropertyException("property " + name + " must be an instance of type " + "boolean, not " + value.getClass().getName());
            }
            this.jsonConfig = JSONConfiguration.createJSONConfigurationWithFormatted(this.jsonConfig, (boolean)value);
        }
        else {
            this.jaxbMarshaller.setProperty(name, value);
        }
    }
    
    @Override
    public Object getProperty(final String key) throws PropertyException {
        return this.jaxbMarshaller.getProperty(key);
    }
    
    @Override
    public void setEventHandler(final ValidationEventHandler handler) throws JAXBException {
        this.jaxbMarshaller.setEventHandler(handler);
    }
    
    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return this.jaxbMarshaller.getEventHandler();
    }
    
    @Override
    public void setAdapter(final XmlAdapter adapter) {
        this.jaxbMarshaller.setAdapter(adapter);
    }
    
    @Override
    public <A extends XmlAdapter> void setAdapter(final Class<A> type, final A adapter) {
        this.jaxbMarshaller.setAdapter(type, adapter);
    }
    
    @Override
    public <A extends XmlAdapter> A getAdapter(final Class<A> type) {
        return this.jaxbMarshaller.getAdapter(type);
    }
    
    @Override
    public void setAttachmentMarshaller(final AttachmentMarshaller marshaller) {
        this.jaxbMarshaller.setAttachmentMarshaller(marshaller);
    }
    
    @Override
    public AttachmentMarshaller getAttachmentMarshaller() {
        return this.jaxbMarshaller.getAttachmentMarshaller();
    }
    
    @Override
    public void setSchema(final Schema schema) {
        this.jaxbMarshaller.setSchema(schema);
    }
    
    @Override
    public Schema getSchema() {
        return this.jaxbMarshaller.getSchema();
    }
    
    @Override
    public void setListener(final Listener listener) {
        this.jaxbMarshaller.setListener(listener);
    }
    
    @Override
    public Listener getListener() {
        return this.jaxbMarshaller.getListener();
    }
}
