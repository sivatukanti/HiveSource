// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.http;

import java.util.EventListener;

public interface HttpSessionIdListener extends EventListener
{
    void sessionIdChanged(final HttpSessionEvent p0, final String p1);
}
