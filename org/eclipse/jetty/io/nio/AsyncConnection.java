// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.nio;

import java.io.IOException;
import org.eclipse.jetty.io.Connection;

public interface AsyncConnection extends Connection
{
    void onInputShutdown() throws IOException;
}
