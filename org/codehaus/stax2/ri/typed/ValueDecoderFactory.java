// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.typed.TypedArrayDecoder;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.stax2.typed.TypedValueDecoder;
import javax.xml.namespace.NamespaceContext;

public final class ValueDecoderFactory
{
    protected BooleanDecoder mBooleanDecoder;
    protected IntDecoder mIntDecoder;
    protected LongDecoder mLongDecoder;
    protected FloatDecoder mFloatDecoder;
    protected DoubleDecoder mDoubleDecoder;
    
    public ValueDecoderFactory() {
        this.mBooleanDecoder = null;
        this.mIntDecoder = null;
        this.mLongDecoder = null;
        this.mFloatDecoder = null;
        this.mDoubleDecoder = null;
    }
    
    public BooleanDecoder getBooleanDecoder() {
        if (this.mBooleanDecoder == null) {
            this.mBooleanDecoder = new BooleanDecoder();
        }
        return this.mBooleanDecoder;
    }
    
    public IntDecoder getIntDecoder() {
        if (this.mIntDecoder == null) {
            this.mIntDecoder = new IntDecoder();
        }
        return this.mIntDecoder;
    }
    
    public LongDecoder getLongDecoder() {
        if (this.mLongDecoder == null) {
            this.mLongDecoder = new LongDecoder();
        }
        return this.mLongDecoder;
    }
    
    public FloatDecoder getFloatDecoder() {
        if (this.mFloatDecoder == null) {
            this.mFloatDecoder = new FloatDecoder();
        }
        return this.mFloatDecoder;
    }
    
    public DoubleDecoder getDoubleDecoder() {
        if (this.mDoubleDecoder == null) {
            this.mDoubleDecoder = new DoubleDecoder();
        }
        return this.mDoubleDecoder;
    }
    
    public IntegerDecoder getIntegerDecoder() {
        return new IntegerDecoder();
    }
    
    public DecimalDecoder getDecimalDecoder() {
        return new DecimalDecoder();
    }
    
    public QNameDecoder getQNameDecoder(final NamespaceContext namespaceContext) {
        return new QNameDecoder(namespaceContext);
    }
    
    public IntArrayDecoder getIntArrayDecoder(final int[] array, final int n, final int n2) {
        return new IntArrayDecoder(array, n, n2, this.getIntDecoder());
    }
    
    public IntArrayDecoder getIntArrayDecoder() {
        return new IntArrayDecoder(this.getIntDecoder());
    }
    
    public LongArrayDecoder getLongArrayDecoder(final long[] array, final int n, final int n2) {
        return new LongArrayDecoder(array, n, n2, this.getLongDecoder());
    }
    
    public LongArrayDecoder getLongArrayDecoder() {
        return new LongArrayDecoder(this.getLongDecoder());
    }
    
    public FloatArrayDecoder getFloatArrayDecoder(final float[] array, final int n, final int n2) {
        return new FloatArrayDecoder(array, n, n2, this.getFloatDecoder());
    }
    
    public FloatArrayDecoder getFloatArrayDecoder() {
        return new FloatArrayDecoder(this.getFloatDecoder());
    }
    
    public DoubleArrayDecoder getDoubleArrayDecoder(final double[] array, final int n, final int n2) {
        return new DoubleArrayDecoder(array, n, n2, this.getDoubleDecoder());
    }
    
    public DoubleArrayDecoder getDoubleArrayDecoder() {
        return new DoubleArrayDecoder(this.getDoubleDecoder());
    }
    
    public abstract static class DecoderBase extends TypedValueDecoder
    {
        static final long L_BILLION = 1000000000L;
        static final long L_MAX_INT = 2147483647L;
        static final long L_MIN_INT = -2147483648L;
        static final BigInteger BD_MIN_LONG;
        static final BigInteger BD_MAX_LONG;
        protected int mNextPtr;
        
        protected DecoderBase() {
        }
        
        public abstract String getType();
        
        @Override
        public void handleEmptyValue() {
            throw new IllegalArgumentException("Empty value (all white space) not a valid lexical representation of " + this.getType());
        }
        
