// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import java.util.Vector;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealScrollInsensitiveResultSetStatistics extends RealNoPutResultSetStatistics
{
    public ResultSetStatistics childResultSetStatistics;
    public int numFromHashTable;
    public int numToHashTable;
    
    public RealScrollInsensitiveResultSetStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int numFromHashTable, final int numToHashTable, final int n8, final double n9, final double n10, final ResultSetStatistics childResultSetStatistics) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.numFromHashTable = numFromHashTable;
        this.numToHashTable = numToHashTable;
        this.childResultSetStatistics = childResultSetStatistics;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        return this.indent + MessageService.getTextMessage("43Y03.U") + ":\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X04.U") + " = " + this.rowsSeen + "\n" + this.indent + MessageService.getTextMessage("43Y04.U") + " = " + this.numFromHashTable + "\n" + this.indent + MessageService.getTextMessage("43Y05.U") + " = " + this.numToHashTable + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.dumpEstimatedCosts(this.subIndent) + "\n" + this.indent + MessageService.getTextMessage("43X05.U") + ":\n" + this.childResultSetStatistics.getStatementExecutionPlanText(this.sourceDepth) + "\n";
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
        return MessageService.getTextMessage("43Y03.U");
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
        return "SCROLL";
    }
    
    public String getRSXplainDetails() {
        return "(" + this.resultSetNumber + "), " + "[" + this.numFromHashTable + ", " + this.numToHashTable + "]";
    }
}
