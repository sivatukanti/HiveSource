// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.monitor;

import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.Collections;
import org.apache.derby.iapi.error.ShutdownException;
import java.util.ResourceBundle;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import org.apache.derby.io.StorageFactory;
import java.util.Iterator;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;
import org.apache.derby.iapi.services.info.JVMInfo;
import java.util.Enumeration;
import org.apache.derby.iapi.services.stream.PrintWriterGetHeader;
import org.apache.derby.iapi.error.ErrorStringBuilder;
import java.lang.reflect.InvocationTargetException;
import org.apache.derby.iapi.services.io.FormatIdUtil;
import org.apache.derby.iapi.services.loader.ClassInfo;
import org.apache.derby.iapi.services.io.FormatableInstanceGetter;
import org.apache.derby.iapi.services.io.RegisteredFormatIds;
import java.util.Locale;
import org.apache.derby.iapi.services.monitor.PersistentService;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.context.Context;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Date;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.io.AccessibleByteArrayOutputStream;
import org.apache.derby.iapi.services.loader.InstanceGetter;
import java.io.PrintWriter;
import org.apache.derby.iapi.services.timer.TimerFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.stream.InfoStreams;
import java.util.Properties;
import java.util.Vector;
import java.util.HashMap;
import org.apache.derby.iapi.services.i18n.BundleFinder;
import org.apache.derby.iapi.services.monitor.ModuleFactory;

abstract class BaseMonitor implements ModuleFactory, BundleFinder
{
    private HashMap serviceProviders;
    private static final String LINE = "----------------------------------------------------------------";
    Vector[] implementationSets;
    private Vector services;
    Properties bootProperties;
    Properties applicationProperties;
    boolean inShutdown;
    private InfoStreams systemStreams;
    private ContextService contextService;
    private UUIDFactory uuidFactory;
    private TimerFactory timerFactory;
    boolean reportOn;
    private PrintWriter logging;
    ThreadGroup daemonGroup;
    private InstanceGetter[] rc2;
    private Boolean exceptionTrace;
    private static final String SERVICE = "derby.service.";
    private static final HashMap storageFactories;
    private PrintWriter tmpWriter;
    private AccessibleByteArrayOutputStream tmpArray;
    private boolean dumpedTempWriter;
    
    BaseMonitor() {
        this.serviceProviders = new HashMap();
        (this.services = new Vector(0, 1)).add(new TopService(this));
    }
    
    public InfoStreams getSystemStreams() {
        return this.systemStreams;
    }
    
    public void shutdown() {
        Object o = this;
        synchronized (this) {
            if (this.inShutdown) {
                return;
            }
            this.inShutdown = true;
        }
        Monitor.getStream().println("----------------------------------------------------------------");
        Monitor.getStream().println(MessageService.getTextMessage("J003", new Date().toString()));
        this.contextService.notifyAllActiveThreads(null);
        while (true) {
            synchronized (this) {
                final int index = this.services.size() - 1;
                if (index == 0) {
                    break;
                }
                o = this.services.get(index);
            }
            final ContextManager contextManager = this.contextService.newContextManager();
            try {
                contextManager.popContext();
                this.contextService.setCurrentContextManager(contextManager);
                this.shutdown(((TopService)o).getService());
            }
            finally {
                this.contextService.resetCurrentContextManager(contextManager);
            }
        }
        Monitor.getStream().println("----------------------------------------------------------------");
        this.services.get(0).shutdown();
        ContextService.stop();
        Monitor.clearMonitor();
    }
    
    public void shutdown(final Object o) {
        if (o == null) {
            return;
        }
        final TopService topService = this.findTopService(o);
        if (topService == null) {
            return;
        }
        boolean shutdown = true;
        try {
            shutdown = topService.shutdown();
        }
        finally {
            synchronized (this) {
                if (shutdown) {
                    this.services.remove(topService);
                }
            }
        }
    }
    
