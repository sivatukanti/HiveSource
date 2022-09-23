// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

public abstract class NegotiatingClientConnectionFactory implements ClientConnectionFactory
{
    private final ClientConnectionFactory connectionFactory;
    
    protected NegotiatingClientConnectionFactory(final ClientConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    public ClientConnectionFactory getClientConnectionFactory() {
        return this.connectionFactory;
    }
}
