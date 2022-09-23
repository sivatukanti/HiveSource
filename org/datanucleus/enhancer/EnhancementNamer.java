// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

public interface EnhancementNamer
{
    String getStateManagerFieldName();
    
    String getFlagsFieldName();
    
    String getFieldNamesFieldName();
    
    String getFieldTypesFieldName();
    
    String getFieldFlagsFieldName();
    
    String getPersistableSuperclassFieldName();
    
    String getInheritedFieldCountFieldName();
    
    String getDetachedStateFieldName();
    
    String getSerialVersionUidFieldName();
    
    String getFieldNamesInitMethodName();
    
    String getFieldTypesInitMethodName();
    
    String getFieldFlagsInitMethodName();
    
    String getGetObjectIdMethodName();
    
    String getGetTransactionalObjectIdMethodName();
    
    String getGetVersionMethodName();
    
    String getIsDetachedMethodName();
    
    String getIsDetachedInternalMethodName();
    
    String getIsDeletedMethodName();
    
    String getIsDirtyMethodName();
    
    String getIsNewMethodName();
    
    String getIsPersistentMethodName();
    
    String getIsTransactionalMethodName();
    
    String getGetPersistenceManagerMethodName();
    
    String getPreSerializeMethodName();
    
    String getGetInheritedFieldCountMethodName();
    
    String getSuperCloneMethodName();
    
    String getGetManagedFieldCountMethodName();
    
    String getPersistableSuperclassInitMethodName();
    
    String getLoadClassMethodName();
    
    String getCopyFieldMethodName();
    
    String getCopyFieldsMethodName();
    
    String getCopyKeyFieldsFromObjectIdMethodName();
    
    String getCopyKeyFieldsToObjectIdMethodName();
    
    String getProvideFieldMethodName();
    
    String getProvideFieldsMethodName();
    
    String getReplaceFieldMethodName();
    
    String getReplaceFieldsMethodName();
    
    String getReplaceFlagsMethodName();
    
    String getReplaceStateManagerMethodName();
    
    String getReplaceDetachedStateMethodName();
    
    String getMakeDirtyMethodName();
    
    String getMakeDirtyDetachedMethodName();
    
    String getNewInstanceMethodName();
    
    String getNewObjectIdInstanceMethodName();
    
    String getGetMethodPrefixMethodName();
    
    String getSetMethodPrefixMethodName();
    
    String getDetachListenerAsmClassName();
    
    String getStateManagerAsmClassName();
    
    String getPersistenceManagerAsmClassName();
    
    String getPersistableAsmClassName();
    
    String getDetachableAsmClassName();
    
    String getObjectIdFieldConsumerAsmClassName();
    
    String getObjectIdFieldSupplierAsmClassName();
    
    String getDetachedFieldAccessExceptionAsmClassName();
    
    String getFatalInternalExceptionAsmClassName();
    
    String getHelperAsmClassName();
    
    String getImplHelperAsmClassName();
    
    String getByteIdentityDescriptor();
    
    String getCharIdentityDescriptor();
    
    String getIntIdentityDescriptor();
    
    String getLongIdentityDescriptor();
    
    String getShortIdentityDescriptor();
    
    String getStringIdentityDescriptor();
    
    String getObjectIdentityDescriptor();
    
    String getSingleFieldIdentityDescriptor(final String p0);
    
    String getTypeDescriptorForSingleFieldIdentityGetKey(final String p0);
    
    String getTypeNameForUseWithSingleFieldIdentity(final String p0);
    
    String getStateManagerDescriptor();
    
    String getPersistenceManagerDescriptor();
    
    String getPersistableDescriptor();
    
    String getDetachableDescriptor();
    
    String getObjectIdFieldConsumerDescriptor();
    
    String getObjectIdFieldSupplierDescriptor();
    
    Class getPersistenceManagerClass();
    
    Class getStateManagerClass();
    
    Class getPersistableClass();
    
    Class getDetachableClass();
    
    Class getObjectIdFieldSupplierClass();
    
    Class getObjectIdFieldConsumerClass();
    
    Class getObjectIdentityClass();
}
