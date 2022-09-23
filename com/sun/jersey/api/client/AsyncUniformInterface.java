// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import com.sun.jersey.api.client.async.ITypeListener;
import java.util.concurrent.Future;

public interface AsyncUniformInterface
{
    Future<ClientResponse> head();
    
    Future<ClientResponse> head(final ITypeListener<ClientResponse> p0);
    
     <T> Future<T> options(final Class<T> p0);
    
     <T> Future<T> options(final GenericType<T> p0);
    
     <T> Future<T> options(final ITypeListener<T> p0);
    
     <T> Future<T> get(final Class<T> p0) throws UniformInterfaceException;
    
     <T> Future<T> get(final GenericType<T> p0) throws UniformInterfaceException;
    
     <T> Future<T> get(final ITypeListener<T> p0);
    
    Future<?> put();
    
    Future<?> put(final Object p0);
    
     <T> Future<T> put(final Class<T> p0);
    
     <T> Future<T> put(final GenericType<T> p0);
    
     <T> Future<T> put(final ITypeListener<T> p0);
    
     <T> Future<T> put(final Class<T> p0, final Object p1);
    
     <T> Future<T> put(final GenericType<T> p0, final Object p1);
    
     <T> Future<T> put(final ITypeListener<T> p0, final Object p1);
    
    Future<?> post();
    
    Future<?> post(final Object p0);
    
     <T> Future<T> post(final Class<T> p0);
    
     <T> Future<T> post(final GenericType<T> p0);
    
     <T> Future<T> post(final ITypeListener<T> p0);
    
     <T> Future<T> post(final Class<T> p0, final Object p1);
    
     <T> Future<T> post(final GenericType<T> p0, final Object p1);
    
     <T> Future<T> post(final ITypeListener<T> p0, final Object p1);
    
    Future<?> delete();
    
    Future<?> delete(final Object p0);
    
     <T> Future<T> delete(final Class<T> p0);
    
     <T> Future<T> delete(final GenericType<T> p0);
    
     <T> Future<T> delete(final ITypeListener<T> p0);
    
     <T> Future<T> delete(final Class<T> p0, final Object p1);
    
     <T> Future<T> delete(final GenericType<T> p0, final Object p1);
    
     <T> Future<T> delete(final ITypeListener<T> p0, final Object p1);
    
    Future<?> method(final String p0);
    
    Future<?> method(final String p0, final Object p1);
    
     <T> Future<T> method(final String p0, final Class<T> p1);
    
     <T> Future<T> method(final String p0, final GenericType<T> p1);
    
     <T> Future<T> method(final String p0, final ITypeListener<T> p1);
    
     <T> Future<T> method(final String p0, final Class<T> p1, final Object p2);
    
     <T> Future<T> method(final String p0, final GenericType<T> p1, final Object p2);
    
     <T> Future<T> method(final String p0, final ITypeListener<T> p1, final Object p2);
}
