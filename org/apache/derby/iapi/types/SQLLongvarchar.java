// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.util.StringUtil;
import java.text.RuleBasedCollator;
import org.apache.derby.iapi.error.StandardException;

public class SQLLongvarchar extends SQLVarchar
{
    public String getTypeName() {
        return "LONG VARCHAR";
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        try {
            return new SQLLongvarchar(this.getString());
        }
        catch (StandardException ex) {
            return null;
        }
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLLongvarchar();
    }
    
    public StringDataValue getValue(final RuleBasedCollator ruleBasedCollator) {
        if (ruleBasedCollator == null) {
            return this;
        }
        final CollatorSQLLongvarchar collatorSQLLongvarchar = new CollatorSQLLongvarchar(ruleBasedCollator);
        collatorSQLLongvarchar.copyState(this);
        return collatorSQLLongvarchar;
    }
    
    public int getTypeFormatId() {
        return 235;
    }
    
    public SQLLongvarchar() {
    }
    
    public SQLLongvarchar(final String s) {
        super(s);
    }
    
    protected void normalize(final DataTypeDescriptor dataTypeDescriptor, final String value) throws StandardException {
        if (value.length() > dataTypeDescriptor.getMaximumWidth()) {
            throw StandardException.newException("22001", this.getTypeName(), StringUtil.formatForPrint(value), String.valueOf(dataTypeDescriptor.getMaximumWidth()));
        }
        this.setValue(value);
    }
    
    public StringDataValue concatenate(final StringDataValue stringDataValue, final StringDataValue stringDataValue2, StringDataValue concatenate) throws StandardException {
        concatenate = super.concatenate(stringDataValue, stringDataValue2, concatenate);
        if (concatenate.getString() != null && concatenate.getString().length() > 32700) {
            throw StandardException.newException("54006", "CONCAT", String.valueOf(32700));
        }
        return concatenate;
    }
    
    public int typePrecedence() {
        return 12;
    }
}
