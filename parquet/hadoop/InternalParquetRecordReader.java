// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import parquet.io.ParquetDecodingException;
import parquet.schema.Type;
import parquet.schema.GroupType;
import java.util.Iterator;
import parquet.column.ColumnDescriptor;
import parquet.hadoop.api.InitContext;
import org.apache.hadoop.conf.Configuration;
import parquet.hadoop.metadata.BlockMetaData;
import java.util.List;
import java.util.Map;
import parquet.io.MessageColumnIO;
import parquet.column.page.PageReadStore;
import parquet.hadoop.util.counters.BenchmarkCounter;
import java.io.IOException;
import parquet.filter.UnboundRecordFilter;
import parquet.Preconditions;
import org.apache.hadoop.fs.Path;
import parquet.io.RecordReader;
import parquet.io.api.RecordMaterializer;
import parquet.hadoop.api.ReadSupport;
import parquet.schema.MessageType;
import parquet.filter2.compat.FilterCompat;
import parquet.io.ColumnIOFactory;
import parquet.Log;

class InternalParquetRecordReader<T>
{
    private static final Log LOG;
    private final ColumnIOFactory columnIOFactory;
    private final FilterCompat.Filter filter;
    private MessageType requestedSchema;
    private MessageType fileSchema;
    private int columnCount;
    private final ReadSupport<T> readSupport;
    private RecordMaterializer<T> recordConverter;
    private T currentValue;
    private long total;
    private long current;
    private int currentBlock;
    private ParquetFileReader reader;
    private RecordReader<T> recordReader;
    private boolean strictTypeChecking;
    private long totalTimeSpentReadingBytes;
    private long totalTimeSpentProcessingRecords;
    private long startedAssemblingCurrentBlockAt;
    private long totalCountLoadedSoFar;
    private Path file;
    
    public InternalParquetRecordReader(final ReadSupport<T> readSupport, final FilterCompat.Filter filter) {
        this.columnIOFactory = new ColumnIOFactory();
        this.current = 0L;
        this.currentBlock = -1;
        this.totalCountLoadedSoFar = 0L;
        this.readSupport = readSupport;
        this.filter = Preconditions.checkNotNull(filter, "filter");
    }
    
    public InternalParquetRecordReader(final ReadSupport<T> readSupport) {
        this(readSupport, FilterCompat.NOOP);
    }
    
    @Deprecated
    public InternalParquetRecordReader(final ReadSupport<T> readSupport, final UnboundRecordFilter filter) {
        this(readSupport, FilterCompat.get(filter));
    }
    
