// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import parquet.hadoop.metadata.ParquetMetadata;
import org.apache.hadoop.fs.Path;

public class Footer
{
    private final Path file;
    private final ParquetMetadata parquetMetadata;
    
    public Footer(final Path file, final ParquetMetadata parquetMetadata) {
        this.file = file;
        this.parquetMetadata = parquetMetadata;
    }
    
    public Path getFile() {
        return this.file;
    }
    
    public ParquetMetadata getParquetMetadata() {
        return this.parquetMetadata;
    }
    
    @Override
    public String toString() {
        return "Footer{" + this.file + ", " + this.parquetMetadata + "}";
    }
}
