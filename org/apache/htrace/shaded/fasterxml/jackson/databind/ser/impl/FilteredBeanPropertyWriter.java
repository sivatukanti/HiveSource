// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public abstract class FilteredBeanPropertyWriter
{
    public static BeanPropertyWriter constructViewBased(final BeanPropertyWriter base, final Class<?>[] viewsToIncludeIn) {
        if (viewsToIncludeIn.length == 1) {
            return new SingleView(base, viewsToIncludeIn[0]);
        }
        return new MultiView(base, viewsToIncludeIn);
    }
    
    private static final class SingleView extends BeanPropertyWriter
    {
        protected final BeanPropertyWriter _delegate;
        protected final Class<?> _view;
        
        protected SingleView(final BeanPropertyWriter delegate, final Class<?> view) {
            super(delegate);
            this._delegate = delegate;
            this._view = view;
        }
        
        @Override
        public SingleView rename(final NameTransformer transformer) {
            return new SingleView(this._delegate.rename(transformer), this._view);
        }
        
        @Override
        public void assignSerializer(final JsonSerializer<Object> ser) {
            this._delegate.assignSerializer(ser);
        }
        
        @Override
        public void assignNullSerializer(final JsonSerializer<Object> nullSer) {
            this._delegate.assignNullSerializer(nullSer);
        }
        
        @Override
        public void serializeAsField(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws Exception {
            final Class<?> activeView = prov.getActiveView();
            if (activeView == null || this._view.isAssignableFrom(activeView)) {
                this._delegate.serializeAsField(bean, jgen, prov);
            }
            else {
                this._delegate.serializeAsOmittedField(bean, jgen, prov);
            }
        }
        
        @Override
        public void serializeAsElement(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws Exception {
            final Class<?> activeView = prov.getActiveView();
            if (activeView == null || this._view.isAssignableFrom(activeView)) {
                this._delegate.serializeAsElement(bean, jgen, prov);
            }
            else {
                this._delegate.serializeAsPlaceholder(bean, jgen, prov);
            }
        }
    }
    
    private static final class MultiView extends BeanPropertyWriter
    {
        protected final BeanPropertyWriter _delegate;
        protected final Class<?>[] _views;
        
        protected MultiView(final BeanPropertyWriter delegate, final Class<?>[] views) {
            super(delegate);
            this._delegate = delegate;
            this._views = views;
        }
        
        @Override
        public MultiView rename(final NameTransformer transformer) {
            return new MultiView(this._delegate.rename(transformer), this._views);
        }
        
        @Override
        public void assignSerializer(final JsonSerializer<Object> ser) {
            this._delegate.assignSerializer(ser);
        }
        
        @Override
        public void assignNullSerializer(final JsonSerializer<Object> nullSer) {
            this._delegate.assignNullSerializer(nullSer);
        }
        
        @Override
        public void serializeAsField(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws Exception {
            final Class<?> activeView = prov.getActiveView();
            if (activeView != null) {
                int i;
                int len;
                for (i = 0, len = this._views.length; i < len && !this._views[i].isAssignableFrom(activeView); ++i) {}
                if (i == len) {
                    this._delegate.serializeAsOmittedField(bean, jgen, prov);
                    return;
                }
            }
            this._delegate.serializeAsField(bean, jgen, prov);
        }
        
        @Override
        public void serializeAsElement(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws Exception {
            final Class<?> activeView = prov.getActiveView();
            if (activeView != null) {
                int i;
                int len;
                for (i = 0, len = this._views.length; i < len && !this._views[i].isAssignableFrom(activeView); ++i) {}
                if (i == len) {
                    this._delegate.serializeAsPlaceholder(bean, jgen, prov);
                    return;
                }
            }
            this._delegate.serializeAsElement(bean, jgen, prov);
        }
    }
}