    private void checkRead() throws IOException {
        if (this.current == this.totalCountLoadedSoFar) {
            if (this.current != 0L) {
                this.totalTimeSpentProcessingRecords += System.currentTimeMillis() - this.startedAssemblingCurrentBlockAt;
                if (Log.INFO) {
                    InternalParquetRecordReader.LOG.info("Assembled and processed " + this.totalCountLoadedSoFar + " records from " + this.columnCount + " columns in " + this.totalTimeSpentProcessingRecords + " ms: " + this.totalCountLoadedSoFar / (float)this.totalTimeSpentProcessingRecords + " rec/ms, " + this.totalCountLoadedSoFar * (float)this.columnCount / this.totalTimeSpentProcessingRecords + " cell/ms");
                    final long totalTime = this.totalTimeSpentProcessingRecords + this.totalTimeSpentReadingBytes;
                    if (totalTime != 0L) {
                        final long percentReading = 100L * this.totalTimeSpentReadingBytes / totalTime;
                        final long percentProcessing = 100L * this.totalTimeSpentProcessingRecords / totalTime;
                        InternalParquetRecordReader.LOG.info("time spent so far " + percentReading + "% reading (" + this.totalTimeSpentReadingBytes + " ms) and " + percentProcessing + "% processing (" + this.totalTimeSpentProcessingRecords + " ms)");
                    }
                }
            }
            InternalParquetRecordReader.LOG.info("at row " + this.current + ". reading next block");
            final long t0 = System.currentTimeMillis();
            final PageReadStore pages = this.reader.readNextRowGroup();
            if (pages == null) {
                throw new IOException("expecting more rows but reached last block. Read " + this.current + " out of " + this.total);
            }
            final long timeSpentReading = System.currentTimeMillis() - t0;
            this.totalTimeSpentReadingBytes += timeSpentReading;
            BenchmarkCounter.incrementTime(timeSpentReading);
            if (Log.INFO) {
                InternalParquetRecordReader.LOG.info("block read in memory in " + timeSpentReading + " ms. row count = " + pages.getRowCount());
            }
            if (Log.DEBUG) {
                InternalParquetRecordReader.LOG.debug("initializing Record assembly with requested schema " + this.requestedSchema);
            }
            final MessageColumnIO columnIO = this.columnIOFactory.getColumnIO(this.requestedSchema, this.fileSchema, this.strictTypeChecking);
            this.recordReader = columnIO.getRecordReader(pages, this.recordConverter, this.filter);
            this.startedAssemblingCurrentBlockAt = System.currentTimeMillis();
            this.totalCountLoadedSoFar += pages.getRowCount();
            ++this.currentBlock;
        }
    }
    
    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }
    
    public Void getCurrentKey() throws IOException, InterruptedException {
        return null;
    }
    
    public T getCurrentValue() throws IOException, InterruptedException {
        return this.currentValue;
    }
    
    public float getProgress() throws IOException, InterruptedException {
        return this.current / (float)this.total;
    }
    
    public void initialize(final MessageType fileSchema, final Map<String, String> fileMetadata, final Path file, final List<BlockMetaData> blocks, final Configuration configuration) throws IOException {
        final ReadSupport.ReadContext readContext = this.readSupport.init(new InitContext(configuration, toSetMultiMap(fileMetadata), fileSchema));
        this.requestedSchema = readContext.getRequestedSchema();
        this.fileSchema = fileSchema;
        this.file = file;
        this.columnCount = this.requestedSchema.getPaths().size();
        this.recordConverter = this.readSupport.prepareForRead(configuration, fileMetadata, fileSchema, readContext);
        this.strictTypeChecking = configuration.getBoolean("parquet.strict.typing", true);
        final List<ColumnDescriptor> columns = this.requestedSchema.getColumns();
        this.reader = new ParquetFileReader(configuration, file, blocks, columns);
        for (final BlockMetaData block : blocks) {
            this.total += block.getRowCount();
        }
        InternalParquetRecordReader.LOG.info("RecordReader initialized will read a total of " + this.total + " records.");
    }
    
    private boolean contains(final GroupType group, final String[] path, final int index) {
        if (index == path.length) {
            return false;
        }
        if (!group.containsField(path[index])) {
            return false;
        }
        final Type type = group.getType(path[index]);
        if (type.isPrimitive()) {
            return index + 1 == path.length;
        }
        return this.contains(type.asGroupType(), path, index + 1);
    }
    
    public boolean nextKeyValue() throws IOException, InterruptedException {
        boolean recordFound = false;
        while (!recordFound) {
            if (this.current >= this.total) {
                return false;
            }
            try {
                this.checkRead();
                this.currentValue = this.recordReader.read();
                ++this.current;
                if (this.recordReader.shouldSkipCurrentRecord()) {
                    if (!Log.DEBUG) {
                        continue;
                    }
                    InternalParquetRecordReader.LOG.debug("skipping record");
                    continue;
                }
                if (this.currentValue == null) {
                    this.current = this.totalCountLoadedSoFar;
                    if (!Log.DEBUG) {
                        continue;
                    }
                    InternalParquetRecordReader.LOG.debug("filtered record reader reached end of block");
                    continue;
                }
                recordFound = true;
                if (!Log.DEBUG) {
                    continue;
                }
                InternalParquetRecordReader.LOG.debug("read value: " + this.currentValue);
                continue;
            }
            catch (RuntimeException e) {
                throw new ParquetDecodingException(String.format("Can not read value at %d in block %d in file %s", this.current, this.currentBlock, this.file), e);
            }
            break;
        }
        return true;
    }
    
    private static <K, V> Map<K, Set<V>> toSetMultiMap(final Map<K, V> map) {
        final Map<K, Set<V>> setMultiMap = new HashMap<K, Set<V>>();
        for (final Map.Entry<K, V> entry : map.entrySet()) {
            final Set<V> set = new HashSet<V>();
            set.add(entry.getValue());
            setMultiMap.put(entry.getKey(), Collections.unmodifiableSet((Set<? extends V>)set));
        }
        return Collections.unmodifiableMap((Map<? extends K, ? extends Set<V>>)setMultiMap);
    }
    
    static {
        LOG = Log.getLog(InternalParquetRecordReader.class);
    }
}
