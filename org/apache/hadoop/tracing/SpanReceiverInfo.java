// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tracing;

import java.util.LinkedList;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class SpanReceiverInfo
{
    private final long id;
    private final String className;
    final List<ConfigurationPair> configPairs;
    
    SpanReceiverInfo(final long id, final String className) {
        this.configPairs = new LinkedList<ConfigurationPair>();
        this.id = id;
        this.className = className;
    }
    
    public long getId() {
        return this.id;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    static class ConfigurationPair
    {
        private final String key;
        private final String value;
        
        ConfigurationPair(final String key, final String value) {
            this.key = key;
            this.value = value;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
