// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import javax.xml.stream.XMLStreamException;
import javax.xml.bind.UnmarshalException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.bind.JAXBElement;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import javax.xml.bind.JAXBException;
import com.sun.jersey.api.json.JSONConfiguration;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.nio.charset.Charset;
import com.sun.jersey.api.json.JSONConfigurated;
import com.sun.jersey.api.json.JSONUnmarshaller;

public class BaseJSONUnmarshaller implements JSONUnmarshaller, JSONConfigurated
{
    private static final Charset UTF8;
    protected final Unmarshaller jaxbUnmarshaller;
    private final JAXBContext jaxbContext;
    protected final JSONConfiguration jsonConfig;
    
    public BaseJSONUnmarshaller(final JAXBContext jaxbContext, final JSONConfiguration jsonConfig) throws JAXBException {
        this(jaxbContext.createUnmarshaller(), jaxbContext, jsonConfig);
    }
    
    public BaseJSONUnmarshaller(final Unmarshaller jaxbUnmarshaller, final JAXBContext jaxbContext, final JSONConfiguration jsonConfig) {
        this.jaxbUnmarshaller = jaxbUnmarshaller;
        this.jaxbContext = jaxbContext;
        this.jsonConfig = jsonConfig;
    }
    
    @Override
    public JSONConfiguration getJSONConfiguration() {
        return this.jsonConfig;
    }
    
    @Override
    public <T> T unmarshalFromJSON(final InputStream inputStream, final Class<T> expectedType) throws JAXBException {
        return this.unmarshalFromJSON(new InputStreamReader(inputStream, BaseJSONUnmarshaller.UTF8), expectedType);
    }
    
    @Override
    public <T> T unmarshalFromJSON(final Reader reader, final Class<T> expectedType) throws JAXBException {
        if (this.jsonConfig.isRootUnwrapping() || !expectedType.isAnnotationPresent(XmlRootElement.class)) {
            return this.unmarshalJAXBElementFromJSON(reader, expectedType).getValue();
        }
        return (T)this.jaxbUnmarshaller.unmarshal(this.createXmlStreamReader(reader, expectedType));
    }
    
    @Override
    public <T> JAXBElement<T> unmarshalJAXBElementFromJSON(final InputStream inputStream, final Class<T> declaredType) throws JAXBException {
        return this.unmarshalJAXBElementFromJSON(new InputStreamReader(inputStream, BaseJSONUnmarshaller.UTF8), declaredType);
    }
    
    @Override
    public <T> JAXBElement<T> unmarshalJAXBElementFromJSON(final Reader reader, final Class<T> declaredType) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(this.createXmlStreamReader(reader, declaredType), declaredType);
    }
    
    private XMLStreamReader createXmlStreamReader(final Reader reader, final Class expectedType) throws JAXBException {
        try {
            return Stax2JsonFactory.createReader(reader, this.jsonConfig, this.jsonConfig.isRootUnwrapping() ? JSONHelper.getRootElementName(expectedType) : null, expectedType, this.jaxbContext);
        }
        catch (XMLStreamException ex) {
            throw new UnmarshalException("Error creating JSON-based XMLStreamReader", ex);
        }
    }
    
    static {
        UTF8 = Charset.forName("UTF-8");
    }
}
