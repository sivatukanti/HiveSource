// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.xml;

import java.util.AbstractList;
import org.xml.sax.Attributes;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.LazyList;
import java.net.InetAddress;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.eclipse.jetty.util.MultiException;
import org.eclipse.jetty.util.TypeUtil;
import java.util.LinkedList;
import java.util.ServiceLoader;
import java.util.Collection;
import java.util.Set;
import java.util.Queue;
import java.util.HashSet;
import org.eclipse.jetty.util.ArrayQueue;
import org.eclipse.jetty.util.log.Log;
import java.security.AccessController;
import java.util.List;
import org.eclipse.jetty.util.component.LifeCycle;
import java.util.ArrayList;
import org.eclipse.jetty.util.resource.Resource;
import java.util.Locale;
import java.util.Properties;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Iterator;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.util.HashMap;
import org.eclipse.jetty.util.Loader;
import java.net.URL;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;

public class XmlConfiguration
{
    private static final Logger LOG;
    private static final Class<?>[] __primitives;
    private static final Class<?>[] __boxedPrimitives;
    private static final Class<?>[] __supportedCollections;
    private static final Iterable<ConfigurationProcessorFactory> __factoryLoader;
    private static final XmlParser __parser;
    private final Map<String, Object> _idMap;
    private final Map<String, String> _propertyMap;
    private final URL _url;
    private final String _dtd;
    private ConfigurationProcessor _processor;
    
    private static XmlParser initParser() {
        final XmlParser parser = new XmlParser();
        final URL config60 = Loader.getResource(XmlConfiguration.class, "org/eclipse/jetty/xml/configure_6_0.dtd");
        final URL config61 = Loader.getResource(XmlConfiguration.class, "org/eclipse/jetty/xml/configure_7_6.dtd");
        final URL config62 = Loader.getResource(XmlConfiguration.class, "org/eclipse/jetty/xml/configure_9_0.dtd");
        final URL config63 = Loader.getResource(XmlConfiguration.class, "org/eclipse/jetty/xml/configure_9_3.dtd");
        parser.redirectEntity("configure.dtd", config62);
        parser.redirectEntity("configure_1_0.dtd", config60);
        parser.redirectEntity("configure_1_1.dtd", config60);
        parser.redirectEntity("configure_1_2.dtd", config60);
        parser.redirectEntity("configure_1_3.dtd", config60);
        parser.redirectEntity("configure_6_0.dtd", config60);
        parser.redirectEntity("configure_7_6.dtd", config61);
        parser.redirectEntity("configure_9_0.dtd", config62);
        parser.redirectEntity("configure_9_3.dtd", config63);
        parser.redirectEntity("http://jetty.mortbay.org/configure.dtd", config63);
        parser.redirectEntity("http://jetty.eclipse.org/configure.dtd", config63);
        parser.redirectEntity("http://www.eclipse.org/jetty/configure.dtd", config63);
        parser.redirectEntity("-//Mort Bay Consulting//DTD Configure//EN", config63);
        parser.redirectEntity("-//Jetty//Configure//EN", config63);
        return parser;
    }
    
    public XmlConfiguration(final URL configuration) throws SAXException, IOException {
        this._idMap = new HashMap<String, Object>();
        this._propertyMap = new HashMap<String, String>();
        synchronized (XmlConfiguration.__parser) {
            this._url = configuration;
            this.setConfig(XmlConfiguration.__parser.parse(configuration.toString()));
            this._dtd = XmlConfiguration.__parser.getDTD();
        }
    }
    
    public XmlConfiguration(String configuration) throws SAXException, IOException {
        this._idMap = new HashMap<String, Object>();
        this._propertyMap = new HashMap<String, String>();
        configuration = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<!DOCTYPE Configure PUBLIC \"-//Jetty//Configure//EN\" \"http://eclipse.org/jetty/configure.dtd\">" + configuration;
        final InputSource source = new InputSource(new StringReader(configuration));
        synchronized (XmlConfiguration.__parser) {
            this._url = null;
            this.setConfig(XmlConfiguration.__parser.parse(source));
            this._dtd = XmlConfiguration.__parser.getDTD();
        }
    }
    
    public XmlConfiguration(final InputStream configuration) throws SAXException, IOException {
        this._idMap = new HashMap<String, Object>();
        this._propertyMap = new HashMap<String, String>();
        final InputSource source = new InputSource(configuration);
        synchronized (XmlConfiguration.__parser) {
            this._url = null;
            this.setConfig(XmlConfiguration.__parser.parse(source));
            this._dtd = XmlConfiguration.__parser.getDTD();
        }
    }
    
    private void setConfig(final XmlParser.Node config) {
        if ("Configure".equals(config.getTag())) {
            this._processor = new JettyXmlConfiguration();
        }
        else {
            if (XmlConfiguration.__factoryLoader == null) {
                throw new IllegalArgumentException("Unknown XML tag:" + config.getTag());
            }
            for (final ConfigurationProcessorFactory factory : XmlConfiguration.__factoryLoader) {
                this._processor = factory.getConfigurationProcessor(this._dtd, config.getTag());
                if (this._processor != null) {
                    break;
                }
            }
            if (this._processor == null) {
                throw new IllegalStateException("Unknown configuration type: " + config.getTag() + " in " + this);
            }
        }
        this._processor.init(this._url, config, this);
    }
    
