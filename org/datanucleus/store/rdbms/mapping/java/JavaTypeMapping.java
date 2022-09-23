// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.ClassConstants;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.state.ObjectProvider;
import java.sql.ResultSet;
import org.datanucleus.exceptions.NucleusException;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.NucleusContext;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.util.Localiser;

public abstract class JavaTypeMapping
{
    protected static final Localiser LOCALISER;
    protected static final Localiser LOCALISER_RDBMS;
    protected AbstractMemberMetaData mmd;
    protected int roleForMember;
    protected DatastoreMapping[] datastoreMappings;
    protected Table table;
    protected RDBMSStoreManager storeMgr;
    protected String type;
    protected JavaTypeMapping referenceMapping;
    protected int absFieldNumber;
    
    protected JavaTypeMapping() {
        this.roleForMember = 0;
        this.datastoreMappings = new DatastoreMapping[0];
        this.absFieldNumber = -1;
    }
    
    public void initialize(final RDBMSStoreManager storeMgr, final String type) {
        this.storeMgr = storeMgr;
        this.type = type;
    }
    
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr) {
        this.storeMgr = table.getStoreManager();
        this.mmd = mmd;
        this.table = table;
        if (this.roleForMember == 4) {
            this.type = mmd.getArray().getElementType();
        }
        else if (this.roleForMember == 3) {
            this.type = mmd.getCollection().getElementType();
        }
        else if (this.roleForMember == 5) {
            this.type = mmd.getMap().getKeyType();
        }
        else if (this.roleForMember == 6) {
            this.type = mmd.getMap().getValueType();
        }
        else {
            this.type = mmd.getType().getName();
        }
    }
    
    @Override
    public int hashCode() {
        return (this.mmd == null || this.table == null) ? super.hashCode() : (this.mmd.hashCode() ^ this.table.hashCode());
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }
        final AbstractContainerMapping other = (AbstractContainerMapping)obj;
        return this.mmd.equals(other.mmd) && this.table.equals(other.table);
    }
    
    public void setMemberMetaData(final AbstractMemberMetaData mmd) {
        this.mmd = mmd;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    public void setRoleForMember(final int role) {
        this.roleForMember = role;
    }
    
    protected int getAbsoluteFieldNumber() {
        if (this.absFieldNumber < 0 && this.mmd != null) {
            this.absFieldNumber = this.mmd.getAbsoluteFieldNumber();
        }
        return this.absFieldNumber;
    }
    
    public void setAbsFieldNumber(final int num) {
        this.absFieldNumber = num;
    }
    
    public AbstractMemberMetaData getMemberMetaData() {
        return this.mmd;
    }
    
    public int getRoleForMember() {
        return this.roleForMember;
    }
    
    public boolean isSerialised() {
        if (this.roleForMember == 3) {
            return this.mmd != null && this.mmd.getCollection() != null && this.mmd.getCollection().isSerializedElement();
        }
        if (this.roleForMember == 4) {
            return this.mmd != null && this.mmd.getArray() != null && this.mmd.getArray().isSerializedElement();
        }
        if (this.roleForMember == 5) {
            return this.mmd != null && this.mmd.getMap() != null && this.mmd.getMap().isSerializedKey();
        }
        if (this.roleForMember == 6) {
            return this.mmd != null && this.mmd.getMap() != null && this.mmd.getMap().isSerializedValue();
        }
        return this.mmd != null && this.mmd.isSerialized();
    }
    
    public boolean isNullable() {
        for (int i = 0; i < this.datastoreMappings.length; ++i) {
            if (!this.datastoreMappings[i].isNullable()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean hasSimpleDatastoreRepresentation() {
        return true;
    }
    
    public boolean representableAsStringLiteralInStatement() {
        return true;
    }
    
    public DatastoreMapping[] getDatastoreMappings() {
        return this.datastoreMappings;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public DatastoreMapping getDatastoreMapping(final int index) {
        return this.datastoreMappings[index];
    }
    
    public Object getValueForDatastoreMapping(final NucleusContext nucleusCtx, final int index, final Object value) {
        return value;
    }
    
    public JavaTypeMapping getReferenceMapping() {
        return this.referenceMapping;
    }
    
    public void setReferenceMapping(final JavaTypeMapping referenceMapping) {
        this.referenceMapping = referenceMapping;
    }
    
    public void addDatastoreMapping(final DatastoreMapping datastoreMapping) {
        final DatastoreMapping[] dm = this.datastoreMappings;
        System.arraycopy(dm, 0, this.datastoreMappings = new DatastoreMapping[this.datastoreMappings.length + 1], 0, dm.length);
        this.datastoreMappings[dm.length] = datastoreMapping;
    }
    
    public int getNumberOfDatastoreMappings() {
        return this.datastoreMappings.length;
    }
    
    public RDBMSStoreManager getStoreManager() {
        return this.storeMgr;
    }
    
    public abstract Class getJavaType();
    
    public String getJavaTypeForDatastoreMapping(final int index) {
        throw new UnsupportedOperationException("Datastore type mapping is not supported by: " + this.getClass());
    }
    
    public String getType() {
        return this.type;
    }
    
    public boolean includeInFetchStatement() {
        return true;
    }
    
    public boolean includeInUpdateStatement() {
        return true;
    }
    
    public boolean includeInInsertStatement() {
        return true;
    }
    
    protected String failureMessage(final String method) {
        return JavaTypeMapping.LOCALISER_RDBMS.msg("041004", this.getClass().getName(), method);
    }
    
    public void setBoolean(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final boolean value) {
        throw new NucleusException(this.failureMessage("setBoolean")).setFatal();
    }
    
    public boolean getBoolean(final ExecutionContext ec, final ResultSet rs, final int[] exprIndex) {
        throw new NucleusException(this.failureMessage("setBoolean")).setFatal();
    }
    
    public void setChar(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final char value) {
        throw new NucleusException(this.failureMessage("setChar")).setFatal();
    }
    
    public char getChar(final ExecutionContext ec, final ResultSet rs, final int[] exprIndex) {
        throw new NucleusException(this.failureMessage("getChar")).setFatal();
    }
    
    public void setByte(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final byte value) {
        throw new NucleusException(this.failureMessage("setByte")).setFatal();
    }
    
    public byte getByte(final ExecutionContext ec, final ResultSet rs, final int[] exprIndex) {
        throw new NucleusException(this.failureMessage("getByte")).setFatal();
    }
    
    public void setShort(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final short value) {
        throw new NucleusException(this.failureMessage("setShort")).setFatal();
    }
    
    public short getShort(final ExecutionContext ec, final ResultSet rs, final int[] exprIndex) {
        throw new NucleusException(this.failureMessage("getShort")).setFatal();
    }
    
    public void setInt(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final int value) {
        throw new NucleusException(this.failureMessage("setInt")).setFatal();
    }
    
    public int getInt(final ExecutionContext ec, final ResultSet rs, final int[] exprIndex) {
        throw new NucleusException(this.failureMessage("getInt")).setFatal();
    }
    
    public void setLong(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final long value) {
        throw new NucleusException(this.failureMessage("setLong")).setFatal();
    }
    
    public long getLong(final ExecutionContext ec, final ResultSet rs, final int[] exprIndex) {
        throw new NucleusException(this.failureMessage("getLong")).setFatal();
    }
    
    public void setFloat(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final float value) {
        throw new NucleusException(this.failureMessage("setFloat")).setFatal();
    }
    
    public float getFloat(final ExecutionContext ec, final ResultSet rs, final int[] exprIndex) {
        throw new NucleusException(this.failureMessage("getFloat")).setFatal();
    }
    
    public void setDouble(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final double value) {
        throw new NucleusException(this.failureMessage("setDouble")).setFatal();
    }
    
    public double getDouble(final ExecutionContext ec, final ResultSet rs, final int[] exprIndex) {
        throw new NucleusException(this.failureMessage("getDouble")).setFatal();
    }
    
    public void setString(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final String value) {
        throw new NucleusException(this.failureMessage("setString")).setFatal();
    }
    
    public String getString(final ExecutionContext ec, final ResultSet rs, final int[] exprIndex) {
        throw new NucleusException(this.failureMessage("getString")).setFatal();
    }
    
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        throw new NucleusException(this.failureMessage("setObject")).setFatal();
    }
    
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        throw new NucleusException(this.failureMessage("setObject")).setFatal();
    }
    
    public Object getObject(final ExecutionContext ec, final ResultSet rs, final int[] exprIndex, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        throw new NucleusException(this.failureMessage("getObject")).setFatal();
    }
    
    public Object getObject(final ExecutionContext ec, final ResultSet rs, final int[] exprIndex) {
        throw new NucleusException(this.failureMessage("getObject")).setFatal();
    }
    
    protected static ColumnMetaData[] getColumnMetaDataForMember(final AbstractMemberMetaData mmd, final int role) {
        if (mmd == null) {
            return null;
        }
        ColumnMetaData[] colmds = null;
        if (role == 3 || role == 4) {
            if (mmd.getJoinMetaData() != null && mmd.getElementMetaData() != null && mmd.getElementMetaData().getColumnMetaData() != null) {
                colmds = mmd.getElementMetaData().getColumnMetaData();
            }
        }
        else if (role == 5) {
            if (mmd.getJoinMetaData() != null && mmd.getKeyMetaData() != null && mmd.getKeyMetaData().getColumnMetaData() != null) {
                colmds = mmd.getKeyMetaData().getColumnMetaData();
            }
        }
        else if (role == 6) {
            if (mmd.getJoinMetaData() != null && mmd.getValueMetaData() != null && mmd.getValueMetaData().getColumnMetaData() != null) {
                colmds = mmd.getValueMetaData().getColumnMetaData();
            }
        }
        else if (mmd.getColumnMetaData() != null && mmd.getColumnMetaData().length > 0) {
            colmds = mmd.getColumnMetaData();
        }
        return colmds;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
