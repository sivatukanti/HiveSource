// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.io.OutputStream;
import java.io.InputStream;

public interface ForwardedTCPIPDaemon extends Runnable
{
    void setChannel(final ChannelForwardedTCPIP p0, final InputStream p1, final OutputStream p2);
    
    void setArg(final Object[] p0);
}
