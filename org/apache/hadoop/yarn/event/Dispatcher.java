// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.event;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface Dispatcher
{
    public static final String DISPATCHER_EXIT_ON_ERROR_KEY = "yarn.dispatcher.exit-on-error";
    public static final boolean DEFAULT_DISPATCHER_EXIT_ON_ERROR = false;
    
    EventHandler getEventHandler();
    
    void register(final Class<? extends Enum> p0, final EventHandler p1);
}
