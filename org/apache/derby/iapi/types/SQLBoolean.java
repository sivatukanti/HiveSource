// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.cache.ClassSize;
import org.apache.derby.iapi.util.StringUtil;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.derby.iapi.error.StandardException;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public final class SQLBoolean extends DataType implements BooleanDataValue
{
    static final int BOOLEAN_LENGTH = 1;
    private static final SQLBoolean BOOLEAN_TRUE;
    private static final SQLBoolean BOOLEAN_FALSE;
    static final SQLBoolean UNKNOWN;
    private static final int BASE_MEMORY_USAGE;
    private boolean value;
    private boolean isnull;
    private boolean immutable;
    
    public boolean isNull() {
        return this.isnull;
    }
    
    public boolean getBoolean() {
        return this.value;
    }
    
    private static int makeInt(final boolean b) {
        return b ? 1 : 0;
    }
    
    public byte getByte() {
        return (byte)makeInt(this.value);
    }
    
    public short getShort() {
        return (short)makeInt(this.value);
    }
    
    public int getInt() {
        return makeInt(this.value);
    }
    
    public long getLong() {
        return makeInt(this.value);
    }
    
    public float getFloat() {
        return (float)makeInt(this.value);
    }
    
    public double getDouble() {
        return makeInt(this.value);
    }
    
    public int typeToBigDecimal() {
        return -5;
    }
    
    public String getString() {
        if (this.isNull()) {
            return null;
        }
        if (this.value) {
            return "true";
        }
        return "false";
    }
    
    public Object getObject() {
        if (this.isNull()) {
            return null;
        }
        return new Boolean(this.value);
    }
    
    public int getLength() {
        return 1;
    }
    
    public String getTypeName() {
        return "BOOLEAN";
    }
    
    public DataValueDescriptor recycle() {
        if (this.immutable) {
            return new SQLBoolean();
        }
        return super.recycle();
    }
    
    public int getTypeFormatId() {
        return 77;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeBoolean(this.value);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.value = objectInput.readBoolean();
        this.isnull = false;
    }
    
    public void restoreToNull() {
        this.value = false;
        this.isnull = true;
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.typePrecedence() < dataValueDescriptor.typePrecedence()) {
            return -dataValueDescriptor.compare(this);
        }
        final boolean null = this.isNull();
        final boolean null2 = dataValueDescriptor.isNull();
        if (null || null2) {
            if (!null) {
                return -1;
            }
            if (!null2) {
                return 1;
            }
            return 0;
        }
        else {
            final boolean boolean1 = this.getBoolean();
            final boolean boolean2 = dataValueDescriptor.getBoolean();
            if (boolean1 == boolean2) {
                return 0;
            }
            if (boolean1 && !boolean2) {
                return 1;
            }
            return -1;
        }
    }
    
    public boolean compare(final int n, final DataValueDescriptor dataValueDescriptor, final boolean b, final boolean b2) throws StandardException {
        if (!b && (this.isNull() || dataValueDescriptor.isNull())) {
            return b2;
        }
        return super.compare(n, dataValueDescriptor, b, b2);
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        return new SQLBoolean(this.value, this.isnull);
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLBoolean();
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException {
        this.value = set.getBoolean(n);
        this.isnull = (b && set.wasNull());
    }
    
    public final void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException {
        if (this.isNull()) {
            preparedStatement.setNull(n, -7);
            return;
        }
        preparedStatement.setBoolean(n, this.value);
    }
    
    public SQLBoolean() {
        this.isnull = true;
    }
    
    public SQLBoolean(final boolean value) {
        this.value = value;
    }
    
    public SQLBoolean(final Boolean b) {
        final boolean isnull = b == null;
        this.isnull = isnull;
        if (!isnull) {
            this.value = b;
        }
    }
    
    private SQLBoolean(final boolean value, final boolean isnull) {
        this.value = value;
        this.isnull = isnull;
    }
    
    public void setValue(final boolean value) {
        this.value = value;
        this.isnull = false;
    }
    
    public void setValue(final Boolean b) {
        if (b == null) {
            this.value = false;
            this.isnull = true;
        }
        else {
            this.value = b;
            this.isnull = false;
        }
    }
    
    public void setValue(final byte b) {
        this.value = (b != 0);
        this.isnull = false;
    }
    
    public void setValue(final short n) {
        this.value = (n != 0);
        this.isnull = false;
    }
    
    public void setValue(final int n) {
        this.value = (n != 0);
        this.isnull = false;
    }
    
    public void setValue(final long n) {
        this.value = (n != 0L);
        this.isnull = false;
    }
    
    public void setValue(final float n) {
        this.value = (n != 0.0f);
        this.isnull = false;
    }
    
    public void setValue(final double n) {
        this.value = (n != 0.0);
        this.isnull = false;
    }
    
    public void setBigDecimal(final Number bigDecimal) throws StandardException {
        if (bigDecimal == null) {
            this.value = false;
            this.isnull = true;
        }
        else {
            final DataValueDescriptor newNull = NumberDataType.ZERO_DECIMAL.getNewNull();
            newNull.setBigDecimal(bigDecimal);
            this.value = (NumberDataType.ZERO_DECIMAL.compare(newNull) != 0);
            this.isnull = false;
        }
    }
    
    public void setValue(final String s) throws StandardException {
        if (s == null) {
            this.value = false;
            this.isnull = true;
        }
        else {
            final String sqlToUpperCase = StringUtil.SQLToUpperCase(s.trim());
            if (sqlToUpperCase.equals("TRUE")) {
                this.value = true;
                this.isnull = false;
            }
            else if (sqlToUpperCase.equals("FALSE")) {
                this.value = false;
                this.isnull = false;
            }
            else {
                if (!sqlToUpperCase.equals("UNKNOWN")) {
                    throw this.invalidFormat();
                }
                this.value = false;
                this.isnull = true;
            }
        }
    }
    
    void setObject(final Object o) {
        this.setValue((Boolean)o);
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor instanceof SQLChar) {
            this.setValue(dataValueDescriptor.getString());
        }
        else {
            if (!(dataValueDescriptor instanceof SQLBoolean)) {
                throw StandardException.newException("XCL12.S", dataValueDescriptor.getTypeName(), this.getTypeName());
            }
            this.setValue(dataValueDescriptor.getBoolean());
        }
    }
    
    public BooleanDataValue equals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getBoolean() == dataValueDescriptor2.getBoolean());
    }
    
    public BooleanDataValue notEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.getBoolean() != dataValueDescriptor2.getBoolean());
    }
    
    public BooleanDataValue lessThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        final boolean boolean1 = dataValueDescriptor.getBoolean();
        final boolean boolean2 = dataValueDescriptor2.getBoolean();
        return truthValue(dataValueDescriptor, dataValueDescriptor2, !boolean1 && boolean2);
    }
    
    public BooleanDataValue greaterThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        final boolean boolean1 = dataValueDescriptor.getBoolean();
        final boolean boolean2 = dataValueDescriptor2.getBoolean();
        return truthValue(dataValueDescriptor, dataValueDescriptor2, boolean1 && !boolean2);
    }
    
    public BooleanDataValue lessOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        final boolean boolean1 = dataValueDescriptor.getBoolean();
        final boolean boolean2 = dataValueDescriptor2.getBoolean();
        return truthValue(dataValueDescriptor, dataValueDescriptor2, !boolean1 || boolean2);
    }
    
    public BooleanDataValue greaterOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        final boolean boolean1 = dataValueDescriptor.getBoolean();
        final boolean boolean2 = dataValueDescriptor2.getBoolean();
        return truthValue(dataValueDescriptor, dataValueDescriptor2, boolean1 || !boolean2);
    }
    
    public BooleanDataValue and(final BooleanDataValue booleanDataValue) {
        if (this.equals(false) || booleanDataValue.equals(false)) {
            return SQLBoolean.BOOLEAN_FALSE;
        }
        return truthValue(this, booleanDataValue, this.getBoolean() && booleanDataValue.getBoolean());
    }
    
    public BooleanDataValue or(final BooleanDataValue booleanDataValue) {
        if (this.equals(true) || booleanDataValue.equals(true)) {
            return SQLBoolean.BOOLEAN_TRUE;
        }
        return truthValue(this, booleanDataValue, this.getBoolean() || booleanDataValue.getBoolean());
    }
    
    public BooleanDataValue is(final BooleanDataValue booleanDataValue) {
        if (this.equals(true) && booleanDataValue.equals(true)) {
            return SQLBoolean.BOOLEAN_TRUE;
        }
        if (this.equals(false) && booleanDataValue.equals(false)) {
            return SQLBoolean.BOOLEAN_TRUE;
        }
        if (this.isNull() && booleanDataValue.isNull()) {
            return SQLBoolean.BOOLEAN_TRUE;
        }
        return SQLBoolean.BOOLEAN_FALSE;
    }
    
    public BooleanDataValue isNot(final BooleanDataValue booleanDataValue) {
        if (this.is(booleanDataValue).equals(true)) {
            return SQLBoolean.BOOLEAN_FALSE;
        }
        return SQLBoolean.BOOLEAN_TRUE;
    }
    
    public BooleanDataValue throwExceptionIfFalse(final String s, final String s2, final String s3) throws StandardException {
        if (!this.isNull() && !this.value) {
            throw StandardException.newException(s, s2, s3);
        }
        return this;
    }
    
    public int typePrecedence() {
        return 130;
    }
    
    public static SQLBoolean truthValue(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2, final boolean b) {
        if (dataValueDescriptor.isNull() || dataValueDescriptor2.isNull()) {
            return unknownTruthValue();
        }
        if (b) {
            return SQLBoolean.BOOLEAN_TRUE;
        }
        return SQLBoolean.BOOLEAN_FALSE;
    }
    
    public static SQLBoolean truthValue(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2, final Boolean b) {
        if (dataValueDescriptor.isNull() || dataValueDescriptor2.isNull() || b == null) {
            return unknownTruthValue();
        }
        if (b == Boolean.TRUE) {
            return SQLBoolean.BOOLEAN_TRUE;
        }
        return SQLBoolean.BOOLEAN_FALSE;
    }
    
    public static SQLBoolean truthValue(final boolean b) {
        if (b) {
            return SQLBoolean.BOOLEAN_TRUE;
        }
        return SQLBoolean.BOOLEAN_FALSE;
    }
    
    public static SQLBoolean unknownTruthValue() {
        return SQLBoolean.UNKNOWN;
    }
    
    public boolean equals(final boolean b) {
        return !this.isNull() && this.value == b;
    }
    
    public BooleanDataValue getImmutable() {
        if (this.isNull()) {
            return SQLBoolean.UNKNOWN;
        }
        return this.value ? SQLBoolean.BOOLEAN_TRUE : SQLBoolean.BOOLEAN_FALSE;
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        if (this.value) {
            return "true";
        }
        return "false";
    }
    
    public int hashCode() {
        if (this.isNull()) {
            return -1;
        }
        return this.value ? 1 : 0;
    }
    
    public int estimateMemoryUsage() {
        return SQLBoolean.BASE_MEMORY_USAGE;
    }
    
    static {
        BOOLEAN_TRUE = new SQLBoolean(true);
        BOOLEAN_FALSE = new SQLBoolean(false);
        UNKNOWN = new SQLBoolean();
        SQLBoolean.BOOLEAN_TRUE.immutable = true;
        SQLBoolean.BOOLEAN_FALSE.immutable = true;
        SQLBoolean.UNKNOWN.immutable = true;
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLBoolean.class);
    }
}
