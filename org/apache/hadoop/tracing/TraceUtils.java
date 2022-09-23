// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tracing;

import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.htrace.core.HTraceConfiguration;
import org.apache.hadoop.conf.Configuration;
import java.util.List;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class TraceUtils
{
    private static List<SpanReceiverInfo.ConfigurationPair> EMPTY;
    static final String DEFAULT_HADOOP_TRACE_PREFIX = "hadoop.htrace.";
    
    public static HTraceConfiguration wrapHadoopConf(final String prefix, final Configuration conf) {
        return wrapHadoopConf(prefix, conf, TraceUtils.EMPTY);
    }
    
    public static HTraceConfiguration wrapHadoopConf(final String prefix, final Configuration conf, final List<SpanReceiverInfo.ConfigurationPair> extraConfig) {
        final HashMap<String, String> extraMap = new HashMap<String, String>();
        for (final SpanReceiverInfo.ConfigurationPair pair : extraConfig) {
            extraMap.put(pair.getKey(), pair.getValue());
        }
        return new HTraceConfiguration() {
            @Override
            public String get(final String key) {
                final String ret = this.getInternal(prefix + key);
                if (ret != null) {
                    return ret;
                }
                return this.getInternal("hadoop.htrace." + key);
            }
            
            @Override
            public String get(final String key, final String defaultValue) {
                final String ret = this.get(key);
                if (ret != null) {
                    return ret;
                }
                return defaultValue;
            }
            
            private String getInternal(final String key) {
                if (extraMap.containsKey(key)) {
                    return extraMap.get(key);
                }
                return conf.get(key);
            }
        };
    }
    
    static {
        TraceUtils.EMPTY = Collections.emptyList();
    }
}
