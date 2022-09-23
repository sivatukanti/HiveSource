// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class Interns
{
    private static final Logger LOG;
    static final int MAX_INFO_NAMES = 2010;
    static final int MAX_INFO_DESCS = 100;
    static final int MAX_TAG_NAMES = 100;
    static final int MAX_TAG_VALUES = 1000;
    
    public static MetricsInfo info(final String name, final String description) {
        return Info.INSTANCE.cache.add(name, description);
    }
    
    public static MetricsTag tag(final MetricsInfo info, final String value) {
        return Tags.INSTANCE.cache.add(info, value);
    }
    
    public static MetricsTag tag(final String name, final String description, final String value) {
        return Tags.INSTANCE.cache.add(info(name, description), value);
    }
    
    static {
        LOG = LoggerFactory.getLogger(Interns.class);
    }
    
    private abstract static class CacheWith2Keys<K1, K2, V>
    {
        private final Map<K1, Map<K2, V>> k1Map;
        
        private CacheWith2Keys() {
            this.k1Map = new LinkedHashMap<K1, Map<K2, V>>() {
                private static final long serialVersionUID = 1L;
                private boolean gotOverflow = false;
                
                @Override
                protected boolean removeEldestEntry(final Map.Entry<K1, Map<K2, V>> e) {
                    final boolean overflow = CacheWith2Keys.this.expireKey1At(this.size());
                    if (overflow && !this.gotOverflow) {
                        Interns.LOG.info("Metrics intern cache overflow at {} for {}", (Object)this.size(), e);
                        this.gotOverflow = true;
                    }
                    return overflow;
                }
            };
        }
        
        protected abstract boolean expireKey1At(final int p0);
        
        protected abstract boolean expireKey2At(final int p0);
        
        protected abstract V newValue(final K1 p0, final K2 p1);
        
        synchronized V add(final K1 k1, final K2 k2) {
            Map<K2, V> k2Map = this.k1Map.get(k1);
            if (k2Map == null) {
                k2Map = new LinkedHashMap<K2, V>() {
                    private static final long serialVersionUID = 1L;
                    private boolean gotOverflow = false;
                    
                    @Override
                    protected boolean removeEldestEntry(final Map.Entry<K2, V> e) {
                        final boolean overflow = CacheWith2Keys.this.expireKey2At(this.size());
                        if (overflow && !this.gotOverflow) {
                            Interns.LOG.info("Metrics intern cache overflow at {} for {}", (Object)this.size(), e);
                            this.gotOverflow = true;
                        }
                        return overflow;
                    }
                };
                this.k1Map.put(k1, k2Map);
            }
            V v = k2Map.get(k2);
            if (v == null) {
                v = this.newValue(k1, k2);
                k2Map.put(k2, v);
            }
            return v;
        }
    }
    
    enum Info
    {
        INSTANCE;
        
        final CacheWith2Keys<String, String, MetricsInfo> cache;
        
        private Info() {
            this.cache = new CacheWith2Keys<String, String, MetricsInfo>() {
                @Override
                protected boolean expireKey1At(final int size) {
                    return size > 2010;
                }
                
                @Override
                protected boolean expireKey2At(final int size) {
                    return size > 100;
                }
                
                @Override
                protected MetricsInfo newValue(final String name, final String desc) {
                    return new MetricsInfoImpl(name, desc);
                }
            };
        }
    }
    
    enum Tags
    {
        INSTANCE;
        
        final CacheWith2Keys<MetricsInfo, String, MetricsTag> cache;
        
        private Tags() {
            this.cache = new CacheWith2Keys<MetricsInfo, String, MetricsTag>() {
                @Override
                protected boolean expireKey1At(final int size) {
                    return size > 100;
                }
                
                @Override
                protected boolean expireKey2At(final int size) {
                    return size > 1000;
                }
                
                @Override
                protected MetricsTag newValue(final MetricsInfo info, final String value) {
                    return new MetricsTag(info, value);
                }
            };
        }
    }
}
