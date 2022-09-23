// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.serde.serdeConstants;
import java.text.ParseException;
import java.sql.Date;
import java.text.DateFormat;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsData;
import com.google.common.collect.Lists;
import org.datanucleus.store.rdbms.query.ForwardQueryResult;
import org.apache.hive.common.util.BloomFilter;
import java.util.Arrays;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.AggrStats;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsDesc;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.api.SkewedInfo;
import org.apache.hadoop.hive.metastore.api.Order;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import java.util.TreeMap;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.metastore.parser.ExpressionTree;
import org.apache.hadoop.hive.metastore.api.Table;
import java.util.ArrayList;
import org.apache.hadoop.hive.metastore.api.Partition;
import java.util.Iterator;
import java.util.Map;
import javax.jdo.Query;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.hive.metastore.api.Database;
import java.sql.SQLException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import javax.jdo.Transaction;
import org.apache.hadoop.hive.metastore.model.MPartitionColumnStatistics;
import org.apache.hadoop.hive.metastore.model.MTableColumnStatistics;
import org.apache.hadoop.hive.metastore.model.MDatabase;
import javax.jdo.datastore.JDOConnection;
import java.sql.Connection;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.conf.Configuration;
import javax.jdo.PersistenceManager;
import org.apache.commons.logging.Log;

class MetaStoreDirectSql
{
    private static final int NO_BATCHING = -1;
    private static final int DETECT_BATCHING = 0;
    private static final Log LOG;
    private final PersistenceManager pm;
    private final DB dbType;
    private final int batchSize;
    private final boolean convertMapNullsToEmptyStrings;
    private final boolean isCompatibleDatastore;
    private final boolean isAggregateStatsCacheEnabled;
    private AggregateStatsCache aggrStatsCache;
    private static final String STATS_COLLIST = "\"COLUMN_NAME\", \"COLUMN_TYPE\", \"LONG_LOW_VALUE\", \"LONG_HIGH_VALUE\", \"DOUBLE_LOW_VALUE\", \"DOUBLE_HIGH_VALUE\", \"BIG_DECIMAL_LOW_VALUE\", \"BIG_DECIMAL_HIGH_VALUE\", \"NUM_NULLS\", \"NUM_DISTINCTS\", \"AVG_COL_LEN\", \"MAX_COL_LEN\", \"NUM_TRUES\", \"NUM_FALSES\", \"LAST_ANALYZED\" ";
    
    public MetaStoreDirectSql(final PersistenceManager pm, final Configuration conf) {
        this.pm = pm;
        this.dbType = this.determineDbType();
        int batchSize = HiveConf.getIntVar(conf, HiveConf.ConfVars.METASTORE_DIRECT_SQL_PARTITION_BATCH_SIZE);
        if (batchSize == 0) {
            batchSize = ((this.dbType == DB.ORACLE || this.dbType == DB.MSSQL) ? 1000 : -1);
        }
        this.batchSize = batchSize;
        this.convertMapNullsToEmptyStrings = HiveConf.getBoolVar(conf, HiveConf.ConfVars.METASTORE_ORM_RETRIEVE_MAPNULLS_AS_EMPTY_STRINGS);
        final String jdoIdFactory = HiveConf.getVar(conf, HiveConf.ConfVars.METASTORE_IDENTIFIER_FACTORY);
        if (!"datanucleus1".equalsIgnoreCase(jdoIdFactory)) {
            MetaStoreDirectSql.LOG.warn("Underlying metastore does not use 'datanuclues1' for its ORM naming scheme. Disabling directSQL as it uses hand-hardcoded SQL with that assumption.");
            this.isCompatibleDatastore = false;
        }
        else {
            this.isCompatibleDatastore = (this.ensureDbInit() && this.runTestQuery());
            if (this.isCompatibleDatastore) {
                MetaStoreDirectSql.LOG.info("Using direct SQL, underlying DB is " + this.dbType);
            }
        }
        this.isAggregateStatsCacheEnabled = HiveConf.getBoolVar(conf, HiveConf.ConfVars.METASTORE_AGGREGATE_STATS_CACHE_ENABLED);
        if (this.isAggregateStatsCacheEnabled) {
            this.aggrStatsCache = AggregateStatsCache.getInstance(conf);
        }
    }
    
    private DB determineDbType() {
        DB dbType = DB.OTHER;
        if (this.runDbCheck("SET @@session.sql_mode=ANSI_QUOTES", "MySql")) {
            dbType = DB.MYSQL;
        }
        else if (this.runDbCheck("SELECT version FROM v$instance", "Oracle")) {
            dbType = DB.ORACLE;
        }
        else if (this.runDbCheck("SELECT @@version", "MSSQL")) {
            dbType = DB.MSSQL;
        }
        else {
            final String productName = this.getProductName();
            if (productName != null && productName.toLowerCase().contains("derby")) {
                dbType = DB.DERBY;
            }
        }
        return dbType;
    }
    
    private String getProductName() {
        final JDOConnection jdoConn = this.pm.getDataStoreConnection();
        try {
            return ((Connection)jdoConn.getNativeConnection()).getMetaData().getDatabaseProductName();
        }
        catch (Throwable t) {
            MetaStoreDirectSql.LOG.warn("Error retrieving product name", t);
            return null;
        }
        finally {
            jdoConn.close();
        }
    }
    
    private boolean ensureDbInit() {
        final Transaction tx = this.pm.currentTransaction();
        try {
            this.pm.newQuery(MDatabase.class, "name == ''").execute();
            this.pm.newQuery(MTableColumnStatistics.class, "dbName == ''").execute();
            this.pm.newQuery(MPartitionColumnStatistics.class, "dbName == ''").execute();
            return true;
        }
        catch (Exception ex) {
            MetaStoreDirectSql.LOG.warn("Database initialization failed; direct SQL is disabled", ex);
            tx.rollback();
            return false;
        }
    }
    
    private boolean runTestQuery() {
        final Transaction tx = this.pm.currentTransaction();
        final String selfTestQuery = "select \"DB_ID\" from \"DBS\"";
        try {
            this.pm.newQuery("javax.jdo.query.SQL", selfTestQuery).execute();
            tx.commit();
            return true;
        }
        catch (Exception ex) {
            MetaStoreDirectSql.LOG.warn("Self-test query [" + selfTestQuery + "] failed; direct SQL is disabled", ex);
            tx.rollback();
            return false;
        }
    }
    
    public boolean isCompatibleDatastore() {
        return this.isCompatibleDatastore;
    }
    
    private void doDbSpecificInitializationsBeforeQuery() throws MetaException {
        if (this.dbType != DB.MYSQL) {
            return;
        }
        try {
            assert this.pm.currentTransaction().isActive();
            this.executeNoResult("SET @@session.sql_mode=ANSI_QUOTES");
        }
        catch (SQLException sqlEx) {
            throw new MetaException("Error setting ansi quotes: " + sqlEx.getMessage());
        }
    }
    
    private void executeNoResult(final String queryText) throws SQLException {
        final JDOConnection jdoConn = this.pm.getDataStoreConnection();
        final boolean doTrace = MetaStoreDirectSql.LOG.isDebugEnabled();
        try {
            final long start = doTrace ? System.nanoTime() : 0L;
            ((Connection)jdoConn.getNativeConnection()).createStatement().execute(queryText);
            this.timingTrace(doTrace, queryText, start, doTrace ? System.nanoTime() : 0L);
        }
        finally {
            jdoConn.close();
        }
    }
    
    private boolean runDbCheck(final String queryText, final String name) {
        Transaction tx = this.pm.currentTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
        try {
            this.executeNoResult(queryText);
            return true;
        }
        catch (Throwable t) {
            MetaStoreDirectSql.LOG.debug(name + " check failed, assuming we are not on " + name + ": " + t.getMessage());
            tx.rollback();
            tx = this.pm.currentTransaction();
            tx.begin();
            return false;
        }
    }
    
