// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import org.apache.derby.catalog.types.RoutineAliasInfo;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.impl.jdbc.Util;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;

final class GenericParameter
{
    private static int DECIMAL_PARAMETER_DEFAULT_PRECISION;
    private static int DECIMAL_PARAMETER_DEFAULT_SCALE;
    private final GenericParameterValueSet pvs;
    private DataValueDescriptor value;
    int jdbcTypeId;
    String declaredClassName;
    short parameterMode;
    boolean isSet;
    private final boolean isReturnOutputParameter;
    int registerOutType;
    int registerOutScale;
    int registerOutPrecision;
    
    GenericParameter(final GenericParameterValueSet pvs, final boolean isReturnOutputParameter) {
        this.registerOutType = 0;
        this.registerOutScale = -1;
        this.registerOutPrecision = -1;
        this.pvs = pvs;
        this.isReturnOutputParameter = isReturnOutputParameter;
        this.parameterMode = (short)(isReturnOutputParameter ? 4 : 1);
    }
    
    public GenericParameter getClone(final GenericParameterValueSet set) {
        final GenericParameter genericParameter = new GenericParameter(set, this.isReturnOutputParameter);
        genericParameter.initialize(this.getValue().cloneValue(false), this.jdbcTypeId, this.declaredClassName);
        genericParameter.isSet = true;
        return genericParameter;
    }
    
    void initialize(final DataValueDescriptor value, final int jdbcTypeId, final String declaredClassName) {
        this.value = value;
        this.jdbcTypeId = jdbcTypeId;
        this.declaredClassName = declaredClassName;
    }
    
    void clear() {
        this.isSet = false;
    }
    
    DataValueDescriptor getValue() {
        return this.value;
    }
    
    void setOutParameter(final int registerOutType, final int n) throws StandardException {
        if (this.registerOutType == registerOutType && n == this.registerOutScale) {
            return;
        }
        switch (this.parameterMode) {
            default: {
                throw StandardException.newException("XCL22.S", this.getJDBCParameterNumberStr());
            }
            case 2:
            case 4: {
                if (!DataTypeDescriptor.isJDBCTypeEquivalent(this.jdbcTypeId, registerOutType)) {
                    throw this.throwInvalidOutParamMap(registerOutType);
                }
                this.registerOutType = registerOutType;
            }
        }
    }
    
    private StandardException throwInvalidOutParamMap(final int n) {
        final String typeName = Util.typeName(n);
        final TypeId builtInTypeId = TypeId.getBuiltInTypeId(this.jdbcTypeId);
        return StandardException.newException("XCL25.S", this.getJDBCParameterNumberStr(), typeName, (builtInTypeId == null) ? this.declaredClassName : builtInTypeId.getSQLTypeName());
    }
    
    void validate() throws StandardException {
        switch (this.parameterMode) {
            case 0: {}
            case 2:
            case 4: {
                if (this.registerOutType == 0) {
                    throw StandardException.newException("07004", this.getJDBCParameterNumberStr(), RoutineAliasInfo.parameterMode(this.parameterMode));
                }
                break;
            }
        }
    }
    
    int getScale() {
        return (this.registerOutScale == -1) ? 0 : this.registerOutScale;
    }
    
    int getPrecision() {
        return this.registerOutPrecision;
    }
    
    String getJDBCParameterNumberStr() {
        return Integer.toString(this.pvs.getParameterNumber(this));
    }
    
    public String toString() {
        if (this.value == null) {
            return "null";
        }
        try {
            return this.value.getTraceString();
        }
        catch (StandardException obj) {
            return "unexpected exception from getTraceString() - " + obj;
        }
    }
    
    static {
        GenericParameter.DECIMAL_PARAMETER_DEFAULT_PRECISION = 31;
        GenericParameter.DECIMAL_PARAMETER_DEFAULT_SCALE = 15;
    }
}
