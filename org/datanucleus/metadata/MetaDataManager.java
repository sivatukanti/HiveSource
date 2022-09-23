// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.HashMap;
import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NoPersistenceInformationException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.datanucleus.exceptions.NucleusException;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Properties;
import java.io.File;
import java.net.URI;
import java.util.Enumeration;
import java.io.IOException;
import java.net.URL;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.api.ApiAdapter;
import java.util.Iterator;
import org.datanucleus.metadata.annotations.AnnotationManagerImpl;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import org.datanucleus.util.MultiMap;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import org.datanucleus.metadata.xml.MetaDataParser;
import org.datanucleus.metadata.annotations.AnnotationManager;
import org.datanucleus.NucleusContext;
import org.datanucleus.util.Localiser;
import java.io.Serializable;

public abstract class MetaDataManager implements Serializable
{
    protected static final Localiser LOCALISER;
    protected final NucleusContext nucleusContext;
    protected final AnnotationManager annotationManager;
    protected MetaDataParser metaDataParser;
    protected boolean validateXML;
    protected boolean supportXMLNamespaces;
    protected boolean allowMetaDataLoad;
    protected boolean allowXML;
    protected boolean allowAnnotations;
    protected boolean allowORM;
    protected Lock updateLock;
    protected Collection<String> classesWithoutPersistenceInfo;
    protected Map<String, AbstractClassMetaData> classMetaDataByClass;
    protected Map<String, FileMetaData> fileMetaDataByURLString;
    protected Map<String, AbstractClassMetaData> classMetaDataByEntityName;
    protected Map<String, AbstractClassMetaData> classMetaDataByDiscriminatorName;
    protected Map<String, Set<String>> directSubclassesByClass;
    protected Map<String, QueryMetaData> queryMetaDataByName;
    protected Map<String, StoredProcQueryMetaData> storedProcQueryMetaDataByName;
    protected Map<String, FetchPlanMetaData> fetchPlanMetaDataByName;
    protected Map<String, SequenceMetaData> sequenceMetaDataByPackageSequence;
    protected Map<String, TableGeneratorMetaData> tableGeneratorMetaDataByPackageSequence;
    protected Map<String, QueryResultMetaData> queryResultMetaDataByName;
    protected MultiMap classMetaDataByAppIdClassName;
    protected Set<MetaDataListener> listeners;
    protected int userMetaDataNumber;
    protected Map<String, DiscriminatorLookup> discriminatorLookupByRootClassName;
    protected ArrayList<FileMetaData> utilisedFileMetaData;
    protected List<AbstractClassMetaData> loadedMetaData;
    
    public MetaDataManager(final NucleusContext ctx) {
        this.metaDataParser = null;
        this.validateXML = true;
        this.supportXMLNamespaces = true;
        this.allowMetaDataLoad = true;
        this.allowXML = true;
        this.allowAnnotations = true;
        this.allowORM = true;
        this.updateLock = null;
        this.classesWithoutPersistenceInfo = new HashSet<String>();
        this.classMetaDataByClass = new ConcurrentHashMap<String, AbstractClassMetaData>();
        this.fileMetaDataByURLString = new ConcurrentHashMap<String, FileMetaData>();
        this.classMetaDataByEntityName = new ConcurrentHashMap<String, AbstractClassMetaData>();
        this.classMetaDataByDiscriminatorName = new ConcurrentHashMap<String, AbstractClassMetaData>();
        this.directSubclassesByClass = new ConcurrentHashMap<String, Set<String>>();
        this.queryMetaDataByName = null;
        this.storedProcQueryMetaDataByName = null;
        this.fetchPlanMetaDataByName = null;
        this.sequenceMetaDataByPackageSequence = null;
        this.tableGeneratorMetaDataByPackageSequence = null;
        this.queryResultMetaDataByName = null;
        this.classMetaDataByAppIdClassName = new MultiMap();
        this.listeners = null;
        this.userMetaDataNumber = 0;
        this.discriminatorLookupByRootClassName = new ConcurrentHashMap<String, DiscriminatorLookup>();
        this.utilisedFileMetaData = new ArrayList<FileMetaData>();
        this.loadedMetaData = null;
        this.nucleusContext = ctx;
        this.updateLock = new ReentrantLock();
        this.validateXML = this.nucleusContext.getPersistenceConfiguration().getBooleanProperty("datanucleus.metadata.xml.validate");
        this.supportXMLNamespaces = this.nucleusContext.getPersistenceConfiguration().getBooleanProperty("datanucleus.metadata.xml.namespaceAware");
        this.allowXML = this.nucleusContext.getPersistenceConfiguration().getBooleanProperty("datanucleus.metadata.allowXML");
        this.allowAnnotations = this.nucleusContext.getPersistenceConfiguration().getBooleanProperty("datanucleus.metadata.allowAnnotations");
        this.annotationManager = new AnnotationManagerImpl(this);
        final Set supportedClasses = this.nucleusContext.getTypeManager().getSupportedSecondClassTypes();
        final Iterator<String> iter = supportedClasses.iterator();
        while (iter.hasNext()) {
            this.classesWithoutPersistenceInfo.add(iter.next());
        }
        if (this.nucleusContext.isStoreManagerInitialised()) {
            this.allowORM = this.nucleusContext.getStoreManager().getSupportedOptions().contains("ORM");
            if (this.allowORM) {
                final Boolean configOrm = this.nucleusContext.getPersistenceConfiguration().getBooleanObjectProperty("datanucleus.metadata.supportORM");
                if (configOrm != null && !configOrm) {
                    this.allowORM = false;
                }
            }
        }
    }
    
    public void close() {
        this.classMetaDataByClass.clear();
        this.classMetaDataByClass = null;
        this.fileMetaDataByURLString.clear();
        this.fileMetaDataByURLString = null;
        this.classesWithoutPersistenceInfo.clear();
        this.classesWithoutPersistenceInfo = null;
        this.directSubclassesByClass.clear();
        this.directSubclassesByClass = null;
        if (this.classMetaDataByEntityName != null) {
            this.classMetaDataByEntityName.clear();
            this.classMetaDataByEntityName = null;
        }
        if (this.classMetaDataByDiscriminatorName != null) {
            this.classMetaDataByDiscriminatorName.clear();
            this.classMetaDataByDiscriminatorName = null;
        }
        if (this.queryMetaDataByName != null) {
            this.queryMetaDataByName.clear();
            this.queryMetaDataByName = null;
        }
        if (this.storedProcQueryMetaDataByName != null) {
            this.storedProcQueryMetaDataByName.clear();
            this.storedProcQueryMetaDataByName = null;
        }
        if (this.fetchPlanMetaDataByName != null) {
            this.fetchPlanMetaDataByName.clear();
            this.fetchPlanMetaDataByName = null;
        }
        if (this.sequenceMetaDataByPackageSequence != null) {
            this.sequenceMetaDataByPackageSequence.clear();
            this.sequenceMetaDataByPackageSequence = null;
        }
        if (this.tableGeneratorMetaDataByPackageSequence != null) {
            this.tableGeneratorMetaDataByPackageSequence.clear();
            this.tableGeneratorMetaDataByPackageSequence = null;
        }
        if (this.queryResultMetaDataByName != null) {
            this.queryResultMetaDataByName.clear();
            this.queryResultMetaDataByName = null;
        }
        if (this.classMetaDataByAppIdClassName != null) {
            this.classMetaDataByAppIdClassName.clear();
            this.classMetaDataByAppIdClassName = null;
        }
        if (this.listeners != null) {
            this.listeners.clear();
            this.listeners = null;
        }
    }
    
    public void registerListener(final MetaDataListener listener) {
        if (this.listeners == null) {
            this.listeners = new HashSet<MetaDataListener>();
        }
        this.listeners.add(listener);
    }
    
    public void deregisterListener(final MetaDataListener listener) {
        if (this.listeners == null) {
            return;
        }
        this.listeners.remove(listener);
        if (this.listeners.size() == 0) {
            this.listeners = null;
        }
    }
    
    public void setAllowMetaDataLoad(final boolean allow) {
        this.allowMetaDataLoad = allow;
    }
    
    public boolean getAllowMetaDataLoad() {
        return this.allowMetaDataLoad;
    }
    
