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
import org.apache.derby.iapi.error.StandardException;

public final class SQLInteger extends NumberDataType
{
    static final int INTEGER_LENGTH = 4;
    private static final int BASE_MEMORY_USAGE;
    private int value;
    private boolean isnull;
    
    public int getInt() {
        return this.value;
    }
    
    public byte getByte() throws StandardException {
        if (this.value > 127 || this.value < -128) {
            throw StandardException.newException("22003", "TINYINT");
        }
        return (byte)this.value;
    }
    
    public short getShort() throws StandardException {
        if (this.value > 32767 || this.value < -32768) {
            throw StandardException.newException("22003", "SMALLINT");
        }
        return (short)this.value;
    }
    
    public long getLong() {
        return this.value;
    }
    
    public float getFloat() {
        return (float)this.value;
    }
    
    public double getDouble() {
        return this.value;
    }
    
    public boolean getBoolean() {
        return this.value != 0;
    }
    
    public String getString() {
        if (this.isNull()) {
            return null;
        }
        return Integer.toString(this.value);
    }
    
    public Object getObject() {
        if (this.isNull()) {
            return null;
        }
        return new Integer(this.value);
    }
    
    public int getLength() {
        return 4;
    }
    
    public String getTypeName() {
        return "INTEGER";
    }
    
    public int getTypeFormatId() {
        return 80;
    }
    
    public boolean isNull() {
        return this.isnull;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.value);
    }
    
    public final void readExternal(final ObjectInput objectInput) throws IOException {
        this.value = objectInput.readInt();
        this.isnull = false;
    }
    
    public void restoreToNull() {
        this.value = 0;
        this.isnull = true;
    }
    
    protected int typeCompare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final int int1 = this.getInt();
        final int int2 = dataValueDescriptor.getInt();
        if (int1 == int2) {
            return 0;
        }
        if (int1 > int2) {
            return 1;
        }
        return -1;
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        final SQLInteger sqlInteger = new SQLInteger(this.value);
        sqlInteger.isnull = this.isnull;
        return sqlInteger;
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLInteger();
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException {
        final int int1 = set.getInt(n);
        this.value = int1;
        if (int1 == 0) {
            this.isnull = (b && set.wasNull());
        }
        else {
            this.isnull = false;
        }
    }
    
    public final void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException {
        if (this.isNull()) {
            preparedStatement.setNull(n, 4);
            return;
        }
        preparedStatement.setInt(n, this.value);
    }
    
    public final void setInto(final ResultSet set, final int n) throws SQLException {
        set.updateInt(n, this.value);
    }
    
    public SQLInteger() {
        this.isnull = true;
    }
    
    public SQLInteger(final int value) {
        this.value = value;
    }
    
    public SQLInteger(final char value) {
        this.value = value;
    }
    
    public SQLInteger(final Integer n) {
        final boolean isnull = n == null;
        this.isnull = isnull;
        if (!isnull) {
            this.value = n;
        }
    }
    
    public void setValue(final String s) throws StandardException {
        if (s == null) {
            this.value = 0;
            this.isnull = true;
        }
        else {
            try {
                this.value = Integer.parseInt(s.trim());
            }
            catch (NumberFormatException ex) {
                throw this.invalidFormat();
            }
            this.isnull = false;
        }
    }
    
    public void setValue(final int value) {
        this.value = value;
        this.isnull = false;
    }
    
    public void setValue(final long n) throws StandardException {
        if (n > 2147483647L || n < -2147483648L) {
            throw this.outOfRange();
        }
        this.value = (int)n;
        this.isnull = false;
    }
    
    public void setValue(float normalizeREAL) throws StandardException {
        normalizeREAL = NumberDataType.normalizeREAL(normalizeREAL);
        if (normalizeREAL > 2.14748365E9f || normalizeREAL < -2.14748365E9f) {
            throw this.outOfRange();
        }
        this.value = (int)(float)Math.floor(normalizeREAL);
        this.isnull = false;
    }
    
    public void setValue(double normalizeDOUBLE) throws StandardException {
        normalizeDOUBLE = NumberDataType.normalizeDOUBLE(normalizeDOUBLE);
        if (normalizeDOUBLE > 2.147483647E9 || normalizeDOUBLE < -2.147483648E9) {
            throw this.outOfRange();
        }
        this.value = (int)Math.floor(normalizeDOUBLE);
        this.isnull = false;
    }
    
    public void setValue(final boolean value) {
        this.value = (value ? 1 : 0);
        this.isnull = false;
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        this.setValue(dataValueDescriptor.getInt());
    }
    
    public int typePrecedence() {
        return 50;
    }
    
    public BooleanDataValue equals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getInt() == dataValueDescriptor2.getInt());
    }
    
    public BooleanDataValue notEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getInt() != dataValueDescriptor2.getInt());
    }
    
    public BooleanDataValue lessThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getInt() < dataValueDescriptor2.getInt());
    }
    
    public BooleanDataValue greaterThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getInt() > dataValueDescriptor2.getInt());
    }
    
    public BooleanDataValue lessOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getInt() <= dataValueDescriptor2.getInt());
    }
    
    public BooleanDataValue greaterOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getInt() >= dataValueDescriptor2.getInt());
    }
    
    public NumberDataValue times(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLInteger();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        numberDataValue3.setValue(numberDataValue.getLong() * numberDataValue2.getLong());
        return numberDataValue3;
    }
    
    public NumberDataValue mod(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLInteger();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final int int1 = numberDataValue2.getInt();
        if (int1 == 0) {
            throw StandardException.newException("22012");
        }
        numberDataValue3.setValue(numberDataValue.getInt() % int1);
        return numberDataValue3;
    }
    
    public NumberDataValue minus(NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLInteger();
        }
        if (this.isNull()) {
            numberDataValue.setToNull();
            return numberDataValue;
        }
        final int int1 = this.getInt();
        if (int1 == Integer.MIN_VALUE) {
            throw this.outOfRange();
        }
        numberDataValue.setValue(-int1);
        return numberDataValue;
    }
    
    protected boolean isNegative() {
        return !this.isNull() && this.value < 0;
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return Integer.toString(this.value);
    }
    
    public int hashCode() {
        return this.value;
    }
    
    public int estimateMemoryUsage() {
        return SQLInteger.BASE_MEMORY_USAGE;
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLInteger.class);
    }
}
