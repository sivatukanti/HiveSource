// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINScanPropsDescriptor;
import org.apache.derby.impl.sql.execute.xplain.XPLAINUtil;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.util.PropertyUtil;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.derby.iapi.services.io.FormatableProperties;

public class RealHashScanStatistics extends RealNoPutResultSetStatistics
{
    public boolean isConstraint;
    public int hashtableSize;
    public int[] hashKeyColumns;
    public String isolationLevel;
    public String lockString;
    public String tableName;
    public String indexName;
    public String nextQualifiers;
    public String scanQualifiers;
    public String startPosition;
    public String stopPosition;
    public FormatableProperties scanProperties;
    
    public RealHashScanStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final String tableName, final String indexName, final boolean isConstraint, final int hashtableSize, final int[] hashKeyColumns, final String scanQualifiers, final String nextQualifiers, final Properties properties, final String startPosition, final String stopPosition, final String isolationLevel, final String lockString, final double n9, final double n10) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.startPosition = null;
        this.stopPosition = null;
        this.tableName = tableName;
        this.indexName = indexName;
        this.isConstraint = isConstraint;
        this.hashtableSize = hashtableSize;
        this.hashKeyColumns = hashKeyColumns;
        this.scanQualifiers = scanQualifiers;
        this.nextQualifiers = nextQualifiers;
        this.scanProperties = new FormatableProperties();
        if (properties != null) {
            final Enumeration<Object> keys = properties.keys();
            while (keys.hasMoreElements()) {
                final String s = keys.nextElement();
                this.scanProperties.put(s, properties.get(s));
            }
        }
        this.startPosition = startPosition;
        this.stopPosition = stopPosition;
        this.isolationLevel = isolationLevel;
        this.lockString = lockString;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        String str;
        if (this.indexName != null) {
            str = this.indent + MessageService.getTextMessage("43X51.U", this.tableName, this.isConstraint ? "constraint" : "index", this.indexName);
        }
        else {
            str = this.indent + MessageService.getTextMessage("43X52.U", this.tableName);
        }
        final String string = str + " " + MessageService.getTextMessage("43X27.U", this.isolationLevel, this.lockString) + ": \n";
        final String string2 = this.indent + MessageService.getTextMessage("43X28.U") + ": \n" + PropertyUtil.sortProperties(this.scanProperties, this.subIndent);
        String str2;
        if (this.hashKeyColumns.length == 1) {
            str2 = MessageService.getTextMessage("43X53.U") + " " + this.hashKeyColumns[0];
        }
        else {
            String s = MessageService.getTextMessage("43X54.U") + " (" + this.hashKeyColumns[0];
            for (int i = 1; i < this.hashKeyColumns.length; ++i) {
                s = s + "," + this.hashKeyColumns[i];
            }
            str2 = s + ")";
        }
        return string + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X31.U") + " = " + this.hashtableSize + "\n" + this.indent + str2 + "\n" + this.indent + MessageService.getTextMessage("43X04.U") + " = " + this.rowsSeen + "\n" + this.indent + MessageService.getTextMessage("43X32.U") + " = " + this.rowsFiltered + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + ((this.rowsSeen > 0) ? (this.subIndent + MessageService.getTextMessage("43X33.U") + " = " + this.nextTime / this.rowsSeen + "\n") : "") + "\n" + string2 + this.subIndent + MessageService.getTextMessage("43X34.U") + ":\n" + StringUtil.ensureIndent(this.startPosition, n + 2) + "\n" + this.subIndent + MessageService.getTextMessage("43X35.U") + ":\n" + StringUtil.ensureIndent(this.stopPosition, n + 2) + "\n" + this.subIndent + MessageService.getTextMessage("43X36.U") + ":\n" + StringUtil.ensureIndent(this.scanQualifiers, n + 2) + "\n" + this.subIndent + MessageService.getTextMessage("43X37.U") + ":\n" + StringUtil.ensureIndent(this.nextQualifiers, n + 2) + "\n" + this.dumpEstimatedCosts(this.subIndent);
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
        return MessageService.getTextMessage("43X55.U");
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        xplainVisitor.setNumberOfChildren(0);
        xplainVisitor.visit(this);
    }
    
    public String getRSXplainType() {
        return "HASHSCAN";
    }
    
    public String getRSXplainDetails() {
        if (this.indexName != null) {
            return (this.isConstraint ? "C: " : "I: ") + this.indexName;
        }
        return "T: " + this.tableName;
    }
    
    public Object getScanPropsDescriptor(final Object o) {
        String s;
        String s2;
        if (this.indexName != null) {
            if (this.isConstraint) {
                s = "C";
                s2 = this.indexName;
            }
            else {
                s = "I";
                s2 = this.indexName;
            }
        }
        else {
            s = "T";
            s2 = this.tableName;
        }
        return XPLAINUtil.extractScanProps(new XPLAINScanPropsDescriptor((UUID)o, s2, s, null, XPLAINUtil.getIsolationLevelCode(this.isolationLevel), null, null, null, null, null, null, null, null, this.startPosition, this.stopPosition, this.scanQualifiers, this.nextQualifiers, XPLAINUtil.getHashKeyColumnNumberString(this.hashKeyColumns), new Integer(this.hashtableSize)), this.scanProperties);
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), new Integer(this.numOpens), null, XPLAINUtil.getLockModeCode(this.lockString), XPLAINUtil.getLockGranularityCode(this.lockString), (UUID)o2, new Double(this.optimizerEstimatedRowCount), new Double(this.optimizerEstimatedCost), null, null, null, new Integer(this.rowsSeen), null, new Integer(this.rowsFiltered), new Integer(this.rowsSeen - this.rowsFiltered), null, null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
}
