// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format.converter;

import java.util.Collection;
import parquet.format.DictionaryPageHeader;
import parquet.format.DataPageHeaderV2;
import parquet.format.DataPageHeader;
import parquet.format.PageType;
import parquet.format.PageHeader;
import parquet.column.statistics.BooleanStatistics;
import java.io.OutputStream;
import parquet.format.FieldRepetitionType;
import parquet.schema.Types;
import parquet.hadoop.metadata.ColumnPath;
import parquet.hadoop.metadata.CompressionCodecName;
import parquet.io.ParquetDecodingException;
import parquet.format.Util;
import java.io.IOException;
import java.io.InputStream;
import parquet.format.KeyValue;
import parquet.schema.OriginalType;
import parquet.format.ConvertedType;
import parquet.column.statistics.Statistics;
import java.util.Collections;
import java.util.HashSet;
import parquet.format.ColumnMetaData;
import java.util.Arrays;
import parquet.hadoop.metadata.ColumnChunkMetaData;
import parquet.format.ColumnChunk;
import parquet.schema.GroupType;
import parquet.schema.PrimitiveType;
import parquet.schema.TypeVisitor;
import parquet.schema.Type;
import parquet.format.SchemaElement;
import parquet.schema.MessageType;
import java.util.Iterator;
import java.util.List;
import parquet.hadoop.metadata.BlockMetaData;
import parquet.format.RowGroup;
import java.util.ArrayList;
import parquet.format.FileMetaData;
import parquet.hadoop.metadata.ParquetMetadata;
import java.util.HashMap;
import parquet.column.Encoding;
import java.util.Set;
import java.util.Map;
import parquet.Log;

public class ParquetMetadataConverter
{
    private static final Log LOG;
    private Map<EncodingList, Set<Encoding>> encodingLists;
    public static final MetadataFilter NO_FILTER;
    public static final MetadataFilter SKIP_ROW_GROUPS;
    
    public ParquetMetadataConverter() {
        this.encodingLists = new HashMap<EncodingList, Set<Encoding>>();
    }
    
    public FileMetaData toParquetMetadata(final int currentVersion, final ParquetMetadata parquetMetadata) {
        final List<BlockMetaData> blocks = parquetMetadata.getBlocks();
        final List<RowGroup> rowGroups = new ArrayList<RowGroup>();
        int numRows = 0;
        for (final BlockMetaData block : blocks) {
            numRows += (int)block.getRowCount();
            this.addRowGroup(parquetMetadata, rowGroups, block);
        }
        final FileMetaData fileMetaData = new FileMetaData(currentVersion, this.toParquetSchema(parquetMetadata.getFileMetaData().getSchema()), numRows, rowGroups);
        final Set<Map.Entry<String, String>> keyValues = parquetMetadata.getFileMetaData().getKeyValueMetaData().entrySet();
        for (final Map.Entry<String, String> keyValue : keyValues) {
            this.addKeyValue(fileMetaData, keyValue.getKey(), keyValue.getValue());
        }
        fileMetaData.setCreated_by(parquetMetadata.getFileMetaData().getCreatedBy());
        return fileMetaData;
    }
    
    List<SchemaElement> toParquetSchema(final MessageType schema) {
        final List<SchemaElement> result = new ArrayList<SchemaElement>();
        this.addToList(result, schema);
        return result;
    }
    
