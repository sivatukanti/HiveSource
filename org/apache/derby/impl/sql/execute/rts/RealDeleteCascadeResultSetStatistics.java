// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealDeleteCascadeResultSetStatistics extends RealDeleteResultSetStatistics
{
    public ResultSetStatistics[] dependentTrackingArray;
    
    public RealDeleteCascadeResultSetStatistics(final int n, final boolean b, final int n2, final boolean b2, final long n3, final ResultSetStatistics resultSetStatistics, final ResultSetStatistics[] dependentTrackingArray) {
        super(n, b, n2, b2, n3, resultSetStatistics);
        this.dependentTrackingArray = dependentTrackingArray;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        String s = "";
        this.initFormatInfo(n);
        if (this.dependentTrackingArray != null) {
            int n2 = 0;
            for (int i = 0; i < this.dependentTrackingArray.length; ++i) {
                if (this.dependentTrackingArray[i] != null) {
                    if (n2 == 0) {
                        s = this.indent + "\n" + MessageService.getTextMessage("43Y53.U") + ":\n";
                        n2 = 1;
                    }
                    s += this.dependentTrackingArray[i].getStatementExecutionPlanText(this.sourceDepth);
                }
            }
        }
        return this.indent + MessageService.getTextMessage("43Y52.U") + " " + MessageService.getTextMessage(this.tableLock ? "43X14.U" : "43X15.U") + ":\n" + this.indent + MessageService.getTextMessage("43X16.U") + ": " + this.deferred + "\n" + this.indent + MessageService.getTextMessage("43X17.U") + " = " + this.rowCount + "\n" + this.indent + MessageService.getTextMessage("43X18.U") + " = " + this.indexesUpdated + "\n" + this.dumpTimeStats(this.indent) + ((this.sourceResultSetStatistics == null) ? "" : this.sourceResultSetStatistics.getStatementExecutionPlanText(1)) + s;
    }
    
    public String getScanStatisticsText(final String s, final int n) {
        String string = "";
        if (this.dependentTrackingArray != null) {
            for (int i = 0; i < this.dependentTrackingArray.length; ++i) {
                if (this.dependentTrackingArray[i] != null) {
                    string = string + "\n" + MessageService.getTextMessage("43Y54.U") + " " + i + "\n" + this.dependentTrackingArray[i].getScanStatisticsText(s, n) + MessageService.getTextMessage("43Y55.U") + " " + i + "\n\n";
                }
            }
        }
        return string + ((this.sourceResultSetStatistics == null) ? "" : this.sourceResultSetStatistics.getScanStatisticsText(s, n));
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage("43Y51.U");
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        int numberOfChildren = 0;
        if (this.sourceResultSetStatistics != null) {
            ++numberOfChildren;
        }
        if (this.dependentTrackingArray != null) {
            numberOfChildren += this.dependentTrackingArray.length;
        }
        xplainVisitor.setNumberOfChildren(numberOfChildren);
        xplainVisitor.visit(this);
        if (this.sourceResultSetStatistics != null) {
            this.sourceResultSetStatistics.accept(xplainVisitor);
        }
        if (this.dependentTrackingArray != null) {
            for (int i = 0; i < this.dependentTrackingArray.length; ++i) {
                if (this.dependentTrackingArray[i] != null) {
                    this.dependentTrackingArray[i].accept(xplainVisitor);
                }
            }
        }
    }
    
    public String getRSXplainDetails() {
        return "CASCADE";
    }
}
