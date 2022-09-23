// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import org.apache.derby.iapi.services.i18n.MessageService;

public class RealRowResultSetStatistics extends RealNoPutResultSetStatistics
{
    public int rowsReturned;
    
    public RealRowResultSetStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final int rowsReturned, final double n9, final double n10) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.rowsReturned = rowsReturned;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        return this.indent + MessageService.getTextMessage("43X99.U") + ":\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X81.U") + " = " + this.rowsReturned + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.dumpEstimatedCosts(this.subIndent) + "\n";
    }
    
    public String getScanStatisticsText(final String s, final int n) {
        return "";
    }
    
    public String toString() {
        return this.getStatementExecutionPlanText(0);
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage("43X99.U");
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        xplainVisitor.setNumberOfChildren(0);
        xplainVisitor.visit(this);
    }
    
    public String getRSXplainType() {
        return "ROW";
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), new Integer(this.numOpens), null, null, null, (UUID)o2, new Double(this.optimizerEstimatedRowCount), new Double(this.optimizerEstimatedCost), null, null, null, new Integer(this.rowsSeen), null, new Integer(this.rowsFiltered), new Integer(this.rowsReturned), null, null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
}
