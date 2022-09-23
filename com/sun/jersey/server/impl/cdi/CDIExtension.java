// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import java.util.concurrent.ConcurrentHashMap;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Provider;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import javax.ws.rs.DefaultValue;
import java.util.Collection;
import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.ws.rs.Encoded;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.naming.Name;
import java.util.Iterator;
import java.util.ArrayList;
import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.spi.container.ExceptionMapperContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Request;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Application;
import java.util.Collections;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.CookieParam;
import java.util.HashSet;
import javax.enterprise.inject.spi.BeanManager;
import java.util.HashMap;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.sun.jersey.server.impl.InitialContextHelper;
import java.util.List;
import com.sun.jersey.api.model.Parameter;
import java.lang.annotation.Annotation;
import java.util.Set;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import java.util.logging.Logger;
import javax.enterprise.inject.spi.Extension;

public class CDIExtension implements Extension
{
    private static final Logger LOGGER;
    private final Context contextAnnotationLiteral;
    private final Inject injectAnnotationLiteral;
    private Map<ClassLoader, WebApplication> webApplications;
    private ResourceConfig resourceConfig;
    private Set<Class<? extends Annotation>> knownParameterQualifiers;
    private Set<Class<?>> staticallyDefinedContextBeans;
    private Map<Class<? extends Annotation>, Parameter.Source> paramQualifiersMap;
    private Map<Class<? extends Annotation>, Set<DiscoveredParameter>> discoveredParameterMap;
    private Map<DiscoveredParameter, SyntheticQualifier> syntheticQualifierMap;
    private int nextSyntheticQualifierValue;
    List<InitializedLater> toBeInitializedLater;
    private static String JNDI_CDIEXTENSION_NAME;
    private static String JNDI_CDIEXTENSION_CTX;
    private static final String LOOKUP_EXTENSION_IN_BEAN_MANAGER_SYSTEM_PROPERTY = "com.sun.jersey.server.impl.cdi.lookupExtensionInBeanManager";
    public static final boolean lookupExtensionInBeanManager;
    
    private static boolean getLookupExtensionInBeanManager() {
        return Boolean.parseBoolean(System.getProperty("com.sun.jersey.server.impl.cdi.lookupExtensionInBeanManager", "false"));
    }
    
