// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINScanPropsDescriptor;
import org.apache.derby.impl.sql.execute.xplain.XPLAINUtil;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import org.apache.derby.iapi.services.io.FormatableProperties;

public class RealHashTableStatistics extends RealNoPutResultSetStatistics
{
    public int hashtableSize;
    public int[] hashKeyColumns;
    public String isolationLevel;
    public String nextQualifiers;
    public FormatableProperties scanProperties;
    public ResultSetStatistics childResultSetStatistics;
    public ResultSetStatistics[] subqueryTrackingArray;
    
    public RealHashTableStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final int hashtableSize, final int[] hashKeyColumns, final String nextQualifiers, final Properties properties, final double n9, final double n10, final ResultSetStatistics[] subqueryTrackingArray, final ResultSetStatistics childResultSetStatistics) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.hashtableSize = hashtableSize;
        this.hashKeyColumns = hashKeyColumns;
        this.nextQualifiers = nextQualifiers;
        this.scanProperties = new FormatableProperties();
        if (properties != null) {
            final Enumeration<Object> keys = properties.keys();
            while (keys.hasMoreElements()) {
                final String s = keys.nextElement();
                this.scanProperties.put(s, properties.get(s));
            }
        }
        this.subqueryTrackingArray = subqueryTrackingArray;
        this.childResultSetStatistics = childResultSetStatistics;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        String str = "";
        this.initFormatInfo(n);
        if (this.subqueryTrackingArray != null) {
            int n2 = 0;
            for (int i = 0; i < this.subqueryTrackingArray.length; ++i) {
                if (this.subqueryTrackingArray[i] != null) {
                    if (n2 == 0) {
                        str = this.indent + MessageService.getTextMessage("43X56.U") + ":\n";
                        n2 = 1;
                    }
                    str += this.subqueryTrackingArray[i].getStatementExecutionPlanText(this.sourceDepth);
                }
            }
        }
        this.initFormatInfo(n);
        String str2;
        if (this.hashKeyColumns.length == 1) {
            str2 = MessageService.getTextMessage("43X53.U") + " " + this.hashKeyColumns[0];
        }
        else {
            String s = MessageService.getTextMessage("43X54.U") + " (" + this.hashKeyColumns[0];
            for (int j = 1; j < this.hashKeyColumns.length; ++j) {
                s = s + "," + this.hashKeyColumns[j];
            }
            str2 = s + ")";
        }
        return this.indent + MessageService.getTextMessage("43X57.U") + " (" + this.resultSetNumber + "):" + "\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X31.U") + " = " + this.hashtableSize + "\n" + this.indent + str2 + "\n" + this.indent + MessageService.getTextMessage("43X04.U") + " = " + this.rowsSeen + "\n" + this.indent + MessageService.getTextMessage("43X32.U") + " = " + this.rowsFiltered + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.dumpEstimatedCosts(this.subIndent) + "\n" + ((this.rowsSeen > 0) ? (this.subIndent + MessageService.getTextMessage("43X33.U") + " = " + this.nextTime / this.rowsSeen + "\n") : "") + "\n" + this.subIndent + MessageService.getTextMessage("43X37.U") + ":\n" + this.nextQualifiers + "\n" + this.indent + MessageService.getTextMessage("43X05.U") + ":\n" + this.childResultSetStatistics.getStatementExecutionPlanText(this.sourceDepth);
    }
    
    public String getScanStatisticsText(final String s, final int n) {
        if (s == null) {
            return this.getStatementExecutionPlanText(n);
        }
        return null;
    }
    
    public String toString() {
        return this.getStatementExecutionPlanText(0);
    }
    
    public String getNodeOn() {
        return "";
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage("43X58.U");
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
        return "HASHTABLE";
    }
    
    public String getRSXplainDetails() {
        return "(" + this.resultSetNumber + ")";
    }
    
    public Object getScanPropsDescriptor(final Object o) {
        return XPLAINUtil.extractScanProps(new XPLAINScanPropsDescriptor((UUID)o, "Temporary HashTable", null, null, XPLAINUtil.getIsolationLevelCode(this.isolationLevel), null, null, null, null, null, null, null, null, null, null, null, this.nextQualifiers, XPLAINUtil.getHashKeyColumnNumberString(this.hashKeyColumns), new Integer(this.hashtableSize)), this.scanProperties);
    }
}
