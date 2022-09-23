// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class WindowsGetSpaceUsed extends CachingGetSpaceUsed
{
    public WindowsGetSpaceUsed(final GetSpaceUsed.Builder builder) throws IOException {
        super(builder.getPath(), builder.getInterval(), builder.getJitter(), builder.getInitialUsed());
    }
    
    @Override
    protected void refresh() {
        this.used.set(DUHelper.getFolderUsage(this.getDirPath()));
    }
}
