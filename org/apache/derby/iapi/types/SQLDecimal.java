// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.cache.ClassSize;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import org.apache.derby.iapi.error.StandardException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public final class SQLDecimal extends NumberDataType implements VariableSizeDataValue
{
    private BigDecimal value;
    private byte[] rawData;
    private int rawScale;
    private static final int BASE_MEMORY_USAGE;
    private static final int BIG_DECIMAL_MEMORY_USAGE;
    private static final Method toPlainString;
    private static final Method bdPrecision;
    
    public int estimateMemoryUsage() {
        int base_MEMORY_USAGE = SQLDecimal.BASE_MEMORY_USAGE;
        if (null != this.value) {
            base_MEMORY_USAGE += SQLDecimal.BIG_DECIMAL_MEMORY_USAGE + (this.value.unscaledValue().bitLength() + 8) / 8;
        }
        if (null != this.rawData) {
            base_MEMORY_USAGE += this.rawData.length;
        }
        return base_MEMORY_USAGE;
    }
    
    public SQLDecimal() {
    }
    
    public SQLDecimal(final BigDecimal value) {
        this.value = value;
    }
    
    public SQLDecimal(final BigDecimal value, final int n, final int newScale) throws StandardException {
        this.value = value;
        if (this.value != null && newScale >= 0) {
            this.value = this.value.setScale(newScale, 1);
        }
    }
    
    public SQLDecimal(final String val) {
        this.value = new BigDecimal(val);
    }
    
    public int getInt() throws StandardException {
        if (this.isNull()) {
            return 0;
        }
        try {
            final long long1 = this.getLong();
            if (long1 >= -2147483648L && long1 <= 2147483647L) {
                return (int)long1;
            }
        }
        catch (StandardException ex) {}
        throw StandardException.newException("22003", "INTEGER");
    }
    
    public byte getByte() throws StandardException {
        if (this.isNull()) {
            return 0;
        }
        try {
            final long long1 = this.getLong();
            if (long1 >= -128L && long1 <= 127L) {
                return (byte)long1;
            }
        }
        catch (StandardException ex) {}
        throw StandardException.newException("22003", "TINYINT");
    }
    
    public short getShort() throws StandardException {
        if (this.isNull()) {
            return 0;
        }
        try {
            final long long1 = this.getLong();
            if (long1 >= -32768L && long1 <= 32767L) {
                return (short)long1;
            }
        }
        catch (StandardException ex) {}
        throw StandardException.newException("22003", "SMALLINT");
    }
    
    public long getLong() throws StandardException {
        final BigDecimal bigDecimal = this.getBigDecimal();
        if (bigDecimal == null) {
            return 0L;
        }
        if (bigDecimal.compareTo(SQLDecimal.MINLONG_MINUS_ONE) == 1 && bigDecimal.compareTo(SQLDecimal.MAXLONG_PLUS_ONE) == -1) {
            return bigDecimal.longValue();
        }
        throw StandardException.newException("22003", "BIGINT");
    }
    
    public float getFloat() throws StandardException {
        final BigDecimal bigDecimal = this.getBigDecimal();
        if (bigDecimal == null) {
            return 0.0f;
        }
        return NumberDataType.normalizeREAL(bigDecimal.floatValue());
    }
    
    public double getDouble() throws StandardException {
        final BigDecimal bigDecimal = this.getBigDecimal();
        if (bigDecimal == null) {
            return 0.0;
        }
        return NumberDataType.normalizeDOUBLE(bigDecimal.doubleValue());
    }
    
    private BigDecimal getBigDecimal() {
        if (this.value == null && this.rawData != null) {
            this.value = new BigDecimal(new BigInteger(this.rawData), this.rawScale);
        }
        return this.value;
    }
    
    public int typeToBigDecimal() {
        return 3;
    }
    
    public boolean getBoolean() {
        final BigDecimal bigDecimal = this.getBigDecimal();
        return bigDecimal != null && bigDecimal.compareTo(SQLDecimal.ZERO) != 0;
    }
    
    public String getString() {
        final BigDecimal bigDecimal = this.getBigDecimal();
        if (bigDecimal == null) {
            return null;
        }
        if (SQLDecimal.toPlainString == null) {
            return bigDecimal.toString();
        }
        try {
            return (String)SQLDecimal.toPlainString.invoke(bigDecimal, (Object[])null);
        }
        catch (IllegalAccessException ex2) {
            throw new IllegalAccessError("toPlainString");
        }
        catch (InvocationTargetException ex) {
            final Throwable targetException = ex.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException)targetException;
            }
            if (targetException instanceof Error) {
                throw (Error)targetException;
            }
            throw new IncompatibleClassChangeError("toPlainString");
        }
    }
    
    public Object getObject() {
        return this.getBigDecimal();
    }
    
    void setObject(final Object o) throws StandardException {
        this.setValue((Number)o);
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        this.setCoreValue(getBigDecimal(dataValueDescriptor));
    }
    
    public int getLength() {
        return this.getDecimalValuePrecision();
    }
    
    public String getTypeName() {
        return "DECIMAL";
    }
    
    public int getTypeFormatId() {
        return 200;
    }
    
    public boolean isNull() {
        return this.value == null && this.rawData == null;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        int n;
        byte[] array;
        if (this.value != null) {
            n = this.value.scale();
            if (n < 0) {
                n = 0;
                this.value = this.value.setScale(0);
            }
            array = this.value.unscaledValue().toByteArray();
        }
        else {
            n = this.rawScale;
            array = this.rawData;
        }
        objectOutput.writeByte(n);
        objectOutput.writeByte(array.length);
        objectOutput.write(array);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.value = null;
        this.rawScale = objectInput.readUnsignedByte();
        final int unsignedByte = objectInput.readUnsignedByte();
        if (this.rawData == null || unsignedByte != this.rawData.length) {
            this.rawData = new byte[unsignedByte];
        }
        objectInput.readFully(this.rawData);
    }
    
    public void restoreToNull() {
        this.value = null;
        this.rawData = null;
    }
    
    protected int typeCompare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        return this.getBigDecimal().compareTo(getBigDecimal(dataValueDescriptor));
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        return new SQLDecimal(this.getBigDecimal());
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLDecimal();
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException {
        this.value = set.getBigDecimal(n);
        this.rawData = null;
    }
    
    public final void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException {
        if (this.isNull()) {
            preparedStatement.setNull(n, 3);
            return;
        }
        preparedStatement.setBigDecimal(n, this.getBigDecimal());
    }
    
    public void setValue(String trim) throws StandardException {
        this.rawData = null;
        if (trim == null) {
            this.value = null;
        }
        else {
            try {
                trim = trim.trim();
                this.value = new BigDecimal(trim);
                this.rawData = null;
            }
            catch (NumberFormatException ex) {
                throw this.invalidFormat();
            }
        }
    }
    
    public void setValue(final double n) throws StandardException {
        this.setCoreValue(NumberDataType.normalizeDOUBLE(n));
    }
    
    public void setValue(final float n) throws StandardException {
        this.setCoreValue(NumberDataType.normalizeREAL(n));
    }
    
    public void setValue(final long val) {
        this.value = BigDecimal.valueOf(val);
        this.rawData = null;
    }
    
    public void setValue(final int n) {
        this.setValue((long)n);
    }
    
    public void setBigDecimal(final Number n) throws StandardException {
        this.setCoreValue((BigDecimal)n);
    }
    
    public void setValue(final Number n) throws StandardException {
        if (n instanceof BigDecimal || n == null) {
            this.setCoreValue((BigDecimal)n);
        }
        else {
            this.setValue(n.longValue());
        }
    }
    
    public void setValue(final boolean b) {
        this.setCoreValue(b ? SQLDecimal.ONE : SQLDecimal.ZERO);
    }
    
    public int typePrecedence() {
        return 70;
    }
    
    private void setCoreValue(final BigDecimal value) {
        this.value = value;
        this.rawData = null;
    }
    
    private void setCoreValue(final double d) {
        this.value = new BigDecimal(Double.toString(d));
        this.rawData = null;
    }
    
    public void normalize(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor from) throws StandardException {
        final int scale = dataTypeDescriptor.getScale();
        final int precision = dataTypeDescriptor.getPrecision();
        this.setFrom(from);
        this.setWidth(precision, scale, true);
    }
    
    public NumberDataValue plus(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLDecimal();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        numberDataValue3.setBigDecimal(getBigDecimal(numberDataValue).add(getBigDecimal(numberDataValue2)));
        return numberDataValue3;
    }
    
    public NumberDataValue minus(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLDecimal();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        numberDataValue3.setBigDecimal(getBigDecimal(numberDataValue).subtract(getBigDecimal(numberDataValue2)));
        return numberDataValue3;
    }
    
    public NumberDataValue times(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLDecimal();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        numberDataValue3.setBigDecimal(getBigDecimal(numberDataValue).multiply(getBigDecimal(numberDataValue2)));
        return numberDataValue3;
    }
    
    public NumberDataValue divide(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, final NumberDataValue numberDataValue3) throws StandardException {
        return this.divide(numberDataValue, numberDataValue2, numberDataValue3, -1);
    }
    
    public NumberDataValue divide(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3, final int n) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLDecimal();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final BigDecimal bigDecimal = getBigDecimal(numberDataValue2);
        if (bigDecimal.compareTo(SQLDecimal.ZERO) == 0) {
            throw StandardException.newException("22012");
        }
        final BigDecimal bigDecimal2 = getBigDecimal(numberDataValue);
        numberDataValue3.setBigDecimal(bigDecimal2.divide(bigDecimal, (n > -1) ? n : Math.max(bigDecimal2.scale() + getWholeDigits(bigDecimal) + 1, 4), 1));
        return numberDataValue3;
    }
    
    public NumberDataValue minus(NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLDecimal();
        }
        if (this.isNull()) {
            numberDataValue.setToNull();
            return numberDataValue;
        }
        numberDataValue.setBigDecimal(this.getBigDecimal().negate());
        return numberDataValue;
    }
    
    protected boolean isNegative() {
        return !this.isNull() && this.getBigDecimal().compareTo(SQLDecimal.ZERO) == -1;
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return this.getString();
    }
    
    public int hashCode() {
        final BigDecimal bigDecimal = this.getBigDecimal();
        final double n = (bigDecimal != null) ? bigDecimal.doubleValue() : 0.0;
        long n2;
        if (Double.isInfinite(n)) {
            n2 = bigDecimal.longValue();
        }
        else {
            n2 = (long)n;
            if (n2 != n) {
                n2 = Double.doubleToLongBits(n);
            }
        }
        return (int)(n2 ^ n2 >> 32);
    }
    
    public void setWidth(final int i, final int n, final boolean b) throws StandardException {
        if (this.isNull()) {
            return;
        }
        if (i != -1 && i - n < getWholeDigits(this.getBigDecimal())) {
            throw StandardException.newException("22003", "DECIMAL/NUMERIC(" + i + "," + n + ")");
        }
        this.value = this.value.setScale(n, 1);
        this.rawData = null;
    }
    
    public int getDecimalValuePrecision() {
        if (this.isNull()) {
            return 0;
        }
        return getWholeDigits(this.getBigDecimal()) + this.getDecimalValueScale();
    }
    
    public int getDecimalValueScale() {
        if (this.isNull()) {
            return 0;
        }
        if (this.value == null) {
            return this.rawScale;
        }
        final int scale = this.value.scale();
        if (scale >= 0) {
            return scale;
        }
        return 0;
    }
    
    public static BigDecimal getBigDecimal(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        switch (dataValueDescriptor.typeToBigDecimal()) {
            case 3: {
                return (BigDecimal)dataValueDescriptor.getObject();
            }
            case 1: {
                try {
                    return new BigDecimal(dataValueDescriptor.getString().trim());
                }
                catch (NumberFormatException ex) {
                    throw StandardException.newException("22018", "java.math.BigDecimal");
                }
            }
            case -5: {
                return BigDecimal.valueOf(dataValueDescriptor.getLong());
            }
            default: {
                return null;
            }
        }
    }
    
    private static int getWholeDigits(BigDecimal abs) {
        abs = abs.abs();
        if (SQLDecimal.ONE.compareTo(abs) == 1) {
            return 0;
        }
        if (SQLDecimal.bdPrecision != null) {
            try {
                return (int)SQLDecimal.bdPrecision.invoke(abs, (Object[])null) - abs.scale();
            }
            catch (IllegalAccessException ex2) {
                throw new IllegalAccessError("precision");
            }
            catch (InvocationTargetException ex) {
                final Throwable targetException = ex.getTargetException();
                if (targetException instanceof RuntimeException) {
                    throw (RuntimeException)targetException;
                }
                if (targetException instanceof Error) {
                    throw (Error)targetException;
                }
                throw new IncompatibleClassChangeError("precision");
            }
        }
        final String string = abs.toString();
        return (abs.scale() == 0) ? string.length() : string.indexOf(46);
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLDecimal.class);
        BIG_DECIMAL_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(BigDecimal.class);
        Method method;
        try {
            method = BigDecimal.class.getMethod("toPlainString", (Class[])null);
        }
        catch (NoSuchMethodException ex) {
            method = null;
        }
        toPlainString = method;
        Method method2;
        try {
            method2 = BigDecimal.class.getMethod("precision", (Class[])null);
        }
        catch (NoSuchMethodException ex2) {
            method2 = null;
        }
        bdPrecision = method2;
    }
}
