// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.exceptions.WrongScaleException;
import org.datanucleus.store.rdbms.exceptions.WrongPrecisionException;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.exceptions.IncompatibleDataTypeException;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.exceptions.ColumnDefinitionException;
import org.datanucleus.util.NucleusLogger;
import java.util.StringTokenizer;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.util.Localiser;

public class ColumnImpl implements Column
{
    private static final Localiser LOCALISER;
    private static final byte PK = 1;
    private static final byte NULLABLE = 2;
    private static final byte UNIQUE = 4;
    private static final byte DEFAULTABLE = 8;
    private static final byte IDENTITY = 16;
    protected DatastoreIdentifier identifier;
    protected ColumnMetaData columnMetaData;
    protected final Table table;
    protected DatastoreMapping datastoreMapping;
    protected final String storedJavaType;
    protected SQLTypeInfo typeInfo;
    protected String constraints;
    protected byte flags;
    protected Object defaultValue;
    protected String[] wrapperFunction;
    
    public ColumnImpl(final Table table, final String javaType, final DatastoreIdentifier identifier, final ColumnMetaData colmd) {
        this.datastoreMapping = null;
        this.table = table;
        this.storedJavaType = javaType;
        this.typeInfo = null;
        this.constraints = null;
        this.flags = 0;
        this.setIdentifier(identifier);
        if (colmd == null) {
            this.columnMetaData = new ColumnMetaData();
        }
        else {
            this.columnMetaData = colmd;
        }
        if (this.columnMetaData.getAllowsNull() != null && this.columnMetaData.isAllowsNull()) {
            this.setNullable();
        }
        if (this.columnMetaData.getUnique()) {
            this.setUnique();
        }
        (this.wrapperFunction = new String[3])[0] = "?";
        this.wrapperFunction[1] = "?";
        this.wrapperFunction[2] = "?";
    }
    
    @Override
    public boolean isUnlimitedLength() {
        return (this.columnMetaData.getJdbcType() != null && this.columnMetaData.getJdbcType().toLowerCase().indexOf("lob") > 0) || (this.columnMetaData.getSqlType() != null && this.columnMetaData.getSqlType().toLowerCase().indexOf("lob") > 0);
    }
    
    @Override
    public DatastoreIdentifier getIdentifier() {
        return this.identifier;
    }
    
