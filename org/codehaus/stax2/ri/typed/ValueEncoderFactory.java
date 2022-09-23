// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.typed.Base64Variant;

public final class ValueEncoderFactory
{
    static final byte BYTE_SPACE = 32;
    protected TokenEncoder _tokenEncoder;
    protected IntEncoder _intEncoder;
    protected LongEncoder _longEncoder;
    protected FloatEncoder _floatEncoder;
    protected DoubleEncoder _doubleEncoder;
    
    public ValueEncoderFactory() {
        this._tokenEncoder = null;
        this._intEncoder = null;
        this._longEncoder = null;
        this._floatEncoder = null;
        this._doubleEncoder = null;
    }
    
    public ScalarEncoder getScalarEncoder(final String s) {
        if (s.length() > 64) {
            if (this._tokenEncoder == null) {
                this._tokenEncoder = new TokenEncoder();
            }
            this._tokenEncoder.reset(s);
            return this._tokenEncoder;
        }
        return new StringEncoder(s);
    }
    
    public ScalarEncoder getEncoder(final boolean b) {
        return this.getScalarEncoder(b ? "true" : "false");
    }
    
    public IntEncoder getEncoder(final int n) {
        if (this._intEncoder == null) {
            this._intEncoder = new IntEncoder();
        }
        this._intEncoder.reset(n);
        return this._intEncoder;
    }
    
    public LongEncoder getEncoder(final long n) {
        if (this._longEncoder == null) {
            this._longEncoder = new LongEncoder();
        }
        this._longEncoder.reset(n);
        return this._longEncoder;
    }
    
    public FloatEncoder getEncoder(final float n) {
        if (this._floatEncoder == null) {
            this._floatEncoder = new FloatEncoder();
        }
        this._floatEncoder.reset(n);
        return this._floatEncoder;
    }
    
    public DoubleEncoder getEncoder(final double n) {
        if (this._doubleEncoder == null) {
            this._doubleEncoder = new DoubleEncoder();
        }
        this._doubleEncoder.reset(n);
        return this._doubleEncoder;
    }
    
    public IntArrayEncoder getEncoder(final int[] array, final int n, final int n2) {
        return new IntArrayEncoder(array, n, n + n2);
    }
    
    public LongArrayEncoder getEncoder(final long[] array, final int n, final int n2) {
        return new LongArrayEncoder(array, n, n + n2);
    }
    
    public FloatArrayEncoder getEncoder(final float[] array, final int n, final int n2) {
        return new FloatArrayEncoder(array, n, n + n2);
    }
    
    public DoubleArrayEncoder getEncoder(final double[] array, final int n, final int n2) {
        return new DoubleArrayEncoder(array, n, n + n2);
    }
    
    public Base64Encoder getEncoder(final Base64Variant base64Variant, final byte[] array, final int n, final int n2) {
        return new Base64Encoder(base64Variant, array, n, n + n2);
    }
    
    abstract static class ScalarEncoder extends AsciiValueEncoder
    {
        protected ScalarEncoder() {
        }
    }
    
    static final class TokenEncoder extends ScalarEncoder
    {
        String _value;
        
        protected TokenEncoder() {
        }
        
        protected void reset(final String value) {
            this._value = value;
        }
        
        @Override
        public boolean isCompleted() {
            return this._value == null;
        }
        
        @Override
        public int encodeMore(final char[] dst, int dstBegin, final int n) {
            final String value = this._value;
            this._value = null;
            final int length = value.length();
            value.getChars(0, length, dst, dstBegin);
            dstBegin += length;
            return dstBegin;
        }
        
        @Override
        public int encodeMore(final byte[] array, int n, final int n2) {
            final String value = this._value;
            this._value = null;
            for (int length = value.length(), i = 0; i < length; ++i) {
                array[n++] = (byte)value.charAt(i);
            }
            return n;
        }
    }
    
    static final class StringEncoder extends ScalarEncoder
    {
        String _value;
        int _offset;
        
        protected StringEncoder(final String value) {
            this._value = value;
        }
        
        @Override
        public boolean isCompleted() {
            return this._value == null;
        }
        
        @Override
        public int encodeMore(final char[] array, final int n, final int n2) {
            final int srcEnd = this._value.length() - this._offset;
            final int srcEnd2 = n2 - n;
            if (srcEnd2 >= srcEnd) {
                this._value.getChars(this._offset, srcEnd, array, n);
                this._value = null;
                return n + srcEnd;
            }
            this._value.getChars(this._offset, srcEnd2, array, n);
            this._offset += srcEnd2;
            return n2;
        }
        
