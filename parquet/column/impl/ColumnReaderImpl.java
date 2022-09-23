// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.impl;

import parquet.column.values.rle.RunLengthBitPackingHybridDecoder;
import java.io.ByteArrayInputStream;
import parquet.bytes.BytesUtils;
import parquet.bytes.BytesInput;
import parquet.column.ValuesType;
import parquet.column.Encoding;
import parquet.column.page.DataPageV2;
import parquet.column.page.DataPageV1;
import parquet.column.page.DataPage;
import parquet.column.page.DictionaryPage;
import java.io.IOException;
import parquet.io.ParquetDecodingException;
import parquet.Preconditions;
import parquet.schema.PrimitiveType;
import parquet.io.api.Binary;
import parquet.io.api.PrimitiveConverter;
import parquet.column.values.ValuesReader;
import parquet.column.Dictionary;
import parquet.column.page.PageReader;
import parquet.column.ColumnDescriptor;
import parquet.Log;
import parquet.column.ColumnReader;

class ColumnReaderImpl implements ColumnReader
{
    private static final Log LOG;
    private final ColumnDescriptor path;
    private final long totalValueCount;
    private final PageReader pageReader;
    private final Dictionary dictionary;
    private IntIterator repetitionLevelColumn;
    private IntIterator definitionLevelColumn;
    protected ValuesReader dataColumn;
    private int repetitionLevel;
    private int definitionLevel;
    private int dictionaryId;
    private long endOfPageValueCount;
    private int readValues;
    private int pageValueCount;
    private final PrimitiveConverter converter;
    private Binding binding;
    private boolean valueRead;
    
    private void bindToDictionary(final Dictionary dictionary) {
        this.binding = new Binding() {
            @Override
            void read() {
                ColumnReaderImpl.this.dictionaryId = ColumnReaderImpl.this.dataColumn.readValueDictionaryId();
            }
            
            public void skip() {
                ColumnReaderImpl.this.dataColumn.skip();
            }
            
            @Override
            public int getDictionaryId() {
                return ColumnReaderImpl.this.dictionaryId;
            }
            
            @Override
            void writeValue() {
                ColumnReaderImpl.this.converter.addValueFromDictionary(ColumnReaderImpl.this.dictionaryId);
            }
            
            @Override
            public int getInteger() {
                return dictionary.decodeToInt(ColumnReaderImpl.this.dictionaryId);
            }
            
            @Override
            public boolean getBoolean() {
                return dictionary.decodeToBoolean(ColumnReaderImpl.this.dictionaryId);
            }
            
            @Override
            public long getLong() {
                return dictionary.decodeToLong(ColumnReaderImpl.this.dictionaryId);
            }
            
            @Override
            public Binary getBinary() {
                return dictionary.decodeToBinary(ColumnReaderImpl.this.dictionaryId);
            }
            
            @Override
            public float getFloat() {
                return dictionary.decodeToFloat(ColumnReaderImpl.this.dictionaryId);
            }
            
            @Override
            public double getDouble() {
                return dictionary.decodeToDouble(ColumnReaderImpl.this.dictionaryId);
            }
        };
    }
    
