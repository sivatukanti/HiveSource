// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.sql.ResultSet;

public class MySQLTypeInfo extends SQLTypeInfo
{
    public MySQLTypeInfo(final ResultSet rs) {
        super(rs);
        if (this.typeName.equalsIgnoreCase("FLOAT")) {
            this.dataType = 6;
        }
        else if (this.typeName.equalsIgnoreCase("CHAR")) {
            this.typeName = "CHAR(M) BINARY";
            this.createParams = "";
        }
        else if (this.typeName.equalsIgnoreCase("VARCHAR")) {
            this.typeName = "VARCHAR(M) BINARY";
            this.createParams = "";
        }
        this.fixAllowsPrecisionSpec();
    }
    
    public MySQLTypeInfo(final String typeName, final short dataType, final int precision, final String literalPrefix, final String literalSuffix, final String createParams, final int nullable, final boolean caseSensitive, final short searchable, final boolean unsignedAttribute, final boolean fixedPrecScale, final boolean autoIncrement, final String localTypeName, final short minimumScale, final short maximumScale, final int numPrecRadix) {
        super(typeName, dataType, precision, literalPrefix, literalSuffix, createParams, nullable, caseSensitive, searchable, unsignedAttribute, fixedPrecScale, autoIncrement, localTypeName, minimumScale, maximumScale, numPrecRadix);
        this.fixAllowsPrecisionSpec();
    }
    
    private void fixAllowsPrecisionSpec() {
        if (this.typeName.equalsIgnoreCase("LONG VARCHAR") || this.typeName.equalsIgnoreCase("BLOB") || this.typeName.equalsIgnoreCase("MEDIUMBLOB") || this.typeName.equalsIgnoreCase("LONGBLOB") || this.typeName.equalsIgnoreCase("MEDIUMTEXT") || this.typeName.equalsIgnoreCase("LONGTEXT") || this.typeName.equalsIgnoreCase("TEXT")) {
            this.allowsPrecisionSpec = false;
        }
    }
    
    @Override
    public boolean isCompatibleWith(final RDBMSColumnInfo colInfo) {
        if (super.isCompatibleWith(colInfo)) {
            return true;
        }
        final short colDataType = colInfo.getDataType();
        if (isStringType(this.dataType) && isStringType(colDataType)) {
            return true;
        }
        if (this.dataType == -7) {
            final int colSize = colInfo.getColumnSize();
            return colDataType == -6 && colSize == 1;
        }
        return (this.dataType == 2004 && colDataType == -4) || (this.dataType == -4 && colDataType == 2004) || ((this.dataType == 2005 && colDataType == -1) || (this.dataType == -1 && colDataType == 2005));
    }
    
    private static boolean isStringType(final int type) {
        switch (type) {
            case -4:
            case -3:
            case -2:
            case -1:
            case 1:
            case 12: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
