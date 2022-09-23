// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

public interface UniformInterface
{
    ClientResponse head();
    
     <T> T options(final Class<T> p0) throws UniformInterfaceException;
    
     <T> T options(final GenericType<T> p0) throws UniformInterfaceException;
    
     <T> T get(final Class<T> p0) throws UniformInterfaceException;
    
     <T> T get(final GenericType<T> p0) throws UniformInterfaceException;
    
    void put() throws UniformInterfaceException;
    
    void put(final Object p0) throws UniformInterfaceException;
    
     <T> T put(final Class<T> p0) throws UniformInterfaceException;
    
     <T> T put(final GenericType<T> p0) throws UniformInterfaceException;
    
     <T> T put(final Class<T> p0, final Object p1) throws UniformInterfaceException;
    
     <T> T put(final GenericType<T> p0, final Object p1) throws UniformInterfaceException;
    
    void post() throws UniformInterfaceException;
    
    void post(final Object p0) throws UniformInterfaceException;
    
     <T> T post(final Class<T> p0) throws UniformInterfaceException;
    
     <T> T post(final GenericType<T> p0) throws UniformInterfaceException;
    
     <T> T post(final Class<T> p0, final Object p1) throws UniformInterfaceException;
    
     <T> T post(final GenericType<T> p0, final Object p1) throws UniformInterfaceException;
    
    void delete() throws UniformInterfaceException;
    
    void delete(final Object p0) throws UniformInterfaceException;
    
     <T> T delete(final Class<T> p0) throws UniformInterfaceException;
    
     <T> T delete(final GenericType<T> p0) throws UniformInterfaceException;
    
     <T> T delete(final Class<T> p0, final Object p1) throws UniformInterfaceException;
    
     <T> T delete(final GenericType<T> p0, final Object p1) throws UniformInterfaceException;
    
    void method(final String p0) throws UniformInterfaceException;
    
    void method(final String p0, final Object p1) throws UniformInterfaceException;
    
     <T> T method(final String p0, final Class<T> p1) throws UniformInterfaceException;
    
     <T> T method(final String p0, final GenericType<T> p1) throws UniformInterfaceException;
    
     <T> T method(final String p0, final Class<T> p1, final Object p2) throws UniformInterfaceException;
    
     <T> T method(final String p0, final GenericType<T> p1, final Object p2) throws UniformInterfaceException;
}
