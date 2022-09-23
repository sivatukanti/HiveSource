// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class ScriptBasedMappingWithDependency extends ScriptBasedMapping implements DNSToSwitchMappingWithDependency
{
    static final String DEPENDENCY_SCRIPT_FILENAME_KEY = "net.topology.dependency.script.file.name";
    private Map<String, List<String>> dependencyCache;
    
    public ScriptBasedMappingWithDependency() {
        super(new RawScriptBasedMappingWithDependency());
        this.dependencyCache = new ConcurrentHashMap<String, List<String>>();
    }
    
    private RawScriptBasedMappingWithDependency getRawMapping() {
        return (RawScriptBasedMappingWithDependency)this.rawMapping;
    }
    
    @Override
    public String toString() {
        return "script-based mapping with " + this.getRawMapping().toString();
    }
    
    @Override
    public void setConf(final Configuration conf) {
        super.setConf(conf);
        this.getRawMapping().setConf(conf);
    }
    
    @Override
    public List<String> getDependency(String name) {
        name = NetUtils.normalizeHostName(name);
        if (name == null) {
            return Collections.emptyList();
        }
        List<String> dependencies = this.dependencyCache.get(name);
        if (dependencies == null) {
            dependencies = this.getRawMapping().getDependency(name);
            if (dependencies != null) {
                this.dependencyCache.put(name, dependencies);
            }
        }
        return dependencies;
    }
    
    private static final class RawScriptBasedMappingWithDependency extends RawScriptBasedMapping implements DNSToSwitchMappingWithDependency
    {
        private String dependencyScriptName;
        
        @Override
        public void setConf(final Configuration conf) {
            super.setConf(conf);
            if (conf != null) {
                this.dependencyScriptName = conf.get("net.topology.dependency.script.file.name");
            }
            else {
                this.dependencyScriptName = null;
            }
        }
        
        public RawScriptBasedMappingWithDependency() {
        }
        
        @Override
        public List<String> getDependency(final String name) {
            if (name == null || this.dependencyScriptName == null) {
                return Collections.emptyList();
            }
            final List<String> m = new LinkedList<String>();
            final List<String> args = new ArrayList<String>(1);
            args.add(name);
            final String output = this.runResolveCommand(args, this.dependencyScriptName);
            if (output != null) {
                final StringTokenizer allSwitchInfo = new StringTokenizer(output);
                while (allSwitchInfo.hasMoreTokens()) {
                    final String switchInfo = allSwitchInfo.nextToken();
                    m.add(switchInfo);
                }
                return m;
            }
            return null;
        }
        
        @Override
        public String toString() {
            return "dependency script " + this.dependencyScriptName;
        }
    }
}
