// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column;

import parquet.column.values.dictionary.DictionaryValuesReader;
import parquet.column.values.deltastrings.DeltaByteArrayReader;
import parquet.column.values.deltalengthbytearray.DeltaLengthByteArrayValuesReader;
import parquet.column.values.delta.DeltaBinaryPackingValuesReader;
import parquet.column.values.bitpacking.ByteBitPackingValuesReader;
import parquet.column.values.bitpacking.Packer;
import parquet.column.values.rle.RunLengthBitPackingHybridValuesReader;
import parquet.column.values.boundedint.ZeroIntegerValuesReader;
import parquet.bytes.BytesUtils;
import parquet.column.values.dictionary.PlainValuesDictionary;
import parquet.column.values.plain.FixedLenByteArrayPlainValuesReader;
import parquet.column.values.plain.PlainValuesReader;
import parquet.column.values.plain.BinaryPlainValuesReader;
import parquet.column.values.plain.BooleanPlainValuesReader;
import parquet.column.values.ValuesReader;
import java.io.IOException;
import parquet.column.page.DictionaryPage;
import parquet.io.ParquetDecodingException;
import parquet.schema.PrimitiveType;

public enum Encoding
{
    PLAIN {
        @Override
        public ValuesReader getValuesReader(final ColumnDescriptor descriptor, final ValuesType valuesType) {
            switch (descriptor.getType()) {
                case BOOLEAN: {
                    return new BooleanPlainValuesReader();
                }
                case BINARY: {
                    return new BinaryPlainValuesReader();
                }
                case FLOAT: {
                    return new PlainValuesReader.FloatPlainValuesReader();
                }
                case DOUBLE: {
                    return new PlainValuesReader.DoublePlainValuesReader();
                }
                case INT32: {
                    return new PlainValuesReader.IntegerPlainValuesReader();
                }
                case INT64: {
                    return new PlainValuesReader.LongPlainValuesReader();
                }
                case INT96: {
                    return new FixedLenByteArrayPlainValuesReader(12);
                }
                case FIXED_LEN_BYTE_ARRAY: {
                    return new FixedLenByteArrayPlainValuesReader(descriptor.getTypeLength());
                }
                default: {
                    throw new ParquetDecodingException("no plain reader for type " + descriptor.getType());
                }
            }
        }
        
        @Override
        public Dictionary initDictionary(final ColumnDescriptor descriptor, final DictionaryPage dictionaryPage) throws IOException {
            switch (descriptor.getType()) {
                case BINARY: {
                    return new PlainValuesDictionary.PlainBinaryDictionary(dictionaryPage);
                }
                case FIXED_LEN_BYTE_ARRAY: {
                    return new PlainValuesDictionary.PlainBinaryDictionary(dictionaryPage, descriptor.getTypeLength());
                }
                case INT96: {
                    return new PlainValuesDictionary.PlainBinaryDictionary(dictionaryPage, 12);
                }
                case INT64: {
                    return new PlainValuesDictionary.PlainLongDictionary(dictionaryPage);
                }
                case DOUBLE: {
                    return new PlainValuesDictionary.PlainDoubleDictionary(dictionaryPage);
                }
                case INT32: {
                    return new PlainValuesDictionary.PlainIntegerDictionary(dictionaryPage);
                }
                case FLOAT: {
                    return new PlainValuesDictionary.PlainFloatDictionary(dictionaryPage);
                }
                default: {
                    throw new ParquetDecodingException("Dictionary encoding not supported for type: " + descriptor.getType());
                }
            }
        }
    }, 
    RLE {
        @Override
        public ValuesReader getValuesReader(final ColumnDescriptor descriptor, final ValuesType valuesType) {
            final int bitWidth = BytesUtils.getWidthFromMaxInt(this.getMaxLevel(descriptor, valuesType));
            if (bitWidth == 0) {
                return new ZeroIntegerValuesReader();
            }
            return new RunLengthBitPackingHybridValuesReader(bitWidth);
        }
    }, 
    @Deprecated
    BIT_PACKED {
        @Override
        public ValuesReader getValuesReader(final ColumnDescriptor descriptor, final ValuesType valuesType) {
            return new ByteBitPackingValuesReader(this.getMaxLevel(descriptor, valuesType), Packer.BIG_ENDIAN);
        }
    }, 
    @Deprecated
    PLAIN_DICTIONARY {
        @Override
        public ValuesReader getDictionaryBasedValuesReader(final ColumnDescriptor descriptor, final ValuesType valuesType, final Dictionary dictionary) {
            return Encoding$4.RLE_DICTIONARY.getDictionaryBasedValuesReader(descriptor, valuesType, dictionary);
        }
        
        @Override
        public Dictionary initDictionary(final ColumnDescriptor descriptor, final DictionaryPage dictionaryPage) throws IOException {
            return Encoding$4.PLAIN.initDictionary(descriptor, dictionaryPage);
        }
        
        @Override
        public boolean usesDictionary() {
            return true;
        }
    }, 
    DELTA_BINARY_PACKED {
        @Override
        public ValuesReader getValuesReader(final ColumnDescriptor descriptor, final ValuesType valuesType) {
            if (descriptor.getType() != PrimitiveType.PrimitiveTypeName.INT32) {
                throw new ParquetDecodingException("Encoding DELTA_BINARY_PACKED is only supported for type INT32");
            }
            return new DeltaBinaryPackingValuesReader();
        }
    }, 
    DELTA_LENGTH_BYTE_ARRAY {
        @Override
        public ValuesReader getValuesReader(final ColumnDescriptor descriptor, final ValuesType valuesType) {
            if (descriptor.getType() != PrimitiveType.PrimitiveTypeName.BINARY) {
                throw new ParquetDecodingException("Encoding DELTA_LENGTH_BYTE_ARRAY is only supported for type BINARY");
            }
            return new DeltaLengthByteArrayValuesReader();
        }
    }, 
    DELTA_BYTE_ARRAY {
        @Override
        public ValuesReader getValuesReader(final ColumnDescriptor descriptor, final ValuesType valuesType) {
            if (descriptor.getType() != PrimitiveType.PrimitiveTypeName.BINARY) {
                throw new ParquetDecodingException("Encoding DELTA_BYTE_ARRAY is only supported for type BINARY");
            }
            return new DeltaByteArrayReader();
        }
    }, 
    RLE_DICTIONARY {
        @Override
        public ValuesReader getDictionaryBasedValuesReader(final ColumnDescriptor descriptor, final ValuesType valuesType, final Dictionary dictionary) {
            switch (descriptor.getType()) {
                case BINARY:
                case FLOAT:
                case DOUBLE:
                case INT32:
                case INT64:
                case INT96:
                case FIXED_LEN_BYTE_ARRAY: {
                    return new DictionaryValuesReader(dictionary);
                }
                default: {
                    throw new ParquetDecodingException("Dictionary encoding not supported for type: " + descriptor.getType());
                }
            }
        }
        
        @Override
        public boolean usesDictionary() {
            return true;
        }
    };
    
