// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import java.util.Currency;
import java.util.Locale;
import org.datanucleus.ClassNameConstants;
import java.util.HashSet;
import org.datanucleus.ClassConstants;
import org.datanucleus.state.AppIdObjectIdFieldConsumer;
import org.datanucleus.store.fieldmanager.FieldManager;
import javax.jdo.spi.StateManager;
import org.datanucleus.state.ObjectProvider;
import javax.jdo.JDODataStoreException;
import javax.jdo.JDOUserException;
import java.util.HashMap;
import java.util.Map;
import javax.jdo.JDONullIdentityException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusException;
import javax.jdo.identity.ObjectIdentity;
import javax.jdo.identity.StringIdentity;
import javax.jdo.identity.CharIdentity;
import javax.jdo.identity.ByteIdentity;
import javax.jdo.identity.ShortIdentity;
import javax.jdo.identity.IntIdentity;
import javax.jdo.identity.LongIdentity;
import org.datanucleus.identity.OID;
import javax.jdo.identity.SingleFieldIdentity;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.InvalidPrimaryKeyException;
import java.lang.reflect.Modifier;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractClassMetaData;
import javax.jdo.spi.Detachable;
import org.datanucleus.api.jdo.state.LifeCycleStateFactory;
import org.datanucleus.state.LifeCycleState;
import javax.jdo.PersistenceManager;
import javax.jdo.JDOHelper;
import javax.jdo.spi.PersistenceCapable;
import org.datanucleus.ExecutionContext;
import java.io.Serializable;
import java.util.Set;
import org.datanucleus.util.Localiser;
import org.datanucleus.api.ApiAdapter;

public class JDOAdapter implements ApiAdapter
{
    protected static final Localiser LOCALISER;
    protected static Set<String> defaultPersistentTypeNames;
    
    public String getName() {
        return "JDO";
    }
    
    public boolean isMemberDefaultPersistent(final Class type) {
        final String typeName = type.getName();
        return JDOAdapter.defaultPersistentTypeNames.contains(typeName) || (Enum.class.isAssignableFrom(type) || Serializable.class.isAssignableFrom(type)) || this.isPersistable(type);
    }
    
    public boolean isManaged(final Object pc) {
        return this.getExecutionContext(pc) != null;
    }
    
