// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class AbstractDNSToSwitchMapping implements DNSToSwitchMapping, Configurable
{
    private Configuration conf;
    
    protected AbstractDNSToSwitchMapping() {
    }
    
    protected AbstractDNSToSwitchMapping(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    public boolean isSingleSwitch() {
        return false;
    }
    
    public Map<String, String> getSwitchMap() {
        return null;
    }
    
    public String dumpTopology() {
        final Map<String, String> rack = this.getSwitchMap();
        final StringBuilder builder = new StringBuilder();
        builder.append("Mapping: ").append(this.toString()).append("\n");
        if (rack != null) {
            builder.append("Map:\n");
            final Set<String> switches = new HashSet<String>();
            for (final Map.Entry<String, String> entry : rack.entrySet()) {
                builder.append("  ").append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
                switches.add(entry.getValue());
            }
            builder.append("Nodes: ").append(rack.size()).append("\n");
            builder.append("Switches: ").append(switches.size()).append("\n");
        }
        else {
            builder.append("No topology information");
        }
        return builder.toString();
    }
    
    protected boolean isSingleSwitchByScriptPolicy() {
        return this.conf != null && this.conf.get("net.topology.script.file.name") == null;
    }
    
    public static boolean isMappingSingleSwitch(final DNSToSwitchMapping mapping) {
        return mapping != null && mapping instanceof AbstractDNSToSwitchMapping && ((AbstractDNSToSwitchMapping)mapping).isSingleSwitch();
    }
}
