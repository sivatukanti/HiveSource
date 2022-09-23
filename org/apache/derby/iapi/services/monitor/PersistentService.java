// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.monitor;

import java.io.IOException;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.io.StorageFactory;
import java.util.Enumeration;

public interface PersistentService
{
    public static final String DIRECTORY = "directory";
    public static final String CLASSPATH = "classpath";
    public static final String JAR = "jar";
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String INMEMORY = "memory";
    public static final String DB_README_FILE_NAME = "README_DO_NOT_TOUCH_FILES.txt";
    public static final String PROPERTIES_NAME = "service.properties";
    public static final String ROOT = "derby.__rt.serviceDirectory";
    public static final String TYPE = "derby.__rt.serviceType";
    
    String getType();
    
    Enumeration getBootTimeServices();
    
    void createDataWarningFile(final StorageFactory p0) throws StandardException;
    
    Properties getServiceProperties(final String p0, final Properties p1) throws StandardException;
    
    void saveServiceProperties(final String p0, final StorageFactory p1, final Properties p2, final boolean p3) throws StandardException;
    
    void saveServiceProperties(final String p0, final Properties p1) throws StandardException;
    
    String createServiceRoot(final String p0, final boolean p1) throws StandardException;
    
    boolean removeServiceRoot(final String p0);
    
    String getCanonicalServiceName(final String p0) throws StandardException;
    
    String getUserServiceName(final String p0);
    
    boolean isSameService(final String p0, final String p1);
    
    boolean hasStorageFactory();
    
    StorageFactory getStorageFactoryInstance(final boolean p0, final String p1, final String p2, final String p3) throws StandardException, IOException;
}
