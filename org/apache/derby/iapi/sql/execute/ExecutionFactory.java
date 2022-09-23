// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINFactoryIF;
import org.apache.derby.iapi.error.StandardException;

public interface ExecutionFactory
{
    public static final String MODULE = "org.apache.derby.iapi.sql.execute.ExecutionFactory";
    
    ResultSetFactory getResultSetFactory();
    
    ResultSetStatisticsFactory getResultSetStatisticsFactory() throws StandardException;
    
    XPLAINFactoryIF getXPLAINFactory() throws StandardException;
    
    ExecutionContext newExecutionContext(final ContextManager p0);
    
    ResultColumnDescriptor getResultColumnDescriptor(final ResultColumnDescriptor p0);
    
    ResultDescription getResultDescription(final ResultColumnDescriptor[] p0, final String p1);
    
    ScanQualifier[][] getScanQualifier(final int p0);
    
    void releaseScanQualifier(final ScanQualifier[][] p0);
    
    Qualifier getQualifier(final int p0, final int p1, final GeneratedMethod p2, final Activation p3, final boolean p4, final boolean p5, final boolean p6, final int p7);
    
    RowChanger getRowChanger(final long p0, final StaticCompiledOpenConglomInfo p1, final DynamicCompiledOpenConglomInfo p2, final IndexRowGenerator[] p3, final long[] p4, final StaticCompiledOpenConglomInfo[] p5, final DynamicCompiledOpenConglomInfo[] p6, final int p7, final TransactionController p8, final int[] p9, final int[] p10, final Activation p11) throws StandardException;
    
    RowChanger getRowChanger(final long p0, final StaticCompiledOpenConglomInfo p1, final DynamicCompiledOpenConglomInfo p2, final IndexRowGenerator[] p3, final long[] p4, final StaticCompiledOpenConglomInfo[] p5, final DynamicCompiledOpenConglomInfo[] p6, final int p7, final TransactionController p8, final int[] p9, final FormatableBitSet p10, final int[] p11, final int[] p12, final Activation p13) throws StandardException;
    
    ExecRow getValueRow(final int p0);
    
    ExecIndexRow getIndexableRow(final int p0);
    
    ExecIndexRow getIndexableRow(final ExecRow p0);
}
