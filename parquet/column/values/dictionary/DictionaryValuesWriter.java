// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.dictionary;

import parquet.it.unimi.dsi.fastutil.floats.FloatIterator;
import parquet.it.unimi.dsi.fastutil.floats.Float2IntLinkedOpenHashMap;
import parquet.it.unimi.dsi.fastutil.floats.Float2IntMap;
import parquet.it.unimi.dsi.fastutil.ints.IntIterator;
import parquet.it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import parquet.it.unimi.dsi.fastutil.ints.Int2IntMap;
import parquet.it.unimi.dsi.fastutil.doubles.DoubleIterator;
import parquet.it.unimi.dsi.fastutil.doubles.Double2IntLinkedOpenHashMap;
import parquet.it.unimi.dsi.fastutil.doubles.Double2IntMap;
import parquet.it.unimi.dsi.fastutil.objects.ObjectIterator;
import parquet.it.unimi.dsi.fastutil.longs.LongIterator;
import parquet.it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import parquet.it.unimi.dsi.fastutil.longs.Long2IntMap;
import parquet.column.values.plain.FixedLenByteArrayPlainValuesWriter;
import java.util.Arrays;
import java.util.Iterator;
import parquet.column.values.plain.PlainValuesWriter;
import parquet.it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import parquet.io.api.Binary;
import parquet.it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.IOException;
import parquet.io.ParquetEncodingException;
import parquet.column.values.rle.RunLengthBitPackingHybridEncoder;
import parquet.bytes.CapacityByteArrayOutputStream;
import parquet.bytes.BytesUtils;
import parquet.bytes.BytesInput;
import parquet.column.page.DictionaryPage;
import parquet.column.Encoding;
import parquet.Log;
import parquet.column.values.RequiresFallback;
import parquet.column.values.ValuesWriter;

public abstract class DictionaryValuesWriter extends ValuesWriter implements RequiresFallback
{
    private static final Log LOG;
    private static final int MAX_DICTIONARY_ENTRIES = 2147483646;
    private static final int MIN_INITIAL_SLAB_SIZE = 64;
    private final Encoding encodingForDataPage;
    protected final Encoding encodingForDictionaryPage;
    protected final int maxDictionaryByteSize;
    protected boolean dictionaryTooBig;
    protected int dictionaryByteSize;
    protected int lastUsedDictionaryByteSize;
    protected int lastUsedDictionarySize;
    protected IntList encodedValues;
    
    protected DictionaryValuesWriter(final int maxDictionaryByteSize, final Encoding encodingForDataPage, final Encoding encodingForDictionaryPage) {
        this.encodedValues = new IntList();
        this.maxDictionaryByteSize = maxDictionaryByteSize;
        this.encodingForDataPage = encodingForDataPage;
        this.encodingForDictionaryPage = encodingForDictionaryPage;
    }
    
    protected DictionaryPage dictPage(final ValuesWriter dictionaryEncoder) {
        return new DictionaryPage(dictionaryEncoder.getBytes(), this.lastUsedDictionarySize, this.encodingForDictionaryPage);
    }
    
    @Override
    public boolean shouldFallBack() {
        return this.dictionaryByteSize > this.maxDictionaryByteSize || this.getDictionarySize() > 2147483646;
    }
    
    @Override
    public boolean isCompressionSatisfying(final long rawSize, final long encodedSize) {
        return encodedSize + this.dictionaryByteSize < rawSize;
    }
    
    @Override
    public void fallBackAllValuesTo(final ValuesWriter writer) {
        this.fallBackDictionaryEncodedData(writer);
        if (this.lastUsedDictionarySize == 0) {
            this.clearDictionaryContent();
            this.dictionaryByteSize = 0;
            this.encodedValues = new IntList();
        }
    }
    
    protected abstract void fallBackDictionaryEncodedData(final ValuesWriter p0);
    
    @Override
    public long getBufferedSize() {
        return this.encodedValues.size() * 4;
    }
    
    @Override
    public long getAllocatedSize() {
        return this.encodedValues.size() * 4 + this.dictionaryByteSize;
    }
    
