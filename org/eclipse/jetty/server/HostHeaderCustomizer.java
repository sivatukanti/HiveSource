// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.Objects;

public class HostHeaderCustomizer implements HttpConfiguration.Customizer
{
    private final String serverName;
    private final int serverPort;
    
    public HostHeaderCustomizer(final String serverName) {
        this(serverName, 0);
    }
    
    public HostHeaderCustomizer(final String serverName, final int serverPort) {
        this.serverName = Objects.requireNonNull(serverName);
        this.serverPort = serverPort;
    }
    
    @Override
    public void customize(final Connector connector, final HttpConfiguration channelConfig, final Request request) {
        if (request.getHeader("Host") == null) {
            request.setAuthority(this.serverName, this.serverPort);
        }
    }
}
