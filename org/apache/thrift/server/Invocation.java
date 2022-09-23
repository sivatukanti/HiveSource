// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.server;

class Invocation implements Runnable
{
    private final AbstractNonblockingServer.FrameBuffer frameBuffer;
    
    public Invocation(final AbstractNonblockingServer.FrameBuffer frameBuffer) {
        this.frameBuffer = frameBuffer;
    }
    
    public void run() {
        this.frameBuffer.invoke();
    }
}
