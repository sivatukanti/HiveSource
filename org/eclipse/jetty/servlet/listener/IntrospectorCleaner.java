// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet.listener;

import java.beans.Introspector;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class IntrospectorCleaner implements ServletContextListener
{
    @Override
    public void contextInitialized(final ServletContextEvent sce) {
    }
    
    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        Introspector.flushCaches();
    }
}
