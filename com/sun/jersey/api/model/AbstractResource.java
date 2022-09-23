// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

import java.util.Collection;
import java.util.LinkedList;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.util.List;
import java.lang.reflect.AnnotatedElement;

public class AbstractResource implements PathAnnotated, AbstractModelComponent, AnnotatedElement
{
    private final Class<?> resourceClass;
    private final PathValue uriPath;
    private final List<AbstractResourceConstructor> constructors;
    private final List<AbstractField> fields;
    private final List<AbstractSetterMethod> setterMethods;
    private final List<AbstractResourceMethod> resourceMethods;
    private final List<AbstractSubResourceMethod> subResourceMethods;
    private final List<AbstractSubResourceLocator> subResourceLocators;
    private final List<Method> postConstructMethods;
    private final List<Method> preDestroyMethods;
    
    public AbstractResource(final Class<?> resourceClass) {
        this(resourceClass, null);
    }
    
    public AbstractResource(final Class<?> resourceClass, final PathValue uriPath) {
        this.resourceClass = resourceClass;
        this.uriPath = uriPath;
        this.constructors = new ArrayList<AbstractResourceConstructor>(4);
        this.fields = new ArrayList<AbstractField>(4);
        this.setterMethods = new ArrayList<AbstractSetterMethod>(2);
        this.resourceMethods = new ArrayList<AbstractResourceMethod>(4);
        this.subResourceLocators = new ArrayList<AbstractSubResourceLocator>(4);
        this.subResourceMethods = new ArrayList<AbstractSubResourceMethod>(4);
        this.postConstructMethods = new ArrayList<Method>(1);
        this.preDestroyMethods = new ArrayList<Method>(1);
    }
    
    public AbstractResource(final String path, final AbstractResource ar) {
        this.uriPath = new PathValue(path);
        this.resourceClass = ar.resourceClass;
        this.constructors = ar.constructors;
        this.fields = ar.fields;
        this.setterMethods = ar.setterMethods;
        this.resourceMethods = ar.resourceMethods;
        this.subResourceMethods = ar.subResourceMethods;
        this.subResourceLocators = ar.subResourceLocators;
        this.postConstructMethods = ar.postConstructMethods;
        this.preDestroyMethods = ar.preDestroyMethods;
    }
    
    public Class<?> getResourceClass() {
        return this.resourceClass;
    }
    
    public boolean isSubResource() {
        return this.uriPath == null;
    }
    
    public boolean isRootResource() {
        return this.uriPath != null;
    }
    
    @Override
    public PathValue getPath() {
        return this.uriPath;
    }
    
    public List<AbstractResourceConstructor> getConstructors() {
        return this.constructors;
    }
    
    public List<AbstractField> getFields() {
        return this.fields;
    }
    
    public List<AbstractSetterMethod> getSetterMethods() {
        return this.setterMethods;
    }
    
    public List<AbstractResourceMethod> getResourceMethods() {
        return this.resourceMethods;
    }
    
    public List<AbstractSubResourceMethod> getSubResourceMethods() {
        return this.subResourceMethods;
    }
    
    public List<AbstractSubResourceLocator> getSubResourceLocators() {
        return this.subResourceLocators;
    }
    
    public List<Method> getPostConstructMethods() {
        return this.postConstructMethods;
    }
    
    public List<Method> getPreDestroyMethods() {
        return this.preDestroyMethods;
    }
    
    @Override
    public void accept(final AbstractModelVisitor visitor) {
        visitor.visitAbstractResource(this);
    }
    
    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> a) {
        return this.resourceClass.isAnnotationPresent(a);
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> a) {
        return this.resourceClass.getAnnotation(a);
    }
    
    @Override
    public Annotation[] getAnnotations() {
        return this.resourceClass.getAnnotations();
    }
    
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.resourceClass.getDeclaredAnnotations();
    }
    
    @Override
    public String toString() {
        return "AbstractResource(" + ((null == this.getPath()) ? "" : ("\"" + this.getPath().getValue() + "\", - ")) + this.getResourceClass().getSimpleName() + ": " + this.getConstructors().size() + " constructors, " + this.getFields().size() + " fields, " + this.getSetterMethods().size() + " setter methods, " + this.getResourceMethods().size() + " res methods, " + this.getSubResourceMethods().size() + " subres methods, " + this.getSubResourceLocators().size() + " subres locators " + ")";
    }
    
    @Override
    public List<AbstractModelComponent> getComponents() {
        final List<AbstractModelComponent> components = new LinkedList<AbstractModelComponent>();
        components.addAll(this.getConstructors());
        components.addAll(this.getFields());
        components.addAll(this.getSetterMethods());
        components.addAll(this.getResourceMethods());
        components.addAll(this.getSubResourceMethods());
        components.addAll(this.getSubResourceLocators());
        return components;
    }
}
