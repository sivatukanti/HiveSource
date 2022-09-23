// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import java.util.Iterator;
import java.util.HashMap;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Collections;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.JAXBContext;

public abstract class DefaultJaxbXmlDocumentStructure implements JaxbXmlDocumentStructure
{
    public static JaxbXmlDocumentStructure getXmlDocumentStructure(final JAXBContext jaxbContext, final Class<?> expectedType, final boolean isReader) throws IllegalStateException {
        Throwable throwable = null;
        try {
            return (JaxbXmlDocumentStructure)JSONHelper.getJaxbProvider(jaxbContext).getDocumentStructureClass().getConstructor(JAXBContext.class, Class.class, Boolean.TYPE).newInstance(jaxbContext, expectedType, isReader);
        }
        catch (InvocationTargetException e) {
            throwable = e;
        }
        catch (NoSuchMethodException e2) {
            throwable = e2;
        }
        catch (InstantiationException e3) {
            throwable = e3;
        }
        catch (IllegalAccessException e4) {
            throwable = e4;
        }
        throw new IllegalStateException("Cannot create a JaxbXmlDocumentStructure instance.", throwable);
    }
    
    protected DefaultJaxbXmlDocumentStructure(final JAXBContext jaxbContext, final Class<?> expectedType, final boolean isReader) {
    }
    
    @Override
    public Collection<QName> getExpectedElements() {
        return (Collection<QName>)Collections.emptyList();
    }
    
    @Override
    public Collection<QName> getExpectedAttributes() {
        return (Collection<QName>)Collections.emptyList();
    }
    
    @Override
    public Map<String, QName> getExpectedElementsMap() {
        return this.qnameCollectionToMap(this.getExpectedElements(), true);
    }
    
    @Override
    public Map<String, QName> getExpectedAttributesMap() {
        return this.qnameCollectionToMap(this.getExpectedAttributes(), false);
    }
    
    @Override
    public void startElement(final QName name) {
    }
    
    @Override
    public void endElement(final QName name) {
    }
    
    @Override
    public boolean canHandleAttributes() {
        return true;
    }
    
    @Override
    public Type getEntityType(final QName entity, final boolean isAttribute) {
        return null;
    }
    
    @Override
    public Type getIndividualType() {
        return null;
    }
    
    @Override
    public void handleAttribute(final QName attributeName, final String value) {
    }
    
    @Override
    public boolean isArrayCollection() {
        return false;
    }
    
    @Override
    public boolean isSameArrayCollection() {
        return true;
    }
    
    protected Map<String, QName> qnameCollectionToMap(final Collection<QName> collection, final boolean elementCollection) {
        final Map<String, QName> map = new HashMap<String, QName>();
        for (final QName qname : collection) {
            final String namespaceUri = qname.getNamespaceURI();
            if (elementCollection && "\u0000".equals(namespaceUri)) {
                map.put("$", null);
            }
            else {
                map.put(qname.getLocalPart(), qname);
            }
        }
        return map;
    }
}
