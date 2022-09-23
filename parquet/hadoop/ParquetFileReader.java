// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.io.SequenceInputStream;
import parquet.bytes.BytesInput;
import parquet.format.DataPageHeaderV2;
import parquet.format.DataPageHeader;
import parquet.format.DictionaryPageHeader;
import parquet.column.page.DataPageV2;
import parquet.column.statistics.Statistics;
import parquet.column.page.DataPageV1;
import parquet.column.page.DictionaryPage;
import parquet.io.ParquetDecodingException;
import parquet.column.page.DataPage;
import parquet.format.Util;
import parquet.format.PageHeader;
import java.io.ByteArrayInputStream;
import parquet.hadoop.util.counters.BenchmarkCounter;
import parquet.hadoop.metadata.ColumnChunkMetaData;
import parquet.column.page.PageReadStore;
import java.io.InputStream;
import parquet.bytes.BytesUtils;
import org.apache.hadoop.fs.FileSystem;
import java.util.Arrays;
import org.apache.hadoop.fs.PathFilter;
import parquet.hadoop.util.HiddenFileFilter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.Iterator;
import parquet.hadoop.metadata.ParquetMetadata;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.IOException;
import java.util.Collection;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.conf.Configuration;
import parquet.column.ColumnDescriptor;
import parquet.hadoop.metadata.ColumnPath;
import java.util.Map;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataInputStream;
import parquet.hadoop.metadata.BlockMetaData;
import java.util.List;
import parquet.format.converter.ParquetMetadataConverter;
import parquet.Log;
import java.io.Closeable;

public class ParquetFileReader implements Closeable
{
    private static final Log LOG;
    public static String PARQUET_READ_PARALLELISM;
    private static ParquetMetadataConverter converter;
    private final CodecFactory codecFactory;
    private final List<BlockMetaData> blocks;
    private final FSDataInputStream f;
    private final Path filePath;
    private int currentBlock;
    private final Map<ColumnPath, ColumnDescriptor> paths;
    
    @Deprecated
    public static List<Footer> readAllFootersInParallelUsingSummaryFiles(final Configuration configuration, final List<FileStatus> partFiles) throws IOException {
        return readAllFootersInParallelUsingSummaryFiles(configuration, partFiles, false);
    }
    
    private static ParquetMetadataConverter.MetadataFilter filter(final boolean skipRowGroups) {
        return skipRowGroups ? ParquetMetadataConverter.SKIP_ROW_GROUPS : ParquetMetadataConverter.NO_FILTER;
    }
    
    public static List<Footer> readAllFootersInParallelUsingSummaryFiles(final Configuration configuration, final Collection<FileStatus> partFiles, final boolean skipRowGroups) throws IOException {
        final Set<Path> parents = new HashSet<Path>();
        for (final FileStatus part : partFiles) {
            parents.add(part.getPath().getParent());
        }
        final List<Callable<Map<Path, Footer>>> summaries = new ArrayList<Callable<Map<Path, Footer>>>();
        for (final Path path : parents) {
            summaries.add(new Callable<Map<Path, Footer>>() {
                @Override
                public Map<Path, Footer> call() throws Exception {
                    final ParquetMetadata mergedMetadata = ParquetFileReader.readSummaryMetadata(configuration, path, skipRowGroups);
                    if (mergedMetadata != null) {
                        List<Footer> footers;
                        if (skipRowGroups) {
                            footers = new ArrayList<Footer>();
                            for (final FileStatus f : partFiles) {
                                footers.add(new Footer(f.getPath(), mergedMetadata));
                            }
                        }
                        else {
                            footers = ParquetFileReader.footersFromSummaryFile(path, mergedMetadata);
                        }
                        final Map<Path, Footer> map = new HashMap<Path, Footer>();
                        for (Footer footer : footers) {
                            footer = new Footer(new Path(path, footer.getFile().getName()), footer.getParquetMetadata());
                            map.put(footer.getFile(), footer);
                        }
                        return map;
                    }
                    return Collections.emptyMap();
                }
            });
        }
        final Map<Path, Footer> cache = new HashMap<Path, Footer>();
        try {
            final List<Map<Path, Footer>> footersFromSummaries = runAllInParallel(configuration.getInt(ParquetFileReader.PARQUET_READ_PARALLELISM, 5), summaries);
            for (final Map<Path, Footer> footers : footersFromSummaries) {
                cache.putAll(footers);
            }
        }
        catch (ExecutionException e) {
            throw new IOException("Error reading summaries", e);
        }
        final List<Footer> result = new ArrayList<Footer>(partFiles.size());
        final List<FileStatus> toRead = new ArrayList<FileStatus>();
        for (final FileStatus part2 : partFiles) {
            final Footer f = cache.get(part2.getPath());
            if (f != null) {
                result.add(f);
            }
            else {
                toRead.add(part2);
            }
        }
        if (toRead.size() > 0) {
            if (Log.INFO) {
                ParquetFileReader.LOG.info("reading another " + toRead.size() + " footers");
            }
            result.addAll(readAllFootersInParallel(configuration, toRead, skipRowGroups));
        }
        return result;
    }
    
