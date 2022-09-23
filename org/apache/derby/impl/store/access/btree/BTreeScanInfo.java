// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Properties;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.ScanInfo;

class BTreeScanInfo implements ScanInfo
{
    private int stat_numpages_visited;
    private int stat_numrows_visited;
    private int stat_numrows_qualified;
    private int stat_numdeleted_rows_visited;
    private int stat_numColumnsFetched;
    private int stat_treeHeight;
    private FormatableBitSet stat_validColumns;
    
    BTreeScanInfo(final BTreeScan bTreeScan) {
        this.stat_numpages_visited = 0;
        this.stat_numrows_visited = 0;
        this.stat_numrows_qualified = 0;
        this.stat_numdeleted_rows_visited = 0;
        this.stat_numColumnsFetched = 0;
        this.stat_treeHeight = 0;
        this.stat_validColumns = null;
        this.stat_numpages_visited = bTreeScan.stat_numpages_visited;
        this.stat_numrows_visited = bTreeScan.stat_numrows_visited;
        this.stat_numrows_qualified = bTreeScan.stat_numrows_qualified;
        this.stat_numdeleted_rows_visited = bTreeScan.stat_numdeleted_rows_visited;
        this.stat_validColumns = ((bTreeScan.init_scanColumnList == null) ? null : ((FormatableBitSet)bTreeScan.init_scanColumnList.clone()));
        if (this.stat_validColumns == null) {
            this.stat_numColumnsFetched = bTreeScan.init_template.length;
        }
        else {
            for (int i = 0; i < this.stat_validColumns.size(); ++i) {
                if (this.stat_validColumns.get(i)) {
                    ++this.stat_numColumnsFetched;
                }
            }
        }
        try {
            this.stat_treeHeight = bTreeScan.getHeight();
        }
        catch (Throwable t) {
            this.stat_treeHeight = -1;
        }
    }
    
    public Properties getAllScanInfo(Properties properties) throws StandardException {
        if (properties == null) {
            properties = new Properties();
        }
        properties.put(MessageService.getTextMessage("XSAJ0.U"), MessageService.getTextMessage("XSAJF.U"));
        properties.put(MessageService.getTextMessage("XSAJ1.U"), Integer.toString(this.stat_numpages_visited));
        properties.put(MessageService.getTextMessage("XSAJ2.U"), Integer.toString(this.stat_numrows_visited));
        properties.put(MessageService.getTextMessage("XSAJ3.U"), Integer.toString(this.stat_numdeleted_rows_visited));
        properties.put(MessageService.getTextMessage("XSAJ4.U"), Integer.toString(this.stat_numrows_qualified));
        properties.put(MessageService.getTextMessage("XSAJ7.U"), Integer.toString(this.stat_treeHeight));
        properties.put(MessageService.getTextMessage("XSAJ5.U"), Integer.toString(this.stat_numColumnsFetched));
        properties.put(MessageService.getTextMessage("XSAJ6.U"), (this.stat_validColumns == null) ? MessageService.getTextMessage("XSAJE.U") : this.stat_validColumns.toString());
        return properties;
    }
}
