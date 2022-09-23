// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import javax.jdo.spi.RegisterClassEvent;
import org.datanucleus.metadata.ImplementsMetaData;
import org.datanucleus.util.StringUtils;
import java.util.StringTokenizer;
import java.util.Iterator;
import org.datanucleus.metadata.MetaDataMerger;
import org.datanucleus.metadata.SequenceMetaData;
import java.util.Enumeration;
import java.util.List;
import java.io.IOException;
import org.datanucleus.exceptions.NucleusException;
import java.util.Collection;
import java.util.ArrayList;
import org.datanucleus.metadata.QueryMetaData;
import org.datanucleus.metadata.InterfaceMetaData;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.PackageMetaData;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.metadata.MetadataFileType;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.xml.MetaDataParser;
import org.datanucleus.metadata.FileMetaData;
import java.net.URL;
import org.datanucleus.PersistenceConfiguration;
import javax.jdo.spi.RegisterClassListener;
import org.datanucleus.api.jdo.NucleusJDOHelper;
import org.datanucleus.util.NucleusLogger;
import java.util.concurrent.ConcurrentHashMap;
import org.datanucleus.NucleusContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Map;
import org.datanucleus.metadata.MetaDataManager;

public class JDOMetaDataManager extends MetaDataManager
{
    public static final int ALL_JDO_LOCATIONS = 1;
    public static final int JDO_1_0_0_LOCATIONS = 2;
    public static final int JDO_1_0_1_LOCATIONS = 3;
    protected int locationDefinition;
    protected Map<String, AbstractClassMetaData> ormClassMetaDataByClass;
    protected Map<String, AbstractClassMetaData> classMetaDataByInterface;
    protected MetaDataRegisterClassListener registerListener;
    private static final char CLASS_SEPARATOR = '.';
    private static final char PATH_SEPARATOR = '/';
    private static final char EXTENSION_SEPARATOR = '.';
    private static final String METADATA_PACKAGE = "package";
    private static final String METADATA_LOCATION_METAINF = "/META-INF/package";
    private static final String METADATA_LOCATION_WEBINF = "/WEB-INF/package";
    
    public JDOMetaDataManager(final NucleusContext ctxt) {
        super(ctxt);
        this.locationDefinition = 1;
        this.ormClassMetaDataByClass = new ConcurrentHashMap<String, AbstractClassMetaData>();
        this.classMetaDataByInterface = new ConcurrentHashMap<String, AbstractClassMetaData>();
        this.locationDefinition = 1;
        boolean useMetadataListener = false;
        final PersistenceConfiguration conf = ctxt.getPersistenceConfiguration();
        if (conf.getStringProperty("datanucleus.PersistenceUnitName") == null && ctxt.getType() == NucleusContext.ContextType.PERSISTENCE && conf.getBooleanProperty("datanucleus.metadata.autoregistration")) {
            useMetadataListener = true;
        }
        if (NucleusLogger.METADATA.isDebugEnabled()) {
            if (this.allowXML && this.allowAnnotations) {
                if (this.allowORM) {
                    final String mappingName = this.getORMMappingName();
                    NucleusLogger.METADATA.debug("MetaDataManager : Input=(XML,Annotations), XML-Validation=" + this.validateXML + ", XML-Suffices=(persistence=*." + this.getJDOFileSuffix() + ", orm=" + this.getORMFileSuffix() + ", query=*." + this.getJDOQueryFileSuffix() + ")" + ((mappingName != null) ? (" mapping-name=" + mappingName) : "") + ", JDO-listener=" + useMetadataListener);
                }
                else {
                    NucleusLogger.METADATA.debug("MetaDataManager : Input=(XML,Annotations), XML-Validation=" + this.validateXML + ", XML-Suffices=(persistence=*." + this.getJDOFileSuffix() + ", query=*." + this.getJDOQueryFileSuffix() + ")" + ", JDO-listener=" + useMetadataListener);
                }
            }
            else if (this.allowXML && !this.allowAnnotations) {
                if (this.allowORM) {
                    final String mappingName = this.getORMMappingName();
                    NucleusLogger.METADATA.debug("MetaDataManager : Input=(XML), XML-Validation=" + this.validateXML + ", XML-Suffices=(persistence=*." + this.getJDOFileSuffix() + ", orm=" + this.getORMFileSuffix() + ", query=*." + this.getJDOQueryFileSuffix() + ")" + ((mappingName != null) ? (" mapping-name=" + mappingName) : "") + ", JDO-listener=" + useMetadataListener);
                }
                else {
                    NucleusLogger.METADATA.debug("MetaDataManager : Input=(XML), XML-Validation=" + this.validateXML + ", XML-Suffices=(persistence=*." + this.getJDOFileSuffix() + ", query=*." + this.getJDOQueryFileSuffix() + ")" + ", JDO-listener=" + useMetadataListener);
                }
            }
            else if (!this.allowXML && this.allowAnnotations) {
                NucleusLogger.METADATA.debug("MetaDataManager : Input=(Annotations), JDO-listener=" + useMetadataListener);
            }
            else {
                NucleusLogger.METADATA.debug("MetaDataManager : Input=(NONE), JDO-listener=" + useMetadataListener);
            }
        }
        if (useMetadataListener) {
            NucleusLogger.METADATA.debug("Registering listener for metadata initialisation");
            this.registerListener = new MetaDataRegisterClassListener();
            NucleusJDOHelper.getJDOImplHelper().addRegisterClassListener(this.registerListener);
        }
    }
    
