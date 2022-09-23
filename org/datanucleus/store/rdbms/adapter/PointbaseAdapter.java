// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.schema.JDBCTypeInfo;
import org.datanucleus.store.rdbms.schema.RDBMSTypesInfo;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.StoreSchemaHandler;
import java.sql.DatabaseMetaData;

public class PointbaseAdapter extends BaseDatastoreAdapter
{
    public PointbaseAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.supportedOptions.remove("BooleanExpression");
        this.supportedOptions.remove("DeferredConstraints");
    }
    
    @Override
    public void initialiseTypes(final StoreSchemaHandler handler, final ManagedConnection mconn) {
        super.initialiseTypes(handler, mconn);
        final RDBMSTypesInfo typesInfo = (RDBMSTypesInfo)handler.getSchemaData(mconn.getConnection(), "types", null);
        JDBCTypeInfo jdbcType = (JDBCTypeInfo)typesInfo.getChild("9");
        if (jdbcType != null && jdbcType.getNumberOfChildren() > 0) {
            final SQLTypeInfo dfltTypeInfo = (SQLTypeInfo)jdbcType.getChild("DEFAULT");
            final SQLTypeInfo sqlType = new SQLTypeInfo(dfltTypeInfo.getTypeName(), (short)(-5), dfltTypeInfo.getPrecision(), dfltTypeInfo.getLiteralPrefix(), dfltTypeInfo.getLiteralSuffix(), dfltTypeInfo.getCreateParams(), dfltTypeInfo.getNullable(), dfltTypeInfo.isCaseSensitive(), dfltTypeInfo.getSearchable(), dfltTypeInfo.isUnsignedAttribute(), dfltTypeInfo.isFixedPrecScale(), dfltTypeInfo.isAutoIncrement(), dfltTypeInfo.getLocalTypeName(), dfltTypeInfo.getMinimumScale(), dfltTypeInfo.getMaximumScale(), dfltTypeInfo.getNumPrecRadix());
            this.addSQLTypeForJDBCType(handler, mconn, (short)(-5), sqlType, true);
        }
        jdbcType = (JDBCTypeInfo)typesInfo.getChild("16");
        if (jdbcType != null) {
            final SQLTypeInfo dfltTypeInfo = (SQLTypeInfo)jdbcType.getChild("DEFAULT");
            final SQLTypeInfo sqlType = new SQLTypeInfo(dfltTypeInfo.getTypeName(), (short)16, dfltTypeInfo.getPrecision(), dfltTypeInfo.getLiteralPrefix(), dfltTypeInfo.getLiteralSuffix(), dfltTypeInfo.getCreateParams(), dfltTypeInfo.getNullable(), dfltTypeInfo.isCaseSensitive(), dfltTypeInfo.getSearchable(), dfltTypeInfo.isUnsignedAttribute(), dfltTypeInfo.isFixedPrecScale(), dfltTypeInfo.isAutoIncrement(), dfltTypeInfo.getLocalTypeName(), dfltTypeInfo.getMinimumScale(), dfltTypeInfo.getMaximumScale(), dfltTypeInfo.getNumPrecRadix());
            this.addSQLTypeForJDBCType(handler, mconn, (short)16, sqlType, true);
        }
    }
    
    @Override
    public String getVendorID() {
        return "pointbase";
    }
    
    @Override
    public int getUnlimitedLengthPrecisionValue(final SQLTypeInfo typeInfo) {
        if (typeInfo.getDataType() == 2004 || typeInfo.getDataType() == 2005) {
            return Integer.MIN_VALUE;
        }
        return super.getUnlimitedLengthPrecisionValue(typeInfo);
    }
}
