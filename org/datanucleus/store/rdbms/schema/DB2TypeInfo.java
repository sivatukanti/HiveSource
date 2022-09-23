// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.sql.ResultSet;

public class DB2TypeInfo extends SQLTypeInfo
{
    public static final int DATALINK = 70;
    
    public DB2TypeInfo(final ResultSet rs) {
        super(rs);
        if (this.typeName.equalsIgnoreCase("DATALINK")) {
            this.createParams = "";
        }
    }
    
    public DB2TypeInfo(final String typeName, final short dataType, final int precision, final String literalPrefix, final String literalSuffix, final String createParams, final int nullable, final boolean caseSensitive, final short searchable, final boolean unsignedAttribute, final boolean fixedPrecScale, final boolean autoIncrement, final String localTypeName, final short minimumScale, final short maximumScale, final int numPrecRadix) {
        super(typeName, dataType, precision, literalPrefix, literalSuffix, createParams, nullable, caseSensitive, searchable, unsignedAttribute, fixedPrecScale, autoIncrement, localTypeName, minimumScale, maximumScale, numPrecRadix);
    }
}
