// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface StreamCapabilities
{
    public static final String HFLUSH = "hflush";
    public static final String HSYNC = "hsync";
    public static final String READAHEAD = "in:readahead";
    public static final String DROPBEHIND = "dropbehind";
    public static final String UNBUFFER = "in:unbuffer";
    
    boolean hasCapability(final String p0);
    
    @Deprecated
    public enum StreamCapability
    {
        HFLUSH("hflush"), 
        HSYNC("hsync");
        
        private final String capability;
        
        private StreamCapability(final String value) {
            this.capability = value;
        }
        
        public final String getValue() {
            return this.capability;
        }
    }
}
