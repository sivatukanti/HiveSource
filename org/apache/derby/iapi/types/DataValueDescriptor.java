// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.io.IOException;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.util.Calendar;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.Storable;

public interface DataValueDescriptor extends Storable, Orderable
{
    public static final int UNKNOWN_LOGICAL_LENGTH = -1;
    
    int getLength() throws StandardException;
    
    String getString() throws StandardException;
    
    String getTraceString() throws StandardException;
    
    boolean getBoolean() throws StandardException;
    
    byte getByte() throws StandardException;
    
    short getShort() throws StandardException;
    
    int getInt() throws StandardException;
    
    long getLong() throws StandardException;
    
    float getFloat() throws StandardException;
    
    double getDouble() throws StandardException;
    
    int typeToBigDecimal() throws StandardException;
    
    byte[] getBytes() throws StandardException;
    
    Date getDate(final Calendar p0) throws StandardException;
    
    Time getTime(final Calendar p0) throws StandardException;
    
    Timestamp getTimestamp(final Calendar p0) throws StandardException;
    
    Object getObject() throws StandardException;
    
    InputStream getStream() throws StandardException;
    
    boolean hasStream();
    
    DataValueDescriptor cloneHolder();
    
    DataValueDescriptor cloneValue(final boolean p0);
    
    DataValueDescriptor recycle();
    
    DataValueDescriptor getNewNull();
    
    void setValueFromResultSet(final ResultSet p0, final int p1, final boolean p2) throws StandardException, SQLException;
    
    void setInto(final PreparedStatement p0, final int p1) throws SQLException, StandardException;
    
    void setInto(final ResultSet p0, final int p1) throws SQLException, StandardException;
    
    void setValue(final int p0) throws StandardException;
    
    void setValue(final double p0) throws StandardException;
    
    void setValue(final float p0) throws StandardException;
    
    void setValue(final short p0) throws StandardException;
    
    void setValue(final long p0) throws StandardException;
    
    void setValue(final byte p0) throws StandardException;
    
    void setValue(final boolean p0) throws StandardException;
    
    void setValue(final Object p0) throws StandardException;
    
    void setValue(final byte[] p0) throws StandardException;
    
    void setBigDecimal(final Number p0) throws StandardException;
    
    void setValue(final String p0) throws StandardException;
    
    void setValue(final Blob p0) throws StandardException;
    
    void setValue(final Clob p0) throws StandardException;
    
    void setValue(final Time p0) throws StandardException;
    
    void setValue(final Time p0, final Calendar p1) throws StandardException;
    
    void setValue(final Timestamp p0) throws StandardException;
    
    void setValue(final Timestamp p0, final Calendar p1) throws StandardException;
    
    void setValue(final Date p0) throws StandardException;
    
    void setValue(final Date p0, final Calendar p1) throws StandardException;
    
    void setValue(final DataValueDescriptor p0) throws StandardException;
    
    void setToNull();
    
    void normalize(final DataTypeDescriptor p0, final DataValueDescriptor p1) throws StandardException;
    
    BooleanDataValue isNullOp();
    
    BooleanDataValue isNotNull();
    
    String getTypeName();
    
    void setObjectForCast(final Object p0, final boolean p1, final String p2) throws StandardException;
    
    void readExternalFromArray(final ArrayInputStream p0) throws IOException, ClassNotFoundException;
    
    int typePrecedence();
    
    BooleanDataValue equals(final DataValueDescriptor p0, final DataValueDescriptor p1) throws StandardException;
    
    BooleanDataValue notEquals(final DataValueDescriptor p0, final DataValueDescriptor p1) throws StandardException;
    
    BooleanDataValue lessThan(final DataValueDescriptor p0, final DataValueDescriptor p1) throws StandardException;
    
    BooleanDataValue greaterThan(final DataValueDescriptor p0, final DataValueDescriptor p1) throws StandardException;
    
    BooleanDataValue lessOrEquals(final DataValueDescriptor p0, final DataValueDescriptor p1) throws StandardException;
    
    BooleanDataValue greaterOrEquals(final DataValueDescriptor p0, final DataValueDescriptor p1) throws StandardException;
    
    DataValueDescriptor coalesce(final DataValueDescriptor[] p0, final DataValueDescriptor p1) throws StandardException;
    
    BooleanDataValue in(final DataValueDescriptor p0, final DataValueDescriptor[] p1, final boolean p2) throws StandardException;
    
    int compare(final DataValueDescriptor p0) throws StandardException;
    
    int compare(final DataValueDescriptor p0, final boolean p1) throws StandardException;
    
    boolean compare(final int p0, final DataValueDescriptor p1, final boolean p2, final boolean p3) throws StandardException;
    
    boolean compare(final int p0, final DataValueDescriptor p1, final boolean p2, final boolean p3, final boolean p4) throws StandardException;
    
    void setValue(final InputStream p0, final int p1) throws StandardException;
    
    void checkHostVariable(final int p0) throws StandardException;
    
    int estimateMemoryUsage();
}