    public Database getDatabase(String dbName) throws MetaException {
        Query queryDbSelector = null;
        Query queryDbParams = null;
        try {
            dbName = dbName.toLowerCase();
            this.doDbSpecificInitializationsBeforeQuery();
            final String queryTextDbSelector = "select \"DB_ID\", \"NAME\", \"DB_LOCATION_URI\", \"DESC\", \"OWNER_NAME\", \"OWNER_TYPE\" FROM \"DBS\" where \"NAME\" = ? ";
            final Object[] params = { dbName };
            queryDbSelector = this.pm.newQuery("javax.jdo.query.SQL", queryTextDbSelector);
            if (MetaStoreDirectSql.LOG.isTraceEnabled()) {
                MetaStoreDirectSql.LOG.trace("getDatabase:query instantiated : " + queryTextDbSelector + " with param [" + params[0] + "]");
            }
            final List<Object[]> sqlResult = this.executeWithArray(queryDbSelector, params, queryTextDbSelector);
            if (sqlResult == null || sqlResult.isEmpty()) {
                return null;
            }
            assert sqlResult.size() == 1;
            if (sqlResult.get(0) == null) {
                return null;
            }
            final Object[] dbline = sqlResult.get(0);
            final Long dbid = extractSqlLong(dbline[0]);
            final String queryTextDbParams = "select \"PARAM_KEY\", \"PARAM_VALUE\"  FROM \"DATABASE_PARAMS\"  WHERE \"DB_ID\" = ?  AND \"PARAM_KEY\" IS NOT NULL";
            params[0] = dbid;
            queryDbParams = this.pm.newQuery("javax.jdo.query.SQL", queryTextDbParams);
            if (MetaStoreDirectSql.LOG.isTraceEnabled()) {
                MetaStoreDirectSql.LOG.trace("getDatabase:query2 instantiated : " + queryTextDbParams + " with param [" + params[0] + "]");
            }
            final Map<String, String> dbParams = new HashMap<String, String>();
            final List<Object[]> sqlResult2 = this.ensureList(this.executeWithArray(queryDbParams, params, queryTextDbParams));
            if (!sqlResult2.isEmpty()) {
                for (final Object[] line : sqlResult2) {
                    dbParams.put(this.extractSqlString(line[0]), this.extractSqlString(line[1]));
                }
            }
            final Database db = new Database();
            db.setName(this.extractSqlString(dbline[1]));
            db.setLocationUri(this.extractSqlString(dbline[2]));
            db.setDescription(this.extractSqlString(dbline[3]));
            db.setOwnerName(this.extractSqlString(dbline[4]));
            final String type = this.extractSqlString(dbline[5]);
            db.setOwnerType((null == type || type.trim().isEmpty()) ? null : PrincipalType.valueOf(type));
            db.setParameters(MetaStoreUtils.trimMapNulls(dbParams, this.convertMapNullsToEmptyStrings));
            if (MetaStoreDirectSql.LOG.isDebugEnabled()) {
                MetaStoreDirectSql.LOG.debug("getDatabase: directsql returning db " + db.getName() + " locn[" + db.getLocationUri() + "] desc [" + db.getDescription() + "] owner [" + db.getOwnerName() + "] ownertype [" + db.getOwnerType() + "]");
            }
            return db;
        }
        finally {
            if (queryDbSelector != null) {
                queryDbSelector.closeAll();
            }
            if (queryDbParams != null) {
                queryDbParams.closeAll();
            }
        }
    }
    
    public List<Partition> getPartitionsViaSqlFilter(final String dbName, final String tblName, final List<String> partNames) throws MetaException {
        if (partNames.isEmpty()) {
            return new ArrayList<Partition>();
        }
        return this.getPartitionsViaSqlFilterInternal(dbName, tblName, null, "\"PARTITIONS\".\"PART_NAME\" in (" + this.makeParams(partNames.size()) + ")", partNames, new ArrayList<String>(), null);
    }
    
    public List<Partition> getPartitionsViaSqlFilter(final Table table, final ExpressionTree tree, final Integer max) throws MetaException {
        assert tree != null;
        final List<Object> params = new ArrayList<Object>();
        final List<String> joins = new ArrayList<String>();
        final boolean dbHasJoinCastBug = this.dbType == DB.DERBY || this.dbType == DB.ORACLE;
        final String sqlFilter = generateSqlFilter(table, tree, params, joins, dbHasJoinCastBug);
        if (sqlFilter == null) {
            return null;
        }
        final Boolean isViewTable = isViewTable(table);
        return this.getPartitionsViaSqlFilterInternal(table.getDbName(), table.getTableName(), isViewTable, sqlFilter, params, joins, max);
    }
    
    public List<Partition> getPartitions(final String dbName, final String tblName, final Integer max) throws MetaException {
        return this.getPartitionsViaSqlFilterInternal(dbName, tblName, null, null, new ArrayList<Object>(), new ArrayList<String>(), max);
    }
    
    private static Boolean isViewTable(final Table t) {
        return t.isSetTableType() ? Boolean.valueOf(t.getTableType().equals(TableType.VIRTUAL_VIEW.toString())) : null;
    }
    
    private boolean isViewTable(final String dbName, final String tblName) throws MetaException {
        final String queryText = "select \"TBL_TYPE\" from \"TBLS\" inner join \"DBS\" on \"TBLS\".\"DB_ID\" = \"DBS\".\"DB_ID\"  where \"TBLS\".\"TBL_NAME\" = ? and \"DBS\".\"NAME\" = ?";
        final Object[] params = { tblName, dbName };
        final Query query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
        query.setUnique(true);
        final Object result = this.executeWithArray(query, params, queryText);
        return result != null && result.toString().equals(TableType.VIRTUAL_VIEW.toString());
    }
    
    private List<Partition> getPartitionsViaSqlFilterInternal(String dbName, String tblName, final Boolean isView, final String sqlFilter, final List<?> paramsForFilter, final List<String> joinsForFilter, final Integer max) throws MetaException {
        final boolean doTrace = MetaStoreDirectSql.LOG.isDebugEnabled();
        dbName = dbName.toLowerCase();
        tblName = tblName.toLowerCase();
        final String orderForFilter = (max != null) ? " order by \"PART_NAME\" asc" : "";
        this.doDbSpecificInitializationsBeforeQuery();
        final String queryText = "select \"PARTITIONS\".\"PART_ID\" from \"PARTITIONS\"  inner join \"TBLS\" on \"PARTITIONS\".\"TBL_ID\" = \"TBLS\".\"TBL_ID\"     and \"TBLS\".\"TBL_NAME\" = ?   inner join \"DBS\" on \"TBLS\".\"DB_ID\" = \"DBS\".\"DB_ID\"      and \"DBS\".\"NAME\" = ? " + StringUtils.join(joinsForFilter, ' ') + (StringUtils.isBlank(sqlFilter) ? "" : (" where " + sqlFilter)) + orderForFilter;
        final Object[] params = new Object[paramsForFilter.size() + 2];
        params[0] = tblName;
        params[1] = dbName;
        for (int i = 0; i < paramsForFilter.size(); ++i) {
            params[i + 2] = paramsForFilter.get(i);
        }
        final long start = doTrace ? System.nanoTime() : 0L;
        final Query query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
        if (max != null) {
            query.setRange(0L, max.shortValue());
        }
        final List<Object> sqlResult = this.executeWithArray(query, params, queryText);
        final long queryTime = doTrace ? System.nanoTime() : 0L;
        if (sqlResult.isEmpty()) {
            this.timingTrace(doTrace, queryText, start, queryTime);
            return new ArrayList<Partition>();
        }
        List<Partition> result = null;
        if (this.batchSize != -1 && this.batchSize < sqlResult.size()) {
            result = new ArrayList<Partition>(sqlResult.size());
            while (result.size() < sqlResult.size()) {
                final int toIndex = Math.min(result.size() + this.batchSize, sqlResult.size());
                final List<Object> batchedSqlResult = sqlResult.subList(result.size(), toIndex);
                result.addAll(this.getPartitionsFromPartitionIds(dbName, tblName, isView, batchedSqlResult));
            }
        }
        else {
            result = this.getPartitionsFromPartitionIds(dbName, tblName, isView, sqlResult);
        }
        this.timingTrace(doTrace, queryText, start, queryTime);
        query.closeAll();
        return result;
    }
    
