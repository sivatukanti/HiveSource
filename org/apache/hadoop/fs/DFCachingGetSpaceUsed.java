// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class DFCachingGetSpaceUsed extends CachingGetSpaceUsed
{
    private final DF df;
    
    public DFCachingGetSpaceUsed(final GetSpaceUsed.Builder builder) throws IOException {
        super(builder);
        this.df = new DF(builder.getPath(), builder.getInterval());
    }
    
    @Override
    protected void refresh() {
        this.used.set(this.df.getUsed());
    }
}
