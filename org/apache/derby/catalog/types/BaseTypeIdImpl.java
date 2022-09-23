// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.services.io.Formatable;

public class BaseTypeIdImpl implements Formatable
{
    private int formatId;
    protected String schemaName;
    String unqualifiedName;
    transient int JDBCTypeId;
    
    public BaseTypeIdImpl() {
    }
    
    public BaseTypeIdImpl(final int formatId) {
        this.formatId = formatId;
        this.setTypeIdSpecificInstanceVariables();
    }
    
    BaseTypeIdImpl(final String unqualifiedName) {
        this.schemaName = null;
        this.unqualifiedName = unqualifiedName;
    }
    
    BaseTypeIdImpl(final String schemaName, final String unqualifiedName) {
        this.schemaName = schemaName;
        this.unqualifiedName = unqualifiedName;
    }
    
    public String getSQLTypeName() {
        if (this.schemaName == null) {
            return this.unqualifiedName;
        }
        return IdUtil.mkQualifiedName(this.schemaName, this.unqualifiedName);
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public String getUnqualifiedName() {
        return this.unqualifiedName;
    }
    
    public boolean isAnsiUDT() {
        return this.schemaName != null;
    }
    
    public int getJDBCTypeId() {
        return this.JDBCTypeId;
    }
    
    public String toParsableString(final TypeDescriptor typeDescriptor) {
        String s = this.getSQLTypeName();
        switch (this.getTypeFormatId()) {
            case 28:
            case 30: {
                final int index = s.indexOf(41);
                s = s.substring(0, index) + typeDescriptor.getMaximumWidth() + s.substring(index);
                break;
            }
            case 17:
            case 25:
            case 442:
            case 446: {
                s = s + "(" + typeDescriptor.getMaximumWidth() + ")";
                break;
            }
            case 198: {
                s = s + "(" + typeDescriptor.getPrecision() + "," + typeDescriptor.getScale() + ")";
                break;
            }
        }
        return s;
    }
    
    public boolean userType() {
        return false;
    }
    
    public String toString() {
        return MessageService.getTextMessage("44X00.U") + ": " + this.getSQLTypeName();
    }
    
    public boolean equals(final Object o) {
        return o instanceof BaseTypeIdImpl && this.getSQLTypeName().equals(((BaseTypeIdImpl)o).getSQLTypeName());
    }
    
    public int hashCode() {
        return this.getSQLTypeName().hashCode();
    }
    
    public int getTypeFormatId() {
        if (this.formatId != 0) {
            return this.formatId;
        }
        if ("BOOLEAN".equals(this.unqualifiedName)) {
            return 16;
        }
        if ("BIGINT".equals(this.unqualifiedName)) {
            return 23;
        }
        if ("INTEGER".equals(this.unqualifiedName)) {
            return 19;
        }
        if ("SMALLINT".equals(this.unqualifiedName)) {
            return 22;
        }
        if ("TINYINT".equals(this.unqualifiedName)) {
            return 196;
        }
        if ("BIGINT".equals(this.unqualifiedName)) {
            return 23;
        }
        if ("DECIMAL".equals(this.unqualifiedName)) {
            return 198;
        }
        if ("NUMERIC".equals(this.unqualifiedName)) {
            return 198;
        }
        if ("DOUBLE".equals(this.unqualifiedName)) {
            return 18;
        }
        if ("REAL".equals(this.unqualifiedName)) {
            return 20;
        }
        if ("REF".equals(this.unqualifiedName)) {
            return 21;
        }
        if ("CHAR".equals(this.unqualifiedName)) {
            return 17;
        }
        if ("VARCHAR".equals(this.unqualifiedName)) {
            return 25;
        }
        if ("LONG VARCHAR".equals(this.unqualifiedName)) {
            return 231;
        }
        if ("CLOB".equals(this.unqualifiedName)) {
            return 446;
        }
        if ("CHAR () FOR BIT DATA".equals(this.unqualifiedName)) {
            return 28;
        }
        if ("CHAR FOR BIT DATA".equals(this.unqualifiedName)) {
            return 28;
        }
        if ("VARCHAR () FOR BIT DATA".equals(this.unqualifiedName)) {
            return 30;
        }
        if ("VARCHAR FOR BIT DATA".equals(this.unqualifiedName)) {
            return 30;
        }
        if ("LONG VARCHAR FOR BIT DATA".equals(this.unqualifiedName)) {
            return 233;
        }
        if ("BLOB".equals(this.unqualifiedName)) {
            return 442;
        }
        if ("DATE".equals(this.unqualifiedName)) {
            return 32;
        }
        if ("TIME".equals(this.unqualifiedName)) {
            return 33;
        }
        if ("TIMESTAMP".equals(this.unqualifiedName)) {
            return 34;
        }
        if ("XML".equals(this.unqualifiedName)) {
            return 457;
        }
        return 0;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.unqualifiedName = objectInput.readUTF();
        if (this.unqualifiedName.charAt(0) == '\"') {
            this.schemaName = this.stripQuotes(this.unqualifiedName);
            this.unqualifiedName = objectInput.readUTF();
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        if (this.schemaName == null) {
            objectOutput.writeUTF(this.unqualifiedName);
        }
        else {
            objectOutput.writeUTF(this.doubleQuote(this.schemaName));
            objectOutput.writeUTF(this.unqualifiedName);
        }
    }
    
    private void setTypeIdSpecificInstanceVariables() {
        switch (this.getTypeFormatId()) {
            case 16: {
                this.schemaName = null;
                this.unqualifiedName = "BOOLEAN";
                this.JDBCTypeId = 16;
                break;
            }
            case 19: {
                this.schemaName = null;
                this.unqualifiedName = "INTEGER";
                this.JDBCTypeId = 4;
                break;
            }
            case 22: {
                this.schemaName = null;
                this.unqualifiedName = "SMALLINT";
                this.JDBCTypeId = 5;
                break;
            }
            case 196: {
                this.schemaName = null;
                this.unqualifiedName = "TINYINT";
                this.JDBCTypeId = -6;
                break;
            }
            case 23: {
                this.schemaName = null;
                this.unqualifiedName = "BIGINT";
                this.JDBCTypeId = -5;
                break;
            }
            case 198: {
                this.schemaName = null;
                this.unqualifiedName = "DECIMAL";
                this.JDBCTypeId = 3;
                break;
            }
            case 18: {
                this.schemaName = null;
                this.unqualifiedName = "DOUBLE";
                this.JDBCTypeId = 8;
                break;
            }
            case 20: {
                this.schemaName = null;
                this.unqualifiedName = "REAL";
                this.JDBCTypeId = 7;
                break;
            }
            case 21: {
                this.schemaName = null;
                this.unqualifiedName = "REF";
                this.JDBCTypeId = 1111;
                break;
            }
            case 17: {
                this.schemaName = null;
                this.unqualifiedName = "CHAR";
                this.JDBCTypeId = 1;
                break;
            }
            case 25: {
                this.schemaName = null;
                this.unqualifiedName = "VARCHAR";
                this.JDBCTypeId = 12;
                break;
            }
            case 231: {
                this.schemaName = null;
                this.unqualifiedName = "LONG VARCHAR";
                this.JDBCTypeId = -1;
                break;
            }
            case 446: {
                this.schemaName = null;
                this.unqualifiedName = "CLOB";
                this.JDBCTypeId = 2005;
                break;
            }
            case 28: {
                this.schemaName = null;
                this.unqualifiedName = "CHAR () FOR BIT DATA";
                this.JDBCTypeId = -2;
                break;
            }
            case 30: {
                this.schemaName = null;
                this.unqualifiedName = "VARCHAR () FOR BIT DATA";
                this.JDBCTypeId = -3;
                break;
            }
            case 233: {
                this.schemaName = null;
                this.unqualifiedName = "LONG VARCHAR FOR BIT DATA";
                this.JDBCTypeId = -4;
                break;
            }
            case 442: {
                this.schemaName = null;
                this.unqualifiedName = "BLOB";
                this.JDBCTypeId = 2004;
                break;
            }
            case 32: {
                this.schemaName = null;
                this.unqualifiedName = "DATE";
                this.JDBCTypeId = 91;
                break;
            }
            case 33: {
                this.schemaName = null;
                this.unqualifiedName = "TIME";
                this.JDBCTypeId = 92;
                break;
            }
            case 34: {
                this.schemaName = null;
                this.unqualifiedName = "TIMESTAMP";
                this.JDBCTypeId = 93;
                break;
            }
            case 457: {
                this.schemaName = null;
                this.unqualifiedName = "XML";
                this.JDBCTypeId = 2009;
                break;
            }
        }
    }
    
    private String doubleQuote(final String str) {
        return '\"' + str + '\"';
    }
    
    private String stripQuotes(final String s) {
        return s.substring(1, s.length() - 1);
    }
}
