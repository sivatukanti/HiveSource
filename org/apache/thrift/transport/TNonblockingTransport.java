// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.io.IOException;

public abstract class TNonblockingTransport extends TTransport
{
    public abstract boolean startConnect() throws IOException;
    
    public abstract boolean finishConnect() throws IOException;
    
    public abstract SelectionKey registerSelector(final Selector p0, final int p1) throws IOException;
    
    public abstract int read(final ByteBuffer p0) throws IOException;
    
    public abstract int write(final ByteBuffer p0) throws IOException;
}
