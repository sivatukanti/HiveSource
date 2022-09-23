// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

public class RealHashJoinStatistics extends RealNestedLoopJoinStatistics
{
    public RealHashJoinStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int n8, final int n9, final int n10, final int n11, final long n12, final boolean b, final double n13, final double n14, final String s, final ResultSetStatistics resultSetStatistics, final ResultSetStatistics resultSetStatistics2) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, b, n13, n14, s, resultSetStatistics, resultSetStatistics2);
    }
    
    protected void setNames() {
        if (this.oneRowRightSide) {
            this.nodeName = MessageService.getTextMessage("43X45.U");
            this.resultSetName = MessageService.getTextMessage("43X46.U");
        }
        else {
            this.nodeName = MessageService.getTextMessage("43X47.U");
            this.resultSetName = MessageService.getTextMessage("43X48.U");
        }
    }
    
    public String getRSXplainType() {
        return "HASHJOIN";
    }
    
    public String getRSXplainDetails() {
        String str = "(" + this.resultSetNumber + ")" + this.resultSetName + ", ";
        if (this.oneRowRightSide) {
            str += ", EXISTS JOIN";
        }
        return str;
    }
}
