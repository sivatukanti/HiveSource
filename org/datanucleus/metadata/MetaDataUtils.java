// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.ClassConstants;
import java.util.Enumeration;
import java.util.Set;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.net.MalformedURLException;
import org.datanucleus.metadata.xml.MetaDataParser;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.util.NucleusLogger;
import java.net.URL;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.ExecutionContext;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.util.Localiser;

public class MetaDataUtils
{
    protected static final Localiser LOCALISER;
    private static MetaDataUtils instance;
    
    public static synchronized MetaDataUtils getInstance() {
        if (MetaDataUtils.instance == null) {
            MetaDataUtils.instance = new MetaDataUtils();
        }
        return MetaDataUtils.instance;
    }
    
    protected MetaDataUtils() {
    }
    
    public boolean arrayStorableAsByteArrayInSingleColumn(final AbstractMemberMetaData fmd) {
        if (fmd == null || !fmd.hasArray()) {
            return false;
        }
        final String arrayComponentType = fmd.getType().getComponentType().getName();
        return arrayComponentType.equals(ClassNameConstants.BOOLEAN) || arrayComponentType.equals(ClassNameConstants.BYTE) || arrayComponentType.equals(ClassNameConstants.CHAR) || arrayComponentType.equals(ClassNameConstants.DOUBLE) || arrayComponentType.equals(ClassNameConstants.FLOAT) || arrayComponentType.equals(ClassNameConstants.INT) || arrayComponentType.equals(ClassNameConstants.LONG) || arrayComponentType.equals(ClassNameConstants.SHORT) || arrayComponentType.equals(ClassNameConstants.JAVA_LANG_BOOLEAN) || arrayComponentType.equals(ClassNameConstants.JAVA_LANG_BYTE) || arrayComponentType.equals(ClassNameConstants.JAVA_LANG_CHARACTER) || arrayComponentType.equals(ClassNameConstants.JAVA_LANG_DOUBLE) || arrayComponentType.equals(ClassNameConstants.JAVA_LANG_FLOAT) || arrayComponentType.equals(ClassNameConstants.JAVA_LANG_INTEGER) || arrayComponentType.equals(ClassNameConstants.JAVA_LANG_LONG) || arrayComponentType.equals(ClassNameConstants.JAVA_LANG_SHORT) || arrayComponentType.equals(ClassNameConstants.JAVA_MATH_BIGDECIMAL) || arrayComponentType.equals(ClassNameConstants.JAVA_MATH_BIGINTEGER);
    }
    
    public boolean storesPersistable(final AbstractMemberMetaData fmd, final ExecutionContext ec) {
        if (fmd == null) {
            return false;
        }
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        final MetaDataManager mmgr = ec.getMetaDataManager();
        if (fmd.hasCollection()) {
            if (fmd.getCollection().elementIsPersistent()) {
                return true;
            }
            final String elementType = fmd.getCollection().getElementType();
            Class elementCls = clr.classForName(elementType);
            if (mmgr.getMetaDataForImplementationOfReference(elementCls, null, clr) != null) {
                return true;
            }
            if (elementCls != null && ClassUtils.isReferenceType(elementCls)) {
                try {
                    final String[] impls = this.getImplementationNamesForReferenceField(fmd, 3, clr, mmgr);
                    if (impls != null) {
                        elementCls = clr.classForName(impls[0]);
                        if (ec.getApiAdapter().isPersistable(elementCls)) {
                            return true;
                        }
                    }
                }
                catch (NucleusUserException ex) {}
            }
        }
        else if (fmd.hasMap()) {
            if (fmd.getMap().keyIsPersistent()) {
                return true;
            }
            final String keyType = fmd.getMap().getKeyType();
            Class keyCls = clr.classForName(keyType);
            if (mmgr.getMetaDataForImplementationOfReference(keyCls, null, clr) != null) {
                return true;
            }
            if (keyCls != null && ClassUtils.isReferenceType(keyCls)) {
                try {
                    final String[] impls = this.getImplementationNamesForReferenceField(fmd, 5, clr, mmgr);
                    if (impls != null) {
                        keyCls = clr.classForName(impls[0]);
                        if (ec.getApiAdapter().isPersistable(keyCls)) {
                            return true;
                        }
                    }
                }
                catch (NucleusUserException ex2) {}
            }
            if (fmd.getMap().valueIsPersistent()) {
                return true;
            }
            final String valueType = fmd.getMap().getValueType();
            Class valueCls = clr.classForName(valueType);
            if (mmgr.getMetaDataForImplementationOfReference(valueCls, null, clr) != null) {
                return true;
            }
            if (valueCls != null && ClassUtils.isReferenceType(valueCls)) {
                try {
                    final String[] impls = this.getImplementationNamesForReferenceField(fmd, 6, clr, mmgr);
                    if (impls != null) {
                        valueCls = clr.classForName(impls[0]);
                        if (ec.getApiAdapter().isPersistable(valueCls)) {
                            return true;
                        }
                    }
                }
                catch (NucleusUserException ex3) {}
            }
        }
        else if (fmd.hasArray()) {
            if (mmgr.getApiAdapter().isPersistable(fmd.getType().getComponentType())) {
                return true;
            }
            final String elementType = fmd.getArray().getElementType();
            Class elementCls = clr.classForName(elementType);
            if (mmgr.getApiAdapter().isPersistable(elementCls)) {
                return true;
            }
            if (mmgr.getMetaDataForImplementationOfReference(elementCls, null, clr) != null) {
                return true;
            }
            if (elementCls != null && ClassUtils.isReferenceType(elementCls)) {
                try {
                    final String[] impls = this.getImplementationNamesForReferenceField(fmd, 4, clr, mmgr);
                    if (impls != null) {
                        elementCls = clr.classForName(impls[0]);
                        if (ec.getApiAdapter().isPersistable(elementCls)) {
                            return true;
                        }
                    }
                }
                catch (NucleusUserException ex4) {}
            }
        }
        else {
            if (ClassUtils.isReferenceType(fmd.getType()) && mmgr.getMetaDataForImplementationOfReference(fmd.getType(), null, clr) != null) {
                return true;
            }
            if (mmgr.getMetaDataForClass(fmd.getType(), clr) != null) {
                return true;
            }
        }
        return false;
    }
    
