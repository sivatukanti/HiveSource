// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.dictionary;

import parquet.column.values.plain.PlainValuesReader;
import parquet.Preconditions;
import parquet.bytes.BytesUtils;
import parquet.io.api.Binary;
import java.io.IOException;
import parquet.io.ParquetDecodingException;
import parquet.column.Encoding;
import parquet.column.page.DictionaryPage;
import parquet.column.Dictionary;

public abstract class PlainValuesDictionary extends Dictionary
{
    protected PlainValuesDictionary(final DictionaryPage dictionaryPage) throws IOException {
        super(dictionaryPage.getEncoding());
        if (dictionaryPage.getEncoding() != Encoding.PLAIN_DICTIONARY && dictionaryPage.getEncoding() != Encoding.PLAIN) {
            throw new ParquetDecodingException("Dictionary data encoding type not supported: " + dictionaryPage.getEncoding());
        }
    }
    
    public static class PlainBinaryDictionary extends PlainValuesDictionary
    {
        private Binary[] binaryDictionaryContent;
        
        public PlainBinaryDictionary(final DictionaryPage dictionaryPage) throws IOException {
            this(dictionaryPage, null);
        }
        
        public PlainBinaryDictionary(final DictionaryPage dictionaryPage, final Integer length) throws IOException {
            super(dictionaryPage);
            this.binaryDictionaryContent = null;
            final byte[] dictionaryBytes = dictionaryPage.getBytes().toByteArray();
            this.binaryDictionaryContent = new Binary[dictionaryPage.getDictionarySize()];
            int offset = 0;
            if (length == null) {
                for (int i = 0; i < this.binaryDictionaryContent.length; ++i) {
                    final int len = BytesUtils.readIntLittleEndian(dictionaryBytes, offset);
                    offset += 4;
                    this.binaryDictionaryContent[i] = Binary.fromByteArray(dictionaryBytes, offset, len);
                    offset += len;
                }
            }
            else {
                Preconditions.checkArgument(length > 0, "Invalid byte array length: " + length);
                for (int i = 0; i < this.binaryDictionaryContent.length; ++i) {
                    this.binaryDictionaryContent[i] = Binary.fromByteArray(dictionaryBytes, offset, length);
                    offset += length;
                }
            }
        }
        
        @Override
        public Binary decodeToBinary(final int id) {
            return this.binaryDictionaryContent[id];
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PlainBinaryDictionary {\n");
            for (int i = 0; i < this.binaryDictionaryContent.length; ++i) {
                sb.append(i).append(" => ").append(this.binaryDictionaryContent[i]).append("\n");
            }
            return sb.append("}").toString();
        }
        
        @Override
        public int getMaxId() {
            return this.binaryDictionaryContent.length - 1;
        }
    }
    
    public static class PlainLongDictionary extends PlainValuesDictionary
    {
        private long[] longDictionaryContent;
        
        public PlainLongDictionary(final DictionaryPage dictionaryPage) throws IOException {
            super(dictionaryPage);
            this.longDictionaryContent = null;
            final byte[] dictionaryBytes = dictionaryPage.getBytes().toByteArray();
            this.longDictionaryContent = new long[dictionaryPage.getDictionarySize()];
            final PlainValuesReader.LongPlainValuesReader longReader = new PlainValuesReader.LongPlainValuesReader();
            longReader.initFromPage(dictionaryPage.getDictionarySize(), dictionaryBytes, 0);
            for (int i = 0; i < this.longDictionaryContent.length; ++i) {
                this.longDictionaryContent[i] = longReader.readLong();
            }
        }
        
        @Override
        public long decodeToLong(final int id) {
            return this.longDictionaryContent[id];
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PlainLongDictionary {\n");
            for (int i = 0; i < this.longDictionaryContent.length; ++i) {
                sb.append(i).append(" => ").append(this.longDictionaryContent[i]).append("\n");
            }
            return sb.append("}").toString();
        }
        
        @Override
        public int getMaxId() {
            return this.longDictionaryContent.length - 1;
        }
    }
    
