// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.util;

import java.io.ObjectInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import java.io.Closeable;
import parquet.Closeables;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.conf.Configuration;
import parquet.Log;

public final class SerializationUtil
{
    private static final Log LOG;
    
    private SerializationUtil() {
    }
    
    public static void writeObjectToConfAsBase64(final String key, final Object obj, final Configuration conf) throws IOException {
        ByteArrayOutputStream baos = null;
        GZIPOutputStream gos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(baos);
            oos = new ObjectOutputStream(gos);
            oos.writeObject(obj);
        }
        finally {
            Closeables.close(oos);
            Closeables.close(gos);
            Closeables.close(baos);
        }
        conf.set(key, new String(Base64.encodeBase64(baos.toByteArray()), "UTF-8"));
    }
    
    public static <T> T readObjectFromConfAsBase64(final String key, final Configuration conf) throws IOException {
        final String b64 = conf.get(key);
        if (b64 == null) {
            return null;
        }
        final byte[] bytes = Base64.decodeBase64(b64.getBytes("UTF-8"));
        ByteArrayInputStream bais = null;
        GZIPInputStream gis = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            gis = new GZIPInputStream(bais);
            ois = new ObjectInputStream(gis);
            return (T)ois.readObject();
        }
        catch (ClassNotFoundException e) {
            throw new IOException("Could not read object from config with key " + key, e);
        }
        catch (ClassCastException e2) {
            throw new IOException("Couldn't cast object read from config with key " + key, e2);
        }
        finally {
            Closeables.close(ois);
            Closeables.close(gis);
            Closeables.close(bais);
        }
    }
    
    static {
        LOG = Log.getLog(SerializationUtil.class);
    }
}
