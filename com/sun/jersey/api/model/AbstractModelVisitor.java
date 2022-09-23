// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

public interface AbstractModelVisitor
{
    void visitAbstractResource(final AbstractResource p0);
    
    void visitAbstractField(final AbstractField p0);
    
    void visitAbstractSetterMethod(final AbstractSetterMethod p0);
    
    void visitAbstractResourceMethod(final AbstractResourceMethod p0);
    
    void visitAbstractSubResourceMethod(final AbstractSubResourceMethod p0);
    
    void visitAbstractSubResourceLocator(final AbstractSubResourceLocator p0);
    
    void visitAbstractResourceConstructor(final AbstractResourceConstructor p0);
}
