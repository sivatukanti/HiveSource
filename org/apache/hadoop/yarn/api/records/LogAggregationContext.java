// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Evolving
@InterfaceAudience.Public
public abstract class LogAggregationContext
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static LogAggregationContext newInstance(final String includePattern, final String excludePattern) {
        final LogAggregationContext context = Records.newRecord(LogAggregationContext.class);
        context.setIncludePattern(includePattern);
        context.setExcludePattern(excludePattern);
        return context;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getIncludePattern();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setIncludePattern(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getExcludePattern();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setExcludePattern(final String p0);
}
