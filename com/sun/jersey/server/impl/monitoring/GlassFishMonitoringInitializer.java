// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.monitoring;

import java.util.Iterator;
import com.sun.jersey.spi.service.ServiceConfigurationError;
import java.util.logging.Level;
import com.sun.jersey.spi.service.ServiceFinder;
import com.sun.jersey.spi.monitoring.GlassfishMonitoringProvider;
import java.util.logging.Logger;

public class GlassFishMonitoringInitializer
{
    private static final Logger LOGGER;
    
    public static void initialize() {
        try {
            for (final GlassfishMonitoringProvider monitoring : ServiceFinder.find(GlassfishMonitoringProvider.class)) {
                monitoring.register();
            }
        }
        catch (ServiceConfigurationError ex) {
            GlassFishMonitoringInitializer.LOGGER.log(Level.CONFIG, "GlassFish Jersey monitoring could not be enabled. Processing will continue but montoring is disabled.", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(GlassFishMonitoringInitializer.class.getName());
    }
}
