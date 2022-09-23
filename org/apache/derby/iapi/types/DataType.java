// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Clob;
import java.sql.Blob;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.util.Calendar;
import org.apache.derby.iapi.error.StandardException;

public abstract class DataType implements DataValueDescriptor, Comparable
{
    public boolean getBoolean() throws StandardException {
        throw this.dataTypeConversion("boolean");
    }
    
    public byte getByte() throws StandardException {
        throw this.dataTypeConversion("byte");
    }
    
    public short getShort() throws StandardException {
        throw this.dataTypeConversion("short");
    }
    
    public int getInt() throws StandardException {
        throw this.dataTypeConversion("int");
    }
    
    public long getLong() throws StandardException {
        throw this.dataTypeConversion("long");
    }
    
    public float getFloat() throws StandardException {
        throw this.dataTypeConversion("float");
    }
    
    public double getDouble() throws StandardException {
        throw this.dataTypeConversion("double");
    }
    
    public int typeToBigDecimal() throws StandardException {
        throw this.dataTypeConversion("java.math.BigDecimal");
    }
    
    public byte[] getBytes() throws StandardException {
        throw this.dataTypeConversion("byte[]");
    }
    
    public Date getDate(final Calendar calendar) throws StandardException {
        throw this.dataTypeConversion("java.sql.Date");
    }
    
    public Time getTime(final Calendar calendar) throws StandardException {
        throw this.dataTypeConversion("java.sql.Time");
    }
    
    public Timestamp getTimestamp(final Calendar calendar) throws StandardException {
        throw this.dataTypeConversion("java.sql.Timestamp");
    }
    
    public InputStream getStream() throws StandardException {
        throw this.dataTypeConversion("InputStream");
    }
    
    public boolean hasStream() {
        return false;
    }
    
    public String getTraceString() throws StandardException {
        return this.getString();
    }
    
    public DataValueDescriptor recycle() {
        this.restoreToNull();
        return this;
    }
    
    public void readExternalFromArray(final ArrayInputStream arrayInputStream) throws IOException, ClassNotFoundException {
        this.readExternal(arrayInputStream);
    }
    
    public final BooleanDataValue isNullOp() {
        return SQLBoolean.truthValue(this.isNull());
    }
    
    public final BooleanDataValue isNotNull() {
        return SQLBoolean.truthValue(!this.isNull());
    }
    
    public void setValue(final Time time) throws StandardException {
        this.setValue(time, null);
    }
    
    public void setValue(final Time time, final Calendar calendar) throws StandardException {
        this.throwLangSetMismatch("java.sql.Time");
    }
    
    public void setValue(final Timestamp timestamp) throws StandardException {
        this.setValue(timestamp, null);
    }
    
    public void setValue(final Timestamp timestamp, final Calendar calendar) throws StandardException {
        this.throwLangSetMismatch("java.sql.Timestamp");
    }
    
    public void setValue(final Date date) throws StandardException {
        this.setValue(date, null);
    }
    
    public void setValue(final Date date, final Calendar calendar) throws StandardException {
        this.throwLangSetMismatch("java.sql.Date");
    }
    
    public void setValue(final Object o) throws StandardException {
        this.throwLangSetMismatch("java.lang.Object");
    }
    
    public void setValue(final String s) throws StandardException {
        this.throwLangSetMismatch("java.lang.String");
    }
    
    public void setValue(final Blob blob) throws StandardException {
        this.throwLangSetMismatch("java.sql.Blob");
    }
    
    public void setValue(final Clob clob) throws StandardException {
        this.throwLangSetMismatch("java.sql.Clob");
    }
    
    public void setValue(final int n) throws StandardException {
        this.throwLangSetMismatch("int");
    }
    
    public void setValue(final double n) throws StandardException {
        this.throwLangSetMismatch("double");
    }
    
    public void setValue(final float n) throws StandardException {
        this.throwLangSetMismatch("float");
    }
    
    public void setValue(final short n) throws StandardException {
        this.throwLangSetMismatch("short");
    }
    