    private void addToList(final List<SchemaElement> result, final Type field) {
        field.accept(new TypeVisitor() {
            @Override
            public void visit(final PrimitiveType primitiveType) {
                final SchemaElement element = new SchemaElement(primitiveType.getName());
                element.setRepetition_type(ParquetMetadataConverter.this.toParquetRepetition(primitiveType.getRepetition()));
                element.setType(ParquetMetadataConverter.this.getType(primitiveType.getPrimitiveTypeName()));
                if (primitiveType.getOriginalType() != null) {
                    element.setConverted_type(ParquetMetadataConverter.this.getConvertedType(primitiveType.getOriginalType()));
                }
                if (primitiveType.getDecimalMetadata() != null) {
                    element.setPrecision(primitiveType.getDecimalMetadata().getPrecision());
                    element.setScale(primitiveType.getDecimalMetadata().getScale());
                }
                if (primitiveType.getTypeLength() > 0) {
                    element.setType_length(primitiveType.getTypeLength());
                }
                result.add(element);
            }
            
            @Override
            public void visit(final MessageType messageType) {
                final SchemaElement element = new SchemaElement(messageType.getName());
                this.visitChildren(result, messageType.asGroupType(), element);
            }
            
            @Override
            public void visit(final GroupType groupType) {
                final SchemaElement element = new SchemaElement(groupType.getName());
                element.setRepetition_type(ParquetMetadataConverter.this.toParquetRepetition(groupType.getRepetition()));
                if (groupType.getOriginalType() != null) {
                    element.setConverted_type(ParquetMetadataConverter.this.getConvertedType(groupType.getOriginalType()));
                }
                this.visitChildren(result, groupType, element);
            }
            
            private void visitChildren(final List<SchemaElement> result, final GroupType groupType, final SchemaElement element) {
                element.setNum_children(groupType.getFieldCount());
                result.add(element);
                for (final Type field : groupType.getFields()) {
                    ParquetMetadataConverter.this.addToList(result, field);
                }
            }
        });
    }
    
    private void addRowGroup(final ParquetMetadata parquetMetadata, final List<RowGroup> rowGroups, final BlockMetaData block) {
        final List<ColumnChunkMetaData> columns = block.getColumns();
        final List<ColumnChunk> parquetColumns = new ArrayList<ColumnChunk>();
        for (final ColumnChunkMetaData columnMetaData : columns) {
            final ColumnChunk columnChunk = new ColumnChunk(columnMetaData.getFirstDataPageOffset());
            columnChunk.file_path = block.getPath();
            columnChunk.meta_data = new ColumnMetaData(this.getType(columnMetaData.getType()), this.toFormatEncodings(columnMetaData.getEncodings()), Arrays.asList(columnMetaData.getPath().toArray()), columnMetaData.getCodec().getParquetCompressionCodec(), columnMetaData.getValueCount(), columnMetaData.getTotalUncompressedSize(), columnMetaData.getTotalSize(), columnMetaData.getFirstDataPageOffset());
            columnChunk.meta_data.dictionary_page_offset = columnMetaData.getDictionaryPageOffset();
            if (!columnMetaData.getStatistics().isEmpty()) {
                columnChunk.meta_data.setStatistics(toParquetStatistics(columnMetaData.getStatistics()));
            }
            parquetColumns.add(columnChunk);
        }
        final RowGroup rowGroup = new RowGroup(parquetColumns, block.getTotalByteSize(), block.getRowCount());
        rowGroups.add(rowGroup);
    }
    
    private List<parquet.format.Encoding> toFormatEncodings(final Set<Encoding> encodings) {
        final List<parquet.format.Encoding> converted = new ArrayList<parquet.format.Encoding>(encodings.size());
        for (final Encoding encoding : encodings) {
            converted.add(this.getEncoding(encoding));
        }
        return converted;
    }
    
    private Set<Encoding> fromFormatEncodings(final List<parquet.format.Encoding> encodings) {
        Set<Encoding> converted = new HashSet<Encoding>();
        for (final parquet.format.Encoding encoding : encodings) {
            converted.add(this.getEncoding(encoding));
        }
        converted = Collections.unmodifiableSet((Set<? extends Encoding>)converted);
        final EncodingList key = new EncodingList(converted);
        Set<Encoding> cached = this.encodingLists.get(key);
        if (cached == null) {
            cached = converted;
            this.encodingLists.put(key, cached);
        }
        return cached;
    }
    
    public Encoding getEncoding(final parquet.format.Encoding encoding) {
        return Encoding.valueOf(encoding.name());
    }
    
    public parquet.format.Encoding getEncoding(final Encoding encoding) {
        return parquet.format.Encoding.valueOf(encoding.name());
    }
    
