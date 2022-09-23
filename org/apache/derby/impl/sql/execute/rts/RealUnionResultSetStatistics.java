// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import java.util.Vector;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealUnionResultSetStatistics extends RealNoPutResultSetStatistics
{
    public int rowsSeenLeft;
    public int rowsSeenRight;
    public int rowsReturned;
    public ResultSetStatistics leftResultSetStatistics;
    public ResultSetStatistics rightResultSetStatistics;
    
    public RealUnionResultSetStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final int rowsSeenLeft, final int rowsSeenRight, final int rowsReturned, final double n9, final double n10, final ResultSetStatistics leftResultSetStatistics, final ResultSetStatistics rightResultSetStatistics) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.rowsSeenLeft = rowsSeenLeft;
        this.rowsSeenRight = rowsSeenRight;
        this.rowsReturned = rowsReturned;
        this.leftResultSetStatistics = leftResultSetStatistics;
        this.rightResultSetStatistics = rightResultSetStatistics;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        return this.indent + MessageService.getTextMessage("43Y14.U") + ":\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X79.U") + " = " + this.rowsSeenLeft + "\n" + this.indent + MessageService.getTextMessage("43X80.U") + " = " + this.rowsSeenRight + "\n" + this.indent + MessageService.getTextMessage("43X81.U") + " = " + this.rowsReturned + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.dumpEstimatedCosts(this.subIndent) + "\n" + this.indent + MessageService.getTextMessage("43X82.U") + ":\n" + this.leftResultSetStatistics.getStatementExecutionPlanText(this.sourceDepth) + "\n" + this.indent + MessageService.getTextMessage("43X83.U") + ":\n" + this.rightResultSetStatistics.getStatementExecutionPlanText(this.sourceDepth) + "\n";
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
        return "Union";
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
        return "UNION";
    }
    
    public String getRSXplainDetails() {
        return "(" + this.resultSetNumber + ")";
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), new Integer(this.numOpens), null, null, null, (UUID)o2, new Double(this.optimizerEstimatedRowCount), new Double(this.optimizerEstimatedCost), null, null, null, new Integer(this.rowsSeenLeft), new Integer(this.rowsSeenRight), new Integer(this.rowsFiltered), new Integer(this.rowsReturned), null, null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
}
