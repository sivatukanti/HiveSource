// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo;

import javax.jdo.spi.JDOImplHelper;
import javax.jdo.JDOHelper;
import javax.jdo.JDOFatalInternalException;
import javax.jdo.JDODetachedFieldAccessException;
import org.datanucleus.util.DetachListener;
import javax.jdo.spi.StateManager;
import javax.jdo.PersistenceManager;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.Detachable;
import org.datanucleus.asm.Type;
import javax.jdo.identity.ObjectIdentity;
import javax.jdo.identity.ByteIdentity;
import javax.jdo.identity.CharIdentity;
import javax.jdo.identity.ShortIdentity;
import javax.jdo.identity.StringIdentity;
import javax.jdo.identity.IntIdentity;
import javax.jdo.identity.LongIdentity;
import org.datanucleus.enhancer.EnhancementNamer;

public class JDOEnhancementNamer implements EnhancementNamer
{
    private static JDOEnhancementNamer instance;
    private static final Class CL_Detachable;
    private static final Class CL_Persistable;
    private static final Class CL_ObjectIdFieldConsumer;
    private static final Class CL_ObjectIdFieldSupplier;
    private static final Class CL_PersistenceManager;
    private static final Class CL_StateManager;
    private static final String ACN_DetachListener;
    private static final String ACN_StateManager;
    private static final String ACN_PersistenceManager;
    private static final String ACN_Persistable;
    private static final String ACN_Detachable;
    private static final String ACN_ObjectIdFieldConsumer;
    private static final String ACN_ObjectIdFieldSupplier;
    private static final String ACN_DetachedFieldAccessException;
    private static final String ACN_FatalInternalException;
    private static final String ACN_Helper;
    private static final String ACN_ImplHelper;
    private static final String CD_ByteIdentity;
    private static final String CD_CharIdentity;
    private static final String CD_IntIdentity;
    private static final String CD_LongIdentity;
    private static final String CD_ShortIdentity;
    private static final String CD_StringIdentity;
    private static final String CD_ObjectIdentity;
    private static final String CD_StateManager;
    private static final String CD_PersistenceManager;
    private static final String CD_PersistenceCapable;
    private static final String CD_Detachable;
    private static final String CD_ObjectIdFieldConsumer;
    private static final String CD_ObjectIdFieldSupplier;
    private static final String CD_String;
    private static final String CD_Object;
    
    public static JDOEnhancementNamer getInstance() {
        if (JDOEnhancementNamer.instance == null) {
            JDOEnhancementNamer.instance = new JDOEnhancementNamer();
        }
        return JDOEnhancementNamer.instance;
    }
    
    protected JDOEnhancementNamer() {
    }
    
    @Override
    public String getStateManagerFieldName() {
        return "jdoStateManager";
    }
    
    @Override
    public String getFlagsFieldName() {
        return "jdoFlags";
    }
    
    @Override
    public String getFieldNamesFieldName() {
        return "jdoFieldNames";
    }
    
    @Override
    public String getFieldTypesFieldName() {
        return "jdoFieldTypes";
    }
    
    @Override
    public String getFieldFlagsFieldName() {
        return "jdoFieldFlags";
    }
    
    @Override
    public String getPersistableSuperclassFieldName() {
        return "jdoPersistenceCapableSuperclass";
    }
    
    @Override
    public String getInheritedFieldCountFieldName() {
        return "jdoInheritedFieldCount";
    }
    
    @Override
    public String getDetachedStateFieldName() {
        return "jdoDetachedState";
    }
    
    @Override
    public String getSerialVersionUidFieldName() {
        return "serialVersionUID";
    }
    
    @Override
    public String getFieldNamesInitMethodName() {
        return "__jdoFieldNamesInit";
    }
    
    @Override
    public String getFieldTypesInitMethodName() {
        return "__jdoFieldTypesInit";
    }
    
    @Override
    public String getFieldFlagsInitMethodName() {
        return "__jdoFieldFlagsInit";
    }
    
    @Override
    public String getGetObjectIdMethodName() {
        return "jdoGetObjectId";
    }
    
    @Override
    public String getGetTransactionalObjectIdMethodName() {
        return "jdoGetTransactionalObjectId";
    }
    
    @Override
    public String getGetVersionMethodName() {
        return "jdoGetVersion";
    }
    
    @Override
    public String getIsDetachedMethodName() {
        return "jdoIsDetached";
    }
    
    @Override
    public String getIsDetachedInternalMethodName() {
        return "jdoIsDetachedInternal";
    }
    
