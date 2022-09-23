// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import javax.jdo.spi.StateInterrogation;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.naming.InitialContext;
import javax.naming.Context;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.File;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.net.URL;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import javax.jdo.spi.PersistenceCapable;
import java.util.Collections;
import java.util.HashMap;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.I18NHelper;
import java.util.Map;

public class JDOHelper implements Constants
{
    static final Map<String, String> ATTRIBUTE_PROPERTY_XREF;
    private static final I18NHelper msg;
    private static JDOImplHelper implHelper;
    private static JDOHelper instance;
    static JDOImplHelper.StateInterrogationObjectReturn getPersistenceManager;
    static JDOImplHelper.StateInterrogationObjectReturn getObjectId;
    static JDOImplHelper.StateInterrogationObjectReturn getTransactionalObjectId;
    static JDOImplHelper.StateInterrogationObjectReturn getVersion;
    static JDOImplHelper.StateInterrogationBooleanReturn isPersistent;
    static JDOImplHelper.StateInterrogationBooleanReturn isTransactional;
    static JDOImplHelper.StateInterrogationBooleanReturn isDirty;
    static JDOImplHelper.StateInterrogationBooleanReturn isNew;
    static JDOImplHelper.StateInterrogationBooleanReturn isDeleted;
    static JDOImplHelper.StateInterrogationBooleanReturn isDetached;
    
