// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.xml;

import org.mortbay.component.LifeCycle;
import org.mortbay.resource.Resource;
import java.util.Properties;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import org.mortbay.util.LazyList;
import org.mortbay.util.TypeUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.xml.sax.SAXException;
import java.util.HashMap;
import java.io.IOException;
import java.net.URL;
import org.mortbay.log.Log;
import org.mortbay.util.Loader;
import java.util.Map;

public class XmlConfiguration
{
    private static Class[] __primitives;
    private static Class[] __primitiveHolders;
    private static final Integer ZERO;
    private static XmlParser __parser;
    private XmlParser.Node _config;
    private Map _idMap;
    private Map _propertyMap;
    
    private static synchronized void initParser() throws IOException {
        if (XmlConfiguration.__parser != null) {
            return;
        }
        XmlConfiguration.__parser = new XmlParser();
        try {
            final URL configURL = Loader.getResource(XmlConfiguration.class, "org/mortbay/xml/configure_6_0.dtd", true);
            XmlConfiguration.__parser.redirectEntity("configure.dtd", configURL);
            XmlConfiguration.__parser.redirectEntity("configure_1_3.dtd", configURL);
            XmlConfiguration.__parser.redirectEntity("http://jetty.mortbay.org/configure.dtd", configURL);
            XmlConfiguration.__parser.redirectEntity("-//Mort Bay Consulting//DTD Configure//EN", configURL);
            XmlConfiguration.__parser.redirectEntity("http://jetty.mortbay.org/configure_1_3.dtd", configURL);
            XmlConfiguration.__parser.redirectEntity("-//Mort Bay Consulting//DTD Configure 1.3//EN", configURL);
            XmlConfiguration.__parser.redirectEntity("configure_1_2.dtd", configURL);
            XmlConfiguration.__parser.redirectEntity("http://jetty.mortbay.org/configure_1_2.dtd", configURL);
            XmlConfiguration.__parser.redirectEntity("-//Mort Bay Consulting//DTD Configure 1.2//EN", configURL);
            XmlConfiguration.__parser.redirectEntity("configure_1_1.dtd", configURL);
            XmlConfiguration.__parser.redirectEntity("http://jetty.mortbay.org/configure_1_1.dtd", configURL);
            XmlConfiguration.__parser.redirectEntity("-//Mort Bay Consulting//DTD Configure 1.1//EN", configURL);
            XmlConfiguration.__parser.redirectEntity("configure_1_0.dtd", configURL);
            XmlConfiguration.__parser.redirectEntity("http://jetty.mortbay.org/configure_1_0.dtd", configURL);
            XmlConfiguration.__parser.redirectEntity("-//Mort Bay Consulting//DTD Configure 1.0//EN", configURL);
        }
        catch (ClassNotFoundException e) {
            Log.warn(e.toString());
            Log.debug(e);
        }
    }
    
    public XmlConfiguration(final URL configuration) throws SAXException, IOException {
        this._idMap = new HashMap();
        this._propertyMap = new HashMap();
        initParser();
        synchronized (XmlConfiguration.__parser) {
            this._config = XmlConfiguration.__parser.parse(configuration.toString());
        }
    }
    
    public XmlConfiguration(String configuration) throws SAXException, IOException {
        this._idMap = new HashMap();
        this._propertyMap = new HashMap();
        initParser();
        configuration = "<?xml version=\"1.0\"  encoding=\"ISO-8859-1\"?>\n<!DOCTYPE Configure PUBLIC \"-//Mort Bay Consulting//DTD Configure 1.2//EN\" \"http://jetty.mortbay.org/configure_1_2.dtd\">" + configuration;
        final InputSource source = new InputSource(new StringReader(configuration));
        synchronized (XmlConfiguration.__parser) {
            this._config = XmlConfiguration.__parser.parse(source);
        }
    }
    
    public XmlConfiguration(final InputStream configuration) throws SAXException, IOException {
        this._idMap = new HashMap();
        this._propertyMap = new HashMap();
        initParser();
        final InputSource source = new InputSource(configuration);
        synchronized (XmlConfiguration.__parser) {
            this._config = XmlConfiguration.__parser.parse(source);
        }
    }
    
