// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.validation.Schema;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.bind.JAXBElement;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import java.net.URL;
import java.io.Reader;
import java.io.InputStream;
import java.io.File;
import javax.xml.bind.JAXBException;
import com.sun.jersey.api.json.JSONConfiguration;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class JSONUnmarshallerImpl extends BaseJSONUnmarshaller implements Unmarshaller
{
    public JSONUnmarshallerImpl(final JAXBContext jaxbContext, final JSONConfiguration jsonConfig) throws JAXBException {
        super(jaxbContext, jsonConfig);
    }
    
    @Override
    public Object unmarshal(final File file) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(file);
    }
    
    @Override
    public Object unmarshal(final InputStream inputStream) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(inputStream);
    }
    
    @Override
    public Object unmarshal(final Reader reader) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(reader);
    }
    
    @Override
    public Object unmarshal(final URL url) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(url);
    }
    
    @Override
    public Object unmarshal(final InputSource inputSource) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(inputSource);
    }
    
    @Override
    public Object unmarshal(final Node node) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(node);
    }
    
    @Override
    public <T> JAXBElement<T> unmarshal(final Node node, final Class<T> type) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(node, type);
    }
    
    @Override
    public Object unmarshal(final Source source) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(source);
    }
    
    @Override
    public <T> JAXBElement<T> unmarshal(final Source source, final Class<T> type) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(source, type);
    }
    
    @Override
    public Object unmarshal(final XMLStreamReader xmlStreamReader) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(xmlStreamReader);
    }
    
    @Override
    public <T> JAXBElement<T> unmarshal(final XMLStreamReader xmlStreamReader, final Class<T> type) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(xmlStreamReader, type);
    }
    
    @Override
    public Object unmarshal(final XMLEventReader xmlEventReader) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(xmlEventReader);
    }
    
    @Override
    public <T> JAXBElement<T> unmarshal(final XMLEventReader xmlEventReader, final Class<T> type) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(xmlEventReader, type);
    }
    
    @Override
    public UnmarshallerHandler getUnmarshallerHandler() {
        return this.jaxbUnmarshaller.getUnmarshallerHandler();
    }
    
    @Override
    public void setValidating(final boolean validating) throws JAXBException {
        this.jaxbUnmarshaller.setValidating(validating);
    }
    
    @Override
    public boolean isValidating() throws JAXBException {
        return this.jaxbUnmarshaller.isValidating();
    }
    
    @Override
    public void setEventHandler(final ValidationEventHandler validationEventHandler) throws JAXBException {
        this.jaxbUnmarshaller.setEventHandler(validationEventHandler);
    }
    
    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return this.jaxbUnmarshaller.getEventHandler();
    }
    
    @Override
    public void setProperty(final String key, final Object value) throws PropertyException {
        this.jaxbUnmarshaller.setProperty(key, value);
    }
    
    @Override
    public Object getProperty(final String key) throws PropertyException {
        return this.jaxbUnmarshaller.getProperty(key);
    }
    
    @Override
    public void setSchema(final Schema schema) {
        this.jaxbUnmarshaller.setSchema(schema);
    }
    
    @Override
    public Schema getSchema() {
        return this.jaxbUnmarshaller.getSchema();
    }
    
    @Override
    public void setAdapter(final XmlAdapter xmlAdapter) {
        this.jaxbUnmarshaller.setAdapter(xmlAdapter);
    }
    
    @Override
    public <A extends XmlAdapter> void setAdapter(final Class<A> type, final A adapter) {
        this.jaxbUnmarshaller.setAdapter(type, adapter);
    }
    
    @Override
    public <A extends XmlAdapter> A getAdapter(final Class<A> type) {
        return this.jaxbUnmarshaller.getAdapter(type);
    }
    
    @Override
    public void setAttachmentUnmarshaller(final AttachmentUnmarshaller attachmentUnmarshaller) {
        this.jaxbUnmarshaller.setAttachmentUnmarshaller(attachmentUnmarshaller);
    }
    
    @Override
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return this.jaxbUnmarshaller.getAttachmentUnmarshaller();
    }
    
    @Override
    public void setListener(final Listener listener) {
        this.jaxbUnmarshaller.setListener(listener);
    }
    
    @Override
    public Listener getListener() {
        return this.jaxbUnmarshaller.getListener();
    }
}