    public static CDIExtension getInitializedExtension() {
        try {
            final InitialContext ic = InitialContextHelper.getInitialContext();
            if (ic == null) {
                throw new RuntimeException();
            }
            return (CDIExtension)lookupJerseyConfigJNDIContext(ic).lookup(CDIExtension.JNDI_CDIEXTENSION_NAME);
        }
        catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public CDIExtension() {
        this.contextAnnotationLiteral = new ContextAnnotationLiteral();
        this.injectAnnotationLiteral = new InjectAnnotationLiteral();
        this.webApplications = new HashMap<ClassLoader, WebApplication>();
        this.nextSyntheticQualifierValue = 0;
    }
    
    private void initialize(final BeanManager manager) {
        if (!CDIExtension.lookupExtensionInBeanManager) {
            try {
                final InitialContext ic = InitialContextHelper.getInitialContext();
                if (ic != null) {
                    final javax.naming.Context jerseyConfigJNDIContext = createJerseyConfigJNDIContext(ic);
                    jerseyConfigJNDIContext.rebind(CDIExtension.JNDI_CDIEXTENSION_NAME, this);
                }
            }
            catch (NamingException ex) {
                throw new RuntimeException(ex);
            }
        }
        final Set<Class<? extends Annotation>> set = new HashSet<Class<? extends Annotation>>();
        set.add(CookieParam.class);
        set.add(FormParam.class);
        set.add(HeaderParam.class);
        set.add(MatrixParam.class);
        set.add(PathParam.class);
        set.add(QueryParam.class);
        set.add(Context.class);
        this.knownParameterQualifiers = Collections.unmodifiableSet((Set<? extends Class<? extends Annotation>>)set);
        final Map<Class<? extends Annotation>, Parameter.Source> map = new HashMap<Class<? extends Annotation>, Parameter.Source>();
        map.put(CookieParam.class, Parameter.Source.COOKIE);
        map.put(FormParam.class, Parameter.Source.FORM);
        map.put(HeaderParam.class, Parameter.Source.HEADER);
        map.put(MatrixParam.class, Parameter.Source.MATRIX);
        map.put(PathParam.class, Parameter.Source.PATH);
        map.put(QueryParam.class, Parameter.Source.QUERY);
        map.put(Context.class, Parameter.Source.CONTEXT);
        this.paramQualifiersMap = Collections.unmodifiableMap((Map<? extends Class<? extends Annotation>, ? extends Parameter.Source>)map);
        final Set<Class<?>> set2 = new HashSet<Class<?>>();
        set2.add(Application.class);
        set2.add(HttpHeaders.class);
        set2.add(Providers.class);
        set2.add(Request.class);
        set2.add(SecurityContext.class);
        set2.add(UriInfo.class);
        set2.add(ExceptionMapperContext.class);
        set2.add(ExtendedUriInfo.class);
        set2.add(FeaturesAndProperties.class);
        set2.add(HttpContext.class);
        set2.add(HttpRequestContext.class);
        set2.add(HttpResponseContext.class);
        set2.add(MessageBodyWorkers.class);
        set2.add(ResourceContext.class);
        set2.add(WebApplication.class);
        this.staticallyDefinedContextBeans = Collections.unmodifiableSet((Set<? extends Class<?>>)set2);
        final Map<Class<? extends Annotation>, Set<DiscoveredParameter>> map2 = new HashMap<Class<? extends Annotation>, Set<DiscoveredParameter>>();
        for (final Class<? extends Annotation> qualifier : this.knownParameterQualifiers) {
            map2.put(qualifier, new HashSet<DiscoveredParameter>());
        }
        this.discoveredParameterMap = Collections.unmodifiableMap((Map<? extends Class<? extends Annotation>, ? extends Set<DiscoveredParameter>>)map2);
        this.syntheticQualifierMap = new HashMap<DiscoveredParameter, SyntheticQualifier>();
        this.toBeInitializedLater = new ArrayList<InitializedLater>();
    }
    
    private static javax.naming.Context diveIntoJNDIContext(final javax.naming.Context initialContext, final JNDIContextDiver diver) throws NamingException {
        final Name jerseyConfigCtxName = initialContext.getNameParser("").parse(CDIExtension.JNDI_CDIEXTENSION_CTX);
        javax.naming.Context currentContext = initialContext;
        for (int i = 0; i < jerseyConfigCtxName.size(); ++i) {
            currentContext = diver.stepInto(currentContext, jerseyConfigCtxName.get(i));
        }
        return currentContext;
    }
    
    private static javax.naming.Context createJerseyConfigJNDIContext(final javax.naming.Context initialContext) throws NamingException {
        return diveIntoJNDIContext(initialContext, new JNDIContextDiver() {
            @Override
            public javax.naming.Context stepInto(final javax.naming.Context ctx, final String name) throws NamingException {
                try {
                    return (javax.naming.Context)ctx.lookup(name);
                }
                catch (NamingException e) {
                    return ctx.createSubcontext(name);
                }
            }
        });
    }
    
    private static javax.naming.Context lookupJerseyConfigJNDIContext(final javax.naming.Context initialContext) throws NamingException {
        return diveIntoJNDIContext(initialContext, new JNDIContextDiver() {
            @Override
            public javax.naming.Context stepInto(final javax.naming.Context ctx, final String name) throws NamingException {
                return (javax.naming.Context)ctx.lookup(name);
            }
        });
    }
    
    void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery event, final BeanManager manager) {
        CDIExtension.LOGGER.fine("Handling BeforeBeanDiscovery event");
        this.initialize(manager);
        for (final Class<? extends Annotation> qualifier : this.knownParameterQualifiers) {
            event.addQualifier((Class)qualifier);
        }
    }
    
