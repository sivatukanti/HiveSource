// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.commons.logging.LogFactory;
import java.util.Collection;
import org.apache.hadoop.hive.common.HiveStatsUtils;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.Map;
import org.apache.hadoop.fs.FileStatus;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.hive.common.FileUtils;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.Database;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.hive.common.JavaUtils;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class Warehouse
{
    private Path whRoot;
    private final Configuration conf;
    private final String whRootString;
    public static final Log LOG;
    private MetaStoreFS fsHandler;
    private boolean storageAuthCheck;
    static final Pattern pat;
    private static final Pattern slash;
    
    public Warehouse(final Configuration conf) throws MetaException {
        this.fsHandler = null;
        this.storageAuthCheck = false;
        this.conf = conf;
        this.whRootString = HiveConf.getVar(conf, HiveConf.ConfVars.METASTOREWAREHOUSE);
        if (StringUtils.isBlank(this.whRootString)) {
            throw new MetaException(HiveConf.ConfVars.METASTOREWAREHOUSE.varname + " is not set in the config or blank");
        }
        this.fsHandler = this.getMetaStoreFsHandler(conf);
        this.storageAuthCheck = HiveConf.getBoolVar(conf, HiveConf.ConfVars.METASTORE_AUTHORIZATION_STORAGE_AUTH_CHECKS);
    }
    
    private MetaStoreFS getMetaStoreFsHandler(final Configuration conf) throws MetaException {
        final String handlerClassStr = HiveConf.getVar(conf, HiveConf.ConfVars.HIVE_METASTORE_FS_HANDLER_CLS);
        try {
            final Class<? extends MetaStoreFS> handlerClass = (Class<? extends MetaStoreFS>)Class.forName(handlerClassStr, true, JavaUtils.getClassLoader());
            final MetaStoreFS handler = ReflectionUtils.newInstance(handlerClass, conf);
            return handler;
        }
        catch (ClassNotFoundException e) {
            throw new MetaException("Error in loading MetaStoreFS handler." + e.getMessage());
        }
    }
    
    public static FileSystem getFs(final Path f, final Configuration conf) throws MetaException {
        try {
            return f.getFileSystem(conf);
        }
        catch (IOException e) {
            MetaStoreUtils.logAndThrowMetaException(e);
            return null;
        }
    }
    
    public FileSystem getFs(final Path f) throws MetaException {
        return getFs(f, this.conf);
    }
    
    public static void closeFs(final FileSystem fs) throws MetaException {
        try {
            if (fs != null) {
                fs.close();
            }
        }
        catch (IOException e) {
            MetaStoreUtils.logAndThrowMetaException(e);
        }
    }
    
    public static Path getDnsPath(final Path path, final Configuration conf) throws MetaException {
        final FileSystem fs = getFs(path, conf);
        return new Path(fs.getUri().getScheme(), fs.getUri().getAuthority(), path.toUri().getPath());
    }
    
    public Path getDnsPath(final Path path) throws MetaException {
        return getDnsPath(path, this.conf);
    }
    
    public Path getWhRoot() throws MetaException {
        if (this.whRoot != null) {
            return this.whRoot;
        }
        return this.whRoot = this.getDnsPath(new Path(this.whRootString));
    }
    
    public Path getTablePath(final String whRootString, final String tableName) throws MetaException {
        final Path whRoot = this.getDnsPath(new Path(whRootString));
        return new Path(whRoot, tableName.toLowerCase());
    }
    
    public Path getDatabasePath(final Database db) throws MetaException {
        if (db.getName().equalsIgnoreCase("default")) {
            return this.getWhRoot();
        }
        return new Path(db.getLocationUri());
    }
    
    public Path getDefaultDatabasePath(final String dbName) throws MetaException {
        if (dbName.equalsIgnoreCase("default")) {
            return this.getWhRoot();
        }
        return new Path(this.getWhRoot(), dbName.toLowerCase() + ".db");
    }
    
    public Path getTablePath(final Database db, final String tableName) throws MetaException {
        return this.getDnsPath(new Path(this.getDatabasePath(db), tableName.toLowerCase()));
    }
    
    public static String getQualifiedName(final Table table) {
        return table.getDbName() + "." + table.getTableName();
    }
    
    public static String getQualifiedName(final Partition partition) {
        return partition.getDbName() + "." + partition.getTableName() + partition.getValues();
    }
    
    public boolean mkdirs(final Path f, final boolean inheritPermCandidate) throws MetaException {
        final boolean inheritPerms = HiveConf.getBoolVar(this.conf, HiveConf.ConfVars.HIVE_WAREHOUSE_SUBDIR_INHERIT_PERMS) && inheritPermCandidate;
        FileSystem fs = null;
        try {
            fs = this.getFs(f);
            return FileUtils.mkdir(fs, f, inheritPerms, this.conf);
        }
        catch (IOException e) {
            MetaStoreUtils.logAndThrowMetaException(e);
            return false;
        }
    }
    
    public boolean renameDir(final Path sourcePath, final Path destPath) throws MetaException {
        return this.renameDir(sourcePath, destPath, false);
    }
    
    public boolean renameDir(final Path sourcePath, final Path destPath, final boolean inheritPerms) throws MetaException {
        try {
            final FileSystem fs = this.getFs(sourcePath);
            return FileUtils.renameWithPerms(fs, sourcePath, destPath, inheritPerms, this.conf);
        }
        catch (Exception ex) {
            MetaStoreUtils.logAndThrowMetaException(ex);
            return false;
        }
    }
    
    public boolean deleteDir(final Path f, final boolean recursive) throws MetaException {
        return this.deleteDir(f, recursive, false);
    }
    
    public boolean deleteDir(final Path f, final boolean recursive, final boolean ifPurge) throws MetaException {
        final FileSystem fs = this.getFs(f);
        return this.fsHandler.deleteDir(fs, f, recursive, ifPurge, this.conf);
    }
    
    public boolean isEmpty(final Path path) throws IOException, MetaException {
        final ContentSummary contents = this.getFs(path).getContentSummary(path);
        return contents != null && contents.getFileCount() == 0L && contents.getDirectoryCount() == 1L;
    }
    
    public boolean isWritable(final Path path) throws IOException {
        if (!this.storageAuthCheck) {
            return true;
        }
        if (path == null) {
            return false;
        }
        try {
            final FileSystem fs = this.getFs(path);
            final FileStatus stat = fs.getFileStatus(path);
            ShimLoader.getHadoopShims().checkFileAccess(fs, stat, FsAction.WRITE);
            return true;
        }
        catch (FileNotFoundException fnfe) {
            return true;
        }
        catch (Exception e) {
            if (Warehouse.LOG.isDebugEnabled()) {
                Warehouse.LOG.debug("Exception when checking if path (" + path + ")", e);
            }
            return false;
        }
    }
    
    static String escapePathName(final String path) {
        return FileUtils.escapePathName(path);
    }
    
    static String unescapePathName(final String path) {
        return FileUtils.unescapePathName(path);
    }
    
    public static String makePartPath(final Map<String, String> spec) throws MetaException {
        return makePartName(spec, true);
    }
    
    public static String makePartName(final Map<String, String> spec, final boolean addTrailingSeperator) throws MetaException {
        final StringBuilder suffixBuf = new StringBuilder();
        int i = 0;
        for (final Map.Entry<String, String> e : spec.entrySet()) {
            if (e.getValue() == null || e.getValue().length() == 0) {
                throw new MetaException("Partition spec is incorrect. " + spec);
            }
            if (i > 0) {
                suffixBuf.append("/");
            }
            suffixBuf.append(escapePathName(e.getKey()));
            suffixBuf.append('=');
            suffixBuf.append(escapePathName(e.getValue()));
            ++i;
        }
        if (addTrailingSeperator) {
            suffixBuf.append("/");
        }
        return suffixBuf.toString();
    }
    
    public static String makeDynamicPartName(final Map<String, String> spec) {
        final StringBuilder suffixBuf = new StringBuilder();
        for (final Map.Entry<String, String> e : spec.entrySet()) {
            if (e.getValue() == null || e.getValue().length() <= 0) {
                break;
            }
            suffixBuf.append(escapePathName(e.getKey()));
            suffixBuf.append('=');
            suffixBuf.append(escapePathName(e.getValue()));
            suffixBuf.append("/");
        }
        return suffixBuf.toString();
    }
    
    public static void makeValsFromName(final String name, final AbstractList<String> result) throws MetaException {
        assert name != null;
        final String[] parts = Warehouse.slash.split(name, 0);
        if (parts.length != result.size()) {
            throw new MetaException("Expected " + result.size() + " components, got " + parts.length + " (" + name + ")");
        }
        for (int i = 0; i < parts.length; ++i) {
            final int eq = parts[i].indexOf(61);
            if (eq <= 0) {
                throw new MetaException("Unexpected component " + parts[i]);
            }
            result.set(i, unescapePathName(parts[i].substring(eq + 1)));
        }
    }
    
    public static LinkedHashMap<String, String> makeSpecFromName(final String name) throws MetaException {
        if (name == null || name.isEmpty()) {
            throw new MetaException("Partition name is invalid. " + name);
        }
        final LinkedHashMap<String, String> partSpec = new LinkedHashMap<String, String>();
        makeSpecFromName(partSpec, new Path(name));
        return partSpec;
    }
    
    public static void makeSpecFromName(final Map<String, String> partSpec, Path currPath) {
        final List<String[]> kvs = new ArrayList<String[]>();
        do {
            final String component = currPath.getName();
            final Matcher m = Warehouse.pat.matcher(component);
            if (m.matches()) {
                final String k = unescapePathName(m.group(1));
                final String v = unescapePathName(m.group(2));
                final String[] kv = { k, v };
                kvs.add(kv);
            }
            currPath = currPath.getParent();
        } while (currPath != null && !currPath.getName().isEmpty());
        for (int i = kvs.size(); i > 0; --i) {
            partSpec.put(kvs.get(i - 1)[0], kvs.get(i - 1)[1]);
        }
    }
    
    public static Map<String, String> makeEscSpecFromName(final String name) throws MetaException {
        if (name == null || name.isEmpty()) {
            throw new MetaException("Partition name is invalid. " + name);
        }
        final LinkedHashMap<String, String> partSpec = new LinkedHashMap<String, String>();
        Path currPath = new Path(name);
        final List<String[]> kvs = new ArrayList<String[]>();
        do {
            final String component = currPath.getName();
            final Matcher m = Warehouse.pat.matcher(component);
            if (m.matches()) {
                final String k = m.group(1);
                final String v = m.group(2);
                final String[] kv = { k, v };
                kvs.add(kv);
            }
            currPath = currPath.getParent();
        } while (currPath != null && !currPath.getName().isEmpty());
        for (int i = kvs.size(); i > 0; --i) {
            partSpec.put(kvs.get(i - 1)[0], kvs.get(i - 1)[1]);
        }
        return partSpec;
    }
    
    public Path getPartitionPath(final Database db, final String tableName, final LinkedHashMap<String, String> pm) throws MetaException {
        return new Path(this.getTablePath(db, tableName), makePartPath(pm));
    }
    
    public Path getPartitionPath(final Path tblPath, final LinkedHashMap<String, String> pm) throws MetaException {
        return new Path(tblPath, makePartPath(pm));
    }
    
    public boolean isDir(final Path f) throws MetaException {
        FileSystem fs = null;
        try {
            fs = this.getFs(f);
            final FileStatus fstatus = fs.getFileStatus(f);
            if (!fstatus.isDir()) {
                return false;
            }
        }
        catch (FileNotFoundException e2) {
            return false;
        }
        catch (IOException e) {
            MetaStoreUtils.logAndThrowMetaException(e);
        }
        return true;
    }
    
    public static String makePartName(final List<FieldSchema> partCols, final List<String> vals) throws MetaException {
        return makePartName(partCols, vals, null);
    }
    
    public FileStatus[] getFileStatusesForSD(final StorageDescriptor desc) throws MetaException {
        return this.getFileStatusesForLocation(desc.getLocation());
    }
    
    public FileStatus[] getFileStatusesForLocation(final String location) throws MetaException {
        try {
            final Path path = new Path(location);
            final FileSystem fileSys = path.getFileSystem(this.conf);
            return HiveStatsUtils.getFileStatusRecurse(path, -1, fileSys);
        }
        catch (IOException ioe) {
            MetaStoreUtils.logAndThrowMetaException(ioe);
            return null;
        }
    }
    
    public FileStatus[] getFileStatusesForUnpartitionedTable(final Database db, final Table table) throws MetaException {
        final Path tablePath = this.getTablePath(db, table.getTableName());
        try {
            final FileSystem fileSys = tablePath.getFileSystem(this.conf);
            return HiveStatsUtils.getFileStatusRecurse(tablePath, -1, fileSys);
        }
        catch (IOException ioe) {
            MetaStoreUtils.logAndThrowMetaException(ioe);
            return null;
        }
    }
    
    public static String makePartName(final List<FieldSchema> partCols, final List<String> vals, final String defaultStr) throws MetaException {
        if (partCols.size() != vals.size() || partCols.size() == 0) {
            String errorStr = "Invalid partition key & values; keys [";
            for (final FieldSchema fs : partCols) {
                errorStr = errorStr + fs.getName() + ", ";
            }
            errorStr += "], values [";
            for (final String val : vals) {
                errorStr = errorStr + val + ", ";
            }
            throw new MetaException(errorStr + "]");
        }
        final List<String> colNames = new ArrayList<String>();
        for (final FieldSchema col : partCols) {
            colNames.add(col.getName());
        }
        return FileUtils.makePartName(colNames, vals, defaultStr);
    }
    
    public static List<String> getPartValuesFromPartName(final String partName) throws MetaException {
        final LinkedHashMap<String, String> partSpec = makeSpecFromName(partName);
        final List<String> values = new ArrayList<String>();
        values.addAll(partSpec.values());
        return values;
    }
    
    public static Map<String, String> makeSpecFromValues(final List<FieldSchema> partCols, final List<String> values) {
        final Map<String, String> spec = new LinkedHashMap<String, String>();
        for (int i = 0; i < values.size(); ++i) {
            spec.put(partCols.get(i).getName(), values.get(i));
        }
        return spec;
    }
    
    static {
        LOG = LogFactory.getLog("hive.metastore.warehouse");
        pat = Pattern.compile("([^/]+)=([^/]+)");
        slash = Pattern.compile("/");
    }
}
