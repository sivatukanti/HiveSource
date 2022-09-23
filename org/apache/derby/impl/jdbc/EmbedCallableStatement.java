// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.Clob;
import java.sql.Blob;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.util.Calendar;
import org.apache.derby.iapi.types.SQLDecimal;
import java.math.BigDecimal;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.ParameterValueSet;
import java.sql.ResultSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import java.sql.SQLException;
import java.sql.CallableStatement;

public class EmbedCallableStatement extends EmbedPreparedStatement implements CallableStatement
{
    private boolean hasReturnOutputParameter;
    protected boolean wasNull;
    
    public EmbedCallableStatement(final EmbedConnection embedConnection, final String s, final int n, final int n2, final int n3) throws SQLException {
        super(embedConnection, s, false, n, n2, n3, 2, null, null);
        this.hasReturnOutputParameter = this.getParms().hasReturnOutputParameter();
    }
    
    protected void checkRequiresCallableStatement(final Activation activation) {
    }
    
    protected final boolean executeStatement(final Activation activation, final boolean b, final boolean b2) throws SQLException {
        this.checkExecStatus();
        synchronized (this.getConnectionSynchronization()) {
            this.wasNull = false;
            try {
                this.getParms().validate();
            }
            catch (StandardException ex) {
                throw EmbedResultSet.noStateChangeException(ex);
            }
            boolean executeStatement = super.executeStatement(activation, b, b2 && !this.hasReturnOutputParameter);
            final ParameterValueSet parms = this.getParms();
            if (this.hasReturnOutputParameter) {
                this.results.next();
                try {
                    parms.getReturnValueForSet().setValueFromResultSet(this.results, 1, true);
                }
                catch (StandardException ex2) {
                    throw EmbedResultSet.noStateChangeException(ex2);
                }
                finally {
                    this.results.close();
                    this.results = null;
                }
                executeStatement = false;
            }
            return executeStatement;
        }
    }
    
