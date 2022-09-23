// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.lang.reflect.Modifier;
import org.datanucleus.exceptions.NucleusUserException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.security.PrivilegedAction;
import org.datanucleus.exceptions.ClassNotResolvedException;
import java.lang.annotation.Annotation;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ClassConstants;
import org.datanucleus.ClassNameConstants;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.HashSet;
import java.util.Collection;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.datanucleus.exceptions.NucleusException;
import java.util.Arrays;
import java.lang.reflect.Constructor;
import java.util.Map;

public class ClassUtils
{
    protected static final Localiser LOCALISER;
    protected static final Map constructorsCache;
    
    public static Object newInstance(final Class type, final Class[] parameterTypes, final Object[] parameters) {
        Object obj;
        try {
            final StringBuilder name = new StringBuilder("" + type.hashCode());
            if (parameterTypes != null) {
                for (int i = 0; i < parameterTypes.length; ++i) {
                    name.append("-").append(parameterTypes[i].hashCode());
                }
            }
            Constructor ctor = ClassUtils.constructorsCache.get(name.toString());
            if (ctor == null) {
                ctor = type.getConstructor((Class[])parameterTypes);
                ClassUtils.constructorsCache.put(name.toString(), ctor);
            }
            obj = ctor.newInstance(parameters);
        }
        catch (NoSuchMethodException e) {
            throw new NucleusException(ClassUtils.LOCALISER.msg("030004", type.getName(), Arrays.asList((Class[])parameterTypes).toString() + " " + Arrays.asList(type.getConstructors()).toString()), new Exception[] { e }).setFatal();
        }
        catch (IllegalAccessException e2) {
            throw new NucleusException(ClassUtils.LOCALISER.msg("030005", type.getName()), new Exception[] { e2 }).setFatal();
        }
        catch (InstantiationException e3) {
            throw new NucleusException(ClassUtils.LOCALISER.msg("030006", type.getName()), new Exception[] { e3 }).setFatal();
        }
        catch (InvocationTargetException e4) {
            final Throwable t = e4.getTargetException();
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new NucleusException(ClassUtils.LOCALISER.msg("030007", type.getName(), t)).setFatal();
        }
        return obj;
    }
    
    public static Constructor getConstructorWithArguments(final Class cls, final Class[] argTypes) {
        try {
            final Constructor[] constructors = cls.getConstructors();
            if (constructors != null) {
                for (int i = 0; i < constructors.length; ++i) {
                    final Class[] ctrParams = constructors[i].getParameterTypes();
                    boolean ctrIsValid = true;
                    if (ctrParams != null && ctrParams.length == argTypes.length) {
                        for (int j = 0; j < ctrParams.length; ++j) {
                            final Class primType = getPrimitiveTypeForType(argTypes[j]);
                            if (argTypes[j] == null && ctrParams[j].isPrimitive()) {
                                ctrIsValid = false;
                                break;
                            }
                            if (argTypes[j] != null && !ctrParams[j].isAssignableFrom(argTypes[j]) && (primType == null || ctrParams[j] != primType)) {
                                ctrIsValid = false;
                                break;
                            }
                        }
                    }
                    else {
                        ctrIsValid = false;
                    }
                    if (ctrIsValid) {
                        return constructors[i];
                    }
                }
            }
        }
        catch (SecurityException ex) {}
        return null;
    }
    
    public static Method getMethodWithArgument(final Class cls, final String methodName, final Class argType) {
        Method m = getMethodForClass(cls, methodName, new Class[] { argType });
        if (m == null) {
            final Class primitive = getPrimitiveTypeForType(argType);
            if (primitive != null) {
                m = getMethodForClass(cls, methodName, new Class[] { primitive });
            }
        }
        return m;
    }
    
    public static Method getMethodForClass(final Class cls, final String methodName, final Class[] argtypes) {
        try {
            return cls.getDeclaredMethod(methodName, (Class[])argtypes);
        }
        catch (NoSuchMethodException e) {
            if (cls.getSuperclass() != null) {
                return getMethodForClass(cls.getSuperclass(), methodName, argtypes);
            }
        }
        catch (Exception ex) {}
        return null;
    }
    
    public static String getClassnameForFilename(final String filename, final String rootfilename) {
        if (filename == null) {
            return null;
        }
        String classname = filename;
        if (rootfilename != null) {
            classname = classname.substring(rootfilename.length());
        }
        classname = classname.substring(0, classname.length() - 6);
        final String file_separator = System.getProperty("file.separator");
        if (classname.indexOf(file_separator) == 0) {
            classname = classname.substring(file_separator.length());
        }
        classname = classname.replace(file_separator, ".");
        return classname;
    }
    