        @Override
        public int encodeMore(final byte[] array, int i, final int n) {
            if (n - i >= this._value.length() - this._offset) {
                final String value = this._value;
                this._value = null;
                for (int length = value.length(), j = this._offset; j < length; ++j) {
                    array[i++] = (byte)value.charAt(j);
                }
                return i;
            }
            while (i < n) {
                array[i] = (byte)this._value.charAt(this._offset++);
                ++i;
            }
            return i;
        }
    }
    
    abstract static class TypedScalarEncoder extends ScalarEncoder
    {
        protected TypedScalarEncoder() {
        }
        
        @Override
        public final boolean isCompleted() {
            return true;
        }
    }
    
    static final class IntEncoder extends TypedScalarEncoder
    {
        int _value;
        
        protected IntEncoder() {
        }
        
        protected void reset(final int value) {
            this._value = value;
        }
        
        @Override
        public int encodeMore(final char[] array, final int n, final int n2) {
            return NumberUtil.writeInt(this._value, array, n);
        }
        
        @Override
        public int encodeMore(final byte[] array, final int n, final int n2) {
            return NumberUtil.writeInt(this._value, array, n);
        }
    }
    
    static final class LongEncoder extends TypedScalarEncoder
    {
        long _value;
        
        protected LongEncoder() {
        }
        
        protected void reset(final long value) {
            this._value = value;
        }
        
        @Override
        public int encodeMore(final char[] array, final int n, final int n2) {
            return NumberUtil.writeLong(this._value, array, n);
        }
        
        @Override
        public int encodeMore(final byte[] array, final int n, final int n2) {
            return NumberUtil.writeLong(this._value, array, n);
        }
    }
    
    static final class FloatEncoder extends TypedScalarEncoder
    {
        float _value;
        
        protected FloatEncoder() {
        }
        
        protected void reset(final float value) {
            this._value = value;
        }
        
        @Override
        public int encodeMore(final char[] array, final int n, final int n2) {
            return NumberUtil.writeFloat(this._value, array, n);
        }
        
        @Override
        public int encodeMore(final byte[] array, final int n, final int n2) {
            return NumberUtil.writeFloat(this._value, array, n);
        }
    }
    
    static final class DoubleEncoder extends TypedScalarEncoder
    {
        double _value;
        
        protected DoubleEncoder() {
        }
        
        protected void reset(final double value) {
            this._value = value;
        }
        
        @Override
        public int encodeMore(final char[] array, final int n, final int n2) {
            return NumberUtil.writeDouble(this._value, array, n);
        }
        
        @Override
        public int encodeMore(final byte[] array, final int n, final int n2) {
            return NumberUtil.writeDouble(this._value, array, n);
        }
    }
    
    abstract static class ArrayEncoder extends AsciiValueEncoder
    {
        int _ptr;
        final int _end;
        
        protected ArrayEncoder(final int ptr, final int end) {
            this._ptr = ptr;
            this._end = end;
        }
        
        @Override
        public final boolean isCompleted() {
            return this._ptr >= this._end;
        }
        
        @Override
        public abstract int encodeMore(final char[] p0, final int p1, final int p2);
    }
    
    static final class IntArrayEncoder extends ArrayEncoder
    {
        final int[] _values;
        
        protected IntArrayEncoder(final int[] values, final int n, final int n2) {
            super(n, n2);
            this._values = values;
        }
        
        @Override
        public int encodeMore(final char[] array, int writeInt, final int n) {
            while (writeInt <= n - 12 && this._ptr < this._end) {
                array[writeInt++] = ' ';
                writeInt = NumberUtil.writeInt(this._values[this._ptr++], array, writeInt);
            }
            return writeInt;
        }
        
        @Override
        public int encodeMore(final byte[] array, int writeInt, final int n) {
            while (writeInt <= n - 12 && this._ptr < this._end) {
                array[writeInt++] = 32;
                writeInt = NumberUtil.writeInt(this._values[this._ptr++], array, writeInt);
            }
            return writeInt;
        }
    }
    
    static final class LongArrayEncoder extends ArrayEncoder
    {
        final long[] _values;
        
        protected LongArrayEncoder(final long[] values, final int n, final int n2) {
            super(n, n2);
            this._values = values;
        }
        
        @Override
        public int encodeMore(final char[] array, int writeLong, final int n) {
            while (writeLong <= n - 22 && this._ptr < this._end) {
                array[writeLong++] = ' ';
                writeLong = NumberUtil.writeLong(this._values[this._ptr++], array, writeLong);
            }
            return writeLong;
        }
        
        @Override
        public int encodeMore(final byte[] array, int writeLong, final int n) {
            while (writeLong <= n - 22 && this._ptr < this._end) {
                array[writeLong++] = 32;
                writeLong = NumberUtil.writeLong(this._values[this._ptr++], array, writeLong);
            }
            return writeLong;
        }
    }
    