    public final void registerOutParameter(final int n, final int n2) throws SQLException {
        this.checkStatus();
        try {
            this.getParms().registerOutParameter(n - 1, n2, -1);
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public final void registerOutParameter(final int n, final int n2, final int value) throws SQLException {
        this.checkStatus();
        if (value < 0) {
            throw this.newSQLException("XJ044.S", new Integer(value));
        }
        try {
            this.getParms().registerOutParameter(n - 1, n2, value);
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public void registerOutParameter(final int n, final int n2, final String s) throws SQLException {
        this.registerOutParameter(n, n2);
    }
    
    public boolean wasNull() throws SQLException {
        this.checkStatus();
        return this.wasNull;
    }
    
    public String getString(final int n) throws SQLException {
        this.checkStatus();
        try {
            final String string = this.getParms().getParameterForGet(n - 1).getString();
            this.wasNull = (string == null);
            return string;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public boolean getBoolean(final int n) throws SQLException {
        this.checkStatus();
        try {
            final DataValueDescriptor parameterForGet = this.getParms().getParameterForGet(n - 1);
            final boolean boolean1 = parameterForGet.getBoolean();
            this.wasNull = (!boolean1 && parameterForGet.isNull());
            return boolean1;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public byte getByte(final int n) throws SQLException {
        this.checkStatus();
        try {
            final DataValueDescriptor parameterForGet = this.getParms().getParameterForGet(n - 1);
            final byte byte1 = parameterForGet.getByte();
            this.wasNull = (byte1 == 0 && parameterForGet.isNull());
            return byte1;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public short getShort(final int n) throws SQLException {
        this.checkStatus();
        try {
            final DataValueDescriptor parameterForGet = this.getParms().getParameterForGet(n - 1);
            final short short1 = parameterForGet.getShort();
            this.wasNull = (short1 == 0 && parameterForGet.isNull());
            return short1;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public int getInt(final int n) throws SQLException {
        this.checkStatus();
        try {
            final DataValueDescriptor parameterForGet = this.getParms().getParameterForGet(n - 1);
            final int int1 = parameterForGet.getInt();
            this.wasNull = (int1 == 0 && parameterForGet.isNull());
            return int1;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public long getLong(final int n) throws SQLException {
        this.checkStatus();
        try {
            final DataValueDescriptor parameterForGet = this.getParms().getParameterForGet(n - 1);
            final long long1 = parameterForGet.getLong();
            this.wasNull = (long1 == 0L && parameterForGet.isNull());
            return long1;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public final BigDecimal getBigDecimal(final int n) throws SQLException {
        this.checkStatus();
        try {
            final DataValueDescriptor parameterForGet = this.getParms().getParameterForGet(n - 1);
            final boolean null = parameterForGet.isNull();
            this.wasNull = null;
            if (null) {
                return null;
            }
            return SQLDecimal.getBigDecimal(parameterForGet);
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public final BigDecimal getBigDecimal(final int n, final int newScale) throws SQLException {
        BigDecimal bigDecimal = this.getBigDecimal(n);
        if (bigDecimal != null) {
            bigDecimal = bigDecimal.setScale(newScale, 5);
        }
        return bigDecimal;
    }
    
    public float getFloat(final int n) throws SQLException {
        this.checkStatus();
        try {
            final DataValueDescriptor parameterForGet = this.getParms().getParameterForGet(n - 1);
            final float float1 = parameterForGet.getFloat();
            this.wasNull = (float1 == 0.0 && parameterForGet.isNull());
            return float1;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public double getDouble(final int n) throws SQLException {
        this.checkStatus();
        try {
            final DataValueDescriptor parameterForGet = this.getParms().getParameterForGet(n - 1);
            final double double1 = parameterForGet.getDouble();
            this.wasNull = (double1 == 0.0 && parameterForGet.isNull());
            return double1;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public byte[] getBytes(final int n) throws SQLException {
        this.checkStatus();
        try {
            final byte[] bytes = this.getParms().getParameterForGet(n - 1).getBytes();
            this.wasNull = (bytes == null);
            return bytes;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public Date getDate(final int n, final Calendar calendar) throws SQLException {
        this.checkStatus();
        try {
            final Date date = this.getParms().getParameterForGet(n - 1).getDate(calendar);
            this.wasNull = (date == null);
            return date;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public Time getTime(final int n, final Calendar calendar) throws SQLException {
        this.checkStatus();
        try {
            final Time time = this.getParms().getParameterForGet(n - 1).getTime(calendar);
            this.wasNull = (time == null);
            return time;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public Timestamp getTimestamp(final int n, final Calendar calendar) throws SQLException {
        this.checkStatus();
        try {
            final Timestamp timestamp = this.getParms().getParameterForGet(n - 1).getTimestamp(calendar);
            this.wasNull = (timestamp == null);
            return timestamp;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public Date getDate(final int n) throws SQLException {
        return this.getDate(n, this.getCal());
    }
    
    public Time getTime(final int n) throws SQLException {
        return this.getTime(n, this.getCal());
    }
    
    public Timestamp getTimestamp(final int n) throws SQLException {
        return this.getTimestamp(n, this.getCal());
    }
    
    public final Object getObject(final int n) throws SQLException {
        this.checkStatus();
        try {
            final Object object = this.getParms().getParameterForGet(n - 1).getObject();
            this.wasNull = (object == null);
            return object;
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    public URL getURL(final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setURL(final String s, final URL url) throws SQLException {
        throw Util.notImplemented();
    }
    
    public URL getURL(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Blob getBlob(final int n) throws SQLException {
        final Object object = this.getObject(n);
        if (object == null || object instanceof Blob) {
            return (Blob)object;
        }
        throw this.newSQLException("22005", Blob.class.getName(), Util.typeName(this.getParameterJDBCType(n)));
    }
    
    public Clob getClob(final int n) throws SQLException {
        final Object object = this.getObject(n);
        if (object == null || object instanceof Clob) {
            return (Clob)object;
        }
        throw this.newSQLException("22005", Clob.class.getName(), Util.typeName(this.getParameterJDBCType(n)));
    }
    
    public void addBatch() throws SQLException {
        this.checkStatus();
        final ParameterValueSet parms = this.getParms();
        for (int parameterCount = parms.getParameterCount(), i = 1; i <= parameterCount; ++i) {
            switch (parms.getParameterMode(i)) {
                case 2:
                case 4: {
                    throw this.newSQLException("XJ04C.S");
                }
            }
        }
        super.addBatch();
    }
}