    private void bind(final PrimitiveType.PrimitiveTypeName type) {
        this.binding = type.convert((PrimitiveType.PrimitiveTypeNameConverter<Binding, Exception>)new PrimitiveType.PrimitiveTypeNameConverter<Binding, RuntimeException>() {
            @Override
            public Binding convertFLOAT(final PrimitiveType.PrimitiveTypeName primitiveTypeName) throws RuntimeException {
                return new Binding() {
                    float current;
                    
                    @Override
                    void read() {
                        this.current = ColumnReaderImpl.this.dataColumn.readFloat();
                    }
                    
                    public void skip() {
                        this.current = 0.0f;
                        ColumnReaderImpl.this.dataColumn.skip();
                    }
                    
                    @Override
                    public float getFloat() {
                        return this.current;
                    }
                    
                    @Override
                    void writeValue() {
                        ColumnReaderImpl.this.converter.addFloat(this.current);
                    }
                };
            }
            
            @Override
            public Binding convertDOUBLE(final PrimitiveType.PrimitiveTypeName primitiveTypeName) throws RuntimeException {
                return new Binding() {
                    double current;
                    
                    @Override
                    void read() {
                        this.current = ColumnReaderImpl.this.dataColumn.readDouble();
                    }
                    
                    public void skip() {
                        this.current = 0.0;
                        ColumnReaderImpl.this.dataColumn.skip();
                    }
                    
                    @Override
                    public double getDouble() {
                        return this.current;
                    }
                    
                    @Override
                    void writeValue() {
                        ColumnReaderImpl.this.converter.addDouble(this.current);
                    }
                };
            }
            
            @Override
            public Binding convertINT32(final PrimitiveType.PrimitiveTypeName primitiveTypeName) throws RuntimeException {
                return new Binding() {
                    int current;
                    
                    @Override
                    void read() {
                        this.current = ColumnReaderImpl.this.dataColumn.readInteger();
                    }
                    
                    public void skip() {
                        this.current = 0;
                        ColumnReaderImpl.this.dataColumn.skip();
                    }
                    
                    @Override
                    public int getInteger() {
                        return this.current;
                    }
                    
                    @Override
                    void writeValue() {
                        ColumnReaderImpl.this.converter.addInt(this.current);
                    }
                };
            }
            
            @Override
            public Binding convertINT64(final PrimitiveType.PrimitiveTypeName primitiveTypeName) throws RuntimeException {
                return new Binding() {
                    long current;
                    
                    @Override
                    void read() {
                        this.current = ColumnReaderImpl.this.dataColumn.readLong();
                    }
                    
                    public void skip() {
                        this.current = 0L;
                        ColumnReaderImpl.this.dataColumn.skip();
                    }
                    
                    @Override
                    public long getLong() {
                        return this.current;
                    }
                    
                    @Override
                    void writeValue() {
                        ColumnReaderImpl.this.converter.addLong(this.current);
                    }
                };
            }
            
            @Override
            public Binding convertINT96(final PrimitiveType.PrimitiveTypeName primitiveTypeName) throws RuntimeException {
                return this.convertBINARY(primitiveTypeName);
            }
            
            @Override
            public Binding convertFIXED_LEN_BYTE_ARRAY(final PrimitiveType.PrimitiveTypeName primitiveTypeName) throws RuntimeException {
                return this.convertBINARY(primitiveTypeName);
            }
            
            @Override
            public Binding convertBOOLEAN(final PrimitiveType.PrimitiveTypeName primitiveTypeName) throws RuntimeException {
                return new Binding() {
                    boolean current;
                    
                    @Override
                    void read() {
                        this.current = ColumnReaderImpl.this.dataColumn.readBoolean();
                    }
                    
                    public void skip() {
                        this.current = false;
                        ColumnReaderImpl.this.dataColumn.skip();
                    }
                    
                    @Override
                    public boolean getBoolean() {
                        return this.current;
                    }
                    
                    @Override
                    void writeValue() {
                        ColumnReaderImpl.this.converter.addBoolean(this.current);
                    }
                };
            }
            
            @Override
            public Binding convertBINARY(final PrimitiveType.PrimitiveTypeName primitiveTypeName) throws RuntimeException {
                return new Binding() {
                    Binary current;
                    
                    @Override
                    void read() {
                        this.current = ColumnReaderImpl.this.dataColumn.readBytes();
                    }
                    
                    public void skip() {
                        this.current = null;
                        ColumnReaderImpl.this.dataColumn.skip();
                    }
                    
                    @Override
                    public Binary getBinary() {
                        return this.current;
                    }
                    
                    @Override
                    void writeValue() {
                        ColumnReaderImpl.this.converter.addBinary(this.current);
                    }
                };
            }
        });
    }
    
    public ColumnReaderImpl(final ColumnDescriptor path, final PageReader pageReader, final PrimitiveConverter converter) {
        this.path = Preconditions.checkNotNull(path, "path");
        this.pageReader = Preconditions.checkNotNull(pageReader, "pageReader");
        this.converter = Preconditions.checkNotNull(converter, "converter");
        final DictionaryPage dictionaryPage = pageReader.readDictionaryPage();
        Label_0125: {
            if (dictionaryPage != null) {
                try {
                    this.dictionary = dictionaryPage.getEncoding().initDictionary(path, dictionaryPage);
                    if (converter.hasDictionarySupport()) {
                        converter.setDictionary(this.dictionary);
                    }
                    break Label_0125;
                }
                catch (IOException e) {
                    throw new ParquetDecodingException("could not decode the dictionary for " + path, e);
                }
            }
            this.dictionary = null;
        }
        this.totalValueCount = pageReader.getTotalValueCount();
        if (this.totalValueCount == 0L) {
            throw new ParquetDecodingException("totalValueCount == 0");
        }
        this.consume();
    }
    
    private boolean isFullyConsumed() {
        return this.readValues >= this.totalValueCount;
    }
    
