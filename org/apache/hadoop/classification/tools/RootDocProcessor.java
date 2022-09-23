// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.classification.tools;

import java.util.List;
import java.util.ArrayList;
import com.sun.javadoc.AnnotationDesc;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import com.sun.javadoc.ProgramElementDoc;
import java.lang.reflect.InvocationTargetException;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import java.lang.reflect.Method;
import java.util.WeakHashMap;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Array;
import com.sun.javadoc.RootDoc;
import java.util.Map;

class RootDocProcessor
{
    static String stability;
    static boolean treatUnannotatedClassesAsPrivate;
    private static Map<Object, Object> proxies;
    
    public static RootDoc process(final RootDoc root) {
        return (RootDoc)process(root, RootDoc.class);
    }
    
    private static Object process(final Object obj, final Class<?> type) {
        if (obj == null) {
            return null;
        }
        final Class<?> cls = obj.getClass();
        if (cls.getName().startsWith("com.sun.")) {
            return getProxy(obj);
        }
        if (obj instanceof Object[]) {
            final Class<?> componentType = type.isArray() ? type.getComponentType() : cls.getComponentType();
            final Object[] array = (Object[])obj;
            final Object[] newArray = (Object[])Array.newInstance(componentType, array.length);
            for (int i = 0; i < array.length; ++i) {
                newArray[i] = process(array[i], componentType);
            }
            return newArray;
        }
        return obj;
    }
    
    private static Object getProxy(final Object obj) {
        Object proxy = RootDocProcessor.proxies.get(obj);
        if (proxy == null) {
            proxy = Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new ExcludeHandler(obj));
            RootDocProcessor.proxies.put(obj, proxy);
        }
        return proxy;
    }
    
    static {
        RootDocProcessor.stability = "-unstable";
        RootDocProcessor.treatUnannotatedClassesAsPrivate = false;
        RootDocProcessor.proxies = new WeakHashMap<Object, Object>();
    }
    
    private static class ExcludeHandler implements InvocationHandler
    {
        private Object target;
        
        public ExcludeHandler(final Object target) {
            this.target = target;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final String methodName = method.getName();
            if (this.target instanceof Doc) {
                if (methodName.equals("isIncluded")) {
                    final Doc doc = (Doc)this.target;
                    return !exclude(doc) && doc.isIncluded();
                }
                if (this.target instanceof RootDoc) {
                    if (methodName.equals("classes")) {
                        return filter(((RootDoc)this.target).classes(), ClassDoc.class);
                    }
                    if (methodName.equals("specifiedClasses")) {
                        return filter(((RootDoc)this.target).specifiedClasses(), ClassDoc.class);
                    }
                    if (methodName.equals("specifiedPackages")) {
                        return filter(((RootDoc)this.target).specifiedPackages(), PackageDoc.class);
                    }
                }
                else if (this.target instanceof ClassDoc) {
                    if (this.isFiltered(args)) {
                        if (methodName.equals("methods")) {
                            return filter(((ClassDoc)this.target).methods(true), MethodDoc.class);
                        }
                        if (methodName.equals("fields")) {
                            return filter(((ClassDoc)this.target).fields(true), FieldDoc.class);
                        }
                        if (methodName.equals("innerClasses")) {
                            return filter(((ClassDoc)this.target).innerClasses(true), ClassDoc.class);
                        }
                        if (methodName.equals("constructors")) {
                            return filter(((ClassDoc)this.target).constructors(true), ConstructorDoc.class);
                        }
                    }
                    else if (methodName.equals("methods")) {
                        return filter(((ClassDoc)this.target).methods(true), MethodDoc.class);
                    }
                }
                else if (this.target instanceof PackageDoc) {
                    if (methodName.equals("allClasses")) {
                        if (this.isFiltered(args)) {
                            return filter(((PackageDoc)this.target).allClasses(true), ClassDoc.class);
                        }
                        return filter(((PackageDoc)this.target).allClasses(), ClassDoc.class);
                    }
                    else {
                        if (methodName.equals("annotationTypes")) {
                            return filter(((PackageDoc)this.target).annotationTypes(), AnnotationTypeDoc.class);
                        }
                        if (methodName.equals("enums")) {
                            return filter(((PackageDoc)this.target).enums(), ClassDoc.class);
                        }
                        if (methodName.equals("errors")) {
                            return filter(((PackageDoc)this.target).errors(), ClassDoc.class);
                        }
                        if (methodName.equals("exceptions")) {
                            return filter(((PackageDoc)this.target).exceptions(), ClassDoc.class);
                        }
                        if (methodName.equals("interfaces")) {
                            return filter(((PackageDoc)this.target).interfaces(), ClassDoc.class);
                        }
                        if (methodName.equals("ordinaryClasses")) {
                            return filter(((PackageDoc)this.target).ordinaryClasses(), ClassDoc.class);
                        }
                    }
                }
            }
            if (args != null && (methodName.equals("compareTo") || methodName.equals("equals") || methodName.equals("overrides") || methodName.equals("subclassOf"))) {
                args[0] = this.unwrap(args[0]);
            }
            try {
                return process(method.invoke(this.target, args), method.getReturnType());
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        
        private static boolean exclude(final Doc doc) {
            AnnotationDesc[] annotations = null;
            if (doc instanceof ProgramElementDoc) {
                annotations = ((ProgramElementDoc)doc).annotations();
            }
            else if (doc instanceof PackageDoc) {
                annotations = ((PackageDoc)doc).annotations();
            }
            if (annotations != null) {
                for (final AnnotationDesc annotation : annotations) {
                    final String qualifiedTypeName = annotation.annotationType().qualifiedTypeName();
                    if (qualifiedTypeName.equals(InterfaceAudience.Private.class.getCanonicalName()) || qualifiedTypeName.equals(InterfaceAudience.LimitedPrivate.class.getCanonicalName())) {
                        return true;
                    }
                    if (RootDocProcessor.stability.equals("-evolving") && qualifiedTypeName.equals(InterfaceStability.Unstable.class.getCanonicalName())) {
                        return true;
                    }
                    if (RootDocProcessor.stability.equals("-stable") && (qualifiedTypeName.equals(InterfaceStability.Unstable.class.getCanonicalName()) || qualifiedTypeName.equals(InterfaceStability.Evolving.class.getCanonicalName()))) {
                        return true;
                    }
                }
                for (final AnnotationDesc annotation : annotations) {
                    final String qualifiedTypeName = annotation.annotationType().qualifiedTypeName();
                    if (qualifiedTypeName.equals(InterfaceAudience.Public.class.getCanonicalName())) {
                        return false;
                    }
                }
            }
            return RootDocProcessor.treatUnannotatedClassesAsPrivate && (doc.isClass() || doc.isInterface() || doc.isAnnotationType());
        }
        
        private static Object[] filter(final Doc[] array, final Class<?> componentType) {
            if (array == null || array.length == 0) {
                return array;
            }
            final List<Object> list = new ArrayList<Object>(array.length);
            for (final Doc entry : array) {
                if (!exclude(entry)) {
                    list.add(process(entry, componentType));
                }
            }
            return list.toArray((Object[])Array.newInstance(componentType, list.size()));
        }
        
        private Object unwrap(final Object proxy) {
            if (proxy instanceof Proxy) {
                return ((ExcludeHandler)Proxy.getInvocationHandler(proxy)).target;
            }
            return proxy;
        }
        
        private boolean isFiltered(final Object[] args) {
            return args != null && Boolean.TRUE.equals(args[0]);
        }
    }
}
