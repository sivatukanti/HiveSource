// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.modelapi.validation;

import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.reflection.AnnotatedMethod;
import com.sun.jersey.core.reflection.MethodList;
import java.security.AccessController;
import java.util.Collection;
import java.util.Arrays;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Context;
import java.util.HashSet;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Iterator;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import javax.ws.rs.Path;
import javax.ws.rs.HttpMethod;
import java.util.LinkedList;
import java.lang.annotation.Annotation;
import javax.ws.rs.FormParam;
import com.sun.jersey.api.model.Parameterized;
import com.sun.jersey.api.model.AbstractResourceMethod;
import java.lang.reflect.Method;
import com.sun.jersey.api.model.AbstractSetterMethod;
import java.lang.reflect.Field;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.AbstractField;
import com.sun.jersey.api.model.AbstractResourceConstructor;
import com.sun.jersey.api.model.ResourceModelIssue;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.api.model.AbstractResource;
import java.util.Set;

public class BasicValidator extends AbstractModelValidator
{
    private static final Set<Class> ParamAnnotationSET;
    
    @Override
    public void visitAbstractResource(final AbstractResource resource) {
        if (resource.isRootResource() && (null == resource.getPath() || null == resource.getPath().getValue())) {
            this.issueList.add(new ResourceModelIssue(resource, ImplMessages.ERROR_RES_URI_PATH_INVALID(resource.getResourceClass(), resource.getPath()), true));
        }
        this.checkNonPublicMethods(resource);
    }
    
    @Override
    public void visitAbstractResourceConstructor(final AbstractResourceConstructor constructor) {
    }
    
    @Override
    public void visitAbstractField(final AbstractField field) {
        final Field f = field.getField();
        this.checkParameter(field.getParameters().get(0), f, f.toGenericString(), f.getName());
    }
    
    @Override
    public void visitAbstractSetterMethod(final AbstractSetterMethod setterMethod) {
        final Method m = setterMethod.getMethod();
        this.checkParameter(setterMethod.getParameters().get(0), m, m.toGenericString(), "1");
    }
    
    @Override
    public void visitAbstractResourceMethod(final AbstractResourceMethod method) {
        this.checkParameters(method, method.getMethod());
        if ("GET".equals(method.getHttpMethod()) && !this.isRequestResponseMethod(method)) {
            if (Void.TYPE == method.getMethod().getReturnType()) {
                this.issueList.add(new ResourceModelIssue(method, ImplMessages.ERROR_GET_RETURNS_VOID(method.getMethod()), false));
            }
            if (method.hasEntity()) {
                this.issueList.add(new ResourceModelIssue(method, ImplMessages.ERROR_GET_CONSUMES_ENTITY(method.getMethod()), false));
            }
            for (final Parameter p : method.getParameters()) {
                if (p.isAnnotationPresent(FormParam.class)) {
                    this.issueList.add(new ResourceModelIssue(method, ImplMessages.ERROR_GET_CONSUMES_FORM_PARAM(method.getMethod()), true));
                    break;
                }
            }
        }
        final List<String> httpAnnotList = new LinkedList<String>();
        for (final Annotation a : method.getMethod().getDeclaredAnnotations()) {
            if (null != a.annotationType().getAnnotation(HttpMethod.class)) {
                httpAnnotList.add(a.toString());
            }
            else if (a.annotationType() == Path.class && !(method instanceof AbstractSubResourceMethod)) {
                this.issueList.add(new ResourceModelIssue(method, ImplMessages.SUB_RES_METHOD_TREATED_AS_RES_METHOD(method.getMethod(), ((Path)a).value()), false));
            }
        }
        if (httpAnnotList.size() > 1) {
            this.issueList.add(new ResourceModelIssue(method, ImplMessages.MULTIPLE_HTTP_METHOD_DESIGNATORS(method.getMethod(), httpAnnotList.toString()), true));
        }
        final Type t = method.getGenericReturnType();
        if (!this.isConcreteType(t)) {
            this.issueList.add(new ResourceModelIssue(method.getMethod(), "Return type " + t + " of method " + method.getMethod().toGenericString() + " is not resolvable to a concrete type", false));
        }
    }
    
    @Override
    public void visitAbstractSubResourceMethod(final AbstractSubResourceMethod method) {
        this.visitAbstractResourceMethod(method);
        if (null == method.getPath() || null == method.getPath().getValue() || method.getPath().getValue().length() == 0) {
            this.issueList.add(new ResourceModelIssue(method, ImplMessages.ERROR_SUBRES_METHOD_URI_PATH_INVALID(method.getMethod(), method.getPath()), true));
        }
    }
    
