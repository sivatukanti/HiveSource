// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.http;

import java.util.EventListener;

public interface HttpSessionAttributeListener extends EventListener
{
    void attributeAdded(final HttpSessionBindingEvent p0);
    
    void attributeRemoved(final HttpSessionBindingEvent p0);
    
    void attributeReplaced(final HttpSessionBindingEvent p0);
}
