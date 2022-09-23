// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import org.datanucleus.ClassConstants;
import java.util.Iterator;
import java.lang.reflect.Array;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.HashMap;

public class Imports
{
    private static final Localiser LOCALISER;
    private HashMap primitives;
    private HashMap importedClassesByName;
    private HashSet importedPackageNames;
    
    public Imports() {
        this.primitives = new HashMap();
        this.importedClassesByName = new HashMap();
        this.importedPackageNames = new HashSet();
        this.primitives.put("boolean", Boolean.TYPE);
        this.primitives.put("byte", Byte.TYPE);
        this.primitives.put("char", Character.TYPE);
        this.primitives.put("short", Short.TYPE);
        this.primitives.put("int", Integer.TYPE);
        this.primitives.put("long", Long.TYPE);
        this.primitives.put("float", Float.TYPE);
        this.primitives.put("double", Double.TYPE);
        this.importedPackageNames.add("java.lang");
    }
    
    public void importPackage(final String className) {
        final int lastDot = className.lastIndexOf(46);
        if (lastDot > 0) {
            this.importedPackageNames.add(className.substring(0, lastDot));
        }
    }
    
    public void importClass(final String className) {
        final int lastDot = className.lastIndexOf(46);
        if (lastDot > 0) {
            this.importedClassesByName.put(className.substring(lastDot + 1), className);
        }
    }
    
    public void parseImports(final String imports) {
        final StringTokenizer t1 = new StringTokenizer(imports, ";");
        while (t1.hasMoreTokens()) {
            final String importDecl = t1.nextToken().trim();
            if (importDecl.length() == 0 && !t1.hasMoreTokens()) {
                break;
            }
            final StringTokenizer t2 = new StringTokenizer(importDecl, " ");
            if (t2.countTokens() != 2 || !t2.nextToken().equals("import")) {
                throw new NucleusUserException(Imports.LOCALISER.msg("021002", importDecl));
            }
            final String importName = t2.nextToken();
            final int lastDot = importName.lastIndexOf(".");
            final String lastPart = importName.substring(lastDot + 1);
            if (lastPart.equals("*")) {
                if (lastDot < 1) {
                    throw new NucleusUserException(Imports.LOCALISER.msg("021003", importName));
                }
                this.importedPackageNames.add(importName.substring(0, lastDot));
            }
            else {
                if (this.importedClassesByName.put(lastPart, importName) == null) {
                    continue;
                }
                NucleusLogger.QUERY.info(Imports.LOCALISER.msg("021004", importName));
            }
        }
    }
    
    public Class resolveClassDeclaration(String classDecl, final ClassLoaderResolver clr, final ClassLoader primaryClassLoader) {
        final boolean isArray = classDecl.indexOf(91) >= 0;
        if (isArray) {
            classDecl = classDecl.substring(0, classDecl.indexOf(91));
        }
        Class c;
        if (classDecl.indexOf(46) < 0) {
            c = this.primitives.get(classDecl);
            if (c == null) {
                final String cd = this.importedClassesByName.get(classDecl);
                if (cd != null) {
                    c = clr.classForName(cd, primaryClassLoader);
                }
            }
            if (c == null) {
                for (final String packageName : this.importedPackageNames) {
                    try {
                        final Class c2 = clr.classForName(packageName + '.' + classDecl, primaryClassLoader);
                        if (c != null && c2 != null) {
                            throw new NucleusUserException(Imports.LOCALISER.msg("021008", c.getName(), c2.getName()));
                        }
                        c = c2;
                    }
                    catch (ClassNotResolvedException ex) {}
                }
                if (c == null) {
                    throw new ClassNotResolvedException(classDecl);
                }
                if (NucleusLogger.GENERAL.isDebugEnabled()) {
                    NucleusLogger.GENERAL.debug(Imports.LOCALISER.msg("021010", classDecl, c.getName()));
                }
            }
        }
        else {
            c = clr.classForName(classDecl, primaryClassLoader);
        }
        if (isArray) {
            c = Array.newInstance(c, 0).getClass();
        }
        return c;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
