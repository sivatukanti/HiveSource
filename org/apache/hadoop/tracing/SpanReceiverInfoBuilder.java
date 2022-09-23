// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tracing;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class SpanReceiverInfoBuilder
{
    private SpanReceiverInfo info;
    
    public SpanReceiverInfoBuilder(final String className) {
        this.info = new SpanReceiverInfo(0L, className);
    }
    
    public void addConfigurationPair(final String key, final String value) {
        this.info.configPairs.add(new SpanReceiverInfo.ConfigurationPair(key, value));
    }
    
    public SpanReceiverInfo build() {
        final SpanReceiverInfo ret = this.info;
        this.info = null;
        return ret;
    }
}