     <T> void processAnnotatedType(@Observes final ProcessAnnotatedType<T> event) {
        CDIExtension.LOGGER.fine("Handling ProcessAnnotatedType event for " + event.getAnnotatedType().getJavaClass().getName());
        final AnnotatedType<T> type = (AnnotatedType<T>)event.getAnnotatedType();
        final boolean classHasEncodedAnnotation = type.isAnnotationPresent((Class)Encoded.class);
        final Set<AnnotatedConstructor<T>> mustPatchConstructors = new HashSet<AnnotatedConstructor<T>>();
        final Map<AnnotatedParameter<? super T>, PatchInformation> parameterToPatchInfoMap = new HashMap<AnnotatedParameter<? super T>, PatchInformation>();
        for (final AnnotatedConstructor<T> constructor : type.getConstructors()) {
            if (this.processAnnotatedConstructor(constructor, classHasEncodedAnnotation, parameterToPatchInfoMap)) {
                mustPatchConstructors.add(constructor);
            }
        }
        final Set<AnnotatedField<? super T>> mustPatchFields = new HashSet<AnnotatedField<? super T>>();
        final Map<AnnotatedField<? super T>, PatchInformation> fieldToPatchInfoMap = new HashMap<AnnotatedField<? super T>, PatchInformation>();
        for (final AnnotatedField<? super T> field : type.getFields()) {
            if (this.processAnnotatedField((javax.enterprise.inject.spi.AnnotatedField<? super Object>)field, (Class<Object>)type.getJavaClass(), classHasEncodedAnnotation, (Map<javax.enterprise.inject.spi.AnnotatedField<? super Object>, PatchInformation>)fieldToPatchInfoMap)) {
                mustPatchFields.add(field);
            }
        }
        final Set<AnnotatedMethod<? super T>> mustPatchMethods = new HashSet<AnnotatedMethod<? super T>>();
        final Set<AnnotatedMethod<? super T>> setterMethodsWithoutInject = new HashSet<AnnotatedMethod<? super T>>();
        for (final AnnotatedMethod<? super T> method : type.getMethods()) {
            if (this.processAnnotatedMethod((javax.enterprise.inject.spi.AnnotatedMethod<? super Object>)method, (Class<Object>)type.getJavaClass(), classHasEncodedAnnotation, (Map<javax.enterprise.inject.spi.AnnotatedParameter<? super Object>, PatchInformation>)parameterToPatchInfoMap, (Set<javax.enterprise.inject.spi.AnnotatedMethod<? super Object>>)setterMethodsWithoutInject)) {
                mustPatchMethods.add(method);
            }
        }
        final boolean typeNeedsPatching = !mustPatchConstructors.isEmpty() || !mustPatchFields.isEmpty() || !mustPatchMethods.isEmpty();
        if (typeNeedsPatching) {
            final AnnotatedTypeImpl<T> newType = new AnnotatedTypeImpl<T>(type);
            final Set<AnnotatedConstructor<T>> newConstructors = new HashSet<AnnotatedConstructor<T>>();
            for (final AnnotatedConstructor<T> constructor2 : type.getConstructors()) {
                final AnnotatedConstructorImpl<T> newConstructor = new AnnotatedConstructorImpl<T>(constructor2, (javax.enterprise.inject.spi.AnnotatedType<T>)newType);
                if (mustPatchConstructors.contains(constructor2)) {
                    this.patchAnnotatedCallable((javax.enterprise.inject.spi.AnnotatedCallable<? super T>)constructor2, newConstructor, parameterToPatchInfoMap);
                }
                else {
                    this.copyParametersOfAnnotatedCallable((javax.enterprise.inject.spi.AnnotatedCallable<? super T>)constructor2, newConstructor);
                }
                newConstructors.add((AnnotatedConstructor<T>)newConstructor);
            }
            final Set<AnnotatedField<? super T>> newFields = new HashSet<AnnotatedField<? super T>>();
            for (final AnnotatedField<? super T> field2 : type.getFields()) {
                if (mustPatchFields.contains(field2)) {
                    final PatchInformation patchInfo = fieldToPatchInfoMap.get(field2);
                    final Set<Annotation> annotations = new HashSet<Annotation>();
                    if (patchInfo.mustAddInject()) {
                        annotations.add(this.injectAnnotationLiteral);
                    }
                    if (patchInfo.getSyntheticQualifier() != null) {
                        annotations.add(patchInfo.getSyntheticQualifier());
                        final Annotation skippedQualifier = patchInfo.getParameter().getAnnotation();
                        for (final Annotation annotation : field2.getAnnotations()) {
                            if (annotation != skippedQualifier) {
                                annotations.add(annotation);
                            }
                        }
                    }
                    else {
                        annotations.addAll(field2.getAnnotations());
                    }
                    if (patchInfo.getAnnotation() != null) {
                        annotations.add(patchInfo.getAnnotation());
                    }
                    newFields.add((AnnotatedField<? super T>)new AnnotatedFieldImpl((javax.enterprise.inject.spi.AnnotatedField<? super Object>)field2, annotations, (javax.enterprise.inject.spi.AnnotatedType<Object>)newType));
                }
                else {
                    newFields.add((AnnotatedField<? super T>)new AnnotatedFieldImpl((javax.enterprise.inject.spi.AnnotatedField<? super Object>)field2, (javax.enterprise.inject.spi.AnnotatedType<Object>)newType));
                }
            }
            final Set<AnnotatedMethod<? super T>> newMethods = new HashSet<AnnotatedMethod<? super T>>();
            for (final AnnotatedMethod<? super T> method2 : type.getMethods()) {
                if (mustPatchMethods.contains(method2)) {
                    if (setterMethodsWithoutInject.contains(method2)) {
                        final Set<Annotation> annotations = new HashSet<Annotation>();
                        annotations.add(this.injectAnnotationLiteral);
                        for (final Annotation annotation2 : method2.getAnnotations()) {
                            if (!this.knownParameterQualifiers.contains(annotation2.annotationType())) {
                                annotations.add(annotation2);
                            }
                        }
                        final AnnotatedMethodImpl<T> newMethod = new AnnotatedMethodImpl<T>(method2, annotations, (javax.enterprise.inject.spi.AnnotatedType<T>)newType);
                        this.patchAnnotatedCallable((javax.enterprise.inject.spi.AnnotatedCallable<? super T>)method2, newMethod, parameterToPatchInfoMap);
                        newMethods.add((AnnotatedMethod<? super T>)newMethod);
                    }
                    else {
                        final AnnotatedMethodImpl<T> newMethod2 = new AnnotatedMethodImpl<T>(method2, (javax.enterprise.inject.spi.AnnotatedType<T>)newType);
                        this.patchAnnotatedCallable((javax.enterprise.inject.spi.AnnotatedCallable<? super T>)method2, newMethod2, parameterToPatchInfoMap);
                        newMethods.add((AnnotatedMethod<? super T>)newMethod2);
                    }
                }
                else {
                    final AnnotatedMethodImpl<T> newMethod2 = new AnnotatedMethodImpl<T>(method2, (javax.enterprise.inject.spi.AnnotatedType<T>)newType);
                    this.copyParametersOfAnnotatedCallable((javax.enterprise.inject.spi.AnnotatedCallable<? super T>)method2, newMethod2);
                    newMethods.add((AnnotatedMethod<? super T>)newMethod2);
                }
            }
            newType.setConstructors(newConstructors);
            newType.setFields(newFields);
            newType.setMethods(newMethods);
            event.setAnnotatedType((AnnotatedType)newType);
            CDIExtension.LOGGER.fine("  replaced annotated type for " + type.getJavaClass());
        }
    }
    
