// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

public interface ViewUniformInterface
{
     <T> T head(final Class<T> p0);
    
     <T> T head(final T p0);
    
     <T> T options(final Class<T> p0);
    
     <T> T options(final T p0);
    
     <T> T get(final Class<T> p0);
    
     <T> T get(final T p0);
    
     <T> T put(final Class<T> p0);
    
     <T> T put(final T p0);
    
     <T> T put(final Class<T> p0, final Object p1);
    
     <T> T put(final T p0, final Object p1);
    
     <T> T post(final Class<T> p0);
    
     <T> T post(final T p0);
    
     <T> T post(final Class<T> p0, final Object p1);
    
     <T> T post(final T p0, final Object p1);
    
     <T> T delete(final Class<T> p0);
    
     <T> T delete(final T p0);
    
     <T> T delete(final Class<T> p0, final Object p1);
    
     <T> T delete(final T p0, final Object p1);
    
     <T> T method(final String p0, final Class<T> p1);
    
     <T> T method(final String p0, final T p1);
    
     <T> T method(final String p0, final Class<T> p1, final Object p2);
    
     <T> T method(final String p0, final T p1, final Object p2);
}
