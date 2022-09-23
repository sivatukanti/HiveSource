// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.Iterator;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitorAdapter;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitor;
import org.apache.commons.configuration2.tree.NodeTreeWalker;

public class HierarchicalConfigurationXMLReader<T> extends ConfigurationXMLReader
{
    private HierarchicalConfiguration<T> configuration;
    
    public HierarchicalConfigurationXMLReader() {
    }
    
    public HierarchicalConfigurationXMLReader(final HierarchicalConfiguration<T> config) {
        this();
        this.setConfiguration(config);
    }
    
    public HierarchicalConfiguration<T> getConfiguration() {
        return this.configuration;
    }
    
    public void setConfiguration(final HierarchicalConfiguration<T> config) {
        this.configuration = config;
    }
    
    @Override
    public Configuration getParsedConfiguration() {
        return this.getConfiguration();
    }
    
    @Override
    protected void processKeys() {
        final NodeHandler<T> nodeHandler = this.getConfiguration().getNodeModel().getNodeHandler();
        NodeTreeWalker.INSTANCE.walkDFS(nodeHandler.getRootNode(), new SAXVisitor(), nodeHandler);
    }
    
    private class SAXVisitor extends ConfigurationNodeVisitorAdapter<T>
    {
        private static final String ATTR_TYPE = "CDATA";
        
        @Override
        public void visitAfterChildren(final T node, final NodeHandler<T> handler) {
            HierarchicalConfigurationXMLReader.this.fireElementEnd(this.nodeName(node, handler));
        }
        
        @Override
        public void visitBeforeChildren(final T node, final NodeHandler<T> handler) {
            HierarchicalConfigurationXMLReader.this.fireElementStart(this.nodeName(node, handler), this.fetchAttributes(node, handler));
            final Object value = handler.getValue(node);
            if (value != null) {
                HierarchicalConfigurationXMLReader.this.fireCharacters(value.toString());
            }
        }
        
        @Override
        public boolean terminate() {
            return HierarchicalConfigurationXMLReader.this.getException() != null;
        }
        
        protected Attributes fetchAttributes(final T node, final NodeHandler<T> handler) {
            final AttributesImpl attrs = new AttributesImpl();
            for (final String attr : handler.getAttributes(node)) {
                final Object value = handler.getAttributeValue(node, attr);
                if (value != null) {
                    attrs.addAttribute("", attr, attr, "CDATA", value.toString());
                }
            }
            return attrs;
        }
        
        private String nodeName(final T node, final NodeHandler<T> handler) {
            final String nodeName = handler.nodeName(node);
            return (nodeName == null) ? HierarchicalConfigurationXMLReader.this.getRootName() : nodeName;
        }
    }
}
