// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.internal.oxm.MappingNodeValue;
import org.eclipse.persistence.internal.oxm.TreeObjectBuilder;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import javax.xml.bind.JAXBElement;
import java.lang.reflect.Type;
import java.util.Map;
import org.eclipse.persistence.jaxb.JAXBHelper;
import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import java.util.Stack;

public class MoxyXmlStructure extends DefaultJaxbXmlDocumentStructure
{
    private Stack<XPathNodeWrapper> xPathNodes;
    private XPathNodeWrapper lastAccessedNode;
    private final Class<?> expectedType;
    private final JAXBContext jaxbContext;
    private boolean firstDocumentElement;
    private final boolean isReader;
    
    public MoxyXmlStructure(final JAXBContext jaxbContext, final Class<?> expectedType, final boolean isReader) {
        super(jaxbContext, expectedType, isReader);
        this.xPathNodes = new Stack<XPathNodeWrapper>();
        this.lastAccessedNode = null;
        this.firstDocumentElement = true;
        this.jaxbContext = jaxbContext;
        this.expectedType = expectedType;
        this.isReader = isReader;
    }
    
    private XPathNodeWrapper getRootNodeWrapperForElement(final QName elementName, final boolean isRoot) {
        if (this.jaxbContext == null) {
            return null;
        }
        final org.eclipse.persistence.jaxb.JAXBContext moxyJaxbContext = JAXBHelper.getJAXBContext(this.jaxbContext);
        final XMLContext xmlContext = moxyJaxbContext.getXMLContext();
        final DatabaseSession session = xmlContext.getSession(0);
        Class<?> expectedType = this.expectedType;
        if (!isRoot) {
            final HashMap<Type, QName> typeToSchemaType = (HashMap<Type, QName>)moxyJaxbContext.getTypeToSchemaType();
            for (final Map.Entry<Type, QName> entry : typeToSchemaType.entrySet()) {
                if (entry.getValue().getLocalPart().equals(elementName.getLocalPart())) {
                    expectedType = entry.getKey();
                    break;
                }
            }
        }
        if (JAXBElement.class.isAssignableFrom(expectedType)) {
            final Map<Class, ClassDescriptor> descriptors = (Map<Class, ClassDescriptor>)session.getDescriptors();
            for (final Map.Entry<Class, ClassDescriptor> descriptor : descriptors.entrySet()) {
                final QName defaultRootElementType = ((XMLDescriptor)descriptor.getValue()).getDefaultRootElementType();
                if (defaultRootElementType != null) {
                    if (!defaultRootElementType.getLocalPart().contains(elementName.getLocalPart())) {
                        continue;
                    }
                    if ((defaultRootElementType.getNamespaceURI() != null || elementName.getNamespaceURI() != null) && (defaultRootElementType.getNamespaceURI() == null || !defaultRootElementType.getNamespaceURI().equals(elementName.getNamespaceURI()))) {
                        continue;
                    }
                    expectedType = descriptor.getKey();
                }
            }
        }
        final ClassDescriptor descriptor2 = session.getDescriptor((Class)expectedType);
        if (descriptor2 != null) {
            final TreeObjectBuilder objectBuilder = (TreeObjectBuilder)descriptor2.getObjectBuilder();
            return new XPathNodeWrapper(objectBuilder.getRootXPathNode(), null, null, descriptor2, new QName(expectedType.getSimpleName()));
        }
        return null;
    }
    
    @Override
    public Collection<QName> getExpectedElements() {
        final List<QName> elements = new LinkedList<QName>();
        final XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        final Map<XPathFragment, XPathNode> nonAttributeChildrenMap = (Map<XPathFragment, XPathNode>)((currentNodeWrapper == null) ? null : currentNodeWrapper.xPathNode.getNonAttributeChildrenMap());
        if (nonAttributeChildrenMap != null) {
            for (final Map.Entry<XPathFragment, XPathNode> entry : nonAttributeChildrenMap.entrySet()) {
                elements.add(new QName(entry.getKey().getNamespaceURI(), entry.getKey().getLocalName()));
            }
        }
        return elements;
    }
    
    private XPathNodeWrapper getCurrentNodeWrapper() {
        final XPathNodeWrapper nodeWrapper = this.xPathNodes.isEmpty() ? null : this.xPathNodes.peek();
        if (nodeWrapper != null) {
            return nodeWrapper;
        }
        return null;
    }
    
    @Override
    public Collection<QName> getExpectedAttributes() {
        final List<QName> attributes = new LinkedList<QName>();
        final XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        final Map<XPathFragment, XPathNode> attributeChildrenMap = (Map<XPathFragment, XPathNode>)((currentNodeWrapper == null) ? null : currentNodeWrapper.xPathNode.getAttributeChildrenMap());
        if (attributeChildrenMap != null) {
            for (final Map.Entry<XPathFragment, XPathNode> entry : attributeChildrenMap.entrySet()) {
                attributes.add(new QName(entry.getKey().getNamespaceURI(), entry.getKey().getLocalName()));
            }
        }
        return attributes;
    }
    
