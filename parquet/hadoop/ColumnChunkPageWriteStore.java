// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.io.OutputStream;
import parquet.io.ParquetEncodingException;
import parquet.bytes.BytesInput;
import java.util.HashSet;
import parquet.column.statistics.Statistics;
import parquet.column.Encoding;
import java.util.Set;
import parquet.column.page.DictionaryPage;
import parquet.bytes.ConcatenatingByteArrayCollector;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import parquet.column.page.PageWriter;
import java.util.Iterator;
import java.util.HashMap;
import parquet.schema.MessageType;
import parquet.column.ColumnDescriptor;
import java.util.Map;
import parquet.format.converter.ParquetMetadataConverter;
import parquet.Log;
import parquet.column.page.PageWriteStore;

class ColumnChunkPageWriteStore implements PageWriteStore
{
    private static final Log LOG;
    private static ParquetMetadataConverter parquetMetadataConverter;
    private final Map<ColumnDescriptor, ColumnChunkPageWriter> writers;
    private final MessageType schema;
    
    public ColumnChunkPageWriteStore(final CodecFactory.BytesCompressor compressor, final MessageType schema, final int pageSize) {
        this.writers = new HashMap<ColumnDescriptor, ColumnChunkPageWriter>();
        this.schema = schema;
        for (final ColumnDescriptor path : schema.getColumns()) {
            this.writers.put(path, new ColumnChunkPageWriter(path, compressor, pageSize));
        }
    }
    
    @Override
    public PageWriter getPageWriter(final ColumnDescriptor path) {
        return this.writers.get(path);
    }
    
    public void flushToFileWriter(final ParquetFileWriter writer) throws IOException {
        for (final ColumnDescriptor path : this.schema.getColumns()) {
            final ColumnChunkPageWriter pageWriter = this.writers.get(path);
            pageWriter.writeToFileWriter(writer);
        }
    }
    
    static {
        LOG = Log.getLog(ColumnChunkPageWriteStore.class);
        ColumnChunkPageWriteStore.parquetMetadataConverter = new ParquetMetadataConverter();
    }
    
    private static final class ColumnChunkPageWriter implements PageWriter
    {
        private final ColumnDescriptor path;
        private final CodecFactory.BytesCompressor compressor;
        private final ByteArrayOutputStream tempOutputStream;
        private final ConcatenatingByteArrayCollector buf;
        private DictionaryPage dictionaryPage;
        private long uncompressedLength;
        private long compressedLength;
        private long totalValueCount;
        private int pageCount;
        private Set<Encoding> encodings;
        private Statistics totalStatistics;
        
        private ColumnChunkPageWriter(final ColumnDescriptor path, final CodecFactory.BytesCompressor compressor, final int pageSize) {
            this.tempOutputStream = new ByteArrayOutputStream();
            this.encodings = new HashSet<Encoding>();
            this.path = path;
            this.compressor = compressor;
            this.buf = new ConcatenatingByteArrayCollector();
            this.totalStatistics = Statistics.getStatsBasedOnType(this.path.getType());
        }
        
        @Override
        public void writePage(final BytesInput bytes, final int valueCount, final Statistics statistics, final Encoding rlEncoding, final Encoding dlEncoding, final Encoding valuesEncoding) throws IOException {
            final long uncompressedSize = bytes.size();
            if (uncompressedSize > 2147483647L) {
                throw new ParquetEncodingException("Cannot write page larger than Integer.MAX_VALUE bytes: " + uncompressedSize);
            }
            final BytesInput compressedBytes = this.compressor.compress(bytes);
            final long compressedSize = compressedBytes.size();
            if (compressedSize > 2147483647L) {
                throw new ParquetEncodingException("Cannot write compressed page larger than Integer.MAX_VALUE bytes: " + compressedSize);
            }
            this.tempOutputStream.reset();
            ColumnChunkPageWriteStore.parquetMetadataConverter.writeDataPageHeader((int)uncompressedSize, (int)compressedSize, valueCount, statistics, rlEncoding, dlEncoding, valuesEncoding, this.tempOutputStream);
            this.uncompressedLength += uncompressedSize;
            this.compressedLength += compressedSize;
            this.totalValueCount += valueCount;
            ++this.pageCount;
            this.totalStatistics.mergeStatistics(statistics);
            this.buf.collect(BytesInput.concat(BytesInput.from(this.tempOutputStream), compressedBytes));
            this.encodings.add(rlEncoding);
            this.encodings.add(dlEncoding);
            this.encodings.add(valuesEncoding);
        }
        
