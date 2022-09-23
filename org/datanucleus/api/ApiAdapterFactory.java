// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api;

import org.datanucleus.ClassConstants;
import java.lang.reflect.InvocationTargetException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.plugin.PluginManager;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.util.Localiser;

public class ApiAdapterFactory
{
    private static final Localiser LOCALISER;
    Map<String, ApiAdapter> adapters;
    static ApiAdapterFactory adapterFactory;
    
    public static ApiAdapterFactory getInstance() {
        return ApiAdapterFactory.adapterFactory;
    }
    
    protected ApiAdapterFactory() {
        this.adapters = new HashMap<String, ApiAdapter>();
    }
    
    private void addAdapter(final String name, final ApiAdapter apiAdapter) {
        if (name == null || apiAdapter == null) {
            return;
        }
        this.adapters.put(name, apiAdapter);
    }
    
    public ApiAdapter getApiAdapter(final String name, final PluginManager pluginMgr) {
        ApiAdapter api = this.adapters.get(name);
        if (api == null) {
            try {
                api = (ApiAdapter)pluginMgr.createExecutableExtension("org.datanucleus.api_adapter", "name", name, "class-name", null, null);
                if (api == null) {
                    final String msg = ApiAdapterFactory.LOCALISER.msg("022001", name);
                    NucleusLogger.PERSISTENCE.error(msg);
                    throw new NucleusUserException(msg);
                }
                ApiAdapterFactory.adapterFactory.addAdapter(name, api);
            }
            catch (Error err) {
                final String className = pluginMgr.getAttributeValueForExtension("org.datanucleus.api_adapter", "name", name, "class-name");
                final String msg2 = ApiAdapterFactory.LOCALISER.msg("022000", className, err.getMessage());
                NucleusLogger.PERSISTENCE.error(msg2, err);
                throw new NucleusUserException(msg2);
            }
            catch (InvocationTargetException e) {
                final String className = pluginMgr.getAttributeValueForExtension("org.datanucleus.api_adapter", "name", name, "class-name");
                final String msg2 = ApiAdapterFactory.LOCALISER.msg("022000", className, e.getTargetException());
                NucleusLogger.PERSISTENCE.error(msg2, e);
                throw new NucleusUserException(msg2);
            }
            catch (NucleusUserException nue) {
                throw nue;
            }
            catch (Exception e2) {
                final String className = pluginMgr.getAttributeValueForExtension("org.datanucleus.api_adapter", "name", name, "class-name");
                final String msg2 = ApiAdapterFactory.LOCALISER.msg("022000", className, e2.getMessage());
                NucleusLogger.PERSISTENCE.error(msg2, e2);
                throw new NucleusUserException(msg2);
            }
        }
        return api;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        ApiAdapterFactory.adapterFactory = new ApiAdapterFactory();
    }
}