    public Map getIdMap() {
        return this._idMap;
    }
    
    public void setIdMap(final Map map) {
        this._idMap = map;
    }
    
    public void setProperties(final Map map) {
        this._propertyMap = map;
    }
    
    public Map getProperties() {
        return this._propertyMap;
    }
    
    public void configure(final Object obj) throws Exception {
        final Class oClass = this.nodeClass(this._config);
        if (!oClass.isInstance(obj)) {
            throw new IllegalArgumentException("Object is not of type " + oClass);
        }
        this.configure(obj, this._config, 0);
    }
    
    public Object configure() throws Exception {
        final Class oClass = this.nodeClass(this._config);
        final String id = this._config.getAttribute("id");
        Object obj = (id == null) ? null : this._idMap.get(id);
        if (obj == null && oClass != null) {
            obj = oClass.newInstance();
        }
        if (oClass != null && !oClass.isInstance(obj)) {
            throw new ClassCastException(oClass.toString());
        }
        this.configure(obj, this._config, 0);
        return obj;
    }
    
    private Class nodeClass(final XmlParser.Node node) throws ClassNotFoundException {
        final String className = node.getAttribute("class");
        if (className == null) {
            return null;
        }
        return Loader.loadClass(XmlConfiguration.class, className, true);
    }
    
    private void configure(final Object obj, final XmlParser.Node cfg, int i) throws Exception {
        final String id = cfg.getAttribute("id");
        if (id != null) {
            this._idMap.put(id, obj);
        }
        while (i < cfg.size()) {
            final Object o = cfg.get(i);
            if (!(o instanceof String)) {
                final XmlParser.Node node = (XmlParser.Node)o;
                try {
                    final String tag = node.getTag();
                    if ("Set".equals(tag)) {
                        this.set(obj, node);
                    }
                    else if ("Put".equals(tag)) {
                        this.put(obj, node);
                    }
                    else if ("Call".equals(tag)) {
                        this.call(obj, node);
                    }
                    else if ("Get".equals(tag)) {
                        this.get(obj, node);
                    }
                    else if ("New".equals(tag)) {
                        this.newObj(obj, node);
                    }
                    else if ("Array".equals(tag)) {
                        this.newArray(obj, node);
                    }
                    else if ("Ref".equals(tag)) {
                        this.refObj(obj, node);
                    }
                    else {
                        if (!"Property".equals(tag)) {
                            throw new IllegalStateException("Unknown tag: " + tag);
                        }
                        this.propertyObj(obj, node);
                    }
                }
                catch (Exception e) {
                    Log.warn("Config error at " + node, e.toString());
                    throw e;
                }
            }
            ++i;
        }
    }
    
