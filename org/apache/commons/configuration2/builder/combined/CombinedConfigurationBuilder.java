// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import java.util.LinkedList;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import java.net.URL;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.ConfigurationLookup;
import org.apache.commons.configuration2.builder.FileBasedBuilderProperties;
import org.apache.commons.configuration2.builder.XMLBuilderProperties;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.beanutils.BeanHelper;
import org.apache.commons.configuration2.resolver.CatalogResolver;
import org.xml.sax.EntityResolver;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.io.FileSystem;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration2.interpol.Lookup;
import java.util.HashMap;
import org.apache.commons.configuration2.tree.UnionCombiner;
import org.apache.commons.configuration2.tree.NodeCombiner;
import org.apache.commons.configuration2.tree.OverrideCombiner;
import org.apache.commons.configuration2.beanutils.CombinedBeanDeclaration;
import org.apache.commons.configuration2.beanutils.XMLBeanDeclaration;
import org.apache.commons.configuration2.beanutils.BeanDeclaration;
import java.util.Collection;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.builder.XMLBuilderParametersImpl;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import java.util.Map;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;

public class CombinedConfigurationBuilder extends BasicConfigurationBuilder<CombinedConfiguration>
{
    public static final String ADDITIONAL_NAME;
    static final String CONFIG_BEAN_FACTORY_NAME;
    static final String ATTR_NAME = "[@config-name]";
    static final String ATTR_ATNAME = "at";
    static final String ATTR_AT_RES = "[@config-at]";
    static final String ATTR_AT = "[@at]";
    static final String ATTR_OPTIONALNAME = "optional";
    static final String ATTR_OPTIONAL_RES = "[@config-optional]";
    static final String ATTR_OPTIONAL = "[@optional]";
    static final String ATTR_FORCECREATE = "[@config-forceCreate]";
    static final String ATTR_RELOAD = "[@config-reload]";
    static final String KEY_SYSTEM_PROPS = "[@systemProperties]";
    static final String SEC_HEADER = "header";
    static final String KEY_UNION = "additional";
    static final String[] CONFIG_SECTIONS;
    static final String KEY_OVERRIDE = "override";
    static final String KEY_OVERRIDE_LIST = "header.combiner.override.list-nodes.node";
    static final String KEY_ADDITIONAL_LIST = "header.combiner.additional.list-nodes.node";
    static final String KEY_CONFIGURATION_PROVIDERS = "header.providers.provider";
    static final String KEY_PROVIDER_KEY = "[@config-tag]";
    static final String KEY_CONFIGURATION_LOOKUPS = "header.lookups.lookup";
    static final String KEY_ENTITY_RESOLVER = "header.entity-resolver";
    static final String KEY_LOOKUP_KEY = "[@config-prefix]";
    static final String FILE_SYSTEM = "header.fileSystem";
    static final String KEY_RESULT = "header.result";
    static final String KEY_COMBINER = "header.result.nodeCombiner";
    static final String EXT_XML = "xml";
    private static final String BASIC_BUILDER = "org.apache.commons.configuration2.builder.BasicConfigurationBuilder";
    private static final String FILE_BUILDER = "org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder";
    private static final String RELOADING_BUILDER = "org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder";
    private static final String FILE_PARAMS = "org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl";
    private static final ConfigurationBuilderProvider PROPERTIES_PROVIDER;
    private static final ConfigurationBuilderProvider XML_PROVIDER;
    private static final BaseConfigurationBuilderProvider JNDI_PROVIDER;
    private static final BaseConfigurationBuilderProvider SYSTEM_PROVIDER;
    private static final BaseConfigurationBuilderProvider INI_PROVIDER;
    private static final BaseConfigurationBuilderProvider ENV_PROVIDER;
    private static final BaseConfigurationBuilderProvider PLIST_PROVIDER;
    private static final BaseConfigurationBuilderProvider COMBINED_PROVIDER;
    private static final MultiFileConfigurationBuilderProvider MULTI_XML_PROVIDER;
    private static final String[] DEFAULT_TAGS;
    private static final ConfigurationBuilderProvider[] DEFAULT_PROVIDERS;
    private static final Map<String, ConfigurationBuilderProvider> DEFAULT_PROVIDERS_MAP;
    private ConfigurationBuilder<? extends HierarchicalConfiguration<?>> definitionBuilder;
    private HierarchicalConfiguration<?> definitionConfiguration;
    private ConfigurationSourceData sourceData;
    private CombinedBuilderParametersImpl currentParameters;
    private XMLBuilderParametersImpl currentXMLParameters;
    private CombinedConfiguration currentConfiguration;
    private ConfigurationInterpolator parentInterpolator;
    
