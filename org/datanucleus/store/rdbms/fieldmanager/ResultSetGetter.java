// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.fieldmanager;

import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.rdbms.mapping.java.SerialisedReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedPCMapping;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

public class ResultSetGetter extends AbstractFieldManager
{
    private final RDBMSStoreManager storeMgr;
    private final ObjectProvider op;
    private final AbstractClassMetaData cmd;
    private final ExecutionContext ec;
    private final ResultSet resultSet;
    private final StatementClassMapping resultMappings;
    
    public ResultSetGetter(final RDBMSStoreManager storeMgr, final ObjectProvider op, final ResultSet rs, final StatementClassMapping resultMappings) {
        this.storeMgr = storeMgr;
        this.op = op;
        this.cmd = op.getClassMetaData();
        this.ec = op.getExecutionContext();
        this.resultSet = rs;
        this.resultMappings = resultMappings;
    }
    
    public ResultSetGetter(final RDBMSStoreManager storeMgr, final ExecutionContext ec, final ResultSet rs, final StatementClassMapping resultMappings, final AbstractClassMetaData cmd) {
        this.storeMgr = storeMgr;
        this.op = null;
        this.cmd = cmd;
        this.ec = ec;
        this.resultSet = rs;
        this.resultMappings = resultMappings;
    }
    
    @Override
    public boolean fetchBooleanField(final int fieldNumber) {
        final StatementMappingIndex mapIdx = this.resultMappings.getMappingForMemberPosition(fieldNumber);
        return mapIdx.getMapping().getBoolean(this.ec, this.resultSet, mapIdx.getColumnPositions());
    }
    
    @Override
    public char fetchCharField(final int fieldNumber) {
        final StatementMappingIndex mapIdx = this.resultMappings.getMappingForMemberPosition(fieldNumber);
        return mapIdx.getMapping().getChar(this.ec, this.resultSet, mapIdx.getColumnPositions());
    }
    
    @Override
    public byte fetchByteField(final int fieldNumber) {
        final StatementMappingIndex mapIdx = this.resultMappings.getMappingForMemberPosition(fieldNumber);
        return mapIdx.getMapping().getByte(this.ec, this.resultSet, mapIdx.getColumnPositions());
    }
    
    @Override
    public short fetchShortField(final int fieldNumber) {
        final StatementMappingIndex mapIdx = this.resultMappings.getMappingForMemberPosition(fieldNumber);
        return mapIdx.getMapping().getShort(this.ec, this.resultSet, mapIdx.getColumnPositions());
    }
    
    @Override
    public int fetchIntField(final int fieldNumber) {
        final StatementMappingIndex mapIdx = this.resultMappings.getMappingForMemberPosition(fieldNumber);
        return mapIdx.getMapping().getInt(this.ec, this.resultSet, mapIdx.getColumnPositions());
    }
    
    @Override
    public long fetchLongField(final int fieldNumber) {
        final StatementMappingIndex mapIdx = this.resultMappings.getMappingForMemberPosition(fieldNumber);
        return mapIdx.getMapping().getLong(this.ec, this.resultSet, mapIdx.getColumnPositions());
    }
    
    @Override
    public float fetchFloatField(final int fieldNumber) {
        final StatementMappingIndex mapIdx = this.resultMappings.getMappingForMemberPosition(fieldNumber);
        return mapIdx.getMapping().getFloat(this.ec, this.resultSet, mapIdx.getColumnPositions());
    }
    
    @Override
    public double fetchDoubleField(final int fieldNumber) {
        final StatementMappingIndex mapIdx = this.resultMappings.getMappingForMemberPosition(fieldNumber);
        return mapIdx.getMapping().getDouble(this.ec, this.resultSet, mapIdx.getColumnPositions());
    }
    
    @Override
    public String fetchStringField(final int fieldNumber) {
        final StatementMappingIndex mapIdx = this.resultMappings.getMappingForMemberPosition(fieldNumber);
        return mapIdx.getMapping().getString(this.ec, this.resultSet, mapIdx.getColumnPositions());
    }
    
    @Override
    public Object fetchObjectField(final int fieldNumber) {
        final StatementMappingIndex mapIdx = this.resultMappings.getMappingForMemberPosition(fieldNumber);
        final JavaTypeMapping mapping = mapIdx.getMapping();
        Object value;
        if (mapping instanceof EmbeddedPCMapping || mapping instanceof SerialisedPCMapping || mapping instanceof SerialisedReferenceMapping) {
            value = mapping.getObject(this.ec, this.resultSet, mapIdx.getColumnPositions(), this.op, fieldNumber);
        }
        else {
            final AbstractMemberMetaData mmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            final RelationType relationType = mmd.getRelationType(this.ec.getClassLoaderResolver());
            if (relationType == RelationType.ONE_TO_ONE_BI || relationType == RelationType.ONE_TO_ONE_UNI || relationType == RelationType.MANY_TO_ONE_BI) {
                final StatementClassMapping relationMappings = this.resultMappings.getMappingDefinitionForMemberPosition(fieldNumber);
                if (relationMappings != null) {
                    final ClassLoaderResolver clr = this.ec.getClassLoaderResolver();
                    final AbstractClassMetaData relatedCmd = this.ec.getMetaDataManager().getMetaDataForClass(mmd.getType(), clr);
                    final ResultObjectFactory relationROF = this.storeMgr.newResultObjectFactory(relatedCmd, relationMappings, false, this.ec.getFetchPlan(), mmd.getType());
                    value = relationROF.getObject(this.ec, this.resultSet);
                }
                else {
                    value = mapping.getObject(this.ec, this.resultSet, mapIdx.getColumnPositions());
                }
            }
            else {
                value = mapping.getObject(this.ec, this.resultSet, mapIdx.getColumnPositions());
            }
        }
        if (this.op != null) {
            return this.op.wrapSCOField(fieldNumber, value, false, false, false);
        }
        return value;
    }
}
