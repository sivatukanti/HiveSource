// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.sql.ResultSet;

public class MSSQLTypeInfo extends SQLTypeInfo
{
    public static final int NVARCHAR = -9;
    public static final int NTEXT = -10;
    public static final int UNIQUEIDENTIFIER = -11;
    
    public MSSQLTypeInfo(final ResultSet rs) {
        super(rs);
        if (this.typeName.equalsIgnoreCase("uniqueidentifier")) {
            this.allowsPrecisionSpec = false;
        }
    }
    
    public MSSQLTypeInfo(final String typeName, final short dataType, final int precision, final String literalPrefix, final String literalSuffix, final String createParams, final int nullable, final boolean caseSensitive, final short searchable, final boolean unsignedAttribute, final boolean fixedPrecScale, final boolean autoIncrement, final String localTypeName, final short minimumScale, final short maximumScale, final int numPrecRadix) {
        super(typeName, dataType, precision, literalPrefix, literalSuffix, createParams, nullable, caseSensitive, searchable, unsignedAttribute, fixedPrecScale, autoIncrement, localTypeName, minimumScale, maximumScale, numPrecRadix);
    }
    
    @Override
    public boolean isCompatibleWith(final RDBMSColumnInfo colInfo) {
        if (super.isCompatibleWith(colInfo)) {
            return true;
        }
        final short colDataType = colInfo.getDataType();
        switch (this.dataType) {
            case 12: {
                return colDataType == -9;
            }
            case -1: {
                return colDataType == -10;
            }
            case -11:
            case -4:
            case -3: {
                return colDataType == -3 || colDataType == -4 || colDataType == -11;
            }
            default: {
                return false;
            }
        }
    }
}
