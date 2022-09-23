// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.db;

import java.sql.SQLException;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;

public class OptimizerTrace
{
    public static boolean setOptimizerTrace(final boolean optimizerTrace) {
        boolean setOptimizerTrace = false;
        try {
            setOptimizerTrace = ConnectionUtil.getCurrentLCC().setOptimizerTrace(optimizerTrace);
        }
        catch (Throwable t) {}
        return setOptimizerTrace;
    }
    
    public static void nullifyTrace() throws SQLException {
        ConnectionUtil.getCurrentLCC().setOptimizerTraceOutput(null);
    }
    
    public static boolean setOptimizerTraceHtml(final boolean optimizerTraceHtml) {
        boolean setOptimizerTraceHtml = false;
        try {
            setOptimizerTraceHtml = ConnectionUtil.getCurrentLCC().setOptimizerTraceHtml(optimizerTraceHtml);
        }
        catch (Throwable t) {}
        return setOptimizerTraceHtml;
    }
    
    public static String getOptimizerTraceOutput() {
        String optimizerTraceOutput = null;
        try {
            optimizerTraceOutput = ConnectionUtil.getCurrentLCC().getOptimizerTraceOutput();
        }
        catch (Throwable t) {}
        return optimizerTraceOutput;
    }
    
    public static boolean writeOptimizerTraceOutputHtml(final String s) {
        boolean b = true;
        try {
            getOptimizerTraceOutput();
        }
        catch (Throwable t) {
            b = false;
        }
        return b;
    }
}