    public boolean storesFCO(final AbstractMemberMetaData fmd, final ExecutionContext ec) {
        if (fmd == null) {
            return false;
        }
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        final MetaDataManager mgr = ec.getMetaDataManager();
        if (fmd.isSerialized() || fmd.isEmbedded()) {
            return false;
        }
        if (fmd.hasCollection() && !fmd.getCollection().isSerializedElement() && !fmd.getCollection().isEmbeddedElement()) {
            if (fmd.getCollection().elementIsPersistent()) {
                return true;
            }
            final String elementType = fmd.getCollection().getElementType();
            final Class elementCls = clr.classForName(elementType);
            if (elementCls != null && ClassUtils.isReferenceType(elementCls) && mgr.getMetaDataForImplementationOfReference(elementCls, null, clr) != null) {
                return true;
            }
        }
        else if (fmd.hasMap()) {
            if (fmd.getMap().keyIsPersistent() && !fmd.getMap().isEmbeddedKey() && !fmd.getMap().isSerializedKey()) {
                return true;
            }
            final String keyType = fmd.getMap().getKeyType();
            final Class keyCls = clr.classForName(keyType);
            if (keyCls != null && ClassUtils.isReferenceType(keyCls) && mgr.getMetaDataForImplementationOfReference(keyCls, null, clr) != null) {
                return true;
            }
            if (fmd.getMap().valueIsPersistent() && !fmd.getMap().isEmbeddedValue() && !fmd.getMap().isSerializedValue()) {
                return true;
            }
            final String valueType = fmd.getMap().getValueType();
            final Class valueCls = clr.classForName(valueType);
            if (valueCls != null && ClassUtils.isReferenceType(valueCls) && mgr.getMetaDataForImplementationOfReference(valueCls, null, clr) != null) {
                return true;
            }
        }
        else if (fmd.hasArray() && !fmd.getArray().isSerializedElement() && !fmd.getArray().isEmbeddedElement()) {
            if (mgr.getApiAdapter().isPersistable(fmd.getType().getComponentType())) {
                return true;
            }
        }
        else {
            if (ClassUtils.isReferenceType(fmd.getType()) && mgr.getMetaDataForImplementationOfReference(fmd.getType(), null, clr) != null) {
                return true;
            }
            if (mgr.getMetaDataForClass(fmd.getType(), clr) != null) {
                return true;
            }
        }
        return false;
    }
    
    public String[] getValuesForCommaSeparatedAttribute(final String attr) {
        if (attr == null || attr.length() == 0) {
            return null;
        }
        final String[] values = StringUtils.split(attr, ",");
        if (values != null) {
            for (int i = 0; i < values.length; ++i) {
                values[i] = values[i].trim();
            }
        }
        return values;
    }
    