    private static <T> List<T> runAllInParallel(final int parallelism, final List<Callable<T>> toRun) throws ExecutionException {
        ParquetFileReader.LOG.info("Initiating action with parallelism: " + parallelism);
        final ExecutorService threadPool = Executors.newFixedThreadPool(parallelism);
        try {
            final List<Future<T>> futures = new ArrayList<Future<T>>();
            for (final Callable<T> callable : toRun) {
                futures.add(threadPool.submit(callable));
            }
            final List<T> result = new ArrayList<T>(toRun.size());
            for (final Future<T> future : futures) {
                try {
                    result.add(future.get());
                }
                catch (InterruptedException e) {
                    throw new RuntimeException("The thread was interrupted", e);
                }
            }
            return result;
        }
        finally {
            threadPool.shutdownNow();
        }
    }
    
    @Deprecated
    public static List<Footer> readAllFootersInParallel(final Configuration configuration, final List<FileStatus> partFiles) throws IOException {
        return readAllFootersInParallel(configuration, partFiles, false);
    }
    
    public static List<Footer> readAllFootersInParallel(final Configuration configuration, final List<FileStatus> partFiles, final boolean skipRowGroups) throws IOException {
        final List<Callable<Footer>> footers = new ArrayList<Callable<Footer>>();
        for (final FileStatus currentFile : partFiles) {
            footers.add(new Callable<Footer>() {
                @Override
                public Footer call() throws Exception {
                    try {
                        return new Footer(currentFile.getPath(), ParquetFileReader.readFooter(configuration, currentFile, filter(skipRowGroups)));
                    }
                    catch (IOException e) {
                        throw new IOException("Could not read footer for file " + currentFile, e);
                    }
                }
            });
        }
        try {
            return runAllInParallel(configuration.getInt(ParquetFileReader.PARQUET_READ_PARALLELISM, 5), footers);
        }
        catch (ExecutionException e) {
            throw new IOException("Could not read footer: " + e.getMessage(), e.getCause());
        }
    }
    
    public static List<Footer> readAllFootersInParallel(final Configuration configuration, final FileStatus fileStatus) throws IOException {
        final List<FileStatus> statuses = listFiles(configuration, fileStatus);
        return readAllFootersInParallel(configuration, statuses, false);
    }
    
    @Deprecated
    public static List<Footer> readFooters(final Configuration configuration, final Path path) throws IOException {
        return readFooters(configuration, status(configuration, path));
    }
    
    private static FileStatus status(final Configuration configuration, final Path path) throws IOException {
        return path.getFileSystem(configuration).getFileStatus(path);
    }
    
    @Deprecated
    public static List<Footer> readFooters(final Configuration configuration, final FileStatus pathStatus) throws IOException {
        return readFooters(configuration, pathStatus, false);
    }
    
    public static List<Footer> readFooters(final Configuration configuration, final FileStatus pathStatus, final boolean skipRowGroups) throws IOException {
        final List<FileStatus> files = listFiles(configuration, pathStatus);
        return readAllFootersInParallelUsingSummaryFiles(configuration, files, skipRowGroups);
    }
    
