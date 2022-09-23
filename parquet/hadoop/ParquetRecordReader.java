// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import parquet.schema.MessageType;
import parquet.hadoop.metadata.ParquetMetadata;
import org.apache.hadoop.fs.Path;
import java.util.Arrays;
import parquet.hadoop.metadata.BlockMetaData;
import java.util.ArrayList;
import java.util.HashSet;
import parquet.filter2.compat.RowGroupFilter;
import parquet.format.converter.ParquetMetadataConverter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.JobContext;
import parquet.hadoop.util.ContextUtil;
import parquet.hadoop.util.counters.BenchmarkCounter;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.InputSplit;
import java.io.IOException;
import parquet.filter.UnboundRecordFilter;
import parquet.filter2.compat.FilterCompat;
import parquet.hadoop.api.ReadSupport;
import parquet.Log;
import org.apache.hadoop.mapreduce.RecordReader;

public class ParquetRecordReader<T> extends RecordReader<Void, T>
{
    private static final Log LOG;
    private final InternalParquetRecordReader<T> internalReader;
    
    public ParquetRecordReader(final ReadSupport<T> readSupport) {
        this(readSupport, FilterCompat.NOOP);
    }
    
    public ParquetRecordReader(final ReadSupport<T> readSupport, final FilterCompat.Filter filter) {
        this.internalReader = new InternalParquetRecordReader<T>(readSupport, filter);
    }
    
    @Deprecated
    public ParquetRecordReader(final ReadSupport<T> readSupport, final UnboundRecordFilter filter) {
        this(readSupport, FilterCompat.get(filter));
    }
    
    public void close() throws IOException {
        this.internalReader.close();
    }
    
    public Void getCurrentKey() throws IOException, InterruptedException {
        return null;
    }
    
    public T getCurrentValue() throws IOException, InterruptedException {
        return this.internalReader.getCurrentValue();
    }
    
    public float getProgress() throws IOException, InterruptedException {
        return this.internalReader.getProgress();
    }
    
    public void initialize(final InputSplit inputSplit, final TaskAttemptContext context) throws IOException, InterruptedException {
        if (context instanceof TaskInputOutputContext) {
            BenchmarkCounter.initCounterFromContext((TaskInputOutputContext<?, ?, ?, ?>)context);
        }
        else {
            ParquetRecordReader.LOG.error("Can not initialize counter due to context is not a instance of TaskInputOutputContext, but is " + context.getClass().getCanonicalName());
        }
        this.initializeInternalReader(this.toParquetSplit(inputSplit), ContextUtil.getConfiguration((JobContext)context));
    }
    
    public void initialize(final InputSplit inputSplit, final Configuration configuration, final Reporter reporter) throws IOException, InterruptedException {
        BenchmarkCounter.initCounterFromReporter(reporter, configuration);
        this.initializeInternalReader(this.toParquetSplit(inputSplit), configuration);
    }
    
    private void initializeInternalReader(final ParquetInputSplit split, final Configuration configuration) throws IOException {
        final Path path = split.getPath();
        final long[] rowGroupOffsets = split.getRowGroupOffsets();
        ParquetMetadata footer;
        List<BlockMetaData> filteredBlocks;
        if (rowGroupOffsets == null) {
            footer = ParquetFileReader.readFooter(configuration, path, ParquetMetadataConverter.range(split.getStart(), split.getEnd()));
            final MessageType fileSchema = footer.getFileMetaData().getSchema();
            final FilterCompat.Filter filter = ParquetInputFormat.getFilter(configuration);
            filteredBlocks = RowGroupFilter.filterRowGroups(filter, footer.getBlocks(), fileSchema);
        }
        else {
            footer = ParquetFileReader.readFooter(configuration, path, ParquetMetadataConverter.NO_FILTER);
            final Set<Long> offsets = new HashSet<Long>();
            for (final long offset : rowGroupOffsets) {
                offsets.add(offset);
            }
            filteredBlocks = new ArrayList<BlockMetaData>();
            for (final BlockMetaData block : footer.getBlocks()) {
                if (offsets.contains(block.getStartingPos())) {
                    filteredBlocks.add(block);
                }
            }
            if (filteredBlocks.size() != rowGroupOffsets.length) {
                final long[] foundRowGroupOffsets = new long[footer.getBlocks().size()];
                for (int i = 0; i < foundRowGroupOffsets.length; ++i) {
                    foundRowGroupOffsets[i] = footer.getBlocks().get(i).getStartingPos();
                }
                throw new IllegalStateException("All the offsets listed in the split should be found in the file. expected: " + Arrays.toString(rowGroupOffsets) + " found: " + filteredBlocks + " out of: " + Arrays.toString(foundRowGroupOffsets) + " in range " + split.getStart() + ", " + split.getEnd());
            }
        }
        final MessageType fileSchema = footer.getFileMetaData().getSchema();
        final Map<String, String> fileMetaData = footer.getFileMetaData().getKeyValueMetaData();
        this.internalReader.initialize(fileSchema, fileMetaData, path, filteredBlocks, configuration);
    }
    
    public boolean nextKeyValue() throws IOException, InterruptedException {
        return this.internalReader.nextKeyValue();
    }
    
    private ParquetInputSplit toParquetSplit(final InputSplit split) throws IOException {
        if (split instanceof ParquetInputSplit) {
            return (ParquetInputSplit)split;
        }
        if (split instanceof FileSplit) {
            return ParquetInputSplit.from((FileSplit)split);
        }
        if (split instanceof org.apache.hadoop.mapred.FileSplit) {
            return ParquetInputSplit.from((org.apache.hadoop.mapred.FileSplit)split);
        }
        throw new IllegalArgumentException("Invalid split (not a FileSplit or ParquetInputSplit): " + split);
    }
    
    static {
        LOG = Log.getLog(ParquetRecordReader.class);
    }
}