    public String[] getImplementationNamesForReferenceField(final AbstractMemberMetaData fmd, final int fieldRole, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        String[] implTypes = null;
        if (4 == fieldRole) {
            final String implTypeStr = fmd.getArray().getElementType();
            if (implTypeStr != null) {
                implTypes = this.getValuesForCommaSeparatedAttribute(implTypeStr);
            }
        }
        else {
            implTypes = fmd.getFieldTypes();
        }
        if (implTypes != null && implTypes.length == 1) {
            final Class implCls = clr.classForName(implTypes[0].trim());
            if (implCls.isInterface()) {
                implTypes = mmgr.getClassesImplementingInterface(implTypes[0], clr);
            }
        }
        if (implTypes == null) {
            if (3 == fieldRole) {
                implTypes = fmd.getValuesForExtension("implementation-classes");
            }
            else if (4 == fieldRole) {
                implTypes = fmd.getValuesForExtension("implementation-classes");
            }
            else if (5 == fieldRole) {
                implTypes = fmd.getValuesForExtension("key-implementation-classes");
            }
            else if (6 == fieldRole) {
                implTypes = fmd.getValuesForExtension("value-implementation-classes");
            }
            else {
                implTypes = fmd.getValuesForExtension("implementation-classes");
            }
        }
        if (implTypes == null) {
            String type = null;
            if (fmd.hasCollection() && fieldRole == 3) {
                type = fmd.getCollection().getElementType();
            }
            else if (fmd.hasMap() && fieldRole == 5) {
                type = fmd.getMap().getKeyType();
            }
            else if (fmd.hasMap() && fieldRole == 6) {
                type = fmd.getMap().getValueType();
            }
            else if (fmd.hasArray() && fieldRole == 4) {
                type = fmd.getType().getComponentType().getName();
            }
            else {
                type = fmd.getTypeName();
            }
            if (!type.equals(ClassNameConstants.Object)) {
                implTypes = mmgr.getClassesImplementingInterface(type, clr);
            }
            if (implTypes == null) {
                throw new InvalidMemberMetaDataException(MetaDataUtils.LOCALISER, "044161", fmd.getClassName(), fmd.getName(), type);
            }
        }
        int noOfDups = 0;
        for (int i = 0; i < implTypes.length; ++i) {
            for (int j = 0; j < i; ++j) {
                if (implTypes[j].equals(implTypes[i])) {
                    ++noOfDups;
                    break;
                }
            }
        }
        final String[] impls = new String[implTypes.length - noOfDups];
        int n = 0;
        for (int k = 0; k < implTypes.length; ++k) {
            boolean dup = false;
            for (int l = 0; l < k; ++l) {
                if (implTypes[l].equals(implTypes[k])) {
                    dup = true;
                    break;
                }
            }
            if (!dup) {
                impls[n++] = implTypes[k];
            }
        }
        return impls;
    }
    
    public static boolean getBooleanForString(final String str, final boolean dflt) {
        if (StringUtils.isWhitespace(str)) {
            return dflt;
        }
        return Boolean.parseBoolean(str);
    }
    
    @Deprecated
    public static String getClassNameFromDiscriminatorValue(final String discrimValue, final DiscriminatorMetaData dismd, final ExecutionContext ec) {
        if (discrimValue == null) {
            return null;
        }
        if (dismd.getStrategy() == DiscriminatorStrategy.CLASS_NAME) {
            return discrimValue;
        }
        if (dismd.getStrategy() == DiscriminatorStrategy.VALUE_MAP) {
            final AbstractClassMetaData baseCmd = (AbstractClassMetaData)((InheritanceMetaData)dismd.getParent()).getParent();
            final AbstractClassMetaData rootCmd = baseCmd.getBaseAbstractClassMetaData();
            return ec.getMetaDataManager().getClassNameForDiscriminatorValueWithRoot(rootCmd, discrimValue);
        }
        return null;
    }
    
    public static String getValueForExtensionRecursively(final MetaData metadata, final String key) {
        if (metadata == null) {
            return null;
        }
        String value = metadata.getValueForExtension(key);
        if (value == null) {
            value = getValueForExtensionRecursively(metadata.getParent(), key);
        }
        return value;
    }
    
    public static String[] getValuesForExtensionRecursively(final MetaData metadata, final String key) {
        if (metadata == null) {
            return null;
        }
        String[] values = metadata.getValuesForExtension(key);
        if (values == null) {
            values = getValuesForExtensionRecursively(metadata.getParent(), key);
        }
        return values;
    }
    