    @Override
    public void close() {
        if (this.registerListener != null) {
            NucleusLogger.METADATA.debug("Deregistering listener for metadata initialisation");
            NucleusJDOHelper.getJDOImplHelper().removeRegisterClassListener(this.registerListener);
        }
        super.close();
        this.ormClassMetaDataByClass.clear();
        this.ormClassMetaDataByClass = null;
    }
    
    @Override
    protected FileMetaData parseFile(final URL fileURL) {
        if (this.metaDataParser == null) {
            this.metaDataParser = new MetaDataParser(this, this.nucleusContext.getPluginManager(), this.validateXML);
        }
        this.metaDataParser.setNamespaceAware(this.supportXMLNamespaces);
        return (FileMetaData)this.metaDataParser.parseMetaDataURL(fileURL, "jdo");
    }
    
    @Override
    public void registerFile(final String fileURLString, final FileMetaData filemd, final ClassLoaderResolver clr) {
        if (fileURLString == null) {
            return;
        }
        if (this.fileMetaDataByURLString.get(fileURLString) != null) {
            return;
        }
        this.fileMetaDataByURLString.put(fileURLString, filemd);
        this.registerQueriesForFile(filemd);
        this.registerFetchPlansForFile(filemd);
        this.registerSequencesForFile(filemd);
        this.registerTableGeneratorsForFile(filemd);
        if (filemd.getType() != MetadataFileType.JDO_QUERY_FILE) {
            for (int i = 0; i < filemd.getNoOfPackages(); ++i) {
                final PackageMetaData pmd = filemd.getPackage(i);
                for (int j = 0; j < pmd.getNoOfClasses(); ++j) {
                    final ClassMetaData cmd = pmd.getClass(j);
                    if (this.classesWithoutPersistenceInfo.contains(cmd.getFullClassName())) {
                        this.classesWithoutPersistenceInfo.remove(cmd.getFullClassName());
                    }
                    if (filemd.getType() == MetadataFileType.JDO_FILE || filemd.getType() == MetadataFileType.ANNOTATIONS) {
                        this.registerMetaDataForClass(cmd.getFullClassName(), cmd);
                    }
                    else if (filemd.getType() == MetadataFileType.JDO_ORM_FILE) {
                        this.ormClassMetaDataByClass.put(cmd.getFullClassName(), cmd);
                    }
                    if (cmd.getEntityName() != null) {
                        this.classMetaDataByEntityName.put(cmd.getEntityName(), cmd);
                    }
                    if (cmd.getInheritanceMetaData() != null) {
                        final DiscriminatorMetaData dismd = cmd.getInheritanceMetaData().getDiscriminatorMetaData();
                        if (dismd != null) {
                            if (dismd.getStrategy() == DiscriminatorStrategy.CLASS_NAME) {
                                this.classMetaDataByDiscriminatorName.put(cmd.getFullClassName(), cmd);
                            }
                            else if (dismd.getStrategy() == DiscriminatorStrategy.VALUE_MAP && dismd.getValue() != null) {
                                this.classMetaDataByDiscriminatorName.put(dismd.getValue(), cmd);
                            }
                        }
                    }
                }
                for (int j = 0; j < pmd.getNoOfInterfaces(); ++j) {
                    final InterfaceMetaData intfmd = pmd.getInterface(j);
                    if (filemd.getType() == MetadataFileType.JDO_FILE || filemd.getType() == MetadataFileType.ANNOTATIONS) {
                        this.registerMetaDataForClass(intfmd.getFullClassName(), intfmd);
                    }
                    else if (filemd.getType() == MetadataFileType.JDO_ORM_FILE) {
                        this.ormClassMetaDataByClass.put(intfmd.getFullClassName(), intfmd);
                    }
                }
            }
        }
    }
    
