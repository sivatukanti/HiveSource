// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import javax.annotation.Nullable;
import java.util.HashSet;
import org.apache.commons.logging.LogFactory;
import java.util.Arrays;
import java.net.URLClassLoader;
import java.net.URL;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import org.apache.hadoop.hive.shims.ShimLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.hive.common.JavaUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.apache.hadoop.hive.thrift.HadoopThriftAuthBridge;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.fs.Path;
import java.util.Properties;
import org.apache.hadoop.hive.serde2.SerDeUtils;
import org.apache.hive.common.util.ReflectionUtil;
import org.apache.hadoop.hive.serde2.Deserializer;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecProxy;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.common.StatsSetupConst;
import java.io.IOException;
import java.io.File;
import java.util.Iterator;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import java.util.ArrayList;
import java.util.Map;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.conf.Configuration;
import java.util.List;
import com.google.common.base.Function;
import org.apache.hadoop.fs.PathFilter;
import java.util.Set;
import java.util.HashMap;
import org.apache.commons.logging.Log;

public class MetaStoreUtils
{
    protected static final Log LOG;
    public static final String DEFAULT_DATABASE_NAME = "default";
    public static final String DEFAULT_DATABASE_COMMENT = "Default Hive database";
    public static final String DATABASE_WAREHOUSE_SUFFIX = ".db";
    static HashMap<String, String> typeToThriftTypeMap;
    static Set<String> hiveThriftTypeMap;
    private static final String FROM_SERIALIZER = "from deserializer";
    private static final PathFilter hiddenFileFilter;
    public static String ARCHIVING_LEVEL;
    private static final Function<String, String> transFormNullsToEmptyString;
    
    public static Table createColumnsetSchema(final String name, final List<String> columns, final List<String> partCols, final Configuration conf) throws MetaException {
        if (columns == null) {
            throw new MetaException("columns not specified for table " + name);
        }
        final Table tTable = new Table();
        tTable.setTableName(name);
        tTable.setSd(new StorageDescriptor());
        final StorageDescriptor sd = tTable.getSd();
        sd.setSerdeInfo(new SerDeInfo());
        final SerDeInfo serdeInfo = sd.getSerdeInfo();
        serdeInfo.setSerializationLib(LazySimpleSerDe.class.getName());
        serdeInfo.setParameters(new HashMap<String, String>());
        serdeInfo.getParameters().put("serialization.format", "1");
        final List<FieldSchema> fields = new ArrayList<FieldSchema>();
        sd.setCols(fields);
        for (final String col : columns) {
            final FieldSchema field = new FieldSchema(col, "string", "'default'");
            fields.add(field);
        }
        tTable.setPartitionKeys(new ArrayList<FieldSchema>());
        for (final String partCol : partCols) {
            final FieldSchema part = new FieldSchema();
            part.setName(partCol);
            part.setType("string");
            tTable.getPartitionKeys().add(part);
        }
        sd.setNumBuckets(-1);
        return tTable;
    }
    
    public static void recursiveDelete(final File f) throws IOException {
        if (f.isDirectory()) {
            final File[] listFiles;
            final File[] fs = listFiles = f.listFiles();
            for (final File subf : listFiles) {
                recursiveDelete(subf);
            }
        }
        if (!f.delete()) {
            throw new IOException("could not delete: " + f.getPath());
        }
    }
    
