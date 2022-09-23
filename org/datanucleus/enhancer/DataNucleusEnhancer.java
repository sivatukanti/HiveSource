// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

import org.datanucleus.util.ClassUtils;
import java.util.StringTokenizer;
import java.io.File;
import org.datanucleus.util.CommandLine;
import java.io.IOException;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.enhancer.jdo.JPAEnhancementNamer;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.PackageMetaData;
import java.util.Iterator;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.FileMetaData;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.enhancer.jdo.JDOClassEnhancer;
import org.datanucleus.util.StringUtils;
import java.util.HashSet;
import java.util.HashMap;
import org.datanucleus.NucleusContext;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Map;
import java.util.Collection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.Localiser;

public class DataNucleusEnhancer
{
    protected static final Localiser LOCALISER;
    public static final NucleusLogger LOGGER;
    private MetaDataManager metadataMgr;
    private ClassLoaderResolver clr;
    private String apiName;
    private String enhancerVersion;
    private String outputDirectory;
    private boolean verbose;
    private boolean systemOut;
    private boolean generatePK;
    private boolean generateConstructor;
    private boolean detachListener;
    protected ClassLoader userClassLoader;
    private Collection<EnhanceComponent> componentsToEnhance;
    private Map<String, byte[]> bytesForClassesToEnhanceByClassName;
    private Map<String, byte[]> enhancedBytesByClassName;
    private Map<String, byte[]> pkClassBytesByClassName;
    
    public DataNucleusEnhancer(final String apiName, final Properties props) {
        this.apiName = "JDO";
        this.enhancerVersion = null;
        this.outputDirectory = null;
        this.verbose = false;
        this.systemOut = false;
        this.generatePK = true;
        this.generateConstructor = true;
        this.detachListener = false;
        this.userClassLoader = null;
        this.componentsToEnhance = new ArrayList<EnhanceComponent>();
        this.bytesForClassesToEnhanceByClassName = null;
        this.enhancedBytesByClassName = null;
        this.pkClassBytesByClassName = null;
        this.apiName = apiName;
        final NucleusContext nucleusContext = new NucleusContext(apiName, NucleusContext.ContextType.ENHANCEMENT, props);
        if (props != null) {
            nucleusContext.getPersistenceConfiguration().setPersistenceProperties(props);
        }
        this.metadataMgr = nucleusContext.getMetaDataManager();
        this.clr = nucleusContext.getClassLoaderResolver(null);
        this.enhancerVersion = nucleusContext.getPluginManager().getVersionForBundle("org.datanucleus");
    }
    
    private void init() {
    }
    
    public MetaDataManager getMetaDataManager() {
        return this.metadataMgr;
    }
    
    public String getOutputDirectory() {
        return this.outputDirectory;
    }
    
    public DataNucleusEnhancer setOutputDirectory(final String dir) {
        this.resetEnhancement();
        this.outputDirectory = dir;
        return this;
    }
    
    public ClassLoader getClassLoader() {
        return this.userClassLoader;
    }
    
