// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.cglib.core.$AbstractClassGenerator;
import com.google.inject.internal.cglib.core.$GeneratorStrategy;
import com.google.inject.internal.cglib.core.$NamingPolicy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import com.google.inject.internal.cglib.core.$CodeGenerationException;
import com.google.inject.internal.cglib.reflect.$FastClass;
import com.google.inject.internal.cglib.core.$Signature;

public class $MethodProxy
{
    private $Signature sig1;
    private $Signature sig2;
    private CreateInfo createInfo;
    private final Object initLock;
    private volatile FastClassInfo fastClassInfo;
    
    public static $MethodProxy create(final Class c1, final Class c2, final String desc, final String name1, final String name2) {
        final $MethodProxy proxy = new $MethodProxy();
        proxy.sig1 = new $Signature(name1, desc);
        proxy.sig2 = new $Signature(name2, desc);
        proxy.createInfo = new CreateInfo(c1, c2);
        return proxy;
    }
    
    private void init() {
        if (this.fastClassInfo == null) {
            synchronized (this.initLock) {
                if (this.fastClassInfo == null) {
                    final CreateInfo ci = this.createInfo;
                    final FastClassInfo fci = new FastClassInfo();
                    fci.f1 = helper(ci, ci.c1);
                    fci.f2 = helper(ci, ci.c2);
                    fci.i1 = fci.f1.getIndex(this.sig1);
                    fci.i2 = fci.f2.getIndex(this.sig2);
                    this.fastClassInfo = fci;
                    this.createInfo = null;
                }
            }
        }
    }
    
    private static $FastClass helper(final CreateInfo ci, final Class type) {
        final $FastClass.Generator g = new $FastClass.Generator();
        g.setType(type);
        g.setClassLoader(ci.c2.getClassLoader());
        g.setNamingPolicy(ci.namingPolicy);
        g.setStrategy(ci.strategy);
        g.setAttemptLoad(ci.attemptLoad);
        return g.create();
    }
    
    private $MethodProxy() {
        this.initLock = new Object();
    }
    
    public $Signature getSignature() {
        return this.sig1;
    }
    
    public String getSuperName() {
        return this.sig2.getName();
    }
    
    public int getSuperIndex() {
        this.init();
        return this.fastClassInfo.i2;
    }
    
    $FastClass getFastClass() {
        this.init();
        return this.fastClassInfo.f1;
    }
    
    $FastClass getSuperFastClass() {
        this.init();
        return this.fastClassInfo.f2;
    }
    
    public static $MethodProxy find(final Class type, final $Signature sig) {
        try {
            final Method m = type.getDeclaredMethod("CGLIB$findMethodProxy", (Class[])$MethodInterceptorGenerator.FIND_PROXY_TYPES);
            return ($MethodProxy)m.invoke(null, sig);
        }
        catch (NoSuchMethodException e3) {
            throw new IllegalArgumentException("Class " + type + " does not use a MethodInterceptor");
        }
        catch (IllegalAccessException e) {
            throw new $CodeGenerationException(e);
        }
        catch (InvocationTargetException e2) {
            throw new $CodeGenerationException(e2);
        }
    }
    
    public Object invoke(final Object obj, final Object[] args) throws Throwable {
        try {
            this.init();
            final FastClassInfo fci = this.fastClassInfo;
            return fci.f1.invoke(fci.i1, obj, args);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        catch (IllegalArgumentException e2) {
            if (this.fastClassInfo.i1 < 0) {
                throw new IllegalArgumentException("Protected method: " + this.sig1);
            }
            throw e2;
        }
    }
    
    public Object invokeSuper(final Object obj, final Object[] args) throws Throwable {
        try {
            this.init();
            final FastClassInfo fci = this.fastClassInfo;
            return fci.f2.invoke(fci.i2, obj, args);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
    
    private static class FastClassInfo
    {
        $FastClass f1;
        $FastClass f2;
        int i1;
        int i2;
    }
    
    private static class CreateInfo
    {
        Class c1;
        Class c2;
        $NamingPolicy namingPolicy;
        $GeneratorStrategy strategy;
        boolean attemptLoad;
        
        public CreateInfo(final Class c1, final Class c2) {
            this.c1 = c1;
            this.c2 = c2;
            final $AbstractClassGenerator fromEnhancer = $AbstractClassGenerator.getCurrent();
            if (fromEnhancer != null) {
                this.namingPolicy = fromEnhancer.getNamingPolicy();
                this.strategy = fromEnhancer.getStrategy();
                this.attemptLoad = fromEnhancer.getAttemptLoad();
            }
        }
    }
}