    private <T> boolean processAnnotatedConstructor(final AnnotatedConstructor<T> constructor, final boolean classHasEncodedAnnotation, final Map<AnnotatedParameter<? super T>, PatchInformation> parameterToPatchInfoMap) {
        boolean mustPatch = false;
        if (constructor.getAnnotation((Class)Inject.class) != null) {
            final boolean methodHasEncodedAnnotation = constructor.isAnnotationPresent((Class)Encoded.class);
            for (final AnnotatedParameter<T> parameter : constructor.getParameters()) {
                for (final Annotation annotation : parameter.getAnnotations()) {
                    final Set<DiscoveredParameter> discovered = this.discoveredParameterMap.get(annotation.annotationType());
                    if (discovered != null && this.knownParameterQualifiers.contains(annotation.annotationType())) {
                        if (methodHasEncodedAnnotation || classHasEncodedAnnotation || parameter.isAnnotationPresent((Class)DefaultValue.class)) {
                            mustPatch = true;
                        }
                        final boolean encoded = parameter.isAnnotationPresent((Class)Encoded.class) || methodHasEncodedAnnotation || classHasEncodedAnnotation;
                        final DefaultValue defaultValue = (DefaultValue)parameter.getAnnotation((Class)DefaultValue.class);
                        if (defaultValue != null) {
                            mustPatch = true;
                        }
                        final DiscoveredParameter jerseyParameter = new DiscoveredParameter(annotation, parameter.getBaseType(), defaultValue, encoded);
                        discovered.add(jerseyParameter);
                        CDIExtension.LOGGER.fine("  recorded " + jerseyParameter);
                        parameterToPatchInfoMap.put(parameter, new PatchInformation(jerseyParameter, this.getSyntheticQualifierFor(jerseyParameter), false));
                    }
                }
            }
        }
        return mustPatch;
    }
    
