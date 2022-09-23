// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import org.apache.derby.iapi.services.i18n.MessageService;

public class RealVTIStatistics extends RealNoPutResultSetStatistics
{
    public String javaClassName;
    
    public RealVTIStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final String javaClassName, final double n9, final double n10) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.javaClassName = javaClassName;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        return this.indent + MessageService.getTextMessage("43Y19.U", this.javaClassName) + ":\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X04.U") + " = " + this.rowsSeen + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.dumpEstimatedCosts(this.subIndent);
    }
    
    public String getScanStatisticsText(final String s, final int n) {
        return this.getStatementExecutionPlanText(n);
    }
    
    public String toString() {
        return this.getStatementExecutionPlanText(0);
    }
    
    public String getNodeOn() {
        return MessageService.getTextMessage("43X75.U", this.javaClassName);
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage("43Y20.U");
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        xplainVisitor.setNumberOfChildren(0);
        xplainVisitor.visit(this);
    }
    
    public String getRSXplainType() {
        return "VTI";
    }
    
    public String getRSXplainDetails() {
        return this.javaClassName + ", (" + this.resultSetNumber + ")";
    }
}