    public DataNucleusEnhancer setClassLoader(final ClassLoader loader) {
        this.resetEnhancement();
        this.userClassLoader = loader;
        if (this.userClassLoader != null) {
            this.clr.registerUserClassLoader(this.userClassLoader);
        }
        return this;
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
    
    public DataNucleusEnhancer setVerbose(final boolean verbose) {
        this.resetEnhancement();
        this.verbose = verbose;
        return this;
    }
    
    public DataNucleusEnhancer setSystemOut(final boolean sysout) {
        this.resetEnhancement();
        this.systemOut = sysout;
        return this;
    }
    
    public DataNucleusEnhancer setGeneratePK(final boolean flag) {
        this.resetEnhancement();
        this.generatePK = flag;
        return this;
    }
    
    public DataNucleusEnhancer setGenerateConstructor(final boolean flag) {
        this.resetEnhancement();
        this.generateConstructor = flag;
        return this;
    }
    
    public DataNucleusEnhancer setDetachListener(final boolean flag) {
        this.resetEnhancement();
        this.detachListener = flag;
        return this;
    }
    
    public DataNucleusEnhancer addClass(final String className, final byte[] bytes) {
        this.init();
        if (className == null) {
            return this;
        }
        if (this.bytesForClassesToEnhanceByClassName == null) {
            this.bytesForClassesToEnhanceByClassName = new HashMap<String, byte[]>();
        }
        this.bytesForClassesToEnhanceByClassName.put(className, bytes);
        this.componentsToEnhance.add(new EnhanceComponent(0, className));
        return this;
    }
    
    public DataNucleusEnhancer addClasses(final String... classNames) {
        this.init();
        if (classNames == null) {
            return this;
        }
        final Collection names = new HashSet();
        for (int i = 0; i < classNames.length; ++i) {
            if (classNames[i].endsWith(".class")) {
                String name = classNames[i];
                String msg = null;
                if (!StringUtils.getFileForFilename(classNames[i]).exists()) {
                    msg = DataNucleusEnhancer.LOCALISER.msg("Enhancer.InputFiles.Invalid", classNames[i]);
                    this.addMessage(msg, true);
                    name = null;
                }
                else {
                    name = JDOClassEnhancer.getClassNameForFileName(classNames[i]);
                }
                if (name != null) {
                    names.add(name);
                }
            }
            else {
                try {
                    this.clr.classForName(classNames[i], false);
                }
                catch (ClassNotResolvedException cnre) {
                    this.addMessage("Class " + classNames[i] + " not found in CLASSPATH! : " + cnre.getMessage(), true);
                }
                names.add(classNames[i]);
            }
        }
        if (names.size() > 0) {
            this.componentsToEnhance.add(new EnhanceComponent(0, names.toArray(new String[names.size()])));
        }
        return this;
    }
    
    public DataNucleusEnhancer addFiles(final String... filenames) {
        this.init();
        if (filenames == null) {
            return this;
        }
        final Collection<String> classFiles = new ArrayList<String>();
        final Collection<String> mappingFiles = new ArrayList<String>();
        final Collection<String> jarFiles = new HashSet<String>();
        for (int i = 0; i < filenames.length; ++i) {
            if (filenames[i].endsWith(".class")) {
                classFiles.add(filenames[i]);
            }
            else if (filenames[i].endsWith(".jar")) {
                jarFiles.add(filenames[i]);
            }
            else {
                mappingFiles.add(filenames[i]);
            }
        }
        if (mappingFiles.size() > 0) {
            this.componentsToEnhance.add(new EnhanceComponent(2, mappingFiles.toArray(new String[mappingFiles.size()])));
        }
        if (jarFiles.size() > 0) {
            this.componentsToEnhance.add(new EnhanceComponent(3, jarFiles.toArray(new String[jarFiles.size()])));
        }
        if (classFiles.size() > 0) {
            this.componentsToEnhance.add(new EnhanceComponent(1, classFiles.toArray(new String[classFiles.size()])));
        }
        return this;
    }
    
    public DataNucleusEnhancer addJar(final String jarFileName) {
        this.init();
        if (jarFileName == null) {
            return this;
        }
        this.componentsToEnhance.add(new EnhanceComponent(3, jarFileName));
        return this;
    }
    
    public DataNucleusEnhancer addPersistenceUnit(final String persistenceUnitName) {
        this.init();
        if (persistenceUnitName == null) {
            return this;
        }
        this.componentsToEnhance.add(new EnhanceComponent(4, persistenceUnitName));
        return this;
    }
    
    public int enhance() {
        if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
            DataNucleusEnhancer.LOGGER.debug("Enhancing classes for JRE=" + JavaUtils.getJREMajorVersion() + "." + JavaUtils.getJREMinorVersion() + " with stackmap-frames=" + JavaUtils.useStackMapFrames());
        }
        this.init();
        if (this.componentsToEnhance.isEmpty()) {
            return 0;
        }
        final long startTime = System.currentTimeMillis();
        final Collection<FileMetaData> fileMetaData = this.getFileMetadataForInput();
        final long inputTime = System.currentTimeMillis();
        final HashSet<String> classNames = new HashSet<String>();
        final Iterator<FileMetaData> filemdIter = fileMetaData.iterator();
        boolean success = true;
        while (filemdIter.hasNext()) {
            final FileMetaData filemd = filemdIter.next();
            for (int packagenum = 0; packagenum < filemd.getNoOfPackages(); ++packagenum) {
                final PackageMetaData pmd = filemd.getPackage(packagenum);
                for (int classnum = 0; classnum < pmd.getNoOfClasses(); ++classnum) {
                    final ClassMetaData cmd = pmd.getClass(classnum);
                    if (!classNames.contains(cmd.getFullClassName())) {
                        classNames.add(cmd.getFullClassName());
                        final byte[] bytes = (byte[])((this.bytesForClassesToEnhanceByClassName != null) ? ((byte[])this.bytesForClassesToEnhanceByClassName.get(cmd.getFullClassName())) : null);
                        final ClassEnhancer classEnhancer = this.getClassEnhancer(cmd, bytes);
                        final boolean clsSuccess = this.enhanceClass(cmd, classEnhancer, bytes == null);
                        if (!clsSuccess) {
                            success = false;
                        }
                    }
                }
            }
        }
        if (!success) {
            throw new NucleusException("Failure during enhancement of classes - see the log for details");
        }
        final long enhanceTime = System.currentTimeMillis();
        String msg = null;
        if (this.verbose) {
            msg = DataNucleusEnhancer.LOCALISER.msg("Enhancer.Success", classNames.size(), "" + (inputTime - startTime), "" + (enhanceTime - inputTime), "" + (enhanceTime - startTime));
        }
        else {
            msg = DataNucleusEnhancer.LOCALISER.msg("Enhancer.Success.Simple", classNames.size());
        }
        this.addMessage(msg, false);
        if (this.bytesForClassesToEnhanceByClassName != null) {
            this.bytesForClassesToEnhanceByClassName.clear();
            this.bytesForClassesToEnhanceByClassName = null;
        }
        this.componentsToEnhance.clear();
        return classNames.size();
    }
    
