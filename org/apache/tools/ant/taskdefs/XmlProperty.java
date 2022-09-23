// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.w3c.dom.NamedNodeMap;
import org.apache.tools.ant.types.Path;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.apache.tools.ant.types.resources.FileProvider;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.tools.ant.BuildException;
import org.xml.sax.EntityResolver;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.XMLCatalog;
import java.util.Hashtable;
import java.io.File;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.Task;

public class XmlProperty extends Task
{
    private Resource src;
    private String prefix;
    private boolean keepRoot;
    private boolean validate;
    private boolean collapseAttributes;
    private boolean semanticAttributes;
    private boolean includeSemanticAttribute;
    private File rootDirectory;
    private Hashtable addedAttributes;
    private XMLCatalog xmlCatalog;
    private String delimiter;
    private static final String ID = "id";
    private static final String REF_ID = "refid";
    private static final String LOCATION = "location";
    private static final String VALUE = "value";
    private static final String PATH = "path";
    private static final String PATHID = "pathid";
    private static final String[] ATTRIBUTES;
    private static final FileUtils FILE_UTILS;
    
    public XmlProperty() {
        this.prefix = "";
        this.keepRoot = true;
        this.validate = false;
        this.collapseAttributes = false;
        this.semanticAttributes = false;
        this.includeSemanticAttribute = false;
        this.rootDirectory = null;
        this.addedAttributes = new Hashtable();
        this.xmlCatalog = new XMLCatalog();
        this.delimiter = ",";
    }
    
    @Override
    public void init() {
        super.init();
        this.xmlCatalog.setProject(this.getProject());
    }
    
    protected EntityResolver getEntityResolver() {
        return this.xmlCatalog;
    }
    
    @Override
    public void execute() throws BuildException {
        final Resource r = this.getResource();
        if (r == null) {
            throw new BuildException("XmlProperty task requires a source resource");
        }
        try {
            this.log("Loading " + this.src, 3);
            if (r.isExists()) {
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(this.validate);
                factory.setNamespaceAware(false);
                final DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setEntityResolver(this.getEntityResolver());
                Document document = null;
                final FileProvider fp = this.src.as(FileProvider.class);
                if (fp != null) {
                    document = builder.parse(fp.getFile());
                }
                else {
                    document = builder.parse(this.src.getInputStream());
                }
                final Element topElement = document.getDocumentElement();
                this.addedAttributes = new Hashtable();
                if (this.keepRoot) {
                    this.addNodeRecursively(topElement, this.prefix, null);
                }
                else {
                    final NodeList topChildren = topElement.getChildNodes();
                    for (int numChildren = topChildren.getLength(), i = 0; i < numChildren; ++i) {
                        this.addNodeRecursively(topChildren.item(i), this.prefix, null);
                    }
                }
            }
            else {
                this.log("Unable to find property resource: " + r, 3);
            }
        }
        catch (SAXException sxe) {
            Exception x = sxe;
            if (sxe.getException() != null) {
                x = sxe.getException();
            }
            throw new BuildException("Failed to load " + this.src, x);
        }
        catch (ParserConfigurationException pce) {
            throw new BuildException(pce);
        }
        catch (IOException ioe) {
            throw new BuildException("Failed to load " + this.src, ioe);
        }
    }
    
    private void addNodeRecursively(final Node node, final String prefix, final Object container) {
        String nodePrefix = prefix;
        if (node.getNodeType() != 3) {
            if (prefix.trim().length() > 0) {
                nodePrefix += ".";
            }
            nodePrefix += node.getNodeName();
        }
        final Object nodeObject = this.processNode(node, nodePrefix, container);
        if (node.hasChildNodes()) {
            final NodeList nodeChildren = node.getChildNodes();
            for (int numChildren = nodeChildren.getLength(), i = 0; i < numChildren; ++i) {
                this.addNodeRecursively(nodeChildren.item(i), nodePrefix, nodeObject);
            }
        }
    }
    
    void addNodeRecursively(final Node node, final String prefix) {
        this.addNodeRecursively(node, prefix, null);
    }
    
