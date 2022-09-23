// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.math.BigDecimal;
import org.apache.derby.iapi.error.StandardException;
import java.math.BigInteger;

public final class BigIntegerDecimal extends BinaryDecimal
{
    private static final BigInteger TEN;
    private static final BigInteger MAXLONG_PLUS_ONE;
    private static final BigInteger MINLONG_MINUS_ONE;
    
    public DataValueDescriptor getNewNull() {
        return new BigIntegerDecimal();
    }
    
    public long getLong() throws StandardException {
        if (this.isNull()) {
            return 0L;
        }
        BigInteger divide = new BigInteger(this.data2c);
        int n = 0;
        if (divide.compareTo(BigIntegerDecimal.MAXLONG_PLUS_ONE) < 0 && divide.compareTo(BigIntegerDecimal.MINLONG_MINUS_ONE) > 0) {
            n = 1;
        }
        for (int i = 0; i < this.sqlScale; ++i) {
            divide = divide.divide(BigIntegerDecimal.TEN);
            if (n == 0) {
                if (divide.compareTo(BigIntegerDecimal.MAXLONG_PLUS_ONE) < 0 && divide.compareTo(BigIntegerDecimal.MINLONG_MINUS_ONE) > 0) {
                    n = 1;
                }
            }
        }
        if (n == 0) {
            throw StandardException.newException("22003", "BIGINT");
        }
        return divide.longValue();
    }
    
    public float getFloat() throws StandardException {
        if (this.isNull()) {
            return 0.0f;
        }
        return NumberDataType.normalizeREAL(Float.parseFloat(this.getString()));
    }
    
    public double getDouble() throws StandardException {
        if (this.isNull()) {
            return 0.0;
        }
        return NumberDataType.normalizeDOUBLE(Double.parseDouble(this.getString()));
    }
    
    public boolean getBoolean() {
        return !this.isNull() && new BigInteger(this.data2c).compareTo(BigInteger.ZERO) != 0;
    }
    
    public void setValue(String val) throws StandardException {
        if (val == null) {
            this.restoreToNull();
            return;
        }
        val = val.trim();
        final int index = val.indexOf(46);
        int endIndex = val.indexOf(101);
        if (endIndex == -1) {
            endIndex = val.indexOf(69);
        }
        int sqlScale = 0;
        try {
            if (endIndex != -1) {
                if (index > endIndex) {
                    throw this.invalidFormat();
                }
                int beginIndex = endIndex + 1;
                if (beginIndex >= val.length()) {
                    throw this.invalidFormat();
                }
                if (val.charAt(beginIndex) == '+') {
                    if (++beginIndex >= val.length()) {
                        throw this.invalidFormat();
                    }
                    if (val.charAt(beginIndex) == '-') {
                        throw this.invalidFormat();
                    }
                }
                sqlScale = -1 * Integer.parseInt(val.substring(beginIndex));
                val = val.substring(0, endIndex);
            }
            if (index != -1) {
                final String substring = val.substring(0, index);
                sqlScale += val.length() - (index + 1);
                val = substring.concat(val.substring(index + 1, val.length()));
            }
            if (sqlScale < 0) {
                for (int i = sqlScale; i < 0; ++i) {
                    val = val.concat("0");
                }
                sqlScale = 0;
            }
            this.data2c = new BigInteger(val).toByteArray();
            this.sqlScale = sqlScale;
        }
        catch (NumberFormatException ex) {
            throw this.invalidFormat();
        }
    }
    
    public Object getObject() throws StandardException {
        if (this.isNull()) {
            return null;
        }
        return new BigDecimal(this.getString());
    }
    
