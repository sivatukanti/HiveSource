// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema;

import org.datanucleus.ClassConstants;
import java.util.List;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.datanucleus.PersistenceConfiguration;
import java.util.Iterator;
import org.datanucleus.util.StringUtils;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.HashMap;
import org.datanucleus.store.StoreManager;
import org.datanucleus.metadata.FileMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaDataManager;
import java.util.Set;
import java.util.Properties;
import org.datanucleus.NucleusContext;
import java.util.TreeSet;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.Map;
import org.datanucleus.util.PersistenceUtils;
import java.util.StringTokenizer;
import java.io.File;
import org.datanucleus.util.CommandLine;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.Localiser;

public class SchemaTool
{
    protected static final Localiser LOCALISER;
    private String apiName;
    private String ddlFilename;
    private boolean completeDdl;
    private boolean includeAutoStart;
    private boolean verbose;
    public static final int SCHEMATOOL_CREATE_MODE = 1;
    public static final int SCHEMATOOL_DELETE_MODE = 2;
    public static final int SCHEMATOOL_DELETECREATE_MODE = 3;
    public static final int SCHEMATOOL_VALIDATE_MODE = 4;
    public static final int SCHEMATOOL_DATABASE_INFO_MODE = 5;
    public static final int SCHEMATOOL_SCHEMA_INFO_MODE = 6;
    public static final NucleusLogger LOGGER;
    
