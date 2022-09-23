// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

import java.net.SocketAddress;

public interface PacketLogger
{
    void log(final String p0, final SocketAddress p1, final SocketAddress p2, final byte[] p3);
}