    @Override
    public synchronized AbstractClassMetaData getMetaDataForClassInternal(final Class c, final ClassLoaderResolver clr) {
        if (c.isArray()) {
            return null;
        }
        final String className = c.getName();
        if (this.isClassWithoutPersistenceInfo(className)) {
            return null;
        }
        AbstractClassMetaData the_md = this.classMetaDataByClass.get(className);
        if (the_md != null) {
            return the_md;
        }
        if (!this.allowMetaDataLoad) {
            return null;
        }
        try {
            this.updateLock.lock();
            if (this.allowXML) {
                final FileMetaData filemd = this.loadXMLMetaDataForClass(c, clr, null, this.getJDOFileSuffix(), MetadataFileType.JDO_FILE, true);
                if (filemd != null) {
                    this.utilisedFileMetaData.add(filemd);
                    the_md = this.classMetaDataByClass.get(className);
                    return the_md;
                }
            }
            if (this.allowAnnotations) {
                final FileMetaData annFilemd = this.loadAnnotationsForClass(c, clr, true, true);
                if (annFilemd != null) {
                    if (c.isInterface()) {
                        return annFilemd.getPackage(0).getInterface(0);
                    }
                    return annFilemd.getPackage(0).getClass(0);
                }
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044043", className));
            }
            this.classesWithoutPersistenceInfo.add(className);
            return null;
        }
        finally {
            this.updateLock.unlock();
        }
    }
    
