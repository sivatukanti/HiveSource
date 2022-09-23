// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.commons.logging.LogFactory;
import org.apache.hive.common.util.HiveStringUtils;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import com.google.common.collect.Lists;
import org.apache.hadoop.hive.metastore.api.AlreadyExistsException;
import org.apache.hadoop.ipc.RemoteException;
import java.net.URI;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.InvalidInputException;
import java.io.IOException;
import org.apache.hadoop.hive.common.FileUtils;
import org.apache.hadoop.fs.Path;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.common.ObjectPair;
import java.util.ArrayList;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.commons.logging.Log;
import org.apache.hadoop.conf.Configuration;

public class HiveAlterHandler implements AlterHandler
{
    protected Configuration hiveConf;
    private static final Log LOG;
    
    @Override
    public Configuration getConf() {
        return this.hiveConf;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.hiveConf = conf;
    }
    
    @Override
    public void alterTable(final RawStore msdb, final Warehouse wh, final String dbname, final String name, final Table newt) throws InvalidOperationException, MetaException {
        this.alterTable(msdb, wh, dbname, name, newt, false);
    }
    
    @Override
    public void alterTable(final RawStore msdb, final Warehouse wh, String dbname, String name, final Table newt, final boolean cascade) throws InvalidOperationException, MetaException {
        if (newt == null) {
            throw new InvalidOperationException("New table is invalid: " + newt);
        }
        if (!MetaStoreUtils.validateName(newt.getTableName())) {
            throw new InvalidOperationException(newt.getTableName() + " is not a valid object name");
        }
        final String validate = MetaStoreUtils.validateTblColumns(newt.getSd().getCols());
        if (validate != null) {
            throw new InvalidOperationException("Invalid column " + validate);
        }
        Path srcPath = null;
        FileSystem srcFs = null;
        Path destPath = null;
        FileSystem destFs = null;
        boolean success = false;
        boolean moveData = false;
        boolean rename = false;
        Table oldt = null;
        final List<ObjectPair<Partition, String>> altps = new ArrayList<ObjectPair<Partition, String>>();
        try {
            msdb.openTransaction();
            name = name.toLowerCase();
            dbname = dbname.toLowerCase();
            if (!newt.getTableName().equalsIgnoreCase(name) || !newt.getDbName().equalsIgnoreCase(dbname)) {
                if (msdb.getTable(newt.getDbName(), newt.getTableName()) != null) {
                    throw new InvalidOperationException("new table " + newt.getDbName() + "." + newt.getTableName() + " already exists");
                }
                rename = true;
            }
            oldt = msdb.getTable(dbname, name);
            if (oldt == null) {
                throw new InvalidOperationException("table " + newt.getDbName() + "." + newt.getTableName() + " doesn't exist");
            }
            if (HiveConf.getBoolVar(this.hiveConf, HiveConf.ConfVars.METASTORE_DISALLOW_INCOMPATIBLE_COL_TYPE_CHANGES, false)) {
                MetaStoreUtils.throwExceptionIfIncompatibleColTypeChange(oldt.getSd().getCols(), newt.getSd().getCols());
            }
            if (cascade) {
                if (MetaStoreUtils.isCascadeNeededInAlterTable(oldt, newt)) {
                    final List<Partition> parts = msdb.getPartitions(dbname, name, -1);
                    for (final Partition part : parts) {
                        final List<FieldSchema> oldCols = part.getSd().getCols();
                        part.getSd().setCols(newt.getSd().getCols());
                        final String oldPartName = Warehouse.makePartName(oldt.getPartitionKeys(), part.getValues());
                        this.updatePartColumnStatsForAlterColumns(msdb, part, oldPartName, part.getValues(), oldCols, part);
                        msdb.alterPartition(dbname, name, part.getValues(), part);
                    }
                }
                else {
                    HiveAlterHandler.LOG.warn("Alter table does not cascade changes to its partitions.");
                }
            }
            final boolean partKeysPartiallyEqual = this.checkPartialPartKeysEqual(oldt.getPartitionKeys(), newt.getPartitionKeys());
            if (!oldt.getTableType().equals(TableType.VIRTUAL_VIEW.toString()) && (oldt.getPartitionKeys().size() != newt.getPartitionKeys().size() || !partKeysPartiallyEqual)) {
                throw new InvalidOperationException("partition keys can not be changed.");
            }
            if (rename && !oldt.getTableType().equals(TableType.VIRTUAL_VIEW.toString()) && (oldt.getSd().getLocation().compareTo(newt.getSd().getLocation()) == 0 || StringUtils.isEmpty(newt.getSd().getLocation())) && !MetaStoreUtils.isExternalTable(oldt)) {
                srcPath = new Path(oldt.getSd().getLocation());
                srcFs = wh.getFs(srcPath);
                final Database db = msdb.getDatabase(newt.getDbName());
                final Path databasePath = this.constructRenamedPath(wh.getDatabasePath(db), srcPath);
                destPath = new Path(databasePath, newt.getTableName());
                destFs = wh.getFs(destPath);
                newt.getSd().setLocation(destPath.toString());
                moveData = true;
                if (!FileUtils.equalsFileSystem(srcFs, destFs)) {
                    throw new InvalidOperationException("table new location " + destPath + " is on a different file system than the old location " + srcPath + ". This operation is not supported");
                }
                try {
                    srcFs.exists(srcPath);
                    if (destFs.exists(destPath)) {
                        throw new InvalidOperationException("New location for this table " + newt.getDbName() + "." + newt.getTableName() + " already exists : " + destPath);
                    }
                }
                catch (IOException e5) {
                    throw new InvalidOperationException("Unable to access new location " + destPath + " for table " + newt.getDbName() + "." + newt.getTableName());
                }
                final String oldTblLocPath = srcPath.toUri().getPath();
                final String newTblLocPath = destPath.toUri().getPath();
                final List<Partition> parts2 = msdb.getPartitions(dbname, name, -1);
                for (final Partition part2 : parts2) {
                    final String oldPartLoc = part2.getSd().getLocation();
                    if (oldPartLoc.contains(oldTblLocPath)) {
                        final URI oldUri = new Path(oldPartLoc).toUri();
                        final String newPath = oldUri.getPath().replace(oldTblLocPath, newTblLocPath);
                        final Path newPartLocPath = new Path(oldUri.getScheme(), oldUri.getAuthority(), newPath);
                        altps.add(ObjectPair.create(part2, part2.getSd().getLocation()));
                        part2.getSd().setLocation(newPartLocPath.toString());
                        final String oldPartName2 = Warehouse.makePartName(oldt.getPartitionKeys(), part2.getValues());
                        try {
                            msdb.deletePartitionColumnStatistics(dbname, name, oldPartName2, part2.getValues(), null);
                        }
                        catch (InvalidInputException iie) {
                            throw new InvalidOperationException("Unable to update partition stats in table rename." + iie);
                        }
                        msdb.alterPartition(dbname, name, part2.getValues(), part2);
                    }
                }
            }
            else if (MetaStoreUtils.requireCalStats(this.hiveConf, null, null, newt) && newt.getPartitionKeysSize() == 0) {
                final Database db = msdb.getDatabase(newt.getDbName());
                MetaStoreUtils.updateUnpartitionedTableStatsFast(db, newt, wh, false, true);
            }
            this.updateTableColumnStatsForAlterTable(msdb, oldt, newt);
            msdb.alterTable(dbname, name, newt);
            success = msdb.commitTransaction();
        }
        catch (InvalidObjectException e) {
            HiveAlterHandler.LOG.debug(e);
            throw new InvalidOperationException("Unable to change partition or table. Check metastore logs for detailed stack." + e.getMessage());
        }
        catch (NoSuchObjectException e2) {
            HiveAlterHandler.LOG.debug(e2);
            throw new InvalidOperationException("Unable to change partition or table. Database " + dbname + " does not exist" + " Check metastore logs for detailed stack." + e2.getMessage());
        }
        finally {
            if (!success) {
                msdb.rollbackTransaction();
            }
            if (success && moveData) {
                try {
                    if (srcFs.exists(srcPath) && !srcFs.rename(srcPath, destPath)) {
                        throw new IOException("Renaming " + srcPath + " to " + destPath + " failed");
                    }
                }
                catch (IOException e3) {
                    HiveAlterHandler.LOG.error("Alter Table operation for " + dbname + "." + name + " failed.", e3);
                    boolean revertMetaDataTransaction = false;
                    try {
                        msdb.openTransaction();
                        msdb.alterTable(newt.getDbName(), newt.getTableName(), oldt);
                        for (final ObjectPair<Partition, String> pair : altps) {
                            final Partition part3 = pair.getFirst();
                            part3.getSd().setLocation(pair.getSecond());
                            msdb.alterPartition(newt.getDbName(), name, part3.getValues(), part3);
                        }
                        revertMetaDataTransaction = msdb.commitTransaction();
                    }
                    catch (Exception e4) {
                        HiveAlterHandler.LOG.error("Reverting metadata by HDFS operation failure failed During HDFS operation failed", e4);
                        HiveAlterHandler.LOG.error("Table " + Warehouse.getQualifiedName(newt) + " should be renamed to " + Warehouse.getQualifiedName(oldt));
                        HiveAlterHandler.LOG.error("Table " + Warehouse.getQualifiedName(newt) + " should have path " + srcPath);
                        for (final ObjectPair<Partition, String> pair2 : altps) {
                            HiveAlterHandler.LOG.error("Partition " + Warehouse.getQualifiedName(pair2.getFirst()) + " should have path " + pair2.getSecond());
                        }
                        if (!revertMetaDataTransaction) {
                            msdb.rollbackTransaction();
                        }
                    }
                    throw new InvalidOperationException("Alter Table operation for " + dbname + "." + name + " failed to move data due to: '" + this.getSimpleMessage(e3) + "' See hive log file for details.");
                }
            }
        }
        if (!success) {
            throw new MetaException("Committing the alter table transaction was not successful.");
        }
    }
    
