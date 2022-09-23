// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.jar.JarEntry;
import java.net.URI;
import org.eclipse.jetty.webapp.JarScanner;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.net.URL;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.Loader;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;

public class AnnotationParser
{
    private static final Logger LOG;
    protected List<String> _parsedClassNames;
    protected Map<String, List<DiscoverableAnnotationHandler>> _annotationHandlers;
    protected List<ClassHandler> _classHandlers;
    protected List<MethodHandler> _methodHandlers;
    protected List<FieldHandler> _fieldHandlers;
    
    public AnnotationParser() {
        this._parsedClassNames = new ArrayList<String>();
        this._annotationHandlers = new HashMap<String, List<DiscoverableAnnotationHandler>>();
        this._classHandlers = new ArrayList<ClassHandler>();
        this._methodHandlers = new ArrayList<MethodHandler>();
        this._fieldHandlers = new ArrayList<FieldHandler>();
    }
    
    public static String normalize(String name) {
        if (name == null) {
            return null;
        }
        if (name.startsWith("L") && name.endsWith(";")) {
            name = name.substring(1, name.length() - 1);
        }
        if (name.endsWith(".class")) {
            name = name.substring(0, name.length() - ".class".length());
        }
        return name.replace('/', '.');
    }
    
    public void registerAnnotationHandler(final String annotationName, final DiscoverableAnnotationHandler handler) {
        List<DiscoverableAnnotationHandler> handlers = this._annotationHandlers.get(annotationName);
        if (handlers == null) {
            handlers = new ArrayList<DiscoverableAnnotationHandler>();
            this._annotationHandlers.put(annotationName, handlers);
        }
        handlers.add(handler);
    }
    
    public List<DiscoverableAnnotationHandler> getAnnotationHandlers(final String annotationName) {
        final List<DiscoverableAnnotationHandler> handlers = this._annotationHandlers.get(annotationName);
        if (handlers == null) {
            return Collections.emptyList();
        }
        return new ArrayList<DiscoverableAnnotationHandler>();
    }
    
    public List<DiscoverableAnnotationHandler> getAnnotationHandlers() {
        final List<DiscoverableAnnotationHandler> allHandlers = new ArrayList<DiscoverableAnnotationHandler>();
        for (final List<DiscoverableAnnotationHandler> list : this._annotationHandlers.values()) {
            allHandlers.addAll(list);
        }
        return allHandlers;
    }
    
    public void registerClassHandler(final ClassHandler handler) {
        this._classHandlers.add(handler);
    }
    
    public boolean isParsed(final String className) {
        return this._parsedClassNames.contains(className);
    }
    
    public void parse(String className, final ClassNameResolver resolver) throws Exception {
        if (className == null) {
            return;
        }
        if (!resolver.isExcluded(className) && (!this.isParsed(className) || resolver.shouldOverride(className))) {
            className = className.replace('.', '/') + ".class";
            final URL resource = Loader.getResource((Class)this.getClass(), className, false);
            if (resource != null) {
                final Resource r = Resource.newResource(resource);
                this.scanClass(r.getInputStream());
            }
        }
    }
    
    public void parse(final Class clazz, final ClassNameResolver resolver, final boolean visitSuperClasses) throws Exception {
        Class cz = clazz;
        while (cz != null) {
            if (!resolver.isExcluded(cz.getName()) && (!this.isParsed(cz.getName()) || resolver.shouldOverride(cz.getName()))) {
                final String nameAsResource = cz.getName().replace('.', '/') + ".class";
                final URL resource = Loader.getResource((Class)this.getClass(), nameAsResource, false);
                if (resource != null) {
                    final Resource r = Resource.newResource(resource);
                    this.scanClass(r.getInputStream());
                }
            }
            if (visitSuperClasses) {
                cz = cz.getSuperclass();
            }
            else {
                cz = null;
            }
        }
    }
    
    public void parse(final String[] classNames, final ClassNameResolver resolver) throws Exception {
        if (classNames == null) {
            return;
        }
        this.parse(Arrays.asList(classNames), resolver);
    }
    
    public void parse(final List<String> classNames, final ClassNameResolver resolver) throws Exception {
        for (String s : classNames) {
            if (resolver == null || (!resolver.isExcluded(s) && (!this.isParsed(s) || resolver.shouldOverride(s)))) {
                s = s.replace('.', '/') + ".class";
                final URL resource = Loader.getResource((Class)this.getClass(), s, false);
                if (resource == null) {
                    continue;
                }
                final Resource r = Resource.newResource(resource);
                this.scanClass(r.getInputStream());
            }
        }
    }
    
    public void parse(final Resource dir, final ClassNameResolver resolver) throws Exception {
        if (!dir.isDirectory() || !dir.exists()) {
            return;
        }
        final String[] files = dir.list();
        for (int f = 0; files != null && f < files.length; ++f) {
            try {
                final Resource res = dir.addPath(files[f]);
                if (res.isDirectory()) {
                    this.parse(res, resolver);
                }
                final String name = res.getName();
                if (name.endsWith(".class") && (resolver == null || (!resolver.isExcluded(name) && (!this.isParsed(name) || resolver.shouldOverride(name))))) {
                    final Resource r = Resource.newResource(res.getURL());
                    this.scanClass(r.getInputStream());
                }
            }
            catch (Exception ex) {
                AnnotationParser.LOG.warn("EXCEPTION ", ex);
            }
        }
    }
    
