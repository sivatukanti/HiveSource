// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import java.util.Map;

public class JaxbJdkXmlStructure extends DefaultJaxbXmlDocumentStructure
{
    private static final ClassLoader systemClassLoader;
    private Map<String, QName> qNamesOfExpElems;
    private Map<String, QName> qNamesOfExpAttrs;
    private LinkedList<NodeWrapper> processedNodes;
    private final boolean isReader;
    
    public JaxbJdkXmlStructure(final JAXBContext jaxbContext, final Class<?> expectedType, final boolean isReader) {
        super(jaxbContext, expectedType, isReader);
        this.qNamesOfExpElems = new HashMap<String, QName>();
        this.qNamesOfExpAttrs = new HashMap<String, QName>();
        this.processedNodes = new LinkedList<NodeWrapper>();
        this.isReader = isReader;
    }
    
    private Collection<QName> getExpectedEntities(final String methodName) {
        try {
            final Class<?> aClass = JaxbJdkXmlStructure.systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext");
            final Object getInstance = aClass.getMethod("getInstance", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
            final Object getCurrentExpectedElements = aClass.getMethod(methodName, (Class<?>[])new Class[0]).invoke(getInstance, new Object[0]);
            return (Collection<QName>)getCurrentExpectedElements;
        }
        catch (NullPointerException npe) {}
        catch (Exception ex) {}
        return (Collection<QName>)Collections.emptyList();
    }
    
    @Override
    public Collection<QName> getExpectedElements() {
        return this.getExpectedEntities("getCurrentExpectedElements");
    }
    
    @Override
    public Collection<QName> getExpectedAttributes() {
        if (this.canHandleAttributes()) {
            return this.getExpectedEntities("getCurrentExpectedAttributes");
        }
        return (Collection<QName>)Collections.emptyList();
    }
    
    @Override
    public Map<String, QName> getExpectedElementsMap() {
        final Collection<QName> expectedElements = this.getExpectedElements();
        if (!expectedElements.isEmpty()) {
            this.qNamesOfExpElems = this.qnameCollectionToMap(expectedElements, true);
        }
        return this.qNamesOfExpElems;
    }
    
    @Override
    public Map<String, QName> getExpectedAttributesMap() {
        final Collection<QName> expectedAttributes = this.getExpectedAttributes();
        if (!expectedAttributes.isEmpty()) {
            this.qNamesOfExpAttrs = this.qnameCollectionToMap(expectedAttributes, false);
        }
        return this.qNamesOfExpAttrs;
    }
    
    @Override
    public boolean canHandleAttributes() {
        return JSONHelper.isNaturalNotationEnabled();
    }
    
    @Override
    public Type getEntityType(final QName entity, final boolean isAttribute) {
        final NodeWrapper peek = this.processedNodes.getLast();
        try {
            final Class<?> runtimeReferencePropertyInfo = JaxbJdkXmlStructure.systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo");
            final Object rawType = runtimeReferencePropertyInfo.getMethod("getRawType", (Class<?>[])new Class[0]).invoke(peek.runtimePropertyInfo, new Object[0]);
        }
        catch (Exception e) {
            final Object rawType = null;
        }
        Object rawType;
        return (peek.runtimePropertyInfo == null) ? null : ((Type)rawType);
    }
    
    @Override
    public Type getIndividualType() {
        final NodeWrapper peek = this.processedNodes.getLast();
        Object individualType = null;
        try {
            final Class<?> runtimeReferencePropertyInfo = JaxbJdkXmlStructure.systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo");
            final Boolean isCollection = (Boolean)runtimeReferencePropertyInfo.getMethod("isCollection", (Class<?>[])new Class[0]).invoke(peek.runtimePropertyInfo, new Object[0]);
            if (isCollection) {
                individualType = runtimeReferencePropertyInfo.getMethod("getIndividualType", (Class<?>[])new Class[0]).invoke(peek.runtimePropertyInfo, new Object[0]);
            }
        }
        catch (Exception e) {
            individualType = null;
        }
        return (peek.runtimePropertyInfo == null) ? null : ((Type)individualType);
    }
    
    @Override
    public void startElement(final QName name) {
        if (!this.isReader) {
            this.processedNodes.add(new NodeWrapper(this.processedNodes.isEmpty() ? null : this.processedNodes.getLast(), this.getCurrentElementRuntimePropertyInfo()));
        }
    }
    
    @Override
    public void handleAttribute(final QName attributeName, final String value) {
        this.startElement(attributeName);
    }
    
    private Object getCurrentElementRuntimePropertyInfo() {
        try {
            final Class<?> aClass = JaxbJdkXmlStructure.systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.runtime.XMLSerializer");
            final Object xs = aClass.getMethod("getInstance", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
            final Method getCurrentProperty = aClass.getMethod("getCurrentProperty", (Class<?>[])new Class[0]);
            final Object cp = (xs == null) ? null : getCurrentProperty.invoke(xs, new Object[0]);
            final Class<?> bClass = JaxbJdkXmlStructure.systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.runtime.property.Property");
            final Method getInfo = bClass.getMethod("getInfo", (Class<?>[])new Class[0]);
            return (cp == null) ? null : getInfo.invoke(cp, new Object[0]);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public boolean isArrayCollection() {
        Object runtimePropertyInfo = this.isReader ? null : this.getCurrentElementRuntimePropertyInfo();
        if (runtimePropertyInfo == null && !this.processedNodes.isEmpty()) {
            final NodeWrapper peek = this.processedNodes.getLast();
            runtimePropertyInfo = peek.runtimePropertyInfo;
        }
        boolean isCollection = false;
        try {
            final Class<?> runtimeReferencePropertyInfo = JaxbJdkXmlStructure.systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo");
            isCollection = (boolean)runtimeReferencePropertyInfo.getMethod("isCollection", (Class<?>[])new Class[0]).invoke(runtimePropertyInfo, new Object[0]);
        }
        catch (Exception e) {
            isCollection = false;
        }
        return runtimePropertyInfo != null && isCollection && !this.isWildcardElement(runtimePropertyInfo);
    }
    
    @Override
    public boolean isSameArrayCollection() {
        final int size = this.processedNodes.size();
        if (size >= 2) {
            final NodeWrapper last = this.processedNodes.getLast();
            final NodeWrapper beforeLast = this.processedNodes.get(size - 2);
            if (last.equals(beforeLast)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean hasSubElements() {
        if (this.isReader) {
            return !this.getExpectedElements().isEmpty();
        }
        return !this.processedNodes.isEmpty() && this.processedNodes.getLast() != this.getCurrentElementRuntimePropertyInfo();
    }
    
    @Override
    public void endElement(final QName name) {
        if (!this.isReader) {
            this.processedNodes.removeLast();
        }
    }
    
    private boolean isWildcardElement(final Object ri) {
        try {
            final Class<?> runtimeReferencePropertyInfo = JaxbJdkXmlStructure.systemClassLoader.loadClass("com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo");
            return runtimeReferencePropertyInfo.getMethod("getWildcard", (Class<?>[])new Class[0]).invoke(ri, new Object[0]) != null;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    static {
        systemClassLoader = ClassLoader.getSystemClassLoader();
    }
    
    private static class NodeWrapper
    {
        private final NodeWrapper parent;
        private final Object runtimePropertyInfo;
        
        private NodeWrapper(final NodeWrapper parent, final Object runtimePropertyInfo) {
            this.parent = parent;
            this.runtimePropertyInfo = runtimePropertyInfo;
        }
        
        @Override
        public int hashCode() {
            int hash = 13;
            hash += ((this.parent == null) ? 0 : this.parent.hashCode());
            hash += ((this.runtimePropertyInfo == null) ? 0 : this.runtimePropertyInfo.hashCode());
            return hash;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof NodeWrapper)) {
                return false;
            }
            final NodeWrapper other = (NodeWrapper)obj;
            return this.runtimePropertyInfo == other.runtimePropertyInfo && this.parent == other.parent;
        }
    }
}
