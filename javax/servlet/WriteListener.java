// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

public interface WriteListener extends EventListener
{
    void onWritePossible() throws IOException;
    
    void onError(final Throwable p0);
}
