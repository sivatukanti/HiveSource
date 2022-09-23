// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.conf;

import org.apache.hive.common.HiveCompat;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.util.Shell;
import java.io.File;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;
import org.apache.hadoop.security.UserGroupInformation;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import org.apache.hadoop.hive.shims.Utils;
import org.apache.commons.lang.StringUtils;
import java.util.HashMap;
import com.google.common.base.Joiner;
import org.apache.hadoop.mapred.JobConf;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.net.URL;
import org.apache.commons.logging.Log;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;

public class HiveConf extends Configuration
{
    protected String hiveJar;
    protected Properties origProp;
    protected String auxJars;
    private static final Log l4j;
    private static boolean loadMetastoreConfig;
    private static boolean loadHiveServer2Config;
    private static URL hiveDefaultURL;
    private static URL hiveSiteURL;
    private static URL hivemetastoreSiteUrl;
    private static URL hiveServer2SiteUrl;
    private static byte[] confVarByteArray;
    private static final Map<String, ConfVars> vars;
    private static final Map<String, ConfVars> metaConfs;
    private final List<String> restrictList;
    private final Set<String> hiddenSet;
    private Pattern modWhiteListPattern;
    private boolean isSparkConfigUpdated;
    public static final ConfVars[] metaVars;
    public static final ConfVars[] metaConfVars;
    public static final ConfVars[] dbVars;
    private static final String[] sqlStdAuthSafeVarNames;
    static final String[] sqlStdAuthSafeVarNameRegexes;
    
    public boolean getSparkConfigUpdated() {
        return this.isSparkConfigUpdated;
    }
    
    public void setSparkConfigUpdated(final boolean isSparkConfigUpdated) {
        this.isSparkConfigUpdated = isSparkConfigUpdated;
    }
    
    private static synchronized InputStream getConfVarInputStream() {
        if (HiveConf.confVarByteArray == null) {
            try {
                final Configuration conf = new Configuration(false);
                applyDefaultNonNullConfVars(conf);
                final ByteArrayOutputStream confVarBaos = new ByteArrayOutputStream();
                conf.writeXml(confVarBaos);
                HiveConf.confVarByteArray = confVarBaos.toByteArray();
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to initialize default Hive configuration variables!", e);
            }
        }
        return new LoopingByteArrayInputStream(HiveConf.confVarByteArray);
    }
    
    public void verifyAndSet(final String name, final String value) throws IllegalArgumentException {
        if (this.modWhiteListPattern != null) {
            final Matcher wlMatcher = this.modWhiteListPattern.matcher(name);
            if (!wlMatcher.matches()) {
                throw new IllegalArgumentException("Cannot modify " + name + " at runtime. " + "It is not in list of params that are allowed to be modified at runtime");
            }
        }
        if (this.restrictList.contains(name)) {
            throw new IllegalArgumentException("Cannot modify " + name + " at runtime. It is in the list" + "of parameters that can't be modified at runtime");
        }
        this.isSparkConfigUpdated = this.isSparkRelatedConfig(name);
        this.set(name, value);
    }
    
    public boolean isHiddenConfig(final String name) {
        return this.hiddenSet.contains(name);
    }
    
    private boolean isSparkRelatedConfig(final String name) {
        boolean result = false;
        if (name.startsWith("spark")) {
            result = true;
        }
        else if (name.startsWith("yarn")) {
            final String sparkMaster = this.get("spark.master");
            if (sparkMaster != null && (sparkMaster.equals("yarn-client") || sparkMaster.equals("yarn-cluster"))) {
                result = true;
            }
        }
        else if (name.startsWith("hive.spark")) {
            result = true;
        }
        return result;
    }
    
    public static int getIntVar(final Configuration conf, final ConfVars var) {
        assert var.valClass == Integer.class : var.varname;
        return conf.getInt(var.varname, var.defaultIntVal);
    }
    
    public static void setIntVar(final Configuration conf, final ConfVars var, final int val) {
        assert var.valClass == Integer.class : var.varname;
        conf.setInt(var.varname, val);
    }
    
    public int getIntVar(final ConfVars var) {
        return getIntVar(this, var);
    }
    
    public void setIntVar(final ConfVars var, final int val) {
        setIntVar(this, var, val);
    }
    
    public static long getTimeVar(final Configuration conf, final ConfVars var, final TimeUnit outUnit) {
        return toTime(getVar(conf, var), getDefaultTimeUnit(var), outUnit);
    }
    
    public static void setTimeVar(final Configuration conf, final ConfVars var, final long time, final TimeUnit timeunit) {
        assert var.valClass == String.class : var.varname;
        conf.set(var.varname, time + stringFor(timeunit));
    }
    
    public long getTimeVar(final ConfVars var, final TimeUnit outUnit) {
        return getTimeVar(this, var, outUnit);
    }
    
    public void setTimeVar(final ConfVars var, final long time, final TimeUnit outUnit) {
        setTimeVar(this, var, time, outUnit);
    }
    
    private static TimeUnit getDefaultTimeUnit(final ConfVars var) {
        TimeUnit inputUnit = null;
        if (var.validator instanceof Validator.TimeValidator) {
            inputUnit = ((Validator.TimeValidator)var.validator).getTimeUnit();
        }
        return inputUnit;
    }
    
    public static long toTime(final String value, final TimeUnit inputUnit, final TimeUnit outUnit) {
        final String[] parsed = parseTime(value.trim());
        return outUnit.convert(Long.valueOf(parsed[0].trim().trim()), unitFor(parsed[1].trim(), inputUnit));
    }
    
    private static String[] parseTime(final String value) {
        char[] chars;
        int i;
        for (chars = value.toCharArray(), i = 0; i < chars.length && (chars[i] == '-' || Character.isDigit(chars[i])); ++i) {}
        return new String[] { value.substring(0, i), value.substring(i) };
    }
    
    public static TimeUnit unitFor(String unit, final TimeUnit defaultUnit) {
        unit = unit.trim().toLowerCase();
        if (unit.isEmpty() || unit.equals("l")) {
            if (defaultUnit == null) {
                throw new IllegalArgumentException("Time unit is not specified");
            }
            return defaultUnit;
        }
        else {
            if (unit.equals("d") || unit.startsWith("day")) {
                return TimeUnit.DAYS;
            }
            if (unit.equals("h") || unit.startsWith("hour")) {
                return TimeUnit.HOURS;
            }
            if (unit.equals("m") || unit.startsWith("min")) {
                return TimeUnit.MINUTES;
            }
            if (unit.equals("s") || unit.startsWith("sec")) {
                return TimeUnit.SECONDS;
            }
            if (unit.equals("ms") || unit.startsWith("msec")) {
                return TimeUnit.MILLISECONDS;
            }
            if (unit.equals("us") || unit.startsWith("usec")) {
                return TimeUnit.MICROSECONDS;
            }
            if (unit.equals("ns") || unit.startsWith("nsec")) {
                return TimeUnit.NANOSECONDS;
            }
            throw new IllegalArgumentException("Invalid time unit " + unit);
        }
    }
    
    public static String stringFor(final TimeUnit timeunit) {
        switch (timeunit) {
            case DAYS: {
                return "day";
            }
            case HOURS: {
                return "hour";
            }
            case MINUTES: {
                return "min";
            }
            case SECONDS: {
                return "sec";
            }
            case MILLISECONDS: {
                return "msec";
            }
            case MICROSECONDS: {
                return "usec";
            }
            case NANOSECONDS: {
                return "nsec";
            }
            default: {
                throw new IllegalArgumentException("Invalid timeunit " + timeunit);
            }
        }
    }
    
    public static long getLongVar(final Configuration conf, final ConfVars var) {
        assert var.valClass == Long.class : var.varname;
        return conf.getLong(var.varname, var.defaultLongVal);
    }
    
    public static long getLongVar(final Configuration conf, final ConfVars var, final long defaultVal) {
        return conf.getLong(var.varname, defaultVal);
    }
    
    public static void setLongVar(final Configuration conf, final ConfVars var, final long val) {
        assert var.valClass == Long.class : var.varname;
        conf.setLong(var.varname, val);
    }
    
    public long getLongVar(final ConfVars var) {
        return getLongVar(this, var);
    }
    
    public void setLongVar(final ConfVars var, final long val) {
        setLongVar(this, var, val);
    }
    
    public static float getFloatVar(final Configuration conf, final ConfVars var) {
        assert var.valClass == Float.class : var.varname;
        return conf.getFloat(var.varname, var.defaultFloatVal);
    }
    
    public static float getFloatVar(final Configuration conf, final ConfVars var, final float defaultVal) {
        return conf.getFloat(var.varname, defaultVal);
    }
    
    public static void setFloatVar(final Configuration conf, final ConfVars var, final float val) {
        assert var.valClass == Float.class : var.varname;
        conf.setFloat(var.varname, val);
    }
    
    public float getFloatVar(final ConfVars var) {
        return getFloatVar(this, var);
    }
    
    public void setFloatVar(final ConfVars var, final float val) {
        setFloatVar(this, var, val);
    }
    
    public static boolean getBoolVar(final Configuration conf, final ConfVars var) {
        assert var.valClass == Boolean.class : var.varname;
        return conf.getBoolean(var.varname, var.defaultBoolVal);
    }
    
    public static boolean getBoolVar(final Configuration conf, final ConfVars var, final boolean defaultVal) {
        return conf.getBoolean(var.varname, defaultVal);
    }
    
    public static void setBoolVar(final Configuration conf, final ConfVars var, final boolean val) {
        assert var.valClass == Boolean.class : var.varname;
        conf.setBoolean(var.varname, val);
    }
    
    public boolean getBoolVar(final ConfVars var) {
        return getBoolVar(this, var);
    }
    
    public void setBoolVar(final ConfVars var, final boolean val) {
        setBoolVar(this, var, val);
    }
    
    public static String getVar(final Configuration conf, final ConfVars var) {
        assert var.valClass == String.class : var.varname;
        return conf.get(var.varname, var.defaultStrVal);
    }
    
    public static String getVar(final Configuration conf, final ConfVars var, final String defaultVal) {
        return conf.get(var.varname, defaultVal);
    }
    
    public static void setVar(final Configuration conf, final ConfVars var, final String val) {
        assert var.valClass == String.class : var.varname;
        conf.set(var.varname, val);
    }
    
    public static ConfVars getConfVars(final String name) {
        return HiveConf.vars.get(name);
    }
    
    public static ConfVars getMetaConf(final String name) {
        return HiveConf.metaConfs.get(name);
    }
    
    public String getVar(final ConfVars var) {
        return getVar(this, var);
    }
    
    public void setVar(final ConfVars var, final String val) {
        setVar(this, var, val);
    }
    
    public void logVars(final PrintStream ps) {
        for (final ConfVars one : ConfVars.values()) {
            ps.println(one.varname + "=" + ((this.get(one.varname) != null) ? this.get(one.varname) : ""));
        }
    }
    
    public HiveConf() {
        this.restrictList = new ArrayList<String>();
        this.hiddenSet = new HashSet<String>();
        this.modWhiteListPattern = null;
        this.isSparkConfigUpdated = false;
        this.initialize(this.getClass());
    }
    
    public HiveConf(final Class<?> cls) {
        this.restrictList = new ArrayList<String>();
        this.hiddenSet = new HashSet<String>();
        this.modWhiteListPattern = null;
        this.isSparkConfigUpdated = false;
        this.initialize(cls);
    }
    
    public HiveConf(final Configuration other, final Class<?> cls) {
        super(other);
        this.restrictList = new ArrayList<String>();
        this.hiddenSet = new HashSet<String>();
        this.modWhiteListPattern = null;
        this.isSparkConfigUpdated = false;
        this.initialize(cls);
    }
    
    public HiveConf(final HiveConf other) {
        super(other);
        this.restrictList = new ArrayList<String>();
        this.hiddenSet = new HashSet<String>();
        this.modWhiteListPattern = null;
        this.isSparkConfigUpdated = false;
        this.hiveJar = other.hiveJar;
        this.auxJars = other.auxJars;
        this.origProp = (Properties)other.origProp.clone();
        this.restrictList.addAll(other.restrictList);
        this.hiddenSet.addAll(other.hiddenSet);
        this.modWhiteListPattern = other.modWhiteListPattern;
    }
    
    public Properties getAllProperties() {
        return getProperties(this);
    }
    
    private static Properties getProperties(final Configuration conf) {
        final Iterator<Map.Entry<String, String>> iter = conf.iterator();
        final Properties p = new Properties();
        while (iter.hasNext()) {
            final Map.Entry<String, String> e = iter.next();
            p.setProperty(e.getKey(), e.getValue());
        }
        return p;
    }
    
    private void initialize(final Class<?> cls) {
        this.hiveJar = new JobConf((Class)cls).getJar();
        this.origProp = this.getAllProperties();
        this.addResource(getConfVarInputStream());
        if (HiveConf.hiveSiteURL != null) {
            this.addResource(HiveConf.hiveSiteURL);
        }
        final String msUri = this.getVar(ConfVars.METASTOREURIS);
        if (HiveConfUtil.isEmbeddedMetaStore(msUri)) {
            setLoadMetastoreConfig(true);
        }
        if (isLoadMetastoreConfig() && HiveConf.hivemetastoreSiteUrl != null) {
            this.addResource(HiveConf.hivemetastoreSiteUrl);
        }
        if (isLoadHiveServer2Config() && HiveConf.hiveServer2SiteUrl != null) {
            this.addResource(HiveConf.hiveServer2SiteUrl);
        }
        this.applySystemProperties();
        if (this.get("hive.metastore.ds.retry.attempts") != null || this.get("hive.metastore.ds.retry.interval") != null) {
            HiveConf.l4j.warn("DEPRECATED: hive.metastore.ds.retry.* no longer has any effect.  Use hive.hmshandler.retry.* instead");
        }
        if (this.hiveJar == null) {
            this.hiveJar = this.get(ConfVars.HIVEJAR.varname);
        }
        if (this.auxJars == null) {
            this.auxJars = this.get(ConfVars.HIVEAUXJARS.varname);
        }
        if (this.getBoolVar(ConfVars.METASTORE_SCHEMA_VERIFICATION)) {
            this.setBoolVar(ConfVars.METASTORE_AUTO_CREATE_SCHEMA, false);
            this.setBoolVar(ConfVars.METASTORE_FIXED_DATASTORE, true);
        }
        if (this.getBoolVar(ConfVars.HIVECONFVALIDATION)) {
            final List<String> trimmed = new ArrayList<String>();
            for (final Map.Entry<String, String> entry : this) {
                final String key = entry.getKey();
                if (key != null) {
                    if (!key.startsWith("hive.")) {
                        continue;
                    }
                    ConfVars var = getConfVars(key);
                    if (var == null) {
                        var = getConfVars(key.trim());
                        if (var != null) {
                            trimmed.add(key);
                        }
                    }
                    if (var == null) {
                        HiveConf.l4j.warn("HiveConf of name " + key + " does not exist");
                    }
                    else {
                        if (var.isType(entry.getValue())) {
                            continue;
                        }
                        HiveConf.l4j.warn("HiveConf " + var.varname + " expects " + var.typeString() + " type value");
                    }
                }
            }
            for (final String key2 : trimmed) {
                this.set(key2.trim(), this.getRaw(key2));
                this.unset(key2);
            }
        }
        this.setupSQLStdAuthWhiteList();
        this.setupRestrictList();
        this.setupHiddenSet();
    }
    
    private void setupSQLStdAuthWhiteList() {
        String whiteListParamsStr = this.getVar(ConfVars.HIVE_AUTHORIZATION_SQL_STD_AUTH_CONFIG_WHITELIST);
        if (whiteListParamsStr == null || whiteListParamsStr.trim().isEmpty()) {
            whiteListParamsStr = getSQLStdAuthDefaultWhiteListPattern();
        }
        this.setVar(ConfVars.HIVE_AUTHORIZATION_SQL_STD_AUTH_CONFIG_WHITELIST, whiteListParamsStr);
    }
    
    private static String getSQLStdAuthDefaultWhiteListPattern() {
        final String confVarPatternStr = Joiner.on("|").join(convertVarsToRegex(HiveConf.sqlStdAuthSafeVarNames));
        final String regexPatternStr = Joiner.on("|").join(HiveConf.sqlStdAuthSafeVarNameRegexes);
        return regexPatternStr + "|" + confVarPatternStr;
    }
    
    private static String[] convertVarsToRegex(final String[] paramList) {
        final String[] regexes = new String[paramList.length];
        for (int i = 0; i < paramList.length; ++i) {
            regexes[i] = paramList[i].replace(".", "\\.");
        }
        return regexes;
    }
    
    private void applySystemProperties() {
        final Map<String, String> systemProperties = getConfSystemProperties();
        for (final Map.Entry<String, String> systemProperty : systemProperties.entrySet()) {
            this.set(systemProperty.getKey(), systemProperty.getValue());
        }
    }
    
    public static Map<String, String> getConfSystemProperties() {
        final Map<String, String> systemProperties = new HashMap<String, String>();
        for (final ConfVars oneVar : ConfVars.values()) {
            if (System.getProperty(oneVar.varname) != null && System.getProperty(oneVar.varname).length() > 0) {
                systemProperties.put(oneVar.varname, System.getProperty(oneVar.varname));
            }
        }
        return systemProperties;
    }
    
    private static void applyDefaultNonNullConfVars(final Configuration conf) {
        for (final ConfVars var : ConfVars.values()) {
            final String defaultValue = var.getDefaultValue();
            if (defaultValue != null) {
                conf.set(var.varname, defaultValue);
            }
        }
    }
    
    public Properties getChangedProperties() {
        final Properties ret = new Properties();
        final Properties newProp = this.getAllProperties();
        for (final Object one : newProp.keySet()) {
            final String oneProp = (String)one;
            final String oldValue = this.origProp.getProperty(oneProp);
            if (!StringUtils.equals(oldValue, newProp.getProperty(oneProp))) {
                ret.setProperty(oneProp, newProp.getProperty(oneProp));
            }
        }
        return ret;
    }
    
    public String getJar() {
        return this.hiveJar;
    }
    
    public String getAuxJars() {
        return this.auxJars;
    }
    
    public void setAuxJars(final String auxJars) {
        this.auxJars = auxJars;
        setVar(this, ConfVars.HIVEAUXJARS, auxJars);
    }
    
    public URL getHiveDefaultLocation() {
        return HiveConf.hiveDefaultURL;
    }
    
    public static void setHiveSiteLocation(final URL location) {
        HiveConf.hiveSiteURL = location;
    }
    
    public static URL getHiveSiteLocation() {
        return HiveConf.hiveSiteURL;
    }
    
    public static URL getMetastoreSiteLocation() {
        return HiveConf.hivemetastoreSiteUrl;
    }
    
    public static URL getHiveServer2SiteLocation() {
        return HiveConf.hiveServer2SiteUrl;
    }
    
    public String getUser() throws IOException {
        try {
            final UserGroupInformation ugi = Utils.getUGI();
            return ugi.getUserName();
        }
        catch (LoginException le) {
            throw new IOException(le);
        }
    }
    
    public static String getColumnInternalName(final int pos) {
        return "_col" + pos;
    }
    
    public static int getPositionFromInternalName(final String internalName) {
        final Pattern internalPattern = Pattern.compile("_col([0-9]+)");
        final Matcher m = internalPattern.matcher(internalName);
        if (!m.matches()) {
            return -1;
        }
        return Integer.parseInt(m.group(1));
    }
    
    public void addToRestrictList(final String restrictListStr) {
        if (restrictListStr == null) {
            return;
        }
        final String oldList = this.getVar(ConfVars.HIVE_CONF_RESTRICTED_LIST);
        if (oldList == null || oldList.isEmpty()) {
            this.setVar(ConfVars.HIVE_CONF_RESTRICTED_LIST, restrictListStr);
        }
        else {
            this.setVar(ConfVars.HIVE_CONF_RESTRICTED_LIST, oldList + "," + restrictListStr);
        }
        this.setupRestrictList();
    }
    
    @InterfaceAudience.LimitedPrivate({ "Currently only for use by HiveAuthorizer" })
    public void setModifiableWhiteListRegex(final String paramNameRegex) {
        if (paramNameRegex == null) {
            return;
        }
        this.modWhiteListPattern = Pattern.compile(paramNameRegex);
    }
    
    private void setupRestrictList() {
        final String restrictListStr = this.getVar(ConfVars.HIVE_CONF_RESTRICTED_LIST);
        this.restrictList.clear();
        if (restrictListStr != null) {
            for (final String entry : restrictListStr.split(",")) {
                this.restrictList.add(entry.trim());
            }
        }
        this.restrictList.add(ConfVars.HIVE_IN_TEST.varname);
        this.restrictList.add(ConfVars.HIVE_CONF_RESTRICTED_LIST.varname);
        this.restrictList.add(ConfVars.HIVE_CONF_HIDDEN_LIST.varname);
    }
    
    private void setupHiddenSet() {
        final String hiddenListStr = this.getVar(ConfVars.HIVE_CONF_HIDDEN_LIST);
        this.hiddenSet.clear();
        if (hiddenListStr != null) {
            for (final String entry : hiddenListStr.split(",")) {
                this.hiddenSet.add(entry.trim());
            }
        }
    }
    
    public void stripHiddenConfigurations(final Configuration conf) {
        for (final String name : this.hiddenSet) {
            if (conf.get(name) != null) {
                conf.set(name, "");
            }
        }
    }
    
    public static boolean isLoadMetastoreConfig() {
        return HiveConf.loadMetastoreConfig;
    }
    
    public static void setLoadMetastoreConfig(final boolean loadMetastoreConfig) {
        HiveConf.loadMetastoreConfig = loadMetastoreConfig;
    }
    
    public static boolean isLoadHiveServer2Config() {
        return HiveConf.loadHiveServer2Config;
    }
    
    public static void setLoadHiveServer2Config(final boolean loadHiveServer2Config) {
        HiveConf.loadHiveServer2Config = loadHiveServer2Config;
    }
    
