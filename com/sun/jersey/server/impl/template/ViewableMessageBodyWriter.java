// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.template;

import javax.ws.rs.WebApplicationException;
import com.sun.jersey.spi.template.ResolvedViewable;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sun.jersey.spi.template.TemplateContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import com.sun.jersey.spi.inject.ServerSide;
import com.sun.jersey.spi.inject.ConstrainedTo;
import com.sun.jersey.api.view.Viewable;
import javax.ws.rs.ext.MessageBodyWriter;

@ConstrainedTo(ServerSide.class)
public final class ViewableMessageBodyWriter implements MessageBodyWriter<Viewable>
{
    @Context
    UriInfo ui;
    @Context
    TemplateContext tc;
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return Viewable.class.isAssignableFrom(type);
    }
    
    @Override
    public void writeTo(final Viewable v, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        final ResolvedViewable rv = this.resolve(v);
        if (rv == null) {
            throw new IOException("The template name, " + v.getTemplateName() + ", could not be resolved to a fully qualified template name");
        }
        rv.writeTo(entityStream);
    }
    
    private ResolvedViewable resolve(final Viewable v) {
        if (v instanceof ResolvedViewable) {
            return (ResolvedViewable)v;
        }
        return this.tc.resolveViewable(v, this.ui);
    }
    
    @Override
    public long getSize(final Viewable t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return -1L;
    }
}
