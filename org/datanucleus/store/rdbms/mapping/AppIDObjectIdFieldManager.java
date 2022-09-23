// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping;

import org.datanucleus.ClassConstants;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

public class AppIDObjectIdFieldManager extends AbstractFieldManager
{
    protected static final Localiser LOCALISER;
    private int[] params;
    private int nextParam;
    private ExecutionContext ec;
    private PreparedStatement statement;
    private JavaTypeMapping[] javaTypeMappings;
    private int mappingNum;
    
    public AppIDObjectIdFieldManager(final int[] param, final ExecutionContext ec, final PreparedStatement statement, final JavaTypeMapping[] javaTypeMappings) {
        this.mappingNum = 0;
        this.params = param;
        this.nextParam = 0;
        this.ec = ec;
        this.statement = statement;
        int numMappings = 0;
        for (int i = 0; i < javaTypeMappings.length; ++i) {
            if (javaTypeMappings[i] instanceof PersistableMapping) {
                numMappings += ((PersistableMapping)javaTypeMappings[i]).getJavaTypeMapping().length;
            }
            else {
                ++numMappings;
            }
        }
        this.javaTypeMappings = new JavaTypeMapping[numMappings];
        int mappingNum = 0;
        for (int j = 0; j < javaTypeMappings.length; ++j) {
            if (javaTypeMappings[j] instanceof PersistableMapping) {
                final PersistableMapping m = (PersistableMapping)javaTypeMappings[j];
                final JavaTypeMapping[] subMappings = m.getJavaTypeMapping();
                for (int k = 0; k < subMappings.length; ++k) {
                    this.javaTypeMappings[mappingNum++] = subMappings[k];
                }
            }
            else {
                this.javaTypeMappings[mappingNum++] = javaTypeMappings[j];
            }
        }
    }
    
    private int[] getParamsForField(final JavaTypeMapping mapping) {
        if (this.javaTypeMappings.length == 1) {
            return this.params;
        }
        final int numCols = mapping.getNumberOfDatastoreMappings();
        final int[] fieldParams = new int[numCols];
        for (int i = 0; i < numCols; ++i) {
            fieldParams[i] = this.params[this.nextParam++];
        }
        return fieldParams;
    }
    
    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
        final JavaTypeMapping mapping = this.javaTypeMappings[this.mappingNum++];
        mapping.setBoolean(this.ec, this.statement, this.getParamsForField(mapping), value);
    }
    
    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
        final JavaTypeMapping mapping = this.javaTypeMappings[this.mappingNum++];
        mapping.setByte(this.ec, this.statement, this.getParamsForField(mapping), value);
    }
    
    @Override
    public void storeCharField(final int fieldNumber, final char value) {
        final JavaTypeMapping mapping = this.javaTypeMappings[this.mappingNum++];
        mapping.setChar(this.ec, this.statement, this.getParamsForField(mapping), value);
    }
    
    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
        final JavaTypeMapping mapping = this.javaTypeMappings[this.mappingNum++];
        mapping.setDouble(this.ec, this.statement, this.getParamsForField(mapping), value);
    }
    
    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
        final JavaTypeMapping mapping = this.javaTypeMappings[this.mappingNum++];
        mapping.setFloat(this.ec, this.statement, this.getParamsForField(mapping), value);
    }
    
    @Override
    public void storeIntField(final int fieldNumber, final int value) {
        final JavaTypeMapping mapping = this.javaTypeMappings[this.mappingNum++];
        mapping.setInt(this.ec, this.statement, this.getParamsForField(mapping), value);
    }
    
    @Override
    public void storeLongField(final int fieldNumber, final long value) {
        final JavaTypeMapping mapping = this.javaTypeMappings[this.mappingNum++];
        mapping.setLong(this.ec, this.statement, this.getParamsForField(mapping), value);
    }
    
    @Override
    public void storeShortField(final int fieldNumber, final short value) {
        final JavaTypeMapping mapping = this.javaTypeMappings[this.mappingNum++];
        mapping.setShort(this.ec, this.statement, this.getParamsForField(mapping), value);
    }
    
    @Override
    public void storeStringField(final int fieldNumber, final String value) {
        final JavaTypeMapping mapping = this.javaTypeMappings[this.mappingNum++];
        mapping.setString(this.ec, this.statement, this.getParamsForField(mapping), value);
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        final ApiAdapter api = this.ec.getApiAdapter();
        if (api.isPersistable(value)) {
            api.copyPkFieldsToPersistableObjectFromId(value, api.getObjectState(value), this);
        }
        else {
            final JavaTypeMapping mapping = this.javaTypeMappings[this.mappingNum++];
            mapping.setObject(this.ec, this.statement, this.getParamsForField(mapping), value);
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
