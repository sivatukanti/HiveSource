// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.xplain;

import java.util.Iterator;
import org.apache.derby.impl.sql.catalog.XPLAINSortPropsDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINScanPropsDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINResultSetTimingsDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import java.sql.PreparedStatement;
import org.apache.derby.iapi.jdbc.ConnectionContext;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import java.sql.Timestamp;
import org.apache.derby.iapi.services.info.JVMInfo;
import org.apache.derby.iapi.sql.execute.RunTimeStatistics;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import java.util.ArrayList;
import java.util.Stack;
import java.util.List;
import org.apache.derby.catalog.UUID;
import org.apache.derby.impl.sql.catalog.XPLAINStatementTimingsDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINStatementDescriptor;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;

public class XPLAINSystemTableVisitor implements XPLAINVisitor
{
    private boolean no_call_stmts;
    private LanguageConnectionContext lcc;
    private DataDictionary dd;
    private Activation activation;
    private boolean considerTimingInformation;
    private XPLAINStatementDescriptor stmt;
    private XPLAINStatementTimingsDescriptor stmtTimings;
    private UUID stmtUUID;
    private List rsets;
    private List rsetsTimings;
    private List sortrsets;
    private List scanrsets;
    private int noChildren;
    private Stack UUIDStack;
    
    public XPLAINSystemTableVisitor() {
        this.no_call_stmts = true;
        this.considerTimingInformation = false;
        this.stmtTimings = null;
        this.rsets = new ArrayList();
        this.rsetsTimings = new ArrayList();
        this.sortrsets = new ArrayList();
        this.scanrsets = new ArrayList();
        this.UUIDStack = new Stack();
    }
    
    private void pushUUIDnoChildren(final UUID item) {
        for (int i = 0; i < this.noChildren; ++i) {
            this.UUIDStack.push(item);
        }
    }
    
    public void setNumberOfChildren(final int noChildren) {
        this.noChildren = noChildren;
    }
    
    public void visit(final ResultSetStatistics resultSetStatistics) {
        Object uuid = null;
        if (this.considerTimingInformation) {
            uuid = this.dd.getUUIDFactory().createUUID();
            this.rsetsTimings.add(resultSetStatistics.getResultSetTimingsDescriptor(uuid));
        }
        UUID uuid2 = this.dd.getUUIDFactory().createUUID();
        final Object sortPropsDescriptor = resultSetStatistics.getSortPropsDescriptor(uuid2);
        if (sortPropsDescriptor != null) {
            this.sortrsets.add(sortPropsDescriptor);
        }
        else {
            uuid2 = null;
        }
        UUID uuid3 = this.dd.getUUIDFactory().createUUID();
        final Object scanPropsDescriptor = resultSetStatistics.getScanPropsDescriptor(uuid3);
        if (scanPropsDescriptor != null) {
            this.scanrsets.add(scanPropsDescriptor);
        }
        else {
            uuid3 = null;
        }
        final UUID uuid4 = this.dd.getUUIDFactory().createUUID();
        this.rsets.add(resultSetStatistics.getResultSetDescriptor(uuid4, this.UUIDStack.empty() ? null : this.UUIDStack.pop(), uuid3, uuid2, this.stmtUUID, uuid));
        this.pushUUIDnoChildren(uuid4);
    }
    
    public void reset() {
        this.lcc = this.activation.getLanguageConnectionContext();
        this.dd = this.lcc.getDataDictionary();
    }
    
    public void doXPLAIN(final RunTimeStatistics runTimeStatistics, final Activation activation) throws StandardException {
        this.activation = activation;
        this.reset();
        this.considerTimingInformation = this.lcc.getStatisticsTiming();
        UUID uuid = null;
        if (this.considerTimingInformation) {
            uuid = this.dd.getUUIDFactory().createUUID();
            final Timestamp endExecutionTimestamp = runTimeStatistics.getEndExecutionTimestamp();
            final Timestamp beginExecutionTimestamp = runTimeStatistics.getBeginExecutionTimestamp();
            long value;
            if (endExecutionTimestamp != null && beginExecutionTimestamp != null) {
                value = endExecutionTimestamp.getTime() - beginExecutionTimestamp.getTime();
            }
            else {
                value = 0L;
            }
            this.stmtTimings = new XPLAINStatementTimingsDescriptor(uuid, new Long(runTimeStatistics.getParseTimeInMillis()), new Long(runTimeStatistics.getBindTimeInMillis()), new Long(runTimeStatistics.getOptimizeTimeInMillis()), new Long(runTimeStatistics.getGenerateTimeInMillis()), new Long(runTimeStatistics.getCompileTimeInMillis()), new Long(value), runTimeStatistics.getBeginCompilationTimestamp(), runTimeStatistics.getEndCompilationTimestamp(), runTimeStatistics.getBeginExecutionTimestamp(), runTimeStatistics.getEndExecutionTimestamp());
        }
        this.stmtUUID = this.dd.getUUIDFactory().createUUID();
        final String statementType = XPLAINUtil.getStatementType(runTimeStatistics.getStatementText());
        if (statementType.equalsIgnoreCase("C") && this.no_call_stmts) {
            return;
        }
        this.stmt = new XPLAINStatementDescriptor(this.stmtUUID, runTimeStatistics.getStatementName(), statementType, runTimeStatistics.getStatementText(), Integer.toString(JVMInfo.JDK_ID), System.getProperty("os.name"), this.lcc.getXplainOnlyMode() ? "O" : "F", new Timestamp(System.currentTimeMillis()), Thread.currentThread().toString(), this.lcc.getTransactionExecute().getTransactionIdString(), Integer.toString(this.lcc.getInstanceNumber()), this.lcc.getDbname(), this.lcc.getDrdaID(), uuid);
        try {
            this.addStmtDescriptorsToSystemCatalog();
            runTimeStatistics.acceptFromTopResultSet(this);
            this.addArraysToSystemCatalogs();
        }
        catch (SQLException ex) {
            throw StandardException.plainWrapException(ex);
        }
        this.clean();
    }
    