    @Override
    public BytesInput getBytes() {
        final int maxDicId = this.getDictionarySize() - 1;
        if (Log.DEBUG) {
            DictionaryValuesWriter.LOG.debug("max dic id " + maxDicId);
        }
        final int bitWidth = BytesUtils.getWidthFromMaxInt(maxDicId);
        final int initialSlabSize = CapacityByteArrayOutputStream.initialSlabSizeHeuristic(64, this.maxDictionaryByteSize, 10);
        final RunLengthBitPackingHybridEncoder encoder = new RunLengthBitPackingHybridEncoder(bitWidth, initialSlabSize, this.maxDictionaryByteSize);
        final IntList.IntIterator iterator = this.encodedValues.iterator();
        try {
            while (iterator.hasNext()) {
                encoder.writeInt(iterator.next());
            }
            final byte[] bytesHeader = { (byte)bitWidth };
            final BytesInput rleEncodedBytes = encoder.toBytes();
            if (Log.DEBUG) {
                DictionaryValuesWriter.LOG.debug("rle encoded bytes " + rleEncodedBytes.size());
            }
            final BytesInput bytes = BytesInput.concat(BytesInput.from(bytesHeader), rleEncodedBytes);
            this.lastUsedDictionarySize = this.getDictionarySize();
            this.lastUsedDictionaryByteSize = this.dictionaryByteSize;
            return bytes;
        }
        catch (IOException e) {
            throw new ParquetEncodingException("could not encode the values", e);
        }
    }
    
    @Override
    public Encoding getEncoding() {
        return this.encodingForDataPage;
    }
    
    @Override
    public void reset() {
        this.encodedValues = new IntList();
    }
    
    @Override
    public void resetDictionary() {
        this.lastUsedDictionaryByteSize = 0;
        this.lastUsedDictionarySize = 0;
        this.dictionaryTooBig = false;
        this.clearDictionaryContent();
    }
    
    protected abstract void clearDictionaryContent();
    
    protected abstract int getDictionarySize();
    
    @Override
    public String memUsageString(final String prefix) {
        return String.format("%s DictionaryValuesWriter{\n%s\n%s\n%s}\n", prefix, prefix + " dict:" + this.dictionaryByteSize, prefix + " values:" + String.valueOf(this.encodedValues.size() * 4), prefix);
    }
    
    static {
        LOG = Log.getLog(DictionaryValuesWriter.class);
    }
    
    public static class PlainBinaryDictionaryValuesWriter extends DictionaryValuesWriter
    {
        protected Object2IntMap<Binary> binaryDictionaryContent;
        
        public PlainBinaryDictionaryValuesWriter(final int maxDictionaryByteSize, final Encoding encodingForDataPage, final Encoding encodingForDictionaryPage) {
            super(maxDictionaryByteSize, encodingForDataPage, encodingForDictionaryPage);
            (this.binaryDictionaryContent = new Object2IntLinkedOpenHashMap<Binary>()).defaultReturnValue(-1);
        }
        
        @Override
        public void writeBytes(final Binary v) {
            int id = this.binaryDictionaryContent.getInt(v);
            if (id == -1) {
                id = this.binaryDictionaryContent.size();
                this.binaryDictionaryContent.put(copy(v), id);
                this.dictionaryByteSize += 4 + v.length();
            }
            this.encodedValues.add(id);
        }
        
        @Override
        public DictionaryPage createDictionaryPage() {
            if (this.lastUsedDictionarySize > 0) {
                final PlainValuesWriter dictionaryEncoder = new PlainValuesWriter(this.lastUsedDictionaryByteSize, this.maxDictionaryByteSize);
                final Iterator<Binary> binaryIterator = this.binaryDictionaryContent.keySet().iterator();
                for (int i = 0; i < this.lastUsedDictionarySize; ++i) {
                    final Binary entry = binaryIterator.next();
                    dictionaryEncoder.writeBytes(entry);
                }
                return this.dictPage(dictionaryEncoder);
            }
            return null;
        }
        
        public int getDictionarySize() {
            return this.binaryDictionaryContent.size();
        }
        
        @Override
        protected void clearDictionaryContent() {
            this.binaryDictionaryContent.clear();
        }
        
        public void fallBackDictionaryEncodedData(final ValuesWriter writer) {
            final Binary[] reverseDictionary = new Binary[this.getDictionarySize()];
            for (final Object2IntMap.Entry<Binary> entry : this.binaryDictionaryContent.object2IntEntrySet()) {
                reverseDictionary[entry.getIntValue()] = entry.getKey();
            }
            for (final int id : this.encodedValues) {
                writer.writeBytes(reverseDictionary[id]);
            }
        }
        
        protected static Binary copy(final Binary binary) {
            return Binary.fromByteArray(Arrays.copyOf(binary.getBytes(), binary.length()));
        }
    }
    
    public static class PlainFixedLenArrayDictionaryValuesWriter extends PlainBinaryDictionaryValuesWriter
    {
        private final int length;
        
        public PlainFixedLenArrayDictionaryValuesWriter(final int maxDictionaryByteSize, final int length, final Encoding encodingForDataPage, final Encoding encodingForDictionaryPage) {
            super(maxDictionaryByteSize, encodingForDataPage, encodingForDictionaryPage);
            this.length = length;
        }
        
