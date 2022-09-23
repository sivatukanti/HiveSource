// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.configuration2.reloading.CombinedReloadingController;
import java.util.Collections;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import java.util.Map;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingControllerSupport;
import org.apache.commons.configuration2.FileBasedConfiguration;

public class ReloadingMultiFileConfigurationBuilder<T extends FileBasedConfiguration> extends MultiFileConfigurationBuilder<T> implements ReloadingControllerSupport
{
    private final ReloadingController reloadingController;
    
    public ReloadingMultiFileConfigurationBuilder(final Class<T> resCls, final Map<String, Object> params, final boolean allowFailOnInit) {
        super(resCls, params, allowFailOnInit);
        this.reloadingController = this.createReloadingController();
    }
    
    public ReloadingMultiFileConfigurationBuilder(final Class<T> resCls, final Map<String, Object> params) {
        super(resCls, params);
        this.reloadingController = this.createReloadingController();
    }
    
    public ReloadingMultiFileConfigurationBuilder(final Class<T> resCls) {
        super(resCls);
        this.reloadingController = this.createReloadingController();
    }
    
    @Override
    public ReloadingController getReloadingController() {
        return this.reloadingController;
    }
    
    @Override
    protected FileBasedConfigurationBuilder<T> createManagedBuilder(final String fileName, final Map<String, Object> params) throws ConfigurationException {
        return new ReloadingFileBasedConfigurationBuilder<T>(this.getResultClass(), params, this.isAllowFailOnInit());
    }
    
    private ReloadingController createReloadingController() {
        final Set<ReloadingController> empty = Collections.emptySet();
        return new CombinedReloadingController(empty) {
            @Override
            public Collection<ReloadingController> getSubControllers() {
                final Collection<FileBasedConfigurationBuilder<T>> builders = (Collection<FileBasedConfigurationBuilder<T>>)ReloadingMultiFileConfigurationBuilder.this.getManagedBuilders().values();
                final Collection<ReloadingController> controllers = new ArrayList<ReloadingController>(builders.size());
                for (final FileBasedConfigurationBuilder<T> b : builders) {
                    controllers.add(((ReloadingControllerSupport)b).getReloadingController());
                }
                return controllers;
            }
        };
    }
}
