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

public final class SQLDouble extends NumberDataType
{
    static final int DOUBLE_LENGTH = 32;
    private static final int BASE_MEMORY_USAGE;
    private double value;
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
    
    public float getFloat() throws StandardException {
        if (Float.isInfinite((float)this.value)) {
            throw StandardException.newException("22003", "REAL");
        }
        return (float)this.value;
    }
    
    public double getDouble() {
        return this.value;
    }
    
    public int typeToBigDecimal() {
        return 1;
    }
    
    public boolean getBoolean() {
        return this.value != 0.0;
    }
    
    public String getString() {
        if (this.isNull()) {
            return null;
        }
        return Double.toString(this.value);
    }
    
    public Object getObject() {
        if (this.isNull()) {
            return null;
        }
        return new Double(this.value);
    }
    
    void setObject(final Object o) throws StandardException {
        this.setValue((double)o);
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        this.setValue(dataValueDescriptor.getDouble());
    }
    
    public int getLength() {
        return 32;
    }
    
    public String getTypeName() {
        return "DOUBLE";
    }
    
    public int getTypeFormatId() {
        return 79;
    }
    
    public boolean isNull() {
        return this.isnull;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeDouble(this.value);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.value = objectInput.readDouble();
        this.isnull = false;
    }
    
    public void restoreToNull() {
        this.value = 0.0;
        this.isnull = true;
    }
    
    protected int typeCompare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final double double1 = this.getDouble();
        final double double2 = dataValueDescriptor.getDouble();
        if (double1 == double2) {
            return 0;
        }
        if (double1 > double2) {
            return 1;
        }
        return -1;
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        try {
            return new SQLDouble(this.value, this.isnull);
        }
        catch (StandardException ex) {
            return null;
        }
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLDouble();
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws StandardException, SQLException {
        final double double1 = set.getDouble(n);
        this.isnull = (b && set.wasNull());
        if (this.isnull) {
            this.value = 0.0;
        }
        else {
            this.value = NumberDataType.normalizeDOUBLE(double1);
        }
    }
    
    public final void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException {
        if (this.isNull()) {
            preparedStatement.setNull(n, 8);
            return;
        }
        preparedStatement.setDouble(n, this.value);
    }
    
    public final void setInto(final ResultSet set, final int n) throws SQLException, StandardException {
        set.updateDouble(n, this.value);
    }
    
    public SQLDouble() {
        this.isnull = true;
    }
    
    public SQLDouble(final double n) throws StandardException {
        this.value = NumberDataType.normalizeDOUBLE(n);
    }
    
    public SQLDouble(final Double n) throws StandardException {
        final boolean isnull = n == null;
        this.isnull = isnull;
        if (!isnull) {
            this.value = NumberDataType.normalizeDOUBLE(n);
        }
    }
    
    private SQLDouble(final double n, final boolean isnull) throws StandardException {
        this.value = NumberDataType.normalizeDOUBLE(n);
        this.isnull = isnull;
    }
    
    public void setValue(final String s) throws StandardException {
        if (s == null) {
            this.value = 0.0;
            this.isnull = true;
        }
        else {
            double double1;
            try {
                double1 = Double.parseDouble(s.trim());
            }
            catch (NumberFormatException ex) {
                throw this.invalidFormat();
            }
            this.value = NumberDataType.normalizeDOUBLE(double1);
            this.isnull = false;
        }
    }
    
    public void setValue(final double n) throws StandardException {
        this.value = NumberDataType.normalizeDOUBLE(n);
        this.isnull = false;
    }
    
    public void setValue(final float n) throws StandardException {
        this.value = NumberDataType.normalizeDOUBLE(n);
        this.isnull = false;
    }
    
    public void setValue(final long n) {
        this.value = (double)n;
        this.isnull = false;
    }
    
    public void setValue(final int n) {
        this.value = n;
        this.isnull = false;
    }
    
    public void setValue(final Number n) throws StandardException {
        if (this.objectNull(n)) {
            return;
        }
        this.setValue(n.doubleValue());
    }
    
    public void setBigDecimal(final Number n) throws StandardException {
        if (this.objectNull(n)) {
            return;
        }
        final double doubleValue = n.doubleValue();
        if (doubleValue == 0.0 && ((BigDecimal)n).compareTo(BigDecimal.ZERO) != 0) {
            throw StandardException.newException("22003", "REAL");
        }
        this.setValue(doubleValue);
    }
    
    public void setValue(final boolean b) {
        this.value = (b ? 1.0 : 0.0);
        this.isnull = false;
    }
    
    public int typePrecedence() {
        return 90;
    }
    
    public BooleanDataValue equals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getDouble() == dataValueDescriptor2.getDouble());
    }
    
    public BooleanDataValue notEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getDouble() != dataValueDescriptor2.getDouble());
    }
    
    public BooleanDataValue lessThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getDouble() < dataValueDescriptor2.getDouble());
    }
    
    public BooleanDataValue greaterThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getDouble() > dataValueDescriptor2.getDouble());
    }
    
    public BooleanDataValue lessOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getDouble() <= dataValueDescriptor2.getDouble());
    }
    
    public BooleanDataValue greaterOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getDouble() >= dataValueDescriptor2.getDouble());
    }
    
    public NumberDataValue plus(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLDouble();
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
            numberDataValue3 = new SQLDouble();
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
            numberDataValue3 = new SQLDouble();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final double double1 = numberDataValue.getDouble();
        final double double2 = numberDataValue2.getDouble();
        final double value = double1 * double2;
        if (value == 0.0 && double1 != 0.0 && double2 != 0.0) {
            throw StandardException.newException("22003", "DOUBLE");
        }
        numberDataValue3.setValue(value);
        return numberDataValue3;
    }
    
    public NumberDataValue divide(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = new SQLDouble();
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
            throw StandardException.newException("22003", "DOUBLE");
        }
        numberDataValue3.setValue(n);
        return numberDataValue3;
    }
    
    public NumberDataValue minus(NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLDouble();
        }
        if (this.isNull()) {
            numberDataValue.setToNull();
            return numberDataValue;
        }
        numberDataValue.setValue(-this.getDouble());
        return numberDataValue;
    }
    
    protected boolean isNegative() {
        return !this.isNull() && this.value < 0.0;
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return Double.toString(this.value);
    }
    
    public int hashCode() {
        long doubleToLongBits = (long)this.value;
        if (doubleToLongBits != this.value) {
            doubleToLongBits = Double.doubleToLongBits(this.value);
        }
        return (int)(doubleToLongBits ^ doubleToLongBits >> 32);
    }
    
    public int estimateMemoryUsage() {
        return SQLDouble.BASE_MEMORY_USAGE;
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLDouble.class);
    }
}
