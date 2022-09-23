// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

import java.util.ArrayList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class AbstractSetterMethod extends AbstractMethod implements Parameterized, AbstractModelComponent
{
    private List<Parameter> parameters;
    
    public AbstractSetterMethod(final AbstractResource resource, final Method method, final Annotation[] annotations) {
        super(resource, method, annotations);
        this.parameters = new ArrayList<Parameter>();
    }
    
    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }
    
    @Override
    public void accept(final AbstractModelVisitor visitor) {
        visitor.visitAbstractSetterMethod(this);
    }
    
    @Override
    public List<AbstractModelComponent> getComponents() {
        return null;
    }
    
    @Override
    public String toString() {
        return "AbstractSetterMethod(" + this.getMethod().getDeclaringClass().getSimpleName() + "#" + this.getMethod().getName() + ")";
    }
}
