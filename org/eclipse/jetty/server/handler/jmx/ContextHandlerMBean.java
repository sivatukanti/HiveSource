// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler.jmx;

import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.util.Enumeration;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.server.handler.ContextHandler;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("ContextHandler mbean wrapper")
public class ContextHandlerMBean extends AbstractHandlerMBean
{
    public ContextHandlerMBean(final Object managedObject) {
        super(managedObject);
    }
    
    @ManagedAttribute("Map of context attributes")
    public Map<String, Object> getContextAttributes() {
        final Map<String, Object> map = new HashMap<String, Object>();
        final Attributes attrs = ((ContextHandler)this._managed).getAttributes();
        final Enumeration<String> en = attrs.getAttributeNames();
        while (en.hasMoreElements()) {
            final String name = en.nextElement();
            final Object value = attrs.getAttribute(name);
            map.put(name, value);
        }
        return map;
    }
    
    @ManagedOperation(value = "Set context attribute", impact = "ACTION")
    public void setContextAttribute(@Name(value = "name", description = "attribute name") final String name, @Name(value = "value", description = "attribute value") Object value) {
        final Attributes attrs = ((ContextHandler)this._managed).getAttributes();
        attrs.setAttribute(name, value);
    }
    
    @ManagedOperation(value = "Set context attribute", impact = "ACTION")
    public void setContextAttribute(@Name(value = "name", description = "attribute name") final String name, @Name(value = "value", description = "attribute value") String value) {
        final Attributes attrs = ((ContextHandler)this._managed).getAttributes();
        attrs.setAttribute(name, value);
    }
    
    @ManagedOperation(value = "Remove context attribute", impact = "ACTION")
    public void removeContextAttribute(@Name(value = "name", description = "attribute name") final String name) {
        final Attributes attrs = ((ContextHandler)this._managed).getAttributes();
        attrs.removeAttribute(name);
    }
}
