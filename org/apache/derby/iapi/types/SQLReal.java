// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.cache.ClassSize;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;

public final class SQLReal extends NumberDataType
{
    static final int REAL_LENGTH = 16;
    private static final int BASE_MEMORY_USAGE;
    private float value;
    private boolean isnull;
    
    public int getInt() throws StandardException {
        if (this.value > 2.147483648E9 || this.value < -2.147483649E9) {
            throw StandardException.newException("22003", "INTEGER");
        }
        return (int)this.value;
    }
    
    public byte getByte() throws StandardException {
        if (this.value > 128.0 || this.value < -129.0) {
            throw StandardException.newException("22003", "TINYINT");
        }
        return (byte)this.value;
    }
    
    public short getShort() throws StandardException {
        if (this.value > 32768.0 || this.value < -32769.0) {
            throw StandardException.newException("22003", "SMALLINT");
        }
        return (short)this.value;
    }
    
    public long getLong() throws StandardException {
        if (this.value > 9.223372036854776E18 || this.value < -9.223372036854776E18) {
            throw StandardException.newException("22003", "BIGINT");
        }
        return (long)this.value;
    }
    
    public float getFloat() {
        return this.value;
    }
    
    public double getDouble() {
        return this.value;
    }
    
    public int typeToBigDecimal() {
        return 1;
    }
    
    public boolean getBoolean() {
        return this.value != 0.0f;
    }
    
    public String getString() {
        if (this.isNull()) {
            return null;
        }
        return Float.toString(this.value);
    }
    
    public int getLength() {
        return 16;
    }
    
    public Object getObject() {
        if (this.isNull()) {
            return null;
        }
        return new Float(this.value);
    }
    
    public String getTypeName() {
        return "REAL";
    }
    
    public int getTypeFormatId() {
        return 81;
    }
    
    public boolean isNull() {
        return this.isnull;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeFloat(this.value);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.value = objectInput.readFloat();
        this.isnull = false;
    }
    
    public void restoreToNull() {
        this.value = 0.0f;
        this.isnull = true;
    }
    
    protected int typeCompare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final float float1 = this.getFloat();
        final float normalizeREAL = NumberDataType.normalizeREAL(dataValueDescriptor.getFloat());
        if (float1 == normalizeREAL) {
            return 0;
        }
        if (float1 > normalizeREAL) {
            return 1;
        }
        return -1;
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        final SQLReal sqlReal = new SQLReal();
        sqlReal.value = this.value;
        sqlReal.isnull = this.isnull;
        return sqlReal;
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLReal();
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws StandardException, SQLException {
        final float float1 = set.getFloat(n);
        if (b && set.wasNull()) {
            this.restoreToNull();
        }
        else {
            this.setValue(float1);
        }
    }
    
    public final void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException {
        if (this.isNull()) {
            preparedStatement.setNull(n, 7);
            return;
        }
        preparedStatement.setFloat(n, this.value);
    }
    
    public final void setInto(final ResultSet set, final int n) throws SQLException, StandardException {
        set.updateFloat(n, this.value);
    }
    
    public SQLReal() {
        this.isnull = true;
    }
    
    public SQLReal(final float n) throws StandardException {
        this.value = NumberDataType.normalizeREAL(n);
    }
    
    public SQLReal(final Float n) throws StandardException {
        final boolean isnull = n == null;
        this.isnull = isnull;
        if (!isnull) {
            this.value = NumberDataType.normalizeREAL(n);
        }
    }
    
    public void setValue(final String s) throws StandardException {
        if (s == null) {
            this.value = 0.0f;
            this.isnull = true;
        }
        else {
            try {
                this.setValue(Double.parseDouble(s.trim()));
            }
            catch (NumberFormatException ex) {
                throw this.invalidFormat();
            }
        }
    }
    
    public void setValue(final Number n) throws StandardException {
        if (this.objectNull(n)) {
            return;
        }
        this.setValue(n.floatValue());
    }
    
