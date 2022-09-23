// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.HashSet;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.StringUtils;
import java.util.Set;

public class ColumnMetaData extends MetaData
{
    private static Set<String> VALID_JDBC_TYPES;
    protected String name;
    protected String target;
    protected String targetMember;
    protected String jdbcType;
    protected String sqlType;
    protected Integer length;
    protected Integer scale;
    protected Boolean allowsNull;
    protected String defaultValue;
    protected String insertValue;
    protected boolean insertable;
    protected boolean updateable;
    protected boolean unique;
    protected String columnDdl;
    protected Integer position;
    
    public ColumnMetaData(final ColumnMetaData colmd) {
        super(null, colmd);
        this.insertable = true;
        this.updateable = true;
        this.unique = false;
        this.columnDdl = null;
        this.position = null;
        this.name = colmd.getName();
        this.target = colmd.getTarget();
        this.targetMember = colmd.getTargetMember();
        this.setJdbcType(colmd.getJdbcType());
        this.sqlType = colmd.getSqlType();
        this.length = colmd.getLength();
        this.scale = colmd.getScale();
        this.allowsNull = colmd.allowsNull;
        this.defaultValue = colmd.getDefaultValue();
        this.insertValue = colmd.getInsertValue();
        this.insertable = colmd.getInsertable();
        this.updateable = colmd.getUpdateable();
        this.unique = colmd.getUnique();
        this.position = colmd.getPosition();
    }
    
