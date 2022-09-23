// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import parquet.schema.MessageType;
import parquet.hadoop.metadata.BlockMetaData;
import parquet.filter2.compat.RowGroupFilter;
import java.util.List;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import java.util.Collection;
import java.util.Arrays;
import org.apache.hadoop.fs.PathFilter;
import parquet.hadoop.util.HiddenFileFilter;
import parquet.Preconditions;
import parquet.filter.UnboundRecordFilter;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import parquet.filter2.compat.FilterCompat;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import parquet.hadoop.api.ReadSupport;
import java.io.Closeable;

public class ParquetReader<T> implements Closeable
{
    private final ReadSupport<T> readSupport;
    private final Configuration conf;
    private final Iterator<Footer> footersIterator;
    private final FilterCompat.Filter filter;
    private InternalParquetRecordReader<T> reader;
    
    @Deprecated
    public ParquetReader(final Path file, final ReadSupport<T> readSupport) throws IOException {
        this(new Configuration(), file, readSupport, FilterCompat.NOOP);
    }
    
    @Deprecated
    public ParquetReader(final Configuration conf, final Path file, final ReadSupport<T> readSupport) throws IOException {
        this(conf, file, readSupport, FilterCompat.NOOP);
    }
    
    @Deprecated
    public ParquetReader(final Path file, final ReadSupport<T> readSupport, final UnboundRecordFilter unboundRecordFilter) throws IOException {
        this(new Configuration(), file, readSupport, FilterCompat.get(unboundRecordFilter));
    }
    
    @Deprecated
    public ParquetReader(final Configuration conf, final Path file, final ReadSupport<T> readSupport, final UnboundRecordFilter unboundRecordFilter) throws IOException {
        this(conf, file, readSupport, FilterCompat.get(unboundRecordFilter));
    }
    
    private ParquetReader(final Configuration conf, final Path file, final ReadSupport<T> readSupport, final FilterCompat.Filter filter) throws IOException {
        this.readSupport = readSupport;
        this.filter = Preconditions.checkNotNull(filter, "filter");
        this.conf = conf;
        final FileSystem fs = file.getFileSystem(conf);
        final List<FileStatus> statuses = Arrays.asList(fs.listStatus(file, HiddenFileFilter.INSTANCE));
        final List<Footer> footers = ParquetFileReader.readAllFootersInParallelUsingSummaryFiles(conf, statuses, false);
        this.footersIterator = footers.iterator();
    }
    
    public T read() throws IOException {
        try {
            if (this.reader != null && this.reader.nextKeyValue()) {
                return this.reader.getCurrentValue();
            }
            this.initReader();
            return (this.reader == null) ? null : this.read();
        }
        catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
    
    private void initReader() throws IOException {
        if (this.reader != null) {
            this.reader.close();
            this.reader = null;
        }
        if (this.footersIterator.hasNext()) {
            final Footer footer = this.footersIterator.next();
            final List<BlockMetaData> blocks = footer.getParquetMetadata().getBlocks();
            final MessageType fileSchema = footer.getParquetMetadata().getFileMetaData().getSchema();
            final List<BlockMetaData> filteredBlocks = RowGroupFilter.filterRowGroups(this.filter, blocks, fileSchema);
            (this.reader = new InternalParquetRecordReader<T>(this.readSupport, this.filter)).initialize(fileSchema, footer.getParquetMetadata().getFileMetaData().getKeyValueMetaData(), footer.getFile(), filteredBlocks, this.conf);
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }
    
    public static <T> Builder<T> builder(final ReadSupport<T> readSupport, final Path path) {
        return new Builder<T>((ReadSupport)readSupport, path);
    }
    
    public static class Builder<T>
    {
        private final ReadSupport<T> readSupport;
        private final Path file;
        private Configuration conf;
        private FilterCompat.Filter filter;
        
        private Builder(final ReadSupport<T> readSupport, final Path path) {
            this.readSupport = Preconditions.checkNotNull(readSupport, "readSupport");
            this.file = Preconditions.checkNotNull(path, "path");
            this.conf = new Configuration();
            this.filter = FilterCompat.NOOP;
        }
        
        public Builder<T> withConf(final Configuration conf) {
            this.conf = Preconditions.checkNotNull(conf, "conf");
            return this;
        }
        
        public Builder<T> withFilter(final FilterCompat.Filter filter) {
            this.filter = Preconditions.checkNotNull(filter, "filter");
            return this;
        }
        
        public ParquetReader<T> build() throws IOException {
            return new ParquetReader<T>(this.conf, this.file, this.readSupport, this.filter, null);
        }
    }
}
