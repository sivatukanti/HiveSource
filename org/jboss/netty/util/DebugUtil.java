// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util;

import org.jboss.netty.util.internal.SystemPropertyUtil;

public final class DebugUtil
{
    private static final boolean DEBUG_ENABLED;
    
    public static boolean isDebugEnabled() {
        return DebugUtil.DEBUG_ENABLED;
    }
    
    private DebugUtil() {
    }
    
    static {
        DEBUG_ENABLED = SystemPropertyUtil.getBoolean("org.jboss.netty.debug", false);
    }
}
