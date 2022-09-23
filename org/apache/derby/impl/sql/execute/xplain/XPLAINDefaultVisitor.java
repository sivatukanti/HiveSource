// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.xplain;

import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import java.sql.SQLException;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.RunTimeStatistics;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;

public class XPLAINDefaultVisitor implements XPLAINVisitor
{
    public void visit(final ResultSetStatistics resultSetStatistics) {
    }
    
    public void reset() {
    }
    
    public void doXPLAIN(final RunTimeStatistics runTimeStatistics, final Activation activation) {
        try {
            final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
            final HeaderPrintWriter headerPrintWriter = currentLCC.getLogQueryPlan() ? Monitor.getStream() : null;
            if (headerPrintWriter != null) {
                headerPrintWriter.printlnWithHeader("(XID = " + currentLCC.getTransactionExecute().getTransactionIdString() + "), " + "(SESSIONID = " + currentLCC.getInstanceNumber() + "), " + runTimeStatistics.getStatementText() + " ******* " + runTimeStatistics.getStatementExecutionPlanText());
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setNumberOfChildren(final int n) {
    }
}
