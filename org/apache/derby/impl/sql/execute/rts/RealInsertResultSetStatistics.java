// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.impl.sql.execute.xplain.XPLAINUtil;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealInsertResultSetStatistics extends RealNoRowsResultSetStatistics
{
    public int rowCount;
    public boolean deferred;
    public int indexesUpdated;
    public boolean userSpecifiedBulkInsert;
    public boolean bulkInsertPerformed;
    public boolean tableLock;
    
    public RealInsertResultSetStatistics(final int rowCount, final boolean deferred, final int indexesUpdated, final boolean userSpecifiedBulkInsert, final boolean bulkInsertPerformed, final boolean tableLock, final long n, final ResultSetStatistics sourceResultSetStatistics) {
        super(n, sourceResultSetStatistics);
        this.rowCount = rowCount;
        this.deferred = deferred;
        this.indexesUpdated = indexesUpdated;
        this.userSpecifiedBulkInsert = userSpecifiedBulkInsert;
        this.bulkInsertPerformed = bulkInsertPerformed;
        this.tableLock = tableLock;
        this.sourceResultSetStatistics = sourceResultSetStatistics;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        String str;
        if (this.userSpecifiedBulkInsert) {
            if (this.bulkInsertPerformed) {
                str = this.indent + MessageService.getTextMessage("43X64.U");
            }
            else {
                str = this.indent + MessageService.getTextMessage("43X65.U");
            }
        }
        else {
            str = this.indent + MessageService.getTextMessage("43X66.U");
        }
        return this.indent + MessageService.getTextMessage("43X67.U") + " " + MessageService.getTextMessage(this.tableLock ? "43X14.U" : "43X15.U") + ":\n" + this.indent + MessageService.getTextMessage("43X16.U") + ": " + this.deferred + "\n" + (str + "\n") + this.indent + MessageService.getTextMessage("43X68.U") + " = " + this.rowCount + "\n" + this.indent + MessageService.getTextMessage("43X18.U") + " = " + this.indexesUpdated + "\n" + this.dumpTimeStats(this.indent) + ((this.sourceResultSetStatistics == null) ? null : this.sourceResultSetStatistics.getStatementExecutionPlanText(1));
    }
    
    public String getScanStatisticsText(final String s, final int n) {
        if (this.sourceResultSetStatistics == null) {
            return null;
        }
        return this.sourceResultSetStatistics.getScanStatisticsText(s, n);
    }
    
    public String toString() {
        return this.getStatementExecutionPlanText(0);
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage("43X69.U");
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        int numberOfChildren = 0;
        if (this.sourceResultSetStatistics != null) {
            ++numberOfChildren;
        }
        xplainVisitor.setNumberOfChildren(numberOfChildren);
        xplainVisitor.visit(this);
        if (this.sourceResultSetStatistics != null) {
            this.sourceResultSetStatistics.accept(xplainVisitor);
        }
    }
    
    public String getRSXplainType() {
        return "INSERT";
    }
    
    public String getRSXplainDetails() {
        return this.bulkInsertPerformed ? "BULK" : null;
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), null, new Integer(this.indexesUpdated), null, this.tableLock ? "T" : "R", (UUID)o2, null, null, new Integer(this.rowCount), XPLAINUtil.getYesNoCharFromBoolean(this.deferred), null, null, null, null, null, null, null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
}
