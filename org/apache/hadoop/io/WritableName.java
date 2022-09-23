// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.util.HashMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class WritableName
{
    private static HashMap<String, Class<?>> NAME_TO_CLASS;
    private static HashMap<Class<?>, String> CLASS_TO_NAME;
    
    private WritableName() {
    }
    
    public static synchronized void setName(final Class<?> writableClass, final String name) {
        WritableName.CLASS_TO_NAME.put(writableClass, name);
        WritableName.NAME_TO_CLASS.put(name, writableClass);
    }
    
    public static synchronized void addName(final Class<?> writableClass, final String name) {
        WritableName.NAME_TO_CLASS.put(name, writableClass);
    }
    
    public static synchronized String getName(final Class<?> writableClass) {
        final String name = WritableName.CLASS_TO_NAME.get(writableClass);
        if (name != null) {
            return name;
        }
        return writableClass.getName();
    }
    
    public static synchronized Class<?> getClass(final String name, final Configuration conf) throws IOException {
        final Class<?> writableClass = WritableName.NAME_TO_CLASS.get(name);
        if (writableClass != null) {
            return writableClass.asSubclass(Writable.class);
        }
        try {
            return conf.getClassByName(name);
        }
        catch (ClassNotFoundException e) {
            final IOException newE = new IOException("WritableName can't load class: " + name);
            newE.initCause(e);
            throw newE;
        }
    }
    
    static {
        WritableName.NAME_TO_CLASS = new HashMap<String, Class<?>>();
        WritableName.CLASS_TO_NAME = new HashMap<Class<?>, String>();
        setName(NullWritable.class, "null");
        setName(LongWritable.class, "long");
        setName(UTF8.class, "UTF8");
        setName(MD5Hash.class, "MD5Hash");
    }
}
