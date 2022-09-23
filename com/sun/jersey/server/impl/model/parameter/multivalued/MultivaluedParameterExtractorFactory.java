// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.spi.StringReader;
import com.sun.jersey.impl.ImplMessages;
import java.security.AccessController;
import java.lang.reflect.Method;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.reflection.ReflectionHelper;
import java.util.SortedSet;
import java.util.Set;
import java.util.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.spi.StringReaderWorkers;

public final class MultivaluedParameterExtractorFactory implements MultivaluedParameterExtractorProvider
{
    private final StringReaderWorkers w;
    
    public MultivaluedParameterExtractorFactory(final StringReaderWorkers w) {
        this.w = w;
    }
    
    @Override
    public MultivaluedParameterExtractor getWithoutDefaultValue(final Parameter p) {
        return this.process(this.w, null, p.getParameterClass(), p.getParameterType(), p.getAnnotations(), p.getSourceName());
    }
    
    @Override
    public MultivaluedParameterExtractor get(final Parameter p) {
        return this.process(this.w, p.getDefaultValue(), p.getParameterClass(), p.getParameterType(), p.getAnnotations(), p.getSourceName());
    }
    
    private MultivaluedParameterExtractor process(final StringReaderWorkers w, final String defaultValue, Class<?> parameter, final Type parameterType, final Annotation[] annotations, final String parameterName) {
        if (parameter == List.class || parameter == Set.class || parameter == SortedSet.class) {
            final ReflectionHelper.TypeClassPair tcp = ReflectionHelper.getTypeArgumentAndClass(parameterType);
            if (tcp == null || tcp.c == String.class) {
                return CollectionStringExtractor.getInstance(parameter, parameterName, defaultValue);
            }
            final StringReader sr = w.getStringReader((Class<Object>)tcp.c, tcp.t, annotations);
            if (sr == null) {
                return null;
            }
            try {
                return CollectionStringReaderExtractor.getInstance(parameter, sr, parameterName, defaultValue);
            }
            catch (Exception e) {
                throw new ContainerException("Could not process parameter type " + parameter, e);
            }
        }
        if (parameter == String.class) {
            return new StringExtractor(parameterName, defaultValue);
        }
        if (parameter.isPrimitive()) {
            parameter = PrimitiveMapper.primitiveToClassMap.get(parameter);
            if (parameter == null) {
                return null;
            }
            final Method valueOf = AccessController.doPrivileged(ReflectionHelper.getValueOfStringMethodPA(parameter));
            if (valueOf != null) {
                try {
                    final Object defaultDefaultValue = PrimitiveMapper.primitiveToDefaultValueMap.get(parameter);
                    return new PrimitiveValueOfExtractor(valueOf, parameterName, defaultValue, defaultDefaultValue);
                }
                catch (Exception e2) {
                    throw new ContainerException(ImplMessages.DEFAULT_COULD_NOT_PROCESS_METHOD(defaultValue, valueOf));
                }
            }
        }
        else {
            final StringReader sr2 = w.getStringReader(parameter, parameterType, annotations);
            if (sr2 == null) {
                return null;
            }
            try {
                return new StringReaderExtractor(sr2, parameterName, defaultValue);
            }
            catch (Exception e2) {
                throw new ContainerException("Could not process parameter type " + parameter, e2);
            }
        }
        return null;
    }
}
