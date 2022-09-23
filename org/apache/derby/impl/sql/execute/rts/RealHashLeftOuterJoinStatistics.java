// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealHashLeftOuterJoinStatistics extends RealNestedLoopLeftOuterJoinStatistics
{
    public RealHashLeftOuterJoinStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final int n9, final int n10, final int n11, final long n12, final double n13, final double n14, final String s, final ResultSetStatistics resultSetStatistics, final ResultSetStatistics resultSetStatistics2, final int n15) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, s, resultSetStatistics, resultSetStatistics2, n15);
    }
    
    protected void setNames() {
        this.nodeName = MessageService.getTextMessage("43X49.U");
        this.resultSetName = MessageService.getTextMessage("43X50.U");
    }
    
    public String getRSXplainType() {
        return "LOHASHJOIN";
    }
    
    public String getRSXplainDetails() {
        String str = "(" + this.resultSetNumber + ")" + this.resultSetName + ", ";
        if (this.oneRowRightSide) {
            str += ", EXISTS JOIN";
        }
        return str;
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), new Integer(this.numOpens), null, null, null, (UUID)o2, new Double(this.optimizerEstimatedRowCount), new Double(this.optimizerEstimatedCost), null, null, null, new Integer(this.rowsSeenLeft), new Integer(this.rowsSeenRight), new Integer(this.rowsFiltered), new Integer(this.rowsReturned), new Integer(this.emptyRightRowsReturned), null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
}
