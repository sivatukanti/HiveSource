// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.modelapi.annotation;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import javax.ws.rs.DefaultValue;
import java.util.Collections;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Context;
import java.util.WeakHashMap;
import java.lang.reflect.Type;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.api.model.AbstractSetterMethod;
import javax.ws.rs.HttpMethod;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.security.AccessController;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.api.model.Parameter;
import java.lang.reflect.Field;
import com.sun.jersey.api.model.AbstractField;
import com.sun.jersey.api.model.Parameterized;
import com.sun.jersey.api.model.AbstractResourceConstructor;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import com.sun.jersey.core.header.MediaTypes;
import java.lang.annotation.Annotation;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.core.reflection.AnnotatedMethod;
import com.sun.jersey.impl.ImplMessages;
import java.util.logging.Level;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import com.sun.jersey.core.reflection.MethodList;
import java.lang.reflect.Constructor;
import com.sun.jersey.api.model.PathValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.Path;
import com.sun.jersey.api.model.AbstractResource;
import java.util.Map;
import java.util.logging.Logger;

public class IntrospectionModeller
{
    private static final Logger LOGGER;
    private static final Map<Class, ParamAnnotationHelper> ANOT_HELPER_MAP;
    
    public static AbstractResource createResource(final Class<?> resourceClass) {
        final Class<?> annotatedResourceClass = (Class<?>)getAnnotatedResourceClass(resourceClass);
        final Path rPathAnnotation = annotatedResourceClass.getAnnotation(Path.class);
        final boolean isRootResourceClass = null != rPathAnnotation;
        final boolean isEncodedAnotOnClass = null != annotatedResourceClass.getAnnotation(Encoded.class);
        AbstractResource resource;
        if (isRootResourceClass) {
            resource = new AbstractResource(resourceClass, new PathValue(rPathAnnotation.value()));
        }
        else {
            resource = new AbstractResource(resourceClass);
        }
        workOutConstructorsList(resource, resourceClass.getConstructors(), isEncodedAnotOnClass);
        workOutFieldsList(resource, isEncodedAnotOnClass);
        final MethodList methodList = new MethodList(resourceClass);
        workOutSetterMethodsList(resource, methodList, isEncodedAnotOnClass);
        final Consumes classScopeConsumesAnnotation = annotatedResourceClass.getAnnotation(Consumes.class);
        final Produces classScopeProducesAnnotation = annotatedResourceClass.getAnnotation(Produces.class);
        workOutResourceMethodsList(resource, methodList, isEncodedAnotOnClass, classScopeConsumesAnnotation, classScopeProducesAnnotation);
        workOutSubResourceMethodsList(resource, methodList, isEncodedAnotOnClass, classScopeConsumesAnnotation, classScopeProducesAnnotation);
        workOutSubResourceLocatorsList(resource, methodList, isEncodedAnotOnClass);
        workOutPostConstructPreDestroy(resource);
        if (IntrospectionModeller.LOGGER.isLoggable(Level.FINEST)) {
            IntrospectionModeller.LOGGER.finest(ImplMessages.NEW_AR_CREATED_BY_INTROSPECTION_MODELER(resource.toString()));
        }
        return resource;
    }
    
    private static Class getAnnotatedResourceClass(final Class rc) {
        if (rc.isAnnotationPresent(Path.class)) {
            return rc;
        }
        for (final Class i : rc.getInterfaces()) {
            if (i.isAnnotationPresent(Path.class)) {
                return i;
            }
        }
        return rc;
    }
    
    private static void addConsumes(final AnnotatedMethod am, final AbstractResourceMethod resourceMethod, Consumes consumeMimeAnnotation) {
        if (am.isAnnotationPresent(Consumes.class)) {
            consumeMimeAnnotation = am.getAnnotation(Consumes.class);
        }
        resourceMethod.setAreInputTypesDeclared(consumeMimeAnnotation != null);
        resourceMethod.getSupportedInputTypes().addAll(MediaTypes.createMediaTypes(consumeMimeAnnotation));
    }
    
