// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.ImmutableConfiguration;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.configuration2.reloading.CombinedReloadingController;
import org.apache.commons.configuration2.Configuration;
import java.util.LinkedList;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.BuilderParameters;
import java.util.Map;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingControllerSupport;

public class ReloadingCombinedConfigurationBuilder extends CombinedConfigurationBuilder implements ReloadingControllerSupport
{
    private ReloadingController reloadingController;
    
    public ReloadingCombinedConfigurationBuilder() {
    }
    
    public ReloadingCombinedConfigurationBuilder(final Map<String, Object> params, final boolean allowFailOnInit) {
        super(params, allowFailOnInit);
    }
    
    public ReloadingCombinedConfigurationBuilder(final Map<String, Object> params) {
        super(params);
    }
    
    @Override
    public ReloadingCombinedConfigurationBuilder configure(final BuilderParameters... params) {
        super.configure(params);
        return this;
    }
    
    @Override
    public synchronized ReloadingController getReloadingController() {
        return this.reloadingController;
    }
    
    @Override
    protected ConfigurationBuilder<? extends HierarchicalConfiguration<?>> createXMLDefinitionBuilder(final BuilderParameters builderParams) {
        return (ConfigurationBuilder<? extends HierarchicalConfiguration<?>>)new ReloadingFileBasedConfigurationBuilder(XMLConfiguration.class).configure(builderParams);
    }
    
    @Override
    protected void initResultInstance(final CombinedConfiguration result) throws ConfigurationException {
        super.initResultInstance(result);
        this.reloadingController = this.createReloadingController();
    }
    
    protected ReloadingController createReloadingController() throws ConfigurationException {
        final Collection<ReloadingController> subControllers = new LinkedList<ReloadingController>();
        final ConfigurationBuilder<? extends HierarchicalConfiguration<?>> defBuilder = this.getDefinitionBuilder();
        obtainReloadingController(subControllers, defBuilder);
        for (final ConfigurationBuilder<? extends Configuration> b : this.getChildBuilders()) {
            obtainReloadingController(subControllers, b);
        }
        final CombinedReloadingController ctrl = new CombinedReloadingController(subControllers);
        ctrl.resetInitialReloadingState();
        return ctrl;
    }
    
    public static void obtainReloadingController(final Collection<ReloadingController> subControllers, final Object builder) {
        if (builder instanceof ReloadingControllerSupport) {
            subControllers.add(((ReloadingControllerSupport)builder).getReloadingController());
        }
    }
}