    int getMaxLevel(final ColumnDescriptor descriptor, final ValuesType valuesType) {
        switch (valuesType) {
            case REPETITION_LEVEL: {
                final int maxLevel = descriptor.getMaxRepetitionLevel();
                return maxLevel;
            }
            case DEFINITION_LEVEL: {
                final int maxLevel = descriptor.getMaxDefinitionLevel();
                return maxLevel;
            }
            case VALUES: {
                if (descriptor.getType() == PrimitiveType.PrimitiveTypeName.BOOLEAN) {
                    final int maxLevel = 1;
                    return maxLevel;
                }
                break;
            }
        }
        throw new ParquetDecodingException("Unsupported encoding for values: " + this);
    }
    
    public boolean usesDictionary() {
        return false;
    }
    
    public Dictionary initDictionary(final ColumnDescriptor descriptor, final DictionaryPage dictionaryPage) throws IOException {
        throw new UnsupportedOperationException(this.name() + " does not support dictionary");
    }
    
    public ValuesReader getValuesReader(final ColumnDescriptor descriptor, final ValuesType valuesType) {
        throw new UnsupportedOperationException("Error decoding " + descriptor + ". " + this.name() + " is dictionary based");
    }
    
    public ValuesReader getDictionaryBasedValuesReader(final ColumnDescriptor descriptor, final ValuesType valuesType, final Dictionary dictionary) {
        throw new UnsupportedOperationException(this.name() + " is not dictionary based");
    }
}
