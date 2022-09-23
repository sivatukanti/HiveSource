// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.List;

public class AbstractField implements Parameterized, AbstractModelComponent
{
    private List<Parameter> parameters;
    private Field field;
    
    public AbstractField(final Field field) {
        assert null != field;
        this.field = field;
        this.parameters = new ArrayList<Parameter>();
    }
    
    public Field getField() {
        return this.field;
    }
    
    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }
    
    @Override
    public void accept(final AbstractModelVisitor visitor) {
        visitor.visitAbstractField(this);
    }
    
    @Override
    public List<AbstractModelComponent> getComponents() {
        return null;
    }
    
    @Override
    public String toString() {
        return "AbstractField(" + this.getField().getDeclaringClass().getSimpleName() + "#" + this.getField().getName() + ")";
    }
}