    protected final void runWithState(final Properties bootProperties, final PrintWriter logging) {
        this.bootProperties = bootProperties;
        this.logging = logging;
        if (!this.initialize(false)) {
            return;
        }
        if (!Monitor.setMonitor(this)) {
            return;
        }
        MessageService.setFinder(this);
        this.applicationProperties = this.readApplicationProperties();
        final Properties properties = null;
        final Vector implementations = this.getImplementations(this.bootProperties, false);
        final Vector implementations2 = this.getImplementations(properties, false);
        final Vector implementations3 = this.getImplementations(this.applicationProperties, false);
        final Vector defaultImplementations = this.getDefaultImplementations();
        int n = 0;
        if (implementations != null) {
            ++n;
        }
        if (implementations2 != null) {
            ++n;
        }
        if (implementations3 != null) {
            ++n;
        }
        if (defaultImplementations != null) {
            ++n;
        }
        this.implementationSets = new Vector[n];
        int n2 = 0;
        if (implementations != null) {
            this.implementationSets[n2++] = implementations;
        }
        if (implementations2 != null) {
            this.implementationSets[n2++] = implementations2;
        }
        if (implementations3 != null) {
            this.implementationSets[n2++] = implementations3;
        }
        if (defaultImplementations != null) {
            this.implementationSets[n2++] = defaultImplementations;
        }
        try {
            this.systemStreams = (InfoStreams)Monitor.startSystemModule("org.apache.derby.iapi.services.stream.InfoStreams");
            this.contextService = new ContextService();
            this.uuidFactory = (UUIDFactory)Monitor.startSystemModule("org.apache.derby.iapi.services.uuid.UUIDFactory");
            this.timerFactory = (TimerFactory)Monitor.startSystemModule("org.apache.derby.iapi.services.timer.TimerFactory");
            Monitor.startSystemModule("org.apache.derby.iapi.services.jmx.ManagementService");
        }
        catch (StandardException ex) {
            this.reportException(ex);
            this.dumpTempWriter(true);
            return;
        }
        this.dumpTempWriter(false);
        this.determineSupportedServiceProviders();
        final boolean booleanValue = Boolean.valueOf(PropertyUtil.getSystemProperty("derby.system.bootAll"));
        this.startServices(this.bootProperties, booleanValue);
        this.startServices(properties, booleanValue);
        this.startServices(this.applicationProperties, booleanValue);
        if (booleanValue) {
            this.bootPersistentServices();
        }
    }
    
    public String getCanonicalServiceName(final String s) throws StandardException {
        if (s == null) {
            return null;
        }
        final PersistentService providerForCreate = this.findProviderForCreate(s);
        if (providerForCreate == null) {
            return null;
        }
        return providerForCreate.getCanonicalServiceName(s);
    }
    
    public Object findService(final String s, final String s2) {
        if (s2 == null) {
            return null;
        }
        ProtocolKey create;
        try {
            create = ProtocolKey.create(s, s2);
        }
        catch (StandardException ex) {
            return null;
        }
        TopService topService = null;
        synchronized (this) {
            for (int i = 1; i < this.services.size(); ++i) {
                final TopService topService2 = this.services.get(i);
                if (topService2.isPotentialService(create)) {
                    topService = topService2;
                    break;
                }
            }
        }
        if (topService != null && topService.isActiveService(create)) {
            return topService.getService();
        }
        return null;
    }
    
    public Locale getLocale(final Object o) {
        final TopService topService = this.findTopService(o);
        if (topService == null) {
            return null;
        }
        return topService.serviceLocale;
    }
    
    public Locale getLocaleFromString(final String s) throws StandardException {
        return staticGetLocaleFromString(s);
    }
    
    public String getServiceName(final Object o) {
        final TopService topService = this.findTopService(o);
        if (topService == null) {
            return null;
        }
        return topService.getServiceType().getUserServiceName(topService.getKey().getIdentifier());
    }
    
    public Locale setLocale(final Object o, final String s) throws StandardException {
        final TopService topService = this.findTopService(o);
        if (topService == null) {
            return null;
        }
        final PersistentService serviceType = topService.getServiceType();
        if (serviceType == null) {
            return null;
        }
        final String identifier = topService.getKey().getIdentifier();
        return this.setLocale(new UpdateServiceProperties(serviceType, identifier, serviceType.getServiceProperties(identifier, null), true), s);
    }
    
    public Locale setLocale(final Properties properties, final String s) throws StandardException {
        final Locale staticGetLocaleFromString = staticGetLocaleFromString(s);
        properties.put("derby.serviceLocale", staticGetLocaleFromString.toString());
        return staticGetLocaleFromString;
    }
    
    public PersistentService getServiceType(final Object o) {
        final TopService topService = this.findTopService(o);
        if (topService == null) {
            return null;
        }
        return topService.getServiceType();
    }
    
    public Object startModule(final boolean b, final Object o, final String s, final String s2, final Properties properties) throws StandardException {
        final Object bootModule = this.findTopService(o).bootModule(b, o, ProtocolKey.create(s, s2), properties);
        if (bootModule == null) {
            throw Monitor.missingImplementation(s);
        }
        return bootModule;
    }
    
    private synchronized TopService findTopService(final Object o) {
        if (o == null) {
            return this.services.get(0);
        }
        for (int i = 1; i < this.services.size(); ++i) {
            final TopService topService = this.services.get(i);
            if (topService.inService(o)) {
                return topService;
            }
        }
        return null;
    }
    
    public Object findModule(final Object o, final String s, final String s2) {
        ProtocolKey create;
        try {
            create = ProtocolKey.create(s, s2);
        }
        catch (StandardException ex) {
            return null;
        }
        final TopService topService = this.findTopService(o);
        if (topService == null) {
            return null;
        }
        return topService.findModule(create, true, null);
    }
    
