// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.Column;

public interface DatastoreMapping
{
    boolean isNullable();
    
    Column getColumn();
    
    JavaTypeMapping getJavaTypeMapping();
    
    boolean isDecimalBased();
    
    boolean isIntegerBased();
    
    boolean isStringBased();
    
    boolean isBitBased();
    
    boolean isBooleanBased();
    
    void setBoolean(final PreparedStatement p0, final int p1, final boolean p2);
    
    void setChar(final PreparedStatement p0, final int p1, final char p2);
    
    void setByte(final PreparedStatement p0, final int p1, final byte p2);
    
    void setShort(final PreparedStatement p0, final int p1, final short p2);
    
    void setInt(final PreparedStatement p0, final int p1, final int p2);
    
    void setLong(final PreparedStatement p0, final int p1, final long p2);
    
    void setFloat(final PreparedStatement p0, final int p1, final float p2);
    
    void setDouble(final PreparedStatement p0, final int p1, final double p2);
    
    void setString(final PreparedStatement p0, final int p1, final String p2);
    
    void setObject(final PreparedStatement p0, final int p1, final Object p2);
    
    boolean getBoolean(final ResultSet p0, final int p1);
    
    char getChar(final ResultSet p0, final int p1);
    
    byte getByte(final ResultSet p0, final int p1);
    
    short getShort(final ResultSet p0, final int p1);
    
    int getInt(final ResultSet p0, final int p1);
    
    long getLong(final ResultSet p0, final int p1);
    
    float getFloat(final ResultSet p0, final int p1);
    
    double getDouble(final ResultSet p0, final int p1);
    
    String getString(final ResultSet p0, final int p1);
    
    Object getObject(final ResultSet p0, final int p1);
}