    public Map<String, Object> getIdMap() {
        return this._idMap;
    }
    
    public Map<String, String> getProperties() {
        return this._propertyMap;
    }
    
    public Object configure(final Object obj) throws Exception {
        return this._processor.configure(obj);
    }
    
    public Object configure() throws Exception {
        return this._processor.configure();
    }
    
    public void initializeDefaults(final Object object) {
    }
    
    public static void main(final String... args) throws Exception {
        final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Properties properties = null;
                    try {
                        final Class<?> config = XmlConfiguration.class.getClassLoader().loadClass("org.eclipse.jetty.start.Config");
                        properties = (Properties)config.getMethod("getProperties", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                        XmlConfiguration.LOG.debug("org.eclipse.jetty.start.Config properties = {}", properties);
                    }
                    catch (NoClassDefFoundError | ClassNotFoundException noClassDefFoundError) {
                        final Throwable t;
                        final Throwable e = t;
                        XmlConfiguration.LOG.ignore(e);
                    }
                    catch (Exception e2) {
                        XmlConfiguration.LOG.warn(e2);
                    }
                    if (properties == null) {
                        properties = new Properties();
                        properties.putAll(System.getProperties());
                    }
                    for (final String arg : args) {
                        if (arg.indexOf(61) >= 0) {
                            final int i = arg.indexOf(61);
                            properties.put(arg.substring(0, i), arg.substring(i + 1));
                        }
                        else if (arg.toLowerCase(Locale.ENGLISH).endsWith(".properties")) {
                            properties.load(Resource.newResource(arg).getInputStream());
                        }
                    }
                    XmlConfiguration last = null;
                    final List<Object> objects = new ArrayList<Object>(args.length);
                    for (int j = 0; j < args.length; ++j) {
                        if (!args[j].toLowerCase(Locale.ENGLISH).endsWith(".properties") && args[j].indexOf(61) < 0) {
                            final XmlConfiguration configuration = new XmlConfiguration(Resource.newResource(args[j]).getURI().toURL());
                            if (last != null) {
                                configuration.getIdMap().putAll(last.getIdMap());
                            }
                            if (properties.size() > 0) {
                                final Map<String, String> props = new HashMap<String, String>();
                                for (final Object key : properties.keySet()) {
                                    props.put(key.toString(), String.valueOf(properties.get(key)));
                                }
                                configuration.getProperties().putAll(props);
                            }
                            final Object obj = configuration.configure();
                            if (obj != null && !objects.contains(obj)) {
                                objects.add(obj);
                            }
                            last = configuration;
                        }
                    }
                    for (final Object obj2 : objects) {
                        if (obj2 instanceof LifeCycle) {
                            final LifeCycle lc = (LifeCycle)obj2;
                            if (lc.isRunning()) {
                                continue;
                            }
                            lc.start();
                        }
                    }
                }
                catch (Exception e3) {
                    XmlConfiguration.LOG.debug("EXCEPTION ", e3);
                    exception.set(e3);
                }
                return null;
            }
        });
        final Throwable th = exception.get();
        if (th == null) {
            return;
        }
        if (th instanceof RuntimeException) {
            throw (RuntimeException)th;
        }
        if (th instanceof Exception) {
            throw (Exception)th;
        }
        if (th instanceof Error) {
            throw (Error)th;
        }
        throw new Error(th);
    }
    
    static {
        LOG = Log.getLogger(XmlConfiguration.class);
        __primitives = new Class[] { Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE };
        __boxedPrimitives = new Class[] { Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class };
        __supportedCollections = new Class[] { ArrayList.class, ArrayQueue.class, HashSet.class, Queue.class, List.class, Set.class, Collection.class };
        __factoryLoader = ServiceLoader.load(ConfigurationProcessorFactory.class);
        __parser = initParser();
    }
    
    private static class JettyXmlConfiguration implements ConfigurationProcessor
    {
        private String _url;
        XmlParser.Node _root;
        XmlConfiguration _configuration;
        
        @Override
        public void init(final URL url, final XmlParser.Node root, final XmlConfiguration configuration) {
            this._url = ((url == null) ? null : url.toString());
            this._root = root;
            this._configuration = configuration;
        }
        
        @Override
        public Object configure(final Object obj) throws Exception {
            final Class<?> oClass = nodeClass(this._root);
            if (oClass != null && !oClass.isInstance(obj)) {
                final String loaders = (oClass.getClassLoader() == obj.getClass().getClassLoader()) ? "" : "Object Class and type Class are from different loaders.";
                throw new IllegalArgumentException("Object of class '" + obj.getClass().getCanonicalName() + "' is not of type '" + oClass.getCanonicalName() + "'. " + loaders + " in " + this._url);
            }
            final String id = this._root.getAttribute("id");
            if (id != null) {
                this._configuration.getIdMap().put(id, obj);
            }
            this.configure(obj, this._root, 0);
            return obj;
        }
        
        @Override
        public Object configure() throws Exception {
            final Class<?> oClass = nodeClass(this._root);
            final String id = this._root.getAttribute("id");
            Object obj = (id == null) ? null : this._configuration.getIdMap().get(id);
            int index = 0;
            if (obj == null && oClass != null) {
                index = this._root.size();
                final Map<String, Object> namedArgMap = new HashMap<String, Object>();
                final List<Object> arguments = new LinkedList<Object>();
                for (int i = 0; i < this._root.size(); ++i) {
                    final Object o = this._root.get(i);
                    if (!(o instanceof String)) {
                        final XmlParser.Node node = (XmlParser.Node)o;
                        if (!node.getTag().equals("Arg")) {
                            index = i;
                            break;
                        }
                        final String namedAttribute = node.getAttribute("name");
                        final Object value = this.value(obj, (XmlParser.Node)o);
                        if (namedAttribute != null) {
                            namedArgMap.put(namedAttribute, value);
                        }
                        arguments.add(value);
                    }
                }
                try {
                    if (namedArgMap.size() > 0) {
                        obj = TypeUtil.construct(oClass, arguments.toArray(), namedArgMap);
                    }
                    else {
                        obj = TypeUtil.construct(oClass, arguments.toArray());
                    }
                }
                catch (NoSuchMethodException x) {
                    throw new IllegalStateException(String.format("No constructor %s(%s,%s) in %s", oClass, arguments, namedArgMap, this._url));
                }
            }
            if (id != null) {
                this._configuration.getIdMap().put(id, obj);
            }
            this._configuration.initializeDefaults(obj);
            this.configure(obj, this._root, index);
            return obj;
        }
        
        private static Class<?> nodeClass(final XmlParser.Node node) throws ClassNotFoundException {
            final String className = node.getAttribute("class");
            if (className == null) {
                return null;
            }
            return (Class<?>)Loader.loadClass(XmlConfiguration.class, className);
        }
        
        public void configure(final Object obj, final XmlParser.Node cfg, int i) throws Exception {
            while (i < cfg.size()) {
                final Object o = cfg.get(i);
                if (!(o instanceof String)) {
                    final XmlParser.Node node = (XmlParser.Node)o;
                    if (!"Arg".equals(node.getTag())) {
                        break;
                    }
                    XmlConfiguration.LOG.warn("Ignored arg: " + node, new Object[0]);
                }
                ++i;
            }
            while (i < cfg.size()) {
                final Object o = cfg.get(i);
                if (!(o instanceof String)) {
                    final XmlParser.Node node = (XmlParser.Node)o;
                    try {
                        final String tag2;
                        final String tag = tag2 = node.getTag();
                        switch (tag2) {
                            case "Set": {
                                this.set(obj, node);
                                break;
                            }
                            case "Put": {
                                this.put(obj, node);
                                break;
                            }
                            case "Call": {
                                this.call(obj, node);
                                break;
                            }
                            case "Get": {
                                this.get(obj, node);
                                break;
                            }
                            case "New": {
                                this.newObj(obj, node);
                                break;
                            }
                            case "Array": {
                                this.newArray(obj, node);
                                break;
                            }
                            case "Map": {
                                this.newMap(obj, node);
                                break;
                            }
                            case "Ref": {
                                this.refObj(obj, node);
                                break;
                            }
                            case "Property": {
                                this.propertyObj(node);
                                break;
                            }
                            case "SystemProperty": {
                                this.systemPropertyObj(node);
                                break;
                            }
                            case "Env": {
                                this.envObj(node);
                                break;
                            }
                            default: {
                                throw new IllegalStateException("Unknown tag: " + tag + " in " + this._url);
                            }
                        }
                    }
                    catch (Exception e) {
                        XmlConfiguration.LOG.warn("Config error at " + node, e.toString() + " in " + this._url);
                        throw e;
                    }
                }
                ++i;
            }
        }
        
        private void set(Object obj, final XmlParser.Node node) throws Exception {
            final String attr = node.getAttribute("name");
            final String name = "set" + attr.substring(0, 1).toUpperCase(Locale.ENGLISH) + attr.substring(1);
            final Object value = this.value(obj, node);
            final Object[] arg = { value };
            Class<?> oClass = nodeClass(node);
            if (oClass != null) {
                obj = null;
            }
            else {
                oClass = obj.getClass();
            }
            final Class<?>[] vClass = (Class<?>[])new Class[] { Object.class };
            if (value != null) {
                vClass[0] = value.getClass();
            }
            if (XmlConfiguration.LOG.isDebugEnabled()) {
                XmlConfiguration.LOG.debug("XML " + ((obj != null) ? obj.toString() : oClass.getName()) + "." + name + "(" + value + ")", new Object[0]);
            }
            final MultiException me = new MultiException();
            try {
                final Method set = oClass.getMethod(name, vClass);
                set.invoke(obj, arg);
            }
            catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException ex5) {
                final Exception ex;
                Exception e = ex;
                XmlConfiguration.LOG.ignore(e);
                me.add(e);
                try {
                    final Field type = vClass[0].getField("TYPE");
                    vClass[0] = (Class<?>)type.get(null);
                    final Method set2 = oClass.getMethod(name, vClass);
                    set2.invoke(obj, arg);
                }
                catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException ex6) {
                    final Exception ex2;
                    e = ex2;
                    XmlConfiguration.LOG.ignore(e);
                    me.add(e);
                    try {
                        final Field field = oClass.getField(attr);
                        if (Modifier.isPublic(field.getModifiers())) {
                            field.set(obj, value);
                            return;
                        }
                    }
                    catch (NoSuchFieldException e2) {
                        XmlConfiguration.LOG.ignore(e2);
                        me.add(e2);
                    }
                    final Method[] sets = oClass.getMethods();
                    Method set2 = null;
                    String types = null;
                    for (int s = 0; sets != null && s < sets.length; ++s) {
                        final Class<?>[] paramTypes = sets[s].getParameterTypes();
                        if (name.equals(sets[s].getName()) && paramTypes.length == 1) {
                            types = ((types == null) ? paramTypes[0].getName() : (types + "," + paramTypes[0].getName()));
                            try {
                                set2 = sets[s];
                                sets[s].invoke(obj, arg);
                                return;
                            }
                            catch (IllegalArgumentException | IllegalAccessException ex7) {
                                final Exception ex3;
                                final Exception e3 = ex3;
                                XmlConfiguration.LOG.ignore(e3);
                                me.add(e3);
                                try {
                                    for (final Class<?> c : XmlConfiguration.__supportedCollections) {
                                        if (paramTypes[0].isAssignableFrom(c)) {
                                            sets[s].invoke(obj, convertArrayToCollection(value, c));
                                            return;
                                        }
                                    }
                                }
                                catch (IllegalAccessException e4) {
                                    XmlConfiguration.LOG.ignore(e4);
                                    me.add(e4);
                                }
                            }
                        }
                    }
                    if (set2 != null) {
                        try {
                            Class<?> sClass = set2.getParameterTypes()[0];
                            if (sClass.isPrimitive()) {
                                for (int t = 0; t < XmlConfiguration.__primitives.length; ++t) {
                                    if (sClass.equals(XmlConfiguration.__primitives[t])) {
                                        sClass = XmlConfiguration.__boxedPrimitives[t];
                                        break;
                                    }
                                }
                            }
                            final Constructor<?> cons = sClass.getConstructor(vClass);
                            arg[0] = cons.newInstance(arg);
                            this._configuration.initializeDefaults(arg[0]);
                            set2.invoke(obj, arg);
                            return;
                        }
                        catch (NoSuchMethodException | IllegalAccessException | InstantiationException ex8) {
                            final ReflectiveOperationException ex4;
                            final ReflectiveOperationException e5 = ex4;
                            XmlConfiguration.LOG.ignore(e5);
                            me.add(e5);
                        }
                    }
                    String message = oClass + "." + name + "(" + vClass[0] + ")";
                    if (types != null) {
                        message = message + ". Found setters for " + types;
                    }
                    throw new NoSuchMethodException(message) {
                        {
                            for (int i = 0; i < me.size(); ++i) {
                                this.addSuppressed(me.getThrowable(i));
                            }
                        }
                    };
                }
            }
        }
        
        private static Collection<?> convertArrayToCollection(final Object array, final Class<?> collectionType) {
            Collection<?> collection = null;
            if (array.getClass().isArray()) {
                if (collectionType.isAssignableFrom(ArrayList.class)) {
                    collection = convertArrayToArrayList(array);
                }
                else if (collectionType.isAssignableFrom(HashSet.class)) {
                    collection = new HashSet<Object>(convertArrayToArrayList(array));
                }
                else if (collectionType.isAssignableFrom(ArrayQueue.class)) {
                    final ArrayQueue<Object> q = new ArrayQueue<Object>();
                    q.addAll(convertArrayToArrayList(array));
                    collection = q;
                }
            }
            if (collection == null) {
                throw new IllegalArgumentException("Can't convert \"" + array.getClass() + "\" to " + collectionType);
            }
            return collection;
        }
        
        private static ArrayList<Object> convertArrayToArrayList(final Object array) {
            final int length = Array.getLength(array);
            final ArrayList<Object> list = new ArrayList<Object>(length);
            for (int i = 0; i < length; ++i) {
                list.add(Array.get(array, i));
            }
            return list;
        }
        
        private void put(final Object obj, final XmlParser.Node node) throws Exception {
            if (!(obj instanceof Map)) {
                throw new IllegalArgumentException("Object for put is not a Map: " + obj);
            }
            final Map<Object, Object> map = (Map<Object, Object>)obj;
            final String name = node.getAttribute("name");
            final Object value = this.value(obj, node);
            map.put(name, value);
            if (XmlConfiguration.LOG.isDebugEnabled()) {
                XmlConfiguration.LOG.debug("XML " + obj + ".put(" + name + "," + value + ")", new Object[0]);
            }
        }
        
        private Object get(Object obj, final XmlParser.Node node) throws Exception {
            Class<?> oClass = nodeClass(node);
            if (oClass != null) {
                obj = null;
            }
            else {
                oClass = obj.getClass();
            }
            final String name = node.getAttribute("name");
            final String id = node.getAttribute("id");
            if (XmlConfiguration.LOG.isDebugEnabled()) {
                XmlConfiguration.LOG.debug("XML get " + name, new Object[0]);
            }
            try {
                if ("class".equalsIgnoreCase(name)) {
                    obj = oClass;
                }
                else {
                    final Method method = oClass.getMethod("get" + name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1), (Class<?>[])null);
                    obj = method.invoke(obj, (Object[])null);
                }
                if (id != null) {
                    this._configuration.getIdMap().put(id, obj);
                }
                this.configure(obj, node, 0);
            }
            catch (NoSuchMethodException nsme) {
                try {
                    final Field field = oClass.getField(name);
                    obj = field.get(obj);
                    this.configure(obj, node, 0);
                }
                catch (NoSuchFieldException nsfe) {
                    throw nsme;
                }
            }
            return obj;
        }
        
        private Object call(Object obj, final XmlParser.Node node) throws Exception {
            final AttrOrElementNode aoeNode = new AttrOrElementNode(obj, node, new String[] { "Id", "Name", "Class", "Arg" });
            final String id = aoeNode.getString("Id");
            final String name = aoeNode.getString("Name");
            final String clazz = aoeNode.getString("Class");
            final List<Object> args = aoeNode.getList("Arg");
            Class<?> oClass;
            if (clazz != null) {
                oClass = (Class<?>)Loader.loadClass(XmlConfiguration.class, clazz);
                obj = null;
            }
            else {
                if (obj == null) {
                    throw new IllegalArgumentException(node.toString());
                }
                oClass = obj.getClass();
            }
            if (XmlConfiguration.LOG.isDebugEnabled()) {
                XmlConfiguration.LOG.debug("XML call " + name, new Object[0]);
            }
            try {
                final Object nobj = TypeUtil.call(oClass, name, obj, args.toArray(new Object[args.size()]));
                if (id != null) {
                    this._configuration.getIdMap().put(id, nobj);
                }
                this.configure(nobj, node, aoeNode.getNext());
                return nobj;
            }
            catch (NoSuchMethodException e) {
                final IllegalStateException ise = new IllegalStateException("No Method: " + node + " on " + oClass);
                ise.initCause(e);
                throw ise;
            }
        }
        
        private Object newObj(final Object obj, final XmlParser.Node node) throws Exception {
            final AttrOrElementNode aoeNode = new AttrOrElementNode(obj, node, new String[] { "Id", "Class", "Arg" });
            final String id = aoeNode.getString("Id");
            final String clazz = aoeNode.getString("Class");
            final List<XmlParser.Node> argNodes = aoeNode.getNodes("Arg");
            if (XmlConfiguration.LOG.isDebugEnabled()) {
                XmlConfiguration.LOG.debug("XML new " + clazz, new Object[0]);
            }
            final Class<?> oClass = (Class<?>)Loader.loadClass(XmlConfiguration.class, clazz);
            final Map<String, Object> namedArgMap = new HashMap<String, Object>();
            final List<Object> arguments = new LinkedList<Object>();
            for (final XmlParser.Node child : argNodes) {
                final String namedAttribute = child.getAttribute("name");
                final Object value = this.value(obj, child);
                if (namedAttribute != null) {
                    namedArgMap.put(namedAttribute, value);
                }
                arguments.add(value);
            }
            Object nobj;
            try {
                if (namedArgMap.size() > 0) {
                    XmlConfiguration.LOG.debug("using named mapping", new Object[0]);
                    nobj = TypeUtil.construct(oClass, arguments.toArray(), namedArgMap);
                }
                else {
                    XmlConfiguration.LOG.debug("using normal mapping", new Object[0]);
                    nobj = TypeUtil.construct(oClass, arguments.toArray());
                }
            }
            catch (NoSuchMethodException e) {
                throw new IllegalStateException("No suitable constructor: " + node + " on " + obj);
            }
            if (id != null) {
                this._configuration.getIdMap().put(id, nobj);
            }
            this._configuration.initializeDefaults(nobj);
            this.configure(nobj, node, aoeNode.getNext());
            return nobj;
        }
        
        private Object refObj(Object obj, final XmlParser.Node node) throws Exception {
            String refid = node.getAttribute("refid");
            if (refid == null) {
                refid = node.getAttribute("id");
            }
            obj = this._configuration.getIdMap().get(refid);
            if (obj == null && node.size() > 0) {
                throw new IllegalStateException("No object for refid=" + refid);
            }
            this.configure(obj, node, 0);
            return obj;
        }
        
        private Object newArray(final Object obj, final XmlParser.Node node) throws Exception {
            final AttrOrElementNode aoeNode = new AttrOrElementNode(obj, node, new String[] { "Id", "Type", "Item" });
            final String id = aoeNode.getString("Id");
            final String type = aoeNode.getString("Type");
            final List<XmlParser.Node> items = aoeNode.getNodes("Item");
            Class<?> aClass = Object.class;
            if (type != null) {
                aClass = TypeUtil.fromName(type);
                if (aClass == null) {
                    final String s = type;
                    switch (s) {
                        case "String": {
                            aClass = String.class;
                            break;
                        }
                        case "URL": {
                            aClass = URL.class;
                            break;
                        }
                        case "InetAddress": {
                            aClass = InetAddress.class;
                            break;
                        }
                        default: {
                            aClass = (Class<?>)Loader.loadClass(XmlConfiguration.class, type);
                            break;
                        }
                    }
                }
            }
            Object al = null;
            for (final XmlParser.Node item : items) {
                final String nid = item.getAttribute("id");
                final Object v = this.value(obj, item);
                al = LazyList.add(al, (v == null && aClass.isPrimitive()) ? Integer.valueOf(0) : v);
                if (nid != null) {
                    this._configuration.getIdMap().put(nid, v);
                }
            }
            final Object array = LazyList.toArray(al, aClass);
            if (id != null) {
                this._configuration.getIdMap().put(id, array);
            }
            return array;
        }
        
        private Object newMap(final Object obj, final XmlParser.Node node) throws Exception {
            final AttrOrElementNode aoeNode = new AttrOrElementNode(node, new String[] { "Id", "Entry" });
            final String id = aoeNode.getString("Id");
            final List<XmlParser.Node> entries = aoeNode.getNodes("Entry");
            final Map<Object, Object> map = new HashMap<Object, Object>();
            if (id != null) {
                this._configuration.getIdMap().put(id, map);
            }
            for (final XmlParser.Node entry : entries) {
                if (!entry.getTag().equals("Entry")) {
                    throw new IllegalStateException("Not an Entry");
                }
                XmlParser.Node key = null;
                XmlParser.Node value = null;
                for (final Object object : entry) {
                    if (object instanceof String) {
                        continue;
                    }
                    final XmlParser.Node item = (XmlParser.Node)object;
                    if (!item.getTag().equals("Item")) {
                        throw new IllegalStateException("Not an Item");
                    }
                    if (key == null) {
                        key = item;
                    }
                    else {
                        value = item;
                    }
                }
                if (key == null || value == null) {
                    throw new IllegalStateException("Missing Item in Entry");
                }
                final String kid = key.getAttribute("id");
                final String vid = value.getAttribute("id");
                final Object k = this.value(obj, key);
                final Object v = this.value(obj, value);
                map.put(k, v);
                if (kid != null) {
                    this._configuration.getIdMap().put(kid, k);
                }
                if (vid == null) {
                    continue;
                }
                this._configuration.getIdMap().put(vid, v);
            }
            return map;
        }
        
        private Object propertyObj(final XmlParser.Node node) throws Exception {
            final AttrOrElementNode aoeNode = new AttrOrElementNode(node, new String[] { "Id", "Name", "Deprecated", "Default" });
            final String id = aoeNode.getString("Id");
            final String name = aoeNode.getString("Name", true);
            final List<Object> deprecated = aoeNode.getList("Deprecated");
            final String dftValue = aoeNode.getString("Default");
            final Map<String, String> properties = this._configuration.getProperties();
            String value = properties.get(name);
            String alternate = null;
            if (!deprecated.isEmpty()) {
                for (final Object d : deprecated) {
                    final String v = properties.get(StringUtil.valueOf(d));
                    if (v != null) {
                        if (value == null) {
                            XmlConfiguration.LOG.warn("Property '{}' is deprecated, use '{}' instead", d, name);
                        }
                        else {
                            XmlConfiguration.LOG.warn("Property '{}' is deprecated, value from '{}' used", d, name);
                        }
                    }
                    if (alternate == null) {
                        alternate = v;
                    }
                }
            }
            if (value == null) {
                value = alternate;
            }
            if (value == null) {
                value = dftValue;
            }
            if (id != null) {
                this._configuration.getIdMap().put(id, value);
            }
            return value;
        }
        
        private Object systemPropertyObj(final XmlParser.Node node) throws Exception {
            final AttrOrElementNode aoeNode = new AttrOrElementNode(node, new String[] { "Id", "Name", "Deprecated", "Default" });
            final String id = aoeNode.getString("Id");
            final String name = aoeNode.getString("Name", true);
            final List<Object> deprecated = aoeNode.getList("Deprecated");
            final String dftValue = aoeNode.getString("Default");
            String value = System.getProperty(name);
            String alternate = null;
            if (!deprecated.isEmpty()) {
                for (final Object d : deprecated) {
                    final String v = System.getProperty(StringUtil.valueOf(d));
                    if (v != null) {
                        if (value == null) {
                            XmlConfiguration.LOG.warn("SystemProperty '{}' is deprecated, use '{}' instead", d, name);
                        }
                        else {
                            XmlConfiguration.LOG.warn("SystemProperty '{}' is deprecated, value from '{}' used", d, name);
                        }
                    }
                    if (alternate == null) {
                        alternate = v;
                    }
                }
            }
            if (value == null) {
                value = alternate;
            }
            if (value == null) {
                value = dftValue;
            }
            if (id != null) {
                this._configuration.getIdMap().put(id, value);
            }
            return value;
        }
        
        private Object envObj(final XmlParser.Node node) throws Exception {
            final AttrOrElementNode aoeNode = new AttrOrElementNode(node, new String[] { "Id", "Name", "Deprecated", "Default" });
            final String id = aoeNode.getString("Id");
            final String name = aoeNode.getString("Name", true);
            final List<Object> deprecated = aoeNode.getList("Deprecated");
            final String dftValue = aoeNode.getString("Default");
            String value = System.getenv(name);
            if (value == null && !deprecated.isEmpty()) {
                for (final Object d : deprecated) {
                    value = System.getenv(StringUtil.valueOf(d));
                    if (value != null) {
                        XmlConfiguration.LOG.warn("Property '{}' is deprecated, use '{}' instead", d, name);
                        break;
                    }
                }
            }
            if (value == null) {
                value = dftValue;
            }
            if (id != null) {
                this._configuration.getIdMap().put(id, value);
            }
            return value;
        }
        
        private Object value(final Object obj, final XmlParser.Node node) throws Exception {
            final String type = node.getAttribute("type");
            final String ref = node.getAttribute("ref");
            Object value;
            if (ref != null) {
                value = this._configuration.getIdMap().get(ref);
            }
            else if (node.size() == 0) {
                if ("String".equals(type)) {
                    return "";
                }
                return null;
            }
            else {
                int first = 0;
                int last = node.size() - 1;
                if (type == null || !"String".equals(type)) {
                    while (first <= last) {
                        Object item = node.get(first);
                        if (!(item instanceof String)) {
                            break;
                        }
                        item = ((String)item).trim();
                        if (((String)item).length() > 0) {
                            break;
                        }
                        ++first;
                    }
                    while (first < last) {
                        Object item = node.get(last);
                        if (!(item instanceof String)) {
                            break;
                        }
                        item = ((String)item).trim();
                        if (((String)item).length() > 0) {
                            break;
                        }
                        --last;
                    }
                    if (first > last) {
                        return null;
                    }
                }
                if (first == last) {
                    value = this.itemValue(obj, node.get(first));
                }
                else {
                    final StringBuilder buf = new StringBuilder();
                    for (int i = first; i <= last; ++i) {
                        final Object item2 = node.get(i);
                        buf.append(this.itemValue(obj, item2));
                    }
                    value = buf.toString();
                }
            }
            if (value == null) {
                if ("String".equals(type)) {
                    return "";
                }
                return null;
            }
            else if (type == null) {
                if (value instanceof String) {
                    return ((String)value).trim();
                }
                return value;
            }
            else {
                if (isTypeMatchingClass(type, String.class)) {
                    return value.toString();
                }
                final Class<?> pClass = TypeUtil.fromName(type);
                if (pClass != null) {
                    return TypeUtil.valueOf(pClass, value.toString());
                }
                if (isTypeMatchingClass(type, URL.class)) {
                    if (value instanceof URL) {
                        return value;
                    }
                    try {
                        return new URL(value.toString());
                    }
                    catch (MalformedURLException e) {
                        throw new InvocationTargetException(e);
                    }
                }
                if (isTypeMatchingClass(type, InetAddress.class)) {
                    if (value instanceof InetAddress) {
                        return value;
                    }
                    try {
                        return InetAddress.getByName(value.toString());
                    }
                    catch (UnknownHostException e2) {
                        throw new InvocationTargetException(e2);
                    }
                }
                for (final Class<?> collectionClass : XmlConfiguration.__supportedCollections) {
                    if (isTypeMatchingClass(type, collectionClass)) {
                        return convertArrayToCollection(value, collectionClass);
                    }
                }
                throw new IllegalStateException("Unknown type " + type);
            }
        }
        
        private static boolean isTypeMatchingClass(final String type, final Class<?> classToMatch) {
            return classToMatch.getSimpleName().equalsIgnoreCase(type) || classToMatch.getName().equals(type);
        }
        
        private Object itemValue(final Object obj, final Object item) throws Exception {
            if (item instanceof String) {
                return item;
            }
            final XmlParser.Node node = (XmlParser.Node)item;
            final String tag = node.getTag();
            if ("Call".equals(tag)) {
                return this.call(obj, node);
            }
            if ("Get".equals(tag)) {
                return this.get(obj, node);
            }
            if ("New".equals(tag)) {
                return this.newObj(obj, node);
            }
            if ("Ref".equals(tag)) {
                return this.refObj(obj, node);
            }
            if ("Array".equals(tag)) {
                return this.newArray(obj, node);
            }
            if ("Map".equals(tag)) {
                return this.newMap(obj, node);
            }
            if ("Property".equals(tag)) {
                return this.propertyObj(node);
            }
            if ("SystemProperty".equals(tag)) {
                return this.systemPropertyObj(node);
            }
            if ("Env".equals(tag)) {
                return this.envObj(node);
            }
            XmlConfiguration.LOG.warn("Unknown value tag: " + node, new Throwable());
            return null;
        }
        
        private class AttrOrElementNode
        {
            final Object _obj;
            final XmlParser.Node _node;
            final Set<String> _elements;
            final int _next;
            
            AttrOrElementNode(final JettyXmlConfiguration jettyXmlConfiguration, final XmlParser.Node node, final String... elements) {
                this(jettyXmlConfiguration, (Object)null, node, elements);
            }
            
            AttrOrElementNode(final Object obj, final XmlParser.Node node, final String... elements) {
                this._elements = new HashSet<String>();
                this._obj = obj;
                this._node = node;
                for (final String e : elements) {
                    this._elements.add(e);
                }
                int next = 0;
                for (final Object o : this._node) {
                    if (o instanceof String) {
                        if (((String)o).trim().length() != 0) {
                            break;
                        }
                        ++next;
                    }
                    else {
                        if (!(o instanceof XmlParser.Node)) {
                            break;
                        }
                        final XmlParser.Node n = (XmlParser.Node)o;
                        if (!this._elements.contains(n.getTag())) {
                            break;
                        }
                        ++next;
                    }
                }
                this._next = next;
            }
            
            public int getNext() {
                return this._next;
            }
            
            public String getString(final String elementName) throws Exception {
                return StringUtil.valueOf(this.get(elementName, false));
            }
            
            public String getString(final String elementName, final boolean manditory) throws Exception {
                return StringUtil.valueOf(this.get(elementName, manditory));
            }
            
            public Object get(final String elementName, final boolean manditory) throws Exception {
                final String attrName = StringUtil.asciiToLowerCase(elementName);
                Object value;
                final String attr = (String)(value = this._node.getAttribute(attrName));
                for (int i = 0; i < this._next; ++i) {
                    final Object o = this._node.get(i);
                    if (o instanceof XmlParser.Node) {
                        final XmlParser.Node n = (XmlParser.Node)o;
                        if (elementName.equals(n.getTag())) {
                            if (attr != null) {
                                throw new IllegalStateException("Cannot have attr '" + attrName + "' and element '" + elementName + "'");
                            }
                            value = JettyXmlConfiguration.this.value(this._obj, n);
                            break;
                        }
                    }
                }
                if (manditory && value == null) {
                    throw new IllegalStateException("Must have attr '" + attrName + "' or element '" + elementName + "'");
                }
                return value;
            }
            
            public List<Object> getList(final String elementName) throws Exception {
                return this.getList(elementName, false);
            }
            
            public List<Object> getList(final String elementName, final boolean manditory) throws Exception {
                final String attrName = StringUtil.asciiToLowerCase(elementName);
                final List<Object> values = new ArrayList<Object>();
                final String attr = this._node.getAttribute(attrName);
                if (attr != null) {
                    values.addAll(StringUtil.csvSplit(null, attr, 0, attr.length()));
                }
                for (int i = 0; i < this._next; ++i) {
                    final Object o = this._node.get(i);
                    if (o instanceof XmlParser.Node) {
                        final XmlParser.Node n = (XmlParser.Node)o;
                        if (elementName.equals(n.getTag())) {
                            if (attr != null) {
                                throw new IllegalStateException("Cannot have attr '" + attrName + "' and element '" + elementName + "'");
                            }
                            values.add(JettyXmlConfiguration.this.value(this._obj, n));
                        }
                    }
                }
                if (manditory && values.isEmpty()) {
                    throw new IllegalStateException("Must have attr '" + attrName + "' or element '" + elementName + "'");
                }
                return values;
            }
            
            public List<XmlParser.Node> getNodes(final String elementName) throws Exception {
                final String attrName = StringUtil.asciiToLowerCase(elementName);
                final List<XmlParser.Node> values = new ArrayList<XmlParser.Node>();
                final String attr = this._node.getAttribute(attrName);
                if (attr != null) {
                    for (final String a : StringUtil.csvSplit(null, attr, 0, attr.length())) {
                        final XmlParser.Node n = new XmlParser.Node(null, elementName, null);
                        ((AbstractList<String>)n).add(a);
                        values.add(n);
                    }
                }
                for (int i = 0; i < this._next; ++i) {
                    final Object o = this._node.get(i);
                    if (o instanceof XmlParser.Node) {
                        final XmlParser.Node n = (XmlParser.Node)o;
                        if (elementName.equals(n.getTag())) {
                            if (attr != null) {
                                throw new IllegalStateException("Cannot have attr '" + attrName + "' and element '" + elementName + "'");
                            }
                            values.add(n);
                        }
                    }
                }
                return values;
            }
        }
    }
}
