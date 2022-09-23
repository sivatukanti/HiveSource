// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.io.Closeable;
import java.lang.reflect.InvocationHandler;

public interface RpcInvocationHandler extends InvocationHandler, Closeable
{
    Client.ConnectionId getConnectionId();
}