    public CombinedConfigurationBuilder() {
        super(CombinedConfiguration.class);
    }
    
    public CombinedConfigurationBuilder(final Map<String, Object> params) {
        super(CombinedConfiguration.class, params);
    }
    
    public CombinedConfigurationBuilder(final Map<String, Object> params, final boolean allowFailOnInit) {
        super(CombinedConfiguration.class, params, allowFailOnInit);
    }
    
    public synchronized ConfigurationBuilder<? extends HierarchicalConfiguration<?>> getDefinitionBuilder() throws ConfigurationException {
        if (this.definitionBuilder == null) {
            this.addDefinitionBuilderChangeListener(this.definitionBuilder = this.setupDefinitionBuilder(this.getParameters()));
        }
        return this.definitionBuilder;
    }
    
    @Override
    public CombinedConfigurationBuilder configure(final BuilderParameters... params) {
        super.configure(params);
        return this;
    }
    
    public synchronized ConfigurationBuilder<? extends Configuration> getNamedBuilder(final String name) throws ConfigurationException {
        if (this.sourceData == null) {
            throw new ConfigurationException("Information about child builders has not been setup yet! Call getConfiguration() first.");
        }
        final ConfigurationBuilder<? extends Configuration> builder = this.sourceData.getNamedBuilder(name);
        if (builder == null) {
            throw new ConfigurationException("Builder cannot be resolved: " + name);
        }
        return builder;
    }
    
