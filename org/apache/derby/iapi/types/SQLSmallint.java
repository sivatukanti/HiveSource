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

public final class SQLSmallint extends NumberDataType
{
    static final int SMALLINT_LENGTH = 2;
    private static final int BASE_MEMORY_USAGE;
    private short value;
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
        if (this.isNull()) {
            return null;
        }
        return Short.toString(this.value);
    }
    
    public int getLength() {
        return 2;
    }
    
    public Object getObject() {
        if (this.isNull()) {
            return null;
        }
        return new Integer(this.value);
    }
    
    public String getTypeName() {
        return "SMALLINT";
    }
    
    public int getTypeFormatId() {
        return 83;
    }
    
    public boolean isNull() {
        return this.isnull;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeShort(this.value);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.value = objectInput.readShort();
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
        return new SQLSmallint(this.value, this.isnull);
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLSmallint();
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException {
        try {
            this.value = set.getShort(n);
            this.isnull = (b && set.wasNull());
        }
        catch (SQLException ex) {
            this.value = (short)set.getInt(n);
            this.isnull = false;
        }
    }
    
    public final void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException {
        if (this.isNull()) {
            preparedStatement.setNull(n, 5);
            return;
        }
        preparedStatement.setShort(n, this.value);
    }
    
    public final void setInto(final ResultSet set, final int n) throws SQLException, StandardException {
        set.updateShort(n, this.value);
    }
    
    public SQLSmallint() {
        this.isnull = true;
    }
    
    public SQLSmallint(final short value) {
        this.value = value;
    }
    
    private SQLSmallint(final short value, final boolean isnull) {
        this.value = value;
        this.isnull = isnull;
    }
    
    public SQLSmallint(final Short n) {
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
                this.value = Short.valueOf(s.trim());
            }
            catch (NumberFormatException ex) {
                throw this.invalidFormat();
            }
            this.isnull = false;
        }
    }
    
    public void setValue(final short value) {
        this.value = value;
        this.isnull = false;
    }
    
    public void setValue(final byte b) {
        this.value = b;
        this.isnull = false;
    }
    
    public void setValue(final int n) throws StandardException {
        if (n > 32767 || n < -32768) {
            throw StandardException.newException("22003", "SMALLINT");
        }
        this.value = (short)n;
        this.isnull = false;
    }
    
    public void setValue(final long n) throws StandardException {
        if (n > 32767L || n < -32768L) {
            throw StandardException.newException("22003", "SMALLINT");
        }
        this.value = (short)n;
        this.isnull = false;
    }
    
    public void setValue(float normalizeREAL) throws StandardException {
        normalizeREAL = NumberDataType.normalizeREAL(normalizeREAL);
        if (normalizeREAL > 32767.0f || normalizeREAL < -32768.0f) {
            throw StandardException.newException("22003", "SMALLINT");
        }
        this.value = (short)Math.floor(normalizeREAL);
        this.isnull = false;
    }
    
    public void setValue(double normalizeDOUBLE) throws StandardException {
        normalizeDOUBLE = NumberDataType.normalizeDOUBLE(normalizeDOUBLE);
        if (normalizeDOUBLE > 32767.0 || normalizeDOUBLE < -32768.0) {
            throw StandardException.newException("22003", "SMALLINT");
        }
        this.value = (short)Math.floor(normalizeDOUBLE);
        this.isnull = false;
    }
    
    public void setValue(final boolean value) {
        this.value = (short)(value ? 1 : 0);
        this.isnull = false;
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        this.setValue(dataValueDescriptor.getShort());
    }
    
    public int typePrecedence() {
        return 40;
    }
    
    public BooleanDataValue equals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getShort() == dataValueDescriptor2.getShort());
    }
    
    public BooleanDataValue notEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getShort() != dataValueDescriptor2.getShort());
    }
    
    public BooleanDataValue lessThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getShort() < dataValueDescriptor2.getShort());
    }
    
    public BooleanDataValue greaterThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getShort() > dataValueDescriptor2.getShort());
    }
    
    public BooleanDataValue lessOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getShort() <= dataValueDescriptor2.getShort());
    }
    
    public BooleanDataValue greaterOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getShort() >= dataValueDescriptor2.getShort());
    }
    
    public NumberDataValue times(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLSmallint();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        numberDataValue3.setValue(numberDataValue.getShort() * numberDataValue2.getShort());
        return numberDataValue3;
    }
    
    public NumberDataValue mod(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLSmallint();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final short short1 = numberDataValue2.getShort();
        if (short1 == 0) {
            throw StandardException.newException("22012");
        }
        numberDataValue3.setValue(numberDataValue.getShort() % short1);
        return numberDataValue3;
    }
    
    public NumberDataValue minus(NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLSmallint();
        }
        if (this.isNull()) {
            numberDataValue.setToNull();
            return numberDataValue;
        }
        numberDataValue.setValue(-this.getShort());
        return numberDataValue;
    }
    
    protected boolean isNegative() {
        return !this.isNull() && this.value < 0;
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return Short.toString(this.value);
    }
    
    public int hashCode() {
        return this.value;
    }
    
    public int estimateMemoryUsage() {
        return SQLSmallint.BASE_MEMORY_USAGE;
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLSmallint.class);
    }
}
