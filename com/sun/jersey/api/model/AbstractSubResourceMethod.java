// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.Method;

public class AbstractSubResourceMethod extends AbstractResourceMethod implements PathAnnotated
{
    private PathValue uriPath;
    
    public AbstractSubResourceMethod(final AbstractResource resource, final Method method, final Class returnType, final Type genericReturnType, final PathValue uriPath, final String httpMethod, final Annotation[] annotations) {
        super(resource, method, returnType, genericReturnType, httpMethod, annotations);
        this.uriPath = uriPath;
    }
    
    @Override
    public PathValue getPath() {
        return this.uriPath;
    }
    
    @Override
    public void accept(final AbstractModelVisitor visitor) {
        visitor.visitAbstractSubResourceMethod(this);
    }
    
    @Override
    public String toString() {
        return "AbstractSubResourceMethod(" + this.getMethod().getDeclaringClass().getSimpleName() + "#" + this.getMethod().getName() + ")";
    }
}
