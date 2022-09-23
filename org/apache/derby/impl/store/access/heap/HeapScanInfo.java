// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.heap;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Properties;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.ScanInfo;

class HeapScanInfo implements ScanInfo
{
    private int stat_numpages_visited;
    private int stat_numrows_visited;
    private int stat_numrows_qualified;
    private int stat_numColumnsFetched;
    private FormatableBitSet stat_validColumns;
    
    HeapScanInfo(final HeapScan heapScan) {
        this.stat_numpages_visited = 0;
        this.stat_numrows_visited = 0;
        this.stat_numrows_qualified = 0;
        this.stat_numColumnsFetched = 0;
        this.stat_validColumns = null;
        this.stat_numpages_visited = heapScan.getNumPagesVisited();
        this.stat_numrows_visited = heapScan.getNumRowsVisited();
        this.stat_numrows_qualified = heapScan.getNumRowsQualified();
        this.stat_validColumns = ((heapScan.getScanColumnList() == null) ? null : ((FormatableBitSet)heapScan.getScanColumnList().clone()));
        if (this.stat_validColumns == null) {
            this.stat_numColumnsFetched = ((Heap)heapScan.getOpenConglom().getConglomerate()).format_ids.length;
        }
        else {
            for (int i = 0; i < this.stat_validColumns.size(); ++i) {
                if (this.stat_validColumns.get(i)) {
                    ++this.stat_numColumnsFetched;
                }
            }
        }
    }
    
    public Properties getAllScanInfo(Properties properties) throws StandardException {
        if (properties == null) {
            properties = new Properties();
        }
        properties.put(MessageService.getTextMessage("XSAJ0.U"), MessageService.getTextMessage("XSAJG.U"));
        properties.put(MessageService.getTextMessage("XSAJ1.U"), Integer.toString(this.stat_numpages_visited));
        properties.put(MessageService.getTextMessage("XSAJ2.U"), Integer.toString(this.stat_numrows_visited));
        properties.put(MessageService.getTextMessage("XSAJ4.U"), Integer.toString(this.stat_numrows_qualified));
        properties.put(MessageService.getTextMessage("XSAJ5.U"), Integer.toString(this.stat_numColumnsFetched));
        properties.put(MessageService.getTextMessage("XSAJ6.U"), (this.stat_validColumns == null) ? MessageService.getTextMessage("XSAJE.U") : this.stat_validColumns.toString());
        return properties;
    }
}
