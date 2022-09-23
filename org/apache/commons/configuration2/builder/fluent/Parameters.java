// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.fluent;

import java.lang.reflect.Method;
import org.apache.commons.configuration2.builder.BuilderParameters;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.apache.commons.configuration2.builder.DatabaseBuilderParametersImpl;
import org.apache.commons.configuration2.builder.combined.MultiFileBuilderParametersImpl;
import org.apache.commons.configuration2.builder.PropertiesBuilderParametersImpl;
import org.apache.commons.configuration2.builder.XMLBuilderParametersImpl;
import org.apache.commons.configuration2.builder.HierarchicalBuilderParametersImpl;
import org.apache.commons.configuration2.builder.JndiBuilderParametersImpl;
import org.apache.commons.configuration2.builder.combined.CombinedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.DefaultParametersHandler;
import org.apache.commons.configuration2.builder.DefaultParametersManager;

public final class Parameters
{
    private final DefaultParametersManager defaultParametersManager;
    
    public Parameters() {
        this(null);
    }
    
    public Parameters(final DefaultParametersManager manager) {
        this.defaultParametersManager = ((manager != null) ? manager : new DefaultParametersManager());
    }
    
    public DefaultParametersManager getDefaultParametersManager() {
        return this.defaultParametersManager;
    }
    
    public <T> void registerDefaultsHandler(final Class<T> paramsClass, final DefaultParametersHandler<? super T> handler) {
        this.getDefaultParametersManager().registerDefaultsHandler(paramsClass, handler);
    }
    
    public <T> void registerDefaultsHandler(final Class<T> paramsClass, final DefaultParametersHandler<? super T> handler, final Class<?> startClass) {
        this.getDefaultParametersManager().registerDefaultsHandler(paramsClass, handler, startClass);
    }
    
    public BasicBuilderParameters basic() {
        return new BasicBuilderParameters();
    }
    
    public FileBasedBuilderParameters fileBased() {
        return this.createParametersProxy(new FileBasedBuilderParametersImpl(), FileBasedBuilderParameters.class, (Class<?>[])new Class[0]);
    }
    
    public CombinedBuilderParameters combined() {
        return this.createParametersProxy(new CombinedBuilderParametersImpl(), CombinedBuilderParameters.class, (Class<?>[])new Class[0]);
    }
    
    public JndiBuilderParameters jndi() {
        return this.createParametersProxy(new JndiBuilderParametersImpl(), JndiBuilderParameters.class, (Class<?>[])new Class[0]);
    }
    
    public HierarchicalBuilderParameters hierarchical() {
        return this.createParametersProxy(new HierarchicalBuilderParametersImpl(), HierarchicalBuilderParameters.class, FileBasedBuilderParameters.class);
    }
    
    public XMLBuilderParameters xml() {
        return this.createParametersProxy(new XMLBuilderParametersImpl(), XMLBuilderParameters.class, FileBasedBuilderParameters.class, HierarchicalBuilderParameters.class);
    }
    
    public PropertiesBuilderParameters properties() {
        return this.createParametersProxy(new PropertiesBuilderParametersImpl(), PropertiesBuilderParameters.class, FileBasedBuilderParameters.class);
    }
    
    public MultiFileBuilderParameters multiFile() {
        return this.createParametersProxy(new MultiFileBuilderParametersImpl(), MultiFileBuilderParameters.class, (Class<?>[])new Class[0]);
    }
    
    public DatabaseBuilderParameters database() {
        return this.createParametersProxy(new DatabaseBuilderParametersImpl(), DatabaseBuilderParameters.class, (Class<?>[])new Class[0]);
    }
    
    private <T> T createParametersProxy(final Object target, final Class<T> ifcClass, final Class<?>... superIfcs) {
        final Class<?>[] ifcClasses = (Class<?>[])new Class[1 + superIfcs.length];
        ifcClasses[0] = ifcClass;
        System.arraycopy(superIfcs, 0, ifcClasses, 1, superIfcs.length);
        final Object obj = Proxy.newProxyInstance(Parameters.class.getClassLoader(), ifcClasses, new ParametersIfcInvocationHandler(target));
        this.getDefaultParametersManager().initializeParameters((BuilderParameters)obj);
        return ifcClass.cast(obj);
    }
    
    private static class ParametersIfcInvocationHandler implements InvocationHandler
    {
        private final Object target;
        
        public ParametersIfcInvocationHandler(final Object targetObj) {
            this.target = targetObj;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final Object result = method.invoke(this.target, args);
            return isFluentResult(method) ? proxy : result;
        }
        
        private static boolean isFluentResult(final Method method) {
            final Class<?> declaringClass = method.getDeclaringClass();
            return declaringClass.isInterface() && !declaringClass.equals(BuilderParameters.class);
        }
    }
}
