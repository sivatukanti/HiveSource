// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.util.PropertyUtil;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Properties;

public class RealDistinctScanStatistics extends RealHashScanStatistics
{
    public RealDistinctScanStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final String s, final String s2, final boolean b, final int n9, final int[] array, final String s3, final String s4, final Properties properties, final String s5, final String s6, final String s7, final String s8, final double n10, final double n11) {
        super(n, n2, n3, n4, n5, n6, n7, n8, s, s2, b, n9, array, s3, s4, properties, s5, s6, s7, s8, n10, n11);
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        String str;
        if (this.indexName != null) {
            str = this.indent + MessageService.getTextMessage("43X23.U", this.tableName, this.isConstraint ? "constraint" : "index", this.indexName);
        }
        else {
            str = this.indent + MessageService.getTextMessage("43X26.U", this.tableName);
        }
        final String string = str + " " + MessageService.getTextMessage("43X27.U", this.isolationLevel, this.lockString) + ": \n";
        final String string2 = this.indent + MessageService.getTextMessage("43X28.U") + ": \n" + PropertyUtil.sortProperties(this.scanProperties, this.subIndent);
        String str2;
        if (this.hashKeyColumns.length == 1) {
            str2 = MessageService.getTextMessage("43X29.U") + " " + this.hashKeyColumns[0];
        }
        else {
            String s = MessageService.getTextMessage("43X30.U") + " (" + this.hashKeyColumns[0];
            for (int i = 1; i < this.hashKeyColumns.length; ++i) {
                s = s + "," + this.hashKeyColumns[i];
            }
            str2 = s + ")";
        }
        return string + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X31.U") + " = " + this.hashtableSize + "\n" + this.indent + str2 + "\n" + this.indent + MessageService.getTextMessage("43X04.U") + " = " + this.rowsSeen + "\n" + this.indent + MessageService.getTextMessage("43X32.U") + " = " + this.rowsFiltered + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.dumpEstimatedCosts(this.subIndent) + "\n" + ((this.rowsSeen > 0) ? (this.subIndent + MessageService.getTextMessage("43X33.U") + " = " + this.nextTime / this.rowsSeen + "\n") : "") + "\n" + string2 + this.subIndent + MessageService.getTextMessage("43X34.U") + ":\n" + StringUtil.ensureIndent(this.startPosition, n + 2) + "\n" + this.subIndent + MessageService.getTextMessage("43X35.U") + ":\n" + StringUtil.ensureIndent(this.stopPosition, n + 2) + "\n" + this.subIndent + MessageService.getTextMessage("43X36.U") + ":\n" + StringUtil.ensureIndent(this.scanQualifiers, n + 2) + "\n" + this.subIndent + MessageService.getTextMessage("43X37.U") + ":\n" + StringUtil.ensureIndent(this.nextQualifiers, n + 2) + "\n" + this.dumpEstimatedCosts(this.subIndent);
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
    
    public String getNodeOn() {
        return MessageService.getTextMessage("43X38.U", this.tableName, this.indexName);
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage("43X39.U");
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        xplainVisitor.setNumberOfChildren(0);
        xplainVisitor.visit(this);
    }
    
    public String getRSXplainType() {
        return "DISTINCTSCAN";
    }
}