    public void setValue(final long n) throws StandardException {
        this.throwLangSetMismatch("long");
    }
    
    public void setValue(final byte b) throws StandardException {
        this.throwLangSetMismatch("byte");
    }
    
    public void setValue(final boolean b) throws StandardException {
        this.throwLangSetMismatch("boolean");
    }
    
    public void setValue(final byte[] array) throws StandardException {
        this.throwLangSetMismatch("byte[]");
    }
    
    public void setBigDecimal(final Number n) throws StandardException {
        this.throwLangSetMismatch("java.math.BigDecimal");
    }
    
    public final void setValue(final DataValueDescriptor from) throws StandardException {
        if (from.isNull()) {
            this.setToNull();
            return;
        }
        try {
            this.setFrom(from);
        }
        catch (StandardException ex) {
            final String messageId = ex.getMessageId();
            if ("22003".equals(messageId)) {
                throw this.outOfRange();
            }
            if ("22018".equals(messageId)) {
                throw this.invalidFormat();
            }
            throw ex;
        }
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        throw StandardException.newException("0A000.S");
    }
    
    public void setToNull() {
        this.restoreToNull();
    }
    
    public void setObjectForCast(final Object object, final boolean b, final String s) throws StandardException {
        if (object == null) {
            this.setToNull();
            return;
        }
        if (!b) {
            throw StandardException.newException("XCL12.S", object.getClass().getName(), this.getTypeName(s));
        }
        this.setObject(object);
    }
    
    void setObject(final Object o) throws StandardException {
        this.genericSetObject(o);
    }
    
    String getTypeName(final String s) {
        return this.getTypeName();
    }
    
    public Object getObject() throws StandardException {
        throw this.dataTypeConversion("java.lang.Object");
    }
    
    void genericSetObject(final Object o) throws StandardException {
        this.throwLangSetMismatch(o);
    }
    
    public DataValueDescriptor cloneHolder() {
        return this.cloneValue(false);
    }
    
    public void throwLangSetMismatch(final Object o) throws StandardException {
        this.throwLangSetMismatch(o.getClass().getName());
    }
    
    void throwLangSetMismatch(final String s) throws StandardException {
        throw StandardException.newException("XCL12.S", s, this.getTypeName());
    }
    
    public void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException, StandardException {
        preparedStatement.setObject(n, this.getObject());
    }
    
    public void setInto(final ResultSet set, final int n) throws SQLException, StandardException {
        set.updateObject(n, this.getObject());
    }
    
    public void normalize(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor value) throws StandardException {
        this.setValue(value);
    }
    
    public int typePrecedence() {
        return -1;
    }
    
