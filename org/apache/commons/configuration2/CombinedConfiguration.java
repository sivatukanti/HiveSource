// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.configuration2.tree.DefaultConfigurationKey;
import org.apache.commons.configuration2.sync.LockMode;
import org.apache.commons.configuration2.tree.UnionCombiner;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventSource;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitor;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import org.apache.commons.configuration2.tree.TreeUtils;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import java.util.Collection;
import org.apache.commons.configuration2.tree.QueryResult;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import java.util.Map;
import java.util.List;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeCombiner;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventListener;

public class CombinedConfiguration extends BaseHierarchicalConfiguration implements EventListener<ConfigurationEvent>, Cloneable
{
    public static final EventType<ConfigurationEvent> COMBINED_INVALIDATE;
    private static final DefaultExpressionEngine AT_ENGINE;
    private static final NodeCombiner DEFAULT_COMBINER;
    private static final ImmutableNode EMPTY_ROOT;
    private NodeCombiner nodeCombiner;
    private List<ConfigData> configurations;
    private Map<String, Configuration> namedConfigurations;
    private ExpressionEngine conversionExpressionEngine;
    private boolean upToDate;
    
    public CombinedConfiguration(final NodeCombiner comb) {
        this.nodeCombiner = ((comb != null) ? comb : CombinedConfiguration.DEFAULT_COMBINER);
        this.initChildCollections();
    }
    
    public CombinedConfiguration() {
        this((NodeCombiner)null);
    }
    
    public NodeCombiner getNodeCombiner() {
        this.beginRead(true);
        try {
            return this.nodeCombiner;
        }
        finally {
            this.endRead();
        }
    }
    
    public void setNodeCombiner(final NodeCombiner nodeCombiner) {
        if (nodeCombiner == null) {
            throw new IllegalArgumentException("Node combiner must not be null!");
        }
        this.beginWrite(true);
        try {
            this.nodeCombiner = nodeCombiner;
            this.invalidateInternal();
        }
        finally {
            this.endWrite();
        }
    }
    
    public ExpressionEngine getConversionExpressionEngine() {
        this.beginRead(true);
        try {
            return this.conversionExpressionEngine;
        }
        finally {
            this.endRead();
        }
    }
    
    public void setConversionExpressionEngine(final ExpressionEngine conversionExpressionEngine) {
        this.beginWrite(true);
        try {
            this.conversionExpressionEngine = conversionExpressionEngine;
        }
        finally {
            this.endWrite();
        }
    }
    