        protected void verifyDigits(final String s, int i, final int n) {
            while (i < n) {
                final char char1 = s.charAt(i);
                if (char1 > '9' || char1 < '0') {
                    throw this.constructInvalidValue(s);
                }
                ++i;
            }
        }
        
        protected void verifyDigits(final char[] array, final int n, final int n2, int i) {
            while (i < n2) {
                final char c = array[i];
                if (c > '9' || c < '0') {
                    throw this.constructInvalidValue(array, n, n2);
                }
                ++i;
            }
        }
        
        protected int skipSignAndZeroes(final String s, char char1, final boolean b, final int n) {
            int n2;
            if (b) {
                n2 = 1;
                if (n2 >= n) {
                    throw this.constructInvalidValue(s);
                }
                char1 = s.charAt(n2++);
            }
            else {
                n2 = 1;
            }
            int n3 = char1 - '0';
            if (n3 < 0 || n3 > 9) {
                throw this.constructInvalidValue(s);
            }
            while (n3 == 0 && n2 < n) {
                final int n4 = s.charAt(n2) - '0';
                if (n4 < 0) {
                    break;
                }
                if (n4 > 9) {
                    break;
                }
                ++n2;
                n3 = n4;
            }
            this.mNextPtr = n2;
            return n3;
        }
        
        protected int skipSignAndZeroes(final char[] array, char c, final boolean b, final int n, final int n2) {
            int mNextPtr = n + 1;
            if (b) {
                if (mNextPtr >= n2) {
                    throw this.constructInvalidValue(array, n, n2);
                }
                c = array[mNextPtr++];
            }
            int n3 = c - '0';
            if (n3 < 0 || n3 > 9) {
                throw this.constructInvalidValue(array, n, n2);
            }
            while (n3 == 0 && mNextPtr < n2) {
                final int n4 = array[mNextPtr] - '0';
                if (n4 < 0) {
                    break;
                }
                if (n4 > 9) {
                    break;
                }
                ++mNextPtr;
                n3 = n4;
            }
            this.mNextPtr = mNextPtr;
            return n3;
        }
        
