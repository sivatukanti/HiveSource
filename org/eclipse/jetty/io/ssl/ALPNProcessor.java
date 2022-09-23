// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.ssl;

import java.util.List;
import javax.net.ssl.SSLEngine;

public interface ALPNProcessor
{
    public interface Server
    {
        public static final Server NOOP = new Server() {};
        
        default void configure(final SSLEngine sslEngine) {
        }
    }
    
    public interface Client
    {
        public static final Client NOOP = new Client() {};
        
        default void configure(final SSLEngine sslEngine, final List<String> protocols) {
        }
        
        default void process(final SSLEngine sslEngine) {
        }
    }
}
