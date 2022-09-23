// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.async;

import com.sun.jersey.api.client.GenericType;

public interface ITypeListener<T> extends FutureListener<T>
{
    Class<T> getType();
    
    GenericType<T> getGenericType();
}
