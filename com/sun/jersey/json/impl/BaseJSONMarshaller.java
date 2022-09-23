// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import javax.xml.bind.PropertyException;
import java.io.IOException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import com.sun.jersey.api.json.JSONConfiguration;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.nio.charset.Charset;
import com.sun.jersey.api.json.JSONConfigurated;
import com.sun.jersey.api.json.JSONMarshaller;

public class BaseJSONMarshaller implements JSONMarshaller, JSONConfigurated
{
    private static final Charset UTF8;
    protected final Marshaller jaxbMarshaller;
    private final JAXBContext jaxbContext;
    protected JSONConfiguration jsonConfig;
    
    public BaseJSONMarshaller(final JAXBContext jaxbContext, final JSONConfiguration jsonConfig) throws JAXBException {
        this(jaxbContext.createMarshaller(), jaxbContext, jsonConfig);
    }
    
    public BaseJSONMarshaller(final Marshaller jaxbMarshaller, final JAXBContext jaxbContext, final JSONConfiguration jsonConfig) {
        this.jsonConfig = jsonConfig;
        this.jaxbContext = jaxbContext;
        this.jaxbMarshaller = jaxbMarshaller;
    }
    
    @Override
    public JSONConfiguration getJSONConfiguration() {
        return this.jsonConfig;
    }
    
    @Override
    public void marshallToJSON(final Object o, final OutputStream outputStream) throws JAXBException {
        if (outputStream == null) {
            throw new IllegalArgumentException("The output stream is null");
        }
        this.marshallToJSON(o, new OutputStreamWriter(outputStream, BaseJSONMarshaller.UTF8));
    }
    
    @Override
    public void marshallToJSON(final Object o, final Writer writer) throws JAXBException {
        if (o == null) {
            throw new IllegalArgumentException("The JAXB element is null");
        }
        if (writer == null) {
            throw new IllegalArgumentException("The writer is null");
        }
        this.jaxbMarshaller.marshal(o, this.getXMLStreamWriter(writer, o.getClass()));
    }
    
    private XMLStreamWriter getXMLStreamWriter(final Writer writer, final Class<?> expectedType) throws JAXBException {
        try {
            return Stax2JsonFactory.createWriter(writer, this.jsonConfig, expectedType, this.jaxbContext);
        }
        catch (IOException ex) {
            throw new JAXBException(ex);
        }
    }
    
    @Override
    public void setProperty(final String key, final Object value) throws PropertyException {
    }
    
    static {
        UTF8 = Charset.forName("UTF-8");
    }
}
