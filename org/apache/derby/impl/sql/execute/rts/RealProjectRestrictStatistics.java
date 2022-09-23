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

public class RealProjectRestrictStatistics extends RealNoPutResultSetStatistics
{
    public boolean doesProjection;
    public boolean restriction;
    public long restrictionTime;
    public long projectionTime;
    public ResultSetStatistics childResultSetStatistics;
    public ResultSetStatistics[] subqueryTrackingArray;
    
    public RealProjectRestrictStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final long restrictionTime, final long projectionTime, final ResultSetStatistics[] subqueryTrackingArray, final boolean restriction, final boolean doesProjection, final double n9, final double n10, final ResultSetStatistics childResultSetStatistics) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.restriction = restriction;
        this.doesProjection = doesProjection;
        this.restrictionTime = restrictionTime;
        this.projectionTime = projectionTime;
        this.subqueryTrackingArray = subqueryTrackingArray;
        this.childResultSetStatistics = childResultSetStatistics;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        String s = "";
        this.initFormatInfo(n);
        if (this.subqueryTrackingArray != null) {
            int n2 = 0;
            for (int i = 0; i < this.subqueryTrackingArray.length; ++i) {
                if (this.subqueryTrackingArray[i] != null) {
                    if (n2 == 0) {
                        s = this.indent + MessageService.getTextMessage("43X56.U") + ":\n";
                        n2 = 1;
                    }
                    s += this.subqueryTrackingArray[i].getStatementExecutionPlanText(this.sourceDepth);
                }
            }
        }
        return s + this.indent + MessageService.getTextMessage("43X93.U") + " (" + this.resultSetNumber + "):" + "\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X04.U") + " = " + this.rowsSeen + "\n" + this.indent + MessageService.getTextMessage("43X32.U") + " = " + this.rowsFiltered + "\n" + this.indent + MessageService.getTextMessage("43X94.U") + " = " + this.restriction + "\n" + this.indent + MessageService.getTextMessage("43X95.U") + " = " + this.doesProjection + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.subIndent + MessageService.getTextMessage("43X96.U") + " = " + this.restrictionTime + "\n" + this.subIndent + MessageService.getTextMessage("43X97.U") + " = " + this.projectionTime + "\n" + this.dumpEstimatedCosts(this.subIndent) + "\n" + this.indent + MessageService.getTextMessage("43X05.U") + ":" + "\n" + this.childResultSetStatistics.getStatementExecutionPlanText(this.sourceDepth);
    }
    
    public String getScanStatisticsText(final String s, final int n) {
        String string = "";
        if (this.subqueryTrackingArray != null) {
            for (int i = 0; i < this.subqueryTrackingArray.length; ++i) {
                if (this.subqueryTrackingArray[i] != null) {
                    string = string + "\n" + MessageService.getTextMessage("43X01.U") + " " + i + "\n" + this.subqueryTrackingArray[i].getScanStatisticsText(s, n) + MessageService.getTextMessage("43X06.U") + " " + i + "\n\n";
                }
            }
        }
        return string + this.childResultSetStatistics.getScanStatisticsText(s, n);
    }
    
    public String toString() {
        return this.getStatementExecutionPlanText(0);
    }
    
    public Vector getChildren() {
        final Vector<ResultSetStatistics> vector = new Vector<ResultSetStatistics>();
        vector.addElement(this.childResultSetStatistics);
        if (this.subqueryTrackingArray != null) {
            for (int i = 0; i < this.subqueryTrackingArray.length; ++i) {
                if (this.subqueryTrackingArray[i] != null) {
                    vector.addElement(this.subqueryTrackingArray[i]);
                }
            }
        }
        return vector;
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage("43X98.U");
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        int numberOfChildren = 0;
        if (this.childResultSetStatistics != null) {
            ++numberOfChildren;
        }
        if (this.subqueryTrackingArray != null) {
            numberOfChildren += this.subqueryTrackingArray.length;
        }
        xplainVisitor.setNumberOfChildren(numberOfChildren);
        xplainVisitor.visit(this);
        if (this.childResultSetStatistics != null) {
            this.childResultSetStatistics.accept(xplainVisitor);
        }
        if (this.subqueryTrackingArray != null) {
            for (int i = 0; i < this.subqueryTrackingArray.length; ++i) {
                if (this.subqueryTrackingArray[i] != null) {
                    this.subqueryTrackingArray[i].accept(xplainVisitor);
                }
            }
        }
    }
    
    public String getRSXplainType() {
        if (this.restriction && this.doesProjection) {
            return "PROJECT-FILTER";
        }
        if (this.doesProjection) {
            return "PROJECTION";
        }
        if (this.restriction) {
            return "FILTER";
        }
        return "PROJECT-FILTER";
    }
    
    public String getRSXplainDetails() {
        return this.resultSetNumber + ";";
    }
    
    public Object getResultSetTimingsDescriptor(final Object o) {
        return new XPLAINResultSetTimingsDescriptor((UUID)o, new Long(this.constructorTime), new Long(this.openTime), new Long(this.nextTime), new Long(this.closeTime), new Long(this.getNodeTime()), XPLAINUtil.getAVGNextTime(this.nextTime, this.rowsSeen), new Long(this.projectionTime), new Long(this.restrictionTime), null, null);
    }
}
