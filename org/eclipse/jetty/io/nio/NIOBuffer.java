// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.nio;

import java.nio.ByteBuffer;
import org.eclipse.jetty.io.Buffer;

public interface NIOBuffer extends Buffer
{
    ByteBuffer getByteBuffer();
    
    boolean isDirect();
}
