// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.reloading.ReloadingControllerSupport;
import org.apache.commons.configuration2.builder.BuilderConfigurationWrapperFactory;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import java.util.Collection;
import java.util.Arrays;

public class MultiFileConfigurationBuilderProvider extends BaseConfigurationBuilderProvider
{
    private static final String BUILDER_CLASS = "org.apache.commons.configuration2.builder.combined.MultiFileConfigurationBuilder";
    private static final String RELOADING_BUILDER_CLASS = "org.apache.commons.configuration2.builder.combined.ReloadingMultiFileConfigurationBuilder";
    private static final String PARAM_CLASS = "org.apache.commons.configuration2.builder.combined.MultiFileBuilderParametersImpl";
    
    public MultiFileConfigurationBuilderProvider(final String configCls, final String paramCls) {
        super("org.apache.commons.configuration2.builder.combined.MultiFileConfigurationBuilder", "org.apache.commons.configuration2.builder.combined.ReloadingMultiFileConfigurationBuilder", configCls, Arrays.asList(paramCls, "org.apache.commons.configuration2.builder.combined.MultiFileBuilderParametersImpl"));
    }
    
    @Override
    public ConfigurationBuilder<? extends Configuration> getConfigurationBuilder(final ConfigurationDeclaration decl) throws ConfigurationException {
        final ConfigurationBuilder<? extends Configuration> multiBuilder = super.getConfigurationBuilder(decl);
        final Configuration wrapConfig = this.createWrapperConfiguration(multiBuilder);
        return createWrapperBuilder(multiBuilder, wrapConfig);
    }
    
    private Configuration createWrapperConfiguration(final ConfigurationBuilder builder) {
        final Class<?> configClass = ConfigurationUtils.loadClassNoEx(this.getConfigurationClass());
        final Class ifcClass = (Class)(HierarchicalConfiguration.class.isAssignableFrom(configClass) ? HierarchicalConfiguration.class : Configuration.class);
        return BuilderConfigurationWrapperFactory.createBuilderConfigurationWrapper((Class<Configuration>)ifcClass, builder, BuilderConfigurationWrapperFactory.EventSourceSupport.BUILDER);
    }
    
    private static ConfigurationBuilder<? extends Configuration> createWrapperBuilder(final ConfigurationBuilder<? extends Configuration> multiBuilder, final Configuration wrapConfig) {
        if (multiBuilder instanceof ReloadingControllerSupport) {
            return new ReloadableWrapperBuilder(wrapConfig, multiBuilder);
        }
        return new WrapperBuilder(wrapConfig, multiBuilder);
    }
    
    private static class WrapperBuilder implements ConfigurationBuilder<Configuration>
    {
        private final Configuration configuration;
        private final ConfigurationBuilder<? extends Configuration> builder;
        
        public WrapperBuilder(final Configuration conf, final ConfigurationBuilder<? extends Configuration> bldr) {
            this.configuration = conf;
            this.builder = bldr;
        }
        
        @Override
        public Configuration getConfiguration() throws ConfigurationException {
            return this.configuration;
        }
        
        @Override
        public <T extends Event> void addEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
            this.builder.addEventListener(eventType, listener);
        }
        
        @Override
        public <T extends Event> boolean removeEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
            return this.builder.removeEventListener(eventType, listener);
        }
    }
    
    private static class ReloadableWrapperBuilder extends WrapperBuilder implements ReloadingControllerSupport
    {
        private final ReloadingControllerSupport ctrlSupport;
        
        public ReloadableWrapperBuilder(final Configuration conf, final ConfigurationBuilder<? extends Configuration> bldr) {
            super(conf, bldr);
            this.ctrlSupport = (ReloadingControllerSupport)bldr;
        }
        
        @Override
        public ReloadingController getReloadingController() {
            return this.ctrlSupport.getReloadingController();
        }
    }
}
