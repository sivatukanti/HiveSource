// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter;

import java.util.List;
import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.api.ParamException;
import javax.ws.rs.core.PathSegment;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import java.lang.annotation.Annotation;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import javax.ws.rs.MatrixParam;

public final class MatrixParamInjectableProvider extends BaseParamInjectableProvider<MatrixParam>
{
    public MatrixParamInjectableProvider(final MultivaluedParameterExtractorProvider w) {
        super(w);
    }
    
    @Override
    public Injectable getInjectable(final ComponentContext ic, final MatrixParam a, final Parameter c) {
        final String parameterName = c.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        final MultivaluedParameterExtractor e = this.get(c);
        if (e == null) {
            return null;
        }
        return new MatrixParamInjectable(e, !c.isEncoded());
    }
    
    private static final class MatrixParamInjectable extends AbstractHttpContextInjectable<Object>
    {
        private final MultivaluedParameterExtractor extractor;
        private final boolean decode;
        
        MatrixParamInjectable(final MultivaluedParameterExtractor extractor, final boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }
        
        @Override
        public Object getValue(final HttpContext context) {
            final List<PathSegment> l = context.getUriInfo().getPathSegments(this.decode);
            final PathSegment p = l.get(l.size() - 1);
            try {
                return this.extractor.extract(p.getMatrixParameters());
            }
            catch (ExtractorContainerException e) {
                throw new ParamException.MatrixParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultStringValue());
            }
        }
    }
}