        protected static final int parseInt(final char[] array, int n, final int n2) {
            int n3 = array[n] - '0';
            if (++n < n2) {
                n3 = n3 * 10 + (array[n] - '0');
                if (++n < n2) {
                    n3 = n3 * 10 + (array[n] - '0');
                    if (++n < n2) {
                        n3 = n3 * 10 + (array[n] - '0');
                        if (++n < n2) {
                            n3 = n3 * 10 + (array[n] - '0');
                            if (++n < n2) {
                                n3 = n3 * 10 + (array[n] - '0');
                                if (++n < n2) {
                                    n3 = n3 * 10 + (array[n] - '0');
                                    if (++n < n2) {
                                        n3 = n3 * 10 + (array[n] - '0');
                                        if (++n < n2) {
                                            n3 = n3 * 10 + (array[n] - '0');
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return n3;
        }
        
        protected static final int parseInt(int n, final char[] array, int n2, final int n3) {
            n = n * 10 + (array[n2] - '0');
            if (++n2 < n3) {
                n = n * 10 + (array[n2] - '0');
                if (++n2 < n3) {
                    n = n * 10 + (array[n2] - '0');
                    if (++n2 < n3) {
                        n = n * 10 + (array[n2] - '0');
                        if (++n2 < n3) {
                            n = n * 10 + (array[n2] - '0');
                            if (++n2 < n3) {
                                n = n * 10 + (array[n2] - '0');
                                if (++n2 < n3) {
                                    n = n * 10 + (array[n2] - '0');
                                    if (++n2 < n3) {
                                        n = n * 10 + (array[n2] - '0');
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return n;
        }
        
        protected static final int parseInt(final String s, int index, final int n) {
            int n2 = s.charAt(index) - '0';
            if (++index < n) {
                n2 = n2 * 10 + (s.charAt(index) - '0');
                if (++index < n) {
                    n2 = n2 * 10 + (s.charAt(index) - '0');
                    if (++index < n) {
                        n2 = n2 * 10 + (s.charAt(index) - '0');
                        if (++index < n) {
                            n2 = n2 * 10 + (s.charAt(index) - '0');
                            if (++index < n) {
                                n2 = n2 * 10 + (s.charAt(index) - '0');
                                if (++index < n) {
                                    n2 = n2 * 10 + (s.charAt(index) - '0');
                                    if (++index < n) {
                                        n2 = n2 * 10 + (s.charAt(index) - '0');
                                        if (++index < n) {
                                            n2 = n2 * 10 + (s.charAt(index) - '0');
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return n2;
        }
        
        protected static final int parseInt(int n, final String s, int n2, final int n3) {
            n = n * 10 + (s.charAt(n2) - '0');
            if (++n2 < n3) {
                n = n * 10 + (s.charAt(n2) - '0');
                if (++n2 < n3) {
                    n = n * 10 + (s.charAt(n2) - '0');
                    if (++n2 < n3) {
                        n = n * 10 + (s.charAt(n2) - '0');
                        if (++n2 < n3) {
                            n = n * 10 + (s.charAt(n2) - '0');
                            if (++n2 < n3) {
                                n = n * 10 + (s.charAt(n2) - '0');
                                if (++n2 < n3) {
                                    n = n * 10 + (s.charAt(n2) - '0');
                                    if (++n2 < n3) {
                                        n = n * 10 + (s.charAt(n2) - '0');
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return n;
        }
        
        protected static final long parseLong(final char[] array, final int n, final int n2) {
            final int n3 = n2 - 9;
            return parseInt(array, n, n3) * 1000000000L + parseInt(array, n3, n2);
        }
        
        protected static final long parseLong(final String s, final int n, final int n2) {
            final int n3 = n2 - 9;
            return parseInt(s, n, n3) * 1000000000L + parseInt(s, n3, n2);
        }
        
        protected IllegalArgumentException constructInvalidValue(final String str) {
            return new IllegalArgumentException("Value \"" + str + "\" not a valid lexical representation of " + this.getType());
        }
        
        protected IllegalArgumentException constructInvalidValue(final char[] array, final int n, final int n2) {
            return new IllegalArgumentException("Value \"" + this.lexicalDesc(array, n, n2) + "\" not a valid lexical representation of " + this.getType());
        }
        
        protected String lexicalDesc(final char[] value, final int offset, final int n) {
            return this._clean(new String(value, offset, n - offset));
        }
        
        protected String lexicalDesc(final String s) {
            return this._clean(s);
        }
        
        protected String _clean(final String s) {
            return s.trim();
        }
        
        static {
            BD_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
            BD_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
        }
    }
    
    public static final class BooleanDecoder extends DecoderBase
    {
        protected boolean mValue;
        
        @Override
        public String getType() {
            return "boolean";
        }
        
        public boolean getValue() {
            return this.mValue;
        }
        
        @Override
        public void decode(final String s) throws IllegalArgumentException {
            final int length = s.length();
            final char char1 = s.charAt(0);
            if (char1 == 't') {
                if (length == 4 && s.charAt(1) == 'r' && s.charAt(2) == 'u' && s.charAt(3) == 'e') {
                    this.mValue = true;
                    return;
                }
            }
            else if (char1 == 'f') {
                if (length == 5 && s.charAt(1) == 'a' && s.charAt(2) == 'l' && s.charAt(3) == 's' && s.charAt(4) == 'e') {
                    this.mValue = false;
                    return;
                }
            }
            else if (char1 == '0') {
                if (length == 1) {
                    this.mValue = false;
                    return;
                }
            }
            else if (char1 == '1' && length == 1) {
                this.mValue = true;
                return;
            }
            throw this.constructInvalidValue(s);
        }
        
        @Override
        public void decode(final char[] array, final int n, final int n2) throws IllegalArgumentException {
            final int n3 = n2 - n;
            final char c = array[n];
            if (c == 't') {
                if (n3 == 4 && array[n + 1] == 'r' && array[n + 2] == 'u' && array[n + 3] == 'e') {
                    this.mValue = true;
                    return;
                }
            }
            else if (c == 'f') {
                if (n3 == 5 && array[n + 1] == 'a' && array[n + 2] == 'l' && array[n + 3] == 's' && array[n + 4] == 'e') {
                    this.mValue = false;
                    return;
                }
            }
            else if (c == '0') {
                if (n3 == 1) {
                    this.mValue = false;
                    return;
                }
            }
            else if (c == '1' && n3 == 1) {
                this.mValue = true;
                return;
            }
            throw this.constructInvalidValue(array, n, n2);
        }
    }
    
    public static final class IntDecoder extends DecoderBase
    {
        protected int mValue;
        
        @Override
        public String getType() {
            return "int";
        }
        
        public int getValue() {
            return this.mValue;
        }
        
        @Override
        public void decode(final String s) throws IllegalArgumentException {
            final int length = s.length();
            final char char1 = s.charAt(0);
            final boolean b = char1 == '-';
            int n;
            if (b || char1 == '+') {
                n = this.skipSignAndZeroes(s, char1, true, length);
            }
            else {
                n = this.skipSignAndZeroes(s, char1, false, length);
            }
            final int mNextPtr = this.mNextPtr;
            final int n2 = length - mNextPtr;
            if (n2 == 0) {
                this.mValue = (b ? (-n) : n);
                return;
            }
            this.verifyDigits(s, mNextPtr, length);
            if (n2 <= 8) {
                final int int1 = DecoderBase.parseInt(n, s, mNextPtr, mNextPtr + n2);
                this.mValue = (b ? (-int1) : int1);
                return;
            }
            if (n2 == 9 && n < 3) {
                long n3 = 1000000000L;
                if (n == 2) {
                    n3 += 1000000000L;
                }
                final long n4 = n3 + DecoderBase.parseInt(s, mNextPtr, mNextPtr + n2);
                if (b) {
                    final long n5 = -n4;
                    if (n5 >= -2147483648L) {
                        this.mValue = (int)n5;
                        return;
                    }
                }
                else if (n4 <= 2147483647L) {
                    this.mValue = (int)n4;
                    return;
                }
            }
            throw new IllegalArgumentException("value \"" + this.lexicalDesc(s) + "\" not a valid 32-bit integer: overflow.");
        }
        
        @Override
        public void decode(final char[] array, final int n, final int n2) throws IllegalArgumentException {
            final char c = array[n];
            final boolean b = c == '-';
            int n3;
            if (b || c == '+') {
                n3 = this.skipSignAndZeroes(array, c, true, n, n2);
            }
            else {
                n3 = this.skipSignAndZeroes(array, c, false, n, n2);
            }
            final int mNextPtr = this.mNextPtr;
            final int n4 = n2 - mNextPtr;
            if (n4 == 0) {
                this.mValue = (b ? (-n3) : n3);
                return;
            }
            this.verifyDigits(array, n, n2, mNextPtr);
            if (n4 <= 8) {
                final int int1 = DecoderBase.parseInt(n3, array, mNextPtr, mNextPtr + n4);
                this.mValue = (b ? (-int1) : int1);
                return;
            }
            if (n4 == 9 && n3 < 3) {
                long n5 = 1000000000L;
                if (n3 == 2) {
                    n5 += 1000000000L;
                }
                final long n6 = n5 + DecoderBase.parseInt(array, mNextPtr, mNextPtr + n4);
                if (b) {
                    final long n7 = -n6;
                    if (n7 >= -2147483648L) {
                        this.mValue = (int)n7;
                        return;
                    }
                }
                else if (n6 <= 2147483647L) {
                    this.mValue = (int)n6;
                    return;
                }
            }
            throw new IllegalArgumentException("value \"" + this.lexicalDesc(array, n, n2) + "\" not a valid 32-bit integer: overflow.");
        }
    }
    
    public static final class LongDecoder extends DecoderBase
    {
        protected long mValue;
        
        @Override
        public String getType() {
            return "long";
        }
        
        public long getValue() {
            return this.mValue;
        }
        
        @Override
        public void decode(final String s) throws IllegalArgumentException {
            final int length = s.length();
            final char char1 = s.charAt(0);
            final boolean b = char1 == '-';
            int n;
            if (b || char1 == '+') {
                n = this.skipSignAndZeroes(s, char1, true, length);
            }
            else {
                n = this.skipSignAndZeroes(s, char1, false, length);
            }
            int mNextPtr = this.mNextPtr;
            int n2 = length - mNextPtr;
            if (n2 == 0) {
                this.mValue = (b ? (-n) : n);
                return;
            }
            this.verifyDigits(s, mNextPtr, length);
            if (n2 <= 8) {
                final int int1 = DecoderBase.parseInt(n, s, mNextPtr, mNextPtr + n2);
                this.mValue = (b ? (-int1) : int1);
                return;
            }
            --mNextPtr;
            if (++n2 <= 18) {
                final long long1 = DecoderBase.parseLong(s, mNextPtr, mNextPtr + n2);
                this.mValue = (b ? (-long1) : long1);
                return;
            }
            this.mValue = this.parseUsingBD(s.substring(mNextPtr, mNextPtr + n2), b);
        }
        
        @Override
        public void decode(final char[] value, final int n, final int n2) throws IllegalArgumentException {
            final char c = value[n];
            final boolean b = c == '-';
            int n3;
            if (b || c == '+') {
                n3 = this.skipSignAndZeroes(value, c, true, n, n2);
            }
            else {
                n3 = this.skipSignAndZeroes(value, c, false, n, n2);
            }
            int mNextPtr = this.mNextPtr;
            int count = n2 - mNextPtr;
            if (count == 0) {
                this.mValue = (b ? (-n3) : n3);
                return;
            }
            this.verifyDigits(value, n, n2, mNextPtr);
            if (count <= 8) {
                final int int1 = DecoderBase.parseInt(n3, value, mNextPtr, mNextPtr + count);
                this.mValue = (b ? (-int1) : ((long)int1));
                return;
            }
            --mNextPtr;
            if (++count <= 18) {
                final long long1 = DecoderBase.parseLong(value, mNextPtr, mNextPtr + count);
                this.mValue = (b ? (-long1) : long1);
                return;
            }
            this.mValue = this.parseUsingBD(new String(value, mNextPtr, count), b);
        }
        
        private long parseUsingBD(final String val, final boolean b) {
            final BigInteger bigInteger = new BigInteger(val);
            if (b) {
                final BigInteger negate = bigInteger.negate();
                if (negate.compareTo(LongDecoder.BD_MIN_LONG) >= 0) {
                    return negate.longValue();
                }
            }
            else if (bigInteger.compareTo(LongDecoder.BD_MAX_LONG) <= 0) {
                return bigInteger.longValue();
            }
            throw new IllegalArgumentException("value \"" + this.lexicalDesc(val) + "\" not a valid long: overflow.");
        }
    }
    
    public static final class FloatDecoder extends DecoderBase
    {
        protected float mValue;
        
        @Override
        public String getType() {
            return "float";
        }
        
        public float getValue() {
            return this.mValue;
        }
        
        @Override
        public void decode(final String s) throws IllegalArgumentException {
            final int length = s.length();
            if (length == 3) {
                final char char1 = s.charAt(0);
                if (char1 == 'I') {
                    if (s.charAt(1) == 'N' && s.charAt(2) == 'F') {
                        this.mValue = Float.POSITIVE_INFINITY;
                        return;
                    }
                }
                else if (char1 == 'N' && s.charAt(1) == 'a' && s.charAt(2) == 'N') {
                    this.mValue = Float.NaN;
                    return;
                }
            }
            else if (length == 4 && s.charAt(0) == '-' && s.charAt(1) == 'I' && s.charAt(2) == 'N' && s.charAt(3) == 'F') {
                this.mValue = Float.NEGATIVE_INFINITY;
                return;
            }
            try {
                this.mValue = Float.parseFloat(s);
            }
            catch (NumberFormatException ex) {
                throw this.constructInvalidValue(s);
            }
        }
        
        @Override
        public void decode(final char[] value, final int offset, final int n) throws IllegalArgumentException {
            final int count = n - offset;
            if (count == 3) {
                final char c = value[offset];
                if (c == 'I') {
                    if (value[offset + 1] == 'N' && value[offset + 2] == 'F') {
                        this.mValue = Float.POSITIVE_INFINITY;
                        return;
                    }
                }
                else if (c == 'N' && value[offset + 1] == 'a' && value[offset + 2] == 'N') {
                    this.mValue = Float.NaN;
                    return;
                }
            }
            else if (count == 4 && value[offset] == '-' && value[offset + 1] == 'I' && value[offset + 2] == 'N' && value[offset + 3] == 'F') {
                this.mValue = Float.NEGATIVE_INFINITY;
                return;
            }
            final String s = new String(value, offset, count);
            try {
                this.mValue = Float.parseFloat(s);
            }
            catch (NumberFormatException ex) {
                throw this.constructInvalidValue(s);
            }
        }
    }
    
    public static final class DoubleDecoder extends DecoderBase
    {
        protected double mValue;
        
        @Override
        public String getType() {
            return "double";
        }
        
        public double getValue() {
            return this.mValue;
        }
        
        @Override
        public void decode(final String s) throws IllegalArgumentException {
            final int length = s.length();
            if (length == 3) {
                final char char1 = s.charAt(0);
                if (char1 == 'I') {
                    if (s.charAt(1) == 'N' && s.charAt(2) == 'F') {
                        this.mValue = Double.POSITIVE_INFINITY;
                        return;
                    }
                }
                else if (char1 == 'N' && s.charAt(1) == 'a' && s.charAt(2) == 'N') {
                    this.mValue = Double.NaN;
                    return;
                }
            }
            else if (length == 4 && s.charAt(0) == '-' && s.charAt(1) == 'I' && s.charAt(2) == 'N' && s.charAt(3) == 'F') {
                this.mValue = Double.NEGATIVE_INFINITY;
                return;
            }
            try {
                this.mValue = Double.parseDouble(s);
            }
            catch (NumberFormatException ex) {
                throw this.constructInvalidValue(s);
            }
        }
        
        @Override
        public void decode(final char[] value, final int offset, final int n) throws IllegalArgumentException {
            final int count = n - offset;
            if (count == 3) {
                final char c = value[offset];
                if (c == 'I') {
                    if (value[offset + 1] == 'N' && value[offset + 2] == 'F') {
                        this.mValue = Double.POSITIVE_INFINITY;
                        return;
                    }
                }
                else if (c == 'N' && value[offset + 1] == 'a' && value[offset + 2] == 'N') {
                    this.mValue = Double.NaN;
                    return;
                }
            }
            else if (count == 4 && value[offset] == '-' && value[offset + 1] == 'I' && value[offset + 2] == 'N' && value[offset + 3] == 'F') {
                this.mValue = Double.NEGATIVE_INFINITY;
                return;
            }
            final String s = new String(value, offset, count);
            try {
                this.mValue = Double.parseDouble(s);
            }
            catch (NumberFormatException ex) {
                throw this.constructInvalidValue(s);
            }
        }
    }
    
    public static final class IntegerDecoder extends DecoderBase
    {
        protected BigInteger mValue;
        
        @Override
        public String getType() {
            return "integer";
        }
        
        public BigInteger getValue() {
            return this.mValue;
        }
        
        @Override
        public void decode(final String val) throws IllegalArgumentException {
            try {
                this.mValue = new BigInteger(val);
            }
            catch (NumberFormatException ex) {
                throw this.constructInvalidValue(val);
            }
        }
        
        @Override
        public void decode(final char[] value, final int offset, final int n) throws IllegalArgumentException {
            final String val = new String(value, offset, n - offset);
            try {
                this.mValue = new BigInteger(val);
            }
            catch (NumberFormatException ex) {
                throw this.constructInvalidValue(val);
            }
        }
    }
    
    public static final class DecimalDecoder extends DecoderBase
    {
        protected BigDecimal mValue;
        
        @Override
        public String getType() {
            return "decimal";
        }
        
        public BigDecimal getValue() {
            return this.mValue;
        }
        
        @Override
        public void decode(final String val) throws IllegalArgumentException {
            try {
                this.mValue = new BigDecimal(val);
            }
            catch (NumberFormatException ex) {
                throw this.constructInvalidValue(val);
            }
        }
        
        @Override
        public void decode(final char[] array, final int n, final int n2) throws IllegalArgumentException {
            final int n3 = n2 - n;
            try {
                this.mValue = new BigDecimal(new String(array, n, n3));
            }
            catch (NumberFormatException ex) {
                throw this.constructInvalidValue(new String(array, n, n3));
            }
        }
    }
    
    public static final class QNameDecoder extends DecoderBase
    {
        final NamespaceContext mNsCtxt;
        protected QName mValue;
        
        public QNameDecoder(final NamespaceContext mNsCtxt) {
            this.mNsCtxt = mNsCtxt;
        }
        
        @Override
        public String getType() {
            return "QName";
        }
        
        public QName getValue() {
            return this.mValue;
        }
        
        @Override
        public void decode(final String s) throws IllegalArgumentException {
            final int index = s.indexOf(58);
            if (index >= 0) {
                this.mValue = this.resolveQName(s.substring(0, index), s.substring(index + 1));
            }
            else {
                this.mValue = this.resolveQName(s);
            }
        }
        
        @Override
        public void decode(final char[] value, final int n, final int n2) throws IllegalArgumentException {
            for (int i = n; i < n2; ++i) {
                if (value[i] == ':') {
                    this.mValue = this.resolveQName(new String(value, n, i - n), new String(value, i + 1, n2 - i - 1));
                    return;
                }
            }
            this.mValue = this.resolveQName(new String(value, n, n2 - n));
        }
        
        protected QName resolveQName(final String localPart) throws IllegalArgumentException {
            String namespaceURI = this.mNsCtxt.getNamespaceURI("");
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            return new QName(namespaceURI, localPart);
        }
        
        protected QName resolveQName(final String s, final String localPart) throws IllegalArgumentException {
            if (s.length() == 0 || localPart.length() == 0) {
                throw this.constructInvalidValue(s + ":" + localPart);
            }
            final String namespaceURI = this.mNsCtxt.getNamespaceURI(s);
            if (namespaceURI == null || namespaceURI.length() == 0) {
                throw new IllegalArgumentException("Value \"" + this.lexicalDesc(s + ":" + localPart) + "\" not a valid QName: prefix '" + s + "' not bound to a namespace");
            }
            return new QName(namespaceURI, localPart, s);
        }
    }
    
    public abstract static class BaseArrayDecoder extends TypedArrayDecoder
    {
        protected static final int INITIAL_RESULT_BUFFER_SIZE = 40;
        protected static final int SMALL_RESULT_BUFFER_SIZE = 4000;
        protected int mStart;
        protected int mEnd;
        protected int mCount;
        
        protected BaseArrayDecoder(final int mStart, final int mEnd) {
            this.mCount = 0;
            this.mStart = mStart;
            if (mEnd < 1) {
                throw new IllegalArgumentException("Number of elements to read can not be less than 1");
            }
            this.mEnd = mEnd;
        }
        
        @Override
        public final int getCount() {
            return this.mCount;
        }
        
        @Override
        public final boolean hasRoom() {
            return this.mCount < this.mEnd;
        }
        
        public abstract void expand();
        
        protected int calcNewSize(final int n) {
            if (n < 4000) {
                return n << 2;
            }
            return n + n;
        }
    }
    
    public static final class IntArrayDecoder extends BaseArrayDecoder
    {
        int[] mResult;
        final IntDecoder mDecoder;
        
        public IntArrayDecoder(final int[] mResult, final int n, final int n2, final IntDecoder mDecoder) {
            super(n, n2);
            this.mResult = mResult;
            this.mDecoder = mDecoder;
        }
        
        public IntArrayDecoder(final IntDecoder mDecoder) {
            super(0, 40);
            this.mResult = new int[40];
            this.mDecoder = mDecoder;
        }
        
        @Override
        public void expand() {
            final int[] mResult = this.mResult;
            final int length = mResult.length;
            final int calcNewSize = this.calcNewSize(length);
            this.mResult = new int[calcNewSize];
            System.arraycopy(mResult, this.mStart, this.mResult, 0, length);
            this.mStart = 0;
            this.mEnd = calcNewSize;
        }
        
        public int[] getValues() {
            final int[] array = new int[this.mCount];
            System.arraycopy(this.mResult, this.mStart, array, 0, this.mCount);
            return array;
        }
        
        @Override
        public boolean decodeValue(final String s) throws IllegalArgumentException {
            this.mDecoder.decode(s);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
        
        @Override
        public boolean decodeValue(final char[] array, final int n, final int n2) throws IllegalArgumentException {
            this.mDecoder.decode(array, n, n2);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
    }
    
    public static final class LongArrayDecoder extends BaseArrayDecoder
    {
        long[] mResult;
        final LongDecoder mDecoder;
        
        public LongArrayDecoder(final long[] mResult, final int n, final int n2, final LongDecoder mDecoder) {
            super(n, n2);
            this.mResult = mResult;
            this.mDecoder = mDecoder;
        }
        
        public LongArrayDecoder(final LongDecoder mDecoder) {
            super(0, 40);
            this.mResult = new long[40];
            this.mDecoder = mDecoder;
        }
        
        @Override
        public void expand() {
            final long[] mResult = this.mResult;
            final int length = mResult.length;
            final int calcNewSize = this.calcNewSize(length);
            this.mResult = new long[calcNewSize];
            System.arraycopy(mResult, this.mStart, this.mResult, 0, length);
            this.mStart = 0;
            this.mEnd = calcNewSize;
        }
        
        public long[] getValues() {
            final long[] array = new long[this.mCount];
            System.arraycopy(this.mResult, this.mStart, array, 0, this.mCount);
            return array;
        }
        
        @Override
        public boolean decodeValue(final String s) throws IllegalArgumentException {
            this.mDecoder.decode(s);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
        
        @Override
        public boolean decodeValue(final char[] array, final int n, final int n2) throws IllegalArgumentException {
            this.mDecoder.decode(array, n, n2);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
    }
    
    public static final class FloatArrayDecoder extends BaseArrayDecoder
    {
        float[] mResult;
        final FloatDecoder mDecoder;
        
        public FloatArrayDecoder(final float[] mResult, final int n, final int n2, final FloatDecoder mDecoder) {
            super(n, n2);
            this.mResult = mResult;
            this.mDecoder = mDecoder;
        }
        
        public FloatArrayDecoder(final FloatDecoder mDecoder) {
            super(0, 40);
            this.mResult = new float[40];
            this.mDecoder = mDecoder;
        }
        
        @Override
        public void expand() {
            final float[] mResult = this.mResult;
            final int length = mResult.length;
            final int calcNewSize = this.calcNewSize(length);
            this.mResult = new float[calcNewSize];
            System.arraycopy(mResult, this.mStart, this.mResult, 0, length);
            this.mStart = 0;
            this.mEnd = calcNewSize;
        }
        
        public float[] getValues() {
            final float[] array = new float[this.mCount];
            System.arraycopy(this.mResult, this.mStart, array, 0, this.mCount);
            return array;
        }
        
        @Override
        public boolean decodeValue(final String s) throws IllegalArgumentException {
            this.mDecoder.decode(s);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
        
        @Override
        public boolean decodeValue(final char[] array, final int n, final int n2) throws IllegalArgumentException {
            this.mDecoder.decode(array, n, n2);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
    }
    
    public static final class DoubleArrayDecoder extends BaseArrayDecoder
    {
        double[] mResult;
        final DoubleDecoder mDecoder;
        
        public DoubleArrayDecoder(final double[] mResult, final int n, final int n2, final DoubleDecoder mDecoder) {
            super(n, n2);
            this.mResult = mResult;
            this.mDecoder = mDecoder;
        }
        
        public DoubleArrayDecoder(final DoubleDecoder mDecoder) {
            super(0, 40);
            this.mResult = new double[40];
            this.mDecoder = mDecoder;
        }
        
        @Override
        public void expand() {
            final double[] mResult = this.mResult;
            final int length = mResult.length;
            final int calcNewSize = this.calcNewSize(length);
            this.mResult = new double[calcNewSize];
            System.arraycopy(mResult, this.mStart, this.mResult, 0, length);
            this.mStart = 0;
            this.mEnd = calcNewSize;
        }
        
        public double[] getValues() {
            final double[] array = new double[this.mCount];
            System.arraycopy(this.mResult, this.mStart, array, 0, this.mCount);
            return array;
        }
        
        @Override
        public boolean decodeValue(final String s) throws IllegalArgumentException {
            this.mDecoder.decode(s);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
        
        @Override
        public boolean decodeValue(final char[] array, final int n, final int n2) throws IllegalArgumentException {
            this.mDecoder.decode(array, n, n2);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            return ++this.mCount >= this.mEnd;
        }
    }
}
