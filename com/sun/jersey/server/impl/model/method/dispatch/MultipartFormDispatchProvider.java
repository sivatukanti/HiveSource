// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.method.dispatch;

import javax.ws.rs.FormParam;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.spi.inject.Injectable;
import java.util.List;
import java.util.Iterator;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.JavaMethodInvokerFactory;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.api.model.AbstractResourceMethod;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

public class MultipartFormDispatchProvider extends FormDispatchProvider
{
    private static final Logger LOGGER;
    private static MediaType MULTIPART_FORM_DATA;
    
    @Override
    public RequestDispatcher create(final AbstractResourceMethod method) {
        return this.create(method, JavaMethodInvokerFactory.getDefault());
    }
    
    @Override
    public RequestDispatcher create(final AbstractResourceMethod method, final JavaMethodInvoker invoker) {
        boolean found = false;
        for (final MediaType m : method.getSupportedInputTypes()) {
            found = (!m.isWildcardSubtype() && m.isCompatible(MultipartFormDispatchProvider.MULTIPART_FORM_DATA));
            if (found) {
                break;
            }
        }
        if (!found) {
            return null;
        }
        return super.create(method, invoker);
    }
    
    @Override
    protected List<Injectable> getInjectables(final AbstractResourceMethod method) {
        for (int i = 0; i < method.getParameters().size(); ++i) {
            final Parameter p = method.getParameters().get(i);
            if (p.getAnnotation().annotationType() == FormParam.class) {
                MultipartFormDispatchProvider.LOGGER.severe("Resource methods utilizing @FormParam and consuming \"multipart/form-data\" are no longer supported. See @FormDataParam.");
            }
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(MultipartFormDispatchProvider.class.getName());
        MultipartFormDispatchProvider.MULTIPART_FORM_DATA = new MediaType("multipart", "form-data");
    }
}
