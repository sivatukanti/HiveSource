// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.txn;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.metastore.api.TxnState;
import org.apache.hadoop.hive.metastore.api.TxnInfo;
import org.apache.hadoop.hive.common.ValidTxnList;
import org.apache.hadoop.hive.metastore.api.GetOpenTxnsInfoResponse;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.hive.metastore.api.CompactionType;
import org.apache.hadoop.hive.metastore.api.MetaException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;

public class CompactionTxnHandler extends TxnHandler
{
    private static final String CLASS_NAME;
    private static final Log LOG;
    
    public CompactionTxnHandler(final HiveConf conf) {
        super(conf);
    }
    
    public Set<CompactionInfo> findPotentialCompactions(final int maxAborted) throws MetaException {
        Connection dbConn = null;
        final Set<CompactionInfo> response = new HashSet<CompactionInfo>();
        Statement stmt = null;
        try {
            try {
                dbConn = this.getDbConn(2);
                stmt = dbConn.createStatement();
                String s = "select distinct ctc_database, ctc_table, ctc_partition from COMPLETED_TXN_COMPONENTS";
                CompactionTxnHandler.LOG.debug("Going to execute query <" + s + ">");
                ResultSet rs = stmt.executeQuery(s);
                while (rs.next()) {
                    final CompactionInfo info = new CompactionInfo();
                    info.dbname = rs.getString(1);
                    info.tableName = rs.getString(2);
                    info.partName = rs.getString(3);
                    response.add(info);
                }
                s = "select tc_database, tc_table, tc_partition from TXNS, TXN_COMPONENTS where txn_id = tc_txnid and txn_state = 'a' group by tc_database, tc_table, tc_partition having count(*) > " + maxAborted;
                CompactionTxnHandler.LOG.debug("Going to execute query <" + s + ">");
                rs = stmt.executeQuery(s);
                while (rs.next()) {
                    final CompactionInfo info = new CompactionInfo();
                    info.dbname = rs.getString(1);
                    info.tableName = rs.getString(2);
                    info.partName = rs.getString(3);
                    info.tooManyAborts = true;
                    response.add(info);
                }
                CompactionTxnHandler.LOG.debug("Going to rollback");
                dbConn.rollback();
            }
            catch (SQLException e) {
                CompactionTxnHandler.LOG.error("Unable to connect to transaction database " + e.getMessage());
                this.checkRetryable(dbConn, e, "findPotentialCompactions(maxAborted:" + maxAborted + ")");
            }
            finally {
                this.closeDbConn(dbConn);
                this.closeStmt(stmt);
            }
            return response;
        }
        catch (RetryException e2) {
            return this.findPotentialCompactions(maxAborted);
        }
    }
    