        @Override
        public void writeBytes(final Binary value) {
            int id = this.binaryDictionaryContent.getInt(value);
            if (id == -1) {
                id = this.binaryDictionaryContent.size();
                this.binaryDictionaryContent.put(PlainBinaryDictionaryValuesWriter.copy(value), id);
                this.dictionaryByteSize += this.length;
            }
            this.encodedValues.add(id);
        }
        
        @Override
        public DictionaryPage createDictionaryPage() {
            if (this.lastUsedDictionarySize > 0) {
                final FixedLenByteArrayPlainValuesWriter dictionaryEncoder = new FixedLenByteArrayPlainValuesWriter(this.length, this.lastUsedDictionaryByteSize, this.maxDictionaryByteSize);
                final Iterator<Binary> binaryIterator = this.binaryDictionaryContent.keySet().iterator();
                for (int i = 0; i < this.lastUsedDictionarySize; ++i) {
                    final Binary entry = binaryIterator.next();
                    dictionaryEncoder.writeBytes(entry);
                }
                return this.dictPage(dictionaryEncoder);
            }
            return null;
        }
    }
    
    public static class PlainLongDictionaryValuesWriter extends DictionaryValuesWriter
    {
        private Long2IntMap longDictionaryContent;
        
        public PlainLongDictionaryValuesWriter(final int maxDictionaryByteSize, final Encoding encodingForDataPage, final Encoding encodingForDictionaryPage) {
            super(maxDictionaryByteSize, encodingForDataPage, encodingForDictionaryPage);
            (this.longDictionaryContent = new Long2IntLinkedOpenHashMap()).defaultReturnValue(-1);
        }
        
        @Override
        public void writeLong(final long v) {
            int id = this.longDictionaryContent.get(v);
            if (id == -1) {
                id = this.longDictionaryContent.size();
                this.longDictionaryContent.put(v, id);
                this.dictionaryByteSize += 8;
            }
            this.encodedValues.add(id);
        }
        
        @Override
        public DictionaryPage createDictionaryPage() {
            if (this.lastUsedDictionarySize > 0) {
                final PlainValuesWriter dictionaryEncoder = new PlainValuesWriter(this.lastUsedDictionaryByteSize, this.maxDictionaryByteSize);
                final LongIterator longIterator = this.longDictionaryContent.keySet().iterator();
                for (int i = 0; i < this.lastUsedDictionarySize; ++i) {
                    dictionaryEncoder.writeLong(longIterator.nextLong());
                }
                return this.dictPage(dictionaryEncoder);
            }
            return null;
        }
        
        public int getDictionarySize() {
            return this.longDictionaryContent.size();
        }
        
        @Override
        protected void clearDictionaryContent() {
            this.longDictionaryContent.clear();
        }
        
        public void fallBackDictionaryEncodedData(final ValuesWriter writer) {
            final long[] reverseDictionary = new long[this.getDictionarySize()];
            for (final Long2IntMap.Entry entry : this.longDictionaryContent.long2IntEntrySet()) {
                reverseDictionary[entry.getIntValue()] = entry.getLongKey();
            }
            for (final int id : this.encodedValues) {
                writer.writeLong(reverseDictionary[id]);
            }
        }
    }
    
    public static class PlainDoubleDictionaryValuesWriter extends DictionaryValuesWriter
    {
        private Double2IntMap doubleDictionaryContent;
        
        public PlainDoubleDictionaryValuesWriter(final int maxDictionaryByteSize, final Encoding encodingForDataPage, final Encoding encodingForDictionaryPage) {
            super(maxDictionaryByteSize, encodingForDataPage, encodingForDictionaryPage);
            (this.doubleDictionaryContent = new Double2IntLinkedOpenHashMap()).defaultReturnValue(-1);
        }
        
        @Override
        public void writeDouble(final double v) {
            int id = this.doubleDictionaryContent.get(v);
            if (id == -1) {
                id = this.doubleDictionaryContent.size();
                this.doubleDictionaryContent.put(v, id);
                this.dictionaryByteSize += 8;
            }
            this.encodedValues.add(id);
        }
        
        @Override
        public DictionaryPage createDictionaryPage() {
            if (this.lastUsedDictionarySize > 0) {
                final PlainValuesWriter dictionaryEncoder = new PlainValuesWriter(this.lastUsedDictionaryByteSize, this.maxDictionaryByteSize);
                final DoubleIterator doubleIterator = this.doubleDictionaryContent.keySet().iterator();
                for (int i = 0; i < this.lastUsedDictionarySize; ++i) {
                    dictionaryEncoder.writeDouble(doubleIterator.nextDouble());
                }
                return this.dictPage(dictionaryEncoder);
            }
            return null;
        }
        
        public int getDictionarySize() {
            return this.doubleDictionaryContent.size();
        }
        
        @Override
        protected void clearDictionaryContent() {
            this.doubleDictionaryContent.clear();
        }
        
