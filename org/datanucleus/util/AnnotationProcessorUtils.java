// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.util.HashSet;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.ArrayType;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeKind;
import javax.lang.model.element.AnnotationValue;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.element.ExecutableElement;
import java.util.Iterator;
import javax.lang.model.element.ElementKind;
import java.util.ArrayList;
import javax.lang.model.element.Element;
import java.util.List;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class AnnotationProcessorUtils
{
    private static Set<String> LIST_CLASSNAMES;
    private static Set<String> SET_CLASSNAMES;
    private static Set<String> MAP_CLASSNAMES;
    private static Set<String> COLLECTION_CLASSNAMES;
    
    public static TypeCategory getTypeCategoryForTypeMirror(final String typeName) {
        if (AnnotationProcessorUtils.COLLECTION_CLASSNAMES.contains(typeName)) {
            return TypeCategory.COLLECTION;
        }
        if (AnnotationProcessorUtils.SET_CLASSNAMES.contains(typeName)) {
            return TypeCategory.SET;
        }
        if (AnnotationProcessorUtils.LIST_CLASSNAMES.contains(typeName)) {
            return TypeCategory.LIST;
        }
        if (AnnotationProcessorUtils.MAP_CLASSNAMES.contains(typeName)) {
            return TypeCategory.MAP;
        }
        return TypeCategory.ATTRIBUTE;
    }
    
    public static List<? extends Element> getFieldMembers(final TypeElement el) {
        final List<? extends Element> members = el.getEnclosedElements();
        final List<Element> fieldMembers = new ArrayList<Element>();
        for (final Element member : members) {
            if (member.getKind() == ElementKind.FIELD) {
                fieldMembers.add(member);
            }
        }
        return fieldMembers;
    }
    
    public static List<? extends Element> getPropertyMembers(final TypeElement el) {
        final List<? extends Element> members = el.getEnclosedElements();
        final List<Element> propertyMembers = new ArrayList<Element>();
        for (final Element member : members) {
            if (member.getKind() == ElementKind.METHOD) {
                final ExecutableElement method = (ExecutableElement)member;
                if (!isJavaBeanGetter(method) && !isJavaBeanSetter(method)) {
                    continue;
                }
                propertyMembers.add(member);
            }
        }
        return propertyMembers;
    }
    
    public static boolean isJavaBeanGetter(final ExecutableElement method) {
        final String methodName = method.getSimpleName().toString();
        if (method.getKind() == ElementKind.METHOD && method.getParameters().isEmpty()) {
            if (returnsBoolean(method) && methodName.startsWith("is")) {
                return true;
            }
            if (methodName.startsWith("get") && !returnsVoid(method)) {
                return true;
            }
        }
        return false;
    }
    
    public static String getMemberName(final Element el) {
        if (el.getKind() == ElementKind.FIELD) {
            return el.toString();
        }
        if (el.getKind() == ElementKind.METHOD) {
            final ExecutableElement method = (ExecutableElement)el;
            if (isJavaBeanGetter(method) || isJavaBeanSetter(method)) {
                String name = method.getSimpleName().toString();
                if (name.startsWith("is")) {
                    name = name.substring(2);
                }
                else {
                    name = name.substring(3);
                }
                return Character.toLowerCase(name.charAt(0)) + name.substring(1);
            }
        }
        return null;
    }
    
    public static boolean isJavaBeanSetter(final ExecutableElement method) {
        final String methodName = method.getSimpleName().toString();
        return method.getKind() == ElementKind.METHOD && methodName.startsWith("set") && method.getParameters().isEmpty() && !returnsVoid(method);
    }
    
    public static boolean isMethod(final Element elem) {
        return elem != null && ExecutableElement.class.isInstance(elem) && elem.getKind() == ElementKind.METHOD;
    }
    
    public static TypeMirror getDeclaredType(final Element elem) {
        if (elem.getKind() == ElementKind.FIELD) {
            return elem.asType();
        }
        if (elem.getKind() == ElementKind.METHOD) {
            return ((ExecutableElement)elem).getReturnType();
        }
        throw new IllegalArgumentException("Unable to get type for " + elem);
    }
    
    public static Object getValueForAnnotationAttribute(final Element elem, final Class annotCls, final String attribute) {
        final List<? extends AnnotationMirror> anns = elem.getAnnotationMirrors();
        for (final AnnotationMirror ann : anns) {
            if (ann.getAnnotationType().toString().equals(annotCls.getName())) {
                final Map<? extends ExecutableElement, ? extends AnnotationValue> values = ann.getElementValues();
                for (final Map.Entry entry : values.entrySet()) {
                    final ExecutableElement ex = entry.getKey();
                    if (ex.getSimpleName().toString().equals(attribute)) {
                        return entry.getValue().getValue();
                    }
                }
            }
        }
        return null;
    }
    
    public static boolean returnsVoid(final ExecutableElement method) {
        final TypeMirror type = method.getReturnType();
        return type != null && type.getKind() == TypeKind.VOID;
    }
    
    public static boolean returnsBoolean(final ExecutableElement method) {
        final TypeMirror type = method.getReturnType();
        return type != null && (type.getKind() == TypeKind.BOOLEAN || "java.lang.Boolean".equals(type.toString()));
    }
    
    public static boolean typeIsPrimitive(final TypeMirror type) {
        final TypeKind kind = type.getKind();
        return kind == TypeKind.BOOLEAN || kind == TypeKind.BYTE || kind == TypeKind.CHAR || kind == TypeKind.DOUBLE || kind == TypeKind.FLOAT || kind == TypeKind.INT || kind == TypeKind.LONG || kind == TypeKind.SHORT;
    }
    
    public static String getDeclaredTypeName(final ProcessingEnvironment processingEnv, TypeMirror type, final boolean box) {
        if (type == null || type.getKind() == TypeKind.NULL || type.getKind() == TypeKind.WILDCARD) {
            return "java.lang.Object";
        }
        if (type.getKind() == TypeKind.ARRAY) {
            final TypeMirror comp = ((ArrayType)type).getComponentType();
            return getDeclaredTypeName(processingEnv, comp, false);
        }
        if (box && typeIsPrimitive(type)) {
            type = processingEnv.getTypeUtils().boxedClass((PrimitiveType)type).asType();
        }
        if (typeIsPrimitive(type)) {
            return ((PrimitiveType)type).toString();
        }
        return processingEnv.getTypeUtils().asElement(type).toString();
    }
    
    static {
        AnnotationProcessorUtils.LIST_CLASSNAMES = null;
        AnnotationProcessorUtils.SET_CLASSNAMES = null;
        AnnotationProcessorUtils.MAP_CLASSNAMES = null;
        AnnotationProcessorUtils.COLLECTION_CLASSNAMES = null;
        (AnnotationProcessorUtils.LIST_CLASSNAMES = new HashSet<String>()).add("java.util.List");
        AnnotationProcessorUtils.LIST_CLASSNAMES.add("java.util.ArrayList");
        AnnotationProcessorUtils.LIST_CLASSNAMES.add("java.util.AbstractList");
        AnnotationProcessorUtils.LIST_CLASSNAMES.add("java.util.Stack");
        AnnotationProcessorUtils.LIST_CLASSNAMES.add("java.util.Vector");
        AnnotationProcessorUtils.LIST_CLASSNAMES.add("java.util.LinkedList");
        (AnnotationProcessorUtils.SET_CLASSNAMES = new HashSet<String>()).add("java.util.Set");
        AnnotationProcessorUtils.SET_CLASSNAMES.add("java.util.HashSet");
        AnnotationProcessorUtils.SET_CLASSNAMES.add("java.util.AbstractSet");
        AnnotationProcessorUtils.SET_CLASSNAMES.add("java.util.LinkedHashSet");
        AnnotationProcessorUtils.SET_CLASSNAMES.add("java.util.TreeSet");
        AnnotationProcessorUtils.SET_CLASSNAMES.add("java.util.SortedSet");
        (AnnotationProcessorUtils.MAP_CLASSNAMES = new HashSet<String>()).add("java.util.Map");
        AnnotationProcessorUtils.MAP_CLASSNAMES.add("java.util.HashMap");
        AnnotationProcessorUtils.MAP_CLASSNAMES.add("java.util.AbstractMap");
        AnnotationProcessorUtils.MAP_CLASSNAMES.add("java.util.Hashtable");
        AnnotationProcessorUtils.MAP_CLASSNAMES.add("java.util.LinkedHashMap");
        AnnotationProcessorUtils.SET_CLASSNAMES.add("java.util.TreeMap");
        AnnotationProcessorUtils.MAP_CLASSNAMES.add("java.util.SortedMap");
        AnnotationProcessorUtils.MAP_CLASSNAMES.add("java.util.Properties");
        (AnnotationProcessorUtils.COLLECTION_CLASSNAMES = new HashSet<String>()).add("java.util.Collection");
        AnnotationProcessorUtils.COLLECTION_CLASSNAMES.add("java.util.AbstractCollection");
        AnnotationProcessorUtils.COLLECTION_CLASSNAMES.add("java.util.Queue");
        AnnotationProcessorUtils.COLLECTION_CLASSNAMES.add("java.util.PriorityQueue");
    }
    
    public enum TypeCategory
    {
        COLLECTION("CollectionAttribute"), 
        SET("SetAttribute"), 
        LIST("ListAttribute"), 
        MAP("MapAttribute"), 
        ATTRIBUTE("SingularAttribute");
        
        private String type;
        
        private TypeCategory(final String type) {
            this.type = type;
        }
        
        public String getTypeName() {
            return this.type;
        }
    }
}
