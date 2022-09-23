// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

import java.util.List;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaDataManager;
import java.io.IOException;
import java.util.Collection;

public interface ClassEnhancer
{
    public static final int ASM_API_VERSION = 262144;
    public static final String OPTION_GENERATE_DEFAULT_CONSTRUCTOR = "generate-default-constructor";
    public static final String OPTION_GENERATE_PK = "generate-primary-key";
    public static final String OPTION_GENERATE_DETACH_LISTENER = "generate-detach-listener";
    
    void setOptions(final Collection<String> p0);
    
    boolean hasOption(final String p0);
    
    boolean validate();
    
    boolean enhance();
    
    void save(final String p0) throws IOException;
    
    byte[] getClassBytes();
    
    byte[] getPrimaryKeyClassBytes();
    
    MetaDataManager getMetaDataManager();
    
    ClassLoaderResolver getClassLoaderResolver();
    
    ClassMetaData getClassMetaData();
    
    void setNamer(final EnhancementNamer p0);
    
    EnhancementNamer getNamer();
    
    Class getClassBeingEnhanced();
    
    String getClassName();
    
    String getASMClassName();
    
    String getClassDescriptor();
    
    List<ClassMethod> getMethodsList();
    
    List<ClassField> getFieldsList();
    
    boolean isPersistable(final String p0);
}
