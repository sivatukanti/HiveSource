// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.auth;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Enumeration;
import org.apache.zookeeper.server.ZooKeeperServer;
import java.util.HashMap;
import org.slf4j.Logger;

public class ProviderRegistry
{
    private static final Logger LOG;
    private static boolean initialized;
    private static HashMap<String, AuthenticationProvider> authenticationProviders;
    
    public static void initialize() {
        synchronized (ProviderRegistry.class) {
            if (ProviderRegistry.initialized) {
                return;
            }
            final IPAuthenticationProvider ipp = new IPAuthenticationProvider();
            final DigestAuthenticationProvider digp = new DigestAuthenticationProvider();
            ProviderRegistry.authenticationProviders.put(ipp.getScheme(), ipp);
            ProviderRegistry.authenticationProviders.put(digp.getScheme(), digp);
            final Enumeration<Object> en = System.getProperties().keys();
            while (en.hasMoreElements()) {
                final String k = en.nextElement();
                if (k.startsWith("zookeeper.authProvider.")) {
                    final String className = System.getProperty(k);
                    try {
                        final Class<?> c = ZooKeeperServer.class.getClassLoader().loadClass(className);
                        final AuthenticationProvider ap = (AuthenticationProvider)c.getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                        ProviderRegistry.authenticationProviders.put(ap.getScheme(), ap);
                    }
                    catch (Exception e) {
                        ProviderRegistry.LOG.warn("Problems loading " + className, e);
                    }
                }
            }
            ProviderRegistry.initialized = true;
        }
    }
    
    public static AuthenticationProvider getProvider(final String scheme) {
        if (!ProviderRegistry.initialized) {
            initialize();
        }
        return ProviderRegistry.authenticationProviders.get(scheme);
    }
    
    public static String listProviders() {
        final StringBuilder sb = new StringBuilder();
        for (final String s : ProviderRegistry.authenticationProviders.keySet()) {
            sb.append(s + " ");
        }
        return sb.toString();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ProviderRegistry.class);
        ProviderRegistry.initialized = false;
        ProviderRegistry.authenticationProviders = new HashMap<String, AuthenticationProvider>();
    }
}
