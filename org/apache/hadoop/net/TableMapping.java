// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configured;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class TableMapping extends CachedDNSToSwitchMapping
{
    private static final Logger LOG;
    
    public TableMapping() {
        super(new RawTableMapping());
    }
    
    private RawTableMapping getRawMapping() {
        return (RawTableMapping)this.rawMapping;
    }
    
    @Override
    public Configuration getConf() {
        return this.getRawMapping().getConf();
    }
    
    @Override
    public void setConf(final Configuration conf) {
        super.setConf(conf);
        this.getRawMapping().setConf(conf);
    }
    
    @Override
    public void reloadCachedMappings() {
        super.reloadCachedMappings();
        this.getRawMapping().reloadCachedMappings();
    }
    
    static {
        LOG = LoggerFactory.getLogger(TableMapping.class);
    }
    
    private static final class RawTableMapping extends Configured implements DNSToSwitchMapping
    {
        private Map<String, String> map;
        
        private Map<String, String> load() {
            final Map<String, String> loadMap = new HashMap<String, String>();
            final String filename = this.getConf().get("net.topology.table.file.name", null);
            if (StringUtils.isBlank(filename)) {
                TableMapping.LOG.warn("net.topology.table.file.name not configured. ");
                return null;
            }
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    line = line.trim();
                    if (line.length() != 0 && line.charAt(0) != '#') {
                        final String[] columns = line.split("\\s+");
                        if (columns.length == 2) {
                            loadMap.put(columns[0], columns[1]);
                        }
                        else {
                            TableMapping.LOG.warn("Line does not have two columns. Ignoring. " + line);
                        }
                    }
                }
            }
            catch (Exception e) {
                TableMapping.LOG.warn(filename + " cannot be read.", e);
                return null;
            }
            return loadMap;
        }
        
        @Override
        public synchronized List<String> resolve(final List<String> names) {
            if (this.map == null) {
                this.map = this.load();
                if (this.map == null) {
                    TableMapping.LOG.warn("Failed to read topology table. /default-rack will be used for all nodes.");
                    this.map = new HashMap<String, String>();
                }
            }
            final List<String> results = new ArrayList<String>(names.size());
            for (final String name : names) {
                final String result = this.map.get(name);
                if (result != null) {
                    results.add(result);
                }
                else {
                    results.add("/default-rack");
                }
            }
            return results;
        }
        
        @Override
        public void reloadCachedMappings() {
            final Map<String, String> newMap = this.load();
            if (newMap == null) {
                TableMapping.LOG.error("Failed to reload the topology table.  The cached mappings will not be cleared.");
            }
            else {
                synchronized (this) {
                    this.map = newMap;
                }
            }
        }
        
        @Override
        public void reloadCachedMappings(final List<String> names) {
            this.reloadCachedMappings();
        }
    }
}
