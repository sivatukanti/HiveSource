// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

import java.util.List;

public interface AbstractModelComponent
{
    void accept(final AbstractModelVisitor p0);
    
    List<AbstractModelComponent> getComponents();
}
