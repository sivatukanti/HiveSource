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

public final class SQLLongint extends NumberDataType
{
    private static final int BASE_MEMORY_USAGE;
    private long value;
    private boolean isnull;
    
    public int getInt() throws StandardException {
        if (this.value > 2147483647L || this.value < -2147483648L) {
            throw StandardException.newException("22003", "INTEGER");
        }
        return (int)this.value;
    }
    
    public byte getByte() throws StandardException {
        if (this.value > 127L || this.value < -128L) {
            throw StandardException.newException("22003", "TINYINT");
        }
        return (byte)this.value;
    }
    
    public short getShort() throws StandardException {
        if (this.value > 32767L || this.value < -32768L) {
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
        return (double)this.value;
    }
    
    public boolean getBoolean() {
        return this.value != 0L;
    }
    
    public String getString() {
        if (this.isNull()) {
            return null;
        }
        return Long.toString(this.value);
    }
    
    public Object getObject() {
        if (this.isNull()) {
            return null;
        }
        return new Long(this.value);
    }
    
    public int getLength() {
        return 8;
    }
    
    public String getTypeName() {
        return "BIGINT";
    }
    
    public int getTypeFormatId() {
        return 84;
    }
    
    public boolean isNull() {
        return this.isnull;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeLong(this.value);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.value = objectInput.readLong();
        this.isnull = false;
    }
    
    public void restoreToNull() {
        this.value = 0L;
        this.isnull = true;
    }
    
    protected int typeCompare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final long long1 = this.getLong();
        final long long2 = dataValueDescriptor.getLong();
        if (long1 == long2) {
            return 0;
        }
        if (long1 > long2) {
            return 1;
        }
        return -1;
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        return new SQLLongint(this.value, this.isnull);
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLLongint();
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException {
        final long long1 = set.getLong(n);
        this.value = long1;
        if (long1 == 0L) {
            this.isnull = (b && set.wasNull());
        }
        else {
            this.isnull = false;
        }
    }
    
    public final void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException {
        if (this.isNull()) {
            preparedStatement.setNull(n, -5);
            return;
        }
        preparedStatement.setLong(n, this.value);
    }
    
    public final void setInto(final ResultSet set, final int n) throws SQLException {
        set.updateLong(n, this.value);
    }
    
    public SQLLongint() {
        this.isnull = true;
    }
    
    public SQLLongint(final long value) {
        this.value = value;
    }
    
    private SQLLongint(final long value, final boolean isnull) {
        this.value = value;
        this.isnull = isnull;
    }
    
    public SQLLongint(final Long n) {
        final boolean isnull = n == null;
        this.isnull = isnull;
        if (!isnull) {
            this.value = n;
        }
    }
    
    public void setValue(final String s) throws StandardException {
        if (s == null) {
            this.value = 0L;
            this.isnull = true;
        }
        else {
            try {
                this.value = Long.valueOf(s.trim());
            }
            catch (NumberFormatException ex) {
                throw this.invalidFormat();
            }
            this.isnull = false;
        }
    }
    
    public final void setValue(final Number n) {
        if (this.objectNull(n)) {
            return;
        }
        this.setValue(n.longValue());
    }
    
    public void setValue(final long value) {
        this.value = value;
        this.isnull = false;
    }
    
    public void setValue(final int n) {
        this.value = n;
        this.isnull = false;
    }
    
    public void setValue(float normalizeREAL) throws StandardException {
        normalizeREAL = NumberDataType.normalizeREAL(normalizeREAL);
        if (normalizeREAL > 9.223372E18f || normalizeREAL < -9.223372E18f) {
            throw StandardException.newException("22003", "BIGINT");
        }
        this.value = (long)(float)Math.floor(normalizeREAL);
        this.isnull = false;
    }
    
    public void setValue(double normalizeDOUBLE) throws StandardException {
        normalizeDOUBLE = NumberDataType.normalizeDOUBLE(normalizeDOUBLE);
        if (normalizeDOUBLE > 9.223372036854776E18 || normalizeDOUBLE < -9.223372036854776E18) {
            throw StandardException.newException("22003", "BIGINT");
        }
        this.value = (long)Math.floor(normalizeDOUBLE);
        this.isnull = false;
    }
    
    public void setValue(final boolean value) {
        this.value = (value ? 1 : 0);
        this.isnull = false;
    }
    
    void setObject(final Object o) {
        this.setValue((long)o);
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        this.setValue(dataValueDescriptor.getLong());
    }
    
    public int typePrecedence() {
        return 60;
    }
    
    public BooleanDataValue equals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getLong() == dataValueDescriptor2.getLong());
    }
    
    public BooleanDataValue notEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getLong() != dataValueDescriptor2.getLong());
    }
    
    public BooleanDataValue lessThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getLong() < dataValueDescriptor2.getLong());
    }
    
    public BooleanDataValue greaterThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getLong() > dataValueDescriptor2.getLong());
    }
    
    public BooleanDataValue lessOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getLong() <= dataValueDescriptor2.getLong());
    }
    
    public BooleanDataValue greaterOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getLong() >= dataValueDescriptor2.getLong());
    }
    
    public NumberDataValue plus(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLLongint();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final long long1 = numberDataValue.getLong();
        final long long2 = numberDataValue2.getLong();
        final long value = long1 + long2;
        if (long1 < 0L == long2 < 0L && long1 < 0L != value < 0L) {
            throw StandardException.newException("22003", "BIGINT");
        }
        numberDataValue3.setValue(value);
        return numberDataValue3;
    }
    
    public NumberDataValue minus(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLLongint();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final long value = numberDataValue.getLong() - numberDataValue2.getLong();
        if (numberDataValue.getLong() < 0L != numberDataValue2.getLong() < 0L && numberDataValue.getLong() < 0L != value < 0L) {
            throw StandardException.newException("22003", "BIGINT");
        }
        numberDataValue3.setValue(value);
        return numberDataValue3;
    }
    
    public NumberDataValue times(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLLongint();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final long value = numberDataValue.getLong() * numberDataValue2.getLong();
        if (numberDataValue2.getLong() != 0L && numberDataValue.getLong() != value / numberDataValue2.getLong()) {
            throw StandardException.newException("22003", "BIGINT");
        }
        numberDataValue3.setValue(value);
        return numberDataValue3;
    }
    
    public NumberDataValue divide(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLLongint();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final long long1 = numberDataValue2.getLong();
        if (long1 == 0L) {
            throw StandardException.newException("22012");
        }
        numberDataValue3.setValue(numberDataValue.getLong() / long1);
        return numberDataValue3;
    }
    
    public NumberDataValue mod(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLLongint();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final long long1 = numberDataValue2.getLong();
        if (long1 == 0L) {
            throw StandardException.newException("22012");
        }
        numberDataValue3.setValue(numberDataValue.getLong() % long1);
        return numberDataValue3;
    }
    
    public NumberDataValue minus(NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLLongint();
        }
        if (this.isNull()) {
            numberDataValue.setToNull();
            return numberDataValue;
        }
        final long long1 = this.getLong();
        if (long1 == Long.MIN_VALUE) {
            throw StandardException.newException("22003", "BIGINT");
        }
        numberDataValue.setValue(-long1);
        return numberDataValue;
    }
    
    protected boolean isNegative() {
        return !this.isNull() && this.value < 0L;
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return Long.toString(this.value);
    }
    
    public int hashCode() {
        return (int)(this.value ^ this.value >> 32);
    }
    
    public int estimateMemoryUsage() {
        return SQLLongint.BASE_MEMORY_USAGE;
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLLongint.class);
    }
}