    static Map<String, String> createAttributePropertyXref() {
        final Map<String, String> xref = new HashMap<String, String>();
        xref.put("class", "javax.jdo.PersistenceManagerFactoryClass");
        xref.put("connection-driver-name", "javax.jdo.option.ConnectionDriverName");
        xref.put("connection-factory-name", "javax.jdo.option.ConnectionFactoryName");
        xref.put("connection-factory2-name", "javax.jdo.option.ConnectionFactory2Name");
        xref.put("connection-password", "javax.jdo.option.ConnectionPassword");
        xref.put("connection-url", "javax.jdo.option.ConnectionURL");
        xref.put("connection-user-name", "javax.jdo.option.ConnectionUserName");
        xref.put("ignore-cache", "javax.jdo.option.IgnoreCache");
        xref.put("mapping", "javax.jdo.option.Mapping");
        xref.put("multithreaded", "javax.jdo.option.Multithreaded");
        xref.put("nontransactional-read", "javax.jdo.option.NontransactionalRead");
        xref.put("nontransactional-write", "javax.jdo.option.NontransactionalWrite");
        xref.put("optimistic", "javax.jdo.option.Optimistic");
        xref.put("persistence-unit-name", "javax.jdo.option.PersistenceUnitName");
        xref.put("name", "javax.jdo.option.Name");
        xref.put("restore-values", "javax.jdo.option.RestoreValues");
        xref.put("retain-values", "javax.jdo.option.RetainValues");
        xref.put("detach-all-on-commit", "javax.jdo.option.DetachAllOnCommit");
        xref.put("server-time-zone-id", "javax.jdo.option.ServerTimeZoneID");
        xref.put("datastore-read-timeout-millis", "javax.jdo.option.DatastoreReadTimeoutMillis");
        xref.put("datastore-write-timeout-millis", "javax.jdo.option.DatastoreWriteTimeoutMillis");
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)xref);
    }
    
    public static JDOHelper getInstance() {
        return JDOHelper.instance;
    }
    
    public static PersistenceManager getPersistenceManager(final Object pc) {
        if (pc instanceof PersistenceCapable) {
            return ((PersistenceCapable)pc).jdoGetPersistenceManager();
        }
        return (PersistenceManager)JDOHelper.implHelper.nonBinaryCompatibleGet(pc, JDOHelper.getPersistenceManager);
    }
    
    public static void makeDirty(final Object pc, final String fieldName) {
        if (pc instanceof PersistenceCapable) {
            ((PersistenceCapable)pc).jdoMakeDirty(fieldName);
        }
        else {
            JDOHelper.implHelper.nonBinaryCompatibleMakeDirty(pc, fieldName);
        }
    }
    
    public static Object getObjectId(final Object pc) {
        if (pc instanceof PersistenceCapable) {
            return ((PersistenceCapable)pc).jdoGetObjectId();
        }
        return JDOHelper.implHelper.nonBinaryCompatibleGet(pc, JDOHelper.getObjectId);
    }
    
    public static Collection<Object> getObjectIds(final Collection<?> pcs) {
        final ArrayList<Object> result = new ArrayList<Object>();
        final Iterator<?> it = pcs.iterator();
        while (it.hasNext()) {
            result.add(getObjectId(it.next()));
        }
        return result;
    }
    
    public static Object[] getObjectIds(final Object[] pcs) {
        final Object[] result = new Object[pcs.length];
        for (int i = 0; i < pcs.length; ++i) {
            result[i] = getObjectId(pcs[i]);
        }
        return result;
    }
    
    public static Object getTransactionalObjectId(final Object pc) {
        if (pc instanceof PersistenceCapable) {
            return ((PersistenceCapable)pc).jdoGetTransactionalObjectId();
        }
        return JDOHelper.implHelper.nonBinaryCompatibleGet(pc, JDOHelper.getTransactionalObjectId);
    }
    
    public static Object getVersion(final Object pc) {
        if (pc instanceof PersistenceCapable) {
            return ((PersistenceCapable)pc).jdoGetVersion();
        }
        return JDOHelper.implHelper.nonBinaryCompatibleGet(pc, JDOHelper.getVersion);
    }
    
    public static boolean isDirty(final Object pc) {
        if (pc instanceof PersistenceCapable) {
            return ((PersistenceCapable)pc).jdoIsDirty();
        }
        return JDOHelper.implHelper.nonBinaryCompatibleIs(pc, JDOHelper.isDirty);
    }
    
    public static boolean isTransactional(final Object pc) {
        if (pc instanceof PersistenceCapable) {
            return ((PersistenceCapable)pc).jdoIsTransactional();
        }
        return JDOHelper.implHelper.nonBinaryCompatibleIs(pc, JDOHelper.isTransactional);
    }
    
    public static boolean isPersistent(final Object pc) {
        if (pc instanceof PersistenceCapable) {
            return ((PersistenceCapable)pc).jdoIsPersistent();
        }
        return JDOHelper.implHelper.nonBinaryCompatibleIs(pc, JDOHelper.isPersistent);
    }
    
    public static boolean isNew(final Object pc) {
        if (pc instanceof PersistenceCapable) {
            return ((PersistenceCapable)pc).jdoIsNew();
        }
        return JDOHelper.implHelper.nonBinaryCompatibleIs(pc, JDOHelper.isNew);
    }
    
    public static boolean isDeleted(final Object pc) {
        if (pc instanceof PersistenceCapable) {
            return ((PersistenceCapable)pc).jdoIsDeleted();
        }
        return JDOHelper.implHelper.nonBinaryCompatibleIs(pc, JDOHelper.isDeleted);
    }
    
    public static boolean isDetached(final Object pc) {
        if (pc instanceof PersistenceCapable) {
            return ((PersistenceCapable)pc).jdoIsDetached();
        }
        return JDOHelper.implHelper.nonBinaryCompatibleIs(pc, JDOHelper.isDetached);
    }
    
    public static ObjectState getObjectState(final Object pc) {
        if (pc == null) {
            return null;
        }
        if (isDetached(pc)) {
            if (isDirty(pc)) {
                return ObjectState.DETACHED_DIRTY;
            }
            return ObjectState.DETACHED_CLEAN;
        }
        else if (isPersistent(pc)) {
            if (isTransactional(pc)) {
                if (!isDirty(pc)) {
                    return ObjectState.PERSISTENT_CLEAN;
                }
                if (isNew(pc)) {
                    if (isDeleted(pc)) {
                        return ObjectState.PERSISTENT_NEW_DELETED;
                    }
                    return ObjectState.PERSISTENT_NEW;
                }
                else {
                    if (isDeleted(pc)) {
                        return ObjectState.PERSISTENT_DELETED;
                    }
                    return ObjectState.PERSISTENT_DIRTY;
                }
            }
            else {
                if (isDirty(pc)) {
                    return ObjectState.PERSISTENT_NONTRANSACTIONAL_DIRTY;
                }
                return ObjectState.HOLLOW_PERSISTENT_NONTRANSACTIONAL;
            }
        }
        else {
            if (!isTransactional(pc)) {
                return ObjectState.TRANSIENT;
            }
            if (isDirty(pc)) {
                return ObjectState.TRANSIENT_DIRTY;
            }
            return ObjectState.TRANSIENT_CLEAN;
        }
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory() {
        final ClassLoader cl = getContextClassLoader();
        return getPersistenceManagerFactory(null, "", cl, cl);
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final ClassLoader pmfClassLoader) {
        return getPersistenceManagerFactory(null, "", pmfClassLoader, pmfClassLoader);
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final Map<?, ?> props) {
        return getPersistenceManagerFactory(null, props, getContextClassLoader());
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final Map<?, ?> props, final ClassLoader pmfClassLoader) {
        return getPersistenceManagerFactory(null, props, pmfClassLoader);
    }
    
    protected static PersistenceManagerFactory getPersistenceManagerFactory(final Map<?, ?> overrides, final Map<?, ?> props, final ClassLoader pmfClassLoader) {
        final List<Throwable> exceptions = new ArrayList<Throwable>();
        if (pmfClassLoader == null) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFNullLoader"));
        }
        String pmfClassName = (String)props.get("javax.jdo.PersistenceManagerFactoryClass");
        if (!isNullOrBlank(pmfClassName)) {
            return invokeGetPersistenceManagerFactoryOnImplementation(pmfClassName, overrides, props, pmfClassLoader);
        }
        Enumeration<URL> urls = null;
        try {
            urls = getResources(pmfClassLoader, "META-INF/services/javax.jdo.PersistenceManagerFactory");
        }
        catch (Throwable ex) {
            exceptions.add(ex);
        }
        if (urls != null) {
            while (urls.hasMoreElements()) {
                try {
                    pmfClassName = getClassNameFromURL(urls.nextElement());
                    final PersistenceManagerFactory pmf = invokeGetPersistenceManagerFactoryOnImplementation(pmfClassName, overrides, props, pmfClassLoader);
                    return pmf;
                }
                catch (Throwable ex) {
                    exceptions.add(ex);
                    continue;
                }
                break;
            }
        }
        throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFNoPMFClassNamePropertyOrPUNameProperty"), exceptions.toArray(new Throwable[exceptions.size()]));
    }
    
    protected static String getClassNameFromURL(final URL url) throws IOException {
        final InputStream is = openStream(url);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() != 0) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    final String[] tokens = line.split("\\s");
                    final String pmfClassName = tokens[0];
                    final int indexOfComment = pmfClassName.indexOf("#");
                    if (indexOfComment == -1) {
                        return pmfClassName;
                    }
                    return pmfClassName.substring(0, indexOfComment);
                }
            }
            return null;
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final String name) {
        final ClassLoader cl = getContextClassLoader();
        return getPersistenceManagerFactory(null, name, cl, cl);
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final String name, final ClassLoader loader) {
        return getPersistenceManagerFactory(null, name, loader, loader);
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final String name, final ClassLoader resourceLoader, final ClassLoader pmfLoader) {
        return getPersistenceManagerFactory(null, name, resourceLoader, pmfLoader);
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final Map<?, ?> overrides, final String name) {
        final ClassLoader cl = getContextClassLoader();
        return getPersistenceManagerFactory(overrides, name, cl, cl);
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final Map<?, ?> overrides, final String name, final ClassLoader resourceLoader) {
        return getPersistenceManagerFactory(overrides, name, resourceLoader, resourceLoader);
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final Map<?, ?> overrides, String name, final ClassLoader resourceLoader, final ClassLoader pmfLoader) {
        if (pmfLoader == null) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFNullPMFLoader"));
        }
        if (resourceLoader == null) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFNullPropsLoader"));
        }
        Map<Object, Object> props = null;
        name = ((name == null) ? "" : name.trim());
        if (!"".equals(name)) {
            props = loadPropertiesFromResource(resourceLoader, name);
        }
        if (props != null) {
            props.put("javax.jdo.option.spi.ResourceName", name);
            props.remove("javax.jdo.option.Name");
            return getPersistenceManagerFactory(overrides, props, pmfLoader);
        }
        props = getPropertiesFromJdoconfig(name, pmfLoader);
        if (props != null) {
            props.put("javax.jdo.option.Name", name);
            props.remove("javax.jdo.option.spi.ResourceName");
            return getPersistenceManagerFactory(overrides, props, pmfLoader);
        }
        if (!"".equals(name)) {
            props = new Properties();
            props.put("javax.jdo.option.PersistenceUnitName", name);
            return getPersistenceManagerFactory(overrides, props, pmfLoader);
        }
        throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_NoPMFConfigurableViaPropertiesOrXML", name));
    }
    
    protected static PersistenceManagerFactory invokeGetPersistenceManagerFactoryOnImplementation(final String pmfClassName, final Map<?, ?> overrides, final Map<?, ?> properties, final ClassLoader cl) {
        if (overrides != null) {
            try {
                final Class<?> implClass = forName(pmfClassName, true, cl);
                final Method m = getMethod(implClass, "getPersistenceManagerFactory", new Class[] { Map.class, Map.class });
                final PersistenceManagerFactory pmf = (PersistenceManagerFactory)invoke(m, null, new Object[] { overrides, properties });
                if (pmf == null) {
                    throw new JDOFatalInternalException(JDOHelper.msg.msg("EXC_GetPMFNullPMF", pmfClassName));
                }
                return pmf;
            }
            catch (ClassNotFoundException e) {
                throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFClassNotFound", pmfClassName), e);
            }
            catch (NoSuchMethodException e2) {
                throw new JDOFatalInternalException(JDOHelper.msg.msg("EXC_GetPMFNoSuchMethod2", pmfClassName), e2);
            }
            catch (NullPointerException e3) {
                throw new JDOFatalInternalException(JDOHelper.msg.msg("EXC_GetPMFNullPointerException", pmfClassName), e3);
            }
            catch (IllegalAccessException e4) {
                throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFIllegalAccess", pmfClassName), e4);
            }
            catch (ClassCastException e5) {
                throw new JDOFatalInternalException(JDOHelper.msg.msg("EXC_GetPMFClassCastException", pmfClassName), e5);
            }
            catch (InvocationTargetException ite) {
                final Throwable nested = ite.getTargetException();
                if (nested instanceof JDOException) {
                    throw (JDOException)nested;
                }
                throw new JDOFatalInternalException(JDOHelper.msg.msg("EXC_GetPMFUnexpectedException"), ite);
            }
        }
        try {
            final Class<?> implClass = forName(pmfClassName, true, cl);
            final Method m = getMethod(implClass, "getPersistenceManagerFactory", new Class[] { Map.class });
            final PersistenceManagerFactory pmf = (PersistenceManagerFactory)invoke(m, null, new Object[] { properties });
            if (pmf == null) {
                throw new JDOFatalInternalException(JDOHelper.msg.msg("EXC_GetPMFNullPMF", pmfClassName));
            }
            return pmf;
        }
        catch (ClassNotFoundException e) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFClassNotFound", pmfClassName), e);
        }
        catch (NoSuchMethodException e2) {
            throw new JDOFatalInternalException(JDOHelper.msg.msg("EXC_GetPMFNoSuchMethod", pmfClassName), e2);
        }
        catch (NullPointerException e3) {
            throw new JDOFatalInternalException(JDOHelper.msg.msg("EXC_GetPMFNullPointerException", pmfClassName), e3);
        }
        catch (IllegalAccessException e4) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFIllegalAccess", pmfClassName), e4);
        }
        catch (ClassCastException e5) {
            throw new JDOFatalInternalException(JDOHelper.msg.msg("EXC_GetPMFClassCastException", pmfClassName), e5);
        }
        catch (InvocationTargetException ite) {
            final Throwable nested = ite.getTargetException();
            if (nested instanceof JDOException) {
                throw (JDOException)nested;
            }
            throw new JDOFatalInternalException(JDOHelper.msg.msg("EXC_GetPMFUnexpectedException"), ite);
        }
    }
    
    protected static Map<Object, Object> loadPropertiesFromResource(final ClassLoader resourceLoader, final String name) {
        InputStream in = null;
        Properties props = null;
        try {
            in = getResourceAsStream(resourceLoader, name);
            if (in != null) {
                props = new Properties();
                props.load(in);
            }
        }
        catch (IOException ioe) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFIOExceptionRsrc", name), ioe);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex) {}
            }
        }
        return props;
    }
    
    protected static Map<Object, Object> getPropertiesFromJdoconfig(final String name, final ClassLoader resourceLoader) {
        return getNamedPMFProperties(name, resourceLoader, "META-INF/jdoconfig.xml");
    }
    
    protected static Map<Object, Object> getNamedPMFProperties(final String name, final ClassLoader resourceLoader, final String jdoconfigResourceName) {
        final Map<String, Map<Object, Object>> propertiesByNameInAllConfigs = new HashMap<String, Map<Object, Object>>();
        try {
            URL firstFoundConfigURL = null;
            final Enumeration<URL> resources = getResources(resourceLoader, jdoconfigResourceName);
            if (resources.hasMoreElements()) {
                final ArrayList<URL> processedResources = new ArrayList<URL>();
                final DocumentBuilderFactory factory = getDocumentBuilderFactory();
                do {
                    final URL currentConfigURL = resources.nextElement();
                    if (processedResources.contains(currentConfigURL)) {
                        continue;
                    }
                    processedResources.add(currentConfigURL);
                    final Map<String, Map<Object, Object>> propertiesByNameInCurrentConfig = readNamedPMFProperties(currentConfigURL, name, factory);
                    if (propertiesByNameInCurrentConfig.containsKey(name)) {
                        if (firstFoundConfigURL == null) {
                            firstFoundConfigURL = currentConfigURL;
                        }
                        if (propertiesByNameInAllConfigs.containsKey(name)) {
                            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_DuplicateRequestedNamedPMFFoundInDifferentConfigs", "".equals(name) ? "(anonymous)" : name, firstFoundConfigURL.toExternalForm(), currentConfigURL.toExternalForm()));
                        }
                    }
                    propertiesByNameInAllConfigs.putAll(propertiesByNameInCurrentConfig);
                } while (resources.hasMoreElements());
            }
        }
        catch (FactoryConfigurationError e) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("ERR_NoDocumentBuilderFactory"), e);
        }
        catch (IOException ioe) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFIOExceptionRsrc", name), ioe);
        }
        return propertiesByNameInAllConfigs.get(name);
    }
    
    protected static DocumentBuilderFactory getDocumentBuilderFactory() {
        final JDOImplHelper implHelper = JDOHelper.implHelper;
        DocumentBuilderFactory factory = JDOImplHelper.getRegisteredDocumentBuilderFactory();
        if (factory == null) {
            factory = getDefaultDocumentBuilderFactory();
        }
        return factory;
    }
    
    protected static DocumentBuilderFactory getDefaultDocumentBuilderFactory() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setExpandEntityReferences(true);
        return factory;
    }
    
    protected static ErrorHandler getErrorHandler() {
        final JDOImplHelper implHelper = JDOHelper.implHelper;
        ErrorHandler handler = JDOImplHelper.getRegisteredErrorHandler();
        if (handler == null) {
            handler = getDefaultErrorHandler();
        }
        return handler;
    }
    
    protected static ErrorHandler getDefaultErrorHandler() {
        return new ErrorHandler() {
            public void error(final SAXParseException exception) throws SAXException {
                throw exception;
            }
            
            public void fatalError(final SAXParseException exception) throws SAXException {
                throw exception;
            }
            
            public void warning(final SAXParseException exception) throws SAXException {
            }
        };
    }
    
    protected static Map<String, Map<Object, Object>> readNamedPMFProperties(final URL url, String requestedPMFName, final DocumentBuilderFactory factory) {
        requestedPMFName = ((requestedPMFName == null) ? "" : requestedPMFName.trim());
        final Map<String, Map<Object, Object>> propertiesByName = new HashMap<String, Map<Object, Object>>();
        InputStream in = null;
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(getErrorHandler());
            in = openStream(url);
            final Document doc = builder.parse(in);
            final Element root = doc.getDocumentElement();
            if (root == null) {
                throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_InvalidJDOConfigNoRoot", url.toExternalForm()));
            }
            final NodeList pmfs = root.getElementsByTagName("persistence-manager-factory");
            for (int i = 0; i < pmfs.getLength(); ++i) {
                final Node pmfElement = pmfs.item(i);
                final Properties pmfPropertiesFromAttributes = readPropertiesFromPMFElementAttributes(pmfElement);
                final Properties pmfPropertiesFromElements = readPropertiesFromPMFSubelements(pmfElement, url);
                final String pmfNameFromAtts = pmfPropertiesFromAttributes.getProperty("javax.jdo.option.Name");
                final String pmfNameFromElem = pmfPropertiesFromElements.getProperty("javax.jdo.option.Name");
                String pmfName = null;
                if (isNullOrBlank(pmfNameFromAtts)) {
                    if (!isNullOrBlank(pmfNameFromElem)) {
                        pmfName = pmfNameFromElem;
                    }
                    else {
                        pmfName = "";
                    }
                }
                else {
                    if (!isNullOrBlank(pmfNameFromElem)) {
                        throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_DuplicatePMFNamePropertyFoundWithinConfig", pmfNameFromAtts, pmfNameFromElem, url.toExternalForm()));
                    }
                    pmfName = pmfNameFromAtts;
                }
                pmfName = ((pmfName == null) ? "" : pmfName.trim());
                if (requestedPMFName.equals(pmfName)) {
                    for (final String property : pmfPropertiesFromAttributes.keySet()) {
                        if (pmfPropertiesFromElements.contains(property)) {
                            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_DuplicatePropertyFound", property, pmfName, url.toExternalForm()));
                        }
                    }
                }
                final Properties pmfProps = new Properties();
                pmfProps.putAll(pmfPropertiesFromAttributes);
                pmfProps.putAll(pmfPropertiesFromElements);
                if (pmfName.equals(requestedPMFName) && propertiesByName.containsKey(pmfName)) {
                    throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_DuplicateRequestedNamedPMFFoundInSameConfig", pmfName, url.toExternalForm()));
                }
                propertiesByName.put(pmfName, pmfProps);
            }
            return propertiesByName;
        }
        catch (IOException ioe) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFIOExceptionRsrc", url.toString()), ioe);
        }
        catch (ParserConfigurationException e) {
            throw new JDOFatalInternalException(JDOHelper.msg.msg("EXC_ParserConfigException"), e);
        }
        catch (SAXParseException e2) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_SAXParseException", url.toExternalForm(), new Integer(e2.getLineNumber()), new Integer(e2.getColumnNumber())), e2);
        }
        catch (SAXException e3) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_SAXException", url.toExternalForm()), e3);
        }
        catch (JDOException e4) {
            throw e4;
        }
        catch (RuntimeException e5) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_SAXException", url.toExternalForm()), e5);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    protected static Properties readPropertiesFromPMFElementAttributes(final Node pmfElement) {
        final Properties p = new Properties();
        final NamedNodeMap attributes = pmfElement.getAttributes();
        if (attributes == null) {
            return p;
        }
        for (int i = 0; i < attributes.getLength(); ++i) {
            final Node att = attributes.item(i);
            final String attName = att.getNodeName();
            final String attValue = att.getNodeValue().trim();
            final String jdoPropertyName = JDOHelper.ATTRIBUTE_PROPERTY_XREF.get(attName);
            p.put((jdoPropertyName != null) ? jdoPropertyName : attName, attValue);
        }
        return p;
    }
    
    protected static Properties readPropertiesFromPMFSubelements(final Node pmfElement, final URL url) {
        final Properties p = new Properties();
        final NodeList elements = pmfElement.getChildNodes();
        if (elements == null) {
            return p;
        }
        for (int i = 0; i < elements.getLength(); ++i) {
            final Node element = elements.item(i);
            if (element.getNodeType() == 1) {
                final String elementName = element.getNodeName();
                final NamedNodeMap attributes = element.getAttributes();
                if ("property".equalsIgnoreCase(elementName)) {
                    final Node nameAtt = attributes.getNamedItem("name");
                    if (nameAtt == null) {
                        throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_PropertyElementHasNoNameAttribute", url));
                    }
                    final String name = nameAtt.getNodeValue().trim();
                    if ("".equals(name)) {
                        throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_PropertyElementNameAttributeHasNoValue", name, url));
                    }
                    final String jdoPropertyName = JDOHelper.ATTRIBUTE_PROPERTY_XREF.get(name);
                    final String propertyName = (jdoPropertyName != null) ? jdoPropertyName : name;
                    if (p.containsKey(propertyName)) {
                        throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_DuplicatePropertyNameGivenInPropertyElement", propertyName, url));
                    }
                    final Node valueAtt = attributes.getNamedItem("value");
                    final String value = (valueAtt == null) ? null : valueAtt.getNodeValue().trim();
                    p.put(propertyName, value);
                }
                else if ("instance-lifecycle-listener".equals(elementName)) {
                    final Node listenerAtt = attributes.getNamedItem("listener");
                    if (listenerAtt == null) {
                        throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_MissingListenerAttribute", url));
                    }
                    String listener = listenerAtt.getNodeValue().trim();
                    if ("".equals(listener)) {
                        throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_MissingListenerAttributeValue", url));
                    }
                    listener = "javax.jdo.listener.InstanceLifecycleListener." + listener;
                    final Node classesAtt = attributes.getNamedItem("classes");
                    final String value2 = (classesAtt == null) ? "" : classesAtt.getNodeValue().trim();
                    p.put(listener, value2);
                }
            }
        }
        return p;
    }
    
    protected static boolean isNullOrBlank(final String s) {
        return s == null || "".equals(s.trim());
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final File propsFile) {
        return getPersistenceManagerFactory(propsFile, getContextClassLoader());
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final File propsFile, final ClassLoader loader) {
        if (propsFile == null) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFNullFile"));
        }
        InputStream in = null;
        try {
            in = new FileInputStream(propsFile);
            return getPersistenceManagerFactory(in, loader);
        }
        catch (FileNotFoundException fnfe) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFNoFile", propsFile), fnfe);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final String jndiLocation, final Context context) {
        return getPersistenceManagerFactory(jndiLocation, context, getContextClassLoader());
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final String jndiLocation, Context context, final ClassLoader loader) {
        if (jndiLocation == null) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFNullJndiLoc"));
        }
        if (loader == null) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFNullLoader"));
        }
        try {
            if (context == null) {
                context = new InitialContext();
            }
            final Object o = context.lookup(jndiLocation);
            return (PersistenceManagerFactory)PortableRemoteObject.narrow(o, (Class)PersistenceManagerFactory.class);
        }
        catch (NamingException ne) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFNamingException", jndiLocation, loader), ne);
        }
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final InputStream stream) {
        return getPersistenceManagerFactory(stream, getContextClassLoader());
    }
    
    public static PersistenceManagerFactory getPersistenceManagerFactory(final InputStream stream, final ClassLoader loader) {
        if (stream == null) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFNullStream"));
        }
        final Properties props = new Properties();
        try {
            props.load(stream);
        }
        catch (IOException ioe) {
            throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetPMFIOExceptionStream"), ioe);
        }
        return getPersistenceManagerFactory(props, loader);
    }
    
    public static JDOEnhancer getEnhancer() {
        return getEnhancer(getContextClassLoader());
    }
    
    public static JDOEnhancer getEnhancer(final ClassLoader loader) {
        ClassLoader ctrLoader = loader;
        if (ctrLoader == null) {
            ctrLoader = Thread.currentThread().getContextClassLoader();
        }
        final ArrayList<Throwable> exceptions = new ArrayList<Throwable>();
        int numberOfJDOEnhancers = 0;
        try {
            final Enumeration<URL> urls = getResources(loader, "META-INF/services/javax.jdo.JDOEnhancer");
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    ++numberOfJDOEnhancers;
                    try {
                        final String enhancerClassName = getClassNameFromURL(urls.nextElement());
                        final Class<?> enhancerClass = forName(enhancerClassName, true, ctrLoader);
                        final JDOEnhancer enhancer = (JDOEnhancer)enhancerClass.newInstance();
                        return enhancer;
                    }
                    catch (Throwable ex) {
                        exceptions.add(ex);
                        continue;
                    }
                    break;
                }
            }
        }
        catch (Throwable ex2) {
            exceptions.add(ex2);
        }
        throw new JDOFatalUserException(JDOHelper.msg.msg("EXC_GetEnhancerNoValidEnhancerAvailable", numberOfJDOEnhancers), exceptions.toArray(new Throwable[exceptions.size()]));
    }
    
    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
    
    private static InputStream getResourceAsStream(final ClassLoader resourceLoader, final String name) {
        return AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction<InputStream>() {
            public InputStream run() {
                return resourceLoader.getResourceAsStream(name);
            }
        });
    }
    
    private static Method getMethod(final Class<?> implClass, final String methodName, final Class<?>[] parameterTypes) throws NoSuchMethodException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                public Method run() throws NoSuchMethodException {
                    return implClass.getMethod(methodName, (Class[])parameterTypes);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (NoSuchMethodException)ex.getException();
        }
    }
    
    private static Object invoke(final Method method, final Object instance, final Object[] parameters) throws IllegalAccessException, InvocationTargetException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                public Object run() throws IllegalAccessException, InvocationTargetException {
                    return method.invoke(instance, parameters);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            final Exception cause = ex.getException();
            if (cause instanceof IllegalAccessException) {
                throw (IllegalAccessException)cause;
            }
            throw (InvocationTargetException)cause;
        }
    }
    
    protected static Enumeration<URL> getResources(final ClassLoader resourceLoader, final String resourceName) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Enumeration<URL>>)new PrivilegedExceptionAction<Enumeration<URL>>() {
                public Enumeration<URL> run() throws IOException {
                    return resourceLoader.getResources(resourceName);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    private static Class<?> forName(final String name, final boolean init, final ClassLoader loader) throws ClassNotFoundException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>)new PrivilegedExceptionAction<Class<?>>() {
                public Class<?> run() throws ClassNotFoundException {
                    return Class.forName(name, init, loader);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (ClassNotFoundException)ex.getException();
        }
    }
    
    private static InputStream openStream(final URL url) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction<InputStream>() {
                public InputStream run() throws IOException {
                    return url.openStream();
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    static {
        ATTRIBUTE_PROPERTY_XREF = createAttributePropertyXref();
        msg = I18NHelper.getInstance("javax.jdo.Bundle");
        JDOHelper.implHelper = AccessController.doPrivileged((PrivilegedAction<JDOImplHelper>)new PrivilegedAction<JDOImplHelper>() {
            public JDOImplHelper run() {
                return JDOImplHelper.getInstance();
            }
        });
        JDOHelper.instance = new JDOHelper();
        JDOHelper.getPersistenceManager = new JDOImplHelper.StateInterrogationObjectReturn() {
            public Object get(final Object pc, final StateInterrogation si) {
                return si.getPersistenceManager(pc);
            }
        };
        JDOHelper.getObjectId = new JDOImplHelper.StateInterrogationObjectReturn() {
            public Object get(final Object pc, final StateInterrogation si) {
                return si.getObjectId(pc);
            }
        };
        JDOHelper.getTransactionalObjectId = new JDOImplHelper.StateInterrogationObjectReturn() {
            public Object get(final Object pc, final StateInterrogation si) {
                return si.getTransactionalObjectId(pc);
            }
        };
        JDOHelper.getVersion = new JDOImplHelper.StateInterrogationObjectReturn() {
            public Object get(final Object pc, final StateInterrogation si) {
                return si.getVersion(pc);
            }
        };
        JDOHelper.isPersistent = new JDOImplHelper.StateInterrogationBooleanReturn() {
            public Boolean is(final Object pc, final StateInterrogation si) {
                return si.isPersistent(pc);
            }
        };
        JDOHelper.isTransactional = new JDOImplHelper.StateInterrogationBooleanReturn() {
            public Boolean is(final Object pc, final StateInterrogation si) {
                return si.isTransactional(pc);
            }
        };
        JDOHelper.isDirty = new JDOImplHelper.StateInterrogationBooleanReturn() {
            public Boolean is(final Object pc, final StateInterrogation si) {
                return si.isDirty(pc);
            }
        };
        JDOHelper.isNew = new JDOImplHelper.StateInterrogationBooleanReturn() {
            public Boolean is(final Object pc, final StateInterrogation si) {
                return si.isNew(pc);
            }
        };
        JDOHelper.isDeleted = new JDOImplHelper.StateInterrogationBooleanReturn() {
            public Boolean is(final Object pc, final StateInterrogation si) {
                return si.isDeleted(pc);
            }
        };
        JDOHelper.isDetached = new JDOImplHelper.StateInterrogationBooleanReturn() {
            public Boolean is(final Object pc, final StateInterrogation si) {
                return si.isDetached(pc);
            }
        };
    }
}
