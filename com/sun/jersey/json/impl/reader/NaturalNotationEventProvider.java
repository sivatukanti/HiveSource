// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import com.sun.jersey.json.impl.DefaultJaxbXmlDocumentStructure;
import javax.xml.bind.JAXBContext;
import com.sun.jersey.api.json.JSONConfiguration;
import org.codehaus.jackson.JsonParser;
import com.sun.jersey.json.impl.JaxbXmlDocumentStructure;

public class NaturalNotationEventProvider extends XmlEventProvider
{
    private final boolean attrsWithPrefix;
    private JaxbXmlDocumentStructure documentStructure;
    
    public NaturalNotationEventProvider(final JsonParser parser, final JSONConfiguration configuration, final String rootName, final JAXBContext jaxbContext, final Class<?> expectedType) throws XMLStreamException {
        super(parser, configuration, rootName);
        this.documentStructure = DefaultJaxbXmlDocumentStructure.getXmlDocumentStructure(jaxbContext, expectedType, true);
        this.attrsWithPrefix = configuration.isUsingPrefixesAtNaturalAttributes();
    }
    
    private QName getFieldQName(final String jsonFieldName, final boolean isAttribute) {
        QName result = isAttribute ? this.documentStructure.getExpectedAttributesMap().get(jsonFieldName) : this.documentStructure.getExpectedElementsMap().get(jsonFieldName);
        if (isAttribute && "type".equals(jsonFieldName)) {
            result = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
        }
        return (result == null) ? new QName(jsonFieldName) : result;
    }
    
    @Override
    protected String getAttributeName(final String jsonFieldName) {
        return this.attrsWithPrefix ? super.getAttributeName(jsonFieldName) : jsonFieldName;
    }
    
    @Override
    protected QName getAttributeQName(final String jsonFieldName) {
        return this.getFieldQName(this.getAttributeName(jsonFieldName), true);
    }
    
    @Override
    protected QName getElementQName(final String jsonFieldName) {
        return this.getFieldQName(jsonFieldName, false);
    }
    
    @Override
    protected boolean isAttribute(final String jsonFieldName) {
        final String attributeName = this.getAttributeName(jsonFieldName);
        if (!"$".equals(attributeName)) {
            if (this.documentStructure.canHandleAttributes()) {
                if (this.documentStructure.getExpectedAttributesMap().containsKey(attributeName)) {
                    return true;
                }
            }
            else if (!this.documentStructure.getExpectedElementsMap().containsKey(attributeName)) {
                return true;
            }
        }
        if (jsonFieldName.equals(attributeName)) {
            return false;
        }
        return true;
    }
    
    @Override
    protected JsonXmlEvent createEndElementEvent(final QName elementName, final Location location) {
        this.documentStructure.endElement(elementName);
        return super.createEndElementEvent(elementName, location);
    }
    
    @Override
    protected JsonXmlEvent createStartElementEvent(final QName elementName, final Location location) {
        this.documentStructure.startElement(elementName);
        return super.createStartElementEvent(elementName, location);
    }
}
