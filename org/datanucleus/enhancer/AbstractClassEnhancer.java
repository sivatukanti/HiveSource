// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

import org.datanucleus.ClassConstants;
import java.io.IOException;
import java.net.URL;
import java.io.FileOutputStream;
import org.datanucleus.util.StringUtils;
import java.io.File;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.NucleusContext;
import org.datanucleus.metadata.ClassPersistenceModifier;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.Localiser;

public abstract class AbstractClassEnhancer implements ClassEnhancer
{
    protected static final Localiser LOCALISER;
    protected final ClassLoaderResolver clr;
    protected final MetaDataManager metaDataMgr;
    protected final ClassMetaData cmd;
    public final String className;
    protected boolean update;
    protected List<ClassField> fieldsToAdd;
    protected List<ClassMethod> methodsToAdd;
    protected boolean initialised;
    protected Collection<String> options;
    
    public AbstractClassEnhancer(final ClassMetaData cmd, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        this.update = false;
        this.fieldsToAdd = new ArrayList<ClassField>();
        this.methodsToAdd = new ArrayList<ClassMethod>();
        this.initialised = false;
        this.options = new HashSet<String>();
        this.clr = clr;
        this.cmd = cmd;
        this.className = cmd.getFullClassName();
        this.metaDataMgr = mmgr;
    }
    
    protected void initialise() {
        if (this.initialised) {
            return;
        }
        this.initialiseFieldsList();
        this.initialiseMethodsList();
        this.initialised = true;
    }
    
    @Override
    public String getClassName() {
        return this.className;
    }
    
    protected abstract void initialiseMethodsList();
    
    protected abstract void initialiseFieldsList();
    
    @Override
    public List<ClassMethod> getMethodsList() {
        return this.methodsToAdd;
    }
    
    @Override
    public List<ClassField> getFieldsList() {
        return this.fieldsToAdd;
    }
    
    @Override
    public ClassLoaderResolver getClassLoaderResolver() {
        return this.clr;
    }
    
    @Override
    public MetaDataManager getMetaDataManager() {
        return this.metaDataMgr;
    }
    
    @Override
    public ClassMetaData getClassMetaData() {
        return this.cmd;
    }
    
    protected boolean requiresDetachable() {
        final boolean isDetachable = this.cmd.isDetachable();
        final boolean hasPcsc = this.cmd.getPersistenceCapableSuperclass() != null;
        return (!hasPcsc && isDetachable) || (hasPcsc && !this.cmd.getSuperAbstractClassMetaData().isDetachable() && isDetachable);
    }
    
    @Override
    public boolean isPersistable(final String className) {
        if (className.equals(this.className) && this.cmd.getPersistenceModifier() != ClassPersistenceModifier.PERSISTENCE_AWARE) {
            return true;
        }
        final NucleusContext nucleusCtx = this.metaDataMgr.getNucleusContext();
        final Class cls = this.clr.classForName(className, new EnhancerClassLoader(this.clr));
        if (nucleusCtx.getApiAdapter().isPersistable(cls)) {
            return true;
        }
        final AbstractClassMetaData cmd = this.metaDataMgr.getMetaDataForClass(cls, this.clr);
        return cmd != null && cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE;
    }
    
    @Override
    public void setOptions(final Collection<String> options) {
        if (options == null || options.isEmpty()) {
            return;
        }
        this.options.addAll(options);
    }
    
    @Override
    public boolean hasOption(final String name) {
        return this.options.contains(name);
    }
    
    @Override
    public void save(final String directoryName) throws IOException {
        if (!this.update) {
            return;
        }
        File classFile = null;
        File pkClassFile = null;
        if (directoryName != null) {
            final File baseDir = new File(directoryName);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
            else if (!baseDir.isDirectory()) {
                throw new RuntimeException("Target directory " + directoryName + " is not a directory");
            }
            final String sep = System.getProperty("file.separator");
            final String baseName = this.cmd.getFullClassName().replace('.', sep.charAt(0));
            classFile = new File(directoryName, baseName + ".class");
            classFile.getParentFile().mkdirs();
            if (this.getPrimaryKeyClassBytes() != null) {
                pkClassFile = new File(directoryName, baseName + "_PK" + ".class");
            }
        }
        else {
            final String baseName2 = this.className.replace('.', '/');
            final URL classURL = this.clr.getResource(baseName2 + ".class", null);
            final URL convertedPath = this.metaDataMgr.getNucleusContext().getPluginManager().resolveURLAsFileURL(classURL);
            final String classFilename = convertedPath.getFile();
            classFile = StringUtils.getFileForFilename(classFilename);
            final String pkClassFilename = classFilename.substring(0, classFilename.length() - 6) + "_PK" + ".class";
            pkClassFile = StringUtils.getFileForFilename(pkClassFilename);
        }
        FileOutputStream out = null;
        try {
            DataNucleusEnhancer.LOGGER.info(AbstractClassEnhancer.LOCALISER.msg("Enhancer.WriteClass", classFile));
            out = new FileOutputStream(classFile);
            out.write(this.getClassBytes());
        }
        finally {
            try {
                out.close();
                out = null;
            }
            catch (Exception ex) {}
        }
        final byte[] pkClassBytes = this.getPrimaryKeyClassBytes();
        if (pkClassBytes != null) {
            try {
                DataNucleusEnhancer.LOGGER.info(AbstractClassEnhancer.LOCALISER.msg("Enhancer.WritePrimaryKeyClass", pkClassFile));
                out = new FileOutputStream(pkClassFile);
                out.write(pkClassBytes);
            }
            finally {
                try {
                    out.close();
                    out = null;
                }
                catch (Exception ex2) {}
            }
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