    private <T> boolean processAnnotatedMethod(final AnnotatedMethod<? super T> method, final Class<T> token, final boolean classHasEncodedAnnotation, final Map<AnnotatedParameter<? super T>, PatchInformation> parameterToPatchInfoMap, final Set<AnnotatedMethod<? super T>> setterMethodsWithoutInject) {
        boolean mustPatch = false;
        if (method.getAnnotation((Class)Inject.class) != null) {
            final boolean methodHasEncodedAnnotation = method.isAnnotationPresent((Class)Encoded.class);
            for (final AnnotatedParameter<? super T> parameter : method.getParameters()) {
                for (final Annotation annotation : parameter.getAnnotations()) {
                    final Set<DiscoveredParameter> discovered = this.discoveredParameterMap.get(annotation.annotationType());
                    if (discovered != null && this.knownParameterQualifiers.contains(annotation.annotationType())) {
                        if (methodHasEncodedAnnotation || classHasEncodedAnnotation || parameter.isAnnotationPresent((Class)DefaultValue.class)) {
                            mustPatch = true;
                        }
                        final boolean encoded = parameter.isAnnotationPresent((Class)Encoded.class) || methodHasEncodedAnnotation || classHasEncodedAnnotation;
                        final DefaultValue defaultValue = (DefaultValue)parameter.getAnnotation((Class)DefaultValue.class);
                        if (defaultValue != null) {
                            mustPatch = true;
                        }
                        final DiscoveredParameter jerseyParameter = new DiscoveredParameter(annotation, parameter.getBaseType(), defaultValue, encoded);
                        discovered.add(jerseyParameter);
                        CDIExtension.LOGGER.fine("  recorded " + jerseyParameter);
                        parameterToPatchInfoMap.put(parameter, new PatchInformation(jerseyParameter, this.getSyntheticQualifierFor(jerseyParameter), false));
                    }
                }
            }
        }
        else if (this.isSetterMethod(method)) {
            final boolean methodHasEncodedAnnotation = method.isAnnotationPresent((Class)Encoded.class);
            for (final Annotation annotation2 : method.getAnnotations()) {
                final Set<DiscoveredParameter> discovered2 = this.discoveredParameterMap.get(annotation2.annotationType());
                if (discovered2 != null && this.knownParameterQualifiers.contains(annotation2.annotationType())) {
                    mustPatch = true;
                    setterMethodsWithoutInject.add(method);
                    for (final AnnotatedParameter<? super T> parameter2 : method.getParameters()) {
                        final boolean encoded = parameter2.isAnnotationPresent((Class)Encoded.class) || methodHasEncodedAnnotation || classHasEncodedAnnotation;
                        DefaultValue defaultValue = (DefaultValue)parameter2.getAnnotation((Class)DefaultValue.class);
                        if (defaultValue == null) {
                            defaultValue = (DefaultValue)method.getAnnotation((Class)DefaultValue.class);
                        }
                        final DiscoveredParameter jerseyParameter = new DiscoveredParameter(annotation2, parameter2.getBaseType(), defaultValue, encoded);
                        discovered2.add(jerseyParameter);
                        CDIExtension.LOGGER.fine("  recorded " + jerseyParameter);
                        final SyntheticQualifier syntheticQualifier = this.getSyntheticQualifierFor(jerseyParameter);
                        final Annotation addedAnnotation = (syntheticQualifier == null) ? annotation2 : null;
                        parameterToPatchInfoMap.put(parameter2, new PatchInformation(jerseyParameter, syntheticQualifier, addedAnnotation, false));
                    }
                    break;
                }
            }
        }
        return mustPatch;
    }
    
    private <T> boolean isSetterMethod(final AnnotatedMethod<T> method) {
        final Method javaMethod = method.getJavaMember();
        if ((javaMethod.getModifiers() & 0x1) != 0x0 && javaMethod.getReturnType() == Void.TYPE && javaMethod.getName().startsWith("set")) {
            final List<AnnotatedParameter<T>> parameters = (List<AnnotatedParameter<T>>)method.getParameters();
            if (parameters.size() == 1) {
                return true;
            }
        }
        return false;
    }
    
    private <T> boolean processAnnotatedField(final AnnotatedField<? super T> field, final Class<T> token, final boolean classHasEncodedAnnotation, final Map<AnnotatedField<? super T>, PatchInformation> fieldToPatchInfoMap) {
        boolean mustPatch = false;
        for (final Annotation annotation : field.getAnnotations()) {
            if (this.knownParameterQualifiers.contains(annotation.annotationType())) {
                final boolean mustAddInjectAnnotation = !field.isAnnotationPresent((Class)Inject.class);
                if (field.isAnnotationPresent((Class)Encoded.class) || classHasEncodedAnnotation || mustAddInjectAnnotation || field.isAnnotationPresent((Class)DefaultValue.class)) {
                    mustPatch = true;
                }
                final Set<DiscoveredParameter> discovered = this.discoveredParameterMap.get(annotation.annotationType());
                if (discovered == null) {
                    continue;
                }
                final boolean encoded = field.isAnnotationPresent((Class)Encoded.class) || classHasEncodedAnnotation;
                final DefaultValue defaultValue = (DefaultValue)field.getAnnotation((Class)DefaultValue.class);
                final DiscoveredParameter parameter = new DiscoveredParameter(annotation, field.getBaseType(), defaultValue, encoded);
                discovered.add(parameter);
                CDIExtension.LOGGER.fine("  recorded " + parameter);
                fieldToPatchInfoMap.put(field, new PatchInformation(parameter, this.getSyntheticQualifierFor(parameter), mustAddInjectAnnotation));
            }
        }
        return mustPatch;
    }
    
