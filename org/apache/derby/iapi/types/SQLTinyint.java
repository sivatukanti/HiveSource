// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.cache.ClassSize;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.derby.iapi.error.StandardException;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public final class SQLTinyint extends NumberDataType
{
    static final int TINYINT_LENGTH = 1;
    private byte value;
    private boolean isnull;
    private static final int BASE_MEMORY_USAGE;
    
    public int estimateMemoryUsage() {
        return SQLTinyint.BASE_MEMORY_USAGE;
    }
    
    public SQLTinyint() {
        this.isnull = true;
    }
    
    public SQLTinyint(final byte value) {
        this.value = value;
    }
    
    private SQLTinyint(final byte value, final boolean isnull) {
        this.value = value;
        this.isnull = isnull;
    }
    
    public SQLTinyint(final Byte b) {
        final boolean isnull = b == null;
        this.isnull = isnull;
        if (!isnull) {
            this.value = b;
        }
    }
    
    public int getInt() {
        return this.value;
    }
    
    public byte getByte() {
        return this.value;
    }
    
    public short getShort() {
        return this.value;
    }
    
    public long getLong() {
        return this.value;
    }
    
    public float getFloat() {
        return this.value;
    }
    
    public double getDouble() {
        return this.value;
    }
    
    public boolean getBoolean() {
        return this.value != 0;
    }
    
    public String getString() {
        return this.isNull() ? null : Byte.toString(this.value);
    }
    
    public int getLength() {
        return 1;
    }
    
    public Object getObject() {
        return this.isNull() ? null : new Integer(this.value);
    }
    
    public String getTypeName() {
        return "TINYINT";
    }
    
    public int getTypeFormatId() {
        return 199;
    }
    
    public boolean isNull() {
        return this.isnull;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeByte(this.value);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.value = objectInput.readByte();
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
        return new SQLTinyint(this.value, this.isnull);
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLTinyint();
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException {
        this.value = set.getByte(n);
        this.isnull = (b && set.wasNull());
    }
    
    public final void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException {
        if (this.isNull()) {
            preparedStatement.setNull(n, -6);
            return;
        }
        preparedStatement.setByte(n, this.value);
    }
    
    public final void setInto(final ResultSet set, final int n) throws SQLException, StandardException {
        set.updateByte(n, this.value);
    }
    
    public void setValue(final String s) throws StandardException {
        if (s == null) {
            this.value = 0;
            this.isnull = true;
        }
        else {
            try {
                this.value = Byte.valueOf(s.trim());
            }
            catch (NumberFormatException ex) {
                throw this.invalidFormat();
            }
            this.isnull = false;
        }
    }
    
    public void setValue(final byte value) {
        this.value = value;
        this.isnull = false;
    }
    
    public void setValue(final short n) throws StandardException {
        if (n > 127 || n < -128) {
            throw StandardException.newException("22003", "TINYINT");
        }
        this.value = (byte)n;
        this.isnull = false;
    }
    
    public void setValue(final int n) throws StandardException {
        if (n > 127 || n < -128) {
            throw StandardException.newException("22003", "TINYINT");
        }
        this.value = (byte)n;
        this.isnull = false;
    }
    
    public void setValue(final long n) throws StandardException {
        if (n > 127L || n < -128L) {
            throw StandardException.newException("22003", "TINYINT");
        }
        this.value = (byte)n;
        this.isnull = false;
    }
    
    public void setValue(float normalizeREAL) throws StandardException {
        normalizeREAL = NumberDataType.normalizeREAL(normalizeREAL);
        if (normalizeREAL > 127.0f || normalizeREAL < -128.0f) {
            throw StandardException.newException("22003", "TINYINT");
        }
        this.value = (byte)Math.floor(normalizeREAL);
        this.isnull = false;
    }
    
    public void setValue(double normalizeDOUBLE) throws StandardException {
        normalizeDOUBLE = NumberDataType.normalizeDOUBLE(normalizeDOUBLE);
        if (normalizeDOUBLE > 127.0 || normalizeDOUBLE < -128.0) {
            throw this.outOfRange();
        }
        this.value = (byte)Math.floor(normalizeDOUBLE);
        this.isnull = false;
    }
    
    public void setValue(final boolean value) {
        this.value = (byte)(value ? 1 : 0);
        this.isnull = false;
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        this.setValue(dataValueDescriptor.getByte());
    }
    
    public int typePrecedence() {
        return 30;
    }
    
    public BooleanDataValue equals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getByte() == dataValueDescriptor2.getByte());
    }
    
    public BooleanDataValue notEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getByte() != dataValueDescriptor2.getByte());
    }
    
    public BooleanDataValue lessThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getByte() < dataValueDescriptor2.getByte());
    }
    
    public BooleanDataValue greaterThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getByte() > dataValueDescriptor2.getByte());
    }
    
    public BooleanDataValue lessOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getByte() <= dataValueDescriptor2.getByte());
    }
    
    public BooleanDataValue greaterOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getByte() >= dataValueDescriptor2.getByte());
    }
    
    public NumberDataValue times(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLTinyint();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        numberDataValue3.setValue(numberDataValue.getByte() * numberDataValue2.getByte());
        return numberDataValue3;
    }
    
    public NumberDataValue mod(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLTinyint();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final byte byte1 = numberDataValue2.getByte();
        if (byte1 == 0) {
            throw StandardException.newException("22012");
        }
        numberDataValue3.setValue(numberDataValue.getByte() % byte1);
        return numberDataValue3;
    }
    
    public NumberDataValue minus(NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLTinyint();
        }
        if (this.isNull()) {
            numberDataValue.setToNull();
            return numberDataValue;
        }
        numberDataValue.setValue(-this.getByte());
        return numberDataValue;
    }
    
    protected boolean isNegative() {
        return !this.isNull() && this.value < 0;
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return Byte.toString(this.value);
    }
    
    public int hashCode() {
        return this.value;
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLTinyint.class);
    }
}
