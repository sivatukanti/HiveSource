// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.tree.ExpressionEngine;
import java.util.Map;

public class HierarchicalBuilderParametersImpl extends FileBasedBuilderParametersImpl implements HierarchicalBuilderProperties<HierarchicalBuilderParametersImpl>
{
    private static final String PROP_EXPRESSION_ENGINE = "expressionEngine";
    
    @Override
    public void inheritFrom(final Map<String, ?> source) {
        super.inheritFrom(source);
        this.copyPropertiesFrom(source, "expressionEngine");
    }
    
    @Override
    public HierarchicalBuilderParametersImpl setExpressionEngine(final ExpressionEngine engine) {
        this.storeProperty("expressionEngine", engine);
        return this;
    }
}
