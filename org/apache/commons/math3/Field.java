// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3;

public interface Field<T>
{
    T getZero();
    
    T getOne();
    
    Class<? extends FieldElement<T>> getRuntimeClass();
}
