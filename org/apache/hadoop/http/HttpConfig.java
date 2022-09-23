// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.http;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class HttpConfig
{
    public enum Policy
    {
        HTTP_ONLY, 
        HTTPS_ONLY, 
        HTTP_AND_HTTPS;
        
        private static final Policy[] VALUES;
        
        public static Policy fromString(final String value) {
            for (final Policy p : Policy.VALUES) {
                if (p.name().equalsIgnoreCase(value)) {
                    return p;
                }
            }
            return null;
        }
        
        public boolean isHttpEnabled() {
            return this == Policy.HTTP_ONLY || this == Policy.HTTP_AND_HTTPS;
        }
        
        public boolean isHttpsEnabled() {
            return this == Policy.HTTPS_ONLY || this == Policy.HTTP_AND_HTTPS;
        }
        
        static {
            VALUES = values();
        }
    }
}
