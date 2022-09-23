// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column;

import parquet.column.impl.ColumnWriteStoreV2;
import parquet.column.impl.ColumnWriteStoreV1;
import parquet.column.page.PageWriteStore;
import parquet.schema.MessageType;
import parquet.column.values.fallback.FallbackValuesWriter;
import parquet.column.values.delta.DeltaBinaryPackingValuesWriter;
import parquet.column.values.deltastrings.DeltaByteArrayWriter;
import parquet.column.values.dictionary.DictionaryValuesWriter;
import parquet.column.values.plain.PlainValuesWriter;
import parquet.column.values.plain.FixedLenByteArrayPlainValuesWriter;
import parquet.column.values.plain.BooleanPlainValuesWriter;
import parquet.column.values.rle.RunLengthBitPackingHybridValuesWriter;
import parquet.bytes.BytesUtils;
import parquet.column.values.boundedint.DevNullValuesWriter;
import parquet.column.values.ValuesWriter;

public class ParquetProperties
{
    private final int dictionaryPageSizeThreshold;
    private final WriterVersion writerVersion;
    private final boolean enableDictionary;
    
    public ParquetProperties(final int dictPageSize, final WriterVersion writerVersion, final boolean enableDict) {
        this.dictionaryPageSizeThreshold = dictPageSize;
        this.writerVersion = writerVersion;
        this.enableDictionary = enableDict;
    }
    
    public static ValuesWriter getColumnDescriptorValuesWriter(final int maxLevel, final int initialSizePerCol, final int pageSize) {
        if (maxLevel == 0) {
            return new DevNullValuesWriter();
        }
        return new RunLengthBitPackingHybridValuesWriter(BytesUtils.getWidthFromMaxInt(maxLevel), initialSizePerCol, pageSize);
    }
    
    private ValuesWriter plainWriter(final ColumnDescriptor path, final int initialSizePerCol, final int pageSize) {
        switch (path.getType()) {
            case BOOLEAN: {
                return new BooleanPlainValuesWriter();
            }
            case INT96: {
                return new FixedLenByteArrayPlainValuesWriter(12, initialSizePerCol, pageSize);
            }
            case FIXED_LEN_BYTE_ARRAY: {
                return new FixedLenByteArrayPlainValuesWriter(path.getTypeLength(), initialSizePerCol, pageSize);
            }
            case BINARY:
            case INT32:
            case INT64:
            case DOUBLE:
            case FLOAT: {
                return new PlainValuesWriter(initialSizePerCol, pageSize);
            }
            default: {
                throw new IllegalArgumentException("Unknown type " + path.getType());
            }
        }
    }
    
    private DictionaryValuesWriter dictionaryWriter(final ColumnDescriptor path, final int initialSizePerCol) {
        Encoding encodingForDataPage = null;
        Encoding encodingForDictionaryPage = null;
        switch (this.writerVersion) {
            case PARQUET_1_0: {
                encodingForDataPage = Encoding.PLAIN_DICTIONARY;
                encodingForDictionaryPage = Encoding.PLAIN_DICTIONARY;
                break;
            }
            case PARQUET_2_0: {
                encodingForDataPage = Encoding.RLE_DICTIONARY;
                encodingForDictionaryPage = Encoding.PLAIN;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown version: " + this.writerVersion);
            }
        }
        switch (path.getType()) {
            case BOOLEAN: {
                throw new IllegalArgumentException("no dictionary encoding for BOOLEAN");
            }
            case BINARY: {
                return new DictionaryValuesWriter.PlainBinaryDictionaryValuesWriter(this.dictionaryPageSizeThreshold, encodingForDataPage, encodingForDictionaryPage);
            }
            case INT32: {
                return new DictionaryValuesWriter.PlainIntegerDictionaryValuesWriter(this.dictionaryPageSizeThreshold, encodingForDataPage, encodingForDictionaryPage);
            }
            case INT64: {
                return new DictionaryValuesWriter.PlainLongDictionaryValuesWriter(this.dictionaryPageSizeThreshold, encodingForDataPage, encodingForDictionaryPage);
            }
            case INT96: {
                return new DictionaryValuesWriter.PlainFixedLenArrayDictionaryValuesWriter(this.dictionaryPageSizeThreshold, 12, encodingForDataPage, encodingForDictionaryPage);
            }
            case DOUBLE: {
                return new DictionaryValuesWriter.PlainDoubleDictionaryValuesWriter(this.dictionaryPageSizeThreshold, encodingForDataPage, encodingForDictionaryPage);
            }
            case FLOAT: {
                return new DictionaryValuesWriter.PlainFloatDictionaryValuesWriter(this.dictionaryPageSizeThreshold, encodingForDataPage, encodingForDictionaryPage);
            }
            case FIXED_LEN_BYTE_ARRAY: {
                return new DictionaryValuesWriter.PlainFixedLenArrayDictionaryValuesWriter(this.dictionaryPageSizeThreshold, path.getTypeLength(), encodingForDataPage, encodingForDictionaryPage);
            }
            default: {
                throw new IllegalArgumentException("Unknown type " + path.getType());
            }
        }
    }
    