    public ExecutionContext getExecutionContext(final Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof PersistenceCapable) {
            final PersistenceManager pm = JDOHelper.getPersistenceManager(obj);
            if (pm == null) {
                return null;
            }
            return ((JDOPersistenceManager)pm).getExecutionContext();
        }
        else {
            if (obj instanceof PersistenceManager) {
                return ((JDOPersistenceManager)obj).getExecutionContext();
            }
            return null;
        }
    }
    
    public LifeCycleState getLifeCycleState(final int stateType) {
        return LifeCycleStateFactory.getLifeCycleState(stateType);
    }
    
    public boolean isPersistent(final Object obj) {
        return JDOHelper.isPersistent(obj);
    }
    
    public boolean isNew(final Object obj) {
        return JDOHelper.isNew(obj);
    }
    
    public boolean isDirty(final Object obj) {
        return JDOHelper.isDirty(obj);
    }
    
    public boolean isDeleted(final Object obj) {
        return JDOHelper.isDeleted(obj);
    }
    
    public boolean isDetached(final Object obj) {
        return JDOHelper.isDetached(obj);
    }
    
    public boolean isTransactional(final Object obj) {
        return JDOHelper.isTransactional(obj);
    }
    
    public boolean isPersistable(final Object obj) {
        return obj != null && obj instanceof PersistenceCapable;
    }
    
    public boolean isPersistable(final Class cls) {
        return cls != null && PersistenceCapable.class.isAssignableFrom(cls);
    }
    
    public boolean isDetachable(final Object obj) {
        return obj != null && obj instanceof Detachable;
    }
    
    public String getObjectState(final Object obj) {
        if (obj == null) {
            return null;
        }
        return JDOHelper.getObjectState(obj).toString();
    }
    
    public void makeDirty(final Object obj, final String member) {
        ((PersistenceCapable)obj).jdoMakeDirty(member);
    }
    
    public Object getIdForObject(final Object obj) {
        if (!this.isPersistable(obj)) {
            return null;
        }
        return ((PersistenceCapable)obj).jdoGetObjectId();
    }
    
    public Object getVersionForObject(final Object obj) {
        if (!this.isPersistable(obj)) {
            return null;
        }
        return ((PersistenceCapable)obj).jdoGetVersion();
    }
    
    public boolean isValidPrimaryKeyClass(final Class pkClass, final AbstractClassMetaData cmd, final ClassLoaderResolver clr, final int noOfPkFields, final MetaDataManager mmgr) {
        if (ClassUtils.isInnerClass(pkClass.getName()) && !Modifier.isStatic(pkClass.getModifiers())) {
            throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019000", cmd.getFullClassName(), pkClass.getName());
        }
        if (!Modifier.isPublic(pkClass.getModifiers())) {
            throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019001", cmd.getFullClassName(), pkClass.getName());
        }
        if (!Serializable.class.isAssignableFrom(pkClass)) {
            throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019002", cmd.getFullClassName(), pkClass.getName());
        }
        if (this.isSingleFieldIdentityClass(pkClass.getName())) {
            if (noOfPkFields != 1) {
                throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019003", cmd.getFullClassName());
            }
        }
        else {
            try {
                final Constructor constructor = pkClass.getConstructor((Class[])new Class[0]);
                if (constructor == null || !Modifier.isPublic(constructor.getModifiers())) {
                    throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019004", cmd.getFullClassName(), pkClass.getName());
                }
            }
            catch (NoSuchMethodException ex) {
                throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019004", cmd.getFullClassName(), pkClass.getName());
            }
            try {
                final Constructor constructor = pkClass.getConstructor(String.class);
                if (constructor == null || !Modifier.isPublic(constructor.getModifiers())) {
                    throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019005", cmd.getFullClassName(), pkClass.getName());
                }
            }
            catch (NoSuchMethodException ex2) {}
            try {
                final Method method = pkClass.getMethod("toString", (Class[])new Class[0]);
                if (method == null || !Modifier.isPublic(method.getModifiers()) || method.getDeclaringClass().equals(Object.class)) {
                    throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019006", cmd.getFullClassName(), pkClass.getName());
                }
            }
            catch (NoSuchMethodException ex3) {}
            try {
                final Method method = pkClass.getMethod("hashCode", (Class[])new Class[0]);
                if (method == null || method.getDeclaringClass().equals(Object.class)) {
                    throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019007", cmd.getFullClassName(), pkClass.getName());
                }
            }
            catch (NoSuchMethodException ex4) {}
            try {
                final Method method = pkClass.getMethod("equals", Object.class);
                if (method == null || method.getDeclaringClass().equals(Object.class)) {
                    throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019008", cmd.getFullClassName(), pkClass.getName());
                }
            }
            catch (NoSuchMethodException ex5) {}
            int noPkFields = this.processPrimaryKeyClass(pkClass, cmd, clr, mmgr);
            for (final Class<?> supercls : ClassUtils.getSuperclasses(pkClass)) {
                noPkFields += this.processPrimaryKeyClass(supercls, cmd, clr, mmgr);
            }
            if (noOfPkFields != noPkFields && cmd.getIdentityType() == IdentityType.APPLICATION) {
                throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019015", cmd.getFullClassName(), pkClass.getName(), "" + noOfPkFields, "" + noPkFields);
            }
        }
        return true;
    }
    
    private int processPrimaryKeyClass(final Class pkClass, final AbstractClassMetaData cmd, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        int noOfPkFields = 0;
        final Field[] fieldsInPkClass = pkClass.getDeclaredFields();
        for (int i = 0; i < fieldsInPkClass.length; ++i) {
            if (!Modifier.isStatic(fieldsInPkClass[i].getModifiers())) {
                if (!fieldsInPkClass[i].getType().isPrimitive() && !Serializable.class.isAssignableFrom(fieldsInPkClass[i].getType())) {
                    throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019009", cmd.getFullClassName(), pkClass.getName(), fieldsInPkClass[i].getName());
                }
                if (!Modifier.isPublic(fieldsInPkClass[i].getModifiers())) {
                    throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019010", cmd.getFullClassName(), pkClass.getName(), fieldsInPkClass[i].getName());
                }
                final AbstractMemberMetaData fieldInPcClass = cmd.getMetaDataForMember(fieldsInPkClass[i].getName());
                boolean found_field = false;
                if (fieldInPcClass == null) {
                    throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019011", cmd.getFullClassName(), pkClass.getName(), fieldsInPkClass[i].getName());
                }
                if (fieldInPcClass.getTypeName().equals(fieldsInPkClass[i].getType().getName())) {
                    found_field = true;
                }
                if (!found_field) {
                    final String fieldTypePkClass = fieldsInPkClass[i].getType().getName();
                    final AbstractClassMetaData ref_cmd = mmgr.getMetaDataForClassInternal(fieldInPcClass.getType(), clr);
                    if (ref_cmd == null) {
                        throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019012", cmd.getFullClassName(), pkClass.getName(), fieldsInPkClass[i].getName(), fieldInPcClass.getType().getName());
                    }
                    if (ref_cmd.getObjectidClass() == null && this.isSingleFieldIdentityClass(fieldTypePkClass)) {
                        throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019014", cmd.getFullClassName(), pkClass.getName(), fieldsInPkClass[i].getName(), fieldTypePkClass, ref_cmd.getFullClassName());
                    }
                    if (!fieldTypePkClass.equals(ref_cmd.getObjectidClass())) {
                        throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019013", cmd.getFullClassName(), pkClass.getName(), fieldsInPkClass[i].getName(), fieldTypePkClass, ref_cmd.getObjectidClass());
                    }
                    found_field = true;
                }
                if (!found_field) {
                    throw new InvalidPrimaryKeyException(JDOAdapter.LOCALISER, "019012", cmd.getFullClassName(), pkClass.getName(), fieldsInPkClass[i].getName(), fieldInPcClass.getType().getName());
                }
                ++noOfPkFields;
            }
        }
        return noOfPkFields;
    }
    
    public boolean isSingleFieldIdentity(final Object id) {
        return id instanceof SingleFieldIdentity;
    }
    
    public boolean isDatastoreIdentity(final Object id) {
        return id != null && id instanceof OID;
    }
    
    public boolean isSingleFieldIdentityClass(final String className) {
        return className != null && className.length() >= 1 && (className.equals(JDOClassNameConstants.JAVAX_JDO_IDENTITY_BYTE_IDENTITY) || className.equals(JDOClassNameConstants.JAVAX_JDO_IDENTITY_CHAR_IDENTITY) || className.equals(JDOClassNameConstants.JAVAX_JDO_IDENTITY_INT_IDENTITY) || className.equals(JDOClassNameConstants.JAVAX_JDO_IDENTITY_LONG_IDENTITY) || className.equals(JDOClassNameConstants.JAVAX_JDO_IDENTITY_OBJECT_IDENTITY) || className.equals(JDOClassNameConstants.JAVAX_JDO_IDENTITY_SHORT_IDENTITY) || className.equals(JDOClassNameConstants.JAVAX_JDO_IDENTITY_STRING_IDENTITY));
    }
    
    public String getSingleFieldIdentityClassNameForLong() {
        return JDOClassNameConstants.JAVAX_JDO_IDENTITY_LONG_IDENTITY;
    }
    
    public String getSingleFieldIdentityClassNameForInt() {
        return JDOClassNameConstants.JAVAX_JDO_IDENTITY_INT_IDENTITY;
    }
    
    public String getSingleFieldIdentityClassNameForShort() {
        return JDOClassNameConstants.JAVAX_JDO_IDENTITY_SHORT_IDENTITY;
    }
    
    public String getSingleFieldIdentityClassNameForByte() {
        return JDOClassNameConstants.JAVAX_JDO_IDENTITY_BYTE_IDENTITY;
    }
    
    public String getSingleFieldIdentityClassNameForChar() {
        return JDOClassNameConstants.JAVAX_JDO_IDENTITY_CHAR_IDENTITY;
    }
    
    public String getSingleFieldIdentityClassNameForString() {
        return JDOClassNameConstants.JAVAX_JDO_IDENTITY_STRING_IDENTITY;
    }
    
    public String getSingleFieldIdentityClassNameForObject() {
        return JDOClassNameConstants.JAVAX_JDO_IDENTITY_OBJECT_IDENTITY;
    }
    
    public Class getTargetClassForSingleFieldIdentity(final Object id) {
        if (id instanceof SingleFieldIdentity) {
            return ((SingleFieldIdentity)id).getTargetClass();
        }
        return null;
    }
    
    public String getTargetClassNameForSingleFieldIdentity(final Object id) {
        if (id instanceof SingleFieldIdentity) {
            return ((SingleFieldIdentity)id).getTargetClassName();
        }
        return null;
    }
    
    public Object getTargetKeyForSingleFieldIdentity(final Object id) {
        if (id instanceof SingleFieldIdentity) {
            return ((SingleFieldIdentity)id).getKeyAsObject();
        }
        return null;
    }
    
    public Class getKeyTypeForSingleFieldIdentityType(final Class idType) {
        if (idType == null) {
            return null;
        }
        if (!this.isSingleFieldIdentityClass(idType.getName())) {
            return null;
        }
        if (LongIdentity.class.isAssignableFrom(idType)) {
            return Long.class;
        }
        if (IntIdentity.class.isAssignableFrom(idType)) {
            return Integer.class;
        }
        if (ShortIdentity.class.isAssignableFrom(idType)) {
            return Short.class;
        }
        if (ByteIdentity.class.isAssignableFrom(idType)) {
            return Byte.class;
        }
        if (CharIdentity.class.isAssignableFrom(idType)) {
            return Character.class;
        }
        if (StringIdentity.class.isAssignableFrom(idType)) {
            return String.class;
        }
        if (ObjectIdentity.class.isAssignableFrom(idType)) {
            return Object.class;
        }
        return null;
    }
    
    public Object getNewSingleFieldIdentity(final Class idType, final Class pcType, final Object value) {
        if (idType == null) {
            throw new NucleusException(JDOAdapter.LOCALISER.msg("029001", pcType)).setFatal();
        }
        if (pcType == null) {
            throw new NucleusException(JDOAdapter.LOCALISER.msg("029000", idType)).setFatal();
        }
        if (value == null) {
            throw new NucleusException(JDOAdapter.LOCALISER.msg("029003", idType, pcType)).setFatal();
        }
        if (!SingleFieldIdentity.class.isAssignableFrom(idType)) {
            throw new NucleusException(JDOAdapter.LOCALISER.msg("029002", idType.getName(), pcType.getName())).setFatal();
        }
        SingleFieldIdentity id = null;
        Class keyType = null;
        if (idType == LongIdentity.class) {
            keyType = Long.class;
            if (!(value instanceof Long)) {
                throw new NucleusException(JDOAdapter.LOCALISER.msg("029004", idType.getName(), pcType.getName(), value.getClass().getName(), "Long")).setFatal();
            }
        }
        else if (idType == IntIdentity.class) {
            keyType = Integer.class;
            if (!(value instanceof Integer)) {
                throw new NucleusException(JDOAdapter.LOCALISER.msg("029004", idType.getName(), pcType.getName(), value.getClass().getName(), "Integer")).setFatal();
            }
        }
        else if (idType == StringIdentity.class) {
            keyType = String.class;
            if (!(value instanceof String)) {
                throw new NucleusException(JDOAdapter.LOCALISER.msg("029004", idType.getName(), pcType.getName(), value.getClass().getName(), "String")).setFatal();
            }
        }
        else if (idType == ByteIdentity.class) {
            keyType = Byte.class;
            if (!(value instanceof Byte)) {
                throw new NucleusException(JDOAdapter.LOCALISER.msg("029004", idType.getName(), pcType.getName(), value.getClass().getName(), "Byte")).setFatal();
            }
        }
        else if (idType == ShortIdentity.class) {
            keyType = Short.class;
            if (!(value instanceof Short)) {
                throw new NucleusException(JDOAdapter.LOCALISER.msg("029004", idType.getName(), pcType.getName(), value.getClass().getName(), "Short")).setFatal();
            }
        }
        else if (idType == CharIdentity.class) {
            keyType = Character.class;
            if (!(value instanceof Character)) {
                throw new NucleusException(JDOAdapter.LOCALISER.msg("029004", idType.getName(), pcType.getName(), value.getClass().getName(), "Character")).setFatal();
            }
        }
        else {
            keyType = Object.class;
        }
        try {
            final Class[] ctrArgs = { Class.class, keyType };
            final Constructor ctr = idType.getConstructor((Class<?>[])ctrArgs);
            final Object[] args = { pcType, value };
            id = ctr.newInstance(args);
        }
        catch (Exception e) {
            NucleusLogger.PERSISTENCE.error("Error encountered while creating SingleFieldIdentity instance of type \"" + idType.getName() + "\"");
            NucleusLogger.PERSISTENCE.error(e);
            return null;
        }
        return id;
    }
    
    public Object getNewApplicationIdentityObjectId(final ClassLoaderResolver clr, final AbstractClassMetaData acmd, final String value) {
        if (acmd.getIdentityType() != IdentityType.APPLICATION) {
            throw new NucleusException("This class (" + acmd.getFullClassName() + ") doesn't use application-identity!");
        }
        final Class targetClass = clr.classForName(acmd.getFullClassName());
        final Class idType = clr.classForName(acmd.getObjectidClass());
        Object id = null;
        if (acmd.usesSingleFieldIdentityClass()) {
            try {
                Class[] ctrArgs;
                if (ObjectIdentity.class.isAssignableFrom(idType)) {
                    ctrArgs = new Class[] { Class.class, Object.class };
                }
                else {
                    ctrArgs = new Class[] { Class.class, String.class };
                }
                final Constructor ctr = idType.getConstructor((Class[])ctrArgs);
                final Object[] args = { targetClass, value };
                id = ctr.newInstance(args);
                return id;
            }
            catch (Exception e) {
                throw new NucleusException("Error encountered while creating SingleFieldIdentity instance with key \"" + value + "\"", e);
            }
        }
        if (Modifier.isAbstract(targetClass.getModifiers()) && acmd.getObjectidClass() != null) {
            try {
                final Constructor c = clr.classForName(acmd.getObjectidClass()).getDeclaredConstructor(String.class);
                id = c.newInstance(value);
                return id;
            }
            catch (Exception e) {
                final String msg = JDOAdapter.LOCALISER.msg("010030", acmd.getObjectidClass(), acmd.getFullClassName());
                NucleusLogger.PERSISTENCE.error(msg);
                NucleusLogger.PERSISTENCE.error(e);
                throw new NucleusUserException(msg);
            }
        }
        clr.classForName(targetClass.getName(), true);
        id = NucleusJDOHelper.getJDOImplHelper().newObjectIdInstance(targetClass, value);
        return id;
    }
    
    public Object getNewApplicationIdentityObjectId(final Object pc, final AbstractClassMetaData cmd) {
        if (pc == null || cmd == null) {
            return null;
        }
        try {
            final Object id = ((PersistenceCapable)pc).jdoNewObjectIdInstance();
            if (!cmd.usesSingleFieldIdentityClass()) {
                ((PersistenceCapable)pc).jdoCopyKeyFieldsToObjectId(id);
            }
            return id;
        }
        catch (JDONullIdentityException nie) {
            return null;
        }
    }
    
    public Object getNewApplicationIdentityObjectId(final Class cls, final Object key) {
        return NucleusJDOHelper.getJDOImplHelper().newObjectIdInstance(cls, key);
    }
    
    public boolean allowPersistOfDeletedObject() {
        return false;
    }
    
    public boolean allowDeleteOfNonPersistentObject() {
        return false;
    }
    
    public boolean allowReadFieldOfDeletedObject() {
        return false;
    }
    
    public boolean clearLoadedFlagsOnDeleteObject() {
        return true;
    }
    
    public boolean getDefaultCascadePersistForField() {
        return true;
    }
    
    public boolean getDefaultCascadeUpdateForField() {
        return true;
    }
    
    public boolean getDefaultCascadeDeleteForField() {
        return false;
    }
    
    public boolean getDefaultCascadeRefreshForField() {
        return false;
    }
    
    public boolean getDefaultDFGForPersistableField() {
        return false;
    }
    
    public Map getDefaultFactoryProperties() {
        final Map props = new HashMap();
        props.put("datanucleus.DetachAllOnCommit", "false");
        props.put("datanucleus.CopyOnAttach", "true");
        props.put("datanucleus.identifierFactory", "datanucleus2");
        props.put("datanucleus.persistenceByReachabilityAtCommit", "true");
        props.put("datanucleus.query.sql.allowAll", "false");
        props.put("datanucleus.validation.mode", "none");
        return props;
    }
    
    public RuntimeException getUserExceptionForException(final String msg, final Exception e) {
        return new JDOUserException(msg, e);
    }
    
    public RuntimeException getDataStoreExceptionForException(final String msg, final Exception e) {
        return new JDODataStoreException(msg, e);
    }
    
    public RuntimeException getApiExceptionForNucleusException(final NucleusException ne) {
        return NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
    }
    
    public Object getCopyOfPersistableObject(final Object obj, final ObjectProvider op, final int[] fieldNumbers) {
        final PersistenceCapable pc = (PersistenceCapable)obj;
        final PersistenceCapable copy = pc.jdoNewInstance((StateManager)op);
        copy.jdoCopyFields(pc, fieldNumbers);
        return copy;
    }
    
    public void copyFieldsFromPersistableObject(final Object pc, final int[] fieldNumbers, final Object pc2) {
        ((PersistenceCapable)pc2).jdoCopyFields(pc, fieldNumbers);
    }
    
    public void copyPkFieldsToPersistableObjectFromId(final Object pc, final Object id, final FieldManager fm) {
        final PersistenceCapable.ObjectIdFieldConsumer consumer = new AppIdObjectIdFieldConsumer(this, fm);
        ((PersistenceCapable)pc).jdoCopyKeyFieldsFromObjectId(consumer, id);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        (JDOAdapter.defaultPersistentTypeNames = new HashSet<String>()).add(ClassNameConstants.BOOLEAN);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.BYTE);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.CHAR);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.DOUBLE);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.FLOAT);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.INT);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.LONG);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.SHORT);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_BOOLEAN);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_BYTE);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_CHARACTER);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_DOUBLE);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_FLOAT);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_INTEGER);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_LONG);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_SHORT);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_STRING);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_UTIL_DATE);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_SQL_DATE);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_SQL_TIME);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_SQL_TIMESTAMP);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_MATH_BIGDECIMAL);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_MATH_BIGINTEGER);
        JDOAdapter.defaultPersistentTypeNames.add(Locale.class.getName());
        JDOAdapter.defaultPersistentTypeNames.add(Currency.class.getName());
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.BOOLEAN_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.BYTE_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.CHAR_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.DOUBLE_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.FLOAT_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.INT_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.LONG_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.SHORT_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_BOOLEAN_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_BYTE_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_CHARACTER_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_DOUBLE_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_FLOAT_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_INTEGER_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_LONG_ARRAY);
        JDOAdapter.defaultPersistentTypeNames.add(ClassNameConstants.JAVA_LANG_SHORT_ARRAY);
    }
}