    public static parquet.format.Statistics toParquetStatistics(final Statistics statistics) {
        final parquet.format.Statistics stats = new parquet.format.Statistics();
        if (!statistics.isEmpty()) {
            stats.setNull_count(statistics.getNumNulls());
            if (statistics.hasNonNullValue()) {
                stats.setMax(statistics.getMaxBytes());
                stats.setMin(statistics.getMinBytes());
            }
        }
        return stats;
    }
    
    public static Statistics fromParquetStatistics(final parquet.format.Statistics statistics, final PrimitiveType.PrimitiveTypeName type) {
        final Statistics stats = Statistics.getStatsBasedOnType(type);
        if (statistics != null) {
            if (statistics.isSetMax() && statistics.isSetMin()) {
                stats.setMinMaxFromBytes(statistics.min.array(), statistics.max.array());
            }
            stats.setNumNulls(statistics.null_count);
        }
        return stats;
    }
    
    public PrimitiveType.PrimitiveTypeName getPrimitive(final parquet.format.Type type) {
        switch (type) {
            case BYTE_ARRAY: {
                return PrimitiveType.PrimitiveTypeName.BINARY;
            }
            case INT64: {
                return PrimitiveType.PrimitiveTypeName.INT64;
            }
            case INT32: {
                return PrimitiveType.PrimitiveTypeName.INT32;
            }
            case BOOLEAN: {
                return PrimitiveType.PrimitiveTypeName.BOOLEAN;
            }
            case FLOAT: {
                return PrimitiveType.PrimitiveTypeName.FLOAT;
            }
            case DOUBLE: {
                return PrimitiveType.PrimitiveTypeName.DOUBLE;
            }
            case INT96: {
                return PrimitiveType.PrimitiveTypeName.INT96;
            }
            case FIXED_LEN_BYTE_ARRAY: {
                return PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY;
            }
            default: {
                throw new RuntimeException("Unknown type " + type);
            }
        }
    }
    
    parquet.format.Type getType(final PrimitiveType.PrimitiveTypeName type) {
        switch (type) {
            case INT64: {
                return parquet.format.Type.INT64;
            }
            case INT32: {
                return parquet.format.Type.INT32;
            }
            case BOOLEAN: {
                return parquet.format.Type.BOOLEAN;
            }
            case BINARY: {
                return parquet.format.Type.BYTE_ARRAY;
            }
            case FLOAT: {
                return parquet.format.Type.FLOAT;
            }
            case DOUBLE: {
                return parquet.format.Type.DOUBLE;
            }
            case INT96: {
                return parquet.format.Type.INT96;
            }
            case FIXED_LEN_BYTE_ARRAY: {
                return parquet.format.Type.FIXED_LEN_BYTE_ARRAY;
            }
            default: {
                throw new RuntimeException("Unknown primitive type " + type);
            }
        }
    }
    
    OriginalType getOriginalType(final ConvertedType type) {
        switch (type) {
            case UTF8: {
                return OriginalType.UTF8;
            }
            case MAP: {
                return OriginalType.MAP;
            }
            case MAP_KEY_VALUE: {
                return OriginalType.MAP_KEY_VALUE;
            }
            case LIST: {
                return OriginalType.LIST;
            }
            case ENUM: {
                return OriginalType.ENUM;
            }
            case DECIMAL: {
                return OriginalType.DECIMAL;
            }
            case DATE: {
                return OriginalType.DATE;
            }
            case TIME_MILLIS: {
                return OriginalType.TIME_MILLIS;
            }
            case TIMESTAMP_MILLIS: {
                return OriginalType.TIMESTAMP_MILLIS;
            }
            case INTERVAL: {
                return OriginalType.INTERVAL;
            }
            case INT_8: {
                return OriginalType.INT_8;
            }
            case INT_16: {
                return OriginalType.INT_16;
            }
            case INT_32: {
                return OriginalType.INT_32;
            }
            case INT_64: {
                return OriginalType.INT_64;
            }
            case UINT_8: {
                return OriginalType.UINT_8;
            }
            case UINT_16: {
                return OriginalType.UINT_16;
            }
            case UINT_32: {
                return OriginalType.UINT_32;
            }
            case UINT_64: {
                return OriginalType.UINT_64;
            }
            case JSON: {
                return OriginalType.JSON;
            }
            case BSON: {
                return OriginalType.BSON;
            }
            default: {
                throw new RuntimeException("Unknown converted type " + type);
            }
        }
    }
    