    private <T> void patchAnnotatedCallable(final AnnotatedCallable<? super T> callable, final AnnotatedCallableImpl<T> newCallable, final Map<AnnotatedParameter<? super T>, PatchInformation> parameterToPatchInfoMap) {
        final List<AnnotatedParameter<T>> newParams = new ArrayList<AnnotatedParameter<T>>();
        for (final AnnotatedParameter<? super T> parameter : callable.getParameters()) {
            final PatchInformation patchInfo = parameterToPatchInfoMap.get(parameter);
            if (patchInfo != null) {
                final Set<Annotation> annotations = new HashSet<Annotation>();
                if (patchInfo.mustAddInject()) {
                    annotations.add(this.injectAnnotationLiteral);
                }
                if (patchInfo.getSyntheticQualifier() != null) {
                    annotations.add(patchInfo.getSyntheticQualifier());
                    final Annotation skippedQualifier = patchInfo.getParameter().getAnnotation();
                    for (final Annotation annotation : parameter.getAnnotations()) {
                        if (annotation != skippedQualifier) {
                            annotations.add(annotation);
                        }
                    }
                }
                else {
                    annotations.addAll(parameter.getAnnotations());
                }
                if (patchInfo.getAnnotation() != null) {
                    annotations.add(patchInfo.getAnnotation());
                }
                newParams.add((AnnotatedParameter<T>)new AnnotatedParameterImpl((javax.enterprise.inject.spi.AnnotatedParameter<? super Object>)parameter, annotations, (javax.enterprise.inject.spi.AnnotatedCallable<Object>)newCallable));
            }
            else {
                newParams.add((AnnotatedParameter<T>)new AnnotatedParameterImpl((javax.enterprise.inject.spi.AnnotatedParameter<? super Object>)parameter, (javax.enterprise.inject.spi.AnnotatedCallable<Object>)newCallable));
            }
        }
        newCallable.setParameters(newParams);
    }
    
    private <T> void copyParametersOfAnnotatedCallable(final AnnotatedCallable<? super T> callable, final AnnotatedCallableImpl<T> newCallable) {
        final List<AnnotatedParameter<T>> newParams = new ArrayList<AnnotatedParameter<T>>();
        for (final AnnotatedParameter<? super T> parameter : callable.getParameters()) {
            newParams.add((AnnotatedParameter<T>)new AnnotatedParameterImpl((javax.enterprise.inject.spi.AnnotatedParameter<? super Object>)parameter, (javax.enterprise.inject.spi.AnnotatedCallable<Object>)newCallable));
        }
        newCallable.setParameters(newParams);
    }
    
    private SyntheticQualifier getSyntheticQualifierFor(final DiscoveredParameter parameter) {
        SyntheticQualifier result = this.syntheticQualifierMap.get(parameter);
        if (result == null && (parameter.isEncoded() || parameter.getDefaultValue() != null)) {
            result = new SyntheticQualifierAnnotationImpl(this.nextSyntheticQualifierValue++);
            this.syntheticQualifierMap.put(parameter, result);
            CDIExtension.LOGGER.fine("  created synthetic qualifier " + result);
        }
        return result;
    }
    
