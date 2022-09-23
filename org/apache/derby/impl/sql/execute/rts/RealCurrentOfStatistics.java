// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import org.apache.derby.iapi.services.i18n.MessageService;

public class RealCurrentOfStatistics extends RealNoPutResultSetStatistics
{
    public RealCurrentOfStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8) {
        super(n, n2, n3, n4, n5, n6, n7, n8, 0.0, 0.0);
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        return this.indent + MessageService.getTextMessage("43X12.U", "getStatementExecutionPlanText", "CurrentOfResultSet\n");
    }
    
    public String getScanStatisticsText(final String s, final int n) {
        return this.indent + MessageService.getTextMessage("43X12.U", "getScanStatisticsText", "CurrentOfResultSet\n");
    }
    
    public String toString() {
        return this.getStatementExecutionPlanText(0);
    }
    
    public String getNodeName() {
        return "Current Of";
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        xplainVisitor.setNumberOfChildren(0);
        xplainVisitor.visit(this);
    }
    
    public String getRSXplainType() {
        return "CURRENT-OF";
    }
}