    public static void main(final String[] args) throws Exception {
        final SchemaTool tool = new SchemaTool();
        final CommandLine cmd = new CommandLine();
        cmd.addOption("create", "create", null, SchemaTool.LOCALISER.msg(false, "014026"));
        cmd.addOption("delete", "delete", null, SchemaTool.LOCALISER.msg(false, "014027"));
        cmd.addOption("deletecreate", "deletecreate", null, SchemaTool.LOCALISER.msg(false, "014044"));
        cmd.addOption("validate", "validate", null, SchemaTool.LOCALISER.msg(false, "014028"));
        cmd.addOption("dbinfo", "dbinfo", null, SchemaTool.LOCALISER.msg(false, "014029"));
        cmd.addOption("schemainfo", "schemainfo", null, SchemaTool.LOCALISER.msg(false, "014030"));
        cmd.addOption("help", "help", null, SchemaTool.LOCALISER.msg(false, "014033"));
        cmd.addOption("ddlFile", "ddlFile", "ddlFile", SchemaTool.LOCALISER.msg(false, "014031"));
        cmd.addOption("completeDdl", "completeDdl", null, SchemaTool.LOCALISER.msg(false, "014032"));
        cmd.addOption("includeAutoStart", "includeAutoStart", null, "Include Auto-Start Mechanisms");
        cmd.addOption("api", "api", "api", "API Adapter (JDO, JPA, etc)");
        cmd.addOption("v", "verbose", null, "verbose output");
        cmd.addOption("pu", "persistenceUnit", "<persistence-unit>", "name of the persistence unit to handle the schema for");
        cmd.addOption("props", "properties", "props", "path to a properties file");
        cmd.parse(args);
        final String[] filenames = cmd.getDefaultArgs();
        if (cmd.hasOption("api")) {
            tool.setApi(cmd.getOptionArg("api"));
        }
        String msg = null;
        Mode mode = Mode.CREATE;
        if (cmd.hasOption("create")) {
            mode = Mode.CREATE;
            msg = SchemaTool.LOCALISER.msg(false, "014000");
        }
        else if (cmd.hasOption("delete")) {
            mode = Mode.DELETE;
            msg = SchemaTool.LOCALISER.msg(false, "014001");
        }
        else if (cmd.hasOption("deletecreate")) {
            mode = Mode.DELETE_CREATE;
            msg = SchemaTool.LOCALISER.msg(false, "014045");
        }
        else if (cmd.hasOption("validate")) {
            mode = Mode.VALIDATE;
            msg = SchemaTool.LOCALISER.msg(false, "014002");
        }
        else if (cmd.hasOption("dbinfo")) {
            mode = Mode.DATABASE_INFO;
            msg = SchemaTool.LOCALISER.msg(false, "014003");
        }
        else if (cmd.hasOption("schemainfo")) {
            mode = Mode.SCHEMA_INFO;
            msg = SchemaTool.LOCALISER.msg(false, "014004");
        }
        else if (cmd.hasOption("help")) {
            System.out.println(SchemaTool.LOCALISER.msg(false, "014023"));
            System.out.println(SchemaTool.LOCALISER.msg(false, "014024"));
            System.out.println(SchemaTool.LOCALISER.msg(false, "014025"));
            System.out.println(cmd.toString());
            System.out.println(SchemaTool.LOCALISER.msg(false, "014034"));
            System.out.println(SchemaTool.LOCALISER.msg(false, "014035"));
            System.exit(0);
        }
        SchemaTool.LOGGER.info(msg);
        System.out.println(msg);
        String propsFileName = null;
        String persistenceUnitName = null;
        if (cmd.hasOption("ddlFile")) {
            tool.setDdlFile(cmd.getOptionArg("ddlFile"));
        }
        if (cmd.hasOption("completeDdl")) {
            tool.setCompleteDdl(true);
        }
        if (cmd.hasOption("includeAutoStart")) {
            tool.setIncludeAutoStart(true);
        }
        if (cmd.hasOption("v")) {
            tool.setVerbose(true);
        }
        if (cmd.hasOption("pu")) {
            persistenceUnitName = cmd.getOptionArg("pu");
        }
        if (cmd.hasOption("props")) {
            propsFileName = cmd.getOptionArg("props");
        }
        msg = SchemaTool.LOCALISER.msg(false, "014005");
        SchemaTool.LOGGER.info(msg);
        if (tool.isVerbose()) {
            System.out.println(msg);
        }
        final StringTokenizer tokeniser = new StringTokenizer(System.getProperty("java.class.path"), File.pathSeparator);
        while (tokeniser.hasMoreTokens()) {
            msg = SchemaTool.LOCALISER.msg(false, "014006", tokeniser.nextToken());
            SchemaTool.LOGGER.info(msg);
            if (tool.isVerbose()) {
                System.out.println(msg);
            }
        }
        if (tool.isVerbose()) {
            System.out.println();
        }
        final String ddlFilename = tool.getDdlFile();
        if (ddlFilename != null) {
            msg = SchemaTool.LOCALISER.msg(false, tool.getCompleteDdl() ? "014018" : "014019", ddlFilename);
            SchemaTool.LOGGER.info(msg);
            if (tool.isVerbose()) {
                System.out.println(msg);
                System.out.println();
            }
        }
        NucleusContext nucleusCtx = null;
        try {
            if (propsFileName != null) {
                final Properties props = PersistenceUtils.setPropertiesUsingFile(propsFileName);
                nucleusCtx = getNucleusContextForMode(mode, tool.getApi(), props, persistenceUnitName, ddlFilename, tool.isVerbose());
            }
            else {
                nucleusCtx = getNucleusContextForMode(mode, tool.getApi(), null, persistenceUnitName, ddlFilename, tool.isVerbose());
            }
        }
        catch (Exception e) {
            SchemaTool.LOGGER.error("Error creating NucleusContext", e);
            System.out.println(SchemaTool.LOCALISER.msg(false, "014008", e.getMessage()));
            System.exit(1);
            return;
        }
        Set<String> classNames = null;
        if (mode != Mode.SCHEMA_INFO && mode != Mode.DATABASE_INFO) {
            try {
                final MetaDataManager metaDataMgr = nucleusCtx.getMetaDataManager();
                final ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
                if (filenames == null && persistenceUnitName == null) {
                    msg = SchemaTool.LOCALISER.msg(false, "014007");
                    SchemaTool.LOGGER.error(msg);
                    System.out.println(msg);
                    throw new NucleusUserException(msg);
                }
                FileMetaData[] filemds = null;
                if (persistenceUnitName != null) {
                    msg = SchemaTool.LOCALISER.msg(false, "014015", persistenceUnitName);
                    SchemaTool.LOGGER.info(msg);
                    if (tool.isVerbose()) {
                        System.out.println(msg);
                        System.out.println();
                    }
                    filemds = metaDataMgr.getFileMetaData();
                }
                else {
                    msg = SchemaTool.LOCALISER.msg(false, "014009");
                    SchemaTool.LOGGER.info(msg);
                    if (tool.isVerbose()) {
                        System.out.println(msg);
                    }
                    for (int i = 0; i < filenames.length; ++i) {
                        final String entry = SchemaTool.LOCALISER.msg(false, "014010", filenames[i]);
                        SchemaTool.LOGGER.info(entry);
                        if (tool.isVerbose()) {
                            System.out.println(entry);
                        }
                    }
                    if (tool.isVerbose()) {
                        System.out.println();
                    }
                    SchemaTool.LOGGER.debug(SchemaTool.LOCALISER.msg(false, "014011", "" + filenames.length));
                    filemds = MetaDataUtils.getFileMetaDataForInputFiles(metaDataMgr, clr, filenames);
                    SchemaTool.LOGGER.debug(SchemaTool.LOCALISER.msg(false, "014012", "" + filenames.length));
                }
                classNames = new TreeSet<String>();
                if (filemds == null) {
                    msg = SchemaTool.LOCALISER.msg(false, "014021");
                    SchemaTool.LOGGER.error(msg);
                    System.out.println(msg);
                    System.exit(2);
                    return;
                }
                for (int i = 0; i < filemds.length; ++i) {
                    for (int j = 0; j < filemds[i].getNoOfPackages(); ++j) {
                        for (int k = 0; k < filemds[i].getPackage(j).getNoOfClasses(); ++k) {
                            final String className = filemds[i].getPackage(j).getClass(k).getFullClassName();
                            if (!classNames.contains(className)) {
                                classNames.add(className);
                            }
                        }
                    }
                }
            }
            catch (Exception e3) {
                System.exit(2);
                return;
            }
        }
        final StoreManager storeMgr = nucleusCtx.getStoreManager();
        if (!(storeMgr instanceof SchemaAwareStoreManager)) {
            SchemaTool.LOGGER.error("StoreManager of type " + storeMgr.getClass().getName() + " is not schema-aware so cannot be used with SchemaTool");
            System.exit(2);
            return;
        }
        final SchemaAwareStoreManager schemaStoreMgr = (SchemaAwareStoreManager)storeMgr;
        try {
            if (mode == Mode.CREATE) {
                tool.createSchema(schemaStoreMgr, classNames);
            }
            else if (mode == Mode.DELETE) {
                tool.deleteSchema(schemaStoreMgr, classNames);
            }
            else if (mode == Mode.DELETE_CREATE) {
                tool.deleteSchema(schemaStoreMgr, classNames);
                tool.createSchema(schemaStoreMgr, classNames);
            }
            else if (mode == Mode.VALIDATE) {
                tool.validateSchema(schemaStoreMgr, classNames);
            }
            else if (mode == Mode.DATABASE_INFO) {
                storeMgr.printInformation("DATASTORE", System.out);
            }
            else if (mode == Mode.SCHEMA_INFO) {
                storeMgr.printInformation("SCHEMA", System.out);
            }
            msg = SchemaTool.LOCALISER.msg(false, "014043");
            SchemaTool.LOGGER.info(msg);
            System.out.println(msg);
        }
        catch (Exception e2) {
            msg = SchemaTool.LOCALISER.msg(false, "014037", e2.getMessage());
            System.out.println(msg);
            SchemaTool.LOGGER.error(msg, e2);
            System.exit(2);
        }
    }
    
