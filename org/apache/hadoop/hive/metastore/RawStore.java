// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;
import org.apache.hadoop.hive.metastore.api.CurrentNotificationEventId;
import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.metastore.api.NotificationEventResponse;
import org.apache.hadoop.hive.metastore.api.NotificationEventRequest;
import org.apache.hadoop.hive.metastore.api.AggrStats;
import org.apache.hadoop.hive.metastore.api.Function;
import org.apache.hadoop.hive.metastore.api.HiveObjectPrivilege;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.model.MRoleMap;
import org.apache.hadoop.hive.metastore.api.PrivilegeBag;
import org.apache.hadoop.hive.metastore.model.MPartitionColumnPrivilege;
import org.apache.hadoop.hive.metastore.model.MTableColumnPrivilege;
import org.apache.hadoop.hive.metastore.model.MPartitionPrivilege;
import org.apache.hadoop.hive.metastore.model.MTablePrivilege;
import org.apache.hadoop.hive.metastore.model.MDBPrivilege;
import org.apache.hadoop.hive.metastore.model.MGlobalPrivilege;
import org.apache.hadoop.hive.metastore.api.PrincipalPrivilegeSet;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import org.apache.hadoop.hive.metastore.api.Role;
import org.apache.hadoop.hive.metastore.api.UnknownPartitionException;
import org.apache.hadoop.hive.metastore.api.InvalidPartitionException;
import org.apache.hadoop.hive.metastore.api.UnknownTableException;
import org.apache.hadoop.hive.metastore.api.PartitionEventType;
import java.util.Map;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.metastore.api.Index;
import org.apache.hadoop.hive.metastore.api.UnknownDBException;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecProxy;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.InvalidInputException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.Type;
import java.util.List;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.conf.Configurable;

public interface RawStore extends Configurable
{
    void shutdown();
    
    boolean openTransaction();
    
    @CanNotRetry
    boolean commitTransaction();
    
    @CanNotRetry
    void rollbackTransaction();
    
    void createDatabase(final Database p0) throws InvalidObjectException, MetaException;
    
    Database getDatabase(final String p0) throws NoSuchObjectException;
    
    boolean dropDatabase(final String p0) throws NoSuchObjectException, MetaException;
    
    boolean alterDatabase(final String p0, final Database p1) throws NoSuchObjectException, MetaException;
    
    List<String> getDatabases(final String p0) throws MetaException;
    
    List<String> getAllDatabases() throws MetaException;
    
    boolean createType(final Type p0);
    
    Type getType(final String p0);
    
    boolean dropType(final String p0);
    
    void createTable(final Table p0) throws InvalidObjectException, MetaException;
    
    boolean dropTable(final String p0, final String p1) throws MetaException, NoSuchObjectException, InvalidObjectException, InvalidInputException;
    
    Table getTable(final String p0, final String p1) throws MetaException;
    
    boolean addPartition(final Partition p0) throws InvalidObjectException, MetaException;
    
    boolean addPartitions(final String p0, final String p1, final List<Partition> p2) throws InvalidObjectException, MetaException;
    
    boolean addPartitions(final String p0, final String p1, final PartitionSpecProxy p2, final boolean p3) throws InvalidObjectException, MetaException;
    
    Partition getPartition(final String p0, final String p1, final List<String> p2) throws MetaException, NoSuchObjectException;
    
    boolean doesPartitionExist(final String p0, final String p1, final List<String> p2) throws MetaException, NoSuchObjectException;
    
    boolean dropPartition(final String p0, final String p1, final List<String> p2) throws MetaException, NoSuchObjectException, InvalidObjectException, InvalidInputException;
    
    List<Partition> getPartitions(final String p0, final String p1, final int p2) throws MetaException, NoSuchObjectException;
    
    void alterTable(final String p0, final String p1, final Table p2) throws InvalidObjectException, MetaException;
    
    List<String> getTables(final String p0, final String p1) throws MetaException;
    
    List<Table> getTableObjectsByName(final String p0, final List<String> p1) throws MetaException, UnknownDBException;
    
    List<String> getAllTables(final String p0) throws MetaException;
    
    List<String> listTableNamesByFilter(final String p0, final String p1, final short p2) throws MetaException, UnknownDBException;
    
    List<String> listPartitionNames(final String p0, final String p1, final short p2) throws MetaException;
    
    List<String> listPartitionNamesByFilter(final String p0, final String p1, final String p2, final short p3) throws MetaException;
    