        @Override
        public void writePageV2(final int rowCount, final int nullCount, final int valueCount, final BytesInput repetitionLevels, final BytesInput definitionLevels, final Encoding dataEncoding, final BytesInput data, final Statistics<?> statistics) throws IOException {
            final int rlByteLength = this.toIntWithCheck(repetitionLevels.size());
            final int dlByteLength = this.toIntWithCheck(definitionLevels.size());
            final int uncompressedSize = this.toIntWithCheck(data.size() + repetitionLevels.size() + definitionLevels.size());
            final BytesInput compressedData = this.compressor.compress(data);
            final int compressedSize = this.toIntWithCheck(compressedData.size() + repetitionLevels.size() + definitionLevels.size());
            this.tempOutputStream.reset();
            ColumnChunkPageWriteStore.parquetMetadataConverter.writeDataPageV2Header(uncompressedSize, compressedSize, valueCount, nullCount, rowCount, statistics, dataEncoding, rlByteLength, dlByteLength, this.tempOutputStream);
            this.uncompressedLength += uncompressedSize;
            this.compressedLength += compressedSize;
            this.totalValueCount += valueCount;
            ++this.pageCount;
            this.totalStatistics.mergeStatistics(statistics);
            this.buf.collect(BytesInput.concat(BytesInput.from(this.tempOutputStream), repetitionLevels, definitionLevels, compressedData));
            this.encodings.add(dataEncoding);
        }
        
        private int toIntWithCheck(final long size) {
            if (size > 2147483647L) {
                throw new ParquetEncodingException("Cannot write page larger than 2147483647 bytes: " + size);
            }
            return (int)size;
        }
        
        @Override
        public long getMemSize() {
            return this.buf.size();
        }
        
        public void writeToFileWriter(final ParquetFileWriter writer) throws IOException {
            writer.startColumn(this.path, this.totalValueCount, this.compressor.getCodecName());
            if (this.dictionaryPage != null) {
                writer.writeDictionaryPage(this.dictionaryPage);
                this.encodings.add(this.dictionaryPage.getEncoding());
            }
            writer.writeDataPages(this.buf, this.uncompressedLength, this.compressedLength, this.totalStatistics, new ArrayList<Encoding>(this.encodings));
            writer.endColumn();
            if (Log.INFO) {
                ColumnChunkPageWriteStore.LOG.info(String.format("written %,dB for %s: %,d values, %,dB raw, %,dB comp, %d pages, encodings: %s", this.buf.size(), this.path, this.totalValueCount, this.uncompressedLength, this.compressedLength, this.pageCount, this.encodings) + ((this.dictionaryPage != null) ? String.format(", dic { %,d entries, %,dB raw, %,dB comp}", this.dictionaryPage.getDictionarySize(), this.dictionaryPage.getUncompressedSize(), this.dictionaryPage.getDictionarySize()) : ""));
            }
            this.encodings.clear();
            this.pageCount = 0;
        }
        
        @Override
        public long allocatedSize() {
            return this.buf.size();
        }
        
        @Override
        public void writeDictionaryPage(final DictionaryPage dictionaryPage) throws IOException {
            if (this.dictionaryPage != null) {
                throw new ParquetEncodingException("Only one dictionary page is allowed");
            }
            final BytesInput dictionaryBytes = dictionaryPage.getBytes();
            final int uncompressedSize = (int)dictionaryBytes.size();
            final BytesInput compressedBytes = this.compressor.compress(dictionaryBytes);
            this.dictionaryPage = new DictionaryPage(BytesInput.copy(compressedBytes), uncompressedSize, dictionaryPage.getDictionarySize(), dictionaryPage.getEncoding());
        }
        
        @Override
        public String memUsageString(final String prefix) {
            return this.buf.memUsageString(prefix + " ColumnChunkPageWriter");
        }
    }
}