    public int validate() {
        this.init();
        if (this.componentsToEnhance.isEmpty()) {
            return 0;
        }
        final long startTime = System.currentTimeMillis();
        final Collection<FileMetaData> fileMetaData = this.getFileMetadataForInput();
        final long inputTime = System.currentTimeMillis();
        final HashSet<String> classNames = new HashSet<String>();
        for (final FileMetaData filemd : fileMetaData) {
            for (int packagenum = 0; packagenum < filemd.getNoOfPackages(); ++packagenum) {
                final PackageMetaData pmd = filemd.getPackage(packagenum);
                for (int classnum = 0; classnum < pmd.getNoOfClasses(); ++classnum) {
                    final ClassMetaData cmd = pmd.getClass(classnum);
                    if (!classNames.contains(cmd.getFullClassName())) {
                        classNames.add(cmd.getFullClassName());
                        final byte[] bytes = (byte[])((this.bytesForClassesToEnhanceByClassName != null) ? ((byte[])this.bytesForClassesToEnhanceByClassName.get(cmd.getFullClassName())) : null);
                        final ClassEnhancer classEnhancer = this.getClassEnhancer(cmd, bytes);
                        this.validateClass(cmd, classEnhancer);
                    }
                }
            }
        }
        final long enhanceTime = System.currentTimeMillis();
        String msg = null;
        if (this.verbose) {
            msg = DataNucleusEnhancer.LOCALISER.msg("Enhancer.Success", classNames.size(), "" + (inputTime - startTime), "" + (enhanceTime - inputTime), "" + (enhanceTime - startTime));
        }
        else {
            msg = DataNucleusEnhancer.LOCALISER.msg("Enhancer.Success.Simple", classNames.size());
        }
        this.addMessage(msg, false);
        if (this.bytesForClassesToEnhanceByClassName != null) {
            this.bytesForClassesToEnhanceByClassName.clear();
            this.bytesForClassesToEnhanceByClassName = null;
        }
        this.componentsToEnhance.clear();
        return classNames.size();
    }
    
