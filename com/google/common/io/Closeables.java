// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.io;

import java.io.IOException;
import java.util.logging.Level;
import javax.annotation.Nullable;
import java.io.Closeable;
import com.google.common.annotations.VisibleForTesting;
import java.util.logging.Logger;
import com.google.common.annotations.Beta;

@Beta
public final class Closeables
{
    @VisibleForTesting
    static final Logger logger;
    
    private Closeables() {
    }
    
    public static void close(@Nullable final Closeable closeable, final boolean swallowIOException) throws IOException {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        }
        catch (IOException e) {
            if (!swallowIOException) {
                throw e;
            }
            Closeables.logger.log(Level.WARNING, "IOException thrown while closing Closeable.", e);
        }
    }
    
    static {
        logger = Logger.getLogger(Closeables.class.getName());
    }
}
