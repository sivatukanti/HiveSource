// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.SQLException;

public abstract class VTITemplate extends VTITemplateBase
{
    public abstract boolean next() throws SQLException;
    
    public abstract void close() throws SQLException;
    
    public String getString(final String s) throws SQLException {
        return this.getString(this.findColumn(s));
    }
    
    public boolean getBoolean(final String s) throws SQLException {
        return this.getBoolean(this.findColumn(s));
    }
    
    public byte getByte(final String s) throws SQLException {
        return this.getByte(this.findColumn(s));
    }
    
    public short getShort(final String s) throws SQLException {
        return this.getShort(this.findColumn(s));
    }
    
    public int getInt(final String s) throws SQLException {
        return this.getInt(this.findColumn(s));
    }
    
    public long getLong(final String s) throws SQLException {
        return this.getLong(this.findColumn(s));
    }
    
    public float getFloat(final String s) throws SQLException {
        return this.getFloat(this.findColumn(s));
    }
    
    public double getDouble(final String s) throws SQLException {
        return this.getDouble(this.findColumn(s));
    }
    
    public BigDecimal getBigDecimal(final String s, final int n) throws SQLException {
        return this.getBigDecimal(this.findColumn(s), n);
    }
    
    public byte[] getBytes(final String s) throws SQLException {
        return this.getBytes(this.findColumn(s));
    }
    
    public Date getDate(final String s) throws SQLException {
        return this.getDate(this.findColumn(s));
    }
    
    public Time getTime(final String s) throws SQLException {
        return this.getTime(this.findColumn(s));
    }
    
    public Timestamp getTimestamp(final String s) throws SQLException {
        return this.getTimestamp(this.findColumn(s));
    }
    
    public Object getObject(final String s) throws SQLException {
        return this.getObject(this.findColumn(s));
    }
    
    public BigDecimal getBigDecimal(final String s) throws SQLException {
        return this.getBigDecimal(this.findColumn(s));
    }
}