    ConvertedType getConvertedType(final OriginalType type) {
        switch (type) {
            case UTF8: {
                return ConvertedType.UTF8;
            }
            case MAP: {
                return ConvertedType.MAP;
            }
            case MAP_KEY_VALUE: {
                return ConvertedType.MAP_KEY_VALUE;
            }
            case LIST: {
                return ConvertedType.LIST;
            }
            case ENUM: {
                return ConvertedType.ENUM;
            }
            case DECIMAL: {
                return ConvertedType.DECIMAL;
            }
            case DATE: {
                return ConvertedType.DATE;
            }
            case TIME_MILLIS: {
                return ConvertedType.TIME_MILLIS;
            }
            case TIMESTAMP_MILLIS: {
                return ConvertedType.TIMESTAMP_MILLIS;
            }
            case INTERVAL: {
                return ConvertedType.INTERVAL;
            }
            case INT_8: {
                return ConvertedType.INT_8;
            }
            case INT_16: {
                return ConvertedType.INT_16;
            }
            case INT_32: {
                return ConvertedType.INT_32;
            }
            case INT_64: {
                return ConvertedType.INT_64;
            }
            case UINT_8: {
                return ConvertedType.UINT_8;
            }
            case UINT_16: {
                return ConvertedType.UINT_16;
            }
            case UINT_32: {
                return ConvertedType.UINT_32;
            }
            case UINT_64: {
                return ConvertedType.UINT_64;
            }
            case JSON: {
                return ConvertedType.JSON;
            }
            case BSON: {
                return ConvertedType.BSON;
            }
            default: {
                throw new RuntimeException("Unknown original type " + type);
            }
        }
    }
    
    private void addKeyValue(final FileMetaData fileMetaData, final String key, final String value) {
        final KeyValue keyValue = new KeyValue(key);
        keyValue.value = value;
        fileMetaData.addToKey_value_metadata(keyValue);
    }
    
    public static final MetadataFilter range(final long startOffset, final long endOffset) {
        return new RangeMetadataFilter(startOffset, endOffset);
    }
    
    @Deprecated
    public ParquetMetadata readParquetMetadata(final InputStream from) throws IOException {
        return this.readParquetMetadata(from, ParquetMetadataConverter.NO_FILTER);
    }
    
    static FileMetaData filterFileMetaData(final FileMetaData metaData, final RangeMetadataFilter filter) {
        final List<RowGroup> rowGroups = metaData.getRow_groups();
        final List<RowGroup> newRowGroups = new ArrayList<RowGroup>();
        for (final RowGroup rowGroup : rowGroups) {
            long totalSize = 0L;
            final long startIndex = getOffset(rowGroup.getColumns().get(0));
            for (final ColumnChunk col : rowGroup.getColumns()) {
                totalSize += col.getMeta_data().getTotal_compressed_size();
            }
            final long midPoint = startIndex + totalSize / 2L;
            if (filter.contains(midPoint)) {
                newRowGroups.add(rowGroup);
            }
        }
        metaData.setRow_groups(newRowGroups);
        return metaData;
    }
    
    static long getOffset(final RowGroup rowGroup) {
        return getOffset(rowGroup.getColumns().get(0));
    }
    
    static long getOffset(final ColumnChunk columnChunk) {
        final ColumnMetaData md = columnChunk.getMeta_data();
        long offset = md.getData_page_offset();
        if (md.isSetDictionary_page_offset() && offset > md.getDictionary_page_offset()) {
            offset = md.getDictionary_page_offset();
        }
        return offset;
    }
    