    public static Collection<File> getClassFilesForDirectory(final File dir, final boolean normal_classes, final boolean inner_classes) {
        if (dir == null) {
            return null;
        }
        final Collection classes = new HashSet();
        final File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isFile()) {
                    if (files[i].getName().endsWith(".class")) {
                        final boolean is_inner_class = isInnerClass(files[i].getName());
                        if ((normal_classes && !is_inner_class) || (inner_classes && is_inner_class)) {
                            classes.add(files[i]);
                        }
                    }
                }
                else {
                    final Collection child_classes = getClassFilesForDirectory(files[i], normal_classes, inner_classes);
                    if (child_classes != null && child_classes.size() > 0) {
                        classes.addAll(child_classes);
                    }
                }
            }
        }
        return (Collection<File>)classes;
    }
    
    public static Collection<File> getFilesForDirectory(final File dir) {
        if (dir == null) {
            return null;
        }
        final Collection files = new HashSet();
        final File[] dirFiles = dir.listFiles();
        if (dirFiles != null) {
            for (int i = 0; i < dirFiles.length; ++i) {
                if (dirFiles[i].isFile()) {
                    files.add(dirFiles[i]);
                }
                else {
                    final Collection childFiles = getFilesForDirectory(dirFiles[i]);
                    if (childFiles != null && childFiles.size() > 0) {
                        files.addAll(childFiles);
                    }
                }
            }
        }
        return (Collection<File>)files;
    }
    
    public static String[] getClassNamesForJarFile(final String jarFileName) {
        try {
            final JarFile jar = new JarFile(jarFileName);
            return getClassNamesForJarFile(jar);
        }
        catch (IOException ioe) {
            NucleusLogger.GENERAL.warn("Error opening the jar file " + jarFileName + " : " + ioe.getMessage());
            return null;
        }
    }
    
    public static String[] getClassNamesForJarFile(final URL jarFileURL) {
        final File jarFile = new File(jarFileURL.getFile());
        try {
            final JarFile jar = new JarFile(jarFile);
            return getClassNamesForJarFile(jar);
        }
        catch (IOException ioe) {
            NucleusLogger.GENERAL.warn("Error opening the jar file " + jarFileURL.getFile() + " : " + ioe.getMessage());
            return null;
        }
    }
    
    public static String[] getClassNamesForJarFile(final URI jarFileURI) {
        try {
            return getClassNamesForJarFile(jarFileURI.toURL());
        }
        catch (MalformedURLException mue) {
            throw new NucleusException("Error opening the jar file " + jarFileURI, mue);
        }
    }
    
    private static String[] getClassNamesForJarFile(final JarFile jar) {
        final Enumeration jarEntries = jar.entries();
        final HashSet classes = new HashSet();
        final String file_separator = System.getProperty("file.separator");
        while (jarEntries.hasMoreElements()) {
            final String entry = jarEntries.nextElement().getName();
            if (entry.endsWith(".class") && !isInnerClass(entry)) {
                String className = entry.substring(0, entry.length() - 6);
                className = className.replace(file_separator, ".");
                classes.add(className);
            }
        }
        return (String[])classes.toArray(new String[classes.size()]);
    }
    
    public static String[] getPackageJdoFilesForJarFile(final String jarFileName) {
        try {
            final JarFile jar = new JarFile(jarFileName);
            return getFileNamesWithSuffixForJarFile(jar, "package.jdo");
        }
        catch (IOException ioe) {
            NucleusLogger.GENERAL.warn("Error opening the jar file " + jarFileName + " : " + ioe.getMessage());
            return null;
        }
    }
    
    public static String[] getPackageJdoFilesForJarFile(final URL jarFileURL) {
        final File jarFile = new File(jarFileURL.getFile());
        try {
            final JarFile jar = new JarFile(jarFile);
            return getFileNamesWithSuffixForJarFile(jar, "package.jdo");
        }
        catch (IOException ioe) {
            NucleusLogger.GENERAL.warn("Error opening the jar file " + jarFileURL.getFile() + " : " + ioe.getMessage());
            return null;
        }
    }
    
    public static String[] getPackageJdoFilesForJarFile(final URI jarFileURI) {
        URL jarFileURL = null;
        try {
            jarFileURL = jarFileURI.toURL();
        }
        catch (MalformedURLException mue) {
            throw new NucleusException("JAR file at " + jarFileURI + " not openable. Invalid URL");
        }
        return getPackageJdoFilesForJarFile(jarFileURL);
    }
    
    private static String[] getFileNamesWithSuffixForJarFile(final JarFile jar, final String suffix) {
        final Enumeration jarEntries = jar.entries();
        final HashSet files = new HashSet();
        while (jarEntries.hasMoreElements()) {
            final String entry = jarEntries.nextElement().getName();
            if (entry.endsWith(suffix)) {
                files.add(entry);
            }
        }
        return (String[])files.toArray(new String[files.size()]);
    }
    
    public static String[] getClassNamesForDirectoryAndBelow(final File dir) {
        if (dir == null) {
            return null;
        }
        final Collection<File> classFiles = getClassFilesForDirectory(dir, true, false);
        if (classFiles == null || classFiles.isEmpty()) {
            return null;
        }
        final String[] classNames = new String[classFiles.size()];
        final Iterator<File> iter = classFiles.iterator();
        final String file_separator = System.getProperty("file.separator");
        int i = 0;
        while (iter.hasNext()) {
            final String filename = iter.next().getAbsolutePath();
            final String classname = filename.substring(dir.getAbsolutePath().length() + 1, filename.length() - 6);
            classNames[i++] = classname.replace(file_separator, ".");
        }
        return classNames;
    }
    
    public static boolean isInnerClass(final String class_name) {
        return class_name != null && class_name.indexOf(36) >= 0;
    }
    
    public static boolean hasDefaultConstructor(final Class the_class) {
        if (the_class == null) {
            return false;
        }
        try {
            the_class.getDeclaredConstructor((Class[])new Class[0]);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public static Collection<Class<?>> getSuperclasses(final Class<?> the_class) {
        final List<Class<?>> result = new ArrayList<Class<?>>();
        for (Class<?> superclass = the_class.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
            result.add(superclass);
        }
        return result;
    }
    
    public static Collection<Class<?>> getSuperinterfaces(final Class<?> the_class) {
        final List<Class<?>> result = new ArrayList<Class<?>>();
        collectSuperinterfaces(the_class, result);
        return result;
    }
    
    private static void collectSuperinterfaces(final Class<?> c, final List<Class<?>> result) {
        for (final Class<?> i : c.getInterfaces()) {
            if (!result.contains(i)) {
                result.add(i);
                collectSuperinterfaces(i, result);
            }
        }
    }
    
    public static Field getFieldForClass(Class cls, final String fieldName) {
        try {
            try {
                return cls.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException e) {
                cls = cls.getSuperclass();
                if (cls == null) {
                    return null;
                }
                return cls.getDeclaredField(fieldName);
            }
        }
        catch (Exception ex) {}
        return null;
    }
    
    public static Method getGetterMethodForClass(final Class cls, final String beanName) {
        Method getter = findDeclaredMethodInHeirarchy(cls, getJavaBeanGetterName(beanName, false), (Class[])null);
        if (getter == null) {
            getter = findDeclaredMethodInHeirarchy(cls, getJavaBeanGetterName(beanName, true), (Class[])null);
        }
        return getter;
    }
    
    public static Method getSetterMethodForClass(final Class cls, final String beanName, final Class type) {
        return findDeclaredMethodInHeirarchy(cls, getJavaBeanSetterName(beanName), type);
    }
    
    private static Method findDeclaredMethodInHeirarchy(Class cls, final String methodName, final Class... parameterTypes) {
        try {
            try {
                return cls.getDeclaredMethod(methodName, (Class[])parameterTypes);
            }
            catch (NoSuchMethodException e) {
                cls = cls.getSuperclass();
                if (cls == null) {
                    return null;
                }
                return cls.getDeclaredMethod(methodName, (Class[])parameterTypes);
            }
        }
        catch (Exception ex) {}
        return null;
    }
    
    public static String getWrapperTypeNameForPrimitiveTypeName(final String typeName) {
        if (typeName.equals("boolean")) {
            return ClassNameConstants.JAVA_LANG_BOOLEAN;
        }
        if (typeName.equals("byte")) {
            return ClassNameConstants.JAVA_LANG_BYTE;
        }
        if (typeName.equals("char")) {
            return ClassNameConstants.JAVA_LANG_CHARACTER;
        }
        if (typeName.equals("double")) {
            return ClassNameConstants.JAVA_LANG_DOUBLE;
        }
        if (typeName.equals("float")) {
            return ClassNameConstants.JAVA_LANG_FLOAT;
        }
        if (typeName.equals("int")) {
            return ClassNameConstants.JAVA_LANG_INTEGER;
        }
        if (typeName.equals("long")) {
            return ClassNameConstants.JAVA_LANG_LONG;
        }
        if (typeName.equals("short")) {
            return ClassNameConstants.JAVA_LANG_SHORT;
        }
        return typeName;
    }
    
    public static Class getWrapperTypeForPrimitiveType(final Class type) {
        if (type == Boolean.TYPE) {
            return ClassConstants.JAVA_LANG_BOOLEAN;
        }
        if (type == Byte.TYPE) {
            return ClassConstants.JAVA_LANG_BYTE;
        }
        if (type == Character.TYPE) {
            return ClassConstants.JAVA_LANG_CHARACTER;
        }
        if (type == Double.TYPE) {
            return ClassConstants.JAVA_LANG_DOUBLE;
        }
        if (type == Float.TYPE) {
            return ClassConstants.JAVA_LANG_FLOAT;
        }
        if (type == Integer.TYPE) {
            return ClassConstants.JAVA_LANG_INTEGER;
        }
        if (type == Long.TYPE) {
            return ClassConstants.JAVA_LANG_LONG;
        }
        if (type == Short.TYPE) {
            return ClassConstants.JAVA_LANG_SHORT;
        }
        return null;
    }
    
    public static Class getPrimitiveTypeForType(final Class type) {
        if (type == Boolean.class) {
            return ClassConstants.BOOLEAN;
        }
        if (type == Byte.class) {
            return ClassConstants.BYTE;
        }
        if (type == Character.class) {
            return ClassConstants.CHAR;
        }
        if (type == Double.class) {
            return ClassConstants.DOUBLE;
        }
        if (type == Float.class) {
            return ClassConstants.FLOAT;
        }
        if (type == Integer.class) {
            return ClassConstants.INT;
        }
        if (type == Long.class) {
            return ClassConstants.LONG;
        }
        if (type == Short.class) {
            return ClassConstants.SHORT;
        }
        return null;
    }
    
    public static boolean isPrimitiveWrapperType(final String typeName) {
        return typeName.equals(ClassNameConstants.JAVA_LANG_BOOLEAN) || typeName.equals(ClassNameConstants.JAVA_LANG_BYTE) || typeName.equals(ClassNameConstants.JAVA_LANG_CHARACTER) || typeName.equals(ClassNameConstants.JAVA_LANG_DOUBLE) || typeName.equals(ClassNameConstants.JAVA_LANG_FLOAT) || typeName.equals(ClassNameConstants.JAVA_LANG_INTEGER) || typeName.equals(ClassNameConstants.JAVA_LANG_LONG) || typeName.equals(ClassNameConstants.JAVA_LANG_SHORT);
    }
    
    public static boolean isPrimitiveArrayType(final String typeName) {
        return typeName.equals(ClassNameConstants.BOOLEAN_ARRAY) || typeName.equals(ClassNameConstants.BYTE_ARRAY) || typeName.equals(ClassNameConstants.CHAR_ARRAY) || typeName.equals(ClassNameConstants.DOUBLE_ARRAY) || typeName.equals(ClassNameConstants.FLOAT_ARRAY) || typeName.equals(ClassNameConstants.INT_ARRAY) || typeName.equals(ClassNameConstants.LONG_ARRAY) || typeName.equals(ClassNameConstants.SHORT_ARRAY);
    }
    
    public static boolean isPrimitiveType(final String typeName) {
        return typeName.equals(ClassNameConstants.BOOLEAN) || typeName.equals(ClassNameConstants.BYTE) || typeName.equals(ClassNameConstants.CHAR) || typeName.equals(ClassNameConstants.DOUBLE) || typeName.equals(ClassNameConstants.FLOAT) || typeName.equals(ClassNameConstants.INT) || typeName.equals(ClassNameConstants.LONG) || typeName.equals(ClassNameConstants.SHORT);
    }
    
    public static Object convertValue(final Object value, final Class cls) {
        if (value == null) {
            return null;
        }
        Class type = cls;
        if (cls.isPrimitive()) {
            type = getWrapperTypeForPrimitiveType(cls);
        }
        if (type.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (type == Long.class && value instanceof Number) {
            return ((Number)value).longValue();
        }
        if (type == Integer.class && value instanceof Number) {
            return ((Number)value).intValue();
        }
        if (type == Short.class && value instanceof Number) {
            return ((Number)value).shortValue();
        }
        if (type == Float.class && value instanceof Number) {
            return ((Number)value).floatValue();
        }
        if (type == Double.class && value instanceof Number) {
            return ((Number)value).doubleValue();
        }
        return null;
    }
    
    public static boolean typesAreCompatible(final Class cls1, final String clsName2, final ClassLoaderResolver clr) {
        if (clr.isAssignableFrom(cls1, clsName2)) {
            return true;
        }
        if (cls1.isPrimitive()) {
            return clr.isAssignableFrom(getWrapperTypeForPrimitiveType(cls1), clsName2);
        }
        return isPrimitiveWrapperType(cls1.getName()) && clr.isAssignableFrom(getPrimitiveTypeForType(cls1), clsName2);
    }
    
    public static boolean typesAreCompatible(final Class cls1, final Class cls2) {
        return cls1.isAssignableFrom(cls2) || (cls1.isPrimitive() && getWrapperTypeForPrimitiveType(cls1).isAssignableFrom(cls2));
    }
    
    public static String createFullClassName(final String pkg_name, final String cls_name) {
        if (StringUtils.isWhitespace(cls_name)) {
            throw new IllegalArgumentException("Class name not specified");
        }
        if (StringUtils.isWhitespace(pkg_name)) {
            return cls_name;
        }
        if (cls_name.indexOf(46) >= 0) {
            return cls_name;
        }
        return pkg_name + "." + cls_name;
    }
    
    public static String getJavaLangClassForType(final String type) {
        String baseType = null;
        if (type.lastIndexOf(46) < 0) {
            baseType = type;
        }
        else {
            baseType = type.substring(type.lastIndexOf(46) + 1);
        }
        if (baseType.equals("String") || baseType.equals("Object") || baseType.equals("Boolean") || baseType.equals("Byte") || baseType.equals("Character") || baseType.equals("Double") || baseType.equals("Float") || baseType.equals("Integer") || baseType.equals("Long") || baseType.equals("Short") || baseType.equals("Number") || baseType.equals("StringBuffer")) {
            return "java.lang." + baseType;
        }
        return type;
    }
    
    public static boolean classesAreDescendents(final ClassLoaderResolver clr, final String class_name_1, final String class_name_2) {
        final Class class_1 = clr.classForName(class_name_1);
        final Class class_2 = clr.classForName(class_name_2);
        return class_1 != null && class_2 != null && (class_1.isAssignableFrom(class_2) || class_2.isAssignableFrom(class_1));
    }
    
    public static void dumpClassInformation(final Class cls) {
        NucleusLogger.GENERAL.info("----------------------------------------");
        NucleusLogger.GENERAL.info("Class Information for class " + cls.getName());
        for (final Class<?> superclass : getSuperclasses(cls)) {
            NucleusLogger.GENERAL.info("    Superclass : " + superclass.getName());
        }
        final Class[] interfaces = cls.getInterfaces();
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; ++i) {
                NucleusLogger.GENERAL.info("    Interface : " + interfaces[i].getName());
            }
        }
        try {
            final Method[] methods = cls.getDeclaredMethods();
            for (int j = 0; j < methods.length; ++j) {
                NucleusLogger.GENERAL.info("    Method : " + methods[j].toString());
                final Annotation[] annots = methods[j].getAnnotations();
                if (annots != null) {
                    for (int k = 0; k < annots.length; ++k) {
                        NucleusLogger.GENERAL.info("        annotation=" + annots[k]);
                    }
                }
            }
        }
        catch (Exception ex) {}
        try {
            final Field[] fields = cls.getDeclaredFields();
            for (int j = 0; j < fields.length; ++j) {
                NucleusLogger.GENERAL.info("    Field : " + fields[j].toString());
                final Annotation[] annots = fields[j].getAnnotations();
                if (annots != null) {
                    for (int k = 0; k < annots.length; ++k) {
                        NucleusLogger.GENERAL.info("        annotation=" + annots[k]);
                    }
                }
            }
        }
        catch (Exception ex2) {}
        NucleusLogger.GENERAL.info("----------------------------------------");
    }
    
    public static String getJavaBeanGetterName(final String fieldName, final boolean isBoolean) {
        if (fieldName == null) {
            return null;
        }
        return buildJavaBeanName(isBoolean ? "is" : "get", fieldName);
    }
    
    public static String getJavaBeanSetterName(final String fieldName) {
        if (fieldName == null) {
            return null;
        }
        return buildJavaBeanName("set", fieldName);
    }
    
    private static String buildJavaBeanName(final String prefix, final String fieldName) {
        final int prefixLength = prefix.length();
        final StringBuilder sb = new StringBuilder(prefixLength + fieldName.length());
        sb.append(prefix);
        sb.append(fieldName);
        sb.setCharAt(prefixLength, Character.toUpperCase(sb.charAt(prefixLength)));
        return sb.toString();
    }
    
    public static String getFieldNameForJavaBeanGetter(final String methodName) {
        if (methodName == null) {
            return null;
        }
        if (methodName.startsWith("get")) {
            return truncateJavaBeanMethodName(methodName, 3);
        }
        if (methodName.startsWith("is")) {
            return truncateJavaBeanMethodName(methodName, 2);
        }
        return null;
    }
    
    public static String getFieldNameForJavaBeanSetter(final String methodName) {
        if (methodName == null) {
            return null;
        }
        if (methodName.startsWith("set")) {
            return truncateJavaBeanMethodName(methodName, 3);
        }
        return null;
    }
    
    private static String truncateJavaBeanMethodName(String methodName, final int prefixLength) {
        if (methodName.length() <= prefixLength) {
            return null;
        }
        methodName = methodName.substring(prefixLength);
        if (methodName.length() == 1) {
            return methodName.toLowerCase();
        }
        final char firstChar = methodName.charAt(0);
        if (Character.isUpperCase(firstChar) && Character.isLowerCase(methodName.charAt(1))) {
            return Character.toLowerCase(firstChar) + methodName.substring(1);
        }
        return methodName;
    }
    
    public static String getClassNameForFileName(final String fileName, final ClassLoaderResolver clr) {
        final String file_separator = System.getProperty("file.separator");
        if (!fileName.endsWith(".class")) {
            return null;
        }
        String name;
        String className;
        Class cls;
        for (name = fileName.substring(0, fileName.length() - 6), name = StringUtils.replaceAll(name, file_separator, "."); name.indexOf(".") >= 0; name = className) {
            className = name.substring(name.indexOf(46) + 1);
            try {
                cls = clr.classForName(className);
                if (cls != null) {
                    return className;
                }
            }
            catch (ClassNotResolvedException ex) {}
        }
        return null;
    }
    
    public static String getClassNameForFileURL(final URL fileURL) throws ClassNotFoundException {
        final ClassLoader loader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                return new ClassLoader() {
                    @Override
                    protected Class findClass(final String name) throws ClassNotFoundException {
                        InputStream in = null;
                        try {
                            in = new BufferedInputStream(fileURL.openStream());
                            final ByteArrayOutputStream byteStr = new ByteArrayOutputStream();
                            int byt = -1;
                            while ((byt = in.read()) != -1) {
                                byteStr.write(byt);
                            }
                            final byte[] byteArr = byteStr.toByteArray();
                            return this.defineClass(null, byteArr, 0, byteArr.length);
                        }
                        catch (RuntimeException rex) {
                            throw rex;
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            throw new ClassNotFoundException(name);
                        }
                        finally {
                            if (in != null) {
                                try {
                                    in.close();
                                }
                                catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            }
                        }
                    }
                };
            }
        });
        final Class cls = loader.loadClass("garbage");
        return (cls != null) ? cls.getName() : null;
    }
    
    public static String getPackageNameForClass(final Class cls) {
        if (cls.getPackage() != null) {
            return cls.getPackage().getName();
        }
        final int separator = cls.getName().lastIndexOf(46);
        if (separator < 0) {
            return null;
        }
        return cls.getName().substring(0, separator);
    }
    
    public static String getClassNameForClass(final Class cls) {
        final int separator = cls.getName().lastIndexOf(46);
        if (separator < 0) {
            return cls.getName();
        }
        return cls.getName().substring(separator + 1);
    }
    
    public static Class getClassForGenericType(final Type genericType, final int pos) {
        if (genericType instanceof ParameterizedType) {
            final ParameterizedType paramtype = (ParameterizedType)genericType;
            if (paramtype.getActualTypeArguments().length > pos) {
                final Type argType = paramtype.getActualTypeArguments()[pos];
                if (argType instanceof Class) {
                    return (Class)argType;
                }
                if (argType instanceof ParameterizedType) {
                    return (Class)((ParameterizedType)argType).getRawType();
                }
                if (argType instanceof GenericArrayType) {
                    final Type cmptType = ((GenericArrayType)argType).getGenericComponentType();
                    return Array.newInstance((Class<?>)cmptType, 0).getClass();
                }
            }
        }
        return null;
    }
    
    public static String getCollectionElementType(final Field field) {
        final Class elementType = getCollectionElementType(field.getType(), field.getGenericType());
        return (elementType != null) ? elementType.getName() : null;
    }
    
    public static String getCollectionElementType(final Method method) {
        final Class elementType = getCollectionElementType(method.getReturnType(), method.getGenericReturnType());
        return (elementType != null) ? elementType.getName() : null;
    }
    
    public static Class getCollectionElementType(final Class type, final Type genericType) {
        if (!Collection.class.isAssignableFrom(type)) {
            return null;
        }
        return getClassForGenericType(genericType, 0);
    }
    
    public static String getMapKeyType(final Field field) {
        final Class keyType = getMapKeyType(field.getType(), field.getGenericType());
        return (keyType != null) ? keyType.getName() : null;
    }
    
    public static String getMapKeyType(final Method method) {
        final Class keyType = getMapKeyType(method.getReturnType(), method.getGenericReturnType());
        return (keyType != null) ? keyType.getName() : null;
    }
    
    public static Class getMapKeyType(final Class type, final Type genericType) {
        if (!Map.class.isAssignableFrom(type)) {
            return null;
        }
        return getClassForGenericType(genericType, 0);
    }
    
    public static String getMapValueType(final Field field) {
        final Class valueType = getMapValueType(field.getType(), field.getGenericType());
        return (valueType != null) ? valueType.getName() : null;
    }
    
    public static String getMapValueType(final Method method) {
        final Class valueType = getMapValueType(method.getReturnType(), method.getGenericReturnType());
        return (valueType != null) ? valueType.getName() : null;
    }
    
    public static Class getMapValueType(final Class type, final Type genericType) {
        if (!Map.class.isAssignableFrom(type)) {
            return null;
        }
        return getClassForGenericType(genericType, 1);
    }
    
    public static int getModifiersForFieldOfClass(final ClassLoaderResolver clr, final String className, final String fieldName) {
        try {
            final Class cls = clr.classForName(className);
            final Field fld = cls.getDeclaredField(fieldName);
            return fld.getModifiers();
        }
        catch (Exception e) {
            return -1;
        }
    }
    
    public static boolean isReferenceType(final Class cls) {
        return cls != null && (cls.isInterface() || cls.getName().equals("java.lang.Object"));
    }
    
    public static void assertClassForJarExistsInClasspath(final ClassLoaderResolver clr, final String className, final String jarName) {
        try {
            final Class cls = clr.classForName(className);
            if (cls == null) {
                throw new NucleusUserException(ClassUtils.LOCALISER.msg("001006", className, jarName));
            }
        }
        catch (Error err) {
            throw new NucleusUserException(ClassUtils.LOCALISER.msg("001006", className, jarName));
        }
        catch (ClassNotResolvedException cnre) {
            throw new NucleusUserException(ClassUtils.LOCALISER.msg("001006", className, jarName));
        }
    }
    
    public static boolean stringArrayContainsValue(final String[] array, final String value) {
        if (value == null || array == null) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (value.equals(array[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static Object getValueOfMethodByReflection(final Object object, final String methodName, final Object... args) {
        if (object == null) {
            return null;
        }
        final Method method = getDeclaredMethodPrivileged(object.getClass(), methodName, (Class[])null);
        if (method == null) {
            throw new NucleusUserException("Cannot access method: " + methodName + " in type " + object.getClass());
        }
        Object methodValue;
        try {
            if (!method.isAccessible()) {
                try {
                    AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                        @Override
                        public Object run() {
                            method.setAccessible(true);
                            return null;
                        }
                    });
                }
                catch (SecurityException ex) {
                    throw new NucleusException("Cannot access method: " + methodName, ex).setFatal();
                }
            }
            methodValue = method.invoke(object, args);
        }
        catch (InvocationTargetException e2) {
            throw new NucleusUserException("Cannot access method: " + methodName, e2);
        }
        catch (IllegalArgumentException e3) {
            throw new NucleusUserException("Cannot access method: " + methodName, e3);
        }
        catch (IllegalAccessException e4) {
            throw new NucleusUserException("Cannot access method: " + methodName, e4);
        }
        return methodValue;
    }
    
    public static Object getValueOfFieldByReflection(final Object object, final String fieldName) {
        if (object == null) {
            return null;
        }
        final Field field = getDeclaredFieldPrivileged(object.getClass(), fieldName);
        if (field == null) {
            throw new NucleusUserException("Cannot access field: " + fieldName + " in type " + object.getClass());
        }
        Object fieldValue;
        try {
            if (!field.isAccessible()) {
                try {
                    AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                        @Override
                        public Object run() {
                            field.setAccessible(true);
                            return null;
                        }
                    });
                }
                catch (SecurityException ex) {
                    throw new NucleusException("Cannot access field: " + fieldName, ex).setFatal();
                }
            }
            fieldValue = field.get(object);
        }
        catch (IllegalArgumentException e2) {
            throw new NucleusUserException("Cannot access field: " + fieldName, e2);
        }
        catch (IllegalAccessException e3) {
            throw new NucleusUserException("Cannot access field: " + fieldName, e3);
        }
        return fieldValue;
    }
    
    private static Field getDeclaredFieldPrivileged(final Class clazz, final String fieldName) {
        if (clazz == null || fieldName == null) {
            return null;
        }
        return AccessController.doPrivileged((PrivilegedAction<Field>)new PrivilegedAction() {
            @Override
            public Object run() {
                Class seekingClass = clazz;
                try {
                    return seekingClass.getDeclaredField(fieldName);
                }
                catch (SecurityException ex) {
                    throw new NucleusException("CannotGetDeclaredField", ex).setFatal();
                }
                catch (NoSuchFieldException ex2) {
                    seekingClass = seekingClass.getSuperclass();
                    if (seekingClass == null) {
                        return null;
                    }
                    return seekingClass.getDeclaredField(fieldName);
                }
                catch (LinkageError linkageError) {}
            }
        });
    }
    
    private static Method getDeclaredMethodPrivileged(final Class clazz, final String methodName, final Class... argTypes) {
        if (clazz == null || methodName == null) {
            return null;
        }
        return AccessController.doPrivileged((PrivilegedAction<Method>)new PrivilegedAction() {
            @Override
            public Object run() {
                Class seekingClass = clazz;
                try {
                    return seekingClass.getDeclaredMethod(methodName, (Class[])argTypes);
                }
                catch (SecurityException ex) {
                    throw new NucleusException("Cannot get declared method " + methodName, ex).setFatal();
                }
                catch (NoSuchMethodException ex2) {
                    seekingClass = seekingClass.getSuperclass();
                    if (seekingClass == null) {
                        return null;
                    }
                    return seekingClass.getDeclaredMethod(methodName, (Class[])argTypes);
                }
                catch (LinkageError linkageError) {}
            }
        });
    }
    
    public static Object getValueForIdentityField(final Object id, final String fieldName) {
        final String getterName = getJavaBeanGetterName(fieldName, false);
        try {
            return getValueOfMethodByReflection(id, getterName, (Object[])null);
        }
        catch (NucleusException ne) {
            try {
                return getValueOfFieldByReflection(id, fieldName);
            }
            catch (NucleusException ne) {
                throw new NucleusUserException("Not possible to get value of field " + fieldName + " from identity " + id);
            }
        }
    }
    
    public static Class getClassForMemberOfClass(final Class cls, final String memberName) {
        final Field fld = getFieldForClass(cls, memberName);
        if (fld != null) {
            return fld.getType();
        }
        final Method method = getGetterMethodForClass(cls, memberName);
        if (method != null) {
            return method.getReturnType();
        }
        return null;
    }
    
    public static boolean isJavaBeanGetterMethod(final Method method) {
        return !Modifier.isStatic(method.getModifiers()) && (method.getName().startsWith("get") || method.getName().startsWith("is")) && (!method.getName().startsWith("get") || method.getName().length() != 3) && (!method.getName().startsWith("is") || method.getName().length() != 2) && method.getReturnType() != null && (method.getParameterTypes() == null || method.getParameterTypes().length == 0);
    }
    
    public static void clearFlags(final boolean[] flags) {
        for (int i = 0; i < flags.length; ++i) {
            flags[i] = false;
        }
    }
    
    public static void clearFlags(final boolean[] flags, final int[] fields) {
        for (int i = 0; i < fields.length; ++i) {
            flags[fields[i]] = false;
        }
    }
    
    public static int[] getFlagsSetTo(final boolean[] flags, final boolean state) {
        final int[] temp = new int[flags.length];
        int j = 0;
        for (int i = 0; i < flags.length; ++i) {
            if (flags[i] == state) {
                temp[j++] = i;
            }
        }
        if (j != 0) {
            final int[] fieldNumbers = new int[j];
            System.arraycopy(temp, 0, fieldNumbers, 0, j);
            return fieldNumbers;
        }
        return null;
    }
    
    public static int[] getFlagsSetTo(final boolean[] flags, final int[] indices, final boolean state) {
        if (indices == null) {
            return null;
        }
        final int[] temp = new int[indices.length];
        int j = 0;
        for (int i = 0; i < indices.length; ++i) {
            if (flags[indices[i]] == state) {
                temp[j++] = indices[i];
            }
        }
        if (j != 0) {
            final int[] fieldNumbers = new int[j];
            System.arraycopy(temp, 0, fieldNumbers, 0, j);
            return fieldNumbers;
        }
        return null;
    }
    
    public static boolean getBitFromInt(final int bits, final int bitIndex) {
        if (bitIndex < 0 || bitIndex > 31) {
            throw new IllegalArgumentException();
        }
        return (bits & 1 << bitIndex) != 0x0;
    }
    
    public static int setBitInInt(final int bits, final int bitIndex, final boolean flag) {
        if (bitIndex < 0 || bitIndex > 31) {
            throw new IllegalArgumentException();
        }
        final int mask = 1 << bitIndex;
        return (bits & ~mask) | (flag ? mask : 0);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        constructorsCache = new SoftValueMap();
    }
}
