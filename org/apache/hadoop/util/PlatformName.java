// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HBase" })
@InterfaceStability.Unstable
public class PlatformName
{
    public static final String PLATFORM_NAME;
    public static final String JAVA_VENDOR_NAME;
    public static final boolean IBM_JAVA;
    
    public static void main(final String[] args) {
        System.out.println(PlatformName.PLATFORM_NAME);
    }
    
    static {
        PLATFORM_NAME = (System.getProperty("os.name").startsWith("Windows") ? System.getenv("os") : System.getProperty("os.name")) + "-" + System.getProperty("os.arch") + "-" + System.getProperty("sun.arch.data.model");
        JAVA_VENDOR_NAME = System.getProperty("java.vendor");
        IBM_JAVA = PlatformName.JAVA_VENDOR_NAME.contains("IBM");
    }
}
