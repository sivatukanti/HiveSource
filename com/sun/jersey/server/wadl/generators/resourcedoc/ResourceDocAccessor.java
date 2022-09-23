// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl.generators.resourcedoc;

import com.sun.jersey.server.wadl.generators.resourcedoc.model.NamedValueType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ResponseDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.RepresentationDocType;
import java.lang.annotation.Annotation;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.AnnotationDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ParamDocType;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.MethodDocType;
import java.lang.reflect.Method;
import java.util.Iterator;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ClassDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ResourceDocType;

public class ResourceDocAccessor
{
    private ResourceDocType _resourceDoc;
    
    public ResourceDocAccessor(final ResourceDocType resourceDoc) {
        this._resourceDoc = resourceDoc;
    }
    
    public ClassDocType getClassDoc(final Class<?> resourceClass) {
        for (final ClassDocType classDocType : this._resourceDoc.getDocs()) {
            if (resourceClass.getName().equals(classDocType.getClassName())) {
                return classDocType;
            }
        }
        return null;
    }
    
    public MethodDocType getMethodDoc(final Class<?> resourceClass, final Method method) {
        final ClassDocType classDoc = this.getClassDoc(resourceClass);
        if (classDoc != null) {
            for (final MethodDocType methodDocType : classDoc.getMethodDocs()) {
                if (method != null && method.getName().equals(methodDocType.getMethodName())) {
                    return methodDocType;
                }
            }
        }
        return null;
    }
    
    public ParamDocType getParamDoc(final Class<?> resourceClass, final Method method, final Parameter p) {
        final MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
        if (methodDoc != null) {
            for (final ParamDocType paramDocType : methodDoc.getParamDocs()) {
                for (final AnnotationDocType annotationDocType : paramDocType.getAnnotationDocs()) {
                    final Class<? extends Annotation> annotationType = p.getAnnotation().annotationType();
                    if (annotationType != null) {
                        final String sourceName = this.getSourceName(annotationDocType);
                        if (sourceName != null && sourceName.equals(p.getSourceName())) {
                            return paramDocType;
                        }
                        continue;
                    }
                }
            }
        }
        return null;
    }
    
    public RepresentationDocType getRequestRepresentation(final Class<?> resourceClass, final Method method, final String mediaType) {
        if (mediaType == null) {
            return null;
        }
        final MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
        return (methodDoc != null && methodDoc.getRequestDoc() != null && methodDoc.getRequestDoc().getRepresentationDoc() != null) ? methodDoc.getRequestDoc().getRepresentationDoc() : null;
    }
    
    public ResponseDocType getResponse(final Class<?> resourceClass, final Method method) {
        final MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
        return (methodDoc != null && methodDoc.getResponseDoc() != null) ? methodDoc.getResponseDoc() : null;
    }
    
    private String getSourceName(final AnnotationDocType annotationDocType) {
        if (annotationDocType.hasAttributeDocs()) {
            for (final NamedValueType namedValueType : annotationDocType.getAttributeDocs()) {
                if ("value".equals(namedValueType.getName())) {
                    return namedValueType.getValue();
                }
            }
        }
        return null;
    }
}
