// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.monitor;

import org.apache.derby.iapi.services.timer.TimerFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.services.info.ProductVersionHolder;
import org.apache.derby.iapi.services.stream.InfoStreams;
import org.apache.derby.iapi.services.loader.InstanceGetter;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import java.util.Locale;

public interface ModuleFactory
{
    Object findModule(final Object p0, final String p1, final String p2);
    
    String getServiceName(final Object p0);
    
    Locale getLocale(final Object p0);
    
    Locale getLocaleFromString(final String p0) throws StandardException;
    
    Locale setLocale(final Object p0, final String p1) throws StandardException;
    
    Locale setLocale(final Properties p0, final String p1) throws StandardException;
    
    PersistentService getServiceType(final Object p0);
    
    PersistentService getServiceProvider(final String p0) throws StandardException;
    
    Properties getApplicationProperties();
    
    void shutdown();
    
    void shutdown(final Object p0);
    
    InstanceGetter classFromIdentifier(final int p0) throws StandardException;
    
    Object newInstanceFromIdentifier(final int p0) throws StandardException;
    
    Object getEnvironment();
    
    String[] getServiceList(final String p0);
    
    boolean startPersistentService(final String p0, final Properties p1) throws StandardException;
    
    Object createPersistentService(final String p0, final String p1, final Properties p2) throws StandardException;
    
    void removePersistentService(final String p0) throws StandardException;
    
    Object startNonPersistentService(final String p0, final String p1, final Properties p2) throws StandardException;
    
    String getCanonicalServiceName(final String p0) throws StandardException;
    
    Object findService(final String p0, final String p1);
    
    Object startModule(final boolean p0, final Object p1, final String p2, final String p3, final Properties p4) throws StandardException;
    
    InfoStreams getSystemStreams();
    
    void startServices(final Properties p0, final boolean p1);
    
    String getJVMProperty(final String p0);
    
    Thread getDaemonThread(final Runnable p0, final String p1, final boolean p2);
    
    void setThreadPriority(final int p0);
    
    ProductVersionHolder getEngineVersion();
    
    UUIDFactory getUUIDFactory();
    
    TimerFactory getTimerFactory();
}
