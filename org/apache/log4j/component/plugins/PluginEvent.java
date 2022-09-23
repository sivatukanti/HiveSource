// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.plugins;

import java.util.EventObject;

public class PluginEvent extends EventObject
{
    PluginEvent(final Plugin source) {
        super(source);
    }
    
    public Plugin getPlugin() {
        return (Plugin)this.getSource();
    }
}
