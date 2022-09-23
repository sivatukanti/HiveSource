// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINScanPropsDescriptor;
import org.apache.derby.impl.sql.execute.xplain.XPLAINUtil;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import org.apache.derby.iapi.services.i18n.MessageService;

public class RealLastIndexKeyScanStatistics extends RealNoPutResultSetStatistics
{
    public String isolationLevel;
    public String tableName;
    public String indexName;
    public String lockString;
    
    public RealLastIndexKeyScanStatistics(final int n, final long n2, final long n3, final long n4, final long n5, final int n6, final String tableName, final String indexName, final String isolationLevel, final String lockString, final double n7, final double n8) {
        super(n, 1, 0, n2, n3, n4, n5, n6, n7, n8);
        this.tableName = tableName;
        this.indexName = indexName;
        this.isolationLevel = isolationLevel;
        this.lockString = lockString;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        return this.indent + MessageService.getTextMessage("43X71.U", this.tableName, this.indexName) + MessageService.getTextMessage("43X72.U", this.isolationLevel, this.lockString) + "\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X04.U") + " = " + this.numOpens + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + ((this.rowsSeen > 0) ? (this.subIndent + MessageService.getTextMessage("43X33.U") + " = " + this.nextTime / this.numOpens + "\n") : "") + "\n" + this.dumpEstimatedCosts(this.subIndent);
    }
    
    public String getScanStatisticsText(final String s, final int n) {
        if (s == null || s.equals(this.tableName)) {
            return this.getStatementExecutionPlanText(n);
        }
        return "";
    }
    
    public String toString() {
        return this.getStatementExecutionPlanText(0);
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage((this.indexName == null) ? "43X73.U" : "43X74.U");
    }
    
    public String getNodeOn() {
        if (this.indexName == null) {
            return MessageService.getTextMessage("43X75.U", this.tableName);
        }
        return MessageService.getTextMessage("43X38.U", this.tableName, this.indexName);
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        xplainVisitor.setNumberOfChildren(0);
        xplainVisitor.visit(this);
    }
    
    public String getRSXplainType() {
        return "LASTINDEXKEYSCAN";
    }
    
    public String getRSXplainDetails() {
        return "I: " + this.indexName + ", T: " + this.tableName;
    }
    
    public Object getScanPropsDescriptor(final Object o) {
        return new XPLAINScanPropsDescriptor((UUID)o, this.indexName, "I", null, XPLAINUtil.getIsolationLevelCode(this.isolationLevel), null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), new Integer(this.numOpens), null, XPLAINUtil.getLockModeCode(this.lockString), XPLAINUtil.getLockGranularityCode(this.lockString), (UUID)o2, new Double(this.optimizerEstimatedRowCount), new Double(this.optimizerEstimatedCost), null, null, null, new Integer(this.rowsSeen), null, new Integer(this.rowsFiltered), new Integer(this.rowsSeen - this.rowsFiltered), null, null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
}
