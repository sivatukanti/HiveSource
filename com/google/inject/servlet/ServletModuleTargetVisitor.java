// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import com.google.inject.spi.BindingTargetVisitor;

public interface ServletModuleTargetVisitor<T, V> extends BindingTargetVisitor<T, V>
{
    V visit(final LinkedFilterBinding p0);
    
    V visit(final InstanceFilterBinding p0);
    
    V visit(final LinkedServletBinding p0);
    
    V visit(final InstanceServletBinding p0);
}