    public Object processNode(final Node node, final String prefix, final Object container) {
        Object addedPath = null;
        String id = null;
        if (node.hasAttributes()) {
            final NamedNodeMap nodeAttributes = node.getAttributes();
            final Node idNode = nodeAttributes.getNamedItem("id");
            id = ((this.semanticAttributes && idNode != null) ? idNode.getNodeValue() : null);
            for (int i = 0; i < nodeAttributes.getLength(); ++i) {
                final Node attributeNode = nodeAttributes.item(i);
                if (!this.semanticAttributes) {
                    final String attributeName = this.getAttributeName(attributeNode);
                    final String attributeValue = this.getAttributeValue(attributeNode);
                    this.addProperty(prefix + attributeName, attributeValue, null);
                }
                else {
                    final String nodeName = attributeNode.getNodeName();
                    final String attributeValue = this.getAttributeValue(attributeNode);
                    final Path containingPath = (container != null && container instanceof Path) ? ((Path)container) : null;
                    if (!nodeName.equals("id")) {
                        if (containingPath != null && nodeName.equals("path")) {
                            containingPath.setPath(attributeValue);
                        }
                        else if (container instanceof Path && nodeName.equals("refid")) {
                            containingPath.setPath(attributeValue);
                        }
                        else if (container instanceof Path && nodeName.equals("location")) {
                            containingPath.setLocation(this.resolveFile(attributeValue));
                        }
                        else if (nodeName.equals("pathid")) {
                            if (container != null) {
                                throw new BuildException("XmlProperty does not support nested paths");
                            }
                            addedPath = new Path(this.getProject());
                            this.getProject().addReference(attributeValue, addedPath);
                        }
                        else {
                            final String attributeName2 = this.getAttributeName(attributeNode);
                            this.addProperty(prefix + attributeName2, attributeValue, id);
                        }
                    }
                }
            }
        }
        String nodeText = null;
        boolean emptyNode = false;
        boolean semanticEmptyOverride = false;
        if (node.getNodeType() == 1 && this.semanticAttributes && node.hasAttributes() && (node.getAttributes().getNamedItem("value") != null || node.getAttributes().getNamedItem("location") != null || node.getAttributes().getNamedItem("refid") != null || node.getAttributes().getNamedItem("path") != null || node.getAttributes().getNamedItem("pathid") != null)) {
            semanticEmptyOverride = true;
        }
        if (node.getNodeType() == 3) {
            nodeText = this.getAttributeValue(node);
        }
        else if (node.getNodeType() == 1 && node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == 4) {
            nodeText = node.getFirstChild().getNodeValue();
            if ("".equals(nodeText) && !semanticEmptyOverride) {
                emptyNode = true;
            }
        }
        else if (node.getNodeType() == 1 && node.getChildNodes().getLength() == 0 && !semanticEmptyOverride) {
            nodeText = "";
            emptyNode = true;
        }
        else if (node.getNodeType() == 1 && node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == 3 && "".equals(node.getFirstChild().getNodeValue()) && !semanticEmptyOverride) {
            nodeText = "";
            emptyNode = true;
        }
        if (nodeText != null) {
            if (this.semanticAttributes && id == null && container instanceof String) {
                id = (String)container;
            }
            if (nodeText.trim().length() != 0 || emptyNode) {
                this.addProperty(prefix, nodeText, id);
            }
        }
        return (addedPath != null) ? addedPath : id;
    }
    
    private void addProperty(final String name, String value, final String id) {
        String msg = name + ":" + value;
        if (id != null) {
            msg = msg + "(id=" + id + ")";
        }
        this.log(msg, 4);
        if (this.addedAttributes.containsKey(name)) {
            value = this.addedAttributes.get(name) + this.getDelimiter() + value;
            this.getProject().setProperty(name, value);
            this.addedAttributes.put(name, value);
        }
        else if (this.getProject().getProperty(name) == null) {
            this.getProject().setNewProperty(name, value);
            this.addedAttributes.put(name, value);
        }
        else {
            this.log("Override ignored for property " + name, 3);
        }
        if (id != null) {
            this.getProject().addReference(id, value);
        }
    }
    
