// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.server.Handler;

public class ProxyHandler extends ConnectHandler
{
    public ProxyHandler() {
    }
    
    public ProxyHandler(final Handler handler, final String[] white, final String[] black) {
        super(handler, white, black);
    }
    
    public ProxyHandler(final Handler handler) {
        super(handler);
    }
    
    public ProxyHandler(final String[] white, final String[] black) {
        super(white, black);
    }
}