    public static boolean isJdbcTypeNumeric(final String jdbcType) {
        return jdbcType != null && (jdbcType.equalsIgnoreCase("INTEGER") || jdbcType.equalsIgnoreCase("SMALLINT") || jdbcType.equalsIgnoreCase("TINYINT") || jdbcType.equalsIgnoreCase("NUMERIC") || jdbcType.equalsIgnoreCase("BIGINT"));
    }
    
    public static boolean isJdbcTypeString(final String jdbcType) {
        return jdbcType != null && (jdbcType.equalsIgnoreCase("VARCHAR") || jdbcType.equalsIgnoreCase("CHAR") || jdbcType.equalsIgnoreCase("LONGVARCHAR"));
    }
    
    public static List<AbstractClassMetaData> getMetaDataForCandidates(final Class cls, final boolean subclasses, final ExecutionContext ec) {
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        final List<AbstractClassMetaData> cmds = new ArrayList<AbstractClassMetaData>();
        if (cls.isInterface()) {
            final AbstractClassMetaData icmd = ec.getMetaDataManager().getMetaDataForInterface(cls, clr);
            if (icmd == null) {
                throw new NucleusUserException("Attempting to query an interface yet it is not declared 'persistent'. Define the interface in metadata as being persistent to perform this operation, and make sure any implementations use the same identity and identity member(s)");
            }
            final String[] impls = ec.getMetaDataManager().getClassesImplementingInterface(cls.getName(), clr);
            for (int i = 0; i < impls.length; ++i) {
                final AbstractClassMetaData implCmd = ec.getMetaDataManager().getMetaDataForClass(impls[i], clr);
                cmds.add(implCmd);
                if (subclasses) {
                    final String[] subclassNames = ec.getMetaDataManager().getSubclassesForClass(implCmd.getFullClassName(), true);
                    if (subclassNames != null && subclassNames.length > 0) {
                        for (int j = 0; j < subclassNames.length; ++j) {
                            final AbstractClassMetaData subcmd = ec.getMetaDataManager().getMetaDataForClass(subclassNames[j], clr);
                            cmds.add(subcmd);
                        }
                    }
                }
            }
        }
        else {
            final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(cls, clr);
            cmds.add(cmd);
            if (subclasses) {
                final String[] subclassNames2 = ec.getMetaDataManager().getSubclassesForClass(cls.getName(), true);
                if (subclassNames2 != null && subclassNames2.length > 0) {
                    for (int k = 0; k < subclassNames2.length; ++k) {
                        final AbstractClassMetaData subcmd2 = ec.getMetaDataManager().getMetaDataForClass(subclassNames2[k], clr);
                        cmds.add(subcmd2);
                    }
                }
            }
        }
        return cmds;
    }
    
    public static FileMetaData[] getFileMetaDataForInputFiles(final MetaDataManager metaDataMgr, final ClassLoaderResolver clr, final String[] inputFiles) {
        FileMetaData[] filemds = null;
        String msg = null;
        try {
            final HashSet metadataFiles = new HashSet();
            final HashSet classNames = new HashSet();
            for (int i = 0; i < inputFiles.length; ++i) {
                if (inputFiles[i].endsWith(".class")) {
                    URL classFileURL = null;
                    try {
                        classFileURL = new URL("file:" + inputFiles[i]);
                    }
                    catch (Exception e3) {
                        msg = MetaDataUtils.LOCALISER.msg(false, "014013", inputFiles[i]);
                        NucleusLogger.METADATA.error(msg);
                        throw new NucleusUserException(msg);
                    }
                    String className = null;
                    try {
                        className = ClassUtils.getClassNameForFileURL(classFileURL);
                        classNames.add(className);
                    }
                    catch (Throwable e) {
                        className = ClassUtils.getClassNameForFileName(inputFiles[i], clr);
                        if (className != null) {
                            classNames.add(className);
                        }
                        else {
                            NucleusLogger.METADATA.info("File \"" + inputFiles[i] + "\" could not be resolved to a class name, so ignoring." + " Specify it as a class explicitly using persistence.xml to overcome this", e);
                        }
                    }
                }
                else {
                    metadataFiles.add(inputFiles[i]);
                }
            }
            final FileMetaData[] filemds2 = metaDataMgr.loadMetadataFiles((String[])metadataFiles.toArray(new String[metadataFiles.size()]), null);
            final FileMetaData[] filemds3 = metaDataMgr.loadClasses((String[])classNames.toArray(new String[classNames.size()]), null);
            filemds = new FileMetaData[filemds2.length + filemds3.length];
            int pos = 0;
            for (int j = 0; j < filemds2.length; ++j) {
                filemds[pos++] = filemds2[j];
            }
            for (int j = 0; j < filemds3.length; ++j) {
                filemds[pos++] = filemds3[j];
            }
        }
        catch (Exception e2) {
            msg = MetaDataUtils.LOCALISER.msg(false, "014014", e2.getMessage());
            NucleusLogger.METADATA.error(msg, e2);
            throw new NucleusUserException(msg, e2);
        }
        return filemds;
    }
    