    public boolean isAllowXML() {
        return this.allowXML;
    }
    
    public void setAllowXML(final boolean allow) {
        this.allowXML = allow;
    }
    
    public boolean isAllowAnnotations() {
        return this.allowAnnotations;
    }
    
    public void setAllowAnnotations(final boolean allow) {
        this.allowAnnotations = allow;
    }
    
    public boolean supportsORM() {
        return this.allowORM;
    }
    
    public boolean isEnhancing() {
        return this.getNucleusContext().getType() == NucleusContext.ContextType.ENHANCEMENT;
    }
    
    public void setValidate(final boolean validate) {
        this.validateXML = validate;
    }
    
    public void setXmlNamespaceAware(final boolean aware) {
        this.supportXMLNamespaces = aware;
    }
    
    public NucleusContext getNucleusContext() {
        return this.nucleusContext;
    }
    
    public ApiAdapter getApiAdapter() {
        return this.nucleusContext.getApiAdapter();
    }
    
    public AnnotationManager getAnnotationManager() {
        return this.annotationManager;
    }
    
    public FileMetaData[] loadMetadataFiles(final String[] metadataFiles, final ClassLoader loader) {
        if (!this.allowMetaDataLoad) {
            return null;
        }
        boolean originatingLoadCall = false;
        if (this.loadedMetaData == null) {
            originatingLoadCall = true;
            this.loadedMetaData = new ArrayList<AbstractClassMetaData>();
        }
        try {
            if (originatingLoadCall) {
                this.updateLock.lock();
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044005", StringUtils.objectArrayToString(metadataFiles)));
            }
            final ClassLoaderResolver clr = this.nucleusContext.getClassLoaderResolver(loader);
            final Collection fileMetaData = this.loadFiles(metadataFiles, clr);
            if (fileMetaData.size() > 0) {
                this.initialiseFileMetaDataForUse(fileMetaData, clr);
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044010"));
            }
            if (originatingLoadCall) {
                this.processListenerLoadingCall();
            }
            return fileMetaData.toArray(new FileMetaData[fileMetaData.size()]);
        }
        finally {
            if (originatingLoadCall) {
                this.updateLock.unlock();
            }
        }
    }
    
    public FileMetaData[] loadClasses(final String[] classNames, final ClassLoader loader) {
        if (!this.allowMetaDataLoad) {
            return null;
        }
        boolean originatingLoadCall = false;
        if (this.loadedMetaData == null) {
            originatingLoadCall = true;
            this.loadedMetaData = new ArrayList<AbstractClassMetaData>();
        }
        try {
            if (originatingLoadCall) {
                this.updateLock.lock();
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044006", StringUtils.objectArrayToString(classNames)));
            }
            final ClassLoaderResolver clr = this.nucleusContext.getClassLoaderResolver(loader);
            final Collection fileMetaData = new ArrayList();
            final HashSet exceptions = new HashSet();
            for (int i = 0; i < classNames.length; ++i) {
                try {
                    final Class cls = clr.classForName(classNames[i]);
                    AbstractClassMetaData cmd = this.classMetaDataByClass.get(classNames[i]);
                    if (cmd == null) {
                        final FileMetaData filemd = this.loadAnnotationsForClass(cls, clr, true, false);
                        if (filemd != null) {
                            this.registerFile("annotations:" + classNames[i], filemd, clr);
                            fileMetaData.add(filemd);
                        }
                        else {
                            cmd = this.getMetaDataForClass(cls, clr);
                            if (cmd == null) {
                                NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044017", classNames[i]));
                            }
                            else {
                                fileMetaData.add(cmd.getPackageMetaData().getFileMetaData());
                            }
                        }
                    }
                    else {
                        fileMetaData.add(cmd.getPackageMetaData().getFileMetaData());
                    }
                }
                catch (ClassNotResolvedException e) {
                    NucleusLogger.METADATA.error(StringUtils.getStringFromStackTrace(e));
                }
                catch (Exception e2) {
                    exceptions.add(e2);
                }
            }
            if (exceptions.size() > 0) {
                throw new NucleusUserException(MetaDataManager.LOCALISER.msg("044016"), (Throwable[])exceptions.toArray(new Throwable[exceptions.size()]), null);
            }
            if (fileMetaData.size() > 0) {
                this.initialiseFileMetaDataForUse(fileMetaData, clr);
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044010"));
            }
            if (originatingLoadCall) {
                this.processListenerLoadingCall();
            }
            return fileMetaData.toArray(new FileMetaData[fileMetaData.size()]);
        }
        finally {
            if (originatingLoadCall) {
                this.updateLock.unlock();
            }
        }
    }
    
    public FileMetaData[] loadJar(final String jarFileName, final ClassLoader loader) {
        if (!this.allowMetaDataLoad) {
            return null;
        }
        boolean originatingLoadCall = false;
        if (this.loadedMetaData == null) {
            originatingLoadCall = true;
            this.loadedMetaData = new ArrayList<AbstractClassMetaData>();
        }
        try {
            if (originatingLoadCall) {
                this.updateLock.lock();
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044009", jarFileName));
            }
            final ClassLoaderResolver clr = this.nucleusContext.getClassLoaderResolver(loader);
            final ArrayList fileMetaData = new ArrayList();
            final Set mappingFiles = new HashSet();
            if (this.allowXML) {
                final String[] packageJdoFiles = ClassUtils.getPackageJdoFilesForJarFile(jarFileName);
                if (packageJdoFiles != null) {
                    for (int i = 0; i < packageJdoFiles.length; ++i) {
                        mappingFiles.add(packageJdoFiles[i]);
                    }
                }
            }
            final Set classNames = new HashSet();
            if (this.allowAnnotations) {
                final String[] jarClassNames = ClassUtils.getClassNamesForJarFile(jarFileName);
                if (jarClassNames != null) {
                    for (int j = 0; j < jarClassNames.length; ++j) {
                        classNames.add(jarClassNames[j]);
                    }
                }
            }
            final Set<Throwable> exceptions = new HashSet<Throwable>();
            if (this.allowXML && !mappingFiles.isEmpty()) {
                for (final String mappingFileName : mappingFiles) {
                    try {
                        final Enumeration files = clr.getResources(mappingFileName, Thread.currentThread().getContextClassLoader());
                        while (files.hasMoreElements()) {
                            final URL url = files.nextElement();
                            if (url != null && this.fileMetaDataByURLString.get(url.toString()) == null) {
                                final FileMetaData filemd = this.parseFile(url);
                                if (filemd == null) {
                                    continue;
                                }
                                this.registerFile(url.toString(), filemd, clr);
                                fileMetaData.add(filemd);
                            }
                        }
                    }
                    catch (InvalidMetaDataException imde) {
                        NucleusLogger.METADATA.error(StringUtils.getStringFromStackTrace(imde));
                        exceptions.add(imde);
                    }
                    catch (IOException ioe) {
                        NucleusLogger.METADATA.error(MetaDataManager.LOCALISER.msg("044027", jarFileName, mappingFileName, ioe.getMessage()), ioe);
                    }
                }
            }
            if (this.allowAnnotations && !classNames.isEmpty()) {
                for (final String className : classNames) {
                    final AbstractClassMetaData cmd = this.classMetaDataByClass.get(className);
                    if (cmd == null) {
                        try {
                            final Class cls = clr.classForName(className);
                            final FileMetaData filemd = this.loadAnnotationsForClass(cls, clr, true, false);
                            if (filemd == null) {
                                continue;
                            }
                            fileMetaData.add(filemd);
                        }
                        catch (Exception e) {
                            exceptions.add(e);
                        }
                    }
                }
            }
            if (exceptions.size() > 0) {
                throw new NucleusUserException(MetaDataManager.LOCALISER.msg("044024", jarFileName), exceptions.toArray(new Throwable[exceptions.size()]));
            }
            if (fileMetaData.size() > 0) {
                this.initialiseFileMetaDataForUse(fileMetaData, clr);
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044010"));
            }
            if (originatingLoadCall) {
                this.processListenerLoadingCall();
            }
            return fileMetaData.toArray(new FileMetaData[fileMetaData.size()]);
        }
        finally {
            if (originatingLoadCall) {
                this.updateLock.unlock();
            }
        }
    }
    
