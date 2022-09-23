// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import java.util.Vector;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealIndexRowToBaseRowStatistics extends RealNoPutResultSetStatistics
{
    public String tableName;
    public ResultSetStatistics childResultSetStatistics;
    public String colsAccessedFromHeap;
    
    public RealIndexRowToBaseRowStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final String tableName, final FormatableBitSet set, final double n9, final double n10, final ResultSetStatistics childResultSetStatistics) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.tableName = tableName;
        this.colsAccessedFromHeap = ((set == null) ? ("{" + MessageService.getTextMessage("43X59.U") + "}") : set.toString());
        this.childResultSetStatistics = childResultSetStatistics;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        return this.indent + MessageService.getTextMessage("43X60.U", this.tableName) + ":" + "\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X04.U") + " = " + this.rowsSeen + "\n" + this.indent + MessageService.getTextMessage("43X61.U") + " = " + this.colsAccessedFromHeap + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.dumpEstimatedCosts(this.subIndent) + "\n" + this.childResultSetStatistics.getStatementExecutionPlanText(this.sourceDepth) + "\n";
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
    
    public Vector getChildren() {
        final Vector<ResultSetStatistics> vector = new Vector<ResultSetStatistics>();
        vector.addElement(this.childResultSetStatistics);
        return vector;
    }
    
    public String getNodeOn() {
        return MessageService.getTextMessage("43X62.U", this.tableName);
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage("43X63.U");
    }
    
    ResultSetStatistics getChildResultSetStatistics() {
        return this.childResultSetStatistics;
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        int numberOfChildren = 0;
        if (this.childResultSetStatistics != null) {
            ++numberOfChildren;
        }
        xplainVisitor.setNumberOfChildren(numberOfChildren);
        xplainVisitor.visit(this);
        if (this.childResultSetStatistics != null) {
            this.childResultSetStatistics.accept(xplainVisitor);
        }
    }
    
    public String getRSXplainType() {
        return "ROWIDSCAN";
    }
    
    public String getRSXplainDetails() {
        return "(" + this.resultSetNumber + ")," + this.tableName;
    }
}
