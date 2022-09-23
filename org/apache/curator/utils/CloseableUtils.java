// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.utils;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.curator.shaded.com.google.common.io.Closeables;
import java.io.Closeable;
import org.slf4j.Logger;

public class CloseableUtils
{
    private static final Logger log;
    
    public static void closeQuietly(final Closeable closeable) {
        try {
            Closeables.close(closeable, true);
        }
        catch (IOException e) {
            CloseableUtils.log.error("IOException should not have been thrown.", e);
        }
    }
    
    static {
        log = LoggerFactory.getLogger(CloseableUtils.class);
    }
}