    private static void addProduces(final AnnotatedMethod am, final AbstractResourceMethod resourceMethod, Produces produceMimeAnnotation) {
        if (am.isAnnotationPresent(Produces.class)) {
            produceMimeAnnotation = am.getAnnotation(Produces.class);
        }
        resourceMethod.setAreOutputTypesDeclared(produceMimeAnnotation != null);
        resourceMethod.getSupportedOutputTypes().addAll(MediaTypes.createQualitySourceMediaTypes(produceMimeAnnotation));
    }
    
    private static void workOutConstructorsList(final AbstractResource resource, final Constructor[] ctorArray, final boolean isEncoded) {
        if (null != ctorArray) {
            for (final Constructor ctor : ctorArray) {
                final AbstractResourceConstructor aCtor = new AbstractResourceConstructor(ctor);
                processParameters(resource.getResourceClass(), ctor.getDeclaringClass(), aCtor, ctor, isEncoded);
                resource.getConstructors().add(aCtor);
            }
        }
    }
    
    private static void workOutFieldsList(final AbstractResource resource, final boolean isEncoded) {
        Class c = resource.getResourceClass();
        if (c.isInterface()) {
            return;
        }
        while (c != Object.class) {
            for (final Field f : c.getDeclaredFields()) {
                if (f.getDeclaredAnnotations().length > 0) {
                    final AbstractField af = new AbstractField(f);
                    final Parameter p = createParameter(resource.getResourceClass(), f.getDeclaringClass(), isEncoded, f.getType(), f.getGenericType(), f.getAnnotations());
                    if (null != p) {
                        af.getParameters().add(p);
                        resource.getFields().add(af);
                    }
                }
            }
            c = c.getSuperclass();
        }
    }
    
    private static void workOutPostConstructPreDestroy(final AbstractResource resource) {
        final Class postConstruct = AccessController.doPrivileged(ReflectionHelper.classForNamePA("javax.annotation.PostConstruct"));
        if (postConstruct == null) {
            return;
        }
        final Class preDestroy = AccessController.doPrivileged(ReflectionHelper.classForNamePA("javax.annotation.PreDestroy"));
        final MethodList methodList = new MethodList(resource.getResourceClass(), true);
        HashSet<String> names = new HashSet<String>();
        for (final AnnotatedMethod m : methodList.hasAnnotation((Class<Annotation>)postConstruct).hasNumParams(0).hasReturnType(Void.TYPE)) {
            final Method method = m.getMethod();
            if (names.add(method.getName())) {
                AccessController.doPrivileged((PrivilegedAction<Object>)ReflectionHelper.setAccessibleMethodPA(method));
                resource.getPostConstructMethods().add(0, method);
            }
        }
        names = new HashSet<String>();
        for (final AnnotatedMethod m : methodList.hasAnnotation((Class<Annotation>)preDestroy).hasNumParams(0).hasReturnType(Void.TYPE)) {
            final Method method = m.getMethod();
            if (names.add(method.getName())) {
                AccessController.doPrivileged((PrivilegedAction<Object>)ReflectionHelper.setAccessibleMethodPA(method));
                resource.getPreDestroyMethods().add(method);
            }
        }
    }
    
    private static void workOutSetterMethodsList(final AbstractResource resource, final MethodList methodList, final boolean isEncoded) {
        for (final AnnotatedMethod m : methodList.hasNotMetaAnnotation(HttpMethod.class).hasNotAnnotation(Path.class).hasNumParams(1).hasReturnType(Void.TYPE).nameStartsWith("set")) {
            final AbstractSetterMethod asm = new AbstractSetterMethod(resource, m.getMethod(), m.getAnnotations());
            final Parameter p = createParameter(resource.getResourceClass(), m.getMethod().getDeclaringClass(), isEncoded, m.getParameterTypes()[0], m.getGenericParameterTypes()[0], m.getAnnotations());
            if (null != p) {
                asm.getParameters().add(p);
                resource.getSetterMethods().add(asm);
            }
        }
    }
    
