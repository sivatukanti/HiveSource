// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata.xml;

import org.datanucleus.plugin.ConfigurationElement;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.util.AbstractXMLEntityResolver;

public class PluginEntityResolver extends AbstractXMLEntityResolver
{
    public PluginEntityResolver(final PluginManager pluginMgr) {
        final ConfigurationElement[] elems = pluginMgr.getConfigurationElementsForExtension("org.datanucleus.metadata_entityresolver", null, (String)null);
        for (int i = 0; i < elems.length; ++i) {
            if (elems[i].getAttribute("type") != null) {
                if (elems[i].getAttribute("type").equals("PUBLIC")) {
                    this.publicIdEntities.put(elems[i].getAttribute("identity"), elems[i].getAttribute("url"));
                }
                else if (elems[i].getAttribute("type").equals("SYSTEM")) {
                    this.systemIdEntities.put(elems[i].getAttribute("identity"), elems[i].getAttribute("url"));
                }
            }
        }
    }
}