    @Override
    public void writeCurrentValueToConverter() {
        this.readValue();
        this.binding.writeValue();
    }
    
    @Override
    public int getCurrentValueDictionaryID() {
        this.readValue();
        return this.binding.getDictionaryId();
    }
    
    @Override
    public int getInteger() {
        this.readValue();
        return this.binding.getInteger();
    }
    
    @Override
    public boolean getBoolean() {
        this.readValue();
        return this.binding.getBoolean();
    }
    
    @Override
    public long getLong() {
        this.readValue();
        return this.binding.getLong();
    }
    
    @Override
    public Binary getBinary() {
        this.readValue();
        return this.binding.getBinary();
    }
    
    @Override
    public float getFloat() {
        this.readValue();
        return this.binding.getFloat();
    }
    
    @Override
    public double getDouble() {
        this.readValue();
        return this.binding.getDouble();
    }
    
    @Override
    public int getCurrentRepetitionLevel() {
        return this.repetitionLevel;
    }
    
    @Override
    public ColumnDescriptor getDescriptor() {
        return this.path;
    }
    
    public void readValue() {
        try {
            if (!this.valueRead) {
                this.binding.read();
                this.valueRead = true;
            }
        }
        catch (RuntimeException e) {
            throw new ParquetDecodingException(String.format("Can't read value in column %s at value %d out of %d, %d out of %d in currentPage. repetition level: %d, definition level: %d", this.path, this.readValues, this.totalValueCount, this.readValues - (this.endOfPageValueCount - this.pageValueCount), this.pageValueCount, this.repetitionLevel, this.definitionLevel), e);
        }
    }
    
    @Override
    public void skip() {
        if (!this.valueRead) {
            this.binding.skip();
            this.valueRead = true;
        }
    }
    
    @Override
    public int getCurrentDefinitionLevel() {
        return this.definitionLevel;
    }
    
    private void readRepetitionAndDefinitionLevels() {
        this.repetitionLevel = this.repetitionLevelColumn.nextInt();
        this.definitionLevel = this.definitionLevelColumn.nextInt();
        ++this.readValues;
    }
    
    private void checkRead() {
        if (this.isPageFullyConsumed()) {
            if (this.isFullyConsumed()) {
                if (Log.DEBUG) {
                    ColumnReaderImpl.LOG.debug("end reached");
                }
                this.repetitionLevel = 0;
                return;
            }
            this.readPage();
        }
        this.readRepetitionAndDefinitionLevels();
    }
    
    private void readPage() {
        if (Log.DEBUG) {
            ColumnReaderImpl.LOG.debug("loading page");
        }
        final DataPage page = this.pageReader.readPage();
        page.accept((DataPage.Visitor<Object>)new DataPage.Visitor<Void>() {
            @Override
            public Void visit(final DataPageV1 dataPageV1) {
                ColumnReaderImpl.this.readPageV1(dataPageV1);
                return null;
            }
            
            @Override
            public Void visit(final DataPageV2 dataPageV2) {
                ColumnReaderImpl.this.readPageV2(dataPageV2);
                return null;
            }
        });
    }
    
    private void initDataReader(final Encoding dataEncoding, final byte[] bytes, final int offset, final int valueCount) {
        this.pageValueCount = valueCount;
        this.endOfPageValueCount = this.readValues + this.pageValueCount;
        if (dataEncoding.usesDictionary()) {
            if (this.dictionary == null) {
                throw new ParquetDecodingException("could not read page in col " + this.path + " as the dictionary was missing for encoding " + dataEncoding);
            }
            this.dataColumn = dataEncoding.getDictionaryBasedValuesReader(this.path, ValuesType.VALUES, this.dictionary);
        }
        else {
            this.dataColumn = dataEncoding.getValuesReader(this.path, ValuesType.VALUES);
        }
        if (dataEncoding.usesDictionary() && this.converter.hasDictionarySupport()) {
            this.bindToDictionary(this.dictionary);
        }
        else {
            this.bind(this.path.getType());
        }
        try {
            this.dataColumn.initFromPage(this.pageValueCount, bytes, offset);
        }
        catch (IOException e) {
            throw new ParquetDecodingException("could not read page in col " + this.path, e);
        }
    }
    