    public BooleanDataValue equals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.compare(dataValueDescriptor2) == 0);
    }
    
    public BooleanDataValue notEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.compare(dataValueDescriptor2) != 0);
    }
    
    public BooleanDataValue lessThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.compare(dataValueDescriptor2) < 0);
    }
    
    public BooleanDataValue greaterThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.compare(dataValueDescriptor2) > 0);
    }
    
    public BooleanDataValue lessOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.compare(dataValueDescriptor2) <= 0);
    }
    
    public BooleanDataValue greaterOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, dataValueDescriptor.compare(dataValueDescriptor2) >= 0);
    }
    
    public boolean compare(final int n, final DataValueDescriptor dataValueDescriptor, final boolean b, final boolean b2) throws StandardException {
        if (this.typePrecedence() < dataValueDescriptor.typePrecedence()) {
            return dataValueDescriptor.compare(flip(n), this, b, b2);
        }
        final int compare = this.compare(dataValueDescriptor);
        switch (n) {
            case 1: {
                return compare < 0;
            }
            case 2: {
                return compare == 0;
            }
            case 3: {
                return compare <= 0;
            }
            case 4: {
                return compare > 0;
            }
            case 5: {
                return compare >= 0;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean compare(final int n, final DataValueDescriptor dataValueDescriptor, final boolean b, final boolean b2, final boolean b3) throws StandardException {
        if (this.typePrecedence() < dataValueDescriptor.typePrecedence()) {
            return dataValueDescriptor.compare(flip(n), this, b, b2, b3);
        }
        final int compare = this.compare(dataValueDescriptor, b2);
        switch (n) {
            case 1: {
                return compare < 0;
            }
            case 2: {
                return compare == 0;
            }
            case 3: {
                return compare <= 0;
            }
            case 4: {
                return compare > 0;
            }
            case 5: {
                return compare >= 0;
            }
            default: {
                return false;
            }
        }
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor, final boolean b) throws StandardException {
        if (!this.isNull() && !dataValueDescriptor.isNull()) {
            return this.compare(dataValueDescriptor);
        }
        if (!this.isNull()) {
            return b ? 1 : -1;
        }
        if (!dataValueDescriptor.isNull()) {
            return b ? -1 : 1;
        }
        return 0;
    }
    
    public int compareTo(final Object o) {
        final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)o;
        try {
            if (this.typePrecedence() < dataValueDescriptor.typePrecedence()) {
                return -1 * dataValueDescriptor.compare(this);
            }
            return this.compare(dataValueDescriptor);
        }
        catch (StandardException ex) {
            return 0;
        }
    }
    
    protected static int flip(final int n) {
        switch (n) {
            case 1: {
                return 4;
            }
            case 3: {
                return 5;
            }
            case 2: {
                return 2;
            }
            default: {
                return n;
            }
        }
    }
    
    public DataValueDescriptor coalesce(final DataValueDescriptor[] array, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].isNull()) {
                dataValueDescriptor.setValue(array[i]);
                return dataValueDescriptor;
            }
        }
        dataValueDescriptor.setToNull();
        return dataValueDescriptor;
    }
    
    public BooleanDataValue in(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor[] array, final boolean b) throws StandardException {
        BooleanDataValue booleanDataValue = null;
        if (dataValueDescriptor.isNull()) {
            return SQLBoolean.truthValue(dataValueDescriptor, array[0], false);
        }
        int n = 0;
        int length = array.length;
        final int typePrecedence = dataValueDescriptor.typePrecedence();
        if (b) {
            while (length - n > 2) {
                final int n2 = (length - n) / 2 + n;
                final DataValueDescriptor dataValueDescriptor2 = (typePrecedence < array[n2].typePrecedence()) ? array[n2] : dataValueDescriptor;
                booleanDataValue = dataValueDescriptor2.equals(dataValueDescriptor, array[n2]);
                if (booleanDataValue.equals(true)) {
                    return booleanDataValue;
                }
                if (dataValueDescriptor2.greaterThan(array[n2], dataValueDescriptor).equals(true)) {
                    length = n2;
                }
                else {
                    n = n2;
                }
            }
        }
        for (int i = n; i < length; ++i) {
            final DataValueDescriptor dataValueDescriptor3 = (typePrecedence < array[i].typePrecedence()) ? array[i] : dataValueDescriptor;
            booleanDataValue = dataValueDescriptor3.equals(dataValueDescriptor, array[i]);
            if (booleanDataValue.equals(true)) {
                break;
            }
            if (b && dataValueDescriptor3.greaterThan(array[i], dataValueDescriptor).equals(true)) {
                break;
            }
        }
        return booleanDataValue;
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof DataValueDescriptor)) {
            return false;
        }
        try {
            return this.compare(2, (DataValueDescriptor)o, true, false);
        }
        catch (StandardException ex) {
            return false;
        }
    }
    
    public void setValue(final InputStream inputStream, final int n) throws StandardException {
        this.throwLangSetMismatch("java.io.InputStream");
    }
    
    public void checkHostVariable(final int n) throws StandardException {
    }
    
    protected final StandardException dataTypeConversion(final String s) {
        return StandardException.newException("22005", s, this.getTypeName());
    }
    
    protected final StandardException outOfRange() {
        return StandardException.newException("22003", this.getTypeName());
    }
    
    protected final StandardException invalidFormat() {
        return StandardException.newException("22018", this.getTypeName());
    }
}