    protected Collection<FileMetaData> getFileMetadataForInput() {
        final Iterator<EnhanceComponent> iter = this.componentsToEnhance.iterator();
        final Collection<FileMetaData> fileMetaData = new ArrayList<FileMetaData>();
        while (iter.hasNext()) {
            final EnhanceComponent comp = iter.next();
            FileMetaData[] filemds = null;
            switch (comp.getType()) {
                case 0: {
                    if (comp.getValue() instanceof String) {
                        final String className = (String)comp.getValue();
                        if (this.bytesForClassesToEnhanceByClassName != null && this.bytesForClassesToEnhanceByClassName.get(className) != null) {
                            final AbstractClassMetaData cmd = this.metadataMgr.getMetaDataForClass(className, this.clr);
                            if (cmd != null) {
                                filemds = new FileMetaData[] { cmd.getPackageMetaData().getFileMetaData() };
                            }
                        }
                        else {
                            filemds = this.metadataMgr.loadClasses(new String[] { (String)comp.getValue() }, this.userClassLoader);
                        }
                        break;
                    }
                    filemds = this.metadataMgr.loadClasses((String[])comp.getValue(), this.userClassLoader);
                    break;
                }
                case 1: {
                    if (comp.getValue() instanceof String) {
                        String className = null;
                        final String classFilename = (String)comp.getValue();
                        if (!StringUtils.getFileForFilename(classFilename).exists()) {
                            final String msg = DataNucleusEnhancer.LOCALISER.msg("Enhancer.InputFiles.Invalid", classFilename);
                            this.addMessage(msg, true);
                        }
                        else {
                            className = JDOClassEnhancer.getClassNameForFileName(classFilename);
                        }
                        if (className != null) {
                            filemds = this.metadataMgr.loadClasses(new String[] { className }, this.userClassLoader);
                        }
                        break;
                    }
                    final Collection<String> classNames = new ArrayList<String>();
                    final String[] classFilenames = (String[])comp.getValue();
                    for (int i = 0; i < classFilenames.length; ++i) {
                        String className2 = null;
                        if (!StringUtils.getFileForFilename(classFilenames[i]).exists()) {
                            final String msg2 = DataNucleusEnhancer.LOCALISER.msg("Enhancer.InputFiles.Invalid", classFilenames[i]);
                            this.addMessage(msg2, true);
                        }
                        else {
                            className2 = JDOClassEnhancer.getClassNameForFileName(classFilenames[i]);
                        }
                        if (className2 != null) {
                            classNames.add(className2);
                        }
                    }
                    filemds = this.metadataMgr.loadClasses(classNames.toArray(new String[classNames.size()]), this.userClassLoader);
                    break;
                }
                case 2: {
                    if (comp.getValue() instanceof String) {
                        filemds = this.metadataMgr.loadMetadataFiles(new String[] { (String)comp.getValue() }, this.userClassLoader);
                        break;
                    }
                    filemds = this.metadataMgr.loadMetadataFiles((String[])comp.getValue(), this.userClassLoader);
                    break;
                }
                case 3: {
                    if (comp.getValue() instanceof String) {
                        filemds = this.metadataMgr.loadJar((String)comp.getValue(), this.userClassLoader);
                        break;
                    }
                    final String[] jarFilenames = (String[])comp.getValue();
                    final Collection<FileMetaData> filemdsColl = new HashSet<FileMetaData>();
                    for (int i = 0; i < jarFilenames.length; ++i) {
                        final FileMetaData[] fmds = this.metadataMgr.loadJar(jarFilenames[i], this.userClassLoader);
                        for (int j = 0; j < fmds.length; ++j) {
                            filemdsColl.add(fmds[j]);
                        }
                    }
                    filemds = filemdsColl.toArray(new FileMetaData[filemdsColl.size()]);
                    break;
                }
                case 4: {
                    PersistenceUnitMetaData pumd = null;
                    try {
                        pumd = this.metadataMgr.getMetaDataForPersistenceUnit((String)comp.getValue());
                    }
                    catch (NucleusException ne) {
                        throw new NucleusEnhanceException(DataNucleusEnhancer.LOCALISER.msg("Enhancer.PersistenceUnit.NoPersistenceFiles", comp.getValue()));
                    }
                    if (pumd == null) {
                        throw new NucleusEnhanceException(DataNucleusEnhancer.LOCALISER.msg("Enhancer.PersistenceUnit.NoSuchUnit", comp.getValue()));
                    }
                    filemds = this.metadataMgr.loadPersistenceUnit(pumd, this.userClassLoader);
                    break;
                }
            }
            if (filemds != null) {
                for (int k = 0; k < filemds.length; ++k) {
                    fileMetaData.add(filemds[k]);
                }
            }
        }
        return fileMetaData;
    }
    
    public byte[] getEnhancedBytes(final String className) {
        if (this.enhancedBytesByClassName != null) {
            final byte[] bytes = this.enhancedBytesByClassName.get(className);
            if (bytes != null) {
                return bytes;
            }
        }
        throw new NucleusException("No enhanced bytes available for " + className);
    }
    