        public void fallBackDictionaryEncodedData(final ValuesWriter writer) {
            final double[] reverseDictionary = new double[this.getDictionarySize()];
            for (final Double2IntMap.Entry entry : this.doubleDictionaryContent.double2IntEntrySet()) {
                reverseDictionary[entry.getIntValue()] = entry.getDoubleKey();
            }
            for (final int id : this.encodedValues) {
                writer.writeDouble(reverseDictionary[id]);
            }
        }
    }
    
    public static class PlainIntegerDictionaryValuesWriter extends DictionaryValuesWriter
    {
        private Int2IntMap intDictionaryContent;
        
        public PlainIntegerDictionaryValuesWriter(final int maxDictionaryByteSize, final Encoding encodingForDataPage, final Encoding encodingForDictionaryPage) {
            super(maxDictionaryByteSize, encodingForDataPage, encodingForDictionaryPage);
            (this.intDictionaryContent = new Int2IntLinkedOpenHashMap()).defaultReturnValue(-1);
        }
        
        @Override
        public void writeInteger(final int v) {
            int id = this.intDictionaryContent.get(v);
            if (id == -1) {
                id = this.intDictionaryContent.size();
                this.intDictionaryContent.put(v, id);
                this.dictionaryByteSize += 4;
            }
            this.encodedValues.add(id);
        }
        
        @Override
        public DictionaryPage createDictionaryPage() {
            if (this.lastUsedDictionarySize > 0) {
                final PlainValuesWriter dictionaryEncoder = new PlainValuesWriter(this.lastUsedDictionaryByteSize, this.maxDictionaryByteSize);
                final IntIterator intIterator = this.intDictionaryContent.keySet().iterator();
                for (int i = 0; i < this.lastUsedDictionarySize; ++i) {
                    dictionaryEncoder.writeInteger(intIterator.nextInt());
                }
                return this.dictPage(dictionaryEncoder);
            }
            return null;
        }
        
        public int getDictionarySize() {
            return this.intDictionaryContent.size();
        }
        
        @Override
        protected void clearDictionaryContent() {
            this.intDictionaryContent.clear();
        }
        
        public void fallBackDictionaryEncodedData(final ValuesWriter writer) {
            final int[] reverseDictionary = new int[this.getDictionarySize()];
            for (final Int2IntMap.Entry entry : this.intDictionaryContent.int2IntEntrySet()) {
                reverseDictionary[entry.getIntValue()] = entry.getIntKey();
            }
            for (final int id : this.encodedValues) {
                writer.writeInteger(reverseDictionary[id]);
            }
        }
    }
    
    public static class PlainFloatDictionaryValuesWriter extends DictionaryValuesWriter
    {
        private Float2IntMap floatDictionaryContent;
        
        public PlainFloatDictionaryValuesWriter(final int maxDictionaryByteSize, final Encoding encodingForDataPage, final Encoding encodingForDictionaryPage) {
            super(maxDictionaryByteSize, encodingForDataPage, encodingForDictionaryPage);
            (this.floatDictionaryContent = new Float2IntLinkedOpenHashMap()).defaultReturnValue(-1);
        }
        
        @Override
        public void writeFloat(final float v) {
            int id = this.floatDictionaryContent.get(v);
            if (id == -1) {
                id = this.floatDictionaryContent.size();
                this.floatDictionaryContent.put(v, id);
                this.dictionaryByteSize += 4;
            }
            this.encodedValues.add(id);
        }
        
        @Override
        public DictionaryPage createDictionaryPage() {
            if (this.lastUsedDictionarySize > 0) {
                final PlainValuesWriter dictionaryEncoder = new PlainValuesWriter(this.lastUsedDictionaryByteSize, this.maxDictionaryByteSize);
                final FloatIterator floatIterator = this.floatDictionaryContent.keySet().iterator();
                for (int i = 0; i < this.lastUsedDictionarySize; ++i) {
                    dictionaryEncoder.writeFloat(floatIterator.nextFloat());
                }
                return this.dictPage(dictionaryEncoder);
            }
            return null;
        }
        
        public int getDictionarySize() {
            return this.floatDictionaryContent.size();
        }
        
        @Override
        protected void clearDictionaryContent() {
            this.floatDictionaryContent.clear();
        }
        
        public void fallBackDictionaryEncodedData(final ValuesWriter writer) {
            final float[] reverseDictionary = new float[this.getDictionarySize()];
            for (final Float2IntMap.Entry entry : this.floatDictionaryContent.float2IntEntrySet()) {
                reverseDictionary[entry.getIntValue()] = entry.getFloatKey();
            }
            for (final int id : this.encodedValues) {
                writer.writeFloat(reverseDictionary[id]);
            }
        }
    }
}
