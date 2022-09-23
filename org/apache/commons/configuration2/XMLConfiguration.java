// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.w3c.dom.CDATASection;
import java.util.ArrayList;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitor;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.Writer;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import org.xml.sax.InputSource;
import java.io.Reader;
import org.apache.commons.configuration2.ex.ConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.configuration2.tree.ReferenceNodeHandler;
import org.w3c.dom.Document;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.resolver.DefaultEntityResolver;
import org.apache.commons.configuration2.io.FileLocator;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilder;
import org.apache.commons.configuration2.io.InputStreamSupport;
import org.apache.commons.configuration2.io.FileLocatorAware;

public class XMLConfiguration extends BaseHierarchicalConfiguration implements FileBasedConfiguration, FileLocatorAware, InputStreamSupport
{
    private static final String DEFAULT_ROOT_NAME = "configuration";
    private static final String ATTR_SPACE = "xml:space";
    private static final String ATTR_SPACE_INTERNAL = "config-xml:space";
    private static final String VALUE_PRESERVE = "preserve";
    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private String rootElementName;
    private String publicID;
    private String systemID;
    private DocumentBuilder documentBuilder;
    private boolean validating;
    private boolean schemaValidation;
    private EntityResolver entityResolver;
    private FileLocator locator;
    
    public XMLConfiguration() {
        this.entityResolver = new DefaultEntityResolver();
        this.initLogger(new ConfigurationLogger(XMLConfiguration.class));
    }
    
    public XMLConfiguration(final HierarchicalConfiguration<ImmutableNode> c) {
        super(c);
        this.entityResolver = new DefaultEntityResolver();
        this.rootElementName = ((c != null) ? c.getRootElementName() : null);
        this.initLogger(new ConfigurationLogger(XMLConfiguration.class));
    }
    
    @Override
    protected String getRootElementNameInternal() {
        final Document doc = this.getDocument();
        if (doc == null) {
            return (this.rootElementName == null) ? "configuration" : this.rootElementName;
        }
        return doc.getDocumentElement().getNodeName();
    }
    
    public void setRootElementName(final String name) {
        this.beginRead(true);
        try {
            if (this.getDocument() != null) {
                throw new UnsupportedOperationException("The name of the root element cannot be changed when loaded from an XML document!");
            }
            this.rootElementName = name;
        }
        finally {
            this.endRead();
        }
    }
    
    public DocumentBuilder getDocumentBuilder() {
        return this.documentBuilder;
    }
    
    public void setDocumentBuilder(final DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }
    
    public String getPublicID() {
        this.beginRead(false);
        try {
            return this.publicID;
        }
        finally {
            this.endRead();
        }
    }
    
    public void setPublicID(final String publicID) {
        this.beginWrite(false);
        try {
            this.publicID = publicID;
        }
        finally {
            this.endWrite();
        }
    }
    
    public String getSystemID() {
        this.beginRead(false);
        try {
            return this.systemID;
        }
        finally {
            this.endRead();
        }
    }
    
    public void setSystemID(final String systemID) {
        this.beginWrite(false);
        try {
            this.systemID = systemID;
        }
        finally {
            this.endWrite();
        }
    }
    
    public boolean isValidating() {
        return this.validating;
    }
    
    public void setValidating(final boolean validating) {
        if (!this.schemaValidation) {
            this.validating = validating;
        }
    }
    
    public boolean isSchemaValidation() {
        return this.schemaValidation;
    }
    
    public void setSchemaValidation(final boolean schemaValidation) {
        this.schemaValidation = schemaValidation;
        if (schemaValidation) {
            this.validating = true;
        }
    }
    
