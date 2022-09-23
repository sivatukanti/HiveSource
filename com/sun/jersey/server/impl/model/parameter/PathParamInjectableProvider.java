// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter;

import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.api.ParamException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import java.lang.annotation.Annotation;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.ws.rs.core.PathSegment;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import javax.ws.rs.PathParam;

public final class PathParamInjectableProvider extends BaseParamInjectableProvider<PathParam>
{
    public PathParamInjectableProvider(final MultivaluedParameterExtractorProvider w) {
        super(w);
    }
    
    @Override
    public Injectable<?> getInjectable(final ComponentContext ic, final PathParam a, final Parameter c) {
        final String parameterName = c.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        if (c.getParameterClass() == PathSegment.class) {
            return new PathParamPathSegmentInjectable(parameterName, !c.isEncoded());
        }
        if (c.getParameterClass() == List.class && c.getParameterType() instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)c.getParameterType();
            final Type[] targs = pt.getActualTypeArguments();
            if (targs.length == 1 && targs[0] == PathSegment.class) {
                return new PathParamListPathSegmentInjectable(parameterName, !c.isEncoded());
            }
        }
        final MultivaluedParameterExtractor e = this.getWithoutDefaultValue(c);
        if (e == null) {
            return null;
        }
        return new PathParamInjectable(e, !c.isEncoded());
    }
    
    private static final class PathParamInjectable extends AbstractHttpContextInjectable<Object>
    {
        private final MultivaluedParameterExtractor extractor;
        private final boolean decode;
        
        PathParamInjectable(final MultivaluedParameterExtractor extractor, final boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }
        
        @Override
        public Object getValue(final HttpContext context) {
            try {
                return this.extractor.extract(context.getUriInfo().getPathParameters(this.decode));
            }
            catch (ExtractorContainerException e) {
                throw new ParamException.PathParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultStringValue());
            }
        }
    }
    
    private static final class PathParamPathSegmentInjectable extends AbstractHttpContextInjectable<PathSegment>
    {
        private final String name;
        private final boolean decode;
        
        PathParamPathSegmentInjectable(final String name, final boolean decode) {
            this.name = name;
            this.decode = decode;
        }
        
        @Override
        public PathSegment getValue(final HttpContext context) {
            final List<PathSegment> ps = context.getUriInfo().getPathSegments(this.name, this.decode);
            if (ps.isEmpty()) {
                return null;
            }
            return ps.get(ps.size() - 1);
        }
    }
    
    private static final class PathParamListPathSegmentInjectable extends AbstractHttpContextInjectable<List<PathSegment>>
    {
        private final String name;
        private final boolean decode;
        
        PathParamListPathSegmentInjectable(final String name, final boolean decode) {
            this.name = name;
            this.decode = decode;
        }
        
        @Override
        public List<PathSegment> getValue(final HttpContext context) {
            return context.getUriInfo().getPathSegments(this.name, this.decode);
        }
    }
}