    private void readPageV1(final DataPageV1 page) {
        final ValuesReader rlReader = page.getRlEncoding().getValuesReader(this.path, ValuesType.REPETITION_LEVEL);
        final ValuesReader dlReader = page.getDlEncoding().getValuesReader(this.path, ValuesType.DEFINITION_LEVEL);
        this.repetitionLevelColumn = new ValuesReaderIntIterator(rlReader);
        this.definitionLevelColumn = new ValuesReaderIntIterator(dlReader);
        try {
            final byte[] bytes = page.getBytes().toByteArray();
            if (Log.DEBUG) {
                ColumnReaderImpl.LOG.debug("page size " + bytes.length + " bytes and " + this.pageValueCount + " records");
            }
            if (Log.DEBUG) {
                ColumnReaderImpl.LOG.debug("reading repetition levels at 0");
            }
            rlReader.initFromPage(this.pageValueCount, bytes, 0);
            int next = rlReader.getNextOffset();
            if (Log.DEBUG) {
                ColumnReaderImpl.LOG.debug("reading definition levels at " + next);
            }
            dlReader.initFromPage(this.pageValueCount, bytes, next);
            next = dlReader.getNextOffset();
            if (Log.DEBUG) {
                ColumnReaderImpl.LOG.debug("reading data at " + next);
            }
            this.initDataReader(page.getValueEncoding(), bytes, next, page.getValueCount());
        }
        catch (IOException e) {
            throw new ParquetDecodingException("could not read page " + page + " in col " + this.path, e);
        }
    }
    
    private void readPageV2(final DataPageV2 page) {
        this.repetitionLevelColumn = this.newRLEIterator(this.path.getMaxRepetitionLevel(), page.getRepetitionLevels());
        this.definitionLevelColumn = this.newRLEIterator(this.path.getMaxDefinitionLevel(), page.getDefinitionLevels());
        try {
            if (Log.DEBUG) {
                ColumnReaderImpl.LOG.debug("page data size " + page.getData().size() + " bytes and " + this.pageValueCount + " records");
            }
            this.initDataReader(page.getDataEncoding(), page.getData().toByteArray(), 0, page.getValueCount());
        }
        catch (IOException e) {
            throw new ParquetDecodingException("could not read page " + page + " in col " + this.path, e);
        }
    }
    
    private IntIterator newRLEIterator(final int maxLevel, final BytesInput bytes) {
        try {
            if (maxLevel == 0) {
                return new NullIntIterator();
            }
            return new RLEIntIterator(new RunLengthBitPackingHybridDecoder(BytesUtils.getWidthFromMaxInt(maxLevel), new ByteArrayInputStream(bytes.toByteArray())));
        }
        catch (IOException e) {
            throw new ParquetDecodingException("could not read levels in page for col " + this.path, e);
        }
    }
    
    private boolean isPageFullyConsumed() {
        return this.readValues >= this.endOfPageValueCount;
    }
    
    @Override
    public void consume() {
        this.checkRead();
        this.valueRead = false;
    }
    
    @Override
    public long getTotalValueCount() {
        return this.totalValueCount;
    }
    
    static {
        LOG = Log.getLog(ColumnReaderImpl.class);
    }
    
    private abstract static class Binding
    {
        abstract void read();
        
        abstract void skip();
        
        abstract void writeValue();
        
        public int getDictionaryId() {
            throw new UnsupportedOperationException();
        }
        
        public int getInteger() {
            throw new UnsupportedOperationException();
        }
        
        public boolean getBoolean() {
            throw new UnsupportedOperationException();
        }
        
        public long getLong() {
            throw new UnsupportedOperationException();
        }
        
        public Binary getBinary() {
            throw new UnsupportedOperationException();
        }
        
        public float getFloat() {
            throw new UnsupportedOperationException();
        }
        
        public double getDouble() {
            throw new UnsupportedOperationException();
        }
    }
    
    abstract static class IntIterator
    {
        abstract int nextInt();
    }
    
    static class ValuesReaderIntIterator extends IntIterator
    {
        ValuesReader delegate;
        
        public ValuesReaderIntIterator(final ValuesReader delegate) {
            this.delegate = delegate;
        }
        
        @Override
        int nextInt() {
            return this.delegate.readInteger();
        }
    }
    
    static class RLEIntIterator extends IntIterator
    {
        RunLengthBitPackingHybridDecoder delegate;
        
        public RLEIntIterator(final RunLengthBitPackingHybridDecoder delegate) {
            this.delegate = delegate;
        }
        
        @Override
        int nextInt() {
            try {
                return this.delegate.readInt();
            }
            catch (IOException e) {
                throw new ParquetDecodingException(e);
            }
        }
    }
    
    private static final class NullIntIterator extends IntIterator
    {
        @Override
        int nextInt() {
            return 0;
        }
    }
}
