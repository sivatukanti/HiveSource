// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.identity;

import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.ClassLoaderResolver;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.FieldMetaData;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ExecutionContext;
import org.datanucleus.api.ApiAdapter;

public class IdentityUtils
{
    public static String getClassNameForIdentitySimple(final ApiAdapter api, final Object id) {
        if (id instanceof OID) {
            return ((OID)id).getPcClass();
        }
        if (api.isSingleFieldIdentity(id)) {
            return api.getTargetClassNameForSingleFieldIdentity(id);
        }
        return null;
    }
    
    public static String getIdentityAsString(final ApiAdapter api, final Object id) {
        if (id == null) {
            return null;
        }
        if (api.isSingleFieldIdentity(id)) {
            return api.getTargetClassNameForSingleFieldIdentity(id) + ":" + api.getTargetKeyForSingleFieldIdentity(id);
        }
        return id.toString();
    }
    
    public static Object getApplicationIdentityForResultSetRow(final ExecutionContext ec, final AbstractClassMetaData cmd, Class pcClass, final boolean inheritanceCheck, final FieldManager resultsFM) {
        if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            if (pcClass == null) {
                pcClass = ec.getClassLoaderResolver().classForName(cmd.getFullClassName());
            }
            final ApiAdapter api = ec.getApiAdapter();
            final int[] pkFieldNums = cmd.getPKMemberPositions();
            final Object[] pkFieldValues = new Object[pkFieldNums.length];
            for (int i = 0; i < pkFieldNums.length; ++i) {
                final AbstractMemberMetaData pkMmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkFieldNums[i]);
                if (pkMmd.getType() == Integer.TYPE) {
                    pkFieldValues[i] = resultsFM.fetchIntField(pkFieldNums[i]);
                }
                else if (pkMmd.getType() == Short.TYPE) {
                    pkFieldValues[i] = resultsFM.fetchShortField(pkFieldNums[i]);
                }
                else if (pkMmd.getType() == Long.TYPE) {
                    pkFieldValues[i] = resultsFM.fetchLongField(pkFieldNums[i]);
                }
                else if (pkMmd.getType() == Character.TYPE) {
                    pkFieldValues[i] = resultsFM.fetchCharField(pkFieldNums[i]);
                }
                else if (pkMmd.getType() == Boolean.TYPE) {
                    pkFieldValues[i] = resultsFM.fetchBooleanField(pkFieldNums[i]);
                }
                else if (pkMmd.getType() == Byte.TYPE) {
                    pkFieldValues[i] = resultsFM.fetchByteField(pkFieldNums[i]);
                }
                else if (pkMmd.getType() == Double.TYPE) {
                    pkFieldValues[i] = resultsFM.fetchDoubleField(pkFieldNums[i]);
                }
                else if (pkMmd.getType() == Float.TYPE) {
                    pkFieldValues[i] = resultsFM.fetchFloatField(pkFieldNums[i]);
                }
                else if (pkMmd.getType() == String.class) {
                    pkFieldValues[i] = resultsFM.fetchStringField(pkFieldNums[i]);
                }
                else {
                    pkFieldValues[i] = resultsFM.fetchObjectField(pkFieldNums[i]);
                }
            }
            final Class idClass = ec.getClassLoaderResolver().classForName(cmd.getObjectidClass());
            if (cmd.usesSingleFieldIdentityClass()) {
                final Object id = api.getNewSingleFieldIdentity(idClass, pcClass, pkFieldValues[0]);
                if (!inheritanceCheck) {
                    return id;
                }
                if (ec.hasIdentityInCache(id)) {
                    return id;
                }
                final String[] subclasses = ec.getMetaDataManager().getSubclassesForClass(pcClass.getName(), true);
                if (subclasses != null) {
                    for (int j = 0; j < subclasses.length; ++j) {
                        final Object subid = api.getNewSingleFieldIdentity(idClass, ec.getClassLoaderResolver().classForName(subclasses[j]), api.getTargetKeyForSingleFieldIdentity(id));
                        if (ec.hasIdentityInCache(subid)) {
                            return subid;
                        }
                    }
                }
                final String className = ec.getStoreManager().getClassNameForObjectID(id, ec.getClassLoaderResolver(), ec);
                return api.getNewSingleFieldIdentity(idClass, ec.getClassLoaderResolver().classForName(className), pkFieldValues[0]);
            }
            else {
                try {
                    final Object id = idClass.newInstance();
                    for (int k = 0; k < pkFieldNums.length; ++k) {
                        final AbstractMemberMetaData pkMmd2 = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkFieldNums[k]);
                        Object value = pkFieldValues[k];
                        if (api.isPersistable(value)) {
                            value = api.getIdForObject(value);
                        }
                        if (pkMmd2 instanceof FieldMetaData) {
                            final Field pkField = ClassUtils.getFieldForClass(idClass, pkMmd2.getName());
                            pkField.set(id, value);
                        }
                        else {
                            final Method pkMethod = ClassUtils.getSetterMethodForClass(idClass, pkMmd2.getName(), pkMmd2.getType());
                            pkMethod.invoke(id, value);
                        }
                    }
                    return id;
                }
                catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    public static Object getValueForMemberInId(final Object id, final AbstractMemberMetaData pkMmd) {
        if (id == null || pkMmd == null || !pkMmd.isPrimaryKey()) {
            return null;
        }
        final String memberName = pkMmd.getName();
        final Field fld = ClassUtils.getFieldForClass(id.getClass(), memberName);
        if (fld != null && !Modifier.isPrivate(fld.getModifiers())) {
            try {
                return fld.get(id);
            }
            catch (Exception ex) {}
        }
        final Method getter = ClassUtils.getGetterMethodForClass(id.getClass(), memberName);
        if (getter != null && !Modifier.isPrivate(getter.getModifiers())) {
            try {
                return getter.invoke(id, (Object[])null);
            }
            catch (Exception ex2) {}
        }
        return null;
    }
    