    public void parse(final ClassLoader loader, final boolean visitParents, final boolean nullInclusive, final ClassNameResolver resolver) throws Exception {
        if (loader == null) {
            return;
        }
        if (!(loader instanceof URLClassLoader)) {
            return;
        }
        final JarScanner scanner = new JarScanner() {
            @Override
            public void processEntry(final URI jarUri, final JarEntry entry) {
                try {
                    final String name = entry.getName();
                    if (name.toLowerCase().endsWith(".class")) {
                        final String shortName = name.replace('/', '.').substring(0, name.length() - 6);
                        if (resolver == null || (!resolver.isExcluded(shortName) && (!AnnotationParser.this.isParsed(shortName) || resolver.shouldOverride(shortName)))) {
                            final Resource clazz = Resource.newResource("jar:" + jarUri + "!/" + name);
                            AnnotationParser.this.scanClass(clazz.getInputStream());
                        }
                    }
                }
                catch (Exception e) {
                    AnnotationParser.LOG.warn("Problem processing jar entry " + entry, e);
                }
            }
        };
        scanner.scan(null, loader, nullInclusive, visitParents);
    }
    
    public void parse(final URI[] uris, final ClassNameResolver resolver) throws Exception {
        if (uris == null) {
            return;
        }
        final JarScanner scanner = new JarScanner() {
            @Override
            public void processEntry(final URI jarUri, final JarEntry entry) {
                try {
                    final String name = entry.getName();
                    if (name.toLowerCase().endsWith(".class")) {
                        final String shortName = name.replace('/', '.').substring(0, name.length() - 6);
                        if (resolver == null || (!resolver.isExcluded(shortName) && (!AnnotationParser.this.isParsed(shortName) || resolver.shouldOverride(shortName)))) {
                            final Resource clazz = Resource.newResource("jar:" + jarUri + "!/" + name);
                            AnnotationParser.this.scanClass(clazz.getInputStream());
                        }
                    }
                }
                catch (Exception e) {
                    AnnotationParser.LOG.warn("Problem processing jar entry " + entry, e);
                }
            }
        };
        scanner.scan(null, uris, true);
    }
    
    public void parse(final URI uri, final ClassNameResolver resolver) throws Exception {
        if (uri == null) {
            return;
        }
        final URI[] uris = { uri };
        this.parse(uris, resolver);
    }
    
    private void scanClass(final InputStream is) throws IOException {
        final ClassReader reader = new ClassReader(is);
        reader.accept((ClassVisitor)new MyClassVisitor(), 7);
    }
    
    static {
        LOG = Log.getLogger(AnnotationParser.class);
    }
    
    public abstract class Value
    {
        String _name;
        
        public Value(final String name) {
            this._name = name;
        }
        
        public String getName() {
            return this._name;
        }
        
        public abstract Object getValue();
    }
    
    public class SimpleValue extends Value
    {
        Object _val;
        
        public SimpleValue(final String name) {
            super(name);
        }
        
        public void setValue(final Object val) {
            this._val = val;
        }
        
        @Override
        public Object getValue() {
            return this._val;
        }
        
        @Override
        public String toString() {
            return "(" + this.getName() + ":" + this._val + ")";
        }
    }
    
    public class ListValue extends Value
    {
        List<Value> _val;
        
        public ListValue(final String name) {
            super(name);
            this._val = new ArrayList<Value>();
        }
        
        @Override
        public Object getValue() {
            return this._val;
        }
        
        public List<Value> getList() {
            return this._val;
        }
        
        public void addValue(final Value v) {
            this._val.add(v);
        }
        
        public int size() {
            return this._val.size();
        }
        
        @Override
        public String toString() {
            final StringBuffer buff = new StringBuffer();
            buff.append("(");
            buff.append(this.getName());
            buff.append(":");
            for (final Value n : this._val) {
                buff.append(" " + n.toString());
            }
            buff.append(")");
            return buff.toString();
        }
    }
    
    public class MyAnnotationVisitor implements AnnotationVisitor
    {
        List<Value> _annotationValues;
        String _annotationName;
        
        public MyAnnotationVisitor(final String annotationName, final List<Value> values) {
            this._annotationValues = values;
            this._annotationName = annotationName;
        }
        
        public List<Value> getAnnotationValues() {
            return this._annotationValues;
        }
        
        @Override
        public void visit(final String aname, final Object avalue) {
            final SimpleValue v = new SimpleValue(aname);
            v.setValue(avalue);
            this._annotationValues.add(v);
        }
        
        @Override
        public AnnotationVisitor visitAnnotation(final String name, final String desc) {
            final String s = AnnotationParser.normalize(desc);
            final ListValue v = new ListValue(s);
            this._annotationValues.add(v);
            final MyAnnotationVisitor visitor = new MyAnnotationVisitor(s, v.getList());
            return (AnnotationVisitor)visitor;
        }
        
