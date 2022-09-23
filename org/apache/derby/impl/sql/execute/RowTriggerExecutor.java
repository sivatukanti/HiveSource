// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;

public class RowTriggerExecutor extends GenericTriggerExecutor
{
    RowTriggerExecutor(final InternalTriggerExecutionContext internalTriggerExecutionContext, final TriggerDescriptor triggerDescriptor, final Activation activation, final LanguageConnectionContext languageConnectionContext) {
        super(internalTriggerExecutionContext, triggerDescriptor, activation, languageConnectionContext);
    }
    
    void fireTrigger(final TriggerEvent triggerEvent, final CursorResultSet set, final CursorResultSet set2, final int[] array) throws StandardException {
        this.tec.setTrigger(this.triggerd);
        try {
            while (set == null || set.getNextRow() != null) {
                if (set2 != null && set2.getNextRow() == null) {
                    return;
                }
                this.tec.setBeforeResultSet((set == null) ? null : TemporaryRowHolderResultSet.getNewRSOnCurrentRow(this.triggerd, this.activation, set, array));
                this.tec.setAfterResultSet((set2 == null) ? null : TemporaryRowHolderResultSet.getNewRSOnCurrentRow(this.triggerd, this.activation, set2, array));
                if (triggerEvent.isAfter()) {
                    this.tec.updateAICounters();
                }
                this.executeSPS(this.getAction());
                if (!triggerEvent.isBefore()) {
                    continue;
                }
                this.tec.updateAICounters();
            }
        }
        finally {
            this.clearSPS();
            this.tec.clearTrigger();
        }
    }
}
