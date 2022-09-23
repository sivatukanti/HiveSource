// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.slf4j.Logger;

public class XmlConfigLoader extends ConfigLoader
{
    private static final Logger LOGGER;
    
    @Override
    protected void loadConfig(final ConfigImpl config, final Resource resource) throws Exception {
        final Element doc = this.loadResourceDocument(resource);
        this.loadConfig(config, doc);
    }
    
    private Element loadResourceDocument(final Resource resource) throws Exception {
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringComments(true);
        docBuilderFactory.setNamespaceAware(true);
        try {
            docBuilderFactory.setXIncludeAware(true);
        }
        catch (UnsupportedOperationException e) {
            XmlConfigLoader.LOGGER.error("Failed to set setXIncludeAware(true) for parser", e);
        }
        final DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
        final InputStream is = (InputStream)resource.getResource();
        Document doc = null;
        try {
            doc = builder.parse(is);
        }
        finally {
            is.close();
        }
        final Element root = doc.getDocumentElement();
        this.validateConfig(root);
        return root;
    }
    
    private boolean validateConfig(final Element root) {
        boolean valid = false;
        if ("config".equals(root.getTagName())) {
            valid = true;
        }
        else {
            XmlConfigLoader.LOGGER.error("bad conf element: top-level element not <configuration>");
        }
        return valid;
    }
    
    private void loadConfig(final ConfigImpl config, final Element element) {
        final NodeList props = element.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            final Node subNode = props.item(i);
            if (subNode instanceof Element) {
                final Element prop = (Element)subNode;
                final String name = getElementName(prop);
                if (name != null) {
                    ConfigObject value = null;
                    final String tagName = prop.getTagName();
                    if ("property".equals(tagName) && prop.hasChildNodes()) {
                        value = loadProperty(prop);
                    }
                    else if ("config".equals(tagName) && prop.hasChildNodes()) {
                        final ConfigImpl cfg = new ConfigImpl(name);
                        this.loadConfig(cfg, prop);
                        value = new ConfigObject(cfg);
                    }
                    config.set(name, value);
                }
            }
        }
    }
    
    private static ConfigObject loadProperty(final Element ele) {
        if (ele.getFirstChild() instanceof Text) {
            final String value = ((Text)ele.getFirstChild()).getData();
            return new ConfigObject(value);
        }
        final NodeList nodes = ele.getChildNodes();
        final List<String> values = new ArrayList<String>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); ++i) {
            String value2 = null;
            final Node valueNode = nodes.item(i);
            if (valueNode instanceof Element) {
                final Element valueEle = (Element)valueNode;
                if ("value".equals(valueEle.getTagName()) && valueEle.hasChildNodes()) {
                    value2 = ((Text)valueEle.getFirstChild()).getData();
                }
                if (value2 != null) {
                    values.add(value2);
                }
            }
        }
        return new ConfigObject(values);
    }
    
    private static String getElementName(final Element ele) {
        final NamedNodeMap nnm = ele.getAttributes();
        for (int i = 0; i < nnm.getLength(); ++i) {
            final Node node = nnm.item(i);
            if (node instanceof Attr) {
                final Attr attr = (Attr)node;
                if ("name".equals(attr.getName())) {
                    return attr.getValue();
                }
            }
        }
        return null;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(Config.class);
    }
}