    private static void workOutResourceMethodsList(final AbstractResource resource, final MethodList methodList, final boolean isEncoded, final Consumes classScopeConsumesAnnotation, final Produces classScopeProducesAnnotation) {
        for (final AnnotatedMethod m : methodList.hasMetaAnnotation(HttpMethod.class).hasNotAnnotation(Path.class)) {
            final ReflectionHelper.ClassTypePair ct = getGenericReturnType(resource.getResourceClass(), m.getMethod());
            final AbstractResourceMethod resourceMethod = new AbstractResourceMethod(resource, m.getMethod(), ct.c, ct.t, m.getMetaMethodAnnotations(HttpMethod.class).get(0).value(), m.getAnnotations());
            addConsumes(m, resourceMethod, classScopeConsumesAnnotation);
            addProduces(m, resourceMethod, classScopeProducesAnnotation);
            processParameters(resourceMethod.getResource().getResourceClass(), resourceMethod.getMethod().getDeclaringClass(), resourceMethod, m, isEncoded);
            resource.getResourceMethods().add(resourceMethod);
        }
    }
    
    private static ReflectionHelper.ClassTypePair getGenericReturnType(final Class concreteClass, final Method m) {
        return getGenericType(concreteClass, m.getDeclaringClass(), m.getReturnType(), m.getGenericReturnType());
    }
    
    private static void workOutSubResourceMethodsList(final AbstractResource resource, final MethodList methodList, final boolean isEncoded, final Consumes classScopeConsumesAnnotation, final Produces classScopeProducesAnnotation) {
        for (final AnnotatedMethod m : methodList.hasMetaAnnotation(HttpMethod.class).hasAnnotation(Path.class)) {
            final Path mPathAnnotation = m.getAnnotation(Path.class);
            final PathValue pv = new PathValue(mPathAnnotation.value());
            final boolean emptySegmentCase = "/".equals(pv.getValue()) || "".equals(pv.getValue());
            if (!emptySegmentCase) {
                final ReflectionHelper.ClassTypePair ct = getGenericReturnType(resource.getResourceClass(), m.getMethod());
                final AbstractSubResourceMethod abstractSubResourceMethod = new AbstractSubResourceMethod(resource, m.getMethod(), ct.c, ct.t, pv, m.getMetaMethodAnnotations(HttpMethod.class).get(0).value(), m.getAnnotations());
                addConsumes(m, abstractSubResourceMethod, classScopeConsumesAnnotation);
                addProduces(m, abstractSubResourceMethod, classScopeProducesAnnotation);
                processParameters(abstractSubResourceMethod.getResource().getResourceClass(), abstractSubResourceMethod.getMethod().getDeclaringClass(), abstractSubResourceMethod, m, isEncoded);
                resource.getSubResourceMethods().add(abstractSubResourceMethod);
            }
            else {
                final ReflectionHelper.ClassTypePair ct = getGenericReturnType(resource.getResourceClass(), m.getMethod());
                final AbstractResourceMethod abstractResourceMethod = new AbstractResourceMethod(resource, m.getMethod(), ct.c, ct.t, m.getMetaMethodAnnotations(HttpMethod.class).get(0).value(), m.getAnnotations());
                addConsumes(m, abstractResourceMethod, classScopeConsumesAnnotation);
                addProduces(m, abstractResourceMethod, classScopeProducesAnnotation);
                processParameters(abstractResourceMethod.getResource().getResourceClass(), abstractResourceMethod.getMethod().getDeclaringClass(), abstractResourceMethod, m, isEncoded);
                resource.getResourceMethods().add(abstractResourceMethod);
            }
        }
    }
    
    private static void workOutSubResourceLocatorsList(final AbstractResource resource, final MethodList methodList, final boolean isEncoded) {
        for (final AnnotatedMethod m : methodList.hasNotMetaAnnotation(HttpMethod.class).hasAnnotation(Path.class)) {
            final Path mPathAnnotation = m.getAnnotation(Path.class);
            final AbstractSubResourceLocator subResourceLocator = new AbstractSubResourceLocator(resource, m.getMethod(), new PathValue(mPathAnnotation.value()), m.getAnnotations());
            processParameters(subResourceLocator.getResource().getResourceClass(), subResourceLocator.getMethod().getDeclaringClass(), subResourceLocator, m, isEncoded);
            resource.getSubResourceLocators().add(subResourceLocator);
        }
    }
    