    public static boolean containsAllFastStats(final Map<String, String> partParams) {
        for (final String stat : StatsSetupConst.fastStats) {
            if (!partParams.containsKey(stat)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean updateUnpartitionedTableStatsFast(final Database db, final Table tbl, final Warehouse wh, final boolean madeDir) throws MetaException {
        return updateUnpartitionedTableStatsFast(db, tbl, wh, madeDir, false);
    }
    
    public static boolean updateUnpartitionedTableStatsFast(final Database db, final Table tbl, final Warehouse wh, final boolean madeDir, final boolean forceRecompute) throws MetaException {
        return updateUnpartitionedTableStatsFast(tbl, wh.getFileStatusesForUnpartitionedTable(db, tbl), madeDir, forceRecompute);
    }
    
    public static boolean updateUnpartitionedTableStatsFast(final Table tbl, final FileStatus[] fileStatus, final boolean newDir, final boolean forceRecompute) throws MetaException {
        Map<String, String> params = tbl.getParameters();
        if (params != null && params.containsKey("DO_NOT_UPDATE_STATS")) {
            final boolean doNotUpdateStats = Boolean.valueOf(params.get("DO_NOT_UPDATE_STATS"));
            params.remove("DO_NOT_UPDATE_STATS");
            tbl.setParameters(params);
            if (doNotUpdateStats) {
                return false;
            }
        }
        boolean updated = false;
        if (forceRecompute || params == null || !containsAllFastStats(params)) {
            if (params == null) {
                params = new HashMap<String, String>();
            }
            if (!newDir) {
                MetaStoreUtils.LOG.info("Updating table stats fast for " + tbl.getTableName());
                populateQuickStats(fileStatus, params);
                MetaStoreUtils.LOG.info("Updated size of table " + tbl.getTableName() + " to " + params.get("totalSize"));
                if (!params.containsKey("STATS_GENERATED_VIA_STATS_TASK")) {
                    for (final String stat : StatsSetupConst.statsRequireCompute) {
                        params.put(stat, "-1");
                    }
                    params.put("COLUMN_STATS_ACCURATE", "false");
                }
                else {
                    params.remove("STATS_GENERATED_VIA_STATS_TASK");
                    params.put("COLUMN_STATS_ACCURATE", "true");
                }
            }
            tbl.setParameters(params);
            updated = true;
        }
        return updated;
    }
    
    public static void populateQuickStats(final FileStatus[] fileStatus, final Map<String, String> params) {
        int numFiles = 0;
        long tableSize = 0L;
        for (final FileStatus status : fileStatus) {
            if (!status.isDir()) {
                tableSize += status.getLen();
                ++numFiles;
            }
        }
        params.put("numFiles", Integer.toString(numFiles));
        params.put("totalSize", Long.toString(tableSize));
    }
    
    public static boolean requireCalStats(final Configuration hiveConf, final Partition oldPart, final Partition newPart, final Table tbl) {
        if (isView(tbl)) {
            return false;
        }
        if (oldPart == null && newPart == null) {
            return true;
        }
        if (newPart == null || newPart.getParameters() == null || !containsAllFastStats(newPart.getParameters())) {
            return true;
        }
        if (newPart.getParameters().containsKey("STATS_GENERATED_VIA_STATS_TASK")) {
            return true;
        }
        if (oldPart != null && oldPart.getParameters() != null) {
            for (final String stat : StatsSetupConst.fastStats) {
                if (oldPart.getParameters().containsKey(stat)) {
                    final Long oldStat = Long.parseLong(oldPart.getParameters().get(stat));
                    final Long newStat = Long.parseLong(newPart.getParameters().get(stat));
                    if (!oldStat.equals(newStat)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean updatePartitionStatsFast(final Partition part, final Warehouse wh) throws MetaException {
        return updatePartitionStatsFast(part, wh, false, false);
    }
    
    public static boolean updatePartitionStatsFast(final Partition part, final Warehouse wh, final boolean madeDir) throws MetaException {
        return updatePartitionStatsFast(part, wh, madeDir, false);
    }
    
    public static boolean updatePartitionStatsFast(final Partition part, final Warehouse wh, final boolean madeDir, final boolean forceRecompute) throws MetaException {
        return updatePartitionStatsFast(new PartitionSpecProxy.SimplePartitionWrapperIterator(part), wh, madeDir, forceRecompute);
    }
    
    public static boolean updatePartitionStatsFast(final PartitionSpecProxy.PartitionIterator part, final Warehouse wh, final boolean madeDir, final boolean forceRecompute) throws MetaException {
        Map<String, String> params = part.getParameters();
        boolean updated = false;
        if (forceRecompute || params == null || !containsAllFastStats(params)) {
            if (params == null) {
                params = new HashMap<String, String>();
            }
            if (!madeDir) {
                MetaStoreUtils.LOG.warn("Updating partition stats fast for: " + part.getTableName());
                final FileStatus[] fileStatus = wh.getFileStatusesForLocation(part.getLocation());
                populateQuickStats(fileStatus, params);
                MetaStoreUtils.LOG.warn("Updated size to " + params.get("totalSize"));
                if (!params.containsKey("STATS_GENERATED_VIA_STATS_TASK")) {
                    for (final String stat : StatsSetupConst.statsRequireCompute) {
                        params.put(stat, "-1");
                    }
                    params.put("COLUMN_STATS_ACCURATE", "false");
                }
                else {
                    params.remove("STATS_GENERATED_VIA_STATS_TASK");
                    params.put("COLUMN_STATS_ACCURATE", "true");
                }
            }
            part.setParameters(params);
            updated = true;
        }
        return updated;
    }
    
    public static Deserializer getDeserializer(final Configuration conf, final Table table, final boolean skipConfError) throws MetaException {
        final String lib = table.getSd().getSerdeInfo().getSerializationLib();
        if (lib == null) {
            return null;
        }
        try {
            final Deserializer deserializer = ReflectionUtil.newInstance(conf.getClassByName(lib).asSubclass(Deserializer.class), conf);
            if (skipConfError) {
                SerDeUtils.initializeSerDeWithoutErrorCheck(deserializer, conf, getTableMetadata(table), null);
            }
            else {
                SerDeUtils.initializeSerDe(deserializer, conf, getTableMetadata(table), null);
            }
            return deserializer;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            MetaStoreUtils.LOG.error("error in initSerDe: " + e2.getClass().getName() + " " + e2.getMessage(), e2);
            throw new MetaException(e2.getClass().getName() + " " + e2.getMessage());
        }
    }
    
    public static Class<? extends Deserializer> getDeserializerClass(final Configuration conf, final Table table) throws Exception {
        final String lib = table.getSd().getSerdeInfo().getSerializationLib();
        return (lib == null) ? null : conf.getClassByName(lib).asSubclass(Deserializer.class);
    }
    
    public static Deserializer getDeserializer(final Configuration conf, final Partition part, final Table table) throws MetaException {
        final String lib = part.getSd().getSerdeInfo().getSerializationLib();
        try {
            final Deserializer deserializer = ReflectionUtil.newInstance(conf.getClassByName(lib).asSubclass(Deserializer.class), conf);
            SerDeUtils.initializeSerDe(deserializer, conf, getTableMetadata(table), getPartitionMetadata(part, table));
            return deserializer;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            MetaStoreUtils.LOG.error("error in initSerDe: " + e2.getClass().getName() + " " + e2.getMessage(), e2);
            throw new MetaException(e2.getClass().getName() + " " + e2.getMessage());
        }
    }
    
    public static void deleteWHDirectory(final Path path, final Configuration conf, final boolean use_trash) throws MetaException {
        try {
            if (!path.getFileSystem(conf).exists(path)) {
                MetaStoreUtils.LOG.warn("drop data called on table/partition with no directory: " + path);
                return;
            }
            if (use_trash) {
                int count = 0;
                Path newPath = new Path("/Trash/Current" + path.getParent().toUri().getPath());
                if (!path.getFileSystem(conf).exists(newPath)) {
                    path.getFileSystem(conf).mkdirs(newPath);
                }
                do {
                    newPath = new Path("/Trash/Current" + path.toUri().getPath() + "." + count);
                    if (path.getFileSystem(conf).exists(newPath)) {
                        ++count;
                    }
                    else {
                        if (path.getFileSystem(conf).rename(path, newPath)) {
                            break;
                        }
                        continue;
                    }
                } while (++count < 50);
                if (count >= 50) {
                    throw new MetaException("Rename failed due to maxing out retries");
                }
            }
            else {
                path.getFileSystem(conf).delete(path, true);
            }
        }
        catch (IOException e) {
            MetaStoreUtils.LOG.error("Got exception trying to delete data dir: " + e);
            throw new MetaException(e.getMessage());
        }
        catch (MetaException e2) {
            MetaStoreUtils.LOG.error("Got exception trying to delete data dir: " + e2);
            throw e2;
        }
    }
    
    public static List<String> getPvals(final List<FieldSchema> partCols, final Map<String, String> partSpec) {
        final List<String> pvals = new ArrayList<String>();
        for (final FieldSchema field : partCols) {
            String val = partSpec.get(field.getName());
            if (val == null) {
                val = "";
            }
            pvals.add(val);
        }
        return pvals;
    }
    
    public static boolean validateName(final String name) {
        final Pattern tpat = Pattern.compile("[\\w_]+");
        final Matcher m = tpat.matcher(name);
        return m.matches();
    }
    
    public static final boolean validateColumnName(final String name) {
        return true;
    }
    
    public static String validateTblColumns(final List<FieldSchema> cols) {
        for (final FieldSchema fieldSchema : cols) {
            if (!validateColumnName(fieldSchema.getName())) {
                return "name: " + fieldSchema.getName();
            }
            if (!validateColumnType(fieldSchema.getType())) {
                return "type: " + fieldSchema.getType();
            }
        }
        return null;
    }
    
    static void throwExceptionIfIncompatibleColTypeChange(final List<FieldSchema> oldCols, final List<FieldSchema> newCols) throws InvalidOperationException {
        final List<String> incompatibleCols = new ArrayList<String>();
        for (int maxCols = Math.min(oldCols.size(), newCols.size()), i = 0; i < maxCols; ++i) {
            if (!areColTypesCompatible(oldCols.get(i).getType(), newCols.get(i).getType())) {
                incompatibleCols.add(newCols.get(i).getName());
            }
        }
        if (!incompatibleCols.isEmpty()) {
            throw new InvalidOperationException("The following columns have types incompatible with the existing columns in their respective positions :\n" + StringUtils.join(incompatibleCols, ','));
        }
    }
    
    static boolean isCascadeNeededInAlterTable(final Table oldTable, final Table newTable) {
        final List<FieldSchema> oldCols = oldTable.getSd().getCols();
        final List<FieldSchema> newCols = newTable.getSd().getCols();
        return !areSameColumns(oldCols, newCols);
    }
    
    static boolean areSameColumns(final List<FieldSchema> oldCols, final List<FieldSchema> newCols) {
        if (oldCols.size() != newCols.size()) {
            return false;
        }
        for (int i = 0; i < oldCols.size(); ++i) {
            final FieldSchema oldCol = oldCols.get(i);
            final FieldSchema newCol = newCols.get(i);
            if (!oldCol.equals(newCol)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean areColTypesCompatible(final String oldType, final String newType) {
        return oldType.equals(newType) || (serdeConstants.PrimitiveTypes.contains(oldType.toLowerCase()) && serdeConstants.PrimitiveTypes.contains(newType.toLowerCase()));
    }
    
    public static boolean validateColumnType(final String type) {
        int last = 0;
        final boolean lastAlphaDigit = isValidTypeChar(type.charAt(last));
        int i = 1;
        while (i <= type.length()) {
            if (i == type.length() || isValidTypeChar(type.charAt(i)) != lastAlphaDigit) {
                final String token = type.substring(last, i);
                last = i;
                if (!MetaStoreUtils.hiveThriftTypeMap.contains(token)) {
                    return false;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return true;
    }
    
    private static boolean isValidTypeChar(final char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }
    
    public static String validateSkewedColNames(final List<String> cols) {
        if (null == cols) {
            return null;
        }
        for (final String col : cols) {
            if (!validateColumnName(col)) {
                return col;
            }
        }
        return null;
    }
    
    public static String validateSkewedColNamesSubsetCol(final List<String> skewedColNames, final List<FieldSchema> cols) {
        if (null == skewedColNames) {
            return null;
        }
        final List<String> colNames = new ArrayList<String>();
        for (final FieldSchema fieldSchema : cols) {
            colNames.add(fieldSchema.getName());
        }
        final List<String> copySkewedColNames = new ArrayList<String>(skewedColNames);
        copySkewedColNames.removeAll(colNames);
        if (copySkewedColNames.isEmpty()) {
            return null;
        }
        return copySkewedColNames.toString();
    }
    
    public static String getListType(final String t) {
        return "array<" + t + ">";
    }
    
    public static String getMapType(final String k, final String v) {
        return "map<" + k + "," + v + ">";
    }
    
    public static void setSerdeParam(final SerDeInfo sdi, final Properties schema, final String param) {
        final String val = schema.getProperty(param);
        if (StringUtils.isNotBlank(val)) {
            sdi.getParameters().put(param, val);
        }
    }
    
    public static String typeToThriftType(final String type) {
        final StringBuilder thriftType = new StringBuilder();
        int last = 0;
        boolean lastAlphaDigit = Character.isLetterOrDigit(type.charAt(last));
        for (int i = 1; i <= type.length(); ++i) {
            if (i == type.length() || Character.isLetterOrDigit(type.charAt(i)) != lastAlphaDigit) {
                final String token = type.substring(last, i);
                last = i;
                final String thriftToken = MetaStoreUtils.typeToThriftTypeMap.get(token);
                thriftType.append((thriftToken == null) ? token : thriftToken);
                lastAlphaDigit = !lastAlphaDigit;
            }
        }
        return thriftType.toString();
    }
    
    public static String getFullDDLFromFieldSchema(final String structName, final List<FieldSchema> fieldSchemas) {
        final StringBuilder ddl = new StringBuilder();
        ddl.append(getDDLFromFieldSchema(structName, fieldSchemas));
        ddl.append('#');
        final StringBuilder colnames = new StringBuilder();
        final StringBuilder coltypes = new StringBuilder();
        boolean first = true;
        for (final FieldSchema col : fieldSchemas) {
            if (first) {
                first = false;
            }
            else {
                colnames.append(',');
                coltypes.append(':');
            }
            colnames.append(col.getName());
            coltypes.append(col.getType());
        }
        ddl.append((CharSequence)colnames);
        ddl.append('#');
        ddl.append((CharSequence)coltypes);
        return ddl.toString();
    }
    
    public static String getDDLFromFieldSchema(final String structName, final List<FieldSchema> fieldSchemas) {
        final StringBuilder ddl = new StringBuilder();
        ddl.append("struct ");
        ddl.append(structName);
        ddl.append(" { ");
        boolean first = true;
        for (final FieldSchema col : fieldSchemas) {
            if (first) {
                first = false;
            }
            else {
                ddl.append(", ");
            }
            ddl.append(typeToThriftType(col.getType()));
            ddl.append(' ');
            ddl.append(col.getName());
        }
        ddl.append("}");
        MetaStoreUtils.LOG.debug("DDL: " + (Object)ddl);
        return ddl.toString();
    }
    
    public static Properties getTableMetadata(final Table table) {
        return getSchema(table.getSd(), table.getSd(), table.getParameters(), table.getDbName(), table.getTableName(), table.getPartitionKeys());
    }
    
    public static Properties getPartitionMetadata(final Partition partition, final Table table) {
        return getSchema(partition.getSd(), partition.getSd(), partition.getParameters(), table.getDbName(), table.getTableName(), table.getPartitionKeys());
    }
    
    public static Properties getSchema(final Partition part, final Table table) {
        return getSchema(part.getSd(), table.getSd(), table.getParameters(), table.getDbName(), table.getTableName(), table.getPartitionKeys());
    }
    
    public static Properties getPartSchemaFromTableSchema(final StorageDescriptor sd, final StorageDescriptor tblsd, final Map<String, String> parameters, final String databaseName, final String tableName, final List<FieldSchema> partitionKeys, final Properties tblSchema) {
        final Properties schema = (Properties)tblSchema.clone();
        String inputFormat = sd.getInputFormat();
        if (inputFormat == null || inputFormat.length() == 0) {
            final String tblInput = schema.getProperty("file.inputformat");
            if (tblInput == null) {
                inputFormat = SequenceFileInputFormat.class.getName();
            }
            else {
                inputFormat = tblInput;
            }
        }
        schema.setProperty("file.inputformat", inputFormat);
        String outputFormat = sd.getOutputFormat();
        if (outputFormat == null || outputFormat.length() == 0) {
            final String tblOutput = schema.getProperty("file.outputformat");
            if (tblOutput == null) {
                outputFormat = SequenceFileOutputFormat.class.getName();
            }
            else {
                outputFormat = tblOutput;
            }
        }
        schema.setProperty("file.outputformat", outputFormat);
        if (sd.getLocation() != null) {
            schema.setProperty("location", sd.getLocation());
        }
        schema.setProperty("bucket_count", Integer.toString(sd.getNumBuckets()));
        if (sd.getBucketCols() != null && sd.getBucketCols().size() > 0) {
            schema.setProperty("bucket_field_name", sd.getBucketCols().get(0));
        }
        if (sd.getSerdeInfo() != null) {
            final String cols = "columns";
            final String colTypes = "columns.types";
            final String parts = "partition_columns";
            for (final Map.Entry<String, String> param : sd.getSerdeInfo().getParameters().entrySet()) {
                final String key = param.getKey();
                if (schema.get(key) != null) {
                    if (key.equals(cols) || key.equals(colTypes)) {
                        continue;
                    }
                    if (key.equals(parts)) {
                        continue;
                    }
                }
                schema.put(key, (param.getValue() != null) ? param.getValue() : "");
            }
            if (sd.getSerdeInfo().getSerializationLib() != null) {
                schema.setProperty("serialization.lib", sd.getSerdeInfo().getSerializationLib());
            }
        }
        if (parameters != null) {
            for (final Map.Entry<String, String> e : parameters.entrySet()) {
                schema.setProperty(e.getKey(), e.getValue());
            }
        }
        return schema;
    }
    
    public static Properties getSchema(final StorageDescriptor sd, final StorageDescriptor tblsd, final Map<String, String> parameters, final String databaseName, final String tableName, final List<FieldSchema> partitionKeys) {
        final Properties schema = new Properties();
        String inputFormat = sd.getInputFormat();
        if (inputFormat == null || inputFormat.length() == 0) {
            inputFormat = SequenceFileInputFormat.class.getName();
        }
        schema.setProperty("file.inputformat", inputFormat);
        String outputFormat = sd.getOutputFormat();
        if (outputFormat == null || outputFormat.length() == 0) {
            outputFormat = SequenceFileOutputFormat.class.getName();
        }
        schema.setProperty("file.outputformat", outputFormat);
        schema.setProperty("name", databaseName + "." + tableName);
        if (sd.getLocation() != null) {
            schema.setProperty("location", sd.getLocation());
        }
        schema.setProperty("bucket_count", Integer.toString(sd.getNumBuckets()));
        if (sd.getBucketCols() != null && sd.getBucketCols().size() > 0) {
            schema.setProperty("bucket_field_name", sd.getBucketCols().get(0));
        }
        if (sd.getSerdeInfo() != null) {
            for (final Map.Entry<String, String> param : sd.getSerdeInfo().getParameters().entrySet()) {
                schema.put(param.getKey(), (param.getValue() != null) ? param.getValue() : "");
            }
            if (sd.getSerdeInfo().getSerializationLib() != null) {
                schema.setProperty("serialization.lib", sd.getSerdeInfo().getSerializationLib());
            }
        }
        final StringBuilder colNameBuf = new StringBuilder();
        final StringBuilder colTypeBuf = new StringBuilder();
        final StringBuilder colComment = new StringBuilder();
        boolean first = true;
        for (final FieldSchema col : tblsd.getCols()) {
            if (!first) {
                colNameBuf.append(",");
                colTypeBuf.append(":");
                colComment.append('\0');
            }
            colNameBuf.append(col.getName());
            colTypeBuf.append(col.getType());
            colComment.append((null != col.getComment()) ? col.getComment() : "");
            first = false;
        }
        final String colNames = colNameBuf.toString();
        final String colTypes = colTypeBuf.toString();
        schema.setProperty("columns", colNames);
        schema.setProperty("columns.types", colTypes);
        schema.setProperty("columns.comments", colComment.toString());
        if (sd.getCols() != null) {
            schema.setProperty("serialization.ddl", getDDLFromFieldSchema(tableName, sd.getCols()));
        }
        String partString = "";
        String partStringSep = "";
        String partTypesString = "";
        String partTypesStringSep = "";
        for (final FieldSchema partKey : partitionKeys) {
            partString = partString.concat(partStringSep);
            partString = partString.concat(partKey.getName());
            partTypesString = partTypesString.concat(partTypesStringSep);
            partTypesString = partTypesString.concat(partKey.getType());
            if (partStringSep.length() == 0) {
                partStringSep = "/";
                partTypesStringSep = ":";
            }
        }
        if (partString.length() > 0) {
            schema.setProperty("partition_columns", partString);
            schema.setProperty("partition_columns.types", partTypesString);
        }
        if (parameters != null) {
            for (final Map.Entry<String, String> e : parameters.entrySet()) {
                if (e.getValue() != null) {
                    schema.setProperty(e.getKey(), e.getValue());
                }
            }
        }
        return schema;
    }
    
    public static String getColumnNamesFromFieldSchema(final List<FieldSchema> fieldSchemas) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldSchemas.size(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(fieldSchemas.get(i).getName());
        }
        return sb.toString();
    }
    
    public static String getColumnTypesFromFieldSchema(final List<FieldSchema> fieldSchemas) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldSchemas.size(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(fieldSchemas.get(i).getType());
        }
        return sb.toString();
    }
    
    public static String getColumnCommentsFromFieldSchema(final List<FieldSchema> fieldSchemas) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldSchemas.size(); ++i) {
            if (i > 0) {
                sb.append('\0');
            }
            sb.append(fieldSchemas.get(i).getComment());
        }
        return sb.toString();
    }
    
    public static void makeDir(final Path path, final HiveConf hiveConf) throws MetaException {
        try {
            final FileSystem fs = path.getFileSystem(hiveConf);
            if (!fs.exists(path)) {
                fs.mkdirs(path);
            }
        }
        catch (IOException e) {
            throw new MetaException("Unable to : " + path);
        }
    }
    
    public static void startMetaStore(final int port, final HadoopThriftAuthBridge bridge) throws Exception {
        startMetaStore(port, bridge, new HiveConf(HiveMetaStore.HMSHandler.class));
    }
    
    public static void startMetaStore(final int port, final HadoopThriftAuthBridge bridge, final HiveConf hiveConf) throws Exception {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HiveMetaStore.startMetaStore(port, bridge, hiveConf);
                }
                catch (Throwable e) {
                    MetaStoreUtils.LOG.error("Metastore Thrift Server threw an exception...", e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        loopUntilHMSReady(port);
    }
    
    private static void loopUntilHMSReady(final int port) throws Exception {
        int retries = 0;
        Exception exc = null;
        while (true) {
            try {
                final Socket socket = new Socket();
                socket.connect(new InetSocketAddress(port), 5000);
                socket.close();
            }
            catch (Exception e) {
                if (retries++ > 6) {
                    exc = e;
                    throw exc;
                }
                Thread.sleep(10000L);
                continue;
            }
            break;
        }
    }
    
    public static int findFreePort() throws IOException {
        final ServerSocket socket = new ServerSocket(0);
        final int port = socket.getLocalPort();
        socket.close();
        return port;
    }
    
    static void logAndThrowMetaException(final Exception e) throws MetaException {
        final String exInfo = "Got exception: " + e.getClass().getName() + " " + e.getMessage();
        MetaStoreUtils.LOG.error(exInfo, e);
        MetaStoreUtils.LOG.error("Converting exception to MetaException");
        throw new MetaException(exInfo);
    }
    
    public static List<FieldSchema> getFieldsFromDeserializer(final String tableName, final Deserializer deserializer) throws SerDeException, MetaException {
        ObjectInspector oi = deserializer.getObjectInspector();
        final String[] names = tableName.split("\\.");
        final String last_name = names[names.length - 1];
        for (int i = 1; i < names.length; ++i) {
            if (oi instanceof StructObjectInspector) {
                final StructObjectInspector soi = (StructObjectInspector)oi;
                final StructField sf = soi.getStructFieldRef(names[i]);
                if (sf == null) {
                    throw new MetaException("Invalid Field " + names[i]);
                }
                oi = sf.getFieldObjectInspector();
            }
            else if (oi instanceof ListObjectInspector && names[i].equalsIgnoreCase("$elem$")) {
                final ListObjectInspector loi = (ListObjectInspector)oi;
                oi = loi.getListElementObjectInspector();
            }
            else if (oi instanceof MapObjectInspector && names[i].equalsIgnoreCase("$key$")) {
                final MapObjectInspector moi = (MapObjectInspector)oi;
                oi = moi.getMapKeyObjectInspector();
            }
            else {
                if (!(oi instanceof MapObjectInspector) || !names[i].equalsIgnoreCase("$value$")) {
                    throw new MetaException("Unknown type for " + names[i]);
                }
                final MapObjectInspector moi = (MapObjectInspector)oi;
                oi = moi.getMapValueObjectInspector();
            }
        }
        final ArrayList<FieldSchema> str_fields = new ArrayList<FieldSchema>();
        if (oi.getCategory() != ObjectInspector.Category.STRUCT) {
            str_fields.add(new FieldSchema(last_name, oi.getTypeName(), "from deserializer"));
        }
        else {
            final List<? extends StructField> fields = ((StructObjectInspector)oi).getAllStructFieldRefs();
            for (int j = 0; j < fields.size(); ++j) {
                final StructField structField = (StructField)fields.get(j);
                final String fieldName = structField.getFieldName();
                final String fieldTypeName = structField.getFieldObjectInspector().getTypeName();
                final String fieldComment = determineFieldComment(structField.getFieldComment());
                str_fields.add(new FieldSchema(fieldName, fieldTypeName, fieldComment));
            }
        }
        return str_fields;
    }
    
    private static String determineFieldComment(final String comment) {
        return (comment == null) ? "from deserializer" : comment;
    }
    
    public static FieldSchema getFieldSchemaFromTypeInfo(final String fieldName, final TypeInfo typeInfo) {
        return new FieldSchema(fieldName, typeInfo.getTypeName(), "generated by TypeInfoUtils.getFieldSchemaFromTypeInfo");
    }
    
    public static boolean isExternalTable(final Table table) {
        if (table == null) {
            return false;
        }
        final Map<String, String> params = table.getParameters();
        return params != null && "TRUE".equalsIgnoreCase(params.get("EXTERNAL"));
    }
    
    public static boolean isImmutableTable(final Table table) {
        if (table == null) {
            return false;
        }
        final Map<String, String> params = table.getParameters();
        return params != null && "TRUE".equalsIgnoreCase(params.get("immutable"));
    }
    
    public static boolean isArchived(final Partition part) {
        final Map<String, String> params = part.getParameters();
        return "true".equalsIgnoreCase(params.get("is_archived"));
    }
    
    public static Path getOriginalLocation(final Partition part) {
        final Map<String, String> params = part.getParameters();
        assert isArchived(part);
        final String originalLocation = params.get("original_location");
        assert originalLocation != null;
        return new Path(originalLocation);
    }
    
    public static boolean isNonNativeTable(final Table table) {
        return table != null && table.getParameters().get("storage_handler") != null;
    }
    
    public static boolean isDirEmpty(final FileSystem fs, final Path path) throws IOException {
        if (fs.exists(path)) {
            final FileStatus[] status = fs.globStatus(new Path(path, "*"), MetaStoreUtils.hiddenFileFilter);
            if (status.length > 0) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean pvalMatches(final List<String> partial, final List<String> full) {
        if (partial.size() > full.size()) {
            return false;
        }
        final Iterator<String> p = partial.iterator();
        final Iterator<String> f = full.iterator();
        while (p.hasNext()) {
            final String pval = p.next();
            final String fval = f.next();
            if (pval.length() != 0 && !pval.equals(fval)) {
                return false;
            }
        }
        return true;
    }
    
    public static String getIndexTableName(final String dbName, final String baseTblName, final String indexName) {
        return dbName + "__" + baseTblName + "_" + indexName + "__";
    }
    
    public static boolean isIndexTable(final Table table) {
        return table != null && TableType.INDEX_TABLE.toString().equals(table.getTableType());
    }
    
    public static String makeFilterStringFromMap(final Map<String, String> m) {
        final StringBuilder filter = new StringBuilder();
        for (final Map.Entry<String, String> e : m.entrySet()) {
            final String col = e.getKey();
            final String val = e.getValue();
            if (filter.length() == 0) {
                filter.append(col + "=\"" + val + "\"");
            }
            else {
                filter.append(" and " + col + "=\"" + val + "\"");
            }
        }
        return filter.toString();
    }
    
    public static boolean isView(final Table table) {
        return table != null && TableType.VIRTUAL_VIEW.toString().equals(table.getTableType());
    }
    
    static <T> List<T> getMetaStoreListeners(final Class<T> clazz, final HiveConf conf, String listenerImplList) throws MetaException {
        final List<T> listeners = new ArrayList<T>();
        listenerImplList = listenerImplList.trim();
        if (listenerImplList.equals("")) {
            return listeners;
        }
        final String[] split;
        final String[] listenerImpls = split = listenerImplList.split(",");
        for (final String listenerImpl : split) {
            try {
                final T listener = (T)Class.forName(listenerImpl.trim(), true, JavaUtils.getClassLoader()).getConstructor(Configuration.class).newInstance(conf);
                listeners.add(listener);
            }
            catch (InvocationTargetException ie) {
                throw new MetaException("Failed to instantiate listener named: " + listenerImpl + ", reason: " + ie.getCause());
            }
            catch (Exception e) {
                throw new MetaException("Failed to instantiate listener named: " + listenerImpl + ", reason: " + e);
            }
        }
        return listeners;
    }
    
    public static Class<?> getClass(final String rawStoreClassName) throws MetaException {
        try {
            return Class.forName(rawStoreClassName, true, JavaUtils.getClassLoader());
        }
        catch (ClassNotFoundException e) {
            throw new MetaException(rawStoreClassName + " class not found");
        }
    }
    
    public static <T> T newInstance(final Class<T> theClass, final Class<?>[] parameterTypes, final Object[] initargs) {
        if (parameterTypes.length != initargs.length) {
            throw new IllegalArgumentException("Number of constructor parameter types doesn't match number of arguments");
        }
        for (int i = 0; i < parameterTypes.length; ++i) {
            final Class<?> clazz = parameterTypes[i];
            if (!clazz.isInstance(initargs[i])) {
                throw new IllegalArgumentException("Object : " + initargs[i] + " is not an instance of " + clazz);
            }
        }
        try {
            final Constructor<T> meth = theClass.getDeclaredConstructor(parameterTypes);
            meth.setAccessible(true);
            return meth.newInstance(initargs);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to instantiate " + theClass.getName(), e);
        }
    }
    
    public static void validatePartitionNameCharacters(final List<String> partVals, final Pattern partitionValidationPattern) throws MetaException {
        final String invalidPartitionVal = getPartitionValWithInvalidCharacter(partVals, partitionValidationPattern);
        if (invalidPartitionVal != null) {
            throw new MetaException("Partition value '" + invalidPartitionVal + "' contains a character " + "not matched by whitelist pattern '" + partitionValidationPattern.toString() + "'.  " + "(configure with " + HiveConf.ConfVars.METASTORE_PARTITION_NAME_WHITELIST_PATTERN.varname + ")");
        }
    }
    
    public static boolean partitionNameHasValidCharacters(final List<String> partVals, final Pattern partitionValidationPattern) {
        return getPartitionValWithInvalidCharacter(partVals, partitionValidationPattern) == null;
    }
    
    public static boolean compareFieldColumns(final List<FieldSchema> schema1, final List<FieldSchema> schema2) {
        if (schema1.size() != schema2.size()) {
            return false;
        }
        for (int i = 0; i < schema1.size(); ++i) {
            final FieldSchema f1 = schema1.get(i);
            final FieldSchema f2 = schema2.get(i);
            if (f1.getName() == null) {
                if (f2.getName() != null) {
                    return false;
                }
            }
            else if (!f1.getName().equals(f2.getName())) {
                return false;
            }
            if (f1.getType() == null) {
                if (f2.getType() != null) {
                    return false;
                }
            }
            else if (!f1.getType().equals(f2.getType())) {
                return false;
            }
        }
        return true;
    }
    
    public static Map<String, String> getMetaStoreSaslProperties(final HiveConf conf) {
        return ShimLoader.getHadoopThriftAuthBridge().getHadoopSaslProperties(conf);
    }
    
    private static String getPartitionValWithInvalidCharacter(final List<String> partVals, final Pattern partitionValidationPattern) {
        if (partitionValidationPattern == null) {
            return null;
        }
        for (final String partVal : partVals) {
            if (!partitionValidationPattern.matcher(partVal).matches()) {
                return partVal;
            }
        }
        return null;
    }
    
    public static ProtectMode getProtectMode(final Partition partition) {
        return getProtectMode(partition.getParameters());
    }
    
    public static ProtectMode getProtectMode(final Table table) {
        return getProtectMode(table.getParameters());
    }
    
    private static ProtectMode getProtectMode(final Map<String, String> parameters) {
        if (parameters == null) {
            return null;
        }
        if (!parameters.containsKey(ProtectMode.PARAMETER_NAME)) {
            return new ProtectMode();
        }
        return ProtectMode.getProtectModeFromString(parameters.get(ProtectMode.PARAMETER_NAME));
    }
    
    public static boolean canDropPartition(final Table table, final Partition partition) {
        final ProtectMode mode = getProtectMode(partition);
        final ProtectMode parentMode = getProtectMode(table);
        return !mode.noDrop && !mode.offline && !mode.readOnly && !parentMode.noDropCascade;
    }
    
    public static int getArchivingLevel(final Partition part) throws MetaException {
        if (!isArchived(part)) {
            throw new MetaException("Getting level of unarchived partition");
        }
        final String lv = part.getParameters().get(MetaStoreUtils.ARCHIVING_LEVEL);
        if (lv != null) {
            return Integer.parseInt(lv);
        }
        return part.getValues().size();
    }
    
    public static String[] getQualifiedName(final String defaultDbName, final String tableName) {
        final String[] names = tableName.split("\\.");
        if (names.length == 1) {
            return new String[] { defaultDbName, tableName };
        }
        return new String[] { names[0], names[1] };
    }
    
    public static Map<String, String> trimMapNulls(final Map<String, String> dnMap, final boolean retrieveMapNullsAsEmptyStrings) {
        if (dnMap == null) {
            return null;
        }
        if (retrieveMapNullsAsEmptyStrings) {
            return (Map<String, String>)Maps.newLinkedHashMap((Map<?, ?>)Maps.transformValues((Map<? extends K, String>)dnMap, (Function<? super String, ? extends V>)MetaStoreUtils.transFormNullsToEmptyString));
        }
        return (Map<String, String>)Maps.newLinkedHashMap((Map<?, ?>)Maps.filterValues((Map<? extends K, ? extends V>)dnMap, Predicates.notNull()));
    }
    
    private static URL urlFromPathString(final String onestr) {
        URL oneurl = null;
        try {
            if (StringUtils.indexOf(onestr, "file:/") == 0) {
                oneurl = new URL(onestr);
            }
            else {
                oneurl = new File(onestr).toURL();
            }
        }
        catch (Exception err) {
            MetaStoreUtils.LOG.error("Bad URL " + onestr + ", ignoring path");
        }
        return oneurl;
    }
    
    public static ClassLoader addToClassPath(final ClassLoader cloader, final String[] newPaths) throws Exception {
        final URLClassLoader loader = (URLClassLoader)cloader;
        List<URL> curPath = Arrays.asList(loader.getURLs());
        final ArrayList<URL> newPath = new ArrayList<URL>();
        for (final URL onePath : curPath) {
            newPath.add(onePath);
        }
        curPath = newPath;
        for (final String onestr : newPaths) {
            final URL oneurl = urlFromPathString(onestr);
            if (oneurl != null && !curPath.contains(oneurl)) {
                curPath.add(oneurl);
            }
        }
        return new URLClassLoader(curPath.toArray(new URL[0]), loader);
    }
    
    static {
        LOG = LogFactory.getLog("hive.log");
        (MetaStoreUtils.typeToThriftTypeMap = new HashMap<String, String>()).put("boolean", "bool");
        MetaStoreUtils.typeToThriftTypeMap.put("tinyint", "byte");
        MetaStoreUtils.typeToThriftTypeMap.put("smallint", "i16");
        MetaStoreUtils.typeToThriftTypeMap.put("int", "i32");
        MetaStoreUtils.typeToThriftTypeMap.put("bigint", "i64");
        MetaStoreUtils.typeToThriftTypeMap.put("double", "double");
        MetaStoreUtils.typeToThriftTypeMap.put("float", "float");
        MetaStoreUtils.typeToThriftTypeMap.put("array", "list");
        MetaStoreUtils.typeToThriftTypeMap.put("map", "map");
        MetaStoreUtils.typeToThriftTypeMap.put("string", "string");
        MetaStoreUtils.typeToThriftTypeMap.put("binary", "binary");
        MetaStoreUtils.typeToThriftTypeMap.put("date", "date");
        MetaStoreUtils.typeToThriftTypeMap.put("datetime", "datetime");
        MetaStoreUtils.typeToThriftTypeMap.put("timestamp", "timestamp");
        MetaStoreUtils.typeToThriftTypeMap.put("decimal", "decimal");
        MetaStoreUtils.typeToThriftTypeMap.put("interval_year_month", "interval_year_month");
        MetaStoreUtils.typeToThriftTypeMap.put("interval_day_time", "interval_day_time");
        (MetaStoreUtils.hiveThriftTypeMap = new HashSet<String>()).addAll(serdeConstants.PrimitiveTypes);
        MetaStoreUtils.hiveThriftTypeMap.addAll(serdeConstants.CollectionTypes);
        MetaStoreUtils.hiveThriftTypeMap.add("uniontype");
        MetaStoreUtils.hiveThriftTypeMap.add("struct");
        hiddenFileFilter = new PathFilter() {
            @Override
            public boolean accept(final Path p) {
                final String name = p.getName();
                return !name.startsWith("_") && !name.startsWith(".");
            }
        };
        MetaStoreUtils.ARCHIVING_LEVEL = "archiving_level";
        transFormNullsToEmptyString = new Function<String, String>() {
            @Override
            public String apply(@Nullable final String string) {
                if (string == null) {
                    return "";
                }
                return string;
            }
        };
    }
}
