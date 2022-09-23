// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.beanutils;

import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import java.util.Set;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import java.util.LinkedList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;

public class XMLBeanDeclaration implements BeanDeclaration
{
    public static final String RESERVED_PREFIX = "config-";
    public static final String ATTR_PREFIX = "[@config-";
    public static final String ATTR_BEAN_CLASS = "[@config-class]";
    public static final String ATTR_BEAN_FACTORY = "[@config-factory]";
    public static final String ATTR_FACTORY_PARAM = "[@config-factoryParam]";
    private static final String ATTR_BEAN_CLASS_NAME = "config-class";
    private static final String ELEM_CTOR_ARG = "config-constrarg";
    private static final String ATTR_CTOR_VALUE = "config-value";
    private static final String ATTR_CTOR_TYPE = "config-type";
    private final HierarchicalConfiguration<?> configuration;
    private final NodeData<?> node;
    private final String defaultBeanClassName;
    
    public <T> XMLBeanDeclaration(final HierarchicalConfiguration<T> config, final String key) {
        this(config, key, false);
    }
    
    public <T> XMLBeanDeclaration(final HierarchicalConfiguration<T> config, final String key, final boolean optional) {
        this(config, key, optional, null);
    }
    
    public <T> XMLBeanDeclaration(final HierarchicalConfiguration<T> config, final String key, final boolean optional, final String defBeanClsName) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration must not be null!");
        }
        HierarchicalConfiguration<?> tmpconfiguration;
        try {
            tmpconfiguration = config.configurationAt(key);
        }
        catch (ConfigurationRuntimeException iex) {
            if (!optional || config.getMaxIndex(key) > 0) {
                throw iex;
            }
            tmpconfiguration = new BaseHierarchicalConfiguration();
        }
        this.node = createNodeDataFromConfiguration(tmpconfiguration);
        this.configuration = tmpconfiguration;
        this.defaultBeanClassName = defBeanClsName;
        this.initSubnodeConfiguration(this.getConfiguration());
    }
    
    public <T> XMLBeanDeclaration(final HierarchicalConfiguration<T> config) {
        this(config, null);
    }
    
    XMLBeanDeclaration(final HierarchicalConfiguration<?> config, final NodeData<?> node) {
        this.node = node;
        this.configuration = config;
        this.defaultBeanClassName = null;
        this.initSubnodeConfiguration(config);
    }
    
    public HierarchicalConfiguration<?> getConfiguration() {
        return this.configuration;
    }
    
    public String getDefaultBeanClassName() {
        return this.defaultBeanClassName;
    }
    
    @Override
    public String getBeanFactoryName() {
        return this.getConfiguration().getString("[@config-factory]", null);
    }
    
    @Override
    public Object getBeanFactoryParameter() {
        return this.getConfiguration().getProperty("[@config-factoryParam]");
    }
    
    @Override
    public String getBeanClassName() {
        return this.getConfiguration().getString("[@config-class]", this.getDefaultBeanClassName());
    }
    
    @Override
    public Map<String, Object> getBeanProperties() {
        final Map<String, Object> props = new HashMap<String, Object>();
        for (final String key : this.getAttributeNames()) {
            if (!this.isReservedAttributeName(key)) {
                props.put(key, this.interpolate(this.getNode().getAttribute(key)));
            }
        }
        return props;
    }
    
    @Override
    public Map<String, Object> getNestedBeanDeclarations() {
        final Map<String, Object> nested = new HashMap<String, Object>();
        for (final NodeData<?> child : this.getNode().getChildren()) {
            if (!this.isReservedChildName(child.nodeName())) {
                if (nested.containsKey(child.nodeName())) {
                    final Object obj = nested.get(child.nodeName());
                    List<BeanDeclaration> list;
                    if (obj instanceof List) {
                        final List<BeanDeclaration> tmpList = list = (List<BeanDeclaration>)obj;
                    }
                    else {
                        list = new ArrayList<BeanDeclaration>();
                        list.add((BeanDeclaration)obj);
                        nested.put(child.nodeName(), list);
                    }
                    list.add(this.createBeanDeclaration(child));
                }
                else {
                    nested.put(child.nodeName(), this.createBeanDeclaration(child));
                }
            }
        }
        return nested;
    }
    
    @Override
    public Collection<ConstructorArg> getConstructorArgs() {
        final Collection<ConstructorArg> args = new LinkedList<ConstructorArg>();
        for (final NodeData<?> child : this.getNode().getChildren("config-constrarg")) {
            args.add(this.createConstructorArg(child));
        }
        return args;
    }
    
    protected Object interpolate(final Object value) {
        final ConfigurationInterpolator interpolator = this.getConfiguration().getInterpolator();
        return (interpolator != null) ? interpolator.interpolate(value) : value;
    }
    
    protected boolean isReservedChildName(final String name) {
        return this.isReservedName(name);
    }
    
    protected boolean isReservedAttributeName(final String name) {
        return this.isReservedName(name);
    }
    
    protected boolean isReservedName(final String name) {
        return name == null || name.startsWith("config-");
    }
    
    protected Set<String> getAttributeNames() {
        return this.getNode().getAttributes();
    }
    
    NodeData<?> getNode() {
        return this.node;
    }
    
    BeanDeclaration createBeanDeclaration(final NodeData<?> node) {
        for (final HierarchicalConfiguration<?> config : this.getConfiguration().configurationsAt(node.escapedNodeName(this.getConfiguration()))) {
            if (node.matchesConfigRootNode(config)) {
                return new XMLBeanDeclaration(config, node);
            }
        }
        throw new ConfigurationRuntimeException("Unable to match node for " + node.nodeName());
    }
    
    private void initSubnodeConfiguration(final HierarchicalConfiguration<?> conf) {
        conf.setExpressionEngine(null);
    }
    
    private ConstructorArg createConstructorArg(final NodeData<?> child) {
        final String type = this.getAttribute(child, "config-type");
        if (isBeanDeclarationArgument(child)) {
            return ConstructorArg.forValue(this.getAttribute(child, "config-value"), type);
        }
        return ConstructorArg.forBeanDeclaration(this.createBeanDeclaration(child), type);
    }
    
    private String getAttribute(final NodeData<?> nd, final String attr) {
        final Object value = nd.getAttribute(attr);
        return (value == null) ? null : String.valueOf(this.interpolate(value));
    }
    
    private static boolean isBeanDeclarationArgument(final NodeData<?> nd) {
        return !nd.getAttributes().contains("config-class");
    }
    
    private static <T> NodeData<T> createNodeDataFromConfiguration(final HierarchicalConfiguration<T> config) {
        final NodeHandler<T> handler = config.getNodeModel().getNodeHandler();
        return new NodeData<T>(handler.getRootNode(), handler);
    }
    
    static class NodeData<T>
    {
        private final T node;
        private final NodeHandler<T> handler;
        
        public NodeData(final T nd, final NodeHandler<T> hndlr) {
            this.node = nd;
            this.handler = hndlr;
        }
        
        public String nodeName() {
            return this.handler.nodeName(this.node);
        }
        
        public String escapedNodeName(final HierarchicalConfiguration<?> config) {
            return config.getExpressionEngine().nodeKey(this.node, "", this.handler);
        }
        
        public List<NodeData<T>> getChildren() {
            return this.wrapInNodeData(this.handler.getChildren(this.node));
        }
        
        public List<NodeData<T>> getChildren(final String name) {
            return this.wrapInNodeData(this.handler.getChildren(this.node, name));
        }
        
        public Set<String> getAttributes() {
            return this.handler.getAttributes(this.node);
        }
        
        public Object getAttribute(final String key) {
            return this.handler.getAttributeValue(this.node, key);
        }
        
        public boolean matchesConfigRootNode(final HierarchicalConfiguration<?> config) {
            return config.getNodeModel().getNodeHandler().getRootNode().equals(this.node);
        }
        
        private List<NodeData<T>> wrapInNodeData(final List<T> nodes) {
            final List<NodeData<T>> result = new ArrayList<NodeData<T>>(nodes.size());
            for (final T node : nodes) {
                result.add(new NodeData<T>(node, this.handler));
            }
            return result;
        }
    }
}
