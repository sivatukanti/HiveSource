// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.security.CodeSource;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.conf.Configuration;
import java.io.PrintStream;
import org.apache.hadoop.conf.Configured;

public final class FindClass extends Configured implements Tool
{
    public static final String A_CREATE = "create";
    public static final String A_LOAD = "load";
    public static final String A_RESOURCE = "locate";
    public static final String A_PRINTRESOURCE = "print";
    public static final int SUCCESS = 0;
    protected static final int E_GENERIC = 1;
    protected static final int E_USAGE = 2;
    protected static final int E_NOT_FOUND = 3;
    protected static final int E_LOAD_FAILED = 4;
    protected static final int E_CREATE_FAILED = 5;
    private static PrintStream stdout;
    private static PrintStream stderr;
    
    public FindClass() {
        super(new Configuration());
    }
    
    public FindClass(final Configuration conf) {
        super(conf);
    }
    
    @VisibleForTesting
    public static void setOutputStreams(final PrintStream out, final PrintStream err) {
        FindClass.stdout = out;
        FindClass.stderr = err;
    }
    
    private Class getClass(final String name) throws ClassNotFoundException {
        return this.getConf().getClassByName(name);
    }
    
    private URL getResource(final String name) {
        return this.getConf().getResource(name);
    }
    
    private int loadResource(final String name) {
        final URL url = this.getResource(name);
        if (url == null) {
            err("Resource not found: %s", name);
            return 3;
        }
        out("%s: %s", name, url);
        return 0;
    }
    
    private int dumpResource(final String name) {
        final URL url = this.getResource(name);
        if (url == null) {
            err("Resource not found:" + name, new Object[0]);
            return 3;
        }
        try {
            final InputStream instream = url.openStream();
            int data;
            while (-1 != (data = instream.read())) {
                FindClass.stdout.print((char)data);
            }
            FindClass.stdout.print('\n');
            return 0;
        }
        catch (IOException e) {
            printStack(e, "Failed to read resource %s at URL %s", name, url);
            return 4;
        }
    }
    
    private static void err(final String s, final Object... args) {
        FindClass.stderr.format(s, args);
        FindClass.stderr.print('\n');
    }
    
    private static void out(final String s, final Object... args) {
        FindClass.stdout.format(s, args);
        FindClass.stdout.print('\n');
    }
    
    private static void printStack(final Throwable e, final String text, final Object... args) {
        err(text, args);
        e.printStackTrace(FindClass.stderr);
    }
    
    private int loadClass(final String name) {
        try {
            final Class clazz = this.getClass(name);
            this.loadedClass(name, clazz);
            return 0;
        }
        catch (ClassNotFoundException e) {
            printStack(e, "Class not found " + name, new Object[0]);
            return 3;
        }
        catch (Exception e2) {
            printStack(e2, "Exception while loading class " + name, new Object[0]);
            return 4;
        }
        catch (Error e3) {
            printStack(e3, "Error while loading class " + name, new Object[0]);
            return 4;
        }
    }
    
    private void loadedClass(final String name, final Class clazz) {
        out("Loaded %s as %s", name, clazz);
        final CodeSource source = clazz.getProtectionDomain().getCodeSource();
        final URL url = source.getLocation();
        out("%s: %s", name, url);
    }
    
    private int createClassInstance(final String name) {
        try {
            final Class clazz = this.getClass(name);
            this.loadedClass(name, clazz);
            final Object instance = clazz.newInstance();
            try {
                out("Created instance " + instance.toString(), new Object[0]);
            }
            catch (Exception e) {
                printStack(e, "Created class instance but the toString() operator failed", new Object[0]);
            }
            return 0;
        }
        catch (ClassNotFoundException e2) {
            printStack(e2, "Class not found " + name, new Object[0]);
            return 3;
        }
        catch (Exception e3) {
            printStack(e3, "Exception while creating class " + name, new Object[0]);
            return 5;
        }
        catch (Error e4) {
            printStack(e4, "Exception while creating class " + name, new Object[0]);
            return 5;
        }
    }
    
    @Override
    public int run(final String[] args) throws Exception {
        if (args.length != 2) {
            return this.usage(args);
        }
        final String action = args[0];
        final String name = args[1];
        int result;
        if ("load".equals(action)) {
            result = this.loadClass(name);
        }
        else if ("create".equals(action)) {
            result = this.loadClass(name);
            if (result == 0) {
                result = this.createClassInstance(name);
            }
        }
        else if ("locate".equals(action)) {
            result = this.loadResource(name);
        }
        else if ("print".equals(action)) {
            result = this.dumpResource(name);
        }
        else {
            result = this.usage(args);
        }
        return result;
    }
    
    private int usage(final String[] args) {
        err("Usage : [load | create] <classname>", new Object[0]);
        err("        [locate | print] <resourcename>]", new Object[0]);
        err("The return codes are:", new Object[0]);
        this.explainResult(0, "The operation was successful");
        this.explainResult(1, "Something went wrong");
        this.explainResult(2, "This usage message was printed");
        this.explainResult(3, "The class or resource was not found");
        this.explainResult(4, "The class was found but could not be loaded");
        this.explainResult(5, "The class was loaded, but an instance of it could not be created");
        return 2;
    }
    
    private void explainResult(final int errorcode, final String text) {
        err(" %2d -- %s ", errorcode, text);
    }
    
    public static void main(final String[] args) {
        try {
            final int result = ToolRunner.run(new FindClass(), args);
            System.exit(result);
        }
        catch (Exception e) {
            printStack(e, "Running FindClass", new Object[0]);
            System.exit(1);
        }
    }
    
    static {
        FindClass.stdout = System.out;
        FindClass.stderr = System.err;
    }
}
