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

public class RealTableScanStatistics extends RealNoPutResultSetStatistics
{
    public boolean isConstraint;
    public boolean coarserLock;
    public int fetchSize;
    public String isolationLevel;
    public String tableName;
    public String userSuppliedOptimizerOverrides;
    public String indexName;
    public String lockString;
    public String qualifiers;
    public String startPosition;
    public String stopPosition;
    public FormatableProperties scanProperties;
    
    public RealTableScanStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final String tableName, final String userSuppliedOptimizerOverrides, final String indexName, final boolean isConstraint, final String qualifiers, final Properties properties, final String startPosition, final String stopPosition, final String isolationLevel, final String lockString, final int fetchSize, final boolean coarserLock, final double n9, final double n10) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.tableName = tableName;
        this.userSuppliedOptimizerOverrides = userSuppliedOptimizerOverrides;
        this.indexName = indexName;
        this.isConstraint = isConstraint;
        this.qualifiers = qualifiers;
        this.scanProperties = new FormatableProperties();
        final Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            final String s = keys.nextElement();
            this.scanProperties.put(s, properties.get(s));
        }
        this.startPosition = startPosition;
        this.stopPosition = stopPosition;
        this.isolationLevel = isolationLevel;
        this.lockString = lockString;
        this.fetchSize = fetchSize;
        this.coarserLock = coarserLock;
    }
    
    public String getStatementExecutionPlanText(final int n) {
        String string = "";
        this.initFormatInfo(n);
        if (this.userSuppliedOptimizerOverrides != null) {
            string = this.indent + MessageService.getTextMessage("43Y56.U", this.tableName, this.userSuppliedOptimizerOverrides) + "\n";
        }
        String str;
        if (this.indexName != null) {
            str = string + this.indent + MessageService.getTextMessage("43Y09.U", this.tableName, this.isConstraint ? "constraint" : "index", this.indexName);
        }
        else {
            str = string + this.indent + MessageService.getTextMessage("43Y10.U", this.tableName);
        }
        String s = str + " " + MessageService.getTextMessage("43X72.U", this.isolationLevel, this.lockString);
        if (this.coarserLock) {
            s = s + " (" + MessageService.getTextMessage("43Y11.U") + ")";
        }
        return s + "\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X04.U") + " = " + this.rowsSeen + "\n" + this.indent + MessageService.getTextMessage("43X32.U") + " = " + this.rowsFiltered + "\n" + this.indent + MessageService.getTextMessage("43Y12.U") + " = " + this.fetchSize + "\n" + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + ((this.rowsSeen > 0) ? (this.subIndent + MessageService.getTextMessage("43X33.U") + " = " + this.nextTime / this.rowsSeen + "\n") : "") + "\n" + (this.indent + MessageService.getTextMessage("43X28.U") + ":\n" + PropertyUtil.sortProperties(this.scanProperties, this.subIndent)) + this.subIndent + MessageService.getTextMessage("43X34.U") + ":\n" + StringUtil.ensureIndent(this.startPosition, n + 2) + "\n" + this.subIndent + MessageService.getTextMessage("43X35.U") + ":\n" + StringUtil.ensureIndent(this.stopPosition, n + 2) + "\n" + this.subIndent + MessageService.getTextMessage("43Y13.U") + ":\n" + StringUtil.ensureIndent(this.qualifiers, n + 2) + "\n" + this.dumpEstimatedCosts(this.subIndent);
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
    
    public String getNodeName() {
        return MessageService.getTextMessage((this.indexName == null) ? "43X73.U" : "43X74.U");
    }
    
    public String getNodeOn() {
        if (this.indexName == null) {
            return MessageService.getTextMessage("43X75.U", this.tableName);
        }
        return MessageService.getTextMessage("43X38.U", this.tableName, this.indexName);
    }
    
    public void accept(final XPLAINVisitor xplainVisitor) {
        xplainVisitor.setNumberOfChildren(0);
        xplainVisitor.visit(this);
    }
    
    public String getRSXplainType() {
        if (this.indexName != null) {
            return this.isConstraint ? "CONSTRAINTSCAN" : "INDEXSCAN";
        }
        return "TABLESCAN";
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
        return XPLAINUtil.extractScanProps(new XPLAINScanPropsDescriptor((UUID)o, s2, s, null, XPLAINUtil.getIsolationLevelCode(this.isolationLevel), null, null, null, null, null, null, null, new Integer(this.fetchSize), this.startPosition, this.stopPosition, this.qualifiers, null, null, null), this.scanProperties);
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), new Integer(this.numOpens), null, XPLAINUtil.getLockModeCode(this.lockString), XPLAINUtil.getLockGranularityCode(this.lockString), (UUID)o2, new Double(this.optimizerEstimatedRowCount), new Double(this.optimizerEstimatedCost), null, null, null, new Integer(this.rowsSeen), null, new Integer(this.rowsFiltered), new Integer(this.rowsSeen - this.rowsFiltered), null, null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
}