    @Override
    public void visitAbstractSubResourceLocator(final AbstractSubResourceLocator locator) {
        this.checkParameters(locator, locator.getMethod());
        if (Void.TYPE == locator.getMethod().getReturnType()) {
            this.issueList.add(new ResourceModelIssue(locator, ImplMessages.ERROR_SUBRES_LOC_RETURNS_VOID(locator.getMethod()), true));
        }
        if (null == locator.getPath() || null == locator.getPath().getValue() || locator.getPath().getValue().length() == 0) {
            this.issueList.add(new ResourceModelIssue(locator, ImplMessages.ERROR_SUBRES_LOC_URI_PATH_INVALID(locator.getMethod(), locator.getPath()), true));
        }
        for (final Parameter parameter : locator.getParameters()) {
            if (Parameter.Source.ENTITY == parameter.getSource()) {
                this.issueList.add(new ResourceModelIssue(locator, ImplMessages.ERROR_SUBRES_LOC_HAS_ENTITY_PARAM(locator.getMethod()), true));
            }
        }
    }
    
    private static Set<Class> createParamAnnotationSet() {
        final Set<Class> set = new HashSet<Class>(6);
        set.add(Context.class);
        set.add(HeaderParam.class);
        set.add(CookieParam.class);
        set.add(MatrixParam.class);
        set.add(QueryParam.class);
        set.add(PathParam.class);
        return (Set<Class>)Collections.unmodifiableSet((Set<? extends Class>)set);
    }
    
    private void checkParameter(final Parameter p, final Object source, final String nameForLogging, final String paramNameForLogging) {
        int annotCount = 0;
        for (final Annotation a : p.getAnnotations()) {
            if (BasicValidator.ParamAnnotationSET.contains(a.annotationType()) && ++annotCount > 1) {
                this.issueList.add(new ResourceModelIssue(source, ImplMessages.AMBIGUOUS_PARAMETER(nameForLogging, paramNameForLogging), false));
                break;
            }
        }
        final Type t = p.getParameterType();
        if (!this.isConcreteType(t)) {
            this.issueList.add(new ResourceModelIssue(source, "Parameter " + paramNameForLogging + " of type " + t + " from " + nameForLogging + " is not resolvable to a concrete type", false));
        }
    }
    
    private boolean isConcreteType(final Type t) {
        if (t instanceof ParameterizedType) {
            return this.isConcreteParameterizedType((ParameterizedType)t);
        }
        return t instanceof Class;
    }
    
    private boolean isConcreteParameterizedType(final ParameterizedType pt) {
        boolean isConcrete = true;
        for (final Type t : pt.getActualTypeArguments()) {
            isConcrete &= this.isConcreteType(t);
        }
        return isConcrete;
    }
    
    private void checkParameters(final Parameterized pl, final Method m) {
        int paramCount = 0;
        for (final Parameter p : pl.getParameters()) {
            this.checkParameter(p, m, m.toGenericString(), Integer.toString(++paramCount));
        }
    }
    
    private List<Method> getDeclaredMethods(final Class _c) {
        final List<Method> ml = new ArrayList<Method>();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            Class c = _c;
            
            @Override
            public Object run() {
                while (this.c != Object.class && this.c != null) {
                    ml.addAll(Arrays.asList(this.c.getDeclaredMethods()));
                    this.c = this.c.getSuperclass();
                }
                return null;
            }
        });
        return ml;
    }
    
    private void checkNonPublicMethods(final AbstractResource ar) {
        final MethodList declaredMethods = new MethodList(this.getDeclaredMethods(ar.getResourceClass()));
        for (final AnnotatedMethod m : declaredMethods.hasMetaAnnotation(HttpMethod.class).hasNotAnnotation(Path.class).isNotPublic()) {
            this.issueList.add(new ResourceModelIssue(ar, ImplMessages.NON_PUB_RES_METHOD(m.getMethod().toGenericString()), false));
        }
        for (final AnnotatedMethod m : declaredMethods.hasMetaAnnotation(HttpMethod.class).hasAnnotation(Path.class).isNotPublic()) {
            this.issueList.add(new ResourceModelIssue(ar, ImplMessages.NON_PUB_SUB_RES_METHOD(m.getMethod().toGenericString()), false));
        }
        for (final AnnotatedMethod m : declaredMethods.hasNotMetaAnnotation(HttpMethod.class).hasAnnotation(Path.class).isNotPublic()) {
            this.issueList.add(new ResourceModelIssue(ar, ImplMessages.NON_PUB_SUB_RES_LOC(m.getMethod().toGenericString()), false));
        }
    }
    
    private boolean isRequestResponseMethod(final AbstractResourceMethod method) {
        return method.getMethod().getParameterTypes().length == 2 && HttpRequestContext.class == method.getMethod().getParameterTypes()[0] && HttpResponseContext.class == method.getMethod().getParameterTypes()[1];
    }
    
    static {
        ParamAnnotationSET = createParamAnnotationSet();
    }
}