        @Override
        public AnnotationVisitor visitArray(final String name) {
            final ListValue v = new ListValue(name);
            this._annotationValues.add(v);
            final MyAnnotationVisitor visitor = new MyAnnotationVisitor(null, v.getList());
            return (AnnotationVisitor)visitor;
        }
        
        @Override
        public void visitEnum(final String name, final String desc, final String value) {
        }
        
        @Override
        public void visitEnd() {
        }
    }
    
    public class MyClassVisitor extends EmptyVisitor
    {
        String _className;
        int _access;
        String _signature;
        String _superName;
        String[] _interfaces;
        int _version;
        
        @Override
        public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
            this._className = AnnotationParser.normalize(name);
            this._access = access;
            this._signature = signature;
            this._superName = superName;
            this._interfaces = interfaces;
            this._version = version;
            AnnotationParser.this._parsedClassNames.add(this._className);
            String[] normalizedInterfaces = null;
            if (interfaces != null) {
                normalizedInterfaces = new String[interfaces.length];
                int i = 0;
                for (final String s : interfaces) {
                    normalizedInterfaces[i++] = AnnotationParser.normalize(s);
                }
            }
            for (final ClassHandler h : AnnotationParser.this._classHandlers) {
                h.handle(this._className, this._version, this._access, this._signature, AnnotationParser.normalize(this._superName), normalizedInterfaces);
            }
        }
        
        @Override
        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            final MyAnnotationVisitor visitor = new MyAnnotationVisitor(AnnotationParser.normalize(desc), new ArrayList()) {
                @Override
                public void visitEnd() {
                    super.visitEnd();
                    final List<DiscoverableAnnotationHandler> handlers = AnnotationParser.this._annotationHandlers.get(this._annotationName);
                    if (handlers != null) {
                        for (final DiscoverableAnnotationHandler h : handlers) {
                            h.handleClass(MyClassVisitor.this._className, MyClassVisitor.this._version, MyClassVisitor.this._access, MyClassVisitor.this._signature, MyClassVisitor.this._superName, MyClassVisitor.this._interfaces, this._annotationName, this._annotationValues);
                        }
                    }
                }
            };
            return (AnnotationVisitor)visitor;
        }
        
        @Override
        public MethodVisitor visitMethod(final int access, final String name, final String methodDesc, final String signature, final String[] exceptions) {
            return (MethodVisitor)new EmptyVisitor() {
                @Override
                public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                    final MyAnnotationVisitor visitor = new MyAnnotationVisitor(AnnotationParser.normalize(desc), new ArrayList()) {
                        @Override
                        public void visitEnd() {
                            super.visitEnd();
                            final List<DiscoverableAnnotationHandler> handlers = AnnotationParser.this._annotationHandlers.get(this._annotationName);
                            if (handlers != null) {
                                for (final DiscoverableAnnotationHandler h : handlers) {
                                    h.handleMethod(MyClassVisitor.this._className, name, access, methodDesc, signature, exceptions, this._annotationName, this._annotationValues);
                                }
                            }
                        }
                    };
                    return (AnnotationVisitor)visitor;
                }
            };
        }
        
        @Override
        public FieldVisitor visitField(final int access, final String fieldName, final String fieldType, final String signature, final Object value) {
            return (FieldVisitor)new EmptyVisitor() {
                @Override
                public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                    final MyAnnotationVisitor visitor = new MyAnnotationVisitor(AnnotationParser.normalize(desc), new ArrayList()) {
                        @Override
                        public void visitEnd() {
                            super.visitEnd();
                            final List<DiscoverableAnnotationHandler> handlers = AnnotationParser.this._annotationHandlers.get(this._annotationName);
                            if (handlers != null) {
                                for (final DiscoverableAnnotationHandler h : handlers) {
                                    h.handleField(MyClassVisitor.this._className, fieldName, access, fieldType, signature, value, this._annotationName, this._annotationValues);
                                }
                            }
                        }
                    };
                    return (AnnotationVisitor)visitor;
                }
            };
        }
    }
    
    public interface ClassHandler
    {
        void handle(final String p0, final int p1, final int p2, final String p3, final String p4, final String[] p5);
    }
    
    public interface DiscoverableAnnotationHandler
    {
        void handleClass(final String p0, final int p1, final int p2, final String p3, final String p4, final String[] p5, final String p6, final List<Value> p7);
        
        void handleMethod(final String p0, final String p1, final int p2, final String p3, final String p4, final String[] p5, final String p6, final List<Value> p7);
        
        void handleField(final String p0, final String p1, final int p2, final String p3, final String p4, final Object p5, final String p6, final List<Value> p7);
    }
    
    public interface FieldHandler
    {
        void handle(final String p0, final String p1, final int p2, final String p3, final String p4, final Object p5);
    }
    
    public interface MethodHandler
    {
        void handle(final String p0, final String p1, final int p2, final String p3, final String p4, final String[] p5);
    }
}
