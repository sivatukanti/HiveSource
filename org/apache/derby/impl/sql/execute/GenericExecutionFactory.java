// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.execute.ExecRow;
import java.util.Vector;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.jdbc.ConnectionContext;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.execute.RowChanger;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.impl.sql.GenericColumnDescriptor;
import org.apache.derby.impl.sql.GenericResultDescription;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.sql.execute.ScanQualifier;
import org.apache.derby.iapi.sql.execute.ExecutionContext;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Properties;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINFactoryIF;
import org.apache.derby.iapi.sql.execute.ResultSetFactory;
import org.apache.derby.iapi.sql.execute.ResultSetStatisticsFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;

public class GenericExecutionFactory implements ModuleControl, ModuleSupportable, ExecutionFactory
{
    private ResultSetStatisticsFactory rssFactory;
    private ResultSetFactory rsFactory;
    private GenericConstantActionFactory genericConstantActionFactory;
    private XPLAINFactoryIF xplainFactory;
    
    public boolean canSupport(final Properties properties) {
        return Monitor.isDesiredType(properties, 130);
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
    }
    
    public void stop() {
    }
    
    public ResultSetFactory getResultSetFactory() {
        if (this.rsFactory == null) {
            this.rsFactory = new GenericResultSetFactory();
        }
        return this.rsFactory;
    }
    
    public GenericConstantActionFactory getConstantActionFactory() {
        if (this.genericConstantActionFactory == null) {
            this.genericConstantActionFactory = new GenericConstantActionFactory();
        }
        return this.genericConstantActionFactory;
    }
    
    public ResultSetStatisticsFactory getResultSetStatisticsFactory() throws StandardException {
        if (this.rssFactory == null) {
            this.rssFactory = (ResultSetStatisticsFactory)Monitor.bootServiceModule(false, this, "org.apache.derby.iapi.sql.execute.ResultSetStatisticsFactory", null);
        }
        return this.rssFactory;
    }
    
    public ExecutionContext newExecutionContext(final ContextManager contextManager) {
        return new GenericExecutionContext(contextManager, this);
    }
    
    public ScanQualifier[][] getScanQualifier(final int n) {
        final GenericScanQualifier[] array = new GenericScanQualifier[n];
        for (int i = 0; i < n; ++i) {
            array[i] = new GenericScanQualifier();
        }
        return new ScanQualifier[][] { array };
    }
    
    public ResultDescription getResultDescription(final ResultColumnDescriptor[] array, final String s) {
        return new GenericResultDescription(array, s);
    }
    
    public ResultColumnDescriptor getResultColumnDescriptor(final ResultColumnDescriptor resultColumnDescriptor) {
        return new GenericColumnDescriptor(resultColumnDescriptor);
    }
    
    public void releaseScanQualifier(final ScanQualifier[][] array) {
    }
    
    public Qualifier getQualifier(final int n, final int n2, final GeneratedMethod generatedMethod, final Activation activation, final boolean b, final boolean b2, final boolean b3, final int n3) {
        return new GenericQualifier(n, n2, generatedMethod, activation, b, b2, b3, n3);
    }
    
    public RowChanger getRowChanger(final long n, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo, final IndexRowGenerator[] array, final long[] array2, final StaticCompiledOpenConglomInfo[] array3, final DynamicCompiledOpenConglomInfo[] array4, final int n2, final TransactionController transactionController, final int[] array5, final int[] array6, final Activation activation) throws StandardException {
        return new RowChangerImpl(n, staticCompiledOpenConglomInfo, dynamicCompiledOpenConglomInfo, array, array2, array3, array4, n2, array5, transactionController, null, array6, activation);
    }
    
    public RowChanger getRowChanger(final long n, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo, final IndexRowGenerator[] array, final long[] array2, final StaticCompiledOpenConglomInfo[] array3, final DynamicCompiledOpenConglomInfo[] array4, final int n2, final TransactionController transactionController, final int[] array5, final FormatableBitSet set, final int[] array6, final int[] array7, final Activation activation) throws StandardException {
        return new RowChangerImpl(n, staticCompiledOpenConglomInfo, dynamicCompiledOpenConglomInfo, array, array2, array3, array4, n2, array5, transactionController, set, array6, activation);
    }
    
    public InternalTriggerExecutionContext getTriggerExecutionContext(final LanguageConnectionContext languageConnectionContext, final ConnectionContext connectionContext, final String s, final int n, final int[] array, final String[] array2, final UUID uuid, final String s2, final Vector vector) throws StandardException {
        return new InternalTriggerExecutionContext(languageConnectionContext, connectionContext, s, n, array, array2, uuid, s2, vector);
    }
    
    public ExecRow getValueRow(final int n) {
        return new ValueRow(n);
    }
    
    public ExecIndexRow getIndexableRow(final int n) {
        return new IndexRow(n);
    }
    
    public ExecIndexRow getIndexableRow(final ExecRow execRow) {
        if (execRow instanceof ExecIndexRow) {
            return (ExecIndexRow)execRow;
        }
        return new IndexValueRow(execRow);
    }
    
    public XPLAINFactoryIF getXPLAINFactory() throws StandardException {
        if (this.xplainFactory == null) {
            this.xplainFactory = (XPLAINFactoryIF)Monitor.bootServiceModule(false, this, "org.apache.derby.iapi.sql.execute.xplain.XPLAINFactoryIF", null);
        }
        return this.xplainFactory;
    }
}
