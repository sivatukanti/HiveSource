// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import com.sun.xml.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.bind.v2.runtime.property.Property;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collections;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.util.Collection;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import java.util.Map;

public class JaxbRiXmlStructure extends DefaultJaxbXmlDocumentStructure
{
    private Map<String, QName> qNamesOfExpElems;
    private Map<String, QName> qNamesOfExpAttrs;
    private LinkedList<NodeWrapper> processedNodes;
    private final boolean isReader;
    
    public JaxbRiXmlStructure(final JAXBContext jaxbContext, final Class<?> expectedType, final boolean isReader) {
        super(jaxbContext, expectedType, isReader);
        this.qNamesOfExpElems = new HashMap<String, QName>();
        this.qNamesOfExpAttrs = new HashMap<String, QName>();
        this.processedNodes = new LinkedList<NodeWrapper>();
        this.isReader = isReader;
    }
    
    @Override
    public Collection<QName> getExpectedElements() {
        try {
            return UnmarshallingContext.getInstance().getCurrentExpectedElements();
        }
        catch (NullPointerException npe) {
            return (Collection<QName>)Collections.emptyList();
        }
    }
    
    @Override
    public Collection<QName> getExpectedAttributes() {
        if (JSONHelper.isNaturalNotationEnabled()) {
            try {
                return UnmarshallingContext.getInstance().getCurrentExpectedAttributes();
            }
            catch (NullPointerException npe) {}
            catch (NoSuchMethodError nsme) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ImplMessages.ERROR_JAXB_RI_2_1_12_MISSING(), nsme);
            }
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
        return (peek.runtimePropertyInfo == null) ? null : peek.runtimePropertyInfo.getRawType();
    }
    
    @Override
    public Type getIndividualType() {
        final NodeWrapper peek = this.processedNodes.getLast();
        return (peek.runtimePropertyInfo == null) ? null : (peek.runtimePropertyInfo.isCollection() ? peek.runtimePropertyInfo.getIndividualType() : null);
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
    
    private RuntimePropertyInfo getCurrentElementRuntimePropertyInfo() {
        final XMLSerializer xs = XMLSerializer.getInstance();
        final Property cp = (xs == null) ? null : xs.getCurrentProperty();
        return (cp == null) ? null : cp.getInfo();
    }
    
    @Override
    public boolean isArrayCollection() {
        RuntimePropertyInfo runtimePropertyInfo = this.isReader ? null : this.getCurrentElementRuntimePropertyInfo();
        if (runtimePropertyInfo == null && !this.processedNodes.isEmpty()) {
            final NodeWrapper peek = this.processedNodes.getLast();
            runtimePropertyInfo = peek.runtimePropertyInfo;
        }
        return runtimePropertyInfo != null && runtimePropertyInfo.isCollection() && !this.isWildcardElement(runtimePropertyInfo);
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
        final RuntimePropertyInfo rpi = this.getCurrentElementRuntimePropertyInfo();
        return !this.processedNodes.isEmpty() && (rpi == null || rpi.elementOnlyContent());
    }
    
    @Override
    public void endElement(final QName name) {
        if (!this.isReader) {
            this.processedNodes.removeLast();
        }
    }
    
    private boolean isWildcardElement(final RuntimePropertyInfo ri) {
        return ri instanceof RuntimeReferencePropertyInfo && ((RuntimeReferencePropertyInfo)ri).getWildcard() != null;
    }
    
    private static class NodeWrapper
    {
        private final NodeWrapper parent;
        private final RuntimePropertyInfo runtimePropertyInfo;
        
        private NodeWrapper(final NodeWrapper parent, final RuntimePropertyInfo runtimePropertyInfo) {
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
