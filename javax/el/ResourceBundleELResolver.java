// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

import java.util.Enumeration;
import java.util.List;
import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleELResolver extends ELResolver
{
    @Override
    public Object getValue(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(true);
            if (property != null) {
                try {
                    return ((ResourceBundle)base).getObject(property.toString());
                }
                catch (MissingResourceException e) {
                    return "???" + property + "???";
                }
            }
        }
        return null;
    }
    
    @Override
    public Class<?> getType(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(true);
        }
        return null;
    }
    
    @Override
    public void setValue(final ELContext context, final Object base, final Object property, final Object value) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(true);
            throw new PropertyNotWritableException("ResourceBundles are immutable");
        }
    }
    
    @Override
    public boolean isReadOnly(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(true);
            return true;
        }
        return false;
    }
    
    @Override
    public Iterator getFeatureDescriptors(final ELContext context, final Object base) {
        if (base instanceof ResourceBundle) {
            final ResourceBundle bundle = (ResourceBundle)base;
            final List features = new ArrayList();
            String key = null;
            FeatureDescriptor desc = null;
            final Enumeration e = bundle.getKeys();
            while (e.hasMoreElements()) {
                key = e.nextElement();
                desc = new FeatureDescriptor();
                desc.setDisplayName(key);
                desc.setExpert(false);
                desc.setHidden(false);
                desc.setName(key);
                desc.setPreferred(true);
                desc.setValue("type", String.class);
                desc.setValue("resolvableAtDesignTime", Boolean.TRUE);
                features.add(desc);
            }
            return features.iterator();
        }
        return null;
    }
    
    @Override
    public Class<?> getCommonPropertyType(final ELContext context, final Object base) {
        if (base instanceof ResourceBundle) {
            return String.class;
        }
        return null;
    }
}
