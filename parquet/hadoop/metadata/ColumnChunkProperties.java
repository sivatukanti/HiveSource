// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import java.util.Arrays;
import java.util.Collection;
import parquet.column.Encoding;
import java.util.Set;
import parquet.schema.PrimitiveType;

public class ColumnChunkProperties
{
    private static Canonicalizer<ColumnChunkProperties> properties;
    private final CompressionCodecName codec;
    private final ColumnPath path;
    private final PrimitiveType.PrimitiveTypeName type;
    private final Set<Encoding> encodings;
    
    public static ColumnChunkProperties get(final ColumnPath path, final PrimitiveType.PrimitiveTypeName type, final CompressionCodecName codec, final Set<Encoding> encodings) {
        return ColumnChunkProperties.properties.canonicalize(new ColumnChunkProperties(codec, path, type, encodings));
    }
    
    private ColumnChunkProperties(final CompressionCodecName codec, final ColumnPath path, final PrimitiveType.PrimitiveTypeName type, final Set<Encoding> encodings) {
        this.codec = codec;
        this.path = path;
        this.type = type;
        this.encodings = encodings;
    }
    
    public CompressionCodecName getCodec() {
        return this.codec;
    }
    
    public ColumnPath getPath() {
        return this.path;
    }
    
    public PrimitiveType.PrimitiveTypeName getType() {
        return this.type;
    }
    
    public Set<Encoding> getEncodings() {
        return this.encodings;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ColumnChunkProperties) {
            final ColumnChunkProperties other = (ColumnChunkProperties)obj;
            return other.codec == this.codec && other.path.equals(this.path) && other.type == this.type && this.equals(other.encodings, this.encodings);
        }
        return false;
    }
    
    private boolean equals(final Set<Encoding> a, final Set<Encoding> b) {
        return a.size() == b.size() && a.containsAll(b);
    }
    
    @Override
    public int hashCode() {
        return this.codec.hashCode() ^ this.path.hashCode() ^ this.type.hashCode() ^ Arrays.hashCode(this.encodings.toArray());
    }
    
    @Override
    public String toString() {
        return this.codec + " " + this.path + " " + this.type + "  " + this.encodings;
    }
    
    static {
        ColumnChunkProperties.properties = new Canonicalizer<ColumnChunkProperties>();
    }
}
