// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.tracing.TraceUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.htrace.core.Tracer;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public final class FsTracer
{
    private static Tracer instance;
    
    public static synchronized Tracer get(final Configuration conf) {
        if (FsTracer.instance == null) {
            FsTracer.instance = new Tracer.Builder("FSClient").conf(TraceUtils.wrapHadoopConf("fs.client.htrace.", conf)).build();
        }
        return FsTracer.instance;
    }
    
    @VisibleForTesting
    public static synchronized void clear() {
        if (FsTracer.instance == null) {
            return;
        }
        try {
            FsTracer.instance.close();
        }
        finally {
            FsTracer.instance = null;
        }
    }
    
    private FsTracer() {
    }
}
