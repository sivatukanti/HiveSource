// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.util;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class HiddenFileFilter implements PathFilter
{
    public static final HiddenFileFilter INSTANCE;
    
    private HiddenFileFilter() {
    }
    
    @Override
    public boolean accept(final Path p) {
        return !p.getName().startsWith("_") && !p.getName().startsWith(".");
    }
    
    static {
        INSTANCE = new HiddenFileFilter();
    }
}