    public byte[] getPkClassBytes(final String className) {
        if (this.pkClassBytesByClassName != null) {
            final byte[] bytes = this.pkClassBytesByClassName.get(className);
            if (bytes != null) {
                return bytes;
            }
        }
        throw new NucleusException("No pk class bytes available for " + className);
    }
    
    protected void resetEnhancement() {
        if (this.enhancedBytesByClassName != null) {
            this.enhancedBytesByClassName.clear();
            this.enhancedBytesByClassName = null;
        }
        if (this.pkClassBytesByClassName != null) {
            this.pkClassBytesByClassName.clear();
            this.pkClassBytesByClassName = null;
        }
    }
    
    protected ClassEnhancer getClassEnhancer(final ClassMetaData cmd, final byte[] bytes) {
        ClassEnhancer classEnhancer = null;
        if (bytes != null) {
            classEnhancer = new JDOClassEnhancer(cmd, this.clr, this.metadataMgr, bytes);
        }
        else {
            classEnhancer = new JDOClassEnhancer(cmd, this.clr, this.metadataMgr);
        }
        if (this.apiName.equalsIgnoreCase("jpa")) {
            classEnhancer.setNamer(JPAEnhancementNamer.getInstance());
        }
        final Collection<String> options = new HashSet<String>();
        if (this.generatePK) {
            options.add("generate-primary-key");
        }
        if (this.generateConstructor) {
            options.add("generate-default-constructor");
        }
        if (this.detachListener) {
            options.add("generate-detach-listener");
        }
        classEnhancer.setOptions(options);
        return classEnhancer;
    }
    
    protected void addMessage(final String msg, final boolean error) {
        if (error) {
            DataNucleusEnhancer.LOGGER.error(msg);
        }
        else {
            DataNucleusEnhancer.LOGGER.info(msg);
        }
        if (this.systemOut) {
            System.out.println(msg);
        }
    }
    