    public void setBigDecimal(final Number n) throws StandardException {
        if (this.objectNull(n)) {
            return;
        }
        final float floatValue = n.floatValue();
        if (floatValue == 0.0f && ((BigDecimal)n).compareTo(BigDecimal.ZERO) != 0) {
            throw StandardException.newException("22003", "REAL");
        }
        this.setValue(floatValue);
    }
    
    public void setValue(final float n) throws StandardException {
        this.value = NumberDataType.normalizeREAL(n);
        this.isnull = false;
    }
    
    public void setValue(final int n) {
        this.value = (float)n;
        this.isnull = false;
    }
    
    public void setValue(final long n) {
        this.value = (float)n;
        this.isnull = false;
    }
    
    public void setValue(final double n) throws StandardException {
        final float value = (float)n;
        if (value == 0.0f && n != 0.0) {
            throw StandardException.newException("22003", "REAL");
        }
        this.setValue(value);
    }
    
    public void setValue(final boolean b) {
        this.value = (b ? 1.0f : 0.0f);
        this.isnull = false;
    }
    
    void setObject(final Object o) throws StandardException {
        this.setValue((float)o);
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor instanceof StringDataValue) {
            this.setValue(dataValueDescriptor.getString());
        }
        else if (dataValueDescriptor instanceof SQLDouble) {
            this.setValue(dataValueDescriptor.getDouble());
        }
        else {
            this.setValue(dataValueDescriptor.getFloat());
        }
    }
    
    public int typePrecedence() {
        return 80;
    }
    
    public BooleanDataValue equals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getFloat() == dataValueDescriptor2.getFloat());
    }
    
    public BooleanDataValue notEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getFloat() != dataValueDescriptor2.getFloat());
    }
    
    public BooleanDataValue lessThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getFloat() < dataValueDescriptor2.getFloat());
    }
    
    public BooleanDataValue greaterThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getFloat() > dataValueDescriptor2.getFloat());
    }
    
    public BooleanDataValue lessOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getFloat() <= dataValueDescriptor2.getFloat());
    }
    
    public BooleanDataValue greaterOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getFloat() >= dataValueDescriptor2.getFloat());
    }
    
    public NumberDataValue plus(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLReal();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        numberDataValue3.setValue(numberDataValue.getDouble() + numberDataValue2.getDouble());
        return numberDataValue3;
    }
    
    public NumberDataValue minus(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLReal();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        numberDataValue3.setValue(numberDataValue.getDouble() - numberDataValue2.getDouble());
        return numberDataValue3;
    }
    
    public NumberDataValue times(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLReal();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final double double1 = numberDataValue.getDouble();
        final double double2 = numberDataValue2.getDouble();
        final double value = double1 * double2;
        if (value == 0.0 && double1 != 0.0 && double2 != 0.0) {
            throw StandardException.newException("22003", "REAL");
        }
        numberDataValue3.setValue(value);
        return numberDataValue3;
    }
    
    public NumberDataValue divide(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLReal();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final double double1 = numberDataValue2.getDouble();
        if (double1 == 0.0) {
            throw StandardException.newException("22012");
        }
        final double double2 = numberDataValue.getDouble();
        final double n = double2 / double1;
        if (Double.isNaN(n)) {
            throw StandardException.newException("22012");
        }
        if (n == 0.0 && double2 != 0.0) {
            throw StandardException.newException("22003", "REAL");
        }
        numberDataValue3.setValue(n);
        return numberDataValue3;
    }
    
    public NumberDataValue minus(NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLReal();
        }
        if (this.isNull()) {
            numberDataValue.setToNull();
            return numberDataValue;
        }
        numberDataValue.setValue(-this.getFloat());
        return numberDataValue;
    }
    
    protected boolean isNegative() {
        return !this.isNull() && this.value < 0.0f;
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return Float.toString(this.value);
    }
    
    public int hashCode() {
        long doubleToLongBits = (long)this.value;
        if (doubleToLongBits != this.value) {
            doubleToLongBits = Double.doubleToLongBits(this.value);
        }
        return (int)(doubleToLongBits ^ doubleToLongBits >> 32);
    }
    
    public int estimateMemoryUsage() {
        return SQLReal.BASE_MEMORY_USAGE;
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLReal.class);
    }
}
