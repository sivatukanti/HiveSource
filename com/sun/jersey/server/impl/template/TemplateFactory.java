// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.template;

import java.util.List;
import javax.ws.rs.core.UriInfo;
import com.sun.jersey.spi.template.TemplateContextException;
import com.sun.jersey.spi.template.ResolvedViewable;
import com.sun.jersey.api.view.Viewable;
import java.util.Iterator;
import com.sun.jersey.spi.template.TemplateProcessor;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.spi.template.ViewProcessor;
import java.util.Set;
import com.sun.jersey.spi.template.TemplateContext;

public final class TemplateFactory implements TemplateContext
{
    private final Set<ViewProcessor> viewProcessors;
    
    public TemplateFactory(final ProviderServices providerServices) {
        this.viewProcessors = (Set<ViewProcessor>)providerServices.getProvidersAndServices(ViewProcessor.class);
        final Set<TemplateProcessor> templateProcessors = providerServices.getProvidersAndServices(TemplateProcessor.class);
        for (final TemplateProcessor tp : templateProcessors) {
            this.viewProcessors.add(new TemplateViewProcessor(tp));
        }
    }
    
    private Set<ViewProcessor> getViewProcessors() {
        return this.viewProcessors;
    }
    
    @Override
    public ResolvedViewable resolveViewable(final Viewable v) {
        if (v.isTemplateNameAbsolute()) {
            return this.resolveAbsoluteViewable(v);
        }
        if (v.getResolvingClass() != null) {
            return this.resolveRelativeViewable(v, v.getResolvingClass());
        }
        if (v.getModel() == null) {
            throw new TemplateContextException("The model of the view MUST not be null");
        }
        return this.resolveRelativeViewable(v, v.getModel().getClass());
    }
    
    @Override
    public ResolvedViewable resolveViewable(final Viewable v, final UriInfo ui) {
        if (v.isTemplateNameAbsolute()) {
            return this.resolveAbsoluteViewable(v);
        }
        if (v.getResolvingClass() != null) {
            return this.resolveRelativeViewable(v, v.getResolvingClass());
        }
        final List<Object> mrs = ui.getMatchedResources();
        if (mrs == null || mrs.size() == 0) {
            throw new TemplateContextException("There is no last matching resource available");
        }
        return this.resolveRelativeViewable(v, mrs.get(0).getClass());
    }
    
    @Override
    public ResolvedViewable resolveViewable(final Viewable v, final Class<?> resolvingClass) {
        if (v.isTemplateNameAbsolute()) {
            return this.resolveAbsoluteViewable(v);
        }
        if (v.getResolvingClass() != null) {
            return this.resolveRelativeViewable(v, v.getResolvingClass());
        }
        if (resolvingClass == null) {
            throw new TemplateContextException("Resolving class MUST not be null");
        }
        return this.resolveRelativeViewable(v, resolvingClass);
    }
    
    private ResolvedViewable resolveAbsoluteViewable(final Viewable v) {
        for (final ViewProcessor vp : this.getViewProcessors()) {
            final Object resolvedTemplateObject = vp.resolve(v.getTemplateName());
            if (resolvedTemplateObject != null) {
                return new ResolvedViewable(vp, (T)resolvedTemplateObject, v);
            }
        }
        return null;
    }
    
    private ResolvedViewable resolveRelativeViewable(final Viewable v, final Class<?> resolvingClass) {
        String path = v.getTemplateName();
        if (path == null || path.length() == 0) {
            path = "index";
        }
        for (Class c = resolvingClass; c != Object.class; c = c.getSuperclass()) {
            final String absolutePath = this.getAbsolutePath(c, path, '/');
            for (final ViewProcessor vp : this.getViewProcessors()) {
                final Object resolvedTemplateObject = vp.resolve(absolutePath);
                if (resolvedTemplateObject != null) {
                    return new ResolvedViewable(vp, (T)resolvedTemplateObject, v, c);
                }
            }
        }
        for (Class c = resolvingClass; c != Object.class; c = c.getSuperclass()) {
            final String absolutePath = this.getAbsolutePath(c, path, '.');
            for (final ViewProcessor vp : this.getViewProcessors()) {
                final Object resolvedTemplateObject = vp.resolve(absolutePath);
                if (resolvedTemplateObject != null) {
                    return new ResolvedViewable(vp, (T)resolvedTemplateObject, v, c);
                }
            }
        }
        return null;
    }
    
    private String getAbsolutePath(final Class<?> resourceClass, final String path, final char delim) {
        return '/' + resourceClass.getName().replace('.', '/').replace('$', delim) + delim + path;
    }
}
