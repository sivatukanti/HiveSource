// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.ProjectComponent;

public class HasMethod extends ProjectComponent implements Condition
{
    private String classname;
    private String method;
    private String field;
    private Path classpath;
    private AntClassLoader loader;
    private boolean ignoreSystemClasses;
    
    public HasMethod() {
        this.ignoreSystemClasses = false;
    }
    
    public void setClasspath(final Path classpath) {
        this.createClasspath().append(classpath);
    }
    
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.createClasspath().setRefid(r);
    }
    
    public void setClassname(final String classname) {
        this.classname = classname;
    }
    
    public void setMethod(final String method) {
        this.method = method;
    }
    
    public void setField(final String field) {
        this.field = field;
    }
    
    public void setIgnoreSystemClasses(final boolean ignoreSystemClasses) {
        this.ignoreSystemClasses = ignoreSystemClasses;
    }
    
    private Class loadClass(final String classname) {
        try {
            if (this.ignoreSystemClasses) {
                (this.loader = this.getProject().createClassLoader(this.classpath)).setParentFirst(false);
                this.loader.addJavaLibraries();
                try {
                    return this.loader.findClass(classname);
                }
                catch (SecurityException se) {
                    throw new BuildException("class \"" + classname + "\" was found but a" + " SecurityException has been" + " raised while loading it", se);
                }
            }
            if (this.loader != null) {
                return this.loader.loadClass(classname);
            }
            final ClassLoader l = this.getClass().getClassLoader();
            if (l != null) {
                return Class.forName(classname, true, l);
            }
            return Class.forName(classname);
        }
        catch (ClassNotFoundException e2) {
            throw new BuildException("class \"" + classname + "\" was not found");
        }
        catch (NoClassDefFoundError e) {
            throw new BuildException("Could not load dependent class \"" + e.getMessage() + "\" for class \"" + classname + "\"");
        }
    }
    
    public boolean eval() throws BuildException {
        if (this.classname == null) {
            throw new BuildException("No classname defined");
        }
        final ClassLoader preLoadClass = this.loader;
        try {
            final Class clazz = this.loadClass(this.classname);
            if (this.method != null) {
                return this.isMethodFound(clazz);
            }
            if (this.field != null) {
                return this.isFieldFound(clazz);
            }
            throw new BuildException("Neither method nor field defined");
        }
        finally {
            if (preLoadClass != this.loader && this.loader != null) {
                this.loader.cleanup();
                this.loader = null;
            }
        }
    }
    
    private boolean isFieldFound(final Class clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            final Field fieldEntry = fields[i];
            if (fieldEntry.getName().equals(this.field)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isMethodFound(final Class clazz) {
        final Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Method methodEntry = methods[i];
            if (methodEntry.getName().equals(this.method)) {
                return true;
            }
        }
        return false;
    }
}