    public String getString() {
        if (this.isNull()) {
            return null;
        }
        String str = new BigInteger(this.data2c).toString();
        if (this.sqlScale == 0) {
            return str;
        }
        final int negative = this.isNegative() ? 1 : 0;
        if (this.sqlScale >= str.length() - negative) {
            if (negative != 0) {
                str = str.substring(1);
            }
            String concat = (negative != 0) ? "-0." : "0.";
            for (int i = this.sqlScale - str.length(); i > 0; --i) {
                concat = concat.concat("0");
            }
            return concat.concat(str);
        }
        return str.substring(0, str.length() - this.sqlScale).concat(".").concat(str.substring(str.length() - this.sqlScale, str.length()));
    }
    
    public int getDecimalValuePrecision() {
        if (this.isNull()) {
            return 0;
        }
        final BigInteger x = new BigInteger(this.data2c);
        if (BigInteger.ZERO.equals(x)) {
            return 0;
        }
        int length = x.toString().length();
        if (this.isNegative()) {
            --length;
        }
        if (length < this.sqlScale) {
            return this.sqlScale;
        }
        return length;
    }
    
    protected int typeCompare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final BigIntegerDecimal bid = this.getBID(dataValueDescriptor);
        final int decimalValueScale = this.getDecimalValueScale();
        final int decimalValueScale2 = bid.getDecimalValueScale();
        BigInteger rescale = new BigInteger(this.data2c);
        BigInteger rescale2 = new BigInteger(bid.data2c);
        if (decimalValueScale < decimalValueScale2) {
            rescale = rescale(rescale, decimalValueScale2 - decimalValueScale);
        }
        else if (decimalValueScale2 < decimalValueScale) {
            rescale2 = rescale(rescale2, decimalValueScale - decimalValueScale2);
        }
        return rescale.compareTo(rescale2);
    }
    
    public NumberDataValue plusNN(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, final NumberDataValue numberDataValue3) throws StandardException {
        BinaryDecimal binaryDecimal = (BinaryDecimal)numberDataValue3;
        if (binaryDecimal == null) {
            binaryDecimal = new BigIntegerDecimal();
        }
        final BigIntegerDecimal bid = this.getBID(numberDataValue);
        final BigIntegerDecimal bid2 = this.getBID(numberDataValue2);
        final int decimalValueScale = bid.getDecimalValueScale();
        final int decimalValueScale2 = bid2.getDecimalValueScale();
        BigInteger rescale = new BigInteger(bid.data2c);
        BigInteger rescale2 = new BigInteger(bid2.data2c);
        int sqlScale;
        if ((sqlScale = decimalValueScale) < decimalValueScale2) {
            rescale = rescale(rescale, decimalValueScale2 - decimalValueScale);
            sqlScale = decimalValueScale2;
        }
        else if (decimalValueScale2 < decimalValueScale) {
            rescale2 = rescale(rescale2, decimalValueScale - decimalValueScale2);
        }
        binaryDecimal.data2c = rescale.add(rescale2).toByteArray();
        binaryDecimal.sqlScale = sqlScale;
        return binaryDecimal;
    }
    
    public NumberDataValue minus(NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = (NumberDataValue)this.getNewNull();
        }
        if (this.isNull()) {
            numberDataValue.setToNull();
        }
        else {
            final BinaryDecimal binaryDecimal = (BinaryDecimal)numberDataValue;
            binaryDecimal.data2c = new BigInteger(this.data2c).negate().toByteArray();
            binaryDecimal.sqlScale = this.sqlScale;
        }
        return numberDataValue;
    }
    
    public NumberDataValue timesNN(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, final NumberDataValue numberDataValue3) throws StandardException {
        BigIntegerDecimal bigIntegerDecimal = (BigIntegerDecimal)numberDataValue3;
        if (bigIntegerDecimal == null) {
            bigIntegerDecimal = new BigIntegerDecimal();
        }
        final BigIntegerDecimal bid = this.getBID(numberDataValue);
        final BigIntegerDecimal bid2 = this.getBID(numberDataValue2);
        bigIntegerDecimal.data2c = new BigInteger(bid.data2c).multiply(new BigInteger(bid2.data2c)).toByteArray();
        bigIntegerDecimal.sqlScale = bid.getDecimalValueScale() + bid2.getDecimalValueScale();
        return bigIntegerDecimal;
    }
    
    public NumberDataValue divideNN(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, final NumberDataValue numberDataValue3, final int n) throws StandardException {
        BinaryDecimal binaryDecimal = (BinaryDecimal)numberDataValue3;
        if (binaryDecimal == null) {
            binaryDecimal = new BigIntegerDecimal();
        }
        final BigIntegerDecimal bid = this.getBID(numberDataValue);
        final BigIntegerDecimal bid2 = this.getBID(numberDataValue2);
        BigInteger rescale = new BigInteger(bid.data2c);
        final BigInteger bigInteger = new BigInteger(bid2.data2c);
        if (BigInteger.ZERO.equals(bigInteger)) {
            throw StandardException.newException("22012");
        }
        int decimalValueScale = bid.getDecimalValueScale();
        final int decimalValueScale2 = bid2.getDecimalValueScale();
        if (n >= 0 && decimalValueScale < n + decimalValueScale2) {
            rescale = rescale(rescale, n + decimalValueScale2 - decimalValueScale);
            decimalValueScale = n + decimalValueScale2;
        }
        BigInteger bigInteger2 = rescale.divide(bigInteger);
        binaryDecimal.sqlScale = decimalValueScale - decimalValueScale2;
        if (binaryDecimal.sqlScale < 0) {
            bigInteger2 = rescale(bigInteger2, -binaryDecimal.sqlScale);
            binaryDecimal.sqlScale = 0;
        }
        binaryDecimal.data2c = bigInteger2.toByteArray();
        return binaryDecimal;
    }
    
    public void normalize(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor from) throws StandardException {
        final int scale = dataTypeDescriptor.getScale();
        final int precision = dataTypeDescriptor.getPrecision();
        this.setFrom(from);
        this.setWidth(precision, scale, true);
    }
    
    public void setWidth(final int i, final int n, final boolean b) throws StandardException {
        if (this.isNull()) {
            return;
        }
        final int n2 = n - this.sqlScale;
        if (i != -1 && this.getDecimalValuePrecision() + n2 > i) {
            throw StandardException.newException("22003", "DECIMAL/NUMERIC(" + i + "," + n + ")");
        }
        if (n2 == 0) {
            return;
        }
        this.data2c = rescale(new BigInteger(this.data2c), n2).toByteArray();
        this.sqlScale = n;
    }
    
    private BigIntegerDecimal getBID(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        switch (dataValueDescriptor.typeToBigDecimal()) {
            case 3: {
                return (BigIntegerDecimal)dataValueDescriptor;
            }
            case 1: {
                final BigIntegerDecimal bigIntegerDecimal = new BigIntegerDecimal();
                bigIntegerDecimal.setValue(dataValueDescriptor.getString());
                return bigIntegerDecimal;
            }
            case -5: {
                final BigIntegerDecimal bigIntegerDecimal2 = new BigIntegerDecimal();
                bigIntegerDecimal2.setValue(dataValueDescriptor.getLong());
                return bigIntegerDecimal2;
            }
            default: {
                return null;
            }
        }
    }
    
    private static BigInteger rescale(BigInteger bigInteger, final int n) {
        if (n == 0) {
            return bigInteger;
        }
        if (n > 0) {
            for (int i = 0; i < n; ++i) {
                bigInteger = bigInteger.multiply(BigIntegerDecimal.TEN);
            }
        }
        else if (n < 0) {
            for (int j = n; j < 0; ++j) {
                bigInteger = bigInteger.divide(BigIntegerDecimal.TEN);
            }
        }
        return bigInteger;
    }
    
    public void setBigDecimal(final Number n) throws StandardException {
        if (n == null) {
            this.setToNull();
        }
        else {
            this.setValue(n.toString());
        }
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return this.getString();
    }
    
    static {
        TEN = BigInteger.valueOf(10L);
        MAXLONG_PLUS_ONE = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        MINLONG_MINUS_ONE = BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE);
    }
}
