// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.spi;

import java.text.ParsePosition;
import java.util.Date;
import java.util.Currency;
import java.util.WeakHashMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Constructor;
import javax.jdo.JDOUserException;
import javax.jdo.JDOException;
import java.util.Locale;
import javax.jdo.JDOFatalUserException;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import javax.jdo.JDOFatalInternalException;
import java.security.Permission;
import org.xml.sax.ErrorHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.DateFormat;
import java.util.List;
import java.util.Map;

public class JDOImplHelper
{
    private static Map<Class, Meta> registeredClasses;
    private static final Map<Class, Class> authorizedStateManagerClasses;
    private static final List<RegisterClassListener> listeners;
    private static List<StateInterrogation> stateInterrogations;
    private static JDOImplHelper jdoImplHelper;
    private static final I18NHelper msg;
    private static String dateFormatPattern;
    private static DateFormat dateFormat;
    private static DocumentBuilderFactory documentBuilderFactory;
    private static ErrorHandler errorHandler;
    static final Map<Class, StringConstructor> stringConstructorMap;
    
    private JDOImplHelper() {
    }
    
    public static JDOImplHelper getInstance() throws SecurityException {
        final SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission(JDOPermission.GET_METADATA);
        }
        return JDOImplHelper.jdoImplHelper;
    }
    
    public String[] getFieldNames(final Class pcClass) {
        final Meta meta = getMeta(pcClass);
        return meta.getFieldNames();
    }
    
    public Class[] getFieldTypes(final Class pcClass) {
        final Meta meta = getMeta(pcClass);
        return meta.getFieldTypes();
    }
    
    public byte[] getFieldFlags(final Class pcClass) {
        final Meta meta = getMeta(pcClass);
        return meta.getFieldFlags();
    }
    
    public Class getPersistenceCapableSuperclass(final Class pcClass) {
        final Meta meta = getMeta(pcClass);
        return meta.getPersistenceCapableSuperclass();
    }
    
    public PersistenceCapable newInstance(final Class pcClass, final StateManager sm) {
        final Meta meta = getMeta(pcClass);
        final PersistenceCapable pcInstance = meta.getPC();
        return (pcInstance == null) ? null : pcInstance.jdoNewInstance(sm);
    }
    
    public PersistenceCapable newInstance(final Class pcClass, final StateManager sm, final Object oid) {
        final Meta meta = getMeta(pcClass);
        final PersistenceCapable pcInstance = meta.getPC();
        return (pcInstance == null) ? null : pcInstance.jdoNewInstance(sm, oid);
    }
    
    public Object newObjectIdInstance(final Class pcClass) {
        final Meta meta = getMeta(pcClass);
        final PersistenceCapable pcInstance = meta.getPC();
        return (pcInstance == null) ? null : pcInstance.jdoNewObjectIdInstance();
    }
    
    public Object newObjectIdInstance(final Class pcClass, final Object obj) {
        final Meta meta = getMeta(pcClass);
        final PersistenceCapable pcInstance = meta.getPC();
        return (pcInstance == null) ? null : pcInstance.jdoNewObjectIdInstance(obj);
    }
    
    public void copyKeyFieldsToObjectId(final Class pcClass, final PersistenceCapable.ObjectIdFieldSupplier fm, final Object oid) {
        final Meta meta = getMeta(pcClass);
        final PersistenceCapable pcInstance = meta.getPC();
        if (pcInstance == null) {
            throw new JDOFatalInternalException(JDOImplHelper.msg.msg("ERR_AbstractClassNoIdentity", pcClass.getName()));
        }
        pcInstance.jdoCopyKeyFieldsToObjectId(fm, oid);
    }
    
    public void copyKeyFieldsFromObjectId(final Class pcClass, final PersistenceCapable.ObjectIdFieldConsumer fm, final Object oid) {
        final Meta meta = getMeta(pcClass);
        final PersistenceCapable pcInstance = meta.getPC();
        if (pcInstance == null) {
            throw new JDOFatalInternalException(JDOImplHelper.msg.msg("ERR_AbstractClassNoIdentity", pcClass.getName()));
        }
        pcInstance.jdoCopyKeyFieldsFromObjectId(fm, oid);
    }
    
    public static void registerClass(final Class pcClass, final String[] fieldNames, final Class[] fieldTypes, final byte[] fieldFlags, final Class persistenceCapableSuperclass, final PersistenceCapable pc) {
        if (pcClass == null) {
            throw new NullPointerException(JDOImplHelper.msg.msg("ERR_NullClass"));
        }
        final Meta meta = new Meta(fieldNames, fieldTypes, fieldFlags, persistenceCapableSuperclass, pc);
        JDOImplHelper.registeredClasses.put(pcClass, meta);
        synchronized (JDOImplHelper.listeners) {
            if (!JDOImplHelper.listeners.isEmpty()) {
                final RegisterClassEvent event = new RegisterClassEvent(JDOImplHelper.jdoImplHelper, pcClass, fieldNames, fieldTypes, fieldFlags, persistenceCapableSuperclass);
                for (final RegisterClassListener crl : JDOImplHelper.listeners) {
                    if (crl != null) {
                        crl.registerClass(event);
                    }
                }
            }
        }
    }
    
    public void unregisterClasses(final ClassLoader cl) {
        final SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission(JDOPermission.MANAGE_METADATA);
        }
        synchronized (JDOImplHelper.registeredClasses) {
            final Iterator i = JDOImplHelper.registeredClasses.keySet().iterator();
            while (i.hasNext()) {
                final Class pcClass = i.next();
                if (pcClass != null && pcClass.getClassLoader() == cl) {
                    i.remove();
                }
            }
        }
    }
    
    public void unregisterClass(final Class pcClass) {
        if (pcClass == null) {
            throw new NullPointerException(JDOImplHelper.msg.msg("ERR_NullClass"));
        }
        final SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission(JDOPermission.MANAGE_METADATA);
        }
        JDOImplHelper.registeredClasses.remove(pcClass);
    }
    
    public void addRegisterClassListener(final RegisterClassListener crl) {
        HashSet alreadyRegisteredClasses = null;
        synchronized (JDOImplHelper.listeners) {
            JDOImplHelper.listeners.add(crl);
            alreadyRegisteredClasses = new HashSet((Collection<? extends E>)JDOImplHelper.registeredClasses.keySet());
        }
        for (final Class pcClass : alreadyRegisteredClasses) {
            final Meta meta = getMeta(pcClass);
            final RegisterClassEvent event = new RegisterClassEvent(this, pcClass, meta.getFieldNames(), meta.getFieldTypes(), meta.getFieldFlags(), meta.getPersistenceCapableSuperclass());
            crl.registerClass(event);
        }
    }
    
    public void removeRegisterClassListener(final RegisterClassListener crl) {
        synchronized (JDOImplHelper.listeners) {
            JDOImplHelper.listeners.remove(crl);
        }
    }
    
    public Collection<Class> getRegisteredClasses() {
        return (Collection<Class>)Collections.unmodifiableCollection((Collection<? extends Class>)JDOImplHelper.registeredClasses.keySet());
    }
    
    private static Meta getMeta(final Class pcClass) {
        final Meta ret = JDOImplHelper.registeredClasses.get(pcClass);
        if (ret == null) {
            throw new JDOFatalUserException(JDOImplHelper.msg.msg("ERR_NoMetadata", pcClass.getName()));
        }
        return ret;
    }
    
    public static void registerAuthorizedStateManagerClass(final Class smClass) throws SecurityException {
        if (smClass == null) {
            throw new NullPointerException(JDOImplHelper.msg.msg("ERR_NullClass"));
        }
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(JDOPermission.SET_STATE_MANAGER);
        }
        synchronized (JDOImplHelper.authorizedStateManagerClasses) {
            JDOImplHelper.authorizedStateManagerClasses.put(smClass, null);
        }
    }
    
    public static void registerAuthorizedStateManagerClasses(final Collection smClasses) throws SecurityException {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(JDOPermission.SET_STATE_MANAGER);
            synchronized (JDOImplHelper.authorizedStateManagerClasses) {
                final Iterator it = smClasses.iterator();
                while (it.hasNext()) {
                    final Object smClass = it.next();
                    if (!(smClass instanceof Class)) {
                        throw new ClassCastException(JDOImplHelper.msg.msg("ERR_StateManagerClassCast", smClass.getClass().getName()));
                    }
                    registerAuthorizedStateManagerClass(it.next());
                }
            }
        }
    }
    
    public synchronized void registerDocumentBuilderFactory(final DocumentBuilderFactory factory) {
        JDOImplHelper.documentBuilderFactory = factory;
    }
    
    public static DocumentBuilderFactory getRegisteredDocumentBuilderFactory() {
        return JDOImplHelper.documentBuilderFactory;
    }
    
    public synchronized void registerErrorHandler(final ErrorHandler handler) {
        JDOImplHelper.errorHandler = handler;
    }
    
    public static ErrorHandler getRegisteredErrorHandler() {
        return JDOImplHelper.errorHandler;
    }
    
    public static void checkAuthorizedStateManager(final StateManager sm) {
        checkAuthorizedStateManagerClass(sm.getClass());
    }
    
    public static void checkAuthorizedStateManagerClass(final Class smClass) {
        final SecurityManager scm = System.getSecurityManager();
        if (scm == null) {
            return;
        }
        synchronized (JDOImplHelper.authorizedStateManagerClasses) {
            if (JDOImplHelper.authorizedStateManagerClasses.containsKey(smClass)) {
                return;
            }
        }
        scm.checkPermission(JDOPermission.SET_STATE_MANAGER);
    }
    
    public Object registerStringConstructor(final Class cls, final StringConstructor sc) {
        synchronized (JDOImplHelper.stringConstructorMap) {
            return JDOImplHelper.stringConstructorMap.put(cls, sc);
        }
    }
    
    private static Locale getLocale(final String s) {
        String lang = s;
        final int firstUnderbar = s.indexOf(95);
        if (firstUnderbar == -1) {
            return new Locale(lang);
        }
        lang = s.substring(0, firstUnderbar);
        final int secondUnderbar = s.indexOf(95, firstUnderbar + 1);
        if (secondUnderbar == -1) {
            final String country = s.substring(firstUnderbar + 1);
            return new Locale(lang, country);
        }
        final String country = s.substring(firstUnderbar + 1, secondUnderbar);
        final String variant = s.substring(secondUnderbar + 1);
        return new Locale(lang, country, variant);
    }
    
    private static boolean isClassLoadable(final String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    public static Object construct(final String className, final String keyString) {
        try {
            final Class keyClass = Class.forName(className);
            final StringConstructor stringConstructor;
            synchronized (JDOImplHelper.stringConstructorMap) {
                stringConstructor = JDOImplHelper.stringConstructorMap.get(keyClass);
            }
            if (stringConstructor != null) {
                return stringConstructor.construct(keyString);
            }
            final Constructor keyConstructor = keyClass.getConstructor(String.class);
            return keyConstructor.newInstance(keyString);
        }
        catch (JDOException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new JDOUserException(JDOImplHelper.msg.msg("EXC_ObjectIdentityStringConstruction", new Object[] { ex2.toString(), className, keyString }), ex2);
        }
    }
    
    static DateFormat getDateTimeInstance() {
        DateFormat result = null;
        try {
            result = AccessController.doPrivileged((PrivilegedAction<DateFormat>)new PrivilegedAction<DateFormat>() {
                public DateFormat run() {
                    return DateFormat.getDateTimeInstance();
                }
            });
        }
        catch (Exception ex) {
            result = DateFormat.getInstance();
        }
        return result;
    }
    
    public synchronized void registerDateFormat(final DateFormat df) {
        JDOImplHelper.dateFormat = df;
        if (df instanceof SimpleDateFormat) {
            JDOImplHelper.dateFormatPattern = ((SimpleDateFormat)df).toPattern();
        }
        else {
            JDOImplHelper.dateFormatPattern = JDOImplHelper.msg.msg("MSG_unknown");
        }
    }
    
    public synchronized void addStateInterrogation(final StateInterrogation si) {
        final List<StateInterrogation> newList = new ArrayList<StateInterrogation>(JDOImplHelper.stateInterrogations);
        newList.add(si);
        JDOImplHelper.stateInterrogations = newList;
    }
    
    public synchronized void removeStateInterrogation(final StateInterrogation si) {
        final List<StateInterrogation> newList = new ArrayList<StateInterrogation>(JDOImplHelper.stateInterrogations);
        newList.remove(si);
        JDOImplHelper.stateInterrogations = newList;
    }
    
    private synchronized Iterator getStateInterrogationIterator() {
        return JDOImplHelper.stateInterrogations.iterator();
    }
    
    public void nonBinaryCompatibleMakeDirty(final Object pc, final String fieldName) {
        final Iterator sit = this.getStateInterrogationIterator();
        while (sit.hasNext()) {
            final StateInterrogation si = sit.next();
            try {
                if (si.makeDirty(pc, fieldName)) {
                    return;
                }
                continue;
            }
            catch (Throwable t) {}
        }
    }
    
    public boolean nonBinaryCompatibleIs(final Object pc, final StateInterrogationBooleanReturn sibr) {
        final Iterator sit = this.getStateInterrogationIterator();
        while (sit.hasNext()) {
            final StateInterrogation si = sit.next();
            Boolean result;
            try {
                result = sibr.is(pc, si);
            }
            catch (Throwable t) {
                continue;
            }
            if (result != null) {
                return result;
            }
        }
        return false;
    }
    
    public Object nonBinaryCompatibleGet(final Object pc, final StateInterrogationObjectReturn sibr) {
        final Iterator sit = this.getStateInterrogationIterator();
        while (sit.hasNext()) {
            final StateInterrogation si = sit.next();
            Object result;
            try {
                result = sibr.get(pc, si);
            }
            catch (Throwable t) {
                continue;
            }
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    static {
        JDOImplHelper.registeredClasses = (Map<Class, Meta>)Collections.synchronizedMap(new HashMap<Class, Meta>());
        authorizedStateManagerClasses = new WeakHashMap<Class, Class>();
        listeners = new ArrayList<RegisterClassListener>();
        JDOImplHelper.stateInterrogations = new ArrayList<StateInterrogation>();
        JDOImplHelper.jdoImplHelper = new JDOImplHelper();
        msg = I18NHelper.getInstance("javax.jdo.Bundle");
        JDOImplHelper.jdoImplHelper.registerDateFormat(getDateTimeInstance());
        stringConstructorMap = new HashMap<Class, StringConstructor>();
        if (isClassLoadable("java.util.Currency")) {
            JDOImplHelper.jdoImplHelper.registerStringConstructor(Currency.class, new StringConstructor() {
                public Object construct(final String s) {
                    try {
                        return Currency.getInstance(s);
                    }
                    catch (IllegalArgumentException ex) {
                        throw new JDOUserException(JDOImplHelper.msg.msg("EXC_CurrencyStringConstructorIllegalArgument", s), ex);
                    }
                    catch (Exception ex2) {
                        throw new JDOUserException(JDOImplHelper.msg.msg("EXC_CurrencyStringConstructorException"), ex2);
                    }
                }
            });
        }
        JDOImplHelper.jdoImplHelper.registerStringConstructor(Locale.class, new StringConstructor() {
            public Object construct(final String s) {
                try {
                    return getLocale(s);
                }
                catch (Exception ex) {
                    throw new JDOUserException(JDOImplHelper.msg.msg("EXC_LocaleStringConstructorException"), ex);
                }
            }
        });
        JDOImplHelper.jdoImplHelper.registerStringConstructor(Date.class, new StringConstructor() {
            public synchronized Object construct(final String s) {
                try {
                    return new Date(Long.parseLong(s));
                }
                catch (NumberFormatException ex) {
                    final ParsePosition pp = new ParsePosition(0);
                    final Date result = JDOImplHelper.dateFormat.parse(s, pp);
                    if (result == null) {
                        throw new JDOUserException(JDOImplHelper.msg.msg("EXC_DateStringConstructor", new Object[] { s, new Integer(pp.getErrorIndex()), JDOImplHelper.dateFormatPattern }));
                    }
                    return result;
                }
            }
        });
    }
    
    static class Meta
    {
        String[] fieldNames;
        Class[] fieldTypes;
        byte[] fieldFlags;
        Class persistenceCapableSuperclass;
        PersistenceCapable pc;
        
        Meta(final String[] fieldNames, final Class[] fieldTypes, final byte[] fieldFlags, final Class persistenceCapableSuperclass, final PersistenceCapable pc) {
            this.fieldNames = fieldNames;
            this.fieldTypes = fieldTypes;
            this.fieldFlags = fieldFlags;
            this.persistenceCapableSuperclass = persistenceCapableSuperclass;
            this.pc = pc;
        }
        
        String[] getFieldNames() {
            return this.fieldNames;
        }
        
        Class[] getFieldTypes() {
            return this.fieldTypes;
        }
        
        byte[] getFieldFlags() {
            return this.fieldFlags;
        }
        
        Class getPersistenceCapableSuperclass() {
            return this.persistenceCapableSuperclass;
        }
        
        PersistenceCapable getPC() {
            return this.pc;
        }
        
        @Override
        public String toString() {
            return "Meta-" + this.pc.getClass().getName();
        }
    }
    
    public interface StateInterrogationObjectReturn
    {
        Object get(final Object p0, final StateInterrogation p1);
    }
    
    public interface StateInterrogationBooleanReturn
    {
        Boolean is(final Object p0, final StateInterrogation p1);
    }
    
    public interface StringConstructor
    {
        Object construct(final String p0);
    }
}