    static {
        l4j = LogFactory.getLog(HiveConf.class);
        HiveConf.loadMetastoreConfig = false;
        HiveConf.loadHiveServer2Config = false;
        HiveConf.hiveDefaultURL = null;
        HiveConf.hiveSiteURL = null;
        HiveConf.hivemetastoreSiteUrl = null;
        HiveConf.hiveServer2SiteUrl = null;
        HiveConf.confVarByteArray = null;
        vars = new HashMap<String, ConfVars>();
        metaConfs = new HashMap<String, ConfVars>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = HiveConf.class.getClassLoader();
        }
        HiveConf.hiveDefaultURL = classLoader.getResource("hive-default.xml");
        HiveConf.hiveSiteURL = classLoader.getResource("hive-site.xml");
        HiveConf.hivemetastoreSiteUrl = classLoader.getResource("hivemetastore-site.xml");
        HiveConf.hiveServer2SiteUrl = classLoader.getResource("hiveserver2-site.xml");
        for (final ConfVars confVar : ConfVars.values()) {
            HiveConf.vars.put(confVar.varname, confVar);
        }
        metaVars = new ConfVars[] { ConfVars.METASTOREWAREHOUSE, ConfVars.METASTOREURIS, ConfVars.METASTORETHRIFTCONNECTIONRETRIES, ConfVars.METASTORETHRIFTFAILURERETRIES, ConfVars.METASTORE_CLIENT_CONNECT_RETRY_DELAY, ConfVars.METASTORE_CLIENT_SOCKET_TIMEOUT, ConfVars.METASTORE_CLIENT_SOCKET_LIFETIME, ConfVars.METASTOREPWD, ConfVars.METASTORECONNECTURLHOOK, ConfVars.METASTORECONNECTURLKEY, ConfVars.METASTORESERVERMINTHREADS, ConfVars.METASTORESERVERMAXTHREADS, ConfVars.METASTORE_TCP_KEEP_ALIVE, ConfVars.METASTORE_INT_ORIGINAL, ConfVars.METASTORE_INT_ARCHIVED, ConfVars.METASTORE_INT_EXTRACTED, ConfVars.METASTORE_KERBEROS_KEYTAB_FILE, ConfVars.METASTORE_KERBEROS_PRINCIPAL, ConfVars.METASTORE_USE_THRIFT_SASL, ConfVars.METASTORE_CACHE_PINOBJTYPES, ConfVars.METASTORE_CONNECTION_POOLING_TYPE, ConfVars.METASTORE_VALIDATE_TABLES, ConfVars.METASTORE_VALIDATE_COLUMNS, ConfVars.METASTORE_VALIDATE_CONSTRAINTS, ConfVars.METASTORE_STORE_MANAGER_TYPE, ConfVars.METASTORE_AUTO_CREATE_SCHEMA, ConfVars.METASTORE_AUTO_START_MECHANISM_MODE, ConfVars.METASTORE_TRANSACTION_ISOLATION, ConfVars.METASTORE_CACHE_LEVEL2, ConfVars.METASTORE_CACHE_LEVEL2_TYPE, ConfVars.METASTORE_IDENTIFIER_FACTORY, ConfVars.METASTORE_PLUGIN_REGISTRY_BUNDLE_CHECK, ConfVars.METASTORE_AUTHORIZATION_STORAGE_AUTH_CHECKS, ConfVars.METASTORE_BATCH_RETRIEVE_MAX, ConfVars.METASTORE_EVENT_LISTENERS, ConfVars.METASTORE_EVENT_CLEAN_FREQ, ConfVars.METASTORE_EVENT_EXPIRY_DURATION, ConfVars.METASTORE_FILTER_HOOK, ConfVars.METASTORE_RAW_STORE_IMPL, ConfVars.METASTORE_END_FUNCTION_LISTENERS, ConfVars.METASTORE_PART_INHERIT_TBL_PROPS, ConfVars.METASTORE_BATCH_RETRIEVE_TABLE_PARTITION_MAX, ConfVars.METASTORE_INIT_HOOKS, ConfVars.METASTORE_PRE_EVENT_LISTENERS, ConfVars.HMSHANDLERATTEMPTS, ConfVars.HMSHANDLERINTERVAL, ConfVars.HMSHANDLERFORCERELOADCONF, ConfVars.METASTORE_PARTITION_NAME_WHITELIST_PATTERN, ConfVars.METASTORE_ORM_RETRIEVE_MAPNULLS_AS_EMPTY_STRINGS, ConfVars.METASTORE_DISALLOW_INCOMPATIBLE_COL_TYPE_CHANGES, ConfVars.USERS_IN_ADMIN_ROLE, ConfVars.HIVE_AUTHORIZATION_MANAGER, ConfVars.HIVE_TXN_MANAGER, ConfVars.HIVE_TXN_TIMEOUT, ConfVars.HIVE_TXN_MAX_OPEN_BATCH, ConfVars.HIVE_METASTORE_STATS_NDV_DENSITY_FUNCTION, ConfVars.METASTORE_AGGREGATE_STATS_CACHE_ENABLED, ConfVars.METASTORE_AGGREGATE_STATS_CACHE_SIZE, ConfVars.METASTORE_AGGREGATE_STATS_CACHE_MAX_PARTITIONS, ConfVars.METASTORE_AGGREGATE_STATS_CACHE_FPP, ConfVars.METASTORE_AGGREGATE_STATS_CACHE_MAX_VARIANCE, ConfVars.METASTORE_AGGREGATE_STATS_CACHE_TTL, ConfVars.METASTORE_AGGREGATE_STATS_CACHE_MAX_WRITER_WAIT, ConfVars.METASTORE_AGGREGATE_STATS_CACHE_MAX_READER_WAIT, ConfVars.METASTORE_AGGREGATE_STATS_CACHE_MAX_FULL, ConfVars.METASTORE_AGGREGATE_STATS_CACHE_CLEAN_UNTIL };
        metaConfVars = new ConfVars[] { ConfVars.METASTORE_TRY_DIRECT_SQL, ConfVars.METASTORE_TRY_DIRECT_SQL_DDL, ConfVars.METASTORE_CLIENT_SOCKET_TIMEOUT };
        for (final ConfVars confVar2 : HiveConf.metaConfVars) {
            HiveConf.metaConfs.put(confVar2.varname, confVar2);
        }
        dbVars = new ConfVars[] { ConfVars.HADOOPBIN, ConfVars.METASTOREWAREHOUSE, ConfVars.SCRATCHDIR };
        sqlStdAuthSafeVarNames = new String[] { ConfVars.BYTESPERREDUCER.varname, ConfVars.CLIENT_STATS_COUNTERS.varname, ConfVars.DEFAULTPARTITIONNAME.varname, ConfVars.DROPIGNORESNONEXISTENT.varname, ConfVars.HIVECOUNTERGROUP.varname, ConfVars.HIVEDEFAULTMANAGEDFILEFORMAT.varname, ConfVars.HIVEENFORCEBUCKETING.varname, ConfVars.HIVEENFORCEBUCKETMAPJOIN.varname, ConfVars.HIVEENFORCESORTING.varname, ConfVars.HIVEENFORCESORTMERGEBUCKETMAPJOIN.varname, ConfVars.HIVEEXPREVALUATIONCACHE.varname, ConfVars.HIVEHASHTABLELOADFACTOR.varname, ConfVars.HIVEHASHTABLETHRESHOLD.varname, ConfVars.HIVEIGNOREMAPJOINHINT.varname, ConfVars.HIVELIMITMAXROWSIZE.varname, ConfVars.HIVEMAPREDMODE.varname, ConfVars.HIVEMAPSIDEAGGREGATE.varname, ConfVars.HIVEOPTIMIZEMETADATAQUERIES.varname, ConfVars.HIVEROWOFFSET.varname, ConfVars.HIVEVARIABLESUBSTITUTE.varname, ConfVars.HIVEVARIABLESUBSTITUTEDEPTH.varname, ConfVars.HIVE_AUTOGEN_COLUMNALIAS_PREFIX_INCLUDEFUNCNAME.varname, ConfVars.HIVE_AUTOGEN_COLUMNALIAS_PREFIX_LABEL.varname, ConfVars.HIVE_CHECK_CROSS_PRODUCT.varname, ConfVars.HIVE_COMPAT.varname, ConfVars.HIVE_CONCATENATE_CHECK_INDEX.varname, ConfVars.HIVE_DISPLAY_PARTITION_COLUMNS_SEPARATELY.varname, ConfVars.HIVE_ERROR_ON_EMPTY_PARTITION.varname, ConfVars.HIVE_EXECUTION_ENGINE.varname, ConfVars.HIVE_EXIM_URI_SCHEME_WL.varname, ConfVars.HIVE_FILE_MAX_FOOTER.varname, ConfVars.HIVE_HADOOP_SUPPORTS_SUBDIRECTORIES.varname, ConfVars.HIVE_INSERT_INTO_MULTILEVEL_DIRS.varname, ConfVars.HIVE_LOCALIZE_RESOURCE_NUM_WAIT_ATTEMPTS.varname, ConfVars.HIVE_MULTI_INSERT_MOVE_TASKS_SHARE_DEPENDENCIES.varname, ConfVars.HIVE_QUOTEDID_SUPPORT.varname, ConfVars.HIVE_RESULTSET_USE_UNIQUE_COLUMN_NAMES.varname, ConfVars.HIVE_STATS_COLLECT_PART_LEVEL_STATS.varname, ConfVars.HIVE_SERVER2_LOGGING_OPERATION_LEVEL.varname, ConfVars.HIVE_SUPPORT_SQL11_RESERVED_KEYWORDS.varname, ConfVars.JOB_DEBUG_CAPTURE_STACKTRACES.varname, ConfVars.JOB_DEBUG_TIMEOUT.varname, ConfVars.MAXCREATEDFILES.varname, ConfVars.MAXREDUCERS.varname, ConfVars.NWAYJOINREORDER.varname, ConfVars.OUTPUT_FILE_EXTENSION.varname, ConfVars.SHOW_JOB_FAIL_DEBUG_INFO.varname, ConfVars.TASKLOG_DEBUG_TIMEOUT.varname };
        sqlStdAuthSafeVarNameRegexes = new String[] { "hive\\.auto\\..*", "hive\\.cbo\\..*", "hive\\.convert\\..*", "hive\\.exec\\.dynamic\\.partition.*", "hive\\.exec\\..*\\.dynamic\\.partitions\\..*", "hive\\.exec\\.compress\\..*", "hive\\.exec\\.infer\\..*", "hive\\.exec\\.mode.local\\..*", "hive\\.exec\\.orc\\..*", "hive\\.exec\\.parallel.*", "hive\\.explain\\..*", "hive\\.fetch.task\\..*", "hive\\.groupby\\..*", "hive\\.hbase\\..*", "hive\\.index\\..*", "hive\\.index\\..*", "hive\\.intermediate\\..*", "hive\\.join\\..*", "hive\\.limit\\..*", "hive\\.log\\..*", "hive\\.mapjoin\\..*", "hive\\.merge\\..*", "hive\\.optimize\\..*", "hive\\.orc\\..*", "hive\\.outerjoin\\..*", "hive\\.parquet\\..*", "hive\\.ppd\\..*", "hive\\.prewarm\\..*", "hive\\.server2\\.proxy\\.user", "hive\\.skewjoin\\..*", "hive\\.smbjoin\\..*", "hive\\.stats\\..*", "hive\\.tez\\..*", "hive\\.vectorized\\..*", "mapred\\.map\\..*", "mapred\\.reduce\\..*", "mapred\\.output\\.compression\\.codec", "mapred\\.job\\.queuename", "mapred\\.output\\.compression\\.type", "mapred\\.min\\.split\\.size", "mapreduce\\.job\\.reduce\\.slowstart\\.completedmaps", "mapreduce\\.job\\.queuename", "mapreduce\\.job\\.tags", "mapreduce\\.input\\.fileinputformat\\.split\\.minsize", "mapreduce\\.map\\..*", "mapreduce\\.reduce\\..*", "mapreduce\\.output\\.fileoutputformat\\.compress\\.codec", "mapreduce\\.output\\.fileoutputformat\\.compress\\.type", "tez\\.am\\..*", "tez\\.task\\..*", "tez\\.runtime\\..*", "tez.queue.name" };
    }
    
    public enum ConfVars
    {
        SCRIPTWRAPPER("hive.exec.script.wrapper", (Object)null, ""), 
        PLAN("hive.exec.plan", (Object)"", ""), 
        PLAN_SERIALIZATION("hive.plan.serialization.format", (Object)"kryo", "Query plan format serialization between client and task nodes. \nTwo supported values are : kryo and javaXML. Kryo is default."), 
        STAGINGDIR("hive.exec.stagingdir", (Object)".hive-staging", "Directory name that will be created inside table locations in order to support HDFS encryption. This is replaces ${hive.exec.scratchdir} for query results with the exception of read-only tables. In all cases ${hive.exec.scratchdir} is still used for other temporary files, such as job plans."), 
        SCRATCHDIR("hive.exec.scratchdir", (Object)"/tmp/hive", "HDFS root scratch dir for Hive jobs which gets created with write all (733) permission. For each connecting user, an HDFS scratch dir: ${hive.exec.scratchdir}/<username> is created, with ${hive.scratch.dir.permission}."), 
        LOCALSCRATCHDIR("hive.exec.local.scratchdir", (Object)("${system:java.io.tmpdir}" + File.separator + "${system:user.name}"), "Local scratch space for Hive jobs"), 
        DOWNLOADED_RESOURCES_DIR("hive.downloaded.resources.dir", (Object)("${system:java.io.tmpdir}" + File.separator + "${hive.session.id}_resources"), "Temporary local directory for added resources in the remote file system."), 
        SCRATCHDIRPERMISSION("hive.scratch.dir.permission", (Object)"700", "The permission for the user specific scratch directories that get created."), 
        SUBMITVIACHILD("hive.exec.submitviachild", (Object)false, ""), 
        SUBMITLOCALTASKVIACHILD("hive.exec.submit.local.task.via.child", (Object)true, "Determines whether local tasks (typically mapjoin hashtable generation phase) runs in \nseparate JVM (true recommended) or not. \nAvoids the overhead of spawning new JVM, but can lead to out-of-memory issues."), 
        SCRIPTERRORLIMIT("hive.exec.script.maxerrsize", (Object)100000, "Maximum number of bytes a script is allowed to emit to standard error (per map-reduce task). \nThis prevents runaway scripts from filling logs partitions to capacity"), 
        ALLOWPARTIALCONSUMP("hive.exec.script.allow.partial.consumption", (Object)false, "When enabled, this option allows a user script to exit successfully without consuming \nall the data from the standard input."), 
        STREAMREPORTERPERFIX("stream.stderr.reporter.prefix", (Object)"reporter:", "Streaming jobs that log to standard error with this prefix can log counter or status information."), 
        STREAMREPORTERENABLED("stream.stderr.reporter.enabled", (Object)true, "Enable consumption of status and counter messages for streaming jobs."), 
        COMPRESSRESULT("hive.exec.compress.output", (Object)false, "This controls whether the final outputs of a query (to a local/HDFS file or a Hive table) is compressed. \nThe compression codec and other options are determined from Hadoop config variables mapred.output.compress*"), 
        COMPRESSINTERMEDIATE("hive.exec.compress.intermediate", (Object)false, "This controls whether intermediate files produced by Hive between multiple map-reduce jobs are compressed. \nThe compression codec and other options are determined from Hadoop config variables mapred.output.compress*"), 
        COMPRESSINTERMEDIATECODEC("hive.intermediate.compression.codec", (Object)"", ""), 
        COMPRESSINTERMEDIATETYPE("hive.intermediate.compression.type", (Object)"", ""), 
        BYTESPERREDUCER("hive.exec.reducers.bytes.per.reducer", (Object)256000000L, "size per reducer.The default is 256Mb, i.e if the input size is 1G, it will use 4 reducers."), 
        MAXREDUCERS("hive.exec.reducers.max", (Object)1009, "max number of reducers will be used. If the one specified in the configuration parameter mapred.reduce.tasks is\nnegative, Hive will use this one as the max number of reducers when automatically determine number of reducers."), 
        PREEXECHOOKS("hive.exec.pre.hooks", (Object)"", "Comma-separated list of pre-execution hooks to be invoked for each statement. \nA pre-execution hook is specified as the name of a Java class which implements the \norg.apache.hadoop.hive.ql.hooks.ExecuteWithHookContext interface."), 
        POSTEXECHOOKS("hive.exec.post.hooks", (Object)"", "Comma-separated list of post-execution hooks to be invoked for each statement. \nA post-execution hook is specified as the name of a Java class which implements the \norg.apache.hadoop.hive.ql.hooks.ExecuteWithHookContext interface."), 
        ONFAILUREHOOKS("hive.exec.failure.hooks", (Object)"", "Comma-separated list of on-failure hooks to be invoked for each statement. \nAn on-failure hook is specified as the name of Java class which implements the \norg.apache.hadoop.hive.ql.hooks.ExecuteWithHookContext interface."), 
        QUERYREDACTORHOOKS("hive.exec.query.redactor.hooks", (Object)"", "Comma-separated list of hooks to be invoked for each query which can \ntranform the query before it's placed in the job.xml file. Must be a Java class which \nextends from the org.apache.hadoop.hive.ql.hooks.Redactor abstract class."), 
        CLIENTSTATSPUBLISHERS("hive.client.stats.publishers", (Object)"", "Comma-separated list of statistics publishers to be invoked on counters on each job. \nA client stats publisher is specified as the name of a Java class which implements the \norg.apache.hadoop.hive.ql.stats.ClientStatsPublisher interface."), 
        EXECPARALLEL("hive.exec.parallel", (Object)false, "Whether to execute jobs in parallel"), 
        EXECPARALLETHREADNUMBER("hive.exec.parallel.thread.number", (Object)8, "How many jobs at most can be executed in parallel"), 
        HIVESPECULATIVEEXECREDUCERS("hive.mapred.reduce.tasks.speculative.execution", (Object)true, "Whether speculative execution for reducers should be turned on. "), 
        HIVECOUNTERSPULLINTERVAL("hive.exec.counters.pull.interval", (Object)1000L, "The interval with which to poll the JobTracker for the counters the running job. \nThe smaller it is the more load there will be on the jobtracker, the higher it is the less granular the caught will be."), 
        DYNAMICPARTITIONING("hive.exec.dynamic.partition", (Object)true, "Whether or not to allow dynamic partitions in DML/DDL."), 
        DYNAMICPARTITIONINGMODE("hive.exec.dynamic.partition.mode", (Object)"strict", "In strict mode, the user must specify at least one static partition\nin case the user accidentally overwrites all partitions.\nIn nonstrict mode all partitions are allowed to be dynamic."), 
        DYNAMICPARTITIONMAXPARTS("hive.exec.max.dynamic.partitions", (Object)1000, "Maximum number of dynamic partitions allowed to be created in total."), 
        DYNAMICPARTITIONMAXPARTSPERNODE("hive.exec.max.dynamic.partitions.pernode", (Object)100, "Maximum number of dynamic partitions allowed to be created in each mapper/reducer node."), 
        MAXCREATEDFILES("hive.exec.max.created.files", (Object)100000L, "Maximum number of HDFS files created by all mappers/reducers in a MapReduce job."), 
        DEFAULTPARTITIONNAME("hive.exec.default.partition.name", (Object)"__HIVE_DEFAULT_PARTITION__", "The default partition name in case the dynamic partition column value is null/empty string or any other values that cannot be escaped. \nThis value must not contain any special character used in HDFS URI (e.g., ':', '%', '/' etc). \nThe user has to be aware that the dynamic partition value should not contain this value to avoid confusions."), 
        DEFAULT_ZOOKEEPER_PARTITION_NAME("hive.lockmgr.zookeeper.default.partition.name", (Object)"__HIVE_DEFAULT_ZOOKEEPER_PARTITION__", ""), 
        SHOW_JOB_FAIL_DEBUG_INFO("hive.exec.show.job.failure.debug.info", (Object)true, "If a job fails, whether to provide a link in the CLI to the task with the\nmost failures, along with debugging hints if applicable."), 
        JOB_DEBUG_CAPTURE_STACKTRACES("hive.exec.job.debug.capture.stacktraces", (Object)true, "Whether or not stack traces parsed from the task logs of a sampled failed task \nfor each failed job should be stored in the SessionState"), 
        JOB_DEBUG_TIMEOUT("hive.exec.job.debug.timeout", (Object)30000, ""), 
        TASKLOG_DEBUG_TIMEOUT("hive.exec.tasklog.debug.timeout", (Object)20000, ""), 
        OUTPUT_FILE_EXTENSION("hive.output.file.extension", (Object)null, "String used as a file extension for output files. \nIf not set, defaults to the codec extension for text files (e.g. \".gz\"), or no extension otherwise."), 
        HIVE_IN_TEST("hive.in.test", (Object)false, "internal usage only, true in test mode", true), 
        HIVE_IN_TEZ_TEST("hive.in.tez.test", (Object)false, "internal use only, true when in testing tez", true), 
        LOCALMODEAUTO("hive.exec.mode.local.auto", (Object)false, "Let Hive determine whether to run in local mode automatically"), 
        LOCALMODEMAXBYTES("hive.exec.mode.local.auto.inputbytes.max", (Object)134217728L, "When hive.exec.mode.local.auto is true, input bytes should less than this for local mode."), 
        LOCALMODEMAXINPUTFILES("hive.exec.mode.local.auto.input.files.max", (Object)4, "When hive.exec.mode.local.auto is true, the number of tasks should less than this for local mode."), 
        DROPIGNORESNONEXISTENT("hive.exec.drop.ignorenonexistent", (Object)true, "Do not report an error if DROP TABLE/VIEW/Index/Function specifies a non-existent table/view/index/function"), 
        HIVEIGNOREMAPJOINHINT("hive.ignore.mapjoin.hint", (Object)true, "Ignore the mapjoin hint"), 
        HIVE_FILE_MAX_FOOTER("hive.file.max.footer", (Object)100, "maximum number of lines for footer user can define for a table file"), 
        HIVE_RESULTSET_USE_UNIQUE_COLUMN_NAMES("hive.resultset.use.unique.column.names", (Object)true, "Make column names unique in the result set by qualifying column names with table alias if needed.\nTable alias will be added to column names for queries of type \"select *\" or \nif query explicitly uses table alias \"select r1.x..\"."), 
        HADOOPBIN("hadoop.bin.path", (Object)findHadoopBinary(), "", true), 
        HIVE_FS_HAR_IMPL("fs.har.impl", (Object)"org.apache.hadoop.hive.shims.HiveHarFileSystem", "The implementation for accessing Hadoop Archives. Note that this won't be applicable to Hadoop versions less than 0.20"), 
        HADOOPFS((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("HADOOPFS"), (Object)null, "", true), 
        HADOOPMAPFILENAME((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("HADOOPMAPFILENAME"), (Object)null, "", true), 
        HADOOPMAPREDINPUTDIR((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("HADOOPMAPREDINPUTDIR"), (Object)null, "", true), 
        HADOOPMAPREDINPUTDIRRECURSIVE((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("HADOOPMAPREDINPUTDIRRECURSIVE"), (Object)false, "", true), 
        MAPREDMAXSPLITSIZE((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("MAPREDMAXSPLITSIZE"), (Object)256000000L, "", true), 
        MAPREDMINSPLITSIZE((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("MAPREDMINSPLITSIZE"), (Object)1L, "", true), 
        MAPREDMINSPLITSIZEPERNODE((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("MAPREDMINSPLITSIZEPERNODE"), (Object)1L, "", true), 
        MAPREDMINSPLITSIZEPERRACK((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("MAPREDMINSPLITSIZEPERRACK"), (Object)1L, "", true), 
        HADOOPNUMREDUCERS((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("HADOOPNUMREDUCERS"), (Object)(-1), "", true), 
        HADOOPJOBNAME((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("HADOOPJOBNAME"), (Object)null, "", true), 
        HADOOPSPECULATIVEEXECREDUCERS((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("HADOOPSPECULATIVEEXECREDUCERS"), (Object)true, "", true), 
        MAPREDSETUPCLEANUPNEEDED((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("MAPREDSETUPCLEANUPNEEDED"), (Object)false, "", true), 
        MAPREDTASKCLEANUPNEEDED((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("MAPREDTASKCLEANUPNEEDED"), (Object)false, "", true), 
        METASTOREWAREHOUSE("hive.metastore.warehouse.dir", (Object)"/user/hive/warehouse", "location of default database for the warehouse"), 
        METASTOREURIS("hive.metastore.uris", (Object)"", "Thrift URI for the remote metastore. Used by metastore client to connect to remote metastore."), 
        METASTORETHRIFTCONNECTIONRETRIES("hive.metastore.connect.retries", (Object)3, "Number of retries while opening a connection to metastore"), 
        METASTORETHRIFTFAILURERETRIES("hive.metastore.failure.retries", (Object)1, "Number of retries upon failure of Thrift metastore calls"), 
        METASTORE_CLIENT_CONNECT_RETRY_DELAY("hive.metastore.client.connect.retry.delay", (Object)"1s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Number of seconds for the client to wait between consecutive connection attempts"), 
        METASTORE_CLIENT_SOCKET_TIMEOUT("hive.metastore.client.socket.timeout", (Object)"600s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "MetaStore Client socket timeout in seconds"), 
        METASTORE_CLIENT_SOCKET_LIFETIME("hive.metastore.client.socket.lifetime", (Object)"0s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "MetaStore Client socket lifetime in seconds. After this time is exceeded, client\nreconnects on the next MetaStore operation. A value of 0s means the connection\nhas an infinite lifetime."), 
        METASTOREPWD("javax.jdo.option.ConnectionPassword", (Object)"mine", "password to use against metastore database"), 
        METASTORECONNECTURLHOOK("hive.metastore.ds.connection.url.hook", (Object)"", "Name of the hook to use for retrieving the JDO connection URL. If empty, the value in javax.jdo.option.ConnectionURL is used"), 
        METASTOREMULTITHREADED("javax.jdo.option.Multithreaded", (Object)true, "Set this to true if multiple threads access metastore through JDO concurrently."), 
        METASTORECONNECTURLKEY("javax.jdo.option.ConnectionURL", (Object)"jdbc:derby:;databaseName=metastore_db;create=true", "JDBC connect string for a JDBC metastore"), 
        HMSHANDLERATTEMPTS("hive.hmshandler.retry.attempts", (Object)10, "The number of times to retry a HMSHandler call if there were a connection error."), 
        HMSHANDLERINTERVAL("hive.hmshandler.retry.interval", (Object)"2000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "The time between HMSHandler retry attempts on failure."), 
        HMSHANDLERFORCERELOADCONF("hive.hmshandler.force.reload.conf", (Object)false, "Whether to force reloading of the HMSHandler configuration (including\nthe connection URL, before the next metastore query that accesses the\ndatastore. Once reloaded, this value is reset to false. Used for\ntesting only."), 
        METASTORESERVERMAXMESSAGESIZE("hive.metastore.server.max.message.size", (Object)104857600, "Maximum message size in bytes a HMS will accept."), 
        METASTORESERVERMINTHREADS("hive.metastore.server.min.threads", (Object)200, "Minimum number of worker threads in the Thrift server's pool."), 
        METASTORESERVERMAXTHREADS("hive.metastore.server.max.threads", (Object)1000, "Maximum number of worker threads in the Thrift server's pool."), 
        METASTORE_TCP_KEEP_ALIVE("hive.metastore.server.tcp.keepalive", (Object)true, "Whether to enable TCP keepalive for the metastore server. Keepalive will prevent accumulation of half-open connections."), 
        METASTORE_INT_ORIGINAL("hive.metastore.archive.intermediate.original", (Object)"_INTERMEDIATE_ORIGINAL", "Intermediate dir suffixes used for archiving. Not important what they\nare, as long as collisions are avoided"), 
        METASTORE_INT_ARCHIVED("hive.metastore.archive.intermediate.archived", (Object)"_INTERMEDIATE_ARCHIVED", ""), 
        METASTORE_INT_EXTRACTED("hive.metastore.archive.intermediate.extracted", (Object)"_INTERMEDIATE_EXTRACTED", ""), 
        METASTORE_KERBEROS_KEYTAB_FILE("hive.metastore.kerberos.keytab.file", (Object)"", "The path to the Kerberos Keytab file containing the metastore Thrift server's service principal."), 
        METASTORE_KERBEROS_PRINCIPAL("hive.metastore.kerberos.principal", (Object)"hive-metastore/_HOST@EXAMPLE.COM", "The service principal for the metastore Thrift server. \nThe special string _HOST will be replaced automatically with the correct host name."), 
        METASTORE_USE_THRIFT_SASL("hive.metastore.sasl.enabled", (Object)false, "If true, the metastore Thrift interface will be secured with SASL. Clients must authenticate with Kerberos."), 
        METASTORE_USE_THRIFT_FRAMED_TRANSPORT("hive.metastore.thrift.framed.transport.enabled", (Object)false, "If true, the metastore Thrift interface will use TFramedTransport. When false (default) a standard TTransport is used."), 
        METASTORE_USE_THRIFT_COMPACT_PROTOCOL("hive.metastore.thrift.compact.protocol.enabled", (Object)false, "If true, the metastore Thrift interface will use TCompactProtocol. When false (default) TBinaryProtocol will be used.\nSetting it to true will break compatibility with older clients running TBinaryProtocol."), 
        METASTORE_CLUSTER_DELEGATION_TOKEN_STORE_CLS("hive.cluster.delegation.token.store.class", (Object)"org.apache.hadoop.hive.thrift.MemoryTokenStore", "The delegation token store implementation. Set to org.apache.hadoop.hive.thrift.ZooKeeperTokenStore for load-balanced cluster."), 
        METASTORE_CLUSTER_DELEGATION_TOKEN_STORE_ZK_CONNECTSTR("hive.cluster.delegation.token.store.zookeeper.connectString", (Object)"", "The ZooKeeper token store connect string. You can re-use the configuration value\nset in hive.zookeeper.quorum, by leaving this parameter unset."), 
        METASTORE_CLUSTER_DELEGATION_TOKEN_STORE_ZK_ZNODE("hive.cluster.delegation.token.store.zookeeper.znode", (Object)"/hivedelegation", "The root path for token store data. Note that this is used by both HiveServer2 and\nMetaStore to store delegation Token. One directory gets created for each of them.\nThe final directory names would have the servername appended to it (HIVESERVER2,\nMETASTORE)."), 
        METASTORE_CLUSTER_DELEGATION_TOKEN_STORE_ZK_ACL("hive.cluster.delegation.token.store.zookeeper.acl", (Object)"", "ACL for token store entries. Comma separated list of ACL entries. For example:\nsasl:hive/host1@MY.DOMAIN:cdrwa,sasl:hive/host2@MY.DOMAIN:cdrwa\nDefaults to all permissions for the hiveserver2/metastore process user."), 
        METASTORE_CACHE_PINOBJTYPES("hive.metastore.cache.pinobjtypes", (Object)"Table,StorageDescriptor,SerDeInfo,Partition,Database,Type,FieldSchema,Order", "List of comma separated metastore object types that should be pinned in the cache"), 
        METASTORE_CONNECTION_POOLING_TYPE("datanucleus.connectionPoolingType", (Object)"BONECP", "Specify connection pool library for datanucleus"), 
        METASTORE_VALIDATE_TABLES("datanucleus.validateTables", (Object)false, "validates existing schema against code. turn this on if you want to verify existing schema"), 
        METASTORE_VALIDATE_COLUMNS("datanucleus.validateColumns", (Object)false, "validates existing schema against code. turn this on if you want to verify existing schema"), 
        METASTORE_VALIDATE_CONSTRAINTS("datanucleus.validateConstraints", (Object)false, "validates existing schema against code. turn this on if you want to verify existing schema"), 
        METASTORE_STORE_MANAGER_TYPE("datanucleus.storeManagerType", (Object)"rdbms", "metadata store type"), 
        METASTORE_AUTO_CREATE_SCHEMA("datanucleus.autoCreateSchema", (Object)true, "creates necessary schema on a startup if one doesn't exist. set this to false, after creating it once"), 
        METASTORE_FIXED_DATASTORE("datanucleus.fixedDatastore", (Object)false, ""), 
        METASTORE_SCHEMA_VERIFICATION("hive.metastore.schema.verification", (Object)false, "Enforce metastore schema version consistency.\nTrue: Verify that version information stored in metastore matches with one from Hive jars.  Also disable automatic\n      schema migration attempt. Users are required to manually migrate schema after Hive upgrade which ensures\n      proper metastore schema migration. (Default)\nFalse: Warn if the version information stored in metastore doesn't match with one from in Hive jars."), 
        METASTORE_SCHEMA_VERIFICATION_RECORD_VERSION("hive.metastore.schema.verification.record.version", (Object)true, "When true the current MS version is recorded in the VERSION table. If this is disabled and verification is\n enabled the MS will be unusable."), 
        METASTORE_AUTO_START_MECHANISM_MODE("datanucleus.autoStartMechanismMode", (Object)"checked", "throw exception if metadata tables are incorrect"), 
        METASTORE_TRANSACTION_ISOLATION("datanucleus.transactionIsolation", (Object)"read-committed", "Default transaction isolation level for identity generation."), 
        METASTORE_CACHE_LEVEL2("datanucleus.cache.level2", (Object)false, "Use a level 2 cache. Turn this off if metadata is changed independently of Hive metastore server"), 
        METASTORE_CACHE_LEVEL2_TYPE("datanucleus.cache.level2.type", (Object)"none", ""), 
        METASTORE_IDENTIFIER_FACTORY("datanucleus.identifierFactory", (Object)"datanucleus1", "Name of the identifier factory to use when generating table/column names etc. \n'datanucleus1' is used for backward compatibility with DataNucleus v1"), 
        METASTORE_USE_LEGACY_VALUE_STRATEGY("datanucleus.rdbms.useLegacyNativeValueStrategy", (Object)true, ""), 
        METASTORE_PLUGIN_REGISTRY_BUNDLE_CHECK("datanucleus.plugin.pluginRegistryBundleCheck", (Object)"LOG", "Defines what happens when plugin bundles are found and are duplicated [EXCEPTION|LOG|NONE]"), 
        METASTORE_BATCH_RETRIEVE_MAX("hive.metastore.batch.retrieve.max", (Object)300, "Maximum number of objects (tables/partitions) can be retrieved from metastore in one batch. \nThe higher the number, the less the number of round trips is needed to the Hive metastore server, \nbut it may also cause higher memory requirement at the client side."), 
        METASTORE_BATCH_RETRIEVE_TABLE_PARTITION_MAX("hive.metastore.batch.retrieve.table.partition.max", (Object)1000, "Maximum number of table partitions that metastore internally retrieves in one batch."), 
        METASTORE_INIT_HOOKS("hive.metastore.init.hooks", (Object)"", "A comma separated list of hooks to be invoked at the beginning of HMSHandler initialization. \nAn init hook is specified as the name of Java class which extends org.apache.hadoop.hive.metastore.MetaStoreInitListener."), 
        METASTORE_PRE_EVENT_LISTENERS("hive.metastore.pre.event.listeners", (Object)"", "List of comma separated listeners for metastore events."), 
        METASTORE_EVENT_LISTENERS("hive.metastore.event.listeners", (Object)"", ""), 
        METASTORE_EVENT_DB_LISTENER_TTL("hive.metastore.event.db.listener.timetolive", (Object)"86400s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "time after which events will be removed from the database listener queue"), 
        METASTORE_AUTHORIZATION_STORAGE_AUTH_CHECKS("hive.metastore.authorization.storage.checks", (Object)false, "Should the metastore do authorization checks against the underlying storage (usually hdfs) \nfor operations like drop-partition (disallow the drop-partition if the user in\nquestion doesn't have permissions to delete the corresponding directory\non the storage)."), 
        METASTORE_EVENT_CLEAN_FREQ("hive.metastore.event.clean.freq", (Object)"0s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Frequency at which timer task runs to purge expired events in metastore."), 
        METASTORE_EVENT_EXPIRY_DURATION("hive.metastore.event.expiry.duration", (Object)"0s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Duration after which events expire from events table"), 
        METASTORE_EXECUTE_SET_UGI("hive.metastore.execute.setugi", (Object)true, "In unsecure mode, setting this property to true will cause the metastore to execute DFS operations using \nthe client's reported user and group permissions. Note that this property must be set on \nboth the client and server sides. Further note that its best effort. \nIf client sets its to true and server sets it to false, client setting will be ignored."), 
        METASTORE_PARTITION_NAME_WHITELIST_PATTERN("hive.metastore.partition.name.whitelist.pattern", (Object)"", "Partition names will be checked against this regex pattern and rejected if not matched."), 
        METASTORE_INTEGER_JDO_PUSHDOWN("hive.metastore.integral.jdo.pushdown", (Object)false, "Allow JDO query pushdown for integral partition columns in metastore. Off by default. This\nimproves metastore perf for integral columns, especially if there's a large number of partitions.\nHowever, it doesn't work correctly with integral values that are not normalized (e.g. have\nleading zeroes, like 0012). If metastore direct SQL is enabled and works, this optimization\nis also irrelevant."), 
        METASTORE_TRY_DIRECT_SQL("hive.metastore.try.direct.sql", (Object)true, "Whether the Hive metastore should try to use direct SQL queries instead of the\nDataNucleus for certain read paths. This can improve metastore performance when\nfetching many partitions or column statistics by orders of magnitude; however, it\nis not guaranteed to work on all RDBMS-es and all versions. In case of SQL failures,\nthe metastore will fall back to the DataNucleus, so it's safe even if SQL doesn't\nwork for all queries on your datastore. If all SQL queries fail (for example, your\nmetastore is backed by MongoDB), you might want to disable this to save the\ntry-and-fall-back cost."), 
        METASTORE_DIRECT_SQL_PARTITION_BATCH_SIZE("hive.metastore.direct.sql.batch.size", (Object)0, "Batch size for partition and other object retrieval from the underlying DB in direct\nSQL. For some DBs like Oracle and MSSQL, there are hardcoded or perf-based limitations\nthat necessitate this. For DBs that can handle the queries, this isn't necessary and\nmay impede performance. -1 means no batching, 0 means automatic batching."), 
        METASTORE_TRY_DIRECT_SQL_DDL("hive.metastore.try.direct.sql.ddl", (Object)true, "Same as hive.metastore.try.direct.sql, for read statements within a transaction that\nmodifies metastore data. Due to non-standard behavior in Postgres, if a direct SQL\nselect query has incorrect syntax or something similar inside a transaction, the\nentire transaction will fail and fall-back to DataNucleus will not be possible. You\nshould disable the usage of direct SQL inside transactions if that happens in your case."), 
        METASTORE_ORM_RETRIEVE_MAPNULLS_AS_EMPTY_STRINGS("hive.metastore.orm.retrieveMapNullsAsEmptyStrings", (Object)false, "Thrift does not support nulls in maps, so any nulls present in maps retrieved from ORM must either be pruned or converted to empty strings. Some backing dbs such as Oracle persist empty strings as nulls, so we should set this parameter if we wish to reverse that behaviour. For others, pruning is the correct behaviour"), 
        METASTORE_DISALLOW_INCOMPATIBLE_COL_TYPE_CHANGES("hive.metastore.disallow.incompatible.col.type.changes", (Object)false, "If true (default is false), ALTER TABLE operations which change the type of a\ncolumn (say STRING) to an incompatible type (say MAP) are disallowed.\nRCFile default SerDe (ColumnarSerDe) serializes the values in such a way that the\ndatatypes can be converted from string to any type. The map is also serialized as\na string, which can be read as a string as well. However, with any binary\nserialization, this is not true. Blocking the ALTER TABLE prevents ClassCastExceptions\nwhen subsequently trying to access old partitions.\n\nPrimitive types like INT, STRING, BIGINT, etc., are compatible with each other and are\nnot blocked.\n\nSee HIVE-4409 for more details."), 
        NEWTABLEDEFAULTPARA("hive.table.parameters.default", (Object)"", "Default property values for newly created tables"), 
        DDL_CTL_PARAMETERS_WHITELIST("hive.ddl.createtablelike.properties.whitelist", (Object)"", "Table Properties to copy over when executing a Create Table Like."), 
        METASTORE_RAW_STORE_IMPL("hive.metastore.rawstore.impl", (Object)"org.apache.hadoop.hive.metastore.ObjectStore", "Name of the class that implements org.apache.hadoop.hive.metastore.rawstore interface. \nThis class is used to store and retrieval of raw metadata objects such as table, database"), 
        METASTORE_CONNECTION_DRIVER("javax.jdo.option.ConnectionDriverName", (Object)"org.apache.derby.jdbc.EmbeddedDriver", "Driver class name for a JDBC metastore"), 
        METASTORE_MANAGER_FACTORY_CLASS("javax.jdo.PersistenceManagerFactoryClass", (Object)"org.datanucleus.api.jdo.JDOPersistenceManagerFactory", "class implementing the jdo persistence"), 
        METASTORE_EXPRESSION_PROXY_CLASS("hive.metastore.expression.proxy", (Object)"org.apache.hadoop.hive.ql.optimizer.ppr.PartitionExpressionForMetastore", ""), 
        METASTORE_DETACH_ALL_ON_COMMIT("javax.jdo.option.DetachAllOnCommit", (Object)true, "Detaches all objects from session so that they can be used after transaction is committed"), 
        METASTORE_NON_TRANSACTIONAL_READ("javax.jdo.option.NonTransactionalRead", (Object)true, "Reads outside of transactions"), 
        METASTORE_CONNECTION_USER_NAME("javax.jdo.option.ConnectionUserName", (Object)"APP", "Username to use against metastore database"), 
        METASTORE_END_FUNCTION_LISTENERS("hive.metastore.end.function.listeners", (Object)"", "List of comma separated listeners for the end of metastore functions."), 
        METASTORE_PART_INHERIT_TBL_PROPS("hive.metastore.partition.inherit.table.properties", (Object)"", "List of comma separated keys occurring in table properties which will get inherited to newly created partitions. \n* implies all the keys will get inherited."), 
        METASTORE_FILTER_HOOK("hive.metastore.filter.hook", (Object)"org.apache.hadoop.hive.metastore.DefaultMetaStoreFilterHookImpl", "Metastore hook class for filtering the metadata read results. If hive.security.authorization.manageris set to instance of HiveAuthorizerFactory, then this value is ignored."), 
        FIRE_EVENTS_FOR_DML("hive.metastore.dml.events", (Object)false, "If true, the metastore will be asked to fire events for DML operations"), 
        METASTORE_CLIENT_DROP_PARTITIONS_WITH_EXPRESSIONS("hive.metastore.client.drop.partitions.using.expressions", (Object)true, "Choose whether dropping partitions with HCatClient pushes the partition-predicate to the metastore, or drops partitions iteratively"), 
        METASTORE_AGGREGATE_STATS_CACHE_ENABLED("hive.metastore.aggregate.stats.cache.enabled", (Object)true, "Whether aggregate stats caching is enabled or not."), 
        METASTORE_AGGREGATE_STATS_CACHE_SIZE("hive.metastore.aggregate.stats.cache.size", (Object)10000, "Maximum number of aggregate stats nodes that we will place in the metastore aggregate stats cache."), 
        METASTORE_AGGREGATE_STATS_CACHE_MAX_PARTITIONS("hive.metastore.aggregate.stats.cache.max.partitions", (Object)10000, "Maximum number of partitions that are aggregated per cache node."), 
        METASTORE_AGGREGATE_STATS_CACHE_FPP("hive.metastore.aggregate.stats.cache.fpp", (Object)0.01f, "Maximum false positive probability for the Bloom Filter used in each aggregate stats cache node (default 1%)."), 
        METASTORE_AGGREGATE_STATS_CACHE_MAX_VARIANCE("hive.metastore.aggregate.stats.cache.max.variance", (Object)0.01f, "Maximum tolerable variance in number of partitions between a cached node and our request (default 1%)."), 
        METASTORE_AGGREGATE_STATS_CACHE_TTL("hive.metastore.aggregate.stats.cache.ttl", (Object)"600s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Number of seconds for a cached node to be active in the cache before they become stale."), 
        METASTORE_AGGREGATE_STATS_CACHE_MAX_WRITER_WAIT("hive.metastore.aggregate.stats.cache.max.writer.wait", (Object)"5000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Number of milliseconds a writer will wait to acquire the writelock before giving up."), 
        METASTORE_AGGREGATE_STATS_CACHE_MAX_READER_WAIT("hive.metastore.aggregate.stats.cache.max.reader.wait", (Object)"1000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Number of milliseconds a reader will wait to acquire the readlock before giving up."), 
        METASTORE_AGGREGATE_STATS_CACHE_MAX_FULL("hive.metastore.aggregate.stats.cache.max.full", (Object)0.9f, "Maximum cache full % after which the cache cleaner thread kicks in."), 
        METASTORE_AGGREGATE_STATS_CACHE_CLEAN_UNTIL("hive.metastore.aggregate.stats.cache.clean.until", (Object)0.8f, "The cleaner thread cleans until cache reaches this % full size."), 
        METADATA_EXPORT_LOCATION("hive.metadata.export.location", (Object)"", "When used in conjunction with the org.apache.hadoop.hive.ql.parse.MetaDataExportListener pre event listener, \nit is the location to which the metadata will be exported. The default is an empty string, which results in the \nmetadata being exported to the current user's home directory on HDFS."), 
        MOVE_EXPORTED_METADATA_TO_TRASH("hive.metadata.move.exported.metadata.to.trash", (Object)true, "When used in conjunction with the org.apache.hadoop.hive.ql.parse.MetaDataExportListener pre event listener, \nthis setting determines if the metadata that is exported will subsequently be moved to the user's trash directory \nalongside the dropped table data. This ensures that the metadata will be cleaned up along with the dropped table data."), 
        CLIIGNOREERRORS("hive.cli.errors.ignore", (Object)false, ""), 
        CLIPRINTCURRENTDB("hive.cli.print.current.db", (Object)false, "Whether to include the current database in the Hive prompt."), 
        CLIPROMPT("hive.cli.prompt", (Object)"hive", "Command line prompt configuration value. Other hiveconf can be used in this configuration value. \nVariable substitution will only be invoked at the Hive CLI startup."), 
        CLIPRETTYOUTPUTNUMCOLS("hive.cli.pretty.output.num.cols", (Object)(-1), "The number of columns to use when formatting output generated by the DESCRIBE PRETTY table_name command.\nIf the value of this property is -1, then Hive will use the auto-detected terminal width."), 
        HIVE_METASTORE_FS_HANDLER_CLS("hive.metastore.fs.handler.class", (Object)"org.apache.hadoop.hive.metastore.HiveMetaStoreFsImpl", ""), 
        HIVESESSIONID("hive.session.id", (Object)"", ""), 
        HIVESESSIONSILENT("hive.session.silent", (Object)false, ""), 
        HIVE_SESSION_HISTORY_ENABLED("hive.session.history.enabled", (Object)false, "Whether to log Hive query, query plan, runtime statistics etc."), 
        HIVEQUERYSTRING("hive.query.string", (Object)"", "Query being executed (might be multiple per a session)"), 
        HIVEQUERYID("hive.query.id", (Object)"", "ID for query being executed (might be multiple per a session)"), 
        HIVEJOBNAMELENGTH("hive.jobname.length", (Object)50, "max jobname length"), 
        HIVEJAR("hive.jar.path", (Object)"", "The location of hive_cli.jar that is used when submitting jobs in a separate jvm."), 
        HIVEAUXJARS("hive.aux.jars.path", (Object)"", "The location of the plugin jars that contain implementations of user defined functions and serdes."), 
        HIVERELOADABLEJARS("hive.reloadable.aux.jars.path", (Object)"", "Jars can be renewed by executing reload command. And these jars can be used as the auxiliary classes like creating a UDF or SerDe."), 
        HIVEADDEDFILES("hive.added.files.path", (Object)"", "This an internal parameter."), 
        HIVEADDEDJARS("hive.added.jars.path", (Object)"", "This an internal parameter."), 
        HIVEADDEDARCHIVES("hive.added.archives.path", (Object)"", "This an internal parameter."), 
        HIVE_CURRENT_DATABASE("hive.current.database", (Object)"", "Database name used by current session. Internal usage only.", true), 
        HIVES_AUTO_PROGRESS_TIMEOUT("hive.auto.progress.timeout", (Object)"0s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "How long to run autoprogressor for the script/UDTF operators.\nSet to 0 for forever."), 
        HIVESCRIPTAUTOPROGRESS("hive.script.auto.progress", (Object)false, "Whether Hive Transform/Map/Reduce Clause should automatically send progress information to TaskTracker \nto avoid the task getting killed because of inactivity.  Hive sends progress information when the script is \noutputting to stderr.  This option removes the need of periodically producing stderr messages, \nbut users should be cautious because this may prevent infinite loops in the scripts to be killed by TaskTracker."), 
        HIVESCRIPTIDENVVAR("hive.script.operator.id.env.var", (Object)"HIVE_SCRIPT_OPERATOR_ID", "Name of the environment variable that holds the unique script operator ID in the user's \ntransform function (the custom mapper/reducer that the user has specified in the query)"), 
        HIVESCRIPTTRUNCATEENV("hive.script.operator.truncate.env", (Object)false, "Truncate each environment variable for external script in scripts operator to 20KB (to fit system limits)"), 
        HIVESCRIPT_ENV_BLACKLIST("hive.script.operator.env.blacklist", (Object)"hive.txn.valid.txns,hive.script.operator.env.blacklist", "Comma separated list of keys from the configuration file not to convert to environment variables when envoking the script operator"), 
        HIVEMAPREDMODE("hive.mapred.mode", (Object)"nonstrict", "The mode in which the Hive operations are being performed. \nIn strict mode, some risky queries are not allowed to run. They include:\n  Cartesian Product.\n  No partition being picked up for a query.\n  Comparing bigints and strings.\n  Comparing bigints and doubles.\n  Orderby without limit."), 
        HIVEALIAS("hive.alias", (Object)"", ""), 
        HIVEMAPSIDEAGGREGATE("hive.map.aggr", (Object)true, "Whether to use map-side aggregation in Hive Group By queries"), 
        HIVEGROUPBYSKEW("hive.groupby.skewindata", (Object)false, "Whether there is skew in data to optimize group by queries"), 
        HIVEJOINEMITINTERVAL("hive.join.emit.interval", (Object)1000, "How many rows in the right-most join operand Hive should buffer before emitting the join result."), 
        HIVEJOINCACHESIZE("hive.join.cache.size", (Object)25000, "How many rows in the joining tables (except the streaming table) should be cached in memory."), 
        HIVE_CBO_ENABLED("hive.cbo.enable", (Object)true, "Flag to control enabling Cost Based Optimizations using Calcite framework."), 
        HIVE_CBO_RETPATH_HIVEOP("hive.cbo.returnpath.hiveop", (Object)false, "Flag to control calcite plan to hive operator conversion"), 
        HIVE_CBO_EXTENDED_COST_MODEL("hive.cbo.costmodel.extended", (Object)false, "Flag to control enabling the extended cost model based onCPU, IO and cardinality. Otherwise, the cost model is based on cardinality."), 
        HIVE_CBO_COST_MODEL_CPU("hive.cbo.costmodel.cpu", (Object)"0.000001", "Default cost of a comparison"), 
        HIVE_CBO_COST_MODEL_NET("hive.cbo.costmodel.network", (Object)"150.0", "Default cost of a transfering a byte over network; expressed as multiple of CPU cost"), 
        HIVE_CBO_COST_MODEL_LFS_WRITE("hive.cbo.costmodel.local.fs.write", (Object)"4.0", "Default cost of writing a byte to local FS; expressed as multiple of NETWORK cost"), 
        HIVE_CBO_COST_MODEL_LFS_READ("hive.cbo.costmodel.local.fs.read", (Object)"4.0", "Default cost of reading a byte from local FS; expressed as multiple of NETWORK cost"), 
        HIVE_CBO_COST_MODEL_HDFS_WRITE("hive.cbo.costmodel.hdfs.write", (Object)"10.0", "Default cost of writing a byte to HDFS; expressed as multiple of Local FS write cost"), 
        HIVE_CBO_COST_MODEL_HDFS_READ("hive.cbo.costmodel.hdfs.read", (Object)"1.5", "Default cost of reading a byte from HDFS; expressed as multiple of Local FS read cost"), 
        HIVEMAPJOINBUCKETCACHESIZE("hive.mapjoin.bucket.cache.size", (Object)100, ""), 
        HIVEMAPJOINUSEOPTIMIZEDTABLE("hive.mapjoin.optimized.hashtable", (Object)true, "Whether Hive should use memory-optimized hash table for MapJoin. Only works on Tez,\nbecause memory-optimized hashtable cannot be serialized."), 
        HIVEUSEHYBRIDGRACEHASHJOIN("hive.mapjoin.hybridgrace.hashtable", (Object)true, "Whether to use hybridgrace hash join as the join method for mapjoin. Tez only."), 
        HIVEHYBRIDGRACEHASHJOINMEMCHECKFREQ("hive.mapjoin.hybridgrace.memcheckfrequency", (Object)1024, "For hybrid grace hash join, how often (how many rows apart) we check if memory is full. This number should be power of 2."), 
        HIVEHYBRIDGRACEHASHJOINMINWBSIZE("hive.mapjoin.hybridgrace.minwbsize", (Object)524288, "For hybrid grace hash join, the minimum write buffer size used by optimized hashtable. Default is 512 KB."), 
        HIVEHYBRIDGRACEHASHJOINMINNUMPARTITIONS("hive.mapjoin.hybridgrace.minnumpartitions", (Object)16, "For hybrid grace hash join, the minimum number of partitions to create."), 
        HIVEHASHTABLEWBSIZE("hive.mapjoin.optimized.hashtable.wbsize", (Object)10485760, "Optimized hashtable (see hive.mapjoin.optimized.hashtable) uses a chain of buffers to\nstore data. This is one buffer size. HT may be slightly faster if this is larger, but for small\njoins unnecessary memory will be allocated and then trimmed."), 
        HIVESMBJOINCACHEROWS("hive.smbjoin.cache.rows", (Object)10000, "How many rows with the same key value should be cached in memory per smb joined table."), 
        HIVEGROUPBYMAPINTERVAL("hive.groupby.mapaggr.checkinterval", (Object)100000, "Number of rows after which size of the grouping keys/aggregation classes is performed"), 
        HIVEMAPAGGRHASHMEMORY("hive.map.aggr.hash.percentmemory", (Object)0.5f, "Portion of total memory to be used by map-side group aggregation hash table"), 
        HIVEMAPJOINFOLLOWEDBYMAPAGGRHASHMEMORY("hive.mapjoin.followby.map.aggr.hash.percentmemory", (Object)0.3f, "Portion of total memory to be used by map-side group aggregation hash table, when this group by is followed by map join"), 
        HIVEMAPAGGRMEMORYTHRESHOLD("hive.map.aggr.hash.force.flush.memory.threshold", (Object)0.9f, "The max memory to be used by map-side group aggregation hash table.\nIf the memory usage is higher than this number, force to flush data"), 
        HIVEMAPAGGRHASHMINREDUCTION("hive.map.aggr.hash.min.reduction", (Object)0.5f, "Hash aggregation will be turned off if the ratio between hash  table size and input rows is bigger than this number. \nSet to 1 to make sure hash aggregation is never turned off."), 
        HIVEMULTIGROUPBYSINGLEREDUCER("hive.multigroupby.singlereducer", (Object)true, "Whether to optimize multi group by query to generate single M/R  job plan. If the multi group by query has \ncommon group by keys, it will be optimized to generate single M/R job."), 
        HIVE_MAP_GROUPBY_SORT("hive.map.groupby.sorted", (Object)false, "If the bucketing/sorting properties of the table exactly match the grouping key, whether to perform \nthe group by in the mapper by using BucketizedHiveInputFormat. The only downside to this\nis that it limits the number of mappers to the number of files."), 
        HIVE_MAP_GROUPBY_SORT_TESTMODE("hive.map.groupby.sorted.testmode", (Object)false, "If the bucketing/sorting properties of the table exactly match the grouping key, whether to perform \nthe group by in the mapper by using BucketizedHiveInputFormat. If the test mode is set, the plan\nis not converted, but a query property is set to denote the same."), 
        HIVE_GROUPBY_ORDERBY_POSITION_ALIAS("hive.groupby.orderby.position.alias", (Object)false, "Whether to enable using Column Position Alias in Group By or Order By"), 
        HIVE_NEW_JOB_GROUPING_SET_CARDINALITY("hive.new.job.grouping.set.cardinality", (Object)30, "Whether a new map-reduce job should be launched for grouping sets/rollups/cubes.\nFor a query like: select a, b, c, count(1) from T group by a, b, c with rollup;\n4 rows are created per row: (a, b, c), (a, b, null), (a, null, null), (null, null, null).\nThis can lead to explosion across map-reduce boundary if the cardinality of T is very high,\nand map-side aggregation does not do a very good job. \n\nThis parameter decides if Hive should add an additional map-reduce job. If the grouping set\ncardinality (4 in the example above), is more than this value, a new MR job is added under the\nassumption that the original group by will reduce the data size."), 
        HIVE_EXEC_COPYFILE_MAXSIZE("hive.exec.copyfile.maxsize", (Object)33554432L, "Maximum file size (in Mb) that Hive uses to do single HDFS copies between directories.Distributed copies (distcp) will be used instead for bigger files so that copies can be done faster."), 
        HIVEUDTFAUTOPROGRESS("hive.udtf.auto.progress", (Object)false, "Whether Hive should automatically send progress information to TaskTracker \nwhen using UDTF's to prevent the task getting killed because of inactivity.  Users should be cautious \nbecause this may prevent TaskTracker from killing tasks with infinite loops."), 
        HIVEDEFAULTFILEFORMAT("hive.default.fileformat", (Object)"TextFile", (Validator)new Validator.StringSet(new String[] { "TextFile", "SequenceFile", "RCfile", "ORC" }), "Default file format for CREATE TABLE statement. Users can explicitly override it by CREATE TABLE ... STORED AS [FORMAT]"), 
        HIVEDEFAULTMANAGEDFILEFORMAT("hive.default.fileformat.managed", (Object)"none", (Validator)new Validator.StringSet(new String[] { "none", "TextFile", "SequenceFile", "RCfile", "ORC" }), "Default file format for CREATE TABLE statement applied to managed tables only. External tables will be \ncreated with format specified by hive.default.fileformat. Leaving this null will result in using hive.default.fileformat \nfor all tables."), 
        HIVEQUERYRESULTFILEFORMAT("hive.query.result.fileformat", (Object)"TextFile", (Validator)new Validator.StringSet(new String[] { "TextFile", "SequenceFile", "RCfile" }), "Default file format for storing result of the query."), 
        HIVECHECKFILEFORMAT("hive.fileformat.check", (Object)true, "Whether to check file format or not when loading data files"), 
        HIVEDEFAULTRCFILESERDE("hive.default.rcfile.serde", (Object)"org.apache.hadoop.hive.serde2.columnar.LazyBinaryColumnarSerDe", "The default SerDe Hive will use for the RCFile format"), 
        HIVEDEFAULTSERDE("hive.default.serde", (Object)"org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe", "The default SerDe Hive will use for storage formats that do not specify a SerDe."), 
        SERDESUSINGMETASTOREFORSCHEMA("hive.serdes.using.metastore.for.schema", (Object)"org.apache.hadoop.hive.ql.io.orc.OrcSerde,org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe,org.apache.hadoop.hive.serde2.columnar.ColumnarSerDe,org.apache.hadoop.hive.serde2.dynamic_type.DynamicSerDe,org.apache.hadoop.hive.serde2.MetadataTypedColumnsetSerDe,org.apache.hadoop.hive.serde2.columnar.LazyBinaryColumnarSerDe,org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe,org.apache.hadoop.hive.serde2.lazybinary.LazyBinarySerDe", "SerDes retriving schema from metastore. This an internal parameter. Check with the hive dev. team"), 
        HIVEHISTORYFILELOC("hive.querylog.location", (Object)("${system:java.io.tmpdir}" + File.separator + "${system:user.name}"), "Location of Hive run time structured log file"), 
        HIVE_LOG_INCREMENTAL_PLAN_PROGRESS("hive.querylog.enable.plan.progress", (Object)true, "Whether to log the plan's progress every time a job's progress is checked.\nThese logs are written to the location specified by hive.querylog.location"), 
        HIVE_LOG_INCREMENTAL_PLAN_PROGRESS_INTERVAL("hive.querylog.plan.progress.interval", (Object)"60000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "The interval to wait between logging the plan's progress.\nIf there is a whole number percentage change in the progress of the mappers or the reducers,\nthe progress is logged regardless of this value.\nThe actual interval will be the ceiling of (this value divided by the value of\nhive.exec.counters.pull.interval) multiplied by the value of hive.exec.counters.pull.interval\nI.e. if it is not divide evenly by the value of hive.exec.counters.pull.interval it will be\nlogged less frequently than specified.\nThis only has an effect if hive.querylog.enable.plan.progress is set to true."), 
        HIVESCRIPTSERDE("hive.script.serde", (Object)"org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe", "The default SerDe for transmitting input data to and reading output data from the user scripts. "), 
        HIVESCRIPTRECORDREADER("hive.script.recordreader", (Object)"org.apache.hadoop.hive.ql.exec.TextRecordReader", "The default record reader for reading data from the user scripts. "), 
        HIVESCRIPTRECORDWRITER("hive.script.recordwriter", (Object)"org.apache.hadoop.hive.ql.exec.TextRecordWriter", "The default record writer for writing data to the user scripts. "), 
        HIVESCRIPTESCAPE("hive.transform.escape.input", (Object)false, "This adds an option to escape special chars (newlines, carriage returns and\ntabs) when they are passed to the user script. This is useful if the Hive tables\ncan contain data that contains special characters."), 
        HIVEBINARYRECORDMAX("hive.binary.record.max.length", (Object)1000, "Read from a binary stream and treat each hive.binary.record.max.length bytes as a record. \nThe last record before the end of stream can have less than hive.binary.record.max.length bytes"), 
        HIVEHWILISTENHOST("hive.hwi.listen.host", (Object)"0.0.0.0", "This is the host address the Hive Web Interface will listen on"), 
        HIVEHWILISTENPORT("hive.hwi.listen.port", (Object)"9999", "This is the port the Hive Web Interface will listen on"), 
        HIVEHWIWARFILE("hive.hwi.war.file", (Object)"${env:HWI_WAR_FILE}", "This sets the path to the HWI war file, relative to ${HIVE_HOME}. "), 
        HIVEHADOOPMAXMEM("hive.mapred.local.mem", (Object)0, "mapper/reducer memory in local mode"), 
        HIVESMALLTABLESFILESIZE("hive.mapjoin.smalltable.filesize", (Object)25000000L, "The threshold for the input file size of the small tables; if the file size is smaller \nthan this threshold, it will try to convert the common join into map join"), 
        HIVESAMPLERANDOMNUM("hive.sample.seednumber", (Object)0, "A number used to percentage sampling. By changing this number, user will change the subsets of data sampled."), 
        HIVETESTMODE("hive.test.mode", (Object)false, "Whether Hive is running in test mode. If yes, it turns on sampling and prefixes the output tablename.", false), 
        HIVETESTMODEPREFIX("hive.test.mode.prefix", (Object)"test_", "In test mode, specfies prefixes for the output table", false), 
        HIVETESTMODESAMPLEFREQ("hive.test.mode.samplefreq", (Object)32, "In test mode, specfies sampling frequency for table, which is not bucketed,\nFor example, the following query:\n  INSERT OVERWRITE TABLE dest SELECT col1 from src\nwould be converted to\n  INSERT OVERWRITE TABLE test_dest\n  SELECT col1 from src TABLESAMPLE (BUCKET 1 out of 32 on rand(1))", false), 
        HIVETESTMODENOSAMPLE("hive.test.mode.nosamplelist", (Object)"", "In test mode, specifies comma separated table names which would not apply sampling", false), 
        HIVETESTMODEDUMMYSTATAGGR("hive.test.dummystats.aggregator", (Object)"", "internal variable for test", false), 
        HIVETESTMODEDUMMYSTATPUB("hive.test.dummystats.publisher", (Object)"", "internal variable for test", false), 
        HIVETESTCURRENTTIMESTAMP("hive.test.currenttimestamp", (Object)null, "current timestamp for test", false), 
        HIVEMERGEMAPFILES("hive.merge.mapfiles", (Object)true, "Merge small files at the end of a map-only job"), 
        HIVEMERGEMAPREDFILES("hive.merge.mapredfiles", (Object)false, "Merge small files at the end of a map-reduce job"), 
        HIVEMERGETEZFILES("hive.merge.tezfiles", (Object)false, "Merge small files at the end of a Tez DAG"), 
        HIVEMERGESPARKFILES("hive.merge.sparkfiles", (Object)false, "Merge small files at the end of a Spark DAG Transformation"), 
        HIVEMERGEMAPFILESSIZE("hive.merge.size.per.task", (Object)256000000L, "Size of merged files at the end of the job"), 
        HIVEMERGEMAPFILESAVGSIZE("hive.merge.smallfiles.avgsize", (Object)16000000L, "When the average output file size of a job is less than this number, Hive will start an additional \nmap-reduce job to merge the output files into bigger files. This is only done for map-only jobs \nif hive.merge.mapfiles is true, and for map-reduce jobs if hive.merge.mapredfiles is true."), 
        HIVEMERGERCFILEBLOCKLEVEL("hive.merge.rcfile.block.level", (Object)true, ""), 
        HIVEMERGEORCFILESTRIPELEVEL("hive.merge.orcfile.stripe.level", (Object)true, "When hive.merge.mapfiles, hive.merge.mapredfiles or hive.merge.tezfiles is enabled\nwhile writing a table with ORC file format, enabling this config will do stripe-level\nfast merge for small ORC files. Note that enabling this config will not honor the\npadding tolerance config (hive.exec.orc.block.padding.tolerance)."), 
        HIVEUSEEXPLICITRCFILEHEADER("hive.exec.rcfile.use.explicit.header", (Object)true, "If this is set the header for RCFiles will simply be RCF.  If this is not\nset the header will be that borrowed from sequence files, e.g. SEQ- followed\nby the input and output RCFile formats."), 
        HIVEUSERCFILESYNCCACHE("hive.exec.rcfile.use.sync.cache", (Object)true, ""), 
        HIVE_RCFILE_RECORD_INTERVAL("hive.io.rcfile.record.interval", (Object)Integer.MAX_VALUE, ""), 
        HIVE_RCFILE_COLUMN_NUMBER_CONF("hive.io.rcfile.column.number.conf", (Object)0, ""), 
        HIVE_RCFILE_TOLERATE_CORRUPTIONS("hive.io.rcfile.tolerate.corruptions", (Object)false, ""), 
        HIVE_RCFILE_RECORD_BUFFER_SIZE("hive.io.rcfile.record.buffer.size", (Object)4194304, ""), 
        PARQUET_MEMORY_POOL_RATIO("parquet.memory.pool.ratio", (Object)0.5f, "Maximum fraction of heap that can be used by Parquet file writers in one task.\nIt is for avoiding OutOfMemory error in tasks. Work with Parquet 1.6.0 and above.\nThis config parameter is defined in Parquet, so that it does not start with 'hive.'."), 
        HIVE_PARQUET_TIMESTAMP_SKIP_CONVERSION("hive.parquet.timestamp.skip.conversion", (Object)true, "Current Hive implementation of parquet stores timestamps to UTC, this flag allows skipping of the conversionon reading parquet files from other tools"), 
        HIVE_INT_TIMESTAMP_CONVERSION_IN_SECONDS("hive.int.timestamp.conversion.in.seconds", (Object)false, "Boolean/tinyint/smallint/int/bigint value is interpreted as milliseconds during the timestamp conversion.\nSet this flag to true to interpret the value as seconds to be consistent with float/double."), 
        HIVE_ORC_FILE_MEMORY_POOL("hive.exec.orc.memory.pool", (Object)0.5f, "Maximum fraction of heap that can be used by ORC file writers"), 
        HIVE_ORC_WRITE_FORMAT("hive.exec.orc.write.format", (Object)null, "Define the version of the file to write. Possible values are 0.11 and 0.12.\nIf this parameter is not defined, ORC will use the run length encoding (RLE)\nintroduced in Hive 0.12. Any value other than 0.11 results in the 0.12 encoding."), 
        HIVE_ORC_DEFAULT_STRIPE_SIZE("hive.exec.orc.default.stripe.size", (Object)67108864L, "Define the default ORC stripe size, in bytes."), 
        HIVE_ORC_DEFAULT_BLOCK_SIZE("hive.exec.orc.default.block.size", (Object)268435456L, "Define the default file system block size for ORC files."), 
        HIVE_ORC_DICTIONARY_KEY_SIZE_THRESHOLD("hive.exec.orc.dictionary.key.size.threshold", (Object)0.8f, "If the number of keys in a dictionary is greater than this fraction of the total number of\nnon-null rows, turn off dictionary encoding.  Use 1 to always use dictionary encoding."), 
        HIVE_ORC_DEFAULT_ROW_INDEX_STRIDE("hive.exec.orc.default.row.index.stride", (Object)10000, "Define the default ORC index stride in number of rows. (Stride is the number of rows\nan index entry represents.)"), 
        HIVE_ORC_ROW_INDEX_STRIDE_DICTIONARY_CHECK("hive.orc.row.index.stride.dictionary.check", (Object)true, "If enabled dictionary check will happen after first row index stride (default 10000 rows)\nelse dictionary check will happen before writing first stripe. In both cases, the decision\nto use dictionary or not will be retained thereafter."), 
        HIVE_ORC_DEFAULT_BUFFER_SIZE("hive.exec.orc.default.buffer.size", (Object)262144, "Define the default ORC buffer size, in bytes."), 
        HIVE_ORC_DEFAULT_BLOCK_PADDING("hive.exec.orc.default.block.padding", (Object)true, "Define the default block padding, which pads stripes to the HDFS block boundaries."), 
        HIVE_ORC_BLOCK_PADDING_TOLERANCE("hive.exec.orc.block.padding.tolerance", (Object)0.05f, "Define the tolerance for block padding as a decimal fraction of stripe size (for\nexample, the default value 0.05 is 5% of the stripe size). For the defaults of 64Mb\nORC stripe and 256Mb HDFS blocks, the default block padding tolerance of 5% will\nreserve a maximum of 3.2Mb for padding within the 256Mb block. In that case, if the\navailable size within the block is more than 3.2Mb, a new smaller stripe will be\ninserted to fit within that space. This will make sure that no stripe written will\ncross block boundaries and cause remote reads within a node local task."), 
        HIVE_ORC_DEFAULT_COMPRESS("hive.exec.orc.default.compress", (Object)"ZLIB", "Define the default compression codec for ORC file"), 
        HIVE_ORC_ENCODING_STRATEGY("hive.exec.orc.encoding.strategy", (Object)"SPEED", (Validator)new Validator.StringSet(new String[] { "SPEED", "COMPRESSION" }), "Define the encoding strategy to use while writing data. Changing this will\nonly affect the light weight encoding for integers. This flag will not\nchange the compression level of higher level compression codec (like ZLIB)."), 
        HIVE_ORC_COMPRESSION_STRATEGY("hive.exec.orc.compression.strategy", (Object)"SPEED", (Validator)new Validator.StringSet(new String[] { "SPEED", "COMPRESSION" }), "Define the compression strategy to use while writing data. \nThis changes the compression level of higher level compression codec (like ZLIB)."), 
        HIVE_ORC_SPLIT_STRATEGY("hive.exec.orc.split.strategy", (Object)"HYBRID", (Validator)new Validator.StringSet(new String[] { "HYBRID", "BI", "ETL" }), "This is not a user level config. BI strategy is used when the requirement is to spend less time in split generation as opposed to query execution (split generation does not read or cache file footers). ETL strategy is used when spending little more time in split generation is acceptable (split generation reads and caches file footers). HYBRID chooses between the above strategies based on heuristics."), 
        HIVE_ORC_INCLUDE_FILE_FOOTER_IN_SPLITS("hive.orc.splits.include.file.footer", (Object)false, "If turned on splits generated by orc will include metadata about the stripes in the file. This\ndata is read remotely (from the client or HS2 machine) and sent to all the tasks."), 
        HIVE_ORC_CACHE_STRIPE_DETAILS_SIZE("hive.orc.cache.stripe.details.size", (Object)10000, "Cache size for keeping meta info about orc splits cached in the client."), 
        HIVE_ORC_COMPUTE_SPLITS_NUM_THREADS("hive.orc.compute.splits.num.threads", (Object)10, "How many threads orc should use to create splits in parallel."), 
        HIVE_ORC_SKIP_CORRUPT_DATA("hive.exec.orc.skip.corrupt.data", (Object)false, "If ORC reader encounters corrupt data, this value will be used to determine\nwhether to skip the corrupt data or throw exception. The default behavior is to throw exception."), 
        HIVE_ORC_ZEROCOPY("hive.exec.orc.zerocopy", (Object)false, "Use zerocopy reads with ORC. (This requires Hadoop 2.3 or later.)"), 
        HIVE_LAZYSIMPLE_EXTENDED_BOOLEAN_LITERAL("hive.lazysimple.extended_boolean_literal", (Object)false, "LazySimpleSerde uses this property to determine if it treats 'T', 't', 'F', 'f',\n'1', and '0' as extened, legal boolean literal, in addition to 'TRUE' and 'FALSE'.\nThe default is false, which means only 'TRUE' and 'FALSE' are treated as legal\nboolean literal."), 
        HIVESKEWJOIN("hive.optimize.skewjoin", (Object)false, "Whether to enable skew join optimization. \nThe algorithm is as follows: At runtime, detect the keys with a large skew. Instead of\nprocessing those keys, store them temporarily in an HDFS directory. In a follow-up map-reduce\njob, process those skewed keys. The same key need not be skewed for all the tables, and so,\nthe follow-up map-reduce job (for the skewed keys) would be much faster, since it would be a\nmap-join."), 
        HIVECONVERTJOIN("hive.auto.convert.join", (Object)true, "Whether Hive enables the optimization about converting common join into mapjoin based on the input file size"), 
        HIVECONVERTJOINNOCONDITIONALTASK("hive.auto.convert.join.noconditionaltask", (Object)true, "Whether Hive enables the optimization about converting common join into mapjoin based on the input file size. \nIf this parameter is on, and the sum of size for n-1 of the tables/partitions for a n-way join is smaller than the\nspecified size, the join is directly converted to a mapjoin (there is no conditional task)."), 
        HIVECONVERTJOINNOCONDITIONALTASKTHRESHOLD("hive.auto.convert.join.noconditionaltask.size", (Object)10000000L, "If hive.auto.convert.join.noconditionaltask is off, this parameter does not take affect. \nHowever, if it is on, and the sum of size for n-1 of the tables/partitions for a n-way join is smaller than this size, \nthe join is directly converted to a mapjoin(there is no conditional task). The default is 10MB"), 
        HIVECONVERTJOINUSENONSTAGED("hive.auto.convert.join.use.nonstaged", (Object)false, "For conditional joins, if input stream from a small alias can be directly applied to join operator without \nfiltering or projection, the alias need not to be pre-staged in distributed cache via mapred local task.\nCurrently, this is not working with vectorization or tez execution engine."), 
        HIVESKEWJOINKEY("hive.skewjoin.key", (Object)100000, "Determine if we get a skew key in join. If we see more than the specified number of rows with the same key in join operator,\nwe think the key as a skew join key. "), 
        HIVESKEWJOINMAPJOINNUMMAPTASK("hive.skewjoin.mapjoin.map.tasks", (Object)10000, "Determine the number of map task used in the follow up map join job for a skew join.\nIt should be used together with hive.skewjoin.mapjoin.min.split to perform a fine grained control."), 
        HIVESKEWJOINMAPJOINMINSPLIT("hive.skewjoin.mapjoin.min.split", (Object)33554432L, "Determine the number of map task at most used in the follow up map join job for a skew join by specifying \nthe minimum split size. It should be used together with hive.skewjoin.mapjoin.map.tasks to perform a fine grained control."), 
        HIVESENDHEARTBEAT("hive.heartbeat.interval", (Object)1000, "Send a heartbeat after this interval - used by mapjoin and filter operators"), 
        HIVELIMITMAXROWSIZE("hive.limit.row.max.size", (Object)100000L, "When trying a smaller subset of data for simple LIMIT, how much size we need to guarantee each row to have at least."), 
        HIVELIMITOPTLIMITFILE("hive.limit.optimize.limit.file", (Object)10, "When trying a smaller subset of data for simple LIMIT, maximum number of files we can sample."), 
        HIVELIMITOPTENABLE("hive.limit.optimize.enable", (Object)false, "Whether to enable to optimization to trying a smaller subset of data for simple LIMIT first."), 
        HIVELIMITOPTMAXFETCH("hive.limit.optimize.fetch.max", (Object)50000, "Maximum number of rows allowed for a smaller subset of data for simple LIMIT, if it is a fetch query. \nInsert queries are not restricted by this limit."), 
        HIVELIMITPUSHDOWNMEMORYUSAGE("hive.limit.pushdown.memory.usage", (Object)(-1.0f), "The max memory to be used for hash in RS operator for top K selection."), 
        HIVELIMITTABLESCANPARTITION("hive.limit.query.max.table.partition", (Object)(-1), "This controls how many partitions can be scanned for each partitioned table.\nThe default value \"-1\" means no limit."), 
        HIVEHASHTABLEKEYCOUNTADJUSTMENT("hive.hashtable.key.count.adjustment", (Object)1.0f, "Adjustment to mapjoin hashtable size derived from table and column statistics; the estimate of the number of keys is divided by this value. If the value is 0, statistics are not usedand hive.hashtable.initialCapacity is used instead."), 
        HIVEHASHTABLETHRESHOLD("hive.hashtable.initialCapacity", (Object)100000, "Initial capacity of mapjoin hashtable if statistics are absent, or if hive.hashtable.stats.key.estimate.adjustment is set to 0"), 
        HIVEHASHTABLELOADFACTOR("hive.hashtable.loadfactor", (Object)0.75f, ""), 
        HIVEHASHTABLEFOLLOWBYGBYMAXMEMORYUSAGE("hive.mapjoin.followby.gby.localtask.max.memory.usage", (Object)0.55f, "This number means how much memory the local task can take to hold the key/value into an in-memory hash table \nwhen this map join is followed by a group by. If the local task's memory usage is more than this number, \nthe local task will abort by itself. It means the data of the small table is too large to be held in memory."), 
        HIVEHASHTABLEMAXMEMORYUSAGE("hive.mapjoin.localtask.max.memory.usage", (Object)0.9f, "This number means how much memory the local task can take to hold the key/value into an in-memory hash table. \nIf the local task's memory usage is more than this number, the local task will abort by itself. \nIt means the data of the small table is too large to be held in memory."), 
        HIVEHASHTABLESCALE("hive.mapjoin.check.memory.rows", (Object)100000L, "The number means after how many rows processed it needs to check the memory usage"), 
        HIVEDEBUGLOCALTASK("hive.debug.localtask", (Object)false, ""), 
        HIVEINPUTFORMAT("hive.input.format", (Object)"org.apache.hadoop.hive.ql.io.CombineHiveInputFormat", "The default input format. Set this to HiveInputFormat if you encounter problems with CombineHiveInputFormat."), 
        HIVETEZINPUTFORMAT("hive.tez.input.format", (Object)"org.apache.hadoop.hive.ql.io.HiveInputFormat", "The default input format for tez. Tez groups splits in the AM."), 
        HIVETEZCONTAINERSIZE("hive.tez.container.size", (Object)(-1), "By default Tez will spawn containers of the size of a mapper. This can be used to overwrite."), 
        HIVETEZCPUVCORES("hive.tez.cpu.vcores", (Object)(-1), "By default Tez will ask for however many cpus map-reduce is configured to use per container.\nThis can be used to overwrite."), 
        HIVETEZJAVAOPTS("hive.tez.java.opts", (Object)null, "By default Tez will use the Java options from map tasks. This can be used to overwrite."), 
        HIVETEZLOGLEVEL("hive.tez.log.level", (Object)"INFO", "The log level to use for tasks executing as part of the DAG.\nUsed only if hive.tez.java.opts is used to configure Java options."), 
        HIVEENFORCEBUCKETING("hive.enforce.bucketing", (Object)false, "Whether bucketing is enforced. If true, while inserting into the table, bucketing is enforced."), 
        HIVEENFORCESORTING("hive.enforce.sorting", (Object)false, "Whether sorting is enforced. If true, while inserting into the table, sorting is enforced."), 
        HIVEOPTIMIZEBUCKETINGSORTING("hive.optimize.bucketingsorting", (Object)true, "If hive.enforce.bucketing or hive.enforce.sorting is true, don't create a reducer for enforcing \nbucketing/sorting for queries of the form: \ninsert overwrite table T2 select * from T1;\nwhere T1 and T2 are bucketed/sorted by the same keys into the same number of buckets."), 
        HIVEPARTITIONER("hive.mapred.partitioner", (Object)"org.apache.hadoop.hive.ql.io.DefaultHivePartitioner", ""), 
        HIVEENFORCESORTMERGEBUCKETMAPJOIN("hive.enforce.sortmergebucketmapjoin", (Object)false, "If the user asked for sort-merge bucketed map-side join, and it cannot be performed, should the query fail or not ?"), 
        HIVEENFORCEBUCKETMAPJOIN("hive.enforce.bucketmapjoin", (Object)false, "If the user asked for bucketed map-side join, and it cannot be performed, \nshould the query fail or not ? For example, if the buckets in the tables being joined are\nnot a multiple of each other, bucketed map-side join cannot be performed, and the\nquery will fail if hive.enforce.bucketmapjoin is set to true."), 
        HIVE_AUTO_SORTMERGE_JOIN("hive.auto.convert.sortmerge.join", (Object)false, "Will the join be automatically converted to a sort-merge join, if the joined tables pass the criteria for sort-merge join."), 
        HIVE_AUTO_SORTMERGE_JOIN_BIGTABLE_SELECTOR("hive.auto.convert.sortmerge.join.bigtable.selection.policy", (Object)"org.apache.hadoop.hive.ql.optimizer.AvgPartitionSizeBasedBigTableSelectorForAutoSMJ", "The policy to choose the big table for automatic conversion to sort-merge join. \nBy default, the table with the largest partitions is assigned the big table. All policies are:\n. based on position of the table - the leftmost table is selected\norg.apache.hadoop.hive.ql.optimizer.LeftmostBigTableSMJ.\n. based on total size (all the partitions selected in the query) of the table \norg.apache.hadoop.hive.ql.optimizer.TableSizeBasedBigTableSelectorForAutoSMJ.\n. based on average size (all the partitions selected in the query) of the table \norg.apache.hadoop.hive.ql.optimizer.AvgPartitionSizeBasedBigTableSelectorForAutoSMJ.\nNew policies can be added in future."), 
        HIVE_AUTO_SORTMERGE_JOIN_TOMAPJOIN("hive.auto.convert.sortmerge.join.to.mapjoin", (Object)false, "If hive.auto.convert.sortmerge.join is set to true, and a join was converted to a sort-merge join, \nthis parameter decides whether each table should be tried as a big table, and effectively a map-join should be\ntried. That would create a conditional task with n+1 children for a n-way join (1 child for each table as the\nbig table), and the backup task will be the sort-merge join. In some cases, a map-join would be faster than a\nsort-merge join, if there is no advantage of having the output bucketed and sorted. For example, if a very big sorted\nand bucketed table with few files (say 10 files) are being joined with a very small sorter and bucketed table\nwith few files (10 files), the sort-merge join will only use 10 mappers, and a simple map-only join might be faster\nif the complete small table can fit in memory, and a map-join can be performed."), 
        HIVESCRIPTOPERATORTRUST("hive.exec.script.trust", (Object)false, ""), 
        HIVEROWOFFSET("hive.exec.rowoffset", (Object)false, "Whether to provide the row offset virtual column"), 
        HIVE_COMBINE_INPUT_FORMAT_SUPPORTS_SPLITTABLE("hive.hadoop.supports.splittable.combineinputformat", (Object)false, ""), 
        HIVEOPTINDEXFILTER("hive.optimize.index.filter", (Object)false, "Whether to enable automatic use of indexes"), 
        HIVEINDEXAUTOUPDATE("hive.optimize.index.autoupdate", (Object)false, "Whether to update stale indexes automatically"), 
        HIVEOPTPPD("hive.optimize.ppd", (Object)true, "Whether to enable predicate pushdown"), 
        HIVEPPDRECOGNIZETRANSITIVITY("hive.ppd.recognizetransivity", (Object)true, "Whether to transitively replicate predicate filters over equijoin conditions."), 
        HIVEPPDREMOVEDUPLICATEFILTERS("hive.ppd.remove.duplicatefilters", (Object)true, "Whether to push predicates down into storage handlers.  Ignored when hive.optimize.ppd is false."), 
        HIVEOPTCONSTANTPROPAGATION("hive.optimize.constant.propagation", (Object)true, "Whether to enable constant propagation optimizer"), 
        HIVEIDENTITYPROJECTREMOVER("hive.optimize.remove.identity.project", (Object)true, "Removes identity project from operator tree"), 
        HIVEMETADATAONLYQUERIES("hive.optimize.metadataonly", (Object)true, ""), 
        HIVENULLSCANOPTIMIZE("hive.optimize.null.scan", (Object)true, "Dont scan relations which are guaranteed to not generate any rows"), 
        HIVEOPTPPD_STORAGE("hive.optimize.ppd.storage", (Object)true, "Whether to push predicates down to storage handlers"), 
        HIVEOPTGROUPBY("hive.optimize.groupby", (Object)true, "Whether to enable the bucketed group by from bucketed partitions/tables."), 
        HIVEOPTBUCKETMAPJOIN("hive.optimize.bucketmapjoin", (Object)false, "Whether to try bucket mapjoin"), 
        HIVEOPTSORTMERGEBUCKETMAPJOIN("hive.optimize.bucketmapjoin.sortedmerge", (Object)false, "Whether to try sorted bucket merge map join"), 
        HIVEOPTREDUCEDEDUPLICATION("hive.optimize.reducededuplication", (Object)true, "Remove extra map-reduce jobs if the data is already clustered by the same key which needs to be used again. \nThis should always be set to true. Since it is a new feature, it has been made configurable."), 
        HIVEOPTREDUCEDEDUPLICATIONMINREDUCER("hive.optimize.reducededuplication.min.reducer", (Object)4, "Reduce deduplication merges two RSs by moving key/parts/reducer-num of the child RS to parent RS. \nThat means if reducer-num of the child RS is fixed (order by or forced bucketing) and small, it can make very slow, single MR.\nThe optimization will be automatically disabled if number of reducers would be less than specified value."), 
        HIVEOPTSORTDYNAMICPARTITION("hive.optimize.sort.dynamic.partition", (Object)false, "When enabled dynamic partitioning column will be globally sorted.\nThis way we can keep only one record writer open for each partition value\nin the reducer thereby reducing the memory pressure on reducers."), 
        HIVESAMPLINGFORORDERBY("hive.optimize.sampling.orderby", (Object)false, "Uses sampling on order-by clause for parallel execution."), 
        HIVESAMPLINGNUMBERFORORDERBY("hive.optimize.sampling.orderby.number", (Object)1000, "Total number of samples to be obtained."), 
        HIVESAMPLINGPERCENTFORORDERBY("hive.optimize.sampling.orderby.percent", (Object)0.1f, (Validator)new Validator.RatioValidator(), "Probability with which a row will be chosen."), 
        HIVEOPTIMIZEDISTINCTREWRITE("hive.optimize.distinct.rewrite", (Object)true, "When applicable this optimization rewrites distinct aggregates from a single stage to multi-stage aggregation. This may not be optimal in all cases. Ideally, whether to trigger it or not should be cost based decision. Until Hive formalizes cost model for this, this is config driven."), 
        HIVE_OPTIMIZE_UNION_REMOVE("hive.optimize.union.remove", (Object)false, "Whether to remove the union and push the operators between union and the filesink above union. \nThis avoids an extra scan of the output by union. This is independently useful for union\nqueries, and specially useful when hive.optimize.skewjoin.compiletime is set to true, since an\nextra union is inserted.\n\nThe merge is triggered if either of hive.merge.mapfiles or hive.merge.mapredfiles is set to true.\nIf the user has set hive.merge.mapfiles to true and hive.merge.mapredfiles to false, the idea was the\nnumber of reducers are few, so the number of files anyway are small. However, with this optimization,\nwe are increasing the number of files possibly by a big margin. So, we merge aggressively."), 
        HIVEOPTCORRELATION("hive.optimize.correlation", (Object)false, "exploit intra-query correlations."), 
        HIVE_HADOOP_SUPPORTS_SUBDIRECTORIES("hive.mapred.supports.subdirectories", (Object)false, "Whether the version of Hadoop which is running supports sub-directories for tables/partitions. \nMany Hive optimizations can be applied if the Hadoop version supports sub-directories for\ntables/partitions. It was added by MAPREDUCE-1501"), 
        HIVE_OPTIMIZE_SKEWJOIN_COMPILETIME("hive.optimize.skewjoin.compiletime", (Object)false, "Whether to create a separate plan for skewed keys for the tables in the join.\nThis is based on the skewed keys stored in the metadata. At compile time, the plan is broken\ninto different joins: one for the skewed keys, and the other for the remaining keys. And then,\na union is performed for the 2 joins generated above. So unless the same skewed key is present\nin both the joined tables, the join for the skewed key will be performed as a map-side join.\n\nThe main difference between this parameter and hive.optimize.skewjoin is that this parameter\nuses the skew information stored in the metastore to optimize the plan at compile time itself.\nIf there is no skew information in the metadata, this parameter will not have any affect.\nBoth hive.optimize.skewjoin.compiletime and hive.optimize.skewjoin should be set to true.\nIdeally, hive.optimize.skewjoin should be renamed as hive.optimize.skewjoin.runtime, but not doing\nso for backward compatibility.\n\nIf the skew information is correctly stored in the metadata, hive.optimize.skewjoin.compiletime\nwould change the query plan to take care of it, and hive.optimize.skewjoin will be a no-op."), 
        HIVEOPTINDEXFILTER_COMPACT_MINSIZE("hive.optimize.index.filter.compact.minsize", (Object)5368709120L, "Minimum size (in bytes) of the inputs on which a compact index is automatically used."), 
        HIVEOPTINDEXFILTER_COMPACT_MAXSIZE("hive.optimize.index.filter.compact.maxsize", (Object)(-1L), "Maximum size (in bytes) of the inputs on which a compact index is automatically used.  A negative number is equivalent to infinity."), 
        HIVE_INDEX_COMPACT_QUERY_MAX_ENTRIES("hive.index.compact.query.max.entries", (Object)10000000L, "The maximum number of index entries to read during a query that uses the compact index. Negative value is equivalent to infinity."), 
        HIVE_INDEX_COMPACT_QUERY_MAX_SIZE("hive.index.compact.query.max.size", (Object)10737418240L, "The maximum number of bytes that a query using the compact index can read. Negative value is equivalent to infinity."), 
        HIVE_INDEX_COMPACT_BINARY_SEARCH("hive.index.compact.binary.search", (Object)true, "Whether or not to use a binary search to find the entries in an index table that match the filter, where possible"), 
        HIVESTATSAUTOGATHER("hive.stats.autogather", (Object)true, "A flag to gather statistics automatically during the INSERT OVERWRITE command."), 
        HIVESTATSDBCLASS("hive.stats.dbclass", (Object)"fs", (Validator)new Validator.PatternSet(new String[] { "jdbc(:.*)", "hbase", "counter", "custom", "fs" }), "The storage that stores temporary Hive statistics. In filesystem based statistics collection ('fs'), \neach task writes statistics it has collected in a file on the filesystem, which will be aggregated \nafter the job has finished. Supported values are fs (filesystem), jdbc:database (where database \ncan be derby, mysql, etc.), hbase, counter, and custom as defined in StatsSetupConst.java."), 
        HIVESTATSJDBCDRIVER("hive.stats.jdbcdriver", (Object)"org.apache.derby.jdbc.EmbeddedDriver", "The JDBC driver for the database that stores temporary Hive statistics."), 
        HIVESTATSDBCONNECTIONSTRING("hive.stats.dbconnectionstring", (Object)"jdbc:derby:;databaseName=TempStatsStore;create=true", "The default connection string for the database that stores temporary Hive statistics."), 
        HIVE_STATS_DEFAULT_PUBLISHER("hive.stats.default.publisher", (Object)"", "The Java class (implementing the StatsPublisher interface) that is used by default if hive.stats.dbclass is custom type."), 
        HIVE_STATS_DEFAULT_AGGREGATOR("hive.stats.default.aggregator", (Object)"", "The Java class (implementing the StatsAggregator interface) that is used by default if hive.stats.dbclass is custom type."), 
        HIVE_STATS_JDBC_TIMEOUT("hive.stats.jdbc.timeout", (Object)"30s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Timeout value used by JDBC connection and statements."), 
        HIVE_STATS_ATOMIC("hive.stats.atomic", (Object)false, "whether to update metastore stats only if all stats are available"), 
        HIVE_STATS_RETRIES_MAX("hive.stats.retries.max", (Object)0, "Maximum number of retries when stats publisher/aggregator got an exception updating intermediate database. \nDefault is no tries on failures."), 
        HIVE_STATS_RETRIES_WAIT("hive.stats.retries.wait", (Object)"3000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "The base waiting window before the next retry. The actual wait time is calculated by baseWindow * failures baseWindow * (failure + 1) * (random number between [0.0,1.0])."), 
        HIVE_STATS_COLLECT_RAWDATASIZE("hive.stats.collect.rawdatasize", (Object)true, "should the raw data size be collected when analyzing tables"), 
        CLIENT_STATS_COUNTERS("hive.client.stats.counters", (Object)"", "Subset of counters that should be of interest for hive.client.stats.publishers (when one wants to limit their publishing). \nNon-display names should be used"), 
        HIVE_STATS_RELIABLE("hive.stats.reliable", (Object)false, "Whether queries will fail because stats cannot be collected completely accurately. \nIf this is set to true, reading/writing from/into a partition may fail because the stats\ncould not be computed accurately."), 
        HIVE_STATS_COLLECT_PART_LEVEL_STATS("hive.analyze.stmt.collect.partlevel.stats", (Object)true, "analyze table T compute statistics for columns. Queries like these should compute partitionlevel stats for partitioned table even when no part spec is specified."), 
        HIVE_STATS_GATHER_NUM_THREADS("hive.stats.gather.num.threads", (Object)10, "Number of threads used by partialscan/noscan analyze command for partitioned tables.\nThis is applicable only for file formats that implement StatsProvidingRecordReader (like ORC)."), 
        HIVE_STATS_COLLECT_TABLEKEYS("hive.stats.collect.tablekeys", (Object)false, "Whether join and group by keys on tables are derived and maintained in the QueryPlan.\nThis is useful to identify how tables are accessed and to determine if they should be bucketed."), 
        HIVE_STATS_COLLECT_SCANCOLS("hive.stats.collect.scancols", (Object)false, "Whether column accesses are tracked in the QueryPlan.\nThis is useful to identify how tables are accessed and to determine if there are wasted columns that can be trimmed."), 
        HIVE_STATS_NDV_ERROR("hive.stats.ndv.error", (Object)20.0f, "Standard error expressed in percentage. Provides a tradeoff between accuracy and compute cost. \nA lower value for error indicates higher accuracy and a higher compute cost."), 
        HIVE_METASTORE_STATS_NDV_DENSITY_FUNCTION("hive.metastore.stats.ndv.densityfunction", (Object)false, "Whether to use density function to estimate the NDV for the whole table based on the NDV of partitions"), 
        HIVE_STATS_KEY_PREFIX_MAX_LENGTH("hive.stats.key.prefix.max.length", (Object)150, "Determines if when the prefix of the key used for intermediate stats collection\nexceeds a certain length, a hash of the key is used instead.  If the value < 0 then hashing"), 
        HIVE_STATS_KEY_PREFIX_RESERVE_LENGTH("hive.stats.key.prefix.reserve.length", (Object)24, "Reserved length for postfix of stats key. Currently only meaningful for counter type which should\nkeep length of full stats key smaller than max length configured by hive.stats.key.prefix.max.length.\nFor counter type, it should be bigger than the length of LB spec if exists."), 
        HIVE_STATS_KEY_PREFIX("hive.stats.key.prefix", (Object)"", "", true), 
        HIVE_STATS_MAX_VARIABLE_LENGTH("hive.stats.max.variable.length", (Object)100, "To estimate the size of data flowing through operators in Hive/Tez(for reducer estimation etc.),\naverage row size is multiplied with the total number of rows coming out of each operator.\nAverage row size is computed from average column size of all columns in the row. In the absence\nof column statistics, for variable length columns (like string, bytes etc.), this value will be\nused. For fixed length columns their corresponding Java equivalent sizes are used\n(float - 4 bytes, double - 8 bytes etc.)."), 
        HIVE_STATS_LIST_NUM_ENTRIES("hive.stats.list.num.entries", (Object)10, "To estimate the size of data flowing through operators in Hive/Tez(for reducer estimation etc.),\naverage row size is multiplied with the total number of rows coming out of each operator.\nAverage row size is computed from average column size of all columns in the row. In the absence\nof column statistics and for variable length complex columns like list, the average number of\nentries/values can be specified using this config."), 
        HIVE_STATS_MAP_NUM_ENTRIES("hive.stats.map.num.entries", (Object)10, "To estimate the size of data flowing through operators in Hive/Tez(for reducer estimation etc.),\naverage row size is multiplied with the total number of rows coming out of each operator.\nAverage row size is computed from average column size of all columns in the row. In the absence\nof column statistics and for variable length complex columns like map, the average number of\nentries/values can be specified using this config."), 
        HIVE_STATS_FETCH_PARTITION_STATS("hive.stats.fetch.partition.stats", (Object)true, "Annotation of operator tree with statistics information requires partition level basic\nstatistics like number of rows, data size and file size. Partition statistics are fetched from\nmetastore. Fetching partition statistics for each needed partition can be expensive when the\nnumber of partitions is high. This flag can be used to disable fetching of partition statistics\nfrom metastore. When this flag is disabled, Hive will make calls to filesystem to get file sizes\nand will estimate the number of rows from row schema."), 
        HIVE_STATS_FETCH_COLUMN_STATS("hive.stats.fetch.column.stats", (Object)false, "Annotation of operator tree with statistics information requires column statistics.\nColumn statistics are fetched from metastore. Fetching column statistics for each needed column\ncan be expensive when the number of columns is high. This flag can be used to disable fetching\nof column statistics from metastore."), 
        HIVE_STATS_JOIN_FACTOR("hive.stats.join.factor", (Object)1.1f, "Hive/Tez optimizer estimates the data size flowing through each of the operators. JOIN operator\nuses column statistics to estimate the number of rows flowing out of it and hence the data size.\nIn the absence of column statistics, this factor determines the amount of rows that flows out\nof JOIN operator."), 
        HIVE_STATS_DESERIALIZATION_FACTOR("hive.stats.deserialization.factor", (Object)1.0f, "Hive/Tez optimizer estimates the data size flowing through each of the operators. In the absence\nof basic statistics like number of rows and data size, file size is used to estimate the number\nof rows and data size. Since files in tables/partitions are serialized (and optionally\ncompressed) the estimates of number of rows and data size cannot be reliably determined.\nThis factor is multiplied with the file size to account for serialization and compression."), 
        HIVE_SUPPORT_CONCURRENCY("hive.support.concurrency", (Object)false, "Whether Hive supports concurrency control or not. \nA ZooKeeper instance must be up and running when using zookeeper Hive lock manager "), 
        HIVE_LOCK_MANAGER("hive.lock.manager", (Object)"org.apache.hadoop.hive.ql.lockmgr.zookeeper.ZooKeeperHiveLockManager", ""), 
        HIVE_LOCK_NUMRETRIES("hive.lock.numretries", (Object)100, "The number of times you want to try to get all the locks"), 
        HIVE_UNLOCK_NUMRETRIES("hive.unlock.numretries", (Object)10, "The number of times you want to retry to do one unlock"), 
        HIVE_LOCK_SLEEP_BETWEEN_RETRIES("hive.lock.sleep.between.retries", (Object)"60s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "The sleep time between various retries"), 
        HIVE_LOCK_MAPRED_ONLY("hive.lock.mapred.only.operation", (Object)false, "This param is to control whether or not only do lock on queries\nthat need to execute at least one mapred job."), 
        HIVE_ZOOKEEPER_QUORUM("hive.zookeeper.quorum", (Object)"", "List of ZooKeeper servers to talk to. This is needed for: \n1. Read/write locks - when hive.lock.manager is set to \norg.apache.hadoop.hive.ql.lockmgr.zookeeper.ZooKeeperHiveLockManager, \n2. When HiveServer2 supports service discovery via Zookeeper.\n3. For delegation token storage if zookeeper store is used, if\nhive.cluster.delegation.token.store.zookeeper.connectString is not set"), 
        HIVE_ZOOKEEPER_CLIENT_PORT("hive.zookeeper.client.port", (Object)"2181", "The port of ZooKeeper servers to talk to.\nIf the list of Zookeeper servers specified in hive.zookeeper.quorum\ndoes not contain port numbers, this value is used."), 
        HIVE_ZOOKEEPER_SESSION_TIMEOUT("hive.zookeeper.session.timeout", (Object)"1200000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "ZooKeeper client's session timeout (in milliseconds). The client is disconnected, and as a result, all locks released, \nif a heartbeat is not sent in the timeout."), 
        HIVE_ZOOKEEPER_NAMESPACE("hive.zookeeper.namespace", (Object)"hive_zookeeper_namespace", "The parent node under which all ZooKeeper nodes are created."), 
        HIVE_ZOOKEEPER_CLEAN_EXTRA_NODES("hive.zookeeper.clean.extra.nodes", (Object)false, "Clean extra nodes at the end of the session."), 
        HIVE_ZOOKEEPER_CONNECTION_MAX_RETRIES("hive.zookeeper.connection.max.retries", (Object)3, "Max number of times to retry when connecting to the ZooKeeper server."), 
        HIVE_ZOOKEEPER_CONNECTION_BASESLEEPTIME("hive.zookeeper.connection.basesleeptime", (Object)"1000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Initial amount of time (in milliseconds) to wait between retries\nwhen connecting to the ZooKeeper server when using ExponentialBackoffRetry policy."), 
        HIVE_TXN_MANAGER("hive.txn.manager", (Object)"org.apache.hadoop.hive.ql.lockmgr.DummyTxnManager", "Set to org.apache.hadoop.hive.ql.lockmgr.DbTxnManager as part of turning on Hive\ntransactions, which also requires appropriate settings for hive.compactor.initiator.on,\nhive.compactor.worker.threads, hive.support.concurrency (true), hive.enforce.bucketing\n(true), and hive.exec.dynamic.partition.mode (nonstrict).\nThe default DummyTxnManager replicates pre-Hive-0.13 behavior and provides\nno transactions."), 
        HIVE_TXN_TIMEOUT("hive.txn.timeout", (Object)"300s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "time after which transactions are declared aborted if the client has not sent a heartbeat."), 
        HIVE_TXN_MAX_OPEN_BATCH("hive.txn.max.open.batch", (Object)1000, "Maximum number of transactions that can be fetched in one call to open_txns().\nThis controls how many transactions streaming agents such as Flume or Storm open\nsimultaneously. The streaming agent then writes that number of entries into a single\nfile (per Flume agent or Storm bolt). Thus increasing this value decreases the number\nof delta files created by streaming agents. But it also increases the number of open\ntransactions that Hive has to track at any given time, which may negatively affect\nread performance."), 
        HIVE_COMPACTOR_INITIATOR_ON("hive.compactor.initiator.on", (Object)false, "Whether to run the initiator and cleaner threads on this metastore instance or not.\nSet this to true on one instance of the Thrift metastore service as part of turning\non Hive transactions. For a complete list of parameters required for turning on\ntransactions, see hive.txn.manager."), 
        HIVE_COMPACTOR_WORKER_THREADS("hive.compactor.worker.threads", (Object)0, "How many compactor worker threads to run on this metastore instance. Set this to a\npositive number on one or more instances of the Thrift metastore service as part of\nturning on Hive transactions. For a complete list of parameters required for turning\non transactions, see hive.txn.manager.\nWorker threads spawn MapReduce jobs to do compactions. They do not do the compactions\nthemselves. Increasing the number of worker threads will decrease the time it takes\ntables or partitions to be compacted once they are determined to need compaction.\nIt will also increase the background load on the Hadoop cluster as more MapReduce jobs\nwill be running in the background."), 
        HIVE_COMPACTOR_WORKER_TIMEOUT("hive.compactor.worker.timeout", (Object)"86400s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Time in seconds after which a compaction job will be declared failed and the\ncompaction re-queued."), 
        HIVE_COMPACTOR_CHECK_INTERVAL("hive.compactor.check.interval", (Object)"300s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Time in seconds between checks to see if any tables or partitions need to be\ncompacted. This should be kept high because each check for compaction requires\nmany calls against the NameNode.\nDecreasing this value will reduce the time it takes for compaction to be started\nfor a table or partition that requires compaction. However, checking if compaction\nis needed requires several calls to the NameNode for each table or partition that\nhas had a transaction done on it since the last major compaction. So decreasing this\nvalue will increase the load on the NameNode."), 
        HIVE_COMPACTOR_DELTA_NUM_THRESHOLD("hive.compactor.delta.num.threshold", (Object)10, "Number of delta directories in a table or partition that will trigger a minor\ncompaction."), 
        HIVE_COMPACTOR_DELTA_PCT_THRESHOLD("hive.compactor.delta.pct.threshold", (Object)0.1f, "Percentage (fractional) size of the delta files relative to the base that will trigger\na major compaction. (1.0 = 100%, so the default 0.1 = 10%.)"), 
        HIVE_COMPACTOR_ABORTEDTXN_THRESHOLD("hive.compactor.abortedtxn.threshold", (Object)1000, "Number of aborted transactions involving a given table or partition that will trigger\na major compaction."), 
        HIVE_COMPACTOR_CLEANER_RUN_INTERVAL("hive.compactor.cleaner.run.interval", (Object)"5000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Time between runs of the cleaner thread"), 
        HIVE_HBASE_WAL_ENABLED("hive.hbase.wal.enabled", (Object)true, "Whether writes to HBase should be forced to the write-ahead log. \nDisabling this improves HBase write performance at the risk of lost writes in case of a crash."), 
        HIVE_HBASE_GENERATE_HFILES("hive.hbase.generatehfiles", (Object)false, "True when HBaseStorageHandler should generate hfiles instead of operate against the online table."), 
        HIVE_HBASE_SNAPSHOT_NAME("hive.hbase.snapshot.name", (Object)null, "The HBase table snapshot name to use."), 
        HIVE_HBASE_SNAPSHOT_RESTORE_DIR("hive.hbase.snapshot.restoredir", (Object)"/tmp", "The directory in which to restore the HBase table snapshot."), 
        HIVEARCHIVEENABLED("hive.archive.enabled", (Object)false, "Whether archiving operations are permitted"), 
        HIVEOPTGBYUSINGINDEX("hive.optimize.index.groupby", (Object)false, "Whether to enable optimization of group-by queries using Aggregate indexes."), 
        HIVEOUTERJOINSUPPORTSFILTERS("hive.outerjoin.supports.filters", (Object)true, ""), 
        HIVEFETCHTASKCONVERSION("hive.fetch.task.conversion", (Object)"more", (Validator)new Validator.StringSet(new String[] { "none", "minimal", "more" }), "Some select queries can be converted to single FETCH task minimizing latency.\nCurrently the query should be single sourced not having any subquery and should not have\nany aggregations or distincts (which incurs RS), lateral views and joins.\n0. none : disable hive.fetch.task.conversion\n1. minimal : SELECT STAR, FILTER on partition columns, LIMIT only\n2. more    : SELECT, FILTER, LIMIT only (support TABLESAMPLE and virtual columns)"), 
        HIVEFETCHTASKCONVERSIONTHRESHOLD("hive.fetch.task.conversion.threshold", (Object)1073741824L, "Input threshold for applying hive.fetch.task.conversion. If target table is native, input length\nis calculated by summation of file lengths. If it's not native, storage handler for the table\ncan optionally implement org.apache.hadoop.hive.ql.metadata.InputEstimator interface."), 
        HIVEFETCHTASKAGGR("hive.fetch.task.aggr", (Object)false, "Aggregation queries with no group-by clause (for example, select count(*) from src) execute\nfinal aggregations in single reduce task. If this is set true, Hive delegates final aggregation\nstage to fetch task, possibly decreasing the query time."), 
        HIVEOPTIMIZEMETADATAQUERIES("hive.compute.query.using.stats", (Object)false, "When set to true Hive will answer a few queries like count(1) purely using stats\nstored in metastore. For basic stats collection turn on the config hive.stats.autogather to true.\nFor more advanced stats collection need to run analyze table queries."), 
        HIVEFETCHOUTPUTSERDE("hive.fetch.output.serde", (Object)"org.apache.hadoop.hive.serde2.DelimitedJSONSerDe", "The SerDe used by FetchTask to serialize the fetch output."), 
        HIVEEXPREVALUATIONCACHE("hive.cache.expr.evaluation", (Object)true, "If true, the evaluation result of a deterministic expression referenced twice or more\nwill be cached.\nFor example, in a filter condition like '.. where key + 10 = 100 or key + 10 = 0'\nthe expression 'key + 10' will be evaluated/cached once and reused for the following\nexpression ('key + 10 = 0'). Currently, this is applied only to expressions in select\nor filter operators."), 
        HIVEVARIABLESUBSTITUTE("hive.variable.substitute", (Object)true, "This enables substitution using syntax like ${var} ${system:var} and ${env:var}."), 
        HIVEVARIABLESUBSTITUTEDEPTH("hive.variable.substitute.depth", (Object)40, "The maximum replacements the substitution engine will do."), 
        HIVECONFVALIDATION("hive.conf.validation", (Object)true, "Enables type checking for registered Hive configurations"), 
        SEMANTIC_ANALYZER_HOOK("hive.semantic.analyzer.hook", (Object)"", ""), 
        HIVE_TEST_AUTHORIZATION_SQLSTD_HS2_MODE("hive.test.authz.sstd.hs2.mode", (Object)false, "test hs2 mode from .q tests", true), 
        HIVE_AUTHORIZATION_ENABLED("hive.security.authorization.enabled", (Object)false, "enable or disable the Hive client authorization"), 
        HIVE_AUTHORIZATION_MANAGER("hive.security.authorization.manager", (Object)"org.apache.hadoop.hive.ql.security.authorization.DefaultHiveAuthorizationProvider", "The Hive client authorization manager class name. The user defined authorization class should implement \ninterface org.apache.hadoop.hive.ql.security.authorization.HiveAuthorizationProvider."), 
        HIVE_AUTHENTICATOR_MANAGER("hive.security.authenticator.manager", (Object)"org.apache.hadoop.hive.ql.security.HadoopDefaultAuthenticator", "hive client authenticator manager class name. The user defined authenticator should implement \ninterface org.apache.hadoop.hive.ql.security.HiveAuthenticationProvider."), 
        HIVE_METASTORE_AUTHORIZATION_MANAGER("hive.security.metastore.authorization.manager", (Object)"org.apache.hadoop.hive.ql.security.authorization.DefaultHiveMetastoreAuthorizationProvider", "Names of authorization manager classes (comma separated) to be used in the metastore\nfor authorization. The user defined authorization class should implement interface\norg.apache.hadoop.hive.ql.security.authorization.HiveMetastoreAuthorizationProvider.\nAll authorization manager classes have to successfully authorize the metastore API\ncall for the command execution to be allowed."), 
        HIVE_METASTORE_AUTHORIZATION_AUTH_READS("hive.security.metastore.authorization.auth.reads", (Object)true, "If this is true, metastore authorizer authorizes read actions on database, table"), 
        HIVE_METASTORE_AUTHENTICATOR_MANAGER("hive.security.metastore.authenticator.manager", (Object)"org.apache.hadoop.hive.ql.security.HadoopDefaultMetastoreAuthenticator", "authenticator manager class name to be used in the metastore for authentication. \nThe user defined authenticator should implement interface org.apache.hadoop.hive.ql.security.HiveAuthenticationProvider."), 
        HIVE_AUTHORIZATION_TABLE_USER_GRANTS("hive.security.authorization.createtable.user.grants", (Object)"", "the privileges automatically granted to some users whenever a table gets created.\nAn example like \"userX,userY:select;userZ:create\" will grant select privilege to userX and userY,\nand grant create privilege to userZ whenever a new table created."), 
        HIVE_AUTHORIZATION_TABLE_GROUP_GRANTS("hive.security.authorization.createtable.group.grants", (Object)"", "the privileges automatically granted to some groups whenever a table gets created.\nAn example like \"groupX,groupY:select;groupZ:create\" will grant select privilege to groupX and groupY,\nand grant create privilege to groupZ whenever a new table created."), 
        HIVE_AUTHORIZATION_TABLE_ROLE_GRANTS("hive.security.authorization.createtable.role.grants", (Object)"", "the privileges automatically granted to some roles whenever a table gets created.\nAn example like \"roleX,roleY:select;roleZ:create\" will grant select privilege to roleX and roleY,\nand grant create privilege to roleZ whenever a new table created."), 
        HIVE_AUTHORIZATION_TABLE_OWNER_GRANTS("hive.security.authorization.createtable.owner.grants", (Object)"", "The privileges automatically granted to the owner whenever a table gets created.\nAn example like \"select,drop\" will grant select and drop privilege to the owner\nof the table. Note that the default gives the creator of a table no access to the\ntable (but see HIVE-8067)."), 
        HIVE_AUTHORIZATION_TASK_FACTORY("hive.security.authorization.task.factory", (Object)"org.apache.hadoop.hive.ql.parse.authorization.HiveAuthorizationTaskFactoryImpl", "Authorization DDL task factory implementation"), 
        HIVE_AUTHORIZATION_SQL_STD_AUTH_CONFIG_WHITELIST("hive.security.authorization.sqlstd.confwhitelist", (Object)"", "List of comma separated Java regexes. Configurations parameters that match these\nregexes can be modified by user when SQL standard authorization is enabled.\nTo get the default value, use the 'set <param>' command.\nNote that the hive.conf.restricted.list checks are still enforced after the white list\ncheck"), 
        HIVE_AUTHORIZATION_SQL_STD_AUTH_CONFIG_WHITELIST_APPEND("hive.security.authorization.sqlstd.confwhitelist.append", (Object)"", "List of comma separated Java regexes, to be appended to list set in\nhive.security.authorization.sqlstd.confwhitelist. Using this list instead\nof updating the original list means that you can append to the defaults\nset by SQL standard authorization instead of replacing it entirely."), 
        HIVE_CLI_PRINT_HEADER("hive.cli.print.header", (Object)false, "Whether to print the names of the columns in query output."), 
        HIVE_ERROR_ON_EMPTY_PARTITION("hive.error.on.empty.partition", (Object)false, "Whether to throw an exception if dynamic partition insert generates empty results."), 
        HIVE_INDEX_COMPACT_FILE("hive.index.compact.file", (Object)"", "internal variable"), 
        HIVE_INDEX_BLOCKFILTER_FILE("hive.index.blockfilter.file", (Object)"", "internal variable"), 
        HIVE_INDEX_IGNORE_HDFS_LOC("hive.index.compact.file.ignore.hdfs", (Object)false, "When true the HDFS location stored in the index file will be ignored at runtime.\nIf the data got moved or the name of the cluster got changed, the index data should still be usable."), 
        HIVE_EXIM_URI_SCHEME_WL("hive.exim.uri.scheme.whitelist", (Object)"hdfs,pfile", "A comma separated list of acceptable URI schemes for import and export."), 
        HIVE_EXIM_RESTRICT_IMPORTS_INTO_REPLICATED_TABLES("hive.exim.strict.repl.tables", (Object)true, "Parameter that determines if 'regular' (non-replication) export dumps can be\nimported on to tables that are the target of replication. If this parameter is\nset, regular imports will check if the destination table(if it exists) has a 'repl.last.id' set on it. If so, it will fail."), 
        HIVE_REPL_TASK_FACTORY("hive.repl.task.factory", (Object)"org.apache.hive.hcatalog.api.repl.exim.EximReplicationTaskFactory", "Parameter that can be used to override which ReplicationTaskFactory will be\nused to instantiate ReplicationTask events. Override for third party repl plugins"), 
        HIVE_MAPPER_CANNOT_SPAN_MULTIPLE_PARTITIONS("hive.mapper.cannot.span.multiple.partitions", (Object)false, ""), 
        HIVE_REWORK_MAPREDWORK("hive.rework.mapredwork", (Object)false, "should rework the mapred work or not.\nThis is first introduced by SymlinkTextInputFormat to replace symlink files with real paths at compile time."), 
        HIVE_CONCATENATE_CHECK_INDEX("hive.exec.concatenate.check.index", (Object)true, "If this is set to true, Hive will throw error when doing\n'alter table tbl_name [partSpec] concatenate' on a table/partition\nthat has indexes on it. The reason the user want to set this to true\nis because it can help user to avoid handling all index drop, recreation,\nrebuild work. This is very helpful for tables with thousands of partitions."), 
        HIVE_IO_EXCEPTION_HANDLERS("hive.io.exception.handlers", (Object)"", "A list of io exception handler class names. This is used\nto construct a list exception handlers to handle exceptions thrown\nby record readers"), 
        HIVE_SERVER2_LOGGING_OPERATION_ENABLED("hive.server2.logging.operation.enabled", (Object)true, "When true, HS2 will save operation logs and make them available for clients"), 
        HIVE_SERVER2_LOGGING_OPERATION_LOG_LOCATION("hive.server2.logging.operation.log.location", (Object)("${system:java.io.tmpdir}" + File.separator + "${system:user.name}" + File.separator + "operation_logs"), "Top level directory where operation logs are stored if logging functionality is enabled"), 
        HIVE_SERVER2_LOGGING_OPERATION_LEVEL("hive.server2.logging.operation.level", (Object)"EXECUTION", (Validator)new Validator.StringSet(new String[] { "NONE", "EXECUTION", "PERFORMANCE", "VERBOSE" }), "HS2 operation logging mode available to clients to be set at session level.\nFor this to work, hive.server2.logging.operation.enabled should be set to true.\n  NONE: Ignore any logging\n  EXECUTION: Log completion of tasks\n  PERFORMANCE: Execution + Performance logs \n  VERBOSE: All logs"), 
        HIVE_LOG4J_FILE("hive.log4j.file", (Object)"", "Hive log4j configuration file.\nIf the property is not set, then logging will be initialized using hive-log4j.properties found on the classpath.\nIf the property is set, the value must be a valid URI (java.net.URI, e.g. \"file:///tmp/my-logging.properties\"), \nwhich you can then extract a URL from and pass to PropertyConfigurator.configure(URL)."), 
        HIVE_EXEC_LOG4J_FILE("hive.exec.log4j.file", (Object)"", "Hive log4j configuration file for execution mode(sub command).\nIf the property is not set, then logging will be initialized using hive-exec-log4j.properties found on the classpath.\nIf the property is set, the value must be a valid URI (java.net.URI, e.g. \"file:///tmp/my-logging.properties\"), \nwhich you can then extract a URL from and pass to PropertyConfigurator.configure(URL)."), 
        HIVE_LOG_EXPLAIN_OUTPUT("hive.log.explain.output", (Object)false, "Whether to log explain output for every query.\nWhen enabled, will log EXPLAIN EXTENDED output for the query at INFO log4j log level."), 
        HIVE_EXPLAIN_USER("hive.explain.user", (Object)false, "Whether to show explain result at user level.\nWhen enabled, will log EXPLAIN output for the query at user level."), 
        HIVE_AUTOGEN_COLUMNALIAS_PREFIX_LABEL("hive.autogen.columnalias.prefix.label", (Object)"_c", "String used as a prefix when auto generating column alias.\nBy default the prefix label will be appended with a column position number to form the column alias. \nAuto generation would happen if an aggregate function is used in a select clause without an explicit alias."), 
        HIVE_AUTOGEN_COLUMNALIAS_PREFIX_INCLUDEFUNCNAME("hive.autogen.columnalias.prefix.includefuncname", (Object)false, "Whether to include function name in the column alias auto generated by Hive."), 
        HIVE_PERF_LOGGER("hive.exec.perf.logger", (Object)"org.apache.hadoop.hive.ql.log.PerfLogger", "The class responsible for logging client side performance metrics. \nMust be a subclass of org.apache.hadoop.hive.ql.log.PerfLogger"), 
        HIVE_START_CLEANUP_SCRATCHDIR("hive.start.cleanup.scratchdir", (Object)false, "To cleanup the Hive scratchdir when starting the Hive Server"), 
        HIVE_INSERT_INTO_MULTILEVEL_DIRS("hive.insert.into.multilevel.dirs", (Object)false, "Where to insert into multilevel directories like\n\"insert directory '/HIVEFT25686/chinna/' from table\""), 
        HIVE_WAREHOUSE_SUBDIR_INHERIT_PERMS("hive.warehouse.subdir.inherit.perms", (Object)true, "Set this to false if the table directories should be created\nwith the permissions derived from dfs umask instead of\ninheriting the permission of the warehouse or database directory."), 
        HIVE_INSERT_INTO_EXTERNAL_TABLES("hive.insert.into.external.tables", (Object)true, "whether insert into external tables is allowed"), 
        HIVE_TEMPORARY_TABLE_STORAGE("hive.exec.temporary.table.storage", (Object)"default", (Validator)new Validator.StringSet(new String[] { "memory", "ssd", "default" }), "Define the storage policy for temporary tables.Choices between memory, ssd and default"), 
        HIVE_DRIVER_RUN_HOOKS("hive.exec.driver.run.hooks", (Object)"", "A comma separated list of hooks which implement HiveDriverRunHook. Will be run at the beginning and end of Driver.run, these will be run in the order specified."), 
        HIVE_DDL_OUTPUT_FORMAT("hive.ddl.output.format", (Object)null, "The data format to use for DDL output.  One of \"text\" (for human\nreadable text) or \"json\" (for a json object)."), 
        HIVE_ENTITY_SEPARATOR("hive.entity.separator", (Object)"@", "Separator used to construct names of tables and partitions. For example, dbname@tablename@partitionname"), 
        HIVE_CAPTURE_TRANSFORM_ENTITY("hive.entity.capture.transform", (Object)false, "Compiler to capture transform URI referred in the query"), 
        HIVE_DISPLAY_PARTITION_COLUMNS_SEPARATELY("hive.display.partition.cols.separately", (Object)true, "In older Hive version (0.10 and earlier) no distinction was made between\npartition columns or non-partition columns while displaying columns in describe\ntable. From 0.12 onwards, they are displayed separately. This flag will let you\nget old behavior, if desired. See, test-case in patch for HIVE-6689."), 
        HIVE_SSL_PROTOCOL_BLACKLIST("hive.ssl.protocol.blacklist", (Object)"SSLv2,SSLv3", "SSL Versions to disable for all Hive Servers"), 
        HIVE_SERVER2_MAX_START_ATTEMPTS("hive.server2.max.start.attempts", (Object)30L, (Validator)new Validator.RangeValidator(0L, null), "Number of times HiveServer2 will attempt to start before exiting, sleeping 60 seconds between retries. \n The default of 30 will keep trying for 30 minutes."), 
        HIVE_SERVER2_SUPPORT_DYNAMIC_SERVICE_DISCOVERY("hive.server2.support.dynamic.service.discovery", (Object)false, "Whether HiveServer2 supports dynamic service discovery for its clients. To support this, each instance of HiveServer2 currently uses ZooKeeper to register itself, when it is brought up. JDBC/ODBC clients should use the ZooKeeper ensemble: hive.zookeeper.quorum in their connection string."), 
        HIVE_SERVER2_ZOOKEEPER_NAMESPACE("hive.server2.zookeeper.namespace", (Object)"hiveserver2", "The parent node in ZooKeeper used by HiveServer2 when supporting dynamic service discovery."), 
        HIVE_SERVER2_GLOBAL_INIT_FILE_LOCATION("hive.server2.global.init.file.location", (Object)"${env:HIVE_CONF_DIR}", "Either the location of a HS2 global init file or a directory containing a .hiverc file. If the \nproperty is set, the value must be a valid path to an init file or directory where the init file is located."), 
        HIVE_SERVER2_TRANSPORT_MODE("hive.server2.transport.mode", (Object)"binary", (Validator)new Validator.StringSet(new String[] { "binary", "http" }), "Transport mode of HiveServer2."), 
        HIVE_SERVER2_THRIFT_BIND_HOST("hive.server2.thrift.bind.host", (Object)"", "Bind host on which to run the HiveServer2 Thrift service."), 
        HIVE_SERVER2_THRIFT_HTTP_PORT("hive.server2.thrift.http.port", (Object)10001, "Port number of HiveServer2 Thrift interface when hive.server2.transport.mode is 'http'."), 
        HIVE_SERVER2_THRIFT_HTTP_PATH("hive.server2.thrift.http.path", (Object)"cliservice", "Path component of URL endpoint when in HTTP mode."), 
        HIVE_SERVER2_THRIFT_MAX_MESSAGE_SIZE("hive.server2.thrift.max.message.size", (Object)104857600, "Maximum message size in bytes a HS2 server will accept."), 
        HIVE_SERVER2_THRIFT_HTTP_MAX_IDLE_TIME("hive.server2.thrift.http.max.idle.time", (Object)"1800s", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Maximum idle time for a connection on the server when in HTTP mode."), 
        HIVE_SERVER2_THRIFT_HTTP_WORKER_KEEPALIVE_TIME("hive.server2.thrift.http.worker.keepalive.time", (Object)"60s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Keepalive time for an idle http worker thread. When the number of workers exceeds min workers, excessive threads are killed after this time interval."), 
        HIVE_SERVER2_THRIFT_HTTP_COOKIE_AUTH_ENABLED("hive.server2.thrift.http.cookie.auth.enabled", (Object)true, "When true, HiveServer2 in HTTP transport mode, will use cookie based authentication mechanism."), 
        HIVE_SERVER2_THRIFT_HTTP_COOKIE_MAX_AGE("hive.server2.thrift.http.cookie.max.age", (Object)"86400s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Maximum age in seconds for server side cookie used by HS2 in HTTP mode."), 
        HIVE_SERVER2_THRIFT_HTTP_COOKIE_DOMAIN("hive.server2.thrift.http.cookie.domain", (Object)null, "Domain for the HS2 generated cookies"), 
        HIVE_SERVER2_THRIFT_HTTP_COOKIE_PATH("hive.server2.thrift.http.cookie.path", (Object)null, "Path for the HS2 generated cookies"), 
        HIVE_SERVER2_THRIFT_HTTP_COOKIE_IS_SECURE("hive.server2.thrift.http.cookie.is.secure", (Object)true, "Secure attribute of the HS2 generated cookie."), 
        HIVE_SERVER2_THRIFT_HTTP_COOKIE_IS_HTTPONLY("hive.server2.thrift.http.cookie.is.httponly", (Object)true, "HttpOnly attribute of the HS2 generated cookie."), 
        HIVE_SERVER2_THRIFT_PORT("hive.server2.thrift.port", (Object)10000, "Port number of HiveServer2 Thrift interface when hive.server2.transport.mode is 'binary'."), 
        HIVE_SERVER2_THRIFT_SASL_QOP("hive.server2.thrift.sasl.qop", (Object)"auth", (Validator)new Validator.StringSet(new String[] { "auth", "auth-int", "auth-conf" }), "Sasl QOP value; set it to one of following values to enable higher levels of\nprotection for HiveServer2 communication with clients.\nSetting hadoop.rpc.protection to a higher level than HiveServer2 does not\nmake sense in most situations. HiveServer2 ignores hadoop.rpc.protection in favor\nof hive.server2.thrift.sasl.qop.\n  \"auth\" - authentication only (default)\n  \"auth-int\" - authentication plus integrity protection\n  \"auth-conf\" - authentication plus integrity and confidentiality protection\nThis is applicable only if HiveServer2 is configured to use Kerberos authentication."), 
        HIVE_SERVER2_THRIFT_MIN_WORKER_THREADS("hive.server2.thrift.min.worker.threads", (Object)5, "Minimum number of Thrift worker threads"), 
        HIVE_SERVER2_THRIFT_MAX_WORKER_THREADS("hive.server2.thrift.max.worker.threads", (Object)500, "Maximum number of Thrift worker threads"), 
        HIVE_SERVER2_THRIFT_LOGIN_BEBACKOFF_SLOT_LENGTH("hive.server2.thrift.exponential.backoff.slot.length", (Object)"100ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Binary exponential backoff slot time for Thrift clients during login to HiveServer2,\nfor retries until hitting Thrift client timeout"), 
        HIVE_SERVER2_THRIFT_LOGIN_TIMEOUT("hive.server2.thrift.login.timeout", (Object)"20s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Timeout for Thrift clients during login to HiveServer2"), 
        HIVE_SERVER2_THRIFT_WORKER_KEEPALIVE_TIME("hive.server2.thrift.worker.keepalive.time", (Object)"60s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Keepalive time (in seconds) for an idle worker thread. When the number of workers exceeds min workers, excessive threads are killed after this time interval."), 
        HIVE_SERVER2_ASYNC_EXEC_THREADS("hive.server2.async.exec.threads", (Object)100, "Number of threads in the async thread pool for HiveServer2"), 
        HIVE_SERVER2_ASYNC_EXEC_SHUTDOWN_TIMEOUT("hive.server2.async.exec.shutdown.timeout", (Object)"10s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "How long HiveServer2 shutdown will wait for async threads to terminate."), 
        HIVE_SERVER2_ASYNC_EXEC_WAIT_QUEUE_SIZE("hive.server2.async.exec.wait.queue.size", (Object)100, "Size of the wait queue for async thread pool in HiveServer2.\nAfter hitting this limit, the async thread pool will reject new requests."), 
        HIVE_SERVER2_ASYNC_EXEC_KEEPALIVE_TIME("hive.server2.async.exec.keepalive.time", (Object)"10s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Time that an idle HiveServer2 async thread (from the thread pool) will wait for a new task\nto arrive before terminating"), 
        HIVE_SERVER2_LONG_POLLING_TIMEOUT("hive.server2.long.polling.timeout", (Object)"5000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Time that HiveServer2 will wait before responding to asynchronous calls that use long polling"), 
        HIVE_SERVER2_AUTHENTICATION("hive.server2.authentication", (Object)"NONE", (Validator)new Validator.StringSet(new String[] { "NOSASL", "NONE", "LDAP", "KERBEROS", "PAM", "CUSTOM" }), "Client authentication types.\n  NONE: no authentication check\n  LDAP: LDAP/AD based authentication\n  KERBEROS: Kerberos/GSSAPI authentication\n  CUSTOM: Custom authentication provider\n          (Use with property hive.server2.custom.authentication.class)\n  PAM: Pluggable authentication module\n  NOSASL:  Raw transport"), 
        HIVE_SERVER2_ALLOW_USER_SUBSTITUTION("hive.server2.allow.user.substitution", (Object)true, "Allow alternate user to be specified as part of HiveServer2 open connection request."), 
        HIVE_SERVER2_KERBEROS_KEYTAB("hive.server2.authentication.kerberos.keytab", (Object)"", "Kerberos keytab file for server principal"), 
        HIVE_SERVER2_KERBEROS_PRINCIPAL("hive.server2.authentication.kerberos.principal", (Object)"", "Kerberos server principal"), 
        HIVE_SERVER2_SPNEGO_KEYTAB("hive.server2.authentication.spnego.keytab", (Object)"", "keytab file for SPNego principal, optional,\ntypical value would look like /etc/security/keytabs/spnego.service.keytab,\nThis keytab would be used by HiveServer2 when Kerberos security is enabled and \nHTTP transport mode is used.\nThis needs to be set only if SPNEGO is to be used in authentication.\nSPNego authentication would be honored only if valid\n  hive.server2.authentication.spnego.principal\nand\n  hive.server2.authentication.spnego.keytab\nare specified."), 
        HIVE_SERVER2_SPNEGO_PRINCIPAL("hive.server2.authentication.spnego.principal", (Object)"", "SPNego service principal, optional,\ntypical value would look like HTTP/_HOST@EXAMPLE.COM\nSPNego service principal would be used by HiveServer2 when Kerberos security is enabled\nand HTTP transport mode is used.\nThis needs to be set only if SPNEGO is to be used in authentication."), 
        HIVE_SERVER2_PLAIN_LDAP_URL("hive.server2.authentication.ldap.url", (Object)null, "LDAP connection URL(s),\nthis value could contain URLs to mutiple LDAP servers instances for HA,\neach LDAP URL is separated by a SPACE character. URLs are used in the \n order specified until a connection is successful."), 
        HIVE_SERVER2_PLAIN_LDAP_BASEDN("hive.server2.authentication.ldap.baseDN", (Object)null, "LDAP base DN"), 
        HIVE_SERVER2_PLAIN_LDAP_DOMAIN("hive.server2.authentication.ldap.Domain", (Object)null, ""), 
        HIVE_SERVER2_CUSTOM_AUTHENTICATION_CLASS("hive.server2.custom.authentication.class", (Object)null, "Custom authentication class. Used when property\n'hive.server2.authentication' is set to 'CUSTOM'. Provided class\nmust be a proper implementation of the interface\norg.apache.hive.service.auth.PasswdAuthenticationProvider. HiveServer2\nwill call its Authenticate(user, passed) method to authenticate requests.\nThe implementation may optionally implement Hadoop's\norg.apache.hadoop.conf.Configurable class to grab Hive's Configuration object."), 
        HIVE_SERVER2_PAM_SERVICES("hive.server2.authentication.pam.services", (Object)null, "List of the underlying pam services that should be used when auth type is PAM\nA file with the same name must exist in /etc/pam.d"), 
        HIVE_SERVER2_ENABLE_DOAS("hive.server2.enable.doAs", (Object)true, "Setting this property to true will have HiveServer2 execute\nHive operations as the user making the calls to it."), 
        HIVE_SERVER2_TABLE_TYPE_MAPPING("hive.server2.table.type.mapping", (Object)"CLASSIC", (Validator)new Validator.StringSet(new String[] { "CLASSIC", "HIVE" }), "This setting reflects how HiveServer2 will report the table types for JDBC and other\nclient implementations that retrieve the available tables and supported table types\n  HIVE : Exposes Hive's native table types like MANAGED_TABLE, EXTERNAL_TABLE, VIRTUAL_VIEW\n  CLASSIC : More generic types like TABLE and VIEW"), 
        HIVE_SERVER2_SESSION_HOOK("hive.server2.session.hook", (Object)"", ""), 
        HIVE_SERVER2_USE_SSL("hive.server2.use.SSL", (Object)false, "Set this to true for using SSL encryption in HiveServer2."), 
        HIVE_SERVER2_SSL_KEYSTORE_PATH("hive.server2.keystore.path", (Object)"", "SSL certificate keystore location."), 
        HIVE_SERVER2_SSL_KEYSTORE_PASSWORD("hive.server2.keystore.password", (Object)"", "SSL certificate keystore password."), 
        HIVE_SERVER2_MAP_FAIR_SCHEDULER_QUEUE("hive.server2.map.fair.scheduler.queue", (Object)true, "If the YARN fair scheduler is configured and HiveServer2 is running in non-impersonation mode,\nthis setting determines the user for fair scheduler queue mapping.\nIf set to true (default), the logged-in user determines the fair scheduler queue\nfor submitted jobs, so that map reduce resource usage can be tracked by user.\nIf set to false, all Hive jobs go to the 'hive' user's queue."), 
        HIVE_SERVER2_BUILTIN_UDF_WHITELIST("hive.server2.builtin.udf.whitelist", (Object)"", "Comma separated list of builtin udf names allowed in queries.\nAn empty whitelist allows all builtin udfs to be executed.  The udf black list takes precedence over udf white list"), 
        HIVE_SERVER2_BUILTIN_UDF_BLACKLIST("hive.server2.builtin.udf.blacklist", (Object)"", "Comma separated list of udfs names. These udfs will not be allowed in queries. The udf black list takes precedence over udf white list"), 
        HIVE_SECURITY_COMMAND_WHITELIST("hive.security.command.whitelist", (Object)"set,reset,dfs,add,list,delete,reload,compile", "Comma separated list of non-SQL Hive commands users are authorized to execute"), 
        HIVE_SERVER2_SESSION_CHECK_INTERVAL("hive.server2.session.check.interval", (Object)"6h", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS, 3000L, true, null, false), "The check interval for session/operation timeout, which can be disabled by setting to zero or negative value."), 
        HIVE_SERVER2_IDLE_SESSION_TIMEOUT("hive.server2.idle.session.timeout", (Object)"7d", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Session will be closed when it's not accessed for this duration, which can be disabled by setting to zero or negative value."), 
        HIVE_SERVER2_IDLE_OPERATION_TIMEOUT("hive.server2.idle.operation.timeout", (Object)"5d", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Operation will be closed when it's not accessed for this duration of time, which can be disabled by setting to zero value.\n  With positive value, it's checked for operations in terminal state only (FINISHED, CANCELED, CLOSED, ERROR).\n  With negative value, it's checked for all of the operations regardless of state."), 
        HIVE_SERVER2_IDLE_SESSION_CHECK_OPERATION("hive.server2.idle.session.check.operation", (Object)true, "Session will be considered to be idle only if there is no activity, and there is no pending operation.\n This setting takes effect only if session idle timeout (hive.server2.idle.session.timeout) and checking\n(hive.server2.session.check.interval) are enabled."), 
        HIVE_CONF_RESTRICTED_LIST("hive.conf.restricted.list", (Object)"hive.security.authenticator.manager,hive.security.authorization.manager,hive.users.in.admin.role", "Comma separated list of configuration options which are immutable at runtime"), 
        HIVE_CONF_HIDDEN_LIST("hive.conf.hidden.list", (Object)(ConfVars.METASTOREPWD.varname + "," + ConfVars.HIVE_SERVER2_SSL_KEYSTORE_PASSWORD.varname), "Comma separated list of configuration options which should not be read by normal user like passwords"), 
        HIVE_MULTI_INSERT_MOVE_TASKS_SHARE_DEPENDENCIES("hive.multi.insert.move.tasks.share.dependencies", (Object)false, "If this is set all move tasks for tables/partitions (not directories) at the end of a\nmulti-insert query will only begin once the dependencies for all these move tasks have been\nmet.\nAdvantages: If concurrency is enabled, the locks will only be released once the query has\n            finished, so with this config enabled, the time when the table/partition is\n            generated will be much closer to when the lock on it is released.\nDisadvantages: If concurrency is not enabled, with this disabled, the tables/partitions which\n               are produced by this query and finish earlier will be available for querying\n               much earlier.  Since the locks are only released once the query finishes, this\n               does not apply if concurrency is enabled."), 
        HIVE_INFER_BUCKET_SORT("hive.exec.infer.bucket.sort", (Object)false, "If this is set, when writing partitions, the metadata will include the bucketing/sorting\nproperties with which the data was written if any (this will not overwrite the metadata\ninherited from the table if the table is bucketed/sorted)"), 
        HIVE_INFER_BUCKET_SORT_NUM_BUCKETS_POWER_TWO("hive.exec.infer.bucket.sort.num.buckets.power.two", (Object)false, "If this is set, when setting the number of reducers for the map reduce task which writes the\nfinal output files, it will choose a number which is a power of two, unless the user specifies\nthe number of reducers to use using mapred.reduce.tasks.  The number of reducers\nmay be set to a power of two, only to be followed by a merge task meaning preventing\nanything from being inferred.\nWith hive.exec.infer.bucket.sort set to true:\nAdvantages:  If this is not set, the number of buckets for partitions will seem arbitrary,\n             which means that the number of mappers used for optimized joins, for example, will\n             be very low.  With this set, since the number of buckets used for any partition is\n             a power of two, the number of mappers used for optimized joins will be the least\n             number of buckets used by any partition being joined.\nDisadvantages: This may mean a much larger or much smaller number of reducers being used in the\n               final map reduce job, e.g. if a job was originally going to take 257 reducers,\n               it will now take 512 reducers, similarly if the max number of reducers is 511,\n               and a job was going to use this many, it will now use 256 reducers."), 
        HIVEOPTLISTBUCKETING("hive.optimize.listbucketing", (Object)false, "Enable list bucketing optimizer. Default value is false so that we disable it by default."), 
        SERVER_READ_SOCKET_TIMEOUT("hive.server.read.socket.timeout", (Object)"10s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Timeout for the HiveServer to close the connection if no response from the client. By default, 10 seconds."), 
        SERVER_TCP_KEEP_ALIVE("hive.server.tcp.keepalive", (Object)true, "Whether to enable TCP keepalive for the Hive Server. Keepalive will prevent accumulation of half-open connections."), 
        HIVE_DECODE_PARTITION_NAME("hive.decode.partition.name", (Object)false, "Whether to show the unquoted partition names in query results."), 
        HIVE_EXECUTION_ENGINE("hive.execution.engine", (Object)"mr", (Validator)new Validator.StringSet(new String[] { "mr", "tez", "spark" }), "Chooses execution engine. Options are: mr (Map reduce, default), tez (hadoop 2 only), spark"), 
        HIVE_JAR_DIRECTORY("hive.jar.directory", (Object)null, "This is the location hive in tez mode will look for to find a site wide \ninstalled hive instance."), 
        HIVE_USER_INSTALL_DIR("hive.user.install.directory", (Object)"hdfs:///user/", "If hive (in tez mode only) cannot find a usable hive jar in \"hive.jar.directory\", \nit will upload the hive jar to \"hive.user.install.directory/user.name\"\nand use it to run queries."), 
        HIVE_VECTORIZATION_ENABLED("hive.vectorized.execution.enabled", (Object)false, "This flag should be set to true to enable vectorized mode of query execution.\nThe default value is false."), 
        HIVE_VECTORIZATION_REDUCE_ENABLED("hive.vectorized.execution.reduce.enabled", (Object)true, "This flag should be set to true to enable vectorized mode of the reduce-side of query execution.\nThe default value is true."), 
        HIVE_VECTORIZATION_REDUCE_GROUPBY_ENABLED("hive.vectorized.execution.reduce.groupby.enabled", (Object)true, "This flag should be set to true to enable vectorized mode of the reduce-side GROUP BY query execution.\nThe default value is true."), 
        HIVE_VECTORIZATION_MAPJOIN_NATIVE_ENABLED("hive.vectorized.execution.mapjoin.native.enabled", (Object)true, "This flag should be set to true to enable native (i.e. non-pass through) vectorization\nof queries using MapJoin.\nThe default value is true."), 
        HIVE_VECTORIZATION_MAPJOIN_NATIVE_MULTIKEY_ONLY_ENABLED("hive.vectorized.execution.mapjoin.native.multikey.only.enabled", (Object)false, "This flag should be set to true to restrict use of native vector map join hash tables to\nthe MultiKey in queries using MapJoin.\nThe default value is false."), 
        HIVE_VECTORIZATION_MAPJOIN_NATIVE_MINMAX_ENABLED("hive.vectorized.execution.mapjoin.minmax.enabled", (Object)false, "This flag should be set to true to enable vector map join hash tables to\nuse max / max filtering for integer join queries using MapJoin.\nThe default value is false."), 
        HIVE_VECTORIZATION_MAPJOIN_NATIVE_OVERFLOW_REPEATED_THRESHOLD("hive.vectorized.execution.mapjoin.overflow.repeated.threshold", (Object)(-1), "The number of small table rows for a match in vector map join hash tables\nwhere we use the repeated field optimization in overflow vectorized row batch for join queries using MapJoin.\nA value of -1 means do use the join result optimization.  Otherwise, threshold value can be 0 to maximum integer."), 
        HIVE_VECTORIZATION_MAPJOIN_NATIVE_FAST_HASHTABLE_ENABLED("hive.vectorized.execution.mapjoin.native.fast.hashtable.enabled", (Object)false, "This flag should be set to true to enable use of native fast vector map join hash tables in\nqueries using MapJoin.\nThe default value is false."), 
        HIVE_VECTORIZATION_GROUPBY_CHECKINTERVAL("hive.vectorized.groupby.checkinterval", (Object)100000, "Number of entries added to the group by aggregation hash before a recomputation of average entry size is performed."), 
        HIVE_VECTORIZATION_GROUPBY_MAXENTRIES("hive.vectorized.groupby.maxentries", (Object)1000000, "Max number of entries in the vector group by aggregation hashtables. \nExceeding this will trigger a flush irrelevant of memory pressure condition."), 
        HIVE_VECTORIZATION_GROUPBY_FLUSH_PERCENT("hive.vectorized.groupby.flush.percent", (Object)0.1f, "Percent of entries in the group by aggregation hash flushed when the memory threshold is exceeded."), 
        HIVE_TYPE_CHECK_ON_INSERT("hive.typecheck.on.insert", (Object)true, "This property has been extended to control whether to check, convert, and normalize partition value to conform to its column type in partition operations including but not limited to insert, such as alter, describe etc."), 
        HIVE_HADOOP_CLASSPATH("hive.hadoop.classpath", (Object)null, "For Windows OS, we need to pass HIVE_HADOOP_CLASSPATH Java parameter while starting HiveServer2 \nusing \"-hiveconf hive.hadoop.classpath=%HIVE_LIB%\"."), 
        HIVE_RPC_QUERY_PLAN("hive.rpc.query.plan", (Object)false, "Whether to send the query plan via local resource or RPC"), 
        HIVE_AM_SPLIT_GENERATION("hive.compute.splits.in.am", (Object)true, "Whether to generate the splits locally or in the AM (tez only)"), 
        HIVE_PREWARM_ENABLED("hive.prewarm.enabled", (Object)false, "Enables container prewarm for Tez (Hadoop 2 only)"), 
        HIVE_PREWARM_NUM_CONTAINERS("hive.prewarm.numcontainers", (Object)10, "Controls the number of containers to prewarm for Tez (Hadoop 2 only)"), 
        HIVESTAGEIDREARRANGE("hive.stageid.rearrange", (Object)"none", (Validator)new Validator.StringSet(new String[] { "none", "idonly", "traverse", "execution" }), ""), 
        HIVEEXPLAINDEPENDENCYAPPENDTASKTYPES("hive.explain.dependency.append.tasktype", (Object)false, ""), 
        HIVECOUNTERGROUP("hive.counters.group.name", (Object)"HIVE", "The name of counter group for internal Hive variables (CREATED_FILE, FATAL_ERROR, etc.)"), 
        HIVE_SERVER2_TEZ_DEFAULT_QUEUES("hive.server2.tez.default.queues", (Object)"", "A list of comma separated values corresponding to YARN queues of the same name.\nWhen HiveServer2 is launched in Tez mode, this configuration needs to be set\nfor multiple Tez sessions to run in parallel on the cluster."), 
        HIVE_SERVER2_TEZ_SESSIONS_PER_DEFAULT_QUEUE("hive.server2.tez.sessions.per.default.queue", (Object)1, "A positive integer that determines the number of Tez sessions that should be\nlaunched on each of the queues specified by \"hive.server2.tez.default.queues\".\nDetermines the parallelism on each queue."), 
        HIVE_SERVER2_TEZ_INITIALIZE_DEFAULT_SESSIONS("hive.server2.tez.initialize.default.sessions", (Object)false, "This flag is used in HiveServer2 to enable a user to use HiveServer2 without\nturning on Tez for HiveServer2. The user could potentially want to run queries\nover Tez without the pool of sessions."), 
        HIVE_QUOTEDID_SUPPORT("hive.support.quoted.identifiers", (Object)"column", (Validator)new Validator.StringSet(new String[] { "none", "column" }), "Whether to use quoted identifier. 'none' or 'column' can be used. \n  none: default(past) behavior. Implies only alphaNumeric and underscore are valid characters in identifiers.\n  column: implies column names can contain any character."), 
        HIVE_SUPPORT_SQL11_RESERVED_KEYWORDS("hive.support.sql11.reserved.keywords", (Object)true, "This flag should be set to true to enable support for SQL2011 reserved keywords.\nThe default value is true."), 
        USERS_IN_ADMIN_ROLE("hive.users.in.admin.role", "", false, "Comma separated list of users who are in admin role for bootstrapping.\nMore users can be added in ADMIN role later."), 
        HIVE_COMPAT("hive.compat", (Object)HiveCompat.DEFAULT_COMPAT_LEVEL, "Enable (configurable) deprecated behaviors by setting desired level of backward compatibility.\nSetting to 0.12:\n  Maintains division behavior: int / int = double"), 
        HIVE_CONVERT_JOIN_BUCKET_MAPJOIN_TEZ("hive.convert.join.bucket.mapjoin.tez", (Object)false, "Whether joins can be automatically converted to bucket map joins in hive \nwhen tez is used as the execution engine."), 
        HIVE_CHECK_CROSS_PRODUCT("hive.exec.check.crossproducts", (Object)true, "Check if a plan contains a Cross Product. If there is one, output a warning to the Session's console."), 
        HIVE_LOCALIZE_RESOURCE_WAIT_INTERVAL("hive.localize.resource.wait.interval", (Object)"5000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Time to wait for another thread to localize the same resource for hive-tez."), 
        HIVE_LOCALIZE_RESOURCE_NUM_WAIT_ATTEMPTS("hive.localize.resource.num.wait.attempts", (Object)5, "The number of attempts waiting for localizing a resource in hive-tez."), 
        TEZ_AUTO_REDUCER_PARALLELISM("hive.tez.auto.reducer.parallelism", (Object)false, "Turn on Tez' auto reducer parallelism feature. When enabled, Hive will still estimate data sizes\nand set parallelism estimates. Tez will sample source vertices' output sizes and adjust the estimates at runtime as\nnecessary."), 
        TEZ_MAX_PARTITION_FACTOR("hive.tez.max.partition.factor", (Object)2.0f, "When auto reducer parallelism is enabled this factor will be used to over-partition data in shuffle edges."), 
        TEZ_MIN_PARTITION_FACTOR("hive.tez.min.partition.factor", (Object)0.25f, "When auto reducer parallelism is enabled this factor will be used to put a lower limit to the number\nof reducers that tez specifies."), 
        TEZ_DYNAMIC_PARTITION_PRUNING("hive.tez.dynamic.partition.pruning", (Object)true, "When dynamic pruning is enabled, joins on partition keys will be processed by sending\nevents from the processing vertices to the Tez application master. These events will be\nused to prune unnecessary partitions."), 
        TEZ_DYNAMIC_PARTITION_PRUNING_MAX_EVENT_SIZE("hive.tez.dynamic.partition.pruning.max.event.size", (Object)1048576L, "Maximum size of events sent by processors in dynamic pruning. If this size is crossed no pruning will take place."), 
        TEZ_DYNAMIC_PARTITION_PRUNING_MAX_DATA_SIZE("hive.tez.dynamic.partition.pruning.max.data.size", (Object)104857600L, "Maximum total data size of events in dynamic pruning."), 
        TEZ_SMB_NUMBER_WAVES("hive.tez.smb.number.waves", (Object)0.5f, "The number of waves in which to run the SMB join. Account for cluster being occupied. Ideally should be 1 wave."), 
        TEZ_EXEC_SUMMARY("hive.tez.exec.print.summary", (Object)false, "Display breakdown of execution steps, for every query executed by the shell."), 
        TEZ_EXEC_INPLACE_PROGRESS("hive.tez.exec.inplace.progress", (Object)true, "Updates tez job execution progress in-place in the terminal."), 
        SPARK_CLIENT_FUTURE_TIMEOUT("hive.spark.client.future.timeout", (Object)"60s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Timeout for requests from Hive client to remote Spark driver."), 
        SPARK_JOB_MONITOR_TIMEOUT("hive.spark.job.monitor.timeout", (Object)"60s", (Validator)new Validator.TimeValidator(TimeUnit.SECONDS), "Timeout for job monitor to get Spark job state."), 
        SPARK_RPC_CLIENT_CONNECT_TIMEOUT("hive.spark.client.connect.timeout", (Object)"1000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Timeout for remote Spark driver in connecting back to Hive client."), 
        SPARK_RPC_CLIENT_HANDSHAKE_TIMEOUT("hive.spark.client.server.connect.timeout", (Object)"90000ms", (Validator)new Validator.TimeValidator(TimeUnit.MILLISECONDS), "Timeout for handshake between Hive client and remote Spark driver.  Checked by both processes."), 
        SPARK_RPC_SECRET_RANDOM_BITS("hive.spark.client.secret.bits", (Object)"256", "Number of bits of randomness in the generated secret for communication between Hive client and remote Spark driver. Rounded down to the nearest multiple of 8."), 
        SPARK_RPC_MAX_THREADS("hive.spark.client.rpc.threads", (Object)8, "Maximum number of threads for remote Spark driver's RPC event loop."), 
        SPARK_RPC_MAX_MESSAGE_SIZE("hive.spark.client.rpc.max.size", (Object)52428800, "Maximum message size in bytes for communication between Hive client and remote Spark driver. Default is 50MB."), 
        SPARK_RPC_CHANNEL_LOG_LEVEL("hive.spark.client.channel.log.level", (Object)null, "Channel logging level for remote Spark driver.  One of {DEBUG, ERROR, INFO, TRACE, WARN}."), 
        SPARK_RPC_SASL_MECHANISM("hive.spark.client.rpc.sasl.mechanisms", (Object)"DIGEST-MD5", "Name of the SASL mechanism to use for authentication."), 
        NWAYJOINREORDER("hive.reorder.nway.joins", (Object)true, "Runs reordering of tables within single n-way join (i.e.: picks streamtable)"), 
        HIVE_LOG_N_RECORDS("hive.log.every.n.records", (Object)0L, (Validator)new Validator.RangeValidator(0L, null), "If value is greater than 0 logs in fixed intervals of size n rather than exponentially.");
        
        public final String varname;
        private final String defaultExpr;
        public final String defaultStrVal;
        public final int defaultIntVal;
        public final long defaultLongVal;
        public final float defaultFloatVal;
        public final boolean defaultBoolVal;
        private final Class<?> valClass;
        private final VarType valType;
        private final Validator validator;
        private final String description;
        private final boolean excluded;
        private final boolean caseSensitive;
        
        private ConfVars(final String varname, final Object defaultVal, final String description) {
            this(varname, defaultVal, null, description, true, false);
        }
        
        private ConfVars(final String varname, final Object defaultVal, final String description, final boolean excluded) {
            this(varname, defaultVal, null, description, true, excluded);
        }
        
        private ConfVars(final String varname, final String defaultVal, final boolean caseSensitive, final String description) {
            this(varname, defaultVal, null, description, caseSensitive, false);
        }
        
        private ConfVars(final String varname, final Object defaultVal, final Validator validator, final String description) {
            this(varname, defaultVal, validator, description, true, false);
        }
        
        private ConfVars(final String varname, final Object defaultVal, final Validator validator, final String description, final boolean caseSensitive, final boolean excluded) {
            this.varname = varname;
            this.validator = validator;
            this.description = description;
            this.defaultExpr = ((defaultVal == null) ? null : String.valueOf(defaultVal));
            this.excluded = excluded;
            this.caseSensitive = caseSensitive;
            if (defaultVal == null || defaultVal instanceof String) {
                this.valClass = String.class;
                this.valType = VarType.STRING;
                this.defaultStrVal = SystemVariables.substitute((String)defaultVal);
                this.defaultIntVal = -1;
                this.defaultLongVal = -1L;
                this.defaultFloatVal = -1.0f;
                this.defaultBoolVal = false;
            }
            else if (defaultVal instanceof Integer) {
                this.valClass = Integer.class;
                this.valType = VarType.INT;
                this.defaultStrVal = null;
                this.defaultIntVal = (int)defaultVal;
                this.defaultLongVal = -1L;
                this.defaultFloatVal = -1.0f;
                this.defaultBoolVal = false;
            }
            else if (defaultVal instanceof Long) {
                this.valClass = Long.class;
                this.valType = VarType.LONG;
                this.defaultStrVal = null;
                this.defaultIntVal = -1;
                this.defaultLongVal = (long)defaultVal;
                this.defaultFloatVal = -1.0f;
                this.defaultBoolVal = false;
            }
            else if (defaultVal instanceof Float) {
                this.valClass = Float.class;
                this.valType = VarType.FLOAT;
                this.defaultStrVal = null;
                this.defaultIntVal = -1;
                this.defaultLongVal = -1L;
                this.defaultFloatVal = (float)defaultVal;
                this.defaultBoolVal = false;
            }
            else {
                if (!(defaultVal instanceof Boolean)) {
                    throw new IllegalArgumentException("Not supported type value " + defaultVal.getClass() + " for name " + varname);
                }
                this.valClass = Boolean.class;
                this.valType = VarType.BOOLEAN;
                this.defaultStrVal = null;
                this.defaultIntVal = -1;
                this.defaultLongVal = -1L;
                this.defaultFloatVal = -1.0f;
                this.defaultBoolVal = (boolean)defaultVal;
            }
        }
        
        public boolean isType(final String value) {
            return this.valType.isType(value);
        }
        
        public Validator getValidator() {
            return this.validator;
        }
        
        public String validate(final String value) {
            return (this.validator == null) ? null : this.validator.validate(value);
        }
        
        public String validatorDescription() {
            return (this.validator == null) ? null : this.validator.toDescription();
        }
        
        public String typeString() {
            String type = this.valType.typeString();
            if (this.valType == VarType.STRING && this.validator != null && this.validator instanceof Validator.TimeValidator) {
                type += "(TIME)";
            }
            return type;
        }
        
        public String getRawDescription() {
            return this.description;
        }
        
        public String getDescription() {
            final String validator = this.validatorDescription();
            if (validator != null) {
                return validator + ".\n" + this.description;
            }
            return this.description;
        }
        
        public boolean isExcluded() {
            return this.excluded;
        }
        
        public boolean isCaseSensitive() {
            return this.caseSensitive;
        }
        
        @Override
        public String toString() {
            return this.varname;
        }
        
        private static String findHadoopBinary() {
            String val = System.getenv("HADOOP_HOME");
            if (val == null) {
                val = System.getenv("HADOOP_PREFIX");
            }
            val = ((val == null) ? (File.separator + "usr") : val) + File.separator + "bin" + File.separator + "hadoop";
            return val + (Shell.WINDOWS ? ".cmd" : "");
        }
        
        public String getDefaultValue() {
            return this.valType.defaultValueString(this);
        }
        
        public String getDefaultExpr() {
            return this.defaultExpr;
        }
        
        enum VarType
        {
            STRING {
                @Override
                void checkType(final String value) throws Exception {
                }
                
                @Override
                String defaultValueString(final ConfVars confVar) {
                    return confVar.defaultStrVal;
                }
            }, 
            INT {
                @Override
                void checkType(final String value) throws Exception {
                    Integer.valueOf(value);
                }
            }, 
            LONG {
                @Override
                void checkType(final String value) throws Exception {
                    Long.valueOf(value);
                }
            }, 
            FLOAT {
                @Override
                void checkType(final String value) throws Exception {
                    Float.valueOf(value);
                }
            }, 
            BOOLEAN {
                @Override
                void checkType(final String value) throws Exception {
                    Boolean.valueOf(value);
                }
            };
            
            boolean isType(final String value) {
                try {
                    this.checkType(value);
                }
                catch (Exception e) {
                    return false;
                }
                return true;
            }
            
            String typeString() {
                return this.name().toUpperCase();
            }
            
            String defaultValueString(final ConfVars confVar) {
                return confVar.defaultExpr;
            }
            
            abstract void checkType(final String p0) throws Exception;
        }
    }
}
