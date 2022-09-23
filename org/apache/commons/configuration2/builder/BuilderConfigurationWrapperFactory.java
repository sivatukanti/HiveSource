// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.ConfigurationUtils;
import java.lang.reflect.Method;
import org.apache.commons.configuration2.event.EventSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.apache.commons.configuration2.ImmutableConfiguration;

public class BuilderConfigurationWrapperFactory
{
    private final EventSourceSupport eventSourceSupport;
    
    public BuilderConfigurationWrapperFactory(final EventSourceSupport evSrcSupport) {
        this.eventSourceSupport = evSrcSupport;
    }
    
    public BuilderConfigurationWrapperFactory() {
        this(EventSourceSupport.NONE);
    }
    
    public <T extends ImmutableConfiguration> T createBuilderConfigurationWrapper(final Class<T> ifcClass, final ConfigurationBuilder<? extends T> builder) {
        return createBuilderConfigurationWrapper(ifcClass, builder, this.getEventSourceSupport());
    }
    
    public EventSourceSupport getEventSourceSupport() {
        return this.eventSourceSupport;
    }
    
    public static <T extends ImmutableConfiguration> T createBuilderConfigurationWrapper(final Class<T> ifcClass, final ConfigurationBuilder<? extends T> builder, final EventSourceSupport evSrcSupport) {
        if (ifcClass == null) {
            throw new IllegalArgumentException("Interface class must not be null!");
        }
        if (builder == null) {
            throw new IllegalArgumentException("Builder must not be null!");
        }
        return ifcClass.cast(Proxy.newProxyInstance(BuilderConfigurationWrapperFactory.class.getClassLoader(), fetchSupportedInterfaces(ifcClass, evSrcSupport), new BuilderConfigurationWrapperInvocationHandler(builder, evSrcSupport)));
    }
    
    private static Class<?>[] fetchSupportedInterfaces(final Class<?> ifcClass, final EventSourceSupport evSrcSupport) {
        if (EventSourceSupport.NONE == evSrcSupport) {
            return (Class<?>[])new Class[] { ifcClass };
        }
        final Class<?>[] result = (Class<?>[])new Class[] { EventSource.class, ifcClass };
        return result;
    }
    
    public enum EventSourceSupport
    {
        NONE, 
        DUMMY, 
        BUILDER;
    }
    
    private static class BuilderConfigurationWrapperInvocationHandler implements InvocationHandler
    {
        private final ConfigurationBuilder<? extends ImmutableConfiguration> builder;
        private final EventSourceSupport eventSourceSupport;
        
        public BuilderConfigurationWrapperInvocationHandler(final ConfigurationBuilder<? extends ImmutableConfiguration> wrappedBuilder, final EventSourceSupport evSrcSupport) {
            this.builder = wrappedBuilder;
            this.eventSourceSupport = evSrcSupport;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (EventSource.class.equals(method.getDeclaringClass())) {
                return this.handleEventSourceInvocation(method, args);
            }
            return this.handleConfigurationInvocation(method, args);
        }
        
        private Object handleConfigurationInvocation(final Method method, final Object[] args) throws Exception {
            return method.invoke(this.builder.getConfiguration(), args);
        }
        
        private Object handleEventSourceInvocation(final Method method, final Object[] args) throws Exception {
            final Object target = (EventSourceSupport.DUMMY == this.eventSourceSupport) ? ConfigurationUtils.asEventSource(this, true) : this.builder;
            return method.invoke(target, args);
        }
    }
}
