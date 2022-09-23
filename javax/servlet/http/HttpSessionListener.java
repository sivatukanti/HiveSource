// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.http;

import java.util.EventListener;

public interface HttpSessionListener extends EventListener
{
    void sessionCreated(final HttpSessionEvent p0);
    
    void sessionDestroyed(final HttpSessionEvent p0);
}