    public static Object getObjectFromIdString(final String idStr, final AbstractClassMetaData cmd, final ExecutionContext ec, final boolean checkInheritance) {
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        Object id = null;
        if (cmd.getIdentityType() == IdentityType.DATASTORE) {
            id = OIDFactory.getInstance(ec.getNucleusContext(), idStr);
        }
        else if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            if (cmd.usesSingleFieldIdentityClass()) {
                id = ec.getApiAdapter().getNewApplicationIdentityObjectId(clr, cmd, idStr);
            }
            else {
                final Class cls = clr.classForName(cmd.getFullClassName());
                id = ec.newObjectId(cls, idStr);
            }
        }
        return ec.findObject(id, true, checkInheritance, null);
    }
    
    public static Object getObjectFromIdString(final String idStr, final AbstractMemberMetaData mmd, final int fieldRole, final ExecutionContext ec, final boolean checkInheritance) {
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        if (fieldRole != 2 || !mmd.getType().isInterface()) {
            AbstractClassMetaData cmd = null;
            if (fieldRole == 3) {
                cmd = mmd.getCollection().getElementClassMetaData(clr, ec.getMetaDataManager());
            }
            else if (fieldRole == 4) {
                cmd = mmd.getArray().getElementClassMetaData(clr, ec.getMetaDataManager());
            }
            else if (fieldRole == 5) {
                cmd = mmd.getMap().getKeyClassMetaData(clr, ec.getMetaDataManager());
            }
            else if (fieldRole == 5) {
                cmd = mmd.getMap().getKeyClassMetaData(clr, ec.getMetaDataManager());
            }
            else {
                cmd = ec.getMetaDataManager().getMetaDataForClass(mmd.getType(), clr);
            }
            Object id = null;
            if (cmd.getIdentityType() == IdentityType.DATASTORE) {
                id = OIDFactory.getInstance(ec.getNucleusContext(), idStr);
            }
            else if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                if (cmd.usesSingleFieldIdentityClass()) {
                    Class cls = clr.classForName(cmd.getFullClassName());
                    if (Modifier.isAbstract(cls.getModifiers())) {
                        final String[] subclasses = ec.getMetaDataManager().getSubclassesForClass(cmd.getFullClassName(), false);
                        if (subclasses != null) {
                            for (int i = 0; i < subclasses.length; ++i) {
                                cls = clr.classForName(subclasses[i]);
                                if (!Modifier.isAbstract(cls.getModifiers())) {
                                    cmd = ec.getMetaDataManager().getMetaDataForClass(cls, clr);
                                    break;
                                }
                            }
                        }
                    }
                    id = ec.getApiAdapter().getNewApplicationIdentityObjectId(clr, cmd, idStr);
                }
                else {
                    final Class cls = clr.classForName(cmd.getFullClassName());
                    id = ec.newObjectId(cls, idStr);
                }
            }
            return ec.findObject(id, true, checkInheritance, null);
        }
        final String[] implNames = MetaDataUtils.getInstance().getImplementationNamesForReferenceField(mmd, fieldRole, clr, ec.getMetaDataManager());
        if (implNames == null || implNames.length == 0) {
            return null;
        }
        AbstractClassMetaData cmd2 = ec.getMetaDataManager().getMetaDataForClass(implNames[0], clr);
        if (cmd2.getIdentityType() == IdentityType.DATASTORE) {
            final Object id2 = OIDFactory.getInstance(ec.getNucleusContext(), idStr);
            return ec.findObject(id2, true, checkInheritance, null);
        }
        if (cmd2.getIdentityType() == IdentityType.APPLICATION) {
            Object id2 = null;
            int j = 0;
            while (j < implNames.length) {
                if (j != 0) {
                    cmd2 = ec.getMetaDataManager().getMetaDataForClass(implNames[j], clr);
                }
                if (cmd2.usesSingleFieldIdentityClass()) {
                    id2 = ec.getApiAdapter().getNewApplicationIdentityObjectId(clr, cmd2, idStr);
                }
                else {
                    id2 = ec.newObjectId(clr.classForName(cmd2.getFullClassName()), idStr);
                }
                try {
                    return ec.findObject(id2, true, checkInheritance, null);
                }
                catch (NucleusObjectNotFoundException nonfe) {
                    ++j;
                    continue;
                }
                break;
            }
        }
        return null;
    }
}
