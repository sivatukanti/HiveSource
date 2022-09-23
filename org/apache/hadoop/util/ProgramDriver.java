// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class ProgramDriver
{
    Map<String, ProgramDescription> programs;
    
    public ProgramDriver() {
        this.programs = new TreeMap<String, ProgramDescription>();
    }
    
    private static void printUsage(final Map<String, ProgramDescription> programs) {
        System.out.println("Valid program names are:");
        for (final Map.Entry<String, ProgramDescription> item : programs.entrySet()) {
            System.out.println("  " + item.getKey() + ": " + item.getValue().getDescription());
        }
    }
    
    public void addClass(final String name, final Class<?> mainClass, final String description) throws Throwable {
        this.programs.put(name, new ProgramDescription(mainClass, description));
    }
    
    public int run(final String[] args) throws Throwable {
        if (args.length == 0) {
            System.out.println("An example program must be given as the first argument.");
            printUsage(this.programs);
            return -1;
        }
        final ProgramDescription pgm = this.programs.get(args[0]);
        if (pgm == null) {
            System.out.println("Unknown program '" + args[0] + "' chosen.");
            printUsage(this.programs);
            return -1;
        }
        final String[] new_args = new String[args.length - 1];
        for (int i = 1; i < args.length; ++i) {
            new_args[i - 1] = args[i];
        }
        pgm.invoke(new_args);
        return 0;
    }
    
    public void driver(final String[] argv) throws Throwable {
        if (this.run(argv) == -1) {
            System.exit(-1);
        }
    }
    
    private static class ProgramDescription
    {
        static final Class<?>[] paramTypes;
        private Method main;
        private String description;
        
        public ProgramDescription(final Class<?> mainClass, final String description) throws SecurityException, NoSuchMethodException {
            this.main = mainClass.getMethod("main", ProgramDescription.paramTypes);
            this.description = description;
        }
        
        public void invoke(final String[] args) throws Throwable {
            try {
                this.main.invoke(null, args);
            }
            catch (InvocationTargetException except) {
                throw except.getCause();
            }
        }
        
        public String getDescription() {
            return this.description;
        }
        
        static {
            paramTypes = new Class[] { String[].class };
        }
    }
}
