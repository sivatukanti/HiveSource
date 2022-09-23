// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;
import java.sql.Clob;
import java.text.RuleBasedCollator;

class CollatorSQLClob extends SQLClob implements CollationElementsInterface
{
    private WorkHorseForCollatorDatatypes holderForCollationSensitiveInfo;
    
    CollatorSQLClob(final RuleBasedCollator collator) {
        this.setCollator(collator);
    }
    
    CollatorSQLClob(final String s, final RuleBasedCollator collator) {
        super(s);
        this.setCollator(collator);
    }
    
    CollatorSQLClob(final Clob clob, final RuleBasedCollator collator) {
        super(clob);
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
        if (b) {
            try {
                return new CollatorSQLClob(this.getString(), this.holderForCollationSensitiveInfo.getCollatorForCollation());
            }
            catch (StandardException ex) {
                return null;
            }
        }
        final SQLClob sqlClob = (SQLClob)super.cloneValue(b);
        final CollatorSQLClob collatorSQLClob = new CollatorSQLClob(this.holderForCollationSensitiveInfo.getCollatorForCollation());
        collatorSQLClob.copyState(sqlClob);
        return collatorSQLClob;
    }
    
    public DataValueDescriptor getNewNull() {
        return new CollatorSQLClob((String)null, this.holderForCollationSensitiveInfo.getCollatorForCollation());
    }
    
    public StringDataValue getValue(final RuleBasedCollator collator) {
        if (collator != null) {
            this.setCollator(collator);
            return this;
        }
        final SQLClob sqlClob = new SQLClob();
        sqlClob.copyState(this);
        return sqlClob;
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