    public FileMetaData[] loadPersistenceUnit(final PersistenceUnitMetaData pumd, final ClassLoader loader) {
        if (!this.allowMetaDataLoad) {
            return null;
        }
        boolean originatingLoadCall = false;
        if (this.loadedMetaData == null) {
            originatingLoadCall = true;
            this.loadedMetaData = new ArrayList<AbstractClassMetaData>();
        }
        try {
            if (originatingLoadCall) {
                this.updateLock.lock();
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044007", pumd.getName()));
            }
            final Properties puProps = pumd.getProperties();
            if (puProps != null) {
                if (puProps.containsKey("datanucleus.metadata.xml.validate")) {
                    final Boolean val = Boolean.valueOf((String)puProps.get("datanucleus.metadata.xml.validate"));
                    if (val != null) {
                        this.validateXML = val;
                    }
                }
                if (puProps.containsKey("datanucleus.metadata.xml.namespaceAware")) {
                    final Boolean val = Boolean.valueOf((String)puProps.get("datanucleus.metadata.xml.namespaceAware"));
                    if (val != null) {
                        this.supportXMLNamespaces = val;
                    }
                }
            }
            final ClassLoaderResolver clr = this.nucleusContext.getClassLoaderResolver(loader);
            final HashSet exceptions = new HashSet();
            final ArrayList fileMetaData = new ArrayList();
            final Set mappingFiles = new HashSet();
            if (this.allowXML) {
                if (this.nucleusContext.getApiName().equalsIgnoreCase("JPA")) {
                    mappingFiles.add("META-INF/orm.xml");
                }
                if (pumd.getMappingFiles() != null) {
                    mappingFiles.addAll(pumd.getMappingFiles());
                }
                if (this.nucleusContext.getApiName().equalsIgnoreCase("JDO")) {
                    final Set jarFileNames = pumd.getJarFiles();
                    if (jarFileNames != null) {
                        for (final Object jarFile : jarFileNames) {
                            if (jarFile instanceof String) {
                                final String[] packageJdoFiles = ClassUtils.getPackageJdoFilesForJarFile((String)jarFile);
                                if (packageJdoFiles == null) {
                                    continue;
                                }
                                for (int i = 0; i < packageJdoFiles.length; ++i) {
                                    mappingFiles.add(packageJdoFiles[i]);
                                }
                            }
                            else if (jarFile instanceof URL) {
                                final String[] packageJdoFiles = ClassUtils.getPackageJdoFilesForJarFile((URL)jarFile);
                                if (packageJdoFiles == null) {
                                    continue;
                                }
                                for (int i = 0; i < packageJdoFiles.length; ++i) {
                                    mappingFiles.add(packageJdoFiles[i]);
                                }
                            }
                            else {
                                if (!(jarFile instanceof URI)) {
                                    continue;
                                }
                                final String[] packageJdoFiles = ClassUtils.getPackageJdoFilesForJarFile((URI)jarFile);
                                if (packageJdoFiles == null) {
                                    continue;
                                }
                                for (int i = 0; i < packageJdoFiles.length; ++i) {
                                    mappingFiles.add(packageJdoFiles[i]);
                                }
                            }
                        }
                    }
                }
            }
            final Set classNames = new HashSet();
            if (this.allowAnnotations) {
                if (pumd.getClassNames() != null) {
                    classNames.addAll(pumd.getClassNames());
                }
                if (this.getNucleusContext().getType() == NucleusContext.ContextType.PERSISTENCE) {
                    final Set jarFileNames2 = pumd.getJarFiles();
                    if (jarFileNames2 != null) {
                        for (final Object jarFile2 : jarFileNames2) {
                            if (jarFile2 instanceof String) {
                                final String[] jarClassNames = ClassUtils.getClassNamesForJarFile((String)jarFile2);
                                if (jarClassNames == null) {
                                    continue;
                                }
                                for (int j = 0; j < jarClassNames.length; ++j) {
                                    classNames.add(jarClassNames[j]);
                                }
                            }
                            else if (jarFile2 instanceof URL) {
                                final String[] jarClassNames = ClassUtils.getClassNamesForJarFile((URL)jarFile2);
                                if (jarClassNames == null) {
                                    continue;
                                }
                                for (int j = 0; j < jarClassNames.length; ++j) {
                                    classNames.add(jarClassNames[j]);
                                }
                            }
                            else {
                                if (!(jarFile2 instanceof URI)) {
                                    continue;
                                }
                                final String[] jarClassNames = ClassUtils.getClassNamesForJarFile((URI)jarFile2);
                                if (jarClassNames == null) {
                                    continue;
                                }
                                for (int j = 0; j < jarClassNames.length; ++j) {
                                    classNames.add(jarClassNames[j]);
                                }
                            }
                        }
                    }
                }
                if (!pumd.getExcludeUnlistedClasses()) {
                    final MetaDataScanner scanner = this.getScanner(clr);
                    if (scanner != null) {
                        final Set<String> scannedClassNames = scanner.scanForPersistableClasses(pumd);
                        if (scannedClassNames != null) {
                            classNames.addAll(scannedClassNames);
                        }
                    }
                    else {
                        try {
                            if (pumd.getRootURI() != null && pumd.getRootURI().getScheme().equals("file")) {
                                final File rootDir = new File(pumd.getRootURI());
                                final String[] scannedClassNames2 = ClassUtils.getClassNamesForDirectoryAndBelow(rootDir);
                                if (scannedClassNames2 != null) {
                                    for (int i = 0; i < scannedClassNames2.length; ++i) {
                                        NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044026", scannedClassNames2[i], pumd.getName()));
                                        classNames.add(scannedClassNames2[i]);
                                    }
                                }
                            }
                        }
                        catch (IllegalArgumentException iae) {
                            NucleusLogger.METADATA.debug("Ignoring scan of classes for this persistence-unit since the URI root is " + pumd.getRootURI() + " and is not hierarchical");
                        }
                    }
                }
            }
            if (this.allowXML && !mappingFiles.isEmpty()) {
                for (final String mappingFileName : mappingFiles) {
                    try {
                        final Enumeration files = clr.getResources(mappingFileName, Thread.currentThread().getContextClassLoader());
                        if (!files.hasMoreElements()) {
                            NucleusLogger.METADATA.debug("Not found any metadata mapping files for resource name " + mappingFileName + " in CLASSPATH");
                        }
                        else {
                            while (files.hasMoreElements()) {
                                final URL url = files.nextElement();
                                if (url != null && this.fileMetaDataByURLString.get(url.toString()) == null) {
                                    final FileMetaData filemd = this.parseFile(url);
                                    if (filemd == null) {
                                        continue;
                                    }
                                    this.registerFile(url.toString(), filemd, clr);
                                    fileMetaData.add(filemd);
                                }
                            }
                        }
                    }
                    catch (InvalidMetaDataException imde) {
                        NucleusLogger.METADATA.error(StringUtils.getStringFromStackTrace(imde));
                        exceptions.add(imde);
                    }
                    catch (IOException ioe) {
                        NucleusLogger.METADATA.error(MetaDataManager.LOCALISER.msg("044027", pumd.getName(), mappingFileName, ioe.getMessage()), ioe);
                    }
                }
            }
            if (this.allowAnnotations && !classNames.isEmpty()) {
                for (final String className : classNames) {
                    final AbstractClassMetaData cmd = this.classMetaDataByClass.get(className);
                    if (cmd == null) {
                        try {
                            final Class cls = clr.classForName(className);
                            final FileMetaData filemd = this.loadAnnotationsForClass(cls, clr, true, false);
                            if (filemd != null) {
                                fileMetaData.add(filemd);
                            }
                            else {
                                NucleusLogger.METADATA.debug("Class " + className + " was specified in persistence-unit (maybe by not putting exclude-unlisted-classes) " + pumd.getName() + " but not annotated, so ignoring");
                            }
                        }
                        catch (Exception e) {
                            exceptions.add(e);
                        }
                    }
                }
            }
            if (exceptions.size() > 0) {
                throw new NucleusUserException(MetaDataManager.LOCALISER.msg("044023", pumd.getName()), (Throwable[])exceptions.toArray(new Throwable[exceptions.size()]));
            }
            if (fileMetaData.size() > 0) {
                this.initialiseFileMetaDataForUse(fileMetaData, clr);
            }
            for (final AbstractClassMetaData cmd2 : this.classMetaDataByClass.values()) {
                if (!cmd2.isPopulated()) {
                    this.populateAbstractClassMetaData(cmd2, clr, loader);
                }
                if (!cmd2.isInitialised()) {
                    this.initialiseAbstractClassMetaData(cmd2, clr);
                }
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044010"));
            }
            if (originatingLoadCall) {
                this.processListenerLoadingCall();
            }
            return fileMetaData.toArray(new FileMetaData[fileMetaData.size()]);
        }
        finally {
            if (originatingLoadCall) {
                this.updateLock.unlock();
            }
        }
    }
    
