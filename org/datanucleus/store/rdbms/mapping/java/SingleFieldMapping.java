// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;

public abstract class SingleFieldMapping extends JavaTypeMapping
{
    @Override
    public void initialize(final AbstractMemberMetaData fmd, final Table table, final ClassLoaderResolver clr) {
        super.initialize(fmd, table, clr);
        this.prepareDatastoreMapping();
    }
    
    protected void prepareDatastoreMapping() {
        final MappingManager mmgr = this.storeMgr.getMappingManager();
        final Column col = mmgr.createColumn(this, this.getJavaTypeForDatastoreMapping(0), 0);
        mmgr.createDatastoreMapping(this, this.mmd, 0, col);
    }
    
    public int getDefaultLength(final int index) {
        return -1;
    }
    
    public Object[] getValidValues(final int index) {
        return null;
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        if (this.getJavaType() == null) {
            return null;
        }
        return this.getJavaType().getName();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SingleFieldMapping)) {
            return false;
        }
        final SingleFieldMapping other = (SingleFieldMapping)obj;
        return this.getClass().equals(other.getClass()) && this.storeMgr.equals(other.storeMgr);
    }
    
    @Override
    public void setBoolean(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final boolean value) {
        this.getDatastoreMapping(0).setBoolean(ps, exprIndex[0], value);
    }
    
    @Override
    public boolean getBoolean(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        return this.getDatastoreMapping(0).getBoolean(resultSet, exprIndex[0]);
    }
    
    @Override
    public void setChar(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final char value) {
        this.getDatastoreMapping(0).setChar(ps, exprIndex[0], value);
    }
    
    @Override
    public char getChar(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        return this.getDatastoreMapping(0).getChar(resultSet, exprIndex[0]);
    }
    
    @Override
    public void setByte(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final byte value) {
        this.getDatastoreMapping(0).setByte(ps, exprIndex[0], value);
    }
    
    @Override
    public byte getByte(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        return this.getDatastoreMapping(0).getByte(resultSet, exprIndex[0]);
    }
    
    @Override
    public void setShort(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final short value) {
        this.getDatastoreMapping(0).setShort(ps, exprIndex[0], value);
    }
    
    @Override
    public short getShort(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        return this.getDatastoreMapping(0).getShort(resultSet, exprIndex[0]);
    }
    
    @Override
    public void setInt(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final int value) {
        this.getDatastoreMapping(0).setInt(ps, exprIndex[0], value);
    }
    
    @Override
    public int getInt(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        return this.getDatastoreMapping(0).getInt(resultSet, exprIndex[0]);
    }
    
    @Override
    public void setLong(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final long value) {
        this.getDatastoreMapping(0).setLong(ps, exprIndex[0], value);
    }
    
    @Override
    public long getLong(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        return this.getDatastoreMapping(0).getLong(resultSet, exprIndex[0]);
    }
    
    @Override
    public void setFloat(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final float value) {
        this.getDatastoreMapping(0).setFloat(ps, exprIndex[0], value);
    }
    
    @Override
    public float getFloat(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        return this.getDatastoreMapping(0).getFloat(resultSet, exprIndex[0]);
    }
    
    @Override
    public void setDouble(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final double value) {
        this.getDatastoreMapping(0).setDouble(ps, exprIndex[0], value);
    }
    
    @Override
    public double getDouble(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        return this.getDatastoreMapping(0).getDouble(resultSet, exprIndex[0]);
    }
    
    @Override
    public void setString(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final String value) {
        this.getDatastoreMapping(0).setString(ps, exprIndex[0], value);
    }
    
    @Override
    public String getString(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        return this.getDatastoreMapping(0).getString(resultSet, exprIndex[0]);
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        this.getDatastoreMapping(0).setObject(ps, exprIndex[0], value);
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return null;
        }
        return this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
    }
}
