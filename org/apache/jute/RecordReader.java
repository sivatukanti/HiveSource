// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.InputStream;
import java.util.HashMap;

public class RecordReader
{
    private InputArchive archive;
    private static HashMap archiveFactory;
    
    private static InputArchive createArchive(final InputStream in, final String format) throws IOException {
        final Method factory = RecordReader.archiveFactory.get(format);
        if (factory != null) {
            final Object[] params = { in };
            try {
                return (InputArchive)factory.invoke(null, params);
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
    
    public RecordReader(final InputStream in, final String format) throws IOException {
        this.archive = createArchive(in, format);
    }
    
    public void read(final Record r) throws IOException {
        r.deserialize(this.archive, "");
    }
    
    static {
        RecordReader.archiveFactory = new HashMap();
        final Class[] params = { InputStream.class };
        try {
            RecordReader.archiveFactory.put("binary", BinaryInputArchive.class.getDeclaredMethod("getArchive", (Class<?>[])params));
            RecordReader.archiveFactory.put("csv", CsvInputArchive.class.getDeclaredMethod("getArchive", (Class<?>[])params));
            RecordReader.archiveFactory.put("xml", XmlInputArchive.class.getDeclaredMethod("getArchive", (Class<?>[])params));
        }
        catch (SecurityException ex) {
            ex.printStackTrace();
        }
        catch (NoSuchMethodException ex2) {
            ex2.printStackTrace();
        }
    }
}