    private static List<FileStatus> listFiles(final Configuration conf, final FileStatus fileStatus) throws IOException {
        if (fileStatus.isDir()) {
            final FileSystem fs = fileStatus.getPath().getFileSystem(conf);
            final FileStatus[] list = fs.listStatus(fileStatus.getPath(), HiddenFileFilter.INSTANCE);
            final List<FileStatus> result = new ArrayList<FileStatus>();
            for (final FileStatus sub : list) {
                result.addAll(listFiles(conf, sub));
            }
            return result;
        }
        return Arrays.asList(fileStatus);
    }
    
    public static List<Footer> readSummaryFile(final Configuration configuration, final FileStatus summaryStatus) throws IOException {
        final Path parent = summaryStatus.getPath().getParent();
        final ParquetMetadata mergedFooters = readFooter(configuration, summaryStatus, filter(false));
        return footersFromSummaryFile(parent, mergedFooters);
    }
    
    static ParquetMetadata readSummaryMetadata(final Configuration configuration, final Path basePath, final boolean skipRowGroups) throws IOException {
        final Path metadataFile = new Path(basePath, "_metadata");
        final Path commonMetaDataFile = new Path(basePath, "_common_metadata");
        final FileSystem fileSystem = basePath.getFileSystem(configuration);
        if (skipRowGroups && fileSystem.exists(commonMetaDataFile)) {
            if (Log.INFO) {
                ParquetFileReader.LOG.info("reading summary file: " + commonMetaDataFile);
            }
            return readFooter(configuration, commonMetaDataFile, filter(skipRowGroups));
        }
        if (fileSystem.exists(metadataFile)) {
            if (Log.INFO) {
                ParquetFileReader.LOG.info("reading summary file: " + metadataFile);
            }
            return readFooter(configuration, metadataFile, filter(skipRowGroups));
        }
        return null;
    }
    
    static List<Footer> footersFromSummaryFile(final Path parent, final ParquetMetadata mergedFooters) {
        final Map<Path, ParquetMetadata> footers = new HashMap<Path, ParquetMetadata>();
        final List<BlockMetaData> blocks = mergedFooters.getBlocks();
        for (final BlockMetaData block : blocks) {
            final String path = block.getPath();
            final Path fullPath = new Path(parent, path);
            ParquetMetadata current = footers.get(fullPath);
            if (current == null) {
                current = new ParquetMetadata(mergedFooters.getFileMetaData(), new ArrayList<BlockMetaData>());
                footers.put(fullPath, current);
            }
            current.getBlocks().add(block);
        }
        final List<Footer> result = new ArrayList<Footer>();
        for (final Map.Entry<Path, ParquetMetadata> entry : footers.entrySet()) {
            result.add(new Footer(entry.getKey(), entry.getValue()));
        }
        return result;
    }
    
    @Deprecated
    public static final ParquetMetadata readFooter(final Configuration configuration, final Path file) throws IOException {
        return readFooter(configuration, file, ParquetMetadataConverter.NO_FILTER);
    }
    
    public static ParquetMetadata readFooter(final Configuration configuration, final Path file, final ParquetMetadataConverter.MetadataFilter filter) throws IOException {
        final FileSystem fileSystem = file.getFileSystem(configuration);
        return readFooter(configuration, fileSystem.getFileStatus(file), filter);
    }
    
    @Deprecated
    public static final ParquetMetadata readFooter(final Configuration configuration, final FileStatus file) throws IOException {
        return readFooter(configuration, file, ParquetMetadataConverter.NO_FILTER);
    }
    
