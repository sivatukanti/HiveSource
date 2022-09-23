// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.text.CollationElementIterator;
import org.apache.derby.iapi.error.StandardException;
import java.text.CollationKey;
import java.text.RuleBasedCollator;

final class WorkHorseForCollatorDatatypes
{
    private RuleBasedCollator collatorForCharacterDatatypes;
    private SQLChar stringData;
    
    WorkHorseForCollatorDatatypes(final RuleBasedCollator collatorForCharacterDatatypes, final SQLChar stringData) {
        this.collatorForCharacterDatatypes = collatorForCharacterDatatypes;
        this.stringData = stringData;
    }
    
    int stringCompare(final SQLChar sqlChar, final SQLChar sqlChar2) throws StandardException {
        final CollationKey collationKey = sqlChar.getCollationKey();
        final CollationKey collationKey2 = sqlChar2.getCollationKey();
        if (collationKey != null && collationKey2 != null) {
            return collationKey.compareTo(collationKey2);
        }
        if (collationKey != null) {
            return -1;
        }
        if (collationKey2 != null) {
            return 1;
        }
        return 0;
    }
    
    BooleanDataValue like(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        return SQLBoolean.truthValue(this.stringData, dataValueDescriptor, Like.like(this.stringData.getCharArray(), this.stringData.getLength(), ((SQLChar)dataValueDescriptor).getCharArray(), dataValueDescriptor.getLength(), null, 0, this.collatorForCharacterDatatypes));
    }
    
    BooleanDataValue like(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        if (dataValueDescriptor2.isNull()) {
            throw StandardException.newException("22501");
        }
        final CollationElementsInterface collationElementsInterface = (CollationElementsInterface)dataValueDescriptor2;
        if (dataValueDescriptor2.getLength() != 1 || !collationElementsInterface.hasSingleCollationElement()) {
            throw StandardException.newException("22019", collationElementsInterface.toString());
        }
        return SQLBoolean.truthValue(this.stringData, dataValueDescriptor, Like.like(this.stringData.getCharArray(), this.stringData.getLength(), ((SQLChar)dataValueDescriptor).getCharArray(), dataValueDescriptor.getLength(), ((SQLChar)dataValueDescriptor2).getCharArray(), dataValueDescriptor2.getLength(), this.collatorForCharacterDatatypes));
    }
    
    RuleBasedCollator getCollatorForCollation() {
        return this.collatorForCharacterDatatypes;
    }
    
    boolean hasSingleCollationElement() throws StandardException {
        if (this.stringData.isNull()) {
            return false;
        }
        final CollationElementIterator collationElementIterator = this.collatorForCharacterDatatypes.getCollationElementIterator(this.stringData.getString());
        return collationElementIterator.next() != -1 && collationElementIterator.next() == -1;
    }
}
