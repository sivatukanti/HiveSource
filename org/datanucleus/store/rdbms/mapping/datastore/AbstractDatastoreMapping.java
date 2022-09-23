// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.ClassConstants;
import java.sql.ResultSet;
import org.datanucleus.exceptions.NucleusException;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.exceptions.UnsupportedDataTypeException;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.util.Localiser;

public abstract class AbstractDatastoreMapping implements DatastoreMapping
{
    protected static final Localiser LOCALISER_RDBMS;
    protected static final Localiser LOCALISER;
    protected final JavaTypeMapping mapping;
    protected final RDBMSStoreManager storeMgr;
    protected Column column;
    
    protected AbstractDatastoreMapping(final RDBMSStoreManager storeMgr, final JavaTypeMapping mapping) {
        this.mapping = mapping;
        if (mapping != null) {
            mapping.addDatastoreMapping(this);
        }
        this.storeMgr = storeMgr;
    }
    
    @Override
    public JavaTypeMapping getJavaTypeMapping() {
        return this.mapping;
    }
    
    protected DatastoreAdapter getDatastoreAdapter() {
        return this.storeMgr.getDatastoreAdapter();
    }
    
    public abstract SQLTypeInfo getTypeInfo();
    
    @Override
    public boolean isNullable() {
        return this.column == null || this.column.isNullable();
    }
    
    public boolean includeInFetchStatement() {
        return true;
    }
    
    public boolean insertValuesOnInsert() {
        return this.getInsertionInputParameter().indexOf(63) > -1;
    }
    
    public String getInsertionInputParameter() {
        return this.column.getWrapperFunction(1);
    }
    
    public String getUpdateInputParameter() {
        return this.column.getWrapperFunction(2);
    }
    
    @Override
    public Column getColumn() {
        return this.column;
    }
    
    protected void initTypeInfo() {
        final SQLTypeInfo typeInfo = this.getTypeInfo();
        if (typeInfo == null) {
            throw new UnsupportedDataTypeException(AbstractDatastoreMapping.LOCALISER_RDBMS.msg("055000", this.column));
        }
        if (this.column != null) {
            this.column.setTypeInfo(typeInfo);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractDatastoreMapping)) {
            return false;
        }
        final AbstractDatastoreMapping cm = (AbstractDatastoreMapping)obj;
        return this.getClass().equals(cm.getClass()) && this.storeMgr.equals(cm.storeMgr) && ((this.column != null) ? this.column.equals(cm.column) : (cm.column == null));
    }
    
    @Override
    public int hashCode() {
        return this.storeMgr.hashCode() ^ ((this.column == null) ? 0 : this.column.hashCode());
    }
    
    protected String failureMessage(final String method, final int position, final Exception e) {
        return AbstractDatastoreMapping.LOCALISER_RDBMS.msg("041050", this.getClass().getName() + "." + method, position, this.column, e.getMessage());
    }
    
    protected String failureMessage(final String method, final Object value, final Exception e) {
        return AbstractDatastoreMapping.LOCALISER_RDBMS.msg("041050", this.getClass().getName() + "." + method, value, this.column, e.getMessage());
    }
    
    @Override
    public void setBoolean(final PreparedStatement ps, final int exprIndex, final boolean value) {
        throw new NucleusException(this.failureMessage("setBoolean")).setFatal();
    }
    
    @Override
    public boolean getBoolean(final ResultSet resultSet, final int exprIndex) {
        throw new NucleusException(this.failureMessage("getBoolean")).setFatal();
    }
    
    @Override
    public void setChar(final PreparedStatement ps, final int exprIndex, final char value) {
        throw new NucleusException(this.failureMessage("setChar")).setFatal();
    }
    
    @Override
    public char getChar(final ResultSet resultSet, final int exprIndex) {
        throw new NucleusException(this.failureMessage("getChar")).setFatal();
    }
    
    @Override
    public void setByte(final PreparedStatement ps, final int exprIndex, final byte value) {
        throw new NucleusException(this.failureMessage("setByte")).setFatal();
    }
    
    @Override
    public byte getByte(final ResultSet resultSet, final int exprIndex) {
        throw new NucleusException(this.failureMessage("getByte")).setFatal();
    }
    
    @Override
    public void setShort(final PreparedStatement ps, final int exprIndex, final short value) {
        throw new NucleusException(this.failureMessage("setShort")).setFatal();
    }
    
    @Override
    public short getShort(final ResultSet resultSet, final int exprIndex) {
        throw new NucleusException(this.failureMessage("getShort")).setFatal();
    }
    
    @Override
    public void setInt(final PreparedStatement ps, final int exprIndex, final int value) {
        throw new NucleusException(this.failureMessage("setInt")).setFatal();
    }
    
    @Override
    public int getInt(final ResultSet resultSet, final int exprIndex) {
        throw new NucleusException(this.failureMessage("getInt")).setFatal();
    }
    
    @Override
    public void setLong(final PreparedStatement ps, final int exprIndex, final long value) {
        throw new NucleusException(this.failureMessage("setLong")).setFatal();
    }
    
    @Override
    public long getLong(final ResultSet resultSet, final int exprIndex) {
        throw new NucleusException(this.failureMessage("getLong")).setFatal();
    }
    
    @Override
    public void setFloat(final PreparedStatement ps, final int exprIndex, final float value) {
        throw new NucleusException(this.failureMessage("setFloat")).setFatal();
    }
    
    @Override
    public float getFloat(final ResultSet resultSet, final int exprIndex) {
        throw new NucleusException(this.failureMessage("getFloat")).setFatal();
    }
    
    @Override
    public void setDouble(final PreparedStatement ps, final int exprIndex, final double value) {
        throw new NucleusException(this.failureMessage("setDouble")).setFatal();
    }
    
    @Override
    public double getDouble(final ResultSet resultSet, final int exprIndex) {
        throw new NucleusException(this.failureMessage("getDouble")).setFatal();
    }
    
    @Override
    public void setString(final PreparedStatement ps, final int exprIndex, final String value) {
        throw new NucleusException(this.failureMessage("setString")).setFatal();
    }
    
    @Override
    public String getString(final ResultSet resultSet, final int exprIndex) {
        throw new NucleusException(this.failureMessage("getString")).setFatal();
    }
    
    @Override
    public void setObject(final PreparedStatement ps, final int exprIndex, final Object value) {
        throw new NucleusException(this.failureMessage("setObject")).setFatal();
    }
    
    @Override
    public Object getObject(final ResultSet resultSet, final int exprIndex) {
        throw new NucleusException(this.failureMessage("getObject")).setFatal();
    }
    
    @Override
    public boolean isDecimalBased() {
        return false;
    }
    
    @Override
    public boolean isIntegerBased() {
        return false;
    }
    
    @Override
    public boolean isStringBased() {
        return false;
    }
    
    @Override
    public boolean isBitBased() {
        return false;
    }
    
    @Override
    public boolean isBooleanBased() {
        return false;
    }
    
    protected String failureMessage(final String method) {
        return AbstractDatastoreMapping.LOCALISER_RDBMS.msg("041005", this.getClass().getName(), method, this.mapping.getMemberMetaData().getFullFieldName());
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