    public static final ParquetMetadata readFooter(final Configuration configuration, final FileStatus file, final ParquetMetadataConverter.MetadataFilter filter) throws IOException {
        final FileSystem fileSystem = file.getPath().getFileSystem(configuration);
        final FSDataInputStream f = fileSystem.open(file.getPath());
        try {
            final long l = file.getLen();
            if (Log.DEBUG) {
                ParquetFileReader.LOG.debug("File length " + l);
            }
            final int FOOTER_LENGTH_SIZE = 4;
            if (l < ParquetFileWriter.MAGIC.length + FOOTER_LENGTH_SIZE + ParquetFileWriter.MAGIC.length) {
                throw new RuntimeException(file.getPath() + " is not a Parquet file (too small)");
            }
            final long footerLengthIndex = l - FOOTER_LENGTH_SIZE - ParquetFileWriter.MAGIC.length;
            if (Log.DEBUG) {
                ParquetFileReader.LOG.debug("reading footer index at " + footerLengthIndex);
            }
            f.seek(footerLengthIndex);
            final int footerLength = BytesUtils.readIntLittleEndian(f);
            final byte[] magic = new byte[ParquetFileWriter.MAGIC.length];
            f.readFully(magic);
            if (!Arrays.equals(ParquetFileWriter.MAGIC, magic)) {
                throw new RuntimeException(file.getPath() + " is not a Parquet file. expected magic number at tail " + Arrays.toString(ParquetFileWriter.MAGIC) + " but found " + Arrays.toString(magic));
            }
            final long footerIndex = footerLengthIndex - footerLength;
            if (Log.DEBUG) {
                ParquetFileReader.LOG.debug("read footer length: " + footerLength + ", footer index: " + footerIndex);
            }
            if (footerIndex < ParquetFileWriter.MAGIC.length || footerIndex >= footerLengthIndex) {
                throw new RuntimeException("corrupted file: the footer index is not within the file");
            }
            f.seek(footerIndex);
            return ParquetFileReader.converter.readParquetMetadata(f, filter);
        }
        finally {
            f.close();
        }
    }
    
    public ParquetFileReader(final Configuration configuration, final Path filePath, final List<BlockMetaData> blocks, final List<ColumnDescriptor> columns) throws IOException {
        this.currentBlock = 0;
        this.paths = new HashMap<ColumnPath, ColumnDescriptor>();
        this.filePath = filePath;
        final FileSystem fs = filePath.getFileSystem(configuration);
        this.f = fs.open(filePath);
        this.blocks = blocks;
        for (final ColumnDescriptor col : columns) {
            this.paths.put(ColumnPath.get(col.getPath()), col);
        }
        this.codecFactory = new CodecFactory(configuration);
    }
    
    public PageReadStore readNextRowGroup() throws IOException {
        if (this.currentBlock == this.blocks.size()) {
            return null;
        }
        final BlockMetaData block = this.blocks.get(this.currentBlock);
        if (block.getRowCount() == 0L) {
            throw new RuntimeException("Illegal row group of 0 rows");
        }
        final ColumnChunkPageReadStore columnChunkPageReadStore = new ColumnChunkPageReadStore(block.getRowCount());
        final List<ConsecutiveChunkList> allChunks = new ArrayList<ConsecutiveChunkList>();
        ConsecutiveChunkList currentChunks = null;
        for (final ColumnChunkMetaData mc : block.getColumns()) {
            final ColumnPath pathKey = mc.getPath();
            BenchmarkCounter.incrementTotalBytes(mc.getTotalSize());
            final ColumnDescriptor columnDescriptor = this.paths.get(pathKey);
            if (columnDescriptor != null) {
                final long startingPos = mc.getStartingPos();
                if (currentChunks == null || currentChunks.endPos() != startingPos) {
                    currentChunks = new ConsecutiveChunkList(startingPos);
                    allChunks.add(currentChunks);
                }
                currentChunks.addChunk(new ChunkDescriptor(columnDescriptor, mc, startingPos, (int)mc.getTotalSize()));
            }
        }
        for (final ConsecutiveChunkList consecutiveChunks : allChunks) {
            final List<Chunk> chunks = consecutiveChunks.readAll(this.f);
            for (final Chunk chunk : chunks) {
                columnChunkPageReadStore.addColumn(chunk.descriptor.col, chunk.readAllPages());
            }
        }
        ++this.currentBlock;
        return columnChunkPageReadStore;
    }
    