    public InstanceGetter classFromIdentifier(final int formatId) throws StandardException {
        int n;
        InstanceGetter[] rc2;
        String className;
        try {
            n = formatId - 0;
            rc2 = this.rc2;
            if (rc2 == null) {
                final InstanceGetter[] rc3 = new InstanceGetter[RegisteredFormatIds.TwoByte.length];
                this.rc2 = rc3;
                rc2 = rc3;
            }
            final InstanceGetter instanceGetter = rc2[n];
            if (instanceGetter != null) {
                return instanceGetter;
            }
            className = RegisteredFormatIds.TwoByte[n];
        }
        catch (ArrayIndexOutOfBoundsException ex5) {
            className = null;
            rc2 = null;
            n = 0;
        }
        if (className != null) {
            ClassNotFoundException ex;
            try {
                final Class<?> forName = Class.forName(className);
                if (FormatableInstanceGetter.class.isAssignableFrom(forName)) {
                    final FormatableInstanceGetter formatableInstanceGetter = (FormatableInstanceGetter)forName.newInstance();
                    formatableInstanceGetter.setFormatId(formatId);
                    return rc2[n] = formatableInstanceGetter;
                }
                return rc2[n] = new ClassInfo(forName);
            }
            catch (ClassNotFoundException ex2) {
                ex = ex2;
            }
            catch (IllegalAccessException ex3) {
                ex = (ClassNotFoundException)ex3;
            }
            catch (InstantiationException ex4) {
                ex = (ClassNotFoundException)ex4;
            }
            catch (LinkageError linkageError) {
                ex = (ClassNotFoundException)linkageError;
            }
            throw StandardException.newException("XBM0V.S", ex, FormatIdUtil.formatIdToString(formatId), className);
        }
        throw StandardException.newException("XBM0U.S", FormatIdUtil.formatIdToString(formatId));
    }
    
    public Object newInstanceFromIdentifier(final int value) throws StandardException {
        final InstanceGetter classFromIdentifier = this.classFromIdentifier(value);
        InstantiationException ex;
        try {
            return classFromIdentifier.getNewInstance();
        }
        catch (InstantiationException ex2) {
            ex = ex2;
        }
        catch (IllegalAccessException ex3) {
            ex = (InstantiationException)ex3;
        }
        catch (InvocationTargetException ex4) {
            ex = (InstantiationException)ex4;
        }
        catch (LinkageError linkageError) {
            ex = (InstantiationException)linkageError;
        }
        throw StandardException.newException("XBM0W.S", ex, new Integer(value), "XX");
    }
    
    protected Object loadInstance(final Class clazz, final Properties properties) {
        Object o = null;
        final Vector implementations = this.getImplementations(properties, false);
        if (implementations != null) {
            o = this.loadInstance(implementations, clazz, properties);
        }
        for (int i = 0; i < this.implementationSets.length; ++i) {
            o = this.loadInstance(this.implementationSets[i], clazz, properties);
            if (o != null) {
                break;
            }
        }
        return o;
    }
    
    private Object loadInstance(final Vector vector, final Class clazz, final Properties properties) {
        int implementation = 0;
        while (true) {
            implementation = findImplementation(vector, implementation, clazz);
            if (implementation < 0) {
                return null;
            }
            final Object instance = this.newInstance(vector.get(implementation));
            if (canSupport(instance, properties)) {
                return instance;
            }
            ++implementation;
        }
    }
    
    private static int findImplementation(final Vector vector, final int n, final Class clazz) {
        for (int i = n; i < vector.size(); ++i) {
            if (clazz.isAssignableFrom(vector.get(i))) {
                return i;
            }
        }
        return -1;
    }
    
    private Object newInstance(final String str) {
        try {
            return Class.forName(str).newInstance();
        }
        catch (ClassNotFoundException ex) {
            this.report(str + " " + ex.toString());
        }
        catch (InstantiationException ex2) {
            this.report(str + " " + ex2.toString());
        }
        catch (IllegalAccessException ex3) {
            this.report(str + " " + ex3.toString());
        }
        catch (LinkageError linkageError) {
            this.report(str + " " + linkageError.toString());
            this.reportException(linkageError);
        }
        return null;
    }
    
    private Object newInstance(final Class clazz) {
        try {
            return clazz.newInstance();
        }
        catch (InstantiationException ex) {
            this.report(clazz.getName() + " " + ex.toString());
        }
        catch (IllegalAccessException ex2) {
            this.report(clazz.getName() + " " + ex2.toString());
        }
        catch (LinkageError linkageError) {
            this.report(clazz.getName() + " " + linkageError.toString());
            this.reportException(linkageError);
        }
        return null;
    }
    
    public Properties getApplicationProperties() {
        return this.applicationProperties;
    }
    
    public String[] getServiceList(final String s) {
        synchronized (this) {
            int n = 0;
            for (int i = 1; i < this.services.size(); ++i) {
                final TopService topService = this.services.get(i);
                if (topService.isActiveService() && topService.getKey().getFactoryInterface().getName().equals(s)) {
                    ++n;
                }
            }
            final String[] array = new String[n];
            if (n != 0) {
                int n2 = 0;
                for (int j = 1; j < this.services.size(); ++j) {
                    final TopService topService2 = this.services.get(j);
                    if (topService2.isActiveService() && topService2.getKey().getFactoryInterface().getName().equals(s)) {
                        array[n2++] = topService2.getServiceType().getUserServiceName(topService2.getKey().getIdentifier());
                        if (n2 == n) {
                            break;
                        }
                    }
                }
            }
            return array;
        }
    }
    
