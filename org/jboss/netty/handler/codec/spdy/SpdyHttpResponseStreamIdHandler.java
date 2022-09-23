// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import org.jboss.netty.channel.SimpleChannelHandler;

public class SpdyHttpResponseStreamIdHandler extends SimpleChannelHandler
{
    private static final Integer NO_ID;
    private final Queue<Integer> ids;
    
    public SpdyHttpResponseStreamIdHandler() {
        this.ids = new ConcurrentLinkedQueue<Integer>();
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpMessage) {
            final boolean contains = ((HttpMessage)e.getMessage()).headers().contains("X-SPDY-Stream-ID");
            if (!contains) {
                this.ids.add(SpdyHttpResponseStreamIdHandler.NO_ID);
            }
            else {
                this.ids.add(SpdyHttpHeaders.getStreamId((HttpMessage)e.getMessage()));
            }
        }
        else if (e.getMessage() instanceof SpdyRstStreamFrame) {
            this.ids.remove(((SpdyRstStreamFrame)e.getMessage()).getStreamId());
        }
        super.messageReceived(ctx, e);
    }
    
    @Override
    public void writeRequested(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpResponse) {
            final HttpResponse response = (HttpResponse)e.getMessage();
            final Integer id = this.ids.poll();
            if (id != null && id != (int)SpdyHttpResponseStreamIdHandler.NO_ID && !response.headers().contains("X-SPDY-Stream-ID")) {
                SpdyHttpHeaders.setStreamId(response, id);
            }
        }
        super.writeRequested(ctx, e);
    }
    
    static {
        NO_ID = -1;
    }
}