    protected boolean enhanceClass(final ClassMetaData cmd, final ClassEnhancer enhancer, final boolean store) {
        boolean success = true;
        try {
            if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
                DataNucleusEnhancer.LOGGER.debug(DataNucleusEnhancer.LOCALISER.msg("Enhancer.EnhanceClassStart", cmd.getFullClassName()));
            }
            final boolean enhanced = enhancer.enhance();
            if (enhanced) {
                if (this.enhancedBytesByClassName == null) {
                    this.enhancedBytesByClassName = new HashMap<String, byte[]>();
                }
                this.enhancedBytesByClassName.put(cmd.getFullClassName(), enhancer.getClassBytes());
                final byte[] pkClassBytes = enhancer.getPrimaryKeyClassBytes();
                if (pkClassBytes != null) {
                    if (this.pkClassBytesByClassName == null) {
                        this.pkClassBytesByClassName = new HashMap<String, byte[]>();
                    }
                    this.pkClassBytesByClassName.put(cmd.getFullClassName(), pkClassBytes);
                }
                if (store) {
                    enhancer.save(this.outputDirectory);
                }
                if (this.isVerbose()) {
                    if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                        this.addMessage("ENHANCED (PersistenceCapable) : " + cmd.getFullClassName(), false);
                    }
                    else if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_AWARE) {
                        this.addMessage("ENHANCED (PersistenceAware) : " + cmd.getFullClassName(), false);
                    }
                    else {
                        this.addMessage("NOT ENHANCED (NonPersistent) : " + cmd.getFullClassName(), false);
                    }
                }
            }
            else {
                if (this.isVerbose()) {
                    if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                        this.addMessage("ERROR (PersistenceCapable) : " + cmd.getFullClassName(), false);
                    }
                    else if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_AWARE) {
                        this.addMessage("ERROR (PersistenceAware) : " + cmd.getFullClassName(), false);
                    }
                    else {
                        this.addMessage("NOT ENHANCED (NonPersistent) : " + cmd.getFullClassName(), false);
                    }
                }
                if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE || cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_AWARE) {
                    success = false;
                }
            }
        }
        catch (IOException ioe) {
            if (this.isVerbose()) {
                this.addMessage("ERROR (NonPersistent) : " + cmd.getFullClassName(), false);
            }
            final String msg = DataNucleusEnhancer.LOCALISER.msg("Enhancer.ErrorEnhancingClass", cmd.getFullClassName(), ioe.getMessage());
            DataNucleusEnhancer.LOGGER.error(msg, ioe);
            System.out.println(msg);
            success = false;
        }
        if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
            DataNucleusEnhancer.LOGGER.debug(DataNucleusEnhancer.LOCALISER.msg("Enhancer.EnhanceClassEnd", cmd.getFullClassName()));
        }
        return success;
    }
    
    protected boolean validateClass(final ClassMetaData cmd, final ClassEnhancer enhancer) {
        if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
            DataNucleusEnhancer.LOGGER.debug(DataNucleusEnhancer.LOCALISER.msg("Enhancer.ValidateClassStart", cmd.getFullClassName()));
        }
        final boolean enhanced = enhancer.validate();
        if (enhanced) {
            if (this.isVerbose()) {
                if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                    this.addMessage("ENHANCED (PersistenceCapable) : " + cmd.getFullClassName(), false);
                }
                else if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_AWARE) {
                    this.addMessage("ENHANCED (PersistenceAware) : " + cmd.getFullClassName(), false);
                }
                else {
                    this.addMessage("NOT ENHANCED (NonPersistent) : " + cmd.getFullClassName(), false);
                }
            }
        }
        else if (this.isVerbose()) {
            if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                this.addMessage("NOT ENHANCED (PersistenceCapable) : " + cmd.getFullClassName(), false);
            }
            else if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_AWARE) {
                this.addMessage("NOT ENHANCED (PersistenceAware) : " + cmd.getFullClassName(), false);
            }
            else {
                this.addMessage("NOT ENHANCED (NonPersistent) : " + cmd.getFullClassName(), false);
            }
        }
        if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
            DataNucleusEnhancer.LOGGER.debug(DataNucleusEnhancer.LOCALISER.msg("Enhancer.ValidateClassEnd", cmd.getFullClassName()));
        }
        return true;
    }
    
    public Properties getProperties() {
        final Properties props = new Properties();
        props.setProperty("VendorName", "DataNucleus");
        props.setProperty("VersionNumber", this.enhancerVersion);
        props.setProperty("API", this.apiName);
        return props;
    }
    
    public static void main(final String[] args) throws Exception {
        final CommandLine cl = new CommandLine();
        cl.addOption("pu", "persistenceUnit", "<name-of-persistence-unit>", "name of the persistence unit to enhance");
        cl.addOption("dir", "directory", "<name-of-directory>", "name of the directory containing things to enhance");
        cl.addOption("d", "dest", "<directory>", "output directory");
        cl.addOption("checkonly", "checkonly", null, "only check if the class is enhanced");
        cl.addOption("q", "quiet", null, "no output");
        cl.addOption("v", "verbose", null, "verbose output");
        cl.addOption("api", "api", "<api-name>", "API Name (JDO, JPA, etc)");
        cl.addOption("alwaysDetachable", "alwaysDetachable", null, "Always detachable?");
        cl.addOption("generatePK", "generatePK", "<generate-pk>", "Generate PK class where needed?");
        cl.addOption("generateConstructor", "generateConstructor", "<generate-constructor>", "Generate default constructor where needed?");
        cl.addOption("detachListener", "detachListener", "<detach-listener>", "Use Detach Listener?");
        cl.parse(args);
        final String apiName = cl.hasOption("api") ? cl.getOptionArg("api") : "JDO";
        final Properties props = new Properties();
        props.setProperty("datanucleus.plugin.allowUserBundles", "true");
        if (cl.hasOption("alwaysDetachable")) {
            props.setProperty("datanucleus.metadata.alwaysDetachable", "true");
        }
        final DataNucleusEnhancer enhancer = new DataNucleusEnhancer(apiName, props);
        boolean quiet = false;
        if (cl.hasOption("q")) {
            quiet = true;
        }
        else if (cl.hasOption("v")) {
            enhancer.setVerbose(true);
        }
        if (!quiet) {
            enhancer.setSystemOut(true);
        }
        String msg = DataNucleusEnhancer.LOCALISER.msg("Enhancer.ClassEnhancer", enhancer.enhancerVersion, apiName, JavaUtils.getJREMajorVersion() + "." + JavaUtils.getJREMinorVersion());
        DataNucleusEnhancer.LOGGER.info(msg);
        if (!quiet) {
            System.out.println(msg);
        }
        if (cl.hasOption("d")) {
            final String destination = cl.getOptionArg("d");
            final File tmp = new File(destination);
            if (tmp.exists()) {
                if (!tmp.isDirectory()) {
                    System.out.println(destination + " is not directory. please set directory.");
                    System.exit(1);
                }
            }
            else {
                tmp.mkdirs();
            }
            enhancer.setOutputDirectory(destination);
        }
        if (cl.hasOption("generateConstructor")) {
            final String val = cl.getOptionArg("generateConstructor");
            if (val.equalsIgnoreCase("false")) {
                enhancer.setGenerateConstructor(false);
            }
        }
        if (cl.hasOption("generatePK")) {
            final String val = cl.getOptionArg("generatePK");
            if (val.equalsIgnoreCase("false")) {
                enhancer.setGeneratePK(false);
            }
        }
        if (cl.hasOption("detachListener")) {
            final String val = cl.getOptionArg("detachListener");
            if (val.equalsIgnoreCase("true")) {
                enhancer.setDetachListener(true);
            }
        }
        final boolean validating = cl.hasOption("checkonly");
        DataNucleusEnhancer.LOGGER.debug(DataNucleusEnhancer.LOCALISER.msg("Enhancer.Classpath"));
        if (enhancer.isVerbose()) {
            System.out.println(DataNucleusEnhancer.LOCALISER.msg("Enhancer.Classpath"));
        }
        final StringTokenizer tokeniser = new StringTokenizer(System.getProperty("java.class.path"), File.pathSeparator);
        while (tokeniser.hasMoreTokens()) {
            final String entry = DataNucleusEnhancer.LOCALISER.msg("Enhancer.Classpath.Entry", tokeniser.nextToken());
            if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
                DataNucleusEnhancer.LOGGER.debug(entry);
            }
            if (enhancer.isVerbose()) {
                System.out.println(entry);
            }
        }
        if (enhancer.isVerbose()) {
            System.out.flush();
        }
        final String persistenceUnitName = cl.hasOption("pu") ? cl.getOptionArg("pu") : null;
        final String directoryName = cl.hasOption("dir") ? cl.getOptionArg("dir") : null;
        final String[] filenames = cl.getDefaultArgs();
        int numClasses = 0;
        try {
            if (persistenceUnitName != null) {
                enhancer.addPersistenceUnit(persistenceUnitName);
            }
            else if (directoryName != null) {
                final File dir = new File(directoryName);
                if (!dir.exists()) {
                    System.out.println(directoryName + " is not a directory. please set this as a directory");
                    System.exit(1);
                }
                final Collection<File> files = ClassUtils.getFilesForDirectory(dir);
                int i = 0;
                final String[] fileNames = new String[files.size()];
                for (final File file : files) {
                    fileNames[i++] = file.getPath();
                }
                enhancer.addFiles(fileNames);
            }
            else {
                enhancer.addFiles(filenames);
            }
            if (validating) {
                numClasses = enhancer.validate();
            }
            else {
                numClasses = enhancer.enhance();
            }
        }
        catch (NucleusException jpe) {
            System.out.println(jpe.getMessage());
            msg = DataNucleusEnhancer.LOCALISER.msg("Enhancer.Failure");
            DataNucleusEnhancer.LOGGER.error(msg, jpe);
            if (!quiet) {
                System.out.println(msg);
            }
            System.exit(1);
        }
        if (numClasses == 0) {
            msg = DataNucleusEnhancer.LOCALISER.msg("Enhancer.NoClassesEnhanced");
            DataNucleusEnhancer.LOGGER.info(msg);
            if (!quiet) {
                System.out.println(msg);
            }
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassEnhancer.class.getClassLoader());
        LOGGER = NucleusLogger.getLoggerInstance("DataNucleus.Enhancer");
    }
    
    static class EnhanceComponent
    {
        public static final int CLASS = 0;
        public static final int CLASS_FILE = 1;
        public static final int MAPPING_FILE = 2;
        public static final int JAR_FILE = 3;
        public static final int PERSISTENCE_UNIT = 4;
        int type;
        Object value;
        
        public EnhanceComponent(final int type, final Object value) {
            this.type = type;
            this.value = value;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public int getType() {
            return this.type;
        }
    }
}
