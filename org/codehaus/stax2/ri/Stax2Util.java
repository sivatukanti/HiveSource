// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import javax.xml.stream.XMLStreamConstants;

public final class Stax2Util implements XMLStreamConstants
{
    private Stax2Util() {
    }
    
    public static String eventTypeDesc(final int i) {
        switch (i) {
            case 1: {
                return "START_ELEMENT";
            }
            case 2: {
                return "END_ELEMENT";
            }
            case 7: {
                return "START_DOCUMENT";
            }
            case 8: {
                return "END_DOCUMENT";
            }
            case 4: {
                return "CHARACTERS";
            }
            case 12: {
                return "CDATA";
            }
            case 6: {
                return "SPACE";
            }
            case 5: {
                return "COMMENT";
            }
            case 3: {
                return "PROCESSING_INSTRUCTION";
            }
            case 11: {
                return "DTD";
            }
            case 9: {
                return "ENTITY_REFERENCE";
            }
            default: {
                return "[" + i + "]";
            }
        }
    }
    
    public static String trimSpaces(final String s) {
        int length = s.length();
        int i = 0;
        while (i < length) {
            if (!_isSpace(s.charAt(i))) {
                --length;
                if (!_isSpace(s.charAt(length))) {
                    return (i == 0) ? s : s.substring(i);
                }
                while (--length > i && _isSpace(s.charAt(length))) {}
                return s.substring(i, length + 1);
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
    private static final boolean _isSpace(final char c) {
        return c <= ' ';
    }
    
    public static final class TextBuffer
    {
        private String mText;
        private StringBuffer mBuilder;
        
        public TextBuffer() {
            this.mText = null;
            this.mBuilder = null;
        }
        
        public void reset() {
            this.mText = null;
            this.mBuilder = null;
        }
        
        public void append(final String s) {
            final int length = s.length();
            if (length > 0) {
                if (this.mText != null) {
                    (this.mBuilder = new StringBuffer(this.mText.length() + length)).append(this.mText);
                    this.mText = null;
                }
                if (this.mBuilder != null) {
                    this.mBuilder.append(s);
                }
                else {
                    this.mText = s;
                }
            }
        }
        
        public String get() {
            if (this.mText != null) {
                return this.mText;
            }
            if (this.mBuilder != null) {
                return this.mBuilder.toString();
            }
            return "";
        }
        
        public boolean isEmpty() {
            return this.mText == null && this.mBuilder == null;
        }
    }
    
    public static final class ByteAggregator
    {
        private static final byte[] NO_BYTES;
        private static final int INITIAL_BLOCK_SIZE = 500;
        static final int DEFAULT_BLOCK_ARRAY_SIZE = 100;
        private byte[][] mBlocks;
        private int mBlockCount;
        private int mTotalLen;
        private byte[] mSpareBlock;
        
        public byte[] startAggregation() {
            this.mTotalLen = 0;
            this.mBlockCount = 0;
            byte[] mSpareBlock = this.mSpareBlock;
            if (mSpareBlock == null) {
                mSpareBlock = new byte[500];
            }
            else {
                this.mSpareBlock = null;
            }
            return mSpareBlock;
        }
        
        public byte[] addFullBlock(final byte[] array) {
            final int length = array.length;
            if (this.mBlocks == null) {
                this.mBlocks = new byte[100][];
            }
            else {
                final int length2 = this.mBlocks.length;
                if (this.mBlockCount >= length2) {
                    System.arraycopy(this.mBlocks, 0, this.mBlocks = new byte[length2 + length2][], 0, length2);
                }
            }
            this.mBlocks[this.mBlockCount] = array;
            ++this.mBlockCount;
            this.mTotalLen += length;
            return new byte[Math.max(this.mTotalLen >> 1, 1000)];
        }
        
        public byte[] aggregateAll(final byte[] mSpareBlock, final int n) {
            final int i = this.mTotalLen + n;
            if (i == 0) {
                return ByteAggregator.NO_BYTES;
            }
            final byte[] array = new byte[i];
            int n2 = 0;
            if (this.mBlocks != null) {
                for (int j = 0; j < this.mBlockCount; ++j) {
                    final byte[] array2 = this.mBlocks[j];
                    final int length = array2.length;
                    System.arraycopy(array2, 0, array, n2, length);
                    n2 += length;
                }
            }
            System.arraycopy(mSpareBlock, 0, array, n2, n);
            this.mSpareBlock = mSpareBlock;
            final int k = n2 + n;
            if (k != i) {
                throw new RuntimeException("Internal error: total len assumed to be " + i + ", copied " + k + " bytes");
            }
            return array;
        }
        
        static {
            NO_BYTES = new byte[0];
        }
    }
}