    @Override
    public void setIdentifier(final DatastoreIdentifier identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public Table getTable() {
        return this.table;
    }
    
    @Override
    public DatastoreMapping getDatastoreMapping() {
        return this.datastoreMapping;
    }
    
    @Override
    public void setDatastoreMapping(final DatastoreMapping mapping) {
        this.datastoreMapping = mapping;
    }
    
    @Override
    public JavaTypeMapping getJavaTypeMapping() {
        return this.datastoreMapping.getJavaTypeMapping();
    }
    
    @Override
    public String getStoredJavaType() {
        return this.storedJavaType;
    }
    
    @Override
    public final SQLTypeInfo getTypeInfo() {
        return this.typeInfo;
    }
    
    @Override
    public int getJdbcType() {
        return this.typeInfo.getDataType();
    }
    
    @Override
    public RDBMSStoreManager getStoreManager() {
        return this.table.getStoreManager();
    }
    
    private int getSQLPrecision() {
        int sqlPrecision = -1;
        if (this.columnMetaData.getLength() != null && this.columnMetaData.getLength() > 0) {
            sqlPrecision = this.columnMetaData.getLength();
        }
        else if (this.isUnlimitedLength()) {
            final int ulpv = this.getStoreManager().getDatastoreAdapter().getUnlimitedLengthPrecisionValue(this.typeInfo);
            if (ulpv > 0) {
                sqlPrecision = ulpv;
            }
        }
        if (this.typeInfo.getTypeName().toLowerCase().startsWith("bit")) {
            return sqlPrecision * 8;
        }
        return sqlPrecision;
    }
    
    @Override
    public String getSQLDefinition() {
        final StringBuffer def = new StringBuffer(this.identifier.toString());
        if (!StringUtils.isWhitespace(this.columnMetaData.getColumnDdl())) {
            def.append(" ").append(this.columnMetaData.getColumnDdl());
            return def.toString();
        }
        final StringBuffer typeSpec = new StringBuffer(this.typeInfo.getTypeName());
        final DatastoreAdapter adapter = this.getStoreManager().getDatastoreAdapter();
        boolean specifyType = true;
        if (adapter.supportsOption("IdentityColumns") && this.isIdentity() && !adapter.supportsOption("AutoIncrementColumnTypeSpecification")) {
            specifyType = false;
        }
        if (specifyType) {
            if (this.typeInfo.getCreateParams() != null && this.typeInfo.getCreateParams().indexOf(40) >= 0 && this.typeInfo.getTypeName().indexOf(40) < 0) {
                final StringTokenizer toks = new StringTokenizer(this.typeInfo.getCreateParams());
                while (toks.hasMoreTokens()) {
                    final String tok = toks.nextToken();
                    if (tok.startsWith("[") && tok.endsWith("]")) {
                        continue;
                    }
                    typeSpec.append(" " + tok);
                }
            }
            final StringBuffer precSpec = new StringBuffer();
            final int sqlPrecision = this.getSQLPrecision();
            if (sqlPrecision > 0 && this.typeInfo.isAllowsPrecisionSpec()) {
                precSpec.append(sqlPrecision);
                if (this.columnMetaData.getScale() != null) {
                    precSpec.append("," + this.columnMetaData.getScale());
                }
            }
            else if (sqlPrecision > 0 && !this.typeInfo.isAllowsPrecisionSpec()) {
                NucleusLogger.DATASTORE_SCHEMA.warn(ColumnImpl.LOCALISER.msg("020183", this.toString()));
            }
            final int lParenIdx = typeSpec.toString().indexOf(40);
            final int rParenIdx = typeSpec.toString().indexOf(41, lParenIdx);
            if (lParenIdx > 0 && rParenIdx > 0) {
                if (precSpec.length() > 0) {
                    typeSpec.replace(lParenIdx + 1, rParenIdx, precSpec.toString());
                }
                else if (rParenIdx == lParenIdx + 1) {
                    throw new ColumnDefinitionException(ColumnImpl.LOCALISER.msg("020184", this.toString()));
                }
            }
            else if (precSpec.length() > 0) {
                typeSpec.append('(');
                typeSpec.append(precSpec.toString());
                typeSpec.append(')');
            }
            def.append(" " + typeSpec.toString());
        }
        if (adapter.supportsOption("ColumnOptions_DefaultBeforeNull") && adapter.supportsOption("ColumnOptions_DefaultKeyword") && this.columnMetaData.getDefaultValue() != null) {
            def.append(" ").append(this.getDefaultDefinition());
        }
        if (this.isIdentity() && this.isPrimaryKey() && adapter.supportsOption("AutoIncrementPkInCreateTableColumnDef")) {
            def.append(" PRIMARY KEY");
        }
        if (!adapter.supportsOption("IdentityColumns") || !this.isIdentity() || adapter.supportsOption("AutoIncrementNullSpecification")) {
            if (!this.isNullable()) {
                if (this.columnMetaData.getDefaultValue() == null || adapter.supportsOption("ColumnOptions_DefaultWithNotNull")) {
                    def.append(" NOT NULL");
                }
            }
            else if (this.typeInfo.getNullable() == 1 && adapter.supportsOption("ColumnOptions_NullsKeyword")) {
                def.append(" NULL");
            }
        }
        if (!adapter.supportsOption("ColumnOptions_DefaultBeforeNull") && adapter.supportsOption("ColumnOptions_DefaultKeyword") && this.columnMetaData.getDefaultValue() != null) {
            def.append(" ").append(this.getDefaultDefinition());
        }
        if (adapter.supportsOption("CheckInCreateStatements") && this.constraints != null) {
            def.append(" " + this.constraints.toString());
        }
        if (adapter.supportsOption("IdentityColumns") && this.isIdentity()) {
            def.append(" " + adapter.getAutoIncrementKeyword());
        }
        if (this.isUnique() && !adapter.supportsOption("UniqueInEndCreateStatements")) {
            def.append(" UNIQUE");
        }
        return def.toString();
    }
    
    private String getDefaultDefinition() {
        if (this.columnMetaData.getDefaultValue().equalsIgnoreCase("#NULL")) {
            return "DEFAULT NULL";
        }
        if (this.typeInfo.getTypeName().toUpperCase().indexOf("CHAR") >= 0 || this.typeInfo.getTypeName().toUpperCase().indexOf("LOB") >= 0) {
            return "DEFAULT '" + this.columnMetaData.getDefaultValue() + "'";
        }
        if (this.typeInfo.getTypeName().toUpperCase().indexOf("BIT") == 0 && (this.columnMetaData.getDefaultValue().equalsIgnoreCase("true") || this.columnMetaData.getDefaultValue().equalsIgnoreCase("false"))) {
            return "DEFAULT '" + this.columnMetaData.getDefaultValue() + "'";
        }
        return "DEFAULT " + this.columnMetaData.getDefaultValue();
    }
    
    @Override
    public void initializeColumnInfoFromDatastore(final RDBMSColumnInfo ci) {
        final String column_default = ci.getColumnDef();
        if (column_default != null) {
            this.setDefaultValue(column_default.replace("'", "").replace("\"", "").replace(")", "").replace("(", ""));
        }
        try {
            this.setIdentity(this.getStoreManager().getDatastoreAdapter().isIdentityFieldDataType(ci.getColumnDef()));
        }
        catch (UnsupportedOperationException ex) {}
    }
    
    @Override
    public void validate(final RDBMSColumnInfo ci) {
        if (!this.typeInfo.isCompatibleWith(ci)) {
            throw new IncompatibleDataTypeException(this, this.typeInfo.getDataType(), ci.getDataType());
        }
        if (ci.getDataType() == 1111) {
            return;
        }
        if (this.table instanceof TableImpl) {
            if (this.typeInfo.isAllowsPrecisionSpec()) {
                final int actualPrecision = ci.getColumnSize();
                final int actualScale = ci.getDecimalDigits();
                final int sqlPrecision = this.getSQLPrecision();
                if (sqlPrecision > 0 && actualPrecision > 0 && sqlPrecision != actualPrecision) {
                    if (this.columnMetaData != null && this.columnMetaData.getParent() != null && this.columnMetaData.getParent() instanceof AbstractMemberMetaData) {
                        throw new WrongPrecisionException(this.toString(), sqlPrecision, actualPrecision, ((AbstractMemberMetaData)this.columnMetaData.getParent()).getFullFieldName());
                    }
                    throw new WrongPrecisionException(this.toString(), sqlPrecision, actualPrecision);
                }
                else if (this.columnMetaData.getScale() != null && actualScale >= 0 && this.columnMetaData.getScale() != actualScale) {
                    if (this.columnMetaData.getParent() != null && this.columnMetaData.getParent() instanceof AbstractMemberMetaData) {
                        throw new WrongScaleException(this.toString(), this.columnMetaData.getScale(), actualScale, ((AbstractMemberMetaData)this.columnMetaData.getParent()).getFullFieldName());
                    }
                    throw new WrongScaleException(this.toString(), this.columnMetaData.getScale(), actualScale);
                }
            }
            final String actualIsNullable = ci.getIsNullable();
            if (actualIsNullable.length() > 0) {
                switch (Character.toUpperCase(actualIsNullable.charAt(0))) {
                    case 'Y': {
                        if (!this.isNullable()) {
                            NucleusLogger.DATASTORE.warn(ColumnImpl.LOCALISER.msg("020025", this));
                            break;
                        }
                        break;
                    }
                }
            }
            try {
                if (this.isIdentity() != this.getStoreManager().getDatastoreAdapter().isIdentityFieldDataType(ci.getColumnDef())) {
                    if (this.isIdentity()) {
                        throw new NucleusException("Expected an auto increment column (" + this.getIdentifier() + ") in the database, but it is not").setFatal();
                    }
                    throw new NucleusException("According to the user metadata, the column (" + this.getIdentifier() + ") is not auto incremented, but the database says it is.").setFatal();
                }
            }
            catch (UnsupportedOperationException ex) {}
        }
    }
    
    @Override
    public final Column setTypeInfo(final SQLTypeInfo typeInfo) {
        if (this.typeInfo == null) {
            this.typeInfo = typeInfo;
        }
        return this;
    }
    
    @Override
    public final Column setConstraints(final String constraints) {
        this.constraints = constraints;
        return this;
    }
    
    @Override
    public final void setAsPrimaryKey() {
        this.flags |= 0x1;
        this.flags &= 0xFFFFFFFD;
    }
    
    @Override
    public final Column setNullable() {
        this.flags |= 0x2;
        return this;
    }
    
    @Override
    public final Column setDefaultable() {
        this.flags |= 0x8;
        return this;
    }
    
    @Override
    public final Column setUnique() {
        this.flags |= 0x4;
        return this;
    }
    
    @Override
    public Column setIdentity(final boolean identity) {
        if (identity) {
            this.flags |= 0x10;
        }
        else {
            this.flags &= 0xFFFFFFEF;
        }
        return this;
    }
    
    @Override
    public final boolean isPrimaryKey() {
        return (this.flags & 0x1) != 0x0;
    }
    
    @Override
    public final boolean isNullable() {
        return (this.flags & 0x2) != 0x0;
    }
    
    @Override
    public final boolean isDefaultable() {
        return (this.flags & 0x8) != 0x0;
    }
    
    @Override
    public final boolean isUnique() {
        return (this.flags & 0x4) != 0x0;
    }
    
    @Override
    public boolean isIdentity() {
        return (this.flags & 0x10) != 0x0;
    }
    
    @Override
    public String applySelectFunction(final String replacementValue) {
        if (replacementValue == null) {
            return this.wrapperFunction[0];
        }
        if (this.wrapperFunction[0] != null) {
            return this.wrapperFunction[0].replace("?", replacementValue);
        }
        return replacementValue;
    }
    
    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    public void setDefaultValue(final Object object) {
        this.defaultValue = object;
    }
    
    @Override
    public final ColumnMetaData getColumnMetaData() {
        return this.columnMetaData;
    }
    
    @Override
    public AbstractMemberMetaData getMemberMetaData() {
        if (this.columnMetaData != null && this.columnMetaData.getParent() instanceof AbstractMemberMetaData) {
            return (AbstractMemberMetaData)this.columnMetaData.getParent();
        }
        return null;
    }
    
    @Override
    public void setColumnMetaData(final ColumnMetaData colmd) {
        if (colmd == null) {
            return;
        }
        if (colmd.getJdbcType() != null) {
            this.columnMetaData.setJdbcType(colmd.getJdbcType());
        }
        if (colmd.getSqlType() != null) {
            this.columnMetaData.setSqlType(colmd.getSqlType());
        }
        if (colmd.getName() != null) {
            this.columnMetaData.setName(colmd.getName());
        }
        if (colmd.getAllowsNull() != null) {
            this.columnMetaData.setAllowsNull(colmd.isAllowsNull());
        }
        if (colmd.getLength() != null) {
            this.columnMetaData.setLength(colmd.getLength());
        }
        if (colmd.getScale() != null) {
            this.columnMetaData.setScale(colmd.getScale());
        }
        if (colmd.getAllowsNull() != null && colmd.isAllowsNull()) {
            this.setNullable();
        }
        if (colmd.getUnique()) {
            this.setUnique();
        }
    }
    
    @Override
    public String getConstraints() {
        return this.constraints;
    }
    
    @Override
    public final void checkPrimitive() throws ColumnDefinitionException {
    }
    
    @Override
    public final void checkInteger() throws ColumnDefinitionException {
    }
    
    @Override
    public final void checkDecimal() throws ColumnDefinitionException {
    }
    
    @Override
    public final void checkString() throws ColumnDefinitionException {
        if (this.columnMetaData.getJdbcType() == null) {
            this.columnMetaData.setJdbcType("VARCHAR");
        }
        if (this.columnMetaData.getLength() == null) {
            this.columnMetaData.setLength(this.getStoreManager().getIntProperty("datanucleus.rdbms.stringDefaultLength"));
        }
    }
    
    @Override
    public void copyConfigurationTo(final Column colIn) {
        final ColumnImpl col = (ColumnImpl)colIn;
        col.typeInfo = this.typeInfo;
        final ColumnImpl columnImpl = col;
        columnImpl.flags |= this.flags;
        final ColumnImpl columnImpl2 = col;
        columnImpl2.flags &= 0xFFFFFFFE;
        final ColumnImpl columnImpl3 = col;
        columnImpl3.flags &= 0xFFFFFFFB;
        final ColumnImpl columnImpl4 = col;
        columnImpl4.flags &= 0xFFFFFFFD;
        final ColumnImpl columnImpl5 = col;
        columnImpl5.flags &= 0xFFFFFFEF;
        final ColumnImpl columnImpl6 = col;
        columnImpl6.flags &= 0xFFFFFFF7;
        col.defaultValue = this.defaultValue;
        col.wrapperFunction = this.wrapperFunction;
        if (this.columnMetaData.getJdbcType() != null) {
            col.columnMetaData.setJdbcType(this.columnMetaData.getJdbcType());
        }
        if (this.columnMetaData.getSqlType() != null) {
            col.columnMetaData.setSqlType(this.columnMetaData.getSqlType());
        }
        if (this.columnMetaData.getLength() != null) {
            col.getColumnMetaData().setLength(this.columnMetaData.getLength());
        }
        if (this.columnMetaData.getScale() != null) {
            col.getColumnMetaData().setScale(this.getColumnMetaData().getScale());
        }
    }
    
    @Override
    public void setWrapperFunction(final String wrapperFunction, final int wrapperMode) {
        if (wrapperFunction != null && wrapperMode == 0 && wrapperFunction.indexOf("?") < 0) {
            throw new NucleusUserException("Wrapping function must have one ? (question mark). e.g. SQRT(?)");
        }
        this.wrapperFunction[wrapperMode] = wrapperFunction;
    }
    
    @Override
    public String getWrapperFunction(final int wrapperMode) {
        return this.wrapperFunction[wrapperMode];
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ColumnImpl)) {
            return false;
        }
        final ColumnImpl col = (ColumnImpl)obj;
        return this.table.equals(col.table) && this.identifier.equals(col.identifier);
    }
    
    @Override
    public int hashCode() {
        return this.table.hashCode() ^ this.identifier.hashCode();
    }
    
    @Override
    public String toString() {
        return this.table.toString() + "." + this.identifier;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
