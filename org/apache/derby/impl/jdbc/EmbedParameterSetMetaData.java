// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.types.DataTypeUtilities;
import java.sql.SQLException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.ParameterValueSet;
import org.apache.derby.iapi.jdbc.EngineParameterMetaData;

public class EmbedParameterSetMetaData implements EngineParameterMetaData
{
    private final ParameterValueSet pvs;
    private final DataTypeDescriptor[] types;
    private final int paramCount;
    
    protected EmbedParameterSetMetaData(final ParameterValueSet pvs, final DataTypeDescriptor[] types) {
        final int parameterCount = pvs.getParameterCount();
        this.pvs = pvs;
        this.paramCount = parameterCount;
        this.types = types;
    }
    
    public int getParameterCount() {
        return this.paramCount;
    }
    
    public int isNullable(final int n) throws SQLException {
        this.checkPosition(n);
        if (this.types[n - 1].isNullable()) {
            return 1;
        }
        return 0;
    }
    
    public boolean isSigned(final int n) throws SQLException {
        this.checkPosition(n);
        return this.types[n - 1].getTypeId().isNumericTypeId();
    }
    
    public int getPrecision(final int n) throws SQLException {
        this.checkPosition(n);
        int precision = -1;
        if (n == 1 && this.pvs.hasReturnOutputParameter()) {
            precision = this.pvs.getPrecision(n);
        }
        if (precision == -1) {
            return DataTypeUtilities.getPrecision(this.types[n - 1]);
        }
        return precision;
    }
    
    public int getScale(final int n) throws SQLException {
        this.checkPosition(n);
        if (n == 1 && this.pvs.hasReturnOutputParameter()) {
            return this.pvs.getScale(n);
        }
        return this.types[n - 1].getScale();
    }
    
    public int getParameterType(final int n) throws SQLException {
        this.checkPosition(n);
        return this.types[n - 1].getTypeId().getJDBCTypeId();
    }
    
    public String getParameterTypeName(final int n) throws SQLException {
        this.checkPosition(n);
        return this.types[n - 1].getTypeId().getSQLTypeName();
    }
    
    public String getParameterClassName(final int n) throws SQLException {
        this.checkPosition(n);
        return this.types[n - 1].getTypeId().getResultSetMetaDataTypeName();
    }
    
    public int getParameterMode(final int n) throws SQLException {
        this.checkPosition(n);
        if (n == 1 && this.pvs.hasReturnOutputParameter()) {
            return 4;
        }
        return this.pvs.getParameterMode(n);
    }
    
    private void checkPosition(final int value) throws SQLException {
        if (value < 1 || value > this.paramCount) {
            throw Util.generateCsSQLException("XCL13.S", new Integer(value), new Integer(this.paramCount));
        }
    }
}