    void dumpProperties(final String s, final Properties properties) {
    }
    
    protected void report(final String x) {
        final PrintWriter tempWriter = this.getTempWriter();
        if (tempWriter != null) {
            tempWriter.println(x);
        }
        if (this.systemStreams != null) {
            this.systemStreams.stream().printlnWithHeader(x);
        }
    }
    
    protected void reportException(final Throwable t) {
        PrintWriterGetHeader header = null;
        if (this.systemStreams != null) {
            header = this.systemStreams.stream().getHeader();
        }
        final ErrorStringBuilder errorStringBuilder = new ErrorStringBuilder(header);
        errorStringBuilder.appendln(t.getMessage());
        errorStringBuilder.stackTrace(t);
        this.report(errorStringBuilder.get().toString());
    }
    
    private void addDebugFlags(final String s, final boolean b) {
    }
    
    public void startServices(final Properties properties, final boolean b) {
        if (properties == null) {
            return;
        }
        final Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String key = (String)propertyNames.nextElement();
            if (key.startsWith("derby.service.")) {
                final String substring = key.substring("derby.service.".length());
                final String property = properties.getProperty(key);
                try {
                    if (property.equals("serviceDirectory")) {
                        if (!b) {
                            continue;
                        }
                        this.findProviderAndStartService(substring, properties, true);
                    }
                    else {
                        this.bootService(null, property, substring, null, false);
                    }
                }
                catch (StandardException ex) {
                    if (property.equals("serviceDirectory")) {
                        continue;
                    }
                    this.reportException(ex);
                }
            }
        }
    }
    
    public final boolean startPersistentService(final String s, final Properties properties) throws StandardException {
        return this.findProviderAndStartService(s, properties, false);
    }
    
    public Object createPersistentService(final String s, final String s2, final Properties properties) throws StandardException {
        final PersistentService providerForCreate = this.findProviderForCreate(s2);
        if (providerForCreate == null) {
            throw StandardException.newException("XBM0K.D", s2);
        }
        return this.bootService(providerForCreate, s, s2, properties, true);
    }
    
    public void removePersistentService(final String s) throws StandardException {
        final PersistentService providerForCreate = this.findProviderForCreate(s);
        final String canonicalServiceName = providerForCreate.getCanonicalServiceName(s);
        if (!providerForCreate.removeServiceRoot(canonicalServiceName)) {
            throw StandardException.newException("XBM0I.D", canonicalServiceName);
        }
    }
    
    public Object startNonPersistentService(final String s, final String s2, final Properties properties) throws StandardException {
        return this.bootService(null, s, s2, properties, false);
    }
    
    private Vector getImplementations(final Properties properties, final boolean b) {
        if (properties == null) {
            return null;
        }
        final Vector<Class<?>> vector = b ? new Vector<Class<?>>(properties.size()) : new Vector<Class<?>>(0, 1);
        final int jdk_ID = JVMInfo.JDK_ID;
        final int[] array = new int[jdk_ID + 1];
        final Enumeration<?> propertyNames = properties.propertyNames();
    Label_0053:
        while (propertyNames.hasMoreElements()) {
            final String key = (String)propertyNames.nextElement();
            String key2;
            if (key.startsWith("derby.module.")) {
                key2 = key.substring("derby.module.".length());
            }
            else {
                if (!key.startsWith("derby.subSubProtocol.")) {
                    continue;
                }
                key2 = key.substring("derby.module.".length());
            }
            final String property = properties.getProperty("derby.env.jdk.".concat(key2));
            int int1 = 0;
            if (property != null) {
                int1 = Integer.parseInt(property.trim());
                if (int1 > jdk_ID) {
                    continue;
                }
            }
            final String property2 = properties.getProperty("derby.env.classes.".concat(key2));
            if (property2 != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(property2, ",");
                while (stringTokenizer.hasMoreTokens()) {
                    try {
                        Class.forName(stringTokenizer.nextToken().trim());
                        continue;
                    }
                    catch (ClassNotFoundException ex2) {
                        continue Label_0053;
                    }
                    catch (LinkageError linkageError2) {
                        continue Label_0053;
                    }
                    break;
                }
            }
            final String property3 = properties.getProperty(key);
            try {
                final Class<?> forName = Class.forName(property3);
                if (this.getPersistentServiceImplementation(forName)) {
                    continue;
                }
                if (StorageFactory.class.isAssignableFrom(forName)) {
                    BaseMonitor.storageFactories.put(key2, property3);
                }
                else if (int1 != 0) {
                    int index = 0;
                    for (int i = jdk_ID; i > int1; --i) {
                        index += array[i];
                    }
                    vector.add(index, forName);
                    final int[] array2 = array;
                    final int n = int1;
                    ++array2[n];
                }
                else {
                    vector.add(forName);
                }
            }
            catch (ClassNotFoundException ex) {
                this.report("Class " + property3 + " " + ex.toString() + ", module ignored.");
            }
            catch (LinkageError linkageError) {
                this.report("Class " + property3 + " " + linkageError.toString() + ", module ignored.");
            }
        }
        if (vector.isEmpty()) {
            return null;
        }
        vector.trimToSize();
        return vector;
    }
    
    private boolean getPersistentServiceImplementation(final Class clazz) {
        if (!PersistentService.class.isAssignableFrom(clazz)) {
            return false;
        }
        final PersistentService value = (PersistentService)this.newInstance(clazz);
        if (value == null) {
            this.report("Class " + clazz.getName() + " cannot create instance, module ignored.");
        }
        else {
            this.serviceProviders.put(value.getType(), value);
        }
        return true;
    }
    
    private Vector getDefaultImplementations() {
        return this.getImplementations(this.getDefaultModuleProperties(), true);
    }
    
    Properties getDefaultModuleProperties() {
        final Properties properties = new Properties();
        int n = 1;
        final ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            final Enumeration<URL> enumeration = (classLoader == null) ? ClassLoader.getSystemResources("org/apache/derby/modules.properties") : classLoader.getResources("org/apache/derby/modules.properties");
            while (enumeration.hasMoreElements()) {
                final URL url = enumeration.nextElement();
                InputStream openStream = null;
                try {
                    openStream = url.openStream();
                    if (n != 0) {
                        properties.load(openStream);
                        n = 0;
                    }
                    else {
                        final Properties properties2 = new Properties();
                        properties2.load(openStream);
                        final Enumeration<Object> keys = properties2.keys();
                        while (keys.hasMoreElements()) {
                            final String s = keys.nextElement();
                            if (properties.containsKey(s)) {
                                this.report("Ignored duplicate property " + s + " in " + url.toString());
                            }
                            else {
                                properties.setProperty(s, properties2.getProperty(s));
                            }
                        }
                    }
                }
                catch (IOException ex) {}
                finally {
                    try {
                        if (openStream != null) {
                            openStream.close();
                        }
                    }
                    catch (IOException ex2) {}
                }
            }
        }
        catch (IOException ex3) {}
        return properties;
    }
    
    protected static Properties removeRuntimeProperties(final Properties properties) {
        final Properties properties2 = new Properties();
        final Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            final String s = keys.nextElement();
            if (s.startsWith("derby.__rt.")) {
                continue;
            }
            properties2.put(s, properties.get(s));
        }
        return properties2;
    }
    
    abstract InputStream applicationPropertiesStream() throws IOException;
    
    protected Properties readApplicationProperties() {
        InputStream applicationPropertiesStream = null;
        try {
            applicationPropertiesStream = this.applicationPropertiesStream();
            if (applicationPropertiesStream == null) {
                return null;
            }
            final Properties properties = new Properties();
            org.apache.derby.iapi.util.PropertyUtil.loadWithTrimmedValues(new BufferedInputStream(applicationPropertiesStream), properties);
            return properties;
        }
        catch (SecurityException ex2) {
            return null;
        }
        catch (IOException ex) {
            this.report(ex.toString() + " (" + "derby.properties" + ")");
            this.reportException(ex);
            return null;
        }
        finally {
            try {
                if (applicationPropertiesStream != null) {
                    applicationPropertiesStream.close();
                }
            }
            catch (IOException ex3) {}
        }
    }
    
    private void determineSupportedServiceProviders() {
        final Iterator<Object> iterator = this.serviceProviders.values().iterator();
        while (iterator.hasNext()) {
            if (!canSupport(iterator.next(), null)) {
                iterator.remove();
            }
        }
    }
    
    private void bootPersistentServices() {
        final ProviderEnumeration providerEnumeration = new ProviderEnumeration(this.applicationProperties);
        while (providerEnumeration.hasMoreElements()) {
            this.bootProviderServices(providerEnumeration.nextElement());
        }
    }
    
    protected void bootProviderServices(final PersistentService persistentService) {
        final Enumeration bootTimeServices = persistentService.getBootTimeServices();
        while (bootTimeServices != null && bootTimeServices.hasMoreElements()) {
            final String s = bootTimeServices.nextElement();
            Properties serviceProperties;
            try {
                serviceProperties = persistentService.getServiceProperties(s, null);
            }
            catch (StandardException ex) {
                this.report("Failed to load service properties, name: " + s + ", type = " + persistentService.getType());
                this.reportException(ex);
                continue;
            }
            if (Boolean.valueOf(serviceProperties.getProperty("derby.database.noAutoBoot"))) {
                continue;
            }
            try {
                this.startProviderService(persistentService, s, serviceProperties);
            }
            catch (StandardException ex2) {
                this.report("Service failed to boot, name: " + s + ", type = " + persistentService.getType());
                this.reportException(ex2);
            }
        }
    }
    
    private boolean findProviderAndStartService(final String s, final Properties properties, final boolean b) throws StandardException {
        PersistentService providerFromName = null;
        Properties properties2 = null;
        String s2 = null;
        final int index = s.indexOf(58);
        if (index != -1) {
            providerFromName = this.findProviderFromName(s, index);
            if (providerFromName != null) {
                final String canonicalServiceName = providerFromName.getCanonicalServiceName(s);
                if (canonicalServiceName == null) {
                    return true;
                }
                final Properties serviceProperties = providerFromName.getServiceProperties(canonicalServiceName, properties);
                if (serviceProperties == null) {
                    return true;
                }
                if (b && Boolean.valueOf(serviceProperties.getProperty("derby.database.noAutoBoot"))) {
                    return true;
                }
                this.startProviderService(providerFromName, canonicalServiceName, serviceProperties);
                return true;
            }
        }
        StandardException ex = null;
        final ProviderEnumeration providerEnumeration = new ProviderEnumeration(properties);
        while (providerEnumeration.hasMoreElements()) {
            final PersistentService persistentService = providerEnumeration.nextElement();
            final String canonicalServiceName2 = persistentService.getCanonicalServiceName(s);
            if (canonicalServiceName2 == null) {
                continue;
            }
            Properties serviceProperties2 = null;
            try {
                serviceProperties2 = persistentService.getServiceProperties(canonicalServiceName2, properties);
                if (serviceProperties2 == null) {
                    continue;
                }
            }
            catch (StandardException ex2) {
                ex = ex2;
            }
            if (providerFromName != null) {
                throw StandardException.newException("XBM0T.D", s);
            }
            providerFromName = persistentService;
            s2 = canonicalServiceName2;
            properties2 = serviceProperties2;
        }
        if (providerFromName == null) {
            return index == -1;
        }
        if (ex != null) {
            throw ex;
        }
        if (b && Boolean.valueOf(properties2.getProperty("derby.database.noAutoBoot"))) {
            return true;
        }
        this.startProviderService(providerFromName, s2, properties2);
        return true;
    }
    
    protected PersistentService findProviderForCreate(final String s) throws StandardException {
        return this.findProviderFromName(s, s.indexOf(58));
    }
    
    private PersistentService findProviderFromName(final String s, final int endIndex) throws StandardException {
        if (endIndex == 0) {
            return null;
        }
        String substring;
        if (endIndex < 2) {
            substring = "directory";
        }
        else {
            substring = s.substring(0, endIndex);
        }
        return this.getServiceProvider(substring);
    }
    
    public PersistentService getServiceProvider(final String key) throws StandardException {
        if (key == null) {
            return null;
        }
        if (this.serviceProviders != null) {
            final PersistentService persistentService = this.serviceProviders.get(key);
            if (persistentService != null) {
                return persistentService;
            }
        }
        return this.getPersistentService(key);
    }
    
    private PersistentService getPersistentService(final String s) throws StandardException {
        return this.getPersistentService(this.getStorageFactoryClassName(s), s);
    }
    
    private PersistentService getPersistentService(final String className, final String s) throws StandardException {
        if (className == null) {
            return null;
        }
        Class<?> forName;
        try {
            forName = Class.forName(className);
        }
        catch (Throwable t) {
            throw StandardException.newException("XBM08.D", t, s, className);
        }
        return new StorageFactoryService(s, forName);
    }
    
    private String getStorageFactoryClassName(final String s) {
        final String systemProperty = PropertyUtil.getSystemProperty("derby.subSubProtocol." + s);
        if (systemProperty != null) {
            return systemProperty;
        }
        return BaseMonitor.storageFactories.get(s);
    }
    
    protected void startProviderService(final PersistentService persistentService, final String s, final Properties properties) throws StandardException {
        final String property = properties.getProperty("derby.serviceProtocol");
        if (property == null) {
            throw StandardException.newException("XCY03.S", "derby.serviceProtocol");
        }
        this.bootService(persistentService, property, s, properties, false);
    }
    
    protected Object bootService(final PersistentService persistentService, final String value, String value2, Properties properties, final boolean b) throws StandardException {
        if (persistentService != null) {
            value2 = persistentService.getCanonicalServiceName(value2);
        }
        ProtocolKey protocolKey = ProtocolKey.create(value, value2);
        ContextManager currentContextManager;
        final ContextManager contextManager = currentContextManager = this.contextService.getCurrentContextManager();
        TopService topService = null;
        Context context = null;
        Object bootModule;
        try {
            synchronized (this) {
                if (this.inShutdown) {
                    throw StandardException.newException("XJ015.M");
                }
                for (int i = 1; i < this.services.size(); ++i) {
                    if (((TopService)this.services.get(i)).isPotentialService(protocolKey)) {
                        return null;
                    }
                }
                Locale locale = null;
                if (b) {
                    properties = new Properties(properties);
                    locale = setLocale(properties);
                    properties.put("derby.serviceProtocol", value);
                    value2 = persistentService.createServiceRoot(value2, Boolean.valueOf(properties.getProperty("derby.__deleteOnCreate")));
                    protocolKey = ProtocolKey.create(value, value2);
                }
                else if (properties != null) {
                    final String property = properties.getProperty("derby.serviceLocale");
                    if (property != null) {
                        locale = staticGetLocaleFromString(property);
                    }
                }
                topService = new TopService(this, protocolKey, persistentService, locale);
                this.services.add(topService);
            }
            if (properties != null) {
                properties.put("derby.__rt.serviceDirectory", value2);
                properties.put("derby.__rt.serviceType", persistentService.getType());
            }
            if (contextManager == null) {
                currentContextManager = this.contextService.newContextManager();
                this.contextService.setCurrentContextManager(currentContextManager);
            }
            context = new ServiceBootContext(currentContextManager);
            final boolean b2 = properties != null && properties.getProperty("derby.__rt.inRestore") != null;
            Properties properties2;
            UpdateServiceProperties updateServiceProperties;
            if (persistentService != null && properties != null) {
                updateServiceProperties = (UpdateServiceProperties)(properties2 = new UpdateServiceProperties(persistentService, value2, properties, !b && !b2));
            }
            else {
                updateServiceProperties = null;
                properties2 = properties;
            }
            bootModule = topService.bootModule(b, null, protocolKey, properties2);
            if (b) {
                persistentService.createDataWarningFile(updateServiceProperties.getStorageFactory());
            }
            if (b || b2) {
                persistentService.saveServiceProperties(value2, updateServiceProperties.getStorageFactory(), removeRuntimeProperties(properties), false);
                updateServiceProperties.setServiceBooted();
            }
            if (currentContextManager != contextManager) {
                currentContextManager.cleanupOnError(StandardException.closeException(), false);
            }
        }
        catch (Throwable t) {
            StandardException exceptionStartingModule;
            if (t instanceof StandardException && ((StandardException)t).getSeverity() == 45000) {
                exceptionStartingModule = (StandardException)t;
            }
            else {
                exceptionStartingModule = Monitor.exceptionStartingModule(t);
            }
            if (currentContextManager != contextManager) {
                currentContextManager.cleanupOnError(exceptionStartingModule, false);
            }
            if (topService != null) {
                topService.shutdown();
                synchronized (this) {
                    this.services.remove(topService);
                }
                final boolean b3 = properties != null && properties.getProperty("derby.__rt.deleteRootOnError") != null;
                if (b || b3) {
                    persistentService.removeServiceRoot(value2);
                }
            }
            final Throwable cause = exceptionStartingModule.getCause();
            if (cause instanceof ThreadDeath) {
                throw (ThreadDeath)cause;
            }
            throw exceptionStartingModule;
        }
        finally {
            if (contextManager == currentContextManager && context != null) {
                context.popMe();
            }
            if (contextManager == null) {
                this.contextService.resetCurrentContextManager(currentContextManager);
            }
        }
        topService.setTopModule(bootModule);
        Thread.yield();
        return bootModule;
    }
    
    public UUIDFactory getUUIDFactory() {
        return this.uuidFactory;
    }
    
    public TimerFactory getTimerFactory() {
        return this.timerFactory;
    }
    
    private PrintWriter getTempWriter() {
        if (this.tmpWriter == null && !this.dumpedTempWriter) {
            this.tmpArray = new AccessibleByteArrayOutputStream();
            this.tmpWriter = new PrintWriter(this.tmpArray);
        }
        return this.tmpWriter;
    }
    
    private void dumpTempWriter(final boolean b) {
        if (this.tmpWriter == null) {
            return;
        }
        this.tmpWriter.flush();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.tmpArray.getInternalByteArray())));
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (this.systemStreams != null) {
                    this.systemStreams.stream().printlnWithHeader(line);
                }
                if (this.systemStreams == null || b) {
                    this.logging.println(line);
                }
            }
        }
        catch (IOException ex) {}
        if (this.systemStreams == null || b) {
            this.logging.flush();
        }
        this.tmpWriter = null;
        this.tmpArray = null;
        this.dumpedTempWriter = true;
        this.logging = null;
    }
    
    static boolean canSupport(final Object o, final Properties properties) {
        return !(o instanceof ModuleSupportable) || ((ModuleSupportable)o).canSupport(properties);
    }
    
    static void boot(final Object o, final boolean b, final Properties properties) throws StandardException {
        if (o instanceof ModuleControl) {
            ((ModuleControl)o).boot(b, properties);
        }
    }
    
    private static Locale staticGetLocaleFromString(final String s) throws StandardException {
        final int length = s.length();
        boolean b = length == 2 || length == 5 || length > 6;
        if (b && length != 2) {
            b = (s.charAt(2) == '_');
        }
        if (b && length > 5) {
            b = (s.charAt(5) == '_');
        }
        if (!b) {
            throw StandardException.newException("XBM0X.D", s);
        }
        final String substring = s.substring(0, 2);
        final String s2 = (length == 2) ? "" : s.substring(3, 5);
        if (length < 6) {
            return new Locale(substring, s2);
        }
        return new Locale(substring, s2, (length > 6) ? s.substring(6, length) : null);
    }
    
    private static Locale setLocale(final Properties properties) throws StandardException {
        final String property = properties.getProperty("territory");
        Locale locale;
        if (property == null) {
            locale = Locale.getDefault();
        }
        else {
            locale = staticGetLocaleFromString(property);
        }
        properties.put("derby.serviceLocale", locale.toString());
        return locale;
    }
    
    public ResourceBundle getBundle(final String s) {
        ContextManager currentContextManager;
        try {
            currentContextManager = ContextService.getFactory().getCurrentContextManager();
        }
        catch (ShutdownException ex) {
            currentContextManager = null;
        }
        if (currentContextManager != null) {
            return MessageService.getBundleForLocale(currentContextManager.getMessageLocale(), s);
        }
        return null;
    }
    
    public Thread getDaemonThread(final Runnable target, final String str, final boolean b) {
        final Thread thread = new Thread(this.daemonGroup, target, "derby.".concat(str));
        thread.setDaemon(true);
        if (b) {
            thread.setPriority(1);
        }
        return thread;
    }
    
    public void setThreadPriority(final int priority) {
        final Thread currentThread = Thread.currentThread();
        if (currentThread.getThreadGroup() == this.daemonGroup) {
            currentThread.setPriority(priority);
        }
    }
    
    abstract boolean initialize(final boolean p0);
    
    static {
        storageFactories = new HashMap();
        String value;
        if (!JVMInfo.J2ME) {
            value = "org.apache.derby.impl.io.DirStorageFactory4";
        }
        else {
            value = "org.apache.derby.impl.io.DirStorageFactory";
        }
        BaseMonitor.storageFactories.put("directory", value);
        BaseMonitor.storageFactories.put("classpath", "org.apache.derby.impl.io.CPStorageFactory");
        BaseMonitor.storageFactories.put("jar", "org.apache.derby.impl.io.JarStorageFactory");
        BaseMonitor.storageFactories.put("http", "org.apache.derby.impl.io.URLStorageFactory");
        BaseMonitor.storageFactories.put("https", "org.apache.derby.impl.io.URLStorageFactory");
        BaseMonitor.storageFactories.put("memory", "org.apache.derby.impl.io.VFMemoryStorageFactory");
    }
    
    class ProviderEnumeration implements Enumeration
    {
        private Enumeration serviceProvidersKeys;
        private Properties startParams;
        private Enumeration paramEnumeration;
        private boolean enumeratedDirectoryProvider;
        private PersistentService storageFactoryPersistentService;
        
        ProviderEnumeration(final Properties startParams) {
            this.serviceProvidersKeys = ((BaseMonitor.this.serviceProviders == null) ? null : Collections.enumeration((Collection<Object>)BaseMonitor.this.serviceProviders.keySet()));
            this.startParams = startParams;
            if (startParams != null) {
                this.paramEnumeration = startParams.keys();
            }
        }
        
        public Object nextElement() throws NoSuchElementException {
            if (this.serviceProvidersKeys != null && this.serviceProvidersKeys.hasMoreElements()) {
                return BaseMonitor.this.serviceProviders.get(this.serviceProvidersKeys.nextElement());
            }
            this.getNextStorageFactory();
            final PersistentService storageFactoryPersistentService = this.storageFactoryPersistentService;
            this.storageFactoryPersistentService = null;
            return storageFactoryPersistentService;
        }
        
        private void getNextStorageFactory() {
            if (this.storageFactoryPersistentService != null) {
                return;
            }
            if (this.paramEnumeration != null) {
                while (this.paramEnumeration.hasMoreElements()) {
                    final String s = this.paramEnumeration.nextElement();
                    if (s.startsWith("derby.subSubProtocol.")) {
                        try {
                            if (this.startParams.get(s) == null) {
                                continue;
                            }
                            this.storageFactoryPersistentService = BaseMonitor.this.getPersistentService((String)this.startParams.get(s), s.substring("derby.subSubProtocol.".length()));
                            if (this.storageFactoryPersistentService != null) {
                                return;
                            }
                            continue;
                        }
                        catch (StandardException ex) {}
                    }
                }
            }
            if (!this.enumeratedDirectoryProvider) {
                try {
                    this.storageFactoryPersistentService = BaseMonitor.this.getPersistentService(BaseMonitor.this.getStorageFactoryClassName("directory"), "directory");
                }
                catch (StandardException ex2) {
                    this.storageFactoryPersistentService = null;
                }
                this.enumeratedDirectoryProvider = true;
            }
        }
        
        public boolean hasMoreElements() {
            if (this.serviceProvidersKeys != null && this.serviceProvidersKeys.hasMoreElements()) {
                return true;
            }
            this.getNextStorageFactory();
            return this.storageFactoryPersistentService != null;
        }
    }
}
