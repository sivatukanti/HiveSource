// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api;

import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.exceptions.NucleusException;
import java.util.Map;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.LifeCycleState;
import org.datanucleus.ExecutionContext;
import java.io.Serializable;

public interface ApiAdapter extends Serializable
{
    String getName();
    
    boolean isMemberDefaultPersistent(final Class p0);
    
    boolean isManaged(final Object p0);
    
    ExecutionContext getExecutionContext(final Object p0);
    
    LifeCycleState getLifeCycleState(final int p0);
    
    boolean isPersistent(final Object p0);
    
    boolean isNew(final Object p0);
    
    boolean isDirty(final Object p0);
    
    boolean isDeleted(final Object p0);
    
    boolean isDetached(final Object p0);
    
    boolean isTransactional(final Object p0);
    
    boolean isPersistable(final Object p0);
    
    boolean isPersistable(final Class p0);
    
    boolean isDetachable(final Object p0);
    
    String getObjectState(final Object p0);
    
    void makeDirty(final Object p0, final String p1);
    
    Object getIdForObject(final Object p0);
    
    Object getVersionForObject(final Object p0);
    
    boolean isValidPrimaryKeyClass(final Class p0, final AbstractClassMetaData p1, final ClassLoaderResolver p2, final int p3, final MetaDataManager p4);
    
    boolean isSingleFieldIdentity(final Object p0);
    
    boolean isDatastoreIdentity(final Object p0);
    
    boolean isSingleFieldIdentityClass(final String p0);
    
    String getSingleFieldIdentityClassNameForLong();
    
    String getSingleFieldIdentityClassNameForInt();
    
    String getSingleFieldIdentityClassNameForShort();
    
    String getSingleFieldIdentityClassNameForByte();
    
    String getSingleFieldIdentityClassNameForChar();
    
    String getSingleFieldIdentityClassNameForString();
    
    String getSingleFieldIdentityClassNameForObject();
    
    Class getTargetClassForSingleFieldIdentity(final Object p0);
    
    String getTargetClassNameForSingleFieldIdentity(final Object p0);
    
    Object getTargetKeyForSingleFieldIdentity(final Object p0);
    
    Class getKeyTypeForSingleFieldIdentityType(final Class p0);
    
    Object getNewSingleFieldIdentity(final Class p0, final Class p1, final Object p2);
    
    Object getNewApplicationIdentityObjectId(final ClassLoaderResolver p0, final AbstractClassMetaData p1, final String p2);
    
    Object getNewApplicationIdentityObjectId(final Object p0, final AbstractClassMetaData p1);
    
    Object getNewApplicationIdentityObjectId(final Class p0, final Object p1);
    
    boolean allowPersistOfDeletedObject();
    
    boolean allowDeleteOfNonPersistentObject();
    
    boolean allowReadFieldOfDeletedObject();
    
    boolean clearLoadedFlagsOnDeleteObject();
    
    boolean getDefaultCascadePersistForField();
    
    boolean getDefaultCascadeUpdateForField();
    
    boolean getDefaultCascadeDeleteForField();
    
    boolean getDefaultCascadeRefreshForField();
    
    boolean getDefaultDFGForPersistableField();
    
    Map getDefaultFactoryProperties();
    
    RuntimeException getApiExceptionForNucleusException(final NucleusException p0);
    
    RuntimeException getUserExceptionForException(final String p0, final Exception p1);
    
    RuntimeException getDataStoreExceptionForException(final String p0, final Exception p1);
    
    Object getCopyOfPersistableObject(final Object p0, final ObjectProvider p1, final int[] p2);
    
    void copyFieldsFromPersistableObject(final Object p0, final int[] p1, final Object p2);
    
    void copyPkFieldsToPersistableObjectFromId(final Object p0, final Object p1, final FieldManager p2);
}