    @Override
    public QueryMetaData getMetaDataForQuery(final Class cls, final ClassLoaderResolver clr, final String queryName) {
        QueryMetaData qmd = super.getMetaDataForQuery(cls, clr, queryName);
        if (qmd != null) {
            return qmd;
        }
        String query_key = queryName;
        if (cls != null) {
            query_key = cls.getName() + "_" + queryName;
        }
        if (cls == null) {
            final List locations = new ArrayList();
            locations.addAll(this.getValidMetaDataLocationsForItem(this.getJDOFileSuffix(), null, null, false));
            locations.addAll(this.getValidMetaDataLocationsForItem(this.getORMFileSuffix(), this.getORMMappingName(), null, false));
            locations.addAll(this.getValidMetaDataLocationsForItem(this.getJDOQueryFileSuffix(), null, null, false));
            for (int i = 0; i < locations.size(); ++i) {
                final String location = locations.get(i);
                Enumeration resources;
                try {
                    resources = clr.getResources(location, (cls != null) ? cls.getClassLoader() : null);
                }
                catch (IOException e) {
                    throw new NucleusException("Error loading resources", e).setFatal();
                }
                while (resources.hasMoreElements()) {
                    final URL fileURL = resources.nextElement();
                    if (this.fileMetaDataByURLString.get(fileURL.toString()) == null) {
                        final FileMetaData filemd = this.parseFile(fileURL);
                        this.registerFile(fileURL.toString(), filemd, clr);
                    }
                }
                if (this.queryMetaDataByName != null) {
                    qmd = this.queryMetaDataByName.get(query_key);
                    if (qmd != null) {
                        if (NucleusLogger.METADATA.isDebugEnabled()) {
                            NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044053", query_key, location));
                        }
                        return qmd;
                    }
                }
                if (NucleusLogger.METADATA.isDebugEnabled()) {
                    NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044050", query_key, location));
                }
            }
            return null;
        }
        AbstractClassMetaData cmd = this.getMetaDataForClass(cls, clr);
        if (cmd == null) {
            return null;
        }
        if (this.queryMetaDataByName != null) {
            final Object obj = this.queryMetaDataByName.get(query_key);
            if (obj != null) {
                return (QueryMetaData)obj;
            }
        }
        if (this.allowXML) {
            final List locations2 = new ArrayList();
            locations2.addAll(this.getValidMetaDataLocationsForClass(this.getJDOQueryFileSuffix(), null, cls.getName()));
            for (int j = 0; j < locations2.size(); ++j) {
                final String location2 = locations2.get(j);
                Enumeration resources2;
                try {
                    resources2 = clr.getResources(location2, cls.getClassLoader());
                }
                catch (IOException e2) {
                    throw new NucleusException("Error loading resource", e2).setFatal();
                }
                while (resources2.hasMoreElements()) {
                    final URL fileURL2 = resources2.nextElement();
                    if (this.fileMetaDataByURLString.get(fileURL2.toString()) == null) {
                        final FileMetaData filemd2 = this.parseFile(fileURL2);
                        filemd2.setType(MetadataFileType.JDO_QUERY_FILE);
                        this.registerFile(fileURL2.toString(), filemd2, clr);
                    }
                }
                cmd = this.getMetaDataForClass(cls, clr);
                if (this.queryMetaDataByName != null) {
                    qmd = this.queryMetaDataByName.get(query_key);
                    if (qmd != null) {
                        if (NucleusLogger.METADATA.isDebugEnabled()) {
                            NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044053", query_key, location2));
                        }
                        return qmd;
                    }
                    if (NucleusLogger.METADATA.isDebugEnabled()) {
                        NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044050", query_key, location2));
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public SequenceMetaData getMetaDataForSequence(final ClassLoaderResolver clr, final String packageSequenceName) {
        SequenceMetaData seqmd = super.getMetaDataForSequence(clr, packageSequenceName);
        if (seqmd != null) {
            return seqmd;
        }
        String packageName = packageSequenceName;
        if (packageSequenceName.lastIndexOf(46) >= 0) {
            packageName = packageSequenceName.substring(0, packageSequenceName.lastIndexOf(46));
        }
        final List locations = new ArrayList();
        locations.addAll(this.getValidMetaDataLocationsForItem(this.getJDOFileSuffix(), null, packageName, false));
        locations.addAll(this.getValidMetaDataLocationsForItem(this.getORMFileSuffix(), this.getORMMappingName(), packageName, false));
        for (int i = 0; i < locations.size(); ++i) {
            final String location = locations.get(i);
            Enumeration resources;
            try {
                resources = clr.getResources(location, null);
            }
            catch (IOException e) {
                throw new NucleusException("Error loading resource", e).setFatal();
            }
            while (resources.hasMoreElements()) {
                final URL fileURL = resources.nextElement();
                if (this.fileMetaDataByURLString.get(fileURL.toString()) == null) {
                    final FileMetaData filemd = this.parseFile(fileURL);
                    this.registerFile(fileURL.toString(), filemd, clr);
                }
            }
            if (this.sequenceMetaDataByPackageSequence != null) {
                seqmd = this.sequenceMetaDataByPackageSequence.get(packageSequenceName);
            }
            if (seqmd != null) {
                if (NucleusLogger.METADATA.isDebugEnabled()) {
                    NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044053", packageSequenceName, location));
                }
                return seqmd;
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044051", packageSequenceName, location));
            }
        }
        return null;
    }
    
    public void addORMDataToClass(final Class c, final ClassLoaderResolver clr) {
        if (this.getNucleusContext().getType() == NucleusContext.ContextType.ENHANCEMENT) {
            return;
        }
        if (!this.allowORM) {
            return;
        }
        final AbstractClassMetaData cmd = this.classMetaDataByClass.get(c.getName());
        AbstractClassMetaData ormCmd = this.ormClassMetaDataByClass.get(c.getName());
        if (ormCmd != null) {
            MetaDataMerger.mergeClassORMData(cmd, ormCmd, this);
            this.ormClassMetaDataByClass.remove(c.getName());
            return;
        }
        if (this.allowXML) {
            final FileMetaData filemdORM = this.loadXMLMetaDataForClass(c, clr, this.getORMMappingName(), this.getORMFileSuffix(), MetadataFileType.JDO_ORM_FILE, false);
            if (filemdORM != null) {
                ormCmd = this.ormClassMetaDataByClass.get(c.getName());
                if (ormCmd != null) {
                    MetaDataMerger.mergeFileORMData((FileMetaData)cmd.getPackageMetaData().getParent(), (FileMetaData)ormCmd.getPackageMetaData().getParent());
                    MetaDataMerger.mergeClassORMData(cmd, ormCmd, this);
                    this.ormClassMetaDataByClass.remove(c.getName());
                }
            }
        }
    }
    
    protected FileMetaData loadXMLMetaDataForClass(final Class pc_class, final ClassLoaderResolver clr, final String mappingModifier, final String metadataFileExtension, final MetadataFileType metadataType, final boolean populate) {
        final List validLocations = this.getValidMetaDataLocationsForClass(metadataFileExtension, mappingModifier, pc_class.getName());
        for (final String location : validLocations) {
            Enumeration resources;
            try {
                resources = clr.getResources(location, pc_class.getClassLoader());
            }
            catch (IOException e) {
                throw new NucleusException("Error loading resource", e).setFatal();
            }
            if (!resources.hasMoreElements() && NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044049", metadataFileExtension, pc_class.getName(), location));
            }
            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                if (url != null) {
                    FileMetaData filemd = this.fileMetaDataByURLString.get(url.toString());
                    if (filemd == null) {
                        filemd = this.parseFile(url);
                        if (filemd.getType() != metadataType) {
                            NucleusLogger.METADATA.warn(JDOMetaDataManager.LOCALISER.msg("044045", url, filemd.getType(), metadataType));
                            filemd = null;
                            break;
                        }
                        this.registerFile(url.toString(), filemd, clr);
                        if (populate) {
                            this.populateFileMetaData(filemd, clr, pc_class.getClassLoader());
                        }
                    }
                    if ((filemd.getType() == MetadataFileType.JDO_FILE && this.classMetaDataByClass.get(pc_class.getName()) != null) || (filemd.getType() == MetadataFileType.JDO_ORM_FILE && this.ormClassMetaDataByClass.get(pc_class.getName()) != null)) {
                        if (NucleusLogger.METADATA.isDebugEnabled()) {
                            NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044052", metadataFileExtension, pc_class.getName(), url));
                        }
                        return filemd;
                    }
                    continue;
                }
            }
        }
        if (NucleusLogger.METADATA.isDebugEnabled()) {
            NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044048", metadataFileExtension, pc_class.getName()));
        }
        return null;
    }
    
    public List getValidMetaDataLocationsForPackage(final String fileExtension, final String fileModifier, final String packageName) {
        return this.getValidMetaDataLocationsForItem(fileExtension, fileModifier, packageName, false);
    }
    
    public List getValidMetaDataLocationsForClass(final String fileExtension, final String fileModifier, final String className) {
        return this.getValidMetaDataLocationsForItem(fileExtension, fileModifier, className, true);
    }
    
    List getValidMetaDataLocationsForItem(String fileExtension, final String fileModifier, final String itemName, final boolean isClass) {
        final List locations = new ArrayList();
        if (fileExtension == null) {
            fileExtension = "jdo";
        }
        final StringTokenizer tokens = new StringTokenizer(fileExtension, ",");
        while (tokens.hasMoreTokens()) {
            locations.addAll(this.getValidMetaDataLocationsForSingleExtension(tokens.nextToken(), fileModifier, itemName, isClass));
        }
        return locations;
    }
    
    private List getValidMetaDataLocationsForSingleExtension(String fileExtension, final String fileModifier, final String itemName, final boolean isClass) {
        final List locations = new ArrayList();
        String suffix = null;
        if (fileExtension == null) {
            fileExtension = "jdo";
        }
        if (fileModifier != null) {
            suffix = "-" + fileModifier + '.' + fileExtension;
        }
        else {
            suffix = '.' + fileExtension;
        }
        if (this.locationDefinition == 1 || this.locationDefinition == 3) {
            locations.add("/META-INF/package" + suffix);
            locations.add("/WEB-INF/package" + suffix);
            locations.add("/package" + suffix);
        }
        if (itemName != null && itemName.length() > 0) {
            int separatorPosition = itemName.indexOf(46);
            if (separatorPosition < 0) {
                if (this.locationDefinition == 1 || this.locationDefinition == 3) {
                    locations.add('/' + itemName + '/' + "package" + suffix);
                }
                if (this.locationDefinition == 1 || this.locationDefinition == 2) {
                    locations.add('/' + itemName + suffix);
                }
            }
            else {
                while (separatorPosition >= 0) {
                    final String name = itemName.substring(0, separatorPosition);
                    if (this.locationDefinition == 1 || this.locationDefinition == 3) {
                        locations.add('/' + name.replace('.', '/') + '/' + "package" + suffix);
                    }
                    if (this.locationDefinition == 1 || this.locationDefinition == 2) {
                        locations.add('/' + name.replace('.', '/') + suffix);
                    }
                    separatorPosition = itemName.indexOf(46, separatorPosition + 1);
                    if (separatorPosition < 0) {
                        if (!isClass && (this.locationDefinition == 1 || this.locationDefinition == 3)) {
                            locations.add('/' + itemName.replace('.', '/') + '/' + "package" + suffix);
                        }
                        if (this.locationDefinition != 1 && this.locationDefinition != 2) {
                            continue;
                        }
                        locations.add('/' + itemName.replace('.', '/') + suffix);
                    }
                }
            }
        }
        return locations;
    }
    
    private String getORMMappingName() {
        final String mappingName = this.nucleusContext.getPersistenceConfiguration().getStringProperty("datanucleus.mapping");
        return StringUtils.isWhitespace(mappingName) ? null : mappingName;
    }
    
    private String getJDOFileSuffix() {
        final String suffix = this.nucleusContext.getPersistenceConfiguration().getStringProperty("datanucleus.metadata.jdoFileExtension");
        return StringUtils.isWhitespace(suffix) ? "jdo" : suffix;
    }
    
    private String getORMFileSuffix() {
        final String suffix = this.nucleusContext.getPersistenceConfiguration().getStringProperty("datanucleus.metadata.ormFileExtension");
        return StringUtils.isWhitespace(suffix) ? "orm" : suffix;
    }
    
    private String getJDOQueryFileSuffix() {
        final String suffix = this.nucleusContext.getPersistenceConfiguration().getStringProperty("datanucleus.metadata.jdoqueryFileExtension");
        return StringUtils.isWhitespace(suffix) ? "jdoquery" : suffix;
    }
    
    @Override
    public InterfaceMetaData getMetaDataForInterface(final Class c, final ClassLoaderResolver clr) {
        if (c == null || !c.isInterface()) {
            return null;
        }
        final InterfaceMetaData imd = (InterfaceMetaData)this.getMetaDataForClassInternal(c, clr);
        if (imd != null) {
            this.populateAbstractClassMetaData(imd, clr, c.getClassLoader());
            this.initialiseAbstractClassMetaData(imd, clr);
            if (this.utilisedFileMetaData.size() > 0) {
                for (final FileMetaData filemd : this.utilisedFileMetaData) {
                    this.initialiseFileMetaData(filemd, clr, c.getClassLoader());
                }
            }
        }
        this.utilisedFileMetaData.clear();
        return imd;
    }
    
    @Override
    public boolean isPersistentInterface(final String name) {
        final AbstractClassMetaData acmd = this.classMetaDataByClass.get(name);
        return acmd != null && acmd instanceof InterfaceMetaData;
    }
    
    @Override
    public boolean isPersistentInterfaceImplementation(final String interfaceName, final String implName) {
        final ClassMetaData cmd = this.classMetaDataByInterface.get(interfaceName);
        return cmd != null && cmd.getFullClassName().equals(implName);
    }
    
    @Override
    public boolean isPersistentDefinitionImplementation(final String implName) {
        final ClassMetaData cmd = this.classMetaDataByClass.get(implName);
        return cmd != null && cmd.isImplementationOfPersistentDefinition();
    }
    
    @Override
    public String getImplementationNameForPersistentInterface(final String interfaceName) {
        final ClassMetaData cmd = this.classMetaDataByInterface.get(interfaceName);
        return (cmd != null) ? cmd.getFullClassName() : null;
    }
    
    @Override
    public ClassMetaData getClassMetaDataForImplementationOfPersistentInterface(final String interfaceName) {
        return this.classMetaDataByInterface.get(interfaceName);
    }
    
    @Override
    public void registerPersistentInterface(final InterfaceMetaData imd, final Class implClass, final ClassLoaderResolver clr) {
        final ClassMetaData cmd = new ClassMetaData(imd, implClass.getName(), true);
        cmd.addImplements(new ImplementsMetaData(imd.getFullClassName()));
        this.registerMetaDataForClass(cmd.getFullClassName(), cmd);
        this.classMetaDataByInterface.put(imd.getFullClassName(), cmd);
        this.initialiseClassMetaData(cmd, implClass, clr);
        if (NucleusLogger.METADATA.isDebugEnabled()) {
            NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044044", implClass.getName()));
        }
        this.classesWithoutPersistenceInfo.remove(implClass.getName());
    }
    
    @Override
    public void registerImplementationOfAbstractClass(final ClassMetaData cmd, final Class implClass, final ClassLoaderResolver clr) {
        final ClassMetaData implCmd = new ClassMetaData(cmd, implClass.getName());
        this.registerMetaDataForClass(implCmd.getFullClassName(), implCmd);
        this.initialiseClassMetaData(implCmd, implClass, clr);
        if (NucleusLogger.METADATA.isDebugEnabled()) {
            NucleusLogger.METADATA.debug(JDOMetaDataManager.LOCALISER.msg("044044", implClass.getName()));
        }
        this.classesWithoutPersistenceInfo.remove(implClass.getName());
    }
    
    private class MetaDataRegisterClassListener implements RegisterClassListener
    {
        public void registerClass(final RegisterClassEvent ev) {
            NucleusLogger.METADATA.debug("Listener found initialisation for persistable class " + ev.getRegisteredClass().getName());
            JDOMetaDataManager.this.getMetaDataForClassInternal(ev.getRegisteredClass(), JDOMetaDataManager.this.nucleusContext.getClassLoaderResolver(ev.getRegisteredClass().getClassLoader()));
        }
    }
}
