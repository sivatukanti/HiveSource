// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import org.apache.hadoop.util.StringInterner;
import java.util.StringTokenizer;
import org.apache.commons.collections.map.UnmodifiableMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.io.DataOutput;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableUtils;
import java.io.DataInput;
import java.util.ListIterator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonFactory;
import java.util.Enumeration;
import org.w3c.dom.Element;
import com.google.common.base.Strings;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import org.w3c.dom.Document;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.codehaus.stax2.XMLStreamReader2;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.StreamBootstrapper;
import com.ctc.wstx.io.SystemId;
import javax.xml.stream.XMLStreamException;
import java.net.URLConnection;
import java.net.JarURLConnection;
import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.io.InputStreamReader;
import com.google.common.base.Charsets;
import java.io.Reader;
import java.io.File;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.net.NetUtils;
import java.net.InetSocketAddress;
import org.apache.hadoop.security.alias.CredentialProviderFactory;
import org.apache.hadoop.security.alias.CredentialProvider;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.util.StringUtils;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Arrays;
import com.google.common.annotations.VisibleForTesting;
import java.io.InputStream;
import org.apache.hadoop.fs.Path;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import com.ctc.wstx.stax.WstxInputFactory;
import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.ArrayList;
import java.util.Set;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;
import java.util.Map;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class Configuration implements Iterable<Map.Entry<String, String>>, Writable
{
    private static final Logger LOG;
    private static final Logger LOG_DEPRECATION;
    private static final Set<String> TAGS;
    private boolean quietmode;
    private static final String DEFAULT_STRING_CHECK = "testingforemptydefaultvalue";
    private static boolean restrictSystemPropsDefault;
    private boolean restrictSystemProps;
    private boolean allowNullValueProperties;
    private ArrayList<Resource> resources;
    static final String UNKNOWN_RESOURCE = "Unknown";
    private Set<String> finalParameters;
    private boolean loadDefaults;
    private static final WeakHashMap<Configuration, Object> REGISTRY;
    private final Map<String, Properties> propertyTagsMap;
    private static final CopyOnWriteArrayList<String> defaultResources;
    private static final Map<ClassLoader, Map<String, WeakReference<Class<?>>>> CACHE_CLASSES;
    private static final Class<?> NEGATIVE_CACHE_SENTINEL;
    private volatile Map<String, String[]> updatingResource;
    private static final WstxInputFactory XML_INPUT_FACTORY;
    private static DeprecationDelta[] defaultDeprecations;
    private static AtomicReference<DeprecationContext> deprecationContext;
    private Properties properties;
    private Properties overlay;
    private ClassLoader classLoader;
    private static final int MAX_SUBST = 20;
    private static final int SUB_START_IDX = 0;
    private static final int SUB_END_IDX = 1;
    
    public static void addDeprecations(final DeprecationDelta[] deltas) {
        DeprecationContext prev;
        DeprecationContext next;
        do {
            prev = Configuration.deprecationContext.get();
            next = new DeprecationContext(prev, deltas);
        } while (!Configuration.deprecationContext.compareAndSet(prev, next));
    }
    
    @Deprecated
    public static void addDeprecation(final String key, final String[] newKeys, final String customMessage) {
        addDeprecations(new DeprecationDelta[] { new DeprecationDelta(key, newKeys, customMessage) });
    }
    
    public static void addDeprecation(final String key, final String newKey, final String customMessage) {
        addDeprecation(key, new String[] { newKey }, customMessage);
    }
    
    @Deprecated
    public static void addDeprecation(final String key, final String[] newKeys) {
        addDeprecation(key, newKeys, null);
    }
    
    public static void addDeprecation(final String key, final String newKey) {
        addDeprecation(key, new String[] { newKey }, null);
    }
    
    public static boolean isDeprecated(final String key) {
        return Configuration.deprecationContext.get().getDeprecatedKeyMap().containsKey(key);
    }
    
    private static String getDeprecatedKey(final String key) {
        return Configuration.deprecationContext.get().getReverseDeprecatedKeyMap().get(key);
    }
    
    private static DeprecatedKeyInfo getDeprecatedKeyInfo(final String key) {
        return Configuration.deprecationContext.get().getDeprecatedKeyMap().get(key);
    }
    
    public void setDeprecatedProperties() {
        final DeprecationContext deprecations = Configuration.deprecationContext.get();
        final Properties props = this.getProps();
        final Properties overlay = this.getOverlay();
        for (final Map.Entry<String, DeprecatedKeyInfo> entry : deprecations.getDeprecatedKeyMap().entrySet()) {
            final String depKey = entry.getKey();
            if (!overlay.contains(depKey)) {
                for (final String newKey : entry.getValue().newKeys) {
                    final String val = overlay.getProperty(newKey);
                    if (val != null) {
                        props.setProperty(depKey, val);
                        overlay.setProperty(depKey, val);
                        break;
                    }
                }
            }
        }
    }
    
    private String[] handleDeprecation(final DeprecationContext deprecations, String name) {
        if (null != name) {
            name = name.trim();
        }
        String[] names = { name };
        final DeprecatedKeyInfo keyInfo = deprecations.getDeprecatedKeyMap().get(name);
        if (keyInfo != null) {
            if (!keyInfo.getAndSetAccessed()) {
                this.logDeprecation(keyInfo.getWarningMessage(name));
            }
            names = keyInfo.newKeys;
        }
        final Properties overlayProperties = this.getOverlay();
        if (overlayProperties.isEmpty()) {
            return names;
        }
        for (final String n : names) {
            final String deprecatedKey = deprecations.getReverseDeprecatedKeyMap().get(n);
            if (deprecatedKey != null && !overlayProperties.containsKey(n)) {
                final String deprecatedValue = overlayProperties.getProperty(deprecatedKey);
                if (deprecatedValue != null) {
                    this.getProps().setProperty(n, deprecatedValue);
                    overlayProperties.setProperty(n, deprecatedValue);
                }
            }
        }
        return names;
    }
    
    private void handleDeprecation() {
        Configuration.LOG.debug("Handling deprecation for all properties in config...");
        final DeprecationContext deprecations = Configuration.deprecationContext.get();
        final Set<Object> keys = new HashSet<Object>();
        keys.addAll(this.getProps().keySet());
        for (final Object item : keys) {
            Configuration.LOG.debug("Handling deprecation for " + (String)item);
            this.handleDeprecation(deprecations, (String)item);
        }
    }
    
    public Configuration() {
        this(true);
    }
    
    public Configuration(final boolean loadDefaults) {
        this.quietmode = true;
        this.restrictSystemProps = Configuration.restrictSystemPropsDefault;
        this.allowNullValueProperties = false;
        this.resources = new ArrayList<Resource>();
        this.finalParameters = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        this.loadDefaults = true;
        this.propertyTagsMap = new ConcurrentHashMap<String, Properties>();
        this.classLoader = Thread.currentThread().getContextClassLoader();
        if (this.classLoader == null) {
            this.classLoader = Configuration.class.getClassLoader();
        }
        this.loadDefaults = loadDefaults;
        synchronized (Configuration.class) {
            Configuration.REGISTRY.put(this, null);
        }
    }
    
    public Configuration(final Configuration other) {
        this.quietmode = true;
        this.restrictSystemProps = Configuration.restrictSystemPropsDefault;
        this.allowNullValueProperties = false;
        this.resources = new ArrayList<Resource>();
        this.finalParameters = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        this.loadDefaults = true;
        this.propertyTagsMap = new ConcurrentHashMap<String, Properties>();
        this.classLoader = Thread.currentThread().getContextClassLoader();
        if (this.classLoader == null) {
            this.classLoader = Configuration.class.getClassLoader();
        }
        synchronized (other) {
            other.getProps();
            this.resources = (ArrayList<Resource>)other.resources.clone();
            if (other.properties != null) {
                this.properties = (Properties)other.properties.clone();
            }
            if (other.overlay != null) {
                this.overlay = (Properties)other.overlay.clone();
            }
            this.restrictSystemProps = other.restrictSystemProps;
            if (other.updatingResource != null) {
                this.updatingResource = new ConcurrentHashMap<String, String[]>(other.updatingResource);
            }
            (this.finalParameters = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>())).addAll(other.finalParameters);
            this.propertyTagsMap.putAll(other.propertyTagsMap);
        }
        synchronized (Configuration.class) {
            Configuration.REGISTRY.put(this, null);
        }
        this.classLoader = other.classLoader;
        this.loadDefaults = other.loadDefaults;
        this.setQuietMode(other.getQuietMode());
    }
    
    public static synchronized void reloadExistingConfigurations() {
        if (Configuration.LOG.isDebugEnabled()) {
            Configuration.LOG.debug("Reloading " + Configuration.REGISTRY.keySet().size() + " existing configurations");
        }
        for (final Configuration conf : Configuration.REGISTRY.keySet()) {
            conf.reloadConfiguration();
        }
    }
    
    public static synchronized void addDefaultResource(final String name) {
        if (!Configuration.defaultResources.contains(name)) {
            Configuration.defaultResources.add(name);
            for (final Configuration conf : Configuration.REGISTRY.keySet()) {
                if (conf.loadDefaults) {
                    conf.reloadConfiguration();
                }
            }
        }
    }
    
    public static void setRestrictSystemPropertiesDefault(final boolean val) {
        Configuration.restrictSystemPropsDefault = val;
    }
    
    public void setRestrictSystemProperties(final boolean val) {
        this.restrictSystemProps = val;
    }
    
    public void addResource(final String name) {
        this.addResourceObject(new Resource(name));
    }
    
    public void addResource(final String name, final boolean restrictedParser) {
        this.addResourceObject(new Resource(name, restrictedParser));
    }
    
    public void addResource(final URL url) {
        this.addResourceObject(new Resource(url));
    }
    
    public void addResource(final URL url, final boolean restrictedParser) {
        this.addResourceObject(new Resource(url, restrictedParser));
    }
    
    public void addResource(final Path file) {
        this.addResourceObject(new Resource(file));
    }
    
    public void addResource(final Path file, final boolean restrictedParser) {
        this.addResourceObject(new Resource(file, restrictedParser));
    }
    
    public void addResource(final InputStream in) {
        this.addResourceObject(new Resource(in));
    }
    
    public void addResource(final InputStream in, final boolean restrictedParser) {
        this.addResourceObject(new Resource(in, restrictedParser));
    }
    
    public void addResource(final InputStream in, final String name) {
        this.addResourceObject(new Resource(in, name));
    }
    
    public void addResource(final InputStream in, final String name, final boolean restrictedParser) {
        this.addResourceObject(new Resource(in, name, restrictedParser));
    }
    
    public void addResource(final Configuration conf) {
        this.addResourceObject(new Resource(conf.getProps(), conf.restrictSystemProps));
    }
    
    public synchronized void reloadConfiguration() {
        this.properties = null;
        this.finalParameters.clear();
    }
    
    private synchronized void addResourceObject(final Resource resource) {
        this.resources.add(resource);
        this.restrictSystemProps |= resource.isParserRestricted();
        this.reloadConfiguration();
    }
    
    private static int[] findSubVariable(final String eval) {
        final int[] result = { -1, -1 };
        int matchedLen;
        int i;
        int subStart;
        Label_0168:Label_0157:
        for (int matchStart = 1, leftBrace = eval.indexOf(123, matchStart); leftBrace > 0 && leftBrace + "{c".length() < eval.length(); leftBrace = eval.indexOf(123, matchStart)) {
            matchedLen = 0;
            if (eval.charAt(leftBrace - 1) == '$') {
                subStart = (i = leftBrace + 1);
                while (i < eval.length()) {
                    switch (eval.charAt(i)) {
                        case '}': {
                            if (matchedLen > 0) {
                                result[1] = (result[0] = subStart) + matchedLen;
                                break Label_0168;
                            }
                        }
                        case ' ':
                        case '$': {
                            matchStart = i + 1;
                            continue Label_0157;
                        }
                        default: {
                            ++matchedLen;
                            ++i;
                            continue;
                        }
                    }
                }
                break;
            }
            matchStart = leftBrace + 1;
        }
        return result;
    }
    
    private String substituteVars(final String expr) {
        if (expr == null) {
            return null;
        }
        String eval = expr;
        for (int s = 0; s < 20; ++s) {
            final int[] varBounds = findSubVariable(eval);
            if (varBounds[0] == -1) {
                return eval;
            }
            final String var = eval.substring(varBounds[0], varBounds[1]);
            String val = null;
            if (!this.restrictSystemProps) {
                try {
                    if (var.startsWith("env.") && 4 < var.length()) {
                        final String v = var.substring(4);
                        int i = 0;
                        while (i < v.length()) {
                            final char c = v.charAt(i);
                            if (c == ':' && i < v.length() - 1 && v.charAt(i + 1) == '-') {
                                val = this.getenv(v.substring(0, i));
                                if (val == null || val.length() == 0) {
                                    val = v.substring(i + 2);
                                    break;
                                }
                                break;
                            }
                            else if (c == '-') {
                                val = this.getenv(v.substring(0, i));
                                if (val == null) {
                                    val = v.substring(i + 1);
                                    break;
                                }
                                break;
                            }
                            else {
                                ++i;
                            }
                        }
                        if (i == v.length()) {
                            val = this.getenv(v);
                        }
                    }
                    else {
                        val = this.getProperty(var);
                    }
                }
                catch (SecurityException se) {
                    Configuration.LOG.warn("Unexpected SecurityException in Configuration", se);
                }
            }
            if (val == null) {
                val = this.getRaw(var);
            }
            if (val == null) {
                return eval;
            }
            final int dollar = varBounds[0] - "${".length();
            final int afterRightBrace = varBounds[1] + "}".length();
            final String refVar = eval.substring(dollar, afterRightBrace);
            if (val.contains(refVar)) {
                return expr;
            }
            eval = eval.substring(0, dollar) + val + eval.substring(afterRightBrace);
        }
        throw new IllegalStateException("Variable substitution depth too large: 20 " + expr);
    }
    
    String getenv(final String name) {
        return System.getenv(name);
    }
    
    String getProperty(final String key) {
        return System.getProperty(key);
    }
    
    public String get(final String name) {
        final String[] names = this.handleDeprecation(Configuration.deprecationContext.get(), name);
        String result = null;
        for (final String n : names) {
            result = this.substituteVars(this.getProps().getProperty(n));
        }
        return result;
    }
    
    @VisibleForTesting
    public void setAllowNullValueProperties(final boolean val) {
        this.allowNullValueProperties = val;
    }
    
    public void setRestrictSystemProps(final boolean val) {
        this.restrictSystemProps = val;
    }
    
    @VisibleForTesting
    public boolean onlyKeyExists(final String name) {
        final String[] handleDeprecation;
        final String[] names = handleDeprecation = this.handleDeprecation(Configuration.deprecationContext.get(), name);
        for (final String n : handleDeprecation) {
            if (this.getProps().getProperty(n, "testingforemptydefaultvalue").equals("testingforemptydefaultvalue")) {
                return true;
            }
        }
        return false;
    }
    
    public String getTrimmed(final String name) {
        final String value = this.get(name);
        if (null == value) {
            return null;
        }
        return value.trim();
    }
    
    public String getTrimmed(final String name, final String defaultValue) {
        final String ret = this.getTrimmed(name);
        return (ret == null) ? defaultValue : ret;
    }
    
    public String getRaw(final String name) {
        final String[] names = this.handleDeprecation(Configuration.deprecationContext.get(), name);
        String result = null;
        for (final String n : names) {
            result = this.getProps().getProperty(n);
        }
        return result;
    }
    
    private String[] getAlternativeNames(final String name) {
        String[] altNames = null;
        DeprecatedKeyInfo keyInfo = null;
        final DeprecationContext cur = Configuration.deprecationContext.get();
        final String depKey = cur.getReverseDeprecatedKeyMap().get(name);
        if (depKey != null) {
            keyInfo = cur.getDeprecatedKeyMap().get(depKey);
            if (keyInfo.newKeys.length > 0) {
                if (this.getProps().containsKey(depKey)) {
                    final List<String> list = new ArrayList<String>();
                    list.addAll(Arrays.asList(keyInfo.newKeys));
                    list.add(depKey);
                    altNames = list.toArray(new String[list.size()]);
                }
                else {
                    altNames = keyInfo.newKeys;
                }
            }
        }
        return altNames;
    }
    
    public void set(final String name, final String value) {
        this.set(name, value, null);
    }
    
    public void set(String name, final String value, final String source) {
        Preconditions.checkArgument(name != null, (Object)"Property name must not be null");
        Preconditions.checkArgument(value != null, "The value of property %s must not be null", name);
        name = name.trim();
        final DeprecationContext deprecations = Configuration.deprecationContext.get();
        if (deprecations.getDeprecatedKeyMap().isEmpty()) {
            this.getProps();
        }
        this.getOverlay().setProperty(name, value);
        this.getProps().setProperty(name, value);
        final String newSource = (source == null) ? "programmatically" : source;
        if (!isDeprecated(name)) {
            this.putIntoUpdatingResource(name, new String[] { newSource });
            final String[] altNames = this.getAlternativeNames(name);
            if (altNames != null) {
                for (final String n : altNames) {
                    if (!n.equals(name)) {
                        this.getOverlay().setProperty(n, value);
                        this.getProps().setProperty(n, value);
                        this.putIntoUpdatingResource(n, new String[] { newSource });
                    }
                }
            }
        }
        else {
            final String[] names = this.handleDeprecation(Configuration.deprecationContext.get(), name);
            final String altSource = "because " + name + " is deprecated";
            for (final String n2 : names) {
                this.getOverlay().setProperty(n2, value);
                this.getProps().setProperty(n2, value);
                this.putIntoUpdatingResource(n2, new String[] { altSource });
            }
        }
    }
    
    @VisibleForTesting
    void logDeprecation(final String message) {
        Configuration.LOG_DEPRECATION.info(message);
    }
    
    void logDeprecationOnce(final String name, final String source) {
        final DeprecatedKeyInfo keyInfo = getDeprecatedKeyInfo(name);
        if (keyInfo != null && !keyInfo.getAndSetAccessed()) {
            Configuration.LOG_DEPRECATION.info(keyInfo.getWarningMessage(name, source));
        }
    }
    
    public synchronized void unset(final String name) {
        String[] names = null;
        if (!isDeprecated(name)) {
            names = this.getAlternativeNames(name);
            if (names == null) {
                names = new String[] { name };
            }
        }
        else {
            names = this.handleDeprecation(Configuration.deprecationContext.get(), name);
        }
        for (final String n : names) {
            this.getOverlay().remove(n);
            this.getProps().remove(n);
        }
    }
    
    public synchronized void setIfUnset(final String name, final String value) {
        if (this.get(name) == null) {
            this.set(name, value);
        }
    }
    
    private synchronized Properties getOverlay() {
        if (this.overlay == null) {
            this.overlay = new Properties();
        }
        return this.overlay;
    }
    
    public String get(final String name, final String defaultValue) {
        final String[] names = this.handleDeprecation(Configuration.deprecationContext.get(), name);
        String result = null;
        for (final String n : names) {
            result = this.substituteVars(this.getProps().getProperty(n, defaultValue));
        }
        return result;
    }
    
    public int getInt(final String name, final int defaultValue) {
        final String valueString = this.getTrimmed(name);
        if (valueString == null) {
            return defaultValue;
        }
        final String hexString = this.getHexDigits(valueString);
        if (hexString != null) {
            return Integer.parseInt(hexString, 16);
        }
        return Integer.parseInt(valueString);
    }
    
    public int[] getInts(final String name) {
        final String[] strings = this.getTrimmedStrings(name);
        final int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; ++i) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints;
    }
    
    public void setInt(final String name, final int value) {
        this.set(name, Integer.toString(value));
    }
    
    public long getLong(final String name, final long defaultValue) {
        final String valueString = this.getTrimmed(name);
        if (valueString == null) {
            return defaultValue;
        }
        final String hexString = this.getHexDigits(valueString);
        if (hexString != null) {
            return Long.parseLong(hexString, 16);
        }
        return Long.parseLong(valueString);
    }
    
    public long getLongBytes(final String name, final long defaultValue) {
        final String valueString = this.getTrimmed(name);
        if (valueString == null) {
            return defaultValue;
        }
        return StringUtils.TraditionalBinaryPrefix.string2long(valueString);
    }
    
    private String getHexDigits(final String value) {
        boolean negative = false;
        String str = value;
        String hexString = null;
        if (value.startsWith("-")) {
            negative = true;
            str = value.substring(1);
        }
        if (str.startsWith("0x") || str.startsWith("0X")) {
            hexString = str.substring(2);
            if (negative) {
                hexString = "-" + hexString;
            }
            return hexString;
        }
        return null;
    }
    
    public void setLong(final String name, final long value) {
        this.set(name, Long.toString(value));
    }
    
    public float getFloat(final String name, final float defaultValue) {
        final String valueString = this.getTrimmed(name);
        if (valueString == null) {
            return defaultValue;
        }
        return Float.parseFloat(valueString);
    }
    
    public void setFloat(final String name, final float value) {
        this.set(name, Float.toString(value));
    }
    
    public double getDouble(final String name, final double defaultValue) {
        final String valueString = this.getTrimmed(name);
        if (valueString == null) {
            return defaultValue;
        }
        return Double.parseDouble(valueString);
    }
    
    public void setDouble(final String name, final double value) {
        this.set(name, Double.toString(value));
    }
    
    public boolean getBoolean(final String name, final boolean defaultValue) {
        final String valueString = this.getTrimmed(name);
        if (null == valueString || valueString.isEmpty()) {
            return defaultValue;
        }
        return StringUtils.equalsIgnoreCase("true", valueString) || (!StringUtils.equalsIgnoreCase("false", valueString) && defaultValue);
    }
    
    public void setBoolean(final String name, final boolean value) {
        this.set(name, Boolean.toString(value));
    }
    
    public void setBooleanIfUnset(final String name, final boolean value) {
        this.setIfUnset(name, Boolean.toString(value));
    }
    
    public <T extends Enum<T>> void setEnum(final String name, final T value) {
        this.set(name, value.toString());
    }
    
    public <T extends Enum<T>> T getEnum(final String name, final T defaultValue) {
        final String val = this.getTrimmed(name);
        return (null == val) ? defaultValue : Enum.valueOf(defaultValue.getDeclaringClass(), val);
    }
    
    public void setTimeDuration(final String name, final long value, final TimeUnit unit) {
        this.set(name, value + ParsedTimeDuration.unitFor(unit).suffix());
    }
    
    public long getTimeDuration(final String name, final long defaultValue, final TimeUnit unit) {
        final String vStr = this.get(name);
        if (null == vStr) {
            return defaultValue;
        }
        return this.getTimeDurationHelper(name, vStr, unit);
    }
    
    public long getTimeDuration(final String name, final String defaultValue, final TimeUnit unit) {
        final String vStr = this.get(name);
        if (null == vStr) {
            return this.getTimeDurationHelper(name, defaultValue, unit);
        }
        return this.getTimeDurationHelper(name, vStr, unit);
    }
    
    public long getTimeDurationHelper(final String name, String vStr, final TimeUnit unit) {
        vStr = vStr.trim();
        vStr = StringUtils.toLowerCase(vStr);
        ParsedTimeDuration vUnit = ParsedTimeDuration.unitFor(vStr);
        if (null == vUnit) {
            this.logDeprecation("No unit for " + name + "(" + vStr + ") assuming " + unit);
            vUnit = ParsedTimeDuration.unitFor(unit);
        }
        else {
            vStr = vStr.substring(0, vStr.lastIndexOf(vUnit.suffix()));
        }
        final long raw = Long.parseLong(vStr);
        final long converted = unit.convert(raw, vUnit.unit());
        if (vUnit.unit().convert(converted, unit) < raw) {
            this.logDeprecation("Possible loss of precision converting " + vStr + vUnit.suffix() + " to " + unit + " for " + name);
        }
        return converted;
    }
    
    public long[] getTimeDurations(final String name, final TimeUnit unit) {
        final String[] strings = this.getTrimmedStrings(name);
        final long[] durations = new long[strings.length];
        for (int i = 0; i < strings.length; ++i) {
            durations[i] = this.getTimeDurationHelper(name, strings[i], unit);
        }
        return durations;
    }
    
    public double getStorageSize(final String name, final String defaultValue, final StorageUnit targetUnit) {
        Preconditions.checkState(org.apache.commons.lang3.StringUtils.isNotBlank(name), (Object)"Key cannot be blank.");
        String vString = this.get(name);
        if (org.apache.commons.lang3.StringUtils.isBlank(vString)) {
            vString = defaultValue;
        }
        final StorageSize measure = StorageSize.parse(vString);
        return this.convertStorageUnit(measure.getValue(), measure.getUnit(), targetUnit);
    }
    
    public double getStorageSize(final String name, final double defaultValue, final StorageUnit targetUnit) {
        Preconditions.checkNotNull(targetUnit, (Object)"Conversion unit cannot be null.");
        Preconditions.checkState(org.apache.commons.lang3.StringUtils.isNotBlank(name), (Object)"Name cannot be blank.");
        final String vString = this.get(name);
        if (org.apache.commons.lang3.StringUtils.isBlank(vString)) {
            return targetUnit.getDefault(defaultValue);
        }
        final StorageSize measure = StorageSize.parse(vString);
        return this.convertStorageUnit(measure.getValue(), measure.getUnit(), targetUnit);
    }
    
    public void setStorageSize(final String name, final double value, final StorageUnit unit) {
        this.set(name, value + unit.getShortName());
    }
    
    private double convertStorageUnit(final double value, final StorageUnit sourceUnit, final StorageUnit targetUnit) {
        final double byteValue = sourceUnit.toBytes(value);
        return targetUnit.fromBytes(byteValue);
    }
    
    public Pattern getPattern(final String name, final Pattern defaultValue) {
        final String valString = this.get(name);
        if (null == valString || valString.isEmpty()) {
            return defaultValue;
        }
        try {
            return Pattern.compile(valString);
        }
        catch (PatternSyntaxException pse) {
            Configuration.LOG.warn("Regular expression '" + valString + "' for property '" + name + "' not valid. Using default", pse);
            return defaultValue;
        }
    }
    
    public void setPattern(final String name, final Pattern pattern) {
        assert pattern != null : "Pattern cannot be null";
        this.set(name, pattern.pattern());
    }
    
    @InterfaceStability.Unstable
    public synchronized String[] getPropertySources(final String name) {
        if (this.properties == null) {
            this.getProps();
        }
        if (this.properties == null || this.updatingResource == null) {
            return null;
        }
        final String[] source = this.updatingResource.get(name);
        if (source == null) {
            return null;
        }
        return Arrays.copyOf(source, source.length);
    }
    
    public IntegerRanges getRange(final String name, final String defaultValue) {
        return new IntegerRanges(this.get(name, defaultValue));
    }
    
    public Collection<String> getStringCollection(final String name) {
        final String valueString = this.get(name);
        return StringUtils.getStringCollection(valueString);
    }
    
    public String[] getStrings(final String name) {
        final String valueString = this.get(name);
        return StringUtils.getStrings(valueString);
    }
    
    public String[] getStrings(final String name, final String... defaultValue) {
        final String valueString = this.get(name);
        if (valueString == null) {
            return defaultValue;
        }
        return StringUtils.getStrings(valueString);
    }
    
    public Collection<String> getTrimmedStringCollection(final String name) {
        final String valueString = this.get(name);
        if (null == valueString) {
            final Collection<String> empty = new ArrayList<String>();
            return empty;
        }
        return StringUtils.getTrimmedStringCollection(valueString);
    }
    
    public String[] getTrimmedStrings(final String name) {
        final String valueString = this.get(name);
        return StringUtils.getTrimmedStrings(valueString);
    }
    
    public String[] getTrimmedStrings(final String name, final String... defaultValue) {
        final String valueString = this.get(name);
        if (null == valueString) {
            return defaultValue;
        }
        return StringUtils.getTrimmedStrings(valueString);
    }
    
    public void setStrings(final String name, final String... values) {
        this.set(name, StringUtils.arrayToString(values));
    }
    
    public char[] getPassword(final String name) throws IOException {
        char[] pass = null;
        pass = this.getPasswordFromCredentialProviders(name);
        if (pass == null) {
            pass = this.getPasswordFromConfig(name);
        }
        return pass;
    }
    
    private CredentialProvider.CredentialEntry getCredentialEntry(final CredentialProvider provider, final String name) throws IOException {
        CredentialProvider.CredentialEntry entry = provider.getCredentialEntry(name);
        if (entry != null) {
            return entry;
        }
        final String oldName = getDeprecatedKey(name);
        if (oldName != null) {
            entry = provider.getCredentialEntry(oldName);
            if (entry != null) {
                this.logDeprecationOnce(oldName, provider.toString());
                return entry;
            }
        }
        final DeprecatedKeyInfo keyInfo = getDeprecatedKeyInfo(name);
        if (keyInfo != null && keyInfo.newKeys != null) {
            for (final String newName : keyInfo.newKeys) {
                entry = provider.getCredentialEntry(newName);
                if (entry != null) {
                    this.logDeprecationOnce(name, null);
                    return entry;
                }
            }
        }
        return null;
    }
    
    public char[] getPasswordFromCredentialProviders(final String name) throws IOException {
        char[] pass = null;
        try {
            final List<CredentialProvider> providers = CredentialProviderFactory.getProviders(this);
            if (providers != null) {
                for (final CredentialProvider provider : providers) {
                    try {
                        final CredentialProvider.CredentialEntry entry = this.getCredentialEntry(provider, name);
                        if (entry != null) {
                            pass = entry.getCredential();
                            break;
                        }
                        continue;
                    }
                    catch (IOException ioe) {
                        throw new IOException("Can't get key " + name + " from key providerof type: " + provider.getClass().getName() + ".", ioe);
                    }
                }
            }
        }
        catch (IOException ioe2) {
            throw new IOException("Configuration problem with provider path.", ioe2);
        }
        return pass;
    }
    
    protected char[] getPasswordFromConfig(final String name) {
        char[] pass = null;
        if (this.getBoolean("hadoop.security.credential.clear-text-fallback", true)) {
            final String passStr = this.get(name);
            if (passStr != null) {
                pass = passStr.toCharArray();
            }
        }
        return pass;
    }
    
    public InetSocketAddress getSocketAddr(final String hostProperty, final String addressProperty, final String defaultAddressValue, final int defaultPort) {
        final InetSocketAddress bindAddr = this.getSocketAddr(addressProperty, defaultAddressValue, defaultPort);
        final String host = this.get(hostProperty);
        if (host == null || host.isEmpty()) {
            return bindAddr;
        }
        return NetUtils.createSocketAddr(host, bindAddr.getPort(), hostProperty);
    }
    
    public InetSocketAddress getSocketAddr(final String name, final String defaultAddress, final int defaultPort) {
        final String address = this.getTrimmed(name, defaultAddress);
        return NetUtils.createSocketAddr(address, defaultPort, name);
    }
    
    public void setSocketAddr(final String name, final InetSocketAddress addr) {
        this.set(name, NetUtils.getHostPortString(addr));
    }
    
    public InetSocketAddress updateConnectAddr(final String hostProperty, final String addressProperty, final String defaultAddressValue, final InetSocketAddress addr) {
        final String host = this.get(hostProperty);
        final String connectHostPort = this.getTrimmed(addressProperty, defaultAddressValue);
        if (host == null || host.isEmpty() || connectHostPort == null || connectHostPort.isEmpty()) {
            return this.updateConnectAddr(addressProperty, addr);
        }
        final String connectHost = connectHostPort.split(":")[0];
        return this.updateConnectAddr(addressProperty, NetUtils.createSocketAddrForHost(connectHost, addr.getPort()));
    }
    
    public InetSocketAddress updateConnectAddr(final String name, final InetSocketAddress addr) {
        final InetSocketAddress connectAddr = NetUtils.getConnectAddress(addr);
        this.setSocketAddr(name, connectAddr);
        return connectAddr;
    }
    
    public Class<?> getClassByName(final String name) throws ClassNotFoundException {
        final Class<?> ret = this.getClassByNameOrNull(name);
        if (ret == null) {
            throw new ClassNotFoundException("Class " + name + " not found");
        }
        return ret;
    }
    
    public Class<?> getClassByNameOrNull(final String name) {
        Map<String, WeakReference<Class<?>>> map;
        synchronized (Configuration.CACHE_CLASSES) {
            map = Configuration.CACHE_CLASSES.get(this.classLoader);
            if (map == null) {
                map = Collections.synchronizedMap(new WeakHashMap<String, WeakReference<Class<?>>>());
                Configuration.CACHE_CLASSES.put(this.classLoader, map);
            }
        }
        Class<?> clazz = null;
        final WeakReference<Class<?>> ref = map.get(name);
        if (ref != null) {
            clazz = ref.get();
        }
        if (clazz == null) {
            try {
                clazz = Class.forName(name, true, this.classLoader);
            }
            catch (ClassNotFoundException e) {
                map.put(name, new WeakReference<Class<?>>(Configuration.NEGATIVE_CACHE_SENTINEL));
                return null;
            }
            map.put(name, new WeakReference<Class<?>>(clazz));
            return clazz;
        }
        if (clazz == Configuration.NEGATIVE_CACHE_SENTINEL) {
            return null;
        }
        return clazz;
    }
    
    public Class<?>[] getClasses(final String name, final Class<?>... defaultValue) {
        final String valueString = this.getRaw(name);
        if (null == valueString) {
            return defaultValue;
        }
        final String[] classnames = this.getTrimmedStrings(name);
        try {
            final Class<?>[] classes = (Class<?>[])new Class[classnames.length];
            for (int i = 0; i < classnames.length; ++i) {
                classes[i] = this.getClassByName(classnames[i]);
            }
            return classes;
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Class<?> getClass(final String name, final Class<?> defaultValue) {
        final String valueString = this.getTrimmed(name);
        if (valueString == null) {
            return defaultValue;
        }
        try {
            return this.getClassByName(valueString);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public <U> Class<? extends U> getClass(final String name, final Class<? extends U> defaultValue, final Class<U> xface) {
        try {
            final Class<?> theClass = this.getClass(name, defaultValue);
            if (theClass != null && !xface.isAssignableFrom(theClass)) {
                throw new RuntimeException(theClass + " not " + xface.getName());
            }
            if (theClass != null) {
                return theClass.asSubclass(xface);
            }
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public <U> List<U> getInstances(final String name, final Class<U> xface) {
        final List<U> ret = new ArrayList<U>();
        final Class<?>[] classes2;
        final Class<?>[] classes = classes2 = this.getClasses(name, (Class<?>[])new Class[0]);
        for (final Class<?> cl : classes2) {
            if (!xface.isAssignableFrom(cl)) {
                throw new RuntimeException(cl + " does not implement " + xface);
            }
            ret.add(ReflectionUtils.newInstance(cl, this));
        }
        return ret;
    }
    
    public void setClass(final String name, final Class<?> theClass, final Class<?> xface) {
        if (!xface.isAssignableFrom(theClass)) {
            throw new RuntimeException(theClass + " not " + xface.getName());
        }
        this.set(name, theClass.getName());
    }
    
    public Path getLocalPath(final String dirsProp, final String path) throws IOException {
        final String[] dirs = this.getTrimmedStrings(dirsProp);
        final int hashCode = path.hashCode();
        final FileSystem fs = FileSystem.getLocal(this);
        for (int i = 0; i < dirs.length; ++i) {
            final int index = (hashCode + i & Integer.MAX_VALUE) % dirs.length;
            final Path file = new Path(dirs[index], path);
            final Path dir = file.getParent();
            if (fs.mkdirs(dir) || fs.exists(dir)) {
                return file;
            }
        }
        Configuration.LOG.warn("Could not make " + path + " in local directories from " + dirsProp);
        for (int i = 0; i < dirs.length; ++i) {
            final int index = (hashCode + i & Integer.MAX_VALUE) % dirs.length;
            Configuration.LOG.warn(dirsProp + "[" + index + "]=" + dirs[index]);
        }
        throw new IOException("No valid local directories in property: " + dirsProp);
    }
    
    public File getFile(final String dirsProp, final String path) throws IOException {
        final String[] dirs = this.getTrimmedStrings(dirsProp);
        final int hashCode = path.hashCode();
        for (int i = 0; i < dirs.length; ++i) {
            final int index = (hashCode + i & Integer.MAX_VALUE) % dirs.length;
            final File file = new File(dirs[index], path);
            final File dir = file.getParentFile();
            if (dir.exists() || dir.mkdirs()) {
                return file;
            }
        }
        throw new IOException("No valid local directories in property: " + dirsProp);
    }
    
    public URL getResource(final String name) {
        return this.classLoader.getResource(name);
    }
    
    public InputStream getConfResourceAsInputStream(final String name) {
        try {
            final URL url = this.getResource(name);
            if (url == null) {
                Configuration.LOG.info(name + " not found");
                return null;
            }
            Configuration.LOG.info("found resource " + name + " at " + url);
            return url.openStream();
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public Reader getConfResourceAsReader(final String name) {
        try {
            final URL url = this.getResource(name);
            if (url == null) {
                Configuration.LOG.info(name + " not found");
                return null;
            }
            Configuration.LOG.info("found resource " + name + " at " + url);
            return new InputStreamReader(url.openStream(), Charsets.UTF_8);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public Set<String> getFinalParameters() {
        final Set<String> setFinalParams = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        setFinalParams.addAll(this.finalParameters);
        return setFinalParams;
    }
    
    protected synchronized Properties getProps() {
        if (this.properties == null) {
            this.properties = new Properties();
            final Map<String, String[]> backup = (this.updatingResource != null) ? new ConcurrentHashMap<String, String[]>(this.updatingResource) : null;
            this.loadResources(this.properties, this.resources, this.quietmode);
            if (this.overlay != null) {
                this.properties.putAll(this.overlay);
                if (backup != null) {
                    for (final Map.Entry<Object, Object> item : this.overlay.entrySet()) {
                        final String key = item.getKey();
                        final String[] source = backup.get(key);
                        if (source != null) {
                            this.updatingResource.put(key, source);
                        }
                    }
                }
            }
        }
        return this.properties;
    }
    
    public int size() {
        return this.getProps().size();
    }
    
    public void clear() {
        this.getProps().clear();
        this.getOverlay().clear();
    }
    
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        final Map<String, String> result = new HashMap<String, String>();
        for (final Map.Entry<Object, Object> item : this.getProps().entrySet()) {
            if (item.getKey() instanceof String && item.getValue() instanceof String) {
                result.put(item.getKey(), item.getValue());
            }
        }
        return result.entrySet().iterator();
    }
    
    public Map<String, String> getPropsWithPrefix(final String confPrefix) {
        final Properties props = this.getProps();
        final Map<String, String> configMap = new HashMap<String, String>();
        for (final String name : props.stringPropertyNames()) {
            if (name.startsWith(confPrefix)) {
                final String value = this.get(name);
                final String keyName = name.substring(confPrefix.length());
                configMap.put(keyName, value);
            }
        }
        return configMap;
    }
    
    private XMLStreamReader parse(final URL url, final boolean restricted) throws IOException, XMLStreamException {
        if (!this.quietmode && Configuration.LOG.isDebugEnabled()) {
            Configuration.LOG.debug("parsing URL " + url);
        }
        if (url == null) {
            return null;
        }
        final URLConnection connection = url.openConnection();
        if (connection instanceof JarURLConnection) {
            connection.setUseCaches(false);
        }
        return this.parse(connection.getInputStream(), url.toString(), restricted);
    }
    
    private XMLStreamReader parse(final InputStream is, final String systemIdStr, final boolean restricted) throws IOException, XMLStreamException {
        if (!this.quietmode) {
            Configuration.LOG.debug("parsing input stream " + is);
        }
        if (is == null) {
            return null;
        }
        final SystemId systemId = SystemId.construct(systemIdStr);
        final ReaderConfig readerConfig = Configuration.XML_INPUT_FACTORY.createPrivateConfig();
        if (restricted) {
            readerConfig.setProperty("javax.xml.stream.supportDTD", false);
        }
        return Configuration.XML_INPUT_FACTORY.createSR(readerConfig, systemId, StreamBootstrapper.getInstance(null, systemId, is), false, true);
    }
    
    private void loadResources(final Properties properties, final ArrayList<Resource> resources, final boolean quiet) {
        if (this.loadDefaults) {
            for (final String resource : Configuration.defaultResources) {
                this.loadResource(properties, new Resource(resource, false), quiet);
            }
        }
        for (int i = 0; i < resources.size(); ++i) {
            final Resource ret = this.loadResource(properties, resources.get(i), quiet);
            if (ret != null) {
                resources.set(i, ret);
            }
        }
        this.addTags(properties);
    }
    
    private Resource loadResource(final Properties properties, final Resource wrapper, final boolean quiet) {
        String name = "Unknown";
        try {
            final Object resource = wrapper.getResource();
            name = wrapper.getName();
            boolean returnCachedProperties = false;
            if (resource instanceof InputStream) {
                returnCachedProperties = true;
            }
            else if (resource instanceof Properties) {
                this.overlay(properties, (Properties)resource);
            }
            final XMLStreamReader2 reader = this.getStreamReader(wrapper, quiet);
            if (reader == null) {
                if (quiet) {
                    return null;
                }
                throw new RuntimeException(resource + " not found");
            }
            else {
                Properties toAddTo = properties;
                if (returnCachedProperties) {
                    toAddTo = new Properties();
                }
                final List<ParsedItem> items = new Parser(reader, wrapper, quiet).parse();
                for (final ParsedItem item : items) {
                    this.loadProperty(toAddTo, item.name, item.key, item.value, item.isFinal, item.sources);
                }
                reader.close();
                if (returnCachedProperties) {
                    this.overlay(properties, toAddTo);
                    return new Resource(toAddTo, name, wrapper.isParserRestricted());
                }
                return null;
            }
        }
        catch (IOException e) {
            Configuration.LOG.error("error parsing conf " + name, e);
            throw new RuntimeException(e);
        }
        catch (XMLStreamException e2) {
            Configuration.LOG.error("error parsing conf " + name, e2);
            throw new RuntimeException(e2);
        }
    }
    
    private XMLStreamReader2 getStreamReader(final Resource wrapper, final boolean quiet) throws XMLStreamException, IOException {
        final Object resource = wrapper.getResource();
        final boolean isRestricted = wrapper.isParserRestricted();
        XMLStreamReader2 reader = null;
        if (resource instanceof URL) {
            reader = (XMLStreamReader2)this.parse((URL)resource, isRestricted);
        }
        else if (resource instanceof String) {
            final URL url = this.getResource((String)resource);
            reader = (XMLStreamReader2)this.parse(url, isRestricted);
        }
        else if (resource instanceof Path) {
            final File file = new File(((Path)resource).toUri().getPath()).getAbsoluteFile();
            if (file.exists()) {
                if (!quiet) {
                    Configuration.LOG.debug("parsing File " + file);
                }
                reader = (XMLStreamReader2)this.parse(new BufferedInputStream(new FileInputStream(file)), ((Path)resource).toString(), isRestricted);
            }
        }
        else if (resource instanceof InputStream) {
            reader = (XMLStreamReader2)this.parse((InputStream)resource, null, isRestricted);
        }
        return reader;
    }
    
    public void addTags(final Properties prop) {
        try {
            if (prop.containsKey("hadoop.tags.system")) {
                final String systemTags = prop.getProperty("hadoop.tags.system");
                Configuration.TAGS.addAll(Arrays.asList(systemTags.split(",")));
            }
            if (prop.containsKey("hadoop.tags.custom")) {
                final String customTags = prop.getProperty("hadoop.tags.custom");
                Configuration.TAGS.addAll(Arrays.asList(customTags.split(",")));
            }
            if (prop.containsKey("hadoop.system.tags")) {
                final String systemTags = prop.getProperty("hadoop.system.tags");
                Configuration.TAGS.addAll(Arrays.asList(systemTags.split(",")));
            }
            if (prop.containsKey("hadoop.custom.tags")) {
                final String customTags = prop.getProperty("hadoop.custom.tags");
                Configuration.TAGS.addAll(Arrays.asList(customTags.split(",")));
            }
        }
        catch (Exception ex) {
            Configuration.LOG.trace("Error adding tags in configuration", ex);
        }
    }
    
    private void readTagFromConfig(final String attributeValue, final String confName, String confValue, final String[] confSource) {
        for (String tagStr : attributeValue.split(",")) {
            try {
                tagStr = tagStr.trim();
                if (confValue == null) {
                    confValue = "";
                }
                if (this.propertyTagsMap.containsKey(tagStr)) {
                    this.propertyTagsMap.get(tagStr).setProperty(confName, confValue);
                }
                else {
                    final Properties props = new Properties();
                    props.setProperty(confName, confValue);
                    this.propertyTagsMap.put(tagStr, props);
                }
            }
            catch (Exception ex) {
                Configuration.LOG.trace("Tag '{}' for property:{} Source:{}", tagStr, confName, confSource, ex);
            }
        }
    }
    
    private void overlay(final Properties to, final Properties from) {
        for (final Map.Entry<Object, Object> entry : from.entrySet()) {
            to.put(entry.getKey(), entry.getValue());
        }
    }
    
    private void loadProperty(final Properties properties, final String name, final String attr, String value, final boolean finalParameter, final String[] source) {
        if (value != null || this.allowNullValueProperties) {
            if (value == null) {
                value = "testingforemptydefaultvalue";
            }
            if (!this.finalParameters.contains(attr)) {
                properties.setProperty(attr, value);
                if (source != null) {
                    this.putIntoUpdatingResource(attr, source);
                }
            }
            else {
                this.checkForOverride(this.properties, name, attr, value);
                if (this.properties != properties) {
                    this.checkForOverride(properties, name, attr, value);
                }
            }
        }
        if (finalParameter && attr != null) {
            this.finalParameters.add(attr);
        }
    }
    
    private void checkForOverride(final Properties properties, final String name, final String attr, final String value) {
        final String propertyValue = properties.getProperty(attr);
        if (propertyValue != null && !propertyValue.equals(value)) {
            Configuration.LOG.warn(name + ":an attempt to override final parameter: " + attr + ";  Ignoring.");
        }
    }
    
    public void writeXml(final OutputStream out) throws IOException {
        this.writeXml(new OutputStreamWriter(out, "UTF-8"));
    }
    
    public void writeXml(final Writer out) throws IOException {
        this.writeXml(null, out);
    }
    
    public void writeXml(final String propertyName, final Writer out) throws IOException, IllegalArgumentException {
        final Document doc = this.asXmlDocument(propertyName);
        try {
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(out);
            final TransformerFactory transFactory = TransformerFactory.newInstance();
            final Transformer transformer = transFactory.newTransformer();
            transformer.transform(source, result);
        }
        catch (TransformerException te) {
            throw new IOException(te);
        }
    }
    
    private synchronized Document asXmlDocument(final String propertyName) throws IOException, IllegalArgumentException {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }
        catch (ParserConfigurationException pe) {
            throw new IOException(pe);
        }
        final Element conf = doc.createElement("configuration");
        doc.appendChild(conf);
        conf.appendChild(doc.createTextNode("\n"));
        this.handleDeprecation();
        if (!Strings.isNullOrEmpty(propertyName)) {
            if (!this.properties.containsKey(propertyName)) {
                throw new IllegalArgumentException("Property " + propertyName + " not found");
            }
            this.appendXMLProperty(doc, conf, propertyName);
            conf.appendChild(doc.createTextNode("\n"));
        }
        else {
            final Enumeration<Object> e = this.properties.keys();
            while (e.hasMoreElements()) {
                this.appendXMLProperty(doc, conf, e.nextElement());
                conf.appendChild(doc.createTextNode("\n"));
            }
        }
        return doc;
    }
    
    private synchronized void appendXMLProperty(final Document doc, final Element conf, final String propertyName) {
        if (!Strings.isNullOrEmpty(propertyName)) {
            final String value = this.properties.getProperty(propertyName);
            if (value != null) {
                final Element propNode = doc.createElement("property");
                conf.appendChild(propNode);
                final Element nameNode = doc.createElement("name");
                nameNode.appendChild(doc.createTextNode(propertyName));
                propNode.appendChild(nameNode);
                final Element valueNode = doc.createElement("value");
                valueNode.appendChild(doc.createTextNode(this.properties.getProperty(propertyName)));
                propNode.appendChild(valueNode);
                final Element finalNode = doc.createElement("final");
                finalNode.appendChild(doc.createTextNode(String.valueOf(this.finalParameters.contains(propertyName))));
                propNode.appendChild(finalNode);
                if (this.updatingResource != null) {
                    final String[] sources = this.updatingResource.get(propertyName);
                    if (sources != null) {
                        for (final String s : sources) {
                            final Element sourceNode = doc.createElement("source");
                            sourceNode.appendChild(doc.createTextNode(s));
                            propNode.appendChild(sourceNode);
                        }
                    }
                }
            }
        }
    }
    
    public static void dumpConfiguration(final Configuration config, final String propertyName, final Writer out) throws IOException {
        if (Strings.isNullOrEmpty(propertyName)) {
            dumpConfiguration(config, out);
        }
        else {
            if (Strings.isNullOrEmpty(config.get(propertyName))) {
                throw new IllegalArgumentException("Property " + propertyName + " not found");
            }
            final JsonFactory dumpFactory = new JsonFactory();
            final JsonGenerator dumpGenerator = dumpFactory.createGenerator(out);
            dumpGenerator.writeStartObject();
            dumpGenerator.writeFieldName("property");
            appendJSONProperty(dumpGenerator, config, propertyName, new ConfigRedactor(config));
            dumpGenerator.writeEndObject();
            dumpGenerator.flush();
        }
    }
    
    public static void dumpConfiguration(final Configuration config, final Writer out) throws IOException {
        final JsonFactory dumpFactory = new JsonFactory();
        final JsonGenerator dumpGenerator = dumpFactory.createGenerator(out);
        dumpGenerator.writeStartObject();
        dumpGenerator.writeFieldName("properties");
        dumpGenerator.writeStartArray();
        dumpGenerator.flush();
        final ConfigRedactor redactor = new ConfigRedactor(config);
        synchronized (config) {
            for (final Map.Entry<Object, Object> item : config.getProps().entrySet()) {
                appendJSONProperty(dumpGenerator, config, item.getKey().toString(), redactor);
            }
        }
        dumpGenerator.writeEndArray();
        dumpGenerator.writeEndObject();
        dumpGenerator.flush();
    }
    
    private static void appendJSONProperty(final JsonGenerator jsonGen, final Configuration config, final String name, final ConfigRedactor redactor) throws IOException {
        if (!Strings.isNullOrEmpty(name) && jsonGen != null) {
            jsonGen.writeStartObject();
            jsonGen.writeStringField("key", name);
            jsonGen.writeStringField("value", redactor.redact(name, config.get(name)));
            jsonGen.writeBooleanField("isFinal", config.finalParameters.contains(name));
            final String[] resources = (String[])((config.updatingResource != null) ? ((String[])config.updatingResource.get(name)) : null);
            String resource = "Unknown";
            if (resources != null && resources.length > 0) {
                resource = resources[0];
            }
            jsonGen.writeStringField("resource", resource);
            jsonGen.writeEndObject();
        }
    }
    
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Configuration: ");
        if (this.loadDefaults) {
            this.toString(Configuration.defaultResources, sb);
            if (this.resources.size() > 0) {
                sb.append(", ");
            }
        }
        this.toString(this.resources, sb);
        return sb.toString();
    }
    
    private <T> void toString(final List<T> resources, final StringBuilder sb) {
        final ListIterator<T> i = resources.listIterator();
        while (i.hasNext()) {
            if (i.nextIndex() != 0) {
                sb.append(", ");
            }
            sb.append(i.next());
        }
    }
    
    public synchronized void setQuietMode(final boolean quietmode) {
        this.quietmode = quietmode;
    }
    
    synchronized boolean getQuietMode() {
        return this.quietmode;
    }
    
    public static void main(final String[] args) throws Exception {
        new Configuration().writeXml(System.out);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.clear();
        for (int size = WritableUtils.readVInt(in), i = 0; i < size; ++i) {
            final String key = Text.readString(in);
            final String value = Text.readString(in);
            this.set(key, value);
            final String[] sources = WritableUtils.readCompressedStringArray(in);
            if (sources != null) {
                this.putIntoUpdatingResource(key, sources);
            }
        }
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        final Properties props = this.getProps();
        WritableUtils.writeVInt(out, props.size());
        for (final Map.Entry<Object, Object> item : props.entrySet()) {
            Text.writeString(out, item.getKey());
            Text.writeString(out, item.getValue());
            WritableUtils.writeCompressedStringArray(out, (String[])((this.updatingResource != null) ? ((String[])this.updatingResource.get(item.getKey())) : null));
        }
    }
    
    public Map<String, String> getValByRegex(final String regex) {
        final Pattern p = Pattern.compile(regex);
        final Map<String, String> result = new HashMap<String, String>();
        for (final Map.Entry<Object, Object> item : this.getProps().entrySet()) {
            if (item.getKey() instanceof String && item.getValue() instanceof String) {
                final Matcher m = p.matcher(item.getKey());
                if (!m.find()) {
                    continue;
                }
                result.put(item.getKey(), this.substituteVars(this.getProps().getProperty(item.getKey())));
            }
        }
        return result;
    }
    
    public static void dumpDeprecatedKeys() {
        final DeprecationContext deprecations = Configuration.deprecationContext.get();
        for (final Map.Entry<String, DeprecatedKeyInfo> entry : deprecations.getDeprecatedKeyMap().entrySet()) {
            final StringBuilder newKeys = new StringBuilder();
            for (final String newKey : entry.getValue().newKeys) {
                newKeys.append(newKey).append("\t");
            }
            System.out.println(entry.getKey() + "\t" + newKeys.toString());
        }
    }
    
    public static boolean hasWarnedDeprecation(final String name) {
        final DeprecationContext deprecations = Configuration.deprecationContext.get();
        return deprecations.getDeprecatedKeyMap().containsKey(name) && deprecations.getDeprecatedKeyMap().get(name).accessed.get();
    }
    
    public Properties getAllPropertiesByTag(final String tag) {
        final Properties props = new Properties();
        if (this.propertyTagsMap.containsKey(tag)) {
            props.putAll(this.propertyTagsMap.get(tag));
        }
        return props;
    }
    
    public Properties getAllPropertiesByTags(final List<String> tagList) {
        final Properties prop = new Properties();
        for (final String tag : tagList) {
            prop.putAll(this.getAllPropertiesByTag(tag));
        }
        return prop;
    }
    
    public boolean isPropertyTag(final String tagStr) {
        return Configuration.TAGS.contains(tagStr);
    }
    
    private void putIntoUpdatingResource(final String key, final String[] value) {
        Map<String, String[]> localUR = this.updatingResource;
        if (localUR == null) {
            synchronized (this) {
                localUR = this.updatingResource;
                if (localUR == null) {
                    localUR = (this.updatingResource = new ConcurrentHashMap<String, String[]>(8));
                }
            }
        }
        localUR.put(key, value);
    }
    
    static {
        LOG = LoggerFactory.getLogger(Configuration.class);
        LOG_DEPRECATION = LoggerFactory.getLogger("org.apache.hadoop.conf.Configuration.deprecation");
        TAGS = ConcurrentHashMap.newKeySet();
        Configuration.restrictSystemPropsDefault = false;
        REGISTRY = new WeakHashMap<Configuration, Object>();
        defaultResources = new CopyOnWriteArrayList<String>();
        CACHE_CLASSES = new WeakHashMap<ClassLoader, Map<String, WeakReference<Class<?>>>>();
        NEGATIVE_CACHE_SENTINEL = NegativeCacheSentinel.class;
        XML_INPUT_FACTORY = new WstxInputFactory();
        Configuration.defaultDeprecations = new DeprecationDelta[] { new DeprecationDelta("topology.script.file.name", "net.topology.script.file.name"), new DeprecationDelta("topology.script.number.args", "net.topology.script.number.args"), new DeprecationDelta("hadoop.configured.node.mapping", "net.topology.configured.node.mapping"), new DeprecationDelta("topology.node.switch.mapping.impl", "net.topology.node.switch.mapping.impl"), new DeprecationDelta("dfs.df.interval", "fs.df.interval"), new DeprecationDelta("fs.default.name", "fs.defaultFS"), new DeprecationDelta("dfs.umaskmode", "fs.permissions.umask-mode"), new DeprecationDelta("dfs.nfs.exports.allowed.hosts", "nfs.exports.allowed.hosts") };
        Configuration.deprecationContext = new AtomicReference<DeprecationContext>(new DeprecationContext(null, Configuration.defaultDeprecations));
        addDefaultResource("core-default.xml");
        addDefaultResource("core-site.xml");
        ClassLoader cL = Thread.currentThread().getContextClassLoader();
        if (cL == null) {
            cL = Configuration.class.getClassLoader();
        }
        if (cL.getResource("hadoop-site.xml") != null) {
            Configuration.LOG.warn("DEPRECATED: hadoop-site.xml found in the classpath. Usage of hadoop-site.xml is deprecated. Instead use core-site.xml, mapred-site.xml and hdfs-site.xml to override properties of core-default.xml, mapred-default.xml and hdfs-default.xml respectively");
            addDefaultResource("hadoop-site.xml");
        }
    }
    
    private static class Resource
    {
        private final Object resource;
        private final String name;
        private final boolean restrictParser;
        
        public Resource(final Object resource) {
            this(resource, resource.toString());
        }
        
        public Resource(final Object resource, final boolean useRestrictedParser) {
            this(resource, resource.toString(), useRestrictedParser);
        }
        
        public Resource(final Object resource, final String name) {
            this(resource, name, getRestrictParserDefault(resource));
        }
        
        public Resource(final Object resource, final String name, final boolean restrictParser) {
            this.resource = resource;
            this.name = name;
            this.restrictParser = restrictParser;
        }
        
        public String getName() {
            return this.name;
        }
        
        public Object getResource() {
            return this.resource;
        }
        
        public boolean isParserRestricted() {
            return this.restrictParser;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        private static boolean getRestrictParserDefault(final Object resource) {
            if (resource instanceof String || !UserGroupInformation.isInitialized()) {
                return false;
            }
            UserGroupInformation user;
            try {
                user = UserGroupInformation.getCurrentUser();
            }
            catch (IOException e) {
                throw new RuntimeException("Unable to determine current user", e);
            }
            return user.getRealUser() != null;
        }
    }
    
    private static class DeprecatedKeyInfo
    {
        private final String[] newKeys;
        private final String customMessage;
        private final AtomicBoolean accessed;
        
        DeprecatedKeyInfo(final String[] newKeys, final String customMessage) {
            this.accessed = new AtomicBoolean(false);
            this.newKeys = newKeys;
            this.customMessage = customMessage;
        }
        
        private final String getWarningMessage(final String key) {
            return this.getWarningMessage(key, null);
        }
        
        private String getWarningMessage(final String key, final String source) {
            String warningMessage;
            if (this.customMessage == null) {
                final StringBuilder message = new StringBuilder(key);
                if (source != null) {
                    message.append(" in " + source);
                }
                message.append(" is deprecated. Instead, use ");
                for (int i = 0; i < this.newKeys.length; ++i) {
                    message.append(this.newKeys[i]);
                    if (i != this.newKeys.length - 1) {
                        message.append(", ");
                    }
                }
                warningMessage = message.toString();
            }
            else {
                warningMessage = this.customMessage;
            }
            return warningMessage;
        }
        
        boolean getAndSetAccessed() {
            return this.accessed.getAndSet(true);
        }
        
        public void clearAccessed() {
            this.accessed.set(false);
        }
    }
    
    public static class DeprecationDelta
    {
        private final String key;
        private final String[] newKeys;
        private final String customMessage;
        
        DeprecationDelta(final String key, final String[] newKeys, final String customMessage) {
            Preconditions.checkNotNull(key);
            Preconditions.checkNotNull(newKeys);
            Preconditions.checkArgument(newKeys.length > 0);
            this.key = key;
            this.newKeys = newKeys;
            this.customMessage = customMessage;
        }
        
        public DeprecationDelta(final String key, final String newKey, final String customMessage) {
            this(key, new String[] { newKey }, customMessage);
        }
        
        public DeprecationDelta(final String key, final String newKey) {
            this(key, new String[] { newKey }, null);
        }
        
        public String getKey() {
            return this.key;
        }
        
        public String[] getNewKeys() {
            return this.newKeys;
        }
        
        public String getCustomMessage() {
            return this.customMessage;
        }
    }
    
    private static class DeprecationContext
    {
        private final Map<String, DeprecatedKeyInfo> deprecatedKeyMap;
        private final Map<String, String> reverseDeprecatedKeyMap;
        
        DeprecationContext(final DeprecationContext other, final DeprecationDelta[] deltas) {
            final HashMap<String, DeprecatedKeyInfo> newDeprecatedKeyMap = new HashMap<String, DeprecatedKeyInfo>();
            final HashMap<String, String> newReverseDeprecatedKeyMap = new HashMap<String, String>();
            if (other != null) {
                for (final Map.Entry<String, DeprecatedKeyInfo> entry : other.deprecatedKeyMap.entrySet()) {
                    newDeprecatedKeyMap.put(entry.getKey(), entry.getValue());
                }
                for (final Map.Entry<String, String> entry2 : other.reverseDeprecatedKeyMap.entrySet()) {
                    newReverseDeprecatedKeyMap.put(entry2.getKey(), entry2.getValue());
                }
            }
            for (final DeprecationDelta delta : deltas) {
                if (!newDeprecatedKeyMap.containsKey(delta.getKey())) {
                    final DeprecatedKeyInfo newKeyInfo = new DeprecatedKeyInfo(delta.getNewKeys(), delta.getCustomMessage());
                    newDeprecatedKeyMap.put(delta.key, newKeyInfo);
                    for (final String newKey : delta.getNewKeys()) {
                        newReverseDeprecatedKeyMap.put(newKey, delta.key);
                    }
                }
            }
            this.deprecatedKeyMap = (Map<String, DeprecatedKeyInfo>)UnmodifiableMap.decorate(newDeprecatedKeyMap);
            this.reverseDeprecatedKeyMap = (Map<String, String>)UnmodifiableMap.decorate(newReverseDeprecatedKeyMap);
        }
        
        Map<String, DeprecatedKeyInfo> getDeprecatedKeyMap() {
            return this.deprecatedKeyMap;
        }
        
        Map<String, String> getReverseDeprecatedKeyMap() {
            return this.reverseDeprecatedKeyMap;
        }
    }
    
    enum ParsedTimeDuration
    {
        NS {
            @Override
            TimeUnit unit() {
                return TimeUnit.NANOSECONDS;
            }
            
            @Override
            String suffix() {
                return "ns";
            }
        }, 
        US {
            @Override
            TimeUnit unit() {
                return TimeUnit.MICROSECONDS;
            }
            
            @Override
            String suffix() {
                return "us";
            }
        }, 
        MS {
            @Override
            TimeUnit unit() {
                return TimeUnit.MILLISECONDS;
            }
            
            @Override
            String suffix() {
                return "ms";
            }
        }, 
        S {
            @Override
            TimeUnit unit() {
                return TimeUnit.SECONDS;
            }
            
            @Override
            String suffix() {
                return "s";
            }
        }, 
        M {
            @Override
            TimeUnit unit() {
                return TimeUnit.MINUTES;
            }
            
            @Override
            String suffix() {
                return "m";
            }
        }, 
        H {
            @Override
            TimeUnit unit() {
                return TimeUnit.HOURS;
            }
            
            @Override
            String suffix() {
                return "h";
            }
        }, 
        D {
            @Override
            TimeUnit unit() {
                return TimeUnit.DAYS;
            }
            
            @Override
            String suffix() {
                return "d";
            }
        };
        
        abstract TimeUnit unit();
        
        abstract String suffix();
        
        static ParsedTimeDuration unitFor(final String s) {
            for (final ParsedTimeDuration ptd : values()) {
                if (s.endsWith(ptd.suffix())) {
                    return ptd;
                }
            }
            return null;
        }
        
        static ParsedTimeDuration unitFor(final TimeUnit unit) {
            for (final ParsedTimeDuration ptd : values()) {
                if (ptd.unit() == unit) {
                    return ptd;
                }
            }
            return null;
        }
    }
    
    public static class IntegerRanges implements Iterable<Integer>
    {
        List<Range> ranges;
        
        public IntegerRanges() {
            this.ranges = new ArrayList<Range>();
        }
        
        public IntegerRanges(final String newValue) {
            this.ranges = new ArrayList<Range>();
            final StringTokenizer itr = new StringTokenizer(newValue, ",");
            while (itr.hasMoreTokens()) {
                final String rng = itr.nextToken().trim();
                final String[] parts = rng.split("-", 3);
                if (parts.length < 1 || parts.length > 2) {
                    throw new IllegalArgumentException("integer range badly formed: " + rng);
                }
                final Range r = new Range();
                r.start = convertToInt(parts[0], 0);
                if (parts.length == 2) {
                    r.end = convertToInt(parts[1], Integer.MAX_VALUE);
                }
                else {
                    r.end = r.start;
                }
                if (r.start > r.end) {
                    throw new IllegalArgumentException("IntegerRange from " + r.start + " to " + r.end + " is invalid");
                }
                this.ranges.add(r);
            }
        }
        
        private static int convertToInt(final String value, final int defaultValue) {
            final String trim = value.trim();
            if (trim.length() == 0) {
                return defaultValue;
            }
            return Integer.parseInt(trim);
        }
        
        public boolean isIncluded(final int value) {
            for (final Range r : this.ranges) {
                if (r.start <= value && value <= r.end) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean isEmpty() {
            return this.ranges == null || this.ranges.isEmpty();
        }
        
        @Override
        public String toString() {
            final StringBuilder result = new StringBuilder();
            boolean first = true;
            for (final Range r : this.ranges) {
                if (first) {
                    first = false;
                }
                else {
                    result.append(',');
                }
                result.append(r.start);
                result.append('-');
                result.append(r.end);
            }
            return result.toString();
        }
        
        public int getRangeStart() {
            if (this.ranges == null || this.ranges.isEmpty()) {
                return -1;
            }
            final Range r = this.ranges.get(0);
            return r.start;
        }
        
        @Override
        public Iterator<Integer> iterator() {
            return new RangeNumberIterator(this.ranges);
        }
        
        private static class Range
        {
            int start;
            int end;
        }
        
        private static class RangeNumberIterator implements Iterator<Integer>
        {
            Iterator<Range> internal;
            int at;
            int end;
            
            public RangeNumberIterator(final List<Range> ranges) {
                if (ranges != null) {
                    this.internal = ranges.iterator();
                }
                this.at = -1;
                this.end = -2;
            }
            
            @Override
            public boolean hasNext() {
                return this.at <= this.end || (this.internal != null && this.internal.hasNext());
            }
            
            @Override
            public Integer next() {
                if (this.at <= this.end) {
                    ++this.at;
                    return this.at - 1;
                }
                if (this.internal != null) {
                    final Range found = this.internal.next();
                    if (found != null) {
                        this.at = found.start;
                        this.end = found.end;
                        ++this.at;
                        return this.at - 1;
                    }
                }
                return null;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
    
    private static class ParsedItem
    {
        String name;
        String key;
        String value;
        boolean isFinal;
        String[] sources;
        
        ParsedItem(final String name, final String key, final String value, final boolean isFinal, final String[] sources) {
            this.name = name;
            this.key = key;
            this.value = value;
            this.isFinal = isFinal;
            this.sources = sources;
        }
    }
    
    private class Parser
    {
        private final XMLStreamReader2 reader;
        private final Resource wrapper;
        private final String name;
        private final String[] nameSingletonArray;
        private final boolean isRestricted;
        private final boolean quiet;
        DeprecationContext deprecations;
        private StringBuilder token;
        private String confName;
        private String confValue;
        private String confInclude;
        private String confTag;
        private boolean confFinal;
        private boolean fallbackAllowed;
        private boolean fallbackEntered;
        private boolean parseToken;
        private List<String> confSource;
        private List<ParsedItem> results;
        
        Parser(final XMLStreamReader2 reader, final Resource wrapper, final boolean quiet) {
            this.deprecations = Configuration.deprecationContext.get();
            this.token = new StringBuilder();
            this.confName = null;
            this.confValue = null;
            this.confInclude = null;
            this.confTag = null;
            this.confFinal = false;
            this.fallbackAllowed = false;
            this.fallbackEntered = false;
            this.parseToken = false;
            this.confSource = new ArrayList<String>();
            this.results = new ArrayList<ParsedItem>();
            this.reader = reader;
            this.wrapper = wrapper;
            this.name = wrapper.getName();
            this.nameSingletonArray = new String[] { this.name };
            this.isRestricted = wrapper.isParserRestricted();
            this.quiet = quiet;
        }
        
        List<ParsedItem> parse() throws IOException, XMLStreamException {
            while (this.reader.hasNext()) {
                this.parseNext();
            }
            return this.results;
        }
        
        private void handleStartElement() throws XMLStreamException, IOException {
            final String localName = this.reader.getLocalName();
            switch (localName) {
                case "property": {
                    this.handleStartProperty();
                    break;
                }
                case "name":
                case "value":
                case "final":
                case "source":
                case "tag": {
                    this.parseToken = true;
                    this.token.setLength(0);
                    break;
                }
                case "include": {
                    this.handleInclude();
                    break;
                }
                case "fallback": {
                    this.fallbackEntered = true;
                }
            }
        }
        
        private void handleStartProperty() {
            this.confName = null;
            this.confValue = null;
            this.confFinal = false;
            this.confTag = null;
            this.confSource.clear();
            for (int attrCount = this.reader.getAttributeCount(), i = 0; i < attrCount; ++i) {
                final String propertyAttr = this.reader.getAttributeLocalName(i);
                if ("name".equals(propertyAttr)) {
                    this.confName = StringInterner.weakIntern(this.reader.getAttributeValue(i));
                }
                else if ("value".equals(propertyAttr)) {
                    this.confValue = StringInterner.weakIntern(this.reader.getAttributeValue(i));
                }
                else if ("final".equals(propertyAttr)) {
                    this.confFinal = "true".equals(this.reader.getAttributeValue(i));
                }
                else if ("source".equals(propertyAttr)) {
                    this.confSource.add(StringInterner.weakIntern(this.reader.getAttributeValue(i)));
                }
                else if ("tag".equals(propertyAttr)) {
                    this.confTag = StringInterner.weakIntern(this.reader.getAttributeValue(i));
                }
            }
        }
        
        private void handleInclude() throws XMLStreamException, IOException {
            this.confInclude = null;
            for (int attrCount = this.reader.getAttributeCount(), i = 0; i < attrCount; ++i) {
                final String attrName = this.reader.getAttributeLocalName(i);
                if ("href".equals(attrName)) {
                    this.confInclude = this.reader.getAttributeValue(i);
                }
            }
            if (this.confInclude == null) {
                return;
            }
            if (this.isRestricted) {
                throw new RuntimeException("Error parsing resource " + this.wrapper + ": XInclude is not supported for restricted resources");
            }
            final URL include = Configuration.this.getResource(this.confInclude);
            List<ParsedItem> items;
            if (include != null) {
                final Resource classpathResource = new Resource(include, this.name, this.wrapper.isParserRestricted());
                synchronized (Configuration.this) {
                    final XMLStreamReader2 includeReader = Configuration.this.getStreamReader(classpathResource, this.quiet);
                    if (includeReader == null) {
                        throw new RuntimeException(classpathResource + " not found");
                    }
                    items = new Parser(includeReader, classpathResource, this.quiet).parse();
                }
            }
            else {
                URL url;
                try {
                    url = new URL(this.confInclude);
                    url.openConnection().connect();
                }
                catch (IOException ioe) {
                    File href = new File(this.confInclude);
                    if (!href.isAbsolute()) {
                        final File baseFile = new File(this.name).getParentFile();
                        href = new File(baseFile, href.getPath());
                    }
                    if (!href.exists()) {
                        this.fallbackAllowed = true;
                        return;
                    }
                    url = href.toURI().toURL();
                }
                final Resource uriResource = new Resource(url, this.name, this.wrapper.isParserRestricted());
                synchronized (Configuration.this) {
                    final XMLStreamReader2 includeReader2 = Configuration.this.getStreamReader(uriResource, this.quiet);
                    if (includeReader2 == null) {
                        throw new RuntimeException(uriResource + " not found");
                    }
                    items = new Parser(includeReader2, uriResource, this.quiet).parse();
                }
            }
            this.results.addAll(items);
        }
        
        void handleEndElement() throws IOException {
            final String tokenStr = this.token.toString();
            final String localName = this.reader.getLocalName();
            switch (localName) {
                case "name": {
                    if (this.token.length() > 0) {
                        this.confName = StringInterner.weakIntern(tokenStr.trim());
                        break;
                    }
                    break;
                }
                case "value": {
                    if (this.token.length() > 0) {
                        this.confValue = StringInterner.weakIntern(tokenStr);
                        break;
                    }
                    break;
                }
                case "final": {
                    this.confFinal = "true".equals(tokenStr);
                    break;
                }
                case "source": {
                    this.confSource.add(StringInterner.weakIntern(tokenStr));
                    break;
                }
                case "tag": {
                    if (this.token.length() > 0) {
                        this.confTag = StringInterner.weakIntern(tokenStr);
                        break;
                    }
                    break;
                }
                case "include": {
                    if (this.fallbackAllowed && !this.fallbackEntered) {
                        throw new IOException("Fetch fail on include for '" + this.confInclude + "' with no fallback while loading '" + this.name + "'");
                    }
                    this.fallbackAllowed = false;
                    this.fallbackEntered = false;
                    break;
                }
                case "property": {
                    this.handleEndProperty();
                    break;
                }
            }
        }
        
        void handleEndProperty() {
            if (this.confName == null || (!this.fallbackAllowed && this.fallbackEntered)) {
                return;
            }
            String[] confSourceArray;
            if (this.confSource.isEmpty()) {
                confSourceArray = this.nameSingletonArray;
            }
            else {
                this.confSource.add(this.name);
                confSourceArray = this.confSource.toArray(new String[this.confSource.size()]);
            }
            if (this.confTag != null) {
                Configuration.this.readTagFromConfig(this.confTag, this.confName, this.confValue, confSourceArray);
            }
            final DeprecatedKeyInfo keyInfo = this.deprecations.getDeprecatedKeyMap().get(this.confName);
            if (keyInfo != null) {
                keyInfo.clearAccessed();
                for (final String key : keyInfo.newKeys) {
                    this.results.add(new ParsedItem(this.name, key, this.confValue, this.confFinal, confSourceArray));
                }
            }
            else {
                this.results.add(new ParsedItem(this.name, this.confName, this.confValue, this.confFinal, confSourceArray));
            }
        }
        
        void parseNext() throws IOException, XMLStreamException {
            switch (this.reader.next()) {
                case 1: {
                    this.handleStartElement();
                    break;
                }
                case 4: {
                    if (this.parseToken) {
                        final char[] text = this.reader.getTextCharacters();
                        this.token.append(text, this.reader.getTextStart(), this.reader.getTextLength());
                        break;
                    }
                    break;
                }
                case 2: {
                    this.handleEndElement();
                    break;
                }
            }
        }
    }
    
    private abstract static class NegativeCacheSentinel
    {
    }
}
