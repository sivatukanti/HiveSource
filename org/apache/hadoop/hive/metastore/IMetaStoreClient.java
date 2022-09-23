// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.metastore.api.SetPartitionsStatsRequest;
import org.apache.hadoop.hive.metastore.api.AggrStats;
import org.apache.hadoop.hive.metastore.api.GetRoleGrantsForPrincipalResponse;
import org.apache.hadoop.hive.metastore.api.GetRoleGrantsForPrincipalRequest;
import org.apache.hadoop.hive.metastore.api.GetPrincipalsInRoleResponse;
import org.apache.hadoop.hive.metastore.api.GetPrincipalsInRoleRequest;
import org.apache.hadoop.hive.metastore.api.FireEventResponse;
import org.apache.hadoop.hive.metastore.api.FireEventRequest;
import org.apache.hadoop.hive.metastore.api.CurrentNotificationEventId;
import org.apache.hadoop.hive.metastore.api.NotificationEventResponse;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponse;
import org.apache.hadoop.hive.metastore.api.CompactionType;
import org.apache.hadoop.hive.metastore.api.HeartbeatTxnRangeResponse;
import org.apache.hadoop.hive.metastore.api.ShowLocksResponse;
import org.apache.hadoop.hive.metastore.api.TxnOpenException;
import org.apache.hadoop.hive.metastore.api.NoSuchLockException;
import org.apache.hadoop.hive.metastore.api.LockResponse;
import org.apache.hadoop.hive.metastore.api.LockRequest;
import org.apache.hadoop.hive.metastore.api.GetOpenTxnsInfoResponse;
import org.apache.hadoop.hive.metastore.api.TxnAbortedException;
import org.apache.hadoop.hive.metastore.api.NoSuchTxnException;
import org.apache.hadoop.hive.metastore.api.OpenTxnsResponse;
import org.apache.hadoop.hive.common.ValidTxnList;
import org.apache.hadoop.hive.metastore.api.Function;
import java.io.IOException;
import org.apache.hadoop.hive.metastore.api.PrivilegeBag;
import org.apache.hadoop.hive.metastore.api.HiveObjectPrivilege;
import org.apache.hadoop.hive.metastore.api.PrincipalPrivilegeSet;
import org.apache.hadoop.hive.metastore.api.HiveObjectRef;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import org.apache.hadoop.hive.metastore.api.Role;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.InvalidInputException;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.api.Index;
import org.apache.hadoop.hive.metastore.api.ConfigValSecurityException;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.common.ObjectPair;
import org.apache.hadoop.hive.metastore.api.InvalidPartitionException;
import org.apache.hadoop.hive.metastore.api.UnknownPartitionException;
import org.apache.hadoop.hive.metastore.api.PartitionEventType;
import java.util.Map;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecProxy;
import org.apache.hadoop.hive.metastore.api.AlreadyExistsException;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.UnknownTableException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.hadoop.hive.metastore.api.UnknownDBException;
import java.util.List;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.common.classification.InterfaceStability;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface IMetaStoreClient
{
    boolean isCompatibleWith(final HiveConf p0);
    
    void setHiveAddedJars(final String p0);
    
    void reconnect() throws MetaException;
    
    void close();
    
    void setMetaConf(final String p0, final String p1) throws MetaException, TException;
    
    String getMetaConf(final String p0) throws MetaException, TException;
    
    List<String> getDatabases(final String p0) throws MetaException, TException;
    
    List<String> getAllDatabases() throws MetaException, TException;
    
    List<String> getTables(final String p0, final String p1) throws MetaException, TException, UnknownDBException;
    
    List<String> getAllTables(final String p0) throws MetaException, TException, UnknownDBException;
    
    List<String> listTableNamesByFilter(final String p0, final String p1, final short p2) throws MetaException, TException, InvalidOperationException, UnknownDBException;
    
    void dropTable(final String p0, final String p1, final boolean p2, final boolean p3) throws MetaException, TException, NoSuchObjectException;
    
    void dropTable(final String p0, final String p1, final boolean p2, final boolean p3, final boolean p4) throws MetaException, TException, NoSuchObjectException;
    
    @Deprecated
    void dropTable(final String p0, final boolean p1) throws MetaException, UnknownTableException, TException, NoSuchObjectException;
    
    void dropTable(final String p0, final String p1) throws MetaException, TException, NoSuchObjectException;
    
    boolean tableExists(final String p0, final String p1) throws MetaException, TException, UnknownDBException;
    
    @Deprecated
    boolean tableExists(final String p0) throws MetaException, TException, UnknownDBException;
    
    @Deprecated
    Table getTable(final String p0) throws MetaException, TException, NoSuchObjectException;
    
    Database getDatabase(final String p0) throws NoSuchObjectException, MetaException, TException;
    
    Table getTable(final String p0, final String p1) throws MetaException, TException, NoSuchObjectException;
    
    List<Table> getTableObjectsByName(final String p0, final List<String> p1) throws MetaException, InvalidOperationException, UnknownDBException, TException;
    
    Partition appendPartition(final String p0, final String p1, final List<String> p2) throws InvalidObjectException, AlreadyExistsException, MetaException, TException;
    
    Partition appendPartition(final String p0, final String p1, final String p2) throws InvalidObjectException, AlreadyExistsException, MetaException, TException;
    
    Partition add_partition(final Partition p0) throws InvalidObjectException, AlreadyExistsException, MetaException, TException;
    
    int add_partitions(final List<Partition> p0) throws InvalidObjectException, AlreadyExistsException, MetaException, TException;
    
    int add_partitions_pspec(final PartitionSpecProxy p0) throws InvalidObjectException, AlreadyExistsException, MetaException, TException;
    
    List<Partition> add_partitions(final List<Partition> p0, final boolean p1, final boolean p2) throws InvalidObjectException, AlreadyExistsException, MetaException, TException;
    
    Partition getPartition(final String p0, final String p1, final List<String> p2) throws NoSuchObjectException, MetaException, TException;
    
    Partition exchange_partition(final Map<String, String> p0, final String p1, final String p2, final String p3, final String p4) throws MetaException, NoSuchObjectException, InvalidObjectException, TException;
    
    Partition getPartition(final String p0, final String p1, final String p2) throws MetaException, UnknownTableException, NoSuchObjectException, TException;
    
    Partition getPartitionWithAuthInfo(final String p0, final String p1, final List<String> p2, final String p3, final List<String> p4) throws MetaException, UnknownTableException, NoSuchObjectException, TException;
    
    List<Partition> listPartitions(final String p0, final String p1, final short p2) throws NoSuchObjectException, MetaException, TException;
    
    PartitionSpecProxy listPartitionSpecs(final String p0, final String p1, final int p2) throws TException;
    
    List<Partition> listPartitions(final String p0, final String p1, final List<String> p2, final short p3) throws NoSuchObjectException, MetaException, TException;
    
    List<String> listPartitionNames(final String p0, final String p1, final short p2) throws MetaException, TException;
    
    List<String> listPartitionNames(final String p0, final String p1, final List<String> p2, final short p3) throws MetaException, TException, NoSuchObjectException;
    
    List<Partition> listPartitionsByFilter(final String p0, final String p1, final String p2, final short p3) throws MetaException, NoSuchObjectException, TException;
    
    PartitionSpecProxy listPartitionSpecsByFilter(final String p0, final String p1, final String p2, final int p3) throws MetaException, NoSuchObjectException, TException;
    
    boolean listPartitionsByExpr(final String p0, final String p1, final byte[] p2, final String p3, final short p4, final List<Partition> p5) throws TException;
    
    List<Partition> listPartitionsWithAuthInfo(final String p0, final String p1, final short p2, final String p3, final List<String> p4) throws MetaException, TException, NoSuchObjectException;
    
    List<Partition> getPartitionsByNames(final String p0, final String p1, final List<String> p2) throws NoSuchObjectException, MetaException, TException;
    
    List<Partition> listPartitionsWithAuthInfo(final String p0, final String p1, final List<String> p2, final short p3, final String p4, final List<String> p5) throws MetaException, TException, NoSuchObjectException;
    
    void markPartitionForEvent(final String p0, final String p1, final Map<String, String> p2, final PartitionEventType p3) throws MetaException, NoSuchObjectException, TException, UnknownTableException, UnknownDBException, UnknownPartitionException, InvalidPartitionException;
    
    boolean isPartitionMarkedForEvent(final String p0, final String p1, final Map<String, String> p2, final PartitionEventType p3) throws MetaException, NoSuchObjectException, TException, UnknownTableException, UnknownDBException, UnknownPartitionException, InvalidPartitionException;
    
    void validatePartitionNameCharacters(final List<String> p0) throws TException, MetaException;
    
    void createTable(final Table p0) throws AlreadyExistsException, InvalidObjectException, MetaException, NoSuchObjectException, TException;
    
    void alter_table(final String p0, final String p1, final Table p2) throws InvalidOperationException, MetaException, TException;
    
    void alter_table(final String p0, final String p1, final Table p2, final boolean p3) throws InvalidOperationException, MetaException, TException;
    
    void createDatabase(final Database p0) throws InvalidObjectException, AlreadyExistsException, MetaException, TException;
    
    void dropDatabase(final String p0) throws NoSuchObjectException, InvalidOperationException, MetaException, TException;
    
    void dropDatabase(final String p0, final boolean p1, final boolean p2) throws NoSuchObjectException, InvalidOperationException, MetaException, TException;
    
    void dropDatabase(final String p0, final boolean p1, final boolean p2, final boolean p3) throws NoSuchObjectException, InvalidOperationException, MetaException, TException;
    
    void alterDatabase(final String p0, final Database p1) throws NoSuchObjectException, MetaException, TException;
    
    boolean dropPartition(final String p0, final String p1, final List<String> p2, final boolean p3) throws NoSuchObjectException, MetaException, TException;
    
    boolean dropPartition(final String p0, final String p1, final List<String> p2, final PartitionDropOptions p3) throws TException;
    
    List<Partition> dropPartitions(final String p0, final String p1, final List<ObjectPair<Integer, byte[]>> p2, final boolean p3, final boolean p4, final boolean p5) throws NoSuchObjectException, MetaException, TException;
    
    List<Partition> dropPartitions(final String p0, final String p1, final List<ObjectPair<Integer, byte[]>> p2, final boolean p3, final boolean p4, final boolean p5, final boolean p6) throws NoSuchObjectException, MetaException, TException;
    
    List<Partition> dropPartitions(final String p0, final String p1, final List<ObjectPair<Integer, byte[]>> p2, final PartitionDropOptions p3) throws TException;
    
    boolean dropPartition(final String p0, final String p1, final String p2, final boolean p3) throws NoSuchObjectException, MetaException, TException;
    
    void alter_partition(final String p0, final String p1, final Partition p2) throws InvalidOperationException, MetaException, TException;
    
    void alter_partitions(final String p0, final String p1, final List<Partition> p2) throws InvalidOperationException, MetaException, TException;
    
    void renamePartition(final String p0, final String p1, final List<String> p2, final Partition p3) throws InvalidOperationException, MetaException, TException;
    
    List<FieldSchema> getFields(final String p0, final String p1) throws MetaException, TException, UnknownTableException, UnknownDBException;
    
    List<FieldSchema> getSchema(final String p0, final String p1) throws MetaException, TException, UnknownTableException, UnknownDBException;
    
    String getConfigValue(final String p0, final String p1) throws TException, ConfigValSecurityException;
    
    List<String> partitionNameToVals(final String p0) throws MetaException, TException;
    
    Map<String, String> partitionNameToSpec(final String p0) throws MetaException, TException;
    
    void createIndex(final Index p0, final Table p1) throws InvalidObjectException, MetaException, NoSuchObjectException, TException, AlreadyExistsException;
    
    void alter_index(final String p0, final String p1, final String p2, final Index p3) throws InvalidOperationException, MetaException, TException;
    
    Index getIndex(final String p0, final String p1, final String p2) throws MetaException, UnknownTableException, NoSuchObjectException, TException;
    
    List<Index> listIndexes(final String p0, final String p1, final short p2) throws NoSuchObjectException, MetaException, TException;
    
    List<String> listIndexNames(final String p0, final String p1, final short p2) throws MetaException, TException;
    
    boolean dropIndex(final String p0, final String p1, final String p2, final boolean p3) throws NoSuchObjectException, MetaException, TException;
    
    boolean updateTableColumnStatistics(final ColumnStatistics p0) throws NoSuchObjectException, InvalidObjectException, MetaException, TException, InvalidInputException;
    
    boolean updatePartitionColumnStatistics(final ColumnStatistics p0) throws NoSuchObjectException, InvalidObjectException, MetaException, TException, InvalidInputException;
    
    List<ColumnStatisticsObj> getTableColumnStatistics(final String p0, final String p1, final List<String> p2) throws NoSuchObjectException, MetaException, TException;
    
    Map<String, List<ColumnStatisticsObj>> getPartitionColumnStatistics(final String p0, final String p1, final List<String> p2, final List<String> p3) throws NoSuchObjectException, MetaException, TException;
    
    boolean deletePartitionColumnStatistics(final String p0, final String p1, final String p2, final String p3) throws NoSuchObjectException, MetaException, InvalidObjectException, TException, InvalidInputException;
    
    boolean deleteTableColumnStatistics(final String p0, final String p1, final String p2) throws NoSuchObjectException, MetaException, InvalidObjectException, TException, InvalidInputException;
    
    boolean create_role(final Role p0) throws MetaException, TException;
    
    boolean drop_role(final String p0) throws MetaException, TException;
    
    List<String> listRoleNames() throws MetaException, TException;
    
    boolean grant_role(final String p0, final String p1, final PrincipalType p2, final String p3, final PrincipalType p4, final boolean p5) throws MetaException, TException;
    
    boolean revoke_role(final String p0, final String p1, final PrincipalType p2, final boolean p3) throws MetaException, TException;
    
    List<Role> list_roles(final String p0, final PrincipalType p1) throws MetaException, TException;
    
    PrincipalPrivilegeSet get_privilege_set(final HiveObjectRef p0, final String p1, final List<String> p2) throws MetaException, TException;
    
    List<HiveObjectPrivilege> list_privileges(final String p0, final PrincipalType p1, final HiveObjectRef p2) throws MetaException, TException;
    
    boolean grant_privileges(final PrivilegeBag p0) throws MetaException, TException;
    
    boolean revoke_privileges(final PrivilegeBag p0, final boolean p1) throws MetaException, TException;
    
    String getDelegationToken(final String p0, final String p1) throws MetaException, TException;
    
    long renewDelegationToken(final String p0) throws MetaException, TException;
    
    void cancelDelegationToken(final String p0) throws MetaException, TException;
    
    String getTokenStrForm() throws IOException;
    
    void createFunction(final Function p0) throws InvalidObjectException, MetaException, TException;
    
    void alterFunction(final String p0, final String p1, final Function p2) throws InvalidObjectException, MetaException, TException;
    
    void dropFunction(final String p0, final String p1) throws MetaException, NoSuchObjectException, InvalidObjectException, InvalidInputException, TException;
    
    Function getFunction(final String p0, final String p1) throws MetaException, TException;
    
    List<String> getFunctions(final String p0, final String p1) throws MetaException, TException;
    
    ValidTxnList getValidTxns() throws TException;
    
    ValidTxnList getValidTxns(final long p0) throws TException;
    
    long openTxn(final String p0) throws TException;
    
    OpenTxnsResponse openTxns(final String p0, final int p1) throws TException;
    
    void rollbackTxn(final long p0) throws NoSuchTxnException, TException;
    
    void commitTxn(final long p0) throws NoSuchTxnException, TxnAbortedException, TException;
    
    GetOpenTxnsInfoResponse showTxns() throws TException;
    
    LockResponse lock(final LockRequest p0) throws NoSuchTxnException, TxnAbortedException, TException;
    
    LockResponse checkLock(final long p0) throws NoSuchTxnException, TxnAbortedException, NoSuchLockException, TException;
    
    void unlock(final long p0) throws NoSuchLockException, TxnOpenException, TException;
    
    ShowLocksResponse showLocks() throws TException;
    
    void heartbeat(final long p0, final long p1) throws NoSuchLockException, NoSuchTxnException, TxnAbortedException, TException;
    
    HeartbeatTxnRangeResponse heartbeatTxnRange(final long p0, final long p1) throws TException;
    
    void compact(final String p0, final String p1, final String p2, final CompactionType p3) throws TException;
    
    ShowCompactResponse showCompactions() throws TException;
    
    void addDynamicPartitions(final long p0, final String p1, final String p2, final List<String> p3) throws TException;
    
    @InterfaceAudience.LimitedPrivate({ "HCatalog" })
    NotificationEventResponse getNextNotification(final long p0, final int p1, final NotificationFilter p2) throws TException;
    
    @InterfaceAudience.LimitedPrivate({ "HCatalog" })
    CurrentNotificationEventId getCurrentNotificationEventId() throws TException;
    
    @InterfaceAudience.LimitedPrivate({ "Apache Hive, HCatalog" })
    FireEventResponse fireListenerEvent(final FireEventRequest p0) throws TException;
    
    GetPrincipalsInRoleResponse get_principals_in_role(final GetPrincipalsInRoleRequest p0) throws MetaException, TException;
    
    GetRoleGrantsForPrincipalResponse get_role_grants_for_principal(final GetRoleGrantsForPrincipalRequest p0) throws MetaException, TException;
    
    AggrStats getAggrColStatsFor(final String p0, final String p1, final List<String> p2, final List<String> p3) throws NoSuchObjectException, MetaException, TException;
    
    boolean setPartitionColumnStatistics(final SetPartitionsStatsRequest p0) throws NoSuchObjectException, InvalidObjectException, MetaException, TException, InvalidInputException;
    
    public static class IncompatibleMetastoreException extends MetaException
    {
        IncompatibleMetastoreException(final String message) {
            super(message);
        }
    }
    
    @InterfaceAudience.LimitedPrivate({ "HCatalog" })
    public interface NotificationFilter
    {
        boolean accept(final NotificationEvent p0);
    }
}
