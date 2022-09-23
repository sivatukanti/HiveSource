// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import java.util.Iterator;
import java.util.Arrays;
import parquet.Preconditions;
import java.io.Serializable;

public final class ColumnPath implements Iterable<String>, Serializable
{
    private static Canonicalizer<ColumnPath> paths;
    private final String[] p;
    
    public static ColumnPath fromDotString(final String path) {
        Preconditions.checkNotNull(path, "path");
        return get(path.split("\\."));
    }
    
    public static ColumnPath get(final String... path) {
        return ColumnPath.paths.canonicalize(new ColumnPath(path));
    }
    
    private ColumnPath(final String[] path) {
        this.p = path;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ColumnPath && Arrays.equals(this.p, ((ColumnPath)obj).p);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.p);
    }
    
    public String toDotString() {
        final Iterator<String> iter = Arrays.asList(this.p).iterator();
        final StringBuilder sb = new StringBuilder();
        while (iter.hasNext()) {
            sb.append(iter.next());
            if (iter.hasNext()) {
                sb.append('.');
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return Arrays.toString(this.p);
    }
    
    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(this.p).iterator();
    }
    
    public int size() {
        return this.p.length;
    }
    
    public String[] toArray() {
        return this.p;
    }
    
    static {
        ColumnPath.paths = new Canonicalizer<ColumnPath>() {
            @Override
            protected ColumnPath toCanonical(final ColumnPath value) {
                final String[] path = new String[value.p.length];
                for (int i = 0; i < value.p.length; ++i) {
                    path[i] = value.p[i].intern();
                }
                return new ColumnPath(path, null);
            }
        };
    }
}