    public ParquetMetadata readParquetMetadata(final InputStream from, final MetadataFilter filter) throws IOException {
        final FileMetaData fileMetaData = filter.accept((MetadataFilterVisitor<FileMetaData, Throwable>)new MetadataFilterVisitor<FileMetaData, IOException>() {
            @Override
            public FileMetaData visit(final NoFilter filter) throws IOException {
                return Util.readFileMetaData(from);
            }
            
            @Override
            public FileMetaData visit(final SkipMetadataFilter filter) throws IOException {
                return Util.readFileMetaData(from, true);
            }
            
            @Override
            public FileMetaData visit(final RangeMetadataFilter filter) throws IOException {
                return ParquetMetadataConverter.filterFileMetaData(Util.readFileMetaData(from), filter);
            }
        });
        if (Log.DEBUG) {
            ParquetMetadataConverter.LOG.debug(fileMetaData);
        }
        final ParquetMetadata parquetMetadata = this.fromParquetMetadata(fileMetaData);
        if (Log.DEBUG) {
            ParquetMetadataConverter.LOG.debug(ParquetMetadata.toPrettyJSON(parquetMetadata));
        }
        return parquetMetadata;
    }
    
    public ParquetMetadata fromParquetMetadata(final FileMetaData parquetMetadata) throws IOException {
        final MessageType messageType = this.fromParquetSchema(parquetMetadata.getSchema());
        final List<BlockMetaData> blocks = new ArrayList<BlockMetaData>();
        final List<RowGroup> row_groups = parquetMetadata.getRow_groups();
        if (row_groups != null) {
            for (final RowGroup rowGroup : row_groups) {
                final BlockMetaData blockMetaData = new BlockMetaData();
                blockMetaData.setRowCount(rowGroup.getNum_rows());
                blockMetaData.setTotalByteSize(rowGroup.getTotal_byte_size());
                final List<ColumnChunk> columns = rowGroup.getColumns();
                final String filePath = columns.get(0).getFile_path();
                for (final ColumnChunk columnChunk : columns) {
                    if ((filePath == null && columnChunk.getFile_path() != null) || (filePath != null && !filePath.equals(columnChunk.getFile_path()))) {
                        throw new ParquetDecodingException("all column chunks of the same row group must be in the same file for now");
                    }
                    final ColumnMetaData metaData = columnChunk.meta_data;
                    final ColumnPath path = this.getPath(metaData);
                    final ColumnChunkMetaData column = ColumnChunkMetaData.get(path, messageType.getType(path.toArray()).asPrimitiveType().getPrimitiveTypeName(), CompressionCodecName.fromParquet(metaData.codec), this.fromFormatEncodings(metaData.encodings), fromParquetStatistics(metaData.statistics, messageType.getType(path.toArray()).asPrimitiveType().getPrimitiveTypeName()), metaData.data_page_offset, metaData.dictionary_page_offset, metaData.num_values, metaData.total_compressed_size, metaData.total_uncompressed_size);
                    blockMetaData.addColumn(column);
                }
                blockMetaData.setPath(filePath);
                blocks.add(blockMetaData);
            }
        }
        final Map<String, String> keyValueMetaData = new HashMap<String, String>();
        final List<KeyValue> key_value_metadata = parquetMetadata.getKey_value_metadata();
        if (key_value_metadata != null) {
            for (final KeyValue keyValue : key_value_metadata) {
                keyValueMetaData.put(keyValue.key, keyValue.value);
            }
        }
        return new ParquetMetadata(new parquet.hadoop.metadata.FileMetaData(messageType, keyValueMetaData, parquetMetadata.getCreated_by()), blocks);
    }
    
    private ColumnPath getPath(final ColumnMetaData metaData) {
        final String[] path = metaData.path_in_schema.toArray(new String[metaData.path_in_schema.size()]);
        return ColumnPath.get(path);
    }
    