    private static void processParameters(final Class concreteClass, final Class declaringClass, final Parameterized parametrized, final Constructor ctor, final boolean isEncoded) {
        final Class[] parameterTypes = ctor.getParameterTypes();
        Type[] genericParameterTypes = ctor.getGenericParameterTypes();
        if (parameterTypes.length != genericParameterTypes.length) {
            final Type[] _genericParameterTypes = new Type[parameterTypes.length];
            _genericParameterTypes[0] = parameterTypes[0];
            System.arraycopy(genericParameterTypes, 0, _genericParameterTypes, 1, genericParameterTypes.length);
            genericParameterTypes = _genericParameterTypes;
        }
        processParameters(concreteClass, declaringClass, parametrized, null != ctor.getAnnotation(Encoded.class) || isEncoded, parameterTypes, genericParameterTypes, ctor.getParameterAnnotations());
    }
    
    private static void processParameters(final Class concreteClass, final Class declaringClass, final Parameterized parametrized, final AnnotatedMethod method, final boolean isEncoded) {
        processParameters(concreteClass, declaringClass, parametrized, null != method.getAnnotation(Encoded.class) || isEncoded, method.getParameterTypes(), method.getGenericParameterTypes(), method.getParameterAnnotations());
    }
    
    private static void processParameters(final Class concreteClass, final Class declaringClass, final Parameterized parametrized, final boolean isEncoded, final Class[] parameterTypes, final Type[] genericParameterTypes, final Annotation[][] parameterAnnotations) {
        for (int i = 0; i < parameterTypes.length; ++i) {
            final Parameter parameter = createParameter(concreteClass, declaringClass, isEncoded, parameterTypes[i], genericParameterTypes[i], parameterAnnotations[i]);
            if (null == parameter) {
                parametrized.getParameters().removeAll(parametrized.getParameters());
                break;
            }
            parametrized.getParameters().add(parameter);
        }
    }
    
