// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import java.util.Vector;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealDistinctScalarAggregateStatistics extends RealScalarAggregateStatistics
{
    public RealDistinctScalarAggregateStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final int n9, final double n10, final double n11, final ResultSetStatistics resultSetStatistics) {
        super(n, n2, n3, n4, n5, n6, n7, n8, false, n9, n10, n11, resultSetStatistics);
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        return this.indent + MessageService.getTextMessage("43X20.U") + ":\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X21.U") + " = " + this.rowsInput + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.dumpEstimatedCosts(this.subIndent) + "\n" + this.indent + MessageService.getTextMessage("43X05.U") + ":\n" + this.childResultSetStatistics.getStatementExecutionPlanText(this.sourceDepth) + "\n";
    }
    
    public String getScanStatisticsText(final String s, final int n) {
        return this.childResultSetStatistics.getScanStatisticsText(s, n);
    }
    
    public String toString() {
        return this.getStatementExecutionPlanText(0);
    }
    
    public Vector getChildren() {
        final Vector<ResultSetStatistics> vector = new Vector<ResultSetStatistics>();
        vector.addElement(this.childResultSetStatistics);
        return vector;
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage("43X22.U");
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        xplainVisitor.setNumberOfChildren(1);
        xplainVisitor.visit(this);
        this.childResultSetStatistics.accept(xplainVisitor);
    }
    
    public String getRSXplainDetails() {
        return "DISTINCT";
    }
}