    public static class PlainDoubleDictionary extends PlainValuesDictionary
    {
        private double[] doubleDictionaryContent;
        
        public PlainDoubleDictionary(final DictionaryPage dictionaryPage) throws IOException {
            super(dictionaryPage);
            this.doubleDictionaryContent = null;
            final byte[] dictionaryBytes = dictionaryPage.getBytes().toByteArray();
            this.doubleDictionaryContent = new double[dictionaryPage.getDictionarySize()];
            final PlainValuesReader.DoublePlainValuesReader doubleReader = new PlainValuesReader.DoublePlainValuesReader();
            doubleReader.initFromPage(dictionaryPage.getDictionarySize(), dictionaryBytes, 0);
            for (int i = 0; i < this.doubleDictionaryContent.length; ++i) {
                this.doubleDictionaryContent[i] = doubleReader.readDouble();
            }
        }
        
        @Override
        public double decodeToDouble(final int id) {
            return this.doubleDictionaryContent[id];
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PlainDoubleDictionary {\n");
            for (int i = 0; i < this.doubleDictionaryContent.length; ++i) {
                sb.append(i).append(" => ").append(this.doubleDictionaryContent[i]).append("\n");
            }
            return sb.append("}").toString();
        }
        
        @Override
        public int getMaxId() {
            return this.doubleDictionaryContent.length - 1;
        }
    }
    
    public static class PlainIntegerDictionary extends PlainValuesDictionary
    {
        private int[] intDictionaryContent;
        
        public PlainIntegerDictionary(final DictionaryPage dictionaryPage) throws IOException {
            super(dictionaryPage);
            this.intDictionaryContent = null;
            final byte[] dictionaryBytes = dictionaryPage.getBytes().toByteArray();
            this.intDictionaryContent = new int[dictionaryPage.getDictionarySize()];
            final PlainValuesReader.IntegerPlainValuesReader intReader = new PlainValuesReader.IntegerPlainValuesReader();
            intReader.initFromPage(dictionaryPage.getDictionarySize(), dictionaryBytes, 0);
            for (int i = 0; i < this.intDictionaryContent.length; ++i) {
                this.intDictionaryContent[i] = intReader.readInteger();
            }
        }
        
        @Override
        public int decodeToInt(final int id) {
            return this.intDictionaryContent[id];
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PlainIntegerDictionary {\n");
            for (int i = 0; i < this.intDictionaryContent.length; ++i) {
                sb.append(i).append(" => ").append(this.intDictionaryContent[i]).append("\n");
            }
            return sb.append("}").toString();
        }
        
        @Override
        public int getMaxId() {
            return this.intDictionaryContent.length - 1;
        }
    }
    
    public static class PlainFloatDictionary extends PlainValuesDictionary
    {
        private float[] floatDictionaryContent;
        
        public PlainFloatDictionary(final DictionaryPage dictionaryPage) throws IOException {
            super(dictionaryPage);
            this.floatDictionaryContent = null;
            final byte[] dictionaryBytes = dictionaryPage.getBytes().toByteArray();
            this.floatDictionaryContent = new float[dictionaryPage.getDictionarySize()];
            final PlainValuesReader.FloatPlainValuesReader floatReader = new PlainValuesReader.FloatPlainValuesReader();
            floatReader.initFromPage(dictionaryPage.getDictionarySize(), dictionaryBytes, 0);
            for (int i = 0; i < this.floatDictionaryContent.length; ++i) {
                this.floatDictionaryContent[i] = floatReader.readFloat();
            }
        }
        
        @Override
        public float decodeToFloat(final int id) {
            return this.floatDictionaryContent[id];
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PlainFloatDictionary {\n");
            for (int i = 0; i < this.floatDictionaryContent.length; ++i) {
                sb.append(i).append(" => ").append(this.floatDictionaryContent[i]).append("\n");
            }
            return sb.append("}").toString();
        }
        
        @Override
        public int getMaxId() {
            return this.floatDictionaryContent.length - 1;
        }
    }
}
