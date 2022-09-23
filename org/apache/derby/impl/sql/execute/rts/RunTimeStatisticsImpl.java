// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import java.util.Vector;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import java.sql.Timestamp;
import org.apache.derby.iapi.sql.execute.RunTimeStatistics;

public final class RunTimeStatisticsImpl implements RunTimeStatistics
{
    public String statementText;
    public String statementName;
    public String spsName;
    public long parseTime;
    public long bindTime;
    public long optimizeTime;
    public long generateTime;
    public long compileTime;
    public long executeTime;
    public Timestamp beginCompilationTimestamp;
    public Timestamp endCompilationTimestamp;
    public Timestamp beginExecutionTimestamp;
    public Timestamp endExecutionTimestamp;
    public ResultSetStatistics topResultSetStatistics;
    public ResultSetStatistics[] subqueryTrackingArray;
    
    public RunTimeStatisticsImpl(final String spsName, final String statementName, final String statementText, final long compileTime, final long parseTime, final long bindTime, final long optimizeTime, final long generateTime, final long executeTime, final Timestamp beginCompilationTimestamp, final Timestamp endCompilationTimestamp, final Timestamp beginExecutionTimestamp, final Timestamp endExecutionTimestamp, final ResultSetStatistics[] subqueryTrackingArray, final ResultSetStatistics topResultSetStatistics) {
        this.spsName = spsName;
        this.statementName = statementName;
        this.statementText = statementText;
        this.compileTime = compileTime;
        this.parseTime = parseTime;
        this.bindTime = bindTime;
        this.optimizeTime = optimizeTime;
        this.generateTime = generateTime;
        this.executeTime = executeTime;
        this.beginCompilationTimestamp = beginCompilationTimestamp;
        this.endCompilationTimestamp = endCompilationTimestamp;
        this.beginExecutionTimestamp = beginExecutionTimestamp;
        this.endExecutionTimestamp = endExecutionTimestamp;
        this.subqueryTrackingArray = subqueryTrackingArray;
        this.topResultSetStatistics = topResultSetStatistics;
    }
    
    public long getCompileTimeInMillis() {
        return this.compileTime;
    }
    
    public long getParseTimeInMillis() {
        return this.parseTime;
    }
    
    public long getBindTimeInMillis() {
        return this.bindTime;
    }
    
    public long getOptimizeTimeInMillis() {
        return this.optimizeTime;
    }
    
    public long getGenerateTimeInMillis() {
        return this.generateTime;
    }
    
    public long getExecuteTimeInMillis() {
        return this.executeTime;
    }
    
    public Timestamp getBeginCompilationTimestamp() {
        return this.beginCompilationTimestamp;
    }
    
    public Timestamp getEndCompilationTimestamp() {
        return this.endCompilationTimestamp;
    }
    
    public Timestamp getBeginExecutionTimestamp() {
        return this.beginExecutionTimestamp;
    }
    
    public Timestamp getEndExecutionTimestamp() {
        return this.endExecutionTimestamp;
    }
    
    public String getStatementName() {
        return this.statementName;
    }
    
    public String getSPSName() {
        return this.spsName;
    }
    
    public String getStatementText() {
        return this.statementText;
    }
    
    public double getEstimatedRowCount() {
        if (this.topResultSetStatistics == null) {
            return 0.0;
        }
        return this.topResultSetStatistics.getEstimatedRowCount();
    }
    
    public String getStatementExecutionPlanText() {
        if (this.topResultSetStatistics == null) {
            return null;
        }
        String s = "";
        if (this.subqueryTrackingArray != null) {
            int n = 0;
            for (int i = 0; i < this.subqueryTrackingArray.length; ++i) {
                if (this.subqueryTrackingArray[i] != null) {
                    if (n == 0) {
                        s = MessageService.getTextMessage("43Y21.U") + ":\n";
                        n = 1;
                    }
                    s += this.subqueryTrackingArray[i].getStatementExecutionPlanText(1);
                }
            }
        }
        return s + this.topResultSetStatistics.getStatementExecutionPlanText(0);
    }
    
    public String getScanStatisticsText() {
        return (this.topResultSetStatistics == null) ? null : this.topResultSetStatistics.getScanStatisticsText(null, 0);
    }
    
    public String getScanStatisticsText(final String s) {
        if (this.topResultSetStatistics == null) {
            return null;
        }
        final String scanStatisticsText = this.topResultSetStatistics.getScanStatisticsText(s, 0);
        return scanStatisticsText.equals("") ? null : scanStatisticsText;
    }
    
    public String toString() {
        return ((this.spsName != null) ? ("Stored Prepared Statement Name: \n\t" + this.spsName + "\n") : "") + MessageService.getTextMessage("43Y22.U") + ": \n\t" + this.statementName + "\n" + MessageService.getTextMessage("43Y23.U") + ": \n\t" + this.statementText + "\n" + MessageService.getTextMessage("43Y24.U") + ": " + this.parseTime + "\n" + MessageService.getTextMessage("43Y25.U") + ": " + this.bindTime + "\n" + MessageService.getTextMessage("43Y26.U") + ": " + this.optimizeTime + "\n" + MessageService.getTextMessage("43Y27.U") + ": " + this.generateTime + "\n" + MessageService.getTextMessage("43Y28.U") + ": " + this.compileTime + "\n" + MessageService.getTextMessage("43Y29.U") + ": " + this.executeTime + "\n" + MessageService.getTextMessage("43Y30.U") + " : " + this.beginCompilationTimestamp + "\n" + MessageService.getTextMessage("43Y31.U") + " : " + this.endCompilationTimestamp + "\n" + MessageService.getTextMessage("43Y32.U") + " : " + this.beginExecutionTimestamp + "\n" + MessageService.getTextMessage("43Y33.U") + " : " + this.endExecutionTimestamp + "\n" + MessageService.getTextMessage("43Y44.U") + ": \n" + this.getStatementExecutionPlanText();
    }
    
    public Vector getChildren() {
        final Vector<ResultSetStatistics> vector = new Vector<ResultSetStatistics>();
        vector.addElement(this.topResultSetStatistics);
        return vector;
    }
    
    public void acceptFromTopResultSet(final XPLAINVisitor xplainVisitor) {
        if (this.topResultSetStatistics != null) {
            this.topResultSetStatistics.accept(xplainVisitor);
        }
    }
}
