// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINResultSetTimingsDescriptor;
import org.apache.derby.impl.sql.execute.xplain.XPLAINUtil;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import java.util.Vector;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealMaterializedResultSetStatistics extends RealNoPutResultSetStatistics
{
    public ResultSetStatistics childResultSetStatistics;
    public long createTCTime;
    public long fetchTCTime;
    
    public RealMaterializedResultSetStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final long createTCTime, final long fetchTCTime, final int n8, final double n9, final double n10, final ResultSetStatistics childResultSetStatistics) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.createTCTime = createTCTime;
        this.fetchTCTime = fetchTCTime;
        this.childResultSetStatistics = childResultSetStatistics;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        return this.indent + MessageService.getTextMessage("43X76.U") + ":\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X04.U") + " = " + this.rowsSeen + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.dumpEstimatedCosts(this.subIndent) + "\n" + this.subIndent + MessageService.getTextMessage("43X77.U") + " = " + this.createTCTime + "\n" + this.subIndent + MessageService.getTextMessage("43X78.U") + " = " + this.fetchTCTime + "\n" + this.indent + MessageService.getTextMessage("43X05.U") + ":\n" + this.childResultSetStatistics.getStatementExecutionPlanText(this.sourceDepth) + "\n";
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
        return MessageService.getTextMessage("43X76.U");
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
        return "MATERIALIZE";
    }
    
    public String getRSXplainDetails() {
        return "(" + this.resultSetNumber + ")";
    }
    
    public Object getResultSetTimingsDescriptor(final Object o) {
        return new XPLAINResultSetTimingsDescriptor((UUID)o, new Long(this.constructorTime), new Long(this.openTime), new Long(this.nextTime), new Long(this.closeTime), new Long(this.getNodeTime()), XPLAINUtil.getAVGNextTime(this.nextTime, this.rowsSeen), null, null, new Long(this.createTCTime), new Long(this.fetchTCTime));
    }
}
