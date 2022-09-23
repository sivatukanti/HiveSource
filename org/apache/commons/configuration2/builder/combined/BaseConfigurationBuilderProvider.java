// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import java.util.Map;
import java.util.Collections;
import java.lang.reflect.Constructor;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.beanutils.BeanDeclaration;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import java.util.Collection;

public class BaseConfigurationBuilderProvider implements ConfigurationBuilderProvider
{
    private static final Class<?>[] CTOR_PARAM_TYPES;
    private final String builderClass;
    private final String reloadingBuilderClass;
    private final String configurationClass;
    private final Collection<String> parameterClasses;
    
    public BaseConfigurationBuilderProvider(final String bldrCls, final String reloadBldrCls, final String configCls, final Collection<String> paramCls) {
        if (bldrCls == null) {
            throw new IllegalArgumentException("Builder class must not be null!");
        }
        if (configCls == null) {
            throw new IllegalArgumentException("Configuration class must not be null!");
        }
        this.builderClass = bldrCls;
        this.reloadingBuilderClass = reloadBldrCls;
        this.configurationClass = configCls;
        this.parameterClasses = initParameterClasses(paramCls);
    }
    
    public String getBuilderClass() {
        return this.builderClass;
    }
    
    public String getReloadingBuilderClass() {
        return this.reloadingBuilderClass;
    }
    
    public String getConfigurationClass() {
        return this.configurationClass;
    }
    
    public Collection<String> getParameterClasses() {
        return this.parameterClasses;
    }
    
    @Override
    public ConfigurationBuilder<? extends Configuration> getConfigurationBuilder(final ConfigurationDeclaration decl) throws ConfigurationException {
        try {
            final Collection<BuilderParameters> params = this.createParameterObjects();
            this.initializeParameterObjects(decl, params);
            final BasicConfigurationBuilder<? extends Configuration> builder = this.createBuilder(decl, params);
            this.configureBuilder(builder, decl, params);
            return builder;
        }
        catch (ConfigurationException cex) {
            throw cex;
        }
        catch (Exception ex) {
            throw new ConfigurationException(ex);
        }
    }
    
    protected boolean isAllowFailOnInit(final ConfigurationDeclaration decl) {
        return decl.isOptional() && decl.isForceCreate();
    }
    
    protected Collection<BuilderParameters> createParameterObjects() throws Exception {
        final Collection<BuilderParameters> params = new ArrayList<BuilderParameters>(this.getParameterClasses().size());
        for (final String paramcls : this.getParameterClasses()) {
            params.add(createParameterObject(paramcls));
        }
        return params;
    }
    
    protected void initializeParameterObjects(final ConfigurationDeclaration decl, final Collection<BuilderParameters> params) throws Exception {
        this.inheritParentBuilderProperties(decl, params);
        final MultiWrapDynaBean wrapBean = new MultiWrapDynaBean(params);
        decl.getConfigurationBuilder().initBean(wrapBean, decl);
    }
    
    protected void inheritParentBuilderProperties(final ConfigurationDeclaration decl, final Collection<BuilderParameters> params) {
        for (final BuilderParameters p : params) {
            decl.getConfigurationBuilder().initChildBuilderParameters(p);
        }
    }
    
    protected BasicConfigurationBuilder<? extends Configuration> createBuilder(final ConfigurationDeclaration decl, final Collection<BuilderParameters> params) throws Exception {
        final Class<?> bldCls = ConfigurationUtils.loadClass(this.determineBuilderClass(decl));
        final Class<?> configCls = ConfigurationUtils.loadClass(this.determineConfigurationClass(decl, params));
        final Constructor<?> ctor = bldCls.getConstructor(BaseConfigurationBuilderProvider.CTOR_PARAM_TYPES);
        final BasicConfigurationBuilder<? extends Configuration> builder = (BasicConfigurationBuilder<? extends Configuration>)ctor.newInstance(configCls, null, this.isAllowFailOnInit(decl));
        return builder;
    }
    
    protected void configureBuilder(final BasicConfigurationBuilder<? extends Configuration> builder, final ConfigurationDeclaration decl, final Collection<BuilderParameters> params) throws Exception {
        builder.configure((BuilderParameters[])params.toArray(new BuilderParameters[params.size()]));
    }
    
    protected String determineBuilderClass(final ConfigurationDeclaration decl) throws ConfigurationException {
        if (!decl.isReload()) {
            return this.getBuilderClass();
        }
        if (this.getReloadingBuilderClass() == null) {
            throw new ConfigurationException("No support for reloading for builder class " + this.getBuilderClass());
        }
        return this.getReloadingBuilderClass();
    }
    
    protected String determineConfigurationClass(final ConfigurationDeclaration decl, final Collection<BuilderParameters> params) throws ConfigurationException {
        return this.getConfigurationClass();
    }
    
    private static BuilderParameters createParameterObject(final String paramcls) throws Exception {
        final Class<?> cls = ConfigurationUtils.loadClass(paramcls);
        final BuilderParameters p = (BuilderParameters)cls.newInstance();
        return p;
    }
    
    private static Collection<String> initParameterClasses(final Collection<String> paramCls) {
        if (paramCls == null) {
            return (Collection<String>)Collections.emptySet();
        }
        return Collections.unmodifiableCollection((Collection<? extends String>)new ArrayList<String>(paramCls));
    }
    
    static {
        CTOR_PARAM_TYPES = new Class[] { Class.class, Map.class, Boolean.TYPE };
    }
}