    private static Class getClassOfType(final Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof GenericArrayType) {
            final GenericArrayType arrayType = (GenericArrayType)type;
            final Type t = arrayType.getGenericComponentType();
            if (t instanceof Class) {
                final Class c = (Class)t;
                try {
                    final Object o = Array.newInstance(c, 0);
                    return o.getClass();
                }
                catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        else if (type instanceof ParameterizedType) {
            final ParameterizedType subType = (ParameterizedType)type;
            final Type t = subType.getRawType();
            if (t instanceof Class) {
                return (Class)t;
            }
        }
        return null;
    }
    
     <T> void processInjectionTarget(@Observes final ProcessInjectionTarget<T> event) {
        CDIExtension.LOGGER.fine("Handling ProcessInjectionTarget event for " + event.getAnnotatedType().getJavaClass().getName());
    }
    
    void processManagedBean(@Observes final ProcessManagedBean<?> event) {
        CDIExtension.LOGGER.fine("Handling ProcessManagedBean event for " + event.getBean().getBeanClass().getName());
        final Bean<?> bean = (Bean<?>)event.getBean();
        for (final InjectionPoint injectionPoint : bean.getInjectionPoints()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("  found injection point ");
            sb.append(injectionPoint.getType());
            for (final Annotation annotation : injectionPoint.getQualifiers()) {
                sb.append(" ");
                sb.append(annotation);
            }
            CDIExtension.LOGGER.fine(sb.toString());
        }
    }
    
    void afterBeanDiscovery(@Observes final AfterBeanDiscovery event) {
        CDIExtension.LOGGER.fine("Handling AfterBeanDiscovery event");
        this.addPredefinedContextBeans(event);
        final BeanGenerator beanGenerator = new BeanGenerator("com/sun/jersey/server/impl/cdi/generated/Bean");
        for (final Set<DiscoveredParameter> parameters : this.discoveredParameterMap.values()) {
            for (final DiscoveredParameter parameter : parameters) {
                final Annotation annotation = parameter.getAnnotation();
                final Class<?> klass = (Class<?>)getClassOfType(parameter.getType());
                if (annotation.annotationType() == Context.class && this.staticallyDefinedContextBeans.contains(klass) && !parameter.isEncoded() && parameter.getDefaultValue() == null) {
                    continue;
                }
                final SyntheticQualifier syntheticQualifier = this.syntheticQualifierMap.get(parameter);
                final Annotation theQualifier = (syntheticQualifier != null) ? syntheticQualifier : annotation;
                final Set<Annotation> annotations = new HashSet<Annotation>();
                annotations.add(theQualifier);
                final Parameter jerseyParameter = new Parameter(new Annotation[] { annotation }, annotation, this.paramQualifiersMap.get(annotation.annotationType()), parameter.getValue(), parameter.getType(), klass, parameter.isEncoded(), (parameter.getDefaultValue() == null) ? null : parameter.getDefaultValue().value());
                final Class<?> beanClass = beanGenerator.createBeanClass();
                final ParameterBean bean = new ParameterBean(beanClass, parameter.getType(), annotations, parameter, jerseyParameter);
                this.toBeInitializedLater.add(bean);
                event.addBean((Bean)bean);
                CDIExtension.LOGGER.fine("Added bean for parameter " + parameter + " and qualifier " + theQualifier);
            }
        }
    }
    
    private void addPredefinedContextBeans(final AfterBeanDiscovery event) {
        event.addBean((Bean)new PredefinedBean((Class<Object>)Application.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)HttpHeaders.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)Providers.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)Request.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)SecurityContext.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)UriInfo.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)ExceptionMapperContext.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)ExtendedUriInfo.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)FeaturesAndProperties.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)HttpContext.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)HttpRequestContext.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)HttpResponseContext.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)MessageBodyWorkers.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new PredefinedBean((Class<Object>)ResourceContext.class, this.contextAnnotationLiteral));
        event.addBean((Bean)new ProviderBasedBean(WebApplication.class, (Provider<Object>)new Provider<WebApplication>() {
            @Override
            public WebApplication get() {
                return CDIExtension.this.lookupWebApplication();
            }
        }, this.contextAnnotationLiteral));
    }
    
    private WebApplication lookupWebApplication() {
        return this.lookupWebApplication(Thread.currentThread().getContextClassLoader());
    }
    
    private WebApplication lookupWebApplication(final ClassLoader cl) {
        return this.webApplications.get(cl);
    }
    
    void setWebApplication(final WebApplication wa) {
        this.webApplications.put(Thread.currentThread().getContextClassLoader(), wa);
    }
    
    WebApplication getWebApplication() {
        return this.lookupWebApplication();
    }
    
    void setResourceConfig(final ResourceConfig rc) {
        this.resourceConfig = rc;
    }
    
    ResourceConfig getResourceConfig() {
        return this.resourceConfig;
    }
    
    void lateInitialize() {
        try {
            for (final InitializedLater object : this.toBeInitializedLater) {
                object.later();
            }
        }
        finally {
            if (!CDIExtension.lookupExtensionInBeanManager) {
                try {
                    final InitialContext ic = InitialContextHelper.getInitialContext();
                    if (ic != null) {
                        lookupJerseyConfigJNDIContext(ic).unbind(CDIExtension.JNDI_CDIEXTENSION_NAME);
                    }
                }
                catch (NamingException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(CDIExtension.class.getName());
        CDIExtension.JNDI_CDIEXTENSION_NAME = "CDIExtension";
        CDIExtension.JNDI_CDIEXTENSION_CTX = "com/sun/jersey/config";
        lookupExtensionInBeanManager = getLookupExtensionInBeanManager();
    }
    
    private static class ContextAnnotationLiteral extends AnnotationLiteral<Context> implements Context
    {
    }
    
    private static class InjectAnnotationLiteral extends AnnotationLiteral<Inject> implements Inject
    {
    }
    
    private static class SyntheticQualifierAnnotationImpl extends AnnotationLiteral<SyntheticQualifier> implements SyntheticQualifier
    {
        private int value;
        
        public SyntheticQualifierAnnotationImpl(final int value) {
            this.value = value;
        }
        
        public int value() {
            return this.value;
        }
    }
    
    private static class PatchInformation
    {
        private DiscoveredParameter parameter;
        private SyntheticQualifier syntheticQualifier;
        private Annotation annotation;
        private boolean mustAddInject;
        
        public PatchInformation(final DiscoveredParameter parameter, final SyntheticQualifier syntheticQualifier, final boolean mustAddInject) {
            this(parameter, syntheticQualifier, null, mustAddInject);
        }
        
        public PatchInformation(final DiscoveredParameter parameter, final SyntheticQualifier syntheticQualifier, final Annotation annotation, final boolean mustAddInject) {
            this.parameter = parameter;
            this.syntheticQualifier = syntheticQualifier;
            this.annotation = annotation;
            this.mustAddInject = mustAddInject;
        }
        
        public DiscoveredParameter getParameter() {
            return this.parameter;
        }
        
        public SyntheticQualifier getSyntheticQualifier() {
            return this.syntheticQualifier;
        }
        
        public Annotation getAnnotation() {
            return this.annotation;
        }
        
        public boolean mustAddInject() {
            return this.mustAddInject;
        }
    }
    
    class PredefinedBean<T> extends AbstractBean<T>
    {
        private Annotation qualifier;
        
        public PredefinedBean(final Class<T> klass, final Annotation qualifier) {
            super(klass, qualifier);
            this.qualifier = qualifier;
        }
        
        @Override
        public T create(final CreationalContext<T> creationalContext) {
            final Injectable<T> injectable = (Injectable<T>)CDIExtension.this.lookupWebApplication().getServerInjectableProviderFactory().getInjectable(this.qualifier.annotationType(), null, this.qualifier, this.getBeanClass(), ComponentScope.Singleton);
            if (injectable == null) {
                Errors.error("No injectable for " + this.getBeanClass().getName());
                return null;
            }
            return injectable.getValue();
        }
    }
    
    class ParameterBean<T> extends AbstractBean<T> implements InitializedLater
    {
        private final DiscoveredParameter discoveredParameter;
        private final Parameter parameter;
        private final Map<ClassLoader, Injectable<T>> injectables;
        private final Map<ClassLoader, Boolean> processed;
        
        public ParameterBean(final Class<?> klass, final Type type, final Set<Annotation> qualifiers, final DiscoveredParameter discoveredParameter, final Parameter parameter) {
            super(klass, type, qualifiers);
            this.injectables = new ConcurrentHashMap<ClassLoader, Injectable<T>>();
            this.processed = new ConcurrentHashMap<ClassLoader, Boolean>();
            this.discoveredParameter = discoveredParameter;
            this.parameter = parameter;
        }
        
        @Override
        public void later() {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (this.injectables.containsKey(contextClassLoader)) {
                return;
            }
            if (this.processed.containsKey(contextClassLoader)) {
                return;
            }
            this.processed.put(contextClassLoader, true);
            final boolean registered = CDIExtension.this.lookupWebApplication(contextClassLoader).getServerInjectableProviderFactory().isParameterTypeRegistered(this.parameter);
            if (!registered) {
                Errors.error("Parameter type not registered " + this.discoveredParameter);
            }
            final Injectable<T> injectable = (Injectable<T>)CDIExtension.this.lookupWebApplication(contextClassLoader).getServerInjectableProviderFactory().getInjectable(this.parameter, ComponentScope.PerRequest);
            if (injectable == null) {
                Errors.error("No injectable for parameter " + this.discoveredParameter);
            }
            else {
                this.injectables.put(contextClassLoader, injectable);
            }
        }
        
        @Override
        public T create(final CreationalContext<T> creationalContext) {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (!this.injectables.containsKey(contextClassLoader)) {
                this.later();
                if (!this.injectables.containsKey(contextClassLoader)) {
                    return null;
                }
            }
            final Injectable<T> injectable = this.injectables.get(contextClassLoader);
            try {
                return injectable.getValue();
            }
            catch (IllegalStateException e) {
                if (injectable instanceof AbstractHttpContextInjectable) {
                    return ((AbstractHttpContextInjectable)this.injectables).getValue(CDIExtension.this.lookupWebApplication(contextClassLoader).getThreadLocalHttpContext());
                }
                throw e;
            }
        }
    }
    
    private interface JNDIContextDiver
    {
        javax.naming.Context stepInto(final javax.naming.Context p0, final String p1) throws NamingException;
    }
}
