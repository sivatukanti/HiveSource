// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import parquet.hadoop.metadata.GlobalMetaData;
import parquet.io.ParquetEncodingException;
import parquet.bytes.BytesUtils;
import parquet.format.Util;
import parquet.hadoop.metadata.ParquetMetadata;
import parquet.hadoop.metadata.FileMetaData;
import parquet.Version;
import java.util.Map;
import java.util.Collection;
import parquet.bytes.BytesInput;
import java.io.OutputStream;
import parquet.column.page.DictionaryPage;
import java.util.HashSet;
import parquet.column.ColumnDescriptor;
import org.apache.hadoop.fs.FileSystem;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import parquet.column.statistics.Statistics;
import parquet.schema.PrimitiveType;
import parquet.hadoop.metadata.ColumnPath;
import parquet.hadoop.metadata.CompressionCodecName;
import parquet.column.Encoding;
import java.util.Set;
import java.util.List;
import parquet.hadoop.metadata.ColumnChunkMetaData;
import parquet.hadoop.metadata.BlockMetaData;
import org.apache.hadoop.fs.FSDataOutputStream;
import parquet.schema.MessageType;
import parquet.format.converter.ParquetMetadataConverter;
import parquet.Log;

public class ParquetFileWriter
{
    private static final Log LOG;
    public static final String PARQUET_METADATA_FILE = "_metadata";
    public static final String PARQUET_COMMON_METADATA_FILE = "_common_metadata";
    public static final byte[] MAGIC;
    public static final int CURRENT_VERSION = 1;
    private static final ParquetMetadataConverter metadataConverter;
    private final MessageType schema;
    private final FSDataOutputStream out;
    private BlockMetaData currentBlock;
    private ColumnChunkMetaData currentColumn;
    private long currentRecordCount;
    private List<BlockMetaData> blocks;
    private long uncompressedLength;
    private long compressedLength;
    private Set<Encoding> currentEncodings;
    private CompressionCodecName currentChunkCodec;
    private ColumnPath currentChunkPath;
    private PrimitiveType.PrimitiveTypeName currentChunkType;
    private long currentChunkFirstDataPage;
    private long currentChunkDictionaryPageOffset;
    private long currentChunkValueCount;
    private Statistics currentStatistics;
    private STATE state;
    
    public ParquetFileWriter(final Configuration configuration, final MessageType schema, final Path file) throws IOException {
        this(configuration, schema, file, Mode.CREATE);
    }
    
    public ParquetFileWriter(final Configuration configuration, final MessageType schema, final Path file, final Mode mode) throws IOException {
        this.blocks = new ArrayList<BlockMetaData>();
        this.state = STATE.NOT_STARTED;
        this.schema = schema;
        final FileSystem fs = file.getFileSystem(configuration);
        final boolean overwriteFlag = mode == Mode.OVERWRITE;
        this.out = fs.create(file, overwriteFlag);
    }
    
