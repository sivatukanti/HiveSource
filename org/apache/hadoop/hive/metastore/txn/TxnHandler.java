// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.txn;

import org.apache.hadoop.hive.common.JavaUtils;
import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import com.jolbox.bonecp.BoneCPDataSource;
import com.jolbox.bonecp.BoneCPConfig;
import java.io.IOException;
import org.apache.hadoop.hive.shims.ShimLoader;
import java.util.SortedSet;
import java.sql.Savepoint;
import java.util.TreeSet;
import org.apache.hadoop.hive.metastore.api.LockComponent;
import java.sql.SQLTransactionRollbackException;
import org.apache.hadoop.hive.metastore.api.AddDynamicPartitions;
import org.apache.hadoop.hive.metastore.api.CompactionType;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponseElement;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponse;
import org.apache.hadoop.hive.metastore.api.ShowCompactRequest;
import org.apache.hadoop.hive.metastore.api.CompactionRequest;
import org.apache.hadoop.hive.metastore.api.HeartbeatTxnRangeResponse;
import org.apache.hadoop.hive.metastore.api.HeartbeatTxnRangeRequest;
import org.apache.hadoop.hive.metastore.api.HeartbeatRequest;
import java.util.Comparator;
import org.apache.hadoop.hive.metastore.api.ShowLocksResponseElement;
import org.apache.hadoop.hive.metastore.api.ShowLocksResponse;
import org.apache.hadoop.hive.metastore.api.ShowLocksRequest;
import org.apache.hadoop.hive.metastore.api.TxnOpenException;
import org.apache.hadoop.hive.metastore.api.UnlockRequest;
import org.apache.hadoop.hive.metastore.api.NoSuchLockException;
import org.apache.hadoop.hive.metastore.api.CheckLockRequest;
import org.apache.hadoop.hive.metastore.api.LockResponse;
import org.apache.hadoop.hive.metastore.api.LockRequest;
import org.apache.hadoop.hive.metastore.api.TxnAbortedException;
import org.apache.hadoop.hive.metastore.api.CommitTxnRequest;
import org.apache.hadoop.hive.metastore.api.NoSuchTxnException;
import java.util.Collections;
import org.apache.hadoop.hive.metastore.api.AbortTxnRequest;
import java.sql.PreparedStatement;
import org.apache.hadoop.hive.metastore.api.OpenTxnsResponse;
import org.apache.hadoop.hive.metastore.api.OpenTxnRequest;
import java.util.Iterator;
import org.apache.hadoop.hive.common.ValidReadTxnList;
import org.apache.hadoop.hive.common.ValidTxnList;
import java.util.Set;
import java.util.HashSet;
import org.apache.hadoop.hive.metastore.api.GetOpenTxnsResponse;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.hive.metastore.api.TxnState;
import org.apache.hadoop.hive.metastore.api.TxnInfo;
import java.util.ArrayList;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.GetOpenTxnsInfoResponse;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.TimeUnit;
import java.sql.SQLException;
import org.apache.hadoop.hive.metastore.api.LockState;
import org.apache.hadoop.hive.metastore.api.LockType;
import java.util.Map;
import org.apache.hadoop.hive.conf.HiveConf;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;

public class TxnHandler
{
    public static final String INITIATED_RESPONSE = "initiated";
    public static final String WORKING_RESPONSE = "working";
    public static final String CLEANING_RESPONSE = "ready for cleaning";
    protected static final char INITIATED_STATE = 'i';
    protected static final char WORKING_STATE = 'w';
    protected static final char READY_FOR_CLEANING = 'r';
    protected static final char MAJOR_TYPE = 'a';
    protected static final char MINOR_TYPE = 'i';
    protected static final char TXN_ABORTED = 'a';
    protected static final char TXN_OPEN = 'o';
    protected static final char LOCK_ACQUIRED = 'a';
    protected static final char LOCK_WAITING = 'w';
    protected static final char LOCK_EXCLUSIVE = 'e';
    protected static final char LOCK_SHARED = 'r';
    protected static final char LOCK_SEMI_SHARED = 'w';
    private static final int ALLOWED_REPEATED_DEADLOCKS = 10;
    private static final int TIMED_OUT_TXN_ABORT_BATCH_SIZE = 100;
    private static final Log LOG;
    private static DataSource connPool;
    private static final Object lockLock;
    protected int deadlockCnt;
    private long deadlockRetryInterval;
    protected HiveConf conf;
    protected DatabaseProduct dbProduct;
    private long timeout;
    private String identifierQuoteString;
    private final long retryInterval;
    private final int retryLimit;
    private int retryNum;
    private static Map<LockType, Map<LockType, Map<LockState, LockAction>>> jumpTable;
    
    public TxnHandler(final HiveConf conf) {
        this.conf = conf;
        this.checkQFileTestHack();
        try {
            setupJdbcConnectionPool(conf);
        }
        catch (SQLException e) {
            final String msg = "Unable to instantiate JDBC connection pooling, " + e.getMessage();
            TxnHandler.LOG.error(msg);
            throw new RuntimeException(e);
        }
        this.timeout = HiveConf.getTimeVar(conf, HiveConf.ConfVars.HIVE_TXN_TIMEOUT, TimeUnit.MILLISECONDS);
        this.deadlockCnt = 0;
        buildJumpTable();
        this.retryInterval = HiveConf.getTimeVar(conf, HiveConf.ConfVars.HMSHANDLERINTERVAL, TimeUnit.MILLISECONDS);
        this.retryLimit = HiveConf.getIntVar(conf, HiveConf.ConfVars.HMSHANDLERATTEMPTS);
        this.deadlockRetryInterval = this.retryInterval / 10L;
    }
    
