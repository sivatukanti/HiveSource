// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

import java.util.Iterator;
import java.util.ArrayList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class AbstractResourceMethod extends AbstractMethod implements Parameterized, AbstractModelComponent
{
    private String httpMethod;
    private List<MediaType> consumeMimeList;
    private List<MediaType> produceMimeList;
    private List<Parameter> parameters;
    private Class returnType;
    private Type genericReturnType;
    private boolean isConsumesDeclared;
    private boolean isProducesDeclared;
    
    public AbstractResourceMethod(final AbstractResource resource, final Method method, final Class returnType, final Type genericReturnType, final String httpMethod, final Annotation[] annotations) {
        super(resource, method, annotations);
        this.httpMethod = httpMethod.toUpperCase();
        this.consumeMimeList = new ArrayList<MediaType>();
        this.produceMimeList = new ArrayList<MediaType>();
        this.returnType = returnType;
        this.genericReturnType = genericReturnType;
        this.parameters = new ArrayList<Parameter>();
    }
    
    public AbstractResource getDeclaringResource() {
        return this.getResource();
    }
    
    public Class getReturnType() {
        return this.returnType;
    }
    
    public Type getGenericReturnType() {
        return this.genericReturnType;
    }
    
    public List<MediaType> getSupportedInputTypes() {
        return this.consumeMimeList;
    }
    
    public void setAreInputTypesDeclared(final boolean declared) {
        this.isConsumesDeclared = declared;
    }
    
    public boolean areInputTypesDeclared() {
        return this.isConsumesDeclared;
    }
    
    public List<MediaType> getSupportedOutputTypes() {
        return this.produceMimeList;
    }
    
    public void setAreOutputTypesDeclared(final boolean declared) {
        this.isProducesDeclared = declared;
    }
    
    public boolean areOutputTypesDeclared() {
        return this.isProducesDeclared;
    }
    
    public String getHttpMethod() {
        return this.httpMethod;
    }
    
    public boolean hasEntity() {
        for (final Parameter p : this.getParameters()) {
            if (Parameter.Source.ENTITY == p.getSource()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }
    
    @Override
    public void accept(final AbstractModelVisitor visitor) {
        visitor.visitAbstractResourceMethod(this);
    }
    
    @Override
    public List<AbstractModelComponent> getComponents() {
        return null;
    }
    
    @Override
    public String toString() {
        return "AbstractResourceMethod(" + this.getMethod().getDeclaringClass().getSimpleName() + "#" + this.getMethod().getName() + ")";
    }
}
