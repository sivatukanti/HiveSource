// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io.nio;

import java.nio.ByteBuffer;
import org.mortbay.io.Buffer;

public interface NIOBuffer extends Buffer
{
    ByteBuffer getByteBuffer();
    
    boolean isDirect();
}
