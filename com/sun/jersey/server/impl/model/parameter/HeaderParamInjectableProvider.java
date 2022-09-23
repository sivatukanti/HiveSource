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
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import javax.ws.rs.HeaderParam;

public final class HeaderParamInjectableProvider extends BaseParamInjectableProvider<HeaderParam>
{
    public HeaderParamInjectableProvider(final MultivaluedParameterExtractorProvider w) {
        super(w);
    }
    
    @Override
    public Injectable getInjectable(final ComponentContext ic, final HeaderParam a, final Parameter c) {
        final String parameterName = c.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        final MultivaluedParameterExtractor e = this.get(c);
        if (e == null) {
            return null;
        }
        return new HeaderParamInjectable(e);
    }
    
    private static final class HeaderParamInjectable extends AbstractHttpContextInjectable<Object>
    {
        private MultivaluedParameterExtractor extractor;
        
        HeaderParamInjectable(final MultivaluedParameterExtractor extractor) {
            this.extractor = extractor;
        }
        
        @Override
        public Object getValue(final HttpContext context) {
            try {
                return this.extractor.extract(context.getRequest().getRequestHeaders());
            }
            catch (ExtractorContainerException e) {
                throw new ParamException.HeaderParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultStringValue());
            }
        }
    }
}
