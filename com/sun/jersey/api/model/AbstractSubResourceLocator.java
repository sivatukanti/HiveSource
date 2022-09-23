// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

import java.util.ArrayList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class AbstractSubResourceLocator extends AbstractMethod implements PathAnnotated, Parameterized, AbstractModelComponent
{
    private PathValue uriPath;
    private List<Parameter> parameters;
    
    public AbstractSubResourceLocator(final AbstractResource resource, final Method method, final PathValue uriPath, final Annotation[] annotations) {
        super(resource, method, annotations);
        this.uriPath = uriPath;
        this.parameters = new ArrayList<Parameter>();
    }
    
    @Override
    public PathValue getPath() {
        return this.uriPath;
    }
    
    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }
    
    @Override
    public void accept(final AbstractModelVisitor visitor) {
        visitor.visitAbstractSubResourceLocator(this);
    }
    
    @Override
    public List<AbstractModelComponent> getComponents() {
        return null;
    }
    
    @Override
    public String toString() {
        return "AbstractSubResourceLocator(" + this.getMethod().getDeclaringClass().getSimpleName() + "#" + this.getMethod().getName() + ")";
    }
}
