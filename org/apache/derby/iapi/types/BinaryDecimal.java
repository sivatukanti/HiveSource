// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;

abstract class BinaryDecimal extends NumberDataType implements VariableSizeDataValue
{
    private static final byte[] ONE_2C;
    protected byte[] data2c;
    protected int sqlScale;
    
    public final int typeToBigDecimal() {
        return 3;
    }
    
    public final int typePrecedence() {
        return 70;
    }
    
    public final String getTypeName() {
        return "DECIMAL";
    }
    
    public final int getTypeFormatId() {
        return 200;
    }
    
    public boolean isNull() {
        return this.data2c == null;
    }
    
    public void restoreToNull() {
        this.data2c = null;
    }
    
    protected boolean isNegative() {
        return !this.isNull() && (this.data2c[0] & 0x80) != 0x0;
    }
    
    public void setValue(final long n) {
        byte[] data2c = this.data2c;
        if (data2c == null || data2c.length < 8) {
            data2c = new byte[8];
        }
        data2c[0] = (byte)(n >>> 56);
        data2c[1] = (byte)(n >>> 48);
        data2c[2] = (byte)(n >>> 40);
        data2c[3] = (byte)(n >>> 32);
        data2c[4] = (byte)(n >>> 24);
        data2c[5] = (byte)(n >>> 16);
        data2c[6] = (byte)(n >>> 8);
        data2c[7] = (byte)n;
        this.data2c = reduceBytes2c(data2c, 0, 8);
        this.sqlScale = 0;
    }
    
    public final void setValue(final int n) {
        byte[] data2c = this.data2c;
        if (data2c == null || data2c.length < 4) {
            data2c = new byte[4];
        }
        data2c[0] = (byte)(n >>> 24);
        data2c[1] = (byte)(n >>> 16);
        data2c[2] = (byte)(n >>> 8);
        data2c[3] = (byte)n;
        this.data2c = reduceBytes2c(data2c, 0, 4);
        this.sqlScale = 0;
    }
    
    public void setValue(final boolean value) {
        this.setValue(value ? 1 : 0);
    }
    
    public final void setValue(final double n) throws StandardException {
        this.setCoreValue(NumberDataType.normalizeDOUBLE(n));
    }
    
    public final void setValue(final float n) throws StandardException {
        this.setCoreValue(NumberDataType.normalizeREAL(n));
    }
    
    private void setCoreValue(final double d) throws StandardException {
        this.setValue(Double.toString(d));
    }
    
    public void setValue(final Number n) throws StandardException {
        if (n == null) {
            this.setToNull();
        }
        else {
            this.setValue(n.longValue());
        }
    }
    
    protected void setFrom(final DataValueDescriptor from) throws StandardException {
        switch (from.typeToBigDecimal()) {
            case 1:
            case 3: {
                this.setValue(from.getString());
                break;
            }
            case -5: {
                this.setValue(from.getLong());
                break;
            }
            default: {
                super.setFrom(from);
                break;
            }
        }
    }
    
    public final int getInt() throws StandardException {
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
    
    public final byte getByte() throws StandardException {
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
    
    public final short getShort() throws StandardException {
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
    
    public final NumberDataValue plus(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = (NumberDataValue)this.getNewNull();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        return this.plusNN(numberDataValue, numberDataValue2, numberDataValue3);
    }
    
    public final NumberDataValue times(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = (NumberDataValue)this.getNewNull();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        return this.timesNN(numberDataValue, numberDataValue2, numberDataValue3);
    }
    
    public NumberDataValue divide(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, final NumberDataValue numberDataValue3) throws StandardException {
        return this.divide(numberDataValue, numberDataValue2, numberDataValue3, -1);
    }
    
    public final NumberDataValue divide(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3, final int n) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = (NumberDataValue)this.getNewNull();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        return this.divideNN(numberDataValue, numberDataValue2, numberDataValue3, n);
    }
    
    public final NumberDataValue minus(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = (NumberDataValue)this.getNewNull();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        return this.minusNN(numberDataValue, numberDataValue2, numberDataValue3);
    }
    
    public NumberDataValue minusNN(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, final NumberDataValue numberDataValue3) throws StandardException {
        return this.plusNN(numberDataValue, numberDataValue2.minus(numberDataValue3), numberDataValue3);
    }
    
    public abstract NumberDataValue timesNN(final NumberDataValue p0, final NumberDataValue p1, final NumberDataValue p2) throws StandardException;
    
    public abstract NumberDataValue plusNN(final NumberDataValue p0, final NumberDataValue p1, final NumberDataValue p2) throws StandardException;
    
    public abstract NumberDataValue divideNN(final NumberDataValue p0, final NumberDataValue p1, final NumberDataValue p2, final int p3) throws StandardException;
    
    private static byte[] reduceBytes2c(final byte[] array, final int n, final int n2) {
        int i;
        for (i = 0; i < n2 - 1; ++i) {
            if (array[n + i] != 0) {
                break;
            }
            if ((array[n + i + 1] & 0x80) != 0x0) {
                break;
            }
        }
        if (i == 0) {
            while (i < n2 - 1 && array[n + i] == -1 && (array[n + i + 1] & 0xFFFFFF80) != 0x0) {
                ++i;
            }
        }
        if (i != 0 || array.length != n2) {
            final byte[] array2 = new byte[n2 - i];
            System.arraycopy(array, n + i, array2, 0, array2.length);
            return array2;
        }
        return array;
    }
    
    public int getDecimalValueScale() {
        if (this.isNull()) {
            return 0;
        }
        return this.sqlScale;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeByte(this.sqlScale);
        objectOutput.writeByte(this.data2c.length);
        objectOutput.write(this.data2c);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.sqlScale = objectInput.readUnsignedByte();
        final int unsignedByte = objectInput.readUnsignedByte();
        if (this.data2c == null || unsignedByte != this.data2c.length) {
            this.data2c = new byte[unsignedByte];
        }
        objectInput.readFully(this.data2c);
    }
    
    public final int getLength() {
        return this.getDecimalValuePrecision();
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        final BinaryDecimal binaryDecimal = (BinaryDecimal)this.getNewNull();
        if (this.data2c != null) {
            binaryDecimal.data2c = new byte[this.data2c.length];
            System.arraycopy(this.data2c, 0, binaryDecimal.data2c, 0, this.data2c.length);
            binaryDecimal.sqlScale = this.sqlScale;
        }
        return binaryDecimal;
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws StandardException, SQLException {
        this.setValue(set.getString(n));
    }
    
    public int estimateMemoryUsage() {
        return 0;
    }
    
    public int hashCode() {
        if (this.isNull()) {
            return 0;
        }
        try {
            return (int)this.getLong();
        }
        catch (StandardException ex) {
            return 0;
        }
    }
    
    static {
        ONE_2C = new byte[] { 1 };
    }
}