    public GetOpenTxnsInfoResponse getOpenTxnsInfo() throws MetaException {
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(2);
                stmt = dbConn.createStatement();
                String s = "select ntxn_next - 1 from NEXT_TXN_ID";
                TxnHandler.LOG.debug("Going to execute query <" + s + ">");
                ResultSet rs = stmt.executeQuery(s);
                if (!rs.next()) {
                    throw new MetaException("Transaction tables not properly initialized, no record found in next_txn_id");
                }
                final long hwm = rs.getLong(1);
                if (rs.wasNull()) {
                    throw new MetaException("Transaction tables not properly initialized, null record found in next_txn_id");
                }
                final List<TxnInfo> txnInfo = new ArrayList<TxnInfo>();
                s = "select txn_id, txn_state, txn_user, txn_host from TXNS";
                TxnHandler.LOG.debug("Going to execute query<" + s + ">");
                rs = stmt.executeQuery(s);
                while (rs.next()) {
                    final char c = rs.getString(2).charAt(0);
                    TxnState state = null;
                    switch (c) {
                        case 'a': {
                            state = TxnState.ABORTED;
                            break;
                        }
                        case 'o': {
                            state = TxnState.OPEN;
                            break;
                        }
                        default: {
                            throw new MetaException("Unexpected transaction state " + c + " found in txns table");
                        }
                    }
                    txnInfo.add(new TxnInfo(rs.getLong(1), state, rs.getString(3), rs.getString(4)));
                }
                TxnHandler.LOG.debug("Going to rollback");
                dbConn.rollback();
                return new GetOpenTxnsInfoResponse(hwm, txnInfo);
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "getOpenTxnsInfo");
                throw new MetaException("Unable to select from transaction database: " + getMessage(e) + StringUtils.stringifyException(e));
            }
            finally {
                this.closeStmt(stmt);
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            return this.getOpenTxnsInfo();
        }
    }
    
    public GetOpenTxnsResponse getOpenTxns() throws MetaException {
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(2);
                this.timeOutTxns(dbConn);
                stmt = dbConn.createStatement();
                String s = "select ntxn_next - 1 from NEXT_TXN_ID";
                TxnHandler.LOG.debug("Going to execute query <" + s + ">");
                ResultSet rs = stmt.executeQuery(s);
                if (!rs.next()) {
                    throw new MetaException("Transaction tables not properly initialized, no record found in next_txn_id");
                }
                final long hwm = rs.getLong(1);
                if (rs.wasNull()) {
                    throw new MetaException("Transaction tables not properly initialized, null record found in next_txn_id");
                }
                final Set<Long> openList = new HashSet<Long>();
                s = "select txn_id from TXNS";
                TxnHandler.LOG.debug("Going to execute query<" + s + ">");
                rs = stmt.executeQuery(s);
                while (rs.next()) {
                    openList.add(rs.getLong(1));
                }
                TxnHandler.LOG.debug("Going to rollback");
                dbConn.rollback();
                return new GetOpenTxnsResponse(hwm, openList);
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "getOpenTxns");
                throw new MetaException("Unable to select from transaction database, " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeStmt(stmt);
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            return this.getOpenTxns();
        }
    }
    
    public static ValidTxnList createValidReadTxnList(final GetOpenTxnsResponse txns, final long currentTxn) {
        final long highWater = txns.getTxn_high_water_mark();
        final Set<Long> open = txns.getOpen_txns();
        final long[] exceptions = new long[open.size() - ((currentTxn > 0L) ? 1 : 0)];
        int i = 0;
        for (final long txn : open) {
            if (currentTxn > 0L && currentTxn == txn) {
                continue;
            }
            exceptions[i++] = txn;
        }
        return new ValidReadTxnList(exceptions, highWater);
    }
    
    public OpenTxnsResponse openTxns(final OpenTxnRequest rqst) throws MetaException {
        this.deadlockCnt = 0;
        int numTxns = rqst.getNum_txns();
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(8);
                final int maxTxns = HiveConf.getIntVar(this.conf, HiveConf.ConfVars.HIVE_TXN_MAX_OPEN_BATCH);
                if (numTxns > maxTxns) {
                    numTxns = maxTxns;
                }
                stmt = dbConn.createStatement();
                String s = "select ntxn_next from NEXT_TXN_ID";
                TxnHandler.LOG.debug("Going to execute query <" + s + ">");
                final ResultSet rs = stmt.executeQuery(s);
                if (!rs.next()) {
                    throw new MetaException("Transaction database not properly configured, can't find next transaction id.");
                }
                final long first = rs.getLong(1);
                s = "update NEXT_TXN_ID set ntxn_next = " + (first + numTxns);
                TxnHandler.LOG.debug("Going to execute update <" + s + ">");
                stmt.executeUpdate(s);
                final long now = this.getDbTime(dbConn);
                s = "insert into TXNS (txn_id, txn_state, txn_started, txn_last_heartbeat, txn_user, txn_host) values (?, 'o', " + now + ", " + now + ", '" + rqst.getUser() + "', '" + rqst.getHostname() + "')";
                TxnHandler.LOG.debug("Going to prepare statement <" + s + ">");
                final PreparedStatement ps = dbConn.prepareStatement(s);
                final List<Long> txnIds = new ArrayList<Long>(numTxns);
                for (long i = first; i < first + numTxns; ++i) {
                    ps.setLong(1, i);
                    ps.executeUpdate();
                    txnIds.add(i);
                }
                TxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
                return new OpenTxnsResponse(txnIds);
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "openTxns(" + rqst + ")");
                throw new MetaException("Unable to select from transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeStmt(stmt);
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            return this.openTxns(rqst);
        }
    }
    
    public void abortTxn(final AbortTxnRequest rqst) throws NoSuchTxnException, MetaException {
        final long txnid = rqst.getTxnid();
        try {
            Connection dbConn = null;
            try {
                dbConn = this.getDbConn(8);
                if (this.abortTxns(dbConn, Collections.singletonList(txnid)) != 1) {
                    TxnHandler.LOG.debug("Going to rollback");
                    dbConn.rollback();
                    throw new NoSuchTxnException("No such transaction: " + txnid);
                }
                TxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "abortTxn(" + rqst + ")");
                throw new MetaException("Unable to update transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            this.abortTxn(rqst);
        }
    }
    
    public void commitTxn(final CommitTxnRequest rqst) throws NoSuchTxnException, TxnAbortedException, MetaException {
        final long txnid = rqst.getTxnid();
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(8);
                stmt = dbConn.createStatement();
                this.heartbeatTxn(dbConn, txnid);
                String s = "insert into COMPLETED_TXN_COMPONENTS select tc_txnid, tc_database, tc_table, tc_partition from TXN_COMPONENTS where tc_txnid = " + txnid;
                TxnHandler.LOG.debug("Going to execute insert <" + s + ">");
                if (stmt.executeUpdate(s) < 1) {
                    TxnHandler.LOG.warn("Expected to move at least one record from txn_components to completed_txn_components when committing txn!");
                }
                s = "delete from TXN_COMPONENTS where tc_txnid = " + txnid;
                TxnHandler.LOG.debug("Going to execute update <" + s + ">");
                stmt.executeUpdate(s);
                s = "delete from HIVE_LOCKS where hl_txnid = " + txnid;
                TxnHandler.LOG.debug("Going to execute update <" + s + ">");
                stmt.executeUpdate(s);
                s = "delete from TXNS where txn_id = " + txnid;
                TxnHandler.LOG.debug("Going to execute update <" + s + ">");
                stmt.executeUpdate(s);
                TxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "commitTxn(" + rqst + ")");
                throw new MetaException("Unable to update transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeStmt(stmt);
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            this.commitTxn(rqst);
        }
    }
    
    public LockResponse lock(final LockRequest rqst) throws NoSuchTxnException, TxnAbortedException, MetaException {
        this.deadlockCnt = 0;
        try {
            Connection dbConn = null;
            try {
                dbConn = this.getDbConn(8);
                return this.lock(dbConn, rqst, true);
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "lock(" + rqst + ")");
                throw new MetaException("Unable to update transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            return this.lock(rqst);
        }
    }
    
    public LockResponse lockNoWait(final LockRequest rqst) throws NoSuchTxnException, TxnAbortedException, MetaException {
        try {
            Connection dbConn = null;
            try {
                dbConn = this.getDbConn(8);
                return this.lock(dbConn, rqst, false);
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "lockNoWait(" + rqst + ")");
                throw new MetaException("Unable to update transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            return this.lockNoWait(rqst);
        }
    }
    
    public LockResponse checkLock(final CheckLockRequest rqst) throws NoSuchTxnException, NoSuchLockException, TxnAbortedException, MetaException {
        try {
            Connection dbConn = null;
            try {
                dbConn = this.getDbConn(8);
                final long extLockId = rqst.getLockid();
                this.timeOutLocks(dbConn);
                this.heartbeatLock(dbConn, extLockId);
                final long txnid = this.getTxnIdFromLockId(dbConn, extLockId);
                if (txnid > 0L) {
                    this.heartbeatTxn(dbConn, txnid);
                }
                return this.checkLock(dbConn, extLockId, true);
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "checkLock(" + rqst + " )");
                throw new MetaException("Unable to update transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            return this.checkLock(rqst);
        }
    }
    
    public void unlock(final UnlockRequest rqst) throws NoSuchLockException, TxnOpenException, MetaException {
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(8);
                final long extLockId = rqst.getLockid();
                this.heartbeatLock(dbConn, extLockId);
                final long txnid = this.getTxnIdFromLockId(dbConn, extLockId);
                if (txnid > 0L) {
                    TxnHandler.LOG.debug("Going to rollback");
                    dbConn.rollback();
                    final String msg = "Unlocking locks associated with transaction not permitted.  Lockid " + extLockId + " is associated with " + "transaction " + txnid;
                    TxnHandler.LOG.error(msg);
                    throw new TxnOpenException(msg);
                }
                stmt = dbConn.createStatement();
                final String s = "delete from HIVE_LOCKS where hl_lock_ext_id = " + extLockId;
                TxnHandler.LOG.debug("Going to execute update <" + s + ">");
                final int rc = stmt.executeUpdate(s);
                if (rc < 1) {
                    TxnHandler.LOG.debug("Going to rollback");
                    dbConn.rollback();
                    throw new NoSuchLockException("No such lock: " + extLockId);
                }
                TxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "unlock(" + rqst + ")");
                throw new MetaException("Unable to update transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeStmt(stmt);
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            this.unlock(rqst);
        }
    }
    
    public ShowLocksResponse showLocks(final ShowLocksRequest rqst) throws MetaException {
        try {
            Connection dbConn = null;
            final ShowLocksResponse rsp = new ShowLocksResponse();
            final List<ShowLocksResponseElement> elems = new ArrayList<ShowLocksResponseElement>();
            final List<LockInfoExt> sortedList = new ArrayList<LockInfoExt>();
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(2);
                stmt = dbConn.createStatement();
                final String s = "select hl_lock_ext_id, hl_txnid, hl_db, hl_table, hl_partition, hl_lock_state, hl_lock_type, hl_last_heartbeat, hl_acquired_at, hl_user, hl_host, hl_lock_int_id from HIVE_LOCKS";
                TxnHandler.LOG.debug("Doing to execute query <" + s + ">");
                final ResultSet rs = stmt.executeQuery(s);
                while (rs.next()) {
                    final ShowLocksResponseElement e = new ShowLocksResponseElement();
                    e.setLockid(rs.getLong(1));
                    final long txnid = rs.getLong(2);
                    if (!rs.wasNull()) {
                        e.setTxnid(txnid);
                    }
                    e.setDbname(rs.getString(3));
                    e.setTablename(rs.getString(4));
                    final String partition = rs.getString(5);
                    if (partition != null) {
                        e.setPartname(partition);
                    }
                    switch (rs.getString(6).charAt(0)) {
                        case 'a': {
                            e.setState(LockState.ACQUIRED);
                            break;
                        }
                        case 'w': {
                            e.setState(LockState.WAITING);
                            break;
                        }
                        default: {
                            throw new MetaException("Unknown lock state " + rs.getString(6).charAt(0));
                        }
                    }
                    switch (rs.getString(7).charAt(0)) {
                        case 'w': {
                            e.setType(LockType.SHARED_WRITE);
                            break;
                        }
                        case 'e': {
                            e.setType(LockType.EXCLUSIVE);
                            break;
                        }
                        case 'r': {
                            e.setType(LockType.SHARED_READ);
                            break;
                        }
                        default: {
                            throw new MetaException("Unknown lock type " + rs.getString(6).charAt(0));
                        }
                    }
                    e.setLastheartbeat(rs.getLong(8));
                    final long acquiredAt = rs.getLong(9);
                    if (!rs.wasNull()) {
                        e.setAcquiredat(acquiredAt);
                    }
                    e.setUser(rs.getString(10));
                    e.setHostname(rs.getString(11));
                    sortedList.add(new LockInfoExt(e, rs.getLong(12)));
                }
                TxnHandler.LOG.debug("Going to rollback");
                dbConn.rollback();
            }
            catch (SQLException e2) {
                this.checkRetryable(dbConn, e2, "showLocks(" + rqst + ")");
                throw new MetaException("Unable to select from transaction database " + StringUtils.stringifyException(e2));
            }
            finally {
                this.closeStmt(stmt);
                this.closeDbConn(dbConn);
            }
            Collections.sort(sortedList, new LockInfoComparator());
            for (final LockInfoExt lockInfoExt : sortedList) {
                elems.add(lockInfoExt.e);
            }
            rsp.setLocks(elems);
            return rsp;
        }
        catch (RetryException e3) {
            return this.showLocks(rqst);
        }
    }
    
    public void heartbeat(final HeartbeatRequest ids) throws NoSuchTxnException, NoSuchLockException, TxnAbortedException, MetaException {
        try {
            Connection dbConn = null;
            try {
                dbConn = this.getDbConn(8);
                this.heartbeatLock(dbConn, ids.getLockid());
                this.heartbeatTxn(dbConn, ids.getTxnid());
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "heartbeat(" + ids + ")");
                throw new MetaException("Unable to select from transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            this.heartbeat(ids);
        }
        finally {
            this.deadlockCnt = 0;
        }
    }
    
    public HeartbeatTxnRangeResponse heartbeatTxnRange(final HeartbeatTxnRangeRequest rqst) throws MetaException {
        try {
            Connection dbConn = null;
            final HeartbeatTxnRangeResponse rsp = new HeartbeatTxnRangeResponse();
            final Set<Long> nosuch = new HashSet<Long>();
            final Set<Long> aborted = new HashSet<Long>();
            rsp.setNosuch(nosuch);
            rsp.setAborted(aborted);
            try {
                dbConn = this.getDbConn(8);
                for (long txn = rqst.getMin(); txn <= rqst.getMax(); ++txn) {
                    try {
                        this.heartbeatTxn(dbConn, txn);
                    }
                    catch (NoSuchTxnException e2) {
                        nosuch.add(txn);
                    }
                    catch (TxnAbortedException e3) {
                        aborted.add(txn);
                    }
                }
                return rsp;
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "heartbeatTxnRange(" + rqst + ")");
                throw new MetaException("Unable to select from transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e4) {
            return this.heartbeatTxnRange(rqst);
        }
    }
    
    public void compact(final CompactionRequest rqst) throws MetaException {
        try {
            Connection dbConn = null;
            Statement stmt = null;
            try {
                dbConn = this.getDbConn(8);
                stmt = dbConn.createStatement();
                String s = "select ncq_next from NEXT_COMPACTION_QUEUE_ID";
                TxnHandler.LOG.debug("going to execute query <" + s + ">");
                final ResultSet rs = stmt.executeQuery(s);
                if (!rs.next()) {
                    TxnHandler.LOG.debug("Going to rollback");
                    dbConn.rollback();
                    throw new MetaException("Transaction tables not properly initiated, no record found in next_compaction_queue_id");
                }
                final long id = rs.getLong(1);
                s = "update NEXT_COMPACTION_QUEUE_ID set ncq_next = " + (id + 1L);
                TxnHandler.LOG.debug("Going to execute update <" + s + ">");
                stmt.executeUpdate(s);
                final StringBuilder buf = new StringBuilder("insert into COMPACTION_QUEUE (cq_id, cq_database, cq_table, ");
                final String partName = rqst.getPartitionname();
                if (partName != null) {
                    buf.append("cq_partition, ");
                }
                buf.append("cq_state, cq_type");
                if (rqst.getRunas() != null) {
                    buf.append(", cq_run_as");
                }
                buf.append(") values (");
                buf.append(id);
                buf.append(", '");
                buf.append(rqst.getDbname());
                buf.append("', '");
                buf.append(rqst.getTablename());
                buf.append("', '");
                if (partName != null) {
                    buf.append(partName);
                    buf.append("', '");
                }
                buf.append('i');
                buf.append("', '");
                switch (rqst.getType()) {
                    case MAJOR: {
                        buf.append('a');
                        break;
                    }
                    case MINOR: {
                        buf.append('i');
                        break;
                    }
                    default: {
                        TxnHandler.LOG.debug("Going to rollback");
                        dbConn.rollback();
                        throw new MetaException("Unexpected compaction type " + rqst.getType().toString());
                    }
                }
                if (rqst.getRunas() != null) {
                    buf.append("', '");
                    buf.append(rqst.getRunas());
                }
                buf.append("')");
                s = buf.toString();
                TxnHandler.LOG.debug("Going to execute update <" + s + ">");
                stmt.executeUpdate(s);
                TxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "compact(" + rqst + ")");
                throw new MetaException("Unable to select from transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeStmt(stmt);
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            this.compact(rqst);
        }
    }
    
    public ShowCompactResponse showCompact(final ShowCompactRequest rqst) throws MetaException {
        final ShowCompactResponse response = new ShowCompactResponse(new ArrayList<ShowCompactResponseElement>());
        Connection dbConn = null;
        Statement stmt = null;
        try {
            try {
                dbConn = this.getDbConn(2);
                stmt = dbConn.createStatement();
                final String s = "select cq_database, cq_table, cq_partition, cq_state, cq_type, cq_worker_id, cq_start, cq_run_as from COMPACTION_QUEUE";
                TxnHandler.LOG.debug("Going to execute query <" + s + ">");
                final ResultSet rs = stmt.executeQuery(s);
                while (rs.next()) {
                    final ShowCompactResponseElement e = new ShowCompactResponseElement();
                    e.setDbname(rs.getString(1));
                    e.setTablename(rs.getString(2));
                    e.setPartitionname(rs.getString(3));
                    switch (rs.getString(4).charAt(0)) {
                        case 'i': {
                            e.setState("initiated");
                            break;
                        }
                        case 'w': {
                            e.setState("working");
                            break;
                        }
                        case 'r': {
                            e.setState("ready for cleaning");
                            break;
                        }
                        default: {
                            throw new MetaException("Unexpected compaction state " + rs.getString(4));
                        }
                    }
                    switch (rs.getString(5).charAt(0)) {
                        case 'a': {
                            e.setType(CompactionType.MAJOR);
                            break;
                        }
                        case 'i': {
                            e.setType(CompactionType.MINOR);
                            break;
                        }
                        default: {
                            throw new MetaException("Unexpected compaction type " + rs.getString(5));
                        }
                    }
                    e.setWorkerid(rs.getString(6));
                    e.setStart(rs.getLong(7));
                    e.setRunAs(rs.getString(8));
                    response.addToCompacts(e);
                }
                TxnHandler.LOG.debug("Going to rollback");
                dbConn.rollback();
            }
            catch (SQLException e2) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e2, "showCompact(" + rqst + ")");
                throw new MetaException("Unable to select from transaction database " + StringUtils.stringifyException(e2));
            }
            finally {
                this.closeStmt(stmt);
                this.closeDbConn(dbConn);
            }
            return response;
        }
        catch (RetryException e3) {
            return this.showCompact(rqst);
        }
    }
    
    public void addDynamicPartitions(final AddDynamicPartitions rqst) throws NoSuchTxnException, TxnAbortedException, MetaException {
        Connection dbConn = null;
        Statement stmt = null;
        try {
            try {
                dbConn = this.getDbConn(2);
                stmt = dbConn.createStatement();
                this.heartbeatTxn(dbConn, rqst.getTxnid());
                for (final String partName : rqst.getPartitionnames()) {
                    final StringBuilder buff = new StringBuilder();
                    buff.append("insert into TXN_COMPONENTS (tc_txnid, tc_database, tc_table, tc_partition) values (");
                    buff.append(rqst.getTxnid());
                    buff.append(", '");
                    buff.append(rqst.getDbname());
                    buff.append("', '");
                    buff.append(rqst.getTablename());
                    buff.append("', '");
                    buff.append(partName);
                    buff.append("')");
                    final String s = buff.toString();
                    TxnHandler.LOG.debug("Going to execute update <" + s + ">");
                    stmt.executeUpdate(s);
                }
                TxnHandler.LOG.debug("Going to commit");
                dbConn.commit();
            }
            catch (SQLException e) {
                TxnHandler.LOG.debug("Going to rollback");
                this.rollbackDBConn(dbConn);
                this.checkRetryable(dbConn, e, "addDynamicPartitions(" + rqst + ")");
                throw new MetaException("Unable to insert into from transaction database " + StringUtils.stringifyException(e));
            }
            finally {
                this.closeStmt(stmt);
                this.closeDbConn(dbConn);
            }
        }
        catch (RetryException e2) {
            this.addDynamicPartitions(rqst);
        }
    }
    
    int numLocksInLockTable() throws SQLException, MetaException {
        final Connection dbConn = this.getDbConn(2);
        Statement stmt = null;
        try {
            stmt = dbConn.createStatement();
            final String s = "select count(*) from HIVE_LOCKS";
            TxnHandler.LOG.debug("Going to execute query <" + s + ">");
            final ResultSet rs = stmt.executeQuery(s);
            rs.next();
            final int rc = rs.getInt(1);
            dbConn.rollback();
            return rc;
        }
        finally {
            this.closeDbConn(dbConn);
            this.closeStmt(stmt);
        }
    }
    
    long setTimeout(final long milliseconds) {
        final long previous_timeout = this.timeout;
        this.timeout = milliseconds;
        return previous_timeout;
    }
    
    protected Connection getDbConn(final int isolationLevel) throws SQLException {
        final Connection dbConn = TxnHandler.connPool.getConnection();
        dbConn.setAutoCommit(false);
        dbConn.setTransactionIsolation(isolationLevel);
        return dbConn;
    }
    
    void rollbackDBConn(final Connection dbConn) {
        try {
            if (dbConn != null) {
                dbConn.rollback();
            }
        }
        catch (SQLException e) {
            TxnHandler.LOG.warn("Failed to rollback db connection " + getMessage(e));
        }
    }
    
    protected void closeDbConn(final Connection dbConn) {
        try {
            if (dbConn != null) {
                dbConn.close();
            }
        }
        catch (SQLException e) {
            TxnHandler.LOG.warn("Failed to close db connection " + getMessage(e));
        }
    }
    
    protected void closeStmt(final Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (SQLException e) {
            TxnHandler.LOG.warn("Failed to close statement " + getMessage(e));
        }
    }
    
    void close(final ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        catch (SQLException ex) {
            TxnHandler.LOG.warn("Failed to close statement " + getMessage(ex));
        }
    }
    
    void close(final ResultSet rs, final Statement stmt, final Connection dbConn) {
        this.close(rs);
        this.closeStmt(stmt);
        this.closeDbConn(dbConn);
    }
    
    protected void checkRetryable(final Connection conn, final SQLException e, final String caller) throws RetryException, MetaException {
        if (this.dbProduct == null && conn != null) {
            this.determineDatabaseProduct(conn);
        }
        if (e instanceof SQLTransactionRollbackException || ((this.dbProduct == DatabaseProduct.MYSQL || this.dbProduct == DatabaseProduct.POSTGRES || this.dbProduct == DatabaseProduct.SQLSERVER) && e.getSQLState().equals("40001")) || (this.dbProduct == DatabaseProduct.POSTGRES && e.getSQLState().equals("40P01")) || (this.dbProduct == DatabaseProduct.ORACLE && (e.getMessage().contains("deadlock detected") || e.getMessage().contains("can't serialize access for this transaction")))) {
            if (this.deadlockCnt++ < 10) {
                final long waitInterval = this.deadlockRetryInterval * this.deadlockCnt;
                TxnHandler.LOG.warn("Deadlock detected in " + caller + ". Will wait " + waitInterval + "ms try again up to " + (10 - this.deadlockCnt + 1) + " times.");
                try {
                    Thread.sleep(waitInterval);
                }
                catch (InterruptedException ex) {}
                throw new RetryException();
            }
            TxnHandler.LOG.error("Too many repeated deadlocks in " + caller + ", giving up.");
            this.deadlockCnt = 0;
        }
        else if (isRetryable(e)) {
            if (this.retryNum++ < this.retryLimit) {
                TxnHandler.LOG.warn("Retryable error detected in " + caller + ".  Will wait " + this.retryInterval + "ms and retry up to " + (this.retryLimit - this.retryNum + 1) + " times.  Error: " + getMessage(e));
                try {
                    Thread.sleep(this.retryInterval);
                }
                catch (InterruptedException ex2) {}
                throw new RetryException();
            }
            TxnHandler.LOG.error("Fatal error. Retry limit (" + this.retryLimit + ") reached. Last error: " + getMessage(e));
            this.retryNum = 0;
        }
        else {
            this.deadlockCnt = 0;
            this.retryNum = 0;
        }
    }
    
    protected long getDbTime(final Connection conn) throws MetaException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            final DatabaseProduct prod = this.determineDatabaseProduct(conn);
            String s = null;
            switch (prod) {
                case DERBY: {
                    s = "values current_timestamp";
                    break;
                }
                case MYSQL:
                case POSTGRES:
                case SQLSERVER: {
                    s = "select current_timestamp";
                    break;
                }
                case ORACLE: {
                    s = "select current_timestamp from dual";
                    break;
                }
                default: {
                    final String msg = "Unknown database product: " + prod.toString();
                    TxnHandler.LOG.error(msg);
                    throw new MetaException(msg);
                }
            }
            TxnHandler.LOG.debug("Going to execute query <" + s + ">");
            final ResultSet rs = stmt.executeQuery(s);
            if (!rs.next()) {
                throw new MetaException("No results from date query");
            }
            return rs.getTimestamp(1).getTime();
        }
        catch (SQLException e) {
            final String msg2 = "Unable to determine current time: " + e.getMessage();
            TxnHandler.LOG.error(msg2);
            throw new MetaException(msg2);
        }
        finally {
            this.closeStmt(stmt);
        }
    }
    
    protected String getIdentifierQuoteString(final Connection conn) throws SQLException {
        if (this.identifierQuoteString == null) {
            this.identifierQuoteString = conn.getMetaData().getIdentifierQuoteString();
        }
        return this.identifierQuoteString;
    }
    
    protected DatabaseProduct determineDatabaseProduct(final Connection conn) throws MetaException {
        if (this.dbProduct == null) {
            try {
                final String s = conn.getMetaData().getDatabaseProductName();
                if (s == null) {
                    final String msg = "getDatabaseProductName returns null, can't determine database product";
                    TxnHandler.LOG.error(msg);
                    throw new MetaException(msg);
                }
                if (s.equals("Apache Derby")) {
                    this.dbProduct = DatabaseProduct.DERBY;
                }
                else if (s.equals("Microsoft SQL Server")) {
                    this.dbProduct = DatabaseProduct.SQLSERVER;
                }
                else if (s.equals("MySQL")) {
                    this.dbProduct = DatabaseProduct.MYSQL;
                }
                else if (s.equals("Oracle")) {
                    this.dbProduct = DatabaseProduct.ORACLE;
                }
                else {
                    if (!s.equals("PostgreSQL")) {
                        final String msg = "Unrecognized database product name <" + s + ">";
                        TxnHandler.LOG.error(msg);
                        throw new MetaException(msg);
                    }
                    this.dbProduct = DatabaseProduct.POSTGRES;
                }
            }
            catch (SQLException e) {
                final String msg = "Unable to get database product name: " + e.getMessage();
                TxnHandler.LOG.error(msg);
                throw new MetaException(msg);
            }
        }
        return this.dbProduct;
    }
    
    private void checkQFileTestHack() {
        final boolean hackOn = HiveConf.getBoolVar(this.conf, HiveConf.ConfVars.HIVE_IN_TEST) || HiveConf.getBoolVar(this.conf, HiveConf.ConfVars.HIVE_IN_TEZ_TEST);
        if (hackOn) {
            TxnHandler.LOG.info("Hacking in canned values for transaction manager");
            TxnDbUtil.setConfValues(this.conf);
            try {
                TxnDbUtil.prepDb();
            }
            catch (Exception e) {
                if (!e.getMessage().contains("already exists")) {
                    throw new RuntimeException("Unable to set up transaction database for testing: " + e.getMessage());
                }
            }
        }
    }
    
    private int abortTxns(final Connection dbConn, final List<Long> txnids) throws SQLException {
        Statement stmt = null;
        int updateCnt = 0;
        try {
            stmt = dbConn.createStatement();
            StringBuilder buf = new StringBuilder("delete from HIVE_LOCKS where hl_txnid in (");
            boolean first = true;
            for (final Long id : txnids) {
                if (first) {
                    first = false;
                }
                else {
                    buf.append(',');
                }
                buf.append(id);
            }
            buf.append(')');
            TxnHandler.LOG.debug("Going to execute update <" + buf.toString() + ">");
            stmt.executeUpdate(buf.toString());
            buf = new StringBuilder("update TXNS set txn_state = 'a' where txn_id in (");
            first = true;
            for (final Long id : txnids) {
                if (first) {
                    first = false;
                }
                else {
                    buf.append(',');
                }
                buf.append(id);
            }
            buf.append(')');
            TxnHandler.LOG.debug("Going to execute update <" + buf.toString() + ">");
            updateCnt = stmt.executeUpdate(buf.toString());
        }
        finally {
            this.closeStmt(stmt);
        }
        return updateCnt;
    }
    
    private LockResponse lock(final Connection dbConn, final LockRequest rqst, final boolean wait) throws NoSuchTxnException, TxnAbortedException, MetaException, SQLException {
        synchronized (TxnHandler.lockLock) {
            this.timeOutLocks(dbConn);
            Statement stmt = null;
            try {
                stmt = dbConn.createStatement();
                String s = "select nl_next from NEXT_LOCK_ID";
                TxnHandler.LOG.debug("Going to execute query <" + s + ">");
                final ResultSet rs = stmt.executeQuery(s);
                if (!rs.next()) {
                    TxnHandler.LOG.debug("Going to rollback");
                    dbConn.rollback();
                    throw new MetaException("Transaction tables not properly initialized, no record found in next_lock_id");
                }
                final long extLockId = rs.getLong(1);
                s = "update NEXT_LOCK_ID set nl_next = " + (extLockId + 1L);
                TxnHandler.LOG.debug("Going to execute update <" + s + ">");
                stmt.executeUpdate(s);
                TxnHandler.LOG.debug("Going to commit.");
                dbConn.commit();
                final long txnid = rqst.getTxnid();
                if (txnid > 0L) {
                    this.heartbeatTxn(dbConn, txnid);
                    for (final LockComponent lc : rqst.getComponent()) {
                        final String dbName = lc.getDbname();
                        final String tblName = lc.getTablename();
                        final String partName = lc.getPartitionname();
                        s = "insert into TXN_COMPONENTS (tc_txnid, tc_database, tc_table, tc_partition) values (" + txnid + ", '" + dbName + "', " + ((tblName == null) ? "null" : ("'" + tblName + "'")) + ", " + ((partName == null) ? "null" : ("'" + partName + "'")) + ")";
                        TxnHandler.LOG.debug("Going to execute update <" + s + ">");
                        stmt.executeUpdate(s);
                    }
                }
                long intLockId = 0L;
                for (final LockComponent lc2 : rqst.getComponent()) {
                    ++intLockId;
                    final String dbName2 = lc2.getDbname();
                    final String tblName2 = lc2.getTablename();
                    final String partName2 = lc2.getPartitionname();
                    final LockType lockType = lc2.getType();
                    char lockChar = 'z';
                    switch (lockType) {
                        case EXCLUSIVE: {
                            lockChar = 'e';
                            break;
                        }
                        case SHARED_READ: {
                            lockChar = 'r';
                            break;
                        }
                        case SHARED_WRITE: {
                            lockChar = 'w';
                            break;
                        }
                    }
                    final long now = this.getDbTime(dbConn);
                    s = "insert into HIVE_LOCKS  (hl_lock_ext_id, hl_lock_int_id, hl_txnid, hl_db, hl_table, hl_partition, hl_lock_state, hl_lock_type, hl_last_heartbeat, hl_user, hl_host) values (" + extLockId + ", " + intLockId + "," + ((txnid >= 0L) ? Long.valueOf(txnid) : "null") + ", '" + dbName2 + "', " + ((tblName2 == null) ? "null" : ("'" + tblName2 + "'")) + ", " + ((partName2 == null) ? "null" : ("'" + partName2 + "'")) + ", '" + 'w' + "', " + "'" + lockChar + "', " + now + ", '" + rqst.getUser() + "', '" + rqst.getHostname() + "')";
                    TxnHandler.LOG.debug("Going to execute update <" + s + ">");
                    stmt.executeUpdate(s);
                }
                LockResponse rsp = this.checkLock(dbConn, extLockId, wait);
                if (!wait && rsp.getState() != LockState.ACQUIRED) {
                    TxnHandler.LOG.debug("Lock not acquired, going to rollback");
                    dbConn.rollback();
                    rsp = new LockResponse();
                    rsp.setState(LockState.NOT_ACQUIRED);
                }
                return rsp;
            }
            catch (NoSuchLockException e) {
                throw new MetaException("Couldn't find a lock we just created!");
            }
            finally {
                this.closeStmt(stmt);
            }
        }
    }
    
    private LockResponse checkLock(final Connection dbConn, final long extLockId, final boolean alwaysCommit) throws NoSuchLockException, NoSuchTxnException, TxnAbortedException, MetaException, SQLException {
        final List<LockInfo> locksBeingChecked = this.getLockInfoFromLockId(dbConn, extLockId);
        final LockResponse response = new LockResponse();
        response.setLockid(extLockId);
        TxnHandler.LOG.debug("checkLock(): Setting savepoint. extLockId=" + extLockId);
        final Savepoint save = dbConn.setSavepoint();
        final StringBuilder query = new StringBuilder("select hl_lock_ext_id, hl_lock_int_id, hl_db, hl_table, hl_partition, hl_lock_state, hl_lock_type, hl_txnid from HIVE_LOCKS where hl_db in (");
        final Set<String> strings = new HashSet<String>(locksBeingChecked.size());
        for (final LockInfo info : locksBeingChecked) {
            strings.add(info.db);
        }
        boolean first = true;
        for (final String s : strings) {
            if (first) {
                first = false;
            }
            else {
                query.append(", ");
            }
            query.append('\'');
            query.append(s);
            query.append('\'');
        }
        query.append(")");
        boolean sawNull = false;
        strings.clear();
        for (final LockInfo info2 : locksBeingChecked) {
            if (info2.table == null) {
                sawNull = true;
                break;
            }
            strings.add(info2.table);
        }
        if (!sawNull) {
            query.append(" and (hl_table is null or hl_table in(");
            first = true;
            for (final String s2 : strings) {
                if (first) {
                    first = false;
                }
                else {
                    query.append(", ");
                }
                query.append('\'');
                query.append(s2);
                query.append('\'');
            }
            query.append("))");
            sawNull = false;
            strings.clear();
            for (final LockInfo info2 : locksBeingChecked) {
                if (info2.partition == null) {
                    sawNull = true;
                    break;
                }
                strings.add(info2.partition);
            }
            if (!sawNull) {
                query.append(" and (hl_partition is null or hl_partition in(");
                first = true;
                for (final String s2 : strings) {
                    if (first) {
                        first = false;
                    }
                    else {
                        query.append(", ");
                    }
                    query.append('\'');
                    query.append(s2);
                    query.append('\'');
                }
                query.append("))");
            }
        }
        query.append(" and hl_lock_ext_id <= ").append(extLockId);
        TxnHandler.LOG.debug("Going to execute query <" + query.toString() + ">");
        Statement stmt = null;
        try {
            stmt = dbConn.createStatement();
            final ResultSet rs = stmt.executeQuery(query.toString());
            final SortedSet<LockInfo> lockSet = new TreeSet<LockInfo>(new LockInfoComparator());
            while (rs.next()) {
                lockSet.add(new LockInfo(rs));
            }
            final LockInfo[] locks = lockSet.toArray(new LockInfo[lockSet.size()]);
            if (TxnHandler.LOG.isDebugEnabled()) {
                TxnHandler.LOG.debug("Locks to check(full): ");
                for (final LockInfo info3 : locks) {
                    TxnHandler.LOG.debug("  " + info3);
                }
            }
            for (final LockInfo info4 : locksBeingChecked) {
                int index = -1;
                for (int i = 0; i < locks.length; ++i) {
                    if (locks[i].equals(info4)) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    TxnHandler.LOG.debug("Going to rollback");
                    dbConn.rollback();
                    throw new MetaException("How did we get here, we heartbeated our lock before we started!");
                }
                if (locks[index].state == LockState.ACQUIRED) {
                    continue;
                }
                boolean acquired = false;
                for (int j = index - 1; j >= 0; --j) {
                    if (locks[index].db.equals(locks[j].db)) {
                        if (locks[index].table == null || locks[j].table == null || locks[index].table.equals(locks[j].table)) {
                            if (locks[index].partition == null || locks[j].partition == null || locks[index].partition.equals(locks[j].partition)) {
                                final LockAction lockAction = TxnHandler.jumpTable.get(locks[index].type).get(locks[j].type).get(locks[j].state);
                                TxnHandler.LOG.debug("desired Lock: " + info4 + " checked Lock: " + locks[j] + " action: " + lockAction);
                                switch (lockAction) {
                                    case WAIT: {
                                        if (!this.ignoreConflict(info4, locks[j])) {
                                            this.wait(dbConn, save);
                                            if (alwaysCommit) {
                                                TxnHandler.LOG.debug("Going to commit");
                                                dbConn.commit();
                                            }
                                            response.setState(LockState.WAITING);
                                            TxnHandler.LOG.debug("Lock(" + info4 + ") waiting for Lock(" + locks[j] + ")");
                                            return response;
                                        }
                                    }
                                    case ACQUIRE: {
                                        this.acquire(dbConn, stmt, extLockId, info4.intLockId);
                                        acquired = true;
                                        break;
                                    }
                                    case KEEP_LOOKING: {
                                        continue;
                                    }
                                }
                                if (acquired) {
                                    break;
                                }
                            }
                        }
                    }
                }
                if (acquired) {
                    continue;
                }
                this.acquire(dbConn, stmt, extLockId, info4.intLockId);
            }
            TxnHandler.LOG.debug("Going to commit");
            dbConn.commit();
            response.setState(LockState.ACQUIRED);
        }
        finally {
            this.closeStmt(stmt);
        }
        return response;
    }
    
    private boolean ignoreConflict(final LockInfo desiredLock, final LockInfo existingLock) {
        return (desiredLock.isDbLock() && desiredLock.type == LockType.SHARED_READ && existingLock.isTableLock() && existingLock.type == LockType.EXCLUSIVE) || (existingLock.isDbLock() && existingLock.type == LockType.SHARED_READ && desiredLock.isTableLock() && desiredLock.type == LockType.EXCLUSIVE) || (desiredLock.txnId != 0L && desiredLock.txnId == existingLock.txnId) || (desiredLock.txnId == 0L && desiredLock.extLockId == existingLock.extLockId);
    }
    
    private void wait(final Connection dbConn, final Savepoint save) throws SQLException {
        TxnHandler.LOG.debug("Going to rollback to savepoint");
        dbConn.rollback(save);
    }
    
    private void acquire(final Connection dbConn, final Statement stmt, final long extLockId, final long intLockId) throws SQLException, NoSuchLockException, MetaException {
        final long now = this.getDbTime(dbConn);
        final String s = "update HIVE_LOCKS set hl_lock_state = 'a', hl_last_heartbeat = " + now + ", hl_acquired_at = " + now + " where hl_lock_ext_id = " + extLockId + " and hl_lock_int_id = " + intLockId;
        TxnHandler.LOG.debug("Going to execute update <" + s + ">");
        final int rc = stmt.executeUpdate(s);
        if (rc < 1) {
            TxnHandler.LOG.debug("Going to rollback");
            dbConn.rollback();
            throw new NoSuchLockException("No such lock: (" + extLockId + "," + intLockId + ")");
        }
    }
    
    private void heartbeatLock(final Connection dbConn, final long extLockId) throws NoSuchLockException, SQLException, MetaException {
        if (extLockId == 0L) {
            return;
        }
        Statement stmt = null;
        try {
            stmt = dbConn.createStatement();
            final long now = this.getDbTime(dbConn);
            final String s = "update HIVE_LOCKS set hl_last_heartbeat = " + now + " where hl_lock_ext_id = " + extLockId;
            TxnHandler.LOG.debug("Going to execute update <" + s + ">");
            final int rc = stmt.executeUpdate(s);
            if (rc < 1) {
                TxnHandler.LOG.debug("Going to rollback");
                dbConn.rollback();
                throw new NoSuchLockException("No such lock: " + extLockId);
            }
            TxnHandler.LOG.debug("Going to commit");
            dbConn.commit();
        }
        finally {
            this.closeStmt(stmt);
        }
    }
    
    private void heartbeatTxn(final Connection dbConn, final long txnid) throws NoSuchTxnException, TxnAbortedException, SQLException, MetaException {
        if (txnid == 0L) {
            return;
        }
        Statement stmt = null;
        try {
            stmt = dbConn.createStatement();
            final long now = this.getDbTime(dbConn);
            String s = "select txn_state from TXNS where txn_id = " + txnid;
            TxnHandler.LOG.debug("Going to execute query <" + s + ">");
            final ResultSet rs = stmt.executeQuery(s);
            if (!rs.next()) {
                TxnHandler.LOG.debug("Going to rollback");
                dbConn.rollback();
                throw new NoSuchTxnException("No such transaction: " + txnid);
            }
            if (rs.getString(1).charAt(0) == 'a') {
                TxnHandler.LOG.debug("Going to rollback");
                dbConn.rollback();
                throw new TxnAbortedException("Transaction " + txnid + " already aborted");
            }
            s = "update TXNS set txn_last_heartbeat = " + now + " where txn_id = " + txnid;
            TxnHandler.LOG.debug("Going to execute update <" + s + ">");
            stmt.executeUpdate(s);
            TxnHandler.LOG.debug("Going to commit");
            dbConn.commit();
        }
        finally {
            this.closeStmt(stmt);
        }
    }
    
    private long getTxnIdFromLockId(final Connection dbConn, final long extLockId) throws NoSuchLockException, MetaException, SQLException {
        Statement stmt = null;
        try {
            stmt = dbConn.createStatement();
            final String s = "select hl_txnid from HIVE_LOCKS where hl_lock_ext_id = " + extLockId;
            TxnHandler.LOG.debug("Going to execute query <" + s + ">");
            final ResultSet rs = stmt.executeQuery(s);
            if (!rs.next()) {
                throw new MetaException("This should never happen!  We already checked the lock existed but now we can't find it!");
            }
            final long txnid = rs.getLong(1);
            TxnHandler.LOG.debug("Return txnid " + (rs.wasNull() ? -1L : txnid));
            return rs.wasNull() ? -1L : txnid;
        }
        finally {
            this.closeStmt(stmt);
        }
    }
    
    private List<LockInfo> getLockInfoFromLockId(final Connection dbConn, final long extLockId) throws NoSuchLockException, MetaException, SQLException {
        Statement stmt = null;
        try {
            stmt = dbConn.createStatement();
            final String s = "select hl_lock_ext_id, hl_lock_int_id, hl_db, hl_table, hl_partition, hl_lock_state, hl_lock_type, hl_txnid from HIVE_LOCKS where hl_lock_ext_id = " + extLockId;
            TxnHandler.LOG.debug("Going to execute query <" + s + ">");
            final ResultSet rs = stmt.executeQuery(s);
            boolean sawAtLeastOne = false;
            final List<LockInfo> ourLockInfo = new ArrayList<LockInfo>();
            while (rs.next()) {
                ourLockInfo.add(new LockInfo(rs));
                sawAtLeastOne = true;
            }
            if (!sawAtLeastOne) {
                throw new MetaException("This should never happen!  We already checked the lock existed but now we can't find it!");
            }
            return ourLockInfo;
        }
        finally {
            this.closeStmt(stmt);
        }
    }
    
    private void timeOutLocks(final Connection dbConn) throws SQLException, MetaException {
        final long now = this.getDbTime(dbConn);
        Statement stmt = null;
        try {
            stmt = dbConn.createStatement();
            final String s = "delete from HIVE_LOCKS where hl_last_heartbeat < " + (now - this.timeout);
            TxnHandler.LOG.debug("Going to execute update <" + s + ">");
            stmt.executeUpdate(s);
            TxnHandler.LOG.debug("Going to commit");
            dbConn.commit();
        }
        finally {
            this.closeStmt(stmt);
        }
    }
    
    private void timeOutTxns(final Connection dbConn) throws SQLException, MetaException, RetryException {
        final long now = this.getDbTime(dbConn);
        Statement stmt = null;
        try {
            stmt = dbConn.createStatement();
            final String s = "select txn_id from TXNS where txn_state = 'o' and txn_last_heartbeat <  " + (now - this.timeout);
            TxnHandler.LOG.debug("Going to execute query <" + s + ">");
            final ResultSet rs = stmt.executeQuery(s);
            final List<Long> deadTxns = new ArrayList<Long>();
            do {
                deadTxns.clear();
                for (int i = 0; i < 100 && rs.next(); ++i) {
                    deadTxns.add(rs.getLong(1));
                }
                if (deadTxns.size() > 0) {
                    this.abortTxns(dbConn, deadTxns);
                }
            } while (deadTxns.size() > 0);
            TxnHandler.LOG.debug("Going to commit");
            dbConn.commit();
        }
        catch (SQLException e) {
            TxnHandler.LOG.debug("Going to rollback");
            this.rollbackDBConn(dbConn);
            this.checkRetryable(dbConn, e, "abortTxn");
            throw new MetaException("Unable to update transaction database " + StringUtils.stringifyException(e));
        }
        finally {
            this.closeStmt(stmt);
        }
    }
    
    private static synchronized void setupJdbcConnectionPool(final HiveConf conf) throws SQLException {
        if (TxnHandler.connPool != null) {
            return;
        }
        final String driverUrl = HiveConf.getVar(conf, HiveConf.ConfVars.METASTORECONNECTURLKEY);
        final String user = HiveConf.getVar(conf, HiveConf.ConfVars.METASTORE_CONNECTION_USER_NAME);
        String passwd;
        try {
            passwd = ShimLoader.getHadoopShims().getPassword(conf, HiveConf.ConfVars.METASTOREPWD.varname);
        }
        catch (IOException err) {
            throw new SQLException("Error getting metastore password", err);
        }
        final String connectionPooler = HiveConf.getVar(conf, HiveConf.ConfVars.METASTORE_CONNECTION_POOLING_TYPE).toLowerCase();
        if ("bonecp".equals(connectionPooler)) {
            final BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(driverUrl);
            config.setMaxConnectionsPerPartition(10);
            config.setPartitionCount(1);
            config.setUser(user);
            config.setPassword(passwd);
            TxnHandler.connPool = new BoneCPDataSource(config);
        }
        else {
            if (!"dbcp".equals(connectionPooler)) {
                throw new RuntimeException("Unknown JDBC connection pooling " + connectionPooler);
            }
            final ObjectPool objectPool = new GenericObjectPool();
            final ConnectionFactory connFactory = new DriverManagerConnectionFactory(driverUrl, user, passwd);
            final PoolableConnectionFactory poolConnFactory = new PoolableConnectionFactory(connFactory, objectPool, null, null, false, true);
            TxnHandler.connPool = new PoolingDataSource(objectPool);
        }
    }
    
    private static synchronized void buildJumpTable() {
        if (TxnHandler.jumpTable != null) {
            return;
        }
        TxnHandler.jumpTable = new HashMap<LockType, Map<LockType, Map<LockState, LockAction>>>(3);
        Map<LockType, Map<LockState, LockAction>> m = new HashMap<LockType, Map<LockState, LockAction>>(3);
        TxnHandler.jumpTable.put(LockType.SHARED_READ, m);
        Map<LockState, LockAction> m2 = new HashMap<LockState, LockAction>(2);
        m.put(LockType.SHARED_READ, m2);
        m2.put(LockState.ACQUIRED, LockAction.ACQUIRE);
        m2.put(LockState.WAITING, LockAction.KEEP_LOOKING);
        m2 = new HashMap<LockState, LockAction>(2);
        m.put(LockType.SHARED_WRITE, m2);
        m2.put(LockState.ACQUIRED, LockAction.ACQUIRE);
        m2.put(LockState.WAITING, LockAction.KEEP_LOOKING);
        m2 = new HashMap<LockState, LockAction>(2);
        m.put(LockType.EXCLUSIVE, m2);
        m2.put(LockState.ACQUIRED, LockAction.WAIT);
        m2.put(LockState.WAITING, LockAction.WAIT);
        m = new HashMap<LockType, Map<LockState, LockAction>>(3);
        TxnHandler.jumpTable.put(LockType.SHARED_WRITE, m);
        m2 = new HashMap<LockState, LockAction>(2);
        m.put(LockType.SHARED_READ, m2);
        m2.put(LockState.ACQUIRED, LockAction.KEEP_LOOKING);
        m2.put(LockState.WAITING, LockAction.KEEP_LOOKING);
        m2 = new HashMap<LockState, LockAction>(2);
        m.put(LockType.SHARED_WRITE, m2);
        m2.put(LockState.ACQUIRED, LockAction.WAIT);
        m2.put(LockState.WAITING, LockAction.WAIT);
        m2 = new HashMap<LockState, LockAction>(2);
        m.put(LockType.EXCLUSIVE, m2);
        m2.put(LockState.ACQUIRED, LockAction.WAIT);
        m2.put(LockState.WAITING, LockAction.WAIT);
        m = new HashMap<LockType, Map<LockState, LockAction>>(3);
        TxnHandler.jumpTable.put(LockType.EXCLUSIVE, m);
        m2 = new HashMap<LockState, LockAction>(2);
        m.put(LockType.SHARED_READ, m2);
        m2.put(LockState.ACQUIRED, LockAction.WAIT);
        m2.put(LockState.WAITING, LockAction.WAIT);
        m2 = new HashMap<LockState, LockAction>(2);
        m.put(LockType.SHARED_WRITE, m2);
        m2.put(LockState.ACQUIRED, LockAction.WAIT);
        m2.put(LockState.WAITING, LockAction.WAIT);
        m2 = new HashMap<LockState, LockAction>(2);
        m.put(LockType.EXCLUSIVE, m2);
        m2.put(LockState.ACQUIRED, LockAction.WAIT);
        m2.put(LockState.WAITING, LockAction.WAIT);
    }
    
    private static boolean isRetryable(final Exception ex) {
        if (ex instanceof SQLException) {
            final SQLException sqlException = (SQLException)ex;
            if ("08S01".equalsIgnoreCase(sqlException.getSQLState())) {
                return true;
            }
        }
        return false;
    }
    
    private static String getMessage(final SQLException ex) {
        return ex.getMessage() + "(SQLState=" + ex.getSQLState() + ",ErrorCode=" + ex.getErrorCode() + ")";
    }
    
    static {
        LOG = LogFactory.getLog(TxnHandler.class.getName());
        lockLock = new Object();
    }
    
    private static class LockInfoExt extends LockInfo
    {
        private final ShowLocksResponseElement e;
        
        LockInfoExt(final ShowLocksResponseElement e, final long intLockId) {
            super(e, intLockId);
            this.e = e;
        }
    }
    
    protected class RetryException extends Exception
    {
    }
    
    protected enum DatabaseProduct
    {
        DERBY, 
        MYSQL, 
        POSTGRES, 
        ORACLE, 
        SQLSERVER;
    }
    
    private static class LockInfo
    {
        private final long extLockId;
        private final long intLockId;
        private final long txnId;
        private final String db;
        private final String table;
        private final String partition;
        private final LockState state;
        private final LockType type;
        
        LockInfo(final ResultSet rs) throws SQLException, MetaException {
            this.extLockId = rs.getLong("hl_lock_ext_id");
            this.intLockId = rs.getLong("hl_lock_int_id");
            this.db = rs.getString("hl_db");
            final String t = rs.getString("hl_table");
            this.table = (rs.wasNull() ? null : t);
            final String p = rs.getString("hl_partition");
            this.partition = (rs.wasNull() ? null : p);
            switch (rs.getString("hl_lock_state").charAt(0)) {
                case 'w': {
                    this.state = LockState.WAITING;
                    break;
                }
                case 'a': {
                    this.state = LockState.ACQUIRED;
                    break;
                }
                default: {
                    throw new MetaException("Unknown lock state " + rs.getString("hl_lock_state").charAt(0));
                }
            }
            switch (rs.getString("hl_lock_type").charAt(0)) {
                case 'e': {
                    this.type = LockType.EXCLUSIVE;
                    break;
                }
                case 'r': {
                    this.type = LockType.SHARED_READ;
                    break;
                }
                case 'w': {
                    this.type = LockType.SHARED_WRITE;
                    break;
                }
                default: {
                    throw new MetaException("Unknown lock type " + rs.getString("hl_lock_type").charAt(0));
                }
            }
            this.txnId = rs.getLong("hl_txnid");
        }
        
        LockInfo(final ShowLocksResponseElement e, final long intLockId) {
            this.extLockId = e.getLockid();
            this.intLockId = intLockId;
            this.db = e.getDbname();
            this.table = e.getTablename();
            this.partition = e.getPartname();
            this.state = e.getState();
            this.type = e.getType();
            this.txnId = e.getTxnid();
        }
        
        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof LockInfo)) {
                return false;
            }
            final LockInfo o = (LockInfo)other;
            return this.extLockId == o.extLockId && this.intLockId == o.intLockId;
        }
        
        @Override
        public String toString() {
            return JavaUtils.lockIdToString(this.extLockId) + " intLockId:" + this.intLockId + " txnId:" + Long.toString(this.txnId) + " db:" + this.db + " table:" + this.table + " partition:" + this.partition + " state:" + ((this.state == null) ? "null" : this.state.toString()) + " type:" + ((this.type == null) ? "null" : this.type.toString());
        }
        
        private boolean isDbLock() {
            return this.db != null && this.table == null && this.partition == null;
        }
        
        private boolean isTableLock() {
            return this.db != null && this.table != null && this.partition == null;
        }
    }
    
    private static class LockInfoComparator implements Comparator<LockInfo>
    {
        private static final LockTypeComparator lockTypeComparator;
        
        @Override
        public boolean equals(final Object other) {
            return this == other;
        }
        
        @Override
        public int compare(final LockInfo info1, final LockInfo info2) {
            if (info1.state == LockState.ACQUIRED && info2.state != LockState.ACQUIRED) {
                return -1;
            }
            if (info1.state != LockState.ACQUIRED && info2.state == LockState.ACQUIRED) {
                return 1;
            }
            final int sortByType = LockInfoComparator.lockTypeComparator.compare(info1.type, info2.type);
            if (sortByType != 0) {
                return sortByType;
            }
            if (info1.extLockId < info2.extLockId) {
                return -1;
            }
            if (info1.extLockId > info2.extLockId) {
                return 1;
            }
            if (info1.intLockId < info2.intLockId) {
                return -1;
            }
            if (info1.intLockId > info2.intLockId) {
                return 1;
            }
            return 0;
        }
        
        static {
            lockTypeComparator = new LockTypeComparator();
        }
    }
    
    private static final class LockTypeComparator implements Comparator<LockType>
    {
        @Override
        public boolean equals(final Object other) {
            return this == other;
        }
        
        @Override
        public int compare(final LockType t1, final LockType t2) {
            switch (t1) {
                case EXCLUSIVE: {
                    if (t2 == LockType.EXCLUSIVE) {
                        return 0;
                    }
                    return 1;
                }
                case SHARED_WRITE: {
                    switch (t2) {
                        case EXCLUSIVE: {
                            return -1;
                        }
                        case SHARED_WRITE: {
                            return 0;
                        }
                        case SHARED_READ: {
                            return 1;
                        }
                        default: {
                            throw new RuntimeException("Unexpected LockType: " + t2);
                        }
                    }
                    break;
                }
                case SHARED_READ: {
                    if (t2 == LockType.SHARED_READ) {
                        return 0;
                    }
                    return -1;
                }
                default: {
                    throw new RuntimeException("Unexpected LockType: " + t1);
                }
            }
        }
    }
    
    private enum LockAction
    {
        ACQUIRE, 
        WAIT, 
        KEEP_LOOKING;
    }
}