    void alterPartition(final String p0, final String p1, final List<String> p2, final Partition p3) throws InvalidObjectException, MetaException;
    
    void alterPartitions(final String p0, final String p1, final List<List<String>> p2, final List<Partition> p3) throws InvalidObjectException, MetaException;
    
    boolean addIndex(final Index p0) throws InvalidObjectException, MetaException;
    
    Index getIndex(final String p0, final String p1, final String p2) throws MetaException;
    
    boolean dropIndex(final String p0, final String p1, final String p2) throws MetaException;
    
    List<Index> getIndexes(final String p0, final String p1, final int p2) throws MetaException;
    
    List<String> listIndexNames(final String p0, final String p1, final short p2) throws MetaException;
    
    void alterIndex(final String p0, final String p1, final String p2, final Index p3) throws InvalidObjectException, MetaException;
    
    List<Partition> getPartitionsByFilter(final String p0, final String p1, final String p2, final short p3) throws MetaException, NoSuchObjectException;
    
    boolean getPartitionsByExpr(final String p0, final String p1, final byte[] p2, final String p3, final short p4, final List<Partition> p5) throws TException;
    
    List<Partition> getPartitionsByNames(final String p0, final String p1, final List<String> p2) throws MetaException, NoSuchObjectException;
    
    Table markPartitionForEvent(final String p0, final String p1, final Map<String, String> p2, final PartitionEventType p3) throws MetaException, UnknownTableException, InvalidPartitionException, UnknownPartitionException;
    
    boolean isPartitionMarkedForEvent(final String p0, final String p1, final Map<String, String> p2, final PartitionEventType p3) throws MetaException, UnknownTableException, InvalidPartitionException, UnknownPartitionException;
    
    boolean addRole(final String p0, final String p1) throws InvalidObjectException, MetaException, NoSuchObjectException;
    
    boolean removeRole(final String p0) throws MetaException, NoSuchObjectException;
    
    boolean grantRole(final Role p0, final String p1, final PrincipalType p2, final String p3, final PrincipalType p4, final boolean p5) throws MetaException, NoSuchObjectException, InvalidObjectException;
    
    boolean revokeRole(final Role p0, final String p1, final PrincipalType p2, final boolean p3) throws MetaException, NoSuchObjectException;
    
    PrincipalPrivilegeSet getUserPrivilegeSet(final String p0, final List<String> p1) throws InvalidObjectException, MetaException;
    
    PrincipalPrivilegeSet getDBPrivilegeSet(final String p0, final String p1, final List<String> p2) throws InvalidObjectException, MetaException;
    
    PrincipalPrivilegeSet getTablePrivilegeSet(final String p0, final String p1, final String p2, final List<String> p3) throws InvalidObjectException, MetaException;
    
    PrincipalPrivilegeSet getPartitionPrivilegeSet(final String p0, final String p1, final String p2, final String p3, final List<String> p4) throws InvalidObjectException, MetaException;
    
    PrincipalPrivilegeSet getColumnPrivilegeSet(final String p0, final String p1, final String p2, final String p3, final String p4, final List<String> p5) throws InvalidObjectException, MetaException;
    
    List<MGlobalPrivilege> listPrincipalGlobalGrants(final String p0, final PrincipalType p1);
    
    List<MDBPrivilege> listPrincipalDBGrants(final String p0, final PrincipalType p1, final String p2);
    
    List<MTablePrivilege> listAllTableGrants(final String p0, final PrincipalType p1, final String p2, final String p3);
    
    List<MPartitionPrivilege> listPrincipalPartitionGrants(final String p0, final PrincipalType p1, final String p2, final String p3, final String p4);
    
    List<MTableColumnPrivilege> listPrincipalTableColumnGrants(final String p0, final PrincipalType p1, final String p2, final String p3, final String p4);
    
    List<MPartitionColumnPrivilege> listPrincipalPartitionColumnGrants(final String p0, final PrincipalType p1, final String p2, final String p3, final String p4, final String p5);
    
    boolean grantPrivileges(final PrivilegeBag p0) throws InvalidObjectException, MetaException, NoSuchObjectException;
    
    boolean revokePrivileges(final PrivilegeBag p0, final boolean p1) throws InvalidObjectException, MetaException, NoSuchObjectException;
    
    Role getRole(final String p0) throws NoSuchObjectException;
    
    List<String> listRoleNames();
    
    List<MRoleMap> listRoles(final String p0, final PrincipalType p1);
    
    List<MRoleMap> listRoleMembers(final String p0);
    
