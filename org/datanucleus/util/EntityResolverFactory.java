// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import org.xml.sax.EntityResolver;
import org.datanucleus.plugin.PluginManager;
import java.util.Map;

public class EntityResolverFactory
{
    private static Map resolvers;
    
    private EntityResolverFactory() {
    }
    
    public static EntityResolver getInstance(final PluginManager pluginManager, final String handlerName) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        EntityResolver resolver = EntityResolverFactory.resolvers.get(handlerName);
        if (resolver == null) {
            resolver = (EntityResolver)pluginManager.createExecutableExtension("org.datanucleus.metadata_handler", "name", handlerName, "entity-resolver", new Class[] { PluginManager.class }, new Object[] { pluginManager });
            EntityResolverFactory.resolvers.put(handlerName, resolver);
        }
        return resolver;
    }
    
    static {
        EntityResolverFactory.resolvers = new HashMap();
    }
}