    public ColumnMetaData() {
        this.insertable = true;
        this.updateable = true;
        this.unique = false;
        this.columnDdl = null;
        this.position = null;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public ColumnMetaData setDefaultValue(final String defaultValue) {
        this.defaultValue = (StringUtils.isWhitespace(defaultValue) ? null : defaultValue);
        return this;
    }
    
    public String getColumnDdl() {
        return this.columnDdl;
    }
    
    public void setColumnDdl(final String columnDdl) {
        this.columnDdl = columnDdl;
    }
    
    public boolean getInsertable() {
        return this.insertable;
    }
    
    public ColumnMetaData setInsertable(final boolean insertable) {
        this.insertable = insertable;
        return this;
    }
    
    public ColumnMetaData setInsertable(final String insertable) {
        if (!StringUtils.isWhitespace(insertable)) {
            this.insertable = Boolean.parseBoolean(insertable);
        }
        return this;
    }
    
    public String getInsertValue() {
        return this.insertValue;
    }
    
    public ColumnMetaData setInsertValue(final String insertValue) {
        this.insertValue = (StringUtils.isWhitespace(insertValue) ? null : insertValue);
        return this;
    }
    
    public String getJdbcType() {
        return this.jdbcType;
    }
    
    public ColumnMetaData setJdbcType(final String jdbcType) {
        if (StringUtils.isWhitespace(jdbcType)) {
            this.jdbcType = null;
        }
        else if (ColumnMetaData.VALID_JDBC_TYPES.contains(jdbcType.toUpperCase())) {
            this.jdbcType = jdbcType;
        }
        else {
            NucleusLogger.METADATA.warn("Metadata has jdbc-type of " + jdbcType + " yet this is not valid. Ignored");
        }
        return this;
    }
    
    public Boolean isIntegralBased() {
        if (this.jdbcType != null && (this.jdbcType.equalsIgnoreCase("INTEGER") || this.jdbcType.equalsIgnoreCase("TINYINT") || this.jdbcType.equalsIgnoreCase("SMALLINT"))) {
            return true;
        }
        return null;
    }
    
    public Boolean isFloatingPointBased() {
        if (this.jdbcType != null && (this.jdbcType.equalsIgnoreCase("DECIMAL") || this.jdbcType.equalsIgnoreCase("FLOAT") || this.jdbcType.equalsIgnoreCase("REAL") || this.jdbcType.equalsIgnoreCase("NUMERIC"))) {
            return true;
        }
        return null;
    }
    
    public Boolean isStringBased() {
        if (this.jdbcType != null && (this.jdbcType.equalsIgnoreCase("CHAR") || this.jdbcType.equalsIgnoreCase("VARCHAR") || this.jdbcType.equalsIgnoreCase("CLOB") || this.jdbcType.equalsIgnoreCase("LONGVARCHAR"))) {
            return true;
        }
        return null;
    }
    
    public Integer getLength() {
        return this.length;
    }
    
    public ColumnMetaData setLength(final Integer length) {
        if (length != null && length > 0) {
            this.length = length;
        }
        return this;
    }
    
    public ColumnMetaData setLength(final String length) {
        if (!StringUtils.isWhitespace(length)) {
            try {
                final int val = Integer.parseInt(length);
                if (val > 0) {
                    this.length = val;
                }
            }
            catch (NumberFormatException ex) {}
        }
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ColumnMetaData setName(final String name) {
        this.name = (StringUtils.isWhitespace(name) ? null : name);
        return this;
    }
    
    public Integer getScale() {
        return this.scale;
    }
    
    public ColumnMetaData setScale(final Integer scale) {
        if (scale != null && scale > 0) {
            this.scale = scale;
        }
        return this;
    }
    
    public ColumnMetaData setScale(final String scale) {
        if (!StringUtils.isWhitespace(scale)) {
            try {
                final int val = Integer.parseInt(scale);
                if (val > 0) {
                    this.scale = val;
                }
            }
            catch (NumberFormatException ex) {}
        }
        return this;
    }
    
    public String getSqlType() {
        return this.sqlType;
    }
    
    public ColumnMetaData setSqlType(final String sqlType) {
        this.sqlType = (StringUtils.isWhitespace(sqlType) ? null : sqlType);
        return this;
    }
    
    public String getTarget() {
        return this.target;
    }
    
    public ColumnMetaData setTarget(final String target) {
        this.target = (StringUtils.isWhitespace(target) ? null : target);
        return this;
    }
    
    public String getTargetMember() {
        return this.targetMember;
    }
    
    public ColumnMetaData setTargetMember(final String targetMember) {
        this.targetMember = (StringUtils.isWhitespace(targetMember) ? null : targetMember);
        return this;
    }
    
    public Integer getPosition() {
        if (this.hasExtension("index")) {
            try {
                return Integer.valueOf(this.getValueForExtension("index"));
            }
            catch (NumberFormatException ex) {}
        }
        return this.position;
    }
    
    public ColumnMetaData setPosition(final int pos) {
        if (pos >= 0) {
            this.position = pos;
        }
        else {
            this.position = null;
        }
        return this;
    }
    
    public ColumnMetaData setPosition(final String pos) {
        if (!StringUtils.isWhitespace(pos)) {
            try {
                final int val = Integer.parseInt(pos);
                if (val >= 0) {
                    this.position = val;
                }
            }
            catch (NumberFormatException ex) {}
        }
        return this;
    }
    
    public boolean getUnique() {
        return this.unique;
    }
    
    public ColumnMetaData setUnique(final boolean unique) {
        this.unique = unique;
        return this;
    }
    
    public ColumnMetaData setUnique(final String unique) {
        if (!StringUtils.isWhitespace(unique)) {
            this.unique = Boolean.parseBoolean(unique);
        }
        return this;
    }
    
    public boolean getUpdateable() {
        return this.updateable;
    }
    
    public ColumnMetaData setUpdateable(final boolean updateable) {
        this.updateable = updateable;
        return this;
    }
    
    public ColumnMetaData setUpdateable(final String updateable) {
        if (!StringUtils.isWhitespace(updateable)) {
            this.updateable = Boolean.parseBoolean(updateable);
        }
        return this;
    }
    
    public boolean isAllowsNull() {
        return this.allowsNull != null && this.allowsNull;
    }
    
    public Boolean getAllowsNull() {
        return this.allowsNull;
    }
    
    public ColumnMetaData setAllowsNull(final Boolean allowsNull) {
        this.allowsNull = allowsNull;
        return this;
    }
    
    public ColumnMetaData setAllowsNull(final String allowsNull) {
        if (!StringUtils.isWhitespace(allowsNull)) {
            this.allowsNull = Boolean.parseBoolean(allowsNull);
        }
        return this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<column");
        if (this.name != null) {
            sb.append(" name=\"" + this.name + "\"");
        }
        if (this.target != null) {
            sb.append(" target=\"" + this.target + "\"");
        }
        if (this.targetMember != null) {
            sb.append(" target-field=\"" + this.targetMember + "\"");
        }
        if (this.jdbcType != null) {
            sb.append(" jdbc-type=\"" + this.jdbcType + "\"");
        }
        if (this.sqlType != null) {
            sb.append(" sql-type=\"" + this.sqlType + "\"");
        }
        if (this.allowsNull != null) {
            sb.append(" allows-null=\"" + this.allowsNull + "\"");
        }
        if (this.length != null) {
            sb.append(" length=\"" + this.length + "\"");
        }
        if (this.scale != null) {
            sb.append(" scale=\"" + this.scale + "\"");
        }
        if (this.defaultValue != null) {
            sb.append(" default-value=\"" + this.defaultValue + "\"");
        }
        if (this.insertValue != null) {
            sb.append(" insert-value=\"" + this.insertValue + "\"");
        }
        if (this.position != null) {
            sb.append(" position=\"" + this.position + "\"");
        }
        if (this.extensions != null && this.extensions.size() > 0) {
            sb.append(">\n");
            sb.append(super.toString(prefix + indent, indent));
            sb.append(prefix).append("</column>\n");
        }
        else {
            sb.append("/>\n");
        }
        return sb.toString();
    }
    
    static {
        (ColumnMetaData.VALID_JDBC_TYPES = new HashSet<String>()).add("BIGINT");
        ColumnMetaData.VALID_JDBC_TYPES.add("BINARY");
        ColumnMetaData.VALID_JDBC_TYPES.add("BIT");
        ColumnMetaData.VALID_JDBC_TYPES.add("BLOB");
        ColumnMetaData.VALID_JDBC_TYPES.add("BOOLEAN");
        ColumnMetaData.VALID_JDBC_TYPES.add("CHAR");
        ColumnMetaData.VALID_JDBC_TYPES.add("CLOB");
        ColumnMetaData.VALID_JDBC_TYPES.add("DATALINK");
        ColumnMetaData.VALID_JDBC_TYPES.add("DATE");
        ColumnMetaData.VALID_JDBC_TYPES.add("DECIMAL");
        ColumnMetaData.VALID_JDBC_TYPES.add("DOUBLE");
        ColumnMetaData.VALID_JDBC_TYPES.add("FLOAT");
        ColumnMetaData.VALID_JDBC_TYPES.add("INTEGER");
        ColumnMetaData.VALID_JDBC_TYPES.add("LONGVARBINARY");
        ColumnMetaData.VALID_JDBC_TYPES.add("LONGVARCHAR");
        ColumnMetaData.VALID_JDBC_TYPES.add("NUMERIC");
        ColumnMetaData.VALID_JDBC_TYPES.add("REAL");
        ColumnMetaData.VALID_JDBC_TYPES.add("SMALLINT");
        ColumnMetaData.VALID_JDBC_TYPES.add("TIME");
        ColumnMetaData.VALID_JDBC_TYPES.add("TIMESTAMP");
        ColumnMetaData.VALID_JDBC_TYPES.add("TINYINT");
        ColumnMetaData.VALID_JDBC_TYPES.add("VARBINARY");
        ColumnMetaData.VALID_JDBC_TYPES.add("VARCHAR");
        ColumnMetaData.VALID_JDBC_TYPES.add("LONGNVARCHAR");
        ColumnMetaData.VALID_JDBC_TYPES.add("NVARCHAR");
        ColumnMetaData.VALID_JDBC_TYPES.add("NCHAR");
        ColumnMetaData.VALID_JDBC_TYPES.add("NCLOB");
    }
}
