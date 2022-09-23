// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.fieldmanager;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.exceptions.NotYetFlushedException;
import org.datanucleus.store.rdbms.mapping.java.InterfaceMapping;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedPCMapping;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.NullValue;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

public class ParameterSetter extends AbstractFieldManager
{
    private static final Localiser LOCALISER;
    protected final ObjectProvider op;
    protected final ExecutionContext ec;
    protected final PreparedStatement statement;
    protected final StatementClassMapping stmtMappings;
    
    public ParameterSetter(final ObjectProvider op, final PreparedStatement stmt, final StatementClassMapping stmtMappings) {
        this.op = op;
        this.ec = op.getExecutionContext();
        this.statement = stmt;
        this.stmtMappings = stmtMappings;
    }
    
    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
        final StatementMappingIndex mapIdx = this.stmtMappings.getMappingForMemberPosition(fieldNumber);
        for (int i = 0; i < mapIdx.getNumberOfParameterOccurrences(); ++i) {
            mapIdx.getMapping().setBoolean(this.ec, this.statement, mapIdx.getParameterPositionsForOccurrence(i), value);
        }
    }
    
    @Override
    public void storeCharField(final int fieldNumber, final char value) {
        final StatementMappingIndex mapIdx = this.stmtMappings.getMappingForMemberPosition(fieldNumber);
        for (int i = 0; i < mapIdx.getNumberOfParameterOccurrences(); ++i) {
            mapIdx.getMapping().setChar(this.ec, this.statement, mapIdx.getParameterPositionsForOccurrence(i), value);
        }
    }
    
    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
        final StatementMappingIndex mapIdx = this.stmtMappings.getMappingForMemberPosition(fieldNumber);
        for (int i = 0; i < mapIdx.getNumberOfParameterOccurrences(); ++i) {
            mapIdx.getMapping().setByte(this.ec, this.statement, mapIdx.getParameterPositionsForOccurrence(i), value);
        }
    }
    
    @Override
    public void storeShortField(final int fieldNumber, final short value) {
        final StatementMappingIndex mapIdx = this.stmtMappings.getMappingForMemberPosition(fieldNumber);
        for (int i = 0; i < mapIdx.getNumberOfParameterOccurrences(); ++i) {
            mapIdx.getMapping().setShort(this.ec, this.statement, mapIdx.getParameterPositionsForOccurrence(i), value);
        }
    }
    
    @Override
    public void storeIntField(final int fieldNumber, final int value) {
        final StatementMappingIndex mapIdx = this.stmtMappings.getMappingForMemberPosition(fieldNumber);
        for (int i = 0; i < mapIdx.getNumberOfParameterOccurrences(); ++i) {
            mapIdx.getMapping().setInt(this.ec, this.statement, mapIdx.getParameterPositionsForOccurrence(i), value);
        }
    }
    
    @Override
    public void storeLongField(final int fieldNumber, final long value) {
        final StatementMappingIndex mapIdx = this.stmtMappings.getMappingForMemberPosition(fieldNumber);
        for (int i = 0; i < mapIdx.getNumberOfParameterOccurrences(); ++i) {
            mapIdx.getMapping().setLong(this.ec, this.statement, mapIdx.getParameterPositionsForOccurrence(i), value);
        }
    }
    
    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
        final StatementMappingIndex mapIdx = this.stmtMappings.getMappingForMemberPosition(fieldNumber);
        for (int i = 0; i < mapIdx.getNumberOfParameterOccurrences(); ++i) {
            mapIdx.getMapping().setFloat(this.ec, this.statement, mapIdx.getParameterPositionsForOccurrence(i), value);
        }
    }
    
    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
        final StatementMappingIndex mapIdx = this.stmtMappings.getMappingForMemberPosition(fieldNumber);
        for (int i = 0; i < mapIdx.getNumberOfParameterOccurrences(); ++i) {
            mapIdx.getMapping().setDouble(this.ec, this.statement, mapIdx.getParameterPositionsForOccurrence(i), value);
        }
    }
    
    @Override
    public void storeStringField(final int fieldNumber, final String value) {
        final StatementMappingIndex mapIdx = this.stmtMappings.getMappingForMemberPosition(fieldNumber);
        if (value == null && mapIdx.getMapping().getMemberMetaData().getNullValue() == NullValue.EXCEPTION) {
            throw new NucleusUserException(ParameterSetter.LOCALISER.msg("052400", mapIdx.getMapping().getMemberMetaData().getFullFieldName()));
        }
        for (int i = 0; i < mapIdx.getNumberOfParameterOccurrences(); ++i) {
            mapIdx.getMapping().setString(this.ec, this.statement, mapIdx.getParameterPositionsForOccurrence(i), value);
        }
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        final StatementMappingIndex mapIdx = this.stmtMappings.getMappingForMemberPosition(fieldNumber);
        if (value == null && mapIdx.getMapping().getMemberMetaData().getNullValue() == NullValue.EXCEPTION) {
            throw new NucleusUserException(ParameterSetter.LOCALISER.msg("052400", mapIdx.getMapping().getMemberMetaData().getFullFieldName()));
        }
        try {
            final JavaTypeMapping mapping = mapIdx.getMapping();
            boolean provideOwner = false;
            if (mapping instanceof EmbeddedPCMapping || mapping instanceof SerialisedPCMapping || mapping instanceof SerialisedReferenceMapping || mapping instanceof PersistableMapping || mapping instanceof InterfaceMapping) {
                provideOwner = true;
            }
            if (mapIdx.getNumberOfParameterOccurrences() > 0) {
                for (int i = 0; i < mapIdx.getNumberOfParameterOccurrences(); ++i) {
                    if (provideOwner) {
                        mapping.setObject(this.ec, this.statement, mapIdx.getParameterPositionsForOccurrence(i), value, this.op, fieldNumber);
                    }
                    else {
                        mapping.setObject(this.ec, this.statement, mapIdx.getParameterPositionsForOccurrence(i), value);
                    }
                }
            }
            else if (provideOwner) {
                mapping.setObject(this.ec, this.statement, null, value, this.op, fieldNumber);
            }
            else {
                mapping.setObject(this.ec, this.statement, null, value);
            }
            this.op.wrapSCOField(fieldNumber, value, false, true, true);
        }
        catch (NotYetFlushedException e) {
            if (this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber).getNullValue() == NullValue.EXCEPTION) {
                throw e;
            }
            this.op.updateFieldAfterInsert(e.getPersistable(), fieldNumber);
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