    public static PersistenceFileMetaData[] parsePersistenceFiles(final PluginManager pluginMgr, final String persistenceFilename, final boolean validate, final ClassLoaderResolver clr) {
        if (persistenceFilename != null) {
            try {
                final URL fileURL = new URL(persistenceFilename);
                final MetaDataParser parser = new MetaDataParser(null, pluginMgr, validate);
                final MetaData permd = parser.parseMetaDataURL(fileURL, "persistence");
                return new PersistenceFileMetaData[] { (PersistenceFileMetaData)permd };
            }
            catch (MalformedURLException mue) {
                NucleusLogger.METADATA.error("Error reading user-specified persistence.xml file " + persistenceFilename, mue);
                return null;
            }
        }
        final Set metadata = new LinkedHashSet();
        try {
            final Enumeration files = clr.getResources("META-INF/persistence.xml", Thread.currentThread().getContextClassLoader());
            if (!files.hasMoreElements()) {
                return null;
            }
            MetaDataParser parser2 = null;
            while (files.hasMoreElements()) {
                final URL fileURL2 = files.nextElement();
                if (parser2 == null) {
                    parser2 = new MetaDataParser(null, pluginMgr, validate);
                }
                final MetaData permd2 = parser2.parseMetaDataURL(fileURL2, "persistence");
                metadata.add(permd2);
            }
        }
        catch (IOException ioe) {
            NucleusLogger.METADATA.warn(StringUtils.getStringFromStackTrace(ioe));
        }
        return metadata.toArray(new PersistenceFileMetaData[metadata.size()]);
    }
    
    public static boolean persistColumnAsNumeric(final ColumnMetaData colmd) {
        boolean useLong = false;
        final String jdbc = (colmd != null) ? colmd.getJdbcType() : null;
        if (jdbc != null && (jdbc.equalsIgnoreCase("int") || jdbc.equalsIgnoreCase("integer"))) {
            useLong = true;
        }
        return useLong;
    }
    
    public static boolean persistColumnAsString(final ColumnMetaData colmd) {
        boolean useString = false;
        final String jdbc = (colmd != null) ? colmd.getJdbcType() : null;
        if (jdbc != null && (jdbc.equalsIgnoreCase("varchar") || jdbc.equalsIgnoreCase("char"))) {
            useString = true;
        }
        return useString;
    }
    
    public static Class getTypeOfDatastoreIdentity(final IdentityMetaData idmd) {
        if (idmd == null) {
            return Long.TYPE;
        }
        if (idmd.getValueStrategy() == IdentityStrategy.UUIDHEX || idmd.getValueStrategy() == IdentityStrategy.UUIDSTRING) {
            return String.class;
        }
        return Long.TYPE;
    }
    
    public static boolean isMemberEmbedded(final AbstractMemberMetaData mmd, final RelationType relationType, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        boolean embedded = false;
        if (mmd.isEmbedded()) {
            embedded = true;
        }
        else if (mmd.getEmbeddedMetaData() != null) {
            embedded = true;
        }
        else if (RelationType.isRelationMultiValued(relationType)) {
            if (mmd.hasCollection() && mmd.getElementMetaData() != null && mmd.getElementMetaData().getEmbeddedMetaData() != null) {
                embedded = true;
            }
            else if (mmd.hasArray() && mmd.getElementMetaData() != null && mmd.getElementMetaData().getEmbeddedMetaData() != null) {
                embedded = true;
            }
            else if (mmd.hasMap() && ((mmd.getKeyMetaData() != null && mmd.getKeyMetaData().getEmbeddedMetaData() != null) || (mmd.getValueMetaData() != null && mmd.getValueMetaData().getEmbeddedMetaData() != null))) {
                embedded = true;
            }
        }
        else if (RelationType.isRelationSingleValued(relationType)) {
            final AbstractClassMetaData mmdCmd = mmgr.getMetaDataForClass(mmd.getType(), clr);
            if (mmdCmd != null && mmdCmd.isEmbeddedOnly()) {
                embedded = true;
            }
        }
        else if (RelationType.isRelationMultiValued(relationType)) {}
        return embedded;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
