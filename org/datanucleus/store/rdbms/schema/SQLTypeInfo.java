// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.JDBCUtils;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.ResultSet;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.schema.StoreSchemaData;

public class SQLTypeInfo implements StoreSchemaData
{
    private static final Localiser LOCALISER;
    protected String typeName;
    protected short dataType;
    protected int precision;
    protected String literalPrefix;
    protected String literalSuffix;
    protected String createParams;
    protected int nullable;
    protected boolean caseSensitive;
    protected short searchable;
    protected boolean unsignedAttribute;
    protected boolean fixedPrecScale;
    protected boolean autoIncrement;
    protected String localTypeName;
    protected short minimumScale;
    protected short maximumScale;
    protected int numPrecRadix;
    protected boolean allowsPrecisionSpec;
    private int hash;
    
    public SQLTypeInfo(final String typeName, final short dataType, final int precision, final String literalPrefix, final String literalSuffix, final String createParams, final int nullable, final boolean caseSensitive, final short searchable, final boolean unsignedAttribute, final boolean fixedPrecScale, final boolean autoIncrement, final String localTypeName, final short minimumScale, final short maximumScale, final int numPrecRadix) {
        this.allowsPrecisionSpec = true;
        this.hash = 0;
        this.typeName = typeName;
        this.dataType = dataType;
        this.precision = precision;
        this.literalPrefix = literalPrefix;
        this.literalSuffix = literalSuffix;
        this.createParams = createParams;
        this.nullable = nullable;
        this.caseSensitive = caseSensitive;
        this.searchable = searchable;
        this.unsignedAttribute = unsignedAttribute;
        this.fixedPrecScale = fixedPrecScale;
        this.autoIncrement = autoIncrement;
        this.localTypeName = localTypeName;
        this.minimumScale = minimumScale;
        this.maximumScale = maximumScale;
        this.numPrecRadix = numPrecRadix;
    }
    
    public SQLTypeInfo(final ResultSet rs) {
        this.allowsPrecisionSpec = true;
        this.hash = 0;
        try {
            this.typeName = rs.getString(1);
            this.dataType = rs.getShort(2);
            this.precision = (int)rs.getLong(3);
            this.literalPrefix = rs.getString(4);
            this.literalSuffix = rs.getString(5);
            this.createParams = rs.getString(6);
            this.nullable = rs.getInt(7);
            this.caseSensitive = rs.getBoolean(8);
            this.searchable = rs.getShort(9);
            this.unsignedAttribute = rs.getBoolean(10);
            this.fixedPrecScale = rs.getBoolean(11);
            this.autoIncrement = rs.getBoolean(12);
            this.localTypeName = rs.getString(13);
            this.minimumScale = rs.getShort(14);
            this.maximumScale = rs.getShort(15);
            this.numPrecRadix = rs.getInt(18);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException("Can't read JDBC metadata from result set", e).setFatal();
        }
    }
    
    @Override
    public void addProperty(final String name, final Object value) {
        throw new UnsupportedOperationException("SQLTypeInfo doesnt support properties");
    }
    
