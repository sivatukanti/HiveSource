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
import javax.ws.rs.core.Cookie;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import javax.ws.rs.CookieParam;

public final class CookieParamInjectableProvider extends BaseParamInjectableProvider<CookieParam>
{
    public CookieParamInjectableProvider(final MultivaluedParameterExtractorProvider w) {
        super(w);
    }
    
    @Override
    public Injectable getInjectable(final ComponentContext ic, final CookieParam a, final Parameter c) {
        final String parameterName = c.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            return null;
        }
        if (c.getParameterClass() == Cookie.class) {
            return new CookieTypeParamInjectable(parameterName);
        }
        final MultivaluedParameterExtractor e = this.get(c);
        if (e == null) {
            return null;
        }
        return new CookieParamInjectable(e);
    }
    
    private static final class CookieParamInjectable extends AbstractHttpContextInjectable<Object>
    {
        private final MultivaluedParameterExtractor extractor;
        
        CookieParamInjectable(final MultivaluedParameterExtractor extractor) {
            this.extractor = extractor;
        }
        
        @Override
        public Object getValue(final HttpContext context) {
            try {
                return this.extractor.extract(context.getRequest().getCookieNameValueMap());
            }
            catch (ExtractorContainerException e) {
                throw new ParamException.CookieParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultStringValue());
            }
        }
    }
    
    private static final class CookieTypeParamInjectable extends AbstractHttpContextInjectable<Cookie>
    {
        private final String name;
        
        CookieTypeParamInjectable(final String name) {
            this.name = name;
        }
        
        @Override
        public Cookie getValue(final HttpContext context) {
            return context.getRequest().getCookies().get(this.name);
        }
    }
}
