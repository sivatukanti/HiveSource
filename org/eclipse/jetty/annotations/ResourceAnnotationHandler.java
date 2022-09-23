// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.plus.annotation.Injection;
import javax.naming.NameNotFoundException;
import javax.naming.InitialContext;
import org.eclipse.jetty.plus.annotation.InjectionCollection;
import java.lang.reflect.Modifier;
import javax.naming.NamingException;
import org.eclipse.jetty.plus.jndi.NamingEntryUtil;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.util.log.Logger;

public class ResourceAnnotationHandler extends AnnotationIntrospector.AbstractIntrospectableAnnotationHandler
{
    private static final Logger LOG;
    protected WebAppContext _context;
    
    public ResourceAnnotationHandler(final WebAppContext wac) {
        super(true);
        this._context = wac;
    }
    
    @Override
    public void doHandle(final Class clazz) {
        if (Util.isServletType(clazz)) {
            this.handleClass(clazz);
            final Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; ++i) {
                this.handleMethod(clazz, methods[i]);
            }
            final Field[] fields = clazz.getDeclaredFields();
            for (int j = 0; j < fields.length; ++j) {
                this.handleField(clazz, fields[j]);
            }
        }
    }
    
    public void handleClass(final Class clazz) {
        final Resource resource = clazz.getAnnotation(Resource.class);
        if (resource != null) {
            final String name = resource.name();
            final String mappedName = resource.mappedName();
            final Resource.AuthenticationType auth = resource.authenticationType();
            final Class type = resource.type();
            final boolean shareable = resource.shareable();
            if (name == null || name.trim().equals("")) {
                throw new IllegalStateException("Class level Resource annotations must contain a name (Common Annotations Spec Section 2.3)");
            }
            try {
                if (!NamingEntryUtil.bindToENC(this._context, name, mappedName) && !NamingEntryUtil.bindToENC(this._context.getServer(), name, mappedName)) {
                    throw new IllegalStateException("No resource at " + ((mappedName == null) ? name : mappedName));
                }
            }
            catch (NamingException e) {
                ResourceAnnotationHandler.LOG.warn(e);
            }
        }
    }
    
    public void handleField(final Class clazz, final Field field) {
        final Resource resource = field.getAnnotation(Resource.class);
        if (resource != null) {
            if (Modifier.isStatic(field.getModifiers())) {
                ResourceAnnotationHandler.LOG.warn("Skipping Resource annotation on " + clazz.getName() + "." + field.getName() + ": cannot be static", new Object[0]);
                return;
            }
            if (Modifier.isFinal(field.getModifiers())) {
                ResourceAnnotationHandler.LOG.warn("Skipping Resource annotation on " + clazz.getName() + "." + field.getName() + ": cannot be final", new Object[0]);
                return;
            }
            String name = clazz.getCanonicalName() + "/" + field.getName();
            name = ((resource.name() != null && !resource.name().trim().equals("")) ? resource.name() : name);
            final String mappedName = (resource.mappedName() != null && !resource.mappedName().trim().equals("")) ? resource.mappedName() : null;
            final Class type = field.getType();
            final MetaData metaData = this._context.getMetaData();
            if (metaData.getOriginDescriptor("resource-ref." + name + ".injection") != null) {
                return;
            }
            InjectionCollection injections = (InjectionCollection)this._context.getAttribute("org.eclipse.jetty.injectionCollection");
            if (injections == null) {
                injections = new InjectionCollection();
                this._context.setAttribute("org.eclipse.jetty.injectionCollection", injections);
            }
            Injection injection = injections.getInjection(name, clazz, field);
            if (injection == null) {
                try {
                    boolean bound = NamingEntryUtil.bindToENC(this._context, name, mappedName);
                    if (!bound) {
                        bound = NamingEntryUtil.bindToENC(this._context.getServer(), name, mappedName);
                    }
                    if (!bound) {
                        bound = NamingEntryUtil.bindToENC(null, name, mappedName);
                    }
                    if (!bound) {
                        try {
                            final InitialContext ic = new InitialContext();
                            final String nameInEnvironment = (mappedName != null) ? mappedName : name;
                            ic.lookup("java:comp/env/" + nameInEnvironment);
                            bound = true;
                        }
                        catch (NameNotFoundException e2) {
                            bound = false;
                        }
                    }
                    if (bound) {
                        ResourceAnnotationHandler.LOG.debug("Bound " + ((mappedName == null) ? name : mappedName) + " as " + name, new Object[0]);
                        injection = new Injection();
                        injection.setTarget(clazz, field, type);
                        injection.setJndiName(name);
                        injection.setMappingName(mappedName);
                        injections.add(injection);
                        metaData.setOrigin("resource-ref." + name + ".injection");
                    }
                    else if (!Util.isEnvEntryType(type)) {
                        throw new IllegalStateException("No resource at " + ((mappedName == null) ? name : mappedName));
                    }
                }
                catch (NamingException e) {
                    if (!Util.isEnvEntryType(type)) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
    }
    
    public void handleMethod(final Class clazz, final Method method) {
        final Resource resource = method.getAnnotation(Resource.class);
        if (resource != null) {
            if (Modifier.isStatic(method.getModifiers())) {
                ResourceAnnotationHandler.LOG.warn("Skipping Resource annotation on " + clazz.getName() + "." + method.getName() + ": cannot be static", new Object[0]);
                return;
            }
            if (!method.getName().startsWith("set")) {
                ResourceAnnotationHandler.LOG.warn("Skipping Resource annotation on " + clazz.getName() + "." + method.getName() + ": invalid java bean, does not start with 'set'", new Object[0]);
                return;
            }
            if (method.getParameterTypes().length != 1) {
                ResourceAnnotationHandler.LOG.warn("Skipping Resource annotation on " + clazz.getName() + "." + method.getName() + ": invalid java bean, not single argument to method", new Object[0]);
                return;
            }
            if (Void.TYPE != method.getReturnType()) {
                ResourceAnnotationHandler.LOG.warn("Skipping Resource annotation on " + clazz.getName() + "." + method.getName() + ": invalid java bean, not void", new Object[0]);
                return;
            }
            String name = method.getName().substring(3);
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
            name = clazz.getCanonicalName() + "/" + name;
            name = ((resource.name() != null && !resource.name().trim().equals("")) ? resource.name() : name);
            final String mappedName = (resource.mappedName() != null && !resource.mappedName().trim().equals("")) ? resource.mappedName() : null;
            final Class paramType = method.getParameterTypes()[0];
            final Class resourceType = resource.type();
            final MetaData metaData = this._context.getMetaData();
            if (metaData.getOriginDescriptor("resource-ref." + name + ".injection") != null) {
                return;
            }
            InjectionCollection injections = (InjectionCollection)this._context.getAttribute("org.eclipse.jetty.injectionCollection");
            if (injections == null) {
                injections = new InjectionCollection();
                this._context.setAttribute("org.eclipse.jetty.injectionCollection", injections);
            }
            Injection injection = injections.getInjection(name, clazz, method, paramType);
            if (injection == null) {
                try {
                    boolean bound = NamingEntryUtil.bindToENC(this._context, name, mappedName);
                    if (!bound) {
                        bound = NamingEntryUtil.bindToENC(this._context.getServer(), name, mappedName);
                    }
                    if (!bound) {
                        bound = NamingEntryUtil.bindToENC(null, name, mappedName);
                    }
                    if (!bound) {
                        try {
                            final InitialContext ic = new InitialContext();
                            final String nameInEnvironment = (mappedName != null) ? mappedName : name;
                            ic.lookup("java:comp/env/" + nameInEnvironment);
                            bound = true;
                        }
                        catch (NameNotFoundException e2) {
                            bound = false;
                        }
                    }
                    if (bound) {
                        ResourceAnnotationHandler.LOG.debug("Bound " + ((mappedName == null) ? name : mappedName) + " as " + name, new Object[0]);
                        injection = new Injection();
                        injection.setTarget(clazz, method, paramType, resourceType);
                        injection.setJndiName(name);
                        injection.setMappingName(mappedName);
                        injections.add(injection);
                        metaData.setOrigin("resource-ref." + name + ".injection");
                    }
                    else if (!Util.isEnvEntryType(paramType)) {
                        throw new IllegalStateException("No resource at " + ((mappedName == null) ? name : mappedName));
                    }
                }
                catch (NamingException e) {
                    if (!Util.isEnvEntryType(paramType)) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
    }
    
    static {
        LOG = Log.getLogger(ResourceAnnotationHandler.class);
    }
}