    MessageType fromParquetSchema(final List<SchemaElement> schema) {
        final Iterator<SchemaElement> iterator = schema.iterator();
        final SchemaElement root = iterator.next();
        final Types.MessageTypeBuilder builder = Types.buildMessage();
        this.buildChildren(builder, iterator, root.getNum_children());
        return builder.named(root.name);
    }
    
    private void buildChildren(final Types.GroupBuilder builder, final Iterator<SchemaElement> schema, final int childrenCount) {
        for (int i = 0; i < childrenCount; ++i) {
            final SchemaElement schemaElement = schema.next();
            Types.Builder childBuilder;
            if (schemaElement.type != null) {
                final Types.PrimitiveBuilder primitiveBuilder = builder.primitive(this.getPrimitive(schemaElement.type), this.fromParquetRepetition(schemaElement.repetition_type));
                if (schemaElement.isSetType_length()) {
                    primitiveBuilder.length(schemaElement.type_length);
                }
                if (schemaElement.isSetPrecision()) {
                    primitiveBuilder.precision(schemaElement.precision);
                }
                if (schemaElement.isSetScale()) {
                    primitiveBuilder.scale(schemaElement.scale);
                }
                childBuilder = primitiveBuilder;
            }
            else {
                childBuilder = builder.group(this.fromParquetRepetition(schemaElement.repetition_type));
                this.buildChildren((Types.GroupBuilder)childBuilder, schema, schemaElement.num_children);
            }
            if (schemaElement.isSetConverted_type()) {
                childBuilder.as(this.getOriginalType(schemaElement.converted_type));
            }
            if (schemaElement.isSetField_id()) {
                childBuilder.id(schemaElement.field_id);
            }
            childBuilder.named(schemaElement.name);
        }
    }
    
    FieldRepetitionType toParquetRepetition(final Type.Repetition repetition) {
        return FieldRepetitionType.valueOf(repetition.name());
    }
    
    Type.Repetition fromParquetRepetition(final FieldRepetitionType repetition) {
        return Type.Repetition.valueOf(repetition.name());
    }
    
    @Deprecated
    public void writeDataPageHeader(final int uncompressedSize, final int compressedSize, final int valueCount, final Encoding rlEncoding, final Encoding dlEncoding, final Encoding valuesEncoding, final OutputStream to) throws IOException {
        Util.writePageHeader(this.newDataPageHeader(uncompressedSize, compressedSize, valueCount, new BooleanStatistics(), rlEncoding, dlEncoding, valuesEncoding), to);
    }
    
    public void writeDataPageHeader(final int uncompressedSize, final int compressedSize, final int valueCount, final Statistics statistics, final Encoding rlEncoding, final Encoding dlEncoding, final Encoding valuesEncoding, final OutputStream to) throws IOException {
        Util.writePageHeader(this.newDataPageHeader(uncompressedSize, compressedSize, valueCount, statistics, rlEncoding, dlEncoding, valuesEncoding), to);
    }
    
    private PageHeader newDataPageHeader(final int uncompressedSize, final int compressedSize, final int valueCount, final Statistics statistics, final Encoding rlEncoding, final Encoding dlEncoding, final Encoding valuesEncoding) {
        final PageHeader pageHeader = new PageHeader(PageType.DATA_PAGE, uncompressedSize, compressedSize);
        pageHeader.setData_page_header(new DataPageHeader(valueCount, this.getEncoding(valuesEncoding), this.getEncoding(dlEncoding), this.getEncoding(rlEncoding)));
        if (!statistics.isEmpty()) {
            pageHeader.getData_page_header().setStatistics(toParquetStatistics(statistics));
        }
        return pageHeader;
    }
    
    public void writeDataPageV2Header(final int uncompressedSize, final int compressedSize, final int valueCount, final int nullCount, final int rowCount, final Statistics statistics, final Encoding dataEncoding, final int rlByteLength, final int dlByteLength, final OutputStream to) throws IOException {
        Util.writePageHeader(this.newDataPageV2Header(uncompressedSize, compressedSize, valueCount, nullCount, rowCount, statistics, dataEncoding, rlByteLength, dlByteLength), to);
    }
    
