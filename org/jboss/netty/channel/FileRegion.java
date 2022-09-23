// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import org.jboss.netty.util.ExternalResourceReleasable;

public interface FileRegion extends ExternalResourceReleasable
{
    long getPosition();
    
    long getCount();
    
    long transferTo(final WritableByteChannel p0, final long p1) throws IOException;
}