    private List<Partition> getPartitionsFromPartitionIds(String dbName, String tblName, Boolean isView, final List<Object> partIdList) throws MetaException {
        final boolean doTrace = MetaStoreDirectSql.LOG.isDebugEnabled();
        final int idStringWidth = (int)Math.ceil(Math.log10(partIdList.size())) + 1;
        final int sbCapacity = partIdList.size() * idStringWidth;
        final StringBuilder partSb = new StringBuilder(sbCapacity);
        for (final Object partitionId : partIdList) {
            partSb.append(extractSqlLong(partitionId)).append(",");
        }
        final String partIds = trimCommaList(partSb);
        String queryText = "select \"PARTITIONS\".\"PART_ID\", \"SDS\".\"SD_ID\", \"SDS\".\"CD_ID\", \"SERDES\".\"SERDE_ID\", \"PARTITIONS\".\"CREATE_TIME\", \"PARTITIONS\".\"LAST_ACCESS_TIME\", \"SDS\".\"INPUT_FORMAT\", \"SDS\".\"IS_COMPRESSED\", \"SDS\".\"IS_STOREDASSUBDIRECTORIES\", \"SDS\".\"LOCATION\", \"SDS\".\"NUM_BUCKETS\", \"SDS\".\"OUTPUT_FORMAT\", \"SERDES\".\"NAME\", \"SERDES\".\"SLIB\" from \"PARTITIONS\"  left outer join \"SDS\" on \"PARTITIONS\".\"SD_ID\" = \"SDS\".\"SD_ID\"   left outer join \"SERDES\" on \"SDS\".\"SERDE_ID\" = \"SERDES\".\"SERDE_ID\" where \"PART_ID\" in (" + partIds + ") order by \"PART_NAME\" asc";
        final long start = doTrace ? System.nanoTime() : 0L;
        final Query query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
        final List<Object[]> sqlResult = this.executeWithArray(query, null, queryText);
        final long queryTime = doTrace ? System.nanoTime() : 0L;
        Deadline.checkTimeout();
        final TreeMap<Long, Partition> partitions = new TreeMap<Long, Partition>();
        final TreeMap<Long, StorageDescriptor> sds = new TreeMap<Long, StorageDescriptor>();
        final TreeMap<Long, SerDeInfo> serdes = new TreeMap<Long, SerDeInfo>();
        final TreeMap<Long, List<FieldSchema>> colss = new TreeMap<Long, List<FieldSchema>>();
        final ArrayList<Partition> orderedResult = new ArrayList<Partition>(partIdList.size());
        final StringBuilder sdSb = new StringBuilder(sbCapacity);
        final StringBuilder serdeSb = new StringBuilder(sbCapacity);
        final StringBuilder colsSb = new StringBuilder(7);
        tblName = tblName.toLowerCase();
        dbName = dbName.toLowerCase();
        for (final Object[] fields : sqlResult) {
            final long partitionId2 = extractSqlLong(fields[0]);
            final Long sdId = extractSqlLong(fields[1]);
            final Long colId = extractSqlLong(fields[2]);
            final Long serdeId = extractSqlLong(fields[3]);
            if (sdId == null || serdeId == null) {
                if (isView == null) {
                    isView = this.isViewTable(dbName, tblName);
                }
                if (sdId != null || colId != null || serdeId != null || !isView) {
                    throw new MetaException("Unexpected null for one of the IDs, SD " + sdId + ", serde " + serdeId + " for a " + (isView ? "" : "non-") + " view");
                }
            }
            final Partition part = new Partition();
            orderedResult.add(part);
            part.setParameters(new HashMap<String, String>());
            part.setValues(new ArrayList<String>());
            part.setDbName(dbName);
            part.setTableName(tblName);
            if (fields[4] != null) {
                part.setCreateTime(this.extractSqlInt(fields[4]));
            }
            if (fields[5] != null) {
                part.setLastAccessTime(this.extractSqlInt(fields[5]));
            }
            partitions.put(partitionId2, part);
            if (sdId == null) {
                continue;
            }
            assert serdeId != null;
            final StorageDescriptor sd = new StorageDescriptor();
            final StorageDescriptor oldSd = sds.put(sdId, sd);
            if (oldSd != null) {
                throw new MetaException("Partitions reuse SDs; we don't expect that");
            }
            sd.setSortCols(new ArrayList<Order>());
            sd.setBucketCols(new ArrayList<String>());
            sd.setParameters(new HashMap<String, String>());
            sd.setSkewedInfo(new SkewedInfo(new ArrayList<String>(), new ArrayList<List<String>>(), new HashMap<List<String>, String>()));
            sd.setInputFormat((String)fields[6]);
            Boolean tmpBoolean = extractSqlBoolean(fields[7]);
            if (tmpBoolean != null) {
                sd.setCompressed(tmpBoolean);
            }
            tmpBoolean = extractSqlBoolean(fields[8]);
            if (tmpBoolean != null) {
                sd.setStoredAsSubDirectories(tmpBoolean);
            }
            sd.setLocation((String)fields[9]);
            if (fields[10] != null) {
                sd.setNumBuckets(this.extractSqlInt(fields[10]));
            }
            sd.setOutputFormat((String)fields[11]);
            sdSb.append(sdId).append(",");
            part.setSd(sd);
            if (colId != null) {
                List<FieldSchema> cols = colss.get(colId);
                if (cols == null) {
                    cols = new ArrayList<FieldSchema>();
                    colss.put(colId, cols);
                    colsSb.append(colId).append(",");
                }
                sd.setCols(cols);
            }
            final SerDeInfo serde = new SerDeInfo();
            final SerDeInfo oldSerde = serdes.put(serdeId, serde);
            if (oldSerde != null) {
                throw new MetaException("SDs reuse serdes; we don't expect that");
            }
            serde.setParameters(new HashMap<String, String>());
            serde.setName((String)fields[12]);
            serde.setSerializationLib((String)fields[13]);
            serdeSb.append(serdeId).append(",");
            sd.setSerdeInfo(serde);
            Deadline.checkTimeout();
        }
        query.closeAll();
        this.timingTrace(doTrace, queryText, start, queryTime);
        queryText = "select \"PART_ID\", \"PARAM_KEY\", \"PARAM_VALUE\" from \"PARTITION_PARAMS\" where \"PART_ID\" in (" + partIds + ") and \"PARAM_KEY\" is not null" + " order by \"PART_ID\" asc";
        this.loopJoinOrderedResult(partitions, queryText, 0, new ApplyFunc<Partition>() {
            @Override
            public void apply(final Partition t, final Object[] fields) {
                t.putToParameters((String)fields[1], (String)fields[2]);
            }
        });
        queryText = "select \"PART_ID\", \"PART_KEY_VAL\" from \"PARTITION_KEY_VALS\" where \"PART_ID\" in (" + partIds + ") and \"INTEGER_IDX\" >= 0" + " order by \"PART_ID\" asc, \"INTEGER_IDX\" asc";
        this.loopJoinOrderedResult(partitions, queryText, 0, new ApplyFunc<Partition>() {
            @Override
            public void apply(final Partition t, final Object[] fields) {
                t.addToValues((String)fields[1]);
            }
        });
        if (sdSb.length() != 0) {
            final String sdIds = trimCommaList(sdSb);
            final String serdeIds = trimCommaList(serdeSb);
            final String colIds = trimCommaList(colsSb);
            queryText = "select \"SD_ID\", \"PARAM_KEY\", \"PARAM_VALUE\" from \"SD_PARAMS\" where \"SD_ID\" in (" + sdIds + ") and \"PARAM_KEY\" is not null" + " order by \"SD_ID\" asc";
            this.loopJoinOrderedResult(sds, queryText, 0, new ApplyFunc<StorageDescriptor>() {
                @Override
                public void apply(final StorageDescriptor t, final Object[] fields) {
                    t.putToParameters((String)fields[1], (String)fields[2]);
                }
            });
            queryText = "select \"SD_ID\", \"COLUMN_NAME\", \"SORT_COLS\".\"ORDER\" from \"SORT_COLS\" where \"SD_ID\" in (" + sdIds + ") and \"INTEGER_IDX\" >= 0" + " order by \"SD_ID\" asc, \"INTEGER_IDX\" asc";
            this.loopJoinOrderedResult(sds, queryText, 0, new ApplyFunc<StorageDescriptor>() {
                @Override
                public void apply(final StorageDescriptor t, final Object[] fields) {
                    if (fields[2] == null) {
                        return;
                    }
                    t.addToSortCols(new Order((String)fields[1], MetaStoreDirectSql.this.extractSqlInt(fields[2])));
                }
            });
            queryText = "select \"SD_ID\", \"BUCKET_COL_NAME\" from \"BUCKETING_COLS\" where \"SD_ID\" in (" + sdIds + ") and \"INTEGER_IDX\" >= 0" + " order by \"SD_ID\" asc, \"INTEGER_IDX\" asc";
            this.loopJoinOrderedResult(sds, queryText, 0, new ApplyFunc<StorageDescriptor>() {
                @Override
                public void apply(final StorageDescriptor t, final Object[] fields) {
                    t.addToBucketCols((String)fields[1]);
                }
            });
            queryText = "select \"SD_ID\", \"SKEWED_COL_NAME\" from \"SKEWED_COL_NAMES\" where \"SD_ID\" in (" + sdIds + ") and \"INTEGER_IDX\" >= 0" + " order by \"SD_ID\" asc, \"INTEGER_IDX\" asc";
            final boolean hasSkewedColumns = this.loopJoinOrderedResult(sds, queryText, 0, new ApplyFunc<StorageDescriptor>() {
                @Override
                public void apply(final StorageDescriptor t, final Object[] fields) {
                    if (!t.isSetSkewedInfo()) {
                        t.setSkewedInfo(new SkewedInfo());
                    }
                    t.getSkewedInfo().addToSkewedColNames((String)fields[1]);
                }
            }) > 0;
            if (hasSkewedColumns) {
                queryText = "select \"SKEWED_VALUES\".\"SD_ID_OID\",  \"SKEWED_STRING_LIST_VALUES\".\"STRING_LIST_ID\",  \"SKEWED_STRING_LIST_VALUES\".\"STRING_LIST_VALUE\" from \"SKEWED_VALUES\"   left outer join \"SKEWED_STRING_LIST_VALUES\" on \"SKEWED_VALUES\".\"STRING_LIST_ID_EID\" = \"SKEWED_STRING_LIST_VALUES\".\"STRING_LIST_ID\" where \"SKEWED_VALUES\".\"SD_ID_OID\" in (" + sdIds + ") " + "  and \"SKEWED_VALUES\".\"STRING_LIST_ID_EID\" is not null " + "  and \"SKEWED_VALUES\".\"INTEGER_IDX\" >= 0 " + "order by \"SKEWED_VALUES\".\"SD_ID_OID\" asc, \"SKEWED_VALUES\".\"INTEGER_IDX\" asc," + "  \"SKEWED_STRING_LIST_VALUES\".\"INTEGER_IDX\" asc";
                this.loopJoinOrderedResult(sds, queryText, 0, new ApplyFunc<StorageDescriptor>() {
                    private Long currentListId;
                    private List<String> currentList;
                    
                    @Override
                    public void apply(final StorageDescriptor t, final Object[] fields) throws MetaException {
                        if (!t.isSetSkewedInfo()) {
                            t.setSkewedInfo(new SkewedInfo());
                        }
                        if (fields[1] == null) {
                            this.currentList = null;
                            this.currentListId = null;
                            t.getSkewedInfo().addToSkewedColValues(new ArrayList<String>());
                        }
                        else {
                            final long fieldsListId = MetaStoreDirectSql.extractSqlLong(fields[1]);
                            if (this.currentListId == null || fieldsListId != this.currentListId) {
                                this.currentList = new ArrayList<String>();
                                this.currentListId = fieldsListId;
                                t.getSkewedInfo().addToSkewedColValues(this.currentList);
                            }
                            this.currentList.add((String)fields[2]);
                        }
                    }
                });
                queryText = "select \"SKEWED_COL_VALUE_LOC_MAP\".\"SD_ID\", \"SKEWED_STRING_LIST_VALUES\".STRING_LIST_ID, \"SKEWED_COL_VALUE_LOC_MAP\".\"LOCATION\", \"SKEWED_STRING_LIST_VALUES\".\"STRING_LIST_VALUE\" from \"SKEWED_COL_VALUE_LOC_MAP\"  left outer join \"SKEWED_STRING_LIST_VALUES\" on \"SKEWED_COL_VALUE_LOC_MAP\".\"STRING_LIST_ID_KID\" = \"SKEWED_STRING_LIST_VALUES\".\"STRING_LIST_ID\" where \"SKEWED_COL_VALUE_LOC_MAP\".\"SD_ID\" in (" + sdIds + ")" + "  and \"SKEWED_COL_VALUE_LOC_MAP\".\"STRING_LIST_ID_KID\" is not null " + "order by \"SKEWED_COL_VALUE_LOC_MAP\".\"SD_ID\" asc," + "  \"SKEWED_STRING_LIST_VALUES\".\"STRING_LIST_ID\" asc," + "  \"SKEWED_STRING_LIST_VALUES\".\"INTEGER_IDX\" asc";
                this.loopJoinOrderedResult(sds, queryText, 0, new ApplyFunc<StorageDescriptor>() {
                    private Long currentListId;
                    private List<String> currentList;
                    
                    @Override
                    public void apply(final StorageDescriptor t, final Object[] fields) throws MetaException {
                        if (!t.isSetSkewedInfo()) {
                            final SkewedInfo skewedInfo = new SkewedInfo();
                            skewedInfo.setSkewedColValueLocationMaps(new HashMap<List<String>, String>());
                            t.setSkewedInfo(skewedInfo);
                        }
                        final Map<List<String>, String> skewMap = t.getSkewedInfo().getSkewedColValueLocationMaps();
                        if (fields[1] == null) {
                            this.currentList = new ArrayList<String>();
                            this.currentListId = null;
                        }
                        else {
                            final long fieldsListId = MetaStoreDirectSql.extractSqlLong(fields[1]);
                            if (this.currentListId == null || fieldsListId != this.currentListId) {
                                this.currentList = new ArrayList<String>();
                                this.currentListId = fieldsListId;
                            }
                            else {
                                skewMap.remove(this.currentList);
                            }
                            this.currentList.add((String)fields[3]);
                        }
                        skewMap.put(this.currentList, (String)fields[2]);
                    }
                });
            }
            if (!colss.isEmpty()) {
                queryText = "select \"CD_ID\", \"COMMENT\", \"COLUMN_NAME\", \"TYPE_NAME\" from \"COLUMNS_V2\" where \"CD_ID\" in (" + colIds + ") and \"INTEGER_IDX\" >= 0" + " order by \"CD_ID\" asc, \"INTEGER_IDX\" asc";
                this.loopJoinOrderedResult(colss, queryText, 0, new ApplyFunc<List<FieldSchema>>() {
                    @Override
                    public void apply(final List<FieldSchema> t, final Object[] fields) {
                        t.add(new FieldSchema((String)fields[2], (String)fields[3], (String)fields[1]));
                    }
                });
            }
            queryText = "select \"SERDE_ID\", \"PARAM_KEY\", \"PARAM_VALUE\" from \"SERDE_PARAMS\" where \"SERDE_ID\" in (" + serdeIds + ") and \"PARAM_KEY\" is not null" + " order by \"SERDE_ID\" asc";
            this.loopJoinOrderedResult(serdes, queryText, 0, new ApplyFunc<SerDeInfo>() {
                @Override
                public void apply(final SerDeInfo t, final Object[] fields) {
                    t.putToParameters((String)fields[1], (String)fields[2]);
                }
            });
            return orderedResult;
        }
        assert serdeSb.length() == 0 && colsSb.length() == 0;
        return orderedResult;
    }
    
