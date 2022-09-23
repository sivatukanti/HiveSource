// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.BuilderParameters;
import java.util.Collection;
import java.util.Arrays;

public class CombinedConfigurationBuilderProvider extends BaseConfigurationBuilderProvider
{
    private static final String BUILDER_CLASS = "org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder";
    private static final String RELOADING_BUILDER_CLASS = "org.apache.commons.configuration2.builder.combined.ReloadingCombinedConfigurationBuilder";
    private static final String CONFIGURATION_CLASS = "org.apache.commons.configuration2.CombinedConfiguration";
    private static final String COMBINED_PARAMS = "org.apache.commons.configuration2.builder.combined.CombinedBuilderParametersImpl";
    private static final String FILE_PARAMS = "org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl";
    
    public CombinedConfigurationBuilderProvider() {
        super("org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder", "org.apache.commons.configuration2.builder.combined.ReloadingCombinedConfigurationBuilder", "org.apache.commons.configuration2.CombinedConfiguration", Arrays.asList("org.apache.commons.configuration2.builder.combined.CombinedBuilderParametersImpl", "org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl"));
    }
    
    @Override
    protected BasicConfigurationBuilder<? extends Configuration> createBuilder(final ConfigurationDeclaration decl, final Collection<BuilderParameters> params) throws Exception {
        CombinedConfigurationBuilder builder;
        if (decl.isReload()) {
            builder = new ReloadingCombinedConfigurationBuilder();
        }
        else {
            builder = new CombinedConfigurationBuilder();
        }
        decl.getConfigurationBuilder().initChildEventListeners(builder);
        return builder;
    }
    
    @Override
    protected void initializeParameterObjects(final ConfigurationDeclaration decl, final Collection<BuilderParameters> params) throws Exception {
        final BasicBuilderParameters basicParams = params.iterator().next();
        setUpBasicParameters(decl.getConfigurationBuilder().getConfigurationUnderConstruction(), basicParams);
        super.initializeParameterObjects(decl, params);
    }
    
    private static void setUpBasicParameters(final CombinedConfiguration config, final BasicBuilderParameters params) {
        params.setListDelimiterHandler(config.getListDelimiterHandler()).setLogger(config.getLogger()).setThrowExceptionOnMissing(config.isThrowExceptionOnMissing()).setConfigurationDecoder(config.getConfigurationDecoder());
    }
}