    String getSimpleMessage(final IOException ex) {
        if (!(ex instanceof RemoteException)) {
            return ex.getMessage();
        }
        final String msg = ex.getMessage();
        if (msg == null || !msg.contains("\n")) {
            return msg;
        }
        return msg.substring(0, msg.indexOf(10));
    }
    
    @Override
    public Partition alterPartition(final RawStore msdb, final Warehouse wh, final String dbname, final String name, final List<String> part_vals, final Partition new_part) throws InvalidOperationException, InvalidObjectException, AlreadyExistsException, MetaException {
        boolean success = false;
        Path srcPath = null;
        Path destPath = null;
        FileSystem srcFs = null;
        FileSystem destFs = null;
        Partition oldPart = null;
        String oldPartLoc = null;
        String newPartLoc = null;
        if (new_part.getParameters() == null || new_part.getParameters().get("transient_lastDdlTime") == null || Integer.parseInt(new_part.getParameters().get("transient_lastDdlTime")) == 0) {
            new_part.putToParameters("transient_lastDdlTime", Long.toString(System.currentTimeMillis() / 1000L));
        }
        final Table tbl = msdb.getTable(dbname, name);
        if (part_vals != null) {
            if (part_vals.size() != 0) {
                try {
                    msdb.openTransaction();
                    try {
                        oldPart = msdb.getPartition(dbname, name, part_vals);
                    }
                    catch (NoSuchObjectException e3) {
                        throw new InvalidObjectException("Unable to rename partition because old partition does not exist");
                    }
                    Partition check_part = null;
                    try {
                        check_part = msdb.getPartition(dbname, name, new_part.getValues());
                    }
                    catch (NoSuchObjectException e) {
                        check_part = null;
                    }
                    if (check_part != null) {
                        throw new AlreadyExistsException("Partition already exists:" + dbname + "." + name + "." + new_part.getValues());
                    }
                    if (tbl == null) {
                        throw new InvalidObjectException("Unable to rename partition because table or database do not exist");
                    }
                    if (tbl.getTableType().equals(TableType.EXTERNAL_TABLE.toString())) {
                        new_part.getSd().setLocation(oldPart.getSd().getLocation());
                        final String oldPartName = Warehouse.makePartName(tbl.getPartitionKeys(), oldPart.getValues());
                        try {
                            msdb.deletePartitionColumnStatistics(dbname, name, oldPartName, oldPart.getValues(), null);
                        }
                        catch (NoSuchObjectException ex) {}
                        catch (InvalidInputException iie) {
                            throw new InvalidOperationException("Unable to update partition stats in table rename." + iie);
                        }
                        msdb.alterPartition(dbname, name, part_vals, new_part);
                    }
                    else {
                        try {
                            destPath = new Path(wh.getTablePath(msdb.getDatabase(dbname), name), Warehouse.makePartName(tbl.getPartitionKeys(), new_part.getValues()));
                            destPath = this.constructRenamedPath(destPath, new Path(new_part.getSd().getLocation()));
                        }
                        catch (NoSuchObjectException e) {
                            HiveAlterHandler.LOG.debug(e);
                            throw new InvalidOperationException("Unable to change partition or table. Database " + dbname + " does not exist" + " Check metastore logs for detailed stack." + e.getMessage());
                        }
                        if (destPath != null) {
                            newPartLoc = destPath.toString();
                            oldPartLoc = oldPart.getSd().getLocation();
                            srcPath = new Path(oldPartLoc);
                            HiveAlterHandler.LOG.info("srcPath:" + oldPartLoc);
                            HiveAlterHandler.LOG.info("descPath:" + newPartLoc);
                            srcFs = wh.getFs(srcPath);
                            destFs = wh.getFs(destPath);
                            if (!FileUtils.equalsFileSystem(srcFs, destFs)) {
                                throw new InvalidOperationException("table new location " + destPath + " is on a different file system than the old location " + srcPath + ". This operation is not supported");
                            }
                            try {
                                srcFs.exists(srcPath);
                                if (newPartLoc.compareTo(oldPartLoc) != 0 && destFs.exists(destPath)) {
                                    throw new InvalidOperationException("New location for this table " + tbl.getDbName() + "." + tbl.getTableName() + " already exists : " + destPath);
                                }
                            }
                            catch (IOException e4) {
                                throw new InvalidOperationException("Unable to access new location " + destPath + " for partition " + tbl.getDbName() + "." + tbl.getTableName() + " " + new_part.getValues());
                            }
                            new_part.getSd().setLocation(newPartLoc);
                            if (MetaStoreUtils.requireCalStats(this.hiveConf, oldPart, new_part, tbl)) {
                                MetaStoreUtils.updatePartitionStatsFast(new_part, wh, false, true);
                            }
                            final String oldPartName = Warehouse.makePartName(tbl.getPartitionKeys(), oldPart.getValues());
                            try {
                                msdb.deletePartitionColumnStatistics(dbname, name, oldPartName, oldPart.getValues(), null);
                            }
                            catch (NoSuchObjectException ex2) {}
                            catch (InvalidInputException iie) {
                                throw new InvalidOperationException("Unable to update partition stats in table rename." + iie);
                            }
                            msdb.alterPartition(dbname, name, part_vals, new_part);
                        }
                    }
                    success = msdb.commitTransaction();
                }
                finally {
                    if (!success) {
                        msdb.rollbackTransaction();
                    }
                    if (success && newPartLoc != null && newPartLoc.compareTo(oldPartLoc) != 0) {
                        try {
                            if (srcFs.exists(srcPath)) {
                                final Path destParentPath = destPath.getParent();
                                if (!wh.mkdirs(destParentPath, true)) {
                                    throw new IOException("Unable to create path " + destParentPath);
                                }
                                wh.renameDir(srcPath, destPath, true);
                                HiveAlterHandler.LOG.info("rename done!");
                            }
                        }
                        catch (IOException e5) {
                            boolean revertMetaDataTransaction = false;
                            try {
                                msdb.openTransaction();
                                msdb.alterPartition(dbname, name, new_part.getValues(), oldPart);
                                revertMetaDataTransaction = msdb.commitTransaction();
                            }
                            catch (Exception e2) {
                                HiveAlterHandler.LOG.error("Reverting metadata opeation failed During HDFS operation failed", e2);
                                if (!revertMetaDataTransaction) {
                                    msdb.rollbackTransaction();
                                }
                            }
                            throw new InvalidOperationException("Unable to access old location " + srcPath + " for partition " + tbl.getDbName() + "." + tbl.getTableName() + " " + part_vals);
                        }
                    }
                }
                return oldPart;
            }
        }
        try {
            oldPart = msdb.getPartition(dbname, name, new_part.getValues());
            if (MetaStoreUtils.requireCalStats(this.hiveConf, oldPart, new_part, tbl)) {
                MetaStoreUtils.updatePartitionStatsFast(new_part, wh, false, true);
            }
            this.updatePartColumnStats(msdb, dbname, name, new_part.getValues(), new_part);
            msdb.alterPartition(dbname, name, new_part.getValues(), new_part);
        }
        catch (InvalidObjectException e6) {
            throw new InvalidOperationException("alter is not possible");
        }
        catch (NoSuchObjectException e3) {
            throw new InvalidOperationException("alter is not possible");
        }
        return oldPart;
    }
    
