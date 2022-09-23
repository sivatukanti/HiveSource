// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.io.IOException;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ProtobufHelper
{
    private ProtobufHelper() {
    }
    
    public static IOException getRemoteException(final ServiceException se) {
        final Throwable e = se.getCause();
        if (e == null) {
            return new IOException(se);
        }
        return (IOException)((e instanceof IOException) ? e : new IOException(se));
    }
}
