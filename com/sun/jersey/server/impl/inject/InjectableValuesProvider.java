// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.inject;

import java.util.Iterator;
import com.sun.jersey.api.container.ContainerException;
import javax.ws.rs.WebApplicationException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.inject.Injectable;
import java.util.List;

public class InjectableValuesProvider
{
    private final List<AbstractHttpContextInjectable> is;
    
    public InjectableValuesProvider(final List<Injectable> is) {
        this.is = (List<AbstractHttpContextInjectable>)AbstractHttpContextInjectable.transform(is);
    }
    
    public List<AbstractHttpContextInjectable> getInjectables() {
        return this.is;
    }
    
    public Object[] getInjectableValues(final HttpContext context) {
        final Object[] params = new Object[this.is.size()];
        try {
            int index = 0;
            for (final AbstractHttpContextInjectable i : this.is) {
                params[index++] = i.getValue(context);
            }
            return params;
        }
        catch (WebApplicationException e) {
            throw e;
        }
        catch (ContainerException e2) {
            throw e2;
        }
        catch (RuntimeException e3) {
            throw new ContainerException("Exception obtaining parameters", e3);
        }
    }
}
