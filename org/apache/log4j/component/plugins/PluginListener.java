// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.plugins;

import java.util.EventListener;

public interface PluginListener extends EventListener
{
    void pluginStarted(final PluginEvent p0);
    
    void pluginStopped(final PluginEvent p0);
}