    private void set(Object obj, final XmlParser.Node node) throws Exception {
        final String attr = node.getAttribute("name");
        final String name = "set" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
        final Object value = this.value(obj, node);
        final Object[] arg = { value };
        Class oClass = this.nodeClass(node);
        if (oClass != null) {
            obj = null;
        }
        else {
            oClass = obj.getClass();
        }
        final Class[] vClass = { Object.class };
        if (value != null) {
            vClass[0] = value.getClass();
        }
        if (Log.isDebugEnabled()) {
            Log.debug("XML " + ((obj != null) ? obj.toString() : oClass.getName()) + "." + name + "(" + value + ")");
        }
        try {
            final Method set = oClass.getMethod(name, (Class[])vClass);
            set.invoke(obj, arg);
            return;
        }
        catch (IllegalArgumentException e) {
            Log.ignore(e);
        }
        catch (IllegalAccessException e2) {
            Log.ignore(e2);
        }
        catch (NoSuchMethodException e3) {
            Log.ignore(e3);
        }
        try {
            final Field type = vClass[0].getField("TYPE");
            vClass[0] = (Class)type.get(null);
            final Method set2 = oClass.getMethod(name, (Class[])vClass);
            set2.invoke(obj, arg);
            return;
        }
        catch (NoSuchFieldException e4) {
            Log.ignore(e4);
        }
        catch (IllegalArgumentException e) {
            Log.ignore(e);
        }
        catch (IllegalAccessException e2) {
            Log.ignore(e2);
        }
        catch (NoSuchMethodException e3) {
            Log.ignore(e3);
        }
        try {
            final Field field = oClass.getField(attr);
            if (Modifier.isPublic(field.getModifiers())) {
                field.set(obj, value);
                return;
            }
        }
        catch (NoSuchFieldException e4) {
            Log.ignore(e4);
        }
        final Method[] sets = oClass.getMethods();
        Method set2 = null;
        for (int s = 0; sets != null && s < sets.length; ++s) {
            if (name.equals(sets[s].getName()) && sets[s].getParameterTypes().length == 1) {
                try {
                    set2 = sets[s];
                    sets[s].invoke(obj, arg);
                    return;
                }
                catch (IllegalArgumentException e5) {
                    Log.ignore(e5);
                }
                catch (IllegalAccessException e6) {
                    Log.ignore(e6);
                }
            }
        }
        if (set2 != null) {
            try {
                Class sClass = set2.getParameterTypes()[0];
                if (sClass.isPrimitive()) {
                    for (int t = 0; t < XmlConfiguration.__primitives.length; ++t) {
                        if (sClass.equals(XmlConfiguration.__primitives[t])) {
                            sClass = XmlConfiguration.__primitiveHolders[t];
                            break;
                        }
                    }
                }
                final Constructor cons = sClass.getConstructor((Class[])vClass);
                arg[0] = cons.newInstance(arg);
                set2.invoke(obj, arg);
                return;
            }
            catch (NoSuchMethodException e7) {
                Log.ignore(e7);
            }
            catch (IllegalAccessException e8) {
                Log.ignore(e8);
            }
            catch (InstantiationException e9) {
                Log.ignore(e9);
            }
        }
        throw new NoSuchMethodException(oClass + "." + name + "(" + vClass[0] + ")");
    }
    
    private void put(final Object obj, final XmlParser.Node node) throws Exception {
        if (!(obj instanceof Map)) {
            throw new IllegalArgumentException("Object for put is not a Map: " + obj);
        }
        final Map map = (Map)obj;
        final String name = node.getAttribute("name");
        final Object value = this.value(obj, node);
        map.put(name, value);
        if (Log.isDebugEnabled()) {
            Log.debug("XML " + obj + ".put(" + name + "," + value + ")");
        }
    }
    
