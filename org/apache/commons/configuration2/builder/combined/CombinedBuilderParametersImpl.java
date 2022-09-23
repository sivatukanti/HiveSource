// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.builder.DefaultParametersHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashMap;
import org.apache.commons.configuration2.builder.DefaultParametersManager;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;

public class CombinedBuilderParametersImpl extends BasicBuilderParameters implements CombinedBuilderProperties<CombinedBuilderParametersImpl>
{
    private static final String PARAM_KEY;
    private ConfigurationBuilder<? extends HierarchicalConfiguration<?>> definitionBuilder;
    private BuilderParameters definitionBuilderParameters;
    private final Map<String, ConfigurationBuilderProvider> providers;
    private final Collection<BuilderParameters> childParameters;
    private DefaultParametersManager childDefaultParametersManager;
    private String basePath;
    private boolean inheritSettings;
    
    public CombinedBuilderParametersImpl() {
        this.providers = new HashMap<String, ConfigurationBuilderProvider>();
        this.childParameters = new LinkedList<BuilderParameters>();
        this.inheritSettings = true;
    }
    
    public static CombinedBuilderParametersImpl fromParameters(final Map<String, ?> params) {
        return fromParameters(params, false);
    }
    
    public static CombinedBuilderParametersImpl fromParameters(final Map<String, ?> params, final boolean createIfMissing) {
        CombinedBuilderParametersImpl result = (CombinedBuilderParametersImpl)params.get(CombinedBuilderParametersImpl.PARAM_KEY);
        if (result == null && createIfMissing) {
            result = new CombinedBuilderParametersImpl();
        }
        return result;
    }
    
    @Override
    public void inheritFrom(final Map<String, ?> source) {
        super.inheritFrom(source);
        final CombinedBuilderParametersImpl srcParams = fromParameters(source);
        if (srcParams != null) {
            this.setChildDefaultParametersManager(srcParams.getChildDefaultParametersManager());
            this.setInheritSettings(srcParams.isInheritSettings());
        }
    }
    
    public boolean isInheritSettings() {
        return this.inheritSettings;
    }
    
    @Override
    public CombinedBuilderParametersImpl setInheritSettings(final boolean inheritSettings) {
        this.inheritSettings = inheritSettings;
        return this;
    }
    
    public ConfigurationBuilder<? extends HierarchicalConfiguration<?>> getDefinitionBuilder() {
        return this.definitionBuilder;
    }
    
    @Override
    public CombinedBuilderParametersImpl setDefinitionBuilder(final ConfigurationBuilder<? extends HierarchicalConfiguration<?>> builder) {
        this.definitionBuilder = builder;
        return this;
    }
    
    @Override
    public CombinedBuilderParametersImpl registerProvider(final String tagName, final ConfigurationBuilderProvider provider) {
        if (tagName == null) {
            throw new IllegalArgumentException("Tag name must not be null!");
        }
        if (provider == null) {
            throw new IllegalArgumentException("Provider must not be null!");
        }
        this.providers.put(tagName, provider);
        return this;
    }
    
    public CombinedBuilderParametersImpl registerMissingProviders(final Map<String, ConfigurationBuilderProvider> providers) {
        if (providers == null) {
            throw new IllegalArgumentException("Map with providers must not be null!");
        }
        for (final Map.Entry<String, ConfigurationBuilderProvider> e : providers.entrySet()) {
            if (!this.providers.containsKey(e.getKey())) {
                this.registerProvider(e.getKey(), e.getValue());
            }
        }
        return this;
    }
    
    public CombinedBuilderParametersImpl registerMissingProviders(final CombinedBuilderParametersImpl params) {
        if (params == null) {
            throw new IllegalArgumentException("Source parameters must not be null!");
        }
        return this.registerMissingProviders(params.getProviders());
    }
    
    public Map<String, ConfigurationBuilderProvider> getProviders() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends ConfigurationBuilderProvider>)this.providers);
    }
    
    public ConfigurationBuilderProvider providerForTag(final String tagName) {
        return this.providers.get(tagName);
    }
    
    public String getBasePath() {
        return this.basePath;
    }
    
    @Override
    public CombinedBuilderParametersImpl setBasePath(final String path) {
        this.basePath = path;
        return this;
    }
    
    public BuilderParameters getDefinitionBuilderParameters() {
        return this.definitionBuilderParameters;
    }
    
    @Override
    public CombinedBuilderParametersImpl setDefinitionBuilderParameters(final BuilderParameters params) {
        this.definitionBuilderParameters = params;
        return this;
    }
    
    public Collection<? extends BuilderParameters> getDefaultChildParameters() {
        return new ArrayList<BuilderParameters>(this.childParameters);
    }
    
    public DefaultParametersManager getChildDefaultParametersManager() {
        if (this.childDefaultParametersManager == null) {
            this.childDefaultParametersManager = new DefaultParametersManager();
        }
        return this.childDefaultParametersManager;
    }
    
    @Override
    public CombinedBuilderParametersImpl setChildDefaultParametersManager(final DefaultParametersManager manager) {
        this.childDefaultParametersManager = manager;
        return this;
    }
    
    @Override
    public <D> CombinedBuilderParametersImpl registerChildDefaultsHandler(final Class<D> paramClass, final DefaultParametersHandler<? super D> handler) {
        this.getChildDefaultParametersManager().registerDefaultsHandler(paramClass, handler);
        return this;
    }
    
    @Override
    public <D> CombinedBuilderParametersImpl registerChildDefaultsHandler(final Class<D> paramClass, final DefaultParametersHandler<? super D> handler, final Class<?> startClass) {
        this.getChildDefaultParametersManager().registerDefaultsHandler(paramClass, handler, startClass);
        return this;
    }
    
    @Override
    public Map<String, Object> getParameters() {
        final Map<String, Object> params = super.getParameters();
        params.put(CombinedBuilderParametersImpl.PARAM_KEY, this);
        return params;
    }
    
    @Override
    public CombinedBuilderParametersImpl clone() {
        final CombinedBuilderParametersImpl copy = (CombinedBuilderParametersImpl)super.clone();
        copy.setDefinitionBuilderParameters((BuilderParameters)ConfigurationUtils.cloneIfPossible(this.getDefinitionBuilderParameters()));
        return copy;
    }
    
    static {
        PARAM_KEY = "config-" + CombinedBuilderParametersImpl.class.getName();
    }
}