    @Override
    public void startElement(final QName name) {
        if (name == null || this.firstDocumentElement) {
            this.firstDocumentElement = false;
            if (name != null) {
                this.xPathNodes.push(this.getRootNodeWrapperForElement(name, true));
            }
            return;
        }
        XPathNode childNode = null;
        XPathNodeWrapper newNodeWrapper = null;
        final XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        final XPathNodeWrapper actualNodeWrapper = (currentNodeWrapper.currentType == null) ? currentNodeWrapper : currentNodeWrapper.currentType;
        final Map<XPathFragment, XPathNode> nonAttributeChildrenMap = (Map<XPathFragment, XPathNode>)((actualNodeWrapper == null) ? null : actualNodeWrapper.xPathNode.getNonAttributeChildrenMap());
        if (nonAttributeChildrenMap != null) {
            for (final Map.Entry<XPathFragment, XPathNode> child : nonAttributeChildrenMap.entrySet()) {
                if (name.getLocalPart().equalsIgnoreCase(child.getKey().getLocalName())) {
                    childNode = child.getValue();
                    break;
                }
            }
            if (childNode != null) {
                final MappingNodeValue nodeValue = (MappingNodeValue)childNode.getNodeValue();
                if (nodeValue != null) {
                    ClassDescriptor descriptor = nodeValue.getMapping().getReferenceDescriptor();
                    if (descriptor == null && !this.isReader) {
                        descriptor = nodeValue.getMapping().getDescriptor();
                    }
                    if (descriptor != null) {
                        final TreeObjectBuilder objectBuilder = (TreeObjectBuilder)descriptor.getObjectBuilder();
                        final XPathNodeWrapper nodeWrapper = actualNodeWrapper;
                        newNodeWrapper = new XPathNodeWrapper(objectBuilder.getRootXPathNode(), nodeWrapper, nodeValue, descriptor, name);
                        this.xPathNodes.push(newNodeWrapper);
                    }
                }
            }
        }
        this.lastAccessedNode = ((newNodeWrapper == null) ? new XPathNodeWrapper(name) : newNodeWrapper);
    }
    
    @Override
    public void endElement(final QName name) {
        final XPathNodeWrapper xPathNodeWrapper = this.getCurrentNodeWrapper();
        if (xPathNodeWrapper != null && xPathNodeWrapper.name.equals(name)) {
            this.xPathNodes.pop();
        }
        this.lastAccessedNode = this.getCurrentNodeWrapper();
    }
    
    @Override
    public Map<String, QName> getExpectedElementsMap() {
        return (this.getCurrentNodeWrapper() == null) ? Collections.emptyMap() : this.getCurrentNodeWrapper().getExpectedElementsMap();
    }
    
    @Override
    public Map<String, QName> getExpectedAttributesMap() {
        return (this.getCurrentNodeWrapper() == null) ? Collections.emptyMap() : this.getCurrentNodeWrapper().getExpectedAttributesMap();
    }
    
    @Override
    public Type getEntityType(final QName entity, final boolean isAttribute) {
        return this.getType(entity, isAttribute, false);
    }
    
    @Override
    public Type getIndividualType() {
        return this.getContainerType(true);
    }
    
    private Type getType(final QName entity, final boolean isAttribute, final boolean isIndividual) {
        final XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        final ClassDescriptor classDescriptor = (currentNodeWrapper == null) ? null : currentNodeWrapper.getClassDescriptor();
        if (classDescriptor == null) {
            return null;
        }
        if (currentNodeWrapper.name.equals(entity)) {
            final Type containerType = this.getContainerType(isIndividual);
            return (containerType != null) ? containerType : classDescriptor.getJavaClass();
        }
        final EntityType entityType = currentNodeWrapper.getEntitiesTypesMap(isAttribute).get(entity.getLocalPart());
        return (entityType == null) ? null : entityType.type;
    }
    