    @Override
    public Object getProperty(final String name) {
        throw new UnsupportedOperationException("SQLTypeInfo doesnt support properties");
    }
    
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof SQLTypeInfo)) {
            return false;
        }
        final SQLTypeInfo other = (SQLTypeInfo)obj;
        return this.getTypeName().equals(other.getTypeName()) && this.getDataType() == other.getDataType();
    }
    
    @Override
    public final int hashCode() {
        if (this.hash == 0) {
            this.hash = (this.getTypeName().hashCode() ^ this.getDataType());
        }
        return this.hash;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer("SQLTypeInfo : ");
        str.append("  typeName          = " + this.getTypeName() + "\n");
        str.append("  jdbcTypeName      = " + JDBCUtils.getNameForJDBCType(this.getDataType()) + "\n");
        str.append("  dataType          = " + this.getDataType() + "\n");
        str.append("  precision         = " + this.getPrecision() + "\n");
        str.append("  literalPrefix     = " + this.getLiteralPrefix() + "\n");
        str.append("  literalSuffix     = " + this.getLiteralSuffix() + "\n");
        str.append("  createParams      = " + this.getCreateParams() + "\n");
        str.append("  nullable          = " + this.getNullable() + "\n");
        str.append("  caseSensitive     = " + this.isCaseSensitive() + "\n");
        str.append("  searchable        = " + this.getSearchable() + "\n");
        str.append("  unsignedAttribute = " + this.isUnsignedAttribute() + "\n");
        str.append("  fixedPrecScale    = " + this.isFixedPrecScale() + "\n");
        str.append("  autoIncrement     = " + this.isAutoIncrement() + "\n");
        str.append("  localTypeName     = " + this.getLocalTypeName() + "\n");
        str.append("  minimumScale      = " + this.getMinimumScale() + "\n");
        str.append("  maximumScale      = " + this.getMaximumScale() + "\n");
        str.append("  numPrecRadix      = " + this.getNumPrecRadix() + "\n");
        str.append("  allowsPrecisionSpec = " + this.isAllowsPrecisionSpec() + "\n");
        return str.toString();
    }
    
    public boolean isCompatibleWith(final RDBMSColumnInfo colInfo) {
        final int expected = this.getDataType();
        final int actual = colInfo.getDataType();
        if (actual == 1111) {
            NucleusLogger.DATASTORE.warn(SQLTypeInfo.LOCALISER.msg("020191", JDBCUtils.getNameForJDBCType(actual)));
            return true;
        }
        switch (expected) {
            case -6:
            case -5:
            case 4:
            case 5: {
                return isIntegerType(actual);
            }
            case 6:
            case 7:
            case 8: {
                return isFloatingType(actual);
            }
            case 2:
            case 3: {
                return isNumericType(actual);
            }
            case -1:
            case 12: {
                return isCharacterType(actual);
            }
            case 91:
            case 92:
            case 93: {
                return isDateType(actual);
            }
            default: {
                return expected == actual;
            }
        }
    }
    
    private static boolean isIntegerType(final int type) {
        switch (type) {
            case -6:
            case -5:
            case 4:
            case 5: {
                return true;
            }
            default: {
                return isNumericType(type);
            }
        }
    }
    
    private static boolean isDateType(final int type) {
        switch (type) {
            case 91:
            case 92:
            case 93: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean isFloatingType(final int type) {
        switch (type) {
            case 6:
            case 7:
            case 8: {
                return true;
            }
            default: {
                return isNumericType(type);
            }
        }
    }
    
    private static boolean isNumericType(final int type) {
        switch (type) {
            case 2:
            case 3: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean isCharacterType(final int type) {
        switch (type) {
            case -1:
            case 12: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }
    
    public String getTypeName() {
        return this.typeName;
    }
    
    public short getDataType() {
        return this.dataType;
    }
    
    public int getPrecision() {
        return this.precision;
    }
    
    public String getLiteralPrefix() {
        return this.literalPrefix;
    }
    
    public String getLiteralSuffix() {
        return this.literalSuffix;
    }
    
    public String getCreateParams() {
        return this.createParams;
    }
    
    public int getNullable() {
        return this.nullable;
    }
    
    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }
    
    public short getSearchable() {
        return this.searchable;
    }
    
    public boolean isUnsignedAttribute() {
        return this.unsignedAttribute;
    }
    
    public boolean isFixedPrecScale() {
        return this.fixedPrecScale;
    }
    
    public boolean isAutoIncrement() {
        return this.autoIncrement;
    }
    
    public void setLocalTypeName(final String localTypeName) {
        this.localTypeName = localTypeName;
    }
    
    public String getLocalTypeName() {
        return this.localTypeName;
    }
    
    public short getMinimumScale() {
        return this.minimumScale;
    }
    
    public short getMaximumScale() {
        return this.maximumScale;
    }
    
    public int getNumPrecRadix() {
        return this.numPrecRadix;
    }
    
    public void setAllowsPrecisionSpec(final boolean allowsPrecisionSpec) {
        this.allowsPrecisionSpec = allowsPrecisionSpec;
    }
    
    public boolean isAllowsPrecisionSpec() {
        return this.allowsPrecisionSpec;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