    private Object get(Object obj, final XmlParser.Node node) throws Exception {
        Class oClass = this.nodeClass(node);
        if (oClass != null) {
            obj = null;
        }
        else {
            oClass = obj.getClass();
        }
        final String name = node.getAttribute("name");
        final String id = node.getAttribute("id");
        if (Log.isDebugEnabled()) {
            Log.debug("XML get " + name);
        }
        try {
            final Method method = oClass.getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1), (Class[])null);
            obj = method.invoke(obj, (Object[])null);
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
        if (id != null) {
            this._idMap.put(id, obj);
        }
        return obj;
    }
    
    private Object call(Object obj, final XmlParser.Node node) throws Exception {
        final String id = node.getAttribute("id");
        Class oClass = this.nodeClass(node);
        if (oClass != null) {
            obj = null;
        }
        else if (obj != null) {
            oClass = obj.getClass();
        }
        if (oClass == null) {
            throw new IllegalArgumentException(node.toString());
        }
        int size = 0;
        int argi = node.size();
        for (int i = 0; i < node.size(); ++i) {
            final Object o = node.get(i);
            if (!(o instanceof String)) {
                if (!((XmlParser.Node)o).getTag().equals("Arg")) {
                    argi = i;
                    break;
                }
                ++size;
            }
        }
        final Object[] arg = new Object[size];
        int j = 0;
        int k = 0;
        while (k < size) {
            final Object o2 = node.get(j);
            if (!(o2 instanceof String)) {
                arg[k++] = this.value(obj, (XmlParser.Node)o2);
            }
            ++j;
        }
        final String method = node.getAttribute("name");
        if (Log.isDebugEnabled()) {
            Log.debug("XML call " + method);
        }
        final Method[] methods = oClass.getMethods();
        for (int c = 0; methods != null && c < methods.length; ++c) {
            if (methods[c].getName().equals(method)) {
                if (methods[c].getParameterTypes().length == size) {
                    if (Modifier.isStatic(methods[c].getModifiers()) == (obj == null)) {
                        if (obj != null || methods[c].getDeclaringClass() == oClass) {
                            Object n = null;
                            boolean called = false;
                            try {
                                n = methods[c].invoke(obj, arg);
                                called = true;
                            }
                            catch (IllegalAccessException e) {
                                Log.ignore(e);
                            }
                            catch (IllegalArgumentException e2) {
                                Log.ignore(e2);
                            }
                            if (called) {
                                if (id != null) {
                                    this._idMap.put(id, n);
                                }
                                this.configure(n, node, argi);
                                return n;
                            }
                        }
                    }
                }
            }
        }
        throw new IllegalStateException("No Method: " + node + " on " + oClass);
    }
    
    private Object newObj(final Object obj, final XmlParser.Node node) throws Exception {
        final Class oClass = this.nodeClass(node);
        final String id = node.getAttribute("id");
        int size = 0;
        int argi = node.size();
        for (int i = 0; i < node.size(); ++i) {
            final Object o = node.get(i);
            if (!(o instanceof String)) {
                if (!((XmlParser.Node)o).getTag().equals("Arg")) {
                    argi = i;
                    break;
                }
                ++size;
            }
        }
        final Object[] arg = new Object[size];
        int j = 0;
        int k = 0;
        while (k < size) {
            final Object o2 = node.get(j);
            if (!(o2 instanceof String)) {
                arg[k++] = this.value(obj, (XmlParser.Node)o2);
            }
            ++j;
        }
        if (Log.isDebugEnabled()) {
            Log.debug("XML new " + oClass);
        }
        final Constructor[] constructors = oClass.getConstructors();
        for (int c = 0; constructors != null && c < constructors.length; ++c) {
            if (constructors[c].getParameterTypes().length == size) {
                Object n = null;
                boolean called = false;
                try {
                    n = constructors[c].newInstance(arg);
                    called = true;
                }
                catch (IllegalAccessException e) {
                    Log.ignore(e);
                }
                catch (InstantiationException e2) {
                    Log.ignore(e2);
                }
                catch (IllegalArgumentException e3) {
                    Log.ignore(e3);
                }
                if (called) {
                    if (id != null) {
                        this._idMap.put(id, n);
                    }
                    this.configure(n, node, argi);
                    return n;
                }
            }
        }
        throw new IllegalStateException("No Constructor: " + node + " on " + obj);
    }
    
    private Object refObj(Object obj, final XmlParser.Node node) throws Exception {
        final String id = node.getAttribute("id");
        obj = this._idMap.get(id);
        if (obj == null) {
            throw new IllegalStateException("No object for id=" + id);
        }
        this.configure(obj, node, 0);
        return obj;
    }
    
    private Object newArray(final Object obj, final XmlParser.Node node) throws Exception {
        Class aClass = Object.class;
        final String type = node.getAttribute("type");
        final String id = node.getAttribute("id");
        if (type != null) {
            aClass = TypeUtil.fromName(type);
            if (aClass == null) {
                if ("String".equals(type)) {
                    aClass = String.class;
                }
                else if ("URL".equals(type)) {
                    aClass = URL.class;
                }
                else if ("InetAddress".equals(type)) {
                    aClass = InetAddress.class;
                }
                else {
                    aClass = Loader.loadClass(XmlConfiguration.class, type, true);
                }
            }
        }
        Object al = null;
        final Iterator iter = node.iterator("Item");
        while (iter.hasNext()) {
            final XmlParser.Node item = iter.next();
            final String nid = item.getAttribute("id");
            final Object v = this.value(obj, item);
            al = LazyList.add(al, (v == null && aClass.isPrimitive()) ? XmlConfiguration.ZERO : v);
            if (nid != null) {
                this._idMap.put(nid, v);
            }
        }
        final Object array = LazyList.toArray(al, aClass);
        if (id != null) {
            this._idMap.put(id, array);
        }
        return array;
    }
    
    private Object newMap(final Object obj, final XmlParser.Node node) throws Exception {
        final String id = node.getAttribute("id");
        final Map map = new HashMap();
        if (id != null) {
            this._idMap.put(id, map);
        }
        for (int i = 0; i < node.size(); ++i) {
            Object o = node.get(i);
            if (!(o instanceof String)) {
                final XmlParser.Node entry = (XmlParser.Node)o;
                if (!entry.getTag().equals("Entry")) {
                    throw new IllegalStateException("Not an Entry");
                }
                XmlParser.Node key = null;
                XmlParser.Node value = null;
                for (int j = 0; j < entry.size(); ++j) {
                    o = entry.get(j);
                    if (!(o instanceof String)) {
                        final XmlParser.Node item = (XmlParser.Node)o;
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
                    this._idMap.put(kid, k);
                }
                if (vid != null) {
                    this._idMap.put(vid, v);
                }
            }
        }
        return map;
    }
    
    private Object propertyObj(final Object obj, final XmlParser.Node node) throws Exception {
        final String id = node.getAttribute("id");
        final String name = node.getAttribute("name");
        final Object defval = node.getAttribute("default");
        Object prop = null;
        if (this._propertyMap != null && this._propertyMap.containsKey(name)) {
            prop = this._propertyMap.get(name);
        }
        else if (defval != null) {
            prop = defval;
        }
        if (id != null) {
            this._idMap.put(id, prop);
        }
        if (prop != null) {
            this.configure(prop, node, 0);
        }
        return prop;
    }
    
    private Object value(final Object obj, final XmlParser.Node node) throws Exception {
        Object value = null;
        final String type = node.getAttribute("type");
        final String ref = node.getAttribute("ref");
        if (ref != null) {
            value = this._idMap.get(ref);
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
                Object item = null;
                while (first <= last) {
                    item = node.get(first);
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
                    item = node.get(last);
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
                final StringBuffer buf = new StringBuffer();
                synchronized (buf) {
                    for (int i = first; i <= last; ++i) {
                        final Object item2 = node.get(i);
                        buf.append(this.itemValue(obj, item2));
                    }
                    value = buf.toString();
                }
            }
        }
        if (value == null) {
            if ("String".equals(type)) {
                return "";
            }
            return null;
        }
        else if (type == null) {
            if (value != null && value instanceof String) {
                return ((String)value).trim();
            }
            return value;
        }
        else {
            if ("String".equals(type) || "java.lang.String".equals(type)) {
                return value.toString();
            }
            final Class pClass = TypeUtil.fromName(type);
            if (pClass != null) {
                return TypeUtil.valueOf(pClass, value.toString());
            }
            if ("URL".equals(type) || "java.net.URL".equals(type)) {
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
            if ("InetAddress".equals(type) || "java.net.InetAddress".equals(type)) {
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
            throw new IllegalStateException("Unknown type " + type);
        }
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
            return this.propertyObj(obj, node);
        }
        if ("SystemProperty".equals(tag)) {
            final String name = node.getAttribute("name");
            final String defaultValue = node.getAttribute("default");
            return System.getProperty(name, defaultValue);
        }
        Log.warn("Unknown value tag: " + node, new Throwable());
        return null;
    }
    
    public static void main(final String[] args) {
        try {
            final Properties properties = new Properties();
            XmlConfiguration last = null;
            final Object[] obj = new Object[args.length];
            for (int i = 0; i < args.length; ++i) {
                if (args[i].toLowerCase().endsWith(".properties")) {
                    properties.load(Resource.newResource(args[i]).getInputStream());
                }
                else {
                    final XmlConfiguration configuration = new XmlConfiguration(Resource.newResource(args[i]).getURL());
                    if (last != null) {
                        configuration.getIdMap().putAll(last.getIdMap());
                    }
                    if (properties.size() > 0) {
                        configuration.setProperties(properties);
                    }
                    obj[i] = configuration.configure();
                    last = configuration;
                }
            }
            for (int i = 0; i < args.length; ++i) {
                if (obj[i] instanceof LifeCycle) {
                    final LifeCycle lc = (LifeCycle)obj[i];
                    if (!lc.isRunning()) {
                        lc.start();
                    }
                }
            }
        }
        catch (Exception e) {
            Log.warn("EXCEPTION ", e);
        }
    }
    
    static {
        XmlConfiguration.__primitives = new Class[] { Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE };
        XmlConfiguration.__primitiveHolders = new Class[] { Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class };
        ZERO = new Integer(0);
    }
}