    @Override
    public List<Partition> alterPartitions(final RawStore msdb, final Warehouse wh, final String dbname, final String name, final List<Partition> new_parts) throws InvalidOperationException, InvalidObjectException, AlreadyExistsException, MetaException {
        final List<Partition> oldParts = new ArrayList<Partition>();
        final List<List<String>> partValsList = new ArrayList<List<String>>();
        final Table tbl = msdb.getTable(dbname, name);
        try {
            for (final Partition tmpPart : new_parts) {
                if (tmpPart.getParameters() == null || tmpPart.getParameters().get("transient_lastDdlTime") == null || Integer.parseInt(tmpPart.getParameters().get("transient_lastDdlTime")) == 0) {
                    tmpPart.putToParameters("transient_lastDdlTime", Long.toString(System.currentTimeMillis() / 1000L));
                }
                final Partition oldTmpPart = msdb.getPartition(dbname, name, tmpPart.getValues());
                oldParts.add(oldTmpPart);
                partValsList.add(tmpPart.getValues());
                if (MetaStoreUtils.requireCalStats(this.hiveConf, oldTmpPart, tmpPart, tbl)) {
                    MetaStoreUtils.updatePartitionStatsFast(tmpPart, wh, false, true);
                }
                this.updatePartColumnStats(msdb, dbname, name, oldTmpPart.getValues(), tmpPart);
            }
            msdb.alterPartitions(dbname, name, partValsList, new_parts);
        }
        catch (InvalidObjectException e) {
            throw new InvalidOperationException("alter is not possible");
        }
        catch (NoSuchObjectException e2) {
            throw new InvalidOperationException("alter is not possible");
        }
        return oldParts;
    }
    