    static final class FloatArrayEncoder extends ArrayEncoder
    {
        final float[] _values;
        
        protected FloatArrayEncoder(final float[] values, final int n, final int n2) {
            super(n, n2);
            this._values = values;
        }
        
        @Override
        public int encodeMore(final char[] array, int writeFloat, final int n) {
            while (writeFloat <= n - 33 && this._ptr < this._end) {
                array[writeFloat++] = ' ';
                writeFloat = NumberUtil.writeFloat(this._values[this._ptr++], array, writeFloat);
            }
            return writeFloat;
        }
        
        @Override
        public int encodeMore(final byte[] array, int writeFloat, final int n) {
            while (writeFloat <= n - 33 && this._ptr < this._end) {
                array[writeFloat++] = 32;
                writeFloat = NumberUtil.writeFloat(this._values[this._ptr++], array, writeFloat);
            }
            return writeFloat;
        }
    }
    
    static final class DoubleArrayEncoder extends ArrayEncoder
    {
        final double[] _values;
        
        protected DoubleArrayEncoder(final double[] values, final int n, final int n2) {
            super(n, n2);
            this._values = values;
        }
        
        @Override
        public int encodeMore(final char[] array, int writeDouble, final int n) {
            while (writeDouble <= n - 33 && this._ptr < this._end) {
                array[writeDouble++] = ' ';
                writeDouble = NumberUtil.writeDouble(this._values[this._ptr++], array, writeDouble);
            }
            return writeDouble;
        }
        
        @Override
        public int encodeMore(final byte[] array, int writeDouble, final int n) {
            while (writeDouble <= n - 33 && this._ptr < this._end) {
                array[writeDouble++] = 32;
                writeDouble = NumberUtil.writeDouble(this._values[this._ptr++], array, writeDouble);
            }
            return writeDouble;
        }
    }
    
    static final class Base64Encoder extends AsciiValueEncoder
    {
        static final char PAD_CHAR = '=';
        static final byte PAD_BYTE = 61;
        static final byte LF_CHAR = 10;
        static final byte LF_BYTE = 10;
        final Base64Variant _variant;
        final byte[] _input;
        int _inputPtr;
        final int _inputEnd;
        int _chunksBeforeLf;
        
        protected Base64Encoder(final Base64Variant variant, final byte[] input, final int inputPtr, final int inputEnd) {
            this._variant = variant;
            this._input = input;
            this._inputPtr = inputPtr;
            this._inputEnd = inputEnd;
            this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
        }
        
        @Override
        public boolean isCompleted() {
            return this._inputPtr >= this._inputEnd;
        }
        
        @Override
        public int encodeMore(final char[] array, int n, int n2) {
            final int n3 = this._inputEnd - 3;
            n2 -= 5;
            while (this._inputPtr <= n3) {
                if (n > n2) {
                    return n;
                }
                n = this._variant.encodeBase64Chunk((this._input[this._inputPtr++] << 8 | (this._input[this._inputPtr++] & 0xFF)) << 8 | (this._input[this._inputPtr++] & 0xFF), array, n);
                if (--this._chunksBeforeLf > 0) {
                    continue;
                }
                array[n++] = '\n';
                this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
            }
            final int n4 = this._inputEnd - this._inputPtr;
            if (n4 > 0 && n <= n2) {
                int n5 = this._input[this._inputPtr++] << 16;
                if (n4 == 2) {
                    n5 |= (this._input[this._inputPtr++] & 0xFF) << 8;
                }
                n = this._variant.encodeBase64Partial(n5, n4, array, n);
            }
            return n;
        }
        
        @Override
        public int encodeMore(final byte[] array, int n, int n2) {
            final int n3 = this._inputEnd - 3;
            n2 -= 5;
            while (this._inputPtr <= n3) {
                if (n > n2) {
                    return n;
                }
                n = this._variant.encodeBase64Chunk((this._input[this._inputPtr++] << 8 | (this._input[this._inputPtr++] & 0xFF)) << 8 | (this._input[this._inputPtr++] & 0xFF), array, n);
                if (--this._chunksBeforeLf > 0) {
                    continue;
                }
                array[n++] = 10;
                this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
            }
            final int n4 = this._inputEnd - this._inputPtr;
            if (n4 > 0 && n <= n2) {
                int n5 = this._input[this._inputPtr++] << 16;
                if (n4 == 2) {
                    n5 |= (this._input[this._inputPtr++] & 0xFF) << 8;
                }
                n = this._variant.encodeBase64Partial(n5, n4, array, n);
            }
            return n;
        }
    }
}
