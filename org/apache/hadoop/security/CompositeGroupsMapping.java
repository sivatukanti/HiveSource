// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import java.util.Map;
import org.apache.hadoop.util.ReflectionUtils;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.TreeSet;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import java.util.List;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class CompositeGroupsMapping implements GroupMappingServiceProvider, Configurable
{
    public static final String MAPPING_PROVIDERS_CONFIG_KEY = "hadoop.security.group.mapping.providers";
    public static final String MAPPING_PROVIDERS_COMBINED_CONFIG_KEY = "hadoop.security.group.mapping.providers.combined";
    public static final String MAPPING_PROVIDER_CONFIG_PREFIX = "hadoop.security.group.mapping.provider";
    private static final Logger LOG;
    private List<GroupMappingServiceProvider> providersList;
    private Configuration conf;
    private boolean combined;
    
    public CompositeGroupsMapping() {
        this.providersList = new ArrayList<GroupMappingServiceProvider>();
    }
    
    @Override
    public synchronized List<String> getGroups(final String user) throws IOException {
        final Set<String> groupSet = new TreeSet<String>();
        List<String> groups = null;
        for (final GroupMappingServiceProvider provider : this.providersList) {
            try {
                groups = provider.getGroups(user);
            }
            catch (Exception e) {
                CompositeGroupsMapping.LOG.warn("Unable to get groups for user {} via {} because: {}", user, provider.getClass().getSimpleName(), e.toString());
                CompositeGroupsMapping.LOG.debug("Stacktrace: ", e);
            }
            if (groups != null && !groups.isEmpty()) {
                groupSet.addAll(groups);
                if (!this.combined) {
                    break;
                }
                continue;
            }
        }
        final List<String> results = new ArrayList<String>(groupSet.size());
        results.addAll(groupSet);
        return results;
    }
    
    @Override
    public void cacheGroupsRefresh() throws IOException {
    }
    
    @Override
    public void cacheGroupsAdd(final List<String> groups) throws IOException {
    }
    
    @Override
    public synchronized Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public synchronized void setConf(final Configuration conf) {
        this.conf = conf;
        this.combined = conf.getBoolean("hadoop.security.group.mapping.providers.combined", true);
        this.loadMappingProviders();
    }
    
    private void loadMappingProviders() {
        final String[] strings;
        final String[] providerNames = strings = this.conf.getStrings("hadoop.security.group.mapping.providers", new String[0]);
        for (final String name : strings) {
            final String providerKey = "hadoop.security.group.mapping.provider." + name;
            final Class<?> providerClass = this.conf.getClass(providerKey, null);
            if (providerClass == null) {
                CompositeGroupsMapping.LOG.error("The mapping provider, " + name + " does not have a valid class");
            }
            else {
                this.addMappingProvider(name, providerClass);
            }
        }
    }
    
    private void addMappingProvider(final String providerName, final Class<?> providerClass) {
        final Configuration newConf = this.prepareConf(providerName);
        final GroupMappingServiceProvider provider = ReflectionUtils.newInstance(providerClass, newConf);
        this.providersList.add(provider);
    }
    
    private Configuration prepareConf(final String providerName) {
        final Configuration newConf = new Configuration();
        final Iterator<Map.Entry<String, String>> entries = this.conf.iterator();
        final String providerKey = "hadoop.security.group.mapping.provider." + providerName;
        while (entries.hasNext()) {
            final Map.Entry<String, String> entry = entries.next();
            String key = entry.getKey();
            if (key.startsWith(providerKey) && !key.equals(providerKey)) {
                key = key.replace(".provider." + providerName, "");
                newConf.set(key, entry.getValue());
            }
        }
        return newConf;
    }
    
    static {
        LOG = LoggerFactory.getLogger(CompositeGroupsMapping.class);
    }
}
