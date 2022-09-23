// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealDeleteVTIResultSetStatistics extends RealNoRowsResultSetStatistics
{
    public int rowCount;
    
    public RealDeleteVTIResultSetStatistics(final int rowCount, final long n, final ResultSetStatistics sourceResultSetStatistics) {
        super(n, sourceResultSetStatistics);
        this.rowCount = rowCount;
        this.sourceResultSetStatistics = sourceResultSetStatistics;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        return this.indent + MessageService.getTextMessage("43Y47.U") + ":\n" + this.indent + MessageService.getTextMessage("43X17.U") + " = " + this.rowCount + "\n" + this.dumpTimeStats(this.indent) + ((this.sourceResultSetStatistics == null) ? "" : this.sourceResultSetStatistics.getStatementExecutionPlanText(1));
    }
    
    public String getScanStatisticsText(final String s, final int n) {
        if (this.sourceResultSetStatistics == null) {
            return "";
        }
        return this.sourceResultSetStatistics.getScanStatisticsText(s, n);
    }
    
    public String toString() {
        return this.getStatementExecutionPlanText(0);
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage("43Y50.U");
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
        return "DELETE";
    }
    
    public String getRSXplainDetails() {
        return "VTI";
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), null, null, null, null, (UUID)o2, null, null, new Integer(this.rowCount), null, null, null, null, null, null, null, null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
}
