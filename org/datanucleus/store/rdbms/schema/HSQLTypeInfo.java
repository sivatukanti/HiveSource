// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.sql.ResultSet;

public class HSQLTypeInfo extends SQLTypeInfo
{
    public static final int MAX_PRECISION = Integer.MAX_VALUE;
    
    public HSQLTypeInfo(final ResultSet rs) {
        super(rs);
        if (this.typeName.equalsIgnoreCase("varchar") || this.typeName.equalsIgnoreCase("char")) {
            this.precision = Integer.MAX_VALUE;
        }
        else if (this.typeName.equalsIgnoreCase("numeric")) {
            this.precision = Integer.MAX_VALUE;
        }
        else if (this.typeName.equalsIgnoreCase("text")) {
            this.dataType = -1;
        }
        if (this.precision > Integer.MAX_VALUE) {
            this.precision = Integer.MAX_VALUE;
        }
    }
    
    public HSQLTypeInfo(final String typeName, final short dataType, final int precision, final String literalPrefix, final String literalSuffix, final String createParams, final int nullable, final boolean caseSensitive, final short searchable, final boolean unsignedAttribute, final boolean fixedPrecScale, final boolean autoIncrement, final String localTypeName, final short minimumScale, final short maximumScale, final int numPrecRadix) {
        super(typeName, dataType, precision, literalPrefix, literalSuffix, createParams, nullable, caseSensitive, searchable, unsignedAttribute, fixedPrecScale, autoIncrement, localTypeName, minimumScale, maximumScale, numPrecRadix);
    }
    
    @Override
    public boolean isCompatibleWith(final RDBMSColumnInfo colInfo) {
        if (super.isCompatibleWith(colInfo)) {
            return true;
        }
        final short colDataType = colInfo.getDataType();
        return (this.dataType == 2005 && colDataType == -1) || (this.dataType == -1 && colDataType == 2005) || ((this.dataType == 2004 && colDataType == -4) || (this.dataType == -4 && colDataType == 2004));
    }
}