    public void setRunAs(final long cq_id, final String user) throws MetaException {
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(8);
                stmt = dbConn.createStatement();
                final String s = "update COMPACTION_QUEUE set cq_run_as = '" + user + "' where cq_id = " + cq_id;
                CompactionTxnHandler.LOG.debug("Going to execute update <" + s + ">");
                if (stmt.executeUpdate(s) != 1) {
                    CompactionTxnHandler.LOG.error("Unable to update compaction record");
                    CompactionTxnHandler.LOG.debug("Going to rollback");
                    dbConn.rollback();
                }
                CompactionTxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
            }
            catch (SQLException e) {
                CompactionTxnHandler.LOG.error("Unable to update compaction queue, " + e.getMessage());
                CompactionTxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "setRunAs(cq_id:" + cq_id + ",user:" + user + ")");
            }
            finally {
                this.closeDbConn(dbConn);
                this.closeStmt(stmt);
            }
        }
        catch (RetryException e2) {
            this.setRunAs(cq_id, user);
        }
    }
    
    public CompactionInfo findNextToCompact(final String workerId) throws MetaException {
        try {
            Connection dbConn = null;
            final CompactionInfo info = new CompactionInfo();
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(8);
                stmt = dbConn.createStatement();
                String s = "select cq_id, cq_database, cq_table, cq_partition, cq_type from COMPACTION_QUEUE where cq_state = 'i'";
                CompactionTxnHandler.LOG.debug("Going to execute query <" + s + ">");
                final ResultSet rs = stmt.executeQuery(s);
                if (!rs.next()) {
                    CompactionTxnHandler.LOG.debug("No compactions found ready to compact");
                    dbConn.rollback();
                    return null;
                }
                info.id = rs.getLong(1);
                info.dbname = rs.getString(2);
                info.tableName = rs.getString(3);
                info.partName = rs.getString(4);
                switch (rs.getString(5).charAt(0)) {
                    case 'a': {
                        info.type = CompactionType.MAJOR;
                        break;
                    }
                    case 'i': {
                        info.type = CompactionType.MINOR;
                        break;
                    }
                    default: {
                        throw new MetaException("Unexpected compaction type " + rs.getString(5));
                    }
                }
                final long now = this.getDbTime(dbConn);
                s = "update COMPACTION_QUEUE set cq_worker_id = '" + workerId + "', " + "cq_start = " + now + ", cq_state = '" + 'w' + "' where cq_id = " + info.id;
                CompactionTxnHandler.LOG.debug("Going to execute update <" + s + ">");
                if (stmt.executeUpdate(s) != 1) {
                    CompactionTxnHandler.LOG.error("Unable to update compaction record");
                    CompactionTxnHandler.LOG.debug("Going to rollback");
                    dbConn.rollback();
                }
                CompactionTxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
                return info;
            }
            catch (SQLException e) {
                CompactionTxnHandler.LOG.error("Unable to select next element for compaction, " + e.getMessage());
                CompactionTxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "findNextToCompact(workerId:" + workerId + ")");
                throw new MetaException("Unable to connect to transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
                this.closeStmt(stmt);
            }
        }
        catch (RetryException e2) {
            return this.findNextToCompact(workerId);
        }
    }
    
    public void markCompacted(final CompactionInfo info) throws MetaException {
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(8);
                stmt = dbConn.createStatement();
                final String s = "update COMPACTION_QUEUE set cq_state = 'r', cq_worker_id = null where cq_id = " + info.id;
                CompactionTxnHandler.LOG.debug("Going to execute update <" + s + ">");
                if (stmt.executeUpdate(s) != 1) {
                    CompactionTxnHandler.LOG.error("Unable to update compaction record");
                    CompactionTxnHandler.LOG.debug("Going to rollback");
                    dbConn.rollback();
                }
                CompactionTxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
            }
            catch (SQLException e) {
                CompactionTxnHandler.LOG.error("Unable to update compaction queue " + e.getMessage());
                CompactionTxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "markCompacted(" + info + ")");
                throw new MetaException("Unable to connect to transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
                this.closeStmt(stmt);
            }
        }
        catch (RetryException e2) {
            this.markCompacted(info);
        }
    }
    
    public List<CompactionInfo> findReadyToClean() throws MetaException {
        Connection dbConn = null;
        final List<CompactionInfo> rc = new ArrayList<CompactionInfo>();
        Statement stmt = null;
        try {
            try {
                dbConn = this.getDbConn(2);
                stmt = dbConn.createStatement();
                final String s = "select cq_id, cq_database, cq_table, cq_partition, cq_type, cq_run_as from COMPACTION_QUEUE where cq_state = 'r'";
                CompactionTxnHandler.LOG.debug("Going to execute query <" + s + ">");
                final ResultSet rs = stmt.executeQuery(s);
                while (rs.next()) {
                    final CompactionInfo info = new CompactionInfo();
                    info.id = rs.getLong(1);
                    info.dbname = rs.getString(2);
                    info.tableName = rs.getString(3);
                    info.partName = rs.getString(4);
                    switch (rs.getString(5).charAt(0)) {
                        case 'a': {
                            info.type = CompactionType.MAJOR;
                            break;
                        }
                        case 'i': {
                            info.type = CompactionType.MINOR;
                            break;
                        }
                        default: {
                            throw new MetaException("Unexpected compaction type " + rs.getString(5));
                        }
                    }
                    info.runAs = rs.getString(6);
                    rc.add(info);
                }
                CompactionTxnHandler.LOG.debug("Going to rollback");
                dbConn.rollback();
                return rc;
            }
            catch (SQLException e) {
                CompactionTxnHandler.LOG.error("Unable to select next element for cleaning, " + e.getMessage());
                CompactionTxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "findReadyToClean");
                throw new MetaException("Unable to connect to transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
                this.closeStmt(stmt);
            }
        }
        catch (RetryException e2) {
            return this.findReadyToClean();
        }
    }
    
    public void markCleaned(final CompactionInfo info) throws MetaException {
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(8);
                stmt = dbConn.createStatement();
                String s = "delete from COMPACTION_QUEUE where cq_id = " + info.id;
                CompactionTxnHandler.LOG.debug("Going to execute update <" + s + ">");
                if (stmt.executeUpdate(s) != 1) {
                    CompactionTxnHandler.LOG.error("Unable to delete compaction record");
                    CompactionTxnHandler.LOG.debug("Going to rollback");
                    dbConn.rollback();
                }
                s = "delete from COMPLETED_TXN_COMPONENTS where ctc_database = '" + info.dbname + "' and " + "ctc_table = '" + info.tableName + "'";
                if (info.partName != null) {
                    s = s + " and ctc_partition = '" + info.partName + "'";
                }
                CompactionTxnHandler.LOG.debug("Going to execute update <" + s + ">");
                if (stmt.executeUpdate(s) < 1) {
                    CompactionTxnHandler.LOG.error("Expected to remove at least one row from completed_txn_components when marking compaction entry as clean!");
                }
                s = "select txn_id from TXNS, TXN_COMPONENTS where txn_id = tc_txnid and txn_state = 'a' and tc_database = '" + info.dbname + "' and tc_table = '" + info.tableName + "'";
                if (info.partName != null) {
                    s = s + " and tc_partition = '" + info.partName + "'";
                }
                CompactionTxnHandler.LOG.debug("Going to execute update <" + s + ">");
                final ResultSet rs = stmt.executeQuery(s);
                final Set<Long> txnids = new HashSet<Long>();
                while (rs.next()) {
                    txnids.add(rs.getLong(1));
                }
                if (txnids.size() > 0) {
                    final StringBuffer buf = new StringBuffer();
                    buf.append("delete from TXN_COMPONENTS where tc_txnid in (");
                    boolean first = true;
                    for (final long id : txnids) {
                        if (first) {
                            first = false;
                        }
                        else {
                            buf.append(", ");
                        }
                        buf.append(id);
                    }
                    buf.append(") and tc_database = '");
                    buf.append(info.dbname);
                    buf.append("' and tc_table = '");
                    buf.append(info.tableName);
                    buf.append("'");
                    if (info.partName != null) {
                        buf.append(" and tc_partition = '");
                        buf.append(info.partName);
                        buf.append("'");
                    }
                    CompactionTxnHandler.LOG.debug("Going to execute update <" + buf.toString() + ">");
                    final int rc = stmt.executeUpdate(buf.toString());
                    CompactionTxnHandler.LOG.debug("Removed " + rc + " records from txn_components");
                }
                CompactionTxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
            }
            catch (SQLException e) {
                CompactionTxnHandler.LOG.error("Unable to delete from compaction queue " + e.getMessage());
                CompactionTxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "markCleaned(" + info + ")");
                throw new MetaException("Unable to connect to transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
                this.closeStmt(stmt);
            }
        }
        catch (RetryException e2) {
            this.markCleaned(info);
        }
    }
    
    public void cleanEmptyAbortedTxns() throws MetaException {
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(8);
                stmt = dbConn.createStatement();
                final String s = "select txn_id from TXNS where txn_id not in (select tc_txnid from TXN_COMPONENTS) and txn_state = 'a'";
                CompactionTxnHandler.LOG.debug("Going to execute query <" + s + ">");
                final ResultSet rs = stmt.executeQuery(s);
                final Set<Long> txnids = new HashSet<Long>();
                while (rs.next()) {
                    txnids.add(rs.getLong(1));
                }
                if (txnids.size() > 0) {
                    final StringBuffer buf = new StringBuffer("delete from TXNS where txn_id in (");
                    boolean first = true;
                    for (final long tid : txnids) {
                        if (first) {
                            first = false;
                        }
                        else {
                            buf.append(", ");
                        }
                        buf.append(tid);
                    }
                    buf.append(")");
                    CompactionTxnHandler.LOG.debug("Going to execute update <" + buf.toString() + ">");
                    final int rc = stmt.executeUpdate(buf.toString());
                    CompactionTxnHandler.LOG.debug("Removed " + rc + " records from txns");
                    CompactionTxnHandler.LOG.debug("Going to commit");
                    dbConn.commit();
                }
            }
            catch (SQLException e) {
                CompactionTxnHandler.LOG.error("Unable to delete from txns table " + e.getMessage());
                CompactionTxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "cleanEmptyAbortedTxns");
                throw new MetaException("Unable to connect to transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
                this.closeStmt(stmt);
            }
        }
        catch (RetryException e2) {
            this.cleanEmptyAbortedTxns();
        }
    }
    
    public void revokeFromLocalWorkers(final String hostname) throws MetaException {
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(8);
                stmt = dbConn.createStatement();
                final String s = "update COMPACTION_QUEUE set cq_worker_id = null, cq_start = null, cq_state = 'i' where cq_state = 'w' and cq_worker_id like '" + hostname + "%'";
                CompactionTxnHandler.LOG.debug("Going to execute update <" + s + ">");
                stmt.executeUpdate(s);
                CompactionTxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
            }
            catch (SQLException e) {
                CompactionTxnHandler.LOG.error("Unable to change dead worker's records back to initiated state " + e.getMessage());
                CompactionTxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "revokeFromLocalWorkers(hostname:" + hostname + ")");
                throw new MetaException("Unable to connect to transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
                this.closeStmt(stmt);
            }
        }
        catch (RetryException e2) {
            this.revokeFromLocalWorkers(hostname);
        }
    }
    
    public void revokeTimedoutWorkers(final long timeout) throws MetaException {
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(8);
                final long latestValidStart = this.getDbTime(dbConn) - timeout;
                stmt = dbConn.createStatement();
                final String s = "update COMPACTION_QUEUE set cq_worker_id = null, cq_start = null, cq_state = 'i' where cq_state = 'w' and cq_start < " + latestValidStart;
                CompactionTxnHandler.LOG.debug("Going to execute update <" + s + ">");
                stmt.executeUpdate(s);
                CompactionTxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
            }
            catch (SQLException e) {
                CompactionTxnHandler.LOG.error("Unable to change dead worker's records back to initiated state " + e.getMessage());
                CompactionTxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "revokeTimedoutWorkers(timeout:" + timeout + ")");
                throw new MetaException("Unable to connect to transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
                this.closeStmt(stmt);
            }
        }
        catch (RetryException e2) {
            this.revokeTimedoutWorkers(timeout);
        }
    }
    
    public List<String> findColumnsWithStats(final CompactionInfo ci) throws MetaException {
        Connection dbConn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            try {
                dbConn = this.getDbConn(2);
                final String quote = this.getIdentifierQuoteString(dbConn);
                stmt = dbConn.createStatement();
                final StringBuilder bldr = new StringBuilder();
                bldr.append("SELECT ").append(quote).append("COLUMN_NAME").append(quote).append(" FROM ").append(quote).append((ci.partName == null) ? "TAB_COL_STATS" : "PART_COL_STATS").append(quote).append(" WHERE ").append(quote).append("DB_NAME").append(quote).append(" = '").append(ci.dbname).append("' AND ").append(quote).append("TABLE_NAME").append(quote).append(" = '").append(ci.tableName).append("'");
                if (ci.partName != null) {
                    bldr.append(" AND ").append(quote).append("PARTITION_NAME").append(quote).append(" = '").append(ci.partName).append("'");
                }
                final String s = bldr.toString();
                CompactionTxnHandler.LOG.debug("Going to execute <" + s + ">");
                rs = stmt.executeQuery(s);
                final List<String> columns = new ArrayList<String>();
                while (rs.next()) {
                    columns.add(rs.getString(1));
                }
                CompactionTxnHandler.LOG.debug("Found columns to update stats: " + columns + " on " + ci.tableName + ((ci.partName == null) ? "" : ("/" + ci.partName)));
                dbConn.commit();
                return columns;
            }
            catch (SQLException e) {
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "findColumnsWithStats(" + ci.tableName + ((ci.partName == null) ? "" : ("/" + ci.partName)) + ")");
                throw new MetaException("Unable to connect to transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.close(rs, stmt, dbConn);
            }
        }
        catch (RetryException ex) {
            return this.findColumnsWithStats(ci);
        }
    }
    
    public static ValidTxnList createValidCompactTxnList(final GetOpenTxnsInfoResponse txns) {
        final long highWater = txns.getTxn_high_water_mark();
        long minOpenTxn = Long.MAX_VALUE;
        final long[] exceptions = new long[txns.getOpen_txnsSize()];
        int i = 0;
        for (final TxnInfo txn : txns.getOpen_txns()) {
            if (txn.getState() == TxnState.OPEN) {
                minOpenTxn = Math.min(minOpenTxn, txn.getId());
            }
            exceptions[i++] = txn.getId();
        }
        return new ValidCompactorTxnList(exceptions, minOpenTxn, highWater);
    }
    
    static {
        CLASS_NAME = CompactionTxnHandler.class.getName();
        LOG = LogFactory.getLog(CompactionTxnHandler.CLASS_NAME);
    }
}