    public synchronized Set<String> builderNames() {
        if (this.sourceData == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet((Set<? extends String>)this.sourceData.builderNames());
    }
    
    @Override
    public synchronized void resetParameters() {
        super.resetParameters();
        this.definitionBuilder = null;
        this.definitionConfiguration = null;
        this.currentParameters = null;
        this.currentXMLParameters = null;
        if (this.sourceData != null) {
            this.sourceData.cleanUp();
            this.sourceData = null;
        }
    }
    
    protected ConfigurationBuilder<? extends HierarchicalConfiguration<?>> setupDefinitionBuilder(final Map<String, Object> params) throws ConfigurationException {
        final CombinedBuilderParametersImpl cbParams = CombinedBuilderParametersImpl.fromParameters(params);
        if (cbParams != null) {
            final ConfigurationBuilder<? extends HierarchicalConfiguration<?>> defBuilder = cbParams.getDefinitionBuilder();
            if (defBuilder != null) {
                return defBuilder;
            }
            if (cbParams.getDefinitionBuilderParameters() != null) {
                return this.createXMLDefinitionBuilder(cbParams.getDefinitionBuilderParameters());
            }
        }
        final BuilderParameters fileParams = FileBasedBuilderParametersImpl.fromParameters(params);
        if (fileParams != null) {
            return this.createXMLDefinitionBuilder(fileParams);
        }
        throw new ConfigurationException("No builder for configuration definition specified!");
    }
    
    protected ConfigurationBuilder<? extends HierarchicalConfiguration<?>> createXMLDefinitionBuilder(final BuilderParameters builderParams) {
        return (ConfigurationBuilder<? extends HierarchicalConfiguration<?>>)new FileBasedConfigurationBuilder(XMLConfiguration.class).configure(builderParams);
    }
    
    protected HierarchicalConfiguration<?> getDefinitionConfiguration() throws ConfigurationException {
        if (this.definitionConfiguration == null) {
            this.definitionConfiguration = (HierarchicalConfiguration<?>)this.getDefinitionBuilder().getConfiguration();
        }
        return this.definitionConfiguration;
    }
    
    protected synchronized Collection<ConfigurationBuilder<? extends Configuration>> getChildBuilders() {
        return this.sourceData.getChildBuilders();
    }
    
    @Override
    protected BeanDeclaration createResultDeclaration(final Map<String, Object> params) throws ConfigurationException {
        final BeanDeclaration paramsDecl = super.createResultDeclaration(params);
        final XMLBeanDeclaration resultDecl = new XMLBeanDeclaration(this.getDefinitionConfiguration(), "header.result", true, CombinedConfiguration.class.getName());
        return new CombinedBeanDeclaration(new BeanDeclaration[] { resultDecl, paramsDecl });
    }
    
    @Override
    protected void initResultInstance(final CombinedConfiguration result) throws ConfigurationException {
        super.initResultInstance(result);
        this.currentConfiguration = result;
        final HierarchicalConfiguration<?> config = this.getDefinitionConfiguration();
        if (config.getMaxIndex("header.result.nodeCombiner") < 0) {
            result.setNodeCombiner(new OverrideCombiner());
        }
        this.setUpCurrentParameters();
        initNodeCombinerListNodes(result, config, "header.combiner.override.list-nodes.node");
        this.registerConfiguredProviders(config);
        this.setUpCurrentXMLParameters();
        this.currentXMLParameters.setFileSystem(this.initFileSystem(config));
        this.initSystemProperties(config, this.getBasePath());
        this.registerConfiguredLookups(config, result);
        this.configureEntityResolver(config, this.currentXMLParameters);
        this.setUpParentInterpolator(this.currentConfiguration, config);
        final ConfigurationSourceData data = this.getSourceData();
        data.createAndAddConfigurations(result, data.getOverrideSources());
        if (!data.getUnionSources().isEmpty()) {
            final CombinedConfiguration addConfig = this.createAdditionalsConfiguration(result);
            result.addConfiguration(addConfig, CombinedConfigurationBuilder.ADDITIONAL_NAME);
            initNodeCombinerListNodes(addConfig, config, "header.combiner.additional.list-nodes.node");
            data.createAndAddConfigurations(addConfig, data.getUnionSources());
        }
        result.isEmpty();
        this.currentConfiguration = null;
    }
    
    protected CombinedConfiguration createAdditionalsConfiguration(final CombinedConfiguration resultConfig) {
        final CombinedConfiguration addConfig = new CombinedConfiguration(new UnionCombiner());
        addConfig.setListDelimiterHandler(resultConfig.getListDelimiterHandler());
        return addConfig;
    }
    
    protected void registerConfiguredLookups(final HierarchicalConfiguration<?> defConfig, final Configuration resultConfig) throws ConfigurationException {
        final Map<String, Lookup> lookups = new HashMap<String, Lookup>();
        final List<? extends HierarchicalConfiguration<?>> nodes = defConfig.configurationsAt("header.lookups.lookup");
        for (final HierarchicalConfiguration<?> config : nodes) {
            final XMLBeanDeclaration decl = new XMLBeanDeclaration(config);
            final String key = config.getString("[@config-prefix]");
            final Lookup lookup = (Lookup)this.fetchBeanHelper().createBean(decl);
            lookups.put(key, lookup);
        }
        if (!lookups.isEmpty()) {
            final ConfigurationInterpolator defCI = defConfig.getInterpolator();
            if (defCI != null) {
                defCI.registerLookups(lookups);
            }
            resultConfig.getInterpolator().registerLookups(lookups);
        }
    }
    
    protected FileSystem initFileSystem(final HierarchicalConfiguration<?> config) throws ConfigurationException {
        if (config.getMaxIndex("header.fileSystem") == 0) {
            final XMLBeanDeclaration decl = new XMLBeanDeclaration(config, "header.fileSystem");
            return (FileSystem)this.fetchBeanHelper().createBean(decl);
        }
        return null;
    }
    
    protected void initSystemProperties(final HierarchicalConfiguration<?> config, final String basePath) throws ConfigurationException {
        final String fileName = config.getString("[@systemProperties]");
        if (fileName != null) {
            try {
                SystemConfiguration.setSystemProperties(basePath, fileName);
            }
            catch (Exception ex) {
                throw new ConfigurationException("Error setting system properties from " + fileName, ex);
            }
        }
    }
    
    protected void configureEntityResolver(final HierarchicalConfiguration<?> config, final XMLBuilderParametersImpl xmlParams) throws ConfigurationException {
        if (config.getMaxIndex("header.entity-resolver") == 0) {
            final XMLBeanDeclaration decl = new XMLBeanDeclaration(config, "header.entity-resolver", true);
            final EntityResolver resolver = (EntityResolver)this.fetchBeanHelper().createBean(decl, CatalogResolver.class);
            final FileSystem fileSystem = xmlParams.getFileHandler().getFileSystem();
            if (fileSystem != null) {
                BeanHelper.setProperty(resolver, "fileSystem", fileSystem);
            }
            final String basePath = xmlParams.getFileHandler().getBasePath();
            if (basePath != null) {
                BeanHelper.setProperty(resolver, "baseDir", basePath);
            }
            final ConfigurationInterpolator ci = new ConfigurationInterpolator();
            ci.registerLookups(this.fetchPrefixLookups());
            BeanHelper.setProperty(resolver, "interpolator", ci);
            xmlParams.setEntityResolver(resolver);
        }
    }
    
    protected ConfigurationBuilderProvider providerForTag(final String tagName) {
        return this.currentParameters.providerForTag(tagName);
    }
    
    protected void initChildBuilderParameters(final BuilderParameters params) {
        this.initDefaultChildParameters(params);
        if (params instanceof BasicBuilderParameters) {
            this.initChildBasicParameters((BasicBuilderParameters)params);
        }
        if (params instanceof XMLBuilderProperties) {
            this.initChildXMLParameters((XMLBuilderProperties<?>)params);
        }
        if (params instanceof FileBasedBuilderProperties) {
            this.initChildFileBasedParameters((FileBasedBuilderProperties<?>)params);
        }
        if (params instanceof CombinedBuilderParametersImpl) {
            this.initChildCombinedParameters((CombinedBuilderParametersImpl)params);
        }
    }
    
    void initChildEventListeners(final BasicConfigurationBuilder<? extends Configuration> dest) {
        this.copyEventListeners(dest);
    }
    
    CombinedConfiguration getConfigurationUnderConstruction() {
        return this.currentConfiguration;
    }
    
    void initBean(final Object bean, final BeanDeclaration decl) {
        this.fetchBeanHelper().initBean(bean, decl);
    }
    
    private void setUpCurrentParameters() {
        (this.currentParameters = CombinedBuilderParametersImpl.fromParameters(this.getParameters(), true)).registerMissingProviders(CombinedConfigurationBuilder.DEFAULT_PROVIDERS_MAP);
    }
    
    private void setUpCurrentXMLParameters() throws ConfigurationException {
        this.currentXMLParameters = new XMLBuilderParametersImpl();
        this.initDefaultBasePath();
    }
    
    private void setUpParentInterpolator(final Configuration resultConfig, final Configuration defConfig) {
        (this.parentInterpolator = new ConfigurationInterpolator()).addDefaultLookup(new ConfigurationLookup(resultConfig));
        final ConfigurationInterpolator defInterpolator = defConfig.getInterpolator();
        if (defInterpolator != null) {
            defInterpolator.setParentInterpolator(this.parentInterpolator);
        }
    }
    
    private void initDefaultBasePath() throws ConfigurationException {
        assert this.currentParameters != null : "Current parameters undefined!";
        if (this.currentParameters.getBasePath() != null) {
            this.currentXMLParameters.setBasePath(this.currentParameters.getBasePath());
        }
        else {
            final ConfigurationBuilder<? extends HierarchicalConfiguration<?>> defBuilder = this.getDefinitionBuilder();
            if (defBuilder instanceof FileBasedConfigurationBuilder) {
                final FileBasedConfigurationBuilder fileBuilder = (FileBasedConfigurationBuilder)defBuilder;
                final URL url = fileBuilder.getFileHandler().getURL();
                this.currentXMLParameters.setBasePath((url != null) ? url.toExternalForm() : fileBuilder.getFileHandler().getBasePath());
            }
        }
    }
    
    private void initDefaultChildParameters(final BuilderParameters params) {
        this.currentParameters.getChildDefaultParametersManager().initializeParameters(params);
    }
    
    private void initChildBasicParameters(final BasicBuilderParameters params) {
        params.setPrefixLookups(this.fetchPrefixLookups());
        params.setParentInterpolator(this.parentInterpolator);
        if (this.currentParameters.isInheritSettings()) {
            params.inheritFrom(this.getParameters());
        }
    }
    
    private void initChildFileBasedParameters(final FileBasedBuilderProperties<?> params) {
        params.setBasePath(this.getBasePath());
        params.setFileSystem(this.currentXMLParameters.getFileHandler().getFileSystem());
    }
    
    private void initChildXMLParameters(final XMLBuilderProperties<?> params) {
        params.setEntityResolver(this.currentXMLParameters.getEntityResolver());
    }
    
    private void initChildCombinedParameters(final CombinedBuilderParametersImpl params) {
        params.registerMissingProviders(this.currentParameters);
        params.setBasePath(this.getBasePath());
    }
    
    private ConfigurationSourceData getSourceData() throws ConfigurationException {
        if (this.sourceData == null) {
            if (this.currentParameters == null) {
                this.setUpCurrentParameters();
                this.setUpCurrentXMLParameters();
            }
            this.sourceData = this.createSourceData();
        }
        return this.sourceData;
    }
    
    private ConfigurationSourceData createSourceData() throws ConfigurationException {
        final ConfigurationSourceData result = new ConfigurationSourceData();
        result.initFromDefinitionConfiguration(this.getDefinitionConfiguration());
        return result;
    }
    
    private String getBasePath() {
        return this.currentXMLParameters.getFileHandler().getBasePath();
    }
    
    private void registerConfiguredProviders(final HierarchicalConfiguration<?> defConfig) throws ConfigurationException {
        final List<? extends HierarchicalConfiguration<?>> nodes = defConfig.configurationsAt("header.providers.provider");
        for (final HierarchicalConfiguration<?> config : nodes) {
            final XMLBeanDeclaration decl = new XMLBeanDeclaration(config);
            final String key = config.getString("[@config-tag]");
            this.currentParameters.registerProvider(key, (ConfigurationBuilderProvider)this.fetchBeanHelper().createBean(decl));
        }
    }
    
    private void addDefinitionBuilderChangeListener(final ConfigurationBuilder<? extends HierarchicalConfiguration<?>> defBuilder) {
        defBuilder.addEventListener(ConfigurationBuilderEvent.RESET, new EventListener<ConfigurationBuilderEvent>() {
            @Override
            public void onEvent(final ConfigurationBuilderEvent event) {
                synchronized (CombinedConfigurationBuilder.this) {
                    CombinedConfigurationBuilder.this.reset();
                    CombinedConfigurationBuilder.this.definitionBuilder = defBuilder;
                }
            }
        });
    }
    
    private Map<String, ? extends Lookup> fetchPrefixLookups() {
        final CombinedConfiguration cc = this.getConfigurationUnderConstruction();
        return (cc != null) ? cc.getInterpolator().getLookups() : null;
    }
    
    private static void initNodeCombinerListNodes(final CombinedConfiguration cc, final HierarchicalConfiguration<?> defConfig, final String key) {
        final List<Object> listNodes = defConfig.getList(key);
        for (final Object listNode : listNodes) {
            cc.getNodeCombiner().addListNode((String)listNode);
        }
    }
    
    private static Map<String, ConfigurationBuilderProvider> createDefaultProviders() {
        final Map<String, ConfigurationBuilderProvider> providers = new HashMap<String, ConfigurationBuilderProvider>();
        for (int i = 0; i < CombinedConfigurationBuilder.DEFAULT_TAGS.length; ++i) {
            providers.put(CombinedConfigurationBuilder.DEFAULT_TAGS[i], CombinedConfigurationBuilder.DEFAULT_PROVIDERS[i]);
        }
        return providers;
    }
    
    static {
        ADDITIONAL_NAME = CombinedConfigurationBuilder.class.getName() + "/ADDITIONAL_CONFIG";
        CONFIG_BEAN_FACTORY_NAME = CombinedConfigurationBuilder.class.getName() + ".CONFIG_BEAN_FACTORY_NAME";
        CONFIG_SECTIONS = new String[] { "additional", "override", "header" };
        PROPERTIES_PROVIDER = new FileExtensionConfigurationBuilderProvider("org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder", "org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder", "org.apache.commons.configuration2.XMLPropertiesConfiguration", "org.apache.commons.configuration2.PropertiesConfiguration", "xml", Collections.singletonList("org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl"));
        XML_PROVIDER = new BaseConfigurationBuilderProvider("org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder", "org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder", "org.apache.commons.configuration2.XMLConfiguration", Collections.singletonList("org.apache.commons.configuration2.builder.XMLBuilderParametersImpl"));
        JNDI_PROVIDER = new BaseConfigurationBuilderProvider("org.apache.commons.configuration2.builder.BasicConfigurationBuilder", null, "org.apache.commons.configuration2.JNDIConfiguration", Collections.singletonList("org.apache.commons.configuration2.builder.JndiBuilderParametersImpl"));
        SYSTEM_PROVIDER = new BaseConfigurationBuilderProvider("org.apache.commons.configuration2.builder.BasicConfigurationBuilder", null, "org.apache.commons.configuration2.SystemConfiguration", Collections.singletonList("org.apache.commons.configuration2.builder.BasicBuilderParameters"));
        INI_PROVIDER = new BaseConfigurationBuilderProvider("org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder", "org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder", "org.apache.commons.configuration2.INIConfiguration", Collections.singletonList("org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl"));
        ENV_PROVIDER = new BaseConfigurationBuilderProvider("org.apache.commons.configuration2.builder.BasicConfigurationBuilder", null, "org.apache.commons.configuration2.EnvironmentConfiguration", Collections.singletonList("org.apache.commons.configuration2.builder.BasicBuilderParameters"));
        PLIST_PROVIDER = new FileExtensionConfigurationBuilderProvider("org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder", "org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder", "org.apache.commons.configuration2.plist.XMLPropertyListConfiguration", "org.apache.commons.configuration2.plist.PropertyListConfiguration", "xml", Collections.singletonList("org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl"));
        COMBINED_PROVIDER = new CombinedConfigurationBuilderProvider();
        MULTI_XML_PROVIDER = new MultiFileConfigurationBuilderProvider("org.apache.commons.configuration2.XMLConfiguration", "org.apache.commons.configuration2.builder.XMLBuilderParametersImpl");
        DEFAULT_TAGS = new String[] { "properties", "xml", "hierarchicalXml", "plist", "ini", "system", "env", "jndi", "configuration", "multiFile" };
        DEFAULT_PROVIDERS = new ConfigurationBuilderProvider[] { CombinedConfigurationBuilder.PROPERTIES_PROVIDER, CombinedConfigurationBuilder.XML_PROVIDER, CombinedConfigurationBuilder.XML_PROVIDER, CombinedConfigurationBuilder.PLIST_PROVIDER, CombinedConfigurationBuilder.INI_PROVIDER, CombinedConfigurationBuilder.SYSTEM_PROVIDER, CombinedConfigurationBuilder.ENV_PROVIDER, CombinedConfigurationBuilder.JNDI_PROVIDER, CombinedConfigurationBuilder.COMBINED_PROVIDER, CombinedConfigurationBuilder.MULTI_XML_PROVIDER };
        DEFAULT_PROVIDERS_MAP = createDefaultProviders();
    }
    
    private class ConfigurationSourceData
    {
        private final Collection<HierarchicalConfiguration<?>> overrideBuilders;
        private final Collection<HierarchicalConfiguration<?>> unionBuilders;
        private final Map<String, ConfigurationBuilder<? extends Configuration>> namedBuilders;
        private final Collection<ConfigurationBuilder<? extends Configuration>> allBuilders;
        private EventListener<ConfigurationBuilderEvent> changeListener;
        
        public ConfigurationSourceData() {
            this.overrideBuilders = new LinkedList<HierarchicalConfiguration<?>>();
            this.unionBuilders = new LinkedList<HierarchicalConfiguration<?>>();
            this.namedBuilders = new HashMap<String, ConfigurationBuilder<? extends Configuration>>();
            this.allBuilders = new LinkedList<ConfigurationBuilder<? extends Configuration>>();
        }
        
        public void initFromDefinitionConfiguration(final HierarchicalConfiguration<?> config) throws ConfigurationException {
            this.overrideBuilders.addAll(this.fetchTopLevelOverrideConfigs(config));
            this.overrideBuilders.addAll(config.childConfigurationsAt("override"));
            this.unionBuilders.addAll(config.childConfigurationsAt("additional"));
        }
        
        public void createAndAddConfigurations(final CombinedConfiguration ccResult, final Collection<HierarchicalConfiguration<?>> srcDecl) throws ConfigurationException {
            this.createBuilderChangeListener();
            for (final HierarchicalConfiguration<?> src : srcDecl) {
                final ConfigurationDeclaration decl = new ConfigurationDeclaration(CombinedConfigurationBuilder.this, src);
                final ConfigurationBuilder<? extends Configuration> builder = this.createConfigurationBuilder(src, decl);
                this.addChildConfiguration(ccResult, decl, builder);
            }
        }
        
        public void cleanUp() {
            for (final ConfigurationBuilder<?> b : this.getChildBuilders()) {
                b.removeEventListener(ConfigurationBuilderEvent.RESET, this.changeListener);
            }
            this.namedBuilders.clear();
        }
        
        public Collection<ConfigurationBuilder<? extends Configuration>> getChildBuilders() {
            return this.allBuilders;
        }
        
        public Collection<HierarchicalConfiguration<?>> getOverrideSources() {
            return this.overrideBuilders;
        }
        
        public Collection<HierarchicalConfiguration<?>> getUnionSources() {
            return this.unionBuilders;
        }
        
        public ConfigurationBuilder<? extends Configuration> getNamedBuilder(final String name) {
            return this.namedBuilders.get(name);
        }
        
        public Set<String> builderNames() {
            return this.namedBuilders.keySet();
        }
        
        private ConfigurationBuilder<? extends Configuration> createConfigurationBuilder(final HierarchicalConfiguration<?> src, final ConfigurationDeclaration decl) throws ConfigurationException {
            final ConfigurationBuilderProvider provider = CombinedConfigurationBuilder.this.providerForTag(src.getRootElementName());
            if (provider == null) {
                throw new ConfigurationException("Unsupported configuration source: " + src.getRootElementName());
            }
            final ConfigurationBuilder<? extends Configuration> builder = provider.getConfigurationBuilder(decl);
            if (decl.getName() != null) {
                this.namedBuilders.put(decl.getName(), builder);
            }
            this.allBuilders.add(builder);
            builder.addEventListener(ConfigurationBuilderEvent.RESET, this.changeListener);
            return builder;
        }
        
        private void addChildConfiguration(final CombinedConfiguration ccResult, final ConfigurationDeclaration decl, final ConfigurationBuilder<? extends Configuration> builder) throws ConfigurationException {
            try {
                ccResult.addConfiguration((Configuration)builder.getConfiguration(), decl.getName(), decl.getAt());
            }
            catch (ConfigurationException cex) {
                if (!decl.isOptional()) {
                    throw cex;
                }
            }
        }
        
        private void createBuilderChangeListener() {
            this.changeListener = new EventListener<ConfigurationBuilderEvent>() {
                @Override
                public void onEvent(final ConfigurationBuilderEvent event) {
                    CombinedConfigurationBuilder.this.resetResult();
                }
            };
        }
        
        private List<? extends HierarchicalConfiguration<?>> fetchTopLevelOverrideConfigs(final HierarchicalConfiguration<?> config) {
            final List<? extends HierarchicalConfiguration<?>> configs = config.childConfigurationsAt(null);
            final Iterator<? extends HierarchicalConfiguration<?>> it = configs.iterator();
            while (it.hasNext()) {
                final String nodeName = ((HierarchicalConfiguration)it.next()).getRootElementName();
                for (final String element : CombinedConfigurationBuilder.CONFIG_SECTIONS) {
                    if (element.equals(nodeName)) {
                        it.remove();
                        break;
                    }
                }
            }
            return configs;
        }
    }
}
