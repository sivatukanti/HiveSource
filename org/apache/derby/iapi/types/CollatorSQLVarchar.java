// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;
import java.text.RuleBasedCollator;

class CollatorSQLVarchar extends SQLVarchar implements CollationElementsInterface
{
    private WorkHorseForCollatorDatatypes holderForCollationSensitiveInfo;
    
    CollatorSQLVarchar(final RuleBasedCollator collator) {
        this.setCollator(collator);
    }
    
    CollatorSQLVarchar(final String s, final RuleBasedCollator collator) {
        super(s);
        this.setCollator(collator);
    }
    
    private void setCollator(final RuleBasedCollator ruleBasedCollator) {
        this.holderForCollationSensitiveInfo = new WorkHorseForCollatorDatatypes(ruleBasedCollator, this);
    }
    
    protected RuleBasedCollator getCollatorForCollation() throws StandardException {
        return this.holderForCollationSensitiveInfo.getCollatorForCollation();
    }
    
    public boolean hasSingleCollationElement() throws StandardException {
        return this.holderForCollationSensitiveInfo.hasSingleCollationElement();
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        try {
            return new CollatorSQLVarchar(this.getString(), this.holderForCollationSensitiveInfo.getCollatorForCollation());
        }
        catch (StandardException ex) {
            return null;
        }
    }
    
    public DataValueDescriptor getNewNull() {
        return new CollatorSQLVarchar(this.holderForCollationSensitiveInfo.getCollatorForCollation());
    }
    
    protected StringDataValue getNewVarchar() throws StandardException {
        return new CollatorSQLVarchar(this.holderForCollationSensitiveInfo.getCollatorForCollation());
    }
    
    public StringDataValue getValue(final RuleBasedCollator collator) {
        if (collator != null) {
            this.setCollator(collator);
            return this;
        }
        final SQLVarchar sqlVarchar = new SQLVarchar();
        sqlVarchar.copyState(this);
        return sqlVarchar;
    }
    
    protected int stringCompare(final SQLChar sqlChar, final SQLChar sqlChar2) throws StandardException {
        return this.holderForCollationSensitiveInfo.stringCompare(sqlChar, sqlChar2);
    }
    
    public int hashCode() {
        return this.hashCodeForCollation();
    }
    
    public BooleanDataValue like(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        return this.holderForCollationSensitiveInfo.like(dataValueDescriptor);
    }
    
    public BooleanDataValue like(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return this.holderForCollationSensitiveInfo.like(dataValueDescriptor, dataValueDescriptor2);
    }
}
