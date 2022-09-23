// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.ArrayList;

public class CompositeELResolver extends ELResolver
{
    private final ArrayList<ELResolver> elResolvers;
    
    public CompositeELResolver() {
        this.elResolvers = new ArrayList<ELResolver>();
    }
    
    public void add(final ELResolver elResolver) {
        if (elResolver == null) {
            throw new NullPointerException();
        }
        this.elResolvers.add(elResolver);
    }
    
    @Override
    public Object getValue(final ELContext context, final Object base, final Object property) {
        context.setPropertyResolved(false);
        for (int i = 0, len = this.elResolvers.size(); i < len; ++i) {
            final ELResolver elResolver = this.elResolvers.get(i);
            final Object value = elResolver.getValue(context, base, property);
            if (context.isPropertyResolved()) {
                return value;
            }
        }
        return null;
    }
    
    @Override
    public Class<?> getType(final ELContext context, final Object base, final Object property) {
        context.setPropertyResolved(false);
        for (int i = 0, len = this.elResolvers.size(); i < len; ++i) {
            final ELResolver elResolver = this.elResolvers.get(i);
            final Class<?> type = elResolver.getType(context, base, property);
            if (context.isPropertyResolved()) {
                return type;
            }
        }
        return null;
    }
    
    @Override
    public void setValue(final ELContext context, final Object base, final Object property, final Object val) {
        context.setPropertyResolved(false);
        for (int i = 0, len = this.elResolvers.size(); i < len; ++i) {
            final ELResolver elResolver = this.elResolvers.get(i);
            elResolver.setValue(context, base, property, val);
            if (context.isPropertyResolved()) {
                return;
            }
        }
    }
    
    @Override
    public boolean isReadOnly(final ELContext context, final Object base, final Object property) {
        context.setPropertyResolved(false);
        for (int i = 0, len = this.elResolvers.size(); i < len; ++i) {
            final ELResolver elResolver = this.elResolvers.get(i);
            final boolean readOnly = elResolver.isReadOnly(context, base, property);
            if (context.isPropertyResolved()) {
                return readOnly;
            }
        }
        return false;
    }
    
    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base) {
        return new CompositeIterator(this.elResolvers.iterator(), context, base);
    }
    
    @Override
    public Class<?> getCommonPropertyType(final ELContext context, final Object base) {
        Class<?> commonPropertyType = null;
        for (final ELResolver elResolver : this.elResolvers) {
            final Class<?> type = elResolver.getCommonPropertyType(context, base);
            if (type == null) {
                continue;
            }
            if (commonPropertyType == null) {
                commonPropertyType = type;
            }
            else {
                if (commonPropertyType.isAssignableFrom(type)) {
                    continue;
                }
                if (!type.isAssignableFrom(commonPropertyType)) {
                    return null;
                }
                commonPropertyType = type;
            }
        }
        return commonPropertyType;
    }
    
    private static class CompositeIterator implements Iterator<FeatureDescriptor>
    {
        Iterator<ELResolver> compositeIter;
        Iterator<FeatureDescriptor> propertyIter;
        ELContext context;
        Object base;
        
        CompositeIterator(final Iterator<ELResolver> iter, final ELContext context, final Object base) {
            this.compositeIter = iter;
            this.context = context;
            this.base = base;
        }
        
        public boolean hasNext() {
            if (this.propertyIter == null || !this.propertyIter.hasNext()) {
                while (this.compositeIter.hasNext()) {
                    final ELResolver elResolver = this.compositeIter.next();
                    this.propertyIter = elResolver.getFeatureDescriptors(this.context, this.base);
                    if (this.propertyIter != null) {
                        return this.propertyIter.hasNext();
                    }
                }
                return false;
            }
            return this.propertyIter.hasNext();
        }
        
        public FeatureDescriptor next() {
            if (this.propertyIter == null || !this.propertyIter.hasNext()) {
                while (this.compositeIter.hasNext()) {
                    final ELResolver elResolver = this.compositeIter.next();
                    this.propertyIter = elResolver.getFeatureDescriptors(this.context, this.base);
                    if (this.propertyIter != null) {
                        return this.propertyIter.next();
                    }
                }
                return null;
            }
            return this.propertyIter.next();
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