    @Override
    public void close() throws IOException {
        this.f.close();
        this.codecFactory.release();
    }
    
    static {
        LOG = Log.getLog(ParquetFileReader.class);
        ParquetFileReader.PARQUET_READ_PARALLELISM = "parquet.metadata.read.parallelism";
        ParquetFileReader.converter = new ParquetMetadataConverter();
    }
    
    private class Chunk extends ByteArrayInputStream
    {
        private final ChunkDescriptor descriptor;
        
        public Chunk(final ChunkDescriptor descriptor, final byte[] data, final int offset) {
            super(data);
            this.descriptor = descriptor;
            this.pos = offset;
        }
        
        protected PageHeader readPageHeader() throws IOException {
            return Util.readPageHeader(this);
        }
        
        public ColumnChunkPageReadStore.ColumnChunkPageReader readAllPages() throws IOException {
            final List<DataPage> pagesInChunk = new ArrayList<DataPage>();
            DictionaryPage dictionaryPage = null;
            long valuesCountReadSoFar = 0L;
            while (valuesCountReadSoFar < this.descriptor.metadata.getValueCount()) {
                final PageHeader pageHeader = this.readPageHeader();
                final int uncompressedPageSize = pageHeader.getUncompressed_page_size();
                final int compressedPageSize = pageHeader.getCompressed_page_size();
                switch (pageHeader.type) {
                    case DICTIONARY_PAGE: {
                        if (dictionaryPage != null) {
                            throw new ParquetDecodingException("more than one dictionary page in column " + this.descriptor.col);
                        }
                        final DictionaryPageHeader dicHeader = pageHeader.getDictionary_page_header();
                        dictionaryPage = new DictionaryPage(this.readAsBytesInput(compressedPageSize), uncompressedPageSize, dicHeader.getNum_values(), ParquetFileReader.converter.getEncoding(dicHeader.getEncoding()));
                        continue;
                    }
                    case DATA_PAGE: {
                        final DataPageHeader dataHeaderV1 = pageHeader.getData_page_header();
                        pagesInChunk.add(new DataPageV1(this.readAsBytesInput(compressedPageSize), dataHeaderV1.getNum_values(), uncompressedPageSize, ParquetMetadataConverter.fromParquetStatistics(dataHeaderV1.getStatistics(), this.descriptor.col.getType()), ParquetFileReader.converter.getEncoding(dataHeaderV1.getRepetition_level_encoding()), ParquetFileReader.converter.getEncoding(dataHeaderV1.getDefinition_level_encoding()), ParquetFileReader.converter.getEncoding(dataHeaderV1.getEncoding())));
                        valuesCountReadSoFar += dataHeaderV1.getNum_values();
                        continue;
                    }
                    case DATA_PAGE_V2: {
                        final DataPageHeaderV2 dataHeaderV2 = pageHeader.getData_page_header_v2();
                        final int dataSize = compressedPageSize - dataHeaderV2.getRepetition_levels_byte_length() - dataHeaderV2.getDefinition_levels_byte_length();
                        pagesInChunk.add(new DataPageV2(dataHeaderV2.getNum_rows(), dataHeaderV2.getNum_nulls(), dataHeaderV2.getNum_values(), this.readAsBytesInput(dataHeaderV2.getRepetition_levels_byte_length()), this.readAsBytesInput(dataHeaderV2.getDefinition_levels_byte_length()), ParquetFileReader.converter.getEncoding(dataHeaderV2.getEncoding()), this.readAsBytesInput(dataSize), uncompressedPageSize, ParquetMetadataConverter.fromParquetStatistics(dataHeaderV2.getStatistics(), this.descriptor.col.getType()), dataHeaderV2.isIs_compressed()));
                        valuesCountReadSoFar += dataHeaderV2.getNum_values();
                        continue;
                    }
                    default: {
                        if (Log.DEBUG) {
                            ParquetFileReader.LOG.debug("skipping page of type " + pageHeader.getType() + " of size " + compressedPageSize);
                        }
                        this.skip(compressedPageSize);
                        continue;
                    }
                }
            }
            if (valuesCountReadSoFar != this.descriptor.metadata.getValueCount()) {
                throw new IOException("Expected " + this.descriptor.metadata.getValueCount() + " values in column chunk at " + ParquetFileReader.this.filePath + " offset " + this.descriptor.metadata.getFirstDataPageOffset() + " but got " + valuesCountReadSoFar + " values instead over " + pagesInChunk.size() + " pages ending at file offset " + (this.descriptor.fileOffset + this.pos()));
            }
            final CodecFactory.BytesDecompressor decompressor = ParquetFileReader.this.codecFactory.getDecompressor(this.descriptor.metadata.getCodec());
            return new ColumnChunkPageReadStore.ColumnChunkPageReader(decompressor, pagesInChunk, dictionaryPage);
        }
        
