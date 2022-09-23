// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import com.google.common.collect.ImmutableMap;
import org.apache.hive.common.util.HiveVersionInfo;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import org.apache.hadoop.hive.conf.HiveConf;

public class MetaStoreSchemaInfo
{
    private static String SQL_FILE_EXTENSION;
    private static String UPGRADE_FILE_PREFIX;
    private static String INIT_FILE_PREFIX;
    private static String VERSION_UPGRADE_LIST;
    private static String PRE_UPGRADE_PREFIX;
    private final String dbType;
    private final String[] hiveSchemaVersions;
    private final HiveConf hiveConf;
    private final String hiveHome;
    private static final Map<String, String> EQUIVALENT_VERSIONS;
    
    public MetaStoreSchemaInfo(final String hiveHome, final HiveConf hiveConf, final String dbType) throws HiveMetaException {
        this.hiveHome = hiveHome;
        this.dbType = dbType;
        this.hiveConf = hiveConf;
        final List<String> upgradeOrderList = new ArrayList<String>();
        final String upgradeListFile = this.getMetaStoreScriptDir() + File.separator + MetaStoreSchemaInfo.VERSION_UPGRADE_LIST + "." + dbType;
        try {
            final BufferedReader bfReader = new BufferedReader(new FileReader(upgradeListFile));
            String currSchemaVersion;
            while ((currSchemaVersion = bfReader.readLine()) != null) {
                upgradeOrderList.add(currSchemaVersion.trim());
            }
        }
        catch (FileNotFoundException e) {
            throw new HiveMetaException("File " + upgradeListFile + "not found ", e);
        }
        catch (IOException e2) {
            throw new HiveMetaException("Error reading " + upgradeListFile, e2);
        }
        this.hiveSchemaVersions = upgradeOrderList.toArray(new String[0]);
    }
    
    public List<String> getUpgradeScripts(final String fromVersion) throws HiveMetaException {
        final List<String> upgradeScriptList = new ArrayList<String>();
        if (getHiveSchemaVersion().equals(fromVersion)) {
            return upgradeScriptList;
        }
        int firstScript = this.hiveSchemaVersions.length;
        for (int i = 0; i < this.hiveSchemaVersions.length; ++i) {
            if (this.hiveSchemaVersions[i].startsWith(fromVersion)) {
                firstScript = i;
            }
        }
        if (firstScript == this.hiveSchemaVersions.length) {
            throw new HiveMetaException("Unknown version specified for upgrade " + fromVersion + " Metastore schema may be too old or newer");
        }
        for (int i = firstScript; i < this.hiveSchemaVersions.length; ++i) {
            final String scriptFile = this.generateUpgradeFileName(this.hiveSchemaVersions[i]);
            upgradeScriptList.add(scriptFile);
        }
        return upgradeScriptList;
    }
    
    public String generateInitFileName(String toVersion) throws HiveMetaException {
        if (toVersion == null) {
            toVersion = getHiveSchemaVersion();
        }
        final String initScriptName = MetaStoreSchemaInfo.INIT_FILE_PREFIX + toVersion + "." + this.dbType + MetaStoreSchemaInfo.SQL_FILE_EXTENSION;
        if (!new File(this.getMetaStoreScriptDir() + File.separatorChar + initScriptName).exists()) {
            throw new HiveMetaException("Unknown version specified for initialization: " + toVersion);
        }
        return initScriptName;
    }
    
    public String getMetaStoreScriptDir() {
        return this.hiveHome + File.separatorChar + "scripts" + File.separatorChar + "metastore" + File.separatorChar + "upgrade" + File.separatorChar + this.dbType;
    }
    
    private String generateUpgradeFileName(final String fileVersion) {
        return MetaStoreSchemaInfo.UPGRADE_FILE_PREFIX + fileVersion + "." + this.dbType + MetaStoreSchemaInfo.SQL_FILE_EXTENSION;
    }
    
    public static String getPreUpgradeScriptName(final int index, final String upgradeScriptName) {
        return MetaStoreSchemaInfo.PRE_UPGRADE_PREFIX + index + "-" + upgradeScriptName;
    }
    
    public static String getHiveSchemaVersion() {
        final String hiveVersion = HiveVersionInfo.getShortVersion();
        final String equivalentVersion = MetaStoreSchemaInfo.EQUIVALENT_VERSIONS.get(hiveVersion);
        if (equivalentVersion != null) {
            return equivalentVersion;
        }
        return hiveVersion;
    }
    
    static {
        MetaStoreSchemaInfo.SQL_FILE_EXTENSION = ".sql";
        MetaStoreSchemaInfo.UPGRADE_FILE_PREFIX = "upgrade-";
        MetaStoreSchemaInfo.INIT_FILE_PREFIX = "hive-schema-";
        MetaStoreSchemaInfo.VERSION_UPGRADE_LIST = "upgrade.order";
        MetaStoreSchemaInfo.PRE_UPGRADE_PREFIX = "pre-";
        EQUIVALENT_VERSIONS = ImmutableMap.of("0.13.1", "0.13.0", "1.0.0", "0.14.0", "1.0.1", "1.0.0", "1.1.1", "1.1.0", "1.2.1", "1.2.0");
    }
}
