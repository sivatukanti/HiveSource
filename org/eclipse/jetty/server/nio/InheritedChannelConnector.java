// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.nio;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import org.eclipse.jetty.util.log.Logger;

public class InheritedChannelConnector extends SelectChannelConnector
{
    private static final Logger LOG;
    
    @Override
    public void open() throws IOException {
        synchronized (this) {
            try {
                final Channel channel = System.inheritedChannel();
                if (channel instanceof ServerSocketChannel) {
                    this._acceptChannel = (ServerSocketChannel)channel;
                }
                else {
                    InheritedChannelConnector.LOG.warn("Unable to use System.inheritedChannel() [" + channel + "]. Trying a new ServerSocketChannel at " + this.getHost() + ":" + this.getPort(), new Object[0]);
                }
                if (this._acceptChannel != null) {
                    this._acceptChannel.configureBlocking(true);
                }
            }
            catch (NoSuchMethodError e) {
                InheritedChannelConnector.LOG.warn("Need at least Java 5 to use socket inherited from xinetd/inetd.", new Object[0]);
            }
            if (this._acceptChannel == null) {
                super.open();
            }
        }
    }
    
    static {
        LOG = Log.getLogger(InheritedChannelConnector.class);
    }
}
