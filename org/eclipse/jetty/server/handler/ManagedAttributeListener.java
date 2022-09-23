// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.util.log.Log;
import java.util.Enumeration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextAttributeEvent;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jetty.util.log.Logger;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;

public class ManagedAttributeListener implements ServletContextListener, ServletContextAttributeListener
{
    private static final Logger LOG;
    final Set<String> _managedAttributes;
    final ContextHandler _context;
    
    public ManagedAttributeListener(final ContextHandler context, final String... managedAttributes) {
        this._managedAttributes = new HashSet<String>();
        this._context = context;
        for (final String attr : managedAttributes) {
            this._managedAttributes.add(attr);
        }
        if (ManagedAttributeListener.LOG.isDebugEnabled()) {
            ManagedAttributeListener.LOG.debug("managedAttributes {}", this._managedAttributes);
        }
    }
    
    @Override
    public void attributeReplaced(final ServletContextAttributeEvent event) {
        if (this._managedAttributes.contains(event.getName())) {
            this.updateBean(event.getName(), event.getValue(), event.getServletContext().getAttribute(event.getName()));
        }
    }
    
    @Override
    public void attributeRemoved(final ServletContextAttributeEvent event) {
        if (this._managedAttributes.contains(event.getName())) {
            this.updateBean(event.getName(), event.getValue(), null);
        }
    }
    
    @Override
    public void attributeAdded(final ServletContextAttributeEvent event) {
        if (this._managedAttributes.contains(event.getName())) {
            this.updateBean(event.getName(), null, event.getValue());
        }
    }
    
    @Override
    public void contextInitialized(final ServletContextEvent event) {
        final Enumeration<String> e = event.getServletContext().getAttributeNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            if (this._managedAttributes.contains(name)) {
                this.updateBean(name, null, event.getServletContext().getAttribute(name));
            }
        }
    }
    
    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        final Enumeration<String> e = this._context.getServletContext().getAttributeNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            if (this._managedAttributes.contains(name)) {
                this.updateBean(name, event.getServletContext().getAttribute(name), null);
            }
        }
    }
    
    protected void updateBean(final String name, final Object oldBean, final Object newBean) {
        ManagedAttributeListener.LOG.info("update {} {}->{} on {}", name, oldBean, newBean, this._context);
        if (ManagedAttributeListener.LOG.isDebugEnabled()) {
            ManagedAttributeListener.LOG.debug("update {} {}->{} on {}", name, oldBean, newBean, this._context);
        }
        this._context.updateBean(oldBean, newBean, false);
    }
    
    static {
        LOG = Log.getLogger(ManagedAttributeListener.class);
    }
}
