// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.el;

import java.util.Enumeration;
import java.util.ArrayList;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.el.ELContext;
import javax.el.ELResolver;

public class ScopedAttributeELResolver extends ELResolver
{
    @Override
    public Object getValue(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null) {
            context.setPropertyResolved(true);
            if (property instanceof String) {
                final String attribute = (String)property;
                final PageContext ctxt = (PageContext)context.getContext(JspContext.class);
                return ctxt.findAttribute(attribute);
            }
        }
        return null;
    }
    
    @Override
    public Class<Object> getType(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null) {
            context.setPropertyResolved(true);
            return Object.class;
        }
        return null;
    }
    
    @Override
    public void setValue(final ELContext context, final Object base, final Object property, final Object val) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null) {
            context.setPropertyResolved(true);
            if (property instanceof String) {
                final PageContext ctxt = (PageContext)context.getContext(JspContext.class);
                final String attr = (String)property;
                if (ctxt.getAttribute(attr, 2) != null) {
                    ctxt.setAttribute(attr, val, 2);
                }
                else if (ctxt.getAttribute(attr, 3) != null) {
                    ctxt.setAttribute(attr, val, 3);
                }
                else if (ctxt.getAttribute(attr, 4) != null) {
                    ctxt.setAttribute(attr, val, 4);
                }
                else {
                    ctxt.setAttribute(attr, val, 1);
                }
            }
        }
    }
    
    @Override
    public boolean isReadOnly(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null) {
            context.setPropertyResolved(true);
        }
        return false;
    }
    
    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base) {
        final ArrayList<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>();
        final PageContext ctxt = (PageContext)context.getContext(JspContext.class);
        Enumeration attrs = ctxt.getAttributeNamesInScope(1);
        while (attrs.hasMoreElements()) {
            final String name = attrs.nextElement();
            final Object value = ctxt.getAttribute(name, 1);
            final FeatureDescriptor descriptor = new FeatureDescriptor();
            descriptor.setName(name);
            descriptor.setDisplayName(name);
            descriptor.setShortDescription("page scope attribute");
            descriptor.setExpert(false);
            descriptor.setHidden(false);
            descriptor.setPreferred(true);
            descriptor.setValue("type", value.getClass());
            descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
            list.add(descriptor);
        }
        attrs = ctxt.getAttributeNamesInScope(2);
        while (attrs.hasMoreElements()) {
            final String name = attrs.nextElement();
            final Object value = ctxt.getAttribute(name, 2);
            final FeatureDescriptor descriptor = new FeatureDescriptor();
            descriptor.setName(name);
            descriptor.setDisplayName(name);
            descriptor.setShortDescription("request scope attribute");
            descriptor.setExpert(false);
            descriptor.setHidden(false);
            descriptor.setPreferred(true);
            descriptor.setValue("type", value.getClass());
            descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
            list.add(descriptor);
        }
        attrs = ctxt.getAttributeNamesInScope(3);
        while (attrs.hasMoreElements()) {
            final String name = attrs.nextElement();
            final Object value = ctxt.getAttribute(name, 3);
            final FeatureDescriptor descriptor = new FeatureDescriptor();
            descriptor.setName(name);
            descriptor.setDisplayName(name);
            descriptor.setShortDescription("session scope attribute");
            descriptor.setExpert(false);
            descriptor.setHidden(false);
            descriptor.setPreferred(true);
            descriptor.setValue("type", value.getClass());
            descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
            list.add(descriptor);
        }
        attrs = ctxt.getAttributeNamesInScope(4);
        while (attrs.hasMoreElements()) {
            final String name = attrs.nextElement();
            final Object value = ctxt.getAttribute(name, 4);
            final FeatureDescriptor descriptor = new FeatureDescriptor();
            descriptor.setName(name);
            descriptor.setDisplayName(name);
            descriptor.setShortDescription("application scope attribute");
            descriptor.setExpert(false);
            descriptor.setHidden(false);
            descriptor.setPreferred(true);
            descriptor.setValue("type", value.getClass());
            descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
            list.add(descriptor);
        }
        return list.iterator();
    }
    
    @Override
    public Class<String> getCommonPropertyType(final ELContext context, final Object base) {
        if (base == null) {
            return String.class;
        }
        return null;
    }
}