    protected MetaDataScanner getScanner(final ClassLoaderResolver clr) {
        final Object so = this.nucleusContext.getPersistenceConfiguration().getProperty("datanucleus.metadata.scanner");
        if (so == null) {
            return null;
        }
        if (so instanceof MetaDataScanner) {
            return (MetaDataScanner)so;
        }
        if (so instanceof String) {
            try {
                final Class clazz = clr.classForName((String)so);
                return clazz.newInstance();
            }
            catch (Throwable t) {
                throw new NucleusUserException(MetaDataManager.LOCALISER.msg("044012", so), t);
            }
        }
        if (NucleusLogger.METADATA.isDebugEnabled()) {
            NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044011", so));
        }
        return null;
    }
    
    public void loadUserMetaData(final FileMetaData fileMetaData, final ClassLoader loader) {
        if (fileMetaData == null) {
            return;
        }
        if (!this.allowMetaDataLoad) {
            return;
        }
        boolean originatingLoadCall = false;
        if (this.loadedMetaData == null) {
            originatingLoadCall = true;
            this.loadedMetaData = new ArrayList<AbstractClassMetaData>();
        }
        try {
            if (originatingLoadCall) {
                this.updateLock.lock();
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044008"));
            }
            final ClassLoaderResolver clr = this.nucleusContext.getClassLoaderResolver(loader);
            fileMetaData.setFilename("User_Metadata_" + this.userMetaDataNumber);
            ++this.userMetaDataNumber;
            this.registerFile(fileMetaData.getFilename(), fileMetaData, clr);
            final Collection filemds = new ArrayList();
            filemds.add(fileMetaData);
            this.initialiseFileMetaDataForUse(filemds, clr);
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044010"));
            }
            if (originatingLoadCall) {
                this.processListenerLoadingCall();
            }
        }
        finally {
            if (originatingLoadCall) {
                this.updateLock.unlock();
            }
        }
    }
    
    protected void initialiseFileMetaDataForUse(final Collection fileMetaData, final ClassLoaderResolver clr) {
        final HashSet exceptions = new HashSet();
        if (NucleusLogger.METADATA.isDebugEnabled()) {
            NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044018"));
        }
        for (final FileMetaData filemd : fileMetaData) {
            if (!filemd.isInitialised()) {
                this.populateFileMetaData(filemd, clr, null);
            }
        }
        if (NucleusLogger.METADATA.isDebugEnabled()) {
            NucleusLogger.METADATA.debug(MetaDataManager.LOCALISER.msg("044019"));
        }
        for (final FileMetaData filemd : fileMetaData) {
            if (!filemd.isInitialised()) {
                try {
                    this.initialiseFileMetaData(filemd, clr, null);
                }
                catch (Exception e) {
                    NucleusLogger.METADATA.error(StringUtils.getStringFromStackTrace(e));
                    exceptions.add(e);
                }
            }
        }
        if (exceptions.size() > 0) {
            throw new NucleusUserException(MetaDataManager.LOCALISER.msg("044020"), (Throwable[])exceptions.toArray(new Throwable[exceptions.size()]));
        }
    }
    
    public Collection<FileMetaData> loadFiles(final String[] metadataFiles, final ClassLoaderResolver clr) {
        final List<FileMetaData> fileMetaData = new ArrayList<FileMetaData>();
        final Set<Throwable> exceptions = new HashSet<Throwable>();
        if (this.allowXML) {
            for (int i = 0; i < metadataFiles.length; ++i) {
                try {
                    URL fileURL = null;
                    try {
                        final File file = new File(metadataFiles[i]);
                        fileURL = file.toURI().toURL();
                        if (!file.exists()) {
                            fileURL = clr.getResource(metadataFiles[i], null);
                        }
                    }
                    catch (Exception mue) {
                        fileURL = clr.getResource(metadataFiles[i], null);
                    }
                    if (fileURL == null) {
                        NucleusLogger.METADATA.warn("Metadata file " + metadataFiles[i] + " not found in CLASSPATH");
                    }
                    else {
                        FileMetaData filemd = this.fileMetaDataByURLString.get(fileURL.toString());
                        if (filemd == null) {
                            filemd = this.parseFile(fileURL);
                            if (filemd == null) {
                                throw new NucleusUserException(MetaDataManager.LOCALISER.msg("044015", metadataFiles[i]));
                            }
                            this.registerFile(fileURL.toString(), filemd, clr);
                            fileMetaData.add(filemd);
                        }
                        else {
                            fileMetaData.add(filemd);
                        }
                    }
                }
                catch (Exception e) {
                    exceptions.add(e);
                    e.printStackTrace();
                }
            }
        }
        if (exceptions.size() > 0) {
            throw new NucleusUserException(MetaDataManager.LOCALISER.msg("044016"), exceptions.toArray(new Throwable[exceptions.size()]), null);
        }
        return fileMetaData;
    }
    
    public boolean isClassPersistable(final String className) {
        final AbstractClassMetaData acmd = this.readMetaDataForClass(className);
        return acmd != null && acmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE;
    }
    
    public FileMetaData[] getFileMetaData() {
        final Collection filemds = this.fileMetaDataByURLString.values();
        return filemds.toArray(new FileMetaData[filemds.size()]);
    }
    
    public Collection<String> getClassesWithMetaData() {
        return Collections.unmodifiableCollection((Collection<? extends String>)this.classMetaDataByClass.keySet());
    }
    
    public boolean hasMetaDataForClass(final String className) {
        return className != null && !this.isClassWithoutPersistenceInfo(className) && this.classMetaDataByClass.get(className) != null;
    }
    
    protected boolean isClassWithoutPersistenceInfo(final String className) {
        return className == null || (className.startsWith("java.") || className.startsWith("javax.")) || this.classesWithoutPersistenceInfo.contains(className);
    }
    
    public Collection<AbstractClassMetaData> getClassMetaDataWithApplicationId(final String objectIdClassName) {
        return this.classMetaDataByAppIdClassName.get(objectIdClassName);
    }
    
    public synchronized AbstractClassMetaData getMetaDataForClass(final String className, final ClassLoaderResolver clr) {
        if (className == null) {
            return null;
        }
        if (this.isClassWithoutPersistenceInfo(className)) {
            return null;
        }
        final AbstractClassMetaData cmd = this.classMetaDataByClass.get(className);
        if (cmd != null && cmd.isPopulated() && cmd.isInitialised() && cmd instanceof ClassMetaData) {
            return cmd;
        }
        Class c = null;
        try {
            if (clr == null) {
                c = Class.forName(className);
            }
            else {
                c = clr.classForName(className, null, false);
            }
        }
        catch (ClassNotFoundException cnfe) {}
        catch (ClassNotResolvedException ex) {}
        if (c != null) {
            return this.getMetaDataForClass(c, clr);
        }
        if (cmd != null && cmd.isPopulated() && cmd.isInitialised()) {
            return cmd;
        }
        return null;
    }
    
    public synchronized AbstractClassMetaData getMetaDataForClass(final Class c, final ClassLoaderResolver clr) {
        if (c == null) {
            return null;
        }
        if (this.isClassWithoutPersistenceInfo(c.getName())) {
            return null;
        }
        boolean originatingLoadCall = false;
        if (this.loadedMetaData == null) {
            originatingLoadCall = true;
            this.loadedMetaData = new ArrayList<AbstractClassMetaData>();
        }
        AbstractClassMetaData cmd = null;
        if (c.isInterface()) {
            cmd = this.getClassMetaDataForImplementationOfPersistentInterface(c.getName());
        }
        else {
            cmd = this.getMetaDataForClassInternal(c, clr);
        }
        if (cmd != null) {
            this.populateAbstractClassMetaData(cmd, clr, c.getClassLoader());
            this.initialiseAbstractClassMetaData(cmd, clr);
            if (this.utilisedFileMetaData.size() > 0) {
                final ArrayList utilisedFileMetaData1 = (ArrayList)this.utilisedFileMetaData.clone();
                this.utilisedFileMetaData.clear();
                for (final FileMetaData filemd : utilisedFileMetaData1) {
                    this.initialiseFileMetaData(filemd, clr, c.getClassLoader());
                }
                if (this.utilisedFileMetaData.size() > 0) {
                    final ArrayList utilisedFileMetaData2 = (ArrayList)this.utilisedFileMetaData.clone();
                    this.utilisedFileMetaData.clear();
                    for (final FileMetaData filemd2 : utilisedFileMetaData2) {
                        this.initialiseFileMetaData(filemd2, clr, c.getClassLoader());
                    }
                }
            }
        }
        else if (!c.isInterface()) {
            this.classesWithoutPersistenceInfo.add(c.getName());
        }
        this.utilisedFileMetaData.clear();
        if (originatingLoadCall) {
            this.processListenerLoadingCall();
        }
        return cmd;
    }
    
    protected void processListenerLoadingCall() {
        if (!this.loadedMetaData.isEmpty()) {
            for (final AbstractClassMetaData acmd : new ArrayList<AbstractClassMetaData>(this.loadedMetaData)) {
                for (final MetaDataListener listener : this.listeners) {
                    listener.loaded(acmd);
                }
            }
        }
        this.loadedMetaData = null;
    }
    
    public synchronized AbstractClassMetaData getMetaDataForEntityName(final String entityName) {
        return this.classMetaDataByEntityName.get(entityName);
    }
    
    public synchronized AbstractClassMetaData getMetaDataForDiscriminator(final String discriminator) {
        return this.classMetaDataByDiscriminatorName.get(discriminator);
    }
    
    public AbstractClassMetaData readMetaDataForClass(final String className) {
        return this.classMetaDataByClass.get(className);
    }
    
    public AbstractMemberMetaData readMetaDataForMember(final String className, final String memberName) {
        final AbstractClassMetaData cmd = this.readMetaDataForClass(className);
        return (cmd != null) ? cmd.getMetaDataForMember(memberName) : null;
    }
    
    public abstract AbstractClassMetaData getMetaDataForClassInternal(final Class p0, final ClassLoaderResolver p1);
    
    protected void registerMetaDataForClass(final String fullClassName, final AbstractClassMetaData cmd) {
        this.classMetaDataByClass.put(fullClassName, cmd);
        this.directSubclassesByClass.clear();
    }
    
    public String[] getSubclassesForClass(final String className, final boolean includeDescendents) {
        final Collection subclassNames = new HashSet();
        this.provideSubclassesForClass(className, includeDescendents, subclassNames);
        if (subclassNames.size() > 0) {
            return subclassNames.toArray(new String[subclassNames.size()]);
        }
        return null;
    }
    
    private void provideSubclassesForClass(final String className, final boolean includeDescendents, final Collection consumer) {
        Set directSubClasses = this.directSubclassesByClass.get(className);
        if (directSubClasses == null) {
            directSubClasses = this.computeDirectSubclassesForClass(className);
            this.directSubclassesByClass.put(className, directSubClasses);
        }
        consumer.addAll(directSubClasses);
        if (includeDescendents) {
            final Iterator subClassNameIter = directSubClasses.iterator();
            while (subClassNameIter.hasNext()) {
                this.provideSubclassesForClass(subClassNameIter.next(), includeDescendents, consumer);
            }
        }
    }
    
    private Set computeDirectSubclassesForClass(final String className) {
        final Set result = new HashSet();
        final Collection cmds = this.classMetaDataByClass.values();
        for (final AbstractClassMetaData acmd : cmds) {
            if (acmd instanceof ClassMetaData) {
                final ClassMetaData cmd = (ClassMetaData)acmd;
                if (cmd.getPersistenceCapableSuperclass() == null || !cmd.getPersistenceCapableSuperclass().equals(className)) {
                    continue;
                }
                result.add(cmd.getFullClassName());
            }
        }
        return result;
    }
    
    public String[] getClassesImplementingInterface(final String interfaceName, final ClassLoaderResolver clr) {
        final Collection classes = new HashSet();
        final Class intfClass = clr.classForName(interfaceName);
        final Collection generatedClassNames = new HashSet();
        final Collection cmds = this.classMetaDataByClass.values();
        final Iterator cmdIter = cmds.iterator();
        boolean isPersistentInterface = false;
        while (cmdIter.hasNext()) {
            final AbstractClassMetaData acmd = cmdIter.next();
            Class implClass = null;
            try {
                implClass = clr.classForName(acmd.getFullClassName());
            }
            catch (ClassNotResolvedException ex) {}
            if (implClass != null) {
                if (acmd instanceof ClassMetaData) {
                    this.initialiseAbstractClassMetaData(acmd, clr);
                    if (!intfClass.isAssignableFrom(implClass) || ((ClassMetaData)acmd).isAbstract()) {
                        continue;
                    }
                    classes.add(implClass);
                }
                else {
                    if (!(acmd instanceof InterfaceMetaData) || !intfClass.isAssignableFrom(implClass)) {
                        continue;
                    }
                    isPersistentInterface = true;
                }
            }
            else {
                if (!this.isPersistentInterfaceImplementation(interfaceName, acmd.getFullClassName())) {
                    continue;
                }
                isPersistentInterface = true;
                generatedClassNames.add(acmd.getFullClassName());
            }
        }
        if (isPersistentInterface && this.nucleusContext.getImplementationCreator() != null) {
            classes.add(this.nucleusContext.getImplementationCreator().newInstance(intfClass, clr).getClass());
            final int numClasses = classes.size() + generatedClassNames.size();
            final String[] classNames = new String[numClasses];
            Iterator iter = classes.iterator();
            int i = 0;
            while (iter.hasNext()) {
                classNames[i++] = iter.next().getName();
            }
            iter = generatedClassNames.iterator();
            while (iter.hasNext()) {
                classNames[i++] = iter.next();
            }
            return classNames;
        }
        if (classes.size() > 0) {
            final Collection classesSorted = new TreeSet(new InterfaceClassComparator());
            final Iterator classesIter = classes.iterator();
            while (classesIter.hasNext()) {
                classesSorted.add(classesIter.next());
            }
            final String[] classNames2 = new String[classesSorted.size()];
            final Iterator iter2 = classesSorted.iterator();
            int j = 0;
            while (iter2.hasNext()) {
                classNames2[j++] = iter2.next().getName();
            }
            return classNames2;
        }
        return null;
    }
    
    protected void addORMDataToClass(final Class c, final ClassLoaderResolver clr) {
    }
    
    void addAnnotationsDataToClass(final Class c, final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
        if (this.allowAnnotations) {
            if (cmd.getPackageMetaData() != null && cmd.getPackageMetaData().getFileMetaData() != null && cmd.getPackageMetaData().getFileMetaData().getType() == MetadataFileType.ANNOTATIONS) {
                return;
            }
            final FileMetaData filemd = this.loadAnnotationsForClass(c, clr, false, false);
            if (filemd != null) {
                final AbstractClassMetaData annotCmd = filemd.getPackage(0).getClass(0);
                if (annotCmd != null) {
                    this.postProcessClassMetaData(annotCmd, clr);
                    MetaDataMerger.mergeClassAnnotationsData(cmd, annotCmd, this);
                }
            }
        }
    }
    
    public ClassMetaData getMetaDataForImplementationOfReference(final Class referenceClass, final Object implValue, final ClassLoaderResolver clr) {
        if (referenceClass == null || (!referenceClass.isInterface() && referenceClass != Object.class)) {
            return null;
        }
        final Object intfMetaData = this.getClassMetaDataForImplementationOfPersistentInterface(referenceClass.getName());
        if (intfMetaData != null) {
            return (ClassMetaData)intfMetaData;
        }
        ClassMetaData cmd = null;
        final Set classMetaDataClasses = this.classMetaDataByClass.keySet();
        for (final String class_name : classMetaDataClasses) {
            final AbstractClassMetaData acmd_cls = this.classMetaDataByClass.get(class_name);
            if (acmd_cls instanceof ClassMetaData && referenceClass.getClassLoader() != null) {
                try {
                    final Class cls = referenceClass.getClassLoader().loadClass(class_name);
                    if (!referenceClass.isAssignableFrom(cls)) {
                        continue;
                    }
                    cmd = (ClassMetaData)acmd_cls;
                    if (implValue != null && cmd.getFullClassName().equals(implValue.getClass().getName())) {
                        return cmd;
                    }
                    AbstractClassMetaData cmd_superclass = cmd.getSuperAbstractClassMetaData();
                    while (cmd_superclass != null) {
                        if (!referenceClass.isAssignableFrom(clr.classForName(((ClassMetaData)cmd_superclass).getFullClassName()))) {
                            break;
                        }
                        cmd = (ClassMetaData)cmd_superclass;
                        if (implValue != null && cmd.getFullClassName().equals(implValue.getClass().getName())) {
                            break;
                        }
                        cmd_superclass = cmd_superclass.getSuperAbstractClassMetaData();
                        if (cmd_superclass == null) {
                            break;
                        }
                    }
                }
                catch (Exception ex) {}
            }
        }
        return cmd;
    }
    
    public AbstractMemberMetaData getMetaDataForMember(final String className, final String memberName, final ClassLoaderResolver clr) {
        if (className == null || memberName == null) {
            return null;
        }
        final AbstractClassMetaData cmd = this.getMetaDataForClass(className, clr);
        return (cmd != null) ? cmd.getMetaDataForMember(memberName) : null;
    }
    
    public AbstractMemberMetaData getMetaDataForMember(final Class c, final ClassLoaderResolver clr, final String memberName) {
        if (c == null || memberName == null) {
            return null;
        }
        final AbstractClassMetaData cmd = this.getMetaDataForClass(c, clr);
        return (cmd != null) ? cmd.getMetaDataForMember(memberName) : null;
    }
    
    public QueryMetaData getMetaDataForQuery(final Class cls, final ClassLoaderResolver clr, final String queryName) {
        if (queryName == null || this.queryMetaDataByName == null) {
            return null;
        }
        String query_key = queryName;
        if (cls != null) {
            query_key = cls.getName() + "_" + queryName;
        }
        return this.queryMetaDataByName.get(query_key);
    }
    
    public Set<String> getNamedQueryNames() {
        if (this.queryMetaDataByName == null || this.queryMetaDataByName.isEmpty()) {
            return null;
        }
        return this.queryMetaDataByName.keySet();
    }
    
    public StoredProcQueryMetaData getMetaDataForStoredProcQuery(final Class cls, final ClassLoaderResolver clr, final String queryName) {
        if (queryName == null || this.storedProcQueryMetaDataByName == null) {
            return null;
        }
        String query_key = queryName;
        if (cls != null) {
            query_key = cls.getName() + "_" + queryName;
        }
        return this.storedProcQueryMetaDataByName.get(query_key);
    }
    
    public FetchPlanMetaData getMetaDataForFetchPlan(final String name) {
        if (name == null || this.fetchPlanMetaDataByName == null) {
            return null;
        }
        return this.fetchPlanMetaDataByName.get(name);
    }
    
    public SequenceMetaData getMetaDataForSequence(final ClassLoaderResolver clr, final String seqName) {
        if (seqName == null || this.sequenceMetaDataByPackageSequence == null) {
            return null;
        }
        return this.sequenceMetaDataByPackageSequence.get(seqName);
    }
    
    public TableGeneratorMetaData getMetaDataForTableGenerator(final ClassLoaderResolver clr, final String genName) {
        if (genName == null || this.tableGeneratorMetaDataByPackageSequence == null) {
            return null;
        }
        return this.tableGeneratorMetaDataByPackageSequence.get(genName);
    }
    
    public QueryResultMetaData getMetaDataForQueryResult(final String name) {
        if (name == null || this.queryResultMetaDataByName == null) {
            return null;
        }
        return this.queryResultMetaDataByName.get(name);
    }
    
    public InterfaceMetaData getMetaDataForInterface(final Class c, final ClassLoaderResolver clr) {
        return null;
    }
    
    public boolean isPersistentInterface(final String name) {
        return false;
    }
    
    public boolean isPersistentInterfaceImplementation(final String interfaceName, final String implName) {
        return false;
    }
    
    public boolean isPersistentDefinitionImplementation(final String implName) {
        return false;
    }
    
    public String getImplementationNameForPersistentInterface(final String interfaceName) {
        return null;
    }
    
    public ClassMetaData getClassMetaDataForImplementationOfPersistentInterface(final String interfaceName) {
        return null;
    }
    
    public void registerPersistentInterface(final InterfaceMetaData imd, final Class implClass, final ClassLoaderResolver clr) {
    }
    
    public void registerImplementationOfAbstractClass(final ClassMetaData cmd, final Class implClass, final ClassLoaderResolver clr) {
    }
    
    public PersistenceUnitMetaData getMetaDataForPersistenceUnit(final String unitName) {
        final String filename = this.nucleusContext.getPersistenceConfiguration().getStringProperty("datanucleus.persistenceXmlFilename");
        final PersistenceFileMetaData[] files = MetaDataUtils.parsePersistenceFiles(this.nucleusContext.getPluginManager(), filename, this.validateXML, this.nucleusContext.getClassLoaderResolver(null));
        if (files == null) {
            throw new NucleusUserException(MetaDataManager.LOCALISER.msg("044046"));
        }
        for (int i = 0; i < files.length; ++i) {
            final PersistenceUnitMetaData[] unitmds = files[i].getPersistenceUnits();
            if (unitmds != null) {
                for (int j = 0; j < unitmds.length; ++j) {
                    if (unitmds[j].getName().equals(unitName)) {
                        return unitmds[j];
                    }
                }
            }
        }
        return null;
    }
    
    protected abstract FileMetaData parseFile(final URL p0);
    
    public abstract void registerFile(final String p0, final FileMetaData p1, final ClassLoaderResolver p2);
    
    public void registerDiscriminatorValueForClass(final AbstractClassMetaData cmd, final String discrimValue) {
        final AbstractClassMetaData rootCmd = cmd.getBaseAbstractClassMetaData();
        DiscriminatorLookup lookup = this.discriminatorLookupByRootClassName.get(rootCmd.getFullClassName());
        if (lookup == null) {
            lookup = new DiscriminatorLookup();
            this.discriminatorLookupByRootClassName.put(rootCmd.getFullClassName(), lookup);
        }
        lookup.addValue(cmd.getFullClassName(), discrimValue);
    }
    
    public String getClassNameForDiscriminatorValueWithRoot(final AbstractClassMetaData rootCmd, final String discrimValue) {
        final DiscriminatorLookup lookup = this.discriminatorLookupByRootClassName.get(rootCmd.getFullClassName());
        if (lookup != null) {
            return lookup.getClassForValue(discrimValue);
        }
        return null;
    }
    
    public String getDiscriminatorValueForClass(final AbstractClassMetaData cmd, final String discrimValue) {
        final AbstractClassMetaData rootCmd = cmd.getBaseAbstractClassMetaData();
        final DiscriminatorLookup lookup = this.discriminatorLookupByRootClassName.get(rootCmd.getFullClassName());
        if (lookup != null) {
            return lookup.getValueForClass(cmd.getFullClassName());
        }
        return null;
    }
    
    public String getClassNameFromDiscriminatorValue(final String discrimValue, final DiscriminatorMetaData dismd) {
        if (discrimValue == null) {
            return null;
        }
        if (dismd.getStrategy() == DiscriminatorStrategy.CLASS_NAME) {
            return discrimValue;
        }
        if (dismd.getStrategy() == DiscriminatorStrategy.VALUE_MAP) {
            final AbstractClassMetaData baseCmd = (AbstractClassMetaData)((InheritanceMetaData)dismd.getParent()).getParent();
            final AbstractClassMetaData rootCmd = baseCmd.getBaseAbstractClassMetaData();
            return this.getClassNameForDiscriminatorValueWithRoot(rootCmd, discrimValue);
        }
        return null;
    }
    
    protected void registerSequencesForFile(final FileMetaData filemd) {
        for (int i = 0; i < filemd.getNoOfPackages(); ++i) {
            final PackageMetaData pmd = filemd.getPackage(i);
            final SequenceMetaData[] seqmds = pmd.getSequences();
            if (seqmds != null) {
                if (this.sequenceMetaDataByPackageSequence == null) {
                    this.sequenceMetaDataByPackageSequence = new ConcurrentHashMap<String, SequenceMetaData>();
                }
                for (int j = 0; j < seqmds.length; ++j) {
                    this.sequenceMetaDataByPackageSequence.put(seqmds[j].getFullyQualifiedName(), seqmds[j]);
                    this.sequenceMetaDataByPackageSequence.put(seqmds[j].getName(), seqmds[j]);
                }
            }
        }
    }
    
    protected void registerTableGeneratorsForFile(final FileMetaData filemd) {
        for (int i = 0; i < filemd.getNoOfPackages(); ++i) {
            final PackageMetaData pmd = filemd.getPackage(i);
            final TableGeneratorMetaData[] tgmds = pmd.getTableGenerators();
            if (tgmds != null) {
                if (this.tableGeneratorMetaDataByPackageSequence == null) {
                    this.tableGeneratorMetaDataByPackageSequence = new ConcurrentHashMap<String, TableGeneratorMetaData>();
                }
                for (int j = 0; j < tgmds.length; ++j) {
                    this.tableGeneratorMetaDataByPackageSequence.put(tgmds[j].getFullyQualifiedName(), tgmds[j]);
                    this.tableGeneratorMetaDataByPackageSequence.put(tgmds[j].getName(), tgmds[j]);
                }
            }
        }
    }
    
    protected void registerQueryResultMetaDataForFile(final FileMetaData filemd) {
        final QueryResultMetaData[] fqrmds = filemd.getQueryResultMetaData();
        if (fqrmds != null) {
            if (this.queryResultMetaDataByName == null) {
                this.queryResultMetaDataByName = new ConcurrentHashMap<String, QueryResultMetaData>();
            }
            for (int i = 0; i < fqrmds.length; ++i) {
                this.queryResultMetaDataByName.put(fqrmds[i].getName(), fqrmds[i]);
            }
        }
        for (int i = 0; i < filemd.getNoOfPackages(); ++i) {
            final PackageMetaData pmd = filemd.getPackage(i);
            for (int j = 0; j < pmd.getNoOfClasses(); ++j) {
                final AbstractClassMetaData cmd = pmd.getClass(j);
                final QueryResultMetaData[] qrmds = cmd.getQueryResultMetaData();
                if (qrmds != null) {
                    if (this.queryResultMetaDataByName == null) {
                        this.queryResultMetaDataByName = new ConcurrentHashMap<String, QueryResultMetaData>();
                    }
                    for (int k = 0; k < qrmds.length; ++k) {
                        this.queryResultMetaDataByName.put(qrmds[k].getName(), qrmds[k]);
                    }
                }
            }
        }
    }
    
    protected void registerQueriesForFile(final FileMetaData filemd) {
        final QueryMetaData[] queries = filemd.getQueries();
        if (queries != null) {
            if (this.queryMetaDataByName == null) {
                this.queryMetaDataByName = new ConcurrentHashMap<String, QueryMetaData>();
            }
            for (int i = 0; i < queries.length; ++i) {
                final String scope = queries[i].getScope();
                String key = queries[i].getName();
                if (scope != null) {
                    key = scope + "_" + key;
                }
                this.queryMetaDataByName.put(key, queries[i]);
            }
        }
        for (int i = 0; i < filemd.getNoOfPackages(); ++i) {
            final PackageMetaData pmd = filemd.getPackage(i);
            for (int j = 0; j < pmd.getNoOfClasses(); ++j) {
                final ClassMetaData cmd = pmd.getClass(j);
                final QueryMetaData[] classQueries = cmd.getQueries();
                if (classQueries != null) {
                    if (this.queryMetaDataByName == null) {
                        this.queryMetaDataByName = new ConcurrentHashMap<String, QueryMetaData>();
                    }
                    for (int k = 0; k < classQueries.length; ++k) {
                        final String scope2 = classQueries[k].getScope();
                        String key2 = classQueries[k].getName();
                        if (scope2 != null) {
                            key2 = scope2 + "_" + key2;
                        }
                        this.queryMetaDataByName.put(key2, classQueries[k]);
                    }
                }
            }
            for (int j = 0; j < pmd.getNoOfInterfaces(); ++j) {
                final InterfaceMetaData intfmd = pmd.getInterface(j);
                final QueryMetaData[] interfaceQueries = intfmd.getQueries();
                if (interfaceQueries != null) {
                    if (this.queryMetaDataByName == null) {
                        this.queryMetaDataByName = new ConcurrentHashMap<String, QueryMetaData>();
                    }
                    for (int k = 0; k < interfaceQueries.length; ++k) {
                        final String scope2 = interfaceQueries[k].getScope();
                        String key2 = interfaceQueries[k].getName();
                        if (scope2 != null) {
                            key2 = scope2 + "_" + key2;
                        }
                        this.queryMetaDataByName.put(key2, interfaceQueries[k]);
                    }
                }
            }
        }
    }
    
    protected void registerStoredProcQueriesForFile(final FileMetaData filemd) {
        final StoredProcQueryMetaData[] queries = filemd.getStoredProcQueries();
        if (queries != null) {
            if (this.storedProcQueryMetaDataByName == null) {
                this.storedProcQueryMetaDataByName = new ConcurrentHashMap<String, StoredProcQueryMetaData>();
            }
            for (int i = 0; i < queries.length; ++i) {
                final String key = queries[i].getName();
                this.storedProcQueryMetaDataByName.put(key, queries[i]);
            }
        }
        for (int i = 0; i < filemd.getNoOfPackages(); ++i) {
            final PackageMetaData pmd = filemd.getPackage(i);
            for (int j = 0; j < pmd.getNoOfClasses(); ++j) {
                final ClassMetaData cmd = pmd.getClass(j);
                final StoredProcQueryMetaData[] classStoredProcQueries = cmd.getStoredProcQueries();
                if (classStoredProcQueries != null) {
                    if (this.storedProcQueryMetaDataByName == null) {
                        this.storedProcQueryMetaDataByName = new ConcurrentHashMap<String, StoredProcQueryMetaData>();
                    }
                    for (int k = 0; k < classStoredProcQueries.length; ++k) {
                        final String key2 = classStoredProcQueries[k].getName();
                        this.storedProcQueryMetaDataByName.put(key2, classStoredProcQueries[k]);
                    }
                }
            }
            for (int j = 0; j < pmd.getNoOfInterfaces(); ++j) {
                final InterfaceMetaData intfmd = pmd.getInterface(j);
                final StoredProcQueryMetaData[] interfaceStoredProcQueries = intfmd.getStoredProcQueries();
                if (interfaceStoredProcQueries != null) {
                    if (this.storedProcQueryMetaDataByName == null) {
                        this.storedProcQueryMetaDataByName = new ConcurrentHashMap<String, StoredProcQueryMetaData>();
                    }
                    for (int k = 0; k < interfaceStoredProcQueries.length; ++k) {
                        final String key2 = interfaceStoredProcQueries[k].getName();
                        this.storedProcQueryMetaDataByName.put(key2, interfaceStoredProcQueries[k]);
                    }
                }
            }
        }
    }
    
    protected void registerFetchPlansForFile(final FileMetaData filemd) {
        final FetchPlanMetaData[] fetchPlans = filemd.getFetchPlans();
        if (fetchPlans != null) {
            if (this.fetchPlanMetaDataByName == null) {
                this.fetchPlanMetaDataByName = new ConcurrentHashMap<String, FetchPlanMetaData>();
            }
            for (int i = 0; i < fetchPlans.length; ++i) {
                this.fetchPlanMetaDataByName.put(fetchPlans[i].getName(), fetchPlans[i]);
            }
        }
    }
    
    protected void populateFileMetaData(final FileMetaData filemd, final ClassLoaderResolver clr, final ClassLoader primary) {
        filemd.setMetaDataManager(this);
        for (int i = 0; i < filemd.getNoOfPackages(); ++i) {
            final PackageMetaData pmd = filemd.getPackage(i);
            for (int j = 0; j < pmd.getNoOfClasses(); ++j) {
                final AbstractClassMetaData cmd = pmd.getClass(j);
                this.populateAbstractClassMetaData(cmd, clr, primary);
            }
            for (int j = 0; j < pmd.getNoOfInterfaces(); ++j) {
                final AbstractClassMetaData cmd = pmd.getInterface(j);
                this.populateAbstractClassMetaData(cmd, clr, primary);
            }
        }
    }
    
    protected void initialiseFileMetaData(final FileMetaData filemd, final ClassLoaderResolver clr, final ClassLoader primary) {
        for (int i = 0; i < filemd.getNoOfPackages(); ++i) {
            final PackageMetaData pmd = filemd.getPackage(i);
            pmd.initialise(clr, this);
            for (int j = 0; j < pmd.getNoOfClasses(); ++j) {
                final ClassMetaData cmd = pmd.getClass(j);
                try {
                    this.initialiseClassMetaData(cmd, clr.classForName(cmd.getFullClassName(), primary), clr);
                }
                catch (NucleusException ne) {
                    throw ne;
                }
                catch (RuntimeException ex) {}
            }
            for (int j = 0; j < pmd.getNoOfInterfaces(); ++j) {
                final InterfaceMetaData imd = pmd.getInterface(j);
                try {
                    this.initialiseInterfaceMetaData(imd, clr, primary);
                }
                catch (NucleusException jpex) {
                    throw jpex;
                }
                catch (RuntimeException ex2) {}
            }
        }
    }
    
    protected void initialiseClassMetaData(final ClassMetaData cmd, final Class cls, final ClassLoaderResolver clr) {
        synchronized (cmd) {
            if (this.getNucleusContext().getType() == NucleusContext.ContextType.PERSISTENCE && cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE && !this.getNucleusContext().getApiAdapter().isPersistable(cls)) {
                throw new NucleusUserException(MetaDataManager.LOCALISER.msg("044059", cls.getName()));
            }
            this.populateAbstractClassMetaData(cmd, clr, cls.getClassLoader());
            this.initialiseAbstractClassMetaData(cmd, clr);
        }
    }
    
    protected void initialiseInterfaceMetaData(final InterfaceMetaData imd, final ClassLoaderResolver clr, final ClassLoader primary) {
        synchronized (imd) {
            this.populateAbstractClassMetaData(imd, clr, primary);
            this.initialiseAbstractClassMetaData(imd, clr);
        }
    }
    
    protected FileMetaData loadAnnotationsForClass(final Class cls, final ClassLoaderResolver clr, final boolean register, final boolean populate) {
        if (!this.allowAnnotations) {
            return null;
        }
        if (this.isClassWithoutPersistenceInfo(cls.getName())) {
            return null;
        }
        String clsPackageName = ClassUtils.getPackageNameForClass(cls);
        if (clsPackageName == null) {
            clsPackageName = "";
        }
        final FileMetaData filemd = new FileMetaData();
        filemd.setType(MetadataFileType.ANNOTATIONS);
        filemd.setMetaDataManager(this);
        final PackageMetaData pmd = filemd.newPackageMetadata(clsPackageName);
        final AbstractClassMetaData cmd = this.annotationManager.getMetaDataForClass(cls, pmd, clr);
        if (cmd != null) {
            if (register) {
                this.registerFile("annotations:" + cls.getName(), filemd, clr);
                if (populate) {
                    this.populateFileMetaData(filemd, clr, cls.getClassLoader());
                }
            }
            return filemd;
        }
        return null;
    }
    
    protected void postProcessClassMetaData(final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
    }
    
    protected void populateAbstractClassMetaData(final AbstractClassMetaData acmd, final ClassLoaderResolver clr, final ClassLoader loader) {
        if (!acmd.isPopulated() && !acmd.isInitialised()) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    try {
                        acmd.populate(clr, loader, MetaDataManager.this);
                    }
                    catch (NucleusException ne) {
                        throw ne;
                    }
                    catch (Exception e) {
                        throw new NucleusUserException("Exception during population of metadata for " + acmd.getFullClassName(), e);
                    }
                    return null;
                }
            });
        }
    }
    
    void abstractClassMetaDataInitialised(final AbstractClassMetaData acmd) {
        if (acmd.getIdentityType() == IdentityType.APPLICATION && !acmd.usesSingleFieldIdentityClass()) {
            this.classMetaDataByAppIdClassName.put(acmd.getObjectidClass(), acmd);
        }
        if (this.listeners != null && this.loadedMetaData != null) {
            this.loadedMetaData.add(acmd);
        }
    }
    
    protected void initialiseAbstractClassMetaData(final AbstractClassMetaData acmd, final ClassLoaderResolver clr) {
        if (!acmd.isInitialised()) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    try {
                        acmd.initialise(clr, MetaDataManager.this);
                        if (acmd.hasExtension("cache-pin") && acmd.getValueForExtension("cache-pin").equalsIgnoreCase("true")) {
                            final Class cls = clr.classForName(acmd.getFullClassName());
                            MetaDataManager.this.nucleusContext.getLevel2Cache().pinAll(cls, false);
                        }
                    }
                    catch (NucleusException ne) {
                        throw ne;
                    }
                    catch (Exception e) {
                        throw new NucleusUserException("Exception during initialisation of metadata for " + acmd.getFullClassName(), e);
                    }
                    return null;
                }
            });
        }
    }
    
    public List<AbstractClassMetaData> getReferencedClasses(final String[] classNames, final ClassLoaderResolver clr) {
        final List<AbstractClassMetaData> cmds = new ArrayList<AbstractClassMetaData>();
        for (int i = 0; i < classNames.length; ++i) {
            Class cls = null;
            try {
                cls = clr.classForName(classNames[i]);
                if (!cls.isInterface()) {
                    final AbstractClassMetaData cmd = this.getMetaDataForClass(classNames[i], clr);
                    if (cmd == null) {
                        NucleusLogger.DATASTORE.warn("Class Invalid " + classNames[i]);
                        throw new NoPersistenceInformationException(classNames[i]);
                    }
                    cmds.addAll(this.getReferencedClassMetaData(cmd, clr));
                }
            }
            catch (ClassNotResolvedException cnre) {
                NucleusLogger.DATASTORE.warn("Class " + classNames[i] + " not found so being ignored");
            }
        }
        return cmds;
    }
    
    protected List<AbstractClassMetaData> getReferencedClassMetaData(final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
        if (cmd == null) {
            return null;
        }
        final List<AbstractClassMetaData> orderedCMDs = new ArrayList<AbstractClassMetaData>();
        final Set referencedCMDs = new HashSet();
        cmd.getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, this);
        return orderedCMDs;
    }
    
    public boolean isFieldTypePersistable(final Class type) {
        if (this.isEnhancing()) {
            final AbstractClassMetaData cmd = this.readMetaDataForClass(type.getName());
            if (cmd != null && cmd instanceof ClassMetaData && cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                return true;
            }
        }
        return this.getApiAdapter().isPersistable(type);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    private class DiscriminatorLookup
    {
        Map<String, String> discrimValueByClass;
        Map<String, String> discrimClassByValue;
        
        private DiscriminatorLookup() {
            this.discrimValueByClass = new HashMap<String, String>();
            this.discrimClassByValue = new HashMap<String, String>();
        }
        
        public void addValue(final String className, final String value) {
            this.discrimValueByClass.put(className, value);
            this.discrimClassByValue.put(value, className);
        }
        
        public String getValueForClass(final String className) {
            return this.discrimValueByClass.get(className);
        }
        
        public String getClassForValue(final String value) {
            return this.discrimClassByValue.get(value);
        }
        
        @Override
        public String toString() {
            return StringUtils.mapToString(this.discrimValueByClass);
        }
    }
    
    private static class InterfaceClassComparator implements Comparator, Serializable
    {
        public InterfaceClassComparator() {
        }
        
        @Override
        public int compare(final Object o1, final Object o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null || o2 == null) {
                return Integer.MIN_VALUE;
            }
            final Class cls1 = (Class)o1;
            final Class cls2 = (Class)o2;
            return cls1.hashCode() - cls2.hashCode();
        }
    }
}
