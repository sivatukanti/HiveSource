// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.nio;

import java.lang.reflect.Method;
import java.io.IOException;
import org.mortbay.log.Log;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.Channel;

public class InheritedChannelConnector extends SelectChannelConnector
{
    public void open() throws IOException {
        synchronized (this) {
            try {
                final Method m = System.class.getMethod("inheritedChannel", (Class[])null);
                if (m != null) {
                    final Channel channel = (Channel)m.invoke(null, (Object[])null);
                    if (channel instanceof ServerSocketChannel) {
                        this._acceptChannel = (ServerSocketChannel)channel;
                    }
                }
                if (this._acceptChannel != null) {
                    this._acceptChannel.configureBlocking(false);
                }
            }
            catch (Exception e) {
                Log.warn(e);
            }
            if (this._acceptChannel != null) {
                throw new IOException("No System.inheritedChannel()");
            }
            super.open();
        }
    }
}
