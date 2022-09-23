// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINResultSetTimingsDescriptor;
import org.apache.derby.impl.sql.execute.xplain.XPLAINUtil;
import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.catalog.UUID;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

abstract class RealBasicNoPutResultSetStatistics implements ResultSetStatistics
{
    public int numOpens;
    public int rowsSeen;
    public int rowsFiltered;
    public long constructorTime;
    public long openTime;
    public long nextTime;
    public long closeTime;
    public long inspectOverall;
    public long inspectNum;
    public String inspectDesc;
    public double optimizerEstimatedRowCount;
    public double optimizerEstimatedCost;
    
    public RealBasicNoPutResultSetStatistics(final int numOpens, final int rowsSeen, final int rowsFiltered, final long constructorTime, final long openTime, final long nextTime, final long closeTime, final double optimizerEstimatedRowCount, final double optimizerEstimatedCost) {
        this.numOpens = numOpens;
        this.rowsSeen = rowsSeen;
        this.rowsFiltered = rowsFiltered;
        this.constructorTime = constructorTime;
        this.openTime = openTime;
        this.nextTime = nextTime;
        this.closeTime = closeTime;
        this.optimizerEstimatedRowCount = optimizerEstimatedRowCount;
        this.optimizerEstimatedCost = optimizerEstimatedCost;
    }
    
    protected final String dumpTimeStats(final String s, final String s2) {
        return s2 + MessageService.getTextMessage("42Z33.U") + " " + this.constructorTime + "\n" + s2 + MessageService.getTextMessage("42Z34.U") + " " + this.openTime + "\n" + s2 + MessageService.getTextMessage("42Z35.U") + " " + this.nextTime + "\n" + s2 + MessageService.getTextMessage("42Z36.U") + " " + this.closeTime;
    }
    
    protected final String dumpEstimatedCosts(final String s) {
        return s + MessageService.getTextMessage("43X07.U", new Double(this.optimizerEstimatedRowCount)) + "\n" + s + MessageService.getTextMessage("43X08.U", new Double(this.optimizerEstimatedCost));
    }
    
    public Vector getChildren() {
        return new Vector();
    }
    
    public long getTotalTime() {
        return this.openTime + this.nextTime + this.closeTime;
    }
    
    public long getChildrenTime() {
        long n = 0L;
        final Enumeration<RealBasicNoPutResultSetStatistics> elements = this.getChildren().elements();
        while (elements.hasMoreElements()) {
            n += elements.nextElement().getTotalTime();
        }
        return n;
    }
    
    public long getNodeTime() {
        return this.getTotalTime() - this.getChildrenTime();
    }
    
    public abstract String getNodeName();
    
    public String getNodeOn() {
        return "";
    }
    
    public double getEstimatedRowCount() {
        return this.optimizerEstimatedRowCount;
    }
    
    public String getRSXplainDetails() {
        return null;
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), new Integer(this.numOpens), null, null, null, (UUID)o2, new Double(this.optimizerEstimatedRowCount), new Double(this.optimizerEstimatedCost), null, null, null, new Integer(this.rowsSeen), null, new Integer(this.rowsFiltered), new Integer(this.rowsSeen - this.rowsFiltered), null, null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
    
    public Object getResultSetTimingsDescriptor(final Object o) {
        return new XPLAINResultSetTimingsDescriptor((UUID)o, new Long(this.constructorTime), new Long(this.openTime), new Long(this.nextTime), new Long(this.closeTime), new Long(this.getNodeTime()), XPLAINUtil.getAVGNextTime(this.nextTime, this.rowsSeen), null, null, null, null);
    }
    
    public Object getSortPropsDescriptor(final Object o) {
        return null;
    }
    
    public Object getScanPropsDescriptor(final Object o) {
        return null;
    }
}