    public void setEntityResolver(final EntityResolver resolver) {
        this.entityResolver = resolver;
    }
    
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }
    
    public Document getDocument() {
        final XMLDocumentHelper docHelper = this.getDocumentHelper();
        return (docHelper != null) ? docHelper.getDocument() : null;
    }
    
    private XMLDocumentHelper getDocumentHelper() {
        final ReferenceNodeHandler handler = this.getReferenceHandler();
        return (XMLDocumentHelper)handler.getReference(handler.getRootNode());
    }
    
    private ReferenceNodeHandler getReferenceHandler() {
        return this.getSubConfigurationParentModel().getReferenceNodeHandler();
    }
    
    private void initProperties(final XMLDocumentHelper docHelper, final boolean elemRefs) {
        final Document document = docHelper.getDocument();
        this.setPublicID(docHelper.getSourcePublicID());
        this.setSystemID(docHelper.getSourceSystemID());
        final ImmutableNode.Builder rootBuilder = new ImmutableNode.Builder();
        final MutableObject<String> rootValue = new MutableObject<String>();
        final Map<ImmutableNode, Object> elemRefMap = elemRefs ? new HashMap<ImmutableNode, Object>() : null;
        final Map<String, String> attributes = this.constructHierarchy(rootBuilder, rootValue, document.getDocumentElement(), elemRefMap, true, 0);
        attributes.remove("config-xml:space");
        final ImmutableNode top = rootBuilder.value(rootValue.getValue()).addAttributes(attributes).create();
        this.getSubConfigurationParentModel().mergeRoot(top, document.getDocumentElement().getTagName(), elemRefMap, elemRefs ? docHelper : null, this);
    }
    
    private Map<String, String> constructHierarchy(final ImmutableNode.Builder node, final MutableObject<String> refValue, final Element element, final Map<ImmutableNode, Object> elemRefs, final boolean trim, final int level) {
        final boolean trimFlag = shouldTrim(element, trim);
        final Map<String, String> attributes = processAttributes(element);
        attributes.put("config-xml:space", String.valueOf(trimFlag));
        final StringBuilder buffer = new StringBuilder();
        final NodeList list = element.getChildNodes();
        boolean hasChildren = false;
        for (int i = 0; i < list.getLength(); ++i) {
            final Node w3cNode = list.item(i);
            if (w3cNode instanceof Element) {
                final Element child = (Element)w3cNode;
                final ImmutableNode.Builder childNode = new ImmutableNode.Builder();
                childNode.name(child.getTagName());
                final MutableObject<String> refChildValue = new MutableObject<String>();
                final Map<String, String> attrmap = this.constructHierarchy(childNode, refChildValue, child, elemRefs, trimFlag, level + 1);
                final Boolean childTrim = Boolean.valueOf(attrmap.remove("config-xml:space"));
                childNode.addAttributes(attrmap);
                final ImmutableNode newChild = this.createChildNodeWithValue(node, childNode, child, refChildValue.getValue(), childTrim, attrmap, elemRefs);
                if (elemRefs != null && !elemRefs.containsKey(newChild)) {
                    elemRefs.put(newChild, child);
                }
                hasChildren = true;
            }
            else if (w3cNode instanceof Text) {
                final Text data = (Text)w3cNode;
                buffer.append(data.getData());
            }
        }
        final boolean childrenFlag = hasChildren || attributes.size() > 1;
        final String text = determineValue(buffer.toString(), childrenFlag, trimFlag);
        if (text.length() > 0 || (!childrenFlag && level != 0)) {
            refValue.setValue(text);
        }
        return attributes;
    }
    
    private static String determineValue(final String content, final boolean hasChildren, final boolean trimFlag) {
        final boolean shouldTrim = trimFlag || (StringUtils.isBlank(content) && hasChildren);
        return shouldTrim ? content.trim() : content;
    }
    
    private static Map<String, String> processAttributes(final Element element) {
        final NamedNodeMap attributes = element.getAttributes();
        final Map<String, String> attrmap = new HashMap<String, String>();
        for (int i = 0; i < attributes.getLength(); ++i) {
            final Node w3cNode = attributes.item(i);
            if (w3cNode instanceof Attr) {
                final Attr attr = (Attr)w3cNode;
                attrmap.put(attr.getName(), attr.getValue());
            }
        }
        return attrmap;
    }
    
    private ImmutableNode createChildNodeWithValue(final ImmutableNode.Builder parent, final ImmutableNode.Builder child, final Element elem, final String value, final boolean trim, final Map<String, String> attrmap, final Map<ImmutableNode, Object> elemRefs) {
        Collection<String> values;
        if (value != null) {
            values = this.getListDelimiterHandler().split(value, trim);
        }
        else {
            values = (Collection<String>)Collections.emptyList();
        }
        ImmutableNode addedChildNode;
        if (values.size() > 1) {
            final Map<ImmutableNode, Object> refs = isSingleElementList(elem) ? elemRefs : null;
            final Iterator<String> it = values.iterator();
            child.value(it.next());
            addedChildNode = child.create();
            parent.addChild(addedChildNode);
            XMLListReference.assignListReference(refs, addedChildNode, elem);
            while (it.hasNext()) {
                final ImmutableNode.Builder c = new ImmutableNode.Builder();
                c.name(addedChildNode.getNodeName());
                c.value(it.next());
                c.addAttributes(attrmap);
                final ImmutableNode newChild = c.create();
                parent.addChild(newChild);
                XMLListReference.assignListReference(refs, newChild, null);
            }
        }
        else if (values.size() == 1) {
            child.value(values.iterator().next());
            addedChildNode = child.create();
            parent.addChild(addedChildNode);
        }
        else {
            addedChildNode = child.create();
            parent.addChild(addedChildNode);
        }
        return addedChildNode;
    }
    
    private static boolean isSingleElementList(final Element element) {
        final Node parentNode = element.getParentNode();
        return countChildElements(parentNode, element.getTagName()) == 1;
    }
    
    private static int countChildElements(final Node parent, final String name) {
        final NodeList childNodes = parent.getChildNodes();
        int count = 0;
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node item = childNodes.item(i);
            if (item instanceof Element && name.equals(((Element)item).getTagName())) {
                ++count;
            }
        }
        return count;
    }
    
    private static boolean shouldTrim(final Element element, final boolean currentTrim) {
        final Attr attr = element.getAttributeNode("xml:space");
        if (attr == null) {
            return currentTrim;
        }
        return !"preserve".equals(attr.getValue());
    }
    
    protected DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        if (this.getDocumentBuilder() != null) {
            return this.getDocumentBuilder();
        }
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        if (this.isValidating()) {
            factory.setValidating(true);
            if (this.isSchemaValidation()) {
                factory.setNamespaceAware(true);
                factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            }
        }
        final DocumentBuilder result = factory.newDocumentBuilder();
        result.setEntityResolver(this.entityResolver);
        if (this.isValidating()) {
            result.setErrorHandler(new DefaultHandler() {
                @Override
                public void error(final SAXParseException ex) throws SAXException {
                    throw ex;
                }
            });
        }
        return result;
    }
    
    protected Transformer createTransformer() throws ConfigurationException {
        final Transformer transformer = XMLDocumentHelper.createTransformer();
        transformer.setOutputProperty("indent", "yes");
        if (this.locator.getEncoding() != null) {
            transformer.setOutputProperty("encoding", this.locator.getEncoding());
        }
        if (this.publicID != null) {
            transformer.setOutputProperty("doctype-public", this.publicID);
        }
        if (this.systemID != null) {
            transformer.setOutputProperty("doctype-system", this.systemID);
        }
        return transformer;
    }
    
    private Document createDocument() throws ConfigurationException {
        final ReferenceNodeHandler handler = this.getReferenceHandler();
        final XMLDocumentHelper docHelper = (XMLDocumentHelper)handler.getReference(handler.getRootNode());
        final XMLDocumentHelper newHelper = (docHelper == null) ? XMLDocumentHelper.forNewDocument(this.getRootElementName()) : docHelper.createCopy();
        final XMLBuilderVisitor builder = new XMLBuilderVisitor(newHelper, this.getListDelimiterHandler());
        builder.handleRemovedNodes(handler);
        builder.processDocument(handler);
        this.initRootElementText(newHelper.getDocument(), this.getModel().getNodeHandler().getRootNode().getValue());
        return newHelper.getDocument();
    }
    
    private void initRootElementText(final Document doc, final Object value) {
        final Element elem = doc.getDocumentElement();
        final NodeList children = elem.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node nd = children.item(i);
            if (nd.getNodeType() == 3) {
                elem.removeChild(nd);
            }
        }
        if (value != null) {
            elem.appendChild(doc.createTextNode(String.valueOf(value)));
        }
    }
    
    @Override
    public void initFileLocator(final FileLocator loc) {
        this.locator = loc;
    }
    
    @Override
    public void read(final Reader in) throws ConfigurationException, IOException {
        this.load(new InputSource(in));
    }
    
    @Override
    public void read(final InputStream in) throws ConfigurationException, IOException {
        this.load(new InputSource(in));
    }
    
    private void load(final InputSource source) throws ConfigurationException {
        if (this.locator == null) {
            throw new ConfigurationException("Load operation not properly initialized! Do not call read(InputStream) directly, but use a FileHandler to load a configuration.");
        }
        try {
            final URL sourceURL = this.locator.getSourceURL();
            if (sourceURL != null) {
                source.setSystemId(sourceURL.toString());
            }
            final DocumentBuilder builder = this.createDocumentBuilder();
            final Document newDocument = builder.parse(source);
            final Document oldDocument = this.getDocument();
            this.initProperties(XMLDocumentHelper.forSourceDocument(newDocument), oldDocument == null);
        }
        catch (SAXParseException spe) {
            throw new ConfigurationException("Error parsing " + source.getSystemId(), spe);
        }
        catch (Exception e) {
            this.getLogger().debug("Unable to load the configuration: " + e);
            throw new ConfigurationException("Unable to load the configuration", e);
        }
    }
    
    @Override
    public void write(final Writer writer) throws ConfigurationException, IOException {
        final Transformer transformer = this.createTransformer();
        final Source source = new DOMSource(this.createDocument());
        final Result result = new StreamResult(writer);
        XMLDocumentHelper.transform(transformer, source, result);
    }
    
    public void validate() throws ConfigurationException {
        this.beginWrite(false);
        try {
            final Transformer transformer = this.createTransformer();
            final Source source = new DOMSource(this.createDocument());
            final StringWriter writer = new StringWriter();
            final Result result = new StreamResult(writer);
            XMLDocumentHelper.transform(transformer, source, result);
            final Reader reader = new StringReader(writer.getBuffer().toString());
            final DocumentBuilder builder = this.createDocumentBuilder();
            builder.parse(new InputSource(reader));
        }
        catch (SAXException e) {
            throw new ConfigurationException("Validation failed", e);
        }
        catch (IOException e2) {
            throw new ConfigurationException("Validation failed", e2);
        }
        catch (ParserConfigurationException pce) {
            throw new ConfigurationException("Validation failed", pce);
        }
        finally {
            this.endWrite();
        }
    }
    
    static class XMLBuilderVisitor extends BuilderVisitor
    {
        private final Document document;
        private final Map<Node, Node> elementMapping;
        private final Map<ImmutableNode, Element> newElements;
        private final ListDelimiterHandler listDelimiterHandler;
        
        public XMLBuilderVisitor(final XMLDocumentHelper docHelper, final ListDelimiterHandler handler) {
            this.document = docHelper.getDocument();
            this.elementMapping = docHelper.getElementMapping();
            this.listDelimiterHandler = handler;
            this.newElements = new HashMap<ImmutableNode, Element>();
        }
        
        public void processDocument(final ReferenceNodeHandler refHandler) {
            NodeTreeWalker.INSTANCE.walkDFS(refHandler.getRootNode(), this, refHandler);
        }
        
        public void handleRemovedNodes(final ReferenceNodeHandler refHandler) {
            for (final Object ref : refHandler.removedReferences()) {
                if (ref instanceof Node) {
                    final Node removedElem = (Node)ref;
                    this.removeReference(this.elementMapping.get(removedElem));
                }
            }
        }
        
        @Override
        protected void insert(final ImmutableNode newNode, final ImmutableNode parent, final ImmutableNode sibling1, final ImmutableNode sibling2, final ReferenceNodeHandler refHandler) {
            if (XMLListReference.isListNode(newNode, refHandler)) {
                return;
            }
            final Element elem = this.document.createElement(newNode.getNodeName());
            this.newElements.put(newNode, elem);
            updateAttributes(newNode, elem);
            if (newNode.getValue() != null) {
                final String txt = String.valueOf(this.listDelimiterHandler.escape(newNode.getValue(), ListDelimiterHandler.NOOP_TRANSFORMER));
                elem.appendChild(this.document.createTextNode(txt));
            }
            if (sibling2 == null) {
                this.getElement(parent, refHandler).appendChild(elem);
            }
            else if (sibling1 != null) {
                this.getElement(parent, refHandler).insertBefore(elem, this.getElement(sibling1, refHandler).getNextSibling());
            }
            else {
                this.getElement(parent, refHandler).insertBefore(elem, this.getElement(parent, refHandler).getFirstChild());
            }
        }
        
        @Override
        protected void update(final ImmutableNode node, final Object reference, final ReferenceNodeHandler refHandler) {
            if (XMLListReference.isListNode(node, refHandler)) {
                if (XMLListReference.isFirstListItem(node, refHandler)) {
                    final String value = XMLListReference.listValue(node, refHandler, this.listDelimiterHandler);
                    this.updateElement(node, refHandler, value);
                }
            }
            else {
                final Object value2 = this.listDelimiterHandler.escape(refHandler.getValue(node), ListDelimiterHandler.NOOP_TRANSFORMER);
                this.updateElement(node, refHandler, value2);
            }
        }
        
        private void updateElement(final ImmutableNode node, final ReferenceNodeHandler refHandler, final Object value) {
            final Element element = this.getElement(node, refHandler);
            this.updateElement(element, value);
            updateAttributes(node, element);
        }
        
        private void updateElement(final Element element, final Object value) {
            Text txtNode = findTextNodeForUpdate(element);
            if (value == null) {
                if (txtNode != null) {
                    element.removeChild(txtNode);
                }
            }
            else {
                final String newValue = String.valueOf(value);
                if (txtNode == null) {
                    txtNode = this.document.createTextNode(newValue);
                    if (element.getFirstChild() != null) {
                        element.insertBefore(txtNode, element.getFirstChild());
                    }
                    else {
                        element.appendChild(txtNode);
                    }
                }
                else {
                    txtNode.setNodeValue(newValue);
                }
            }
        }
        
        private void removeReference(final Element element) {
            final Node parentElem = element.getParentNode();
            if (parentElem != null) {
                parentElem.removeChild(element);
            }
        }
        
        private Element getElement(final ImmutableNode node, final ReferenceNodeHandler refHandler) {
            final Element elementNew = this.newElements.get(node);
            if (elementNew != null) {
                return elementNew;
            }
            final Object reference = refHandler.getReference(node);
            Node element;
            if (reference instanceof XMLDocumentHelper) {
                element = ((XMLDocumentHelper)reference).getDocument().getDocumentElement();
            }
            else if (reference instanceof XMLListReference) {
                element = ((XMLListReference)reference).getElement();
            }
            else {
                element = (Node)reference;
            }
            return (element != null) ? this.elementMapping.get(element) : this.document.getDocumentElement();
        }
        
        private static void updateAttributes(final ImmutableNode node, final Element elem) {
            if (node != null && elem != null) {
                clearAttributes(elem);
                for (final Map.Entry<String, Object> e : node.getAttributes().entrySet()) {
                    if (e.getValue() != null) {
                        elem.setAttribute(e.getKey(), e.getValue().toString());
                    }
                }
            }
        }
        
        private static void clearAttributes(final Element elem) {
            final NamedNodeMap attributes = elem.getAttributes();
            for (int i = 0; i < attributes.getLength(); ++i) {
                elem.removeAttribute(attributes.item(i).getNodeName());
            }
        }
        
        private static Text findTextNodeForUpdate(final Element elem) {
            Text result = null;
            final NodeList children = elem.getChildNodes();
            final Collection<Node> textNodes = new ArrayList<Node>();
            for (int i = 0; i < children.getLength(); ++i) {
                final Node nd = children.item(i);
                if (nd instanceof Text) {
                    if (result == null) {
                        result = (Text)nd;
                    }
                    else {
                        textNodes.add(nd);
                    }
                }
            }
            if (result instanceof CDATASection) {
                textNodes.add(result);
                result = null;
            }
            for (final Node tn : textNodes) {
                elem.removeChild(tn);
            }
            return result;
        }
    }
}