    private Type getContainerType(final boolean isIndividual) {
        final XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        if (currentNodeWrapper.nodeValue != null && currentNodeWrapper.nodeValue.isContainerValue()) {
            final DatabaseMapping mapping = currentNodeWrapper.nodeValue.getMapping();
            Converter valueConverter = null;
            if (mapping != null) {
                if (isIndividual) {
                    if (mapping instanceof AbstractCompositeDirectCollectionMapping) {
                        valueConverter = ((AbstractCompositeDirectCollectionMapping)mapping).getValueConverter();
                    }
                    else if (mapping instanceof XMLCompositeCollectionMapping) {
                        valueConverter = ((XMLCompositeCollectionMapping)mapping).getConverter();
                    }
                }
                if (valueConverter instanceof TypeConversionConverter) {
                    return ((TypeConversionConverter)valueConverter).getObjectClass();
                }
                if (mapping.getContainerPolicy() != null) {
                    return mapping.getContainerPolicy().getContainerClass();
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean isArrayCollection() {
        final XPathNodeWrapper currentNodeWrapper = this.getCurrentNodeWrapper();
        if (currentNodeWrapper != null && this.lastAccessedNode != null && this.lastAccessedNode.name == currentNodeWrapper.name) {
            final MappingNodeValue nodeValue = currentNodeWrapper.getNodeValue();
            return nodeValue != null && nodeValue.isContainerValue();
        }
        return false;
    }
    
    @Override
    public boolean isSameArrayCollection() {
        final int size = this.xPathNodes.size();
        if (size >= 2) {
            final XPathNodeWrapper last = this.xPathNodes.peek();
            final XPathNodeWrapper beforeLast = this.xPathNodes.get(size - 2);
            if (last.isInSameArrayAs(beforeLast)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void handleAttribute(final QName attributeName, final String value) {
        final String localPart = attributeName.getLocalPart();
        if ("@type".equals(localPart) || "type".equals(localPart)) {
            this.getCurrentNodeWrapper().currentType = this.getRootNodeWrapperForElement(new QName(value), false);
        }
    }
    
    @Override
    public boolean hasSubElements() {
        final Collection<QName> expectedElements = this.getExpectedElements();
        return expectedElements != null && !expectedElements.isEmpty();
    }
    
    private final class EntityType
    {
        private final Type type;
        
        private EntityType(final Type type) {
            this.type = type;
        }
    }
    
    private final class XPathNodeWrapper
    {
        private Map<String, EntityType> elementTypeMap;
        private Map<String, EntityType> attributeTypeMap;
        private Map<String, QName> qNamesOfExpElems;
        private Map<String, QName> qNamesOfExpAttrs;
        private final XPathNode xPathNode;
        private final XPathNodeWrapper parent;
        private final ClassDescriptor classDescriptor;
        private final QName name;
        private final MappingNodeValue nodeValue;
        public XPathNodeWrapper currentType;
        
        public XPathNodeWrapper(final MoxyXmlStructure moxyXmlStructure, final QName name) {
            this(moxyXmlStructure, null, null, null, null, name);
        }
        
        public XPathNodeWrapper(final XPathNode xPathNode, final XPathNodeWrapper parent, final MappingNodeValue nodeValue, final ClassDescriptor classDescriptor, final QName name) {
            this.elementTypeMap = new HashMap<String, EntityType>();
            this.attributeTypeMap = new HashMap<String, EntityType>();
            this.qNamesOfExpElems = new HashMap<String, QName>();
            this.qNamesOfExpAttrs = new HashMap<String, QName>();
            this.xPathNode = xPathNode;
            this.parent = parent;
            this.nodeValue = nodeValue;
            this.classDescriptor = classDescriptor;
            this.name = name;
        }
        
        public Map<String, QName> getExpectedElementsMap() {
            if (this.qNamesOfExpElems.isEmpty()) {
                this.qNamesOfExpElems = MoxyXmlStructure.this.qnameCollectionToMap(MoxyXmlStructure.this.getExpectedElements(), true);
            }
            return this.qNamesOfExpElems;
        }
        
        public Map<String, QName> getExpectedAttributesMap() {
            if (this.qNamesOfExpElems.isEmpty()) {
                this.qNamesOfExpAttrs = MoxyXmlStructure.this.qnameCollectionToMap(MoxyXmlStructure.this.getExpectedAttributes(), false);
            }
            return this.qNamesOfExpAttrs;
        }
        
        public Map<String, EntityType> getEntitiesTypesMap(final boolean isAttribute) {
            final Map<String, EntityType> entitiesTypes = isAttribute ? this.attributeTypeMap : this.elementTypeMap;
            if (entitiesTypes.isEmpty()) {
                final Map<XPathFragment, XPathNode> nodeMap = (Map<XPathFragment, XPathNode>)(isAttribute ? this.xPathNode.getAttributeChildrenMap() : this.xPathNode.getNonAttributeChildrenMap());
                if (nodeMap != null) {
                    for (final Map.Entry<XPathFragment, XPathNode> entry : nodeMap.entrySet()) {
                        entitiesTypes.put(entry.getKey().getLocalName(), new EntityType((Type)entry.getKey().getXMLField().getType()));
                    }
                }
            }
            return entitiesTypes;
        }
        
        public MappingNodeValue getNodeValue() {
            return this.nodeValue;
        }
        
        public ClassDescriptor getClassDescriptor() {
            return this.classDescriptor;
        }
        
        public boolean isInSameArrayAs(final XPathNodeWrapper wrapper) {
            return wrapper != null && this.classDescriptor == wrapper.classDescriptor && this.parent == wrapper.parent;
        }
    }
}
