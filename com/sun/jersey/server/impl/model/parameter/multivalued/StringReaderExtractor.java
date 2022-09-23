// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.api.container.ContainerException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.spi.StringReader;

final class StringReaderExtractor extends AbstractStringReaderExtractor
{
    public StringReaderExtractor(final StringReader sr, final String parameter, final String defaultStringValue) {
        super(sr, parameter, defaultStringValue);
    }
    
    @Override
    public Object extract(final MultivaluedMap<String, String> parameters) {
        final String v = parameters.getFirst(this.parameter);
        Object result = null;
        if (v != null) {
            try {
                result = this.sr.fromString(v);
            }
            catch (WebApplicationException ex) {
                throw ex;
            }
            catch (ContainerException ex2) {
                throw ex2;
            }
            catch (Exception ex3) {
                throw new ExtractorContainerException(ex3);
            }
        }
        if (result == null && this.defaultStringValue != null) {
            result = this.sr.fromString(this.defaultStringValue);
        }
        return result;
    }
}
