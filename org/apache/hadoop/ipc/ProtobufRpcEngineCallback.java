// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import com.google.protobuf.Message;

public interface ProtobufRpcEngineCallback
{
    void setResponse(final Message p0);
    
    void error(final Throwable p0);
}