    public void addConfiguration(final Configuration config, final String name, final String at) {
        if (config == null) {
            throw new IllegalArgumentException("Added configuration must not be null!");
        }
        this.beginWrite(true);
        try {
            if (name != null && this.namedConfigurations.containsKey(name)) {
                throw new ConfigurationRuntimeException("A configuration with the name '" + name + "' already exists in this combined configuration!");
            }
            final ConfigData cd = new ConfigData(config, name, at);
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("Adding configuration " + config + " with name " + name);
            }
            this.configurations.add(cd);
            if (name != null) {
                this.namedConfigurations.put(name, config);
            }
            this.invalidateInternal();
        }
        finally {
            this.endWrite();
        }
        this.registerListenerAt(config);
    }
    
    public void addConfiguration(final Configuration config, final String name) {
        this.addConfiguration(config, name, null);
    }
    
    public void addConfiguration(final Configuration config) {
        this.addConfiguration(config, null, null);
    }
    
    public int getNumberOfConfigurations() {
        this.beginRead(true);
        try {
            return this.getNumberOfConfigurationsInternal();
        }
        finally {
            this.endRead();
        }
    }
    
    public Configuration getConfiguration(final int index) {
        this.beginRead(true);
        try {
            final ConfigData cd = this.configurations.get(index);
            return cd.getConfiguration();
        }
        finally {
            this.endRead();
        }
    }
    
    public Configuration getConfiguration(final String name) {
        this.beginRead(true);
        try {
            return this.namedConfigurations.get(name);
        }
        finally {
            this.endRead();
        }
    }
    
    public List<Configuration> getConfigurations() {
        this.beginRead(true);
        try {
            final List<Configuration> list = new ArrayList<Configuration>(this.getNumberOfConfigurationsInternal());
            for (final ConfigData cd : this.configurations) {
                list.add(cd.getConfiguration());
            }
            return list;
        }
        finally {
            this.endRead();
        }
    }
    
    public List<String> getConfigurationNameList() {
        this.beginRead(true);
        try {
            final List<String> list = new ArrayList<String>(this.getNumberOfConfigurationsInternal());
            for (final ConfigData cd : this.configurations) {
                list.add(cd.getName());
            }
            return list;
        }
        finally {
            this.endRead();
        }
    }
    
    public boolean removeConfiguration(final Configuration config) {
        for (int index = 0; index < this.getNumberOfConfigurations(); ++index) {
            if (this.configurations.get(index).getConfiguration() == config) {
                this.removeConfigurationAt(index);
                return true;
            }
        }
        return false;
    }
    
    public Configuration removeConfigurationAt(final int index) {
        final ConfigData cd = this.configurations.remove(index);
        if (cd.getName() != null) {
            this.namedConfigurations.remove(cd.getName());
        }
        this.unregisterListenerAt(cd.getConfiguration());
        this.invalidateInternal();
        return cd.getConfiguration();
    }
    
    public Configuration removeConfiguration(final String name) {
        final Configuration conf = this.getConfiguration(name);
        if (conf != null) {
            this.removeConfiguration(conf);
        }
        return conf;
    }
    
    public Set<String> getConfigurationNames() {
        this.beginRead(true);
        try {
            return this.namedConfigurations.keySet();
        }
        finally {
            this.endRead();
        }
    }
    
    public void invalidate() {
        this.beginWrite(true);
        try {
            this.invalidateInternal();
        }
        finally {
            this.endWrite();
        }
    }
    
    @Override
    public void onEvent(final ConfigurationEvent event) {
        if (event.isBeforeUpdate()) {
            this.invalidate();
        }
    }
    
    @Override
    protected void clearInternal() {
        this.unregisterListenerAtChildren();
        this.initChildCollections();
        this.invalidateInternal();
    }
    
    @Override
    public Object clone() {
        this.beginRead(false);
        try {
            final CombinedConfiguration copy = (CombinedConfiguration)super.clone();
            copy.initChildCollections();
            for (final ConfigData cd : this.configurations) {
                copy.addConfiguration(ConfigurationUtils.cloneConfiguration(cd.getConfiguration()), cd.getName(), cd.getAt());
            }
            return copy;
        }
        finally {
            this.endRead();
        }
    }
    
    public Configuration getSource(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null!");
        }
        final Set<Configuration> sources = this.getSources(key);
        if (sources.isEmpty()) {
            return null;
        }
        final Iterator<Configuration> iterator = sources.iterator();
        final Configuration source = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalArgumentException("The key " + key + " is defined by multiple sources!");
        }
        return source;
    }
    
    public Set<Configuration> getSources(final String key) {
        this.beginRead(false);
        try {
            final List<QueryResult<ImmutableNode>> results = this.fetchNodeList(key);
            final Set<Configuration> sources = new HashSet<Configuration>();
            for (final QueryResult<ImmutableNode> result : results) {
                final Set<Configuration> resultSources = this.findSourceConfigurations(result.getNode());
                if (resultSources.isEmpty()) {
                    sources.add(this);
                }
                else {
                    sources.addAll(resultSources);
                }
            }
            return sources;
        }
        finally {
            this.endRead();
        }
    }
    
    @Override
    protected void beginRead(final boolean optimize) {
        if (optimize) {
            super.beginRead(true);
            return;
        }
        boolean lockObtained = false;
        do {
            super.beginRead(false);
            if (this.isUpToDate()) {
                lockObtained = true;
            }
            else {
                this.endRead();
                this.beginWrite(false);
                this.endWrite();
            }
        } while (!lockObtained);
    }
    
    @Override
    protected void beginWrite(final boolean optimize) {
        super.beginWrite(true);
        if (optimize) {
            return;
        }
        try {
            if (!this.isUpToDate()) {
                this.getSubConfigurationParentModel().replaceRoot(this.constructCombinedNode(), this);
                this.upToDate = true;
            }
        }
        catch (RuntimeException rex) {
            this.endWrite();
            throw rex;
        }
    }
    
    private boolean isUpToDate() {
        return this.upToDate;
    }
    
    private void invalidateInternal() {
        this.upToDate = false;
        this.fireEvent(CombinedConfiguration.COMBINED_INVALIDATE, null, null, false);
    }
    
    private void initChildCollections() {
        this.configurations = new ArrayList<ConfigData>();
        this.namedConfigurations = new HashMap<String, Configuration>();
    }
    
    private ImmutableNode constructCombinedNode() {
        if (this.getNumberOfConfigurationsInternal() < 1) {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("No configurations defined for " + this);
            }
            return CombinedConfiguration.EMPTY_ROOT;
        }
        final Iterator<ConfigData> it = this.configurations.iterator();
        ImmutableNode node = it.next().getTransformedRoot();
        while (it.hasNext()) {
            node = this.nodeCombiner.combine(node, it.next().getTransformedRoot());
        }
        if (this.getLogger().isDebugEnabled()) {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            final PrintStream stream = new PrintStream(os);
            TreeUtils.printTree(stream, node);
            this.getLogger().debug(os.toString());
        }
        return node;
    }
    
    private Set<Configuration> findSourceConfigurations(final ImmutableNode node) {
        final Set<Configuration> result = new HashSet<Configuration>();
        final FindNodeVisitor<ImmutableNode> visitor = new FindNodeVisitor<ImmutableNode>(node);
        for (final ConfigData cd : this.configurations) {
            NodeTreeWalker.INSTANCE.walkBFS(cd.getRootNode(), visitor, this.getModel().getNodeHandler());
            if (visitor.isFound()) {
                result.add(cd.getConfiguration());
                visitor.reset();
            }
        }
        return result;
    }
    
    private void registerListenerAt(final Configuration configuration) {
        if (configuration instanceof EventSource) {
            ((EventSource)configuration).addEventListener(ConfigurationEvent.ANY, this);
        }
    }
    
    private void unregisterListenerAt(final Configuration configuration) {
        if (configuration instanceof EventSource) {
            ((EventSource)configuration).removeEventListener(ConfigurationEvent.ANY, this);
        }
    }
    
    private void unregisterListenerAtChildren() {
        if (this.configurations != null) {
            for (final ConfigData child : this.configurations) {
                this.unregisterListenerAt(child.getConfiguration());
            }
        }
    }
    
    private int getNumberOfConfigurationsInternal() {
        return this.configurations.size();
    }
    
    static {
        COMBINED_INVALIDATE = new EventType<ConfigurationEvent>(ConfigurationEvent.ANY, "COMBINED_INVALIDATE");
        AT_ENGINE = DefaultExpressionEngine.INSTANCE;
        DEFAULT_COMBINER = new UnionCombiner();
        EMPTY_ROOT = new ImmutableNode.Builder().create();
    }
    
    private class ConfigData
    {
        private final Configuration configuration;
        private final String name;
        private final Collection<String> atPath;
        private final String at;
        private ImmutableNode rootNode;
        
        public ConfigData(final Configuration config, final String n, final String at) {
            this.configuration = config;
            this.name = n;
            this.atPath = this.parseAt(at);
            this.at = at;
        }
        
        public Configuration getConfiguration() {
            return this.configuration;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getAt() {
            return this.at;
        }
        
        public ImmutableNode getRootNode() {
            return this.rootNode;
        }
        
        public ImmutableNode getTransformedRoot() {
            final ImmutableNode configRoot = this.getRootNodeOfConfiguration();
            return (this.atPath == null) ? configRoot : this.prependAtPath(configRoot);
        }
        
        private ImmutableNode prependAtPath(final ImmutableNode node) {
            final ImmutableNode.Builder pathBuilder = new ImmutableNode.Builder();
            final Iterator<String> pathIterator = this.atPath.iterator();
            this.prependAtPathComponent(pathBuilder, pathIterator.next(), pathIterator, node);
            return new ImmutableNode.Builder(1).addChild(pathBuilder.create()).create();
        }
        
        private void prependAtPathComponent(final ImmutableNode.Builder builder, final String currentComponent, final Iterator<String> components, final ImmutableNode orgRoot) {
            builder.name(currentComponent);
            if (components.hasNext()) {
                final ImmutableNode.Builder childBuilder = new ImmutableNode.Builder();
                this.prependAtPathComponent(childBuilder, components.next(), components, orgRoot);
                builder.addChild(childBuilder.create());
            }
            else {
                builder.addChildren(orgRoot.getChildren());
                builder.addAttributes(orgRoot.getAttributes());
                builder.value(orgRoot.getValue());
            }
        }
        
        private ImmutableNode getRootNodeOfConfiguration() {
            this.getConfiguration().lock(LockMode.READ);
            try {
                final ImmutableNode root = ConfigurationUtils.convertToHierarchical(this.getConfiguration(), CombinedConfiguration.this.conversionExpressionEngine).getNodeModel().getInMemoryRepresentation();
                return this.rootNode = root;
            }
            finally {
                this.getConfiguration().unlock(LockMode.READ);
            }
        }
        
        private Collection<String> parseAt(final String at) {
            if (at == null) {
                return null;
            }
            final Collection<String> result = new ArrayList<String>();
            final DefaultConfigurationKey.KeyIterator it = new DefaultConfigurationKey(CombinedConfiguration.AT_ENGINE, at).iterator();
            while (it.hasNext()) {
                result.add(it.nextKey());
            }
            return result;
        }
    }
}
