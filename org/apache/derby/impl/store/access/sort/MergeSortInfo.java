// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Properties;
import java.util.Vector;
import org.apache.derby.iapi.store.access.SortInfo;

class MergeSortInfo implements SortInfo
{
    private String stat_sortType;
    private int stat_numRowsInput;
    private int stat_numRowsOutput;
    private int stat_numMergeRuns;
    private Vector stat_mergeRunsSize;
    
    MergeSortInfo(final MergeInserter mergeInserter) {
        this.stat_sortType = mergeInserter.stat_sortType;
        this.stat_numRowsInput = mergeInserter.stat_numRowsInput;
        this.stat_numRowsOutput = mergeInserter.stat_numRowsOutput;
        this.stat_numMergeRuns = mergeInserter.stat_numMergeRuns;
        this.stat_mergeRunsSize = mergeInserter.stat_mergeRunsSize;
    }
    
    public Properties getAllSortInfo(Properties properties) throws StandardException {
        if (properties == null) {
            properties = new Properties();
        }
        properties.put(MessageService.getTextMessage("XSAJ8.U"), "external".equals(this.stat_sortType) ? MessageService.getTextMessage("XSAJI.U") : MessageService.getTextMessage("XSAJJ.U"));
        properties.put(MessageService.getTextMessage("XSAJA.U"), Integer.toString(this.stat_numRowsInput));
        properties.put(MessageService.getTextMessage("XSAJB.U"), Integer.toString(this.stat_numRowsOutput));
        if (this.stat_sortType == "external") {
            properties.put(MessageService.getTextMessage("XSAJC.U"), Integer.toString(this.stat_numMergeRuns));
            properties.put(MessageService.getTextMessage("XSAJD.U"), this.stat_mergeRunsSize.toString());
        }
        return properties;
    }
}