    Partition getPartitionWithAuth(final String p0, final String p1, final List<String> p2, final String p3, final List<String> p4) throws MetaException, NoSuchObjectException, InvalidObjectException;
    
    List<Partition> getPartitionsWithAuth(final String p0, final String p1, final short p2, final String p3, final List<String> p4) throws MetaException, NoSuchObjectException, InvalidObjectException;
    
    List<String> listPartitionNamesPs(final String p0, final String p1, final List<String> p2, final short p3) throws MetaException, NoSuchObjectException;
    
    List<Partition> listPartitionsPsWithAuth(final String p0, final String p1, final List<String> p2, final short p3, final String p4, final List<String> p5) throws MetaException, InvalidObjectException, NoSuchObjectException;
    
    boolean updateTableColumnStatistics(final ColumnStatistics p0) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException;
    
    boolean updatePartitionColumnStatistics(final ColumnStatistics p0, final List<String> p1) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException;
    
    ColumnStatistics getTableColumnStatistics(final String p0, final String p1, final List<String> p2) throws MetaException, NoSuchObjectException;
    
    List<ColumnStatistics> getPartitionColumnStatistics(final String p0, final String p1, final List<String> p2, final List<String> p3) throws MetaException, NoSuchObjectException;
    
    boolean deletePartitionColumnStatistics(final String p0, final String p1, final String p2, final List<String> p3, final String p4) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException;
    
    boolean deleteTableColumnStatistics(final String p0, final String p1, final String p2) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException;
    
    long cleanupEvents();
    
    boolean addToken(final String p0, final String p1);
    
    boolean removeToken(final String p0);
    
    String getToken(final String p0);
    
    List<String> getAllTokenIdentifiers();
    
    int addMasterKey(final String p0) throws MetaException;
    
    void updateMasterKey(final Integer p0, final String p1) throws NoSuchObjectException, MetaException;
    
    boolean removeMasterKey(final Integer p0);
    
    String[] getMasterKeys();
    
    void verifySchema() throws MetaException;
    
    String getMetaStoreSchemaVersion() throws MetaException;
    
    void setMetaStoreSchemaVersion(final String p0, final String p1) throws MetaException;
    
    void dropPartitions(final String p0, final String p1, final List<String> p2) throws MetaException, NoSuchObjectException;
    
    List<HiveObjectPrivilege> listPrincipalDBGrantsAll(final String p0, final PrincipalType p1);
    
    List<HiveObjectPrivilege> listPrincipalTableGrantsAll(final String p0, final PrincipalType p1);
    
    List<HiveObjectPrivilege> listPrincipalPartitionGrantsAll(final String p0, final PrincipalType p1);
    
    List<HiveObjectPrivilege> listPrincipalTableColumnGrantsAll(final String p0, final PrincipalType p1);
    
    List<HiveObjectPrivilege> listPrincipalPartitionColumnGrantsAll(final String p0, final PrincipalType p1);
    
    List<HiveObjectPrivilege> listGlobalGrantsAll();
    
    List<HiveObjectPrivilege> listDBGrantsAll(final String p0);
    
    List<HiveObjectPrivilege> listPartitionColumnGrantsAll(final String p0, final String p1, final String p2, final String p3);
    
    List<HiveObjectPrivilege> listTableGrantsAll(final String p0, final String p1);
    
    List<HiveObjectPrivilege> listPartitionGrantsAll(final String p0, final String p1, final String p2);
    
    List<HiveObjectPrivilege> listTableColumnGrantsAll(final String p0, final String p1, final String p2);
    
    void createFunction(final Function p0) throws InvalidObjectException, MetaException;
    
    void alterFunction(final String p0, final String p1, final Function p2) throws InvalidObjectException, MetaException;
    
    void dropFunction(final String p0, final String p1) throws MetaException, NoSuchObjectException, InvalidObjectException, InvalidInputException;
    
    Function getFunction(final String p0, final String p1) throws MetaException;
    
    List<String> getFunctions(final String p0, final String p1) throws MetaException;
    
    AggrStats get_aggr_stats_for(final String p0, final String p1, final List<String> p2, final List<String> p3) throws MetaException, NoSuchObjectException;
    
    NotificationEventResponse getNextNotification(final NotificationEventRequest p0);
    
    void addNotificationEvent(final NotificationEvent p0);
    
    void cleanNotificationEvents(final int p0);
    
    CurrentNotificationEventId getCurrentNotificationEventId();
    
    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CanNotRetry {
    }
}