    private static Map<Class, ParamAnnotationHelper> createParamAnotHelperMap() {
        final Map<Class, ParamAnnotationHelper> m = new WeakHashMap<Class, ParamAnnotationHelper>();
        m.put(Context.class, new ParamAnnotationHelper<Context>() {
            @Override
            public String getValueOf(final Context a) {
                return null;
            }
            
            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.CONTEXT;
            }
        });
        m.put(HeaderParam.class, new ParamAnnotationHelper<HeaderParam>() {
            @Override
            public String getValueOf(final HeaderParam a) {
                return a.value();
            }
            
            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.HEADER;
            }
        });
        m.put(CookieParam.class, new ParamAnnotationHelper<CookieParam>() {
            @Override
            public String getValueOf(final CookieParam a) {
                return a.value();
            }
            
            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.COOKIE;
            }
        });
        m.put(MatrixParam.class, new ParamAnnotationHelper<MatrixParam>() {
            @Override
            public String getValueOf(final MatrixParam a) {
                return a.value();
            }
            
            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.MATRIX;
            }
        });
        m.put(QueryParam.class, new ParamAnnotationHelper<QueryParam>() {
            @Override
            public String getValueOf(final QueryParam a) {
                return a.value();
            }
            
            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.QUERY;
            }
        });
        m.put(PathParam.class, new ParamAnnotationHelper<PathParam>() {
            @Override
            public String getValueOf(final PathParam a) {
                return a.value();
            }
            
            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.PATH;
            }
        });
        m.put(FormParam.class, new ParamAnnotationHelper<FormParam>() {
            @Override
            public String getValueOf(final FormParam a) {
                return a.value();
            }
            
            @Override
            public Parameter.Source getSource() {
                return Parameter.Source.FORM;
            }
        });
        return (Map<Class, ParamAnnotationHelper>)Collections.unmodifiableMap((Map<? extends Class, ? extends ParamAnnotationHelper>)m);
    }
    
    private static Parameter createParameter(final Class concreteClass, final Class declaringClass, final boolean isEncoded, Class<?> paramClass, Type paramType, final Annotation[] annotations) {
        if (null == annotations) {
            return null;
        }
        Annotation paramAnnotation = null;
        Parameter.Source paramSource = null;
        String paramName = null;
        boolean paramEncoded = isEncoded;
        String paramDefault = null;
        for (final Annotation annotation : annotations) {
            if (IntrospectionModeller.ANOT_HELPER_MAP.containsKey(annotation.annotationType())) {
                final ParamAnnotationHelper helper = IntrospectionModeller.ANOT_HELPER_MAP.get(annotation.annotationType());
                paramAnnotation = annotation;
                paramSource = helper.getSource();
                paramName = helper.getValueOf(annotation);
            }
            else if (Encoded.class == annotation.annotationType()) {
                paramEncoded = true;
            }
            else if (DefaultValue.class == annotation.annotationType()) {
                paramDefault = ((DefaultValue)annotation).value();
            }
            else if (paramAnnotation == null) {
                paramAnnotation = annotation;
                paramSource = Parameter.Source.UNKNOWN;
                paramName = getValue(annotation);
            }
        }
        if (paramAnnotation == null) {
            paramSource = Parameter.Source.ENTITY;
        }
        final ReflectionHelper.ClassTypePair ct = getGenericType(concreteClass, declaringClass, paramClass, paramType);
        paramType = ct.t;
        paramClass = (Class<?>)ct.c;
        return new Parameter(annotations, paramAnnotation, paramSource, paramName, paramType, paramClass, paramEncoded, paramDefault);
    }
    
    private static String getValue(final Annotation a) {
        try {
            final Method m = a.annotationType().getMethod("value", (Class<?>[])new Class[0]);
            if (m.getReturnType() != String.class) {
                return null;
            }
            return (String)m.invoke(a, new Object[0]);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    private static ReflectionHelper.ClassTypePair getGenericType(final Class concreteClass, final Class declaringClass, final Class c, final Type t) {
        if (t instanceof TypeVariable) {
            final ReflectionHelper.ClassTypePair ct = ReflectionHelper.resolveTypeVariable(concreteClass, declaringClass, (TypeVariable)t);
            if (ct != null) {
                return ct;
            }
        }
        else if (t instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)t;
            final Type[] ptts = pt.getActualTypeArguments();
            boolean modified = false;
            for (int i = 0; i < ptts.length; ++i) {
                final ReflectionHelper.ClassTypePair ct2 = getGenericType(concreteClass, declaringClass, (Class)pt.getRawType(), ptts[i]);
                if (ct2.t != ptts[i]) {
                    ptts[i] = ct2.t;
                    modified = true;
                }
            }
            if (modified) {
                final ParameterizedType rpt = new ParameterizedType() {
                    @Override
                    public Type[] getActualTypeArguments() {
                        return ptts.clone();
                    }
                    
                    @Override
                    public Type getRawType() {
                        return pt.getRawType();
                    }
                    
                    @Override
                    public Type getOwnerType() {
                        return pt.getOwnerType();
                    }
                };
                return new ReflectionHelper.ClassTypePair((Class)pt.getRawType(), rpt);
            }
        }
        else if (t instanceof GenericArrayType) {
            final GenericArrayType gat = (GenericArrayType)t;
            final ReflectionHelper.ClassTypePair ct3 = getGenericType(concreteClass, declaringClass, null, gat.getGenericComponentType());
            if (gat.getGenericComponentType() != ct3.t) {
                try {
                    final Class ac = ReflectionHelper.getArrayClass(ct3.c);
                    return new ReflectionHelper.ClassTypePair(ac, ac);
                }
                catch (Exception ex) {}
            }
        }
        return new ReflectionHelper.ClassTypePair(c, t);
    }
    
    static {
        LOGGER = Logger.getLogger(IntrospectionModeller.class.getName());
        ANOT_HELPER_MAP = createParamAnotHelperMap();
    }
    
    private interface ParamAnnotationHelper<T extends Annotation>
    {
        String getValueOf(final T p0);
        
        Parameter.Source getSource();
    }
}
