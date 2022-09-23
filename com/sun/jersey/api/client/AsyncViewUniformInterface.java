// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import java.util.concurrent.Future;

public interface AsyncViewUniformInterface
{
     <T> Future<T> head(final Class<T> p0);
    
     <T> Future<T> head(final T p0);
    
     <T> Future<T> options(final Class<T> p0);
    
     <T> Future<T> options(final T p0);
    
     <T> Future<T> get(final Class<T> p0);
    
     <T> Future<T> get(final T p0);
    
     <T> Future<T> put(final Class<T> p0);
    
     <T> Future<T> put(final T p0);
    
     <T> Future<T> put(final Class<T> p0, final Object p1);
    
     <T> Future<T> put(final T p0, final Object p1);
    
     <T> Future<T> post(final Class<T> p0);
    
     <T> Future<T> post(final T p0);
    
     <T> Future<T> post(final Class<T> p0, final Object p1);
    
     <T> Future<T> post(final T p0, final Object p1);
    
     <T> Future<T> delete(final Class<T> p0);
    
     <T> Future<T> delete(final T p0);
    
     <T> Future<T> delete(final Class<T> p0, final Object p1);
    
     <T> Future<T> delete(final T p0, final Object p1);
    
     <T> Future<T> method(final String p0, final Class<T> p1);
    
     <T> Future<T> method(final String p0, final T p1);
    
     <T> Future<T> method(final String p0, final Class<T> p1, final Object p2);
    
     <T> Future<T> method(final String p0, final T p1, final Object p2);
}
