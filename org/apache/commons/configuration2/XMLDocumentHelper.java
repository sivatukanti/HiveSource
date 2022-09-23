// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.w3c.dom.NodeList;
import java.util.HashMap;
import java.util.Collections;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.util.Map;
import org.w3c.dom.Document;

class XMLDocumentHelper
{
    private final Document document;
    private final Map<Node, Node> elementMapping;
    private final String sourcePublicID;
    private final String sourceSystemID;
    
    XMLDocumentHelper(final Document doc, final Map<Node, Node> elemMap, final String pubID, final String sysID) {
        this.document = doc;
        this.elementMapping = elemMap;
        this.sourcePublicID = pubID;
        this.sourceSystemID = sysID;
    }
    
    public static XMLDocumentHelper forNewDocument(final String rootElementName) throws ConfigurationException {
        final Document doc = createDocumentBuilder(createDocumentBuilderFactory()).newDocument();
        final Element rootElem = doc.createElement(rootElementName);
        doc.appendChild(rootElem);
        return new XMLDocumentHelper(doc, emptyElementMapping(), null, null);
    }
    
    public static XMLDocumentHelper forSourceDocument(final Document srcDoc) throws ConfigurationException {
        String pubID;
        String sysID;
        if (srcDoc.getDoctype() != null) {
            pubID = srcDoc.getDoctype().getPublicId();
            sysID = srcDoc.getDoctype().getSystemId();
        }
        else {
            pubID = null;
            sysID = null;
        }
        return new XMLDocumentHelper(copyDocument(srcDoc), emptyElementMapping(), pubID, sysID);
    }
    
    public Document getDocument() {
        return this.document;
    }
    
    public Map<Node, Node> getElementMapping() {
        return this.elementMapping;
    }
    
    public String getSourcePublicID() {
        return this.sourcePublicID;
    }
    
    public String getSourceSystemID() {
        return this.sourceSystemID;
    }
    
    public static Transformer createTransformer() throws ConfigurationException {
        return createTransformer(createTransformerFactory());
    }
    
    public static void transform(final Transformer transformer, final Source source, final Result result) throws ConfigurationException {
        try {
            transformer.transform(source, result);
        }
        catch (TransformerException tex) {
            throw new ConfigurationException(tex);
        }
    }
    
    public XMLDocumentHelper createCopy() throws ConfigurationException {
        final Document docCopy = copyDocument(this.getDocument());
        return new XMLDocumentHelper(docCopy, createElementMapping(this.getDocument(), docCopy), this.getSourcePublicID(), this.getSourceSystemID());
    }
    
    static TransformerFactory createTransformerFactory() {
        return TransformerFactory.newInstance();
    }
    
    static Transformer createTransformer(final TransformerFactory factory) throws ConfigurationException {
        try {
            return factory.newTransformer();
        }
        catch (TransformerConfigurationException tex) {
            throw new ConfigurationException(tex);
        }
    }
    
    static DocumentBuilder createDocumentBuilder(final DocumentBuilderFactory factory) throws ConfigurationException {
        try {
            return factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException pcex) {
            throw new ConfigurationException(pcex);
        }
    }
    
    private static Document copyDocument(final Document doc) throws ConfigurationException {
        final Transformer transformer = createTransformer();
        final DOMSource source = new DOMSource(doc);
        final DOMResult result = new DOMResult();
        transform(transformer, source, result);
        return (Document)result.getNode();
    }
    
    private static DocumentBuilderFactory createDocumentBuilderFactory() {
        return DocumentBuilderFactory.newInstance();
    }
    
    private static Map<Node, Node> emptyElementMapping() {
        return Collections.emptyMap();
    }
    
    private static Map<Node, Node> createElementMapping(final Document doc1, final Document doc2) {
        final Map<Node, Node> mapping = new HashMap<Node, Node>();
        createElementMappingForNodes(doc1.getDocumentElement(), doc2.getDocumentElement(), mapping);
        return mapping;
    }
    
    private static void createElementMappingForNodes(final Node n1, final Node n2, final Map<Node, Node> mapping) {
        mapping.put(n1, n2);
        final NodeList childNodes1 = n1.getChildNodes();
        final NodeList childNodes2 = n2.getChildNodes();
        for (int count = Math.min(childNodes1.getLength(), childNodes2.getLength()), i = 0; i < count; ++i) {
            createElementMappingForNodes(childNodes1.item(i), childNodes2.item(i), mapping);
        }
    }
}
