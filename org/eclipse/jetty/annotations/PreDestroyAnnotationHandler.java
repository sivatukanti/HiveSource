// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import org.eclipse.jetty.webapp.MetaData;
import java.lang.reflect.Method;
import org.eclipse.jetty.plus.annotation.LifeCycleCallback;
import org.eclipse.jetty.plus.annotation.LifeCycleCallbackCollection;
import org.eclipse.jetty.plus.annotation.PreDestroyCallback;
import org.eclipse.jetty.webapp.Origin;
import java.lang.reflect.Modifier;
import java.lang.annotation.Annotation;
import javax.annotation.PreDestroy;
import org.eclipse.jetty.webapp.WebAppContext;

public class PreDestroyAnnotationHandler extends AnnotationIntrospector.AbstractIntrospectableAnnotationHandler
{
    WebAppContext _context;
    
    public PreDestroyAnnotationHandler(final WebAppContext wac) {
        super(true);
        this._context = wac;
    }
    
    @Override
    public void doHandle(final Class clazz) {
        if (Util.isServletType(clazz)) {
            final Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; ++i) {
                final Method m = methods[i];
                if (m.isAnnotationPresent(PreDestroy.class)) {
                    if (m.getParameterTypes().length != 0) {
                        throw new IllegalStateException(m + " has parameters");
                    }
                    if (m.getReturnType() != Void.TYPE) {
                        throw new IllegalStateException(m + " is not void");
                    }
                    if (m.getExceptionTypes().length != 0) {
                        throw new IllegalStateException(m + " throws checked exceptions");
                    }
                    if (Modifier.isStatic(m.getModifiers())) {
                        throw new IllegalStateException(m + " is static");
                    }
                    final MetaData metaData = this._context.getMetaData();
                    final Origin origin = metaData.getOrigin("pre-destroy");
                    if (origin != null && (origin == Origin.WebXml || origin == Origin.WebDefaults || origin == Origin.WebOverride)) {
                        return;
                    }
                    final PreDestroyCallback callback = new PreDestroyCallback();
                    callback.setTarget(clazz.getName(), m.getName());
                    LifeCycleCallbackCollection lifecycles = (LifeCycleCallbackCollection)this._context.getAttribute("org.eclipse.jetty.lifecyleCallbackCollection");
                    if (lifecycles == null) {
                        lifecycles = new LifeCycleCallbackCollection();
                        this._context.setAttribute("org.eclipse.jetty.lifecyleCallbackCollection", lifecycles);
                    }
                    lifecycles.add(callback);
                }
            }
        }
    }
}
