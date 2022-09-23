// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class RecordWriter
{
    private OutputArchive archive;
    private static HashMap archiveFactory;
    
    private static OutputArchive getBinaryArchive(final OutputStream out) {
        return new BinaryOutputArchive(new DataOutputStream(out));
    }
    
    private static OutputArchive getCsvArchive(final OutputStream out) throws IOException {
        try {
            return new CsvOutputArchive(out);
        }
        catch (UnsupportedEncodingException ex) {
            throw new IOException("Unsupported encoding UTF-8");
        }
    }
    
    private static OutputArchive getXmlArchive(final OutputStream out) throws IOException {
        return new XmlOutputArchive(out);
    }
    
    static HashMap constructFactory() {
        final HashMap factory = new HashMap();
        final Class[] params = { OutputStream.class };
        try {
            factory.put("binary", BinaryOutputArchive.class.getDeclaredMethod("getArchive", (Class<?>[])params));
            factory.put("csv", CsvOutputArchive.class.getDeclaredMethod("getArchive", (Class<?>[])params));
            factory.put("xml", XmlOutputArchive.class.getDeclaredMethod("getArchive", (Class<?>[])params));
        }
        catch (SecurityException ex) {
            ex.printStackTrace();
        }
        catch (NoSuchMethodException ex2) {
            ex2.printStackTrace();
        }
        return factory;
    }
    
    private static OutputArchive createArchive(final OutputStream out, final String format) throws IOException {
        final Method factory = RecordWriter.archiveFactory.get(format);
        if (factory != null) {
            final Object[] params = { out };
            try {
                return (OutputArchive)factory.invoke(null, params);
            }
            catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex2) {
                ex2.printStackTrace();
            }
            catch (IllegalAccessException ex3) {
                ex3.printStackTrace();
            }
        }
        return null;
    }
    
    public RecordWriter(final OutputStream out, final String format) throws IOException {
        this.archive = createArchive(out, format);
    }
    
    public void write(final Record r) throws IOException {
        r.serialize(this.archive, "");
    }
    
    static {
        RecordWriter.archiveFactory = constructFactory();
    }
}