    public SchemaTool() {
        this.apiName = "JDO";
        this.ddlFilename = null;
        this.completeDdl = false;
        this.includeAutoStart = false;
        this.verbose = false;
    }
    
    public Properties getPropertiesForSchemaTool() {
        final Properties props = new Properties();
        if (this.getDdlFile() != null) {
            props.setProperty("ddlFilename", this.getDdlFile());
        }
        if (this.getCompleteDdl()) {
            props.setProperty("completeDdl", "true");
        }
        if (this.getIncludeAutoStart()) {
            props.setProperty("autoStartTable", "true");
        }
        return props;
    }
    
    public void createSchema(final SchemaAwareStoreManager storeMgr, final Set<String> classNames) {
        storeMgr.createSchema(classNames, this.getPropertiesForSchemaTool());
    }
    
    public void deleteSchema(final SchemaAwareStoreManager storeMgr, final Set<String> classNames) {
        storeMgr.deleteSchema(classNames, this.getPropertiesForSchemaTool());
    }
    
    public void validateSchema(final SchemaAwareStoreManager storeMgr, final Set<String> classNames) {
        storeMgr.validateSchema(classNames, this.getPropertiesForSchemaTool());
    }
    
    public static NucleusContext getNucleusContextForMode(final Mode mode, final String api, final Map userProps, final String persistenceUnitName, final String ddlFile, final boolean verbose) {
        Map startupProps = null;
        if (userProps != null) {
            for (final String startupPropName : NucleusContext.STARTUP_PROPERTIES) {
                if (userProps.containsKey(startupPropName)) {
                    if (startupProps == null) {
                        startupProps = new HashMap();
                    }
                    startupProps.put(startupPropName, userProps.get(startupPropName));
                }
            }
        }
        final NucleusContext nucleusCtx = new NucleusContext(api, startupProps);
        final PersistenceConfiguration propConfig = nucleusCtx.getPersistenceConfiguration();
        final Map props = new HashMap();
        PersistenceUnitMetaData pumd = null;
        if (persistenceUnitName != null) {
            props.put("javax.jdo.option.persistenceunitname", persistenceUnitName);
            pumd = nucleusCtx.getMetaDataManager().getMetaDataForPersistenceUnit(persistenceUnitName);
            if (pumd == null) {
                throw new NucleusUserException("SchemaTool has been specified to use persistence-unit with name " + persistenceUnitName + " but none was found with that name");
            }
            if (pumd.getProperties() != null) {
                props.putAll(pumd.getProperties());
            }
            if (api.equalsIgnoreCase("JPA")) {
                pumd.clearJarFiles();
            }
        }
        if (userProps != null) {
            for (final Object key : userProps.keySet()) {
                final String propName = (String)key;
                props.put(propName.toLowerCase(Locale.ENGLISH), userProps.get(propName));
            }
        }
        final String[] propNames = { "datanucleus.ConnectionURL", "datanucleus.ConnectionDriverName", "datanucleus.ConnectionUserName", "datanucleus.ConnectionPassword", "datanucleus.Mapping", "javax.jdo.option.ConnectionURL", "javax.jdo.option.ConnectionDriverName", "javax.jdo.option.ConnectionUserName", "javax.jdo.option.ConnectionPassword", "javax.jdo.option.Mapping", "javax.persistence.jdbc.url", "javax.persistence.jdbc.driver", "javax.persistence.jdbc.user", "javax.persistence.jdbc.password" };
        for (int i = 0; i < propNames.length; ++i) {
            if (System.getProperty(propNames[i]) != null) {
                props.put(propNames[i].toLowerCase(Locale.ENGLISH), System.getProperty(propNames[i]));
            }
        }
        props.put("datanucleus.autostartmechanism", "None");
        if (mode == Mode.CREATE) {
            if (ddlFile != null) {
                props.put("datanucleus.validateconstraints", "false");
                props.put("datanucleus.validatecolumns", "false");
                props.put("datanucleus.validatetables", "false");
            }
            props.remove("datanucleus.autocreateschema");
            if (!props.containsKey("datanucleus.autocreatetables")) {
                props.put("datanucleus.autocreatetables", "true");
            }
            if (!props.containsKey("datanucleus.autocreatecolumns")) {
                props.put("datanucleus.autocreatecolumns", "true");
            }
            if (!props.containsKey("datanucleus.autocreateconstraints")) {
                props.put("datanucleus.autocreateconstraints", "true");
            }
            props.put("datanucleus.fixeddatastore", "false");
            props.put("datanucleus.readonlydatastore", "false");
            props.put("datanucleus.rdbms.checkexisttablesorviews", "true");
        }
        else if (mode == Mode.DELETE) {
            props.put("datanucleus.fixeddatastore", "false");
            props.put("datanucleus.readonlydatastore", "false");
        }
        else if (mode == Mode.DELETE_CREATE) {
            if (ddlFile != null) {
                props.put("datanucleus.validateconstraints", "false");
                props.put("datanucleus.validatecolumns", "false");
                props.put("datanucleus.validatetables", "false");
            }
            props.remove("datanucleus.autocreateschema");
            if (!props.containsKey("datanucleus.autocreatetables")) {
                props.put("datanucleus.autocreatetables", "true");
            }
            if (!props.containsKey("datanucleus.autocreatecolumns")) {
                props.put("datanucleus.autocreatecolumns", "true");
            }
            if (!props.containsKey("datanucleus.autocreateconstraints")) {
                props.put("datanucleus.autocreateconstraints", "true");
            }
            props.put("datanucleus.fixeddatastore", "false");
            props.put("datanucleus.readonlydatastore", "false");
            props.put("datanucleus.rdbms.checkexisttablesorviews", "true");
        }
        else if (mode == Mode.VALIDATE) {
            props.put("datanucleus.autocreateschema", "false");
            props.put("datanucleus.autocreatetables", "false");
            props.put("datanucleus.autocreateconstraints", "false");
            props.put("datanucleus.autocreatecolumns", "false");
            props.put("datanucleus.validatetables", "true");
            props.put("datanucleus.validatecolumns", "true");
            props.put("datanucleus.validateconstraints", "true");
        }
        propConfig.setPersistenceProperties(props);
        if (pumd != null) {
            nucleusCtx.getMetaDataManager().loadPersistenceUnit(pumd, null);
        }
        nucleusCtx.initialise();
        if (verbose) {
            String msg = SchemaTool.LOCALISER.msg(false, "014020");
            SchemaTool.LOGGER.info(msg);
            System.out.println(msg);
            final Map<String, Object> pmfProps = propConfig.getPersistenceProperties();
            final Set<String> keys = pmfProps.keySet();
            final List<String> keyNames = new ArrayList<String>(keys);
            Collections.sort(keyNames);
            for (final String key2 : keyNames) {
                final Object value = pmfProps.get(key2);
                boolean display = true;
                if (!key2.startsWith("datanucleus")) {
                    display = false;
                }
                else if (key2.equals("datanucleus.connectionpassword")) {
                    display = false;
                }
                else if (value == null) {
                    display = false;
                }
                else if (value instanceof String && StringUtils.isWhitespace((String)value)) {
                    display = false;
                }
                if (display) {
                    msg = SchemaTool.LOCALISER.msg(false, "014022", key2, value);
                    SchemaTool.LOGGER.info(msg);
                    System.out.println(msg);
                }
            }
            System.out.println();
        }
        return nucleusCtx;
    }
    
    public String getApi() {
        return this.apiName;
    }
    
    public SchemaTool setApi(final String api) {
        this.apiName = api;
        return this;
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
    
    public SchemaTool setVerbose(final boolean verbose) {
        this.verbose = verbose;
        return this;
    }
    
    public String getDdlFile() {
        return this.ddlFilename;
    }
    
    public SchemaTool setDdlFile(final String file) {
        this.ddlFilename = file;
        return this;
    }
    
    public SchemaTool setCompleteDdl(final boolean completeDdl) {
        this.completeDdl = completeDdl;
        return this;
    }
    
    public boolean getCompleteDdl() {
        return this.completeDdl;
    }
    
    public SchemaTool setIncludeAutoStart(final boolean include) {
        this.includeAutoStart = include;
        return this;
    }
    
    public boolean getIncludeAutoStart() {
        return this.includeAutoStart;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        LOGGER = NucleusLogger.getLoggerInstance("DataNucleus.SchemaTool");
    }
    
    public enum Mode
    {
        CREATE, 
        DELETE, 
        DELETE_CREATE, 
        VALIDATE, 
        DATABASE_INFO, 
        SCHEMA_INFO;
    }
}
