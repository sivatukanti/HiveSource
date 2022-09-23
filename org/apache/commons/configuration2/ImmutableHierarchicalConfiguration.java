// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.List;
import org.apache.commons.configuration2.tree.ExpressionEngine;

public interface ImmutableHierarchicalConfiguration extends ImmutableConfiguration
{
    ExpressionEngine getExpressionEngine();
    
    int getMaxIndex(final String p0);
    
    String getRootElementName();
    
    ImmutableHierarchicalConfiguration immutableConfigurationAt(final String p0, final boolean p1);
    
    ImmutableHierarchicalConfiguration immutableConfigurationAt(final String p0);
    
    List<ImmutableHierarchicalConfiguration> immutableConfigurationsAt(final String p0);
    
    List<ImmutableHierarchicalConfiguration> immutableChildConfigurationsAt(final String p0);
}
