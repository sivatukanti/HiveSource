// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import java.util.Iterator;
import com.sun.jersey.spi.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.spi.StringReaderProvider;
import java.util.Set;
import com.sun.jersey.spi.StringReaderWorkers;

public class StringReaderFactory implements StringReaderWorkers
{
    private Set<StringReaderProvider> readers;
    
    public void init(final ProviderServices providerServices) {
        this.readers = (Set<StringReaderProvider>)providerServices.getProvidersAndServices(StringReaderProvider.class);
    }
    
    @Override
    public <T> StringReader<T> getStringReader(final Class<T> type, final Type genericType, final Annotation[] annotations) {
        for (final StringReaderProvider<T> srp : this.readers) {
            final StringReader<T> sr = srp.getStringReader(type, genericType, annotations);
            if (sr != null) {
                return sr;
            }
        }
        return null;
    }
}
