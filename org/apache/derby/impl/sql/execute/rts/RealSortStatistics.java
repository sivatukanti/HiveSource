// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINSortPropsDescriptor;
import org.apache.derby.impl.sql.execute.xplain.XPLAINUtil;
import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import java.util.Vector;
import org.apache.derby.iapi.util.PropertyUtil;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.derby.iapi.services.io.FormatableProperties;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealSortStatistics extends RealNoPutResultSetStatistics
{
    public int rowsInput;
    public int rowsReturned;
    public boolean eliminateDuplicates;
    public boolean inSortedOrder;
    public ResultSetStatistics childResultSetStatistics;
    public FormatableProperties sortProperties;
    
    public RealSortStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final int rowsInput, final int rowsReturned, final boolean eliminateDuplicates, final boolean inSortedOrder, final Properties properties, final double n9, final double n10, final ResultSetStatistics childResultSetStatistics) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.rowsInput = rowsInput;
        this.rowsReturned = rowsReturned;
        this.eliminateDuplicates = eliminateDuplicates;
        this.inSortedOrder = inSortedOrder;
        this.childResultSetStatistics = childResultSetStatistics;
        this.sortProperties = new FormatableProperties();
        final Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            final String s = keys.nextElement();
            this.sortProperties.put(s, properties.get(s));
        }
    }
    
    public String getStatementExecutionPlanText(final int n) {
        this.initFormatInfo(n);
        return this.indent + MessageService.getTextMessage("43Y06.U") + ":\n" + this.indent + MessageService.getTextMessage("43X03.U") + " = " + this.numOpens + "\n" + this.indent + MessageService.getTextMessage("43X21.U") + " = " + this.rowsInput + "\n" + this.indent + MessageService.getTextMessage("43X81.U") + " = " + this.rowsReturned + "\n" + this.indent + MessageService.getTextMessage("43Y07.U") + " = " + this.eliminateDuplicates + "\n" + this.indent + MessageService.getTextMessage("43X43.U") + " = " + this.inSortedOrder + "\n" + (this.inSortedOrder ? "" : (this.indent + MessageService.getTextMessage("43X40.U") + ": \n" + PropertyUtil.sortProperties(this.sortProperties, this.subIndent))) + this.dumpTimeStats(this.indent, this.subIndent) + "\n" + this.dumpEstimatedCosts(this.subIndent) + "\n" + this.indent + MessageService.getTextMessage("43X05.U") + ":\n" + this.childResultSetStatistics.getStatementExecutionPlanText(this.sourceDepth) + "\n";
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
        return MessageService.getTextMessage("43Y08.U");
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
        return "SORT";
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), new Integer(this.numOpens), null, null, null, (UUID)o2, new Double(this.optimizerEstimatedRowCount), new Double(this.optimizerEstimatedCost), null, null, new Integer(this.rowsInput), new Integer(this.rowsSeen), null, new Integer(this.rowsFiltered), new Integer(this.rowsReturned), null, null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
    
    public Object getSortPropsDescriptor(final Object o) {
        return XPLAINUtil.extractSortProps(new XPLAINSortPropsDescriptor((UUID)o, null, null, null, null, null, XPLAINUtil.getYesNoCharFromBoolean(this.eliminateDuplicates), XPLAINUtil.getYesNoCharFromBoolean(this.inSortedOrder), null), this.sortProperties);
    }
}
