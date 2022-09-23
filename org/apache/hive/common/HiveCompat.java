// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;

public class HiveCompat
{
    private static Log LOG;
    public static final String DEFAULT_COMPAT_LEVEL;
    public static final String LATEST_COMPAT_LEVEL;
    
    public static CompatLevel getCompatLevel(final HiveConf hconf) {
        return getCompatLevel(HiveConf.getVar(hconf, HiveConf.ConfVars.HIVE_COMPAT));
    }
    
    public static CompatLevel getCompatLevel(String compatStr) {
        if (compatStr.equalsIgnoreCase("latest")) {
            compatStr = HiveCompat.LATEST_COMPAT_LEVEL;
        }
        for (final CompatLevel cl : CompatLevel.values()) {
            if (cl.value.equals(compatStr)) {
                return cl;
            }
        }
        HiveCompat.LOG.error("Could not find CompatLevel for " + compatStr + ", using default of " + HiveCompat.DEFAULT_COMPAT_LEVEL);
        return getCompatLevel(HiveCompat.DEFAULT_COMPAT_LEVEL);
    }
    
    private static CompatLevel getLastCompatLevel() {
        final CompatLevel[] compatLevels = CompatLevel.values();
        return compatLevels[compatLevels.length - 1];
    }
    
    static {
        HiveCompat.LOG = LogFactory.getLog(HiveCompat.class);
        DEFAULT_COMPAT_LEVEL = CompatLevel.HIVE_0_12.value;
        LATEST_COMPAT_LEVEL = getLastCompatLevel().value;
    }
    
    public enum CompatLevel
    {
        HIVE_0_12("0.12", 0, 12), 
        HIVE_0_13("0.13", 0, 13);
        
        public final String value;
        public final int majorVersion;
        public final int minorVersion;
        
        private CompatLevel(final String val, final int majorVersion, final int minorVersion) {
            this.value = val;
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
        }
    }
}
