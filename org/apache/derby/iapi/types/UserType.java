// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.services.loader.ClassInspector;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.Calendar;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.cache.ClassSize;

public class UserType extends DataType implements UserDataValue
{
    private Object value;
    private static final int BASE_MEMORY_USAGE;
    
    public int estimateMemoryUsage() {
        int base_MEMORY_USAGE = UserType.BASE_MEMORY_USAGE;
        if (null != this.value) {
            base_MEMORY_USAGE += ClassSize.estimateAndCatalogBase(this.value.getClass());
        }
        return base_MEMORY_USAGE;
    }
    
    public String getString() {
        if (!this.isNull()) {
            return this.value.toString();
        }
        return null;
    }
    
    public boolean getBoolean() throws StandardException {
        if (!this.isNull() && this.value instanceof Boolean) {
            return (boolean)this.value;
        }
        return super.getBoolean();
    }
    
    public byte getByte() throws StandardException {
        if (!this.isNull() && this.value instanceof Number) {
            return ((Number)this.value).byteValue();
        }
        return super.getByte();
    }
    
    public short getShort() throws StandardException {
        if (!this.isNull() && this.value instanceof Number) {
            return ((Number)this.value).shortValue();
        }
        return super.getShort();
    }
    
    public int getInt() throws StandardException {
        if (!this.isNull() && this.value instanceof Number) {
            return ((Number)this.value).intValue();
        }
        return super.getInt();
    }
    
    public long getLong() throws StandardException {
        if (!this.isNull() && this.value instanceof Number) {
            return ((Number)this.value).longValue();
        }
        return super.getLong();
    }
    
    public float getFloat() throws StandardException {
        if (!this.isNull() && this.value instanceof Number) {
            return ((Number)this.value).floatValue();
        }
        return super.getFloat();
    }
    
    public double getDouble() throws StandardException {
        if (!this.isNull() && this.value instanceof Number) {
            return ((Number)this.value).doubleValue();
        }
        return super.getDouble();
    }
    
    public byte[] getBytes() throws StandardException {
        if (!this.isNull() && this.value instanceof byte[]) {
            return (byte[])this.value;
        }
        return super.getBytes();
    }
    
    public Date getDate(final Calendar calendar) throws StandardException {
        if (!this.isNull()) {
            if (this.value instanceof Date) {
                return (Date)this.value;
            }
            if (this.value instanceof Timestamp) {
                return new SQLTimestamp((Timestamp)this.value).getDate(calendar);
            }
        }
        return super.getDate(calendar);
    }
    
    public Time getTime(final Calendar calendar) throws StandardException {
        if (!this.isNull()) {
            if (this.value instanceof Time) {
                return (Time)this.value;
            }
            if (this.value instanceof Timestamp) {
                return new SQLTimestamp((Timestamp)this.value).getTime(calendar);
            }
        }
        return super.getTime(calendar);
    }
    
    public Timestamp getTimestamp(final Calendar calendar) throws StandardException {
        if (!this.isNull()) {
            if (this.value instanceof Timestamp) {
                return (Timestamp)this.value;
            }
            if (this.value instanceof Date) {
                return new SQLDate((Date)this.value).getTimestamp(calendar);
            }
            if (this.value instanceof Time) {
                return new SQLTime((Time)this.value).getTimestamp(calendar);
            }
        }
        return super.getTimestamp(calendar);
    }
    
    void setObject(final Object value) {
        this.setValue(value);
    }
    
    public Object getObject() {
        return this.value;
    }
    
    public int getLength() {
        return -1;
    }
    
    public String getTypeName() {
        return this.isNull() ? "JAVA_OBJECT" : ClassInspector.readableClassName(this.value.getClass());
    }
    
    String getTypeName(final String s) {
        return s;
    }
    
    public int getTypeFormatId() {
        return 266;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.value);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.value = objectInput.readObject();
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        return new UserType(this.value);
    }
    
    public DataValueDescriptor getNewNull() {
        return new UserType();
    }
    
    public void restoreToNull() {
        this.value = null;
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException {
        this.value = set.getObject(n);
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.typePrecedence() < dataValueDescriptor.typePrecedence()) {
            return -dataValueDescriptor.compare(this);
        }
        final boolean null = this.isNull();
        final boolean null2 = dataValueDescriptor.isNull();
        if (!null && !null2) {
            int compareTo;
            try {
                compareTo = ((Comparable)this.value).compareTo(dataValueDescriptor.getObject());
            }
            catch (ClassCastException ex) {
                throw StandardException.newException("XCL15.S", this.getTypeName(), ClassInspector.readableClassName(dataValueDescriptor.getObject().getClass()));
            }
            if (compareTo < 0) {
                compareTo = -1;
            }
            else if (compareTo > 0) {
                compareTo = 1;
            }
            return compareTo;
        }
        if (!null) {
            return -1;
        }
        if (!null2) {
            return 1;
        }
        return 0;
    }
    
    public boolean compare(final int n, final DataValueDescriptor dataValueDescriptor, final boolean b, final boolean b2) throws StandardException {
        if (!b && (this.isNull() || dataValueDescriptor.isNull())) {
            return b2;
        }
        if (n == 2 && !this.isNull() && !dataValueDescriptor.isNull()) {
            final Object object = this.getObject();
            if (!(object instanceof Comparable)) {
                return object.equals(dataValueDescriptor.getObject());
            }
        }
        return super.compare(n, dataValueDescriptor, b, b2);
    }
    
    public UserType() {
    }
    
    public UserType(final Object value) {
        this.value = value;
    }
    
    public void setValue(final Object value) {
        this.value = value;
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        this.setValue(dataValueDescriptor.getObject());
    }
    
    public void setBigDecimal(final Number value) {
        this.setValue(value);
    }
    
    public void setValue(final String value) {
        if (value == null) {
            this.value = null;
        }
        else {
            this.value = value;
        }
    }
    
    public BooleanDataValue equals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.compare(2, dataValueDescriptor2, true, false));
    }
    
    public BooleanDataValue notEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, !dataValueDescriptor.compare(2, dataValueDescriptor2, true, false));
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return this.value.toString();
    }
    
    public int hashCode() {
        if (this.isNull()) {
            return 0;
        }
        return this.value.hashCode();
    }
    
    public int typePrecedence() {
        return 1000;
    }
    
    public final boolean isNull() {
        return this.value == null;
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(UserType.class);
    }
}
