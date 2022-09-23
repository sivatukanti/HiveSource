// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINResultSetTimingsDescriptor;
import org.apache.derby.impl.sql.execute.xplain.XPLAINUtil;
import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.i18n.MessageService;

public abstract class RealJoinResultSetStatistics extends RealNoPutResultSetStatistics
{
    public int rowsSeenLeft;
    public int rowsSeenRight;
    public int rowsReturned;
    public long restrictionTime;
    public String userSuppliedOptimizerOverrides;
    
    public RealJoinResultSetStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final int rowsSeenLeft, final int rowsSeenRight, final int rowsReturned, final long restrictionTime, final double n9, final double n10, final String userSuppliedOptimizerOverrides) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        this.rowsSeenLeft = rowsSeenLeft;
        this.rowsSeenRight = rowsSeenRight;
        this.rowsReturned = rowsReturned;
        this.restrictionTime = restrictionTime;
        this.userSuppliedOptimizerOverrides = userSuppliedOptimizerOverrides;
    }
    
    public String getNodeName() {
        return MessageService.getTextMessage("43X70.U");
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), new Integer(this.numOpens), null, null, null, (UUID)o2, new Double(this.optimizerEstimatedRowCount), new Double(this.optimizerEstimatedCost), null, null, null, new Integer(this.rowsSeenLeft), new Integer(this.rowsSeenRight), new Integer(this.rowsFiltered), new Integer(this.rowsReturned), null, null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
    
    public Object getResultSetTimingsDescriptor(final Object o) {
        return new XPLAINResultSetTimingsDescriptor((UUID)o, new Long(this.constructorTime), new Long(this.openTime), new Long(this.nextTime), new Long(this.closeTime), new Long(this.getNodeTime()), XPLAINUtil.getAVGNextTime(this.nextTime, this.rowsSeenLeft + this.rowsSeenRight), null, null, null, null);
    }
}