    private ValuesWriter writerToFallbackTo(final ColumnDescriptor path, final int initialSizePerCol, final int pageSize) {
        switch (this.writerVersion) {
            case PARQUET_1_0: {
                return this.plainWriter(path, initialSizePerCol, pageSize);
            }
            case PARQUET_2_0: {
                switch (path.getType()) {
                    case BOOLEAN: {
                        return new RunLengthBitPackingHybridValuesWriter(1, initialSizePerCol, pageSize);
                    }
                    case FIXED_LEN_BYTE_ARRAY:
                    case BINARY: {
                        return new DeltaByteArrayWriter(initialSizePerCol, pageSize);
                    }
                    case INT32: {
                        return new DeltaBinaryPackingValuesWriter(initialSizePerCol, pageSize);
                    }
                    case INT96:
                    case INT64:
                    case DOUBLE:
                    case FLOAT: {
                        return this.plainWriter(path, initialSizePerCol, pageSize);
                    }
                    default: {
                        throw new IllegalArgumentException("Unknown type " + path.getType());
                    }
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown version: " + this.writerVersion);
            }
        }
    }
    
    private ValuesWriter dictWriterWithFallBack(final ColumnDescriptor path, final int initialSizePerCol, final int pageSize) {
        final ValuesWriter writerToFallBackTo = this.writerToFallbackTo(path, initialSizePerCol, pageSize);
        if (this.enableDictionary) {
            return FallbackValuesWriter.of(this.dictionaryWriter(path, initialSizePerCol), writerToFallBackTo);
        }
        return writerToFallBackTo;
    }
    
    public ValuesWriter getValuesWriter(final ColumnDescriptor path, final int initialSizePerCol, final int pageSize) {
        switch (path.getType()) {
            case BOOLEAN: {
                return this.writerToFallbackTo(path, initialSizePerCol, pageSize);
            }
            case FIXED_LEN_BYTE_ARRAY: {
                if (this.writerVersion == WriterVersion.PARQUET_2_0) {
                    return this.dictWriterWithFallBack(path, initialSizePerCol, pageSize);
                }
                return this.writerToFallbackTo(path, initialSizePerCol, pageSize);
            }
            case INT96:
            case BINARY:
            case INT32:
            case INT64:
            case DOUBLE:
            case FLOAT: {
                return this.dictWriterWithFallBack(path, initialSizePerCol, pageSize);
            }
            default: {
                throw new IllegalArgumentException("Unknown type " + path.getType());
            }
        }
    }
    
    public int getDictionaryPageSizeThreshold() {
        return this.dictionaryPageSizeThreshold;
    }
    
    public WriterVersion getWriterVersion() {
        return this.writerVersion;
    }
    
    public boolean isEnableDictionary() {
        return this.enableDictionary;
    }
    
    public ColumnWriteStore newColumnWriteStore(final MessageType schema, final PageWriteStore pageStore, final int pageSize) {
        switch (this.writerVersion) {
            case PARQUET_1_0: {
                return new ColumnWriteStoreV1(pageStore, pageSize, this.dictionaryPageSizeThreshold, this.enableDictionary, this.writerVersion);
            }
            case PARQUET_2_0: {
                return new ColumnWriteStoreV2(schema, pageStore, pageSize, new ParquetProperties(this.dictionaryPageSizeThreshold, this.writerVersion, this.enableDictionary));
            }
            default: {
                throw new IllegalArgumentException("unknown version " + this.writerVersion);
            }
        }
    }
    
    public enum WriterVersion
    {
        PARQUET_1_0("v1"), 
        PARQUET_2_0("v2");
        
        private final String shortName;
        
        private WriterVersion(final String shortname) {
            this.shortName = shortname;
        }
        
        public static WriterVersion fromString(final String name) {
            for (final WriterVersion v : values()) {
                if (v.shortName.equals(name)) {
                    return v;
                }
            }
            return valueOf(name);
        }
    }
}