    public void start() throws IOException {
        this.state = this.state.start();
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": start");
        }
        this.out.write(ParquetFileWriter.MAGIC);
    }
    
    public void startBlock(final long recordCount) throws IOException {
        this.state = this.state.startBlock();
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": start block");
        }
        this.currentBlock = new BlockMetaData();
        this.currentRecordCount = recordCount;
    }
    
    public void startColumn(final ColumnDescriptor descriptor, final long valueCount, final CompressionCodecName compressionCodecName) throws IOException {
        this.state = this.state.startColumn();
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": start column: " + descriptor + " count=" + valueCount);
        }
        this.currentEncodings = new HashSet<Encoding>();
        this.currentChunkPath = ColumnPath.get(descriptor.getPath());
        this.currentChunkType = descriptor.getType();
        this.currentChunkCodec = compressionCodecName;
        this.currentChunkValueCount = valueCount;
        this.currentChunkFirstDataPage = this.out.getPos();
        this.compressedLength = 0L;
        this.uncompressedLength = 0L;
        this.currentStatistics = Statistics.getStatsBasedOnType(this.currentChunkType);
    }
    
    public void writeDictionaryPage(final DictionaryPage dictionaryPage) throws IOException {
        this.state = this.state.write();
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": write dictionary page: " + dictionaryPage.getDictionarySize() + " values");
        }
        this.currentChunkDictionaryPageOffset = this.out.getPos();
        final int uncompressedSize = dictionaryPage.getUncompressedSize();
        final int compressedPageSize = (int)dictionaryPage.getBytes().size();
        ParquetFileWriter.metadataConverter.writeDictionaryPageHeader(uncompressedSize, compressedPageSize, dictionaryPage.getDictionarySize(), dictionaryPage.getEncoding(), this.out);
        final long headerSize = this.out.getPos() - this.currentChunkDictionaryPageOffset;
        this.uncompressedLength += uncompressedSize + headerSize;
        this.compressedLength += compressedPageSize + headerSize;
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": write dictionary page content " + compressedPageSize);
        }
        dictionaryPage.getBytes().writeAllTo(this.out);
        this.currentEncodings.add(dictionaryPage.getEncoding());
    }
    
    @Deprecated
    public void writeDataPage(final int valueCount, final int uncompressedPageSize, final BytesInput bytes, final Encoding rlEncoding, final Encoding dlEncoding, final Encoding valuesEncoding) throws IOException {
        this.state = this.state.write();
        final long beforeHeader = this.out.getPos();
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(beforeHeader + ": write data page: " + valueCount + " values");
        }
        final int compressedPageSize = (int)bytes.size();
        ParquetFileWriter.metadataConverter.writeDataPageHeader(uncompressedPageSize, compressedPageSize, valueCount, rlEncoding, dlEncoding, valuesEncoding, this.out);
        final long headerSize = this.out.getPos() - beforeHeader;
        this.uncompressedLength += uncompressedPageSize + headerSize;
        this.compressedLength += compressedPageSize + headerSize;
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": write data page content " + compressedPageSize);
        }
        bytes.writeAllTo(this.out);
        this.currentEncodings.add(rlEncoding);
        this.currentEncodings.add(dlEncoding);
        this.currentEncodings.add(valuesEncoding);
    }
    
    public void writeDataPage(final int valueCount, final int uncompressedPageSize, final BytesInput bytes, final Statistics statistics, final Encoding rlEncoding, final Encoding dlEncoding, final Encoding valuesEncoding) throws IOException {
        this.state = this.state.write();
        final long beforeHeader = this.out.getPos();
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(beforeHeader + ": write data page: " + valueCount + " values");
        }
        final int compressedPageSize = (int)bytes.size();
        ParquetFileWriter.metadataConverter.writeDataPageHeader(uncompressedPageSize, compressedPageSize, valueCount, statistics, rlEncoding, dlEncoding, valuesEncoding, this.out);
        final long headerSize = this.out.getPos() - beforeHeader;
        this.uncompressedLength += uncompressedPageSize + headerSize;
        this.compressedLength += compressedPageSize + headerSize;
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": write data page content " + compressedPageSize);
        }
        bytes.writeAllTo(this.out);
        this.currentStatistics.mergeStatistics(statistics);
        this.currentEncodings.add(rlEncoding);
        this.currentEncodings.add(dlEncoding);
        this.currentEncodings.add(valuesEncoding);
    }
    
    void writeDataPages(final BytesInput bytes, final long uncompressedTotalPageSize, final long compressedTotalPageSize, final Statistics totalStats, final List<Encoding> encodings) throws IOException {
        this.state = this.state.write();
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": write data pages");
        }
        final long headersSize = bytes.size() - compressedTotalPageSize;
        this.uncompressedLength += uncompressedTotalPageSize + headersSize;
        this.compressedLength += compressedTotalPageSize + headersSize;
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": write data pages content");
        }
        bytes.writeAllTo(this.out);
        this.currentEncodings.addAll(encodings);
        this.currentStatistics = totalStats;
    }
    
    public void endColumn() throws IOException {
        this.state = this.state.endColumn();
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": end column");
        }
        this.currentBlock.addColumn(ColumnChunkMetaData.get(this.currentChunkPath, this.currentChunkType, this.currentChunkCodec, this.currentEncodings, this.currentStatistics, this.currentChunkFirstDataPage, this.currentChunkDictionaryPageOffset, this.currentChunkValueCount, this.compressedLength, this.uncompressedLength));
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.info("ended Column chumk: " + this.currentColumn);
        }
        this.currentColumn = null;
        this.currentBlock.setTotalByteSize(this.currentBlock.getTotalByteSize() + this.uncompressedLength);
        this.uncompressedLength = 0L;
        this.compressedLength = 0L;
    }
    
    public void endBlock() throws IOException {
        this.state = this.state.endBlock();
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": end block");
        }
        this.currentBlock.setRowCount(this.currentRecordCount);
        this.blocks.add(this.currentBlock);
        this.currentBlock = null;
    }
    
    public void end(final Map<String, String> extraMetaData) throws IOException {
        this.state = this.state.end();
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(this.out.getPos() + ": end");
        }
        final ParquetMetadata footer = new ParquetMetadata(new FileMetaData(this.schema, extraMetaData, Version.FULL_VERSION), this.blocks);
        serializeFooter(footer, this.out);
        this.out.close();
    }
    
    private static void serializeFooter(final ParquetMetadata footer, final FSDataOutputStream out) throws IOException {
        final long footerIndex = out.getPos();
        final parquet.format.FileMetaData parquetMetadata = new ParquetMetadataConverter().toParquetMetadata(1, footer);
        Util.writeFileMetaData(parquetMetadata, out);
        if (Log.DEBUG) {
            ParquetFileWriter.LOG.debug(out.getPos() + ": footer length = " + (out.getPos() - footerIndex));
        }
        BytesUtils.writeIntLittleEndian(out, (int)(out.getPos() - footerIndex));
        out.write(ParquetFileWriter.MAGIC);
    }
    
    public static void writeMetadataFile(final Configuration configuration, Path outputPath, final List<Footer> footers) throws IOException {
        final ParquetMetadata metadataFooter = mergeFooters(outputPath, footers);
        final FileSystem fs = outputPath.getFileSystem(configuration);
        outputPath = outputPath.makeQualified(fs);
        writeMetadataFile(outputPath, metadataFooter, fs, "_metadata");
        metadataFooter.getBlocks().clear();
        writeMetadataFile(outputPath, metadataFooter, fs, "_common_metadata");
    }
    
    private static void writeMetadataFile(final Path outputPath, final ParquetMetadata metadataFooter, final FileSystem fs, final String parquetMetadataFile) throws IOException {
        final Path metaDataPath = new Path(outputPath, parquetMetadataFile);
        final FSDataOutputStream metadata = fs.create(metaDataPath);
        metadata.write(ParquetFileWriter.MAGIC);
        serializeFooter(metadataFooter, metadata);
        metadata.close();
    }
    
    static ParquetMetadata mergeFooters(final Path root, final List<Footer> footers) {
        final String rootPath = root.toUri().getPath();
        GlobalMetaData fileMetaData = null;
        final List<BlockMetaData> blocks = new ArrayList<BlockMetaData>();
        for (final Footer footer : footers) {
            String footerPath = footer.getFile().toUri().getPath();
            if (!footerPath.startsWith(rootPath)) {
                throw new ParquetEncodingException(footerPath + " invalid: all the files must be contained in the root " + root);
            }
            for (footerPath = footerPath.substring(rootPath.length()); footerPath.startsWith("/"); footerPath = footerPath.substring(1)) {}
            fileMetaData = mergeInto(footer.getParquetMetadata().getFileMetaData(), fileMetaData);
            for (final BlockMetaData block : footer.getParquetMetadata().getBlocks()) {
                block.setPath(footerPath);
                blocks.add(block);
            }
        }
        return new ParquetMetadata(fileMetaData.merge(), blocks);
    }
    
    public long getPos() throws IOException {
        return this.out.getPos();
    }
    
    static GlobalMetaData getGlobalMetaData(final List<Footer> footers) {
        return getGlobalMetaData(footers, true);
    }
    
    static GlobalMetaData getGlobalMetaData(final List<Footer> footers, final boolean strict) {
        GlobalMetaData fileMetaData = null;
        for (final Footer footer : footers) {
            final ParquetMetadata currentMetadata = footer.getParquetMetadata();
            fileMetaData = mergeInto(currentMetadata.getFileMetaData(), fileMetaData, strict);
        }
        return fileMetaData;
    }
    
    static GlobalMetaData mergeInto(final FileMetaData toMerge, final GlobalMetaData mergedMetadata) {
        return mergeInto(toMerge, mergedMetadata, true);
    }
    
    static GlobalMetaData mergeInto(final FileMetaData toMerge, final GlobalMetaData mergedMetadata, final boolean strict) {
        MessageType schema = null;
        final Map<String, Set<String>> newKeyValues = new HashMap<String, Set<String>>();
        final Set<String> createdBy = new HashSet<String>();
        if (mergedMetadata != null) {
            schema = mergedMetadata.getSchema();
            newKeyValues.putAll(mergedMetadata.getKeyValueMetaData());
            createdBy.addAll(mergedMetadata.getCreatedBy());
        }
        if ((schema == null && toMerge.getSchema() != null) || (schema != null && !schema.equals((Object)toMerge.getSchema()))) {
            schema = mergeInto(toMerge.getSchema(), schema, strict);
        }
        for (final Map.Entry<String, String> entry : toMerge.getKeyValueMetaData().entrySet()) {
            Set<String> values = newKeyValues.get(entry.getKey());
            if (values == null) {
                values = new HashSet<String>();
                newKeyValues.put(entry.getKey(), values);
            }
            values.add(entry.getValue());
        }
        createdBy.add(toMerge.getCreatedBy());
        return new GlobalMetaData(schema, newKeyValues, createdBy);
    }
    
    static MessageType mergeInto(final MessageType toMerge, final MessageType mergedSchema) {
        return mergeInto(toMerge, mergedSchema, true);
    }
    
    static MessageType mergeInto(final MessageType toMerge, final MessageType mergedSchema, final boolean strict) {
        if (mergedSchema == null) {
            return toMerge;
        }
        return mergedSchema.union(toMerge, strict);
    }
    
    static {
        LOG = Log.getLog(ParquetFileWriter.class);
        MAGIC = "PAR1".getBytes(Charset.forName("ASCII"));
        metadataConverter = new ParquetMetadataConverter();
    }
    
    public enum Mode
    {
        CREATE, 
        OVERWRITE;
    }
    
    private enum STATE
    {
        NOT_STARTED {
            @Override
            STATE start() {
                return ParquetFileWriter$STATE$1.STARTED;
            }
        }, 
        STARTED {
            @Override
            STATE startBlock() {
                return ParquetFileWriter$STATE$2.BLOCK;
            }
            
            @Override
            STATE end() {
                return ParquetFileWriter$STATE$2.ENDED;
            }
        }, 
        BLOCK {
            @Override
            STATE startColumn() {
                return ParquetFileWriter$STATE$3.COLUMN;
            }
            
            @Override
            STATE endBlock() {
                return ParquetFileWriter$STATE$3.STARTED;
            }
        }, 
        COLUMN {
            @Override
            STATE endColumn() {
                return ParquetFileWriter$STATE$4.BLOCK;
            }
            
            @Override
            STATE write() {
                return this;
            }
        }, 
        ENDED;
        
        STATE start() throws IOException {
            return this.error();
        }
        
        STATE startBlock() throws IOException {
            return this.error();
        }
        
        STATE startColumn() throws IOException {
            return this.error();
        }
        
        STATE write() throws IOException {
            return this.error();
        }
        
        STATE endColumn() throws IOException {
            return this.error();
        }
        
        STATE endBlock() throws IOException {
            return this.error();
        }
        
        STATE end() throws IOException {
            return this.error();
        }
        
        private final STATE error() throws IOException {
            throw new IOException("The file being written is in an invalid state. Probably caused by an error thrown previously. Current state: " + this.name());
        }
    }
}
