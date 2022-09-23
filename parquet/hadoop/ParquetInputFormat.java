// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import parquet.hadoop.util.HiddenFileFilter;
import org.apache.hadoop.fs.FileStatus;
import parquet.hadoop.metadata.GlobalMetaData;
import parquet.hadoop.api.InitContext;
import parquet.io.ParquetDecodingException;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.InputSplit;
import parquet.filter2.compat.FilterCompat;
import java.io.IOException;
import parquet.hadoop.util.SerializationUtil;
import parquet.filter2.predicate.FilterPredicate;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.conf.Configurable;
import parquet.hadoop.util.ConfigurationUtil;
import parquet.Preconditions;
import parquet.filter.UnboundRecordFilter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.JobContext;
import parquet.hadoop.util.ContextUtil;
import org.apache.hadoop.mapreduce.Job;
import parquet.hadoop.api.ReadSupport;
import parquet.Log;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class ParquetInputFormat<T> extends FileInputFormat<Void, T>
{
    private static final Log LOG;
    public static final String READ_SUPPORT_CLASS = "parquet.read.support.class";
    public static final String UNBOUND_RECORD_FILTER = "parquet.read.filter";
    public static final String STRICT_TYPE_CHECKING = "parquet.strict.typing";
    public static final String FILTER_PREDICATE = "parquet.private.read.filter.predicate";
    public static final String TASK_SIDE_METADATA = "parquet.task.side.metadata";
    private static final int MIN_FOOTER_CACHE_SIZE = 100;
    private LruCache<FileStatusWrapper, FootersCacheValue> footersCache;
    private final Class<? extends ReadSupport<T>> readSupportClass;
    
    public static void setTaskSideMetaData(final Job job, final boolean taskSideMetadata) {
        ContextUtil.getConfiguration((JobContext)job).setBoolean("parquet.task.side.metadata", taskSideMetadata);
    }
    
    public static boolean isTaskSideMetaData(final Configuration configuration) {
        return configuration.getBoolean("parquet.task.side.metadata", Boolean.TRUE);
    }
    
    public static void setReadSupportClass(final Job job, final Class<?> readSupportClass) {
        ContextUtil.getConfiguration((JobContext)job).set("parquet.read.support.class", readSupportClass.getName());
    }
    
    public static void setUnboundRecordFilter(final Job job, final Class<? extends UnboundRecordFilter> filterClass) {
        final Configuration conf = ContextUtil.getConfiguration((JobContext)job);
        Preconditions.checkArgument(getFilterPredicate(conf) == null, "You cannot provide an UnboundRecordFilter after providing a FilterPredicate");
        conf.set("parquet.read.filter", filterClass.getName());
    }
    
    @Deprecated
    public static Class<?> getUnboundRecordFilter(final Configuration configuration) {
        return ConfigurationUtil.getClassFromConfig(configuration, "parquet.read.filter", UnboundRecordFilter.class);
    }
    
    private static UnboundRecordFilter getUnboundRecordFilterInstance(final Configuration configuration) {
        final Class<?> clazz = ConfigurationUtil.getClassFromConfig(configuration, "parquet.read.filter", UnboundRecordFilter.class);
        if (clazz == null) {
            return null;
        }
        try {
            final UnboundRecordFilter unboundRecordFilter = (UnboundRecordFilter)clazz.newInstance();
            if (unboundRecordFilter instanceof Configurable) {
                ((Configurable)unboundRecordFilter).setConf(configuration);
            }
            return unboundRecordFilter;
        }
        catch (InstantiationException e) {
            throw new BadConfigurationException("could not instantiate unbound record filter class", e);
        }
        catch (IllegalAccessException e2) {
            throw new BadConfigurationException("could not instantiate unbound record filter class", e2);
        }
    }
    
    public static void setReadSupportClass(final JobConf conf, final Class<?> readSupportClass) {
        conf.set("parquet.read.support.class", readSupportClass.getName());
    }
    
    public static Class<?> getReadSupportClass(final Configuration configuration) {
        return ConfigurationUtil.getClassFromConfig(configuration, "parquet.read.support.class", ReadSupport.class);
    }
    
    public static void setFilterPredicate(final Configuration configuration, final FilterPredicate filterPredicate) {
        Preconditions.checkArgument(getUnboundRecordFilter(configuration) == null, "You cannot provide a FilterPredicate after providing an UnboundRecordFilter");
        configuration.set("parquet.private.read.filter.predicate.human.readable", filterPredicate.toString());
        try {
            SerializationUtil.writeObjectToConfAsBase64("parquet.private.read.filter.predicate", filterPredicate, configuration);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static FilterPredicate getFilterPredicate(final Configuration configuration) {
        try {
            return SerializationUtil.readObjectFromConfAsBase64("parquet.private.read.filter.predicate", configuration);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static FilterCompat.Filter getFilter(final Configuration conf) {
        return FilterCompat.get(getFilterPredicate(conf), getUnboundRecordFilterInstance(conf));
    }
    
    public ParquetInputFormat() {
        this.readSupportClass = null;
    }
    
    public <S extends ReadSupport<T>> ParquetInputFormat(final Class<S> readSupportClass) {
        this.readSupportClass = readSupportClass;
    }
    
    public RecordReader<Void, T> createRecordReader(final InputSplit inputSplit, final TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        final Configuration conf = ContextUtil.getConfiguration((JobContext)taskAttemptContext);
        final ReadSupport<T> readSupport = this.getReadSupport(conf);
        return (RecordReader<Void, T>)new ParquetRecordReader((ReadSupport<Object>)readSupport, getFilter(conf));
    }
    
    @Deprecated
    ReadSupport<T> getReadSupport(final Configuration configuration) {
        return getReadSupportInstance((Class<? extends ReadSupport<T>>)((this.readSupportClass == null) ? getReadSupportClass(configuration) : this.readSupportClass));
    }
    
    public static <T> ReadSupport<T> getReadSupportInstance(final Configuration configuration) {
        return getReadSupportInstance((Class<? extends ReadSupport<T>>)getReadSupportClass(configuration));
    }
    
    static <T> ReadSupport<T> getReadSupportInstance(final Class<? extends ReadSupport<T>> readSupportClass) {
        try {
            return (ReadSupport<T>)readSupportClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new BadConfigurationException("could not instantiate read support class", e);
        }
        catch (IllegalAccessException e2) {
            throw new BadConfigurationException("could not instantiate read support class", e2);
        }
    }
    
    public List<InputSplit> getSplits(final JobContext jobContext) throws IOException {
        final Configuration configuration = ContextUtil.getConfiguration(jobContext);
        final List<InputSplit> splits = new ArrayList<InputSplit>();
        if (isTaskSideMetaData(configuration)) {
            for (final InputSplit split : super.getSplits(jobContext)) {
                Preconditions.checkArgument(split instanceof FileSplit, "Cannot wrap non-FileSplit: " + split);
                splits.add((InputSplit)ParquetInputSplit.from((FileSplit)split));
            }
            return splits;
        }
        splits.addAll((Collection<? extends InputSplit>)this.getSplits(configuration, this.getFooters(jobContext)));
        return splits;
    }
    
    @Deprecated
    public List<ParquetInputSplit> getSplits(final Configuration configuration, final List<Footer> footers) throws IOException {
        final boolean strictTypeChecking = configuration.getBoolean("parquet.strict.typing", true);
        final long maxSplitSize = configuration.getLong("mapred.max.split.size", Long.MAX_VALUE);
        final long minSplitSize = Math.max(this.getFormatMinSplitSize(), configuration.getLong("mapred.min.split.size", 0L));
        if (maxSplitSize < 0L || minSplitSize < 0L) {
            throw new ParquetDecodingException("maxSplitSize or minSplitSize should not be negative: maxSplitSize = " + maxSplitSize + "; minSplitSize = " + minSplitSize);
        }
        final GlobalMetaData globalMetaData = ParquetFileWriter.getGlobalMetaData(footers, strictTypeChecking);
        final ReadSupport.ReadContext readContext = this.getReadSupport(configuration).init(new InitContext(configuration, globalMetaData.getKeyValueMetaData(), globalMetaData.getSchema()));
        return new ClientSideMetadataSplitStrategy().getSplits(configuration, footers, maxSplitSize, minSplitSize, readContext);
    }
    
    protected List<FileStatus> listStatus(final JobContext jobContext) throws IOException {
        return getAllFileRecursively(super.listStatus(jobContext), ContextUtil.getConfiguration(jobContext));
    }
    
    private static List<FileStatus> getAllFileRecursively(final List<FileStatus> files, final Configuration conf) throws IOException {
        final List<FileStatus> result = new ArrayList<FileStatus>();
        for (final FileStatus file : files) {
            if (file.isDir()) {
                final Path p = file.getPath();
                final FileSystem fs = p.getFileSystem(conf);
                staticAddInputPathRecursively(result, fs, p, HiddenFileFilter.INSTANCE);
            }
            else {
                result.add(file);
            }
        }
        ParquetInputFormat.LOG.info("Total input paths to process : " + result.size());
        return result;
    }
    
    private static void staticAddInputPathRecursively(final List<FileStatus> result, final FileSystem fs, final Path path, final PathFilter inputFilter) throws IOException {
        for (final FileStatus stat : fs.listStatus(path, inputFilter)) {
            if (stat.isDir()) {
                staticAddInputPathRecursively(result, fs, stat.getPath(), inputFilter);
            }
            else {
                result.add(stat);
            }
        }
    }
    
    public List<Footer> getFooters(final JobContext jobContext) throws IOException {
        final List<FileStatus> statuses = this.listStatus(jobContext);
        if (statuses.isEmpty()) {
            return Collections.emptyList();
        }
        final Configuration config = ContextUtil.getConfiguration(jobContext);
        final List<Footer> footers = new ArrayList<Footer>(statuses.size());
        final Set<FileStatus> missingStatuses = new HashSet<FileStatus>();
        final Map<Path, FileStatusWrapper> missingStatusesMap = new HashMap<Path, FileStatusWrapper>(missingStatuses.size());
        if (this.footersCache == null) {
            this.footersCache = new LruCache<FileStatusWrapper, FootersCacheValue>(Math.max(statuses.size(), 100));
        }
        for (final FileStatus status : statuses) {
            final FileStatusWrapper statusWrapper = new FileStatusWrapper(status);
            final FootersCacheValue cacheEntry = this.footersCache.getCurrentValue(statusWrapper);
            if (Log.DEBUG) {
                ParquetInputFormat.LOG.debug("Cache entry " + ((cacheEntry == null) ? "not " : "") + " found for '" + status.getPath() + "'");
            }
            if (cacheEntry != null) {
                footers.add(cacheEntry.getFooter());
            }
            else {
                missingStatuses.add(status);
                missingStatusesMap.put(status.getPath(), statusWrapper);
            }
        }
        if (Log.DEBUG) {
            ParquetInputFormat.LOG.debug("found " + footers.size() + " footers in cache and adding up " + "to " + missingStatuses.size() + " missing footers to the cache");
        }
        if (missingStatuses.isEmpty()) {
            return footers;
        }
        final List<Footer> newFooters = this.getFooters(config, missingStatuses);
        for (final Footer newFooter : newFooters) {
            final FileStatusWrapper fileStatus = missingStatusesMap.get(newFooter.getFile());
            this.footersCache.put(fileStatus, new FootersCacheValue(fileStatus, newFooter));
        }
        footers.addAll(newFooters);
        return footers;
    }
    
    public List<Footer> getFooters(final Configuration configuration, final List<FileStatus> statuses) throws IOException {
        return this.getFooters(configuration, (Collection<FileStatus>)statuses);
    }
    
    public List<Footer> getFooters(final Configuration configuration, final Collection<FileStatus> statuses) throws IOException {
        if (Log.DEBUG) {
            ParquetInputFormat.LOG.debug("reading " + statuses.size() + " files");
        }
        final boolean taskSideMetaData = isTaskSideMetaData(configuration);
        return ParquetFileReader.readAllFootersInParallelUsingSummaryFiles(configuration, statuses, taskSideMetaData);
    }
    
    public GlobalMetaData getGlobalMetaData(final JobContext jobContext) throws IOException {
        return ParquetFileWriter.getGlobalMetaData(this.getFooters(jobContext));
    }
    
    static {
        LOG = Log.getLog(ParquetInputFormat.class);
    }
    
    static final class FootersCacheValue implements LruCache.Value<FileStatusWrapper, FootersCacheValue>
    {
        private final long modificationTime;
        private final Footer footer;
        
        public FootersCacheValue(final FileStatusWrapper status, final Footer footer) {
            this.modificationTime = status.getModificationTime();
            this.footer = new Footer(footer.getFile(), footer.getParquetMetadata());
        }
        
        @Override
        public boolean isCurrent(final FileStatusWrapper key) {
            final long currentModTime = key.getModificationTime();
            final boolean isCurrent = this.modificationTime >= currentModTime;
            if (Log.DEBUG && !isCurrent) {
                ParquetInputFormat.LOG.debug("The cache value for '" + key + "' is not current: " + "cached modification time=" + this.modificationTime + ", " + "current modification time: " + currentModTime);
            }
            return isCurrent;
        }
        
        public Footer getFooter() {
            return this.footer;
        }
        
        @Override
        public boolean isNewerThan(final FootersCacheValue otherValue) {
            return otherValue == null || this.modificationTime > otherValue.modificationTime;
        }
        
        public Path getPath() {
            return this.footer.getFile();
        }
    }
    
    static final class FileStatusWrapper
    {
        private final FileStatus status;
        
        public FileStatusWrapper(final FileStatus fileStatus) {
            if (fileStatus == null) {
                throw new IllegalArgumentException("FileStatus object cannot be null");
            }
            this.status = fileStatus;
        }
        
        public long getModificationTime() {
            return this.status.getModificationTime();
        }
        
        @Override
        public int hashCode() {
            return this.status.hashCode();
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof FileStatusWrapper && this.status.equals(((FileStatusWrapper)other).status);
        }
        
        @Override
        public String toString() {
            return this.status.getPath().toString();
        }
    }
}
