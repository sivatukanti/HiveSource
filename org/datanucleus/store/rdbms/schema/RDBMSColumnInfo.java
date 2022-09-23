// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.util.List;
import org.datanucleus.store.schema.StoreSchemaData;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.ResultSet;
import org.datanucleus.store.schema.ListStoreSchemaData;

public class RDBMSColumnInfo implements ListStoreSchemaData
{
    protected String tableCat;
    protected String tableSchem;
    protected String tableName;
    protected String columnName;
    protected short dataType;
    protected String typeName;
    protected int columnSize;
    protected int decimalDigits;
    protected int numPrecRadix;
    protected int nullable;
    protected String remarks;
    protected String columnDef;
    protected int charOctetLength;
    protected int ordinalPosition;
    protected String isNullable;
    private int hash;
    RDBMSTableInfo tableInfo;
    
    public RDBMSColumnInfo(final ResultSet rs) {
        this.hash = 0;
        try {
            this.tableCat = rs.getString(1);
            this.tableSchem = rs.getString(2);
            this.tableName = rs.getString(3);
            this.columnName = rs.getString(4);
            this.dataType = rs.getShort(5);
            this.typeName = rs.getString(6);
            this.columnSize = rs.getInt(7);
            this.decimalDigits = rs.getInt(9);
            this.numPrecRadix = rs.getInt(10);
            this.nullable = rs.getInt(11);
            this.remarks = rs.getString(12);
            this.columnDef = rs.getString(13);
            this.charOctetLength = rs.getInt(16);
            this.ordinalPosition = rs.getInt(17);
            this.isNullable = rs.getString(18);
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException("Can't read JDBC metadata from result set", e).setFatal();
        }
    }
    
    @Override
    public void setParent(final StoreSchemaData parent) {
        this.tableInfo = (RDBMSTableInfo)parent;
    }
    
    @Override
    public StoreSchemaData getParent() {
        return this.tableInfo;
    }
    
    public void setDecimalDigits(final int digits) {
        this.decimalDigits = digits;
    }
    
    public void setDataType(final short type) {
        this.dataType = type;
    }
    
    public void setColumnSize(final int size) {
        this.columnSize = size;
    }
    
    public int getDecimalDigits() {
        return this.decimalDigits;
    }
    
    public String getIsNullable() {
        return this.isNullable;
    }
    
    public int getNullable() {
        return this.nullable;
    }
    
    public int getColumnSize() {
        return this.columnSize;
    }
    
    public short getDataType() {
        return this.dataType;
    }
    
    public int getNumPrecRadix() {
        return this.numPrecRadix;
    }
    
    public int getCharOctetLength() {
        return this.charOctetLength;
    }
    
    public int getOrdinalPosition() {
        return this.ordinalPosition;
    }
    
    public String getColumnDef() {
        return this.columnDef;
    }
    
    public String getRemarks() {
        return this.remarks;
    }
    
    public String getTypeName() {
        return this.typeName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public String getTableCat() {
        return this.tableCat;
    }
    
    public String getTableSchem() {
        return this.tableSchem;
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
    public void addChild(final StoreSchemaData child) {
    }
    
    @Override
    public void clearChildren() {
    }
    
    @Override
    public StoreSchemaData getChild(final int position) {
        return null;
    }
    
    @Override
    public List getChildren() {
        return null;
    }
    
    @Override
    public int getNumberOfChildren() {
        return 0;
    }
    
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof RDBMSColumnInfo)) {
            return false;
        }
        final RDBMSColumnInfo other = (RDBMSColumnInfo)obj;
        if (this.tableCat == null) {
            if (other.tableCat != null) {
                return false;
            }
        }
        else if (!this.tableCat.equals(other.tableCat)) {
            return false;
        }
        if (this.tableSchem == null) {
            if (other.tableSchem != null) {
                return false;
            }
        }
        else if (!this.tableSchem.equals(other.tableSchem)) {
            return false;
        }
        if (this.tableName.equals(other.tableName) && this.columnName.equals(other.columnName)) {
            return true;
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        if (this.hash == 0) {
            this.hash = (((this.tableCat == null) ? 0 : this.tableCat.hashCode()) ^ ((this.tableSchem == null) ? 0 : this.tableSchem.hashCode()) ^ this.tableName.hashCode() ^ this.columnName.hashCode());
        }
        return this.hash;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer("RDBMSColumnInfo : ");
        str.append("  tableCat        = " + this.tableCat + "\n");
        str.append("  tableSchem      = " + this.tableSchem + "\n");
        str.append("  tableName       = " + this.tableName + "\n");
        str.append("  columnName      = " + this.columnName + "\n");
        str.append("  dataType        = " + this.dataType + "\n");
        str.append("  typeName        = " + this.typeName + "\n");
        str.append("  columnSize      = " + this.columnSize + "\n");
        str.append("  decimalDigits   = " + this.decimalDigits + "\n");
        str.append("  numPrecRadix    = " + this.numPrecRadix + "\n");
        str.append("  nullable        = " + this.nullable + "\n");
        str.append("  remarks         = " + this.remarks + "\n");
        str.append("  columnDef       = " + this.columnDef + "\n");
        str.append("  charOctetLength = " + this.charOctetLength + "\n");
        str.append("  ordinalPosition = " + this.ordinalPosition + "\n");
        str.append("  isNullable      = " + this.isNullable + "\n");
        return str.toString();
    }
}
