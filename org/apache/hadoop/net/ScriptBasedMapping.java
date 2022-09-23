// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.Shell;
import java.io.File;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ScriptBasedMapping extends CachedDNSToSwitchMapping
{
    static final int MIN_ALLOWABLE_ARGS = 1;
    static final int DEFAULT_ARG_COUNT = 100;
    static final String SCRIPT_FILENAME_KEY = "net.topology.script.file.name";
    static final String SCRIPT_ARG_COUNT_KEY = "net.topology.script.number.args";
    public static final String NO_SCRIPT = "no script";
    
    public ScriptBasedMapping() {
        this(new RawScriptBasedMapping());
    }
    
    public ScriptBasedMapping(final DNSToSwitchMapping rawMap) {
        super(rawMap);
    }
    
    public ScriptBasedMapping(final Configuration conf) {
        this();
        this.setConf(conf);
    }
    
    private RawScriptBasedMapping getRawMapping() {
        return (RawScriptBasedMapping)this.rawMapping;
    }
    
    @Override
    public Configuration getConf() {
        return this.getRawMapping().getConf();
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
    
    protected static class RawScriptBasedMapping extends AbstractDNSToSwitchMapping
    {
        private String scriptName;
        private int maxArgs;
        private static final Logger LOG;
        
        @Override
        public void setConf(final Configuration conf) {
            super.setConf(conf);
            if (conf != null) {
                this.scriptName = conf.get("net.topology.script.file.name");
                this.maxArgs = conf.getInt("net.topology.script.number.args", 100);
            }
            else {
                this.scriptName = null;
                this.maxArgs = 0;
            }
        }
        
        public RawScriptBasedMapping() {
        }
        
        @Override
        public List<String> resolve(final List<String> names) {
            final List<String> m = new ArrayList<String>(names.size());
            if (names.isEmpty()) {
                return m;
            }
            if (this.scriptName == null) {
                for (final String name : names) {
                    m.add("/default-rack");
                }
                return m;
            }
            final String output = this.runResolveCommand(names, this.scriptName);
            if (output == null) {
                return null;
            }
            final StringTokenizer allSwitchInfo = new StringTokenizer(output);
            while (allSwitchInfo.hasMoreTokens()) {
                final String switchInfo = allSwitchInfo.nextToken();
                m.add(switchInfo);
            }
            if (m.size() != names.size()) {
                RawScriptBasedMapping.LOG.error("Script " + this.scriptName + " returned " + Integer.toString(m.size()) + " values when " + Integer.toString(names.size()) + " were expected.");
                return null;
            }
            return m;
        }
        
        protected String runResolveCommand(final List<String> args, final String commandScriptName) {
            int loopCount = 0;
            if (args.size() == 0) {
                return null;
            }
            final StringBuilder allOutput = new StringBuilder();
            int numProcessed = 0;
            if (this.maxArgs < 1) {
                RawScriptBasedMapping.LOG.warn("Invalid value " + Integer.toString(this.maxArgs) + " for " + "net.topology.script.number.args" + "; must be >= " + Integer.toString(1));
                return null;
            }
            while (numProcessed != args.size()) {
                final int start = this.maxArgs * loopCount;
                final List<String> cmdList = new ArrayList<String>();
                cmdList.add(commandScriptName);
                for (numProcessed = start; numProcessed < start + this.maxArgs && numProcessed < args.size(); ++numProcessed) {
                    cmdList.add(args.get(numProcessed));
                }
                File dir = null;
                final String userDir;
                if ((userDir = System.getProperty("user.dir")) != null) {
                    dir = new File(userDir);
                }
                final Shell.ShellCommandExecutor s = new Shell.ShellCommandExecutor(cmdList.toArray(new String[cmdList.size()]), dir);
                try {
                    s.execute();
                    allOutput.append(s.getOutput()).append(" ");
                }
                catch (Exception e) {
                    RawScriptBasedMapping.LOG.warn("Exception running " + s, e);
                    return null;
                }
                ++loopCount;
            }
            return allOutput.toString();
        }
        
        @Override
        public boolean isSingleSwitch() {
            return this.scriptName == null;
        }
        
        @Override
        public String toString() {
            return (this.scriptName != null) ? ("script " + this.scriptName) : "no script";
        }
        
        @Override
        public void reloadCachedMappings() {
        }
        
        @Override
        public void reloadCachedMappings(final List<String> names) {
        }
        
        static {
            LOG = LoggerFactory.getLogger(ScriptBasedMapping.class);
        }
    }
}
