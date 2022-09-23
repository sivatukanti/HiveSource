// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import org.apache.commons.logging.LogFactory;
import java.util.Set;
import java.util.HashMap;
import org.apache.commons.logging.Log;

public abstract class InstanceCache<SeedObject, Instance>
{
    private static final Log LOG;
    HashMap<Integer, Instance> cache;
    
    public InstanceCache() {
        this.cache = new HashMap<Integer, Instance>();
    }
    
    public Instance retrieve(final SeedObject hv) throws AvroSerdeException {
        return this.retrieve(hv, null);
    }
    
    public Instance retrieve(final SeedObject hv, final Set<SeedObject> seenSchemas) throws AvroSerdeException {
        if (InstanceCache.LOG.isDebugEnabled()) {
            InstanceCache.LOG.debug("Checking for hv: " + hv.toString());
        }
        if (this.cache.containsKey(hv.hashCode())) {
            if (InstanceCache.LOG.isDebugEnabled()) {
                InstanceCache.LOG.debug("Returning cache result.");
            }
            return this.cache.get(hv.hashCode());
        }
        if (InstanceCache.LOG.isDebugEnabled()) {
            InstanceCache.LOG.debug("Creating new instance and storing in cache");
        }
        final Instance instance = this.makeInstance(hv, seenSchemas);
        this.cache.put(hv.hashCode(), instance);
        return instance;
    }
    
    protected abstract Instance makeInstance(final SeedObject p0, final Set<SeedObject> p1) throws AvroSerdeException;
    
    static {
        LOG = LogFactory.getLog(InstanceCache.class);
    }
}