    private String getAttributeName(final Node attributeNode) {
        final String attributeName = attributeNode.getNodeName();
        if (!this.semanticAttributes) {
            return this.collapseAttributes ? ("." + attributeName) : ("(" + attributeName + ")");
        }
        if (attributeName.equals("refid")) {
            return "";
        }
        if (!isSemanticAttribute(attributeName) || this.includeSemanticAttribute) {
            return "." + attributeName;
        }
        return "";
    }
    
    private static boolean isSemanticAttribute(final String attributeName) {
        for (int i = 0; i < XmlProperty.ATTRIBUTES.length; ++i) {
            if (attributeName.equals(XmlProperty.ATTRIBUTES[i])) {
                return true;
            }
        }
        return false;
    }
    
    private String getAttributeValue(final Node attributeNode) {
        String nodeValue = attributeNode.getNodeValue().trim();
        if (this.semanticAttributes) {
            final String attributeName = attributeNode.getNodeName();
            nodeValue = this.getProject().replaceProperties(nodeValue);
            if (attributeName.equals("location")) {
                final File f = this.resolveFile(nodeValue);
                return f.getPath();
            }
            if (attributeName.equals("refid")) {
                final Object ref = this.getProject().getReference(nodeValue);
                if (ref != null) {
                    return ref.toString();
                }
            }
        }
        return nodeValue;
    }
    
    public void setFile(final File src) {
        this.setSrcResource(new FileResource(src));
    }
    
    public void setSrcResource(final Resource src) {
        if (src.isDirectory()) {
            throw new BuildException("the source can't be a directory");
        }
        if (src.as(FileProvider.class) != null || this.supportsNonFileResources()) {
            this.src = src;
            return;
        }
        throw new BuildException("Only FileSystem resources are supported.");
    }
    
    public void addConfigured(final ResourceCollection a) {
        if (a.size() != 1) {
            throw new BuildException("only single argument resource collections are supported as archives");
        }
        this.setSrcResource(a.iterator().next());
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix.trim();
    }
    
    public void setKeeproot(final boolean keepRoot) {
        this.keepRoot = keepRoot;
    }
    
    public void setValidate(final boolean validate) {
        this.validate = validate;
    }
    
    public void setCollapseAttributes(final boolean collapseAttributes) {
        this.collapseAttributes = collapseAttributes;
    }
    
    public void setSemanticAttributes(final boolean semanticAttributes) {
        this.semanticAttributes = semanticAttributes;
    }
    
    public void setRootDirectory(final File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
    
    public void setIncludeSemanticAttribute(final boolean includeSemanticAttribute) {
        this.includeSemanticAttribute = includeSemanticAttribute;
    }
    
    public void addConfiguredXMLCatalog(final XMLCatalog catalog) {
        this.xmlCatalog.addConfiguredXMLCatalog(catalog);
    }
    
    protected File getFile() {
        final FileProvider fp = this.src.as(FileProvider.class);
        return (fp != null) ? fp.getFile() : null;
    }
    
    protected Resource getResource() {
        final File f = this.getFile();
        final FileProvider fp = this.src.as(FileProvider.class);
        return (f == null) ? this.src : ((fp != null && fp.getFile().equals(f)) ? this.src : new FileResource(f));
    }
    
    protected String getPrefix() {
        return this.prefix;
    }
    
    protected boolean getKeeproot() {
        return this.keepRoot;
    }
    
    protected boolean getValidate() {
        return this.validate;
    }
    
    protected boolean getCollapseAttributes() {
        return this.collapseAttributes;
    }
    
    protected boolean getSemanticAttributes() {
        return this.semanticAttributes;
    }
    
    protected File getRootDirectory() {
        return this.rootDirectory;
    }
    
    protected boolean getIncludeSementicAttribute() {
        return this.includeSemanticAttribute;
    }
    
    private File resolveFile(final String fileName) {
        return XmlProperty.FILE_UTILS.resolveFile((this.rootDirectory == null) ? this.getProject().getBaseDir() : this.rootDirectory, fileName);
    }
    
    protected boolean supportsNonFileResources() {
        return this.getClass().equals(XmlProperty.class);
    }
    
    public String getDelimiter() {
        return this.delimiter;
    }
    
    public void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }
    
    static {
        ATTRIBUTES = new String[] { "id", "refid", "location", "value", "path", "pathid" };
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
