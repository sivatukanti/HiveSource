// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import java.util.Map;

public class PropertiesBuilderParametersImpl extends FileBasedBuilderParametersImpl implements PropertiesBuilderProperties<PropertiesBuilderParametersImpl>
{
    private static final String PROP_INCLUDES_ALLOWED = "includesAllowed";
    private static final String PROP_LAYOUT = "layout";
    private static final String PROP_IO_FACTORY = "IOFactory";
    
    @Override
    public PropertiesBuilderParametersImpl setIncludesAllowed(final boolean f) {
        this.storeProperty("includesAllowed", f);
        return this;
    }
    
    @Override
    public void inheritFrom(final Map<String, ?> source) {
        super.inheritFrom(source);
        this.copyPropertiesFrom(source, "includesAllowed", "IOFactory");
    }
    
    @Override
    public PropertiesBuilderParametersImpl setLayout(final PropertiesConfigurationLayout layout) {
        this.storeProperty("layout", layout);
        return this;
    }
    
    @Override
    public PropertiesBuilderParametersImpl setIOFactory(final PropertiesConfiguration.IOFactory factory) {
        this.storeProperty("IOFactory", factory);
        return this;
    }
}
