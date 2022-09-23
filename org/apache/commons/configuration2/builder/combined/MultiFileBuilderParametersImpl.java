// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.ConfigurationUtils;
import java.util.Map;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;

public class MultiFileBuilderParametersImpl extends BasicBuilderParameters implements MultiFileBuilderProperties<MultiFileBuilderParametersImpl>
{
    private static final String PARAM_KEY;
    private BuilderParameters managedBuilderParameters;
    private String filePattern;
    
    public static MultiFileBuilderParametersImpl fromParameters(final Map<String, Object> params) {
        return fromParameters(params, false);
    }
    
    public static MultiFileBuilderParametersImpl fromParameters(final Map<String, Object> params, final boolean createIfMissing) {
        MultiFileBuilderParametersImpl instance = params.get(MultiFileBuilderParametersImpl.PARAM_KEY);
        if (instance == null && createIfMissing) {
            instance = new MultiFileBuilderParametersImpl();
        }
        return instance;
    }
    
    public String getFilePattern() {
        return this.filePattern;
    }
    
    @Override
    public MultiFileBuilderParametersImpl setFilePattern(final String p) {
        this.filePattern = p;
        return this;
    }
    
    public BuilderParameters getManagedBuilderParameters() {
        return this.managedBuilderParameters;
    }
    
    @Override
    public MultiFileBuilderParametersImpl setManagedBuilderParameters(final BuilderParameters p) {
        this.managedBuilderParameters = p;
        return this;
    }
    
    @Override
    public Map<String, Object> getParameters() {
        final Map<String, Object> params = super.getParameters();
        params.put(MultiFileBuilderParametersImpl.PARAM_KEY, this);
        return params;
    }
    
    @Override
    public MultiFileBuilderParametersImpl clone() {
        final MultiFileBuilderParametersImpl copy = (MultiFileBuilderParametersImpl)super.clone();
        copy.setManagedBuilderParameters((BuilderParameters)ConfigurationUtils.cloneIfPossible(this.getManagedBuilderParameters()));
        return copy;
    }
    
    static {
        PARAM_KEY = "config-" + MultiFileBuilderParametersImpl.class.getName();
    }
}