    @Override
    public String getIsDeletedMethodName() {
        return "jdoIsDeleted";
    }
    
    @Override
    public String getIsDirtyMethodName() {
        return "jdoIsDirty";
    }
    
    @Override
    public String getIsNewMethodName() {
        return "jdoIsNew";
    }
    
    @Override
    public String getIsPersistentMethodName() {
        return "jdoIsPersistent";
    }
    
    @Override
    public String getIsTransactionalMethodName() {
        return "jdoIsTransactional";
    }
    
    @Override
    public String getGetPersistenceManagerMethodName() {
        return "jdoGetPersistenceManager";
    }
    
    @Override
    public String getPreSerializeMethodName() {
        return "jdoPreSerialize";
    }
    
    @Override
    public String getGetInheritedFieldCountMethodName() {
        return "__jdoGetInheritedFieldCount";
    }
    
    @Override
    public String getSuperCloneMethodName() {
        return "jdoSuperClone";
    }
    
    @Override
    public String getGetManagedFieldCountMethodName() {
        return "jdoGetManagedFieldCount";
    }
    
    @Override
    public String getPersistableSuperclassInitMethodName() {
        return "__jdoPersistenceCapableSuperclassInit";
    }
    
    @Override
    public String getLoadClassMethodName() {
        return "___jdo$loadClass";
    }
    
    @Override
    public String getCopyFieldMethodName() {
        return "jdoCopyField";
    }
    
    @Override
    public String getCopyFieldsMethodName() {
        return "jdoCopyFields";
    }
    
    @Override
    public String getCopyKeyFieldsFromObjectIdMethodName() {
        return "jdoCopyKeyFieldsFromObjectId";
    }
    
    @Override
    public String getCopyKeyFieldsToObjectIdMethodName() {
        return "jdoCopyKeyFieldsToObjectId";
    }
    
    @Override
    public String getProvideFieldMethodName() {
        return "jdoProvideField";
    }
    
    @Override
    public String getProvideFieldsMethodName() {
        return "jdoProvideFields";
    }
    
    @Override
    public String getReplaceFieldMethodName() {
        return "jdoReplaceField";
    }
    
    @Override
    public String getReplaceFieldsMethodName() {
        return "jdoReplaceFields";
    }
    
    @Override
    public String getReplaceFlagsMethodName() {
        return "jdoReplaceFlags";
    }
    
    @Override
    public String getReplaceStateManagerMethodName() {
        return "jdoReplaceStateManager";
    }
    
    @Override
    public String getReplaceDetachedStateMethodName() {
        return "jdoReplaceDetachedState";
    }
    
    @Override
    public String getMakeDirtyMethodName() {
        return "jdoMakeDirty";
    }
    
    @Override
    public String getMakeDirtyDetachedMethodName() {
        return "jdoMakeDirtyDetached";
    }
    
    @Override
    public String getNewInstanceMethodName() {
        return "jdoNewInstance";
    }
    
    @Override
    public String getNewObjectIdInstanceMethodName() {
        return "jdoNewObjectIdInstance";
    }
    
    @Override
    public String getGetMethodPrefixMethodName() {
        return "jdoGet";
    }
    
    @Override
    public String getSetMethodPrefixMethodName() {
        return "jdoSet";
    }
    
    @Override
    public String getDetachListenerAsmClassName() {
        return JDOEnhancementNamer.ACN_DetachListener;
    }
    
    @Override
    public String getStateManagerAsmClassName() {
        return JDOEnhancementNamer.ACN_StateManager;
    }
    
    @Override
    public String getPersistenceManagerAsmClassName() {
        return JDOEnhancementNamer.ACN_PersistenceManager;
    }
    
    @Override
    public String getPersistableAsmClassName() {
        return JDOEnhancementNamer.ACN_Persistable;
    }
    
    @Override
    public String getDetachableAsmClassName() {
        return JDOEnhancementNamer.ACN_Detachable;
    }
    
    @Override
    public String getObjectIdFieldConsumerAsmClassName() {
        return JDOEnhancementNamer.ACN_ObjectIdFieldConsumer;
    }
    
    @Override
    public String getObjectIdFieldSupplierAsmClassName() {
        return JDOEnhancementNamer.ACN_ObjectIdFieldSupplier;
    }
    
    @Override
    public String getDetachedFieldAccessExceptionAsmClassName() {
        return JDOEnhancementNamer.ACN_DetachedFieldAccessException;
    }
    
