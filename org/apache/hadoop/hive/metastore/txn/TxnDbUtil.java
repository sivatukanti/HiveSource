// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.txn;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.shims.ShimLoader;
import java.util.Properties;
import java.sql.Driver;
import org.apache.hadoop.conf.Configuration;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;

public final class TxnDbUtil
{
    private static final Log LOG;
    private static final String TXN_MANAGER = "org.apache.hadoop.hive.ql.lockmgr.DbTxnManager";
    private static int deadlockCnt;
    
    private TxnDbUtil() {
        throw new UnsupportedOperationException("Can't initialize class");
    }
    
    public static void setConfValues(final HiveConf conf) {
        conf.setVar(HiveConf.ConfVars.HIVE_TXN_MANAGER, "org.apache.hadoop.hive.ql.lockmgr.DbTxnManager");
        conf.setBoolVar(HiveConf.ConfVars.HIVE_SUPPORT_CONCURRENCY, true);
    }
    
    public static void prepDb() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE TXNS (  TXN_ID bigint PRIMARY KEY,  TXN_STATE char(1) NOT NULL,  TXN_STARTED bigint NOT NULL,  TXN_LAST_HEARTBEAT bigint NOT NULL,  TXN_USER varchar(128) NOT NULL,  TXN_HOST varchar(128) NOT NULL)");
            stmt.execute("CREATE TABLE TXN_COMPONENTS (  TC_TXNID bigint REFERENCES TXNS (TXN_ID),  TC_DATABASE varchar(128) NOT NULL,  TC_TABLE varchar(128),  TC_PARTITION varchar(767))");
            stmt.execute("CREATE TABLE COMPLETED_TXN_COMPONENTS (  CTC_TXNID bigint,  CTC_DATABASE varchar(128) NOT NULL,  CTC_TABLE varchar(128),  CTC_PARTITION varchar(767))");
            stmt.execute("CREATE TABLE NEXT_TXN_ID (  NTXN_NEXT bigint NOT NULL)");
            stmt.execute("INSERT INTO NEXT_TXN_ID VALUES(1)");
            stmt.execute("CREATE TABLE HIVE_LOCKS ( HL_LOCK_EXT_ID bigint NOT NULL, HL_LOCK_INT_ID bigint NOT NULL, HL_TXNID bigint, HL_DB varchar(128) NOT NULL, HL_TABLE varchar(128), HL_PARTITION varchar(767), HL_LOCK_STATE char(1) NOT NULL, HL_LOCK_TYPE char(1) NOT NULL, HL_LAST_HEARTBEAT bigint NOT NULL, HL_ACQUIRED_AT bigint, HL_USER varchar(128) NOT NULL, HL_HOST varchar(128) NOT NULL, PRIMARY KEY(HL_LOCK_EXT_ID, HL_LOCK_INT_ID))");
            stmt.execute("CREATE INDEX HL_TXNID_INDEX ON HIVE_LOCKS (HL_TXNID)");
            stmt.execute("CREATE TABLE NEXT_LOCK_ID ( NL_NEXT bigint NOT NULL)");
            stmt.execute("INSERT INTO NEXT_LOCK_ID VALUES(1)");
            stmt.execute("CREATE TABLE COMPACTION_QUEUE ( CQ_ID bigint PRIMARY KEY, CQ_DATABASE varchar(128) NOT NULL, CQ_TABLE varchar(128) NOT NULL, CQ_PARTITION varchar(767), CQ_STATE char(1) NOT NULL, CQ_TYPE char(1) NOT NULL, CQ_WORKER_ID varchar(128), CQ_START bigint, CQ_RUN_AS varchar(128))");
            stmt.execute("CREATE TABLE NEXT_COMPACTION_QUEUE_ID (NCQ_NEXT bigint NOT NULL)");
            stmt.execute("INSERT INTO NEXT_COMPACTION_QUEUE_ID VALUES(1)");
            conn.commit();
        }
        catch (SQLException e) {
            conn.rollback();
            if (!(e instanceof SQLTransactionRollbackException) || TxnDbUtil.deadlockCnt++ >= 5) {
                throw e;
            }
            TxnDbUtil.LOG.warn("Caught deadlock, retrying db creation");
            prepDb();
        }
        finally {
            TxnDbUtil.deadlockCnt = 0;
            closeResources(conn, stmt, null);
        }
    }
    
    public static void cleanDb() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            try {
                stmt.execute("DROP INDEX HL_TXNID_INDEX");
            }
            catch (Exception e) {
                System.err.println("Unable to drop index HL_TXNID_INDEX " + e.getMessage());
            }
            dropTable(stmt, "TXN_COMPONENTS");
            dropTable(stmt, "COMPLETED_TXN_COMPONENTS");
            dropTable(stmt, "TXNS");
            dropTable(stmt, "NEXT_TXN_ID");
            dropTable(stmt, "HIVE_LOCKS");
            dropTable(stmt, "NEXT_LOCK_ID");
            dropTable(stmt, "COMPACTION_QUEUE");
            dropTable(stmt, "NEXT_COMPACTION_QUEUE_ID");
            conn.commit();
        }
        finally {
            closeResources(conn, stmt, null);
        }
    }
    
    private static void dropTable(final Statement stmt, final String name) {
        try {
            stmt.execute("DROP TABLE " + name);
        }
        catch (Exception e) {
            System.err.println("Unable to drop table " + name + ": " + e.getMessage());
        }
    }
    
    public static int countLockComponents(final long lockId) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT count(*) FROM hive_locks WHERE hl_lock_ext_id = ?");
            stmt.setLong(1, lockId);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return 0;
            }
            return rs.getInt(1);
        }
        finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    public static int findNumCurrentLocks() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select count(*) from hive_locks");
            if (!rs.next()) {
                return 0;
            }
            return rs.getInt(1);
        }
        finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    private static Connection getConnection() throws Exception {
        final HiveConf conf = new HiveConf();
        final String jdbcDriver = HiveConf.getVar(conf, HiveConf.ConfVars.METASTORE_CONNECTION_DRIVER);
        final Driver driver = (Driver)Class.forName(jdbcDriver).newInstance();
        final Properties prop = new Properties();
        final String driverUrl = HiveConf.getVar(conf, HiveConf.ConfVars.METASTORECONNECTURLKEY);
        final String user = HiveConf.getVar(conf, HiveConf.ConfVars.METASTORE_CONNECTION_USER_NAME);
        final String passwd = ShimLoader.getHadoopShims().getPassword(conf, HiveConf.ConfVars.METASTOREPWD.varname);
        prop.setProperty("user", user);
        prop.setProperty("password", passwd);
        return driver.connect(driverUrl, prop);
    }
    
    private static void closeResources(final Connection conn, final Statement stmt, final ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException e) {
                System.err.println("Error closing Statement: " + e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.rollback();
            }
            catch (SQLException e) {
                System.err.println("Error rolling back: " + e.getMessage());
            }
            try {
                conn.close();
            }
            catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(TxnDbUtil.class.getName());
        TxnDbUtil.deadlockCnt = 0;
    }
}
