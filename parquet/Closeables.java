// 
// Decompiled by Procyon v0.5.36
// 

package parquet;

import java.io.IOException;
import java.io.Closeable;

public final class Closeables
{
    private static final Log LOG;
    
    private Closeables() {
    }
    
    public static void close(final Closeable c) throws IOException {
        if (c == null) {
            return;
        }
        c.close();
    }
    
    public static void closeAndSwallowIOExceptions(final Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        }
        catch (IOException e) {
            Closeables.LOG.warn("Encountered exception closing closeable", e);
        }
    }
    
    static {
        LOG = Log.getLog(Closeables.class);
    }
}
