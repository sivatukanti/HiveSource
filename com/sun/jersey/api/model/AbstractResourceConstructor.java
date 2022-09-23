// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Constructor;

public class AbstractResourceConstructor implements Parameterized, AbstractModelComponent
{
    private Constructor ctor;
    private List<Parameter> parameters;
    
    public AbstractResourceConstructor(final Constructor constructor) {
        this.ctor = constructor;
        this.parameters = new ArrayList<Parameter>();
    }
    
    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }
    
    public Constructor getCtor() {
        return this.ctor;
    }
    
    @Override
    public void accept(final AbstractModelVisitor visitor) {
        visitor.visitAbstractResourceConstructor(this);
    }
    
    @Override
    public List<AbstractModelComponent> getComponents() {
        return null;
    }
}
