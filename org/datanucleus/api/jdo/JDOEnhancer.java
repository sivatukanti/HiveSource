// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.FileMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.api.jdo.metadata.JDOMetadataImpl;
import javax.jdo.metadata.JDOMetadata;
import java.util.Properties;
import org.datanucleus.enhancer.DataNucleusEnhancer;

public class JDOEnhancer implements javax.jdo.JDOEnhancer
{
    DataNucleusEnhancer enhancer;
    
    public JDOEnhancer() {
        this.enhancer = new DataNucleusEnhancer("JDO", null);
    }
    
    public JDOEnhancer(final Properties props) {
        this.enhancer = new DataNucleusEnhancer("JDO", props);
    }
    
    public JDOMetadata newMetadata() {
        return new JDOMetadataImpl();
    }
    
    public void registerMetadata(final JDOMetadata metadata) {
        final MetaDataManager mmgr = this.enhancer.getMetaDataManager();
        final FileMetaData filemd = ((JDOMetadataImpl)metadata).getInternal();
        mmgr.loadUserMetaData(filemd, this.enhancer.getClassLoader());
    }
    
    public JDOEnhancer addClass(final String className, final byte[] bytes) {
        this.enhancer.addClass(className, bytes);
        return this;
    }
    
    public JDOEnhancer addClasses(final String... classNames) {
        this.enhancer.addClasses(classNames);
        return this;
    }
    
    public JDOEnhancer addFiles(final String... metadataFiles) {
        this.enhancer.addFiles(metadataFiles);
        return this;
    }
    
    public JDOEnhancer addJar(final String jarFileName) {
        this.enhancer.addJar(jarFileName);
        return this;
    }
    
    public JDOEnhancer addPersistenceUnit(final String persistenceUnitName) {
        this.enhancer.addPersistenceUnit(persistenceUnitName);
        return this;
    }
    
    public int enhance() {
        try {
            return this.enhancer.enhance();
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public byte[] getEnhancedBytes(final String className) {
        try {
            return this.enhancer.getEnhancedBytes(className);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public byte[] getPkClassBytes(final String className) {
        try {
            return this.enhancer.getPkClassBytes(className);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public Properties getProperties() {
        return this.enhancer.getProperties();
    }
    
    public JDOEnhancer setClassLoader(final ClassLoader loader) {
        this.enhancer.setClassLoader(loader);
        return this;
    }
    
    public JDOEnhancer setOutputDirectory(final String dir) {
        this.enhancer.setOutputDirectory(dir);
        return this;
    }
    
    public JDOEnhancer setVerbose(final boolean verbose) {
        this.enhancer.setVerbose(verbose);
        return this;
    }
    
    public int validate() {
        try {
            return this.enhancer.validate();
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
        return null;
    }
}