    @Override
    public String getFatalInternalExceptionAsmClassName() {
        return JDOEnhancementNamer.ACN_FatalInternalException;
    }
    
    @Override
    public String getHelperAsmClassName() {
        return JDOEnhancementNamer.ACN_Helper;
    }
    
    @Override
    public String getImplHelperAsmClassName() {
        return JDOEnhancementNamer.ACN_ImplHelper;
    }
    
    @Override
    public String getByteIdentityDescriptor() {
        return JDOEnhancementNamer.CD_ByteIdentity;
    }
    
    @Override
    public String getCharIdentityDescriptor() {
        return JDOEnhancementNamer.CD_CharIdentity;
    }
    
    @Override
    public String getIntIdentityDescriptor() {
        return JDOEnhancementNamer.CD_IntIdentity;
    }
    
    @Override
    public String getLongIdentityDescriptor() {
        return JDOEnhancementNamer.CD_LongIdentity;
    }
    
    @Override
    public String getShortIdentityDescriptor() {
        return JDOEnhancementNamer.CD_ShortIdentity;
    }
    
    @Override
    public String getStringIdentityDescriptor() {
        return JDOEnhancementNamer.CD_StringIdentity;
    }
    
    @Override
    public String getObjectIdentityDescriptor() {
        return JDOEnhancementNamer.CD_ObjectIdentity;
    }
    
    @Override
    public String getStateManagerDescriptor() {
        return JDOEnhancementNamer.CD_StateManager;
    }
    
    @Override
    public String getPersistenceManagerDescriptor() {
        return JDOEnhancementNamer.CD_PersistenceManager;
    }
    
    @Override
    public String getPersistableDescriptor() {
        return JDOEnhancementNamer.CD_PersistenceCapable;
    }
    
    @Override
    public String getDetachableDescriptor() {
        return JDOEnhancementNamer.CD_Detachable;
    }
    
    @Override
    public String getSingleFieldIdentityDescriptor(final String oidClassName) {
        if (oidClassName.equals(LongIdentity.class.getName())) {
            return JDOEnhancementNamer.CD_LongIdentity;
        }
        if (oidClassName.equals(IntIdentity.class.getName())) {
            return JDOEnhancementNamer.CD_IntIdentity;
        }
        if (oidClassName.equals(StringIdentity.class.getName())) {
            return JDOEnhancementNamer.CD_StringIdentity;
        }
        if (oidClassName.equals(ShortIdentity.class.getName())) {
            return JDOEnhancementNamer.CD_ShortIdentity;
        }
        if (oidClassName.equals(CharIdentity.class.getName())) {
            return JDOEnhancementNamer.CD_CharIdentity;
        }
        if (oidClassName.equals(ByteIdentity.class.getName())) {
            return JDOEnhancementNamer.CD_ByteIdentity;
        }
        if (oidClassName.equals(ObjectIdentity.class.getName())) {
            return JDOEnhancementNamer.CD_ObjectIdentity;
        }
        return null;
    }
    
    @Override
    public String getTypeDescriptorForSingleFieldIdentityGetKey(final String oidClassName) {
        if (oidClassName.equals(LongIdentity.class.getName())) {
            return Type.LONG_TYPE.getDescriptor();
        }
        if (oidClassName.equals(IntIdentity.class.getName())) {
            return Type.INT_TYPE.getDescriptor();
        }
        if (oidClassName.equals(ShortIdentity.class.getName())) {
            return Type.SHORT_TYPE.getDescriptor();
        }
        if (oidClassName.equals(CharIdentity.class.getName())) {
            return Type.CHAR_TYPE.getDescriptor();
        }
        if (oidClassName.equals(ByteIdentity.class.getName())) {
            return Type.BYTE_TYPE.getDescriptor();
        }
        if (oidClassName.equals(StringIdentity.class.getName())) {
            return JDOEnhancementNamer.CD_String;
        }
        if (oidClassName.equals(ObjectIdentity.class.getName())) {
            return JDOEnhancementNamer.CD_Object;
        }
        return null;
    }
    
    @Override
    public String getTypeNameForUseWithSingleFieldIdentity(final String oidClassName) {
        if (oidClassName == null) {
            return null;
        }
        if (oidClassName.equals(ByteIdentity.class.getName())) {
            return "Byte";
        }
        if (oidClassName.equals(CharIdentity.class.getName())) {
            return "Char";
        }
        if (oidClassName.equals(IntIdentity.class.getName())) {
            return "Int";
        }
        if (oidClassName.equals(LongIdentity.class.getName())) {
            return "Long";
        }
        if (oidClassName.equals(ShortIdentity.class.getName())) {
            return "Short";
        }
        if (oidClassName.equals(StringIdentity.class.getName())) {
            return "String";
        }
        return "Object";
    }
    
