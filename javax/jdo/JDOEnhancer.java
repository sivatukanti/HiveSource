// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import javax.jdo.metadata.JDOMetadata;
import java.util.Properties;
import java.lang.instrument.ClassFileTransformer;

public interface JDOEnhancer extends ClassFileTransformer
{
    Properties getProperties();
    
    JDOEnhancer setVerbose(final boolean p0);
    
    JDOEnhancer setOutputDirectory(final String p0);
    
    JDOEnhancer setClassLoader(final ClassLoader p0);
    
    JDOEnhancer addPersistenceUnit(final String p0);
    
    JDOEnhancer addClass(final String p0, final byte[] p1);
    
    JDOEnhancer addClasses(final String... p0);
    
    JDOEnhancer addFiles(final String... p0);
    
    JDOEnhancer addJar(final String p0);
    
    int enhance();
    
    int validate();
    
    byte[] getEnhancedBytes(final String p0);
    
    void registerMetadata(final JDOMetadata p0);
    
    JDOMetadata newMetadata();
}