        public int pos() {
            return this.pos;
        }
        
        public BytesInput readAsBytesInput(final int size) throws IOException {
            final BytesInput r = BytesInput.from(this.buf, this.pos, size);
            this.pos += size;
            return r;
        }
    }
    
    private class WorkaroundChunk extends Chunk
    {
        private final FSDataInputStream f;
        
        private WorkaroundChunk(final ChunkDescriptor descriptor, final byte[] data, final int offset, final FSDataInputStream f) {
            super(descriptor, data, offset);
            this.f = f;
        }
        
        @Override
        protected PageHeader readPageHeader() throws IOException {
            final int initialPos = this.pos;
            PageHeader pageHeader;
            try {
                pageHeader = Util.readPageHeader(this);
            }
            catch (IOException e) {
                this.pos = initialPos;
                ParquetFileReader.LOG.info("completing the column chunk to read the page header");
                pageHeader = Util.readPageHeader(new SequenceInputStream(this, this.f));
            }
            return pageHeader;
        }
        
        @Override
        public BytesInput readAsBytesInput(final int size) throws IOException {
            if (this.pos + size > this.count) {
                final int l1 = this.count - this.pos;
                final int l2 = size - l1;
                ParquetFileReader.LOG.info("completed the column chunk with " + l2 + " bytes");
                return BytesInput.concat(super.readAsBytesInput(l1), BytesInput.copy(BytesInput.from(this.f, l2)));
            }
            return super.readAsBytesInput(size);
        }
    }
    
    private static class ChunkDescriptor
    {
        private final ColumnDescriptor col;
        private final ColumnChunkMetaData metadata;
        private final long fileOffset;
        private final int size;
        
        private ChunkDescriptor(final ColumnDescriptor col, final ColumnChunkMetaData metadata, final long fileOffset, final int size) {
            this.col = col;
            this.metadata = metadata;
            this.fileOffset = fileOffset;
            this.size = size;
        }
    }
    
    private class ConsecutiveChunkList
    {
        private final long offset;
        private int length;
        private final List<ChunkDescriptor> chunks;
        
        ConsecutiveChunkList(final long offset) {
            this.chunks = new ArrayList<ChunkDescriptor>();
            this.offset = offset;
        }
        
        public void addChunk(final ChunkDescriptor descriptor) {
            this.chunks.add(descriptor);
            this.length += descriptor.size;
        }
        
        public List<Chunk> readAll(final FSDataInputStream f) throws IOException {
            final List<Chunk> result = new ArrayList<Chunk>(this.chunks.size());
            f.seek(this.offset);
            final byte[] chunksBytes = new byte[this.length];
            f.readFully(chunksBytes);
            BenchmarkCounter.incrementBytesRead(this.length);
            int currentChunkOffset = 0;
            for (int i = 0; i < this.chunks.size(); ++i) {
                final ChunkDescriptor descriptor = this.chunks.get(i);
                if (i < this.chunks.size() - 1) {
                    result.add(new Chunk(descriptor, chunksBytes, currentChunkOffset));
                }
                else {
                    result.add(new WorkaroundChunk(descriptor, chunksBytes, currentChunkOffset, f));
                }
                currentChunkOffset += descriptor.size;
            }
            return result;
        }
        
        public long endPos() {
            return this.offset + this.length;
        }
    }
}