    private PageHeader newDataPageV2Header(final int uncompressedSize, final int compressedSize, final int valueCount, final int nullCount, final int rowCount, final Statistics<?> statistics, final Encoding dataEncoding, final int rlByteLength, final int dlByteLength) {
        final DataPageHeaderV2 dataPageHeaderV2 = new DataPageHeaderV2(valueCount, nullCount, rowCount, this.getEncoding(dataEncoding), dlByteLength, rlByteLength);
        if (!statistics.isEmpty()) {
            dataPageHeaderV2.setStatistics(toParquetStatistics(statistics));
        }
        final PageHeader pageHeader = new PageHeader(PageType.DATA_PAGE_V2, uncompressedSize, compressedSize);
        pageHeader.setData_page_header_v2(dataPageHeaderV2);
        return pageHeader;
    }
    
    public void writeDictionaryPageHeader(final int uncompressedSize, final int compressedSize, final int valueCount, final Encoding valuesEncoding, final OutputStream to) throws IOException {
        final PageHeader pageHeader = new PageHeader(PageType.DICTIONARY_PAGE, uncompressedSize, compressedSize);
        pageHeader.setDictionary_page_header(new DictionaryPageHeader(valueCount, this.getEncoding(valuesEncoding)));
        Util.writePageHeader(pageHeader, to);
    }
    
    static {
        LOG = Log.getLog(ParquetMetadataConverter.class);
        NO_FILTER = new NoFilter();
        SKIP_ROW_GROUPS = new SkipMetadataFilter();
    }
    
    private static final class EncodingList
    {
        private final Set<Encoding> encodings;
        
        public EncodingList(final Set<Encoding> encodings) {
            this.encodings = encodings;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof EncodingList) {
                final Set<Encoding> other = ((EncodingList)obj).encodings;
                return other.size() == this.encodings.size() && this.encodings.containsAll(other);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = 1;
            for (final Encoding element : this.encodings) {
                result = 31 * result + ((element == null) ? 0 : element.hashCode());
            }
            return result;
        }
    }
    
    public abstract static class MetadataFilter
    {
        private MetadataFilter() {
        }
        
        abstract <T, E extends Throwable> T accept(final MetadataFilterVisitor<T, E> p0) throws E, Throwable;
    }
    
    private static final class NoFilter extends MetadataFilter
    {
        @Override
         <T, E extends Throwable> T accept(final MetadataFilterVisitor<T, E> visitor) throws E, Throwable {
            return visitor.visit(this);
        }
        
        @Override
        public String toString() {
            return "NO_FILTER";
        }
    }
    
    private static final class SkipMetadataFilter extends MetadataFilter
    {
        @Override
         <T, E extends Throwable> T accept(final MetadataFilterVisitor<T, E> visitor) throws E, Throwable {
            return visitor.visit(this);
        }
        
        @Override
        public String toString() {
            return "SKIP_ROW_GROUPS";
        }
    }
    
    static final class RangeMetadataFilter extends MetadataFilter
    {
        final long startOffset;
        final long endOffset;
        
        RangeMetadataFilter(final long startOffset, final long endOffset) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }
        
        @Override
         <T, E extends Throwable> T accept(final MetadataFilterVisitor<T, E> visitor) throws E, Throwable {
            return visitor.visit(this);
        }
        
        boolean contains(final long offset) {
            return offset >= this.startOffset && offset < this.endOffset;
        }
        
        @Override
        public String toString() {
            return "range(s:" + this.startOffset + ", e:" + this.endOffset + ")";
        }
    }
    
    private interface MetadataFilterVisitor<T, E extends Throwable>
    {
        T visit(final NoFilter p0) throws E, Throwable;
        
        T visit(final SkipMetadataFilter p0) throws E, Throwable;
        
        T visit(final RangeMetadataFilter p0) throws E, Throwable;
    }
}
