// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.resolver;

import java.util.Map;
import java.net.URL;

public interface EntityRegistry
{
    void registerEntityId(final String p0, final URL p1);
    
    Map<String, URL> getRegisteredEntities();
}
