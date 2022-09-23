// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.template;

import com.sun.jersey.spi.dispatch.RequestDispatcher;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.core.header.QualitySourceMediaType;
import java.util.List;
import com.sun.jersey.server.impl.model.method.ResourceMethod;

public class ViewResourceMethod extends ResourceMethod
{
    public ViewResourceMethod(final List<QualitySourceMediaType> produces) {
        super("GET", UriTemplate.EMPTY, MediaTypes.GENERAL_MEDIA_TYPE_LIST, produces, false, null);
    }
}