    private void timingTrace(final boolean doTrace, final String queryText, final long start, final long queryTime) {
        if (!doTrace) {
            return;
        }
        MetaStoreDirectSql.LOG.debug("Direct SQL query in " + (queryTime - start) / 1000000.0 + "ms + " + (System.nanoTime() - queryTime) / 1000000.0 + "ms, the query is [" + queryText + "]");
    }
    
    static Long extractSqlLong(final Object obj) throws MetaException {
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof Number)) {
            throw new MetaException("Expected numeric type but got " + obj.getClass().getName());
        }
        return ((Number)obj).longValue();
    }
    
    private static Boolean extractSqlBoolean(final Object value) throws MetaException {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        Character c = null;
        if (value instanceof String && ((String)value).length() == 1) {
            c = ((String)value).charAt(0);
        }
        if (c == null) {
            return null;
        }
        if (c == 'Y') {
            return true;
        }
        if (c == 'N') {
            return false;
        }
        throw new MetaException("Cannot extract boolean from column value " + value);
    }
    
    private int extractSqlInt(final Object field) {
        return ((Number)field).intValue();
    }
    
    private String extractSqlString(final Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
    
    static Double extractSqlDouble(final Object obj) throws MetaException {
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof Number)) {
            throw new MetaException("Expected numeric type but got " + obj.getClass().getName());
        }
        return ((Number)obj).doubleValue();
    }
    
    private static String trimCommaList(final StringBuilder sb) {
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    private <T> int loopJoinOrderedResult(final TreeMap<Long, T> tree, final String queryText, final int keyIndex, final ApplyFunc<T> func) throws MetaException {
        final boolean doTrace = MetaStoreDirectSql.LOG.isDebugEnabled();
        final long start = doTrace ? System.nanoTime() : 0L;
        final Query query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
        final Object result = query.execute();
        final long queryTime = doTrace ? System.nanoTime() : 0L;
        if (result == null) {
            query.closeAll();
            return 0;
        }
        final List<Object[]> list = this.ensureList(result);
        final Iterator<Object[]> iter = list.iterator();
        Object[] fields = null;
        for (final Map.Entry<Long, T> entry : tree.entrySet()) {
            if (fields == null && !iter.hasNext()) {
                break;
            }
            final long id = entry.getKey();
            while (fields != null || iter.hasNext()) {
                if (fields == null) {
                    fields = iter.next();
                }
                final long nestedId = extractSqlLong(fields[keyIndex]);
                if (nestedId < id) {
                    throw new MetaException("Found entries for unknown ID " + nestedId);
                }
                if (nestedId > id) {
                    break;
                }
                func.apply(entry.getValue(), fields);
                fields = null;
            }
            Deadline.checkTimeout();
        }
        final int rv = list.size();
        query.closeAll();
        this.timingTrace(doTrace, queryText, start, queryTime);
        return rv;
    }
    
    public ColumnStatistics getTableStats(final String dbName, final String tableName, final List<String> colNames) throws MetaException {
        if (colNames.isEmpty()) {
            return null;
        }
        final boolean doTrace = MetaStoreDirectSql.LOG.isDebugEnabled();
        final long start = doTrace ? System.nanoTime() : 0L;
        final String queryText = "select \"COLUMN_NAME\", \"COLUMN_TYPE\", \"LONG_LOW_VALUE\", \"LONG_HIGH_VALUE\", \"DOUBLE_LOW_VALUE\", \"DOUBLE_HIGH_VALUE\", \"BIG_DECIMAL_LOW_VALUE\", \"BIG_DECIMAL_HIGH_VALUE\", \"NUM_NULLS\", \"NUM_DISTINCTS\", \"AVG_COL_LEN\", \"MAX_COL_LEN\", \"NUM_TRUES\", \"NUM_FALSES\", \"LAST_ANALYZED\"  from \"TAB_COL_STATS\"  where \"DB_NAME\" = ? and \"TABLE_NAME\" = ? and \"COLUMN_NAME\" in (" + this.makeParams(colNames.size()) + ")";
        final Query query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
        final Object[] params = new Object[colNames.size() + 2];
        params[0] = dbName;
        params[1] = tableName;
        for (int i = 0; i < colNames.size(); ++i) {
            params[i + 2] = colNames.get(i);
        }
        final Object qResult = this.executeWithArray(query, params, queryText);
        final long queryTime = doTrace ? System.nanoTime() : 0L;
        if (qResult == null) {
            query.closeAll();
            return null;
        }
        final List<Object[]> list = this.ensureList(qResult);
        if (list.isEmpty()) {
            return null;
        }
        final ColumnStatisticsDesc csd = new ColumnStatisticsDesc(true, dbName, tableName);
        final ColumnStatistics result = this.makeColumnStats(list, csd, 0);
        this.timingTrace(doTrace, queryText, start, queryTime);
        query.closeAll();
        return result;
    }
    
    public AggrStats aggrColStatsForPartitions(final String dbName, final String tableName, final List<String> partNames, final List<String> colNames, final boolean useDensityFunctionForNDVEstimation) throws MetaException {
        if (colNames.isEmpty() || partNames.isEmpty()) {
            MetaStoreDirectSql.LOG.debug("Columns is empty or partNames is empty : Short-circuiting stats eval");
            return new AggrStats(new ArrayList<ColumnStatisticsObj>(), 0L);
        }
        final long partsFound = this.partsFoundForPartitions(dbName, tableName, partNames, colNames);
        List<ColumnStatisticsObj> colStatsList;
        if (this.isAggregateStatsCacheEnabled) {
            final int maxPartsPerCacheNode = this.aggrStatsCache.getMaxPartsPerCacheNode();
            final float fpp = this.aggrStatsCache.getFalsePositiveProbability();
            final int partitionsRequested = partNames.size();
            if (partitionsRequested > maxPartsPerCacheNode) {
                colStatsList = this.columnStatisticsObjForPartitions(dbName, tableName, partNames, colNames, partsFound, useDensityFunctionForNDVEstimation);
            }
            else {
                colStatsList = new ArrayList<ColumnStatisticsObj>();
                final BloomFilter bloomFilter = this.createPartsBloomFilter(maxPartsPerCacheNode, fpp, partNames);
                for (final String colName : colNames) {
                    final AggregateStatsCache.AggrColStats colStatsAggrCached = this.aggrStatsCache.get(dbName, tableName, colName, partNames);
                    if (colStatsAggrCached != null) {
                        colStatsList.add(colStatsAggrCached.getColStats());
                    }
                    else {
                        final List<String> colNamesForDB = new ArrayList<String>();
                        colNamesForDB.add(colName);
                        final List<ColumnStatisticsObj> colStatsAggrFromDB = this.columnStatisticsObjForPartitions(dbName, tableName, partNames, colNamesForDB, partsFound, useDensityFunctionForNDVEstimation);
                        if (colStatsAggrFromDB.isEmpty()) {
                            continue;
                        }
                        final ColumnStatisticsObj colStatsAggr = colStatsAggrFromDB.get(0);
                        colStatsList.add(colStatsAggr);
                        this.aggrStatsCache.add(dbName, tableName, colName, partsFound, colStatsAggr, bloomFilter);
                    }
                }
            }
        }
        else {
            colStatsList = this.columnStatisticsObjForPartitions(dbName, tableName, partNames, colNames, partsFound, useDensityFunctionForNDVEstimation);
        }
        MetaStoreDirectSql.LOG.info("useDensityFunctionForNDVEstimation = " + useDensityFunctionForNDVEstimation + "\npartsFound = " + partsFound + "\nColumnStatisticsObj = " + Arrays.toString(colStatsList.toArray()));
        return new AggrStats(colStatsList, partsFound);
    }
    
    private BloomFilter createPartsBloomFilter(final int maxPartsPerCacheNode, final float fpp, final List<String> partNames) {
        final BloomFilter bloomFilter = new BloomFilter(maxPartsPerCacheNode, fpp);
        for (final String partName : partNames) {
            bloomFilter.add(partName.getBytes());
        }
        return bloomFilter;
    }
    
    private long partsFoundForPartitions(final String dbName, final String tableName, final List<String> partNames, final List<String> colNames) throws MetaException {
        assert !colNames.isEmpty() && !partNames.isEmpty();
        long partsFound = 0L;
        final boolean doTrace = MetaStoreDirectSql.LOG.isDebugEnabled();
        final String queryText = "select count(\"COLUMN_NAME\") from \"PART_COL_STATS\" where \"DB_NAME\" = ? and \"TABLE_NAME\" = ?  and \"COLUMN_NAME\" in (" + this.makeParams(colNames.size()) + ")" + " and \"PARTITION_NAME\" in (" + this.makeParams(partNames.size()) + ")" + " group by \"PARTITION_NAME\"";
        final long start = doTrace ? System.nanoTime() : 0L;
        final Query query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
        final Object qResult = this.executeWithArray(query, this.prepareParams(dbName, tableName, partNames, colNames), queryText);
        final long end = doTrace ? System.nanoTime() : 0L;
        this.timingTrace(doTrace, queryText, start, end);
        final ForwardQueryResult fqr = (ForwardQueryResult)qResult;
        final Iterator<?> iter = (Iterator<?>)fqr.iterator();
        while (iter.hasNext()) {
            if (extractSqlLong(iter.next()) == colNames.size()) {
                ++partsFound;
            }
        }
        return partsFound;
    }
    
    private List<ColumnStatisticsObj> columnStatisticsObjForPartitions(final String dbName, final String tableName, final List<String> partNames, final List<String> colNames, final long partsFound, final boolean useDensityFunctionForNDVEstimation) throws MetaException {
        final String commonPrefix = "select \"COLUMN_NAME\", \"COLUMN_TYPE\", min(\"LONG_LOW_VALUE\"), max(\"LONG_HIGH_VALUE\"), min(\"DOUBLE_LOW_VALUE\"), max(\"DOUBLE_HIGH_VALUE\"), min(cast(\"BIG_DECIMAL_LOW_VALUE\" as decimal)), max(cast(\"BIG_DECIMAL_HIGH_VALUE\" as decimal)), sum(\"NUM_NULLS\"), max(\"NUM_DISTINCTS\"), max(\"AVG_COL_LEN\"), max(\"MAX_COL_LEN\"), sum(\"NUM_TRUES\"), sum(\"NUM_FALSES\"), avg((\"LONG_HIGH_VALUE\"-\"LONG_LOW_VALUE\")/cast(\"NUM_DISTINCTS\" as decimal)),avg((\"DOUBLE_HIGH_VALUE\"-\"DOUBLE_LOW_VALUE\")/\"NUM_DISTINCTS\"),avg((cast(\"BIG_DECIMAL_HIGH_VALUE\" as decimal)-cast(\"BIG_DECIMAL_LOW_VALUE\" as decimal))/\"NUM_DISTINCTS\"),sum(\"NUM_DISTINCTS\") from \"PART_COL_STATS\" where \"DB_NAME\" = ? and \"TABLE_NAME\" = ? ";
        String queryText = null;
        long start = 0L;
        long end = 0L;
        Query query = null;
        final boolean doTrace = MetaStoreDirectSql.LOG.isDebugEnabled();
        Object qResult = null;
        ForwardQueryResult fqr = null;
        if (partsFound == partNames.size()) {
            queryText = commonPrefix + " and \"COLUMN_NAME\" in (" + this.makeParams(colNames.size()) + ")" + " and \"PARTITION_NAME\" in (" + this.makeParams(partNames.size()) + ")" + " group by \"COLUMN_NAME\", \"COLUMN_TYPE\"";
            start = (doTrace ? System.nanoTime() : 0L);
            query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
            qResult = this.executeWithArray(query, this.prepareParams(dbName, tableName, partNames, colNames), queryText);
            if (qResult == null) {
                query.closeAll();
                return (List<ColumnStatisticsObj>)Lists.newArrayList();
            }
            end = (doTrace ? System.nanoTime() : 0L);
            this.timingTrace(doTrace, queryText, start, end);
            final List<Object[]> list = this.ensureList(qResult);
            final List<ColumnStatisticsObj> colStats = new ArrayList<ColumnStatisticsObj>(list.size());
            for (final Object[] row : list) {
                colStats.add(this.prepareCSObjWithAdjustedNDV(row, 0, useDensityFunctionForNDVEstimation));
                Deadline.checkTimeout();
            }
            query.closeAll();
            return colStats;
        }
        else {
            final List<ColumnStatisticsObj> colStats2 = new ArrayList<ColumnStatisticsObj>(colNames.size());
            queryText = "select \"COLUMN_NAME\", \"COLUMN_TYPE\", count(\"PARTITION_NAME\")  from \"PART_COL_STATS\" where \"DB_NAME\" = ? and \"TABLE_NAME\" = ?  and \"COLUMN_NAME\" in (" + this.makeParams(colNames.size()) + ")" + " and \"PARTITION_NAME\" in (" + this.makeParams(partNames.size()) + ")" + " group by \"COLUMN_NAME\", \"COLUMN_TYPE\"";
            start = (doTrace ? System.nanoTime() : 0L);
            query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
            qResult = this.executeWithArray(query, this.prepareParams(dbName, tableName, partNames, colNames), queryText);
            end = (doTrace ? System.nanoTime() : 0L);
            this.timingTrace(doTrace, queryText, start, end);
            if (qResult == null) {
                query.closeAll();
                return (List<ColumnStatisticsObj>)Lists.newArrayList();
            }
            final List<String> noExtraColumnNames = new ArrayList<String>();
            final Map<String, String[]> extraColumnNameTypeParts = new HashMap<String, String[]>();
            List<Object[]> list2 = this.ensureList(qResult);
            for (final Object[] row2 : list2) {
                final String colName = (String)row2[0];
                final String colType = (String)row2[1];
                final Long count = extractSqlLong(row2[2]);
                if (count == partNames.size() || count < 2L) {
                    noExtraColumnNames.add(colName);
                }
                else {
                    extraColumnNameTypeParts.put(colName, new String[] { colType, String.valueOf(count) });
                }
                Deadline.checkTimeout();
            }
            query.closeAll();
            if (noExtraColumnNames.size() != 0) {
                queryText = commonPrefix + " and \"COLUMN_NAME\" in (" + this.makeParams(noExtraColumnNames.size()) + ")" + " and \"PARTITION_NAME\" in (" + this.makeParams(partNames.size()) + ")" + " group by \"COLUMN_NAME\", \"COLUMN_TYPE\"";
                start = (doTrace ? System.nanoTime() : 0L);
                query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
                qResult = this.executeWithArray(query, this.prepareParams(dbName, tableName, partNames, noExtraColumnNames), queryText);
                if (qResult == null) {
                    query.closeAll();
                    return (List<ColumnStatisticsObj>)Lists.newArrayList();
                }
                list2 = this.ensureList(qResult);
                for (final Object[] row2 : list2) {
                    colStats2.add(this.prepareCSObjWithAdjustedNDV(row2, 0, useDensityFunctionForNDVEstimation));
                    Deadline.checkTimeout();
                }
                end = (doTrace ? System.nanoTime() : 0L);
                this.timingTrace(doTrace, queryText, start, end);
                query.closeAll();
            }
            if (extraColumnNameTypeParts.size() != 0) {
                final Map<String, Integer> indexMap = new HashMap<String, Integer>();
                for (int index = 0; index < partNames.size(); ++index) {
                    indexMap.put(partNames.get(index), index);
                }
                final Map<String, Map<Integer, Object>> sumMap = new HashMap<String, Map<Integer, Object>>();
                queryText = "select \"COLUMN_NAME\", sum(\"NUM_NULLS\"), sum(\"NUM_TRUES\"), sum(\"NUM_FALSES\"), sum(\"NUM_DISTINCTS\") from \"PART_COL_STATS\" where \"DB_NAME\" = ? and \"TABLE_NAME\" = ?  and \"COLUMN_NAME\" in (" + this.makeParams(extraColumnNameTypeParts.size()) + ")" + " and \"PARTITION_NAME\" in (" + this.makeParams(partNames.size()) + ")" + " group by \"COLUMN_NAME\"";
                start = (doTrace ? System.nanoTime() : 0L);
                query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
                final List<String> extraColumnNames = new ArrayList<String>();
                extraColumnNames.addAll(extraColumnNameTypeParts.keySet());
                qResult = this.executeWithArray(query, this.prepareParams(dbName, tableName, partNames, extraColumnNames), queryText);
                if (qResult == null) {
                    query.closeAll();
                    return (List<ColumnStatisticsObj>)Lists.newArrayList();
                }
                list2 = this.ensureList(qResult);
                final Integer[] sumIndex = { 6, 10, 11, 15 };
                for (final Object[] row3 : list2) {
                    final Map<Integer, Object> indexToObject = new HashMap<Integer, Object>();
                    for (int ind = 1; ind < row3.length; ++ind) {
                        indexToObject.put(sumIndex[ind - 1], row3[ind]);
                    }
                    sumMap.put((String)row3[0], indexToObject);
                    Deadline.checkTimeout();
                }
                end = (doTrace ? System.nanoTime() : 0L);
                this.timingTrace(doTrace, queryText, start, end);
                query.closeAll();
                for (final Map.Entry<String, String[]> entry : extraColumnNameTypeParts.entrySet()) {
                    final Object[] row4 = new Object[IExtrapolatePartStatus.colStatNames.length + 2];
                    final String colName2 = entry.getKey();
                    final String colType2 = entry.getValue()[0];
                    final Long sumVal = Long.parseLong(entry.getValue()[1]);
                    row4[0] = colName2;
                    row4[1] = colType2;
                    final IExtrapolatePartStatus extrapolateMethod = new LinearExtrapolatePartStatus();
                    Integer[] index2 = null;
                    boolean decimal = false;
                    if (colType2.toLowerCase().startsWith("decimal")) {
                        index2 = IExtrapolatePartStatus.indexMaps.get("decimal");
                        decimal = true;
                    }
                    else {
                        index2 = IExtrapolatePartStatus.indexMaps.get(colType2.toLowerCase());
                    }
                    if (index2 == null) {
                        index2 = IExtrapolatePartStatus.indexMaps.get("default");
                    }
                    for (final int colStatIndex : index2) {
                        final String colStatName = IExtrapolatePartStatus.colStatNames[colStatIndex];
                        if (IExtrapolatePartStatus.aggrTypes[colStatIndex] == IExtrapolatePartStatus.AggrType.Sum) {
                            final Object o = sumMap.get(colName2).get(colStatIndex);
                            if (o == null) {
                                row4[2 + colStatIndex] = null;
                            }
                            else {
                                final Long val = extractSqlLong(o);
                                row4[2 + colStatIndex] = val / sumVal * partNames.size();
                            }
                        }
                        else if (IExtrapolatePartStatus.aggrTypes[colStatIndex] == IExtrapolatePartStatus.AggrType.Min || IExtrapolatePartStatus.aggrTypes[colStatIndex] == IExtrapolatePartStatus.AggrType.Max) {
                            if (!decimal) {
                                queryText = "select \"" + colStatName + "\",\"PARTITION_NAME\" from \"PART_COL_STATS\"" + " where \"DB_NAME\" = ? and \"TABLE_NAME\" = ?" + " and \"COLUMN_NAME\" = ?" + " and \"PARTITION_NAME\" in (" + this.makeParams(partNames.size()) + ")" + " order by \"" + colStatName + "\"";
                            }
                            else {
                                queryText = "select \"" + colStatName + "\",\"PARTITION_NAME\" from \"PART_COL_STATS\"" + " where \"DB_NAME\" = ? and \"TABLE_NAME\" = ?" + " and \"COLUMN_NAME\" = ?" + " and \"PARTITION_NAME\" in (" + this.makeParams(partNames.size()) + ")" + " order by cast(\"" + colStatName + "\" as decimal)";
                            }
                            start = (doTrace ? System.nanoTime() : 0L);
                            query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
                            qResult = this.executeWithArray(query, this.prepareParams(dbName, tableName, partNames, Arrays.asList(colName2)), queryText);
                            if (qResult == null) {
                                query.closeAll();
                                return (List<ColumnStatisticsObj>)Lists.newArrayList();
                            }
                            fqr = (ForwardQueryResult)qResult;
                            final Object[] min = (Object[])fqr.get(0);
                            final Object[] max = (Object[])fqr.get(fqr.size() - 1);
                            end = (doTrace ? System.nanoTime() : 0L);
                            this.timingTrace(doTrace, queryText, start, end);
                            query.closeAll();
                            if (min[0] == null || max[0] == null) {
                                row4[2 + colStatIndex] = null;
                            }
                            else {
                                row4[2 + colStatIndex] = extrapolateMethod.extrapolate(min, max, colStatIndex, indexMap);
                            }
                        }
                        else {
                            queryText = "select avg((\"LONG_HIGH_VALUE\"-\"LONG_LOW_VALUE\")/cast(\"NUM_DISTINCTS\" as decimal)),avg((\"DOUBLE_HIGH_VALUE\"-\"DOUBLE_LOW_VALUE\")/\"NUM_DISTINCTS\"),avg((cast(\"BIG_DECIMAL_HIGH_VALUE\" as decimal)-cast(\"BIG_DECIMAL_LOW_VALUE\" as decimal))/\"NUM_DISTINCTS\") from \"PART_COL_STATS\" where \"DB_NAME\" = ? and \"TABLE_NAME\" = ? and \"COLUMN_NAME\" = ? and \"PARTITION_NAME\" in (" + this.makeParams(partNames.size()) + ")" + " group by \"COLUMN_NAME\"";
                            start = (doTrace ? System.nanoTime() : 0L);
                            query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
                            qResult = this.executeWithArray(query, this.prepareParams(dbName, tableName, partNames, Arrays.asList(colName2)), queryText);
                            if (qResult == null) {
                                query.closeAll();
                                return (List<ColumnStatisticsObj>)Lists.newArrayList();
                            }
                            fqr = (ForwardQueryResult)qResult;
                            final Object[] avg = (Object[])fqr.get(0);
                            row4[2 + colStatIndex] = avg[colStatIndex - 12];
                            end = (doTrace ? System.nanoTime() : 0L);
                            this.timingTrace(doTrace, queryText, start, end);
                            query.closeAll();
                        }
                    }
                    colStats2.add(this.prepareCSObjWithAdjustedNDV(row4, 0, useDensityFunctionForNDVEstimation));
                    Deadline.checkTimeout();
                }
            }
            return colStats2;
        }
    }
    
    private ColumnStatisticsObj prepareCSObj(final Object[] row, int i) throws MetaException {
        final ColumnStatisticsData data = new ColumnStatisticsData();
        final ColumnStatisticsObj cso = new ColumnStatisticsObj((String)row[i++], (String)row[i++], data);
        final Object llow = row[i++];
        final Object lhigh = row[i++];
        final Object dlow = row[i++];
        final Object dhigh = row[i++];
        final Object declow = row[i++];
        final Object dechigh = row[i++];
        final Object nulls = row[i++];
        final Object dist = row[i++];
        final Object avglen = row[i++];
        final Object maxlen = row[i++];
        final Object trues = row[i++];
        final Object falses = row[i++];
        StatObjectConverter.fillColumnStatisticsData(cso.getColType(), data, llow, lhigh, dlow, dhigh, declow, dechigh, nulls, dist, avglen, maxlen, trues, falses);
        return cso;
    }
    
    private ColumnStatisticsObj prepareCSObjWithAdjustedNDV(final Object[] row, int i, final boolean useDensityFunctionForNDVEstimation) throws MetaException {
        final ColumnStatisticsData data = new ColumnStatisticsData();
        final ColumnStatisticsObj cso = new ColumnStatisticsObj((String)row[i++], (String)row[i++], data);
        final Object llow = row[i++];
        final Object lhigh = row[i++];
        final Object dlow = row[i++];
        final Object dhigh = row[i++];
        final Object declow = row[i++];
        final Object dechigh = row[i++];
        final Object nulls = row[i++];
        final Object dist = row[i++];
        final Object avglen = row[i++];
        final Object maxlen = row[i++];
        final Object trues = row[i++];
        final Object falses = row[i++];
        final Object avgLong = row[i++];
        final Object avgDouble = row[i++];
        final Object avgDecimal = row[i++];
        final Object sumDist = row[i++];
        StatObjectConverter.fillColumnStatisticsData(cso.getColType(), data, llow, lhigh, dlow, dhigh, declow, dechigh, nulls, dist, avglen, maxlen, trues, falses, avgLong, avgDouble, avgDecimal, sumDist, useDensityFunctionForNDVEstimation);
        return cso;
    }
    
    private Object[] prepareParams(final String dbName, final String tableName, final List<String> partNames, final List<String> colNames) throws MetaException {
        final Object[] params = new Object[colNames.size() + partNames.size() + 2];
        int paramI = 0;
        params[paramI++] = dbName;
        params[paramI++] = tableName;
        for (final String colName : colNames) {
            params[paramI++] = colName;
        }
        for (final String partName : partNames) {
            params[paramI++] = partName;
        }
        return params;
    }
    
    public List<ColumnStatistics> getPartitionStats(final String dbName, final String tableName, final List<String> partNames, final List<String> colNames) throws MetaException {
        if (colNames.isEmpty() || partNames.isEmpty()) {
            return (List<ColumnStatistics>)Lists.newArrayList();
        }
        final boolean doTrace = MetaStoreDirectSql.LOG.isDebugEnabled();
        final long start = doTrace ? System.nanoTime() : 0L;
        final String queryText = "select \"PARTITION_NAME\", \"COLUMN_NAME\", \"COLUMN_TYPE\", \"LONG_LOW_VALUE\", \"LONG_HIGH_VALUE\", \"DOUBLE_LOW_VALUE\", \"DOUBLE_HIGH_VALUE\", \"BIG_DECIMAL_LOW_VALUE\", \"BIG_DECIMAL_HIGH_VALUE\", \"NUM_NULLS\", \"NUM_DISTINCTS\", \"AVG_COL_LEN\", \"MAX_COL_LEN\", \"NUM_TRUES\", \"NUM_FALSES\", \"LAST_ANALYZED\"  from \"PART_COL_STATS\" where \"DB_NAME\" = ? and \"TABLE_NAME\" = ? and \"COLUMN_NAME\" in (" + this.makeParams(colNames.size()) + ") AND \"PARTITION_NAME\" in (" + this.makeParams(partNames.size()) + ") order by \"PARTITION_NAME\"";
        final Query query = this.pm.newQuery("javax.jdo.query.SQL", queryText);
        final Object qResult = this.executeWithArray(query, this.prepareParams(dbName, tableName, partNames, colNames), queryText);
        final long queryTime = doTrace ? System.nanoTime() : 0L;
        if (qResult == null) {
            query.closeAll();
            return (List<ColumnStatistics>)Lists.newArrayList();
        }
        final List<Object[]> list = this.ensureList(qResult);
        final List<ColumnStatistics> result = new ArrayList<ColumnStatistics>(Math.min(list.size(), partNames.size()));
        String lastPartName = null;
        int from = 0;
        for (int i = 0; i <= list.size(); ++i) {
            final boolean isLast = i == list.size();
            final String partName = isLast ? null : ((String)list.get(i)[0]);
            if (isLast || !partName.equals(lastPartName)) {
                if (from != i) {
                    final ColumnStatisticsDesc csd = new ColumnStatisticsDesc(false, dbName, tableName);
                    csd.setPartName(lastPartName);
                    result.add(this.makeColumnStats(list.subList(from, i), csd, 1));
                }
                lastPartName = partName;
                from = i;
                Deadline.checkTimeout();
            }
        }
        this.timingTrace(doTrace, queryText, start, queryTime);
        query.closeAll();
        return result;
    }
    
    private ColumnStatistics makeColumnStats(final List<Object[]> list, final ColumnStatisticsDesc csd, final int offset) throws MetaException {
        final ColumnStatistics result = new ColumnStatistics();
        result.setStatsDesc(csd);
        final List<ColumnStatisticsObj> csos = new ArrayList<ColumnStatisticsObj>(list.size());
        for (final Object[] row : list) {
            final Object laObj = row[offset + 14];
            if (laObj != null && (!csd.isSetLastAnalyzed() || csd.getLastAnalyzed() > extractSqlLong(laObj))) {
                csd.setLastAnalyzed(extractSqlLong(laObj));
            }
            csos.add(this.prepareCSObj(row, offset));
            Deadline.checkTimeout();
        }
        result.setStatsObj(csos);
        return result;
    }
    
    private List<Object[]> ensureList(final Object result) throws MetaException {
        if (!(result instanceof List)) {
            throw new MetaException("Wrong result type " + result.getClass());
        }
        return (List<Object[]>)result;
    }
    
    private String makeParams(final int size) {
        return (size == 0) ? "" : StringUtils.repeat(",?", size).substring(1);
    }
    
    private <T> T executeWithArray(final Query query, final Object[] params, final String sql) throws MetaException {
        try {
            return (T)((params == null) ? query.execute() : query.executeWithArray(params));
        }
        catch (Exception ex) {
            String error = "Failed to execute [" + sql + "] with parameters [";
            if (params != null) {
                boolean isFirst = true;
                for (final Object param : params) {
                    error = error + (isFirst ? "" : ", ") + param;
                    isFirst = false;
                }
            }
            MetaStoreDirectSql.LOG.warn(error + "]", ex);
            throw new MetaException("See previous errors; " + ex.getMessage());
        }
    }
    
    static {
        LOG = LogFactory.getLog(MetaStoreDirectSql.class);
    }
    
    private enum DB
    {
        MYSQL, 
        ORACLE, 
        MSSQL, 
        DERBY, 
        OTHER;
    }
    
    private abstract class ApplyFunc<Target>
    {
        public abstract void apply(final Target p0, final Object[] p1) throws MetaException;
    }
    
    private static class PartitionFilterGenerator extends ExpressionTree.TreeVisitor
    {
        private final Table table;
        private final ExpressionTree.FilterBuilder filterBuffer;
        private final List<Object> params;
        private final List<String> joins;
        private final boolean dbHasJoinCastBug;
        
        private PartitionFilterGenerator(final Table table, final List<Object> params, final List<String> joins, final boolean dbHasJoinCastBug) {
            this.table = table;
            this.params = params;
            this.joins = joins;
            this.dbHasJoinCastBug = dbHasJoinCastBug;
            this.filterBuffer = new ExpressionTree.FilterBuilder(false);
        }
        
        private static String generateSqlFilter(final Table table, final ExpressionTree tree, final List<Object> params, final List<String> joins, final boolean dbHasJoinCastBug) throws MetaException {
            assert table != null;
            if (tree.getRoot() == null) {
                return "";
            }
            final PartitionFilterGenerator visitor = new PartitionFilterGenerator(table, params, joins, dbHasJoinCastBug);
            tree.accept(visitor);
            if (visitor.filterBuffer.hasError()) {
                MetaStoreDirectSql.LOG.info("Unable to push down SQL filter: " + visitor.filterBuffer.getErrorMessage());
                return null;
            }
            for (int i = 0; i < joins.size(); ++i) {
                if (joins.get(i) == null) {
                    joins.remove(i--);
                }
            }
            return "(" + visitor.filterBuffer.getFilter() + ")";
        }
        
        @Override
        protected void beginTreeNode(final ExpressionTree.TreeNode node) throws MetaException {
            this.filterBuffer.append(" (");
        }
        
        @Override
        protected void midTreeNode(final ExpressionTree.TreeNode node) throws MetaException {
            this.filterBuffer.append((node.getAndOr() == ExpressionTree.LogicalOperator.AND) ? " and " : " or ");
        }
        
        @Override
        protected void endTreeNode(final ExpressionTree.TreeNode node) throws MetaException {
            this.filterBuffer.append(") ");
        }
        
        @Override
        protected boolean shouldStop() {
            return this.filterBuffer.hasError();
        }
        
        public void visit(final ExpressionTree.LeafNode node) throws MetaException {
            if (node.operator == ExpressionTree.Operator.LIKE) {
                this.filterBuffer.setError("LIKE is not supported for SQL filter pushdown");
                return;
            }
            final int partColCount = this.table.getPartitionKeys().size();
            final int partColIndex = node.getPartColIndexForFilter(this.table, this.filterBuffer);
            if (this.filterBuffer.hasError()) {
                return;
            }
            final String colTypeStr = this.table.getPartitionKeys().get(partColIndex).getType();
            final FilterType colType = FilterType.fromType(colTypeStr);
            if (colType == FilterType.Invalid) {
                this.filterBuffer.setError("Filter pushdown not supported for type " + colTypeStr);
                return;
            }
            FilterType valType = FilterType.fromClass(node.value);
            Object nodeValue = node.value;
            if (valType == FilterType.Invalid) {
                this.filterBuffer.setError("Filter pushdown not supported for value " + node.value.getClass());
                return;
            }
            if (colType == FilterType.Date && valType == FilterType.String) {
                try {
                    nodeValue = new Date(HiveMetaStore.PARTITION_DATE_FORMAT.get().parse((String)nodeValue).getTime());
                    valType = FilterType.Date;
                }
                catch (ParseException ex) {}
            }
            if (colType != valType) {
                this.filterBuffer.setError("Cannot push down filter for " + colTypeStr + " column and value " + nodeValue.getClass());
                return;
            }
            if (this.joins.isEmpty()) {
                for (int i = 0; i < partColCount; ++i) {
                    this.joins.add(null);
                }
            }
            if (this.joins.get(partColIndex) == null) {
                this.joins.set(partColIndex, "inner join \"PARTITION_KEY_VALS\" \"FILTER" + partColIndex + "\" on \"FILTER" + partColIndex + "\".\"PART_ID\" = \"PARTITIONS\".\"PART_ID\"" + " and \"FILTER" + partColIndex + "\".\"INTEGER_IDX\" = " + partColIndex);
            }
            String tableValue = "\"FILTER" + partColIndex + "\".\"PART_KEY_VAL\"";
            if (node.isReverseOrder) {
                this.params.add(nodeValue);
            }
            if (colType != FilterType.String) {
                if (colType == FilterType.Integral) {
                    tableValue = "cast(" + tableValue + " as decimal(21,0))";
                }
                else if (colType == FilterType.Date) {
                    tableValue = "cast(" + tableValue + " as date)";
                }
                if (this.dbHasJoinCastBug) {
                    tableValue = "(case when \"TBLS\".\"TBL_NAME\" = ? and \"DBS\".\"NAME\" = ? and \"FILTER" + partColIndex + "\".\"PART_ID\" = \"PARTITIONS\".\"PART_ID\" and " + "\"FILTER" + partColIndex + "\".\"INTEGER_IDX\" = " + partColIndex + " then " + tableValue + " else null end)";
                    this.params.add(this.table.getTableName().toLowerCase());
                    this.params.add(this.table.getDbName().toLowerCase());
                }
            }
            if (!node.isReverseOrder) {
                this.params.add(nodeValue);
            }
            this.filterBuffer.append(node.isReverseOrder ? ("(? " + node.operator.getSqlOp() + " " + tableValue + ")") : ("(" + tableValue + " " + node.operator.getSqlOp() + " ?)"));
        }
        
        private enum FilterType
        {
            Integral, 
            String, 
            Date, 
            Invalid;
            
            static FilterType fromType(final String colTypeStr) {
                if (colTypeStr.equals("string")) {
                    return FilterType.String;
                }
                if (colTypeStr.equals("date")) {
                    return FilterType.Date;
                }
                if (serdeConstants.IntegralTypes.contains(colTypeStr)) {
                    return FilterType.Integral;
                }
                return FilterType.Invalid;
            }
            
            public static FilterType fromClass(final Object value) {
                if (value instanceof String) {
                    return FilterType.String;
                }
                if (value instanceof Long) {
                    return FilterType.Integral;
                }
                if (value instanceof Date) {
                    return FilterType.Date;
                }
                return FilterType.Invalid;
            }
        }
    }
}
