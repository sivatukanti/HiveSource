// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import java.util.Vector;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealNestedLoopJoinStatistics extends RealJoinResultSetStatistics
{
    public boolean oneRowRightSide;
    public ResultSetStatistics leftResultSetStatistics;
    public ResultSetStatistics rightResultSetStatistics;
    protected String nodeName;
    public String resultSetName;
    
    public RealNestedLoopJoinStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final int n9, final int n10, final int n11, final long n12, final boolean oneRowRightSide, final double n13, final double n14, final String s, final ResultSetStatistics leftResultSetStatistics, final ResultSetStatistics rightResultSetStatistics) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, s);
        this.oneRowRightSide = oneRowRightSide;
        this.leftResultSetStatistics = leftResultSetStatistics;
        this.rightResultSetStatistics = rightResultSetStatistics;
        this.setNames();
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        String string = "";
        if (this.userSuppliedOptimizerOverrides != null) {
            string = this.indent + MessageService.getTextMessage("43Y57.U", this.userSuppliedOptimizerOverrides) + "\n";
        }
        return string + this.indent + this.resultSetName + ":\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X79.U") + " = " + this.rowsSeenLeft + "\n" + this.indent + MessageService.getTextMessage("43X80.U") + " = " + this.rowsSeenRight + "\n" + this.indent + MessageService.getTextMessage("43X32.U") + " = " + this.rowsFiltered + "\n" + this.indent + MessageService.getTextMessage("43X81.U") + " = " + this.rowsReturned + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.dumpEstimatedCosts(this.subIndent) + "\n" + this.indent + MessageService.getTextMessage("43X82.U") + ":\n" + this.leftResultSetStatistics.getStatementExecutionPlanText(this.sourceDepth) + "\n" + this.indent + MessageService.getTextMessage("43X83.U") + ":\n" + this.rightResultSetStatistics.getStatementExecutionPlanText(this.sourceDepth) + "\n";
    }
    
    public String getScanStatisticsText(final String s, final int n) {
        return this.leftResultSetStatistics.getScanStatisticsText(s, n) + this.rightResultSetStatistics.getScanStatisticsText(s, n);
    }
    
    public String toString() {
        return this.getStatementExecutionPlanText(0);
    }
    
    public Vector getChildren() {
        final Vector<ResultSetStatistics> vector = new Vector<ResultSetStatistics>();
        vector.addElement(this.leftResultSetStatistics);
        vector.addElement(this.rightResultSetStatistics);
        return vector;
    }
    
    public String getNodeName() {
        return this.nodeName;
    }
    
    protected void setNames() {
        if (this.nodeName == null) {
            if (this.oneRowRightSide) {
                this.nodeName = MessageService.getTextMessage("43X84.U");
                this.resultSetName = MessageService.getTextMessage("43X85.U");
            }
            else {
                this.nodeName = MessageService.getTextMessage("43X86.U");
                this.resultSetName = MessageService.getTextMessage("43X87.U");
            }
        }
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        int numberOfChildren = 0;
        if (this.leftResultSetStatistics != null) {
            ++numberOfChildren;
        }
        if (this.rightResultSetStatistics != null) {
            ++numberOfChildren;
        }
        xplainVisitor.setNumberOfChildren(numberOfChildren);
        xplainVisitor.visit(this);
        if (this.leftResultSetStatistics != null) {
            this.leftResultSetStatistics.accept(xplainVisitor);
        }
        if (this.rightResultSetStatistics != null) {
            this.rightResultSetStatistics.accept(xplainVisitor);
        }
    }
    
    public String getRSXplainType() {
        return "NLJOIN";
    }
    
    public String getRSXplainDetails() {
        String str = "(" + this.resultSetNumber + ")";
        if (this.oneRowRightSide) {
            str += ", EXISTS JOIN";
        }
        return str;
    }
}
