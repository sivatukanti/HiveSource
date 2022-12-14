// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.proxy;

import java.lang.reflect.Method;
import net.sf.cglib.core.CodeGenerationException;
import java.io.Serializable;

public class Proxy implements Serializable
{
    protected InvocationHandler h;
    private static final CallbackFilter BAD_OBJECT_METHOD_FILTER;
    
    protected Proxy(final InvocationHandler h) {
        Enhancer.registerCallbacks(this.getClass(), new Callback[] { h, null });
        this.h = h;
    }
    
    public static InvocationHandler getInvocationHandler(final Object proxy) {
        if (!(proxy instanceof ProxyImpl)) {
            throw new IllegalArgumentException("Object is not a proxy");
        }
        return ((Proxy)proxy).h;
    }
    
    public static Class getProxyClass(final ClassLoader loader, final Class[] interfaces) {
        final Enhancer e = new Enhancer();
        e.setSuperclass(ProxyImpl.class);
        e.setInterfaces(interfaces);
        e.setCallbackTypes(new Class[] { InvocationHandler.class, NoOp.class });
        e.setCallbackFilter(Proxy.BAD_OBJECT_METHOD_FILTER);
        e.setUseFactory(false);
        return e.createClass();
    }
    
    public static boolean isProxyClass(final Class cl) {
        return cl.getSuperclass().equals(ProxyImpl.class);
    }
    
    public static Object newProxyInstance(final ClassLoader loader, final Class[] interfaces, final InvocationHandler h) {
        try {
            final Class clazz = getProxyClass(loader, interfaces);
            return clazz.getConstructor(InvocationHandler.class).newInstance(h);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new CodeGenerationException(e2);
        }
    }
    
    static {
        BAD_OBJECT_METHOD_FILTER = new CallbackFilter() {
            public int accept(final Method method) {
                if (method.getDeclaringClass().getName().equals("java.lang.Object")) {
                    final String name = method.getName();
                    if (!name.equals("hashCode") && !name.equals("equals") && !name.equals("toString")) {
                        return 1;
                    }
                }
                return 0;
            }
        };
    }
    
    private static class ProxyImpl extends Proxy
    {
        protected ProxyImpl(final InvocationHandler h) {
            super(h);
        }
    }
}
