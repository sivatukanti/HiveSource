// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.sql.Clob;
import java.text.RuleBasedCollator;
import org.apache.derby.iapi.error.StandardException;

public class SQLVarchar extends SQLChar
{
    public String getTypeName() {
        return "VARCHAR";
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        try {
            return new SQLVarchar(this.getString());
        }
        catch (StandardException ex) {
            return null;
        }
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLVarchar();
    }
    
    public StringDataValue getValue(final RuleBasedCollator ruleBasedCollator) {
        if (ruleBasedCollator == null) {
            return this;
        }
        final CollatorSQLVarchar collatorSQLVarchar = new CollatorSQLVarchar(ruleBasedCollator);
        collatorSQLVarchar.copyState(this);
        return collatorSQLVarchar;
    }
    
    public int getTypeFormatId() {
        return 85;
    }
    
    public SQLVarchar() {
    }
    
    public SQLVarchar(final String s) {
        super(s);
    }
    
    public SQLVarchar(final Clob clob) {
        super(clob);
    }
    
    public SQLVarchar(final char[] array) {
        super(array);
    }
    
    public void normalize(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        this.normalize(dataTypeDescriptor, dataValueDescriptor.getString());
    }
    
    protected void normalize(final DataTypeDescriptor dataTypeDescriptor, String substring) throws StandardException {
        final int maximumWidth = dataTypeDescriptor.getMaximumWidth();
        final int length = substring.length();
        if (length > maximumWidth) {
            this.hasNonBlankChars(substring, maximumWidth, length);
            substring = substring.substring(0, maximumWidth);
        }
        this.setValue(substring);
    }
    
    public int typePrecedence() {
        return 10;
    }
    
    protected final int growBy() {
        return 4096;
    }
}