    private boolean checkPartialPartKeysEqual(final List<FieldSchema> oldPartKeys, final List<FieldSchema> newPartKeys) {
        if (newPartKeys == null || oldPartKeys == null) {
            return oldPartKeys == newPartKeys;
        }
        if (oldPartKeys.size() != newPartKeys.size()) {
            return false;
        }
        final Iterator<FieldSchema> oldPartKeysIter = oldPartKeys.iterator();
        final Iterator<FieldSchema> newPartKeysIter = newPartKeys.iterator();
        while (oldPartKeysIter.hasNext()) {
            final FieldSchema oldFs = oldPartKeysIter.next();
            final FieldSchema newFs = newPartKeysIter.next();
            if (!oldFs.getName().equals(newFs.getName())) {
                return false;
            }
        }
        return true;
    }
    
    private Path constructRenamedPath(final Path defaultNewPath, final Path currentPath) {
        final URI currentUri = currentPath.toUri();
        return new Path(currentUri.getScheme(), currentUri.getAuthority(), defaultNewPath.toUri().getPath());
    }
    
    private void updatePartColumnStatsForAlterColumns(final RawStore msdb, final Partition oldPartition, final String oldPartName, final List<String> partVals, final List<FieldSchema> oldCols, final Partition newPart) throws MetaException, InvalidObjectException {
        final String dbName = oldPartition.getDbName();
        final String tableName = oldPartition.getTableName();
        try {
            final List<String> oldPartNames = Lists.newArrayList(oldPartName);
            final List<String> oldColNames = new ArrayList<String>(oldCols.size());
            for (final FieldSchema oldCol : oldCols) {
                oldColNames.add(oldCol.getName());
            }
            final List<FieldSchema> newCols = newPart.getSd().getCols();
            final List<ColumnStatistics> partsColStats = msdb.getPartitionColumnStatistics(dbName, tableName, oldPartNames, oldColNames);
            assert partsColStats.size() <= 1;
            for (final ColumnStatistics partColStats : partsColStats) {
                final List<ColumnStatisticsObj> statsObjs = partColStats.getStatsObj();
                for (final ColumnStatisticsObj statsObj : statsObjs) {
                    boolean found = false;
                    for (final FieldSchema newCol : newCols) {
                        if (statsObj.getColName().equals(newCol.getName()) && statsObj.getColType().equals(newCol.getType())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        msdb.deletePartitionColumnStatistics(dbName, tableName, oldPartName, partVals, statsObj.getColName());
                    }
                }
            }
        }
        catch (NoSuchObjectException nsoe) {
            HiveAlterHandler.LOG.debug("Could not find db entry." + nsoe);
        }
        catch (InvalidInputException iie) {
            throw new InvalidObjectException("Invalid input to update partition column stats in alter table change columns" + iie);
        }
    }
    
    private void updatePartColumnStats(final RawStore msdb, String dbName, String tableName, final List<String> partVals, final Partition newPart) throws MetaException, InvalidObjectException {
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        final String newDbName = HiveStringUtils.normalizeIdentifier(newPart.getDbName());
        final String newTableName = HiveStringUtils.normalizeIdentifier(newPart.getTableName());
        final Table oldTable = msdb.getTable(dbName, tableName);
        if (oldTable == null) {
            return;
        }
        try {
            final String oldPartName = Warehouse.makePartName(oldTable.getPartitionKeys(), partVals);
            final String newPartName = Warehouse.makePartName(oldTable.getPartitionKeys(), newPart.getValues());
            if (!dbName.equals(newDbName) || !tableName.equals(newTableName) || !oldPartName.equals(newPartName)) {
                msdb.deletePartitionColumnStatistics(dbName, tableName, oldPartName, partVals, null);
            }
            else {
                final Partition oldPartition = msdb.getPartition(dbName, tableName, partVals);
                if (oldPartition == null) {
                    return;
                }
                if (oldPartition.getSd() != null && newPart.getSd() != null) {
                    final List<FieldSchema> oldCols = oldPartition.getSd().getCols();
                    if (!MetaStoreUtils.areSameColumns(oldCols, newPart.getSd().getCols())) {
                        this.updatePartColumnStatsForAlterColumns(msdb, oldPartition, oldPartName, partVals, oldCols, newPart);
                    }
                }
            }
        }
        catch (NoSuchObjectException nsoe) {
            HiveAlterHandler.LOG.debug("Could not find db entry." + nsoe);
        }
        catch (InvalidInputException iie) {
            throw new InvalidObjectException("Invalid input to update partition column stats." + iie);
        }
    }
    
    private void updateTableColumnStatsForAlterTable(final RawStore msdb, final Table oldTable, final Table newTable) throws MetaException, InvalidObjectException {
        final String dbName = oldTable.getDbName();
        final String tableName = oldTable.getTableName();
        final String newDbName = HiveStringUtils.normalizeIdentifier(newTable.getDbName());
        final String newTableName = HiveStringUtils.normalizeIdentifier(newTable.getTableName());
        try {
            if (!dbName.equals(newDbName) || !tableName.equals(newTableName)) {
                msdb.deleteTableColumnStatistics(dbName, tableName, null);
            }
            else {
                final List<FieldSchema> oldCols = oldTable.getSd().getCols();
                final List<FieldSchema> newCols = newTable.getSd().getCols();
                if (!MetaStoreUtils.areSameColumns(oldCols, newCols)) {
                    final List<String> oldColNames = new ArrayList<String>(oldCols.size());
                    for (final FieldSchema oldCol : oldCols) {
                        oldColNames.add(oldCol.getName());
                    }
                    final ColumnStatistics cs = msdb.getTableColumnStatistics(dbName, tableName, oldColNames);
                    if (cs == null) {
                        return;
                    }
                    final List<ColumnStatisticsObj> statsObjs = cs.getStatsObj();
                    for (final ColumnStatisticsObj statsObj : statsObjs) {
                        boolean found = false;
                        for (final FieldSchema newCol : newCols) {
                            if (statsObj.getColName().equalsIgnoreCase(newCol.getName()) && statsObj.getColType().equals(newCol.getType())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            msdb.deleteTableColumnStatistics(dbName, tableName, statsObj.getColName());
                        }
                    }
                }
            }
        }
        catch (NoSuchObjectException nsoe) {
            HiveAlterHandler.LOG.debug("Could not find db entry." + nsoe);
        }
        catch (InvalidInputException e) {
            throw new InvalidObjectException("Invalid inputs to update table column stats: " + e);
        }
    }
    
    static {
        LOG = LogFactory.getLog(HiveAlterHandler.class.getName());
    }
}