    @Override
    public String getObjectIdFieldConsumerDescriptor() {
        return JDOEnhancementNamer.CD_ObjectIdFieldConsumer;
    }
    
    @Override
    public String getObjectIdFieldSupplierDescriptor() {
        return JDOEnhancementNamer.CD_ObjectIdFieldSupplier;
    }
    
    @Override
    public Class getPersistenceManagerClass() {
        return JDOEnhancementNamer.CL_PersistenceManager;
    }
    
    @Override
    public Class getStateManagerClass() {
        return JDOEnhancementNamer.CL_StateManager;
    }
    
    @Override
    public Class getPersistableClass() {
        return JDOEnhancementNamer.CL_Persistable;
    }
    
    @Override
    public Class getDetachableClass() {
        return JDOEnhancementNamer.CL_Detachable;
    }
    
    @Override
    public Class getObjectIdFieldSupplierClass() {
        return JDOEnhancementNamer.CL_ObjectIdFieldSupplier;
    }
    
    @Override
    public Class getObjectIdFieldConsumerClass() {
        return JDOEnhancementNamer.CL_ObjectIdFieldConsumer;
    }
    
    @Override
    public Class getObjectIdentityClass() {
        return ObjectIdentity.class;
    }
    
    static {
        JDOEnhancementNamer.instance = null;
        CL_Detachable = Detachable.class;
        CL_Persistable = PersistenceCapable.class;
        CL_ObjectIdFieldConsumer = PersistenceCapable.ObjectIdFieldConsumer.class;
        CL_ObjectIdFieldSupplier = PersistenceCapable.ObjectIdFieldSupplier.class;
        CL_PersistenceManager = PersistenceManager.class;
        CL_StateManager = StateManager.class;
        ACN_DetachListener = DetachListener.class.getName().replace('.', '/');
        ACN_StateManager = JDOEnhancementNamer.CL_StateManager.getName().replace('.', '/');
        ACN_PersistenceManager = JDOEnhancementNamer.CL_PersistenceManager.getName().replace('.', '/');
        ACN_Persistable = JDOEnhancementNamer.CL_Persistable.getName().replace('.', '/');
        ACN_Detachable = JDOEnhancementNamer.CL_Detachable.getName().replace('.', '/');
        ACN_ObjectIdFieldConsumer = JDOEnhancementNamer.CL_ObjectIdFieldConsumer.getName().replace('.', '/');
        ACN_ObjectIdFieldSupplier = JDOEnhancementNamer.CL_ObjectIdFieldSupplier.getName().replace('.', '/');
        ACN_DetachedFieldAccessException = JDODetachedFieldAccessException.class.getName().replace('.', '/');
        ACN_FatalInternalException = JDOFatalInternalException.class.getName().replace('.', '/');
        ACN_Helper = JDOHelper.class.getName().replace('.', '/');
        ACN_ImplHelper = JDOImplHelper.class.getName().replace('.', '/');
        CD_ByteIdentity = Type.getDescriptor(ByteIdentity.class);
        CD_CharIdentity = Type.getDescriptor(CharIdentity.class);
        CD_IntIdentity = Type.getDescriptor(IntIdentity.class);
        CD_LongIdentity = Type.getDescriptor(LongIdentity.class);
        CD_ShortIdentity = Type.getDescriptor(ShortIdentity.class);
        CD_StringIdentity = Type.getDescriptor(StringIdentity.class);
        CD_ObjectIdentity = Type.getDescriptor(ObjectIdentity.class);
        CD_StateManager = Type.getDescriptor(StateManager.class);
        CD_PersistenceManager = Type.getDescriptor(PersistenceManager.class);
        CD_PersistenceCapable = Type.getDescriptor(PersistenceCapable.class);
        CD_Detachable = Type.getDescriptor(JDOEnhancementNamer.CL_Detachable);
        CD_ObjectIdFieldConsumer = Type.getDescriptor(PersistenceCapable.ObjectIdFieldConsumer.class);
        CD_ObjectIdFieldSupplier = Type.getDescriptor(PersistenceCapable.ObjectIdFieldSupplier.class);
        CD_String = Type.getDescriptor(String.class);
        CD_Object = Type.getDescriptor(Object.class);
    }
}
