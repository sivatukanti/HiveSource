// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class CachedDNSToSwitchMapping extends AbstractDNSToSwitchMapping
{
    private Map<String, String> cache;
    protected final DNSToSwitchMapping rawMapping;
    
    public CachedDNSToSwitchMapping(final DNSToSwitchMapping rawMapping) {
        this.cache = new ConcurrentHashMap<String, String>();
        this.rawMapping = rawMapping;
    }
    
    private List<String> getUncachedHosts(final List<String> names) {
        final List<String> unCachedHosts = new ArrayList<String>(names.size());
        for (final String name : names) {
            if (this.cache.get(name) == null) {
                unCachedHosts.add(name);
            }
        }
        return unCachedHosts;
    }
    
    private void cacheResolvedHosts(final List<String> uncachedHosts, final List<String> resolvedHosts) {
        if (resolvedHosts != null) {
            for (int i = 0; i < uncachedHosts.size(); ++i) {
                this.cache.put(uncachedHosts.get(i), resolvedHosts.get(i));
            }
        }
    }
    
    private List<String> getCachedHosts(final List<String> names) {
        final List<String> result = new ArrayList<String>(names.size());
        for (final String name : names) {
            final String networkLocation = this.cache.get(name);
            if (networkLocation == null) {
                return null;
            }
            result.add(networkLocation);
        }
        return result;
    }
    
    @Override
    public List<String> resolve(List<String> names) {
        names = NetUtils.normalizeHostNames(names);
        final List<String> result = new ArrayList<String>(names.size());
        if (names.isEmpty()) {
            return result;
        }
        final List<String> uncachedHosts = this.getUncachedHosts(names);
        final List<String> resolvedHosts = this.rawMapping.resolve(uncachedHosts);
        this.cacheResolvedHosts(uncachedHosts, resolvedHosts);
        return this.getCachedHosts(names);
    }
    
    @Override
    public Map<String, String> getSwitchMap() {
        final Map<String, String> switchMap = new HashMap<String, String>(this.cache);
        return switchMap;
    }
    
    @Override
    public String toString() {
        return "cached switch mapping relaying to " + this.rawMapping;
    }
    
    @Override
    public boolean isSingleSwitch() {
        return AbstractDNSToSwitchMapping.isMappingSingleSwitch(this.rawMapping);
    }
    
    @Override
    public void reloadCachedMappings() {
        this.cache.clear();
    }
    
    @Override
    public void reloadCachedMappings(final List<String> names) {
        for (final String name : names) {
            this.cache.remove(name);
        }
    }
}
