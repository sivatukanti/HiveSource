// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;

public class StatementTriggerExecutor extends GenericTriggerExecutor
{
    StatementTriggerExecutor(final InternalTriggerExecutionContext internalTriggerExecutionContext, final TriggerDescriptor triggerDescriptor, final Activation activation, final LanguageConnectionContext languageConnectionContext) {
        super(internalTriggerExecutionContext, triggerDescriptor, activation, languageConnectionContext);
    }
    
    void fireTrigger(final TriggerEvent triggerEvent, final CursorResultSet beforeResultSet, final CursorResultSet afterResultSet, final int[] array) throws StandardException {
        this.tec.setTrigger(this.triggerd);
        this.tec.setBeforeResultSet(beforeResultSet);
        this.tec.setAfterResultSet(afterResultSet);
        try {
            this.executeSPS(this.getAction());
        }
        finally {
            this.clearSPS();
            this.tec.clearTrigger();
        }
    }
}