    private void clean() {
        this.activation = null;
        this.lcc = null;
        this.dd = null;
        this.stmtUUID = null;
        this.stmt = null;
        this.stmtTimings = null;
        this.rsets.clear();
        this.rsetsTimings.clear();
        this.sortrsets.clear();
        this.scanrsets.clear();
        this.UUIDStack.clear();
    }
    
    private Connection getDefaultConn() throws SQLException {
        return ((ConnectionContext)this.lcc.getContextManager().getContext("JDBC_ConnectionContext")).getNestedConnection(true);
    }
    
    private void addStmtDescriptorsToSystemCatalog() throws StandardException, SQLException {
        final boolean runTimeStatisticsMode = this.lcc.getRunTimeStatisticsMode();
        this.lcc.setRunTimeStatisticsMode(false);
        final Connection defaultConn = this.getDefaultConn();
        final PreparedStatement prepareStatement = defaultConn.prepareStatement((String)this.lcc.getXplainStatement("SYSXPLAIN_STATEMENTS"));
        this.stmt.setStatementParameters(prepareStatement);
        prepareStatement.executeUpdate();
        prepareStatement.close();
        if (this.considerTimingInformation) {
            final PreparedStatement prepareStatement2 = defaultConn.prepareStatement((String)this.lcc.getXplainStatement("SYSXPLAIN_STATEMENT_TIMINGS"));
            this.stmtTimings.setStatementParameters(prepareStatement2);
            prepareStatement2.executeUpdate();
            prepareStatement2.close();
        }
        defaultConn.close();
        this.lcc.setRunTimeStatisticsMode(runTimeStatisticsMode);
    }
    
    private void addArraysToSystemCatalogs() throws StandardException, SQLException {
        final boolean runTimeStatisticsMode = this.lcc.getRunTimeStatisticsMode();
        this.lcc.setRunTimeStatisticsMode(false);
        final Connection defaultConn = this.getDefaultConn();
        final PreparedStatement prepareStatement = defaultConn.prepareStatement((String)this.lcc.getXplainStatement("SYSXPLAIN_RESULTSETS"));
        final Iterator<XPLAINResultSetDescriptor> iterator = (Iterator<XPLAINResultSetDescriptor>)this.rsets.iterator();
        while (iterator.hasNext()) {
            iterator.next().setStatementParameters(prepareStatement);
            prepareStatement.executeUpdate();
        }
        prepareStatement.close();
        if (this.considerTimingInformation) {
            final PreparedStatement prepareStatement2 = defaultConn.prepareStatement((String)this.lcc.getXplainStatement("SYSXPLAIN_RESULTSET_TIMINGS"));
            final Iterator<XPLAINResultSetTimingsDescriptor> iterator2 = (Iterator<XPLAINResultSetTimingsDescriptor>)this.rsetsTimings.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().setStatementParameters(prepareStatement2);
                prepareStatement2.executeUpdate();
            }
            prepareStatement2.close();
        }
        final PreparedStatement prepareStatement3 = defaultConn.prepareStatement((String)this.lcc.getXplainStatement("SYSXPLAIN_SCAN_PROPS"));
        final Iterator<XPLAINScanPropsDescriptor> iterator3 = (Iterator<XPLAINScanPropsDescriptor>)this.scanrsets.iterator();
        while (iterator3.hasNext()) {
            iterator3.next().setStatementParameters(prepareStatement3);
            prepareStatement3.executeUpdate();
        }
        prepareStatement3.close();
        final PreparedStatement prepareStatement4 = defaultConn.prepareStatement((String)this.lcc.getXplainStatement("SYSXPLAIN_SORT_PROPS"));
        final Iterator<XPLAINSortPropsDescriptor> iterator4 = (Iterator<XPLAINSortPropsDescriptor>)this.sortrsets.iterator();
        while (iterator4.hasNext()) {
            iterator4.next().setStatementParameters(prepareStatement4);
            prepareStatement4.executeUpdate();
        }
        prepareStatement4.close();
        defaultConn.close();
        this.lcc.setRunTimeStatisticsMode(runTimeStatisticsMode);
    }
}
